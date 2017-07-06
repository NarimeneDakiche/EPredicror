/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection;

import Attributes.AttributesComputer;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author ado_k
 */
public class CommunityDetectionStats {

    public static double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    public static double calculateAverageDouble(List<Double> marks) {
        double sum = 0;
        if (!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }

    private static LinkedList<TimeFrame> readDynamicNetwork(String file) {
        /*Read file of structure: "n1 att1 n2 att2 t g" */
        LinkedList<TimeFrame> dynamicNet = new LinkedList<TimeFrame>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] str = sCurrentLine.split(" ");
                int snp = Integer.parseInt(str[2]);
                int comm = Integer.parseInt(str[3]);

                while (dynamicNet.size() <= snp) {
                    dynamicNet.add(new TimeFrame(new LinkedList<Graph>()));
                }
                while (dynamicNet.get(snp).getCommunities().size() <= comm) {
                    Graph group = new SingleGraph("");
                    group.setStrict(false);
                    group.setAutoCreate(true);
                    dynamicNet.get(snp).getCommunities().add(group);
                }

                dynamicNet.get(snp).getCommunities().get(comm).addEdge(str[0] + ";" + str[1], str[0], str[1]);
//                        dynamicNet.get(snp).getCommunities().get(comm).getEdge(str[0] + ";" + str[2]).getSourceNode().setAttribute("bcentrality", Double.parseDouble(str[1]));
//                        dynamicNet.get(snp).getCommunities().get(comm).getEdge(str[0] + ";" + str[2]).getTargetNode().setAttribute("bcentrality", Double.parseDouble(str[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dynamicNet;
    }

    public static void main(String[] args) throws IOException {
        List<String> selectedAttributes = new ArrayList<>();
        selectedAttributes.add("averageClusteringCoefficient");
        ObservableList<String> observableListAttibutes = FXCollections.observableList(selectedAttributes);

        LinkedList<TimeFrame> o = CommunityDetectionStats.readDynamicNetwork("communities_links");
        AttributesComputer.calculateAttributes(o, observableListAttibutes);
        int[] commeTailleList = new int[7];

        List<Integer> size = new ArrayList<Integer>();
        List<Integer> nbCommPerSnap = new ArrayList<Integer>();
        List<Double> averageClusteringCoefficient = new ArrayList<Double>();
        int commSum = 0;
        for (TimeFrame tf : o) {
            nbCommPerSnap.add(tf.getCommunities().size());
            commSum += tf.getCommunities().size();
            for (Graph com : tf.getCommunities()) {
                averageClusteringCoefficient.add(com.getAttribute("averageClusteringCoefficient"));
                int commTaille = com.getNodeCount();
                size.add(commTaille);

                if (commTaille <= 5) {
                    commeTailleList[0]++;
                } else if (commTaille <= 10) {
                    commeTailleList[1]++;
                } else if (commTaille <= 15) {
                    commeTailleList[2]++;
                } else if (commTaille <= 20) {
                    commeTailleList[3]++;
                } else if (commTaille <= 50) {
                    commeTailleList[4]++;
                } else if (commTaille <= 100) {
                    commeTailleList[5]++;
                } else {
                    commeTailleList[6]++;
                }

            }
        }

        //DecimalFormat df = new DecimalFormat("#.###");
        //System.out.print(df.format(d));
        System.out.println("Taille moyenne de communauté: " + calculateAverage(size));
        System.out.println("Nombre moyen de communautés par snapshot: " + calculateAverage(nbCommPerSnap));
        System.out.println("Nombre total de communautés: " + commSum);
        System.out.println("Coefficent de clustering moyen: " + calculateAverageDouble(averageClusteringCoefficient));
        for (int i = 0; i < commeTailleList.length; i++) {
            System.out.print(commeTailleList[i] + "\t");
        }
    }

}
