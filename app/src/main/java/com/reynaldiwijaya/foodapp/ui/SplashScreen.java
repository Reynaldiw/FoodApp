package com.reynaldiwijaya.foodapp.ui;

import android.os.Bundle;
import android.os.Handler;

import com.reynaldiwijaya.foodapp.R;
import com.reynaldiwijaya.foodapp.helper.SessionManager;

public class SplashScreen extends SessionManager {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setUpDelay();
    }

    private void setUpDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sessionManager.checkLogin();
                finish();
            }
        }, 1000);
    }
}