package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class PaymentTest {

    @Json(name = "success")
    String success;

    public String getTokenId() {
        return success;
    }

    public void setTokenId(String tokenId) {
        this.success = success;
    }
}
