
package com.apkeditor.pro;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

import java.io.File;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_APK = 1001;
    private TextView tvFileName, tvFileInfo, tvLog;
    private File currentApk, workDir, extractedDir;
    private ApkMeta apkMeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvFileName = findViewById(R.id.tvFileName);
        tvFileInfo = findViewById(R.id.tvFileInfo);
        tvLog      = findViewById(R.id.tvLog);

        workDir = new File(getExternalFilesDir(null), "apkeditor");
        if (!workDir.exists()) workDir.mkdirs();

        findViewById(R.id.btnPickApk).setOnClickListener(v -> pickApk());
        findViewById(R.id.btnDecompile).setOnClickListener(v -> runApktool("d"));
        findViewById(R.id.btnRecompile).setOnClickListener(v -> runApktool("b"));
        findViewById(R.id.btnSign).setOnClickListener(v -> signApk());
        findViewById(R.id.btnZipalign).setOnClickListener(v -> zipalign());
        findViewById(R.id.btnInstall).setOnClickListener(v -> installApk());
        findViewById(R.id.btnEditManifest).setOnClickListener(v -> openEditor());
        findViewById(R.id.btnExtract).setOnClickListener(v -> extractApk());
        findViewById(R.id.btnRepack).setOnClickListener(v -> repackApk());
        findViewById(R.id.btnInfo).setOnClickListener(v -> showInfo());

        log("APK Editor Pro siap. Binary: apktool 2.9.3, smali 2.5.2");
    }

    private void pickApk() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("application/vnd.android.package-archive");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Pilih APK"), PICK_APK);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == PICK_APK && res == Activity.RESULT_OK && data != null) {
            try {
                File out = new File(workDir, "input_" + System.currentTimeMillis() + ".apk");
                FileUtils.copyUriToFile(this, data.getData(), out);
                currentApk = out;
                loadApkInfo();
                log("APK: " + out.getAbsolutePath());
            } catch (Exception e) { log("Err: " + e.getMessage()); }
        }
    }

    private void loadApkInfo() {
        try (ApkFile apk = new ApkFile(currentApk)) {
            apkMeta = apk.getApkMeta();
            tvFileName.setText(apkMeta.getLabel());
            tvFileInfo.setText("Package: " + apkMeta.getPackageName()
                + "\nVersion: " + apkMeta.getVersionName()
                + "\nSize: " + formatSize(currentApk.length()));
        } catch (Exception e) {
            tvFileName.setText(currentApk.getName());
            tvFileInfo.setText("Err: " + e.getMessage());
        }
    }

    private void runApktool(String mode) {
        if (currentApk == null) { toast("Pilih APK dulu"); return; }
        new Thread(() -> {
            try {
                ApktoolRunner runner = new ApktoolRunner(this);
                File result;
                if (mode.equals("d")) {
                    extractedDir = new File(workDir, "out_" + System.currentTimeMillis());
                    runOnUiThread(() -> log("▶ Decompiling..."));
                    result = runner.decompile(currentApk, extractedDir);
                    runOnUiThread(() -> log("✅ Decompile: " + result.getAbsolutePath()));
                } else {
                    if (extractedDir == null || !extractedDir.exists()) {
                        runOnUiThread(() -> log("❌ Belum decompile"));
                        return;
                    }
                    File outApk = new File(workDir, "rebuilt_" + System.currentTimeMillis() + ".apk");
                    runOnUiThread(() -> log("▶ Recompiling..."));
                    result = runner.recompile(extractedDir, outApk);
                    currentApk = result;
                    runOnUiThread(() -> {
                        log("✅ Recompile: " + result.getAbsolutePath());
                        loadApkInfo();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> log("❌ " + e.getMessage()));
            }
        }).start();
    }

    private void signApk() {
        if (currentApk == null) { toast("Pilih APK dulu"); return; }
        new Thread(() -> {
            try {
                File signed = SignUtil.sign(this, currentApk);
                runOnUiThread(() -> {
                    log("✅ Signed: " + signed.getAbsolutePath());
                    currentApk = signed;
                    toast("Signed!");
                });
            } catch (Exception e) {
                runOnUiThread(() -> log("❌ Sign: " + e.getMessage()));
            }
        }).start();
    }

    private void zipalign() {
        if (currentApk == null) { toast("Pilih APK dulu"); return; }
        new Thread(() -> {
            try {
                File out = new File(currentApk.getParent(),
                        "aligned_" + currentApk.getName());
                ZipAlignUtil.align(currentApk, out);
                runOnUiThread(() -> {
                    log("✅ Aligned: " + out.getAbsolutePath());
                    currentApk = out;
                });
            } catch (Exception e) {
                runOnUiThread(() -> log("❌ Zipalign: " + e.getMessage()));
            }
        }).start();
    }

    private void installApk() {
        if (currentApk == null) { toast("Pilih APK dulu"); return; }
        Uri uri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", currentApk);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(uri, "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void openEditor() {
        if (currentApk == null) { toast("Pilih APK dulu"); return; }
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("apk_path", currentApk.getAbsolutePath());
        startActivity(i);
    }

    private void extractApk() {
        if (currentApk == null) { toast("Pilih APK dulu"); return; }
        new Thread(() -> {
            try {
                File out = new File(workDir, "unzip_" + System.currentTimeMillis());
                ZipUtil.unzip(currentApk, out);
                runOnUiThread(() -> log("✅ Extracted: " + out.getAbsolutePath()));
            } catch (Exception e) {
                runOnUiThread(() -> log("❌ Extract: " + e.getMessage()));
            }
        }).start();
    }

    private void repackApk() {
        if (extractedDir == null || !extractedDir.exists()) {
            toast("Decompile dulu"); return;
        }
        new Thread(() -> {
            try {
                File out = new File(workDir, "repacked_" + System.currentTimeMillis() + ".apk");
                ZipUtil.zip(extractedDir, out);
                runOnUiThread(() -> log("✅ Repacked: " + out.getAbsolutePath()));
            } catch (Exception e) {
                runOnUiThread(() -> log("❌ Repack: " + e.getMessage()));
            }
        }).start();
    }

    private void showInfo() {
        if (apkMeta == null) { toast("Pilih APK dulu"); return; }
        String s = "Package: " + apkMeta.getPackageName()
            + "\nLabel: " + apkMeta.getLabel()
            + "\nVersion: " + apkMeta.getVersionName()
            + "\nMin SDK: " + apkMeta.getMinSdkVersion()
            + "\nTarget SDK: " + apkMeta.getTargetSdkVersion()
            + "\nPermissions: " + apkMeta.getUsesPermissions().size();
        new AlertDialog.Builder(this).setTitle("Info APK").setMessage(s)
            .setPositiveButton("OK", null).show();
    }

    void log(String s) {
        runOnUiThread(() -> tvLog.append(s + "\n"));
    }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    static String formatSize(long size) {
        if (size <= 0) return "0 B";
        String[] u = {"B","KB","MB","GB"};
        int d = (int)(Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, d)) + " " + u[d];
    }
}
