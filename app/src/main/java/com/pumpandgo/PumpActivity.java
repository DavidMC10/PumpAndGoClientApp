package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
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

/**
 * Created by David McElhinney on 14/03/2020.
 */

public class PumpActivity extends AppCompatActivity {
    private static final String TAG = "PumpActivity";

    // Declare layout fields.
    private LinearLayout pumpingRootLayout;
    private TextView textViewPumpNumber;
    private TextView textViewFuelAmount;
    private ProgressBar loader;

    // Initialise variables.
    private int fuelStationId;
    private String fuelStationName;
    private int fuelAmount;
    private int pumpNumber;
    private int channelId;

    // Initialise objects.
    ApiService service;
    TokenManager tokenManager;
    Call<Void> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pumping);
        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        // If no token go to the Login Activity.
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Get data from the previous activity.
        fuelStationId = getIntent().getIntExtra("FUEL_STATION_ID", 0);
        fuelStationName = getIntent().getStringExtra("FUEL_STATION_NAME");
        fuelAmount = getIntent().getIntExtra("FUEL_AMOUNT", 0);
        pumpNumber = getIntent().getIntExtra("PUMP_NUMBER", 0);
        channelId = getIntent().getIntExtra("CHANNEL_ID", 0);

        // View bindings.
        textViewFuelAmount = (TextView) findViewById(R.id.textViewFuelAmount);
        textViewPumpNumber = (TextView) findViewById(R.id.textViewPumpNumber);
        textViewPumpNumber.setText(String.valueOf(pumpNumber));

        // Find the toolbar view inside the activity layout.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText(fuelStationName);

        // Set pusher details.
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("25ce4a082b0080ec345e", options);

        // Connect to the websocket.
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);

        // Get the websocket data from the fuel_pump channel.
        Channel channel = pusher.subscribe("channelId." + channelId);
        channel.bind("pumping", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                String pumpData = event.getData();
                pumpData = pumpData.substring(22, pumpData.length() - 23);
                if (pumpData.equals("finished") == false) {
                    updateCurrentFuelAmount("€" + pumpData);
                } else {
                    pusher.unsubscribe("channelId." + channelId);
                    pusher.disconnect();
                    startActivity(new Intent(PumpActivity.this, TransactionCompleteActivity.class));
                    finish();
                }
            }
        });

        // Make the Api call.
        startPumping();
    }

    // Updates the current fuel data.
    public void updateCurrentFuelAmount(String pumpData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update the UI with the pump data.
                textViewFuelAmount.setText(pumpData);
            }
        });
    }

    // Creates the user transaction and starts the fuel pump.
    public void startPumping() {
        call = service.createTransaction(fuelStationId, fuelAmount, pumpNumber);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    // Do nothing
                } else {
                    // Ensure activity is not null.
                    if (getApplicationContext() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(PumpActivity.this, LoginActivity.class));
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

    // Do nothing on back button pressed.
    @Override
    public void onBackPressed() {
        // Do nothing.
    }
}
