package com.app.yamamz.deviceipmacscanner.controller;

import android.util.Log;

import com.stealthcopter.networktools.Ping;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by AMRI on 10/6/2016.
 */

public class DiscoverOL implements Runnable {

    private static final String CMD = "/system/bin/ping -q -n -w 1 -c 1 %s";
    //private static final String CMD = "/system/bin/ping -c 1 %s";
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


                if(Ping.onAddress(subnet).setTimeOutMillis(timeout).doPing().isReachable){

                    results.add(true);
                }

                else if(!Ping.onAddress(subnet).setTimeOutMillis(timeout).doPing().isReachable){

                    results.add(false);
                }
            } catch (UnknownHostException e) {

                results.add(false);
                e.printStackTrace();
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


        Log.i(TAG, "Devices found  ccc"+ results.size());
    }

 public  List<Boolean> getResults() {
        return results;
    }

}

