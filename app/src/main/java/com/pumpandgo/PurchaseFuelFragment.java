package com.pumpandgo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pumpandgo.entities.DefaultPaymentMethodResponse;
import com.pumpandgo.entities.LocatingStationResponse;
import com.pumpandgo.entities.VisitCountResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Author of the original code: Ferdousur Rahman Sarker
 * Author of the refactored code: David McElhinney
 * Date: 14/03/2020
 * Note: The code to obtain the location is from Ferdousur Rahman Sarker. Minor refactoring was required to enable it to function in my project.
 * The code in this class that does not have anything to do with obtaining a location is my own code.
 * Availability: https://www.androdocs.com/java/getting-current-location-latitude-longitude-in-android-using-java.html
 */

public class PurchaseFuelFragment extends Fragment {
    private static final String TAG = "PurchaseFuelFragment";

    // Declare layout fields.
    private RelativeLayout purchaseFuelRootLayout;
    private LinearLayout purchaseFuelContainer;
    private TextView invalidPermissions;
    private TextView textViewTitle;
    private TextView textViewAtFuelStation;
    private TextView textViewFirstName;
    private TextView textViewVisitCount;
    private TextView textViewEndingText;
    private ImageView imageViewBuyFuel;
    private ProgressBar loader;

    // Initialise variables.
    int PERMISSION_ID = 44;
    int fuelStationId = 0;
    String fuelStationName;
    int numberOfPumps;
    int channelId = 0;
    double latitude;
    double longitude;
    String defaultPaymentMethod;

    // Initialise objects.
    FusedLocationProviderClient mFusedLocationClient;
    ApiService service;
    TokenManager tokenManager;
    Call call;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_purchasefuel, container, false);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        ButterKnife.bind(this, view);

        // If no token go to the Login Activity.
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

        // View binding.
        purchaseFuelRootLayout = (RelativeLayout) view.findViewById(R.id.purchaseFuelRootLayout);
        purchaseFuelContainer = (LinearLayout) view.findViewById(R.id.purchaseFuelContainer);
        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewAtFuelStation = (TextView) view.findViewById(R.id.textViewAtFuelStation);
        textViewFirstName = (TextView) view.findViewById(R.id.textViewUserFirstName);
        textViewVisitCount = (TextView) view.findViewById(R.id.textViewVisitCount);
        textViewEndingText = (TextView) view.findViewById(R.id.textViewEndingText);
        imageViewBuyFuel = (ImageView) view.findViewById(R.id.imageViewBuyFuel);
        loader = (ProgressBar) view.findViewById(R.id.progressBar);

        // Create TextView programatically.
        invalidPermissions = new TextView(getContext());
        invalidPermissions.setText("Please ensure Location permissions are enabled and GPS is switched on.");
        invalidPermissions.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        invalidPermissions.setTextSize(20);
        invalidPermissions.setTextColor(Color.BLACK);
        invalidPermissions.setGravity(Gravity.CENTER);
        invalidPermissions.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT));
        purchaseFuelRootLayout.addView(invalidPermissions);

        // Set views visibility.
        invalidPermissions.setVisibility(View.INVISIBLE);
        purchaseFuelRootLayout.setVisibility(View.INVISIBLE);
        purchaseFuelContainer.setVisibility(View.INVISIBLE);

        // Make Api call.
        getLastLocation();
        return view;
    }

    // Checks if the user is at a Fuelstation.
    public void checkIfAtFuelStation(double latitude, double longitude) {
        loader.setVisibility(View.VISIBLE);
        purchaseFuelContainer.setVisibility(View.INVISIBLE);
        call = service.checkIfAtFuelStation(latitude, longitude);
        call.enqueue(new Callback<LocatingStationResponse>() {

            @Override
            public void onResponse(Call<LocatingStationResponse> call, Response<LocatingStationResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        channelId = response.body().getChannelId();
                        fuelStationId = response.body().getFuelStationId();
                        fuelStationName = response.body().getFuelStationName();
                        numberOfPumps = response.body().getNumberOfPumps();
                        textViewAtFuelStation.setText("You are are at " + response.body().getFuelStationName());
                        imageViewBuyFuel.setImageResource(R.drawable.start_payment_image);
                        getVisitCount();
                    }
                } else if (response.code() == 404) {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        textViewAtFuelStation.setText("You are are not currently at a fuel station");
                        imageViewBuyFuel.setImageResource(R.drawable.locate_station_image);
                        getVisitCount();
                    }
                } else {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<LocatingStationResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Get the user's visit count.
    public void getVisitCount() {
        call = service.visitCount();
        call.enqueue(new Callback<VisitCountResponse>() {

            @Override
            public void onResponse(Call<VisitCountResponse> call, Response<VisitCountResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        textViewTitle.setText("Good Afternoon");
                        textViewFirstName.setText(response.body().getFirstName());
                        if (response.body().getVisitCount() == 0) {
                            textViewVisitCount.setText("Congratulations you have unlocked 15% fuel discount on this fuel purchase.");
                            textViewEndingText.setVisibility(View.INVISIBLE);
                        } else {
                            textViewVisitCount.setText(String.valueOf(response.body().getVisitCount()) + " visits to go");
                            textViewEndingText.setVisibility(View.VISIBLE);
                        }
                        textViewEndingText.setText("to unlock your fuel reward");
                        getDefaultPaymentMethod();
                    }
                } else {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<VisitCountResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Gets the user's default payment method.
    public void getDefaultPaymentMethod() {
        call = service.getDefaultPaymentMethod();
        call.enqueue(new Callback<DefaultPaymentMethodResponse>() {
            @Override
            public void onResponse(Call<DefaultPaymentMethodResponse> call, Response<DefaultPaymentMethodResponse> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        defaultPaymentMethod = response.body().getCardId();
                        loader.setVisibility(View.INVISIBLE);
                        purchaseFuelRootLayout.setVisibility(View.VISIBLE);
                        purchaseFuelContainer.setVisibility(View.VISIBLE);
                    }
                } else if (response.code() == 404) {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        loader.setVisibility(View.INVISIBLE);
                        purchaseFuelRootLayout.setVisibility(View.VISIBLE);
                        purchaseFuelContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<DefaultPaymentMethodResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Loads the PumpNumber Activity.
    @OnClick(R.id.imageViewBuyFuel)
    public void goToPumpNumberActivity() {
        // If no fuel station has been located then load the NearbyStationsFragment.
        if (fuelStationId != 0) {
            // If there is no default payment method then display an error message.
            if (TextUtils.isEmpty(defaultPaymentMethod)) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Error:");
                alertDialog.setMessage("A payment method must be added to continue.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                // Change button colour.
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            } else {
                // Load the PumpNumberActivity if the user is at a fuel station.
                Intent intent = new Intent(getContext(), PumpNumberActivity.class);

                // Pass data to the PumpNumberActivity.
                intent.putExtra("FUEL_STATION_ID", fuelStationId);
                intent.putExtra("FUEL_STATION_NAME", fuelStationName);
                intent.putExtra("NUMBER_OF_PUMPS", numberOfPumps);
                intent.putExtra("CHANNEL_ID", channelId);
                getContext().startActivity(intent);
            }
        } else {
            getActivity().findViewById(R.id.navigationNearbyStations).performClick();
        }

    }

    // Get the user's last location.
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    checkIfAtFuelStation(latitude, longitude);
                                    invalidPermissions.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                purchaseFuelRootLayout.setVisibility(View.VISIBLE);
                invalidPermissions.setVisibility(View.VISIBLE);
            }
        } else {
            requestPermissions();
            purchaseFuelRootLayout.setVisibility(View.VISIBLE);
            invalidPermissions.setVisibility(View.VISIBLE);
        }
    }

    // Request new location data for the user.
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    // Get the location data.
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    };

    // Check if user permissions are enabled.
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    // Request location permissions.
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    // Check if location is enabled.
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    // If the permission result is granted get the user's last location.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}
