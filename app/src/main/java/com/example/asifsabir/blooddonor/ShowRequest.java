package com.example.asifsabir.blooddonor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by asifsabir on 11/10/17.
 */

public class ShowRequest extends AppCompatActivity {

    TextView nametext, bloodGroupText, phoneText, locationText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);

        nametext = (TextView) findViewById(R.id.name);
        bloodGroupText = (TextView) findViewById(R.id.blood_group);
        phoneText = (TextView) findViewById(R.id.phone);
        locationText = (TextView) findViewById(R.id.location);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String bloodGroup = intent.getStringExtra("bloodGroup");
        String phone = intent.getStringExtra("phone");
        String location = intent.getStringExtra("location");

        nametext.setText(name);
        bloodGroupText.setText(bloodGroup);
        phoneText.setText(phone);
        locationText.setText(location);

    }
}
