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

    Graph timGraph;
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
    List<Color> listColor = new ArrayList<Color>();

    public TimeFrame(LinkedList<Graph> communities) {
        setCommunities(communities);
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        this.timGraph = new SingleGraph("TimeFrame");
        this.timGraph.setStrict(false);
        this.timGraph.setAutoCreate(true);

        Random rand = new Random();
        List<String> colors = new ArrayList<String>();
        String colorsIO = "";
       
        listColor.add(new Color(200, 200, 0));
        listColor.add(new Color(255, 0, 127));
        
        listColor.add(new Color(0, 127, 255));
        listColor.add(new Color(188, 23, 230));
        listColor.add(new Color(0xFFFFB300));
        listColor.add(new Color(0xFF803E75));
        listColor.add(new Color(0xFFFF6800));
        listColor.add(new Color(0xFFA6BDD7));
        listColor.add(new Color(0xFFC10020));
        listColor.add(new Color(0xFFCEA262));
        listColor.add(new Color(0xFF817066));
        listColor.add(new Color(0xFF007D34));
        listColor.add(new Color(0xFFF6768E));
        listColor.add(new Color(0xFF00538A));
        listColor.add(new Color(0xFFFF7A5C));
        listColor.add(new Color(0xFF53377A));
        listColor.add(new Color(0xFFFF8E00));
        listColor.add(new Color(0xFFB32851));
        listColor.add(new Color(0xFFF4C800));
        listColor.add(new Color(0xFF7F180D));
        listColor.add(new Color(0xFF93AA00));
        listColor.add(new Color(0xFF593315));
        listColor.add(new Color(0xFFF13A13));
        listColor.add(new Color(0xFF232C16));

        generateColors(communities.size());

        for (Color c1 : listColor) {
            int r1 = c1.getRed();
            int g1 = c1.getGreen();
            int b1 = c1.getBlue();
            colors.add("rgb(" + r1 + "," + g1 + "," + b1 + ")");
            colorsIO += colors.get(colors.size() - 1) + ",";
        }
        
        colorsIO = colorsIO.substring(0, colorsIO.length() - 1);
        colorsIO += ";";

        for (Graph com : communities) {
            for (Node node : com.getNodeSet()) {
                Node n = (Node) timGraph.getNode(node.getId());
                if (n == null) {
                    n = timGraph.addNode(node.getId());
                    n.addAttribute("comm", new ArrayList<String>());
                }

                List<String> nbComm = (List<String>) n.getAttribute("comm");
                nbComm.add(colors.get(communities.indexOf(com)));
                
                n.setAttribute("ui.class", "marked");

                n.setAttribute("comm", nbComm);

                n.setAttribute("ui.style", "shape:pie-chart;fill-color:" + getColorsRGB(nbComm));
                n.setAttribute("ui.pie-values", getColors(nbComm.size()));
            }
            for (Edge edge : com.getEdgeSet()) {
                
                timGraph.addEdge(edge.getId(), edge.getSourceNode().getId(), edge.getTargetNode().getId());

            }
        }
        
        System.out.println("Hello");
        this.timGraph.addAttribute("ui.quality");
        this.timGraph.addAttribute("ui.antialias");
        this.timGraph.addAttribute("ui.stylesheet", "url('style.css')");

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

    private void generateColors(int size) {
        for (int i = 0; i < size; i++) {
            listColor.add(generateRandomColor(new Color(255, 255, 255)));
        }
    }

    private Color generateRandomColor(Color mix) {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        // mix the color
        if (mix != null) {
            red = (red + mix.getRed()) / 2;
            green = (green + mix.getGreen()) / 2;
            blue = (blue + mix.getBlue()) / 2;
        }

        Color color = new Color(red, green, blue);
        return color;
    }

}
