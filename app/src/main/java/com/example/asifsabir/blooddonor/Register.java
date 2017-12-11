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
    public String regDate;
    public String userBan;

    public Register() {
        // Default constructor required for calls to DataSnapshot.getValue(Article.class)
    }

    public Register(String fullName, String phone, String bloodGroup, String lat, String lon,String regDate,String userBan) {
        this.fullName = fullName;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.lat = lat;
        this.lon= lon;
        this.regDate=regDate;
        this.userBan=userBan;

    }
}