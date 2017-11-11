package com.example.asifsabir.blooddonor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by asifsabir on 11/11/17.
 */

public class MainActivity extends AppCompatActivity {

Button makeReqBtn ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    makeReqBtn = (Button) findViewById(R.id.btn_make_req);


    makeReqBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(),MakeRequest.class));
        }
    });
    }

}
