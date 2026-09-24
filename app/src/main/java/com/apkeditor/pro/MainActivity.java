
package com.apkeditor.pro;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

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
    private File currentApk;
    private ApkMeta apkMeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btnApkFile).setOnClickListener(v -> pickApk());
        findViewById(R.id.btnInstalledApp).setOnClickListener(v -> showInstalledApps());
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

    private void showInstalledApps() {
        Toast.makeText(this, "Installed app list - coming soon", Toast.LENGTH_SHORT).show();
    }

    private static String formatSize(long size) {
        if (size <= 0) return "0 B";
        String[] u = {"B", "KB", "MB", "GB"};
        int d = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, d)) + " " + u[d];
    }
}
