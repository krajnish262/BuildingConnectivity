package com.airtel.buildingconnectivitymmi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airtel.buildingconnectivitymmi.R;
import com.airtel.buildingconnectivitymmi.util.Constant;


/*
    In splash, userSerial is checked if empty then redirected to login screen and if some value then redirected directly to MainMenu.
 */
public class Splash extends AppCompatActivity {

    private String userSerial;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        SharedPreferences dduSharedPref = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        token = dduSharedPref.getString("token", "");

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(!token.equals(""))//If user already logged in then userSerial is not empty, open MainMenu screen
                {
                    startActivity(new Intent(Splash.this,MainActivity.class));
                    Splash.this.overridePendingTransition(R.anim.zoom,R.anim.zoomout);
                    finish();
                }
                else {
                    //If user not logged in then userSerial is empty, open Login screen
                    startActivity(new Intent(Splash.this, Login.class));
                    Splash.this.overridePendingTransition(R.anim.zoom, R.anim.zoomout);
                    finish();
                }

            }
        }, secondsDelayed * 1000);
    }



}
