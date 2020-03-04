package com.pumpandgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.pumpandgo.entities.DefaultPaymentMethodResponse;
import com.pumpandgo.entities.PaymentMethod;
import com.pumpandgo.entities.PaymentMethodResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodActivity extends AppCompatActivity {

    private static final String TAG = "PaymentMethodActivity";

    // Declaration variables.
    Call call;
    ApiService service;
    TokenManager tokenManager;
    List<PaymentMethod> paymentMethodList;
    ListView listView;
    String defaultPaymentMethod;

    @BindView(R.id.progressBar)
    ProgressBar loader;
    @BindView(R.id.paymentMethodRootLayout)
    LinearLayout paymentMethodRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentmethod);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        ButterKnife.bind(this);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText("Payment Methods");
        getDefaultPaymentMethod();
    }


    // Gets the user's default payment method.
    public void getDefaultPaymentMethod() {
        call = service.getDefaultPaymentMethod();
        call.enqueue(new Callback<DefaultPaymentMethodResponse>() {
            @Override
            public void onResponse(Call<DefaultPaymentMethodResponse> call, Response<DefaultPaymentMethodResponse> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    defaultPaymentMethod = response.body().getCardId();
                    getPaymentMethods();
                } else if (response.code() == 404) {
                    getPaymentMethods();
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(PaymentMethodActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DefaultPaymentMethodResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Gets a list of the user's payment methods and adds them to the list adapter.
    public void getPaymentMethods() {
        loader.setVisibility(View.VISIBLE);
        paymentMethodRootLayout.setVisibility(View.INVISIBLE);
        call = service.getPaymentMethods();
        call.enqueue(new Callback<PaymentMethodResponse>() {

            @Override
            public void onResponse(Call<PaymentMethodResponse> call, Response<PaymentMethodResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    loader.setVisibility(View.INVISIBLE);
                    paymentMethodRootLayout.setVisibility(View.VISIBLE);
                    paymentMethodList = response.body().getData();

                    // Initializing objects.
                    listView = (ListView) findViewById(R.id.listView);

                    // Creating the adapter.
                    PaymentMethodListAdapter adapter = new PaymentMethodListAdapter(PaymentMethodActivity.this, R.layout.layout_paymentmethod_list, paymentMethodList, defaultPaymentMethod);

                    // Attaching adapter to the listview.
                    listView.setAdapter(adapter);

                } else if (response.code() == 404) {
                    loader.setVisibility(View.INVISIBLE);
                    paymentMethodRootLayout.setVisibility(View.VISIBLE);
                    View linearLayout = findViewById(R.id.paymentMethodRootLayout);
                    TextView emptyPaymentMethods = new TextView(getApplication());
                    emptyPaymentMethods.setText("You don't have any payment methods.");
                    emptyPaymentMethods.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    emptyPaymentMethods.setTextSize(20);
                    emptyPaymentMethods.setTextColor(Color.BLACK);
                    emptyPaymentMethods.setGravity(Gravity.CENTER);
                    emptyPaymentMethods.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT));
                    ((LinearLayout) linearLayout).addView(emptyPaymentMethods);
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(PaymentMethodActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PaymentMethodResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Binds to menu item.
    public void addFuelCardAction(MenuItem mi) {
        addFuelCardDialog();
    }

    // Check if the list contains a fuelcard.
    public Boolean containsFuelCard() {
        if (paymentMethodList != null) {
            for (int i = 0; i < paymentMethodList.size(); i++) {
                if (paymentMethodList.get(i).getBrand().equals("Fuelcard")) {
                    return true;
                }
            }
        }
        return false;
    }

    // Allows the user to add a fuelcard.
    public void addFuelCardDialog() {
        if (!containsFuelCard()) {
            // Create layout inflater.
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_addfuelcard, null);

            // Create the AlertDialog and set attributes.
            AlertDialog addFuelCardDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .setTitle("Add Fuel Card:")
                    .setPositiveButton("Save", null)
                    .setNegativeButton("Cancel", null)
                    .create();

            // Get EditText fields.
            EditText editTextCardNumber = (EditText) view.findViewById(R.id.editTextCardNumber);
            EditText editTextExpMonth = (EditText) view.findViewById(R.id.editTextExpMonth);
            EditText editTextExpYear = (EditText) view.findViewById(R.id.editTextExpYear);

            // Set validation.
            AwesomeValidation validator = new AwesomeValidation(ValidationStyle.BASIC);
            validator.addValidation(editTextCardNumber, "^\\s*([0-9]{16})\\z", "Invalid Fuel Card Number.");
            validator.addValidation(editTextExpMonth, "0[1-9]|1[0-2]", "Invalid Month.");
            validator.addValidation(editTextExpYear, "^\\d{2}$", "Invalid Year.");

            addFuelCardDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // If fields are validated send the user a prompt if they want to add the fuel card.
                            if (validator.validate()) {

                                // Close the addFuelCardDialog.
                                addFuelCardDialog.dismiss();

                                // Create a new confirmation dialog builder.
                                AlertDialog.Builder addFuelCardConfirmationDialog = new AlertDialog.Builder(PaymentMethodActivity.this);

                                // Set the title of the dialog.
                                addFuelCardConfirmationDialog.setTitle("Are you sure you want to add this fuel card?");

                                // If the response is yes then add the fuel card.
                                addFuelCardConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        addFuelCard(editTextCardNumber.getText().toString(), editTextExpMonth.getText().toString(), editTextExpYear.getText().toString());
                                    }
                                });

                                // If response is no then do nothing.
                                addFuelCardConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });

                                // Creating and displaying the addFuelCardConfirmationDialog.
                                AlertDialog alertDialog = addFuelCardConfirmationDialog.create();
                                alertDialog.show();

                                // Setting the dialog button colours.
                                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                negativeButton.setTextColor(Color.BLACK);
                                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }
                    });

                    // Negative button do nothing.
                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setTextColor(Color.BLACK);
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addFuelCardDialog.dismiss();
                        }
                    });
                }
            });

            // Displays the addFuelCardDialog.
            addFuelCardDialog.show();
        } else {
            // Create a new dialog builder.
            AlertDialog.Builder fuelCardDialogAlert = new AlertDialog.Builder(this);

            // Set the title of the dialog.
            fuelCardDialogAlert.setTitle("You can't have more than one fuel card.");

            // Do nothing.
            fuelCardDialogAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            // Creating and displaying the addFuelCardConfirmationDialog.
            AlertDialog alertDialog = fuelCardDialogAlert.create();
            alertDialog.show();

            // Setting the dialog button colours.
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    // Adds the fuelcard to the user's account.
    public void addFuelCard(String fuelCardNo, String expMonth, String expYear) {
        call = service.addFuelCard(fuelCardNo, expMonth, expYear);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(PaymentMethodActivity.this, PaymentMethodActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(PaymentMethodActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Binds to menu item.
    public void addStripeCardAction(MenuItem mi) {
        addStripeCardDialog();
    }

    // Allows the user to add a credit/debit card.
    public void addStripeCardDialog() {
        // Create layout inflater.
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_addstripecard, null);

        // Create the AlertDialog and set attributes.
        AlertDialog addStripeCardDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Add Credit/Debit Card:")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        // Get EditText fields.
        EditText editTextCardNumber = (EditText) view.findViewById(R.id.editTextCardNumber);
        EditText editTextExpMonth = (EditText) view.findViewById(R.id.editTextExpMonth);
        EditText editTextExpYear = (EditText) view.findViewById(R.id.editTextExpYear);
        EditText editTextCvc = (EditText) view.findViewById(R.id.editTextCvc);

        // Set validation.
        AwesomeValidation validator = new AwesomeValidation(ValidationStyle.BASIC);
        validator.addValidation(editTextCardNumber, "^\\s*([0-9]{16})\\z", "Invalid Credit/Debit Card Number.");
        validator.addValidation(editTextExpMonth, "0[1-9]|1[0-2]", "Invalid Month.");
        validator.addValidation(editTextExpYear, "^\\d{2}$", "Invalid Year.");
        validator.addValidation(editTextCvc, "^[0-9]{3}$", "Invalid CVC.");

        addStripeCardDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // If fields are validated send the user a prompt if they want to add the fuel card.
                        if (validator.validate()) {

                            // Close the addStripeCardDialog.
                            addStripeCardDialog.dismiss();

                            // Create a new confirmation dialog builder.
                            AlertDialog.Builder addStripeCardConfirmationDialog = new AlertDialog.Builder(PaymentMethodActivity.this);

                            // Set the title of the dialog.
                            addStripeCardConfirmationDialog.setTitle("Are you sure you want to add this credit/debit card?");

                            // If the response is yes then add the credit/debit card.
                            addStripeCardConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    addStripeCard(editTextCardNumber.getText().toString(), editTextExpMonth.getText().toString(), editTextExpYear.getText().toString(), editTextCvc.getText().toString());
                                }
                            });

                            // If response is no then do nothing.
                            addStripeCardConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                            // Creating and displaying the addStripeCardConfirmationDialog.
                            AlertDialog alertDialog = addStripeCardConfirmationDialog.create();
                            alertDialog.show();

                            // Setting the dialog button colours.
                            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(Color.BLACK);
                            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    }
                });

                // Negative button do nothing.
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setTextColor(Color.BLACK);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addStripeCardDialog.dismiss();
                    }
                });
            }
        });

        // Displays the addFuelCardDialog.
        addStripeCardDialog.show();
    }

    // Adds the credit/debit to the user's account.
    public void addStripeCard(String stripeCardNo, String expMonth, String expYear, String cvc) {
        call = service.addStripeCard(stripeCardNo, expMonth, expYear, cvc);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(PaymentMethodActivity.this, PaymentMethodActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(PaymentMethodActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Inflates menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.payment_method_menu, menu);
        return true;
    }

    // Cancels any api calls when the activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}
