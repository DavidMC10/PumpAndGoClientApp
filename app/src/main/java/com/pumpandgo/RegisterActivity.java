package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.pumpandgo.entities.AccessToken;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David McElhinney on 14/02/2020.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    // Declare layout fields.
    private EditText editTextFirstname;
    private EditText editTextLastname;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private ProgressBar loader;

    // Initialise objects.
    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        setupRules();

        // View bindings.
        editTextFirstname = (EditText) findViewById(R.id.editTextFirstname);
        editTextLastname = (EditText) findViewById(R.id.editTextLastname);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        loader = (ProgressBar) findViewById(R.id.progressBar);

        // If token is not null go to the Home activity.
        if (tokenManager.getToken().getAccessToken() != null) {
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }
    }

    // Function that allows the user to register.
    @OnClick(R.id.buttonSignup)
    public void register() {
        // Gets the data from the textfields.
        String firstName = editTextFirstname.getText().toString();
        String lastName = editTextLastname.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (validator.validate()) {
            loader.setVisibility(View.VISIBLE);
            call = service.register(firstName, lastName, email.toLowerCase(), password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {
                        // Ensure activity is not null.
                        if (getApplicationContext() != null) {
                            tokenManager.saveToken(response.body());
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        }
                    } else if (response.code() == 422) {
                        Toast.makeText(getApplicationContext(), "The email has already been taken.", Toast.LENGTH_LONG).show();
                    } else {
                        // Ensure activity is not null.
                        if (getApplicationContext() != null) {
                            loader.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
    }

    // Sets validation rules.
    public void setupRules() {
        validator.addValidation(this, R.id.editTextFirstname, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.editTextLastname, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.editTextEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.editTextPassword, "[a-zA-Z0-9]{6,}", R.string.err_password);
        validator.addValidation(this, R.id.editTextConfirmPassword, R.id.editTextPassword, R.string.err_confirm_password);
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

    // Go to LoginActivity on back pressed.
    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}