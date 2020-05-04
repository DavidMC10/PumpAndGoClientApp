package com.pumpandgo;

import com.pumpandgo.entities.ApiError;
import com.pumpandgo.network.RetrofitBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Title: Utils
 * Author: ProgrammationAndroid
 * Date: 22/06/2017
 * Availability: https://github.com/ProgrammationAndroid/Laravel-Passport-Android/blob/master/android/app/src/main/java/test/tuto_passport/Utils.java
 */

public class Utils {

    public static ApiError converErrors(ResponseBody response){
        Converter<ResponseBody, ApiError> converter = RetrofitBuilder.getRetrofit().responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError apiError = null;

        try {
            apiError = converter.convert(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiError;
    }
}