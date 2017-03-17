package com.app.yamamz.deviceipmacscanner.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yamamz on 10/11/2016.
 */

public class PortScanRunnable implements Runnable {

    private String hostName;
    private int port;

    private List<Integer> ports;

    public PortScanRunnable(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        ports = new LinkedList<Integer>();
    }


    @Override
    public void run() {

        for (int i = port; i < port + 1; port++) {

            if (isSocketAliveUitlitybyCrunchify(hostName, port)) {
                ports.add(port);
            }
        }
    }

    public List<Integer> getResults(){
        return ports;
    }


    public static boolean isSocketAliveUitlitybyCrunchify(String hostName, int port) {
        boolean isAlive = false;

        // Creates a socket address from a hostname and a port number
        SocketAddress socketAddress = new InetSocketAddress(hostName, port);
        Socket socket = new Socket();

        // Timeout required - it's in milliseconds
        int timeout = 3000;

        log("hostName: " + hostName + ", port: " + port);
        try {
            socket.connect(socketAddress, timeout);
            socket.close();
            isAlive = true;

        } catch (SocketTimeoutException exception) {
            System.out.println("SocketTimeoutException " + hostName + ":" + port + ". " + exception.getMessage());
        } catch (IOException exception) {
            System.out.println(
                    "IOException - Unable to connect to " + hostName + ":" + port + ". " + exception.getMessage());
        }
        return isAlive;
    }

    // Simple log utility
    private static void log(String string) {
        System.out.println(string);
    }

    // Simple log utility returns boolean result
    private static void log(boolean isAlive) {
        System.out.println("isAlive result: " + isAlive + "\n");
    }

}


