package com.pumpandgo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.pumpandgo.entities.LocatingStationResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocatingStationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LocatingStationActivity";
    public static final int LOCATION_REQUEST = 101;
    private GoogleApiClient googleApiClient;

    ApiService service;
    TokenManager tokenManager;
    Call<LocatingStationResponse> call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locatingstation);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Building a instance of Google Api Client.
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
    }

    public void onStart() {
        super.onStart();
        // Initiating the GoogleApiClient Connection when the activity is visible.
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        // Disconnecting the GoogleApiClient when the activity goes invisible.
        googleApiClient.disconnect();
    }


    // This callback is invoked when the GoogleApiClient is successfully connected.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // We set a listener to our button only when the ApiClient is connected successfully.
        getCurrentLocation();
    }

    // This callback is invoked when the user grants or rejects the location permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    locationAlertDialog();
                }
                break;
        }
    }

    // If permissions or GPS is disabled, show the user a dialog.
    public void locationAlertDialog() {
        AlertDialog.Builder locationAlertDialog = new AlertDialog.Builder(this);
        locationAlertDialog.setMessage("Please ensure Location permissions are enabled and GPS is switched on");
        locationAlertDialog.setCancelable(true);
        locationAlertDialog.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                });
        AlertDialog alert = locationAlertDialog.create();
        alert.show();
    }

    // Checks if permissions are granted and then obtains the users coordinates.
    private void getCurrentLocation() {
        // Checking if the location permission is granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_REQUEST);
        }

        // Fetching location using FusedLOcationProviderAPI.
        FusedLocationProviderApi fusedLocationApi = LocationServices.FusedLocationApi;
        Location location = fusedLocationApi.getLastLocation(googleApiClient);

        // In some rare cases Location obtained can be null.
        if (location != null) {
            checkIfAtFuelStation(location.getLatitude(), location.getLongitude());
            Log.d("Location co-ord are ", location.getLatitude() + "," + location.getLongitude());
        }

    }

    // Callback invoked if the GoogleApiClient connection is suspended.
    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection was suspended", Toast.LENGTH_SHORT);
        Log.d("test", "Connection was suspended");
    }

    // Callback invoked if the GoogleApiClient connection fails.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT);
        Log.d("test", "Connection failed");
    }

    // Checks if the user is at a Fuelstation and launches the next activity if so.
    public void checkIfAtFuelStation(double latitude, double longitude) {
        call = service.checkIfAtFuelStation(latitude, longitude);
        call.enqueue(new Callback<LocatingStationResponse>() {

            @Override
            public void onResponse(Call<LocatingStationResponse> call, Response<LocatingStationResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body().getFuelStationId());
                    Log.d(TAG, "onResponse: " + response.body().getNumberOfPumps());

                    // If a station is found launch the next PumpNumberActivity.
                    if (response.body().getFuelStationId() != 0 && response.body().getNumberOfPumps() != 0) {
                        Intent intent = new Intent(getBaseContext(), PumpNumberActivity.class);
                        intent.putExtra("FUEL_STATION_ID", response.body().getFuelStationId());
                        intent.putExtra("NUMBER_OF_PUMPS", response.body().getNumberOfPumps());
                        startActivity(intent);
                    }
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(LocatingStationActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LocatingStationResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
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

    // Go back to the Home Activity when the back button is pressed.
    @Override
    public void onBackPressed() {
        startActivity(new Intent(LocatingStationActivity.this, HomeActivity.class));
        finish();
    }
}


