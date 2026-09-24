package com.apkeditor.pro;

import android.content.Context;

import brut.androlib.ApkDecoder;
import brut.androlib.ApkBuilder;
import brut.androlib.Config;
import brut.directory.ExtFile;

import java.io.File;

/**
 * Decompile/recompile APK pakai apktool-lib (API resmi, bukan CLI).
 *
 * Cara kerja:
 *   - decode: ApkDecoder.decode()  → folder dengan AndroidManifest.xml,
 *             res/, smali/, dan resources.arsc yang sudah decoded
 *   - build : ApkBuilder.build()   → APK siap di-sign
 *
 * apktool-lib ditambahkan sebagai dependency Maven di app/build.gradle:
 *   implementation 'org.apktool:apktool-lib:2.9.3'
 */
public class ApktoolRunner {
    private final Context ctx;

    public ApktoolRunner(Context c) { this.ctx = c; }

    /** Decompile APK ke folder outDir. */
    public File decompile(File apk, File outDir) throws Exception {
        if (!outDir.exists()) outDir.mkdirs();

        Config config = Config.getDefaultConfig();
        config.frameworkDirectory = new File(ctx.getFilesDir(), "framework");

        ApkDecoder decoder = new ApkDecoder(config);
        decoder.setApkFile(new ExtFile(apk));
        decoder.setOutDir(outDir);
        decoder.setForceDelete(true);
        decoder.setDecodeResources(ApkDecoder.DECODE_RESOURCES_FULL);
        decoder.setDecodeSources(ApkDecoder.DECODE_SOURCES_SMALI);

        decoder.decode();
        return outDir;
    }

    /** Recompile folder kerja menjadi APK. */
    public File recompile(File srcDir, File outApk) throws Exception {
        Config config = Config.getDefaultConfig();
        config.frameworkDirectory = new File(ctx.getFilesDir(), "framework");

        ApkBuilder builder = new ApkBuilder(config);
        builder.build(srcDir, outApk);

        return outApk;
    }
}
