/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prediction;

import EvaluationReport.PModel;
import EvaluationReport.ResultsStats;
import evolutionIdentification.EvolutionUtils;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author HADJER
 */
public class testPrediction {

    public static void main(String[] args) throws Exception {
        String filePath = "./LibPrediction/";
        String filename = "trainData";
        String extension = ".arff";
        String BDpath = "./LibEvolution/";
        String BDfilename = "testGED.db";
        String tabname = "GED";
        int nbtimeframe = 44;

        //EvolutionUtils.writeEvolutionChain(BDpath, BDfilename, tabname,nbtimeframe/**nbre timeframes**/);
        //PredictionUtils.createClassifierJ48(filePath+filename+extension,10);
        //PredictionUtils.createArff(filePath, filename,BDpath,BDfilename,nbtimeframe, "", "");
        //PredictionUtils.createArffAttribute(filePath, filename,BDpath, BDfilename,nbtimeframe, 4,null, dynamicNetwork);
        //PredictionUtils.createArff(filePath, filename, BDpath, BDfilename, nbtimeframe, "", "", 4);
        String selectionMethod = "Wrapper";//"Filter";
        String evalMethod = "CfsSubsetEval";
        String searchMethod = "GreedyStepwise";
        String wekaClassifier = "naiveBayes";
        String[] options = null;
        String fichierEntrainement = filePath + filename + extension;
        int kfolds = 10;

        EvaluationReport e
                = PredictionUtils.makePredictor(selectionMethod, searchMethod, evalMethod,
                        wekaClassifier, options, fichierEntrainement, kfolds);

        //e.printReport();
        //e.generateCurve1(/*filePath+filename+*/"f.png");
        //e.generateROCcurve();
        //e.saveReportTextFile("C:\\Users\\HADJER\\Desktop\\report.txt",0);
        //e.saveReportTextPDF("C:\\Users\\HADJER\\Desktop\\report.pdf", nbtimeframe);
        String filenamePDF = "C:\\Users\\HADJER\\Desktop\\report.pdf";
        PModel pModel = new PModel();
        ResultsStats rs = new ResultsStats();

        ///
        ///
        e.saveReportTextPDF(filenamePDF, pModel, rs, 1);
    }
}
