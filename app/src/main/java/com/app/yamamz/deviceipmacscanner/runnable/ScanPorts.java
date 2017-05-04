package com.app.yamamz.deviceipmacscanner.runnable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.app.yamamz.deviceipmacscanner.model.Port;
import com.app.yamamz.deviceipmacscanner.model.ScanResult;
import com.app.yamamz.deviceipmacscanner.view.PortsAdapter;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by yamamz on 10/12/2016.
 */

public class ScanPorts {
    private List<Port> portList = new ArrayList<>();
    private Context mContex;
    private PortsAdapter mAdapter;
    private String IP;
Activity activity;
    public ScanPorts(Context mContex,PortsAdapter mAdapter,String IP,Activity activity){
        this.mContex=mContex;
        this.mAdapter=mAdapter;
        this.activity=activity;
        this.IP=IP;
    }

    void ScanPorts() throws InterruptedException, ExecutionException {

        final ExecutorService es = Executors.newFixedThreadPool(20);

        final int timeout = 200;
        final List<Future<ScanResult>> futures = new ArrayList<>();
        for (int port = 1; port <= 65535; port++) {
            // for (int port = 1; port <= 80; port++) {
            futures.add(portIsOpen(es, IP, port, timeout));
        }
        es.awaitTermination(200L, TimeUnit.MILLISECONDS);
        int openPorts = 0;
        for (final Future<ScanResult> f : futures) {
            if (f.get().isOpen()) {
                openPorts++;

                @SuppressLint("SimpleDateFormat") final DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                final Calendar calobj = Calendar.getInstance();
                Port port = new Port("PORT-" + String.valueOf(f.get().getPort()), String.valueOf(df.format(calobj.getTime())), String.valueOf(mAdapter.getItemCount()));
                portList.add(port);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    //    activity.startProgressBar();
                    mAdapter.notifyDataSetChanged();
                    }
                });

            }
        }
     activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
              //  stopProgressBar();
                Toast.makeText(mContex,"the client have "+mAdapter.getItemCount()+" open ports",Toast.LENGTH_LONG).show();
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    public static Future<ScanResult> portIsOpen(final ExecutorService es, final String ip, final int port,
                                                final int timeout) {
        return es.submit(new Callable<ScanResult>() {
            @Override
            public ScanResult call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    return new ScanResult(port, true);
                } catch (Exception ex) {
                    return new ScanResult(port, false);
                }
            }
        });
    }


}
