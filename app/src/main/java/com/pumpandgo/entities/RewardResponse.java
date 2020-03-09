package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class RewardResponse {

    @Json(name = "barcode_number")
    String barcodeNumber;

    @Json(name = "car_wash_discount_percentage")
    String carWashDiscountPercentage;

    @Json(name = "fuel_discount_percentage")
    String fuelDiscountPercentage;

    @Json(name = "deli_discount_percentage")
    String deliDiscountPercentage;

    @Json(name = "coffee_discount_percentage")
    String cofffeeDiscountPercentage;

    public String getBarcodeNumber() {
        return barcodeNumber;
    }

    public String getCarWashDiscountPercentage() {
        return carWashDiscountPercentage;
    }

    public String getFuelDiscountPercentage() {
        return fuelDiscountPercentage;
    }

    public String getDeliDiscountPercentage() {
        return deliDiscountPercentage;
    }

    public String getCofffeeDiscountPercentage() {
        return cofffeeDiscountPercentage;
    }

    public void setBarcodeNumber(String barcodeNumber) {
        this.barcodeNumber = barcodeNumber;
    }

    public void setCarWashDiscountPercentage(String carWashDiscountPercentage) {
        this.carWashDiscountPercentage = carWashDiscountPercentage;
    }

    public void setFuelDiscountPercentage(String fuelDiscountPercentage) {
        this.fuelDiscountPercentage = fuelDiscountPercentage;
    }

    public void setDeliDiscountPercentage(String deliDiscountPercentage) {
        this.deliDiscountPercentage = deliDiscountPercentage;
    }

    public void setCofffeeDiscountPercentage(String cofffeeDiscountPercentage) {
        this.cofffeeDiscountPercentage = cofffeeDiscountPercentage;
    }
}
