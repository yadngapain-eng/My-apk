
package com.apkeditor.pro;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class EditorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        String apkPath = getIntent().getStringExtra("apk_path");
        TextView tvPath = findViewById(R.id.tvPath);
        EditText et = findViewById(R.id.etContent);
        Button btnSave = findViewById(R.id.btnSave);

        // Cari AndroidManifest.xml di folder hasil decompile
        File workDir = new File(getExternalFilesDir(null), "apkeditor");
        File manifest = null;
        File[] outs = workDir.listFiles((d, n) -> n.startsWith("out_"));
        if (outs != null && outs.length > 0) {
            manifest = new File(outs[outs.length - 1], "AndroidManifest.xml");
        }

        if (manifest != null && manifest.exists()) {
            tvPath.setText(manifest.getAbsolutePath());
            try {
                et.setText(FileUtils.read(manifest));
            } catch (Exception e) {
                et.setText("Err: " + e.getMessage());
            }
            final File mf = manifest;
            btnSave.setOnClickListener(v -> {
                try {
                    FileUtils.write(mf, et.getText().toString());
                    Toast.makeText(this, "Tersimpan", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Err: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                }
            });
        } else {
            tvPath.setText("Decompile APK dulu untuk edit Manifest");
            et.setText("(kosong)");
        }
    }
}
