package org.example.server;

import java.util.ArrayList;

class OctreeNode {
    /*
     * Utterly Implemented but have no time
     * */

    public static int leafCount;
    private static long minRef = Long.MAX_VALUE;
    public static ArrayList<OctreeNode> leaves = new ArrayList<OctreeNode>(); // Create an ArrayList object

    public static ArrayList<Px> palette = new ArrayList<Px>(); // Create an ArrayList object

    private OctreeNode parent;
    private OctreeNode[] childs = null;
    public int level; // 0-7
    private long references; //
    private long red; //
    private long green;
    private long blue;
    public int ind;
    public int pR;
    public int pG;
    public int pB;

    @Override
    public String toString() {
        return "OctreeNode{" +
                "level=" + level +
                ", references=" + references +
                ", red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", ind=" + ind +
                ", pR=" + pR +
                ", pG=" + pG +
                ", pB=" + pB +
                '}';
    }


    public OctreeNode(int level) {
        this.level = level;
        this.childs = new OctreeNode[8];
    }
    // Now we insert all colors into the tree each time we found the leaf of
    // the tree we increment the reference count and
    // add the color value to the red, green and blue counters

    // add node
    // node is parent
    public void addNode(OctreeNode node, int r, int g, int b, int currentLevel) {
        // sample bits
        int bitInd = 7 - currentLevel;
        int mask = 1 << bitInd;
        int rInd = ((r & mask) >> bitInd) << 2;
        int gInd = ((g & mask) >> bitInd) << 1;
        int bInd = (b & mask) >> bitInd;
        int ind = rInd + gInd + bInd;
        // System.out.println("Ind: " + int2binStrInd(ind) + ", node level: " + node.level + ", Current level: " + currentLevel);
        // if (currentLevel == 0) {
        //     System.out.print("Root node ");
        //     System.out.println("Child ind: " + int2binStrInd(ind));
        // }
        if (currentLevel == 7) {
            // System.out.println("Leaf parent node");
            // System.out.println("Child ind: " + int2binStrInd(ind));
            if (node.childs[ind] == null) {
                node.childs[ind] = new OctreeNode(node.level + 1);
                node.childs[ind].ind = ind;
                node.childs[ind].parent = node;
            }
            // System.out.println(node.childs[ind].toString());
            leafCount++;
            node.childs[ind].references++;
            node.childs[ind].red += r;
            node.childs[ind].green += g;
            node.childs[ind].blue += b;
            leaves.add(node.childs[ind]);
            // System.out.println(node.childs[ind].toString());
            // addNode(node.childs[ind], r, g, b, currentLevel + 1);
            return;
        } else {
            // System.out.println("Inner node");
            // System.out.println("Child ind: " + int2binStrInd(ind));
            if (node.childs[ind] == null) {
                node.childs[ind] = new OctreeNode(node.level + 1);
                node.childs[ind].ind = ind;
                node.childs[ind].parent = node;
            }
            addNode(node.childs[ind], r, g, b, currentLevel + 1);
        }
    }

    public static void dfs(OctreeNode node) {
        for (int i = 0; i < 8; i++) {
            if (node != null) {
                System.out.println(node.toString());
                // if references > 0 leaf node
                if (node.references > 0) {
                    //System.out.println(node.toString());
                    return;
                }
                dfs(node.childs[i]);
            }
        }
    }

    // find min
    // how to reduce
    // search the node, where the sum of the childs references is minimal and reduce it.
    // reduce without metric
    // reduce all depths
    public static void reduceTree(OctreeNode node) {
        if (node.references > 0) {
            // System.out.println(node.toString());
            // node.parent.childs[node.ind] = null;
            if (node.parent == null) return;
            node.parent.references += node.references;
            node.parent.red += node.red;
            node.parent.green += node.green;
            node.parent.blue += node.blue;
            node.parent.childs[node.ind] = null;
            OctreeNode.leafCount--;
            return;
        }

        for (int i = 0; i < 8; i++) {
            if (node.childs[i] != null) {
                reduceTree(node.childs[i]);
            }
        }
    }

    public static void buildPalette(OctreeNode node){
        // System.out.println("debug");
        for (int i = 0; i < 8; i++) {
            if (node != null) {
                // if references > 0 leaf node
                if (node.references > 0) {
                    // System.out.println(node.toString());
                    // System.out.println("palette upgraded");
                    double[] clr = new double[3];
                    // B G R
                    clr[0] = node.blue / node.references;
                    clr[1] = node.green / node.references;
                    clr[2] = node.red / node.references;

                    node.pB = (int) clr[0];
                    node.pG = (int) clr[1];
                    node.pR = (int) clr[2];

                    Px tmp = new Px(clr);
                    palette.add(tmp);
                    return;
                }
                buildPalette(node.childs[i]);
            }
        }

    }

    public static int[] getQtzdColor(OctreeNode node){
        int[] ret = null;


        return null;
    }

    /*
     * For testing purposes
     * */
    public static void main(String[] args) {
        OctreeNode root = new OctreeNode(0);
        root.parent = null;
        // int r = 0b10011011;
        // int g = 0b01010100;
        // int b = 0b01110110;
        // System.out.println("r: " + int2binStrP(r));
        // System.out.println("g: " + int2binStrP(g));
        // System.out.println("b: " + int2binStrP(b));

        root.addNode(root, 0b10011011, 0b01010100, 0b1110110, 0);
        root.addNode(root, 0b10011011, 0b01010100, 0b1110110, 0);
        root.addNode(root, 0b10011001, 0b01010101, 0b1110110, 0);
        root.addNode(root, 0b10011001, 0b01010100, 0b1110110, 0);
        root.addNode(root, 0b10011001, 0b01010100, 0b1110110, 0);
        root.addNode(root, 0b10011001, 0b01010100, 0b1110110, 0);
        root.addNode(root, 0b10011101, 0b01010100, 0b1010110, 0);

        System.out.println("DFS");
        dfs(root);
        System.out.println(leafCount);

        // System.out.println("minRef");
        // findMinRef(root);
        // System.out.println(minRef);

        System.out.println("reduceTree");
        reduceTree(root);

        System.out.println("DFS");
        dfs(root);
    }

    public static String int2binStrP(int i) {
        return String.format("%08d", Integer.parseInt(Integer.toBinaryString(i)));
    }

    public static String int2binStrInd(int i) {
        return String.format("%03d", Integer.parseInt(Integer.toBinaryString(i)));
    }

    public static String int2binStr(int i) {
        return Integer.toBinaryString(i);
    }


}

