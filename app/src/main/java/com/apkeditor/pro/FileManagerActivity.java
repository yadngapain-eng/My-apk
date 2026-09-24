package com.apkeditor.pro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * File manager sederhana: browse folder di externalFilesDir.
 * Bisa dipakai untuk lihat hasil decompile, output, dsb.
 */
public class FileManagerActivity extends AppCompatActivity {

    private LinearLayout content;
    private TextView tvPath;
    private File currentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("File Manager");
        toolbar.setBackgroundColor(getResources().getColor(R.color.background));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary));
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(v -> finish());
        root.addView(toolbar);

        // Baris path + tombol up
        LinearLayout pathRow = new LinearLayout(this);
        pathRow.setOrientation(LinearLayout.HORIZONTAL);
        pathRow.setPadding(24, 16, 24, 16);
        pathRow.setGravity(Gravity.CENTER_VERTICAL);

        Button btnUp = new Button(this);
        btnUp.setText("⬆");
        btnUp.setAllCaps(false);
        btnUp.setTextColor(Color.WHITE);
        btnUp.setBackgroundColor(Color.parseColor("#757575"));
        btnUp.setOnClickListener(v -> goUp());
        pathRow.addView(btnUp);

        tvPath = new TextView(this);
        tvPath.setTextColor(getResources().getColor(R.color.text_secondary));
        tvPath.setTextSize(12);
        tvPath.setPadding(16, 0, 0, 0);
        LinearLayout.LayoutParams pathLp = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tvPath.setLayoutParams(pathLp);
        pathRow.addView(tvPath);

        root.addView(pathRow);

        // Konten
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(24, 0, 24, 24);
        scroll.addView(content);
        root.addView(scroll);

        setContentView(root);

        // Mulai dari externalFilesDir
        currentDir = getExternalFilesDir(null);
        if (currentDir == null) currentDir = getFilesDir();
        listDir(currentDir);
    }

    private void listDir(File dir) {
        currentDir = dir;
        tvPath.setText(dir.getAbsolutePath());
        content.removeAllViews();

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            TextView empty = new TextView(this);
            empty.setText("(kosong)");
            empty.setTextColor(getResources().getColor(R.color.text_secondary));
            empty.setPadding(0, 24, 0, 0);
            content.addView(empty);
            return;
        }

        Arrays.sort(files, (a, b) -> {
            if (a.isDirectory() && !b.isDirectory()) return -1;
            if (!a.isDirectory() && b.isDirectory()) return 1;
            return a.getName().compareToIgnoreCase(b.getName());
        });

        for (File file : files) {
            content.addView(makeRow(file));
        }
    }

    private View makeRow(File file) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 140);
        lp.setMargins(0, 8, 0, 8);
        row.setLayoutParams(lp);
        row.setBackground(getResources().getDrawable(R.drawable.bg_menu_button));
        row.setPadding(32, 0, 32, 0);

        TextView icon = new TextView(this);
        icon.setText(file.isDirectory() ? "📁" : "📄");
        icon.setTextSize(20);
        icon.setPadding(0, 0, 16, 0);
        row.addView(icon);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        info.setLayoutParams(infoLp);

        TextView name = new TextView(this);
        name.setText(file.getName());
        name.setTextColor(getResources().getColor(R.color.text_primary));
        name.setTextSize(14);
        info.addView(name);

        TextView meta = new TextView(this);
        String sizeStr = file.isDirectory() ? "folder" : formatSize(file.length());
        String dateStr = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US)
            .format(new Date(file.lastModified()));
        meta.setText(sizeStr + "  •  " + dateStr);
        meta.setTextColor(getResources().getColor(R.color.text_secondary));
        meta.setTextSize(11);
        info.addView(meta);

        row.addView(info);

        row.setOnClickListener(v -> {
            if (file.isDirectory()) {
                listDir(file);
            } else {
                Toast.makeText(this, file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
            }
        });
        return row;
    }

    private void goUp() {
        if (currentDir == null) return;
        File parent = currentDir.getParentFile();
        File root = getExternalFilesDir(null);
        if (root == null) root = getFilesDir();
        // Jangan keluar dari sandbox externalFilesDir
        if (parent != null && currentDir.getAbsolutePath()
                .startsWith(root.getAbsolutePath())) {
            listDir(parent);
        } else {
            Toast.makeText(this, "Sudah di root", Toast.LENGTH_SHORT).show();
        }
    }

    private static String formatSize(long size) {
        if (size <= 0) return "0 B";
        String[] u = {"B", "KB", "MB", "GB"};
        int d = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, d))
            + " " + u[d];
    }
}
