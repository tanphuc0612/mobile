package com.example.giaothong.ui.custom;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.giaothong.R;
import com.example.giaothong.data.JsonResponse;
import com.example.giaothong.service.HttpCommon;
import com.example.giaothong.ui.base.CameraBase;
import com.example.giaothong.ui.base.UploadApis;

import java.io.File;
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
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_photo);
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
                file = new File(getRealPathFromURI(image_uri));
                Retrofit retrofit = HttpCommon.getRetrofit();
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part parts = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

                RequestBody someData = RequestBody.create(MediaType.parse("text/plain"), "This is an new image");
                UploadApis uploadApis = retrofit.create(UploadApis.class);
                Call<List<JsonResponse>> call = uploadApis.uploadImage(parts, someData);
                call.enqueue(new Callback<List<JsonResponse>>() {
                    @Override
                    public void onResponse(Call<List<JsonResponse>> call, Response<List<JsonResponse>> response) {
                        for(JsonResponse model : response.body()) {
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

}