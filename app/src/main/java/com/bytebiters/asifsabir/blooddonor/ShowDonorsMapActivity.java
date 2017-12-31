package com.bytebiters.asifsabir.blooddonor;

/**
 * Created by asifsabir on 12/20/17.
 */


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowDonorsMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public DatabaseReference databaseUserReference;
    public GoogleMap mMap;
    String dbLat, dbLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_donors_map);
        getSupportActionBar().setTitle("Map view of donors");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences prefs = getSharedPreferences("gpsData", MODE_PRIVATE);
        dbLat = prefs.getString("dbLat", null);
        dbLon = prefs.getString("dbLon", null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device.
     * This method will only be triggered once the user has installed
     * Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;


        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String donorBloodGroup = dataSnapshot.child("bloodGroup").getValue().toString();
                    String donorName = dataSnapshot.child("fullName").getValue().toString();

                    double latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue(String.class).toString());
                    double longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue(String.class).toString());
                    Log.d("map", latitude + " || " + longitude + "||" + donorBloodGroup + "||" + donorName);

                    mMap.addMarker(new MarkerOptions()
                            .title(donorBloodGroup)
                            .snippet(donorName)
                            .visible(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(new LatLng(
                                    latitude,
                                    longitude
                            ))
                    );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
//your position
        mMap = googleMap;
        LatLng YourPosition = new LatLng(Double.parseDouble(dbLat), Double.parseDouble(dbLon));
        Marker mMarker = googleMap.addMarker(new MarkerOptions()
                .position(YourPosition).snippet("this is you.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("You!"));



        googleMap.addCircle(new CircleOptions()
                .center(YourPosition)
                .radius(50000)
                .strokeWidth(2)
                .strokeColor(Color.RED)
                .fillColor(0x25FF0000));  //0x :hexa code ; 60 is % ; last 6 digit is color code

        mMarker.showInfoWindow();
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(YourPosition, 9);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(YourPosition));
        googleMap.animateCamera(yourLocation);

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityIfNeeded(intent, 0);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ShowDonorsMapActivity.this, MainActivity.class));
    }

}
