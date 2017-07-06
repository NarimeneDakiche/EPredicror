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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
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
 *
 *  * 
 * This class executes the .jar of the community detection method SLPA in the tool GANXiSw. 
 * The method is not ours, it belongs to its developper
 * For further information, please refer to the application help file
 * 
 * Overlapping by default (-ov 0 : to get non overlapping communities)
 *
 * By default, GANXiSw is set for overlapping community detection. It runs once
 * with ten different thresholds, including r ∈ {0.01, 0.05, 0.1, 0.15, 0.2,
 * 0.25, 0.3, 0.35, 0.4, 0.45, 0.5}
 *
 * Since GANXiSw is non-deterministic process, usually you want to repeat
 * several times and either record the best performance or take the average
 * performance. For that use: -run numberIterations
 */
public class SLPA extends CommunityMiner {

    private BufferedReader error;
    private BufferedReader op;
    private int exitVal;
    String jarFilePath = "\".\\LibDetection\\SLPA\\GANXiSw.jar\"";

    @Override
    public String getName() {
        return "SLPA";
    }

    @Override
    public String getShortName() {
        return "SLPA";
    }

    public static String getfileName(String string) {
        Path p = Paths.get(string);
        String fileName = p.getFileName().toString();
        return (fileName.indexOf(".") >= 0) ? fileName.substring(0, fileName.indexOf(".")) : fileName;
    }

    public LinkedList<Graph> findCommunities(String filePath, int minSize) {
        // Create run arguments for the
        //LinkedList<Community> communities = null;
        String filename = SLPA.getfileName(filePath);
        LinkedList<Graph> communities = new LinkedList<>();
        System.out.println("entered");

        String newFilePath = ".\\LibDetection\\SLPA\\" + filename + ".ipairs";
        try {
            File source = new File(filePath);
            File dest = new File(newFilePath);
            Files.copy(source.toPath(), dest.toPath(), REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }

        final List<String> actualArgs = new ArrayList<>();
        actualArgs.add(0, "java");
        actualArgs.add(1, "-jar");
        actualArgs.add(2, jarFilePath);
        actualArgs.add(3, "-i");
        actualArgs.add(4, "\"" + newFilePath + "\"");//GraphPath
        actualArgs.add(5, "-d");
        actualArgs.add(6, "\".\\LibDetection\\SLPA\"");
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
            } else {
                //System.out.println("Execution Completed1");
                //*Read resulted files and construct Communities**/
                //File file = new File("filePath");
                //System.out.println("------"+file.getPath());
                File f = new File(".\\LibDetection\\SLPA\\" + "SLPA" + "w_" + filename + "_run" + "1" + "_r" + actualArgs.get(8) + "_v3" + "_T100.icpm");
                FileInputStream fis = new FileInputStream(f);
                //Construct BufferedReader from InputStreamReader
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                communities = new LinkedList<>();
                String line = null;
                int nbcomm = 0;
                while ((line = br.readLine()) != null) {
                    //System.out.println("comm:" + nbcomm + " == " + line);
                    String[] nodes = line.split(" ");
                    //System.out.println("The number of nodes is: " + nodes.length);
                    if (nodes.length >= minSize) {
                        communities.add(new SingleGraph(""));
                        for (String nodeId : nodes) {
                            if ((communities.get(nbcomm).getNode(nodeId)) == null) {
                                communities.get(nbcomm).addNode(nodeId);
                            }
                        }
                        nbcomm++;
                    }
                    //communities.add(new Community(new LinkedList(Arrays.asList(nodes)),null));
                }
                br.close();
                f.delete();

                //add the edges for each community
                f = new File(newFilePath);
                fis = new FileInputStream(f);
                //Construct BufferedReader from InputStreamReader
                br = new BufferedReader(new InputStreamReader(fis));

                line = null;
                //Read the file line by line and affect the edge to the community
                while ((line = br.readLine()) != null) {
                    //System.out.println("line: == " + line);
                    String[] nodes = line./*split(" +");*/replaceAll("(^\\s+|\\s+$)", "").split("\\s+");
//                    System.out.println("The number of nodes is: " + nodes.length);
//                    System.out.println("minsize:" + minSize);

                    String nodeId0 = new String(nodes[0]);
                    String nodeId1 = new String(nodes[1]);

                    //Search for the community that contains it
                    for (Graph com : communities) {
                        if (((com.getNode(nodeId0)) != null) && ((com.getNode(nodeId1)) != null)) {
                            try {
                                //System.out.println("node affected==" + nodeId0 + ";" + nodeId1);
                                com.addEdge(nodeId0 + ";" + nodeId1, nodeId0, nodeId1);
                            } catch (EdgeRejectedException | IdAlreadyInUseException e) {
                                //System.out.println("node affected==" + nodeId0 + ";" + nodeId1 + "rejected");
                            }
                        }
                    }

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
            while ((line = this.error.readLine()) != null) {
                error = error + "\n" + line;
            }
        } catch (final IOException e) {
        }
        String output = "";
        try {
            while ((line = this.op.readLine()) != null) {
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
    public LinkedList<Graph> findCommunities(String filePath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
