package com.example.lightmap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lightmap.R;
import com.example.lightmap.model.Bus.StepBus;
import com.example.lightmap.model.TransitDetail;
import com.google.maps.model.TravelMode;

import java.util.List;

public class DirectionBusAdapter extends RecyclerView.Adapter<DirectionBusAdapter.ViewHolder> {
    private List<StepBus> stepBusList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        ImageView imgDirection;
        View lineBus;
        TextView departure_stop_name;
        TextView busName;
        TextView headsign, num_stop, arrival_stop;
        View layout;
        ImageView imgStop;

        public ViewHolder(View view) {
            super(view);
            layout = view;

            imgDirection = view.findViewById(R.id.imgDirection);
            lineBus = view.findViewById(R.id.lineBus);
            departure_stop_name = view.findViewById(R.id.departure_stop_name);
            busName = view.findViewById(R.id.busName);
            headsign = view.findViewById(R.id.headsign);
            num_stop = view.findViewById(R.id.num_stop);
            arrival_stop = view.findViewById(R.id.arrival_stop);
            imgStop = view.findViewById(R.id.imgStop);
        }
    }

    public DirectionBusAdapter(@NonNull Context context, List<StepBus> stepBusList) {
        this.context = context;
        this.stepBusList = stepBusList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(
                viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_direction_bus, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        StepBus stepBus = stepBusList.get(i);

        TransitDetail transitDetail = stepBus.getTransitDetail();
        if (stepBus.getTravel_mode().equals(TravelMode.WALKING)) {
            viewHolder.departure_stop_name.setTextSize(16);
            viewHolder.departure_stop_name.setTextColor(Color.parseColor("#FF616161"));
            viewHolder.departure_stop_name.setText(stepBus.getHtml_instructions() + "\n" + stepBus.getDistance());
            viewHolder.imgDirection.setImageResource(R.drawable.ic_walk);
            viewHolder.lineBus.setVisibility(View.GONE);
            viewHolder.busName.setVisibility(View.GONE);
            viewHolder.headsign.setVisibility(View.GONE);
            viewHolder.num_stop.setVisibility(View.GONE);
            viewHolder.arrival_stop.setVisibility(View.GONE);
            viewHolder.imgStop.setVisibility(View.GONE);

            return;
        }
        viewHolder.lineBus.setVisibility(View.VISIBLE);
        viewHolder.departure_stop_name.setText(transitDetail.getDeparture_stop_name());
        viewHolder.busName.setText(transitDetail.getBusName());
        viewHolder.headsign.setText(transitDetail.getHeadsign());
        viewHolder.num_stop.setText("Rides " + transitDetail.getNum_stop() + " stops");
        viewHolder.arrival_stop.setText(transitDetail.getArrival_stop());
//            else if (i == 0) {
//                imgDirection.setImageResource(R.drawable.gps_green);
//            } else if (i == stepList.size()) {
//                imgDirection.setImageResource(R.drawable.gps_red);
//            }

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return stepBusList.size();
    }

}
