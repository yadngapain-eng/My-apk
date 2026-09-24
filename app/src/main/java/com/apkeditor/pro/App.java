package com.apkeditor.pro;

import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.apkeditor.pro.util.ThemePrefs;

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean dark = ThemePrefs.isDark(this);
        AppCompatDelegate.setDefaultNightMode(dark
            ? AppCompatDelegate.MODE_NIGHT_YES
            : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
