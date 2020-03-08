package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pumpandgo.entities.FuelStation;
import com.pumpandgo.entities.FuelStationResponse;
import com.pumpandgo.entities.TransactionHistory;
import com.pumpandgo.entities.TransactionHistoryResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class TransactionHistoryFragment extends Fragment {

    private static final String TAG = "TransactionHistoryFragment";

    ApiService service;
    TokenManager tokenManager;
    Call<TransactionHistoryResponse> call;
    List<TransactionHistory> transactionHistoryList;
    RecyclerView recyclerView;
    View view;

    @BindView(R.id.progressBar)
    ProgressBar loader;
    @BindView(R.id.transactionHistoryRootLayout)
    RelativeLayout transactionHistoryRootLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transactionhistory, container, false);

        ButterKnife.bind(this, view);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

        // Find the toolbar view inside the activity layout.
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText("Transactions");

        getNearbyFuelStations();
        return view;
    }

    public void getNearbyFuelStations(){
        loader.setVisibility(View.VISIBLE);
        transactionHistoryRootLayout.setVisibility(View.INVISIBLE);
        call = service.getNearbyStations(53.304857,-6.304662, 20);
        call.enqueue(new Callback<TransactionHistoryResponse>() {
            @Override
            public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    loader.setVisibility(View.INVISIBLE);
                    transactionHistoryRootLayout.setVisibility(View.VISIBLE);
                    // Ensure activity is not null.
                    if (getActivity() != null) {

                        transactionHistoryList = response.body().getData();
                        // Set Recycler View.
                        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                        // Creating recyclerview adapter.
                        FuelStationListAdapter adapter = new FuelStationListAdapter(getContext(), transactionHistoryList);

                        // Setting adapter to recyclerview.
                        recyclerView.setAdapter(adapter);
                    }
                    Log.d(TAG, response.body().getData().get(0).getFuelStationName());
                } else {
                    // Ensure activity is not null.
                    if (getActivity() != null) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }
}