package com.example.giaothong.ui.custom;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.giaothong.R;
import com.example.giaothong.service.HttpCommon;
import com.example.giaothong.ui.base.CameraBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class TakePhotoActivity extends CameraBase {
    Button mCaptureBtn;
    Button btnUp;
    HttpCommon http = new HttpCommon();
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_photo);
        this.http.activity = TakePhotoActivity.this;
        this.http.requestQueue = Volley.newRequestQueue(this);
        mImageView = findViewById(R.id.imageview);
        mCaptureBtn = findViewById(R.id.button3);
        btnUp = findViewById(R.id.btn_Up);

        btnTakePhoto();
        btnUp();
    }

    public void btnTakePhoto() {
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if system os is >= marshmallow, request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                        //permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to request permissions
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        openCamera();
                    }
                } else {
                    //system os < marshmallow
                    openCamera();
                }
            }
        });
    }

    public void btnUp() {
        btnUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("img", file.getAbsoluteFile());
                    http.postMethod("/marker", object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}