/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prediction;

import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/**
 *
 * @author HADJER
 */
public class PredictionUtils {

    /**
     * Create the Arff file that contains History data of communities and its
     * caracteristics
     *
     *
     * @param filePath
     * @param filename
     * @param BDpath
     * @param BDfilename
     * @param nbtimeframe
     * @param nbevents
     * @param Features
     * @param dynamicNetwork
     */
    public static void createArffAttribute(String filePath, String filename/**
             * Without extension*
             */
            ,
            String BDpath, String BDfilename,
            int nbtimeframe, int nbevents,/*Chain's length*/
            ArrayList<String> Features, LinkedList<TimeFrame> dynamicNetwork) {
        try {

            FastVector atts;
            FastVector attVals = null;
            FastVector attVals1 = null;
            Instances data;
            double[] vals;
            int nbFeatures;
            if (Features != null) {
                nbFeatures = Features.size();
            } else {
                nbFeatures = 0;
            }

            //Use Switch one you have multiple choices
            atts = new FastVector();

            for (int k = 0; k < nbevents; k++) {
                for (int j = 0; j < nbFeatures; j++) {
                    atts.addElement(new Attribute(Features.get(j) + "_" + k));
                }
                if (k == 0) {
                    //first event
                    // - nominal
                    attVals = new FastVector();
                    //attVals.addElement("dummy");
                    attVals.addElement("forming");//We want a chain of 4 so we can get 'forming' only at the beginning
                    attVals.addElement("continuing");
                    attVals.addElement("growing");
                    attVals.addElement("shrinking");
                    attVals.addElement("merging");
                    attVals.addElement("splitting");
                    attVals.addElement("dissolving");
                    int k1 = k + 1;
                    atts.addElement(new Attribute("EventT_" + k + "_" + k1, attVals));
                } else {
                    attVals1 = new FastVector();
                    //attVals1.addElement("dummy");
                    attVals1.addElement("continuing");
                    attVals1.addElement("growing");
                    attVals1.addElement("shrinking");
                    attVals1.addElement("merging");
                    attVals1.addElement("splitting");
                    attVals1.addElement("dissolving");
                    int k1 = k + 1;
                    atts.addElement(new Attribute("EventT_" + k + "_" + k1, attVals1));
                }
            }

            //System.out.println("atts==" + atts.toString());
            // 2. create Instances object
            data = new Instances("EvolutionChain", atts, 0);
            // 3. fill with data

            //Connect to the table and get the data
            String sql = "select group1,timeframe1,event_type from Chains";
            //System.out.println(sql);
            try (Connection conn = connect(BDpath, BDfilename);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                // loop through the result set
                while (rs.next()) {
                    String event_type = rs.getString("event_type");
                    String group1 = rs.getString("group1");
                    String timeframe1 = rs.getString("timeframe1");
                    //System.out.println(group1 + "\t" + timeframe1 + "\t" + event_type);
                    String[] events = event_type.split(",");
                    if (events.length > (nbevents - 1)) {
                        //Create data for Arff file
                        String[] groups = group1.split(",");
                        String[] timeframes = timeframe1.split(",");
                        //System.out.println("numAttributes=="+data.numAttributes());

                        int ievents = 0;
                        while (ievents <= (events.length - nbevents)) {
                            int ievents2 = ievents;
                            vals = new double[data.numAttributes()];// important: needs NEW array!

                            int iInsertion = 0;
                            for (int iparcours = 0; iparcours < nbevents; iparcours++) {

                                String e = new String(events[ievents2]);
                                //System.out.println("Insert : events[" + ievents2 + "]==" + e);
                                /*String g = new String(groups[ievents2]);
                                 String t = new String(timeframes[ievents2]);*/

                                //Store data : instances
                                //Features
                                for (int k = 0; k < nbFeatures; k++) {

                                    if ((groups[ievents2]).matches("null")) {
                                        //Case the group didn't exist before
                                        vals[iInsertion] = 0;

                                    } else {
                                        //extract feature of group from dynamic graph
                                        int g = Integer.parseInt(groups[ievents2]);
                                        int t = Integer.parseInt(timeframes[ievents2]);

                                        /*System.out.println("case not null==>Insert : Feature[" + k+ ","+ g +
                                         ","+ t +"]==" + e);
                                        
                                
                                         //Read Feature
                                         /System.out.println("Feature[k=="+ k + "]==" + Features.get(k)+"   for g=="+
                                         g +"   for t=="+ t);*/
                                        try {
                                            vals[iInsertion] = dynamicNetwork.get(t).getCommunities().get(
                                                    g).getAttribute(Features.get(k));
                                        } catch (NullPointerException n) {
                                            //System.err.println("Attribute: " + Features.get(k)+ "not calculated");
                                            vals[iInsertion] = 0;
                                        }

                                    }

                                    iInsertion++;
                                }
                                //Event
                                try {
                                    switch (iparcours) {
                                        case 0:
                                            vals[iInsertion] = attVals.indexOf(e);
                                            break;
                                        default:
                                            vals[iInsertion] = attVals1.indexOf(e);
                                            break;
                                    }
                                    //instance.setValue(ievents, e);
                                } catch (ArrayIndexOutOfBoundsException ex) {
                                    //System.err.println("i  events==" + ievents + "   events==" + events.toString() + " groups==" + groups + "  timeframes==" + timeframes);
                                    ex.printStackTrace();
                                    return;
                                }

                                ievents2++;
                                iInsertion++;
                            }
                            // add
                            try {
                                data.add(new DenseInstance(1.0, vals));
                                //data.add(instance);
                                //System.out.println("Completed Insertion");
                            } catch (ArrayIndexOutOfBoundsException ex) {
                                //System.err.println("error data insertion, data size==" + data.size() + "   events==" + events.toString() + " groups==" + groups + "  timeframes==" + timeframes);
                                ex.printStackTrace();
                                return;
                            }
                            ievents++;
                        }

                    }

                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            // 4. output data
            //System.out.println(data);

            //Normalize Attributes 
            Filter m_Filter = new Normalize();
            m_Filter.setInputFormat(data);
            data = Filter.useFilter(data, m_Filter);

            //Write into Arff File
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            File file = new File(filePath + filename + ".arff");
            //deleting the file if exists
            try {

                if (file.delete()) {
                    System.out.println(file.getName() + " is deleted!");
                } else {
                    System.out.println("Delete operation is failed.");
                }

            } catch (Exception e) {

            }

            saver.setFile(new File(filePath + filename + ".arff"));
            saver.writeBatch();

        } catch (Exception e) {
            System.err.println("Erreur Génération Fichier ARFF");
        }
    }

    public static EvaluationReport createClassifier(String wekaClassifier, String[] options, String fichierEntrainement, int kfolds) throws FileNotFoundException, IOException, Exception {
        Instances instances;
        //création des exemples d'apprentissage à partir du fichier
        try (
                //Chargement d’un fichier arff
                //Lecture du fichier d'apprentissage
                FileReader reader = new FileReader(fichierEntrainement)) {
            //création des exemples d'apprentissage à partir du fichier
            instances = new Instances(reader);
            //Fermeture du flux de lecture
        }

        //choix de la classe à apprendre
        instances.setClassIndex(instances.numAttributes() - 1);

        //Instanciation d’un classifieur de type C4.5 (appelé ici J48) et apprentissage
        //J48 classifieur = new J48();
        Classifier classifieur = WekaUtils.makeClassifier(wekaClassifier, options);//if no options put null
        //Evaluation par validation croisée (avec k = 10)
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(classifieur, instances, kfolds, new Random());
        //Create evaliuation report 
        EvaluationReport report = new EvaluationReport(eval, instances.size());
        return report;
    }

    public static Evaluation createClassifier2(Classifier classifieur, String[] options, String fichierEntrainement, int kfolds) throws FileNotFoundException, IOException, Exception {
        Instances instances;
        //création des exemples d'apprentissage à partir du fichier
        try (
                //Chargement d’un fichier arff
                //Lecture du fichier d'apprentissage
                FileReader reader = new FileReader(fichierEntrainement)) {
            //création des exemples d'apprentissage à partir du fichier
            instances = new Instances(reader);
            //Fermeture du flux de lecture
        }

        //choix de la classe à apprendre
        instances.setClassIndex(instances.numAttributes() - 1);
        //Evaluation par validation croisée (avec k = 10)
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(classifieur, instances, kfolds, new Random());
        //Result 
        System.out.println("Summary :" + eval.toSummaryString());
        return eval;
    }

    /**Create prediction model and return its evaluation report**/
    public static EvaluationReport makePredictor(String selectionMethod, String searchMethod, String evalMethod,
            String wekaClassifier, String[] options, String fichierEntrainement, int kfolds) throws Exception {

        EvaluationReport report = null;
        switch (selectionMethod) {
            case "Filter":

                    String newFichierEntrainement = AttributeSelector.useFilter(fichierEntrainement, searchMethod, evalMethod);
                    report = PredictionUtils.createClassifier(wekaClassifier, options, newFichierEntrainement, kfolds);

                break;
            case "Wrapper":

                    report = AttributeSelector.useClassifier(fichierEntrainement, wekaClassifier, options, searchMethod, evalMethod);

                break;
            case "Manual":
            default:

                    report = PredictionUtils.createClassifier(wekaClassifier, options, fichierEntrainement, kfolds);

                break;
        }

        return report;
    }

    //Connect to a database
    public static Connection connect(String path, String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
