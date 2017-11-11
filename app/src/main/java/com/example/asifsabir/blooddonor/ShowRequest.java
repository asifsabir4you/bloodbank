package com.example.asifsabir.blooddonor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by asifsabir on 11/10/17.
 */

public class ShowRequest extends AppCompatActivity {

    TextView nametext, bloodGroupText, phoneText, locationText;
    ImageButton callActionButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);

        //checking permissions
        if (Build.VERSION.SDK_INT >= 23) {
            permissionCheck();
        }

        nametext = (TextView) findViewById(R.id.name);
        bloodGroupText = (TextView) findViewById(R.id.blood_group);
        phoneText = (TextView) findViewById(R.id.phone);
        locationText = (TextView) findViewById(R.id.location);
        callActionButton = (ImageButton) findViewById(R.id.call);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String bloodGroup = intent.getStringExtra("bloodGroup");
        final String phone = intent.getStringExtra("phone");
        String location = intent.getStringExtra("location");

        nametext.setText(name);
        bloodGroupText.setText(bloodGroup);
        phoneText.setText(phone);
        locationText.setText(location);

        callActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall(phone);
            }
        });

    }

//method for onclick of call image button
public void makeCall(String number) {
    Intent callIntent = new Intent(Intent.ACTION_CALL);
    callIntent.setData(Uri.parse("tel:" + number));
    startActivity(Intent.createChooser(callIntent, "Choose dialing client..."));
}

    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(ShowRequest.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ShowRequest.this, new String[]{android.Manifest.permission.CALL_PHONE}, 1); //1 for phone call
        }
    }
}
