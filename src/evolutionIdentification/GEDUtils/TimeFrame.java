package evolutionIdentification.GEDUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    public TimeFrame(LinkedList<Graph> communities) {
        setCommunities(communities);
        this.timGraph.setStrict(false);
        //this.timGraph.setAutoCreate(true);

        for (Graph com : communities) {
            for (Node node : com.getNodeSet()) {
                timGraph.addNode(node.getId());
                //System.out.println("Nodes added: "+node.getId());
            }
            for (Edge edge : com.getEdgeSet()) {
                try {
                    timGraph.addEdge(edge.getId(), (Node) edge.getSourceNode(), (Node) edge.getTargetNode());
                    //timGraph.addEdge(edge.getId(), (Node) edge.getSourceNode(), (Node) edge.getTargetNode(), edge.isDirected());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //System.out.println(Integer.parseInt(splitContent[0])+ " "+ Integer.parseInt(splitContent[1]));
            }
        }
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

}
