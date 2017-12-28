package com.example.asifsabir.blooddonor;

import android.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by asifsabir on 11/10/17.
 */

public class ShowRequest extends AppCompatActivity implements OnMapReadyCallback {

    //location found from firebase service intent exta
    String latitude, longitude;
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
    private InterstitialAd mInterstitialAd;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);
        layoutReqView = (LinearLayout) findViewById(R.id.layout_req_view);
        getSupportActionBar().setTitle("Showing Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Here
//making map ready

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_request);
        fm.getMapAsync(this);


        //firebase database for saving
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //interstitial ad view on req show
        mInterstitialAd = new InterstitialAd(this);
        // demo: ca-app-pub-3940256099942544/1033173712
        //showreqAd: ca-app-pub-9816854223245104/6041567935
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        // Begin listening to interstitial & show ads.
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });



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
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        final String timeStamp = intent.getStringExtra("timeStamp");
        final String reqId = intent.getStringExtra("reqId");

        mAuth = FirebaseAuth.getInstance();

        // getting this users location data
        gps = new GPSTracker(ShowRequest.this);

        // check if GPS enabled
        if (gps.canGetLocation() && gps.getLatitude() != 0) {

            currentLat = gps.getLatitude();
            currentLon = gps.getLongitude();
//updating last lat long in firebase

            DatabaseReference latRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(mAuth.getCurrentUser().getUid().toString())
                    .child("latitude");
            latRef.setValue(String.valueOf(latitude));

            DatabaseReference lonRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(mAuth.getCurrentUser().getUid().toString())
                    .child("longitude");
            lonRef.setValue(String.valueOf(latitude));

            DatabaseReference lastRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(mAuth.getCurrentUser().getUid().toString())
                    .child("lastEntry");
            lastRef.setValue(String.valueOf(getTimeStamp()));


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

        //incrementing view
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("bloodRequest").child(reqId).child("view");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String count = snapshot.getValue(String.class);
                int countNum = Integer.parseInt(count);
                ++countNum;
                rootRef.setValue(String.valueOf(countNum));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowRequest.this, "Error happened in fetching user data!", Toast.LENGTH_SHORT).show();
            }
        });


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
                savedReqRef.child("reqID").setValue(reqId);

                Snackbar snackbar = Snackbar.make(layoutReqView, "Request Saved!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                snackbar.show();
                btnSaveReq.setVisibility(View.INVISIBLE);

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

    public String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("'Time: 'KK:mm a 'Date: 'dd-MM-yyyy ");
        String format = simpleDateFormat.format(new Date());
        return format;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList pointsArr = null;
        PolylineOptions polylineOptions;


        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setPadding(0, 0, 0, 100);

        //collecting co-ordinate points
        pointsArr = new ArrayList<>();
        pointsArr.add(new LatLng(currentLat, currentLon));
        pointsArr.add(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)));

        //draw polyline on google map


        //adding your marker position
        googleMap.addMarker(new MarkerOptions()
                .position((LatLng) pointsArr.get(0))
                .title("You")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).showInfoWindow();

//adding requesters postion
        googleMap.addMarker(new MarkerOptions()
                .position((LatLng) pointsArr.get(1))
                .title("Requester")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).showInfoWindow();


        //draw polyline
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        polylineOptions.addAll(pointsArr);
        googleMap.addPolyline(polylineOptions);

        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLon), 8));

    }
}

