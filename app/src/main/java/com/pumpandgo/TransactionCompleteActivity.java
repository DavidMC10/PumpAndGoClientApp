package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pumpandgo.entities.ReceiptResponse;
import com.pumpandgo.entities.TransactionIdResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David McElhinney on 14/03/2020.
 */

public class TransactionCompleteActivity extends AppCompatActivity {
    private static final String TAG = "TransactionCompleteActivity";

    // Declare layout fields.
    private LinearLayout receiptRootLayout;
    private TextView textViewFuelStationName;
    private TextView textViewAddress;
    private TextView textViewDateTime;
    private TextView textViewFullName;
    private TextView textViewPaymentMethod;
    private TextView textViewPumpNumber;
    private TextView textViewFuelType;
    private TextView textViewFuelAmount;
    private TextView textViewPricePerLitre;
    private TextView textViewDiscount;
    private TextView textViewVatRate;
    private TextView textViewTotalExVat;
    private TextView textViewVat;
    private TextView textViewTotal;
    private ProgressBar loader;

    // Initialise objects.
    ApiService service;
    TokenManager tokenManager;
    Call call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactioncomplete);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        // If no token go to the Login Activity.
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // View bindings.
        receiptRootLayout = (LinearLayout) findViewById(R.id.receiptRootLayout);
        textViewFuelStationName = (TextView) findViewById(R.id.textViewFuelStationName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewDateTime = (TextView) findViewById(R.id.textViewDateTime);
        textViewFullName = (TextView) findViewById(R.id.textViewFullName);
        textViewPaymentMethod = (TextView) findViewById(R.id.textViewPaymentMethod);
        textViewPumpNumber = (TextView) findViewById(R.id.textViewPumpNumber);
        textViewFuelType = (TextView) findViewById(R.id.textViewFuelType);
        textViewFuelAmount = (TextView) findViewById(R.id.textViewFuelAmount);
        textViewPricePerLitre = (TextView) findViewById(R.id.textViewPricePerLitre);
        textViewDiscount = (TextView) findViewById(R.id.textViewDiscount);
        textViewVatRate = (TextView) findViewById(R.id.textViewVatRate);
        textViewTotalExVat = (TextView) findViewById(R.id.textViewTotalExVat);
        textViewVat = (TextView) findViewById(R.id.textViewVat);
        textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        loader = (ProgressBar) findViewById(R.id.progressBar);

        // Find the toolbar view inside the activity layout.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText("Receipt");

        // Make Api call.
        getRecentTransactionId();
    }

    // Gets the user's most recent transaction id.
    public void getRecentTransactionId() {
        loader.setVisibility(View.VISIBLE);
        receiptRootLayout.setVisibility(View.INVISIBLE);
        call = service.getRecentTransactionId();
        call.enqueue(new Callback<TransactionIdResponse>() {

            @Override
            public void onResponse(Call<TransactionIdResponse> call, Response<TransactionIdResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        int transactionId = response.body().getTransactionId();
                        getReceipt(transactionId);
                    }
                } else {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(TransactionCompleteActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionIdResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Gets a receipt for the user's transaction.
    public void getReceipt(int transactionId) {
        Log.d("transactionid", String.valueOf(transactionId));
        call = service.getReceipt(transactionId);
        call.enqueue(new Callback<ReceiptResponse>() {

            @Override
            public void onResponse(Call<ReceiptResponse> call, Response<ReceiptResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        loader.setVisibility(View.INVISIBLE);
                        receiptRootLayout.setVisibility(View.VISIBLE);

                        // Set text fields.
                        textViewFuelStationName.setText(response.body().getFuelStationName());
                        textViewAddress.setText(response.body().getFuelStationAddress1() + ", " + response.body().getfuelStationAddress2() + ", " + response.body().getfuelStationAddressCityTown());
                        textViewDateTime.setText(response.body().getTransactionDate());
                        textViewFullName.setText(response.body().getfirstName() + " " + response.body().getLastName());
                        textViewPaymentMethod.setText(response.body().getPaymentMethod());
                        textViewPumpNumber.setText(response.body().getPumpNumber());
                        textViewFuelType.setText(response.body().getFuelType());
                        textViewFuelAmount.setText(response.body().getNumberOfLitres() + " litres");
                        textViewPricePerLitre.setText("€" + response.body().getPricePerLitre());
                        textViewDiscount.setText(response.body().getDiscount() + "%");
                        textViewVatRate.setText(response.body().getVatRate() + "%");
                        textViewTotalExVat.setText("€" + response.body().getPriceExVat());
                        textViewVat.setText("€" + response.body().getVat());
                        textViewTotal.setText("€" + response.body().getTotalPrice());
                    }
                } else {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(TransactionCompleteActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReceiptResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Loads the PumpNumber Activity.
    @OnClick(R.id.buttonContinue)
    public void goToHomeActivity() {
        // Ensure activity is not null.
        if (getApplicationContext() != null) {
            startActivity(new Intent(TransactionCompleteActivity.this, HomeActivity.class));
            finish();
        }
    }

    // Cancels any api calls when the activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}
