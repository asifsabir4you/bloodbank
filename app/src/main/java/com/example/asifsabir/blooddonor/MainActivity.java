package com.example.asifsabir.blooddonor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by asifsabir on 11/11/17.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button makeReqBtn;
    TextView tvPhone, tvBloodGroup, tvFullName, tvLatLon;
    TextView tvNotificationRange;
    private FirebaseAuth mAuth;
    String fullName = "", phone = "", bloodGroup = "", lat = "", lon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvFullName = (TextView) findViewById(R.id.tv_full_name);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvLatLon = (TextView) findViewById(R.id.tv_lat_lon);
        tvBloodGroup = (TextView) findViewById(R.id.tv_blood_group);
        makeReqBtn = (Button) findViewById(R.id.btn_make_req);
        tvNotificationRange = (TextView) findViewById(R.id.tv_notification_range);
        //subscribing to that blood group topics

        checkSettingsData();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            //checking whether the user is registered or not, if then send to MainActivity
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid().toString());
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    Register RegisteredUserData = snapshot.getValue(Register.class);


                    fullName = RegisteredUserData.fullName.toString();
                    phone = RegisteredUserData.phone.toString();
                    bloodGroup = RegisteredUserData.bloodGroup.toString();
                    lat = RegisteredUserData.lat.toString();
                    lon = RegisteredUserData.lon.toString();

                    tvFullName.setText("Welcome " + fullName);
                    tvPhone.setText(phone);
                    tvBloodGroup.setText(bloodGroup);
                    tvLatLon.setText("lattitude: " + lat + "\nlongitude:" + lon);
                    //subscribing to the topics
                    String topics = "all";
                    if (bloodGroup.equals("A+")) topics = "Ap";
                    else if (bloodGroup.equals("A-")) topics = "An";
                    else if (bloodGroup.equals("B+")) topics = "Bp";
                    else if (bloodGroup.equals("B-")) topics = "Bn";
                    else if (bloodGroup.equals("O+")) topics = "Op";
                    else if (bloodGroup.equals("O-")) topics = "On";
                    else topics = "all";
                    FirebaseMessaging.getInstance().subscribeToTopic(topics);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error happened in fetching user data!", Toast.LENGTH_SHORT).show();
                }
            });

        }


        makeReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MakeRequest.class);
                i.putExtra("fullName", fullName);
                i.putExtra("phone", phone);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_saved_req) {
            Toast.makeText(this, "Saved requests", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_log_out) {
            FirebaseMessaging.getInstance().subscribeToTopic("none");
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
            finish();
        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "about", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void checkSettingsData() {
        //viewing saved data in settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean enableNotification = prefs.getBoolean("enable_notification", true);
        String radiusRange = prefs.getString("notification_range", "50");
        if (enableNotification) {
            tvNotificationRange.setText(radiusRange + " km");
            tvNotificationRange.setTextColor(Color.BLACK);
        } else {
            tvNotificationRange.setText("Disable");
            tvNotificationRange.setTextColor(Color.RED);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkSettingsData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSettingsData();
    }
}

