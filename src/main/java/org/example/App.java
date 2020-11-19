package org.example;

/**
 * Hello world!
 *
 */
import nu.pattern.OpenCV;

public class App
{
    public static void main( String[] args )
    {
        // load opencv so
        OpenCV.loadShared();

        // load image from fs
        String imgFilePath = "./assets/t34.jpg";
        MedianCut mc = new MedianCut(imgFilePath, 9);

        BasicDisplayer dips = new BasicDisplayer();
        for (int i = 0; i < mc.avrP8List.size(); i++) {
            double bgr[] = mc.avrP8List.get(i);
            int r = (int)bgr[2];
            int g = (int)bgr[1];
            int b = (int)bgr[0];
            dips.addColor(b,g,r,i);
        }
    }
}
