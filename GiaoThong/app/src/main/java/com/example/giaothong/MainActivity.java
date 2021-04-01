package com.example.giaothong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.giaothong.ui.custom.TakePhotoActivity;
import com.example.giaothong.ui.map.CurrentLocationActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout layoutFindRouting, layoutFindAddress, layoutCurrentPlace, layoutTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        setOnClickListener();
    }

    private void initUI() {
        layoutFindAddress = findViewById(R.id.layoutFindAddress);
        layoutFindRouting = findViewById(R.id.layoutFindRouting);
        layoutCurrentPlace = findViewById(R.id.layoutCurrentPlace);
        layoutTakePhoto = findViewById(R.id.layoutTakePhoto);
    }

    private void setOnClickListener() {
        layoutFindAddress.setOnClickListener(this);
        layoutFindRouting.setOnClickListener(this);
        layoutCurrentPlace.setOnClickListener(this);
        layoutTakePhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutCurrentPlace:
                startActivity(new Intent(MainActivity.this, CurrentLocationActivity.class));
                break;
            case R.id.layoutTakePhoto:
                startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
                break;
        }
    }
}