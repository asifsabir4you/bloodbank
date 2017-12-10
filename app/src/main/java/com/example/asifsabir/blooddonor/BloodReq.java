package com.example.asifsabir.blooddonor;

/**
 * Created by asifsabir on 11/3/17.
 */

public class BloodReq {

    public String name;
    public String phone;
    public String bloodGroup;
    public String location;
    public String latitude;
    public String longitude;
    public String uID;
    public String timeStamp;


    public BloodReq() {
        // Default constructor required for calls to DataSnapshot.getValue(Article.class)
    }

    public BloodReq(String name, String phone,
                    String bloodGroup, String location,
                    String latitude, String longitude,
                    String uID, String timeStamp) {
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.location = location;
        this.latitude=latitude;
        this.longitude=longitude;
        this.uID=uID;
        this.timeStamp=timeStamp;
    }
}