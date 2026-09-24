package com.apkeditor.pro;

import android.content.Context;

import java.io.File;

/**
 * Decompile/recompile APK secara native di Android.
 *
 * Pendekatan:
 *  - Decode resources + manifest pakai ARSCLib (sudah ada di app/libs).
 *  - Decode dex pakai baksmali (dependency Maven).
 *  - Encode ulang pakai smali + ARSCLib.
 *
 * Catatan: ini versi ringkas. Untuk produksi, eksplor API ARSCLib lebih dalam
 * (com.reandroid.arsc.*) dan smali/baksmali langsung.
 */
public class ApktoolRunner {
    private final Context ctx;

    public ApktoolRunner(Context c) { this.ctx = c; }

    /**
     * Decompile APK ke folder outDir (manifest, resources, smali).
     * Implementasi ringkas: buka APK sebagai ZIP, ekstrak isi ke folder,
     * decode resources.arsc pakai ARSCLib, decode *.dex pakai baksmali.
     */
    public File decompile(File apk, File outDir) throws Exception {
        if (!outDir.exists()) outDir.mkdirs();

        // 1. Ekstrak ZIP
        ZipUtil.unzip(apk, outDir);

        // 2. Decode resources.arsc pakai ARSCLib (jika ada)
        File arsc = new File(outDir, "resources.arsc");
        if (arsc.exists()) {
            try {
                Class<?> arscLibClass = Class.forName("com.reandroid.arsc.chunk.TableBlock");
                Object table = arscLibClass.getMethod("readTable", File.class)
                        .invoke(null, arsc);
                // Simpan JSON representation kalau API-nya tersedia
                File resJson = new File(outDir, "resources.json");
                try {
                    arscLibClass.getMethod("toJson", File.class)
                            .invoke(table, resJson);
                } catch (NoSuchMethodException ignored) {}
            } catch (ClassNotFoundException ignored) {
                // ARSCLib tidak tersedia — biarkan resources.arsc apa adanya
            }
        }

        // 3. Decode dex pakai baksmali
        File[] dexFiles = outDir.listFiles((d, n) -> n.endsWith(".dex"));
        if (dexFiles != null && dexFiles.length > 0) {
            File smaliDir = new File(outDir, "smali");
            if (!smaliDir.exists()) smaliDir.mkdirs();
            for (File dex : dexFiles) {
                try {
                    org.jf.baksmali.Main.main(new String[]{
                        "disassemble",
                        "-o", smaliDir.getAbsolutePath(),
                        dex.getAbsolutePath()
                    });
                } catch (Throwable t) {
                    // Lewati kalau API baksmali beda versi
                }
            }
        }

        return outDir;
    }

    /**
     * Recompile folder (hasil decompile) kembali jadi APK.
     * Versi ringkas: build dex pakai smali, lalu ZIP-kan folder ke .apk.
     */
    public File recompile(File srcDir, File outApk) throws Exception {
        // 1. Compile smali → dex (kalau ada folder smali)
        File smaliDir = new File(srcDir, "smali");
        if (smaliDir.exists() && smaliDir.isDirectory()) {
            File dexOut = new File(srcDir, "classes.dex");
            try {
                org.jf.smali.Main.main(new String[]{
                    "assemble",
                    "-o", dexOut.getAbsolutePath(),
                    smaliDir.getAbsolutePath()
                });
            } catch (Throwable t) {
                // Lewati kalau API smali beda versi
            }
        }

        // 2. ZIP-kan hasilnya jadi APK
        ZipUtil.zip(srcDir, outApk);
        return outApk;
    }
}
