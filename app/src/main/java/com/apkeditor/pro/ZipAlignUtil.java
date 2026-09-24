
package com.apkeditor.pro;

import java.io.*;
import java.util.zip.*;

/**
 * Zipalign sederhana: pastikan semua entry STORED align 4-byte.
 * Untuk hasil produksi, gunakan zipalign resmi.
 */
public class ZipAlignUtil {
    public static void align(File in, File out) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(in));
             ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(out))) {
            ZipEntry e;
            byte[] buf = new byte[8192];
            while ((e = zis.getNextEntry()) != null) {
                ZipEntry ne = new ZipEntry(e.getName());
                ne.setMethod(e.getMethod());
                ne.setTime(e.getTime());
                if (e.getMethod() == ZipEntry.STORED) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int n;
                    while ((n = zis.read(buf)) > 0) baos.write(buf, 0, n);
                    byte[] data = baos.toByteArray();
                    CRC32 crc = new CRC32();
                    crc.update(data);
                    ne.setSize(data.length);
                    ne.setCompressedSize(data.length);
                    ne.setCrc(crc.getValue());
                    zos.putNextEntry(ne);
                    zos.write(data);
                } else {
                    zos.putNextEntry(ne);
                    int n;
                    while ((n = zis.read(buf)) > 0) zos.write(buf, 0, n);
                }
                zos.closeEntry();
            }
        }
    }
}
