package com.pumpandgo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.pumpandgo.entities.Setting;
import com.pumpandgo.entities.UserDetails;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.progressBar)
    ProgressBar loader;

    @BindView(R.id.settingsRootLayout)
    LinearLayout settingsRootLayout;

    ApiService service;
    TokenManager tokenManager;
    Call<UserDetails> call;
    List<Setting> settingsList;
    ListView listView;

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

        //initializing objects
        settingsList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listView);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        // Remove default title text/
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Get access to the custom title view.
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText("Settings");

        getUserProfileDetails();
        return view;
    }

    public void getUserProfileDetails() {
        loader.setVisibility(View.VISIBLE);
        settingsRootLayout.setVisibility(View.INVISIBLE);
        call = service.getUserProfileDetails();
        call.enqueue(new Callback<UserDetails>() {

            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    loader.setVisibility(View.INVISIBLE);
                    settingsRootLayout.setVisibility(View.VISIBLE);

                    //adding some values to our list
                    settingsList.add(new Setting(R.drawable.ic_account_circle_24px, "Full Name", response.body().getFirstName() + " " + response.body().getLastName()));
                    settingsList.add(new Setting(R.drawable.ic_email_24px, "Email", response.body().getEmail()));
                    settingsList.add(new Setting(R.drawable.ic_lock_24px, "Password", "*********"));
                    settingsList.add(new Setting(R.drawable.ic_noun_distance_24px, "Maximum Distance", String.valueOf(response.body().getMaxDistanceLimit()) + "KM"));

                    //creating the adapter
                    SettingsListAdapter adapter = new SettingsListAdapter((AppCompatActivity)getActivity(), R.layout.layout_settings_list, settingsList);

                    //attaching adapter to the listview
                    listView.setAdapter(adapter);

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

//    // Loads the paymentmethod activity.
//    @OnClick(R.id.paymentMethodTitle)
//    void goToPaymentMethodActivity() {
//        Intent intent = new Intent(getActivity(), PaymentMethodActivity.class);
//        startActivity(intent);
//    }
}