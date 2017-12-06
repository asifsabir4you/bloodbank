package com.example.asifsabir.blooddonor;

/**
 * Created by asifsabir on 12/6/17.
 */


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
    Animation dropletAnim,appNameAnim;
TextView appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bloodDrop = (ImageView) findViewById(R.id.iv_bloodDrop);
        appName =(TextView)findViewById(R.id.tv_appName);
        dropletAnim = AnimationUtils.loadAnimation(this, R.anim.blood_drop_anim);
        appNameAnim = AnimationUtils.loadAnimation(this, R.anim.app_name_anim);

        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */
        new PrefetchData().execute();

    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... arg0) {
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
                    } else {
                        startActivity(iMain);
                        //    Toast.makeText(SplashScreen.this, "main", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }, 1500);
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
                    } else {
                        //sending for registration
                        status = 1;
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


}