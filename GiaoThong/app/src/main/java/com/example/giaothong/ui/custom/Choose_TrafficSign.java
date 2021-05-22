package com.example.giaothong.ui.custom;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giaothong.R;

import java.util.ArrayList;

public class Choose_TrafficSign extends AppCompatActivity {

    private ArrayList<TrafficSign_WillBeChosen> mTrafficSign ;
    private RecyclerView mRecyclerTrafficSign;
    private ChooseTrafficSignAdapter mTrafficSignAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose__traffic_sign);




        mRecyclerTrafficSign = findViewById(R.id.recyclerTrafficSign);
        mTrafficSign = new ArrayList<>();
        createTrafficSignList();
        mTrafficSignAdapter = new ChooseTrafficSignAdapter(this,mTrafficSign);
        mRecyclerTrafficSign.setAdapter(mTrafficSignAdapter);
        mRecyclerTrafficSign.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));




        ImageView iv_photo= (ImageView) findViewById(R.id.imageViewMain);
        Bundle extras= getIntent().getExtras();
        if(extras!=null)
        {
            Uri path = (Uri) extras.get("image_uri");
            iv_photo.setImageURI(path);
        }

    }

    private void createTrafficSignList() {
        mTrafficSign.add(new TrafficSign_WillBeChosen(R.drawable.c_102));
        mTrafficSign.add(new TrafficSign_WillBeChosen(R.drawable.c_103a));
        mTrafficSign.add(new TrafficSign_WillBeChosen(R.drawable.c_103b));
    }


}