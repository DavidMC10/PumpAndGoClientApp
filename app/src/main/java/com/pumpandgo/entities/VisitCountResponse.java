package com.pumpandgo.entities;

import com.squareup.moshi.Json;

public class VisitCountResponse {

    @Json(name = "first_name")
    String firstName;

    @Json(name = "visit_count")
    int visitCount;

    public String getFirstName() {
        return firstName;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}
