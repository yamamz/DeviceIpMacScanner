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
 * Created by AMRI on 10/2/2016.
 */

public class PortsAdapter extends RecyclerView.Adapter<PortsAdapter.MyViewHolder> {

    private List<Port> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.date);
            year = (TextView) view.findViewById(R.id.position);
        }
    }


    public PortsAdapter(List<Port> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_ports, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Port port = moviesList.get(position);
        holder.title.setText(port.getTitle());
        holder.genre.setText(port.getGenre());
        holder.year.setText(port.getYear());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}