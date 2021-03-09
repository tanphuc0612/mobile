package com.example.lightmap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lightmap.R;
import com.example.lightmap.model.Address;

import java.util.List;

public class AddressAdapter extends BaseAdapter {
    private List<Address> addressList;
    private Context context;

    public AddressAdapter(@NonNull Context context, List<Address> addressList) {
        this.addressList = addressList;
        this.context = context;
    }

    static class ViewHolder {
        TextView txtAddressTitle;
        TextView txtAddressDetail;
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Object getItem(int i) {
        return addressList.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.item_address, null);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.txtAddressTitle = row.findViewById(R.id.addressTitle);
            viewHolder.txtAddressDetail = row.findViewById(R.id.addressDetail);
            row.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) row.getTag();

        holder.txtAddressTitle.setText(addressList.get(i).getAddressTitle());
        holder.txtAddressDetail.setText(addressList.get(i).getAddressDetail());
        return row;
    }
}
