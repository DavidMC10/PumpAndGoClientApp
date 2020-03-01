package com.pumpandgo;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.pumpandgo.entities.Setting;

import java.util.List;

/**
 * Created by Belal on 9/14/2017.
 */

//we need to extend the ArrayAdapter class as we are building an adapter
public class SettingsListAdapter extends ArrayAdapter<Setting> {

    //the list values in the List of type hero
    List<Setting> settingsList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public SettingsListAdapter(Context context, int resource, List<Setting> settingsList) {
        super(context, resource, settingsList);
        this.context = context;
        this.resource = resource;
        this.settingsList = settingsList;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        ImageView imageViewIcon = view.findViewById(R.id.imageViewSettingIcon);
        TextView textViewTitle = view.findViewById(R.id.textViewSettingTitle);
        TextView textViewUserData = view.findViewById(R.id.textViewUserData);
        TextView textViewEdit = view.findViewById(R.id.textViewEdit);

        //getting the hero of the specified position
        Setting setting = settingsList.get(position);

        //adding values to the list item
        imageViewIcon.setImageDrawable(context.getResources().getDrawable(setting.getIcon()));
        Log.d("title:::", setting.getTitle());
        textViewTitle.setText(setting.getTitle());

        textViewUserData.setText(setting.getUserData());

        //adding a click listener to the button to remove item from the list
        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we will call this method to remove the selected value from the list
                //we are passing the position which is to be removed in the method
                removeHero(position);
            }
        });

        //finally returning the view
        return view;
    }

    //this method will remove the item from the list
    private void removeHero(final int position) {
        //Creating an alert dialog to confirm the deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this?");

        //if the response is positive in the alert
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //removing the item
//                heroList.remove(position);

                //reloading the list
//                notifyDataSetChanged();
            }
        });

        //if response is negative nothing is being done
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //creating and displaying the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
