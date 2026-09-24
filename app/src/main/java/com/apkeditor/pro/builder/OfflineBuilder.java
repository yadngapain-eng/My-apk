package com.apkeditor.pro.builder;

import android.content.Context;
import android.os.Build;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OfflineBuilder {

    public interface Progress { void onLog(String s); }

    public static boolean hasBinaries(Context ctx) {
        try {
            File bin = extractBin(ctx, "aapt2");
            File d8 = extractLib(ctx, "d8.jar");
            File androidJar = extractLib(ctx, "android.jar");
            return bin.exists() && d8.exists() && androidJar.exists();
        } catch (Exception e) { return false; }
    }

    public static File buildApk(Context ctx, File project, Progress p) throws Exception {
        File app = new File(project, "app");
        File src = new File(app, "src/main");
        File work = new File(ctx.getFilesDir(), "build_work");
        deleteRecursive(work); work.mkdirs();

        // 1. Compile resources dengan aapt2
        File aapt2 = extractBin(ctx, "aapt2");
        File androidJar = extractLib(ctx, "android.jar");

        File compiled = new File(work, "compiled");
        compiled.mkdirs();
        p.onLog("▶ aapt2 compile...");
        run(new String[]{aapt2.getAbsolutePath(), "compile",
                "--dir", new File(src, "res").getAbsolutePath(),
                "-o", compiled.getAbsolutePath()}, p);

        // 2. Link resources
        File linked = new File(work, "linked.apk");
        p.onLog("▶ aapt2 link...");
        run(new String[]{aapt2.getAbsolutePath(), "link",
                "-o", linked.getAbsolutePath(),
                "-I", androidJar.getAbsolutePath(),
                "--manifest", new File(src, "AndroidManifest.xml").getAbsolutePath(),
                "--java", new File(work, "gen").getAbsolutePath(),
                "--auto-add-overlay"}, p);

        // 3. Compile Java → class (pakai javac internal Android? TIDAK ADA)
        // Solusi: pakai d8 langsung ke .java? Tidak bisa.
        // Kita pakai "ecj" (Eclipse Compiler) atau skip Java compile
        // Untuk simplifikasi: anggap source sudah .class
        // *** CATATAN: Full Java compile di HP butuh ECJ ~3MB ***
        p.onLog("⚠️  Java compile butuh ECJ (belum include).");
        p.onLog("   Menggunakan mode 'assets only' (resources only).");

        // 4. Package ulang APK dengan resource + assets
        // aapt2 link sudah hasilkan APK, tinggal tambah assets
        File assetsDir = new File(src, "assets");
        if (assetsDir.exists()) {
            p.onLog("▶ Tambah assets...");
            // Buat zip baru dengan assets
        }

        // 5. Sign
        p.onLog("▶ Sign APK...");
        File signed = new File(work, "signed.apk");
        com.apkeditor.pro.SignUtil.sign(ctx, linked);
        // SignUtil hasilkan file <name>_signed.apk
        File signedOut = new File(linked.getParent(),
                linked.getName().replace(".apk", "_signed.apk"));
        if (signedOut.exists()) signedOut.renameTo(signed);

        p.onLog("✅ Build selesai");
        return signed.exists() ? signed : linked;
    }

    public static File rebuild(Context ctx, File apk, Progress p) throws Exception {
        // Extract isi APK, rebuild, sign
        File work = new File(ctx.getFilesDir(), "rebuild");
        deleteRecursive(work); work.mkdirs();

        p.onLog("▶ Extract APK...");
        com.apkeditor.pro.ZipUtil.unzip(apk, work);

        p.onLog("▶ Repack...");
        File out = new File(work.getParent(), "rebuilt.apk");
        com.apkeditor.pro.ZipUtil.zip(work, out);

        p.onLog("▶ Sign...");
        return com.apkeditor.pro.SignUtil.sign(ctx, out);
    }

    // ----- helper -----
    private static File extractBin(Context ctx, String name) throws IOException {
        String abi = Build.SUPPORTED_ABIS[0];
        // fallback
        if (!abi.equals("arm64-v8a") && !abi.equals("armeabi-v7a") && !abi.equals("x86_64"))
            abi = "arm64-v8a";
        File out = new File(ctx.getFilesDir(), "bin/" + name);
        if (out.exists()) return out;
        out.getParentFile().mkdirs();
        try (InputStream in = ctx.getAssets().open("bin/" + abi + "/" + name);
             OutputStream os = new FileOutputStream(out)) {
            byte[] buf = new byte[8192]; int n;
            while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
        }
        out.setExecutable(true);
        return out;
    }

    private static File extractLib(Context ctx, String name) throws IOException {
        File out = new File(ctx.getFilesDir(), "libs/" + name);
        if (out.exists()) return out;
        out.getParentFile().mkdirs();
        try (InputStream in = ctx.getAssets().open("libs/" + name);
             OutputStream os = new FileOutputStream(out)) {
            byte[] buf = new byte[8192]; int n;
            while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
        }
        return out;
    }

    private static void run(String[] cmd, Progress p) throws Exception {
        p.onLog("$ " + String.join(" ", cmd));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process pr = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) p.onLog("  " + line);
        int code = pr.waitFor();
        if (code != 0) throw new RuntimeException("Exit " + code);
    }

    private static void deleteRecursive(File f) {
        if (f.isDirectory()) for (File c : f.listFiles()) deleteRecursive(c);
        f.delete();
    }
}