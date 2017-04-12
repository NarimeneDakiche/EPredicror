/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection.ExternMethods;

/**
 *
 * @author HADJER
 */

import graphclasses1.Community;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author HADJER
 * 
 * Overlapping by default (-ov 0 : to get non overlapping communities)
 * 
 * By default, GANXiSw is set for overlapping community detection. It runs once with ten different thresholds, 
 * including r ∈ {0.01, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5}
 * 
 * Since GANXiSw is non-deterministic process, usually you want to repeat several times and either record 
 * the best performance or take the average performance. For that use: -run numberIterations
 */
public class SLPA extends CommunityMiner{

    private BufferedReader error;
    private BufferedReader op;
    private int exitVal;
    String jarFilePath="\".\\LibDetection\\SLPA\\GANXiSw.jar\"";

    public LinkedList<Graph> findCommunities(String filePath,String filename) {
        // Create run arguments for the
        //LinkedList<Community> communities = null;
        LinkedList<Graph> communities=new LinkedList<>();
        System.out.println("entered");
        final List<String> actualArgs = new ArrayList<>();
        actualArgs.add(0, "java");
        actualArgs.add(1, "-jar");
        actualArgs.add(2, jarFilePath);
        actualArgs.add(3, "-i");
        actualArgs.add(4, "\""+filePath+"\"");//GraphPath
        actualArgs.add(5, "-d");
        actualArgs.add(6, "\".\\LibDetection\\SLPA\\output\"");
        actualArgs.add(7, "-r");
        actualArgs.add(8, "0.1");

        try {
            final Runtime re = Runtime.getRuntime();
            final Process command = re.exec(actualArgs.toArray(new String[0]));
            this.error = new BufferedReader(new InputStreamReader(command.getErrorStream()));
            this.op = new BufferedReader(new InputStreamReader(command.getInputStream()));
            // Wait for the application to Finish
            command.waitFor();
            this.exitVal = command.exitValue();
            if (this.exitVal != 0) {
                throw new IOException("Failed to execure jar, " + this.getExecutionLog());
            }else{
                System.out.println("Execution Competed1");
                //*Read resulted files and construct Communities**/
                //File file = new File("filePath");
                //System.out.println("------"+file.getPath());
                File f = new File(".\\LibDetection\\SLPA\\output\\"+"SLPA"+"w_"+filename+"_run"+"1"+"_r"+actualArgs.get(8)+"_v3"+"_T100.icpm");
                FileInputStream fis = new FileInputStream(f);
                //Construct BufferedReader from InputStreamReader
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                communities=new LinkedList<>();
                String line = null;
                int nbcomm=0;
                while ((line = br.readLine()) != null) {
                        System.out.println("comm:"+ nbcomm + " == "+line);
                        String[] nodes = line.split(" ");
                        System.out.println("The number of nodes is: " + nodes.length);
                        communities.add(new SingleGraph(""));
                        for (String nodeId : nodes) {
                            if((communities.get(nbcomm).getNode(nodeId))==null){
                                communities.get(nbcomm).addNode(nodeId);
                            }
                        }
                        nbcomm++;
                        //communities.add(new Community(new LinkedList(Arrays.asList(nodes)),null));
                }
                //add the edges for each community
                br.close();
                f.delete();
            }

        } catch (final IOException | InterruptedException e) {
            System.out.println("IO exception");
        }

	return communities;
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
        return "SLPA";
    }

    @Override
    public String getShortName() {
        return "SLPA";
    }

    @Override
    public LinkedList<Community> findCommunities(String filePath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

