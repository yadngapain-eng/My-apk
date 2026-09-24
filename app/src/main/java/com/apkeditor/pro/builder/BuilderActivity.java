package com.apkeditor.pro.builder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.apkeditor.pro.R;
import com.apkeditor.pro.FileUtils;
import com.apkeditor.pro.webtoapk.WebToApkBuilder;
import com.apkeditor.pro.webtoapk.WebToApkActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BuilderActivity extends AppCompatActivity {

    private static final int PICK_ICON = 3001;
    private static final int PICK_HTML = 3002;
    private static final int PICK_APK  = 3003;

    private EditText etName, etPkg, etUrl;
    private ImageView ivIcon;
    private TextView tvLog;
    private File iconFile, htmlZip, srcApk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_builder);

        etName = findViewById(R.id.etAppName);
        etPkg  = findViewById(R.id.etPackageName);
        etUrl  = findViewById(R.id.etUrl);
        ivIcon = findViewById(R.id.ivIcon);
        tvLog  = findViewById(R.id.tvLog);

        findViewById(R.id.btnPickIcon).setOnClickListener(v -> pick(PICK_ICON, "image/*"));
        findViewById(R.id.btnPickHtml).setOnClickListener(v -> pick(PICK_HTML, "application/zip"));
        findViewById(R.id.btnPickApk).setOnClickListener(v -> pick(PICK_APK, "application/vnd.android.package-archive"));
        findViewById(R.id.btnBuildWeb).setOnClickListener(v -> buildWeb());
        findViewById(R.id.btnRebuildApk).setOnClickListener(v -> rebuildApk());
        findViewById(R.id.btnTestAapt2).setOnClickListener(v -> testAapt2());

        log("Build Offline Builder siap.");
        log("Cek binary: " + (OfflineBuilder.hasBinaries(this) ? "✅ OK" : "❌ Tidak ada"));
    }

    private void testAapt2() {
        new Thread(() -> {
            try {
                if (OfflineBuilder.hasBinaries(this)) {
                    String ver = OfflineBuilder.getVersion(this);
                    runOnUiThread(() -> log("✅ aapt2 OK: " + ver));
                } else {
                    runOnUiThread(() -> log("❌ aapt2 binary tidak ada di assets"));
                }
            } catch (Exception e) {
                runOnUiThread(() -> log("❌ " + e.getMessage()));
            }
        }).start();
    }

    private void pick(int code, String type) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType(type);
        startActivityForResult(Intent.createChooser(i, "Pilih"), code);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res != Activity.RESULT_OK || data == null) return;
        try {
            File dir = new File(getExternalFilesDir(null), "builder");
            dir.mkdirs();
            if (req == PICK_ICON) {
                iconFile = new File(dir, "icon.png");
                FileUtils.copyUriToFile(this, data.getData(), iconFile);
                android.graphics.Bitmap b = android.graphics.BitmapFactory.decodeFile(iconFile.getAbsolutePath());
                ivIcon.setImageBitmap(b);
                log("Icon: " + iconFile.length() + " bytes");
            } else if (req == PICK_HTML) {
                htmlZip = new File(dir, "web.zip");
                FileUtils.copyUriToFile(this, data.getData(), htmlZip);
                log("HTML zip: " + htmlZip.length() + " bytes");
            } else if (req == PICK_APK) {
                srcApk = new File(dir, "source.apk");
                FileUtils.copyUriToFile(this, data.getData(), srcApk);
                log("APK: " + srcApk.length() + " bytes");
            }
        } catch (Exception e) { log("❌ " + e.getMessage()); }
    }

    private void buildWeb() {
        String name = etName.getText().toString().trim();
        String pkg  = etPkg.getText().toString().trim();
        String url  = etUrl.getText().toString().trim();

        if (name.isEmpty() || pkg.isEmpty() || (!url.isEmpty() == false && htmlZip == null)) {
            toast("Isi nama + package + (URL atau HTML zip)"); return;
        }
        if (iconFile == null) { toast("Pilih icon"); return; }

        final String fName = name, fPkg = pkg;
        final String fUrl = url.isEmpty() ? "" : (url.startsWith("http") ? url : "https://" + url);
        final File fHtml = htmlZip, fIcon = iconFile;

        new Thread(() -> {
            try {
                File project = WebToApkBuilder.build(this, fName, fPkg,
                        fUrl.isEmpty() ? "file:///android_asset/index.html" : fUrl, fIcon);
                if (fHtml != null) {
                    runOnUiThread(() -> log("Extract HTML ke assets..."));
                    File assets = new File(project, "app/src/main/assets");
                    assets.mkdirs();
                    unzip(fHtml, assets);
                    runOnUiThread(() -> log("✅ HTML extracted"));
                }
                runOnUiThread(() -> log("▶ Mulai build APK offline..."));
                File apk = OfflineBuilder.buildApk(this, project, new OfflineBuilder.Progress() {
                    @Override public void onLog(String s) { runOnUiThread(() -> log(s)); }
                });
                runOnUiThread(() -> {
                    log("✅ APK jadi: " + apk.getAbsolutePath());
                    installApk(apk);
                });
            } catch (Exception e) {
                runOnUiThread(() -> log("❌ " + e.getMessage()));
            }
        }).start();
    }

    private void rebuildApk() {
        if (srcApk == null) { toast("Pilih APK dulu"); return; }
        new Thread(() -> {
            try {
                File out = OfflineBuilder.rebuild(this, srcApk,
                    new OfflineBuilder.Progress() {
                        @Override public void onLog(String s) { runOnUiThread(() -> log(s)); }
                    });
                runOnUiThread(() -> {
                    log("✅ Rebuild: " + out.getAbsolutePath());
                    installApk(out);
                });
            } catch (Exception e) {
                runOnUiThread(() -> log("❌ " + e.getMessage()));
            }
        }).start();
    }

    private void installApk(File apk) {
        try {
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", apk);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(uri, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) { log("Install: " + e.getMessage()); }
    }

    private static void unzip(File zip, File out) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry e; byte[] buf = new byte[8192];
            while ((e = zis.getNextEntry()) != null) {
                File f = new File(out, e.getName());
                if (e.isDirectory()) { f.mkdirs(); continue; }
                f.getParentFile().mkdirs();
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    int n; while ((n = zis.read(buf)) > 0) fos.write(buf, 0, n);
                }
            }
        }
    }

    private void log(String s) { tvLog.append(s + "\n"); }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}