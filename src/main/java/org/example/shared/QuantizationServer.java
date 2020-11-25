package org.example.shared;

import java.awt.image.BufferedImage;
import java.io.File;
import java.rmi.*;
import java.util.ArrayList;

public interface QuantizationServer extends java.rmi.Remote {

    public byte[] Qtz(byte[] imgBytes, String fileName, int quantizationLevel)
            throws RemoteException;

    public ArrayList<int[]> QtzColor(byte[] imgBytes, String fileName, int quantizationLevel)
            throws RemoteException;

    public long getCalcTimeDelta()
            throws RemoteException;

}
