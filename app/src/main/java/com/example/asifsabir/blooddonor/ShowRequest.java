package com.example.asifsabir.blooddonor;

import android.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
    LinearLayout layoutReqView;
    // GPSTracker class
    GPSTracker gps;
    FirebaseAuth mAuth;
    //add ad ad ad ad ad
    private AdView mAdView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);
        layoutReqView = (LinearLayout) findViewById(R.id.layout_req_view);
        getSupportActionBar().setTitle("Showing Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Here

        //firebase database for saving
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //banner
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        nametext = (TextView) findViewById(R.id.name);
        bloodGroupText = (TextView) findViewById(R.id.blood_group);
        phoneText = (TextView) findViewById(R.id.phone);
        locationText = (TextView) findViewById(R.id.location);
        distanceText = (TextView) findViewById(R.id.distance);
        requestTimeText = (TextView) findViewById(R.id.reqtime);


        callActionButton = (ImageButton) findViewById(R.id.call);
        btnSaveReq = (Button) findViewById(R.id.button_save);
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String bloodGroup = intent.getStringExtra("bloodGroup");
        phone = intent.getStringExtra("phone");
        final String location = intent.getStringExtra("location");
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");
        final String timeStamp = intent.getStringExtra("timeStamp");

        mAuth = FirebaseAuth.getInstance();

        // getting this users location data
        gps = new GPSTracker(ShowRequest.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            currentLat = gps.getLatitude();
            currentLon = gps.getLongitude();

            //saving to localDB//updating data
            SharedPreferences.Editor editor = getSharedPreferences("gpsData", MODE_PRIVATE).edit();
            editor.putString("dbLat", String.valueOf(currentLat));
            editor.putString("dbLon", String.valueOf(currentLon));
            editor.apply();

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





        Location loc1 = new Location("");
        loc1.setLatitude(currentLat);
        loc1.setLongitude(currentLon);

        Location loc2 = new Location("");
        loc2.setLatitude(Double.valueOf(latitude));
        loc2.setLongitude(Double.valueOf(longitude));
        final double distance = loc1.distanceTo(loc2);


        nametext.setText(name);
        bloodGroupText.setText(bloodGroup);
        phoneText.setText(phone);
        locationText.setText(location);

        if (distance > 1000) {
            int distanceFinal = (int) distance / 1000;
            distanceText.setText(distanceFinal + " Km away");
        } else {
            int distanceFinal = (int) distance;
            distanceText.setText(distanceFinal + " m only");
        }

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
                String uID = mAuth.getCurrentUser().getUid();
                DatabaseReference savedReqRef = database.getReference("Users").child(uID).child("savedReq").push();
                SavedReq savedReq = new SavedReq(bloodGroup, name, phone, location, String.valueOf((int) distance / 1000), timeStamp);

                savedReqRef.setValue(savedReq);

                Snackbar snackbar = Snackbar.make(layoutReqView, "Request Saved!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                snackbar.show();

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
