package com.example.asifsabir.blooddonor;

/**
 * Created by asifsabir on 11/3/17.
 */

public class BloodReq {

    public String name;
    public String phone;
    public String bloodGroup;
    public String location;

    public BloodReq() {
        // Default constructor required for calls to DataSnapshot.getValue(Article.class)
    }

    public BloodReq(String name, String phone, String bloodGroup, String location) {
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.location = location;
    }
}