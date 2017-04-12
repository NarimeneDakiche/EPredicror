/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection;

import java.util.Iterator;
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
public class AttributesComupter {

    public static void calculateAttributes(Graph g, Graph x) {
        // Creates an attribute = "centroid"
        /**
         * Can be added: ==> Shortest path algorithms (Dijkstra, A* Shortest
         * path algorithm, Betweenness Centrality **
         *
         */
        x.addAttribute("averageDegree", Toolkit.averageDegree(x));
        x.addAttribute("averageClusteringCoefficient", Toolkit.averageClusteringCoefficient(x));
        x.addAttribute("averageClusteringCoefficients", Toolkit.clusteringCoefficients(x));
        x.addAttribute("degreeAverageDeviation", Toolkit.degreeAverageDeviation(x));
        x.addAttribute("degreeDistribution", Toolkit.degreeDistribution(x));
        x.addAttribute("density", Toolkit.density(x));
        x.addAttribute("diameter", Toolkit.diameter(x));

        calculateBc(x);
        calculateCentroid(x);
        calculateCohesion(g, x);
        calculateLeadership(x);
        calculateReciprocity(x);
        calculateInOutTotalDegree(x);
        calculateClosenessCentrality(x);
    }

    private static void calculateBc(Graph x) {
        BetweennessCentrality bcb = new BetweennessCentrality("bcentrality");
        //bcb.setWeightAttributeName("weight");
        bcb.init(x);
        bcb.compute();
        // Creates an attribute = "Cb"
    }

    private static void calculateCentroid(Graph x) {
        APSP apsp = new APSP();
        apsp.init(x);
        apsp.compute();
        Centroid centroid = new Centroid();
        centroid.init(x);
        centroid.compute();
    }

    private static void calculateCohesion(Graph g, Graph x) {

        double a = 0, b = 0;
        int n = x.getNodeCount(), N = g.getNodeCount();
        for (Node n1 : x.getEachNode()) {
            if (n1.hasAttribute("weight")) {
                a += Double.parseDouble(n1.getAttribute("weight"));
            } else {
                a++;
            }
        }
        a /= n * (n - 1);
        for (Node n1 : g.getEachNode()) {
            if (n1.hasAttribute("weight")) {
                b += Double.parseDouble(n1.getAttribute("weight"));
            } else {
                b++;
            }
        }
        b /= N * (N - n);
        x.setAttribute("cohesion", a / b);
        // System.out.println(a + " " + b + " " + a / b + " " + n + " " + N);
    }

    private static void calculateLeadership(Graph x) {
        double sum = 0;
        int dmax = Toolkit.degreeDistribution(x).length - 1; // degree maximal 
        for (Node n : x.getEachNode()) {
            sum = sum + (dmax - n.getDegree());
            // System.out.println(n.getDegree());
        }
        //System.out.println( sum + " " + dmax + " " + sum / ((x.getNodeCount() - 2) * (x.getNodeCount() - 1)));
        x.setAttribute("leadership", sum / ((x.getNodeCount() - 2) * (x.getNodeCount() - 1)));
    }

    private static void calculateReciprocity(Graph x) {
        /**
         * *
         * Attention: this function is calculated only undirected graphs. For
         * directed graphs there must be edges in the two orientation to
         * increment r.*
         */

        double r = 0;
        for (Node n1 : x.getEachNode()) {
            for (Node n2 : x.getEachNode()) {
                if (x.getEdge(n1.getId() + ";" + n2.getId()) != null) {
                    r++;
                }
            }
        }
        r /= x.getEdgeCount();
        x.setAttribute("reciprocity", r);
    }

    private static void calculateInOutTotalDegree(Graph x) {
        double cpt1, cpt2;
        for (Node n : x.getEachNode()) {
            cpt1 = cpt2 = 0;
            Iterator it = x.getEdgeIterator();
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

    private static void calculateClosenessCentrality(Graph x) {
        ClosenessCentrality cc = new ClosenessCentrality("ccentrality");
        //bcb.setWeightAttributeName("weight");
        cc.init(x);
        cc.compute();
    }

    private static void calculateDegreeCentrality(Graph x) {
        DegreeCentrality dc = new DegreeCentrality();
        //bcb.setWeightAttributeName("weight");
        dc.init(x);
        dc.compute();

    }
}
