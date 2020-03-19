package com.pumpandgo.network;

import com.pumpandgo.entities.AccessToken;
import com.pumpandgo.entities.DefaultPaymentMethodResponse;
import com.pumpandgo.entities.FuelStationResponse;
import com.pumpandgo.entities.LocatingStationResponse;
import com.pumpandgo.entities.PaymentMethodResponse;
import com.pumpandgo.entities.PaymentTest;
import com.pumpandgo.entities.ReceiptResponse;
import com.pumpandgo.entities.RewardResponse;
import com.pumpandgo.entities.TransactionHistoryResponse;
import com.pumpandgo.entities.TransactionIdResponse;
import com.pumpandgo.entities.UserDetailsResponse;
import com.pumpandgo.entities.VisitCountResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by David McElhinney on 14/03/2020.
 */

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("first_name") String firstName, @Field("last_name") String lastName, @Field("email") String email, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("email") String email, @Field("password") String password);

    @POST("logout")
    Call<Void> logout();

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("createpasswordresettoken")
    @FormUrlEncoded
    Call<Void> sendPasswordResetEmail(@Field("email") String email);

    @POST("visitcount")
    Call<VisitCountResponse> visitCount();

    @POST("getrewards")
    Call<RewardResponse> getRewards();

    @POST("testing")
    @FormUrlEncoded
    Call<PaymentTest> paymentTest(@Field("token_id") String token_id);

    @POST("getcurrentstation")
    @FormUrlEncoded
    Call<LocatingStationResponse> checkIfAtFuelStation(@Field("latitude") double latitude, @Field("longitude") double longitude);

    @POST("getuserprofiledetails")
    Call<UserDetailsResponse> getUserProfileDetails();

    @POST("getnearbystations")
    @FormUrlEncoded
    Call<FuelStationResponse> getNearbyStations(@Field("latitude") double latitude, @Field("longitude") double longitude, @Field("max_distance_limit") int maxDistanceLimit);

    @POST("gettransactionhistory")
    Call<TransactionHistoryResponse> getTransactionHistory();

    @POST("getreceipt")
    @FormUrlEncoded
    Call<ReceiptResponse> getReceipt(@Field("transaction_id") int transactionId);

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

    @POST("addstripecard")
    @FormUrlEncoded
    Call<Void> addStripeCard(@Field("card_number") String cardNumber, @Field("exp_month") String expMonth, @Field("exp_year") String expYear, @Field("cvc") String cvc);

    @POST("updatestripecard")
    @FormUrlEncoded
    Call<Void> updateStripeCard(@Field("card_id") String cardId, @Field("exp_month") String expMonth, @Field("exp_year") String expYear);

    @POST("deletestripecard")
    @FormUrlEncoded
    Call<Void> deleteStripeCard(@Field("card_id") String cardId);

    @POST("addfuelcard")
    @FormUrlEncoded
    Call<Void> addFuelCard(@Field("fuel_card_no") String fuelCardNo, @Field("exp_month") String expMonth, @Field("exp_year") String expYear);

    @POST("updatefuelcard")
    @FormUrlEncoded
    Call<Void> updateFuelCard(@Field("exp_month") String expMonth, @Field("exp_year") String expYear);

    @POST("deletefuelcard")
    Call<Void> deleteFuelCard();

    @POST("getdefaultpaymentmethod")
    Call<DefaultPaymentMethodResponse> getDefaultPaymentMethod();

    @POST("setdefaultpaymentmethod")
    @FormUrlEncoded
    Call<Void> setDefaultPaymentMethod(@Field("default_payment_method") String cardId);

    @POST("retrievepaymentmethods")
    Call<PaymentMethodResponse> getPaymentMethods();

    @POST("createcharge")
    @FormUrlEncoded
    Call<Void> createCharge(@Field("fuel_amount") int fuelAmount);

    @POST("getrecenttransactionid")
    Call<TransactionIdResponse> getRecentTransactionId();

    @POST("createtransaction")
    @FormUrlEncoded
    Call<Void> createTransaction(@Field("fuel_station_id") int fuelStationId, @Field("fuel_amount") int fuelStationAmount, @Field("pump_number") int pumpNumber);
}