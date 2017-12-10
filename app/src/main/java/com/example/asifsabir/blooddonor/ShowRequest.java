package com.example.asifsabir.blooddonor;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by asifsabir on 11/10/17.
 */

public class ShowRequest extends AppCompatActivity {

    TextView nametext, bloodGroupText, phoneText, locationText, distanceText, requestTimeText;
    ImageButton callActionButton;
    public static String phone;
    Button btnSaveReq;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    double currentLat, currentLon;
    // GPSTracker class
    GPSTracker gps;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);
        getSupportActionBar().setTitle("Showing Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Here


        nametext = (TextView) findViewById(R.id.name);
        bloodGroupText = (TextView) findViewById(R.id.blood_group);
        phoneText = (TextView) findViewById(R.id.phone);
        locationText = (TextView) findViewById(R.id.location);
        distanceText = (TextView) findViewById(R.id.distance);
        requestTimeText = (TextView) findViewById(R.id.reqtime);


        callActionButton = (ImageButton) findViewById(R.id.call);
        btnSaveReq = (Button) findViewById(R.id.button_save);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String bloodGroup = intent.getStringExtra("bloodGroup");
        phone = intent.getStringExtra("phone");
        String location = intent.getStringExtra("location");
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");
        String timeStamp = intent.getStringExtra("timeStamp");


        // getting this users location data
        gps = new GPSTracker(ShowRequest.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            currentLat = gps.getLatitude();
            currentLon = gps.getLongitude();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


//calculating distances
/*
        int R = 6371; // km
        double x = (currentLon - Double.valueOf(longitude)) * Math.cos((Double.valueOf(latitude) + currentLat) / 2);
        double y = (currentLat - Double.valueOf(latitude));
        double distance = Math.sqrt(x * x + y * y) * R;       */


        double distance = distance(currentLat,currentLon,Double.valueOf(latitude),Double.valueOf(longitude));


        nametext.setText(name);
        bloodGroupText.setText(bloodGroup);
        phoneText.setText(phone);
        locationText.setText(location);
        distanceText.setText(latitude + "\n" + longitude+"\n"+distance);
        requestTimeText.setText(timeStamp);

        callActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission and calling will be done in permission method
                permissionCheck();
            }
        });
        btnSaveReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowRequest.this, "Request has been saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //method for making call
    public void makeCall(String number) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(Intent.createChooser(callIntent, "Choose dialing client..."));

    }

    public void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ShowRequest.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ShowRequest.this, new String[]{android.Manifest.permission.CALL_PHONE}, 1); //1 for phone call
            } else {

                makeCall(phone);
            }
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);  //in Miles
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    makeCall(phone);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Please allow permission to make calls.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityIfNeeded(intent, 0);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }



}
