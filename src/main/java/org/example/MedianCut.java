package org.example;

import java.util.ArrayList;


import org.opencv.core.*;
import org.opencv.imgcodecs.*;

public class MedianCut {

    private Mat img;
    private int sortChannel;
    private ArrayList<double[]> p8List = new ArrayList<double[]>();
    public ArrayList<double[]> avrP8List = new ArrayList<double[]>();

    public Mat output;

    public MedianCut(String imgFilePath, int depth) {
        img = Imgcodecs.imread(imgFilePath);
        findSortChannel();
        p8List.sort((double[] p1, double[] p2) -> {
            if (p1[sortChannel] < p2[sortChannel])
                return 1;
            if (p1[sortChannel] > p2[sortChannel])
                return -1;
            return 0;
        });

        divideBuckets(p8List,depth,1);
        // TODO repaint image
    }

    public void divideBuckets(ArrayList<double[]> list, int baseDepth, int currDepth) {
        ArrayList<double[]> upper = new ArrayList<double[]>(list.subList(0, list.size() / 2));
        ArrayList<double[]> lower = new ArrayList<double[]>(
                list.subList((list.size() / 2), list.size()));

        System.out.print(currDepth);
        System.out.print(baseDepth);
        System.out.println();
        if (currDepth == baseDepth){
            this.avrP8List.add(bucketAverage(list));
            return;
        }
        divideBuckets(upper,baseDepth,currDepth+1);
        divideBuckets(lower,baseDepth,currDepth+1);
    }

    public double[] bucketAverage(ArrayList<double[]> bucket) {
        double bucketAvr[] =  new double[3];
        for (int i = 0; i < bucket.size() ; i++) {
            bucketAvr[0] += bucket.get(i)[0];
            bucketAvr[1] += bucket.get(i)[1];
            bucketAvr[2] += bucket.get(i)[2];
        }
        bucketAvr[0] /= bucket.size();
        bucketAvr[1] /= bucket.size();
        bucketAvr[2] /= bucket.size();

        System.out.println(bucketAvr[2]);
        System.out.println(bucketAvr[1]);
        System.out.println(bucketAvr[0]);
        return bucketAvr;
    }


    // find largest Diff and make pixels a list
    private void findSortChannel() {
        double Rmin = 255;
        double Rmax = 0;
        double Gmin = 255;
        double Gmax = 0;
        double Bmin = 255;
        double Bmax = 0;

        int rows = img.rows();
        int cols = img.cols();
        int ch = img.channels();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] data = img.get(i, j); //Stores element in an array
                p8List.add(data);
                for (int k = 0; k < ch; k++) //Runs for the available number of channels
                {
                    double B = data[0];
                    double G = data[1];
                    double R = data[2];
                    if (B > Bmax) Bmax = B;
                    if (G > Gmax) Gmax = G;
                    if (R > Rmax) Rmax = R;
                    if (B < Bmin) Bmin = B;
                    if (G < Gmin) Gmin = G;
                    if (R < Rmin) Rmin = R;
                }
            }
        }
        double Bdiff = Bmax - Bmin;
        double Gdiff = Gmax - Gmin;
        double Rdiff = Rmax - Rmin;

        int sChannel = 0;
        if (Bdiff > Gdiff && Bdiff > Rdiff) {
            sChannel = 0;
        }
        if (Gdiff > Rdiff && Gdiff > Bdiff) {
            sChannel = 1;
        }
        if (Rdiff > Bdiff && Rdiff > Gdiff) {
            sChannel = 2;
        }
        this.sortChannel = sChannel;
    }
}


