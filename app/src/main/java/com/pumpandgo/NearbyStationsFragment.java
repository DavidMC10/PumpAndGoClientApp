package com.pumpandgo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pumpandgo.entities.FuelStation;
import com.pumpandgo.entities.FuelStationResponse;
import com.pumpandgo.entities.UserDetailsResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by David McElhinney on 14/03/2020.
 */

public class NearbyStationsFragment extends Fragment {
    private static final String TAG = "NearbyStationsFragment";

    // Declare layout fields.
    private RelativeLayout nearbyStationsRootLayout;
    private RecyclerView fuelStationRecyclerView;
    private TextView emptyFuelStations;
    private TextView invalidPermissions;
    private ProgressBar loader;

    // Initialise variables.
    int PERMISSION_ID = 44;
    double latitude;
    double longitude;

    // Initialise objects.
    FusedLocationProviderClient mFusedLocationClient;
    ApiService service;
    TokenManager tokenManager;
    Call call;
    List<FuelStation> fuelStationList;
    RecyclerView recyclerView;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nearbystations, container, false);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        // If no token go to the Login Activity.
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

        // Find the toolbar view inside the activity layout.
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText("Nearby Fuel Stations");

        // View bindings.
        fuelStationRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        nearbyStationsRootLayout = (RelativeLayout) view.findViewById(R.id.nearbyStationsRootLayout);
        loader = (ProgressBar) view.findViewById(R.id.progressBar);

        // Create Textview programatically.
        emptyFuelStations = new TextView(getContext());
        emptyFuelStations.setText("There are no fuel stations nearby.");
        emptyFuelStations.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        emptyFuelStations.setTextSize(20);
        emptyFuelStations.setTextColor(Color.BLACK);
        emptyFuelStations.setGravity(Gravity.CENTER);
        emptyFuelStations.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT));
        nearbyStationsRootLayout.addView(emptyFuelStations);
        emptyFuelStations.setVisibility(View.INVISIBLE);

        // Create Textview programatically.
        invalidPermissions = new TextView(getContext());
        invalidPermissions.setText("Please ensure Location permissions are enabled and GPS is switched on.");
        invalidPermissions.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        invalidPermissions.setTextSize(20);
        invalidPermissions.setTextColor(Color.BLACK);
        invalidPermissions.setGravity(Gravity.CENTER);
        invalidPermissions.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT));
        nearbyStationsRootLayout.addView(invalidPermissions);
        invalidPermissions.setVisibility(View.INVISIBLE);
        fuelStationRecyclerView.setVisibility(View.VISIBLE);

        // Make Api call.
        getLastLocation();
        return view;
    }

    // Gets the user's profile details.
    public void getUserProfileDetails() {
        call = service.getUserProfileDetails();
        call.enqueue(new Callback<UserDetailsResponse>() {

            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        int maxDistanceLimit = response.body().getMaxDistanceLimit();
                        getNearbyFuelStations(maxDistanceLimit);
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
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Get fuel stations nearby.
    public void getNearbyFuelStations(int maxDistanceLimit) {
        loader.setVisibility(View.VISIBLE);
        nearbyStationsRootLayout.setVisibility(View.INVISIBLE);
        call = service.getNearbyStations(latitude, longitude, maxDistanceLimit);
        call.enqueue(new Callback<FuelStationResponse>() {
            @Override
            public void onResponse(Call<FuelStationResponse> call, Response<FuelStationResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        loader.setVisibility(View.INVISIBLE);
                        fuelStationRecyclerView.setVisibility(View.VISIBLE);
                        nearbyStationsRootLayout.setVisibility(View.VISIBLE);
                        emptyFuelStations.setVisibility(View.INVISIBLE);

                        fuelStationList = response.body().getData();

                        // Set Recycler View.
                        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                        // Creating recyclerview adapter.
                        FuelStationListAdapter adapter = new FuelStationListAdapter(getContext(), fuelStationList);

                        // Setting adapter to recyclerview.
                        recyclerView.setAdapter(adapter);
                    }
                } else if (response.code() == 404) {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        loader.setVisibility(View.INVISIBLE);
                        nearbyStationsRootLayout.setVisibility(View.VISIBLE);
                        fuelStationRecyclerView.setVisibility(View.INVISIBLE);
                        emptyFuelStations.setVisibility(View.VISIBLE);
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
            public void onFailure(Call<FuelStationResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

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
                                    getUserProfileDetails();
                                    invalidPermissions.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                invalidPermissions.setVisibility(View.VISIBLE);
            }
        } else {
            requestPermissions();
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