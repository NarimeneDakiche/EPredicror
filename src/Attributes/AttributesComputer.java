/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Attributes;

import evolutionIdentification.GEDUtils.TimeFrame;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.algorithm.Centroid;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.measure.ClosenessCentrality;
import org.graphstream.algorithm.measure.DegreeCentrality;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 *
 * @author ado_k
 */
public class AttributesComputer {

    public static void calculateAttributes(LinkedList<TimeFrame> dynamicNetwork, List<String> listAttributes) {
        for (int k = 0; k < dynamicNetwork.size(); k++) {
            TimeFrame tf = dynamicNetwork.get(k);
            //TreeItem<String> child = new TreeItem<>(Integer.toString(k + 1));
            //System.out.println("comm: " + tf.getCommunities().size());
            for (Graph com : tf.getCommunities()) {
                //TreeItem<String> child2 = new TreeItem<>(Integer.toString(tf.getCommunities().indexOf(com) + 1));
                AttributesComputer.calculateAttributes(tf.getTimGraph(), com, listAttributes);
            }
        }
    }

    public static void calculateAttributes(Graph snap, Graph comm, List<String> listAttributes) {
        // Creates an attribute = "centroid"
        /**
         * Can be added: ==> Shortest path algorithms (Dijkstra, A* Shortest
         * path algorithm, Betweenness Centrality **
         *
         */
        if (listAttributes.contains("size")) {
            //System.out.println("calculating size...");
            int i = comm.getNodeCount();
            double d = i;
            comm.addAttribute("size", d);
        }
        if (listAttributes.contains("averageDegree")) {
            //System.out.println("calculating average degree...");
            comm.addAttribute("averageDegree", Toolkit.averageDegree(comm));
        }
        if (listAttributes.contains("averageClusteringCoefficient")) {
            //System.out.println("calculating averageClusteringCoefficient...");
            comm.addAttribute("averageClusteringCoefficient", Toolkit.averageClusteringCoefficient(comm));
        }
        /*if (listAttributes.contains("averageClusteringCoefficients")) {
            System.out.println("calculating averageClusteringCoefficient...");
            comm.addAttribute("averageClusteringCoefficients", Toolkit.clusteringCoefficients(comm));
        }*/
        if (listAttributes.contains("degreeAverageDeviation")) {
            //System.out.println("calculating degreeAverageDeviation...");
            comm.addAttribute("degreeAverageDeviation", Toolkit.degreeAverageDeviation(comm));
        }
        /*if (listAttributes.contains("degreeDistribution")) {
            System.out.println("calculating degreeDistribution...");
            comm.addAttribute("degreeDistribution", Toolkit.degreeDistribution(comm));
        }*/
        if (listAttributes.contains("density")) {
            //System.out.println("calculating density...");
            comm.addAttribute("density", Toolkit.density(comm));
        }
        if (listAttributes.contains("diameter")) {
            //System.out.println("calculating diameter...");
            comm.addAttribute("diameter", Toolkit.diameter(comm));
        }
        if (listAttributes.contains("Bc")) {
            //System.out.println("calculating Bc...");
            calculateBc(comm);
        }
//        System.out.println("calculating Centroid...");
//        calculateCentroid(comm);
        if (listAttributes.contains("Cohesion")) {
            System.out.println("calculating Cohesion...");
            calculateCohesion(snap, comm);
        }
        if (listAttributes.contains("Leadership")) {
            System.out.println("calculating Leadership...");
            calculateLeadership(comm);
        }
        if (listAttributes.contains("Reciprocity")) {
            System.out.println("calculating Reciprocity...");
            calculateReciprocity(comm);
        }
        if (listAttributes.contains("InOutTotalDegree")) {

            System.out.println("calculating InOut degree...");
            calculateInOutTotalDegree(comm);
        }
        if (listAttributes.contains("ClosenessCentrality")) {

            System.out.println("calculating Cc...");
            calculateClosenessCentrality(comm);
        }
        System.out.println("att cal done");

    }

    private static void calculateBc(Graph comm) {
        BetweennessCentrality bcb = new BetweennessCentrality("bcentrality");
        //bcb.setWeightAttributeName("weight");
        bcb.init(comm);
        bcb.compute();
        // Creates an attribute = "Cb"
    }

    private static void calculateCentroid(Graph comm) {
        APSP apsp = new APSP();
        apsp.init(comm);
        apsp.compute();
        Centroid centroid = new Centroid();
        centroid.init(comm);
        centroid.compute();
    }

    private static void calculateCohesion(Graph snap, Graph comm) {

        double a = 0, b = 0;
        int n = comm.getNodeCount(), N = snap.getNodeCount();
        for (Node n1 : comm.getEachNode()) {
            if (n1.hasAttribute("weight")) {
                a += Double.parseDouble(n1.getAttribute("weight"));
            } else {
                a++;
            }
        }
        a /= n * (n - 1);
        for (Node n1 : snap.getEachNode()) {
            if (n1.hasAttribute("weight")) {
                b += Double.parseDouble(n1.getAttribute("weight"));
            } else {
                b++;
            }
        }
        b /= N * (N - n);
        comm.setAttribute("cohesion", a / b);
        // System.out.println(a + " " + b + " " + a / b + " " + n + " " + N);
    }

    private static void calculateLeadership(Graph comm) {
        double sum = 0;
        int dmax = Toolkit.degreeDistribution(comm).length - 1; // degree maximal 
        for (Node n : comm.getEachNode()) {
            sum = sum + (dmax - n.getDegree());
            // System.out.println(n.getDegree());
        }
        //System.out.println( sum + " " + dmax + " " + sum / ((comm.getNodeCount() - 2) * (comm.getNodeCount() - 1)));
        comm.setAttribute("leadership", sum / ((comm.getNodeCount() - 2) * (comm.getNodeCount() - 1)));
    }

    private static void calculateReciprocity(Graph comm) {
        /**
         * *
         * Attention: this function is calculated only undirected graphs. For
         * directed graphs there must be edges in the two orientation to
         * increment r.*
         */

        double r = 0;
        for (Node n1 : comm.getEachNode()) {
            for (Node n2 : comm.getEachNode()) {
                if (comm.getEdge(n1.getId() + ";" + n2.getId()) != null) {
                    r++;
                }
            }
        }
        r /= comm.getEdgeCount();
        comm.setAttribute("reciprocity", r);
    }

    private static void calculateInOutTotalDegree(Graph comm) {
        double cpt1, cpt2;
        for (Node n : comm.getEachNode()) {
            cpt1 = cpt2 = 0;
            Iterator it = comm.getEdgeIterator();
            while (it.hasNext()) {
                org.graphstream.graph.Edge e = (org.graphstream.graph.Edge) it.next();
                if (e.getSourceNode().getId().equals(n.getId())) {
                    cpt1++;
                }
                if (e.getTargetNode().getId().equals(n.getId())) {
                    cpt2++;
                }
            }
            n.setAttribute("indegree", cpt1);
            n.setAttribute("outdegree", cpt2);
            n.setAttribute("totaldegree", cpt1 + cpt2);
        }
    }

    private static void calculateClosenessCentrality(Graph comm) {
        ClosenessCentrality cc = new ClosenessCentrality("ccentrality");
        //bcb.setWeightAttributeName("weight");
        cc.init(comm);
        cc.compute();
    }

    private static void calculateDegreeCentrality(Graph comm) {
        DegreeCentrality dc = new DegreeCentrality();
        //bcb.setWeightAttributeName("weight");
        dc.init(comm);
        dc.compute();

    }
}
