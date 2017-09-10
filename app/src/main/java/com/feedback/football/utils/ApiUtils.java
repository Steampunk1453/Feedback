package com.feedback.football.utils;

import com.feedback.football.remote.APIService;
import com.feedback.football.remote.RetrofitClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

    public static final String BASE_URL = "https://boot-heroku.herokuapp.com/v1/";
    public static final String BASE_URL_RETRO = "https://retro-feedback.herokuapp.com/";

    private ApiUtils() {
    }

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://boot-heroku.herokuapp.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
