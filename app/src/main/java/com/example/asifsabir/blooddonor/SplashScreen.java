package com.example.asifsabir.blooddonor;

/**
 * Created by asifsabir on 12/6/17.
 */


import android.Manifest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends Activity {
    public static int status = 5; //0 for not auth; 1 for auth; 2 for registered;
    private FirebaseAuth mAuth;
    ImageView bloodDrop;
    Animation dropletAnim, appNameAnim;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    // Splash screen timer
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bloodDrop = (ImageView) findViewById(R.id.iv_bloodDrop);
        appName = (TextView) findViewById(R.id.tv_appName);
        dropletAnim = AnimationUtils.loadAnimation(this, R.anim.blood_drop_anim);
        appNameAnim = AnimationUtils.loadAnimation(this, R.anim.app_name_anim);


        if (Build.VERSION.SDK_INT >= 23) {
            permissionCheck();
        } else {
            new PrefetchData().execute();
        }


    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Object, Object, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Object... arg0) {
            /* Will make http call here This call will download required data
             * before launching the app */

            checkDatabaseRegistrationData();


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            bloodDrop.setAnimation(dropletAnim);
            appName.setAnimation(appNameAnim);
            // close this activity
            new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent iMain = new Intent(SplashScreen.this, MainActivity.class);
                    Intent iAuth = new Intent(SplashScreen.this, PhoneAuthActivity.class);
                    Intent iReg = new Intent(SplashScreen.this, RegistrationActivity.class);

                    if (status == 0) {
                        startActivity(iAuth);
                        //    Toast.makeText(SplashScreen.this, "auth", Toast.LENGTH_SHORT).show();
                    } else if (status == 1) {
                        startActivity(iReg);
                        //  Toast.makeText(SplashScreen.this, "refg", Toast.LENGTH_SHORT).show();
                    } else if (status == 2) {
                        startActivity(iMain);
                        //    Toast.makeText(SplashScreen.this, "main", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //do nothing
                    }
                    finish();
                }
            }, 5000);
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
                    new PrefetchData().execute();
                } else {

                    //---------showing informations---------
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SplashScreen.this);
                    builder1.setMessage("To use the app you must enable GPS permission. \nwant to grant ?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    permissionCheck();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Toast.makeText(SplashScreen.this, "Sorry! You can't use the app!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
        }
    }


    public void checkDatabaseRegistrationData() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            //checking whether the user is registered or not, if then send to MainActivity
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users");
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(mAuth.getCurrentUser().getUid().toString())) {
                        //send to mainActivity
                        status = 2;
                        return ;
                    } else {
                        //sending for registration
                        status = 1;
                        return;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //DO nothing
                }
            });

        } else {
            status = 0;
        }
    }

    void permissionCheck() {
// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(SplashScreen.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

}