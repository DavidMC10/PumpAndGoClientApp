package com.pumpandgo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.pumpandgo.entities.DeleteFuelCardResponse;
import com.pumpandgo.entities.PaymentMethod;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by David McElhinney on 29/02/2020.
 */

public class PaymentMethodListAdapter extends ArrayAdapter<PaymentMethod> {

    ApiService service;
    TokenManager tokenManager;
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
                updatePaymentMethod(position);
            }
        });

        // Finally returning the view.
        return view;
    }

    //this method will remove the item from the list
    private void updatePaymentMethod(final int position) {
        AlertDialog.Builder editCardDialog = new AlertDialog.Builder(context);
        AwesomeValidation validator = new AwesomeValidation(ValidationStyle.BASIC);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_editpayment, null);
        editCardDialog.setView(view);
        //removing the item

        PaymentMethod paymentMethod = paymentMethodList.get(position);
        EditText test = (EditText) view.findViewById(R.id.editTextCardNumber);
        TextView dialogTitle = (TextView) view.findViewById(R.id.textViewDialogTitle);
        dialogTitle.setText("Update card " + paymentMethod.getLast4());
        validator.addValidation(test, RegexTemplate.NOT_EMPTY, "Error");

        test.setText(paymentMethod.getLast4());
        //if the response is positive in the alert
        TextView cancelButton = (TextView) view.findViewById(R.id.textViewCancel);
// Register the onClick listener with the implementation above
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (validator.validate()) {
                    test.setText("prick");
                }
//                test.setText("bastard");
                //DO SOMETHING! {RUN SOME FUNCTION ... DO CHECKS... ETC}
            }
        });

        TextView removeCardButton = (TextView) view.findViewById(R.id.textViewRemoveCard);
        removeCardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder removeCardDialog = new AlertDialog.Builder(context);

                removeCardDialog.setTitle("Are you sure you want to delete this payment method?");

                //if the response is positive in the alert
                removeCardDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (paymentMethod.getBrand().equals("Fuelcard")) {
                            deleteFuelCard();
                            Log.d("Ballvbag", "ballbag");
                        } else {
                            Log.d("Ballvbag", paymentMethod.getBrand());
                        }
                    }
                });

                //if response is negative nothing is being done
                removeCardDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                //creating and displaying the alert dialog
                AlertDialog alertDialog = removeCardDialog.create();
                alertDialog.show();
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(Color.RED);
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(Color.GREEN);
            }
        });

        TextView updateCardButton = (TextView) view.findViewById(R.id.textViewUpdateCard);
        updateCardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                test.setText("bastard");
                //DO SOMETHING! {RUN SOME FUNCTION ... DO CHECKS... ETC}
            }
        });
        editCardDialog.show();
    }

    public void deleteFuelCard() {
        Call<DeleteFuelCardResponse> call;
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.deleteFuelCard();
        call.enqueue(new Callback<DeleteFuelCardResponse>() {

            @Override
            public void onResponse(Call<DeleteFuelCardResponse> call, Response<DeleteFuelCardResponse> response) {
//                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    context.startActivity(new Intent(context.getApplicationContext(), PaymentMethodActivity.class));
                } else {
                    tokenManager.deleteToken();
                    context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call<DeleteFuelCardResponse> call, Throwable t) {
//                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
