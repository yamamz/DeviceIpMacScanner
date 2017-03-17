package com.app.yamamz.deviceipmacscanner.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.yamamz.deviceipmacscanner.R;
import com.app.yamamz.deviceipmacscanner.controller.Host;
import com.app.yamamz.deviceipmacscanner.model.Device;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;


/**
 * Created by yamamz on 9/27/2016.
 */
public class NetDeviceAdapter extends  RecyclerView.Adapter<NetDeviceAdapter.ViewHolder> {

    private static final String CMD = "/system/bin/ping -q -n -w 1 -c 1 %s";
    private String ip;
    private ArrayList<Device> AllDeviceSave;
    private List<Device> addresses;
    private int rowLayout;
    private static boolean isReachable=false;
    @SuppressLint("StaticFieldLeak")

    private Context mContext;

    public NetDeviceAdapter(List<Device> addresses, int rowLayout, Context mContext) {
        this.addresses = addresses;
        this.rowLayout = rowLayout;
        this.mContext = mContext;

    }


    @Override
    public NetDeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
       return new ViewHolder(v);


    }

    @Override
    public void onBindViewHolder(final NetDeviceAdapter.ViewHolder viewHolder, int i) {

        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        Device address = addresses.get(i);
        viewHolder.deviceName.setText(address.getDeviceName());
        viewHolder.deviceIp.setText(address.getIpAddress());
        viewHolder.macAdd.setText(address.getMacAddress());
        viewHolder.imageView.setImageResource(address.getImage());
        viewHolder.deviceName.setTextColor(ContextCompat.getColor(mContext, address.getTextColorDeviceName()));
        viewHolder.deviceIp.setTextColor(ContextCompat.getColor(mContext, address.getTextColorIP()));
        viewHolder.macAdd.setTextColor(ContextCompat.getColor(mContext, address.getTextColorMac()));
        viewHolder.macVendor.setTextColor(ContextCompat.getColor(mContext, address.getTextColorMacVendor()));

        /** Get macVendor from Mac Address from database
         *  if mac is not Empty
         */

        String mac=viewHolder.macAdd.getText().toString();
        if(!mac.equals("")){
            viewHolder.macVendor.setText(Host.getMacVendor(viewHolder.macAdd.getText().toString().replace(":", "").substring(0, 6), (Activity) mContext));
        }
        else if(mac.equals("")){
            viewHolder.macVendor.setText("");
        }




/**
 * Check the wifi connectivity when it is offline
 * the text color will turn to gray to indicates that you don't have network connection
 *
 */
        try {
            if (!mWifi.isConnected() && mWifi.isAvailable()) {
                viewHolder.deviceName.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                viewHolder.deviceIp.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                viewHolder.macAdd.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                viewHolder.macVendor.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
            }

        }

        catch (Exception e){


        }
    }

    @Override
    public int getItemCount() {
        return addresses == null ? 0 : addresses.size();
    }

    /**
     *
     * @return
     */
    public List<Device> getAddresses() {
        return addresses;
    }

    /**
     *
     * @param addresses
     */
    public void setAddresses(List<Device> addresses) {

        this.addresses = addresses;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

 class ViewHolder extends RecyclerView.ViewHolder{
        private TextView deviceName;
        private TextView deviceIp;
        private TextView macAdd;
        private TextView macVendor;
        private ImageView imageView;
        private ViewHolder(View itemView) {
            super(itemView);

            deviceName = (TextView) itemView.findViewById(R.id.deviceName);
            deviceIp = (TextView) itemView.findViewById(R.id.deviceIp);
            macAdd = (TextView)itemView.findViewById(R.id.macAdd);
            imageView=(ImageView) itemView.findViewById(R.id.deviceLogo);
            macVendor=(TextView) itemView.findViewById(R.id.deviceMacvendor);
            mContext = itemView.getContext();

        }
    }






}


