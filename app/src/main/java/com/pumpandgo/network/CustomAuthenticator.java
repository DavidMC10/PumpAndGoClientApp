package com.pumpandgo.network;

import java.io.IOException;

import androidx.annotation.Nullable;

import com.pumpandgo.TokenManager;
import com.pumpandgo.entities.AccessToken;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

/**
 * Title: CustomAuthenticator
 * Author: ProgrammationAndroid
 * Date: 22/06/2017
 * Availability: https://github.com/ProgrammationAndroid/Laravel-Passport-Android/blob/master/android/app/src/main/java/test/tuto_passport/network/CustomAuthenticator.java
 */

public class CustomAuthenticator implements Authenticator {

    private TokenManager tokenManager;
    private static CustomAuthenticator INSTANCE;

    private CustomAuthenticator(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    static synchronized CustomAuthenticator getInstance(TokenManager tokenManager) {
        if (INSTANCE == null) {
            INSTANCE = new CustomAuthenticator(tokenManager);
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        if (responseCount(response) >= 3) {
            return null;
        }

        AccessToken token = tokenManager.getToken();

        ApiService service = RetrofitBuilder.createService(ApiService.class);
        Call<AccessToken> call = service.refresh(token.getRefreshToken() + "a");
        retrofit2.Response<AccessToken> res = call.execute();

        if (res.isSuccessful()) {
            AccessToken newToken = res.body();
            tokenManager.saveToken(newToken);

            return response.request().newBuilder().header("Authorization", "Bearer " + res.body().getAccessToken()).build();
        } else {
            return null;
        }
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}