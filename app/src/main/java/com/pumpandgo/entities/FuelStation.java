package com.pumpandgo.entities;

import com.squareup.moshi.Json;

import java.util.List;

public class FuelStation {

    @Json(name = "fuel_station_id")
    int fuelStationId;

    @Json(name = "name")
    String fuelStationName;

    @Json(name = "address1")
    String address1;

    @Json(name = "address2")
    String address2;

    @Json(name = "city_town")
    String cityTown;

    @Json(name = "longitude")
    double longitude;

    @Json(name = "latitude")
    double latitude;

    @Json(name = "distance")
    double distance;

    @Json(name = "business_hours")
    List<BusinessHours> data;

    public int getFuelStationId() {
        return fuelStationId;
    }

    public String getFuelStationName() {
        return fuelStationName;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCityTown() {
        return cityTown;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setFuelStationName(String fuelStationName) {
        this.fuelStationName = fuelStationName;
    }

    public void setFuelStationId(int fuelStationId) {
        this.fuelStationId = fuelStationId;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setCityTown(String cityTown) {
        this.cityTown = cityTown;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<BusinessHours> getData() {
        return data;
    }
}
