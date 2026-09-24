
package com.apkeditor.pro;

import android.content.Context;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ApktoolRunner {
    private final Context ctx;
    public ApktoolRunner(Context c) { this.ctx = c; }

    private File extractAsset(String name, String outName) throws IOException {
        File out = new File(ctx.getFilesDir(), outName);
        if (out.exists() && out.length() > 0) return out;
        try (InputStream in = ctx.getAssets().open(name);
             OutputStream os = new FileOutputStream(out)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
        }
        return out;
    }

    private String getJavaBin() {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            File f = new File(javaHome, "bin/java");
            if (f.exists()) return f.getAbsolutePath();
        }
        return "java";
    }

    public File decompile(File apk, File outDir) throws Exception {
        File apktoolJar = extractAsset("libs/apktool.jar", "apktool.jar");
        List<String> cmd = new ArrayList<>();
        cmd.add(getJavaBin());
        cmd.add("-jar");
        cmd.add(apktoolJar.getAbsolutePath());
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
        List<String> cmd = new ArrayList<>();
        cmd.add(getJavaBin());
        cmd.add("-jar");
        cmd.add(apktoolJar.getAbsolutePath());
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
        BufferedReader br = new BufferedReader(
            new InputStreamReader(p.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) sb.append(line).append("\n");
        int code = p.waitFor();
        if (code != 0) throw new RuntimeException(
            "Exit " + code + ":\n" + sb.toString());
    }
}
