
package com.apkeditor.pro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apkeditor.pro.builder.BuilderActivity;
import com.apkeditor.pro.learn.LearnCodeActivity;
import com.apkeditor.pro.theme.ThemeActivity;
import com.apkeditor.pro.webtoapk.WebToApkActivity;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

import java.io.File;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final long BANNER_INTERVAL = 7000; // 7 detik

    private ActivityResultLauncher<String> pickApkLauncher;
    private File currentApk;
    private ApkMeta apkMeta;
    private FrameLayout bannerContainer;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private boolean bannerVisible = false;

    private final Runnable bannerToggle = new Runnable() {
        @Override public void run() {
            if (bannerContainer == null) return;
            bannerVisible = !bannerVisible;
            if (bannerVisible) {
                bannerContainer.setAlpha(0f);
                bannerContainer.setVisibility(View.VISIBLE);
                bannerContainer.animate().alpha(1f).setDuration(400).start();
            } else {
                bannerContainer.animate().alpha(0f).setDuration(400)
                    .withEndAction(() -> bannerContainer.setVisibility(View.GONE)).start();
            }
            bannerHandler.postDelayed(this, BANNER_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pickApkLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> handlePickedApk(uri));

        bannerContainer = findViewById(R.id.bannerContainer);
        // Mulai siklus banner
        bannerHandler.postDelayed(bannerToggle, 1000);

        findViewById(R.id.btnApkFile).setOnClickListener(v -> pickApk());
        findViewById(R.id.btnInstalledApp).setOnClickListener(v -> openInstalledApps());
        findViewById(R.id.btnBuilder).setOnClickListener(v ->
            startActivity(new Intent(this, BuilderActivity.class)));
        findViewById(R.id.btnLearnCode).setOnClickListener(v ->
            startActivity(new Intent(this, LearnCodeActivity.class)));
        findViewById(R.id.btnFileManager).setOnClickListener(v ->
            startActivity(new Intent(this, FileManagerActivity.class)));
        findViewById(R.id.btnTheme).setOnClickListener(v ->
            startActivity(new Intent(this, ThemeActivity.class)));
        findViewById(R.id.btnProjects).setOnClickListener(v ->
            Toast.makeText(this, "Projects - coming soon", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnSettings).setOnClickListener(v -> showSettings());
        findViewById(R.id.btnExit).setOnClickListener(v -> finish());

        // Minta izin storage (Android 11+ pakai MANAGE_EXTERNAL_STORAGE)
        requestStoragePermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerHandler.removeCallbacks(bannerToggle);
    }

    private void pickApk() {
        pickApkLauncher.launch("application/vnd.android.package-archive");
    }

    private void handlePickedApk(android.net.Uri uri) {
        if (uri == null) return;
        try {
            File dir = new File(getExternalFilesDir(null), "apks");
            if (!dir.exists()) dir.mkdirs();
            File out = new File(dir, "input_" + System.currentTimeMillis() + ".apk");
            FileUtils.copyUriToFile(this, uri, out);
            currentApk = out;

            try (ApkFile apk = new ApkFile(currentApk)) {
                apkMeta = apk.getApkMeta();
                String info = "Package: " + apkMeta.getPackageName()
                    + "\nVersion: " + apkMeta.getVersionName()
                    + "\nSize: " + formatSize(currentApk.length());
                new AlertDialog.Builder(this)
                    .setTitle(apkMeta.getLabel())
                    .setMessage(info)
                    .setPositiveButton("OK", null)
                    .show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    private static String formatSize(long size) {
        if (size <= 0) return "0 B";
        String[] u = {"B", "KB", "MB", "GB"};
        int d = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, d)) + " " + u[d];
    }


    // ================= INSTALLED APPS =================
    private void openInstalledApps() {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        android.content.pm.PackageManager pm = getPackageManager();
        java.util.List<android.content.pm.ResolveInfo> apps =
            pm.queryIntentActivities(i, 0);

        java.util.List<String> labels = new java.util.ArrayList<>();
        final java.util.List<android.content.pm.ResolveInfo> finalApps = apps;
        for (android.content.pm.ResolveInfo ri : apps) {
            labels.add(ri.loadLabel(pm).toString());
        }

        new AlertDialog.Builder(this)
            .setTitle("Pilih aplikasi")
            .setItems(labels.toArray(new String[0]), (d, which) -> {
                android.content.pm.ResolveInfo ri = finalApps.get(which);
                String pkg = ri.activityInfo.packageName;
                try {
                    android.content.pm.PackageInfo pi =
                        pm.getPackageInfo(pkg, 0);
                    java.io.File apk = new java.io.File(pi.applicationInfo.publicSourceDir);
                    String msg = "Package: " + pkg
                        + "\nVersion: " + pi.versionName
                        + "\nSize: " + formatSize(apk.length())
                        + "\nAPK: " + apk.getAbsolutePath();
                    new AlertDialog.Builder(this)
                        .setTitle(ri.loadLabel(pm).toString())
                        .setMessage(msg)
                        .setPositiveButton("Salin ke folder apks", (d2, w2) -> {
                            try {
                                File dir = new File(getExternalFilesDir(null), "apks");
                                if (!dir.exists()) dir.mkdirs();
                                File out = new File(dir, pkg + ".apk");
                                try (java.io.InputStream in = new java.io.FileInputStream(apk);
                                     java.io.OutputStream os = new java.io.FileOutputStream(out)) {
                                    byte[] buf = new byte[8192];
                                    int n;
                                    while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
                                }
                                Toast.makeText(this, "Tersimpan: " + out.getName(),
                                    Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(this, "Err: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Tutup", null)
                        .show();
                } catch (Exception e) {
                    Toast.makeText(this, "Err: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    // ================= ABOUT =================
    private void showAbout() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_about_title)
            .setMessage(R.string.dialog_about_msg)
            .setPositiveButton("OK", null)
            .show();
    }

    // ================= HELP =================
    private void showHelp() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_help_title)
            .setMessage(R.string.dialog_help_msg)
            .setPositiveButton("OK", null)
            .show();
    }

    // ================= SETTINGS =================
    private void showSettings() {
        boolean dark = com.apkeditor.pro.util.ThemePrefs.isDark(this);
        String[] items = {
            getString(R.string.btn_open_folder),
            getString(R.string.btn_clear_cache),
            dark ? "☀️ Mode Terang" : "🌙 Mode Gelap",
        };
        new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_settings_title)
            .setItems(items, (d, which) -> {
                if (which == 0) {
                    startActivity(new Intent(this, FileManagerActivity.class));
                } else if (which == 1) {
                    deleteRecursive(getCacheDir());
                    Toast.makeText(this, R.string.toast_cache_cleared,
                        Toast.LENGTH_SHORT).show();
                } else {
                    toggleTheme();
                }
            })
            .setNegativeButton("Tutup", null)
            .show();
    }

    private void toggleTheme() {
        boolean dark = com.apkeditor.pro.util.ThemePrefs.isDark(this);
        com.apkeditor.pro.util.ThemePrefs.setDark(this, !dark);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(!dark
            ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }

    private static void deleteRecursive(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] cs = f.listFiles();
            if (cs != null) for (File c : cs) deleteRecursive(c);
        }
        f.delete();
    }

    // ================= PERMISSION =================
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!android.os.Environment.isExternalStorageManager()) {
                try {
                    Intent i = new Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                    startActivity(i);
                } catch (Exception e) {
                    Intent i = new Intent(
                        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(i);
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }, 2001);
            }
        }
    }

    // ================= MENU (toolbar) =================
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) { showAbout(); return true; }
        if (id == R.id.action_help)  { showHelp();  return true; }
        return super.onOptionsItemSelected(item);
    }

}
