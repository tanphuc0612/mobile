package com.example.giaothong.ui.custom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.giaothong.MainActivity;
import com.example.giaothong.R;

public class ThanksScreen extends AppCompatActivity {

    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks_screen);

        btn_back = (Button) findViewById(R.id.btn_BackMain);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeintent = new Intent(ThanksScreen.this, MainActivity.class);
                ThanksScreen.this.startActivity(homeintent);
            }
        });
    }
}