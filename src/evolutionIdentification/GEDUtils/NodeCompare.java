package evolutionIdentification.GEDUtils;

import java.util.Comparator;
import org.graphstream.graph.Node;

/**
 *
 * @author ado_k
 */
public class NodeCompare implements Comparator<Node> {

    public int compare(Node n1, Node n2) {
        return Double.compare(n1.getAttribute("bcentrality"), n2.getAttribute("bcentrality"));
    }
}
