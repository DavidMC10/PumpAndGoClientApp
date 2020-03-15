package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class TransactionIdResponse {

    @Json(name = "transaction_id")
    int transactionId;

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
}
