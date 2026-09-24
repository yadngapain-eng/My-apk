
package com.apkeditor.pro;

import java.io.*;
import java.util.zip.*;

public class ZipUtil {
    public static void unzip(File zip, File outDir) throws IOException {
        if (!outDir.exists()) outDir.mkdirs();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry e;
            byte[] buf = new byte[8192];
            while ((e = zis.getNextEntry()) != null) {
                File f = new File(outDir, e.getName());
                if (e.isDirectory()) { f.mkdirs(); continue; }
                f.getParentFile().mkdirs();
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    int n;
                    while ((n = zis.read(buf)) > 0) fos.write(buf, 0, n);
                }
            }
        }
    }

    public static void zip(File dir, File outZip) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outZip))) {
            addDir(dir, dir, zos);
        }
    }

    private static void addDir(File root, File dir, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;
        byte[] buf = new byte[8192];
        for (File f : files) {
            if (f.isDirectory()) {
                addDir(root, f, zos);
            } else {
                String rel = root.toURI().relativize(f.toURI()).getPath();
                ZipEntry e = new ZipEntry(rel);
                zos.putNextEntry(e);
                try (FileInputStream fis = new FileInputStream(f)) {
                    int n;
                    while ((n = fis.read(buf)) > 0) zos.write(buf, 0, n);
                }
                zos.closeEntry();
            }
        }
    }
}
