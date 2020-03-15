package com.pumpandgo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.pumpandgo.entities.Setting;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by David McElhinney on 02/03/2020.
 */

public class SettingsListAdapter extends ArrayAdapter<Setting> {

    private static final String TAG = "SettingsListAdapter";

    // Initialise objects.
    Call call;
    ApiService service;
    TokenManager tokenManager;
    List<Setting> settingsList;
    Context context;

    // Declare variable.
    int resource;

    // Constructor initialising the values.
    public SettingsListAdapter(Context context, int resource, List<Setting> settingsList) {
        super(context, resource, settingsList);
        this.context = context;
        this.resource = resource;
        this.settingsList = settingsList;
    }

    // This function will return the ListView Item as a View.
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // LayoutInflater for the list items.
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Gets the view.
        View view = layoutInflater.inflate(resource, null, false);

        // Gets the view elements of the list from the view.
        ImageView imageViewIcon = view.findViewById(R.id.imageViewSettingIcon);
        TextView textViewTitle = view.findViewById(R.id.textViewSettingTitle);
        TextView textViewUserData = view.findViewById(R.id.textViewUserData);
        TextView textViewEdit = view.findViewById(R.id.textViewEdit);

        // Getting the setting of the specified position.
        Setting setting = settingsList.get(position);

        // Adding values to the list item.
        imageViewIcon.setImageDrawable(context.getResources().getDrawable(setting.getIcon()));
        textViewTitle.setText(setting.getTitle());
        textViewUserData.setText(setting.getUserData());

        // Adding a click listener to allow profile updates.
        textViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0) {
                    updateNameDialog();
                } else if (position == 1) {
                    updateEmailDialog();
                } else if (position == 2) {
                    updatePasswordDialog();
                } else if (position == 3) {
                    updateMaxDistanceLimitDialog();
                }
            }
        });

        // Return the view.
        return view;
    }

    // Allows the user to update their name.
    public void updateNameDialog() {
        // Create layout inflater.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_editname, null);

        // Create the AlertDialog and set attributes.
        AlertDialog updateNameDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Update Name:")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        // Get EditText fields.
        EditText editTextFirstName = (EditText) view.findViewById(R.id.editTextFirstName);
        EditText editTextLastName = (EditText) view.findViewById(R.id.editTextLastName);

        // Set validation.
        AwesomeValidation validator = new AwesomeValidation(ValidationStyle.BASIC);
        validator.addValidation(editTextFirstName, RegexTemplate.NOT_EMPTY, "The first name field is required.");
        validator.addValidation(editTextLastName, RegexTemplate.NOT_EMPTY, "The last name field is required.");

        updateNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // If fields are validated send the user a prompt if they want to update their email.
                        if (validator.validate()) {

                            // Close the updateEmailAlertDialog.
                            updateNameDialog.dismiss();

                            // Create a new confirmation dialog builder.
                            AlertDialog.Builder updateNameConfirmationDialog = new AlertDialog.Builder(context);

                            // Set the title of the dialog.
                            updateNameConfirmationDialog.setTitle("Are you sure you want to update your name?");

                            // If the response is yes then update the user's name.
                            updateNameConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updateName(editTextFirstName.getText().toString(), editTextLastName.getText().toString());
                                }
                            });

                            // If response is no then do nothing.
                            updateNameConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                            // Creating and displaying the updateNameConfirmationDialog.
                            AlertDialog alertDialog = updateNameConfirmationDialog.create();
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
                        updateNameDialog.dismiss();
                    }
                });
            }
        });

        // Displays the updateNameDialog.
        updateNameDialog.show();
    }

    // Function to update the user's name.
    public void updateName(String firstName, String lastName) {
        // Gets the token manager instance and sets up a Retrofit Api service.
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        // Sends the Api call.
        call = service.updateName(firstName, lastName);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);
                // If successful reload the fragment or else delete the token and display the Login Activity.
                if (response.isSuccessful()) {
                    ((FragmentActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, new SettingsFragment())
                            .commit();
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

    // Allows the user to update their email.
    public void updateEmailDialog() {
        // Create layout inflater.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_editemail, null);

        // Create the AlertDialog and set attributes.
        AlertDialog updateEmailDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Update Email:")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        // Get EditText fields.
        EditText editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);

        // Set validation.
        AwesomeValidation validator = new AwesomeValidation(ValidationStyle.BASIC);
        validator.addValidation(editTextEmail, Patterns.EMAIL_ADDRESS, "The email must be a valid email address.");

        updateEmailDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // If fields are validated send the user a prompt if they want to update their email.
                        if (validator.validate()) {

                            // Close the updateEmailDialog.
                            updateEmailDialog.dismiss();

                            // Create a new confirmation dialog builder.
                            AlertDialog.Builder updateEmailConfirmationDialog = new AlertDialog.Builder(context);

                            // Set the title of the dialog.
                            updateEmailConfirmationDialog.setTitle("Are you sure you want to update your email address?");

                            // If the response is yes then update the user's email.
                            updateEmailConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updateEmail(editTextEmail.getText().toString());
                                }
                            });

                            // If response is no then do nothing.
                            updateEmailConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                            // Creating and displaying the updateEmailConfirmationDialog.
                            AlertDialog alertDialog = updateEmailConfirmationDialog.create();
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
                        updateEmailDialog.dismiss();
                    }
                });
            }
        });

        // Displays the updateEmailDialog.
        updateEmailDialog.show();
    }

    // Function to update the user's email.
    public void updateEmail(String email) {
        // Gets the token manager instance and sets up a Retrofit Api service.
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        // Sends the Api call.
        call = service.updateEmail(email);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);
                // If successful reload the fragment, or if the email is taken tell the user, or else delete the token and display the Login Activity.
                if (response.isSuccessful()) {
                    ((FragmentActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, new SettingsFragment())
                            .commit();
                } else if (response.code() == 422) {
                    Toast.makeText(context.getApplicationContext(), "The email has already been taken.", Toast.LENGTH_LONG).show();
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

    // Allows the user to update their password.
    public void updatePasswordDialog() {
        // Create layout inflater.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_editpassword, null);

        // Create the AlertDialog and set attributes.
        AlertDialog updatePasswordDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Update Password:")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        // Get EditText fields.
        EditText editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = (EditText) view.findViewById(R.id.editTextConfirmPassword);

        // Set validation.
        AwesomeValidation validator = new AwesomeValidation(ValidationStyle.BASIC);
        validator.addValidation(editTextPassword, "[a-zA-Z0-9]{6,}", "The password must be at least 6 characters.");
        validator.addValidation(editTextConfirmPassword, editTextPassword, "Passwords do not match.");

        updatePasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // If fields are validated send the user a prompt if they want to update their password.
                        if (validator.validate()) {

                            // Close the updatePasswordAlertDialog.
                            updatePasswordDialog.dismiss();

                            // Create a new confirmation dialog builder.
                            AlertDialog.Builder updatePasswordConfirmationDialog = new AlertDialog.Builder(context);

                            // Set the title of the dialog.
                            updatePasswordConfirmationDialog.setTitle("Are you sure you want to update your password?");

                            // If the response is yes then update the user's password.
                            updatePasswordConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updatePassword(editTextPassword.getText().toString());
                                }
                            });

                            // If response is no then do nothing.
                            updatePasswordConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                            // Creating and displaying the updatePasswordConfirmationDialog.
                            AlertDialog alertDialog = updatePasswordConfirmationDialog.create();
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
                        updatePasswordDialog.dismiss();
                    }
                });
            }
        });

        // Displays the updateEmailAlertDialog.
        updatePasswordDialog.show();
    }

    // Function to update the user's password.
    public void updatePassword(String password) {
        // Gets the token manager instance and sets up a Retrofit Api service.
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        // Sends the Api call.
        call = service.updatePassword(password);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);
                // If successful reload the fragment or else delete the token and display the Login Activity.
                if (response.isSuccessful()) {
                    ((FragmentActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, new SettingsFragment())
                            .commit();
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

    // Allows the user to update their maxDistanceLimit.
    public void updateMaxDistanceLimitDialog() {
        // Create Number picker.
        NumberPicker maxDistanceNumberPicker = new NumberPicker(context);

        // Add values to Number Picker Array.
        int minValue = 5;
        int maxValue = 100;
        int step = 5;
        String[] numberPickerArray = new String[maxValue / minValue];
        for (int i = 0; i < numberPickerArray.length; i++) {
            numberPickerArray[i] = String.valueOf(step + i * step);
        }

        // Set Number Picker Attributes.
        maxDistanceNumberPicker.setMinValue(0);
        maxDistanceNumberPicker.setMaxValue(19);
        maxDistanceNumberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        maxDistanceNumberPicker.setWrapSelectorWheel(false);
        maxDistanceNumberPicker.setDisplayedValues(numberPickerArray);

        // Create AlertDialog and set Attributes.
        AlertDialog.Builder maxDistanceNumberPickerDialog = new AlertDialog.Builder(context);
        maxDistanceNumberPickerDialog.setView(maxDistanceNumberPicker);
        maxDistanceNumberPickerDialog.setTitle("Update Max Distance Limit:");

        // If user selects Update, update their maxDistanceLimit.
        maxDistanceNumberPickerDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateMaxDistanceLimit(Integer.parseInt(numberPickerArray[maxDistanceNumberPicker.getValue()]));
            }
        });

        // Do nothing.
        maxDistanceNumberPickerDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Creating and displaying the maxDistanceNumberPickerDialog.
        AlertDialog alertDialog = maxDistanceNumberPickerDialog.create();
        alertDialog.show();

        // Setting the dialog button colours.
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.BLACK);
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }

    // Function to update the user's maxDistanceLimit.
    public void updateMaxDistanceLimit(int maxDistanceLimit) {
        // Gets the token manager instance and sets up a Retrofit Api service.
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        // Sends the Api call.
        call = service.updateMaxDistanceLimit(maxDistanceLimit);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "onResponse: " + response);
                // If successful reload the fragment or else delete the token and display the Login Activity.
                if (response.isSuccessful()) {
                    ((FragmentActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, new SettingsFragment())
                            .commit();
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
