package com.app.yamamz.deviceipmacscanner.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.app.yamamz.deviceipmacscanner.model.Device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by yamamz on 9/27/2016.
 */
public class Pinger {



    private static final int NUMTHREADS = 254;


    private static final String TAG = "DiscoverRunner";

    @SuppressLint("StaticFieldLeak")
    private static Context context1;

    public  Pinger(Context context1){

        Pinger.context1 =context1;


    }

    public static List<Device> getDevicesOnNetwork(String subnet) throws InterruptedException, SocketException {
        LinkedList<InetAddress> resAddresses = new LinkedList<>();
        DiscoverRunner[] tasks = new DiscoverRunner[NUMTHREADS];

        Thread[] threads = new Thread[NUMTHREADS];



        //Create Tasks and treads
        for(int i = 0; i < NUMTHREADS; i++){
            tasks[i] = new DiscoverRunner(subnet, 254/NUMTHREADS*i, 254/NUMTHREADS);
            threads[i] = new Thread(tasks[i]);

        }
        //Starts threads
        for(int i = 0; i < NUMTHREADS; i++){
            threads[i].start();
            Thread.sleep(100);

        }

        for(int i = 0; i < NUMTHREADS; i++){
            try{
                threads[i].join();

            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        for(int i = 0; i < NUMTHREADS; i++){
            for(InetAddress a: tasks[i].getResults()){
                try {
                    a = InetAddress.getByName(a.getHostAddress());
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                resAddresses.add(a);
            }

        }
        Log.i(TAG, "Found device add "+ String.valueOf(resAddresses.size()));

        DescoverDeviceRunable[] tasks1 = new DescoverDeviceRunable[resAddresses.size()];
        ArrayList<Device> foundDev = new ArrayList<>(resAddresses.size());
        myImageURL imageData = new myImageURL();
        Thread[] threads1 = new Thread[resAddresses.size()];

        for(int i = 0; i < resAddresses.size(); i++){
            tasks1[i] = new DescoverDeviceRunable(resAddresses.get(i), i, 1);
            threads1[i] = new Thread(tasks1[i]);

        }
        //Starts threads
        for(int i = 0; i < resAddresses.size(); i++){
            threads1[i].start();

        }

        for(int i = 0; i < resAddresses.size(); i++){
            try{
                threads1[i].join();

            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        for(int i = 0; i < resAddresses.size(); i++){
            for(Device a: tasks1[i].getResults()){

                foundDev.add(a);
            }

        }

        return foundDev;
    }

    /**
     * Try to extract a hardware MAC address from a given IP address using the
     * ARP cache (/proc/net/arp).<br>
     * <br>
     * We assume that the file has this structure:<br>
     * <br>
     * IP address       HW type     Flags       HW address            Mask     Device
     * 192.168.18.11    0x1         0x2         00:04:20:06:55:1a     *        eth0
     * 192.168.18.36    0x1         0x2         00:22:43:ab:2a:5b     *        eth0
     *
     * @param ip
     * @return the MAC from the ARP cache
     */


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





}
