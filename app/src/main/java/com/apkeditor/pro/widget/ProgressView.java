package com.apkeditor.pro.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Dialog progress custom dengan ProgressBar horizontal + persen.
 */
public class ProgressView {

    private final Dialog dialog;
    private final ProgressBar bar;
    private final TextView tvMsg, tvPct;

    public ProgressView(Context ctx, String title) {
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(48, 48, 48, 48);
        root.setBackgroundColor(Color.parseColor("#EE1E1E1E"));

        TextView tvTitle = new TextView(ctx);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(16);
        tvTitle.setPadding(0, 0, 0, 24);
        root.addView(tvTitle);

        bar = new ProgressBar(ctx, null,
            android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setProgress(0);
        root.addView(bar);

        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 16, 0, 0);

        tvMsg = new TextView(ctx);
        tvMsg.setText("Memproses...");
        tvMsg.setTextColor(Color.parseColor("#B0B0B0"));
        tvMsg.setTextSize(12);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tvMsg.setLayoutParams(lp);
        row.addView(tvMsg);

        tvPct = new TextView(ctx);
        tvPct.setText("0%");
        tvPct.setTextColor(Color.WHITE);
        tvPct.setTextSize(12);
        row.addView(tvPct);

        root.addView(row);

        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    public void show() { dialog.show(); }
    public void dismiss() { dialog.dismiss(); }

    public void setProgress(int pct) {
        bar.setProgress(pct);
        tvPct.setText(pct + "%");
    }

    public void setMessage(String msg) {
        tvMsg.setText(msg);
    }
}
