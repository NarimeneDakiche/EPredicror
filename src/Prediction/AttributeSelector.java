/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prediction;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.*;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;

/**
 *
 * @author HADJER
 */
public class AttributeSelector {
    
    /**Selection method Filter the train Data**/
    
    /**Filtering Method
     * train data
     * Search Method
     * Evaluation Method
     * returns new .arff filename
     * @param datad**/
    protected static String useFilter(String fichierEntrainement, String searchMethod, String evalMethod) throws Exception {
        
        System.out.println("\n2. Filter");
        
        Instances data=null;
        //création des exemples d'apprentissage à partir du fichier
        try (
                //Chargement d’un fichier arff
                //Lecture du fichier d'apprentissage
                FileReader reader = new FileReader(fichierEntrainement)) {
            //création des exemples d'apprentissage à partir du fichier
            data = new Instances(reader);
            //Fermeture du flux de lecture
        }catch(IOException e){
            e.printStackTrace();
        }

        //choix de la classe à apprendre
        data.setClassIndex(data.numAttributes() - 1);

        
        weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
        
        
        ASEvaluation eval=null;
        switch(evalMethod){
            case "CfsSubsetEval": 
                eval = new CfsSubsetEval();
                break;
            case "WrapperSubsetEval": 
                eval = new WrapperSubsetEval();
                break;
            default: 
                eval = new WrapperSubsetEval();
                break;
        }
        
        
        ASSearch search = null;
        switch(searchMethod){
            case "GreedyStepwise":
                search = new GreedyStepwise();
                ((GreedyStepwise) search).setSearchBackwards(true);
                break;
            case "BestFirst":
                search = new BestFirst();
                break;
            default:
                search = new GreedyStepwise();
                ((GreedyStepwise) search).setSearchBackwards(true);
                break;
        }

        filter.setEvaluator(eval);
        filter.setSearch(search);
        filter.setInputFormat(data);
        Instances newData = Filter.useFilter(data, filter);

        //Write into Arff File
            String filePath = "./LibPrediction/";
            String newFichierEntrainement=filePath+getfileName(fichierEntrainement)+"-Filtred.arff";
            ArffSaver saver = new ArffSaver();
            saver.setInstances(newData);
            saver.setFile(new File(newFichierEntrainement));
            saver.writeBatch();
        return newFichierEntrainement;
    }
    /**Selection method : Wrapper**/
    
    /**
     * train data
     * Classifier/Classifier options
     * Search Method
     * Evaluation Method
     * returns the Evaluation Report of the model
     **/
    
    public static EvaluationReport useClassifier(String fichierEntrainement, String wekaClassifier,String[] options,
            String searchMethod, String evalMethod) {
        System.out.println("\n1. Meta-classfier");
        AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
        
        Instances data=null;
        //création des exemples d'apprentissage à partir du fichier
        try (
                //Chargement d’un fichier arff
                //Lecture du fichier d'apprentissage
                FileReader reader = new FileReader(fichierEntrainement)) {
            //création des exemples d'apprentissage à partir du fichier
            data = new Instances(reader);
            //Fermeture du flux de lecture
        }catch(IOException e){
            e.printStackTrace();
        }

        //choix de la classe à apprendre
        data.setClassIndex(data.numAttributes() - 1);

        
        ASEvaluation eval=null;
        switch(evalMethod){
            case "CfsSubsetEval": 
                eval = new CfsSubsetEval();
                break;
            case "WrapperSubsetEval": 
                eval = new WrapperSubsetEval();
                break;
            default: 
                eval = new WrapperSubsetEval();
                break;
        }
        
        
        ASSearch search = null;
        switch(searchMethod){
            case "GreedyStepwise":
                search = new GreedyStepwise();
                ((GreedyStepwise) search).setSearchBackwards(true);
                break;
            case "BestFirst":
                search = new BestFirst();
                break;
            default:
                search = new GreedyStepwise();
                ((GreedyStepwise) search).setSearchBackwards(true);
                break;
        }
        
        Evaluation evaluation = null;
        try{
            Classifier base = WekaUtils.makeClassifier(wekaClassifier, options);
            classifier.setClassifier(base);
            classifier.setEvaluator(eval);
            classifier.setSearch(search);
            evaluation = new Evaluation(data);
            evaluation.crossValidateModel(classifier, data, 10, new Random(1));
        }catch(Exception e){
            e.printStackTrace();
        }
        //Retourner les résultats d'évaluation de Weka
        return (new EvaluationReport(evaluation,data.numAttributes() - 1));
    }
    /**Get the filename out of a file path**/
    public static String getfileName(String string) {
        Path p = Paths.get(string);
        String fileName = p.getFileName().toString();
        return (fileName.indexOf(".") >= 0) ? fileName.substring(0, fileName.indexOf(".")) : fileName;
    }

 
}
