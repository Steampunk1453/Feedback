package com.feedback.football.remote;

import com.feedback.football.model.ProductBean;
import com.feedback.football.model.ProfileInfoVM;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {

    @POST("products")
    Call<ProductBean> savePost(@Body ProductBean productBean);

    @GET("products")
    Call<List<ProductBean>> getData();

    @GET("api/profile-info")
    Call<ProfileInfoVM> getDataRetro();
}