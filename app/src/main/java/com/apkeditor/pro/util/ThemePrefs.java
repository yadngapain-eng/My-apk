package com.apkeditor.pro.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper tema: dark/light + warna primary.
 * Dipakai di App.onCreate() untuk apply sebelum Activity dibuat.
 */
public class ThemePrefs {
    private static final String PREFS = "theme_prefs";
    private static final String KEY_MODE  = "dark_mode";
    private static final String KEY_COLOR = "primary_color";

    public static boolean isDark(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_MODE, true);
    }
    public static void setDark(Context c, boolean dark) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_MODE, dark).apply();
    }
    public static String getColor(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_COLOR, "#2196F3");
    }
    public static void setColor(Context c, String hex) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_COLOR, hex).apply();
    }
}
