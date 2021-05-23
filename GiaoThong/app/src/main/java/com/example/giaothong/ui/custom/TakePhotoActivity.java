package com.example.giaothong.ui.custom;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.giaothong.R;
import com.example.giaothong.data.JsonResponse;
import com.example.giaothong.service.HttpCommon;
import com.example.giaothong.ui.base.CameraBase;
import com.example.giaothong.ui.base.UploadApis;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class TakePhotoActivity extends CameraBase {
    Button mCaptureBtn;
    Button btnUp;
    Button mSelectBtn;
    String trafficCode;
    File file;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_take_photo);
        mImageView = findViewById(R.id.img_imageview);
        mSelectBtn = findViewById(R.id.btn_selectImg);
        mCaptureBtn = findViewById(R.id.btn_captureImg);
        btnUp = findViewById(R.id.btn_Next);
        btnTakePhoto();
        getDeviceLocation();
        onClickBtnSelect();
        btnUp();
    }

    public void onClickBtnSelect() {
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                        //permission not enabled, request it
                        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to request permissions
                        requestPermissions(permission, PICK_IMAGE);
                    } else {
                        //permission already granted
                        openGallery();
                    }
                } else {
                    //system os < marshmallow
                    openGallery();
                }
            }
        });
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
                file = new File(getRealPathFromURI(image_uri));
                Retrofit retrofit = HttpCommon.getRetrofit();
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part parts = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mLastKnownLocation.getLatitude()));
                RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mLastKnownLocation.getLongitude()));
                UploadApis uploadApis = retrofit.create(UploadApis.class);
                Call<List<JsonResponse>> call = uploadApis.uploadImage(parts, latitude, longitude);
                call.enqueue(new Callback<List<JsonResponse>>() {
                    @Override
                    public void onResponse(Call<List<JsonResponse>> call, Response<List<JsonResponse>> response) {
                        ArrayList<String> mTrafficSign = new ArrayList<>();
                        for (JsonResponse a : response.body()
                        ) {
                            mTrafficSign.add(a.getName());
                        }
                        if (response.body().size() > 1) {
                            Intent intent = new Intent(TakePhotoActivity.this, Choose_TrafficSign.class);
                            intent.putExtra("image_uri", image_uri);
                            intent.putExtra("response", mTrafficSign);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(TakePhotoActivity.this, ThanksScreen.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(TakePhotoActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void postCode(){
        Retrofit retrofit = HttpCommon.getRetrofit();
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mLastKnownLocation.getLatitude()));
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mLastKnownLocation.getLongitude()));
        RequestBody code = RequestBody.create(MediaType.parse("text/plain"), trafficCode);
        UploadApis uploadApis = retrofit.create(UploadApis.class);
        Call<JsonResponse> call = uploadApis.uploadImageCode( latitude, longitude, code);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.body() == null) {
                    Toast.makeText(TakePhotoActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TakePhotoActivity.this, TakePhotoActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(TakePhotoActivity.this, ThanksScreen.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(TakePhotoActivity.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        Bundle extras = getIntent().getExtras();
                        if (extras != null) {
                            Integer code = (Integer) extras.get("image_code");
                            trafficCode = getResources().getResourceEntryName(code);
                            System.out.println("enter post code");
                            postCode();
                        }
                    } else {
                        System.out.println("Current location is null. Using defaults.");
                        System.out.println("Exception: " + task.getException());
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}