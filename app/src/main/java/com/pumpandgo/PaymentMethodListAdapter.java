package com.pumpandgo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.Patterns;
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

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.pumpandgo.entities.PaymentMethod;

import java.util.List;

/**
 * Created by David McElhinney on 29/02/2020.
 */

public class PaymentMethodListAdapter extends ArrayAdapter<PaymentMethod> {

    List<PaymentMethod> paymentMethodList;
    Context context;
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

        Log.d("yessss", String.valueOf(position));

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
        final View customLayout = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_payment, null);
        builder1.setCancelable(true);
        builder1.setView(customLayout);

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(((Activity) context), R.id.editTextCardNumber, Patterns.EMAIL_ADDRESS, R.string.err_email);

        builder1.setPositiveButton("Test",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                });

        AlertDialog dialog = builder1.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAwesomeValidation.validate()) {
                    Log.d("dsdsd", "dfdf");
                }
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (wantToCloseDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }

    public void setupRules() {

    }

}
