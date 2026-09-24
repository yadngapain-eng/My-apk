package com.apkeditor.pro;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.apkeditor.pro.util.CrashReporter;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Cek crash lama
        String crash = CrashReporter.readAndClear(this);
        if (crash != null) {
            new AlertDialog.Builder(this)
                .setTitle("App pernah crash")
                .setMessage(crash.length() > 2000
                    ? crash.substring(0, 2000) + "..." : crash)
                .setPositiveButton("OK", (d, w) -> route())
                .setCancelable(false)
                .show();
            return;
        }
        route();
    }

    private void route() {
        Intent next = OnboardingActivity.shouldShow(this)
            ? new Intent(this, OnboardingActivity.class)
            : new Intent(this, MainActivity.class);
        startActivity(next);
        finish();
    }
}
