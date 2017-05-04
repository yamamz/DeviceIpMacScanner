package com.app.yamamz.deviceipmacscanner.runnable;

import android.util.Log;

import com.app.yamamz.deviceipmacscanner.model.Device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import jcifs.netbios.NbtAddress;

/**
 * Created by Yamamz on 10/11/2016.
 */

public class DescoverDeviceRunable implements Runnable  {

    private static final String CMD = "/system/bin/ping -q -n -w 1 -c 1 %s";
    private static final String TAG = "DiscoverRunner";
    private List<Device> results;

    private InetAddress IPToSearch;
    private Integer startAdd;
    private Integer numAdds;

    public  DescoverDeviceRunable(InetAddress IPToSearch, Integer start, Integer steps) {
        this.IPToSearch =IPToSearch;
        this.startAdd = start;
        this.numAdds = steps;
        results = new LinkedList<>();
    }
    /**
     * Add the Device found in the list
     * it loops on threads created
     *
     */
    @Override
    public void run() {
        for (int i = startAdd; i < startAdd + numAdds; i++) {
            myImageURL imageData=new myImageURL();
            int loc=0;
                loc++;
                try {

                    results.add(new Device(startAdd,IPToSearch.getHostAddress(),getMacFromArpCache(IPToSearch.getHostAddress()), IPToSearch.getCanonicalHostName(),imageData.drawableArray[1],imageData.TextColor[2],imageData.TextColor[1],imageData.TextColor[2],imageData.TextColor[2]));
                    NbtAddress[] netbios = NbtAddress.getAllByAddress(IPToSearch.getHostAddress());
                    for (NbtAddress addr : netbios) {
                        if (addr.getNameType() == 0x20) {
                            results.remove(loc-1);
                            results.add(new Device(startAdd,addr.getHostAddress(), getMacFromArpCache(addr.getHostAddress()), addr.getHostName(), imageData.drawableArray[1],imageData.TextColor[2],imageData.TextColor[1],imageData.TextColor[2],imageData.TextColor[2]));
                            Log.i(TAG, "Devices found "+ results.get(loc-1));
                        }

                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Devices found "+ results.size());
    }


    public static String getMacFromArpCache(String ip) {
        if (ip == null)
            return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0])) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        return mac;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(br!=null){
                    br.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }





    List<Device> getResults() {
        return results;
    }

}
