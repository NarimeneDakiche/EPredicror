/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prediction;

import UI.PModel;
import UI.ResultsStats;
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

////////////////////////////////////////////////////////////////////*
import java.io.FileOutputStream;
import java.util.Date;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.Arrays;

/////////////////////////////////////////////////////////////////////
/**
 *
 * @author HADJER
 */
public class EvaluationReport implements java.io.Serializable {

    /**
     * ****Prediction Model
     * Info//////////////////////////////////////////////////////////////*
     */
    /**
     * Segmentation*
     */
    String SocialNetwork;
    String dataSizeNode;
    /**
     * NbNodes*
     */
    String dataSizeEdge;
    /**
     * NbEdges*
     */
    String SnapshotPeriod;
    String nbSnapshots;
    String Overlapping;
    //Include temporelDistributionImg

    /**
     * Detection*
     */
    String DetectionMethod;
    String DetectionParameters;

    String DetectionStats;//NumComm and ...

    /**
     * Evolution*
     */
    String EvolutionMethod;
    String EvolutionParameters;

    String EvolutionStats;//Tab Results

    /**
     * Prediction*
     */
    String[] Metrics;
    String selectionMethod;
    String Evaluator;
    String Search;
    String classifier;
    String validationMethod;
    String validationParam;

    /**
     * ****Prediction
     * Results//////////////////////////////////////////////////////////////*
     */
    Evaluation eval;
    int nbInstances;
    String Summary;
    String DetailedAccuracy;
    PModel pModel;
    ResultsStats rs;

    public String getDetailedAccuracy() {
        return DetailedAccuracy;
    }
    ArrayList<String> ConfusionMatrix = new ArrayList<>();

    EvaluationReport(Evaluation eval, int nbInstances) {
        this.eval = eval;
        this.nbInstances = nbInstances;
        try {
            this.DetailedAccuracy = eval.toClassDetailsString();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        this.Summary = eval.toSummaryString();

        double[][] matriceConfusion = eval.confusionMatrix();
        ConfusionMatrix.add("Confusion Matrix :");

        for (int i = 0; i < matriceConfusion.length; i++) {
            String line = "";
            for (int j = 0; j < matriceConfusion[i].length; j++) {
                line = line + "\t" + matriceConfusion[i][j];
            }
            ConfusionMatrix.add(line);
        }

    }

    void printReport() {
        System.out.println("Number of Instances :" + this.nbInstances);
        System.out.println("Summary :" + this.Summary);
        //System.out.println("Confusion Matrix :");
        for (String e : this.ConfusionMatrix) {
            System.out.println(e);
        }
        try {
            System.out.println(eval.toClassDetailsString());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    void generateCurve1(String filename) {
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
            for (int n = 1; n < cp.length; n++) {
                cp[n] = true;
            }
            tempd.setConnectPoints(cp);
            // add plot
            vmc.addPlot(tempd);

            // We want a precision-recall curve
            vmc.setXIndex(result.attribute("Recall").index());
            vmc.setYIndex(result.attribute("Precision").index());

            // Make window with plot but don't show it
            String plotName = vmc.getName();
            final javax.swing.JFrame jf
                    = new javax.swing.JFrame("Weka Classifier Visualize: " + plotName);
            jf.setSize(500, 400);
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
            ex.printStackTrace();
        }
    }

    void generateROCcurve() {
        try {
            // generate curve
            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0;
            Instances result = tc.getCurve(eval.predictions(), classIndex);

            // plot curve
            ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
            vmc.setROCString("(Area under ROC = "
                    + Utils.doubleToString(tc.getROCArea(result), 4) + ")");
            vmc.setName(result.relationName());
            PlotData2D tempd = new PlotData2D(result);
            tempd.setPlotName(result.relationName());
            tempd.addInstanceNumberAttribute();
            // specify which points are connected
            boolean[] cp = new boolean[result.numInstances()];
            for (int n = 1; n < cp.length; n++) {
                cp[n] = true;
            }
            tempd.setConnectPoints(cp);
            // add plot
            vmc.addPlot(tempd);

            // display curve
            String plotName = vmc.getName();
            final javax.swing.JFrame jf
                    = new javax.swing.JFrame("Weka Classifier Visualize: " + plotName);
            jf.setSize(500, 400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(vmc, BorderLayout.CENTER);
            jf.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    jf.dispose();
                }
            });
            jf.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveReportTextFile(String filename, int networkName) {

        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.println("----------------- Evaluaton Report -------------------------");
            writer.println("------------------------------------------------------------");
            writer.println("Network: " + networkName);

            writer.println("Description: -------------------------");
            writer.println("--------------------------------------");
            /**
             * ****Prediction Model
             * Info//////////////////////////////////////////////////////////////*
             */

            /**
             * Segmentation*
             */
            writer.println(SocialNetwork);
            writer.println("Number of Nodes: " + dataSizeNode);
            /**
             * NbNodes*
             */
            writer.println("Number of Edges: " + dataSizeEdge);
            /**
             * NbNodes*
             */
            writer.println("Duration Segmentation: " + SnapshotPeriod);
            writer.println("Overlapping Threshold: " + Overlapping);
            //Include temporelDistributionImg

            /**
             * Detection*
             */
            writer.println("Detection Method: " + DetectionMethod);
            writer.println("Detection Method Parameters: " + DetectionParameters);
            writer.println("Detection Stats: -------------------------");
            writer.println(DetectionStats);//NumComm and ...

            /**
             * Evolution*
             */
            writer.println("Evolution Method: " + EvolutionMethod);
            writer.println("Evolution Method Parameters: " + EvolutionParameters);

            writer.println("Evolution Stats: -------------------------");
            writer.println(EvolutionStats);//Tab Results

            /**
             * Prediction*
             */
            writer.println("Number of Instances: " + this.nbInstances);
            writer.println("Metrics list: " + Metrics);
            writer.println("Selection Method: " + selectionMethod);
            writer.println("------Evaluator Method: " + Evaluator);
            writer.println("------Search Method: " + Search);
            writer.println("Classifier: " + classifier);
            writer.println("------Validation Method: " + validationMethod);
            writer.println("------Validation Parameters: " + validationParam);

            writer.close();
        } catch (IOException e) {
            // do something
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void saveReportTextPDF(String filename, PModel pModel, ResultsStats rs, int startingStep) {
        try {
            this.pModel = pModel;
            this.rs = rs;
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            addMetaData(document);
            addTitlePage(document);
            addContent(document, startingStep);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //PDF function//////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private static Font IMAGE_CAPTION = new Font(Font.FontFamily.TIMES_ROMAN, 11,
            Font.NORMAL);

    ;

    
    /////////////////////////////////////////////////////////////////////////////////
    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private static void addMetaData(Document document) {
        document.addTitle("Prediction Model Evaluation Report");
        document.addSubject("");
        document.addKeywords("Community Evolution, , DYNAMIC SOCIAL NETWORKS, Predicting Community Evolution");
        document.addAuthor("Author");
        document.addCreator("EPredictor: Evolution Prediction tool for communities in dynamic social networks");
    }

    private static void addTitlePage(Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();

        // We add one empty line
        addEmptyLine(preface, 15);
        // Lets write a big header
        preface.add(new Paragraph("Prediction Model Evaluation Report", catFont));
        preface.setAlignment(Element.ALIGN_CENTER);
        document.add(preface);

        preface = new Paragraph();
        addEmptyLine(preface, 2);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph(
                "Report generated by: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));
        preface.setAlignment(Element.ALIGN_CENTER);
        document.add(preface);

        preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_LEFT);
        addEmptyLine(preface, 25);
        //Contenu
        preface.add(new Paragraph(
                "This document describes test results of a Prediction Model  created by  "
                + "\"EPredictor Evolution Prediction tool for communnity dynamic social networks.\"",
                smallBold));

        //Remarks
        /*addEmptyLine(preface, 8);

         preface.add(new Paragraph(
         "This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-).",
         redFont));*/
        document.add(preface);
        // Start a new page
        document.newPage();
    }

    private void addContent(Document document, int startingStep) throws DocumentException {

        try {
            String NetworkDescription = "NetworkDescription";

            Anchor anchor = new Anchor("Benchmark Data Description", catFont);
            anchor.setName("Benchmark Data Description");

            // Second parameter is the number of the chapter
            Chapter catPart = new Chapter(new Paragraph(anchor), 1);
            Paragraph preface = new Paragraph();

            // We add Network description
            if (!(rs.getDescription().matches("")) /*|| (rs.getDescription() != null)*/) {
                // We add one empty line
                addEmptyLine(preface, 3);
                catPart.add(preface);

                catPart.add(new Paragraph(rs.getDescription()));
            }

            // We add one empty line
            addEmptyLine(preface, 3);
            catPart.add(preface);

            //Paragraph subPara = new Paragraph("", subFont);
            //Section subCatPart = catPart.addSection(subPara);
            /*subCatPart.add(new Paragraph("Hello"));
            
             subPara = new Paragraph("Step 2: Community Detection", subFont);
             subCatPart = catPart.addSection(subPara);
             subCatPart.add(new Paragraph("Paragraph 1"));
             subCatPart.add(new Paragraph("Paragraph 2"));
             subCatPart.add(new Paragraph("Paragraph 3"));
            
             // add a list
             createList(subCatPart);
             Paragraph paragraph = new Paragraph();
             addEmptyLine(paragraph, 5);
             subCatPart.add(paragraph);*/
            // add a table
            createTableNetworkData(catPart);

            preface = new Paragraph();
            // We add one empty line
            addEmptyLine(preface, 3);
            catPart.add(preface);

            // now add all this to the document
            //document.add(catPart);
            //Time Distribution
            if (!(rs.getDistribution().matches(""))) {

                Image image = Image.getInstance(rs.getDistribution());
                /*Image image = Image.getInstance("C:\\Users\\HADJER\\Documents\\TEXMaker\\"
                 + "Plan de test\\time_histogram-facebook-wosn-wall-small.png");*/
                image.scaleAbsolute(300f, 300f);
                image.setAlignment(Element.ALIGN_CENTER);

                //image.setAbsolutePosition(0f, 0f);
                //document.add(image);
                catPart.add(decorateImage(image, "Fig. 1: Temporel Distribution of the network"));

            }
            //add data to doc
            document.add(catPart);
            /*Image image2 = Image.getInstance("C:\\Users\\HADJER\\Documents\\TEXMaker\\"
             + "Plan de test\\time_histogram-facebook-wosn-wall-small.png");
             image2.scalePercent(300f);
             document.add(image2);*/

            //anchor = new Anchor("Model Description", catFont);
            //anchor.setName("Model Description");
            // Second parameter is the number of the chapter
            //catPart = new Chapter(new Paragraph(anchor), 1);
            // Next section
            anchor = new Anchor("Second Chapter", catFont);
            anchor.setName("Second Chapter");

            // Second parameter is the number of the chapter
            catPart = new Chapter(new Paragraph(anchor), 2);

            Paragraph subPara;
            Section subCatPart;
            if (startingStep == 1) {
                subPara = new Paragraph("Step 1: Snapshots Creation (Data Segmentation)", subFont);
                subCatPart = catPart.addSection(subPara);
                if (!(pModel.getSnapshotDuration().matches(""))) {
                    subCatPart.add(new Paragraph("Snapshot Durations: " + pModel.getSnapshotDuration()));
                }

                if (!(pModel.getNbSnapshots() == 0)) {
                    subCatPart.add(new Paragraph("Number of Snapshots : " + pModel.getNbSnapshots()));
                }

                if (!(pModel.getOverlapping() == 0)) {
                    subCatPart.add(new Paragraph("Overlapping: " + pModel.getOverlapping()));
                }
                /**
                 * Results1*
                 */
                subCatPart.add(new Paragraph("Results: "));

                if (!(rs.getNbSnaps() == 0)) {
                    subCatPart.add(new Paragraph("Number of Snapshot: " + rs.getNbSnaps()));
                }

                if (!(rs.getAverageSnapSize() == 0)) {
                    subCatPart.add(new Paragraph("Average snapshot size: " + rs.getAverageSnapSize()));
                }

                preface = new Paragraph();
                // We add one empty line
                addEmptyLine(preface, 2);
                subCatPart.add(preface);

            }

            if (startingStep <= 2) {
                subPara = new Paragraph("Step 2: Community Detection", subFont);
                subCatPart = catPart.addSection(subPara);
                if (!(pModel.getDetectionMethod().matches(""))) {
                    subCatPart.add(new Paragraph("Detection Method: " + pModel.getDetectionMethod()));
                }
                if (!(pModel.getDetectionParameters() == 0)) {
                    subCatPart.add(new Paragraph("Detection Method Parameters: " + pModel.getDetectionParameters()));
                }
                /**
                 * Results2*
                 */
                subCatPart.add(new Paragraph("Results: "));

                if (!(rs.getTotalNbCommunities() == 0)) {
                    subCatPart.add(new Paragraph("Number of communities: " + rs.getTotalNbCommunities()));
                }
                if (!(rs.getAverageNbCommunitiesPerSnap() == 0)) {
                    subCatPart.add(new Paragraph("Average number of communities per snapshot: " + rs.getAverageNbCommunitiesPerSnap()));
                }
                if (!(rs.getAverageCommSize() == 0)) {
                    subCatPart.add(new Paragraph("Average community size: " + rs.getAverageCommSize()));
                }

                if (!(pModel.getAttributesList().length == 0)) {
                    subCatPart.add(new Paragraph("Attributes: " + Arrays.toString(pModel.getAttributesList())));
                }

                preface = new Paragraph();
                // We add one empty line
                addEmptyLine(preface, 2);
                subCatPart.add(preface);
            }

            if (startingStep <= 3) {
                subPara = new Paragraph("Step 3: Evolution Identification", subFont);
                subCatPart = catPart.addSection(subPara);
                if (!(pModel.getEvolutionMethod().matches(""))) {
                    subCatPart.add(new Paragraph("Evolution Method: " + pModel.getEvolutionMethod()));
                }
                if (!(pModel.getEvolutionParameters().matches(""))) {
                    subCatPart.add(new Paragraph("Evolution Parameters: " + pModel.getEvolutionParameters()));
                }

                /**
                 * Results3*
                 */
                if (!(rs.getEvolutionResults().matches(""))) {
                    subCatPart.add(new Paragraph("Results: "));

                    Image image = Image.getInstance(rs.getEvolutionResults());
                    /*image = Image.getInstance("C:\\Users\\HADJER\\Documents\\TEXMaker\\"
                     + "Plan de test\\time_histogram-facebook-wosn-wall-small.png");*/
                    image.scaleAbsolute(300f, 300f);
                    image.setAlignment(Element.ALIGN_CENTER);

                    //image.setAbsolutePosition(0f, 0f);
                    //document.add(image);
                    subCatPart.add(decorateImage(image, "Fig. 2: Number of evolution events"));
                    //document.add(catPart);      
                }

                preface = new Paragraph();
                // We add one empty line
                addEmptyLine(preface, 2);
                subCatPart.add(preface);
            }

            if (startingStep <= 4) {
                subPara = new Paragraph("Step 4: Prediction", subFont);
                subCatPart = catPart.addSection(subPara);
                if (!(pModel.getattributesPrediction().length == 0)) {
                    subCatPart.add(new Paragraph("Metrics: " + Arrays.toString(pModel.getattributesPrediction())));
                }
                if (!(pModel.getChainLength() == 0)) {
                    subCatPart.add(new Paragraph("Lengh of the evolution chain: " + pModel.getChainLength()));
                }
                if (!(pModel.getSelectionMethod().matches(""))) {
                    subCatPart.add(new Paragraph("Selection Method: " + pModel.getSelectionMethod()));
                }
                if (!(pModel.getEvaluator().matches(""))) {
                    subCatPart.add(new Paragraph("Evaluator: " + pModel.getEvaluator()));
                }
                if (!(pModel.getSearch().matches(""))) {
                    subCatPart.add(new Paragraph("Search: " + pModel.getSearch()));
                }
                if (!(pModel.getClassifier().matches(""))) {
                    subCatPart.add(new Paragraph("Classifier: " + pModel.getClassifier()));
                }
                if (!(pModel.getSearch().matches(""))) {
                    subCatPart.add(new Paragraph("validation Method:" + pModel.getSearch()));
                }
                if (!(pModel.getValidationParam().matches(""))) {
                    subCatPart.add(new Paragraph("validation Parameters:" + pModel.getValidationParam()));
                }
                /**
                 * Results4*
                 */
                subCatPart.add(new Paragraph("Results: " + rs.getPredictionResults()));
                // We add one empty line
                preface = new Paragraph();
                addEmptyLine(preface, 3);
                subCatPart.add(preface);
            }
            /*subCatPart.add(new Paragraph("Summary :" + this.Summary));
             //subCatPart.add(new Paragraph("Confusion Matrix :"));
             for (String e : this.ConfusionMatrix) {
             subCatPart.add(new Paragraph(e));
             }

             // We add one empty line
             preface = new Paragraph();
             addEmptyLine(preface, 3);
             subCatPart.add(preface);

             subCatPart.add(new Paragraph("Accuracy :" + this.DetailedAccuracy));
             */ // now add all this to the document
            document
                    .add(catPart);

        } catch (BadElementException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void createTableNetworkData(Section subCatPart)
            throws BadElementException {

        String NetworkType = "NetworkType";
        PdfPTable table = new PdfPTable(2);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);
        PdfPCell c1 = new PdfPCell(new Phrase("Category"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(NetworkType));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        /*c1 = new PdfPCell(new Phrase("Table Header 3"));
         c1.setHorizontalAlignment(Element.ALIGN_CENTER);
         table.addCell(c1);*/
        table.setHeaderRows(1);

        table.addCell("link");
        if (rs.getLinkReference().matches("")) {
            table.addCell("/");
        } else {
            table.addCell(rs.getLinkReference());
        }

        table.addCell("Data file structure");
        table.addCell("{Node1, Node2, Timestamp, Others}");
        table.addCell("Number of nodes");
        if (rs.getNbNodes() == 0) {
            table.addCell("/");
        } else {
            table.addCell(rs.getNbNodes() + " users");
        }

        table.addCell("Number of edges");
        if (rs.getNbEdges() == 0) {
            table.addCell("/");
        } else {
            table.addCell(rs.getNbEdges() + " edges");
        }

        table.addCell("Average degree");
        if (rs.getAverageDegree().matches("")) {
            table.addCell("/");
        } else {
            table.addCell(rs.getAverageDegree());
        }
        table.addCell("Maximal degree");
        if (rs.getMaxDegree().matches("")) {
            table.addCell("/");
        } else {
            table.addCell(rs.getMaxDegree() + " edge/node");
        }

        table.addCell("Average Clustering coefficient");
        if (rs.getAverageClusteringCoeff() == 0) {
            table.addCell("/");
        } else {
            table.addCell(rs.getAverageClusteringCoeff() + " edge/node");
        }

        /*table.addCell("Diameter");
         table.addCell("" + " edges");*/
        subCatPart.add(table);

    }

    protected Paragraph decorateImage(Image i, String caption) {

        Paragraph p = new Paragraph("", IMAGE_CAPTION);
        p.setAlignment(Element.ALIGN_CENTER);
        i.setAlignment(Element.ALIGN_CENTER);
        i.setBorder(Image.BOX);
        i.setBorderWidth(3f);
        i.setBorderColor(new BaseColor(52, 90, 138));
        p.add(i);
        Paragraph captionP = new Paragraph(caption, IMAGE_CAPTION);
        captionP.setAlignment(Element.ALIGN_CENTER);
        //p.add(Chunk.NEWLINE);
        p.add(captionP);

        //p.setKeepTogether(true);
        return p;
    }

    private static void createTable(Section subCatPart)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);
        PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Table Header 2"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Table Header 3"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        table.addCell("1.0");
        table.addCell("1.1");
        table.addCell("1.2");
        table.addCell("2.1");
        table.addCell("2.2");
        table.addCell("2.3");

        subCatPart.add(table);

    }

    private static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void saveReport(String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in" + filename);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static EvaluationReport getReport(String filename) {
        EvaluationReport report = null;
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            report = (EvaluationReport) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();

        } catch (ClassNotFoundException c) {
            System.out.println("EvaluationReport class not found");
            c.printStackTrace();
        }
        return report;
    }

    public String getnbInstances() {
        return "Number of Instances: " + nbInstances;
    }

    /*public void setnbInstances(int nbInstances) {
     this.nbInstances = nbInstances;
     }*/
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
