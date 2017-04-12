/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prediction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 *
 * @author HADJER
 */
public class PredictionUtils {
    
    /**Create the Arff file that contains the training data**/
    public static void createArff2(String filePath, String filename/**Without extension**/,
                                  /*LinkedList<TimeFrame> dynamicNetwork,*/String BDpath,String BDfilename,
                                  int nbtimeframe, String EvolM, String PredictM){
        try{

            FastVector      atts;
            FastVector      attsRel;
            FastVector      attVals;
            FastVector      attVals2;
            FastVector      attVals3;
            FastVector      attVals1;
            FastVector      attValsRel;
            Instances       data;
            Instances       dataRel;
            double[]        vals;
            DenseInstance instance;
            double[]        valsRel;
            int             i;

            //Use Switch one you have multiple choices

            // 1. set up attributes
            atts = new FastVector();
            // - numeric
            ///////atts.addElement(new Attribute("SizeTn_3"));
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
            atts.addElement(new Attribute("EventTn_3Tn_2", attVals));
            // - numeric
            ///////atts.addElement(new Attribute("SizeTn_2"));
            // - nominal
            attVals1 = new FastVector();
            //attVals1.addElement("dummy");
            attVals1.addElement("continuing");
            attVals1.addElement("growing");
            attVals1.addElement("shrinking");
            attVals1.addElement("merging");
            attVals1.addElement("splitting");
            attVals1.addElement("dissolving");
            atts.addElement(new Attribute("EventTn_2Tn_1", attVals1));
            // - numeric
            //////atts.addElement(new Attribute("SizeTn_1"));
            // - nominal
            attVals2 = new FastVector();
            //attVals2.addElement("dummy");
            attVals2.addElement("continuing");
            attVals2.addElement("growing");
            attVals2.addElement("shrinking");
            attVals2.addElement("merging");
            attVals2.addElement("splitting");
            attVals2.addElement("dissolving");
            atts.addElement(new Attribute("EventTn_1Tn", attVals2));
            // - numeric
            //////atts.addElement(new Attribute("SizeTn"));
            // - nominal
            attVals3 = new FastVector();
            //attVals3.addElement("dummy");
            attVals3.addElement("continuing");
            attVals3.addElement("growing");
            attVals3.addElement("shrinking");
            attVals3.addElement("merging");
            attVals3.addElement("splitting");
            attVals3.addElement("dissolving");
            atts.addElement(new Attribute("EventTnTn1", attVals3));
            System.out.println("atts=="+atts.toString());
            // 2. create Instances object
            data = new Instances("EvolutionChain", atts, 0);
            // 3. fill with data

            //Connect to the table and get the data
            String sql = "select group1,timeframe1,event_type from Chains";
            System.out.println(sql);
            try (Connection conn = connect(BDpath,BDfilename);
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){
                // loop through the result set
                while (rs.next()) {
                    String event_type=rs.getString("event_type");
                    String group1=rs.getString("group1");
                    String timeframe1=rs.getString("timeframe1");
                    System.out.println( group1 +  "\t" + timeframe1 + "\t" + event_type);
                    String [] events= event_type.split(",");
                    if(events.length>3){
                        //Create data for Arff file
                        String [] groups= group1.split(",");
                        String [] timeframes= timeframe1.split(",");
                        System.out.println("numAttributes=="+data.numAttributes());
                        vals = new double[data.numAttributes()];// important: needs NEW array!
                        //instance=new DenseInstance(atts.size());
                        //instance.setDataset(data);
                        for(int ievents=0;ievents<4/*events.length*/;ievents++){
                            //Read Group & timeframe: treat exceptions try and catch them
                            //////int g=Integer.getInteger(groups[ievents]);
                            //////int t=Integer.getInteger(timeframes[ievents]);
                            //Extract its caracteristics from the graph
                            //Graph group=dynamicNetwork.get(t).getCommunities().get(g);

                            //Exract the next event
                            String e=new String(events[ievents]);
                            System.out.println("Insert : events["+ievents+"]=="+e);

                            //Store data : instances
                            try{
                                switch(ievents){
                                    case 0:
                                        vals[ievents] = attVals.indexOf(e);
                                        break;
                                    case 1:
                                        vals[ievents] = attVals1.indexOf(e);
                                        break;
                                    case 2:
                                        vals[ievents] = attVals2.indexOf(e);
                                        break;
                                    case 3:
                                        vals[ievents] = attVals3.indexOf(e);
                                        break;
                                }
                                //instance.setValue(ievents, e);
                            }catch(ArrayIndexOutOfBoundsException ex){
                                System.err.println("i  events=="+ievents+"   events=="+events.toString()+" groups=="+groups+"  timeframes=="+timeframes);
                                ex.printStackTrace();
                                return;
                            }

                        }
                        // add
                        try{
                            data.add(new DenseInstance(1.0, vals));
                            //data.add(instance);
                            System.out.println("Completed Insertion");
                        }catch(ArrayIndexOutOfBoundsException ex){
                            System.err.println("error data insertion, data size=="+data.size()+"   events=="+events.toString()+" groups=="+groups+"  timeframes=="+timeframes);
                            ex.printStackTrace();
                            return;
                        } 
                    }
                            
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            // 4. output data
            //System.out.println(data);

            //Write into Arff File
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(filePath+filename+".arff"));
            saver.writeBatch();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    /**Create the Arff file that contains the training data**/
    public static void createArff(String filePath, String filename/**Without extension**/,
                                  /*LinkedList<TimeFrame> dynamicNetwork,*/String BDpath,String BDfilename,
                                  int nbtimeframe, String EvolM, String PredictM, int nbevents/*Chain's length*/){
        try{

            FastVector      atts;
            FastVector      attsRel;
            FastVector      attVals;
            FastVector      attVals2;
            FastVector      attVals3;
            FastVector      attVals1;
            FastVector      attValsRel;
            Instances       data;
            Instances       dataRel;
            double[]        vals;
            DenseInstance instance;
            double[]        valsRel;
            int             i;

            //Use Switch one you have multiple choices

            // 1. set up attributes
            atts = new FastVector();
            // - numeric
            ///////atts.addElement(new Attribute("SizeTn_3"));
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
            atts.addElement(new Attribute("EventTn_3Tn_2", attVals));
            // - numeric
            ///////atts.addElement(new Attribute("SizeTn_2"));
            // - nominal
            attVals1 = new FastVector();
            //attVals1.addElement("dummy");
            attVals1.addElement("continuing");
            attVals1.addElement("growing");
            attVals1.addElement("shrinking");
            attVals1.addElement("merging");
            attVals1.addElement("splitting");
            attVals1.addElement("dissolving");
            atts.addElement(new Attribute("EventTn_2Tn_1", attVals1));
            // - numeric
            //////atts.addElement(new Attribute("SizeTn_1"));
            // - nominal
            attVals2 = new FastVector();
            //attVals2.addElement("dummy");
            attVals2.addElement("continuing");
            attVals2.addElement("growing");
            attVals2.addElement("shrinking");
            attVals2.addElement("merging");
            attVals2.addElement("splitting");
            attVals2.addElement("dissolving");
            atts.addElement(new Attribute("EventTn_1Tn", attVals2));
            // - numeric
            //////atts.addElement(new Attribute("SizeTn"));
            // - nominal
            attVals3 = new FastVector();
            //attVals3.addElement("dummy");
            attVals3.addElement("continuing");
            attVals3.addElement("growing");
            attVals3.addElement("shrinking");
            attVals3.addElement("merging");
            attVals3.addElement("splitting");
            attVals3.addElement("dissolving");
            atts.addElement(new Attribute("EventTnTn1", attVals3));
            System.out.println("atts=="+atts.toString());
            // 2. create Instances object
            data = new Instances("EvolutionChain", atts, 0);
            // 3. fill with data

            //Connect to the table and get the data
            String sql = "select group1,timeframe1,event_type from Chains";
            System.out.println(sql);
            try (Connection conn = connect(BDpath,BDfilename);
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql)){
                // loop through the result set
                while (rs.next()) {
                    String event_type=rs.getString("event_type");
                    String group1=rs.getString("group1");
                    String timeframe1=rs.getString("timeframe1");
                    System.out.println( group1 +  "\t" + timeframe1 + "\t" + event_type);
                    String [] events= event_type.split(",");
                    if(events.length>3){
                        //Create data for Arff file
                        String [] groups= group1.split(",");
                        String [] timeframes= timeframe1.split(",");
                        System.out.println("numAttributes=="+data.numAttributes());
                        //instance=new DenseInstance(atts.size());
                        //instance.setDataset(data);
                        //for(int ievents=0;ievents<nbevents/*events.length*/;ievents++){
                            //Read Group & timeframe: treat exceptions try and catch them
                            //////int g=Integer.getInteger(groups[ievents]);
                            //////int t=Integer.getInteger(timeframes[ievents]);
                            //Extract its caracteristics from the graph
                            //Graph group=dynamicNetwork.get(t).getCommunities().get(g);

                            //Exract the next event
                            /*String e=new String(events[ievents]);
                            System.out.println("Insert : events["+ievents+"]=="+e);

                            //Store data : instances
                            try{
                                switch(ievents){
                                    case 0:
                                        vals[ievents] = attVals.indexOf(e);
                                        break;
                                    case 1:
                                        vals[ievents] = attVals1.indexOf(e);
                                        break;
                                    case 2:
                                        vals[ievents] = attVals2.indexOf(e);
                                        break;
                                    case 3:
                                        vals[ievents] = attVals3.indexOf(e);
                                        break;
                                }
                                //instance.setValue(ievents, e);
                            }catch(ArrayIndexOutOfBoundsException ex){
                                System.err.println("i  events=="+ievents+"   events=="+events.toString()+" groups=="+groups+"  timeframes=="+timeframes);
                                ex.printStackTrace();
                                return;
                            }

                        }*/
                        int ievents =0;
                        while(ievents<=(events.length-nbevents)){
                            int ievents2=ievents;
                            vals = new double[data.numAttributes()];// important: needs NEW array!
                            
                            for(int iparcours=0;iparcours<nbevents;iparcours++){
                                
                                String e=new String(events[ievents2]);
                                System.out.println("Insert : events["+ievents2+"]=="+e);

                                //Store data : instances
                                try{
                                    switch(iparcours){
                                        case 0:
                                            vals[iparcours] = attVals.indexOf(e);
                                            break;
                                        case 1:
                                            vals[iparcours] = attVals1.indexOf(e);
                                            break;
                                        case 2:
                                            vals[iparcours] = attVals2.indexOf(e);
                                            break;
                                        case 3:
                                            vals[iparcours] = attVals3.indexOf(e);
                                            break;
                                    }
                                    //instance.setValue(ievents, e);
                                }catch(ArrayIndexOutOfBoundsException ex){
                                    System.err.println("i  events=="+ievents+"   events=="+events.toString()+" groups=="+groups+"  timeframes=="+timeframes);
                                    ex.printStackTrace();
                                    return;
                                }
                                
                                ievents2++;
                            }
                            // add
                            try{
                                data.add(new DenseInstance(1.0, vals));
                                //data.add(instance);
                                System.out.println("Completed Insertion");
                            }catch(ArrayIndexOutOfBoundsException ex){
                                System.err.println("error data insertion, data size=="+data.size()+"   events=="+events.toString()+" groups=="+groups+"  timeframes=="+timeframes);
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

            //Write into Arff File
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(filePath+filename+".arff"));
            saver.writeBatch();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void createClassifierJ48(String fichierEntrainement, int kfolds) throws FileNotFoundException, IOException, Exception{
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
        J48 classifieur = new J48();
 
        //Evaluation par validation croisée (avec k = 10)
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(classifieur, instances, kfolds, new Random());
        System.out.println("Taux d’erreurs par VC :" + eval.errorRate());
    }
    
    public static void createClassifier(String fichierEntrainement, int kfolds) throws FileNotFoundException, IOException, Exception{
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
        
        Classifier classifieur = WekaUtils.makeClassifier("decisionTree", null);
        //Evaluation par validation croisée (avec k = 10)
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(classifieur, instances, kfolds, new Random());
        System.out.println("Taux d’erreurs par VC :" + eval.errorRate());
    }
    //Connect to a database
    public static Connection connect(String path,String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:"+ path + fileName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
