package com.bytebiters.asifsabir.blooddonor;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MakeRequest extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    LinearLayout layoutbloodRequest;
    String bloodGroupText = "";
    public double foundLatitude, foundLongitude;
    String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_make_request);
        getSupportActionBar().setTitle("Make Blood Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Here
        layoutbloodRequest = (LinearLayout)findViewById(R.id.blood_request_layout);

        Spinner spinner = (Spinner) findViewById(R.id.blood_group_spinner);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

//subscribing to topics
//        FirebaseMessaging.getInstance().subscribeToTopic("bloodReq");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final EditText name = (EditText) findViewById(R.id.et_name);
        final EditText phone = (EditText) findViewById(R.id.et_phone);
        final EditText location = (EditText) findViewById(R.id.et_location);
        final Button requestButton = (Button) findViewById(R.id.btn_request);

        //retrieving intent data
        Bundle extras = getIntent().getExtras();
        String foundfullName = extras.getString("fullName");
        String foundPhone = extras.getString("phone");
        foundLatitude = extras.getDouble("latitude");
        foundLongitude = extras.getDouble("longitude");
        final String parsedLat = String.valueOf(foundLatitude);
        final String parsedLon = String.valueOf(foundLongitude);

        uID = extras.getString("uID");


//        Toast.makeText(this, foundLatitude + "\n" + foundLongitude+"\n"+getTimeStamp()+"\n"+uID, Toast.LENGTH_SHORT).show();
        //----putting data on edit texts;
        name.setText(foundfullName);
        phone.setText(foundPhone);


        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameText = name.getText().toString().trim();
                final String phoneText = phone.getText().toString().trim();
                final String locationText = location.getText().toString().trim();

                if(ConnectivityReceiver.isConnected()){
                    if (nameText.equals("") || phoneText.equals("") || bloodGroupText.equals("") || locationText.equals("")) {

                        Snackbar snackbar = Snackbar.make(view, "Unsuccessful! Fill all fields", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();

//
//                    Snackbar.make(view, "Unsuccessful! Fill all fields", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();


                    } else {

                        DatabaseReference reqRef = database.getReference("bloodRequest").push();
                        String reqKey = reqRef.getKey();

                        DatabaseReference myReqRef = database.getReference("Users").child(uID).child("myReq").push();
                        DatabaseReference myReqTimeRef = database.getReference("Users").child(uID).child("lastReqTime");

                        myReqTimeRef.setValue(String.valueOf(System.currentTimeMillis()));
                        myReqRef.child("reqID").setValue(reqKey);

                        BloodReq bloodReq = new BloodReq(nameText, phoneText, bloodGroupText, locationText, parsedLat, parsedLon, uID, getTimeStamp(),"0");
                        reqRef.setValue(bloodReq);

                        Snackbar snackbar = Snackbar.make(view, "Successful! Request has been sent.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                        snackbar.show();
                        requestButton.setVisibility(View.INVISIBLE);


                    }
                }else{
                    checkConnection();
                }

            }
        });

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
        startActivity(new Intent(MakeRequest.this,MainActivity.class));
    }

    public String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("'Date: 'dd-MM-yyyy ' Time: 'KK:mm a ");
        String format = simpleDateFormat.format(new Date());
        return format;
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

        Snackbar snackbar = Snackbar.make(layoutbloodRequest,message, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();

        if(isConnected)
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
        else
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        snackbar.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
