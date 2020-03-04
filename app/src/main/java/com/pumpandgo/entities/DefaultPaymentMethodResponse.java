package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class DefaultPaymentMethodResponse {

    @Json(name = "card_id")
    String cardId;

    public String getCardId() {
        return cardId;
    }

    public void setPaymentMethodId(String cardId) {
        this.cardId = cardId;
    }
}
