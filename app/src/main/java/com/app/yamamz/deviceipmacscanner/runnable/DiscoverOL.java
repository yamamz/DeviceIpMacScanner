package com.app.yamamz.deviceipmacscanner.runnable;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yamamz on 10/6/2016.
 */

public class DiscoverOL implements Runnable {

    private static final String CMD = "/system/bin/ping -q -n -w 1 -c 1 %s";
    private static final String TAG = "DiscoverRunner";
    private List<Boolean> results;

    private String subnet;
    private Integer startAdd;
    private Integer numAdds;
    private myImageURL imageURL=new myImageURL();

    DiscoverOL(String subnet, Integer start, Integer steps) {
        this.subnet = subnet;
        this.startAdd = start;
        this.numAdds = steps;
        results = new LinkedList<Boolean>();
    }

    @Override
    public void run() {
        int timeout = 3000;
        for (int i = startAdd; i < startAdd + numAdds; i++) {


            try {
                Process exec = Runtime.getRuntime().exec(String.format(CMD, subnet));
                int i1 = exec.waitFor();
                if (i1 == 0) {
                    InetAddress a = InetAddress.getByName(subnet);
                    Log.i(TAG, "run: " + a.getHostAddress());
                    Log.i(TAG, "runs: " + a.getCanonicalHostName());
                    results.add(true);
                }
                else{

                    try {
                        InetAddress a = InetAddress.getByName(subnet);

                        if(a.isReachable(timeout)){
                            results.add(true);
                        }

                        else{
                            results.add(false);
                        }
                    } catch (IOException ioe) {

                        ioe.printStackTrace();
                    }


                }


            } catch (IOException | InterruptedException e) {


            }

        }


        Log.i(TAG, "Devices found  ccc"+ results.size());
    }

 public  List<Boolean> getResults() {
        return results;
    }

}

