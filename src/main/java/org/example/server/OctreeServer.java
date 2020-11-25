package org.example.server;

import org.example.shared.QuantizationServer;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Median Cut Server
 */

public class OctreeServer implements QuantizationServer {
    long calcTimeDelta;

    // Run server
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        // create and put server to registry
        QuantizationServer mCutServer = new OctreeServer();
        Registry registry = LocateRegistry.createRegistry(1100);
        registry.bind("OctreeServer", mCutServer);
        // if (System.getSecurityManager() == null)
        // System.setSecurityManager(new RMISecurityManager());
    }

    public OctreeServer() throws RemoteException {
        // TODO RMI hello world
        // 0 default port
        UnicastRemoteObject.exportObject(this, 0);
        System.out.println("Octree Server Started");
    }

    // run mc and return processed file
    @Override
    public byte[] Qtz(byte[] imgBytes, String fileName, int quantizationLevel) throws RemoteException {
        return null;
    }

    @Override
    public ArrayList<int[]> QtzColor(byte[] imgBytes, String fileName, int quantizationLevel) throws RemoteException {
        System.out.println("Filename: " + fileName + ", " + " Qtz level: " + quantizationLevel);

        long startTime = System.currentTimeMillis();
        Octree ot = new Octree();
        ArrayList<int[]> ret = ot.getQuantizedColors(imgBytes,fileName,quantizationLevel);
        long endTime = System.currentTimeMillis();

        calcTimeDelta = (endTime - startTime);
        return ret;
    }

    @Override
    public long getCalcTimeDelta() throws RemoteException {
        return calcTimeDelta;
    }
}
