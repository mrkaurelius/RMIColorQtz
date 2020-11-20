package org.example.server;

import nu.pattern.OpenCV;
import org.apache.commons.io.FileUtils;
import org.example.client.BasicDisplayer;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MedianCut {

    private Mat img;
    private int sortChannel;
    private ArrayList<Px> PxList = new ArrayList<Px>();
    private Mat output;
    private ArrayList<double[]> avrP8List = new ArrayList<double[]>();

    public MedianCut(String imgFilePath, String outputFilePath, int depth) {
        // load opencv
        OpenCV.loadShared();

        this.img = Imgcodecs.imread(imgFilePath);
        this.output = new Mat(img.rows(), img.cols(), img.type());

        findSortChannel();

        this.PxList.sort((Px p1, Px p2) -> {
            if (p1.data[sortChannel] < p2.data[sortChannel])
                return 1;
            if (p1.data[sortChannel] > p2.data[sortChannel])
                return -1;
            return 0;
        });

        divideBuckets(PxList, depth + 1, 1);
        Imgcodecs.imwrite(outputFilePath, output);
    }

    public MedianCut() {
        // load opencv
        OpenCV.loadShared();
    }

    public byte[] getQuantizedImage(byte[] imgBytes, String imgFileName, int depth) {
        try (FileOutputStream fos = new FileOutputStream("./tmp/" + imgFileName)) {
            fos.write(imgBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.img = Imgcodecs.imread("./tmp/" + imgFileName);
        this.output = new Mat(img.rows(), img.cols(), img.type());
        findSortChannel();
        this.PxList.sort((Px p1, Px p2) -> {
            if (p1.data[sortChannel] < p2.data[sortChannel])
                return 1;
            if (p1.data[sortChannel] > p2.data[sortChannel])
                return -1;
            return 0;
        });
        divideBuckets(PxList, depth + 1, 1);
        Imgcodecs.imwrite("./tmp/" + imgFileName, output);

        byte[] ret = null;
        try {
            ret =FileUtils.readFileToByteArray(new File("./tmp/" + imgFileName));
        } catch (Exception e){
            e.printStackTrace();
        }
        // System.out.println(ret);
        return ret;
        // return readImage("./tmp/" + imgFileName);
    }

    public ArrayList<double[]> getAvrP8List() {
        return avrP8List;
    }

    public void divideBuckets(ArrayList<Px> list, int baseDepth, int currDepth) {
        ArrayList<Px> upper = new ArrayList<Px>(list.subList(0, list.size() / 2));
        ArrayList<Px> lower = new ArrayList<Px>(
                list.subList((list.size() / 2), list.size()));

        if (currDepth == baseDepth) {
            paintBucket(list);
            return;
        }
        divideBuckets(upper, baseDepth, currDepth + 1);
        divideBuckets(lower, baseDepth, currDepth + 1);
    }

    // mediancutu kullanilabilir hale getir.
    private void paintBucket(ArrayList<Px> bucket) {
        double bucketAvr[] = new double[3];
        for (int i = 0; i < bucket.size(); i++) {
            bucketAvr[0] += bucket.get(i).data[0];
            bucketAvr[1] += bucket.get(i).data[1];
            bucketAvr[2] += bucket.get(i).data[2];
        }
        bucketAvr[0] /= bucket.size();
        bucketAvr[1] /= bucket.size();
        bucketAvr[2] /= bucket.size();
        avrP8List.add(bucketAvr);

        for (int i = 0; i < bucket.size(); i++) {
            Px opx = bucket.get(i); // Original Px
            output.put(opx.x, opx.y, bucketAvr);
        }
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
                Px tmp = new Px(data, i, j);
                PxList.add(tmp);

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
        sortChannel = sChannel;
    }

    public static BufferedImage readImage(String filePath) {
        // System.out.println(filePath);
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}

/*
 *  Helper class
 * */
class Px {
    public double data[];
    public int x;
    public int y;

    public Px(double[] data, int x, int y) {
        this.data = data;
        this.x = x;
        this.y = y;
    }
}

class RunMedianCut {
    /*
     * main function for testing
     * */
    public static void main(String[] args) {
        // String inpImgFilePath = "./assets/dt.png";
        // String outImgFilePath = "./assets/output.jpg"; // file extension misleading !
        // MedianCut mc = new MedianCut(inpImgFilePath, outImgFilePath, 9);
        String fileName = "as.jpg";
        String filePath = "./assets/" + fileName;
        byte[] imgBytes = null;

        File f = new File(filePath);
        try {
            imgBytes = FileUtils.readFileToByteArray(f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MedianCut mc = new MedianCut();
        // BufferedImage output = mc.getQuantizedImage(imgBytes, fileName, 4);
        //
        // BasicDisplayer disp = new BasicDisplayer();
        // ArrayList<double[]> avrPxList = mc.getAvrP8List();
        // for (int i = 0; i < avrPxList.size(); i++) {
        //     double bgr[] = avrPxList.get(i);
        //     int r = (int) bgr[2];
        //     int g = (int) bgr[1];
        //     int b = (int) bgr[0];
        //     disp.addColor(b, g, r, i);
        // }
        //
        // // BufferedImage img = BasicDisplayer.readImage(outImgFilePath);
        // disp.addImage(output);
    }
}
