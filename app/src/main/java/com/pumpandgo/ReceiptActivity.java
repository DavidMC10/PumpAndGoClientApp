package com.pumpandgo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pumpandgo.entities.LocatingStationResponse;
import com.pumpandgo.entities.ReceiptResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptActivity extends AppCompatActivity {
    private static final String TAG = "ReceiptActivity";

    @BindView(R.id.receiptRootLayout)
    LinearLayout receiptRootLayout;
    @BindView(R.id.textViewFuelStationName)
    TextView textViewFuelStationName;
    @BindView(R.id.textViewAddress)
    TextView textViewAddress;
    @BindView(R.id.textViewDateTime)
    TextView textViewDateTime;
    @BindView(R.id.textViewFullName)
    TextView textViewFullName;
    @BindView(R.id.textViewPaymentMethod)
    TextView textViewPaymentMethod;
    @BindView(R.id.textViewPumpNumber)
    TextView textViewPumpNumber;
    @BindView(R.id.textViewFuelType)
    TextView textViewFuelType;
    @BindView(R.id.textViewFuelAmount)
    TextView textViewFuelAmount;
    @BindView(R.id.textViewPricePerLitre)
    TextView textViewPricePerLitre;
    @BindView(R.id.textViewDiscount)
    TextView textViewDiscount;
    @BindView(R.id.textViewVatRate)
    TextView textViewVatRate;
    @BindView(R.id.textViewTotalExVat)
    TextView textViewTotalExVat;
    @BindView(R.id.textViewVat)
    TextView textViewVat;
    @BindView(R.id.textViewTotal)
    TextView textViewTotal;
    @BindView(R.id.progressBar)
    ProgressBar loader;

    // Declaration variables
    ApiService service;
    TokenManager tokenManager;
    Call<ReceiptResponse> call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Find the toolbar view inside the activity layout.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set back arrow.
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_keyboard_backspace_24px);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText("Receipt");

        // Get data from TransactionHistoryListAdapter
        int transactionId = getIntent().getIntExtra("TRANSACTION_ID",0);

        if (transactionId != 0) {
            getReceipt(transactionId);
        }
    }

    // Gets a receipt for the user's transaction.
    public void getReceipt(int transactionId) {
        loader.setVisibility(View.VISIBLE);
        receiptRootLayout.setVisibility(View.INVISIBLE);
        call = service.getReceipt(transactionId);
        call.enqueue(new Callback<ReceiptResponse>() {

            @Override
            public void onResponse(Call<ReceiptResponse> call, Response<ReceiptResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    loader.setVisibility(View.INVISIBLE);
                    receiptRootLayout.setVisibility(View.VISIBLE);

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
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(ReceiptActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ReceiptResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Kill activity when the back button is pressed.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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

