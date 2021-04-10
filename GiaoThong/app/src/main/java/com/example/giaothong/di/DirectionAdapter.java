package com.example.giaothong.di;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giaothong.R;
import com.example.giaothong.data.Step;

import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<com.example.giaothong.di.DirectionAdapter.ViewHolder> {
    private List<Step> stepList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView html_instructions;
        TextView distance;
        ImageView imgDirection;
        View layout;

        public ViewHolder(View view) {
            super(view);
            layout = view;
            html_instructions = view.findViewById(R.id.Html_instructions);
            distance = view.findViewById(R.id.distance);
            imgDirection = view.findViewById(R.id.imgDirection);
        }
    }

    public DirectionAdapter(@NonNull Context context, List<Step> stepList) {
        this.context = context;
        this.stepList = stepList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(
                viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_direction, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Step step = stepList.get(i);
//            if (step.getDistance() != null)
        viewHolder.distance.setText(step.getDistance());
        viewHolder.html_instructions.setText(Html.fromHtml(step.getHtml_instructions()));
        String maneuver;
        if (step.getManeuver() != null) {
            maneuver = step.getManeuver();
            switch (maneuver) {
                case "turn-right":
                    viewHolder.imgDirection.setImageResource(R.drawable.turn_right);
                    break;
                case "turn-left":
                    viewHolder.imgDirection.setImageResource(R.drawable.turn_left);
                    break;
                case "turn-slight-right":
                    viewHolder.imgDirection.setImageResource(R.drawable.slight_right);
                    break;
                case "turn-slight-left":
                    viewHolder.imgDirection.setImageResource(R.drawable.slide_left);
                    break;
                case "uturn-left":
                case "uturn-right":
                    viewHolder.imgDirection.setImageResource(R.drawable.u_turn);
                    break;
                case "keep-right":
                case "keep-left":
                    viewHolder.imgDirection.setImageResource(R.drawable.go_ahead);
                    break;
            }
        }
//            else if (i == 0) {
//                imgDirection.setImageResource(R.drawable.gps_green);
//            } else if (i == stepList.size()) {
//                imgDirection.setImageResource(R.drawable.gps_red);
//            }
        else {
            viewHolder.imgDirection.setImageResource(R.drawable.go_ahead);
        }

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }


}
