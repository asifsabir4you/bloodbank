package com.example.asifsabir.blooddonor;

/**
 * Created by asifsabir on 12/6/17.
 */

public class Register {

    public String fullName;
    public String phone;
    public String bloodGroup;
    public String lat;
    public String lon;

    public Register() {
        // Default constructor required for calls to DataSnapshot.getValue(Article.class)
    }

    public Register(String fullName, String phone, String bloodGroup, String lat, String lon) {
        this.fullName = fullName;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.lat = lat;
        this.lon= lon;

    }
}