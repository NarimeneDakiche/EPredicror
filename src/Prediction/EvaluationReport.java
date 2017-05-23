/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prediction;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.openide.util.Exceptions;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.JComponentWriter;
import weka.gui.visualize.JPEGWriter;
import weka.gui.visualize.PNGWriter;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

/**
 *
 * @author HADJER
 */
public class EvaluationReport implements java.io.Serializable{
    
    /******Prediction Model Info//////////////////////////////////////////////////////////////**/
    
    /**Segmentation**/
    String SocialNetwork;
    String dataSize;/**NbNodes**/
    String SnapshotPeriod;
    String Overlapping;
    //Include temporelDistributionImg
    
    /**Detection**/
    String DetectionMethod;
    String DetectionParameters;
    
    String DetectionStats;//NumComm and ...
    
    /**Evolution**/
    String EvolutionMethod;
    String EvolutionParameters;
    
    String EvolutionStats;//Tab Results
    
    /**Prediction**/
    String [] Metrics;
    String selectionMethod;
    String Evaluator;
    String Search;
    String classifier;
    String validationMethod; 
    String validationParam;
    
    
    /******Prediction Results//////////////////////////////////////////////////////////////**/
    Evaluation eval;
    int predictedClass;
    String Summary;
    String Fmeasure;
    String Accuracy;
    String Recall;
    ArrayList<String> ConfusionMatrix= new ArrayList<>();
    
    
    EvaluationReport(Evaluation eval, int attribute) {
        this.eval=eval;
        this.predictedClass=attribute;
        
        this.Summary= eval.toSummaryString();
        
        double [][] matriceConfusion=eval.confusionMatrix();
        ConfusionMatrix.add("Confusion Matrix :");
        
        for(int i = 0 ; i < matriceConfusion.length; i++ ){  
            String line="";
            for(int j = 0; j< matriceConfusion[i].length; j++){   
                line=line+"  "+matriceConfusion[i][j];
            } 
            ConfusionMatrix.add(line); 
        }
        
        /*this.Fmeasure=""+eval.fMeasure(attribute);
        this.Accuracy=""+eval.precision(attribute);
        this.Recall=""+eval.recall(attribute);*/
        
    }
    
    void printReport(){
        System.out.println("Summary :" + this.Summary);
        System.out.println("Confusion Matrix :");
        for(String e: this.ConfusionMatrix){
            System.out.println(e);
        }
        System.out.println(this.Fmeasure);
        System.out.println(this.Accuracy);
        System.out.println(this.Recall);
    }
    
    void generateCurve1(String filename){
        try {
            // generate curve
            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0;
            Instances result = tc.getCurve(eval.predictions(), classIndex);
            
            // plot curve
            ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
            PlotData2D tempd = new PlotData2D(result);
            
            // specify which points are connected
            boolean[] cp = new boolean[result.numInstances()];
            for (int n = 1; n < cp.length; n++)
                cp[n] = true;
            tempd.setConnectPoints(cp);
            // add plot
            vmc.addPlot(tempd);
            
            // We want a precision-recall curve
            vmc.setXIndex(result.attribute("Recall").index());
            vmc.setYIndex(result.attribute("Precision").index());
            
            // Make window with plot but don't show it
            String plotName = vmc.getName();
            final javax.swing.JFrame jf =
                    new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
            jf.setSize(500,400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(vmc, BorderLayout.CENTER);
            jf.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    jf.dispose();
                }
            });
            jf.setVisible(true);
            
            // Save to file specified as second argument (can use any of
            // BMPWriter, JPEGWriter, PNGWriter, PostscriptWriter for different formats)
            JComponentWriter jcw = new PNGWriter(vmc.getPlotPanel(), new File(filename));
            jcw.toOutput();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    void generateROCcurve(){
        try {
            // generate curve
            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0;
            Instances result = tc.getCurve(eval.predictions(), classIndex);
            
            // plot curve
            ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
            vmc.setROCString("(Area under ROC = " +
                    Utils.doubleToString(tc.getROCArea(result), 4) + ")");
            vmc.setName(result.relationName());
            PlotData2D tempd = new PlotData2D(result);
            tempd.setPlotName(result.relationName());
            tempd.addInstanceNumberAttribute();
            // specify which points are connected
            boolean[] cp = new boolean[result.numInstances()];
            for (int n = 1; n < cp.length; n++)
                cp[n] = true;
            tempd.setConnectPoints(cp);
            // add plot
            vmc.addPlot(tempd);
            
            // display curve
            String plotName = vmc.getName();
            final javax.swing.JFrame jf =
                    new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
            jf.setSize(500,400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(vmc, BorderLayout.CENTER);
            jf.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    jf.dispose();
                }
            });
            jf.setVisible(true);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    void saveReportTextFile(String filename){
        
        try{
            PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();
        } catch (IOException e) {
           // do something
        }
        try {
            
        }catch(Exception e){
            
        }
    }
    
    void saveReport(String filename){
        try {
         FileOutputStream fileOut = new FileOutputStream(filename);
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(this);
         out.close();
         fileOut.close();
         System.out.printf("Serialized data is saved in"+filename);
      }catch(IOException i) {
         i.printStackTrace();
      }
    }

    public static EvaluationReport getReport(String filename){
        EvaluationReport report=null;
        try {
         FileInputStream fileIn = new FileInputStream(filename);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         report = (EvaluationReport) in.readObject();
         in.close();
         fileIn.close();
      }catch(IOException i) {
         i.printStackTrace();
         
      }catch(ClassNotFoundException c) {
         System.out.println("EvaluationReport class not found");
         c.printStackTrace();
      }
        return report;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String Summary) {
        this.Summary = Summary;
    }

    public ArrayList<String> getConfusionMatrix() {
        return ConfusionMatrix;
    }

    public void setConfusionMatrix(ArrayList<String> ConfusionMatrix) {
        this.ConfusionMatrix = ConfusionMatrix;
    }
    
    
}
