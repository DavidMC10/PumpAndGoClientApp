package com.pumpandgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pumpandgo.entities.DefaultPaymentMethodResponse;
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

public class FuelAmountActivity extends AppCompatActivity {
    private static final String TAG = "FuelAmountActivity";

    // Declare layout fields.
    private NumberPicker fuelAmountNumberPicker;
    private Button authoriseButton;
    private ProgressBar loader;

    // Initialise variables.
    private String defaultPaymentMethod;
    private String[] numberPickerArray;
    private int fuelStationId;
    private String fuelStationName;
    private int fuelAmount;
    private int pumpNumber;

    // Initialise objects.
    ApiService service;
    TokenManager tokenManager;
    Call call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuelamount);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        // If no token go to the Login Activity.
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Get data from previous activity.
        fuelStationId = getIntent().getIntExtra("FUEL_STATION_ID", 0);
        fuelStationName = getIntent().getStringExtra("FUEL_STATION_NAME");
        pumpNumber = getIntent().getIntExtra("PUMP_NUMBER", 0);

        // View binding.
        fuelAmountNumberPicker = (NumberPicker) findViewById(R.id.fuelAmountNumberPicker);
        authoriseButton = (Button) findViewById(R.id.buttonAuthorise);
        loader = (ProgressBar) findViewById(R.id.progressBar);

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
        mTitle.setText(fuelStationName);

        // Add values to fuel amount number picker array.
        int minValue = 5;
        int maxValue = 100;
        int step = 5;
        numberPickerArray = new String[maxValue / minValue];
        for (int i = 0; i < numberPickerArray.length; i++) {
            numberPickerArray[i] = "€" + String.valueOf(step + i * step);
        }

        // Set Number Picker Attributes.
        fuelAmountNumberPicker.setMinValue(0);
        fuelAmountNumberPicker.setMaxValue(19);
        fuelAmountNumberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        fuelAmountNumberPicker.setDisplayedValues(numberPickerArray);
    }

    // Gets the user's default payment method.
    @OnClick(R.id.buttonAuthorise)
    public void getDefaultPaymentMethod() {
        loader.setVisibility(View.VISIBLE);
        call = service.getDefaultPaymentMethod();
        call.enqueue(new Callback<DefaultPaymentMethodResponse>() {
            @Override
            public void onResponse(Call<DefaultPaymentMethodResponse> call, Response<DefaultPaymentMethodResponse> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        fuelAmount = Integer.parseInt(numberPickerArray[fuelAmountNumberPicker.getValue()].substring(1));
                        createCharge(fuelAmount);
                    }
                } else if (response.code() == 404) {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        loader.setVisibility(View.INVISIBLE);
                        // If there is no default payment method then display an error message.
                        AlertDialog alertDialog = new AlertDialog.Builder(FuelAmountActivity.this).create();
                        alertDialog.setTitle("Error:");
                        alertDialog.setMessage("A payment method must be added to continue.");
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        finish();
                                    }
                                });
                        alertDialog.show();
                        // Change button colour.
                        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                } else {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<DefaultPaymentMethodResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Create a charge for the user.
    public void createCharge(int fuelAmount) {
        call = service.createCharge(fuelAmount);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        loader.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplication(), PumpActivity.class);
                        // Send data to the PumpActivity.
                        intent.putExtra("FUEL_STATION_ID", fuelStationId);
                        intent.putExtra("FUEL_AMOUNT", fuelAmount);
                        intent.putExtra("FUEL_STATION_NAME", fuelStationName);
                        intent.putExtra("PUMP_NUMBER", pumpNumber);
                        startActivity(intent);
                    }
                } else {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Allows the user to go to the payment method activity.
    @OnClick(R.id.textViewChangePaymentMethod)
    public void goToPaymentMethodActivity() {
        Intent intent = new Intent(this, PaymentMethodActivity.class);
        startActivity(intent);
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
