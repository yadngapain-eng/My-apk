package com.apkeditor.pro.util;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Simpan stacktrace ke file "last_crash.txt" saat app crash.
 * Di App.onCreate(), pasang handler ini.
 */
public class CrashReporter {

    private static final String FILE_NAME = "last_crash.txt";

    public static void install(Context ctx) {
        final Context appCtx = ctx.getApplicationContext();
        final Thread.UncaughtExceptionHandler prev =
            Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                File f = new File(appCtx.getFilesDir(), FILE_NAME);
                try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                    pw.println("=== CRASH " +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                            .format(new Date()) + " ===");
                    pw.println("Device : " + Build.MANUFACTURER + " " + Build.MODEL);
                    pw.println("Android: " + Build.VERSION.RELEASE
                        + " (SDK " + Build.VERSION.SDK_INT + ")");
                    pw.println();
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    pw.println(sw.toString());
                }
            } catch (Exception ignored) {}
            if (prev != null) prev.uncaughtException(t, e);
        });
    }

    public static String readAndClear(Context ctx) {
        File f = new File(ctx.getFilesDir(), FILE_NAME);
        if (!f.exists()) return null;
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.FileReader(f));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            br.close();
            f.delete();
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
