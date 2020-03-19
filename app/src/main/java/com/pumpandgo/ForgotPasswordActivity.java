package com.pumpandgo;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.pumpandgo.entities.AccessToken;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";

    // Declare layout fields.
    private TextView editTextEmail;
    private ProgressBar loader;

    // Initialise objects.
    ApiService service;
    TokenManager tokenManager;
    AwesomeValidation validator;
    Call<Void> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        setupRules();

        // View bindings.
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        loader = (ProgressBar) findViewById(R.id.progressBar);
    }


    // Sends the user a password reset link.
    @OnClick(R.id.buttonResetPassword)
    public void sendPasswordResetLink() {
        String email = editTextEmail.getText().toString();
        if (validator.validate()) {
            loader.setVisibility(View.VISIBLE);
            call = service.sendPasswordResetEmail(email.toLowerCase());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.w(TAG, "onResponse: " + response);
                    if (response.isSuccessful()) {
                        loader.setVisibility(View.INVISIBLE);
                        // Ensure activity is not null.
                        if (getApplicationContext() != null) {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent.", Toast.LENGTH_LONG).show();
                        }
                    } else if (response.code() == 404) {
                        Toast.makeText(ForgotPasswordActivity.this, "User does not exist.", Toast.LENGTH_LONG).show();
                        loader.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                    loader.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    // Sets validation rules.
    public void setupRules() {
        validator.addValidation(this, R.id.editTextEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
    }
}
