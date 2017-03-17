package com.app.yamamz.deviceipmacscanner.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.yamamz.deviceipmacscanner.R;
import com.app.yamamz.deviceipmacscanner.model.Port;

import java.util.List;

/**
 * Created by yamamz on 10/2/2016.
 */

public class PortsAdapter extends RecyclerView.Adapter<PortsAdapter.MyViewHolder> {

    private List<Port> portsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView port, date, count;




        public MyViewHolder(View view) {
            super(view);
            port = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            count = (TextView) view.findViewById(R.id.position);
        }
    }


    public PortsAdapter(List<Port> portsList) {
        this.portsList =  portsList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_ports, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Port port = portsList.get(position);
        holder.port.setText(port.getPort());
        holder.count.setText(port.getCount());
        holder.date.setText(port.getDate());
    }

    @Override
    public int getItemCount() {
        return portsList.size();
    }
}