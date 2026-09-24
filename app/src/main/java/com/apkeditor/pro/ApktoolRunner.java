package com.apkeditor.pro;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Menjalankan apktool.jar di Android lewat dalvikvm.
 *
 * Cara lama (java -jar) tidak jalan di Android karena tidak ada JVM desktop.
 * Solusi: pakai "dalvikvm -cp apktool.jar brut.apktool.Main ..." + android.jar
 * di classpath supaya class Android yang dibutuhkan apktool tersedia.
 */
public class ApktoolRunner {
    private final Context ctx;

    public ApktoolRunner(Context c) { this.ctx = c; }

    private File extractAsset(String assetName, String outName) throws IOException {
        File out = new File(ctx.getFilesDir(), outName);
        if (out.exists() && out.length() > 0) return out;
        try (InputStream in = ctx.getAssets().open(assetName);
             OutputStream os = new FileOutputStream(out)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
        }
        return out;
    }

    /** Path dalvikvm — selalu ada di /system/bin atau /apex. */
    private String dalvikvm() {
        String[] candidates = {
            "/apex/com.android.runtime/bin/dalvikvm",
            "/system/bin/dalvikvm",
            "/system/xbin/dalvikvm",
        };
        for (String p : candidates) {
            if (new File(p).exists()) return p;
        }
        return "dalvikvm"; // fallback: biar PATH yang cari
    }

    public File decompile(File apk, File outDir) throws Exception {
        File apktoolJar = extractAsset("libs/apktool.jar", "apktool.jar");
        File androidJar = extractAsset("libs/android.jar", "android.jar");

        List<String> cmd = new ArrayList<>();
        cmd.add(dalvikvm());
        cmd.add("-Xmx256m");
        cmd.add("-cp");
        cmd.add(apktoolJar.getAbsolutePath() + ":" + androidJar.getAbsolutePath());
        cmd.add("brut.apktool.Main");
        cmd.add("d");
        cmd.add("-f");
        cmd.add("-o");
        cmd.add(outDir.getAbsolutePath());
        cmd.add(apk.getAbsolutePath());

        run(cmd);
        return outDir;
    }

    public File recompile(File srcDir, File outApk) throws Exception {
        File apktoolJar = extractAsset("libs/apktool.jar", "apktool.jar");
        File androidJar = extractAsset("libs/android.jar", "android.jar");

        List<String> cmd = new ArrayList<>();
        cmd.add(dalvikvm());
        cmd.add("-Xmx256m");
        cmd.add("-cp");
        cmd.add(apktoolJar.getAbsolutePath() + ":" + androidJar.getAbsolutePath());
        cmd.add("brut.apktool.Main");
        cmd.add("b");
        cmd.add("-f");
        cmd.add("-o");
        cmd.add(outApk.getAbsolutePath());
        cmd.add(srcDir.getAbsolutePath());

        run(cmd);
        return outApk;
    }

    private void run(List<String> cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        int code = p.waitFor();
        if (code != 0) {
            throw new RuntimeException("Exit " + code + ":\n" + sb);
        }
    }
}
