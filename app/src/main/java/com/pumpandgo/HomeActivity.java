package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pumpandgo.entities.AccessToken;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import retrofit2.Call;

/**
 * Created by David McElhinney on 14/03/2020.
 */

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActivity";

    // Initialise objects.
    ApiService service;
    TokenManager tokenManager;
    Call<AccessToken> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        // Load the default fragment.
        loadFragment(new PurchaseFuelFragment());

        // Gets the bottom navigation view and attaches the listener.
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(this);

        // If no token go to the Login Activity.
        if (tokenManager.getToken().getAccessToken() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    }

    // Switches to the selected fragment.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigationPurchaseFuel:
                fragment = new PurchaseFuelFragment();
                break;

            case R.id.navigationNearbyStations:
                fragment = new NearbyStationsFragment();
                break;

            case R.id.navigationRewards:
                fragment = new RewardsFragment();
                break;

            case R.id.navigationTransactionHistory:
                fragment = new TransactionHistoryFragment();
                break;

            case R.id.navigationSettings:
                fragment = new SettingsFragment();
                break;
        }
        return loadFragment(fragment);
    }

    // Loads the fragment.
    private boolean loadFragment(Fragment fragment) {
        // Switching fragment.
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
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
