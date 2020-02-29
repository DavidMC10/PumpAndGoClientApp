package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class PaymentMethod {

    @Json(name = "payment_method_id")
    String paymentMethodId;

    @Json(name = "brand")
    String brand;

    @Json(name = "last4")
    String last4;

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }
}
