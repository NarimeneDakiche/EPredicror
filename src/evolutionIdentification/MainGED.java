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

            }
            System.out.println(cpm + " groups");
        }

        GED ged1 = new GED();
        ged1.excuteGED(network, 50, 50, "text");

    }
}
