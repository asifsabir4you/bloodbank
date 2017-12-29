package com.bytebiters.asifsabir.blooddonor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/*e
 * Created by asifsabir on 12/12/17.
 */





import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedReqActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;


    ProgressDialog progressDialog;

    List<BloodReq> list = new ArrayList<>();

    RecyclerView recyclerView;

    RecyclerView.Adapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_req);
        getSupportActionBar().setTitle("Saved Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Here

        mAuth = FirebaseAuth.getInstance();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(SavedReqActivity.this));

        progressDialog = new ProgressDialog(SavedReqActivity.this);

        progressDialog.setMessage("Loading Data... \n Please wait!");

        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid().toString())
                .child("savedReq");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("bloodRequest");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    final String studentDetails = String.valueOf(dataSnapshot.child("reqID").getValue());
                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                if(dataSnapshot.getKey().equals(studentDetails)){

                                    BloodReq studentDetails = dataSnapshot.getValue(BloodReq.class);

                                    list.add(studentDetails);}
                            }

                            adapter = new RecyclerViewAdapter(SavedReqActivity.this, list);

                            recyclerView.setAdapter(adapter);

                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            progressDialog.dismiss();

                        }
                    });
                }

                adapter = new RecyclerViewAdapter(SavedReqActivity.this, list);

                recyclerView.setAdapter(adapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();

            }
        });

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