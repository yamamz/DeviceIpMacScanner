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
 * Created by yamamz on 10/8/2016.
 */

public class PingerSubnet {


    private static final String TAG = "DiscoverRunner";

    @SuppressLint("StaticFieldLeak")
    private static Context context1;

    public  PingerSubnet(Context context1){

        PingerSubnet.context1 =context1;


    }

    public static List<Device> getDevicesOnNetwork(String subnet) throws InterruptedException, SocketException {
        int[] bounds = rangeFromCidr(subnet);
        int count=0;
        for(int i = bounds[0]; i <=bounds[1]; i++){
           count++;

        }

        int NUMTHREADS = count;
        LinkedList<InetAddress> resAddresses = new LinkedList<>();
        DiscoverBySubNet[] tasks = new DiscoverBySubNet[NUMTHREADS];

        Thread[] threads = new Thread[NUMTHREADS];


        int count2=-1;
        int count3=-1;
        int count4=-1;

        //Create Tasks and treads
        for(int h = bounds[0]; h <= bounds[1]; h++){
            count2++;
                tasks[count2] = new DiscoverBySubNet(subnet, h, 1);
                threads[count2] = new Thread(tasks[count2]);

        }
        //Starts threads
        for(int h = bounds[0]; h <= bounds[1]; h++){
            count3++;
            threads[count3].start();
            Thread.sleep(100);


        }

        for(int h = bounds[0]; h <= bounds[1]; h++){
            try{
                count4++;
                threads[count4].join();

            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        for(int i = 0; i < NUMTHREADS; i++) {
            for (InetAddress a : tasks[i].getResults()) {

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
            Thread.sleep(500);

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






    public static int[] rangeFromCidr(String cidrIp) {
        int maskStub = 1 << 31;
        String[] atoms = cidrIp.split("/");
        int mask = Integer.parseInt(atoms[1]);
        System.out.println(mask);

        int[] result = new int[2];
        result[0] = DiscoverBySubNet.InetRange.ipToInt(atoms[0]) & (maskStub >> (mask - 1)); // lower bound
        result[1] = DiscoverBySubNet.InetRange.ipToInt(atoms[0]); // upper bound
        System.out.println(DiscoverBySubNet.InetRange.intToIp(result[0]));
        System.out.println(DiscoverBySubNet.InetRange.intToIp(result[1]));

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




}