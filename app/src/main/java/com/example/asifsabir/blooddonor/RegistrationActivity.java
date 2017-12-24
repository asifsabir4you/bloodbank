package com.example.asifsabir.blooddonor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button registerButton;
    String bloodGroupText = "";
    GPSTracker gps;
    double latOfSensor = 0, lonOfSensor = 0;
    String latitude = "", longitude = "";
    private FirebaseAuth mAuth;
    final Handler ha = new Handler();
    LinearLayout gpsSearchLayout,registrationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_registration);
        registerButton = (Button) findViewById(R.id.btn_register);


        gpsSearchLayout = (LinearLayout) findViewById(R.id.layout_gps_search);
        registrationLayout = (LinearLayout) findViewById(R.id.layout_registration);


        getSupportActionBar().setTitle("Register a new user");
        mAuth = FirebaseAuth.getInstance();
        Spinner spinner = (Spinner) findViewById(R.id.blood_group_spinner);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        //getting gps data
        gps = new GPSTracker(RegistrationActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation() && gps.getLatitude() != 0) {

            latOfSensor = gps.getLatitude();
            lonOfSensor = gps.getLongitude();

            //button enable kore dao

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final EditText name = (EditText) findViewById(R.id.et_name);
        final EditText phone = (EditText) findViewById(R.id.et_phone);
        final Button changeButton = (Button) findViewById(R.id.btn_change_number);

        phone.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            phone.setText(mAuth.getCurrentUser().getPhoneNumber());
        }


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameText = name.getText().toString().trim();
                final String phoneText = phone.getText().toString().trim();
                if (latOfSensor ==0) {
                    gps.showSettingsAlert();
                    Toast.makeText(getApplicationContext(), "Error getting location!", Toast.LENGTH_LONG).show();
                    Log.e("Error", "location error1");
                } else {
                    latitude = String.valueOf(latOfSensor);
                    longitude = String.valueOf(lonOfSensor);
                }

                if (nameText.equals("") || phoneText.equals("") || bloodGroupText.equals("") || latitude.equals("") || longitude.equals("")) {
                    if (latitude.equals("") || longitude.equals("")) {
                        Toast.makeText(getApplicationContext(), "Error getting location!", Toast.LENGTH_LONG).show();
                        Log.e("Error", "location error2");
                    } else {

                        Snackbar snackbar = Snackbar.make(view, "Unsuccessful! Fill all fields", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();

                    }

                } else {
                    DatabaseReference myRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
                    Register register = new Register(nameText, phoneText, bloodGroupText, latitude, longitude, getTimeStamp(), "false");
                    myRef.setValue(register);
                    Toast.makeText(RegistrationActivity.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    finish();

                }
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(RegistrationActivity.this, PhoneAuthActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        gpsSearchLayout.setVisibility(View.VISIBLE);
        locationThread();
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String item = parent.getItemAtPosition(pos).toString();
        bloodGroupText = item;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "please select something", Toast.LENGTH_SHORT).show();
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


    public void locationThread() {
        gps = new GPSTracker(RegistrationActivity.this);

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
                        latOfSensor = gps.getLatitude();
                        lonOfSensor = gps.getLongitude();
                        gpsSearchLayout.setVisibility(View.GONE);

                        Snackbar snackbar = Snackbar.make(registrationLayout, "Location found successfully!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                        snackbar.show();

                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                    if (gps.getLatitude() == 0)
                        ha.postDelayed(this, 3000);
                }
            }

        }, 3000);

    }
}
