/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prefuse.demos;

import javax.swing.JFrame;
import prefuse.data.Graph;
import prefuse.data.Node;
import static prefuse.demos.AggregateDemo.demo;

/**
 *
 * @author ado_k
 */
public class NewClass {

    public static void main(String[] argv) {
        Graph g = new Graph();
        for (int i = 0; i < 4; ++i) {
            Node n1 = g.addNode();
            Node n2 = g.addNode();
            Node n3 = g.addNode();
            g.addEdge(n1, n2);
            g.addEdge(n1, n3);
            g.addEdge(n2, n3);
        }
        g.addEdge(0, 3);
        g.addEdge(3, 6);
        g.addEdge(6, 9);
        g.addEdge(9, 0);
        JFrame frame = demo(g);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
