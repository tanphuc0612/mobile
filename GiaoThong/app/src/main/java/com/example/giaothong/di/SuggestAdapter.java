package com.example.giaothong.di;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.giaothong.R;
import com.example.giaothong.data.Address;

import java.util.List;

public class SuggestAdapter extends BaseAdapter {
    private List<Address> list;
    private Context mContext;

    public SuggestAdapter(List<Address> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_suggestion, null);
        }
        TextView txtName = view.findViewById(R.id.txtName);
        txtName.setText(list.get(position).getAddressTitle());
        return view;
    }
}
