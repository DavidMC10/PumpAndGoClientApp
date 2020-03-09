package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class ReceiptResponse {
    @Json(name = "fuel_station_name")
    String fuelStationName;

    @Json(name = "fuel_station_address_1")
    String fuelStationAddress1;

    @Json(name = "fuel_station_address_2")
    String fuelStationAddress2;

    @Json(name = "fuel_station_address_city_town")
    String fuelStationAddressCityTown;

    @Json(name = "transaction_date")
    String fuelStationTransactionDate;

    @Json(name = "first_name")
    String firstName;

    @Json(name = "last_name")
    String lastName;

    @Json(name = "payment_method")
    String paymentMethod;

    @Json(name = "pump_number")
    String pumpNumber;

    @Json(name = "fuel_type")
    String fuelType;

    @Json(name = "number_of_litres")
    String numberOfLitres;

    @Json(name = "price_per_litre")
    String pricePerLitre;

    @Json(name = "discount")
    String discount;

    @Json(name = "vat_rate")
    String vatRate;

    @Json(name = "price_excluding_vat")
    String priceExVat;

    @Json(name = "vat")
    String vat;

    @Json(name = "total_price")
    String totalPrice;

    public String getFuelStationName() {
        return fuelStationName;
    }

    public String getFuelStationAddress1() {
        return fuelStationAddress1;
    }

    public String getfuelStationAddress2() {
        return fuelStationAddress2;
    }

    public String getfuelStationAddressCityTown() {
        return fuelStationAddressCityTown;
    }

    public String getTransactionDate() {
        return fuelStationTransactionDate;
    }

    public String getfirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPumpNumber() {
        return pumpNumber;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getNumberOfLitres() {
        return numberOfLitres;
    }

    public String getPricePerLitre() {
        return pricePerLitre;
    }

    public String getDiscount() {
        return discount;
    }

    public String getVatRate() {
        return vatRate;
    }

    public String getPriceExVat() {
        return priceExVat;
    }

    public String getVat() {
        return vat;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setFuelStationName(String fuelStationName) {
        this.fuelStationName = fuelStationName;
    }

    public void setFuelStationAddress1(String fuelStationAddress1) {
        this.fuelStationAddress1 = fuelStationAddress1;
    }

    public void setFuelStationAddress2(String fuelStationAddress2) {
        this.fuelStationAddress2 = fuelStationAddress2;
    }

    public void setFuelStationAddressCityTown(String fuelStationAddressCityTown) {
        this.fuelStationAddressCityTown = fuelStationAddressCityTown;
    }

    public void setTransactionDate(String fuelStationTransactionDate) {
        this.fuelStationTransactionDate = fuelStationTransactionDate;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPaymentMethod() {
        this.paymentMethod = paymentMethod;
    }

    public void setPumpNumber(String pumpNumber) {
        this.pumpNumber = pumpNumber;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setNumberOfLitres(String numberOfLitres) {
        this.numberOfLitres = numberOfLitres;
    }

    public void setPricePerLitre(String pricePerLitre) {
        this.pricePerLitre = pricePerLitre;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setVatRate(String vatRate) {
        this.vatRate = vatRate;
    }

    public void setPriceExVat(String priceExVat) {
        this.priceExVat = priceExVat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
