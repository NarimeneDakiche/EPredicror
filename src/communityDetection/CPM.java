/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// last modif 11/04/2017
package communityDetection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javafx.concurrent.Task;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author ado_k
 */
public class CPM {

    private int k = 3;
    GenQueue<TreeSet<Node>> bk = new GenQueue<TreeSet<Node>>();
    private Set<Set<Node>> cliques = new HashSet<Set<Node>>();

    public static Graph readCommunityFile(String file) {
        Graph g = new SingleGraph("");
        g.setStrict(false);
        g.setAutoCreate(true);
        String sCurrentLine;
        String[] splitContent;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((sCurrentLine = br.readLine()) != null) {
                splitContent = sCurrentLine.split(" ");
                g.addEdge(splitContent[0] + ";" + splitContent[1], splitContent[0], splitContent[1]);
            }
        } catch (Exception e) {

        }
        return g;
    }

    public LinkedList<Graph> execute(Graph g, int k, Task task) {
        System.out.println("CPM");
        setK(k);
        TreeSet<Node> tmp;
        Collection<Node> nSet = g.getNodeSet();
        List<Node> list = new ArrayList(nSet);
        Node n;
        System.out.println("Started");
        for (int i = 0; i < nSet.size(); i++) {
            n = list.get(i);
            if (n.getDegree() >= k - 1) {
                tmp = new TreeSet<Node>(new SortByID());
                tmp.add(n);
                bk.enqueue(tmp);
            }
        }

        //Now start the iterative process for finding cliques
        System.out.println("Finding cliques");
        tmp = bk.dequeue();

        while (tmp != null) {
            if (task.isCancelled()) {
                return null;
            } else {
                //Search for bk+1
                Node vi = tmp.last(); //(Node) getLastElement(tmp);
                Vector<Node> largerIndexes = getLargerIndexNodes(g, vi);
                //System.out.println("larger: " + largerIndexes.size());
                for (Node vj : largerIndexes) {
                    TreeSet<Node> Bk1 = new TreeSet<Node>(new SortByID());
                    Bk1.addAll(tmp); //Clone current bk into bk+1
                    Bk1.add(vj);
                    //System.out.println("Bk1.size():" + Bk1.size() + "   " + checkBk1IsClique(g, Bk1));
                    if (Bk1.size() <= getK() && checkBk1IsClique(g, Bk1)) { //
                        if (Bk1.size() == getK()) { //A clique of size k found. Finish expanding this bk+1 here.
                            cliques.add(Bk1);
                        } else if (Bk1.size() < getK()) {
                            bk.enqueue(Bk1); //k should be checked for finding cliques of size k.
                        } else { //Clique with larger size will be omitted.
                            System.out.println("What are you doing here :o");
                        }
                    }
                }
                tmp = bk.dequeue(); //Check next item
                //    System.out.println(bk.size());
            }
        }
        //Algorithm finished.

        System.out.println("Cliques found: " + cliques.size());

        int nID = 0;
        //Set<Node> nodes = new HashSet<Node>();
        Graph newGraph = new SingleGraph("");

        for (Set<Node> firstClique : cliques) { //Create the nodes
            //Node firstNode = gm.factory().newNode(String.valueOf(nID++));
            String nodeLabel = "";
            for (Node n1 : firstClique) {
                nodeLabel += n1.getId() + ",";
            }
            nodeLabel = nodeLabel.substring(0, nodeLabel.length() - 1); //remove last ,
            //firstNode.setAttribute("nodesIDs", nodeLabel);
            newGraph.addNode(Integer.toString(nID));
            newGraph.getNode(Integer.toString(nID++)).setAttribute("nodesIDs", nodeLabel);
        }

        //HashSet<Edge> edges = new HashSet<Edge>();
        /*int i = 0;
         float j = -1;
         DecimalFormat decimalFormat = new DecimalFormat("#.##");*/
        System.out.println("Cliques graph created, creating edges...");
        List<Node> listCliques = new ArrayList<Node>(newGraph.getNodeSet());
//        Node vi, vj;
        for (int i = 0; i < listCliques.size() - 1; i++) {
            Node vi = listCliques.get(i);
            for (int j = i + 1; j < listCliques.size(); j++) {
                if (task.isCancelled()) {
                    return null;
                } else {
                    Node vj = listCliques.get(j);
                    if (getSharedNodes(vi, vj) == k - 1) {
                        if (newGraph.getEdge(vj.getId() + ";" + vi.getId()) == null) { // care
                            // System.out.println(vi.getId() + " " + vj.getId());
                            newGraph.addEdge(vi.getId() + ";" + vj.getId(), vi.getId(), vj.getId());
                        }
                    }
                }
            }
        }
//        for (Node vi : newGraph.getNodeSet()) {
//            for (Node vj : newGraph.getNodeSet()) {
//                if ((!vi.getId().equals(vj.getId())) && (getSharedNodes(vi, vj) == k - 1)) {
//                    if (newGraph.getEdge(vj.getId() + ";" + vi.getId()) == null) { // care
//                        // System.out.println(vi.getId() + " " + vj.getId());
//                        newGraph.addEdge(vi.getId() + ";" + vj.getId(), vi.getId(), vj.getId());
//                    }
//                }
//            }
//            /*float actualK = Float.valueOf(decimalFormat.format((float) 100 * i++ / nodes.size()));
//             if (j != actualK) {
//             j = actualK;
//             System.out.print(Float.valueOf(decimalFormat.format(j)) + "%, ");
//             }*/
//        }
        System.out.println("done.\nConnected components and finishing...");
        TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
        tscc.init(newGraph);
        tscc.compute();
        //System.out.println(tscc.);

        // Add to each node of the second graph its attribute
        for (Node n2 : newGraph.getEachNode()) {
            // System.out.println(n2.getAttribute(tscc.getSCCIndexAttribute()).toString());
            //n2.addAttribute("group", n2.getAttribute(tscc.getSCCIndexAttribute().toString()));
            n2.setAttribute("group", n2.getAttribute(tscc.getSCCIndexAttribute()).toString());
        }
        /*ConnectedComponents cc = new ConnectedComponents(newGraph);
         cc.compute();*/

        //g.clear();
        LinkedList<Graph> finalGraph = new LinkedList<>();

        // Create finalGraph's nodes 
        for (Node vi : newGraph.getEachNode()) {
            while (finalGraph.size() <= Integer.parseInt(vi.getAttribute("group").toString())) {
                finalGraph.add(new SingleGraph(""));
            }
            String[] firstCliqueNodes = vi.getAttribute("nodesIDs").toString().split(",");
            for (String n1 : firstCliqueNodes) {
                finalGraph.get(Integer.parseInt(vi.getAttribute("group").toString()));
                if (finalGraph.get(Integer.parseInt(vi.getAttribute("group").toString())).getNode(n1) == null) {
                    finalGraph.get(Integer.parseInt(vi.getAttribute("group").toString())).addNode(n1);
                    //System.err.println(n1);
                }
                //System.err.println(n1);

                finalGraph.get(Integer.parseInt(vi.getAttribute("group").toString())).getNode(n1)
                        .setAttribute("group", vi.getAttribute(tscc.getSCCIndexAttribute()).toString());
                //System.out.println("group: " + n1 + " " + vi.getAttribute(tscc.getSCCIndexAttribute()).toString());
                //g.getNode(n1)
                //finalGraph.get(i).setEdges(getIEdges(vi, ));
            }
        }

        // Create finalGraph's edges
        for (Graph gTmp : finalGraph) {
            for (Edge edge : g.getEachEdge()) {
                if (gTmp.getNode(edge.getSourceNode().getId()) != null && gTmp.getNode(edge.getTargetNode().getId()) != null) { // care &&
                    // System.out.println("Hello");
                    gTmp.addEdge(edge.getId(), edge.getSourceNode().toString(), edge.getTargetNode().toString());
                }
            }
        }

        /*for (Node vi : g.getEachNode()) {
            
         finalGraph.get(vi.getAttribute("group")).addNode(vi);
         }*/

        /*for (i = 0; i < cc.getConnectedComponentsCount(); i++) {

         LinkedList<Node> list = value.get(i);
         for (Node vi : list) {
         String[] firstCliqueNodes = vi.getLabel().split(",");
         for (String n1 : firstCliqueNodes) {
         finalGraph.get(i).addNode(n1);
         //finalGraph.get(i).setEdges(getIEdges(vi, ));
         }
         }
         }*/
        System.out.println(finalGraph.size() + " communities detected");
        return finalGraph;
    }

    public LinkedList<Graph> execute(Graph g, int k) {
        System.out.println("CPM");
        setK(k);
        TreeSet<Node> tmp;
        Collection<Node> nSet = g.getNodeSet();
        List<Node> list = new ArrayList(nSet);
        Node n;
        System.out.println("Started");
        for (int i = 0; i < nSet.size(); i++) {
            n = list.get(i);
            if (n.getDegree() >= k - 1) {
                tmp = new TreeSet<Node>(new SortByID());
                tmp.add(n);
                bk.enqueue(tmp);
            }
        }

        //Now start the iterative process for finding cliques
        System.out.println("Finding cliques");
        tmp = bk.dequeue();

        while (tmp != null) {

            //Search for bk+1
            Node vi = tmp.last(); //(Node) getLastElement(tmp);
            Vector<Node> largerIndexes = getLargerIndexNodes(g, vi);
            //System.out.println("larger: " + largerIndexes.size());
            for (Node vj : largerIndexes) {
                TreeSet<Node> Bk1 = new TreeSet<Node>(new SortByID());
                Bk1.addAll(tmp); //Clone current bk into bk+1
                Bk1.add(vj);
                //System.out.println("Bk1.size():" + Bk1.size() + "   " + checkBk1IsClique(g, Bk1));
                if (Bk1.size() <= getK() && checkBk1IsClique(g, Bk1)) { //
                    if (Bk1.size() == getK()) { //A clique of size k found. Finish expanding this bk+1 here.
                        cliques.add(Bk1);
                    } else if (Bk1.size() < getK()) {
                        bk.enqueue(Bk1); //k should be checked for finding cliques of size k.
                    } else { //Clique with larger size will be omitted.
                        System.out.println("What are you doing here :o");
                    }
                }
            }
            tmp = bk.dequeue(); //Check next item
            //    System.out.println(bk.size());

        }
        //Algorithm finished.

        System.out.println("Cliques found: " + cliques.size());

        int nID = 0;
        //Set<Node> nodes = new HashSet<Node>();
        Graph newGraph = new SingleGraph("");

        for (Set<Node> firstClique : cliques) { //Create the nodes
            //Node firstNode = gm.factory().newNode(String.valueOf(nID++));
            String nodeLabel = "";
            for (Node n1 : firstClique) {
                nodeLabel += n1.getId() + ",";
            }
            nodeLabel = nodeLabel.substring(0, nodeLabel.length() - 1); //remove last ,
            //firstNode.setAttribute("nodesIDs", nodeLabel);
            newGraph.addNode(Integer.toString(nID));
            newGraph.getNode(Integer.toString(nID++)).setAttribute("nodesIDs", nodeLabel);
        }

        //HashSet<Edge> edges = new HashSet<Edge>();
        /*int i = 0;
         float j = -1;
         DecimalFormat decimalFormat = new DecimalFormat("#.##");*/
        System.out.println("Cliques graph created, creating edges...");
        List<Node> listCliques = new ArrayList<Node>(newGraph.getNodeSet());
//        Node vi, vj;
        for (int i = 0; i < listCliques.size() - 1; i++) {
            Node vi = listCliques.get(i);
            for (int j = i + 1; j < listCliques.size(); j++) {
                Node vj = listCliques.get(j);
                if (getSharedNodes(vi, vj) == k - 1) {
                    if (newGraph.getEdge(vj.getId() + ";" + vi.getId()) == null) { // care
                        // System.out.println(vi.getId() + " " + vj.getId());
                        newGraph.addEdge(vi.getId() + ";" + vj.getId(), vi.getId(), vj.getId());
                    }
                }

            }
        }
//        for (Node vi : newGraph.getNodeSet()) {
//            for (Node vj : newGraph.getNodeSet()) {
//                if ((!vi.getId().equals(vj.getId())) && (getSharedNodes(vi, vj) == k - 1)) {
//                    if (newGraph.getEdge(vj.getId() + ";" + vi.getId()) == null) { // care
//                        // System.out.println(vi.getId() + " " + vj.getId());
//                        newGraph.addEdge(vi.getId() + ";" + vj.getId(), vi.getId(), vj.getId());
//                    }
//                }
//            }
//            /*float actualK = Float.valueOf(decimalFormat.format((float) 100 * i++ / nodes.size()));
//             if (j != actualK) {
//             j = actualK;
//             System.out.print(Float.valueOf(decimalFormat.format(j)) + "%, ");
//             }*/
//        }
        System.out.println("done.\nConnected components and finishing...");
        TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
        tscc.init(newGraph);
        tscc.compute();
        //System.out.println(tscc.);

        // Add to each node of the second graph its attribute
        for (Node n2 : newGraph.getEachNode()) {
            // System.out.println(n2.getAttribute(tscc.getSCCIndexAttribute()).toString());
            //n2.addAttribute("group", n2.getAttribute(tscc.getSCCIndexAttribute().toString()));
            n2.setAttribute("group", n2.getAttribute(tscc.getSCCIndexAttribute()).toString());
        }
        /*ConnectedComponents cc = new ConnectedComponents(newGraph);
         cc.compute();*/

        //g.clear();
        LinkedList<Graph> finalGraph = new LinkedList<>();

        // Create finalGraph's nodes 
        for (Node vi : newGraph.getEachNode()) {
            while (finalGraph.size() <= Integer.parseInt(vi.getAttribute("group").toString())) {
                finalGraph.add(new SingleGraph(""));
            }
            String[] firstCliqueNodes = vi.getAttribute("nodesIDs").toString().split(",");
            for (String n1 : firstCliqueNodes) {
                finalGraph.get(Integer.parseInt(vi.getAttribute("group").toString()));
                if (finalGraph.get(Integer.parseInt(vi.getAttribute("group").toString())).getNode(n1) == null) {
                    finalGraph.get(Integer.parseInt(vi.getAttribute("group").toString())).addNode(n1);
                    //System.err.println(n1);
                }
                //System.err.println(n1);

                finalGraph.get(Integer.parseInt(vi.getAttribute("group").toString())).getNode(n1)
                        .setAttribute("group", vi.getAttribute(tscc.getSCCIndexAttribute()).toString());
                //System.out.println("group: " + n1 + " " + vi.getAttribute(tscc.getSCCIndexAttribute()).toString());
                //g.getNode(n1)
                //finalGraph.get(i).setEdges(getIEdges(vi, ));
            }
        }

        // Create finalGraph's edges
        for (Graph gTmp : finalGraph) {
            for (Edge edge : g.getEachEdge()) {
                if (gTmp.getNode(edge.getSourceNode().getId()) != null && gTmp.getNode(edge.getTargetNode().getId()) != null) { // care &&
                    // System.out.println("Hello");
                    gTmp.addEdge(edge.getId(), edge.getSourceNode().toString(), edge.getTargetNode().toString());
                }
            }
        }

        /*for (Node vi : g.getEachNode()) {
            
         finalGraph.get(vi.getAttribute("group")).addNode(vi);
         }*/

        /*for (i = 0; i < cc.getConnectedComponentsCount(); i++) {

         LinkedList<Node> list = value.get(i);
         for (Node vi : list) {
         String[] firstCliqueNodes = vi.getLabel().split(",");
         for (String n1 : firstCliqueNodes) {
         finalGraph.get(i).addNode(n1);
         //finalGraph.get(i).setEdges(getIEdges(vi, ));
         }
         }
         }*/
        System.out.println(finalGraph.size() + " communities detected");
        return finalGraph;
    }

    private Vector<Node> getLargerIndexNodes(Graph g, Node vi) {

        Vector<Node> output = new Vector<Node>();
        /*for (Edge ed/ge : g.getEdges()) {
         System.out.println(g.getEdge(edge.getSource(), edge.getTarget())!=null);
         }*/

        for (Node n : g.getNodeSet()) {
            if (n.getId().toString().compareTo(vi.getId().toString()) > 0) {
                //System.out.println("compare: " + n.getId().toString().compareTo(vi.getId().toString()));
                //System.out.println(n.getId().toString()+" "+vi.getId().toString()+ " " + n.getId().toString().compareTo(vi.getId().toString()) + "n.getId().toString() + ";" + vi.getId().toString()) +" "+ (vi.getId().toString() + ";" + n.getId().toString());
                if ((g.getEdge(n.getId().toString() + ";" + vi.getId().toString()) != null) || (g.getEdge(vi.getId().toString() + ";" + n.getId().toString()) != null)) {//  {   // || g.getEdge(vi, n) != null
                    //TODO check degree of n and vi
                    //System.out.println("Hello");
                    output.addElement(n);
                }
            }
        }
        //System.out.println(output.size());
        return output;
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

    public class SortByID implements Comparator<Node> {

        @Override
        public int compare(Node n1, Node n2) {
            if (n1.getId().toString().compareTo(n2.getId().toString()) > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    private boolean checkBk1IsClique(Graph g, TreeSet<Node> Bk1) {
        for (Node firstNode : Bk1) {
            for (Node secondNode : Bk1) {
                if (firstNode == secondNode) {
                    continue;
                }
                //System.out.println(g.getEdge(firstNode.getId() + ";" + secondNode.getId()) + "+" + firstNode.getId() + ";" + secondNode.getId());
                if ((g.getEdge(firstNode.getId() + ";" + secondNode.getId()) == null) && (g.getEdge(secondNode.getId() + ";" + firstNode.getId()) == null)) { //One edge is missing in the Bk+1 clique () && g.getEdge(secondNode, firstNode) == null) {// 
                    return false;
                }

            }
        }
        // System.out.println("Damn yes!");
        return true;
    }

    private int getSharedNodes(Node vi, Node vj) {
//        String[] firstCliqueNodes = vi.getAttribute("nodesIDs").toString().split(",");
//        String[] secondCliqueNodes = vj.getAttribute("nodesIDs").toString().split(",");
//        //String[] secondCliqueNodes = vj.getLabel().split(",");
        List<String> nodes1 = new ArrayList<String>(Arrays.asList(vi.getAttribute("nodesIDs").toString().split(",")));
        List<String> nodes2 = new ArrayList<String>(Arrays.asList(vj.getAttribute("nodesIDs").toString().split(",")));

        nodes1.retainAll(nodes2);

        return nodes1.size();
//        int sharedNodes = 0;
//
//        for (String n1 : firstCliqueNodes) {
//            for (String n2 : secondCliqueNodes) {
//                if (n1.equals(n2)) {
//                    sharedNodes++;
//                    break;
//                }
//            }
//        }
//
//        return sharedNodes;

    }
}
