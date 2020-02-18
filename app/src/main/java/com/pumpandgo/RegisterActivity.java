package com.pumpandgo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

//    private static final String TAG = "RegisterActivity";

//    @BindView(R.id.editTextFirstname)
//    TextInputLayout tilName;
//    @BindView(R.id.editTextEmail)
//    TextInputLayout tilEmail;
//    @BindView(R.id.editTextPassword)
//    TextInputLayout tilPassword;
//
//    ApiService service;
//    Call<AccessToken> call;
//    AwesomeValidation validator;
//    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        ButterKnife.bind(this);
//
//        service = RetrofitBuilder.createService(ApiService.class);
//        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
//        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
//        setupRules();

//        if(tokenManager.getToken().getAccessToken() != null){
//            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//            finish();
//        }
    }

//    @OnClick(R.id.buttonSignup)
//    void register(){
//
//        String name = tilName.getEditText().getText().toString();
//        String email = tilEmail.getEditText().getText().toString();
//        String password = tilPassword.getEditText().getText().toString();
//
//        tilName.setError(null);
//        tilEmail.setError(null);
//        tilPassword.setError(null);
//
//        validator.clear();
//
//        if(validator.validate()) {
//            call = service.register(name, email, password);
//            call.enqueue(new Callback<AccessToken>() {
//                @Override
//                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
//
//                    Log.w(TAG, "onResponse: " + response);
//
//                    if (response.isSuccessful()) {
//                        Log.w(TAG, "onResponse: " + response.body() );
//                        tokenManager.saveToken(response.body());
//                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                        finish();
//                    } else {
//                        handleErrors(response.errorBody());
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<AccessToken> call, Throwable t) {
//                    Log.w(TAG, "onFailure: " + t.getMessage());
//                }
//            });
//        }
//    }
//
//    @OnClick(R.id.textViewRegister)
//    void goToLogin(){
//        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//    }
//
//
//    private void handleErrors(ResponseBody response){
//
//        ApiError apiError = Utils.converErrors(response);
//
//        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
//            if(error.getKey().equals("name")){
//                tilName.setError(error.getValue().get(0));
//            }
//            if(error.getKey().equals("email")){
//                tilEmail.setError(error.getValue().get(0));
//            }
//            if(error.getKey().equals("password")){
//                tilPassword.setError(error.getValue().get(0));
//            }
//        }
//
//    }
//
//    public void setupRules(){
//        validator.addValidation(this, R.id.editTextFirstname, RegexTemplate.NOT_EMPTY, R.string.err_name);
//        validator.addValidation(this, R.id.editTextEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
//        validator.addValidation(this, R.id.editTextPassword, "[a-zA-Z0-9]{6,}", R.string.err_password);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(call != null) {
//            call.cancel();
//            call = null;
//        }
//    }
}