package com.apkeditor.pro;

import com.apkeditor.pro.widget.ProgressView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.apkeditor.pro.util.MediaStoreUtil;

import java.io.File;
import java.text.DecimalFormat;

public class EditorActivity extends AppCompatActivity {

    private TextView tvPath;
    private EditText et;
    private File workDir, manifest, outDir;
    private String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        apkPath = getIntent().getStringExtra("apk_path");
        tvPath = findViewById(R.id.tvPath);
        et = findViewById(R.id.etContent);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDec = findViewById(R.id.btnDecompile);
        Button btnRec = findViewById(R.id.btnRecompile);
        Button btnSign = findViewById(R.id.btnSign);

        workDir = new File(getExternalFilesDir(null), "apkeditor");
        if (!workDir.exists()) workDir.mkdirs();

        // Cari hasil decompile terakhir
        File[] outs = workDir.listFiles((d, n) -> n.startsWith("out_"));
        if (outs != null && outs.length > 0) {
            outDir = outs[outs.length - 1];
            manifest = new File(outDir, "AndroidManifest.xml");
        }

        loadManifest();

        btnSave.setOnClickListener(v -> saveManifest());
        btnDec.setOnClickListener(v -> doDecompile());
        btnRec.setOnClickListener(v -> doRecompile());
        btnSign.setOnClickListener(v -> doSign());
    }

    private void loadManifest() {
        if (manifest != null && manifest.exists()) {
            tvPath.setText(manifest.getAbsolutePath());
            try { et.setText(FileUtils.read(manifest)); }
            catch (Exception e) { et.setText("Err: " + e.getMessage()); }
        } else {
            tvPath.setText("Belum ada hasil decompile. Tekan Decompile.");
            et.setText("(kosong)");
        }
    }

    private void saveManifest() {
        if (manifest == null || !manifest.exists()) {
            Toast.makeText(this, "Tidak ada file untuk disimpan",
                Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileUtils.write(manifest, et.getText().toString());
            Toast.makeText(this, "Tersimpan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Err: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    private void doDecompile() {
        if (apkPath == null || !new File(apkPath).exists()) {
            Toast.makeText(this, "APK belum dipilih", Toast.LENGTH_SHORT).show();
            return;
        }
        final File out = new File(workDir,
            "out_" + System.currentTimeMillis());
        ProgressView pd = showProgress("Decompile APK...");
        new Thread(() -> {
            try {
                new ApktoolRunner(this).decompile(new File(apkPath), out);
                runOnUiThread(() -> {
                    pd.dismiss();
                    outDir = out;
                    manifest = new File(out, "AndroidManifest.xml");
                    loadManifest();
                    Toast.makeText(this, "Decompile sukses",
                        Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "Gagal: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void doRecompile() {
        if (outDir == null || !outDir.exists()) {
            Toast.makeText(this, "Decompile dulu", Toast.LENGTH_SHORT).show();
            return;
        }
        final File outApk = new File(workDir,
            "build_" + System.currentTimeMillis() + ".apk");
        ProgressView pd = showProgress("Recompile APK...");
        new Thread(() -> {
            try {
                new ApktoolRunner(this).recompile(outDir, outApk);
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "Recompile sukses:\n" + outApk.getName(),
                        Toast.LENGTH_LONG).show();
                    // Auto sign
                    doSignWithFile(outApk);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "Gagal: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void doSign() {
        File latest = findLatestApk();
        if (latest == null) {
            Toast.makeText(this, "Tidak ada APK untuk di-sign",
                Toast.LENGTH_SHORT).show();
            return;
        }
        doSignWithFile(latest);
    }

    private void doSignWithFile(File apk) {
        ProgressView pd = showProgress("Signing APK...");
        new Thread(() -> {
            try {
                File signed = SignUtil.sign(this, apk);
                runOnUiThread(() -> {
                    pd.dismiss();
                    try {
                        MediaStoreUtil.saveToDownloads(this, signed,
                            "application/vnd.android.package-archive",
                            signed.getName());
                        Toast.makeText(this,
                            "APK tersimpan di Downloads: " + signed.getName(),
                            Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this,
                            "Sign sukses, gagal simpan: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(this, "Gagal sign: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private File findLatestApk() {
        File[] apks = workDir.listFiles((d, n) -> n.endsWith(".apk"));
        if (apks == null || apks.length == 0) return null;
        File latest = apks[0];
        for (File f : apks) if (f.lastModified() > latest.lastModified()) latest = f;
        return latest;
    }

    private ProgressView showProgress(String msg) {
        ProgressView pv = new ProgressView(this, msg);
        pv.show();
        return pv;
    }
}
