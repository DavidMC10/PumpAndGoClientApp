package com.pumpandgo.entities;

import com.squareup.moshi.Json;

import java.util.List;

public class DeleteFuelCardResponse {

    @Json(name = "response_code")
    int responseCode;

    public int getResponseCode() {
        return responseCode;
    }

    public void setFuelStationId(int responseCode) {
        this.responseCode = responseCode;
    }
}
