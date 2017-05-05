package com.app.yamamz.deviceipmacscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.app.yamamz.deviceipmacscanner.model.Device;
import com.app.yamamz.deviceipmacscanner.runnable.Pinger;
import com.app.yamamz.deviceipmacscanner.runnable.PingerSubnet;
import com.app.yamamz.deviceipmacscanner.util.Subnet;
import com.app.yamamz.deviceipmacscanner.view.NetDeviceAdapter;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.http.conn.util.InetAddressUtils.isIPv4Address;

/**
 * Created by AMRI on 5/5/2017.
 */
public class AsyncSearch extends AsyncTask<NetDeviceAdapter,Integer, List<Device>> {

    private NetDeviceAdapter adapter;
    private boolean isSubnetSeachEnable;
    private String MaxIP;
    private String SubnetType;
    private String parseIPandSubnet;
    private Context context;
    AsyncSearch(Context context) {
        super();
        this.context=context;
    }


    @Override
    protected final void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        //   pDialog.setMessage(String.valueOf(values[0]));

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        getPreferences();

    }

    @Override
    protected List<Device> doInBackground(NetDeviceAdapter... voids) {

        List<Device> addresses;
        String ipString = getLocalIpv4Address();


        if (ipString == null) {
            return new ArrayList<Device>(1);
        }


        if (isSubnetSeachEnable && !SubnetType.equals("") && !MaxIP.equals("")) {


            try {
                Subnet subnet=new Subnet();
                subnet.setIPAddress(ipString);
                String Subnet=getSubnetMask(ipString);
                subnet.setSubnetMask(Subnet);
                String broadcast=subnet.getBroadcastAddress();
                int bits=subnet.getMaskedBits();
                String parse=broadcast+"/"+bits;
                addresses = PingerSubnet.getDevicesOnNetwork(parse);
                adapter = voids[0];

                return addresses;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (SocketException e) {
                e.printStackTrace();

            }

        } else {


            try {
                Subnet subnet=new Subnet();
                subnet.setIPAddress(ipString);
                String Subnet=getSubnetMask(ipString);
                subnet.setSubnetMask(Subnet);
                String broadcast=subnet.getBroadcastAddress();
                int bits=subnet.getMaskedBits();
                String parse=broadcast+"/"+bits;

                addresses = Pinger.getDevicesOnNetwork(parse);

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
        ((MainActivity)context).UpdateList(inetAddresses);
    }

    String getLocalIpv4Address() {
        try {
            String ipv4;
            List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            if (nilist.size() > 0) {
                for (NetworkInterface ni : nilist) {
                    List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                    if (ialist.size() > 0) {
                        for (InetAddress address : ialist) {
                            if (!address.isLoopbackAddress() && isIPv4Address(ipv4 = address.getHostAddress())) {
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

    void getPreferences() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        MaxIP = sharedPrefs.getString("IP", "");
        SubnetType = sharedPrefs.getString("subnet", "");
        isSubnetSeachEnable = sharedPrefs.getBoolean("perform_subnetSearch", false);
        parseIPandSubnet = MaxIP + "/" + SubnetType;
    }

String getSubnetMask(String ip){
    String checkclass = ip.substring(0, 3);
    int cc = Integer.parseInt(checkclass);
    String mask = null;
    if(cc>0 && cc<224)
    {
        if(cc<128)
        {
            mask = "255.0.0.0";
        }
        if(cc>127 && cc<192)
        {
            mask = "255.255.0.0";
        }
        if(cc>191)
        {
            mask = "255.255.255.0";
        }
    }

    return mask;
}

}
