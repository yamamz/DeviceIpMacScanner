package com.app.yamamz.deviceipmacscanner;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.app.yamamz.deviceipmacscanner.model.Device;
import com.app.yamamz.deviceipmacscanner.runnable.PingerForActiveDevice;
import com.app.yamamz.deviceipmacscanner.runnable.Wireless;
import com.app.yamamz.deviceipmacscanner.runnable.myImageURL;
import com.app.yamamz.deviceipmacscanner.view.NetDeviceAdapter;
import com.app.yamamz.deviceipmacscanner.view.RecyclerItemClickListener;
import com.app.yamamz.deviceipmacscanner.view.Settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private NetDeviceAdapter adapter = new NetDeviceAdapter(new ArrayList<Device>(15), R.layout.device_fragment, this);
    private FloatingActionButton fab;
    private boolean isFabShowing = true;
    private PingerForActiveDevice pinger;
    private Device d = new Device();

    private final static int TIMER_INTERVAL = 100000;
    private Wireless wifi;

    private TextView externalIp;
    private TextView signalStrength;
    private TextView ssid;
    private TextView bssid;
    private Realm realmDB;
    private Handler mHandler = new Handler();
    private TextView ipExternal;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter = new IntentFilter();
    private String ExternalIp;
    private myImageURL imageData = new myImageURL();

    private ProgressDialog pDialog;


    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        Realm.init(this);
        realmDB = Realm.getDefaultInstance();
        pinger = new PingerForActiveDevice(this);


        this.InitViews();

        Transition exitTrans = new Explode();
        getWindow().setExitTransition(exitTrans);
        Transition reenterTrans = new Explode();
        getWindow().setReenterTransition(reenterTrans);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        initRecyclerView();
        loadDeviceOnDatabase();

        initFab();


        this.wifi = new Wireless(getApplicationContext());
        this.setupReceivers();


    }

    void initFab() {
        fab = (FloatingActionButton) findViewById(R.id.fabMain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                realmDB.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        //     bgRealm.delete(Device.class);
                    }
                }, new Realm.Transaction.OnSuccess()

                {
                    @Override
                    public void onSuccess() {

                        rescan();
                    }

                });
            }
        });


    }

    public void UpdateList(final List<Device> inetAddresses) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        fab.setEnabled(true);
        final ArrayList<Device> AllDeviceSave = new ArrayList<Device>();

        realmDB.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {

                bgRealm.copyToRealmOrUpdate(inetAddresses);

            }
        }, new Realm.Transaction.OnSuccess()

        {
            @Override
            public void onSuccess() {


                for (Device devices : realmDB.where(Device.class).findAllSorted("ipAddress", Sort.ASCENDING)) {

                    int i = 0;
                    AllDeviceSave.add(new Device(i, devices.getIpAddress(),
                            devices.getMacAddress(), devices.getDeviceName(), devices.getImage()
                            , devices.getTextColorIP(), devices.getTextColorMac(), devices.getTextColorDeviceName()
                            , devices.getTextColorMacVendor()));

                    i++;
                }

                adapter.setAddresses(AllDeviceSave);
                adapter.notifyDataSetChanged();

            }


        });

    }

    void initRecyclerView() {

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.animate();
        try {

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onItemClick(View view, int position) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                            intent.putExtra("IP", adapter.getAddresses().get(position).getIpAddress());
                            intent.putExtra("hostName", adapter.getAddresses().get(position).getDeviceName());
                            intent.putExtra("mac", adapter.getAddresses().get(position).getMacAddress());
                            startActivity(intent, options.toBundle());
                        }
                    })
            );

        } catch (Exception e) {


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    Toast.makeText(MainActivity.this, "Item Scroll", Toast.LENGTH_LONG).show();

                    if (dy > 0) {
                        hideFab();
                    } else if (dy < 0) {
                        showFab();
                    }
                }
            });

        }


    }

    private void InitViews() {

        this.externalIp = (TextView) findViewById(R.id.externalIpAddress);
        this.signalStrength = (TextView) findViewById(R.id.signalStrength);
        this.ssid = (TextView) findViewById(R.id.ssid);
        this.bssid = (TextView) findViewById(R.id.bssid);
        ipExternal = (TextView) findViewById(R.id.externalIpAddress);

        ExternalIp = "";
    }


    private void setupReceivers() {
        this.receiver = new BroadcastReceiver() {
            /**
             * Detect if a network connection has been lost or established
             * @param context
             * @param intent
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    if (info.isConnected()) {
                        getNetworkInfo();

                    } else {
                        mHandler.removeCallbacksAndMessages(null);

                        externalIp.setText(ExternalIp);
                        signalStrength.setText(R.string.noWifi);
                        ssid.setText(R.string.noWifi);
                        bssid.setText(R.string.noWifi);
                    }
                }
            }
        };
        try {
            this.intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(receiver, this.intentFilter);
        } catch (Exception e) {

        }
    }

    private void getExternalIp() {

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected() && mWifi.isAvailable()) {
            externalIp.setText(ExternalIp);
        } else {
            Toast.makeText(this, "wifi on device is offline", Toast.LENGTH_LONG).show();
        }


    }

    private void getInternalIp() {
        int netmask = this.wifi.getInternalWifiSubnet();
        String InternalIpWithSubnet = this.wifi.getInternalWifiIpAddress() + "/" + Integer.toString(netmask);

    }

    private void getNetworkInfo() {
        final int linkSpeed = wifi.getLinkSpeed();


        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                signalStrength.setText(String.valueOf(wifi.getSignalStrength()) + " dBm/" + linkSpeed + "Mbps");
                mHandler.postDelayed(this, TIMER_INTERVAL);

            }
        }, 2000);
        this.getInternalIp();
        this.ssid.setText(this.wifi.getSSID());
        this.bssid.setText(this.wifi.getBSSID());
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

    private void rescan() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected() && mWifi.isAvailable()) {
            AsyncSearch scan = new AsyncSearch(this);

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setProgress(0);
            pDialog.setMessage("Scanning your network");
            pDialog.setCancelable(false);
            pDialog.show();
            fab.setEnabled(false);

            scan.execute(adapter);

        } else {
            Toast.makeText(this, getString(R.string.not_connected_error), Toast.LENGTH_LONG).show();
        }

    }

    void checkForOL() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        ArrayList<Device> AllDeviceSave = new ArrayList<Device>();
        for (Device devices : realmDB.where(Device.class).findAll()) {
            int i = 0;
            AllDeviceSave.add(new Device(i, devices.getIpAddress(), devices.getMacAddress(), devices.getDeviceName(), imageData.drawableArray[0], imageData.TextColor[0], imageData.TextColor[0], imageData.TextColor[0], imageData.TextColor[0]));

            i++;
        }


        if (mWifi.isConnected() && mWifi.isAvailable() && AllDeviceSave.size() > 0) {
            AppCompatDialog dialog = new AppCompatDialog(MainActivity.this);
            dialog.setTitle(R.string.scanning);
            AsyncScanOnline scan = new AsyncScanOnline(dialog, getString(R.string.scanning_your_network));

            scan.execute(adapter);


        } else {


            Toast.makeText(this, "Device is offline", Toast.LENGTH_LONG).show();


        }
    }

    void loadDeviceOnDatabase() {

        ArrayList<Device> AllDeviceSave = new ArrayList<>();
        myImageURL imageData = new myImageURL();

        for (Device devices : realmDB.where(Device.class).findAllSorted("ipAddress", Sort.ASCENDING)) {

            int i = 0;
            AllDeviceSave.add(new Device(i, devices.getIpAddress(), devices.getMacAddress(), devices.getDeviceName(), imageData.drawableArray[0], imageData.TextColor[0], imageData.TextColor[0], imageData.TextColor[0], imageData.TextColor[0]));

            i++;
        }


        adapter.setAddresses(AllDeviceSave);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Realm.init(this);
        realmDB = Realm.getDefaultInstance();

    }

    /**
     * Activity restarted
     */
    @Override
    public void onRestart() {
        super.onRestart();
        Realm.init(this);
        realmDB = Realm.getDefaultInstance();
        registerReceiver(this.receiver, this.intentFilter);
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


            Intent modifySettings = new Intent(MainActivity.this, Settings.class);
            startActivity(modifySettings);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        Realm.init(this);
        realmDB = Realm.getDefaultInstance();

        super.onResume();
    }

    @Override
    public void onPause() {
        realmDB.close();
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();

        }
        super.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        realmDB.close();

        mHandler.removeCallbacksAndMessages(null);

        if (this.receiver != null) {
            unregisterReceiver(this.receiver);
        }

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }


    private class AsyncGetExternalIP extends AsyncTask<String, String, String> {

        //the return string Value
        private String result;


        @Override
        protected String doInBackground(String... params) {

            try {

                URL whatismyip = new URL("http://icanhazip.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

                result = in.readLine(); //you get the IP as a String


            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation

            ipExternal.setText(result);
            ExternalIp = result;
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }


    private class AsyncScanOnline extends AsyncTask<NetDeviceAdapter, Void, List<Device>> {

        private NetDeviceAdapter adapter;
        private AppCompatDialog mDialog;

        AsyncScanOnline(AppCompatDialog dialog, String string) {
            super();
            this.mDialog = dialog;
        }

        @Override
        protected List<Device> doInBackground(NetDeviceAdapter... voids) {
            List<Device> addresses;
            try {
                addresses = PingerForActiveDevice.getDevicesOnNetwork();
                Thread.sleep(1000);
                adapter = voids[0];
                return addresses;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(final List<Device> inetAddresses) {
            super.onPostExecute(inetAddresses);


            final ArrayList<Device> AllDeviceSave = new ArrayList<Device>();
            realmDB.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {

                    bgRealm.copyToRealmOrUpdate(inetAddresses);

                }
            }, new Realm.Transaction.OnSuccess()

            {
                @Override
                public void onSuccess() {


                    myImageURL imageData = new myImageURL();

                    for (Device devices : realmDB.where(Device.class).findAllSorted("ipAddress", Sort.ASCENDING)) {

                        int i = 0;
                        AllDeviceSave.add(new Device(i, devices.getIpAddress(), devices.getMacAddress(), devices.getDeviceName(), devices.getImage(), devices.getTextColorIP(), devices.getTextColorMac(), devices.getTextColorDeviceName(), devices.getTextColorMacVendor()));

                        i++;
                    }


                    adapter.setAddresses(AllDeviceSave);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(MainActivity.this, "online check successfull", Toast.LENGTH_LONG).show();

                }


            });


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            // adapter.notifyDataSetChanged();
        }


    }


    public String getItem(int position) {
        return adapter.getAddresses().toString();
    }

}


