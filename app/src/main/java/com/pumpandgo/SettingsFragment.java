package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.pumpandgo.entities.UserDetails;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private Toolbar topToolbar;

    @BindView(R.id.textViewEmail)
    TextView emailTextView;
    @BindView(R.id.textViewFirstname)
    TextView userFirstNameTextView;
    @BindView(R.id.textViewLastname)
    TextView userLastNameTextView;
    @BindView(R.id.textViewMaxFuellingLimit)
    TextView maxFuellingLimitTextView;
    @BindView(R.id.textViewMaxDistanceLimit)
    TextView maxDistanceLimitTextView;
    @BindView(R.id.progressBar)
    ProgressBar loader;
    @BindView(R.id.settingsRootLayout)
    LinearLayout rootLayout;

    ApiService service;
    TokenManager tokenManager;
    Call<UserDetails> call;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, view);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

        getUserProfileDetails();
        return view;
    }

    public void getUserProfileDetails() {
        rootLayout.setVisibility(View.INVISIBLE);
        loader.setVisibility(View.VISIBLE);

        call = service.getUserProfileDetails();
        call.enqueue(new Callback<UserDetails>() {

            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    loader.setVisibility(View.INVISIBLE);
                    rootLayout.setVisibility(View.VISIBLE);
                    emailTextView.setText(response.body().getEmail());
                    userFirstNameTextView.setText(response.body().getFirstName());
                    userLastNameTextView.setText(response.body().getLastName());
                    maxFuellingLimitTextView.setText("€" + response.body().getMaxFuelLimit());
                    maxDistanceLimitTextView.setText(response.body().getMaxDistanceLimit()+ "KM");
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Loads the paymentmethod activity.
    @OnClick(R.id.paymentMethodTitle)
    void goToPaymentMethodActivity() {
        Intent intent = new Intent(getActivity(), PaymentMethodActivity.class);
        startActivity(intent);
    }
}