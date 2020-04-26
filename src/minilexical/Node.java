/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minilexical;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Node {

    String name;
    boolean isTerminal;
    String father = "none";
    ArrayList<Node> childs = new ArrayList<>();

    Node(String program, String f, boolean t) {
        name = program;
        isTerminal = t;
        father = f;
    }

    void setFather(String f) {
        father = f;
    }

    Node() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    static int COUNT = 1;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";

    static void print(Node root) throws InterruptedException {
        if (root == null) {
            return;
        }

        // Standard level order traversal code 
        // using queue 
        Queue<Node> q = new LinkedList<>(); // Create a queue 
        q.add(root); // Enqueue root  

        while (!q.isEmpty()) {
            int n = q.size();
            // If this node has children 
            while (n > 0) {
                // Dequeue an item from queue 
                // and print it 
                Node p = q.poll();
                n--;
                if (p == null) {
                    continue;
                }
                if (p.isTerminal) {
                    System.out.print(BLUE + p.name + ANSI_RESET + "  ");
                } else {
                    System.out.print(GREEN + p.name + ANSI_RESET + "  ");
                }
                for (int i = 0; i < p.childs.size(); i++) {
                    q.add(p.childs.get(i));
                }

            }
            // Print new line between two levels 
            System.out.println("");

        }

    }

   public void printRec(Node x) {
        if (!x.isTerminal||x.childs.isEmpty()) 
            return;
        System.out.print(x.name + " -> ");
        for (int i = 0; i < x.childs.size(); i++) {
            if (x.childs.get(i) == null) 
                continue;
            
            if (x.childs.get(i).isTerminal) {
                    System.out.print(BLUE + x.childs.get(i).name  + ANSI_RESET + "  ");
                } else {
                    System.out.print(GREEN + x.childs.get(i).name  + ANSI_RESET + "  ");
                }
        }
        System.out.println("");
        for (int i = 0; i < x.childs.size(); i++) {
            if (x.childs.get(i) == null) 
                continue;
            
            printRec(x.childs.get(i));
        }
    }
}
