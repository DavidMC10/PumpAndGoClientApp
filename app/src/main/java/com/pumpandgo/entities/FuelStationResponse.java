package com.pumpandgo.entities;

import com.squareup.moshi.Json;

import java.util.List;

public class FuelStationResponse {

    @Json(name = "data")
    List<FuelStation> data;

    public List<FuelStation> getData() {
        return data;
    }
}
