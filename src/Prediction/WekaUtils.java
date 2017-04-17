/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prediction;

/**
 *
 * @author programcreek
 */
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.clusterers.*;
import weka.core.Attribute;
import weka.core.OptionHandler;
import weka.filters.AllFilter;

import java.text.MessageFormat;
import java.util.ArrayList;

public class WekaUtils {

    public static final String CLASSES_ATTR_NAME = "classes";
    public static final String FEATURE_PREFIX = "feature-";

    public static Classifier makeClassifier(String wekaClassifier, String[] options) throws Exception {

        switch (WekaClassificationAlgorithms.valueOf(wekaClassifier)) {
            case decisionTree:
                J48 j48 = new J48();
                setOptionsForWekaPredictor(options, j48);
                return j48;
            case svm:
                SMO smo = new SMO();
                smo.setNumFolds(-1);
                setOptionsForWekaPredictor(options, smo);
                return smo;
            case logisticRegression:
                Logistic logistic = new Logistic();
                setOptionsForWekaPredictor(options, logistic);
                return logistic;
            case randomForest:
                RandomForest forest = new RandomForest();
                setOptionsForWekaPredictor(options, forest);
                return forest;
            case decisionStump:
                DecisionStump stump = new DecisionStump();
                setOptionsForWekaPredictor(options, stump);
                return stump;
            case perceptron:
                MultilayerPerceptron perceptron = new MultilayerPerceptron();
                setOptionsForWekaPredictor(options, perceptron);
                return perceptron;
            default:
                return new SMO();
        }
    }

    public static Clusterer makeClusterer(String wekaClassifier, int numClusters, String[] options) throws Exception {
        try {
            switch (WekaClusterers.valueOf(wekaClassifier)) {
                case kmeans:
                    SimpleKMeans kmeans = new SimpleKMeans();
                    kmeans.setNumClusters(numClusters);
                    setOptionsForWekaPredictor(options, kmeans);
                    return kmeans;
                case densityBased:
                    MakeDensityBasedClusterer clusterer = new MakeDensityBasedClusterer();
                    clusterer.setNumClusters(numClusters);
                    setOptionsForWekaPredictor(options, clusterer);
                    return clusterer;
                case farthestFirst:
                    FarthestFirst ff = new FarthestFirst();
                    ff.setNumClusters(numClusters);
                    setOptionsForWekaPredictor(options, ff);
                    return ff;
                case hierarchicalClusterer:
                    HierarchicalClusterer hc = new HierarchicalClusterer();
                    hc.setNumClusters(numClusters);
                    setOptionsForWekaPredictor(options, hc);
                    return hc;
                case em:
                    EM em = new EM();
                    em.setMaxIterations(10);
                    em.setMaximumNumberOfClusters(numClusters);
                    em.setNumClusters(numClusters);
                    setOptionsForWekaPredictor(options, em);
                    return em;
                case filteredClusterer:
                    kmeans = new SimpleKMeans();
                    kmeans.setNumClusters(numClusters);
                    FilteredClusterer fc = new FilteredClusterer();
                    fc.setFilter(new AllFilter());
                    fc.setClusterer(kmeans);
                    setOptionsForWekaPredictor(options, fc);
                    return fc;
                default:
                    kmeans = new SimpleKMeans();
                    kmeans.setNumClusters(numClusters);
                    setOptionsForWekaPredictor(options, kmeans);
                    return kmeans;
            }
        } catch (Exception e) {
            throw new Exception("Could not make Clusterer", e);
        }
    }

    public static ArrayList<Attribute> makeFeatureVectorForBatchClustering(int noOfAttributes, int numClasses) {
        // Declare FAST VECTOR 
        ArrayList<Attribute> attributeInfo = new ArrayList<>();

        // Declare FEATURES and add them to FEATURE VECTOR 
        for (int i = 0; i < noOfAttributes; i++) {
            attributeInfo.add(new Attribute(MessageFormat.format("feature-{0}", i)));
        }

        System.err.println("DEBUG: no. of attributes = " + attributeInfo.size());
        return attributeInfo;
    }

    public static ArrayList<Attribute> makeFeatureVectorForBinaryClassification(int noOfAttributes) {
        ArrayList<Attribute> attributeInfo = new ArrayList<>();
        // Declare FEATURES and add them to FEATURE VECTOR 
        for (int i = 0; i < noOfAttributes; i++) {
            attributeInfo.add(new Attribute(MessageFormat.format("feature-{0}", i)));
        }
        // last element in a FEATURE VECTOR is the category 
        ArrayList<String> classNames = new ArrayList<>(2);
        for (int i = 1; i <= 2; i++) {
            classNames.add(MessageFormat.format("class-{0}", String.valueOf(i)));
        }
        Attribute classes = new Attribute(CLASSES_ATTR_NAME, classNames);
        // last element in a FEATURE VECTOR is the category 
        attributeInfo.add(classes);
        System.err.println("DEBUG: no. of attributes = " + attributeInfo.size());
        return attributeInfo;
    }

    public static ArrayList<Attribute> makeFeatureVectorForOnlineClustering(int noOfClusters, int noOfAttributes) {
        // Declare FAST VECTOR 
        ArrayList<Attribute> attributeInfo = new ArrayList<>();

        // Declare FEATURES and add them to FEATURE VECTOR 
        for (int i = 0; i < noOfAttributes; i++) {
            attributeInfo.add(new Attribute(MessageFormat.format("feature-{0}", i)));
        }

        System.err.println("DEBUG: no. of attributes = " + attributeInfo.size());
        return attributeInfo;
    }

    public static void setOptionsForWekaPredictor(String[] options, OptionHandler kmeans) throws Exception {
        if (options != null) {
            kmeans.setOptions(options);
        }
    }
}
