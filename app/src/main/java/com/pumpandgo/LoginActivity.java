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
import com.pumpandgo.entities.AccessToken;
import com.pumpandgo.entities.ApiError;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Bind variables from view.
    @BindView(R.id.editTextEmail)
    EditText emailText;
    @BindView(R.id.editTextPassword)
    EditText passwordText;
    @BindView(R.id.progressBar)
    ProgressBar loader;

    ApiService service;
    TokenManager tokenManager;
    AwesomeValidation validator;
    Call<AccessToken> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        setupRules();

        if (tokenManager.getToken().getAccessToken() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

    }

    // Allows the user to login.
    @OnClick(R.id.buttonSignIn)
    public void login() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (validator.validate()) {
            loader.setVisibility(View.VISIBLE);
            call = service.login(email, password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        if (response.code() == 422) {

                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.converErrors(response.errorBody());
                            Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        loader.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                    loader.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    // Loads the register activity.
    @OnClick(R.id.textViewRegister)
    public void goToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    // Sets validation rules.
    public void setupRules() {
        validator.addValidation(this, R.id.editTextEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.editTextPassword, "[a-zA-Z0-9]{6,}", R.string.err_password);
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