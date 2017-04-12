/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.algorithm.Centroid;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.measure.ClosenessCentrality;
import org.graphstream.algorithm.measure.DegreeCentrality;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author ado_k
 */
public class MainCPM_1 {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, UnsupportedEncodingException, SQLException {
        Graph graph = new SingleGraph("Betweenness Test");

        //    E----D  AB=1, BC=5, CD=3, DE=2, BE=6, EA=4  
        //   /|    |  Cb(A)=4
        //  / |    |  Cb(B)=2
        // A  |    |  Cb(C)=0
        //  \ |    |  Cb(D)=2
        //   \|    |  Cb(E)=4
        //    B----C
       /* Node A = graph.addNode("A");
         Node B = graph.addNode("B");
         Node E = graph.addNode("E");
         Node C = graph.addNode("C");
         Node D = graph.addNode("D");

         graph.addEdge("A;B", "A", "B");
         graph.addEdge("B;E", "B", "E");
         //graph.addEdge("B;D", "B", "D");
         graph.addEdge("B;C", "B", "C");
         graph.addEdge("E;D", "E", "D");
         graph.addEdge("C;D", "C", "D");
         graph.addEdge("A;E", "A", "E");*/
        graph.setStrict(false);
        graph.setAutoCreate(true);
        // graph.display();

        //g.display();
        CPM cpm = new CPM();
        String fileName = "co-authorship_graph_cond-mat_small.txt";
        Graph g = cpm.readCommunityFile("etc/" + fileName);
        System.out.println(g.getNodeCount());
        LinkedList<Graph> linkedList = cpm.execute(g, 5);

        boolean exportResults = true;

        if (exportResults) {
            String dir = "CPM";
            File myOutputDir = new File(dir);
            if (!myOutputDir.exists()) {
                myOutputDir.mkdir();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(dir + "\\" + fileName));
            for (Graph gTmp : linkedList) {
                for (Node n : gTmp.getEachNode()) {
                    out.write(n.getId().toString() + " ");
                }
                out.write("\n");
            }
            out.close();
        }

        System.out.println(linkedList.size() + " communities detected.");
        for (Graph x : linkedList) {
            /*System.out.println(x.getNodeCount() + " " + x.getEdgeCount());
             Iterator<Node> it = x.getNodeIterator();
             while (it.hasNext()) {
             Node n1 = it.next();
             System.out.print(n1.getId() + ", ");
             }
             System.out.println("");*/
            AttributesComupter.calculateAttributes(g, x);
            Iterator<Node> it = x.getNodeIterator();
            String cb = "", centroid = "";
            while (it.hasNext()) {
                Node n1 = it.next();
                cb += n1.getAttribute("bcentrality") + " ";
                centroid += n1.getAttribute("ccentrality") + " ";
            }
            System.out.println("Bc: " + cb);
            System.out.println("Cc: " + centroid);
            //System.out.println(x.getAttribute("reciprocity").toString());
            //System.out.println(x.getAttribute("cohesion").toString());
            //System.out.println("cohesion");
        }
    }


}
