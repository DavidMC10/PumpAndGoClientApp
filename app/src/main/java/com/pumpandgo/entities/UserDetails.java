package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class UserDetails {

    @Json(name = "first_name")
    String firstName;

    @Json(name = "last_name")
    String lastName;

    @Json(name = "email")
    String email;

    @Json(name = "max_fuel_limit")
    int maxFuelLimit;

    @Json(name = "max_distance_limit")
    int maxDistanceLimit;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getMaxFuelLimit() {
        return maxFuelLimit;
    }

    public int getMaxDistanceLimit() {
        return maxDistanceLimit;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMaxFuelLimit(int maxFuelLimit) {
        this.maxFuelLimit = maxFuelLimit;
    }

    public void setMaxDistanceLimit(int maxDistanceLimit) {
        this.maxDistanceLimit = maxDistanceLimit;
    }
}
