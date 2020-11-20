package org.example.client;

import org.apache.commons.io.FileUtils;
import org.example.shared.QuantizationServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.rmi.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientCLI {

    private QuantizationServer mcServer;

    public static void main(String[] args) throws Exception {
        ClientCLI cli = new ClientCLI();
    }

    public ClientCLI() throws RemoteException, NotBoundException, FileNotFoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        mcServer = (QuantizationServer) registry.lookup("MedianCutServer");

        String fileName = "mq9.jpg";
        String filePath = "./assets/" + fileName;

        byte[] imgBytes = null;

        File inpFile = new File(filePath);
        try {
            imgBytes = FileUtils.readFileToByteArray(inpFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] output = medianCutQtz(imgBytes, fileName, 1);
        long delta = mcServer.getCalcTimeDelta();
        System.out.println("Delta: " + delta);

        BufferedImage outputBufImg = byteArrtoBufferedImage(output);
        BasicDisplayer disp = new BasicDisplayer();
        disp.addImage(outputBufImg);
    }

    public byte[] medianCutQtz(byte[] imgBytes, String fileName, int quantizationLevel) throws RemoteException {
        return mcServer.medianCutQtz(imgBytes, fileName, quantizationLevel);
    }


    private BufferedImage byteArrtoBufferedImage(byte[] byteArrInp){
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
