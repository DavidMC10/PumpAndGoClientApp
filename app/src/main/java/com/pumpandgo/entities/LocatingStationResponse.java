package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class LocatingStationResponse {

    @Json(name = "fuel_station_id")
    int fuelStationId;

    @Json(name = "name")
    String fuelStationName;

    @Json(name = "number_of_pumps")
    int numberOfPumps;

    public int getFuelStationId() {
        return fuelStationId;
    }

    public String getFuelStationName() {
        return fuelStationName;
    }

    public int getNumberOfPumps() {
        return numberOfPumps;
    }

    public void setFuelStationId(int fuelStationId) {
        this.fuelStationId = fuelStationId;
    }

    public void setFuelStationName(String fuelStationName) {
        this.fuelStationName = fuelStationName;
    }

    public void setNumberOfPumps(int numberOfPumps) {
        this.numberOfPumps = numberOfPumps;
    }
}
