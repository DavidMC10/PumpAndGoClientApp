package com.pumpandgo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.pumpandgo.entities.PaymentMethod;
import com.pumpandgo.entities.PaymentMethodResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodActivity extends AppCompatActivity {

    private static final String TAG = "PaymentMethodActivity";

    ApiService service;
    TokenManager tokenManager;
    List<PaymentMethod> paymentMethodList;
    Call<PaymentMethodResponse> call;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentmethod);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Remove default title text/
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText("Payment Methods");
        getPaymentMethods();
    }

    public void getPaymentMethods() {
        call = service.getPaymentMethods();
        call.enqueue(new Callback<PaymentMethodResponse>() {

            @Override
            public void onResponse(Call<PaymentMethodResponse> call, Response<PaymentMethodResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    paymentMethodList = response.body().getData();

                    // Initializing objects.
                    listView = (ListView) findViewById(R.id.listView);

                    // Creating the adapter.
                    PaymentMethodListAdapter adapter = new PaymentMethodListAdapter(PaymentMethodActivity.this, R.layout.layout_paymentmethod_list, paymentMethodList);

                    // Attaching adapter to the listview.
                    listView.setAdapter(adapter);

                } else {
                    tokenManager.deleteToken();
//                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<PaymentMethodResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
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
