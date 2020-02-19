package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.pumpandgo.entities.AccessToken;
import com.pumpandgo.entities.ApiError;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.editTextFirstname)
    EditText firstNameText;
    @BindView(R.id.editTextLastname)
    EditText lastNameText;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.editTextConfirmPassword)
    EditText editTextConfirmPassword;
    @BindView(R.id.progressBar)
    ProgressBar loader;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        setupRules();

        if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(RegisterActivity.this, ForgotPasswordActivity.class));
            finish();
        }
    }

    @OnClick(R.id.buttonSignup)
    void register(){

        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
//
//        tilName.setError(null);
//        tilEmail.setError(null);
//        tilPassword.setError(null);
//
//        validator.clear();

        if(validator.validate()) {
            loader.setVisibility(View.VISIBLE);
            call = service.register(firstName, lastName, email, password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {
                        Log.w(TAG, "onResponse: " + response.body() );
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        handleErrors(response.errorBody());
                        loader.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.textViewRegister)
    void goToLogin(){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }


    private void handleErrors(ResponseBody response){

        ApiError apiError = Utils.converErrors(response);

        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("first_name")){
                firstNameText.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("last_name")){
                firstNameText.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("email")){
                editTextEmail.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("password")){
                editTextPassword.setError(error.getValue().get(0));
            }
        }

    }

    public void setupRules(){
        validator.addValidation(this, R.id.editTextFirstname, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.editTextLastname, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.editTextEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.editTextPassword, "[a-zA-Z0-9]{6,}", R.string.err_password);
        validator.addValidation(this, R.id.editTextConfirmPassword, R.id.editTextPassword, R.string.err_confirm_password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null) {
            call.cancel();
            call = null;
        }
    }
}