package com.app.yamamz.deviceipmacscanner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.yamamz.deviceipmacscanner.R;
import com.app.yamamz.deviceipmacscanner.model.Device;

import org.droitateddb.EntityService;

import java.util.List;


/**
 * Created by Admin on 9/27/2016.
 */
public class NetDeviceAdapter extends RecyclerView.Adapter<NetDeviceAdapter.ViewHolder> {

    private static final String CMD = "/system/bin/ping -q -n -w 1 -c 1 %s";
    private String ip;
    private List<Device> addresses;
    private int rowLayout;
private static boolean isReachable=false;
    @SuppressLint("StaticFieldLeak")
    public static Context context;


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
    public void onBindViewHolder(final NetDeviceAdapter.ViewHolder viewHolder, int i) {
        Device address = addresses.get(i);

        EntityService userService = new EntityService(mContext, Device.class);
        List<Device> allDevice = userService.get();



        //List<Boolean> onlineTrue;





        viewHolder.deviceName.setText(address.getDeviceName());
        viewHolder.deviceIp.setText(address.getIpAddress());
        viewHolder.macAdd.setText(address.getMacAddress());
        viewHolder.imageView.setImageResource(address.getImage());


    if (allDevice.get(i).getImage()==R.drawable.ic_cast_connected_white_48dp) {
        viewHolder.deviceName.setTextColor(ContextCompat.getColor(context, R.color.White));
        viewHolder.deviceIp.setTextColor(ContextCompat.getColor(context, R.color.White));
        viewHolder.macAdd.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        //  Toast.makeText(mContext,String.valueOf(addresses.get(i).getImage()),Toast.LENGTH_LONG).show();

    } else {
        viewHolder.deviceName.setTextColor(ContextCompat.getColor(context, R.color.gray));
        viewHolder.deviceIp.setTextColor(ContextCompat.getColor(context, R.color.gray));
        viewHolder.macAdd.setTextColor(ContextCompat.getColor(context, R.color.gray));


}
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

    private class AsyncTaskRunner extends AsyncTask<String, Boolean, Boolean> {

        private Boolean resp;


        @Override
        protected Boolean doInBackground(String... params) {

            try {



            } catch (Exception e) {
                e.printStackTrace();

            }
            return resp;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            // execution of result of Long time consuming operation


        }


        @Override
        protected void onPreExecute() {


        }


        @Override
        protected void onProgressUpdate(Boolean... text) {


        }
    }


}


