package com.example.asifsabir.blooddonor;

import android.content.Intent;
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


public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;

    String bloodGroupText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setTitle("Register a new user");

        Spinner spinner = (Spinner) findViewById(R.id.blood_group_spinner);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final EditText name = (EditText) findViewById(R.id.et_name);
        final EditText phone = (EditText) findViewById(R.id.et_phone);
        final EditText lat = (EditText) findViewById(R.id.et_lat);
        final EditText lon = (EditText) findViewById(R.id.et_lon);
        final Button registerButton = (Button) findViewById(R.id.btn_register);
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
                final String lattitude = lat.getText().toString().trim();
                final String longitude = lon.getText().toString().trim();

                if (nameText.equals("") || phoneText.equals("") || bloodGroupText.equals("") || lattitude.equals("") || longitude.equals("")) {

                    Snackbar snackbar = Snackbar.make(view, "Unsuccessful! Fill all fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snackbar.show();


                } else {
                    DatabaseReference myRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
                    Register register = new Register(nameText, phoneText, bloodGroupText, lattitude, longitude);
                    myRef.setValue(register);
//                    FirebaseMessaging.getInstance().subscribeToTopic(bloodGroupText);

                    Toast.makeText(RegistrationActivity.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    finish();

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
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
