package org.example.client;

import org.apache.commons.io.FileUtils;
import org.example.shared.QuantizationServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.rmi.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.SplittableRandom;

public class ClientCLI {

    private QuantizationServer mcServer;
    private QuantizationServer oServer; // TODO

    public static void main(String[] args) throws Exception {
        ClientCLI cli = new ClientCLI();
        // TODO implement cli
        // TODO parse args

        // String filePath = "./assets/as.jpg";
        String filePath = null;
        int qLevel = 0;
        int qLevelInp = 0;

        Scanner scan = new Scanner(System.in);
        for (; ; ) {
            System.out.print("file> ");
            filePath = scan.next();
            System.out.print("Qtz level> ");
            qLevelInp = scan.nextInt();
            System.out.print("Qtz Type(MC:  1, Octree: 2)> ");
            int qType = scan.nextInt();

            if (qType == 1) {
                // map qlevel to implementation
                switch (qLevelInp) {
                    case 8:
                        qLevel = 3;
                        break;
                    case 16:
                        qLevel = 4;
                        break;
                    case 32:
                        qLevel = 5;
                        break;
                    case 64:
                        qLevel = 6;
                        break;
                    case 128:
                        qLevel = 7;
                        break;
                    case 256:
                        qLevel = 8;
                        break;
                    default:
                        qLevel = 1;
                }
                System.out.println(qLevel);
                cli.runMedianCut(filePath, qLevel);

            } else if (qType == 2) {
                switch (qLevelInp) {
                    case 8:
                        qLevel = 1;
                        break;
                    case 16:
                        qLevel = 2;
                        break;
                    case 32:
                        qLevel = 3;
                        break;
                    case 64:
                        qLevel = 4;
                        break;
                    case 128:
                        qLevel = 5;
                        break;
                    case 256:
                        qLevel = 6;
                        break;
                    default:
                        qLevel = 1;
                }

                System.out.println(qLevel);
                cli.runOctree(filePath, qLevel);

            }
        }

    }

    public void runOctree(String filePath, int qLevel) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1100);
        oServer = (QuantizationServer) registry.lookup("OctreeServer");

        File inpFile = new File(filePath);
        String fileName = inpFile.getName();
        byte[] imgBytes = null;
        try {
            imgBytes = FileUtils.readFileToByteArray(inpFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<int[]> output = octreeColorQtz(imgBytes, fileName, qLevel);
        long delta = oServer.getCalcTimeDelta();
        System.out.println("Delta: " + delta);
        BasicDisplayer disp = new BasicDisplayer();
        for (int i = 0; i < output.size(); i++) {
            int b = output.get(i)[0];
            int g = output.get(i)[1];
            int r = output.get(i)[2];
            disp.addColor(b, g, r, 0);
        }
    }

    public void runMedianCut(String filePath, int qLevel) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        mcServer = (QuantizationServer) registry.lookup("MedianCutServer");

        File inpFile = new File(filePath);
        String fileName = inpFile.getName();

        byte[] imgBytes = null;
        try {
            imgBytes = FileUtils.readFileToByteArray(inpFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] output = medianCutQtz(imgBytes, fileName, qLevel);
        long delta = mcServer.getCalcTimeDelta();
        System.out.println("Delta: " + delta);

        BufferedImage outputBufImg = byteArrtoBufferedImage(output);
        BasicDisplayer disp = new BasicDisplayer();

        disp.addImage(outputBufImg);
    }

    public ClientCLI() throws Exception {

    }

    public byte[] medianCutQtz(byte[] imgBytes, String fileName, int quantizationLevel) throws RemoteException {
        return mcServer.Qtz(imgBytes, fileName, quantizationLevel);
    }

    public ArrayList<int[]> octreeColorQtz(byte[] imgBytes, String fileName, int quantizationLevel) throws RemoteException {
        return oServer.QtzColor(imgBytes, fileName, quantizationLevel);
    }


    public static BufferedImage byteArrtoBufferedImage(byte[] byteArrInp) {
        BufferedImage ret = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrInp);
            ret = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

}
