package com.app.yamamz.deviceipmacscanner.runnable;

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
 * Created by Yamamz on 10/8/2016.
 */




    public class DiscoverBySubNet implements Runnable {

        private static final String CMD = "/system/bin/ping -q -n -w 1 -c 1 %s";
        //private static final String CMD = "/system/bin/ping -c 1 %s";
        private static final String TAG = "DiscoverRunner";
        private List<InetAddress> results;


    private Integer startAdd;
    private Integer numAdds;

        public DiscoverBySubNet(Integer start, Integer steps) {
            this.startAdd = start;
            this.numAdds = steps;
            results = new LinkedList<InetAddress>();
        }

        @Override
        public void run() {
            int timeout = 3000;


            for (int i = startAdd; i < startAdd + numAdds; i++) {
                String address = InetRange.intToIp(i);





        try {
                Process exec = Runtime.getRuntime().exec(String.format(CMD, address));
                int i1 = exec.waitFor();
                if (i1 == 0) {
                    InetAddress a = InetAddress.getByName(address);
                    Log.i(TAG, "run: " + a.getHostAddress());
                    Log.i(TAG, "runs: " + a.getCanonicalHostName());
                    results.add(a);
                }
                    else{

                    try {
                        InetAddress a = InetAddress.getByName(address);

                        if(a.isReachable(timeout)){
                            results.add(a);

                        }

                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }


                }


            } catch (IOException | InterruptedException e) {


            }



            }


            Log.i(TAG, "Devices found "+ results.size());
        }

        List<InetAddress> getResults() {

            return results;
        }

    public static int[] rangeFromCidr(String cidrIp) {
        int maskStub = 1 << 31;
        String[] atoms = cidrIp.split("/");
        int mask = Integer.parseInt(atoms[1]);
        System.out.println(mask);

        int[] result = new int[2];
        result[0] = InetRange.ipToInt(atoms[0]) & (maskStub >> (mask - 1)); // lower bound
        result[1] = InetRange.ipToInt(atoms[0]); // upper bound
        System.out.println(InetRange.intToIp(result[0]));
        System.out.println(InetRange.intToIp(result[1]));

        return result;
    }

    static class InetRange {
        public static int ipToInt(String ipAddress) {
            try {
                byte[] bytes = InetAddress.getByName(ipAddress).getAddress();
                int octet1 = (bytes[0] & 0xFF) << 24;
                int octet2 = (bytes[1] & 0xFF) << 16;
                int octet3 = (bytes[2] & 0xFF) << 8;
                int octet4 = bytes[3] & 0xFF;
                int address = octet1 | octet2 | octet3 | octet4;

                return address;
            } catch (Exception e) {
                e.printStackTrace();

                return 0;
            }
        }

        public static String intToIp(int ipAddress) {
            int octet1 = (ipAddress & 0xFF000000) >>> 24;
            int octet2 = (ipAddress & 0xFF0000) >>> 16;
            int octet3 = (ipAddress & 0xFF00) >>> 8;
            int octet4 = ipAddress & 0xFF;

            return new StringBuffer().append(octet1).append('.').append(octet2)
                    .append('.').append(octet3).append('.')
                    .append(octet4).toString();
        }
    }

    public static boolean isSocketAliveUitlitybyCrunchify(String hostName, int port) {
        boolean isAlive = false;

        // Creates a socket address from a hostname and a port number
        SocketAddress socketAddress = new InetSocketAddress(hostName, port);
        Socket socket = new Socket();

        // Timeout required - it's in milliseconds
        int timeout = 0;


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
}


