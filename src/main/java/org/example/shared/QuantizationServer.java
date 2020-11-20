package org.example.shared;

import java.awt.image.BufferedImage;
import java.io.File;
import java.rmi.*;

public interface QuantizationServer extends java.rmi.Remote {

    public byte[] medianCutQtz(byte[] imgBytes, String fileName, int quantizationLevel)
            throws RemoteException;

    public long getCalcTimeDelta()
            throws RemoteException;

}
