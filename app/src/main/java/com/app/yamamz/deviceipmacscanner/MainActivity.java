package com.app.yamamz.deviceipmacscanner;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.yamamz.deviceipmacscanner.controller.Pinger;
import com.app.yamamz.deviceipmacscanner.model.Device;
import com.app.yamamz.deviceipmacscanner.view.DeviderItemDecoration;
import com.app.yamamz.deviceipmacscanner.view.NetDeviceAdapter;
import com.app.yamamz.deviceipmacscanner.view.RecyclerItemClickListener;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.http.conn.util.InetAddressUtils.isIPv4Address;

public class MainActivity extends AppCompatActivity {

    ArrayList<Device> selected;
    private NetDeviceAdapter adapter = new NetDeviceAdapter(new ArrayList<Device>(15), R.layout.device_fragment, this);

    private  ProgressBar progressBarPing;
    private FloatingActionButton fab;
    private boolean isFabShowing=true;

    private Device d=new Device();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);

     fab = (FloatingActionButton) findViewById(R.id.fabMain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar();
                rescan();
            }
        });

        Transition exitTrans = new Explode();
        getWindow().setExitTransition(exitTrans);

        Transition reenterTrans = new Explode();
        getWindow().setReenterTransition(reenterTrans);



        progressBarPing = (ProgressBar) this.findViewById(R.id.progress);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);



        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DeviderItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());


        stopProgressBar();

        recyclerView.animate();
        rescan();
        try {
final String ip=d.getIpAddress();
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {


                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                         intent.putExtra("IP",adapter.getAddresses().get(position).getIpAddress());
                            intent.putExtra("hostName",adapter.getAddresses().get(position).getDeviceName());
                            intent.putExtra("mac",adapter.getAddresses().get(position).getMacAddress());
                            startActivity(intent,options.toBundle());



                        }
                    })
            );

        }
        catch (Exception e){


        }


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);



                if (dy > 0) {
                    hideFab();
                } else if (dy < 0) {
                    showFab();
                }
            }
        });

    }

    private void hideFab() {

        if (isFabShowing) {
            isFabShowing = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                final Point point = new Point();
                this.getWindow().getWindowManager().getDefaultDisplay().getSize(point);
                final float translation = fab.getY() - point.y;
                fab.animate().translationYBy(-translation).start();
            } else {
                Animation animation = AnimationUtils.makeOutAnimation(this, true);
                animation.setFillAfter(true);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fab.setClickable(false);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                fab.startAnimation(animation);
            }
        }
    }

    private void showFab() {
        if (!isFabShowing) {
            isFabShowing = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                fab.animate().translationY(0).start();
            } else {
                Animation animation = AnimationUtils.makeInAnimation(this, false);
                animation.setFillAfter(true);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fab.setClickable(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                fab.startAnimation(animation);
            }
        }
    }
    private void rescan(){

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected() && mWifi.isAvailable()){
            AppCompatDialog dialog = new AppCompatDialog(this);
            dialog.setTitle(R.string.scanning);
            dialog.setCancelable(false);

            startProgressBar();
            AsyncScan scan = new AsyncScan(dialog, getString(R.string.scanning_your_network));

            scan.execute(adapter);

        }else {
            Toast.makeText(this, getString(R.string.not_connected_error), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        // Destroy
        super.onDestroy();
    }

    public static String intToIp(int i) {

        return ((i >> 24 ) & 0xFF ) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ( i & 0xFF) ;
    }

    private  class AsyncScan extends AsyncTask<NetDeviceAdapter, Void, List<Device>> {

        private NetDeviceAdapter adapter;
        private AppCompatDialog mDialog;


        AsyncScan(AppCompatDialog dialog, String string) {
            super();
            this.mDialog = dialog;
        }

        @Override
        protected List<Device> doInBackground(NetDeviceAdapter... voids) {

            List<Device> addresses;
            String ipString = getLocalIpv4Address();


            if (ipString == null){
                return new ArrayList<Device>(1);
            }
            int lastdot = ipString.lastIndexOf(".");
            ipString = ipString.substring(0, lastdot);
            //ipString="192.168.85";
            try {

          addresses = Pinger.getDevicesOnNetwork(ipString);
                Thread.sleep(100);
                adapter = voids[0];
                return addresses;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Device> inetAddresses) {
            super.onPostExecute(inetAddresses);


            adapter.setAddresses(inetAddresses);
            adapter.notifyDataSetChanged();

            stopProgressBar();


        }

        @Override
        protected void onProgressUpdate(Void... values) {


            super.onProgressUpdate(values);

            adapter.notifyDataSetChanged();

        }




         String getLocalIpv4Address(){
            try {
                String ipv4;
                List<NetworkInterface>  nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
                if(nilist.size() > 0){
                    for (NetworkInterface ni: nilist){
                        List<InetAddress>  ialist = Collections.list(ni.getInetAddresses());
                        if(ialist.size()>0){
                            for (InetAddress address: ialist){
                                if (!address.isLoopbackAddress() && isIPv4Address(ipv4=address.getHostAddress())){
                                    return ipv4;
                                }
                            }
                        }

                    }

                }

            } catch (SocketException ignored) {

            }
            return "";
        }


    }

    public void startProgressBar() {
        progressBarPing.setVisibility(View.VISIBLE);
    }

    public  void stopProgressBar() {
        progressBarPing.setVisibility(View.GONE);
    }

    public String getItem(int position) {
        return adapter.getAddresses().toString();
    }

}
