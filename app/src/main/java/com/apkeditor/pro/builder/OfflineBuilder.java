package com.apkeditor.pro.builder;

import android.content.Context;
import android.os.Build;

import java.io.*;

public class OfflineBuilder {

    public interface Progress {
        void onLog(String s);
    }

    private static final String[] SUPPORTED_ABIS = {
        "arm64-v8a", "armeabi-v7a", "x86_64", "x86"
    };

    /** Cek apakah aapt2 binary tersedia di assets untuk ABI device */
    public static boolean hasBinaries(Context ctx) {
        try {
            File bin = extractAapt2(ctx);
            return bin.exists() && bin.length() > 100_000;
        } catch (Exception e) {
            return false;
        }
    }

    /** Extract aapt2 dari assets ke filesDir (perlu executable) */
    public static File extractAapt2(Context ctx) throws IOException {
        String abi = Build.SUPPORTED_ABIS[0];
        boolean found = false;
        for (String a : SUPPORTED_ABIS) {
            if (a.equals(abi)) { found = true; break; }
        }
        if (!found) abi = "arm64-v8a";

        File out = new File(ctx.getFilesDir(), "bin/" + abi + "/aapt2");
        if (out.exists() && out.length() > 100_000) return out;
        out.getParentFile().mkdirs();

        String assetPath = "bin/" + abi + "/aapt2";
        try (InputStream in = ctx.getAssets().open(assetPath);
             OutputStream os = new FileOutputStream(out)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
        }
        out.setExecutable(true, false);
        return out;
    }

    /** Ambil versi aapt2 (untuk debugging) */
    public static String getVersion(Context ctx) {
        try {
            File aapt2 = extractAapt2(ctx);
            ProcessBuilder pb = new ProcessBuilder(aapt2.getAbsolutePath(), "version");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            p.waitFor();
            return line != null ? line : "(empty)";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}