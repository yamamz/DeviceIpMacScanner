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




    public static List<Device> getDevicesOnNetwork(String subnet) throws InterruptedException, SocketException {

        int[] bounds = rangeFromCidr(subnet);
        int count=0;
        for(int i = bounds[0]; i <=bounds[1]; i++){
            count++;

        }
        final int NUMTHREADS = count;
        final DiscoverRunner[] tasks = new DiscoverRunner[NUMTHREADS];
        int count2=-1;
        ExecutorService executorService= Executors.newCachedThreadPool();
        if (!executorService.isShutdown()) {
            for (int h = bounds[0]; h <= bounds[1]; h++) {
                count2++;
                tasks[count2] = new DiscoverRunner(h, 1);
                executorService.execute(tasks[count2]);
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
