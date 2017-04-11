/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedList;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author ado_k
 */
public class MainCPM {

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

        graph.addEdge("A;B", "A", "B");
        graph.addEdge("B;C", "B", "C");
        graph.addEdge("C;A", "C", "A");
        graph.addEdge("A;D", "A", "D");
        graph.addEdge("D;E", "D", "E");
        graph.addEdge("D;F", "D", "F");
        graph.addEdge("E;F", "E", "F");
        graph.display();
        CPM cpm = new CPM();
        LinkedList<Graph> linkedList = cpm.execute(graph,3);
        for (Graph gTmp : linkedList) {
            gTmp.display();
        }

    }
}
