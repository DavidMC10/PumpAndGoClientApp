package com.pumpandgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.pumpandgo.entities.RewardResponse;
import com.pumpandgo.network.ApiService;
import com.pumpandgo.network.RetrofitBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class RewardsFragment extends Fragment {

    private static final String TAG = "RewardsFragment";

    @BindView(R.id.progressBar)
    ProgressBar loader;
    @BindView(R.id.rewardsRootLayout)
    LinearLayout rewardsRootLayout;
    @BindView(R.id.textViewCoffeeDiscount)
    TextView textViewCoffeeDiscount;
    @BindView(R.id.textViewDeliDiscount)
    TextView textViewDeliDiscount;
    @BindView(R.id.textViewCarWashDiscount)
    TextView textViewCarWashDiscount;
    @BindView(R.id.textViewFuelDiscount)
    TextView textViewFuelDiscount;

    // Declaration variables.
    ApiService service;
    TokenManager tokenManager;
    Call<RewardResponse> call;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
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
        mTitle.setText("Rewards");

        getRewards();
        return view;
    }

    // Gets the user's rewards.
    public void getRewards() {
        loader.setVisibility(View.VISIBLE);
        rewardsRootLayout.setVisibility(View.INVISIBLE);
        call = service.getRewards();
        call.enqueue(new Callback<RewardResponse>() {

            @Override
            public void onResponse(Call<RewardResponse> call, Response<RewardResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    loader.setVisibility(View.INVISIBLE);
                    rewardsRootLayout.setVisibility(View.VISIBLE);

                    // Set the Qr Code.
                    String barcodeNumber = response.body().getBarcodeNumber();
                    ImageView image = getView().findViewById(R.id.imageViewQrCode);
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(barcodeNumber, BarcodeFormat.QR_CODE,800,600);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        image.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                    // Set text fields.
                    textViewCoffeeDiscount.setText(response.body().getCofffeeDiscountPercentage() + "% off of coffee on each fuel transaction");
                    textViewDeliDiscount.setText(response.body().getDeliDiscountPercentage() + "% off the deli on each fuel transaction");
                    textViewCarWashDiscount.setText(response.body().getCarWashDiscountPercentage() + "% off the a carwash on each fuel transaction");
                    textViewFuelDiscount.setText(response.body().getFuelDiscountPercentage() + "% off fuel on every 10th fuel transaction");

                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<RewardResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
