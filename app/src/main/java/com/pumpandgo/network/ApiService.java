package com.pumpandgo.network;

import com.pumpandgo.entities.AccessToken;
import com.pumpandgo.entities.VisitCount;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("first_name") String firstName, @Field("last_name") String lastName, @Field("email") String email, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("email") String email, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("visitcount")
    Call<VisitCount> visitCount();

    @POST("getnearbystations")
    @FormUrlEncoded
    Call<AccessToken> getNearbyStations();
}