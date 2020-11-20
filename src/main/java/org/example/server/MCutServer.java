package org.example.server;

import org.example.shared.QuantizationServer;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *  Median Cut Server
 */

public class MCutServer implements QuantizationServer {
    long calcTimeDelta;

    // Run server
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        // create and put server to registry
        QuantizationServer mCutServer = new MCutServer();
        Registry registry =  LocateRegistry.createRegistry(1099);
        registry.bind("MedianCutServer",mCutServer);
        // if (System.getSecurityManager() == null)
        // System.setSecurityManager(new RMISecurityManager());
    }

    public MCutServer() throws  RemoteException{
        // TODO RMI hello world
        // 0 default port
        UnicastRemoteObject.exportObject(this, 0);
        System.out.println("Median Cut Server Started");
    }

    // run mc and return processed file
    @Override
    public byte[] medianCutQtz(byte[] imgBytes, String fileName, int quantizationLevel) throws RemoteException {
        System.out.println("Filename: " + fileName + ", " + " Qtz level: " + quantizationLevel);
        long startTime = System.currentTimeMillis();

        MedianCut mc = new MedianCut();
        byte[] ret = mc.getQuantizedImage(imgBytes, fileName, quantizationLevel);

        long endTime = System.currentTimeMillis();
        calcTimeDelta = (endTime - startTime);
        return ret;
    }

    @Override
    public long getCalcTimeDelta() throws RemoteException {
        return calcTimeDelta;
    }
}
