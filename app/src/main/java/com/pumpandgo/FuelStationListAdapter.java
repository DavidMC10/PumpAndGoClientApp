package com.pumpandgo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pumpandgo.entities.FuelStation;

import java.util.List;
import java.util.Locale;

/**
 * Created by David McElhinney on 14/03/2020.
 */

public class FuelStationListAdapter extends RecyclerView.Adapter<FuelStationListAdapter.fuelStationViewHolder> {

    // Declaration variables
    private Context context;
    private List<FuelStation> fuelStationList;

    // Constructor
    public FuelStationListAdapter(Context context, List<FuelStation> fuelStationList) {
        this.context = context;
        this.fuelStationList = fuelStationList;
    }

    // Creates a new recycler view.
    @Override
    public fuelStationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_fuelstations, null);
        return new fuelStationViewHolder(view);
    }

    // Binds the data to the view.
    @Override
    public void onBindViewHolder(fuelStationViewHolder holder, int position) {

        // Gets the Fuelstation of the specified position.
        FuelStation fuelStation = fuelStationList.get(position);

        // Binds the data to the viewholder views.
        holder.textViewFuelStationName.setText(fuelStation.getFuelStationName());
        holder.textViewAddress.setText(fuelStation.getAddress1() + ", " + fuelStation.getAddress2() + ", " + fuelStation.getCityTown());
        holder.textViewDistance.setText(fuelStation.getDistance() + "KM");
        holder.textViewOpeningHours.setText(fuelStation.getData().get(0).getOpenTime() + "-" + fuelStation.getData().get(0).getCloseTime());

        // Button that takes the user to Google Maps to get directions to the fuel station.
        holder.directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", fuelStation.getLatitude(), fuelStation.getLongitude(), "");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                context.startActivity(intent);
            }
        });
    }

    // Returns the size of the list.
    @Override
    public int getItemCount() {
        return fuelStationList.size();
    }

    class fuelStationViewHolder extends RecyclerView.ViewHolder {

        TextView textViewFuelStationName, textViewDistance, textViewAddress, textViewOpeningHours;
        Button directionButton;

        public fuelStationViewHolder(View fuelStationView) {
            super(fuelStationView);
            // Set text views.
            textViewFuelStationName = fuelStationView.findViewById(R.id.textViewFuelStationName);
            textViewDistance = fuelStationView.findViewById(R.id.textViewDistance);
            textViewAddress = fuelStationView.findViewById(R.id.textViewAddress);
            textViewOpeningHours = fuelStationView.findViewById(R.id.textViewOpeningHours);
            directionButton = fuelStationView.findViewById(R.id.buttonDirections);
        }
    }
}