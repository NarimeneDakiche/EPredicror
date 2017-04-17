package evolutionIdentification;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author ado_k
 */
public class MainGED {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, UnsupportedEncodingException, SQLException {
        /*Graph graph = new SingleGraph("Betweenness Test");

         //    E----D  AB=1, BC=5, CD=3, DE=2, BE=6, EA=4  
         //   /|    |  Cb(A)=4
         //  / |    |  Cb(B)=2
         // A  |    |  Cb(C)=0
         //  \ |    |  Cb(D)=2
         //   \|    |  Cb(E)=4
         //    B----C
         Node A = graph.addNode("A");
         Node B = graph.addNode("B");
         Node E = graph.addNode("E");
         Node C = graph.addNode("C");
         Node D = graph.addNode("D");

         graph.addEdge("A;B", "A", "B");
         graph.addEdge("B;E", "B", "E");
         graph.addEdge("B;C", "B", "C");
         graph.addEdge("E;D", "E", "D");
         graph.addEdge("C;D", "C", "D");
         graph.addEdge("A;E", "A", "E");

         CPM1 cpm = new CPM1();
         cpm.execute(graph);*/

        LinkedList<TimeFrame> network = new LinkedList<TimeFrame>();
        List<LinkedList<Graph>> communities = new ArrayList<LinkedList<Graph>>();

        try (BufferedReader br = new BufferedReader(new FileReader("etc/All_Data.txt"))) {
            LinkedList comms = new LinkedList();
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                // process the line.
                String[] splitContent = line.split(" ");

                //System.out.println(line);
                int group_id, num_timeframe;
                group_id = Integer.parseInt(splitContent[0]);
                num_timeframe = Integer.parseInt(splitContent[2]);
                while (num_timeframe >= communities.size()) {
                    communities.add(new LinkedList<Graph>());
                }
                while (group_id >= communities.get(num_timeframe).size()) {
                    communities.get(num_timeframe).add(new SingleGraph(""));
                }
                communities.get(num_timeframe).get(group_id).addNode(splitContent[1]);
                NumberFormat f = NumberFormat.getInstance(); // Gets a NumberFormat with the default locale, you can specify a Locale as first parameter (like Locale.FRENCH)
                double myNumber = f.parse(splitContent[3]).doubleValue();
                communities.get(num_timeframe).get(group_id).getNode(splitContent[1]).setAttribute("Cb", myNumber);

            }
            int cpm = 0;
            for (int i = 0; i < communities.size(); i++) {
                network.add(new TimeFrame(communities.get(i)));
                cpm += communities.get(i).size();
                /*System.out.println(network.size()+" "+network.get(i).getCommunities().size());
                 for (Graph g: communities.get(i)){
                 cpm+= g.getNodeCount();
                 }*/

            }
            System.out.println(cpm + " groups");
            // System.out.println(cpm);
        }

        GED1 ged1 = new GED1();
        ged1.excuteGED(network, 50, 50);

    }
}
