package com.example.asifsabir.blooddonor;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
//subscribing to topics
        FirebaseMessaging.getInstance().subscribeToTopic("bloodReq");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final EditText name = (EditText) findViewById(R.id.et_name);
        final EditText phone = (EditText) findViewById(R.id.et_phone);
        final EditText bloodGroup = (EditText) findViewById(R.id.et_bloodGroup);
        final EditText location = (EditText) findViewById(R.id.et_location);
        Button requestButton = (Button) findViewById(R.id.btn_request);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameText = name.getText().toString();
                final String phoneText = phone.getText().toString();
                final String bloodGroupText = bloodGroup.getText().toString();
                final String locationText = location.getText().toString();

                if (nameText.equals("") || phoneText.equals("") || bloodGroupText.equals("") || locationText.equals("")) {

//                    Snackbar.make(findViewById(R.id.blood_request_layout), "error", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "UnSuccessful! Fill all fields", Toast.LENGTH_SHORT).show();

                } else {
                    DatabaseReference myRef = database.getReference("bloodRequest").push();
                    BloodReq bloodReq = new BloodReq(nameText, phoneText, bloodGroupText, locationText);
                    myRef.setValue(bloodReq);
                    Toast.makeText(MainActivity.this, "Request Successful!", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

}
