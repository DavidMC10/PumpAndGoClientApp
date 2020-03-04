package com.pumpandgo;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
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

    private static final String TAG = "PaymentMethodListAdapter";

    // Declaration variables.
    Call call;
    ApiService service;
    TokenManager tokenManager;
    List<PaymentMethod> paymentMethodList;
    Context context;
    int resource;
    String defaultPaymentMethod;

    // Constructor initializing the values.
    public PaymentMethodListAdapter(Context context, int resource, List<PaymentMethod> paymentMethodList, String defaultPaymentMethod) {
        super(context, resource, paymentMethodList);
        this.context = context;
        this.resource = resource;
        this.paymentMethodList = paymentMethodList;
        this.defaultPaymentMethod = defaultPaymentMethod;
    }

    // This will return the ListView Item as a View.
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // We need to get the view of the xml for our list item.
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Getting the view.
        View view = layoutInflater.inflate(resource, null, false);

        // Getting the view elements of the list from the view.
        ImageView imageViewDefault = view.findViewById(R.id.imageViewDefault);
        TextView textViewBrand = view.findViewById(R.id.textViewBrand);
        TextView textViewLast4 = view.findViewById(R.id.textViewLast4);
        TextView textViewEditCard = view.findViewById(R.id.textViewEditCard);

        // Getting the payment method of the specified position.
        PaymentMethod paymentMethod = paymentMethodList.get(position);

        // If default payment then set the text colour to blue.
        if (paymentMethodList != null) {
            if (defaultPaymentMethod.equals(paymentMethod.getCardId())) {
                textViewBrand.setTextColor(Color.BLUE);
                textViewLast4.setTextColor(Color.BLUE);
                imageViewDefault.setVisibility(View.VISIBLE);
            }
        }

        // Adding values to the list item.
        textViewBrand.setText(paymentMethod.getBrand());
        textViewLast4.setText(paymentMethod.getLast4());

        // Calls the updateDefaultPaymentMethod function on click.
        textViewLast4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDefaultPaymentMethod(paymentMethod.getCardId());
            }
        });

        // Adding a click listener to the button to update an item on the list.
        textViewEditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentMethod.getBrand().equals("Fuelcard")) {
                    updateFuelCardDialog();
                } else {
                    updateStripeCardDialog(paymentMethod.getCardId());
                }
            }
        });

        // Finally returning the view.
        return view;
    }

    // Prompts the user to update their Fuel Card.
    public void updateFuelCardDialog() {
        // Create layout inflater.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_editfuelcard, null);

        // Create the AlertDialog and set attributes.
        AlertDialog updateFuelCardDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Update Payment Method:")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Remove Card", null)
                .create();

        // Get EditText fields.
        EditText editTextExpMonth = (EditText) view.findViewById(R.id.editTextExpMonth);
        EditText editTextExpYear = (EditText) view.findViewById(R.id.editTextExpYear);

        // Set validation.
        AwesomeValidation validator = new AwesomeValidation(ValidationStyle.BASIC);
        validator.addValidation(editTextExpMonth, "0[1-9]|1[0-2]", "Invalid Month.");
        validator.addValidation(editTextExpYear, "^\\d{2}$", "Invalid Year.");

        updateFuelCardDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // If fields are validated send the user a prompt if they want to update their card.
                        if (validator.validate()) {
                            // Close the updateFuelCardDialog.
                            updateFuelCardDialog.dismiss();

                            // Create a new confirmation dialog builder.
                            AlertDialog.Builder updateFuelCardConfirmationDialog = new AlertDialog.Builder(context);

                            // Set the title of the dialog.
                            updateFuelCardConfirmationDialog.setTitle("Are you sure you want to update this card?");

                            // If the response is yes then update the user's card.
                            updateFuelCardConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updateFuelCard(editTextExpMonth.getText().toString(), editTextExpYear.getText().toString());
                                }
                            });

                            // If response is no then do nothing.
                            updateFuelCardConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                            // Creating and displaying the updateFuelCardConfirmationDialog.
                            AlertDialog alertDialog = updateFuelCardConfirmationDialog.create();
                            alertDialog.show();

                            // Setting the dialog button colours.
                            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(Color.BLACK);
                            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        }
                    }
                });

                // Negative button do nothing.
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setTextColor(Color.BLACK);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFuelCardDialog.dismiss();
                    }
                });

                // Delete fuel card.
                Button neutralButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralButton.setTextColor(Color.RED);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Close the updateFuelCardDialog.
                        updateFuelCardDialog.dismiss();

                        // Create a new confirmation dialog builder.
                        AlertDialog.Builder deleteFuelCardConfirmationDialog = new AlertDialog.Builder(context);

                        // Set the title of the dialog.
                        deleteFuelCardConfirmationDialog.setTitle("Are you sure you want to delete this card?");

                        // If the response is yes then delete the user's card.
                        deleteFuelCardConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteFuelCard();
                            }
                        });

                        // If response is no then do nothing.
                        deleteFuelCardConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        // Creating and displaying the deleteFuelCardConfirmationDialog.
                        AlertDialog alertDialog = deleteFuelCardConfirmationDialog.create();
                        alertDialog.show();

                        // Setting the dialog button colours.
                        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(Color.BLACK);
                        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    }
                });
            }
        });

        // Displays the updateFuelCardDialog.
        updateFuelCardDialog.show();
    }

    // Prompts the user to update their Stripe Card.
    public void updateStripeCardDialog(String cardId) {
        // Create layout inflater.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_editstripecard, null);

        // Create the AlertDialog and set attributes.
        AlertDialog updateStripeCardDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Update Payment Method:")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Remove Card", null)
                .create();

        // Get EditText fields.
        EditText editTextExpMonth = (EditText) view.findViewById(R.id.editTextExpMonth);
        EditText editTextExpYear = (EditText) view.findViewById(R.id.editTextExpYear);

        // Set validation.
        AwesomeValidation validator = new AwesomeValidation(ValidationStyle.BASIC);
        validator.addValidation(editTextExpMonth, "0[1-9]|1[0-2]", "Invalid Month.");
        validator.addValidation(editTextExpYear, "^\\d{2}$", "Invalid Year.");

        updateStripeCardDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // If fields are validated send the user a prompt if they want to update their email.
                        if (validator.validate()) {
                            // Close the updateStripeCardDialog.
                            updateStripeCardDialog.dismiss();

                            // Create a new confirmation dialog builder.
                            AlertDialog.Builder updateStripeCardConfirmationDialog = new AlertDialog.Builder(context);

                            // Set the title of the dialog.
                            updateStripeCardConfirmationDialog.setTitle("Are you sure you want to update this card?");

                            // If the response is yes then update the user's card.
                            updateStripeCardConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updateStripeCard(cardId, editTextExpMonth.getText().toString(), editTextExpYear.getText().toString());
                                }
                            });

                            // If response is no then do nothing.
                            updateStripeCardConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                            // Creating and displaying the updateStripeCardConfirmationDialog.
                            AlertDialog alertDialog = updateStripeCardConfirmationDialog.create();
                            alertDialog.show();

                            // Setting the dialog button colours.
                            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(Color.BLACK);
                            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        }
                    }
                });

                // Negative button do nothing.
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setTextColor(Color.BLACK);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateStripeCardDialog.dismiss();
                    }
                });

                Button neutralButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralButton.setTextColor(Color.RED);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateStripeCardDialog.dismiss();
                        // Close the updateStripeCardDialog.
                        updateStripeCardDialog.dismiss();

                        // Create a new confirmation dialog builder.
                        AlertDialog.Builder deleteStripeCardConfirmationDialog = new AlertDialog.Builder(context);

                        // Set the title of the dialog.
                        deleteStripeCardConfirmationDialog.setTitle("Are you sure you want to delete this card?");

                        // If the response is yes then delete user's card.
                        deleteStripeCardConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteStripeCard(cardId);
                            }
                        });

                        // If response is no then do nothing.
                        deleteStripeCardConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        // Creating and displaying the deleteStripeCardConfirmationDialog.
                        AlertDialog alertDialog = deleteStripeCardConfirmationDialog.create();
                        alertDialog.show();

                        // Setting the dialog button colours.
                        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(Color.BLACK);
                        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    }
                });
            }
        });

        // Displays the updateStripeCardDialog.
        updateStripeCardDialog.show();
    }

    // Updates the user's default payment method.
    public void updateDefaultPaymentMethod(String cardId) {
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.setDefaultPaymentMethod(cardId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    ((Activity) context).overridePendingTransition(0, 0);
                    context.startActivity(new Intent(context.getApplicationContext(), PaymentMethodActivity.class));
                    ((Activity) context).overridePendingTransition(0, 0);
                    ((Activity) context).finish();
                } else {
                    tokenManager.deleteToken();
                    context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Updates the user's Stripe Card.
    public void updateStripeCard(String cardId, String expMonth, String expYear) {
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.updateStripeCard(cardId, expMonth, expYear);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    ((Activity) context).overridePendingTransition(0, 0);
                    context.startActivity(new Intent(context.getApplicationContext(), PaymentMethodActivity.class));
                    ((Activity) context).overridePendingTransition(0, 0);
                    ((Activity) context).finish();
                } else {
                    tokenManager.deleteToken();
                    context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Updates the user's Fuel Card.
    public void updateFuelCard(String expMonth, String expYear) {
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.updateFuelCard(expMonth, expYear);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    ((Activity) context).overridePendingTransition(0, 0);
                    context.startActivity(new Intent(context.getApplicationContext(), PaymentMethodActivity.class));
                    ((Activity) context).overridePendingTransition(0, 0);
                    ((Activity) context).finish();
                } else {
                    tokenManager.deleteToken();
                    context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Deletes the user's Stripe Card.
    public void deleteStripeCard(String cardId) {
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.deleteStripeCard(cardId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    ((Activity) context).overridePendingTransition(0, 0);
                    context.startActivity(new Intent(context.getApplicationContext(), PaymentMethodActivity.class));
                    ((Activity) context).overridePendingTransition(0, 0);
                    ((Activity) context).finish();
                } else {
                    tokenManager.deleteToken();
                    context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Deletes the user's Fuel Card.
    public void deleteFuelCard() {
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.deleteFuelCard();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    ((Activity) context).overridePendingTransition(0, 0);
                    context.startActivity(new Intent(context.getApplicationContext(), PaymentMethodActivity.class));
                    ((Activity) context).overridePendingTransition(0, 0);
                    ((Activity) context).finish();
                } else {
                    tokenManager.deleteToken();
                    context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
