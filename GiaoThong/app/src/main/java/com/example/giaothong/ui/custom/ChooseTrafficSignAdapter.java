package com.example.giaothong.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.giaothong.R;

import java.util.ArrayList;

public class ChooseTrafficSignAdapter extends RecyclerView.Adapter<ChooseTrafficSignAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton imageTrafficSign;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTrafficSign = itemView.findViewById(R.id.imageButtonTraffic);

        }

    }


    private Context mContext;
    private ArrayList<TrafficSign_WillBeChosen> mTrafficSign;

    public ChooseTrafficSignAdapter(Context mContext, ArrayList<TrafficSign_WillBeChosen> mTrafficSign) {
        this.mContext = mContext;
        this.mTrafficSign = mTrafficSign;
    }

    @NonNull
    @Override
    public ChooseTrafficSignAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View chooseTrafficSignView = inflater.inflate(R.layout.activity_custom_choose_trafficsign, parent, false);
        ViewHolder viewHolder = new ViewHolder(chooseTrafficSignView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseTrafficSignAdapter.ViewHolder holder, int position) {
        TrafficSign_WillBeChosen trafficSign = mTrafficSign.get(position);
        Glide.with(mContext)
                .load(trafficSign.getCode())
                .into(holder.imageTrafficSign);
        holder.imageTrafficSign.setOnClickListener((view -> {
            Intent intent = new Intent(mContext, TakePhotoActivity.class);
            intent.putExtra("image_code", trafficSign.getCode());
            mContext.startActivity(intent);
            //(*) v???n ch??a l???y ??c v??? tr?? t???m ???nh l??n.
            // Ch??? n??y ????? th???c hi???n upload ???nh l??n server.
            // Ti???p theo n??n render ra m??n h??nh ch???a map v?? marker c???a ???nh v???a ch???n ????nh tr??n ????.
        }));
    }

    @Override
    public int getItemCount() {
        return mTrafficSign.size();
    }
}
