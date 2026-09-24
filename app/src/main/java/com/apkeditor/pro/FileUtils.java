
package com.apkeditor.pro;

import android.content.Context;
import android.net.Uri;
import java.io.*;

public class FileUtils {
    public static void copyUriToFile(Context ctx, Uri uri, File out) throws IOException {
        try (InputStream in = ctx.getContentResolver().openInputStream(uri);
             OutputStream os = new FileOutputStream(out)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
        }
    }

    public static String read(File f) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String l;
            while ((l = br.readLine()) != null) sb.append(l).append("\n");
        }
        return sb.toString();
    }

    public static void write(File f, String s) throws IOException {
        try (FileWriter w = new FileWriter(f)) { w.write(s); }
    }
}
