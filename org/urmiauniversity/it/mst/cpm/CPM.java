package org.urmiauniversity.it.mst.cpm;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.ConnectedComponents;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;

/**
 *
 * @author Account
 */
public class CPM implements LongTask {

    private String report = "";
    private boolean cancel = false;

    private ProgressTicket progressTicket;
    private int k = 5;
    private Set<Set<Node>> Cliques = new HashSet<Set<Node>>();
    GenQueue<TreeSet<Node>> Bk = new GenQueue<TreeSet<Node>>();

    public class SortByID implements Comparator<Node> {

        @Override
        public int compare(Node n1, Node n2) {
            if (Float.parseFloat(n1.getId().toString()) > Float.parseFloat(n2.getId().toString())) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Queue Implementation">
    public Object getLastElement(final Collection c) {
        /*
         final Iterator itr = c.iterator();
         Object lastElement = itr.next();
         while (itr.hasNext()) {
         lastElement = itr.next();
         }
         return lastElement;
         */
        return null;
    }

    class GenQueue<E> {

        private LinkedList<E> list = new LinkedList<E>();

        public void enqueue(E item) {
            list.addLast(item);
        }

        public E dequeue() {
            return list.pollFirst();
        }

        public boolean hasItems() {
            return !list.isEmpty();
        }

        public int size() {
            return list.size();
        }

        public void addItems(GenQueue<? extends E> q) {
            while (q.hasItems()) {
                list.addLast(q.dequeue());
            }
        }
    }
    //</editor-fold>

    private Vector<Node> getLargerIndexNodes(Graph g, Node vi) {

        Vector<Node> output = new Vector<Node>();
        /*for (Edge ed/ge : g.getEdges()) {
         System.out.println(g.getEdge(edge.getSource(), edge.getTarget())!=null);
         }*/
        for (Node n : g.getNodes()) {
            if (Float.parseFloat(n.getId().toString()) > Float.parseFloat(vi.getId().toString())) {
                //System.out.println(n.getId().toString() + " "+vi.getId().toString() );

                if (g.getEdge(n, vi) != null) {   // || g.getEdge(vi, n) != null
                    //TODO check degree of n and vi
                    //System.out.println("Hello");
                    output.addElement(n);
                }
            }
        }
        return output;
    }

    private boolean checkBk1IsClique(Graph g, TreeSet<Node> Bk1) {
        for (Node firstNode : Bk1) {
            for (Node secondNode : Bk1) {
                if (firstNode == secondNode) {
                    continue;
                }
                if (g.getEdge(firstNode, secondNode) == null) { //One edge is missing in the Bk+1 clique () && g.getEdge(secondNode, firstNode) == null
                    return false;
                }
            }
        }

        return true;
    }

    public LinkedList<LinkedList<Node>> execute(GraphModel gm, boolean directed) {
        Graph g;
        if (directed) {
            g = gm.getDirectedGraph();
        } else {
            g = gm.getUndirectedGraph();
        }

       /* for (Edge e : g.getEdges()) {
            System.out.println(e.getSource().getId() + " -> " + e.getTarget().getId());
        }*/

        g.readLock();
        //Firstly add each node as an item in Bk
        TreeSet<Node> tmp;
        for (Node n : g.getNodes()) {
            //Trick: if the node's degree is less than k-1, it can not involve in k-clique
            if (g.getDegree(n) >= k - 1) {
                tmp = new TreeSet<Node>(new SortByID());
                tmp.add(n);
                Bk.enqueue(tmp); //Add the B1 (node itself) to the queue
            }
        }

        //Now start the iterative process for finding cliques
        tmp = Bk.dequeue();

        while (tmp != null) {

            if (cancel) {
                //Empty variables
                Bk.list.clear();
                tmp.clear();
                Cliques.clear();
                return null;
            }

            //Search for Bk+1
            Node vi = tmp.last(); //(Node) getLastElement(tmp);
            Vector<Node> largerIndexes = getLargerIndexNodes(g, vi);

            for (Node vj : largerIndexes) {
                TreeSet<Node> Bk1 = new TreeSet<Node>(new SortByID());
                Bk1.addAll(tmp); //Clone current Bk into Bk+1
                Bk1.add(vj);
                if (Bk1.size() <= getK() && checkBk1IsClique(g, Bk1)) { //
                    //System.out.println("XX");
                    if (Bk1.size() == getK()) { //A clique of size k found. Finish expanding this Bk+1 here.
                        Cliques.add(Bk1);
                    } else if (Bk1.size() < getK()) {
                        Bk.enqueue(Bk1); //k should be checked for finding cliques of size k.
                    } else { //Clique with larger size will be omitted.
                        report += "\nLarger Clique Found. It should not be here\n";
                    }
                }
            }

            tmp = Bk.dequeue(); //Check next item

        }
        g.readUnlock();
        //Algorithm finished.

        System.out.println("Nb cliques: " + Cliques.size());
        //Write the output
        report += "Clique Detection started. \nNodes with <b>" + (k - 1) + "</b> edges will not be included.";
        report += "Found Cliques of size " + getK() + ".\nNow making new graph ...\nClearing old graph ...";

        //edit the graph
        g.clear();
        report += " [+]\nCreating new nodes ...";

        gm = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        
        int nID = 0;
        Set<Node> nodes = new HashSet<Node>();

        for (Set<Node> firstClique : Cliques) { //Create the nodes
            Node firstNode = gm.factory().newNode(String.valueOf(nID++));
            String nodeLabel = "";

            for (Node n : firstClique) {
                nodeLabel += n.getLabel() + ",";
            }

            nodeLabel = nodeLabel.substring(0, nodeLabel.length() - 1); //remove last ,
            firstNode.setLabel(nodeLabel);

            nodes.add(firstNode);
        }

        report += "[+]\nDetecting and creating the edges ...";
        HashSet<Edge> edges = new HashSet<Edge>();

        for (Node vi : nodes) {
            for (Node vj : nodes) {
                if ((vi != vj) && (getSharedNodes(vi, vj) == k - 1)) {
                    if (g.isDirected()) {
                        edges.add(gm.factory().newEdge(vi, vj, 1, true));
                    } else {
                        edges.add(gm.factory().newEdge(vi, vj, 1, false));
                    }
                }
            }
        }

        report += "[+]\nRedrawing new graph ...\n";
        for (Node n : nodes) {
            g.addNode(n);
        }
        //System.out.println(nodes.size());

        for (Edge e : edges) {
            g.addEdge(e);
        }

        //report += "[+]\nDone!\n\n\nPalla, Gergely, Imre Derényi, Illés Farkas, and Tamás Vicsek. \"Uncovering the overlapping community structure of complex networks in nature and society.\" Nature 435, no. 7043 (2005): 814-818";
        //System.out.println(report);
        //System.out.println(edges.size());
        ConnectedComponents cp = new ConnectedComponents();
        cp.execute(gm);
        //for (int i : cp.getComponentsSize())   System.out.println(cp.getConnectedComponentsCount() +" "+ i);
        System.out.println("Nb comm: " + cp.computeWeeklyConnectedComponents(g, cp.createIndiciesMap(g)).size());
        return cp.computeWeeklyConnectedComponents(g, cp.createIndiciesMap(g));

    }

    private int getSharedNodes(Node vi, Node vj) {
        String[] firstCliqueNodes = vi.getLabel().split(",");
        String[] secondCliqueNodes = vj.getLabel().split(",");

        int sharedNodes = 0;

        for (String n1 : firstCliqueNodes) {
            for (String n2 : secondCliqueNodes) {
                if (n1.equals(n2)) {
                    sharedNodes++;
                }
            }
        }

        return sharedNodes;
    }

    public String getReport() {
        return report;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket pt) {
        this.progressTicket = pt;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }
}
