package com.app.yamamz.deviceipmacscanner.runnable;

import android.util.Log;

import com.app.yamamz.deviceipmacscanner.model.Device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import jcifs.netbios.NbtAddress;

/**
 * Created by yamamz on 9/27/2016.
 */
public class DiscoverRunner implements Runnable {

    private static final String CMD = "/system/bin/ping -q -n -w 1 -c 1 %s";
    private static final String TAG = "DiscoverRunner";
    private List<Device> results;
    private InetAddress a;
    private String subnet;
    private Integer startAdd;
    private Integer numAdds;


    public DiscoverRunner(String subnet, Integer start, Integer steps) {
        this.subnet = subnet;
        this.startAdd = start;
        this.numAdds = steps;

        results = new LinkedList<>();
    }

    @Override
    public void run() {

        for (int i = startAdd; i < startAdd + numAdds; i++) {
            String host = subnet + "." + i;


            Socket socket = new Socket();
            socket.setPerformancePreferences(1, 0, 0);

            try {
                socket.setTcpNoDelay(false);
                socket.connect(new InetSocketAddress(host, 7), 200);
            } catch (IOException ignored) {
            } finally {
                try {

                    socket.close();
                    if (socket.isClosed())
                        try {
                            String mac = "";
                            InetAddress a = InetAddress.getByName(host);
                            mac = getMacFromArpCache(a.getHostAddress());
                            if (!"00:00:00:00:00:00".equals(mac)) {
                                System.out.println(mac);
                                results.add(new Device(startAdd, a.getHostAddress(), getMacFromArpCache(a.getHostAddress()), a.getCanonicalHostName(), myImageURL.drawableArray[1], myImageURL.TextColor[2], myImageURL.TextColor[1], myImageURL.TextColor[2], myImageURL.TextColor[2]));
                                NbtAddress[] netbios = NbtAddress.getAllByAddress(a.getHostAddress());
                                for (NbtAddress addr : netbios) {
                                    if (addr.getNameType() == 0x20) {
                                        results.remove(results.size() - 1);
                                        results.add(new Device(startAdd, addr.getHostAddress(), getMacFromArpCache(addr.getHostAddress()), addr.getHostName(), myImageURL.drawableArray[1], myImageURL.TextColor[2], myImageURL.TextColor[1], myImageURL.TextColor[2], myImageURL.TextColor[2]));
                                    }
                                }
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }


                } catch (IOException ignored) {
                }
            }

            Log.i(TAG, "Devices found " + results.size());
        }
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
                if (br != null) {
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
