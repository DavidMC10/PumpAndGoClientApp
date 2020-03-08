package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class TransactionHistory {

    @Json(name = "transaction_id")
    String transactionId;

    @Json(name = "fuel_station_name")
    String fuelStationName;

    @Json(name = "total_price")
    String totalPrice;

    @Json(name = "transaction_date")
    String transactionDate;

    @Json(name = "number_of_litres")
    String numberOfLitres;

    public String getTransactionId() {
        return transactionId;
    }

    public String getFuelStationName() {
        return fuelStationName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getNumberOfLitres() {
        return numberOfLitres ;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setFuelStationName(String fuelStationName) {
        this.fuelStationName = fuelStationName;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setNumberOfLitres(String numberOfLitres) {
        this.numberOfLitres = numberOfLitres;
    }
}
