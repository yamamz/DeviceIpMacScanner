package com.app.yamamz.deviceipmacscanner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.yamamz.deviceipmacscanner.R;
import com.app.yamamz.deviceipmacscanner.model.Device;

import java.util.List;

/**
 * Created by Admin on 9/27/2016.
 */
public class NetDeviceAdapter extends RecyclerView.Adapter<NetDeviceAdapter.ViewHolder> {


    private List<Device> addresses;
    private int rowLayout;

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    ViewHolder viewHolder;
    int lastPosition = -1;


    // Allows to remember the last item shown on screen



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

       // viewholder = new ViewHolder(v);
      //  return viewholder;
    }

    @Override
    public void onBindViewHolder(NetDeviceAdapter.ViewHolder viewHolder, int i) {
        Device address = addresses.get(i);
        viewHolder.deviceName.setText(address.getDeviceName());
        viewHolder.deviceIp.setText(address.getIpAddress());
        viewHolder.macAdd.setText(address.getMacAddress());


        viewHolder.imageView.setImageResource(R.drawable.ic_important_devices_green_a700_36dp);

    }

    @Override
    public int getItemCount() {

        //return (dataCursor == null) ? 0 : dataCursor.getCount();
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

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView deviceName;
        public TextView deviceIp;
        public TextView macAdd;

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            deviceName = (TextView) itemView.findViewById(R.id.deviceName);
            deviceIp = (TextView) itemView.findViewById(R.id.deviceIp);
            macAdd = (TextView)itemView.findViewById(R.id.macAdd);
            imageView=(ImageView) itemView.findViewById(R.id.deviceLogo);

            context = itemView.getContext();

        }
    }



}
