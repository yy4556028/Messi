package com.yuyang.messi.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.splashscreen.SplashScreen;

import com.yuyang.messi.ui.base.AppBaseActivity;

public class SplashActivity extends AppBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        startActivity(new Intent(this, StartActivity.class));
    }

}
