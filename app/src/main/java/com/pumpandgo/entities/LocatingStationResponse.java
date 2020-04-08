package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class LocatingStationResponse {

    @Json(name = "fuel_station_id")
    int fuelStationId;

    @Json(name = "name")
    String fuelStationName;

    @Json(name = "number_of_pumps")
    int numberOfPumps;

    @Json(name = "channel_id")
    int channelId;

    public int getFuelStationId() {
        return fuelStationId;
    }

    public String getFuelStationName() {
        return fuelStationName;
    }

    public int getNumberOfPumps() {
        return numberOfPumps;
    }

    public int getChannelId() {
        return channelId;
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

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
}
