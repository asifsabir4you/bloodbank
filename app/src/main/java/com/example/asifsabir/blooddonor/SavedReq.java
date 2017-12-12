package com.example.asifsabir.blooddonor;

/**
 * Created by asifsabir on 12/12/17.
 */


public class SavedReq {
    public String bloodGroup;
    public String name;
    public String phone;
    public String location;
    public String distance;
    public String timeStamp;


    public SavedReq() {
        // Default constructor required for calls to DataSnapshot.getValue(Article.class)
    }

    public SavedReq(String bloodGroup, String name, String phone,
                    String location, String distance,
                    String timeStamp) {
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.location = location;
        this.distance = distance;
        this.timeStamp = timeStamp;
    }
}