package com.pumpandgo;

import android.content.SharedPreferences;

import com.pumpandgo.entities.AccessToken;

/**
 * Title: TokenManager
 * Author: ProgrammationAndroid
 * Date: 22/06/2017
 * Availability: https://github.com/ProgrammationAndroid/Laravel-Passport-Android/blob/master/android/app/src/main/java/test/tuto_passport/TokenManager.java
 */

public class TokenManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static TokenManager INSTANCE = null;

    private TokenManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    static synchronized TokenManager getInstance(SharedPreferences prefs){
        if(INSTANCE == null){
            INSTANCE = new TokenManager(prefs);
        }
        return INSTANCE;
    }

    public void saveToken(AccessToken token){
        editor.putString("ACCESS_TOKEN", token.getAccessToken()).commit();
    }

    public void deleteToken(){
        editor.remove("ACCESS_TOKEN").commit();
    }

    public AccessToken getToken(){
        AccessToken token = new AccessToken();
        token.setAccessToken(prefs.getString("ACCESS_TOKEN", null));
        return token;
    }
}
