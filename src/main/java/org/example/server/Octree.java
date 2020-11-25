package org.example.server;

import nu.pattern.OpenCV;
import org.apache.commons.io.FileUtils;
import org.example.client.BasicDisplayer;
import org.example.client.ClientCLI;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Octree {

    private Mat img;
    private OctreeNode root;

    public Octree() {
        // load opencv
        OpenCV.loadShared();
        // OctreeNode root = new OctreeNode(0);
    }

    // todo run from psvm
    public ArrayList<int[]> getQuantizedColors(byte[] imgBytes, String imgFileName, int qLevel) {
        try (FileOutputStream fos = new FileOutputStream("./tmp/" + imgFileName)) {
            fos.write(imgBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // System.out.println(imgFileName);
        this.img = Imgcodecs.imread("./tmp/" + imgFileName);
        // System.out.println(img.cols());

        root = new OctreeNode(0);
        BuildTree();

        // TODO reduce with param
        qLevel = 8 - qLevel;
        for (int i = 0; i < qLevel; i++) {
            OctreeNode.reduceTree(root);
        }
        // OctreeNode.dfs(oct.root);
        // System.out.println("debug");
        OctreeNode.dfs(root);
        OctreeNode.buildPalette(root);
        // oct.paintOutput();
        // System.out.println(OctreeNode.palette.size());
        //System.out.println();

        ArrayList<int[]> ret = new ArrayList<int[]>(); // Create an ArrayList object
        for (int i = 0; i < OctreeNode.palette.size(); i++) {
            Px tmp = OctreeNode.palette.get(i);
            int[] retArr = new int[3];
            retArr[0] = (int) tmp.data[0];
            retArr[1] = (int) tmp.data[1];
            retArr[2] = (int) tmp.data[2];
            ret.add(retArr);
        }
        OctreeNode.palette.clear();
        return ret;
    }

    public static void main(String[] args) {
        Octree oct = new Octree();
        BasicDisplayer bd = new BasicDisplayer();

        // read file to byte array
        String fileName = "as.jpg";
        String filePath = "./assets/" + fileName;

        byte[] imgBytes = null;
        File inpFile = new File(filePath);
        try {
            imgBytes = FileUtils.readFileToByteArray(inpFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<int[]> palette = null; // Create an ArrayList object
        palette = oct.getQuantizedColors(imgBytes, fileName, 1);

        for (int i = 0; i < palette.size(); i++) {
            int[] data = palette.get(i);
            int b = data[0];
            int g = data[1];
            int r = data[2];
            // System.out.println(b);
            // System.out.println(g);
            // System.out.println(r);
            bd.addColor(b, g, r, 0);
        }

    }

    // build trees returns octree root node
    public void BuildTree() {
        int rows = img.rows();
        int cols = img.cols();
        // int ch = img.channels();
        int ch = 3;

        // add colors
        // traverse input mat
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] data = img.get(i, j); //Stores element in an array
                int B = (int) data[0];
                int G = (int) data[1];
                int R = (int) data[2];
                root.addNode(root, R, G, B, 0);
            }
        }
    }

    // public void paintOutput(){
    //     int rows = img.rows();
    //     int cols = img.cols();
    //
    //     for (int i = 0; i < rows; i++) {
    //         for (int j = 0; j < cols; j++) {
    //             double[] data = img.get(i, j); //Stores element in an array
    //             int imgB = (int) data[0];
    //             int imgG = (int) data[1];
    //             int imgR = (int) data[2];
    //
    //             // OctreeNode.getQtzdColor(this.root);
    //             //output.put(i, j, data);
    //         }
    //     }
    // }

}

