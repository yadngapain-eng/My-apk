package com.apkeditor.pro.webtoapk;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apkeditor.pro.R;

/**
 * Web to APK — input URL, preview via WebView, tombol convert (stub).
 * Layout dibuat programatik biar tidak perlu file XML tambahan.
 */
public class WebToApkActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));

        // Toolbar
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Web to APK");
        toolbar.setBackgroundColor(getResources().getColor(R.color.background));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary));
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(v -> finish());
        root.addView(toolbar);

        // Baris input
        LinearLayout inputRow = new LinearLayout(this);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);
        inputRow.setPadding(24, 24, 24, 12);

        EditText etUrl = new EditText(this);
        etUrl.setHint("https://example.com");
        etUrl.setTextColor(getResources().getColor(R.color.text_primary));
        etUrl.setHintTextColor(getResources().getColor(R.color.text_secondary));
        etUrl.setSingleLine(true);
        LinearLayout.LayoutParams etLp = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        etUrl.setLayoutParams(etLp);
        inputRow.addView(etUrl);

        Button btnGo = new Button(this);
        btnGo.setText("Buka");
        btnGo.setAllCaps(false);
        btnGo.setTextColor(Color.WHITE);
        btnGo.setBackgroundColor(Color.parseColor("#2196F3"));
        btnGo.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "Masukkan URL dulu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            webView.loadUrl(url);
        });
        inputRow.addView(btnGo);
        root.addView(inputRow);

        // WebView
        webView = new WebView(this);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams wvLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        webView.setLayoutParams(wvLp);
        root.addView(webView);

        // Baris bawah: tombol convert
        LinearLayout bottomRow = new LinearLayout(this);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setPadding(24, 12, 24, 24);
        bottomRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView info = new TextView(this);
        info.setText("Ubah website jadi APK (beta)");
        info.setTextColor(getResources().getColor(R.color.text_secondary));
        info.setTextSize(12);
        LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        info.setLayoutParams(infoLp);
        bottomRow.addView(info);

        Button btnConvert = new Button(this);
        btnConvert.setText("Convert");
        btnConvert.setAllCaps(false);
        btnConvert.setTextColor(Color.WHITE);
        btnConvert.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnConvert.setOnClickListener(v ->
            Toast.makeText(this,
                "Fitur convert dalam pengembangan",
                Toast.LENGTH_SHORT).show());
        bottomRow.addView(btnConvert);
        root.addView(bottomRow);

        setContentView(root);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }
}
