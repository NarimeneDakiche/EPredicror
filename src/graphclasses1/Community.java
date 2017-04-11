package graphclasses1;

import java.util.LinkedList;

/**
 *
 * @author ado_k
 */
public class Community {
    LinkedList<String> nodes;
    LinkedList<String> edges;

    public Community(LinkedList<String> nodes, LinkedList<String> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }
    
    public LinkedList<String> getNodes() {
        return nodes;
    }

    public void setNodes(LinkedList<String> nodes) {
        this.nodes = nodes;
    }

    public LinkedList<String> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<String> edges) {
        this.edges = edges;
    }
}
