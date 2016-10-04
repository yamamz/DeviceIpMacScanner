package com.app.yamamz.deviceipmacscanner.controller;

import android.content.Context;

import com.app.yamamz.deviceipmacscanner.db.DatabaseHelpher;
import com.app.yamamz.deviceipmacscanner.model.Device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jcifs.netbios.NbtAddress;


/**
 * Created by Admin on 9/27/2016.
 */
public class Pinger {



    private static final int NUMTHREADS = 254;
    private  Context context;

    private  DatabaseHelpher helpher;
    private static List<Device> dbList;
    public static List<Device> getDevicesOnNetwork(String subnet){
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



        ArrayList<Device> foundDev = new ArrayList<Device>(resAddresses.size());

        String result="";
        int loc=0;
        for (InetAddress a: resAddresses){
            foundDev.add(new Device(a.getHostAddress(), getMacFromArpCache(a.getHostAddress()), a.getHostName()));
            loc++;
            try {
                NbtAddress[] netbios = NbtAddress.getAllByAddress(a.getHostAddress());
                for (NbtAddress addr : netbios) {
                    if (addr.getNameType() == 0x20) {
                        foundDev.remove(loc-1);
                        foundDev.add(new Device(addr.getHostAddress(), getMacFromArpCache(addr.getHostAddress()), addr.getHostName()));


                    }



                    }
            } catch (UnknownHostException e) {
                e.printStackTrace();
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
