
import SnapshotsPrep.*;
import communityDetection.AttributesComupter;
import communityDetection.CPM;
import evolutionIdentification.GED1;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author ado_k
 */
public class DataSet {

    public static void main(String[] args) throws FileNotFoundException, ParseException, UnsupportedEncodingException, IOException, SQLException, InterruptedException {
        // TODO code application logic here

        //snapp.getSplitSnapshots(null, 3, null);
        MyResult myResult = new MyResult();
        myResult.getResults("etc/out.dnc-temporalGraph", null, "VWXT", " ");
        System.out.println(TimeLength.timestampToDate(myResult.getMaxTS()) + "***" + TimeLength.timestampToDate(myResult.getMinTS()));

        SnapshotsPrep snapp = new SnapshotsPrep();
        Duration d = Duration.ofDays(10);
        List<Duration> listDuration = new ArrayList<Duration>();
        //listDuration.add(Duration.ofDays(500));
        listDuration.add(Duration.ofDays(900));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));

        int nbSnap = snapp.getSplitSnapshots("etc/out.dnc-temporalGraph", listDuration,
                null, "VWXT", " ", "export", false, true, "gml");

        LinkedList<TimeFrame> dynamicNetwork = new LinkedList<>();
        List<Graph> graphs = new ArrayList<Graph>();
        //System.out.println(dynamicNetwork.size());
        for (int i = 0; i < nbSnap; i++) { //for (int i = 0; i < nbSnap; i++) {
            // reading files 

            graphs.add(readCommunity("Ec" + i + ".txt"));
            System.out.println("file " + "Ec" + i + ".txt was read");
            System.out.println(graphs.get(i).getNodeCount() + " nodes were read");
            CPM cpm = new CPM();
            LinkedList<Graph> communities = cpm.execute(graphs.get(i), 15);
            if (communities.size() > 0) {
                dynamicNetwork.add(new TimeFrame(communities));
            }

            for (TimeFrame timeFrame : dynamicNetwork) {
                BetweennessCentrality bcb = new BetweennessCentrality();
                //bcb.setWeightAttributeName("weight");
                for (Graph com : timeFrame.getCommunities()) {
                    AttributesComupter.calculateAttributes(timeFrame.getTimGraph(), com);
                    bcb.init(com);
                    bcb.compute();
                }
            }
        }
        System.out.println("Detection finished. GED was called.");
        GED1 ged = new GED1();
        ged.excuteGED(dynamicNetwork, 50, 50);

    }

    private static Graph readCommunity(String file) {
        Graph g = new SingleGraph("");
        g.setStrict(false);
        g.setAutoCreate(true);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                String[] str = sCurrentLine.split(" ");
                g.addEdge(str[0] + ";" + str[1], str[0], str[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return g;

    }

}
