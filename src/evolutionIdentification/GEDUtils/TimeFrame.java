package evolutionIdentification.GEDUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

/**
 *
 * @author ado_k
 */
public class TimeFrame {

    Graph timGraph = new SingleGraph("TimeFrame");
    List<Graph> communities = new ArrayList<>();

    public TimeFrame(Graph timGraph) {
        this.timGraph = timGraph;
        this.communities = null;
    }

    public String getColors(int i) {
        String str = "";
        for (int j = 0; j < i; j++) {
            float f = (float) 1 / i;
            str += String.format("%.3f", f) + ",";

        }
        return str.substring(0, str.length() - 1);
    }

    /* public String getColors(String int i) {
     String str = "";
     for (int j = 0; j < i; j++) {
     str += (float) 1 / i + ",";
     }
     return str.substring(0, str.length() - 1);
     }
     */
    public TimeFrame(LinkedList<Graph> communities) {
        setCommunities(communities);
        this.timGraph.setStrict(false);
        this.timGraph.setAutoCreate(true);

        Random rand = new Random();
        List<String> colors = new ArrayList<String>();
        String colorsIO = "";
        for (int i = 0; i < 10; i++) {
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();

            Color c1 = new Color(r, g, b);
            float hue = rand.nextFloat();
            // Saturation between 0.1 and 0.3
            float saturation = (rand.nextInt(2000) + 1000) / 10000f;
            float luminance = 0.9f;
            //Color c1 = Color.getHSBColor(hue, saturation, luminance);
            int r1 = c1.getRed();
            int g1 = c1.getGreen();
            int b1 = c1.getBlue();
            colors.add("rgb(" + r1 + "," + g1 + "," + b1 + ")");
            colorsIO += colors.get(i) + ",";
        }
        colorsIO = colorsIO.substring(0, colorsIO.length() - 1);
        colorsIO += ";";

        for (Graph com : communities) {
            for (Node node : com.getNodeSet()) {
                Node n = (Node) timGraph.getNode(node.getId());
                if (n == null) {
                    timGraph.addNode(node.getId());
                    n = (Node) timGraph.getNode(node.getId());
                    n.addAttribute("comm", new ArrayList<String>());
                }

                List<String> nbComm = (List<String>) n.getAttribute("comm");
                nbComm.add(colors.get(communities.indexOf(com)));

                n.setAttribute("comm", nbComm);
                //System.out.println(n.getId() + " " + n.getAttribute("comm")+" "+ nbComm);

                n.setAttribute("ui.style", "shape:pie-chart;fill-color:" + getColorsRGB(nbComm));
                System.out.println(getColorsRGB(nbComm));
                n.setAttribute("ui.pie-values", getColors(nbComm.size()));
                // System.out.println("Nodes added: "+node.getId());

                /*
                 * System.setProperty("org.graphstream.ui.renderer",
                 * "org.graphstream.ui.j2dviewer.J2DGraphRenderer"); Graph
                 * Karate = new SingleGraph("Karate");
                 * Karate.read("karate.gml");
                 * Karate.getNode("33").setAttribute("ui.style",
                 * "shape:pie-chart;fill-color:rgb(127,0,55),rgb(255,0,110),rgb(1,127,1);"
                 * ); Karate.getNode("33").setAttribute("ui.pie-values",
                 * "0.333,0.333,0.333");
                 * Karate.getNode("34").setAttribute("ui.style",
                 * "shape:pie-chart; fill-color: rgb(255,0,110), rgb(0,255,1);"
                 * ); Karate.getNode("34").setAttribute("ui.pie-values",
                 * "0.5,0.5"); Karate.getNode(0).setAttribute("ui.style",
                 * "shape:pie-chart; fill-color: rgb(255,0,110), rgb(0,255,1);"
                 * ); Karate.getNode(0).setAttribute("ui.pie-values",
                 * "0.5,0.5"); 
                 */
            }
            for (Edge edge : com.getEdgeSet()) {
                try {
                    timGraph.addEdge(edge.getId(), edge.getSourceNode().getId(), edge.getTargetNode().getId());

                    // timGraph.addEdge(edge.getId(), (Node)
                    // edge.getSourceNode(), (Node) edge.getTargetNode(),
                    // edge.isDirected());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // System.out.println(Integer.parseInt(splitContent[0])+ " "+
                // Integer.parseInt(splitContent[1]));
            }
            System.out.println("timGraphCount:" + timGraph.getNodeCount());
            System.out.println("getEdgeCount():" + timGraph.getEdgeCount());

        }
        this.timGraph.addAttribute("ui.quality");
        this.timGraph.addAttribute("ui.antialias");
        this.timGraph.addAttribute("ui.stylesheet", "url('style.css')");
        this.timGraph.display();

    }

    public Graph getTimGraph() {
        return timGraph;
    }

    public List<Graph> getCommunities() {
        return communities;
    }

    public void setCommunities(LinkedList<Graph> communities) {
        this.communities.addAll(communities);
    }

    private String getColorsRGB(List<String> colors, int nbComm) {
        String strin = "";
        for (int i = 0; i < nbComm; i++) {
            strin += colors.get(i) + ",";

        }
        strin = strin.substring(0, strin.length() - 1);
        strin += ";";

        return strin;
    }

    private String getColorsRGB(List<String> nbComm) {
        String strin = "";
        for (int i = 0; i < nbComm.size(); i++) {
            strin += nbComm.get(i) + ",";
        }
        strin = strin.substring(0, strin.length() - 1);
        strin += ";";
        return strin;
    }

}
