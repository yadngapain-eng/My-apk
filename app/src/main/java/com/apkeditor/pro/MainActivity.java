
package com.apkeditor.pro;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private static final int PICK_APK = 1001;
    private static final long BANNER_INTERVAL = 7000; // 7 detik

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

        bannerContainer = findViewById(R.id.bannerContainer);
        // Mulai siklus banner
        bannerHandler.postDelayed(bannerToggle, 1000);

        findViewById(R.id.btnApkFile).setOnClickListener(v -> pickApk());
        findViewById(R.id.btnInstalledApp).setOnClickListener(v ->
            Toast.makeText(this, "Fitur dalam pengembangan", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnBuilder).setOnClickListener(v ->
            startActivity(new Intent(this, BuilderActivity.class)));
        findViewById(R.id.btnLearnCode).setOnClickListener(v ->
            startActivity(new Intent(this, LearnCodeActivity.class)));
        findViewById(R.id.btnTheme).setOnClickListener(v ->
            startActivity(new Intent(this, ThemeActivity.class)));
        findViewById(R.id.btnProjects).setOnClickListener(v ->
            Toast.makeText(this, "Projects - coming soon", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnSettings).setOnClickListener(v ->
            Toast.makeText(this, "Settings - coming soon", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnExit).setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerHandler.removeCallbacks(bannerToggle);
    }

    private void pickApk() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("application/vnd.android.package-archive");
        startActivityForResult(Intent.createChooser(i, "Pilih APK"), PICK_APK);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == PICK_APK && res == Activity.RESULT_OK && data != null) {
            try {
                File dir = new File(getExternalFilesDir(null), "apks");
                if (!dir.exists()) dir.mkdirs();
                File out = new File(dir, "input_" + System.currentTimeMillis() + ".apk");
                FileUtils.copyUriToFile(this, data.getData(), out);
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
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private static String formatSize(long size) {
        if (size <= 0) return "0 B";
        String[] u = {"B", "KB", "MB", "GB"};
        int d = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, d)) + " " + u[d];
    }
}
