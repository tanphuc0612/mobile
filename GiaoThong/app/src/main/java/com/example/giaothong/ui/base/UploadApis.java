package com.example.giaothong.ui.base;

import com.example.giaothong.data.JsonResponse;
import com.example.giaothong.data.MarkerResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApis {
    @Multipart
    @POST("marker")
    Call<List<JsonResponse>> uploadImage(@Part MultipartBody.Part part, @Part("latitude") RequestBody latitude, @Part("longitude") RequestBody longitude) ;

    @Multipart
    @POST("marker")
    Call<List<JsonResponse>> uploadImageCode(@Part("latitude") RequestBody latitude, @Part("longitude") RequestBody longitude, @Part("code") RequestBody code) ;

    @GET("marker")
    Call<List<MarkerResponse>> getImage() ;
}
