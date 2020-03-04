package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class PaymentMethod {

    @Json(name = "card_id")
    String cardId;

    @Json(name = "brand")
    String brand;

    @Json(name = "last4")
    String last4;

    public String getCardId() {
        return cardId;
    }

    public String getBrand() {
        return brand;
    }

    public String getLast4() {
        return last4;
    }

    public void setPaymentMethodId(String cardId) {
        this.cardId = cardId;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }
}
