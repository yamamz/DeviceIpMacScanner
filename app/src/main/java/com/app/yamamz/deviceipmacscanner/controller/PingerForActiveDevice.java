package com.app.yamamz.deviceipmacscanner.controller;

import android.annotation.SuppressLint;
import android.content.Context;

import com.app.yamamz.deviceipmacscanner.R;
import com.app.yamamz.deviceipmacscanner.model.Device;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by yamamz on 10/6/2016.
 */

public class PingerForActiveDevice {


    private static int NUMTHREADS;


    private static final String TAG = "DiscoverRunner";

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static List<Device> allDevice;

    private static Realm realm;

    private static List<Integer> resAddresses;


    public PingerForActiveDevice(Context context) {

        PingerForActiveDevice.context = context;


    }


    public static List<Device> getDevicesOnNetwork() throws InterruptedException {
        LinkedList<Integer> resAddresses = new LinkedList<>();
        LinkedList<Integer> ipTextColor = new LinkedList<>();
        LinkedList<Integer> MacTextColor = new LinkedList<>();
        LinkedList<Integer> DeviceNameTextColor = new LinkedList<>();
        LinkedList<Integer> macVendorTextColor = new LinkedList<>();

       // realm = Realm.getDefaultInstance();

        Realm.init(context);
        realm = Realm.getDefaultInstance();

        ArrayList<Device> AllDevices= new ArrayList<Device>();
        myImageURL imageData = new myImageURL();

        for (Device devices : realm.where(Device.class).findAll()) {

            int i=0;

            AllDevices.add(new Device(i, devices.getIpAddress(), devices.getMacAddress(), devices.getDeviceName(), devices.getImage(),devices.getTextColorIP(),devices.getTextColorMac(),devices.getTextColorDeviceName(),devices.getTextColorMacVendor()));

            i++;
        }


        //  EntityService userService = new EntityService(context, Device.class);
       // allDevice = userService.get()

    NUMTHREADS = AllDevices.size();

    DiscoverOL[] tasks = new DiscoverOL[NUMTHREADS];


        Thread[] threads = new Thread[NUMTHREADS];


        //Create Tasks and treads
        for (int i = 0; i < AllDevices.size();i++) {
            tasks[i] = new DiscoverOL(AllDevices.get(i).getIpAddress(), i, 1);
            threads[i] = new Thread(tasks[i]);

        }
        //Starts threads
        for (int i = 0; i < NUMTHREADS;i++) {
            threads[i].start();
            Thread.sleep(100);
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

                    if(!res){
                        resAddresses.add(R.drawable.ic_devices_blue_grey_500_18dp);
                        ipTextColor.add(R.color.gray);
                        MacTextColor.add(R.color.gray);
                        DeviceNameTextColor.add(R.color.gray);
                        macVendorTextColor.add(R.color.gray);
                    }

                    else {

                        resAddresses.add(R.drawable.ic_devices_light_green_a400_18dp);

                        ipTextColor.add(R.color.White);
                        MacTextColor.add(R.color.colorAccent);
                        DeviceNameTextColor.add(R.color.White);
                        macVendorTextColor.add(R.color.White);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
       //   Log.i(TAG, "Found device is online " + String.valueOf(tasks.length));


        }

        ArrayList<Device> foundDev = new ArrayList<Device>(resAddresses.size());
        try {

            myImageURL imageDatas = new myImageURL();

            String result = "";
            int loc = 0;
            for (int i = 0; i < AllDevices.size(); i++) {


                foundDev.add(new Device(i, AllDevices.get(i).getIpAddress(), AllDevices.get(i).getMacAddress(), AllDevices.get(i).getDeviceName(), resAddresses.get(i),ipTextColor.get(i),MacTextColor.get(i),DeviceNameTextColor.get(i),macVendorTextColor.get(i)));


            }


        } catch (Exception e) {
            e.printStackTrace();

        }


        return foundDev;
}



}
