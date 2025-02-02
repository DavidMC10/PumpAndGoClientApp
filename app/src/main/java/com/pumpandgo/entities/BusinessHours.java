package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class BusinessHours {

    @Json(name = "fuel_station_id")
    int fuelStationId;

    @Json(name = "business_hours_id")
    int businessHoursId;

    @Json(name = "day")
    String day;

    @Json(name = "open_time")
    String openTime;

    @Json(name = "close_time")
    String closeTime;

    public int getFuelStationId() {
        return fuelStationId;
    }

    public int getBusinessHoursId() {
        return businessHoursId;
    }

    public String getDay() {
        return day;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setFuelStationId(int fuelStationId) {
        this.fuelStationId = fuelStationId;
    }

    public void setBusinessHoursId(int businessHoursId) {
        this.businessHoursId = businessHoursId;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }
}
