package com.example.asifsabir.blooddonor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MakeRequest extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String bloodGroupText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_make_request);

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
        FirebaseMessaging.getInstance().subscribeToTopic("bloodReq");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final EditText name = (EditText) findViewById(R.id.et_name);
        final EditText phone = (EditText) findViewById(R.id.et_phone);
        final EditText location = (EditText) findViewById(R.id.et_location);
        Button requestButton = (Button) findViewById(R.id.btn_request);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameText = name.getText().toString();
                final String phoneText = phone.getText().toString();
                final String locationText = location.getText().toString();

                if (nameText.equals("") || phoneText.equals("") || bloodGroupText.equals("") || locationText.equals("")) {

//                    Snackbar.make(findViewById(R.id.blood_request_layout), "error", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(MakeRequest.this, "UnSuccessful! Fill all fields", Toast.LENGTH_SHORT).show();

                } else {
                    DatabaseReference myRef = database.getReference("bloodRequest").push();
                    BloodReq bloodReq = new BloodReq(nameText, phoneText, bloodGroupText, locationText);
                    myRef.setValue(bloodReq);
                    Toast.makeText(MakeRequest.this, "Request Successful!", Toast.LENGTH_SHORT).show();

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


}
