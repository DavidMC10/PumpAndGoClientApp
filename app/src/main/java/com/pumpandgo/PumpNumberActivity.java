package com.pumpandgo;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by David McElhinney on 14/03/2020.
 */

public class PumpNumberActivity extends AppCompatActivity {
    private static final String TAG = "PumpNumberActivity";

    // Declare layout fields.
    private NumberPicker pumpNumberPicker;
    private Button buttonContinue;

    // Initialise variables.
    int fuelStationId = 0;
    String fuelStationName;
    int numberOfPumps;
    int pumpNumber;
    int channelId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pumpnumber);
        ButterKnife.bind(this);

        // Get data from the previous activity.
        fuelStationId = getIntent().getIntExtra("FUEL_STATION_ID",0);
        fuelStationName = getIntent().getStringExtra("FUEL_STATION_NAME");
        numberOfPumps = getIntent().getIntExtra("NUMBER_OF_PUMPS", 0);
        channelId = getIntent().getIntExtra("CHANNEL_ID",0);

        // View bindings.
        pumpNumberPicker = (NumberPicker) findViewById(R.id.pumpNumberNumberPicker);
        pumpNumberPicker.setMinValue(1);
        pumpNumberPicker.setMaxValue(numberOfPumps);
        buttonContinue = (Button) findViewById(R.id.buttonContinue);

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
    }

    // Allows the user to go to the fuel amount activity.
    @OnClick(R.id.buttonContinue)
    public void goToFuelAmountActivity() {
        Intent intent = new Intent(this, FuelAmountActivity.class);
        pumpNumber = pumpNumberPicker.getValue();
        // Send data to the FuelAmountActivity.
        intent.putExtra("FUEL_STATION_ID", fuelStationId);
        intent.putExtra("FUEL_STATION_NAME", fuelStationName);
        intent.putExtra("PUMP_NUMBER", pumpNumber);
        intent.putExtra("CHANNEL_ID", channelId);
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

    // Go back to the Home Activity when the back button is pressed.
    @Override
    public void onBackPressed() {
        startActivity(new Intent(PumpNumberActivity.this, HomeActivity.class));
        finish();
    }
}
