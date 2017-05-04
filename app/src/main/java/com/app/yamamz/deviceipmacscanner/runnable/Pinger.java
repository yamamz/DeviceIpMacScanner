package com.app.yamamz.deviceipmacscanner.runnable;

import com.app.yamamz.deviceipmacscanner.model.Device;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Created by yamamz on 9/27/2016.
 *
 */
public class Pinger {
    private static final int NUMTHREADS = 254;
    private static List<Device> foundDev = new ArrayList<>();
    private static final String TAG = "DiscoverRunner";
    final static DiscoverRunner[] tasks = new DiscoverRunner[NUMTHREADS];



    public static List<Device> getDevicesOnNetwork(String subnet) throws InterruptedException, SocketException {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMTHREADS);
        if (!executorService.isShutdown()) {
            for (int i = 0; i < NUMTHREADS; i++) {
                tasks[i] = new DiscoverRunner(subnet, i, 254 / NUMTHREADS);
                executorService.execute(tasks[i]);
                Thread.sleep(30);
            }
        }

executorService.execute(new Runnable() {
    @Override
    public void run() {
        for (int i = 0; i < NUMTHREADS; i++) {
            for (Device a : tasks[i].getResults()) {
                foundDev.add(a);
            }
        }
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



}
