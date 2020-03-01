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

public class FuelStationListAdapter extends RecyclerView.Adapter<FuelStationListAdapter.fuelStationViewHolder> {

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

        // Gets the fuelstation of the specified position.
        FuelStation fuelStation = fuelStationList.get(position);

        // Binds the data to the viewholder views.
        holder.textViewName.setText(fuelStation.getFuelStationName());
        holder.textViewAddress1.setText(fuelStation.getAddress1());
        holder.textViewAddress2.setText(fuelStation.getAddress2());
        holder.textViewCityTown.setText(fuelStation.getCityTown());

        // Button that takes the user to Google Maps to get directions to the fuel station.
        holder.directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:" + fuelStation.getLatitude()+ "," + fuelStation.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
    }


    // Returns the size of the list.
    @Override
    public int getItemCount() {
        return fuelStationList.size();
    }


    class fuelStationViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewAddress1, textViewAddress2, textViewCityTown;
        Button directionButton;

        public fuelStationViewHolder(View fuelStationView) {
            super(fuelStationView);

            textViewName = fuelStationView.findViewById(R.id.textViewName);
            textViewAddress1 = fuelStationView.findViewById(R.id.textViewAddress1);
            textViewAddress2 = fuelStationView.findViewById(R.id.textViewAddress2);
            textViewCityTown = fuelStationView.findViewById(R.id.textViewCityTown);
            directionButton = fuelStationView.findViewById(R.id.buttonDirections);
        }
    }
}