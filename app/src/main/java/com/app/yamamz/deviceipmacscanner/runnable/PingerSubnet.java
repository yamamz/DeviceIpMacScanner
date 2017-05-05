package com.app.yamamz.deviceipmacscanner.runnable;

import android.util.Log;

import com.app.yamamz.deviceipmacscanner.model.Device;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by yamamz on 10/8/2016.
 */

public class PingerSubnet {


    private static final String TAG = "DiscoverRunner";
    private static List<Device> foundDev = new ArrayList<>();


    public static List<Device> getDevicesOnNetwork(String subnet) throws InterruptedException, SocketException {
        int[] bounds = rangeFromCidr(subnet);
        int count=0;
        for(int i = bounds[0]; i <=bounds[1]; i++){
           count++;

        }
        final int NUMTHREADS = count;
        final LinkedList<InetAddress> resAddresses = new LinkedList<>();
        final DiscoverBySubNet[] tasks = new DiscoverBySubNet[NUMTHREADS];
        int count2=-1;
        ExecutorService executorService= Executors.newCachedThreadPool();

        //Create Tasks and treads
        for(int h = bounds[0]; h <= bounds[1]; h++){
                 count2++;
                tasks[count2] = new DiscoverBySubNet(h, 1);
                executorService.execute(tasks[count2]);
             Thread.sleep(40);
        }
        //Starts threads

executorService.execute(new Runnable() {
    @Override
    public void run() {

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
        Log.i(TAG, "Found device add " + String.valueOf(resAddresses.size()));
        foundDev = new ArrayList<>(resAddresses.size());
        DescoverDeviceRunable[] tasks1 = new DescoverDeviceRunable[resAddresses.size()];
        executeThread(tasks1,resAddresses);
    }
});
        System.gc();
        System.runFinalization();
        executorService.shutdownNow();

        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {
        }

        return foundDev;
    }

    public  static   void executeThread(DescoverDeviceRunable[] tasks1,LinkedList<InetAddress> resAddresses){

        ExecutorService executorService1 = Executors.newCachedThreadPool();
        for (int i =0; i < resAddresses.size(); i++) {
            tasks1[i]= new DescoverDeviceRunable(resAddresses.get(i), i, 1);
            executorService1.execute(tasks1[i]);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        System.gc();
        System.runFinalization();
        executorService1.shutdownNow();

        try {
            executorService1.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {
        }

        for (int i = 0; i < resAddresses.size(); i++) {
            for (Device a : tasks1[i].getResults()) {

                foundDev.add(a);
            }

        }
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






}