package com.app.yamamz.deviceipmacscanner;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.app.yamamz.deviceipmacscanner.controller.Host;
import com.app.yamamz.deviceipmacscanner.controller.Pinger;
import com.app.yamamz.deviceipmacscanner.controller.PingerForActiveDevice;
import com.app.yamamz.deviceipmacscanner.controller.PingerSubnet;
import com.app.yamamz.deviceipmacscanner.controller.Wireless;
import com.app.yamamz.deviceipmacscanner.controller.myImageURL;
import com.app.yamamz.deviceipmacscanner.model.Device;
import com.app.yamamz.deviceipmacscanner.view.DeviderItemDecoration;
import com.app.yamamz.deviceipmacscanner.view.NetDeviceAdapter;
import com.app.yamamz.deviceipmacscanner.view.RecyclerItemClickListener;
import com.app.yamamz.deviceipmacscanner.view.Settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

import static org.apache.http.conn.util.InetAddressUtils.isIPv4Address;

public class MainActivity extends AppCompatActivity   {

    private NetDeviceAdapter adapter = new NetDeviceAdapter(new ArrayList<Device>(15), R.layout.device_fragment, this);

    private  ProgressBar progressBarPing;
    private FloatingActionButton fab;
    private boolean isFabShowing=true;
     private    PingerForActiveDevice pinger;

    private Device d=new Device();
    private boolean isSubnetSeachEnable;
    private String MaxIP;
    private String SubnetType;
    private String parseIPandSubnet;
    private final static int TIMER_INTERVAL = 100000;
    private Wireless wifi;
    private TextView internalIp;
    private TextView externalIp;
    private TextView signalStrength;
    private TextView ssid;
    private TextView bssid;
    private Realm realm;
    private Handler mHandler = new Handler();
    private  TextView ipExternal;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter = new IntentFilter();
    private String ExternalIp;
    private myImageURL imageData=new myImageURL();


   @SuppressWarnings("deprecation")

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
         Realm.init(this);
         realm = Realm.getDefaultInstance();
         pinger=new PingerForActiveDevice(this);


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
         InitCheckForOnline();
        initFab();



        this.wifi = new Wireless(getApplicationContext());
        this.setupReceivers();
        this.setupMac();






    }

    void initFab(){
        fab = (FloatingActionButton) findViewById(R.id.fabMain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreferences();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                   //     bgRealm.delete(Device.class);
                    }
                }, new Realm.Transaction.OnSuccess()

                {
                    @Override
                    public void onSuccess () {
                        Toast.makeText(MainActivity.this,"Search Device on Network",Toast.LENGTH_LONG).show();
                        rescan();
                    }

                });
            }
        });


    }

  void InitCheckForOnline(){

      this.mHandler.postDelayed(new Runnable() {
          @Override
          public void run() {
              checkForOL();
              mHandler.postDelayed(this, TIMER_INTERVAL);

          }
      }, 3000);

  }



   void initRecyclerView(){


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DeviderItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());


       recyclerView.animate();
       try {
           final String ip = d.getIpAddress();
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
                   //super.onScrolled(recyclerView, dx, dy);
                   Toast.makeText(MainActivity.this,"Item Scroll",Toast.LENGTH_LONG).show();

                   if (dy > 0) {
                       hideFab();
                   } else if (dy < 0) {
                       showFab();
                   }
               }
           });

       }


   }

   private void InitViews(){

        this.internalIp = (TextView) findViewById(R.id.internalIpAddress);
        this.externalIp = (TextView) findViewById(R.id.externalIpAddress);
        this.signalStrength = (TextView) findViewById(R.id.signalStrength);
        this.ssid = (TextView) findViewById(R.id.ssid);
        this.bssid = (TextView) findViewById(R.id.bssid);
        ipExternal = (TextView) findViewById(R.id.externalIpAddress);
        progressBarPing = (ProgressBar) this.findViewById(R.id.progress);
       ExternalIp="";
    }

    private void setupMac() {
        //Set MAC address
        TextView macAddress = (TextView) findViewById(R.id.deviceMacAddress);
        String mac = this.wifi.getMacAddress();
        macAddress.setText(mac);
        //Set the device's vendor
        if (mac != null) {
            TextView macVendor = (TextView) findViewById(R.id.deviceMacVendor);
            macVendor.setText(Host.getMacVendor(mac.replace(":", "").substring(0, 6), this));
        }
    }

    private void setupReceivers() {
        this.receiver = new BroadcastReceiver() {

            /**
             * Detect if a network connection has been lost or established
             *
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
                        internalIp.setText(Wireless.getInternalMobileIpAddress());
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
}
     catch (Exception e) {

     }
    }
        private void getExternalIp() {

            ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected() && mWifi.isAvailable()) {
            externalIp.setText(ExternalIp);
            } else {
                Toast.makeText(this,"wifi on device is offline", Toast.LENGTH_LONG).show();
               // stopProgressBar();
            }


    }
    private void getInternalIp() {
        int netmask = this.wifi.getInternalWifiSubnet();
        String InternalIpWithSubnet = this.wifi.getInternalWifiIpAddress() + "/" + Integer.toString(netmask);
        this.internalIp.setText(InternalIpWithSubnet);
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
void getPreferences(){
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    MaxIP=sharedPrefs.getString("IP", "");
    SubnetType=sharedPrefs.getString("subnet", "");
    isSubnetSeachEnable=sharedPrefs.getBoolean("perform_subnetSearch",false);
    parseIPandSubnet=MaxIP+"/"+SubnetType;
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
            AsyncScanSearch scan = new AsyncScanSearch(dialog, getString(R.string.scanning_your_network));
          scan.execute(adapter);
           startProgressBar();
        }else {
            Toast.makeText(this, getString(R.string.not_connected_error), Toast.LENGTH_LONG).show();
        }

    }

    void checkForOL(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        ArrayList<Device> AllDeviceSave = new ArrayList<Device>();
        for (Device devices : realm.where(Device.class).findAll()) {
            int i=0;
            AllDeviceSave.add(new Device(i, devices.getIpAddress(), devices.getMacAddress(), devices.getDeviceName(), imageData.drawableArray[0],imageData.TextColor[0],imageData.TextColor[0],imageData.TextColor[0],imageData.TextColor[0]));

            i++;
        }


        if (mWifi.isConnected() && mWifi.isAvailable() && AllDeviceSave.size()>0) {
            AppCompatDialog dialog = new AppCompatDialog(MainActivity.this);
            dialog.setTitle(R.string.scanning);
            dialog.setCancelable(false);

            AsyncScanOnline scan = new AsyncScanOnline(dialog, getString(R.string.scanning_your_network));

            scan.execute(adapter);


        } else {


            Toast.makeText(this,"Device is offline", Toast.LENGTH_LONG).show();
            stopProgressBar();


        }
    }

    void loadDeviceOnDatabase(){

        ArrayList<Device> AllDeviceSave = new ArrayList<>();
        myImageURL imageData = new myImageURL();

        for (Device devices : realm.where(Device.class).findAllSorted("ipAddress", Sort.ASCENDING)) {

            int i=0;
            AllDeviceSave.add(new Device(i, devices.getIpAddress(), devices.getMacAddress(), devices.getDeviceName(), imageData.drawableArray[0],imageData.TextColor[0],imageData.TextColor[0],imageData.TextColor[0],imageData.TextColor[0]));

            i++;
        }



        adapter.setAddresses(AllDeviceSave);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Realm.init(this);
        realm = Realm.getDefaultInstance();

    }

    /**
     * Activity restarted
     */
    @Override
    public void onRestart() {
        super.onRestart();
        Realm.init(this);
        realm = Realm.getDefaultInstance();
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


            Intent modifySettings=new Intent(MainActivity.this,Settings.class);
            startActivity(modifySettings);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        super.onResume();
    }

    @Override
    public void onPause() {
        realm.close();

        super.onPause();
    }
    @Override
    public void onDestroy() {

            super.onDestroy();
        realm.close();

            mHandler.removeCallbacksAndMessages(null);

            if (this.receiver != null) {
                unregisterReceiver(this.receiver);
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
          ExternalIp=result;
        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }


    private  class AsyncScanSearch extends AsyncTask<NetDeviceAdapter, Void, List<Device>> {

        private NetDeviceAdapter adapter;
        private AppCompatDialog mDialog;


        AsyncScanSearch(AppCompatDialog dialog, String string) {
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


         if(isSubnetSeachEnable&& !SubnetType.equals("") && !MaxIP.equals("")){


             try {

                 addresses = PingerSubnet.getDevicesOnNetwork(parseIPandSubnet);
                 Thread.sleep(2000);
                 adapter = voids[0];
                 return addresses;
             } catch (InterruptedException e) {
                 e.printStackTrace();
                 return null;
             } catch (SocketException e) {
                 e.printStackTrace();

             }

         }

            else {


             int lastdot = ipString.lastIndexOf(".");
             ipString = ipString.substring(0, lastdot);
             //ipString="192.168.85";
             try {

                 addresses = Pinger.getDevicesOnNetwork(ipString);
                 Thread.sleep(1000);
                 adapter = voids[0];
                 return addresses;
             } catch (InterruptedException e) {
                 e.printStackTrace();
                 return null;
             } catch (SocketException e) {
                 e.printStackTrace();

             }
         }

            return null;
        }

        @Override

        protected void onPostExecute(final List<Device> inetAddresses) {
            super.onPostExecute(inetAddresses);

            final ArrayList<Device> AllDeviceSave = new ArrayList<Device>();

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {

                    bgRealm.copyToRealmOrUpdate(inetAddresses);

                }
            }, new Realm.Transaction.OnSuccess()

            {
                @Override
                public void onSuccess () {


                    for (Device devices : realm.where(Device.class).findAllSorted("ipAddress",Sort.ASCENDING)) {

                        int i=0;
                        AllDeviceSave.add(new Device(i, devices.getIpAddress(),
                                devices.getMacAddress(), devices.getDeviceName(), devices.getImage()
                                ,devices.getTextColorIP(),devices.getTextColorMac(),devices.getTextColorDeviceName()
                                ,devices.getTextColorMacVendor()));

                        i++;
                    }

                    adapter.setAddresses(AllDeviceSave);
                    adapter.notifyDataSetChanged();

                    stopProgressBar();
                    // Original queries and Realm objects are automatically updated.
                    //    puppies.size(); // => 0 because there are no more puppies younger than 2 years old
                    //  managedDog.getAge();   // => 3 the dogs age is updated
                }


            });










        }

        @Override
        protected void onProgressUpdate(Void... values) {


            super.onProgressUpdate(values);

           // adapter.notifyDataSetChanged();

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
    private  class AsyncScanOnline extends AsyncTask<NetDeviceAdapter, Void, List<Device>> {

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
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {

                    bgRealm.copyToRealmOrUpdate(inetAddresses);

                }
          }, new Realm.Transaction.OnSuccess()

                {
                    @Override
                    public void onSuccess () {



                        myImageURL imageData = new myImageURL();

                        for (Device devices : realm.where(Device.class).findAllSorted("ipAddress", Sort.ASCENDING)) {

                            int i=0;
                            AllDeviceSave.add(new Device(i, devices.getIpAddress(), devices.getMacAddress(), devices.getDeviceName(), devices.getImage(),devices.getTextColorIP(),devices.getTextColorMac(),devices.getTextColorDeviceName(),devices.getTextColorMacVendor()));

                            i++;
                        }


                        adapter.setAddresses(AllDeviceSave);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(MainActivity.this,"online check successfull",Toast.LENGTH_LONG).show();

                }


            });





    stopProgressBar();


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            // adapter.notifyDataSetChanged();
        }




    }



    public void startProgressBar() {
        progressBarPing.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        progressBarPing.setVisibility(View.GONE);
    }

    public String getItem(int position) {
        return adapter.getAddresses().toString();
    }

    }


