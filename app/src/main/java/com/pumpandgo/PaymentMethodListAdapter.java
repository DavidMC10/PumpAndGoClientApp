package com.pumpandgo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.pumpandgo.entities.PaymentMethod;

import java.util.List;

/**
 * Created by David McElhinney on 29/02/2020.
 */

public class PaymentMethodListAdapter extends ArrayAdapter<PaymentMethod> {

    // The list values in the List of type Payment Method.
    List<PaymentMethod> paymentMethodList;

    // Activity context.
    Context context;

    // The layout resource file for the list items.
    int resource;

    // Constructor initializing the values.
    public PaymentMethodListAdapter(Context context, int resource, List<PaymentMethod> paymentMethodList) {
        super(context, resource, paymentMethodList);
        this.context = context;
        this.resource = resource;
        this.paymentMethodList = paymentMethodList;
    }

    // This will return the ListView Item as a View.
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // We need to get the view of the xml for our list item.
        // And for this we need a layoutinflater.
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Getting the view.
        View view = layoutInflater.inflate(resource, null, false);

        // Getting the view elements of the list from the view.
        TextView textViewBrand = view.findViewById(R.id.textViewBrand);
        TextView textViewLast4 = view.findViewById(R.id.textViewLast4);
        TextView textViewEditCard = view.findViewById(R.id.textViewEditCard);


        // Getting the payment method of the specified position.
        PaymentMethod paymentMethod = paymentMethodList.get(position);

        Log.d("yessss",String.valueOf(position));

        // Adding values to the list item.
        textViewBrand.setText(paymentMethod.getBrand());
        textViewLast4.setText(paymentMethod.getLast4());

        // Adding a click listener to the button to remove item from the list.
        textViewEditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // We will call this method to remove the selected value from the list.
                // We are passing the position which is to be removed in the method.
                removeHero(position);
            }
        });

        // Finally returning the view.
        return view;
    }

    //this method will remove the item from the list
    private void removeHero(final int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Write your message here.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
