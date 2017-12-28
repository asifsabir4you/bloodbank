package com.example.asifsabir.blooddonor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by asifsabir on 11/11/17.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {
    LinearLayout layoutMainActivity;
    private static final int REQUEST_CODE_PERMISSION = 2;
    long lastReqTimeLong;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    LinearLayout userDataLayout, suspendLayout;
    ProgressBar progressBar, gpsProgressBar;
    GPSTracker gps;
    double latitude, longitude;
    Button makeReqBtn;
    TextView tvPhone, tvBloodGroup, tvFullName, tvLatLon;
    TextView tvNotificationRange, tvTimer;
    private FirebaseAuth mAuth;
    String fullName = "", phone = "", bloodGroup = "", lat = "", lon = "";
    boolean userBan;
    String topics;
    final Handler ha = new Handler();
    //add ad ad ad ad ad
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layoutMainActivity = (LinearLayout) findViewById(R.id.layout_main_activity);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading);
        gpsProgressBar = (ProgressBar) findViewById(R.id.gps_progressbar);
        tvFullName = (TextView) findViewById(R.id.tv_full_name);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvLatLon = (TextView) findViewById(R.id.tv_lat_lon);
        tvBloodGroup = (TextView) findViewById(R.id.tv_blood_group);
        tvTimer = (TextView) findViewById(R.id.tv_timer);
        makeReqBtn = (Button) findViewById(R.id.btn_make_req);
        tvNotificationRange = (TextView) findViewById(R.id.tv_notification_range);
        userDataLayout = (LinearLayout) findViewById(R.id.layout_user_area);
        suspendLayout = (LinearLayout) findViewById(R.id.layout_suspend);

        if (!ConnectivityReceiver.isConnected()) {
            checkConnection();
        }

        //interstitial ad view on req show
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        // Begin listening to interstitial & show ads.
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });


        mAuth = FirebaseAuth.getInstance();


        //subscribing to that blood group topics

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //   execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // show location button click event
        gps = new GPSTracker(MainActivity.this);
        //checking sync to db and showin lcoation condition in UI
        syncGPS();
        // check parameter of settings
        checkSettingsData();
        //show user the MAKE REQ  button?
        checkReqData();


        if (mAuth != null)

        {
            //checking whether the user is registered or not, if then send to MainActivity
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid().toString());
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    Register RegisteredUserData = snapshot.getValue(Register.class);


                    fullName = RegisteredUserData.fullName.toString();

                    phone = RegisteredUserData.phone.toString();
                    bloodGroup = RegisteredUserData.bloodGroup.toString();
                    userBan = Boolean.valueOf(RegisteredUserData.userBan.toString());
                    progressBar.setVisibility(View.GONE);
                    if (userBan == true) {
                        userDataLayout.setVisibility(View.GONE);
                        suspendLayout.setVisibility(View.VISIBLE);

                        if (bloodGroup.equals("A+")) topics = "Ap";
                        else if (bloodGroup.equals("A-")) topics = "An";
                        else if (bloodGroup.equals("B+")) topics = "Bp";
                        else if (bloodGroup.equals("B-")) topics = "Bn";
                        else if (bloodGroup.equals("AB+")) topics = "ABp";
                        else if (bloodGroup.equals("AB-")) topics = "ABn";
                        else if (bloodGroup.equals("O+")) topics = "Op";
                        else if (bloodGroup.equals("O-")) topics = "On";
                        else topics = "all";

                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topics);
                        FirebaseMessaging.getInstance().subscribeToTopic("banned");

                    } else {
                        tvFullName.setText("Welcome " + fullName);
                        tvPhone.setText(phone);
                        tvBloodGroup.setText(bloodGroup);
                        //      tvLatLon.setText("lattitude: " + lat + "\nlongitude:" + lon);
                        //subscribing to the topics
                        if (bloodGroup.equals("A+")) topics = "Ap";
                        else if (bloodGroup.equals("A-")) topics = "An";
                        else if (bloodGroup.equals("B+")) topics = "Bp";
                        else if (bloodGroup.equals("B-")) topics = "Bn";
                        else if (bloodGroup.equals("AB+")) topics = "ABp";
                        else if (bloodGroup.equals("AB-")) topics = "ABn";
                        else if (bloodGroup.equals("O+")) topics = "Op";
                        else if (bloodGroup.equals("O-")) topics = "On";
                        else topics = "all";
                        FirebaseMessaging.getInstance().subscribeToTopic(topics);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error happened in fetching user data!", Toast.LENGTH_SHORT).show();
                }
            });

        }


        makeReqBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    if ((tvLatLon.getText().toString().equals("synced"))) {

                        Intent i = new Intent(MainActivity.this, MakeRequest.class);
                        i.putExtra("fullName", fullName);
                        i.putExtra("phone", phone);
                        i.putExtra("latitude", latitude);
                        i.putExtra("longitude", longitude);
                        i.putExtra("uID", mAuth.getCurrentUser().getUid());
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Location Error!", Toast.LENGTH_SHORT).show();
                        gps.showSettingsAlert();
                    }
                } else {
                    checkConnection();
                }
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

        if (id == R.id.nav_my_req) {
            if (ConnectivityReceiver.isConnected())
                startActivity(new Intent(getApplicationContext(), MyReqActivity.class));
            else
                checkConnection();
        } else if (id == R.id.nav_map) {
            if (ConnectivityReceiver.isConnected())
                startActivity(new Intent(getApplicationContext(), ShowDonorsMapActivity.class));
            else
                checkConnection();
        } else if (id == R.id.nav_saved_req) {
            if (ConnectivityReceiver.isConnected())
                startActivity(new Intent(getApplicationContext(), SavedReqActivity.class));
            else
                checkConnection();

        } else if (id == R.id.nav_log_out) {
            if (ConnectivityReceiver.isConnected()) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topics);
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
                finish();
            } else
                checkConnection();

        } else if (id == R.id.nav_fb) {
            Uri uri = Uri.parse("https://web.facebook.com/groups/713171778879956/"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Want to be a part of noble work? Be a blood donor.Or search donor around you.\n IT'S FREE!\n\ndownload link:\n";
            shareBody = shareBody + "https://play.google.com/store/apps/details?id=com.example.asifsabir.blooddonor";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Donate blood or search donor!");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), AboutPage.class));
        } else {
            //do nothing
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
            tvNotificationRange.setText("Disabled");
            tvNotificationRange.setTextColor(Color.RED);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkSettingsData();
        checkSettingsData();
        gpsProgressBar.setVisibility(View.VISIBLE);
        locationThread();


    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

    }


    public String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("'Time: 'KK:mm a 'Date: 'dd-MM-yyyy ");
        String format = simpleDateFormat.format(new Date());
        return format;
    }


    public void checkReqData() {
        if (mAuth != null)

        {
            //checking whether the user is registered or not, if then send to MainActivity
            final DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(mAuth.getCurrentUser().getUid().toString())
                    .child("lastReqTime");

// Attach a listener to read the data at our posts reference
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String lastReqTime = dataSnapshot.getValue(String.class);
                    if (lastReqTime != null) {
                        lastReqTimeLong = Long.parseLong(lastReqTime);
                        Log.d("time", lastReqTime);

                        if (lastReqTimeLong + 24 * 60 * 60 * 1000 > System.currentTimeMillis()) {
                            showTimer();
                        } else {
                            makeReqBtn.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //never made a request ; open
                        makeReqBtn.setVisibility(View.VISIBLE);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Last req time reading failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showTimer() {
        tvTimer.setVisibility(View.VISIBLE);
        long timeDiffInMilliseconds = (lastReqTimeLong + 24 * 60 * 60 * 1000 - System.currentTimeMillis());

        //  int seconds = (int) (timeInMilliseconds / 1000) % 60 ;
        long minutes = ((timeDiffInMilliseconds / (1000 * 60)) % 60);
        long hours = ((timeDiffInMilliseconds / (1000 * 60 * 60)) % 24);
        tvTimer.setText("please wait " + hours + " hours " + minutes + " minutes \n" + "for making further requests.");
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = "Good! Connected to Internet";
        } else {
            message = "Sorry! Not connected to internet";
        }

        Snackbar snackbar = Snackbar.make(layoutMainActivity, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();

        if (isConnected)
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
        else
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        snackbar.show();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);

    }

    public void locationThread() {

        if (tvLatLon.getText().toString().equals("synced"))
            gpsProgressBar.setVisibility(View.INVISIBLE);
        else
            tvLatLon.setText("searching...");

        gps = new GPSTracker(MainActivity.this);

        ha.postDelayed(new Runnable() {

            @SuppressLint("NewApi")
            @Override
            public void run() {


                //call function
                if (gps.getLocation() == null) {
                    gps.showSettingsAlert();
                } else {

                    // check if GPS enabled
                    if (gps.canGetLocation() && gps.getLatitude() != 0) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        gpsProgressBar.setVisibility(View.INVISIBLE);
                        syncGPS();
                        //removing this handler though not working
//                        ha.removeCallbacksAndMessages(null);
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
//                    if (gps.getLatitude() != 0.00)
//                        ha.postDelayed(this, 3000);
                }
            }

        }, 3000);

    }


    public void syncGPS() {
        if (gps.canGetLocation() && gps.getLatitude() != 0) {
            gpsProgressBar.setVisibility(View.INVISIBLE);
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            //updating last lat long in firebase

            DatabaseReference latRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(mAuth.getCurrentUser().getUid().toString())
                    .child("latitude");
            latRef.setValue(String.valueOf(latitude));

            DatabaseReference lonRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(mAuth.getCurrentUser().getUid().toString())
                    .child("longitude");
            lonRef.setValue(String.valueOf(longitude));

            DatabaseReference lastRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(mAuth.getCurrentUser().getUid().toString())
                    .child("lastEntry");
            lastRef.setValue(String.valueOf(getTimeStamp()));
            //saving to localDB for notifications
            SharedPreferences.Editor editor = getSharedPreferences("gpsData", MODE_PRIVATE).edit();
            editor.putString("dbLat", String.valueOf(latitude));
            editor.putString("dbLon", String.valueOf(longitude));
            editor.apply();
            // \n is for new line
            tvLatLon.setText("synced");
            tvLatLon.setTextColor(Color.BLACK);
            gpsProgressBar.setVisibility(View.INVISIBLE);


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            tvLatLon.setText("Error");
            tvLatLon.setTextColor(Color.RED);
            gps.showSettingsAlert();
        }
    }

}
