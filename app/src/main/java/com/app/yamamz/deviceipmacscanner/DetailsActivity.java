package com.app.yamamz.deviceipmacscanner;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.yamamz.deviceipmacscanner.model.Port;
import com.app.yamamz.deviceipmacscanner.model.ScanResult;
import com.app.yamamz.deviceipmacscanner.view.DeviderItemDecoration;
import com.app.yamamz.deviceipmacscanner.view.PortsAdapter;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.PortScan;
import com.stealthcopter.networktools.ping.PingResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
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


public class DetailsActivity extends AppCompatActivity {

    private List<Port> portList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PortsAdapter mAdapter;

    private  ProgressBar progressBarPing;

    private TextView ping1;
    private TextView external;

private String IP;
    private List listPingresult;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);

        TextView ipAdd = (TextView) findViewById(R.id.ipAddress);
        TextView macAdd = (TextView) findViewById(R.id.macAddress);
        TextView hostname1 = (TextView) findViewById(R.id.hostAdd);
        ping1=(TextView) findViewById(R.id.ping);
       external=(TextView) findViewById(R.id.External);

        progressBarPing = (ProgressBar) this.findViewById(R.id.progressdetails);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent startingIntent = getIntent();
        String hostName=startingIntent.getStringExtra("hostName");
        IP = startingIntent.getStringExtra("IP");
        String mac = startingIntent.getStringExtra("mac");
        getSupportActionBar().setTitle(hostName);
        Transition enterTrans = new Explode();
        getWindow().setEnterTransition(enterTrans);


        recyclerView = (RecyclerView) findViewById(R.id.listPort1);

        mAdapter = new PortsAdapter(portList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DeviderItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

       // prepareMovieData();

        Transition returnTrans = new Explode();
        getWindow().setReturnTransition(returnTrans);
ipAdd.setText(IP);
        macAdd.setText(mac);
        hostname1.setText(hostName);
        //Get Expernal Ip in Background

        startProgressBar();
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    doPing();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ScanPorts();
                           // doPortScan();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                mAdapter.notifyDataSetChanged();


            }


        });


      //  prepareMovieData();


    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {

            try {

                URL whatismyip = new URL("http://icanhazip.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

                resp = in.readLine(); //you get the IP as a String


            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation

            external.setText(result);
            stopProgressBar();
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doPing() throws Exception {
        String ipAddress = IP;

        if (TextUtils.isEmpty(ipAddress)) {
            appendResultsText("Invalid Ip Address");

        }

        else if(Ping.onAddress(IP).doPing().isReachable){


            PingResult pingResult = Ping.onAddress(ipAddress).setTimeOutMillis(1000).doPing();



            ping1.setText(String.format("%.2f ms",pingResult.getTimeTaken()));


        }

        else if(!Ping.onAddress(IP).doPing().isReachable){

            ping1.setText("Unreachable");

        }

        }

    private void doPortScan() throws UnknownHostException {
        String ipAddress = IP;





        if (TextUtils.isEmpty(ipAddress)) {
            appendResultsText("Invalid Ip Address");

        }

        else{
          //  appendResultsText("PortScanning IP: "+ipAddress);
            final ArrayList<Integer> openPort = PortScan.onAddress(ipAddress).setPort(21).doScan();

          PortScan.onAddress(ipAddress).setTimeOutMillis(1000).setPortsAll().doScan(new PortScan.PortListener() {
              @Override
              public void onResult(int portNo, boolean open) {

                  @SuppressLint("SimpleDateFormat") final DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                  final Calendar calobj = Calendar.getInstance();
                  int position = 1;

                  if (open) {

                      // appendResultsText("Open Port: "+portNo);

                      Port port = new Port("PORT-" + String.valueOf(portNo), String.valueOf(df.format(calobj.getTime())), String.valueOf(mAdapter.getItemCount()));
                      portList.add(port);

                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              // This code will always run on the UI thread, therefore is safe to modify UI elements.
                              startProgressBar();

                              mAdapter.notifyDataSetChanged();
                          }
                      });


                  }









                }

                @Override
                public void onFinished(final ArrayList<Integer> openPorts) {


                 //   appendResultsText("Open Ports: "+openPorts.size());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            stopProgressBar();
                            Toast.makeText(DetailsActivity.this,"the client have "+openPorts.size()+" open ports",Toast.LENGTH_LONG).show();
                            mAdapter.notifyDataSetChanged();
                        }
                    });


                }
            });

        }


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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    startProgressBar();
                    mAdapter.notifyDataSetChanged();
                }
            });

        }
    }
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            // This code will always run on the UI thread, therefore is safe to modify UI elements.
            stopProgressBar();
            Toast.makeText(DetailsActivity.this,"the client have "+mAdapter.getItemCount()+" open ports",Toast.LENGTH_LONG).show();
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



    private void appendResultsText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                external.append(text+"\n");
            }
        });
    }

    public  void startProgressBar() {
        progressBarPing.setVisibility(View.VISIBLE);
    }

    public  void stopProgressBar() {
        progressBarPing.setVisibility(View.GONE);



    }
}
