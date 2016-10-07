package com.app.yamamz.deviceipmacscanner.controller;

import android.annotation.SuppressLint;
import android.content.Context;

import com.app.yamamz.deviceipmacscanner.R;
import com.app.yamamz.deviceipmacscanner.model.Device;

import org.droitateddb.EntityService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by AMRI on 10/6/2016.
 */

public class PingerForActiveDevice {


    private static int NUMTHREADS;


    private static final String TAG = "DiscoverRunner";

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static List<Device> allDevice;

    private static List<Integer> resAddresses;


    public PingerForActiveDevice(Context context) {

        this.context = context;

    }


    public static List<Device> getDevicesOnNetwork() {
        LinkedList<Integer> resAddresses = new LinkedList<>();

        EntityService userService = new EntityService(context, Device.class);
        allDevice = userService.get();

        NUMTHREADS = allDevice.size();

        DiscoverOL[] tasks = new DiscoverOL[NUMTHREADS];


        Thread[] threads = new Thread[NUMTHREADS];


        //Create Tasks and treads
        for (int i = 0; i < allDevice.size(); i++) {
            tasks[i] = new DiscoverOL(allDevice.get(i).getIpAddress(), NUMTHREADS / NUMTHREADS * i, NUMTHREADS / NUMTHREADS);
            threads[i] = new Thread(tasks[i]);

        }
        //Starts threads
        for (int i = 0; i < NUMTHREADS; i++) {
            threads[i].start();
        }

        for (int i = 0; i < NUMTHREADS; i++) {
            try {
                threads[i].join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i <NUMTHREADS; i++) {

            try {

              for(Boolean res:tasks[i].getResults())

                {

                    if(res==false){
                        resAddresses.add(R.drawable.ic_cast_connected_grey_700_48dp);
                    }

                    else {

                        resAddresses.add(R.drawable.ic_cast_connected_white_48dp);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
       //   Log.i(TAG, "Found device is online " + String.valueOf(tasks.length));


        }


        try {

            ArrayList<Device> foundDev = new ArrayList<Device>(resAddresses.size());
            myImageURL imageData = new myImageURL();

            String result = "";
            int loc = 0;
            for (int i = 0; i < allDevice.size(); i++) {


                foundDev.add(new Device(i, allDevice.get(i).getIpAddress(), allDevice.get(i).getMacAddress(), allDevice.get(i).getDeviceName(), resAddresses.get(i)));


            }

            return foundDev;


        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

    }

}
