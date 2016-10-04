package com.app.yamamz.deviceipmacscanner.controller;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Admin on 9/27/2016.
 */
public class DiscoverRunner implements Runnable {
    private static final String CMD = "/system/bin/ping -c 1 %s";
    private static final String TAG = "DiscoverRunner";
    private List<InetAddress> results;

    private String subnet;
    private Integer startAdd;
    private Integer numAdds;

    DiscoverRunner(String subnet, Integer start, Integer steps) {
        this.subnet = subnet;
        this.startAdd = start;
        this.numAdds = steps;
        results = new LinkedList<InetAddress>();
    }

    @Override
    public void run() {
        int timeout = 2000;
        for (int i = startAdd; i < startAdd + numAdds; i++) {
            String host = subnet + "." + i;

            try {
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

        }
    }

    List<InetAddress> getResults() {
        return results;
    }

}
