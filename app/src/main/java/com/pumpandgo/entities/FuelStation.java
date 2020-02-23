package com.pumpandgo.entities;

import com.squareup.moshi.Json;

import java.util.List;

public class FuelStation {

    @Json(name = "fuel_station_id")
    int fuelStationId;

    @Json(name = "fuel_station_name")
    String fuelStationName;

    @Json(name = "address1")
    String address1;

    @Json(name = "address2")
    String address2;

    @Json(name = "city_town")
    String cityTown;

    @Json(name = "telephone_no")
    String telephoneNo;

    @Json(name = "number_of_pumps")
    int numberOfPumps;

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

    public void setFuelStationId(int fuelStationId) {
        this.fuelStationId = fuelStationId;
    }

    public String getFuelStationName() {
        return fuelStationName;
    }

    public void setFuelStationName(String fuelStationName) {
        this.fuelStationName = fuelStationName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCityTown() {
        return cityTown;
    }

    public void setCityTown(String cityTown) {
        this.cityTown = cityTown;
    }

    public String getTelephoneNo() {
        return telephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }

    public int getNumberOfPumps() {
        return numberOfPumps;
    }

    public void setNumberOfPumps(int numberOfPumps) {
        this.numberOfPumps = numberOfPumps;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDistancee() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<BusinessHours> getData() {
        return data;
    }
}
