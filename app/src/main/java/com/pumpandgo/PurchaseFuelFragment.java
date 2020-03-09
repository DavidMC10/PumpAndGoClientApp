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
import androidx.fragment.app.Fragment;

import com.pumpandgo.entities.VisitCountResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PurchaseFuelFragment extends Fragment {

    private static final String TAG = "PurchaseFuelFragment";

    @BindView(R.id.purchaseFuelRootLayout)
    LinearLayout purchaseFuelRootLayout;
    @BindView(R.id.textViewTitle)
    TextView titleTextView;
    @BindView(R.id.textViewUserFirstName)
    TextView userFirstNameTextView;
    @BindView(R.id.textViewVisitCount)
    TextView visitCountTextView;
    @BindView(R.id.textViewEndingText)
    TextView endingTextTextView;
    @BindView(R.id.progressBar)
    ProgressBar loader;

    // Declaration Variables
    ApiService service;
    TokenManager tokenManager;
    Call<VisitCountResponse> call;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_purchasefuel, container, false);

        ButterKnife.bind(this, view);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        getVisitCount();
        // Inflates the layout.
        return view;
    }

    public void getVisitCount() {
        loader.setVisibility(View.VISIBLE);
        purchaseFuelRootLayout.setVisibility(View.INVISIBLE);
        call = service.visitCount();
        call.enqueue(new Callback<VisitCountResponse>() {

            @Override
            public void onResponse(Call<VisitCountResponse> call, Response<VisitCountResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    loader.setVisibility(View.INVISIBLE);
                    purchaseFuelRootLayout.setVisibility(View.VISIBLE);
                    titleTextView.setText("Good Afternoon");
                    userFirstNameTextView.setText(response.body().getFirstName());
                    if (response.body().getVisitCount() == 0) {
                        visitCountTextView.setText("Congratulations you have unlocked 10% fuel discount on your next fuel purchase.");
                    } else {
                        visitCountTextView.setText(String.valueOf(response.body().getVisitCount()) + " visits to go");
                    }

                    endingTextTextView.setText("to unlock your fuel reward");
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<VisitCountResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    // Loads the register activity.
    @OnClick(R.id.paymentGraphic)
    void goToLocatingStationActivity() {

    }
}
