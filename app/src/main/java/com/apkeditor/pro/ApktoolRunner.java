package com.apkeditor.pro;

import android.content.Context;

import brut.androlib.ApkBuilder;
import brut.androlib.ApkDecoder;
import brut.androlib.Config;
import brut.directory.ExtFile;

import java.io.File;

public class ApktoolRunner {
    private final Context ctx;

    public ApktoolRunner(Context c) { this.ctx = c; }

    /** Decompile APK ke folder outDir. */
    public File decompile(File apk, File outDir) throws Exception {
        if (!outDir.exists()) outDir.mkdirs();

        File fwDir = new File(ctx.getFilesDir(), "framework");
        if (!fwDir.exists()) fwDir.mkdirs();

        Config config = Config.getDefaultConfig();
        config.frameworkDirectory = fwDir.getAbsolutePath();
        config.forceDelete = true;
        config.decodeResources = Config.DECODE_RESOURCES_FULL;
        config.decodeSources = Config.DECODE_SOURCES_SMALI;

        ApkDecoder decoder = new ApkDecoder(config, new ExtFile(apk));
        decoder.decode(outDir);

        return outDir;
    }

    /** Recompile folder kerja menjadi APK. */
    public File recompile(File srcDir, File outApk) throws Exception {
        File fwDir = new File(ctx.getFilesDir(), "framework");
        if (!fwDir.exists()) fwDir.mkdirs();

        Config config = Config.getDefaultConfig();
        config.frameworkDirectory = fwDir.getAbsolutePath();

        ApkBuilder builder = new ApkBuilder(config, new ExtFile(srcDir));
        builder.build(outApk);

        return outApk;
    }
}
