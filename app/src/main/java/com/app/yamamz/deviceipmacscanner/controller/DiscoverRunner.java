package com.app.yamamz.deviceipmacscanner.controller;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yamamz on 9/27/2016.
 */
public class DiscoverRunner implements Runnable {

    private static final String CMD = "/system/bin/ping -q -n -w 1 -c 1 %s";
    //private static final String CMD = "/system/bin/ping -c 1 %s";
    private static final String TAG = "DiscoverRunner";
    private List<InetAddress> results;
    private InetAddress a;
    private String subnet;
    private Integer startAdd;
    private Integer numAdds;


    public DiscoverRunner(String subnet, Integer start, Integer steps) {
        this.subnet = subnet;
        this.startAdd = start;
        this.numAdds = steps;
        results = new LinkedList<InetAddress>();
    }

    @Override
    public void run() {
        int timeout = 3000;
        for (int i = startAdd; i < startAdd + numAdds; i++) {
            String host = subnet + "." + i;


            try {
                Process exec = Runtime.getRuntime().exec(String.format(CMD, host));
                int i1 = exec.waitFor();
                if (i1 == 0) {
                    InetAddress a = InetAddress.getByName(host);
                    Log.i(TAG, "run: " + a.getHostAddress());
                    Log.i(TAG, "runs: " + a.getCanonicalHostName());
                    results.add(a);
                }
                else{

                    try {
                        InetAddress a = InetAddress.getByName(host);

                        if(a.isReachable(timeout)){
                            results.add(a);



                        }


                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }


                }


            } catch (IOException | InterruptedException e) {


            }



      /*  try {
                Process exec = Runtime.getRuntime().exec(String.format(CMD, host));
                int i1 = exec.waitFor();
                if (i1 == 0){
                    InetAddress a = InetAddress.getByName(host);
                    Log.i(TAG, "run: "+ a.getHostAddress());
                    Log.i(TAG, "runs: "+ a.getCanonicalHostName());
                    results.add(a);
                }else {
                    throw new IOException("Unable to get ping from runtime");
                }


            } catch (IOException | InterruptedException e) {
                try {
                    InetAddress a = InetAddress.getByName(host);


                    if (a.isReachable(timeout)) {
                        results.add(a);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }




            }

*/

            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "Devices found " + results.size());
        }



    public static boolean isSocketAliveUitlitybyCrunchify(String hostName, int port) {
        boolean isAlive = false;

        // Creates a socket address from a hostname and a port number
        SocketAddress socketAddress = new InetSocketAddress(hostName, port);
        Socket socket = new Socket();

        // Timeout required - it's in milliseconds
        int timeout = 0;

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



    List<InetAddress> getResults() {
        return results;
    }

}
