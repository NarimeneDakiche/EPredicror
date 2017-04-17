/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prediction;

import evolutionIdentification.EvolutionUtils;

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
        PredictionUtils.createArff(filePath, filename, BDpath, BDfilename, nbtimeframe, "", "", 4);
    }
}
