package com.example.giaothong.ui.base;

import com.example.giaothong.data.JsonResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApis {
    @Multipart
    @POST("marker")
    Call<List<JsonResponse>> uploadImage(@Part MultipartBody.Part part, @Part("somedata") RequestBody requestBody) ;
}
