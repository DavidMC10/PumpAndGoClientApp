package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PumpActivity extends AppCompatActivity {

    private static final String TAG = "PumpActivity";
    ApiService service;
    TokenManager tokenManager;
    Call<Void> call;
    private TextView testing;
    private int fuelStationId;
    private String fuelStationName;
    private int pumpNumber;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pumping);
        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        fuelStationId = getIntent().getIntExtra("FUEL_STATION_ID", 0);
        fuelStationName = getIntent().getStringExtra("FUEL_STATION_NAME");
        pumpNumber = getIntent().getIntExtra("PUMP_NUMBER", 0);

        // View binding.
        testing = (TextView) findViewById(R.id.titleLayout);

        // Find the toolbar view inside the activity layout.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText(fuelStationName);

        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("25ce4a082b0080ec345e", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());

                Log.d("Current State", change.getCurrentState().toString());
                Log.d("Previous State", change.getPreviousState().toString());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );

                Log.d("error", message);
            }
        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe("my-channel");

        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                String yes = event.getData();
                updateText(yes);
                System.out.println("Received event with data: " + event.getData().toString());
                Log.d("event", event.getData().toString());
            }
        });

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI

            }
        });
        startPumping();
    }

    public void updateText(String yes) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                testing.setText(yes);
            }
        });

    }


    // Gets a receipt for the user's transaction.
    public void startPumping() {

        call = service.createTransaction();
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {

                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(PumpActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing.
    }
}
