package com.pumpandgo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pumpandgo.entities.VisitCount;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import retrofit2.Call;

public class PumpNumberActivity extends AppCompatActivity {
    private static final String TAG = "PumpNumberActivity";
    public static final int LOCATION_REQUEST = 101;

    ApiService service;
    TokenManager tokenManager;
    Call<VisitCount> call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pumpnumber);

//        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.layout_actionbar);
////        getSupportActionBar().setTitle("Texaco Dublin Road");
//
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
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

    // Go back to the Home Activity when the back button is pressed.
    @Override
    public void onBackPressed() {
        startActivity(new Intent(PumpNumberActivity.this, HomeActivity.class));
        finish();
    }
}

//    public void getNumberOfPumps() {
//
//        call = service.getNearbyStations(53.304857,-6.304662, 20);
//        call.enqueue(new Callback<FuelStationResponse>() {
//            @Override
//            public void onResponse(Call<FuelStationResponse> call, Response<FuelStationResponse> response) {
//                Log.w(TAG, "onResponse: " + response );
//
//                if(response.isSuccessful()){
//                    fuelStationList = response.body().getData();
//                    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//                    recyclerView.setHasFixedSize(true);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                    //creating recyclerview adapter
//                    FuelStationAdapter adapter = new FuelStationAdapter(getContext(), fuelStationList);
//
//                    //setting adapter to recyclerview
//                    recyclerView.setAdapter(adapter);
//                    Log.d(TAG, response.body().getData().get(0).getAddress1());
//                    Log.d(TAG, response.body().getData().get(0).getData().get(0).getDay());
//                } else {
//                    tokenManager.deleteToken();
//                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                    getActivity().finish();
//                }
//            }
//            @Override
//            public void onFailure(Call<FuelStationResponse> call, Throwable t) {
//                Log.w(TAG, "onFailure: " + t.getMessage() );
//            }
//        });

//    }
