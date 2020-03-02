package com.pumpandgo.network;

import com.pumpandgo.entities.AccessToken;
import com.pumpandgo.entities.FuelStationResponse;
import com.pumpandgo.entities.LocatingStationResponse;
import com.pumpandgo.entities.PaymentMethodResponse;
import com.pumpandgo.entities.PaymentTest;
import com.pumpandgo.entities.UserDetails;
import com.pumpandgo.entities.VisitCountResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
    Call<VisitCountResponse> visitCount();

    @POST("testing")
    @FormUrlEncoded
    Call<PaymentTest> paymentTest(@Field("token_id") String token_id);

    @POST("getcurrentstation")
    @FormUrlEncoded
    Call<LocatingStationResponse> checkIfAtFuelStation(@Field("latitude") double latitude, @Field("longitude") double longitude);

    @POST("getuserprofiledetails")
    Call<UserDetails> getUserProfileDetails();

    @POST("getnearbystations")
    @FormUrlEncoded
    Call<FuelStationResponse> getNearbyStations(@Field("latitude") double latitude, @Field("longitude") double longitude, @Field("max_distance_limit") int maxDistanceLimit);

    @POST("updatename")
    @FormUrlEncoded
    Call<Void> updateName(@Field("first_name") String firstName, @Field("last_name") String lastName);

    @POST("updateemail")
    @FormUrlEncoded
    Call<Void> updateEmail(@Field("email") String email);

    @POST("updatepassword")
    @FormUrlEncoded
    Call<Void> updatePassword(@Field("password") String password);

    @POST("updatedistancelimit")
    @FormUrlEncoded
    Call<Void> updateMaxDistanceLimit(@Field("max_distance_limit") int maxDistanceLimit);

    @POST("deletefuelcard")
    Call<Void> deleteFuelCard();

    @POST("retrievepaymentmethods")
    Call<PaymentMethodResponse> getPaymentMethods();
}