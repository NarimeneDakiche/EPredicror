/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection.ExternMethods;

import graphclasses1.Community;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author HADJER
 */
public class CONCLUDE extends CommunityMiner{
    
    private BufferedReader error;
    private BufferedReader op;
    private int exitVal;
    String jarFilePath="\".\\LibDetection\\CONCLUDE\\CONCLUDE.jar\"";
        
     
    public LinkedList<Community> findCommunities2(String filePath) {  
        // Arguments

        String filename=DetectionUtils.getfileName(filePath);
        LinkedList<Graph> communities=new LinkedList<>();
        
	    final List<String> actualArgs = new ArrayList<String>();
	    actualArgs.add(0, "java");
	    actualArgs.add(1, "-jar");
	    actualArgs.add(2, jarFilePath);
            actualArgs.add(3, "\""+filePath+"\"");
            actualArgs.add(4, "\".\\LibDetection\\CONCLUDE\\clusters-"+filename+".txt\"");//output file
            actualArgs.add(5, " ");//Delimiter
            
	    //actualArgs.addAll(args);
            try {
                String line;
                Process p = Runtime.getRuntime().exec(actualArgs.toArray(new String[0]));
                BufferedReader bri = new BufferedReader
                  (new InputStreamReader(p.getInputStream()));
                BufferedReader bre = new BufferedReader
                  (new InputStreamReader(p.getErrorStream()));
                while ((line = bri.readLine()) != null) {
                  //System.out.println(line);
                }
                bri.close();
                while ((line = bre.readLine()) != null) {
                  //System.out.println(line);
                }
                bre.close();
                p.waitFor();
                this.exitVal = p.exitValue();
                if (this.exitVal != 0) {
                    throw new IOException("Failed to execure jar, ");// + this.getExecutionLog());
                }else{
                    System.out.println("Done.");
                    
                    File f = new File(".\\LibDetection\\CONCLUDE\\clusters-"+filename+".txt");
                    FileInputStream fis = new FileInputStream(f);
                    //Construct BufferedReader from InputStreamReader
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                    communities=new LinkedList<>();
                    line = null;
                    int nbcomm=0;
                    while ((line = br.readLine()) != null) {
                        //check if last line
                        if(!line.contains("Q = ")){
                            System.out.println("comm:"+ nbcomm + " == "+line);
                            String[] nodes = line.split("\\W+");
                            System.out.println("The number of nodes is: " + nodes.length);
                            communities.add(new SingleGraph(""));
                            for (String nodeId : nodes) {
                                if((communities.get(nbcomm).getNode(nodeId))==null){
                                    communities.get(nbcomm).addNode(nodeId);
                                }
                            }
                            nbcomm++;
                        }
                            //communities.add(new Community(new LinkedList(Arrays.asList(nodes)),null));
                    }
                    br.close();
                    f.delete();

                    //Delete weights file
                    f = new File(".\\LibDetection\\CONCLUDE\\weights-"+filename+".txt");
                    f.delete();
                    
                    //add the edges for each community
                    f = new File(filePath);
                    fis = new FileInputStream(f);
                    //Construct BufferedReader from InputStreamReader
                    br = new BufferedReader(new InputStreamReader(fis));

                    line = null;
                    //Read the file line by line and affect the edge to the community
                    while ((line = br.readLine()) != null) {
                        System.out.println("line: == "+line);
                        String[] nodes = line.replaceAll("(^\\s+|\\s+$)", "").split("\\s+");
                        System.out.println("The number of nodes is: " + nodes.length);
                        String nodeId0=new String(nodes[0]);
                        String nodeId1=new String(nodes[1]);

                        //Search for the community that contains it
                        for (Graph com : communities) {
                            if(((com.getNode(nodeId0))!=null) && ((com.getNode(nodeId1))!=null)){
                                try{
                                    System.out.println("node affected=="+nodeId0+";"+nodeId1);
                                    com.addEdge(nodeId0+";"+nodeId1, nodeId0, nodeId1);
                                }catch(EdgeRejectedException | IdAlreadyInUseException e){
                                    System.out.println("node affected=="+nodeId0+";"+nodeId1+"rejected");
                                }
                            }
                        }
                    }
                    //add the edges for each community
                    br.close();
                }
                
              }
              catch (Exception err) {
                err.printStackTrace();
              }
        //Read the file and extract communities
        
        return null;        
    }
    
    public String getExecutionLog() {
        String error = "";
        String line;
        try {
            while((line = this.error.readLine()) != null) {
                error = error + "\n" + line;
            }
        } catch (final IOException e) {
        }
        String output = "";
        try {
            while((line = this.op.readLine()) != null) {
                output = output + "\n" + line;
            }
        } catch (final IOException e) {
        }
        try {
            this.error.close();
            this.op.close();
        } catch (final IOException e) {
        }
        return "exitVal: " + this.exitVal + ", error: " + error + ", output: " + output;
    }

    @Override
    public String getName() {
        return "CONGA";
    }

    @Override
    public String getShortName() {
        return "CONGA";
    }

    @Override
    public LinkedList<Graph> findCommunities(String filePath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
