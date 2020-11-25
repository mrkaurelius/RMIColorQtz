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

public class Octree {

    private Mat img;
    private Mat output;
    private OctreeNode root;

    public Octree() {
        // load opencv
        OpenCV.loadShared();
        OctreeNode root;
    }

    public byte[] getQuantizedImage(byte[] imgBytes, String imgFileName, int K) {
        // write file for opencv imread
        try (FileOutputStream fos = new FileOutputStream("./tmp/" + imgFileName)) {
            fos.write(imgBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.img = Imgcodecs.imread("./tmp/" + imgFileName);
        this.output = new Mat(img.rows(), img.cols(), img.type());

        // The principle of the octree algorithm is to sequentially read in the image.
        // Every color is then stored in an octree of depth 8 (every leaf at depth 8 represents a distinct color)
        // A limit of K (in this case K = 256) leaves is placed on the tree.
        // reference counting: a way to know wheter an object has other users

        this.root = new OctreeNode(0);
        byte[] ret = null;
        return ret;
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

    public static void main(String[] args) {
        String fileName = "as.jpg";
        String filePath = "./assets/" + fileName;
        BasicDisplayer disp = new BasicDisplayer();

        byte[] imgBytes = null;

        File inpFile = new File(filePath);
        try {
            imgBytes = FileUtils.readFileToByteArray(inpFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Octree oct = new Octree();
        oct.img = Imgcodecs.imread(filePath);
        oct.root = new OctreeNode(0);
        oct.BuildTree();


        OctreeNode.reduceTree(oct.root);
        OctreeNode.reduceTree(oct.root);
        OctreeNode.reduceTree(oct.root);
        OctreeNode.reduceTree(oct.root);
        OctreeNode.reduceTree(oct.root);
        OctreeNode.reduceTree(oct.root);
        // OctreeNode.reduceTree(oct.root);
        // OctreeNode.reduceTree(oct.root);

        // disp.addColor();
        // OctreeNode.dfs(oct.root);
        OctreeNode.dfs(oct.root);
        System.out.println("debug");

        OctreeNode.buildPalette(oct.root);
        OctreeNode.dfs(oct.root);

        // oct.paintOutput();
        // imwrite output

        System.out.println(OctreeNode.palette.size());

        for (int i = 0; i < oct.root.palette.size(); i++) {
            double[] data = oct.root.palette.get(i).data;
            int b = (int) data[0];
            int g = (int) data[1];
            int r = (int) data[2];
            // System.out.println(b);
            // System.out.println(g);
            // System.out.println(r);
            disp.addColor(b,g,r,0);
        }

    }

}

