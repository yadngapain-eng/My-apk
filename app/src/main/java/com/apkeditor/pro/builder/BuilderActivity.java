
package com.apkeditor.pro.builder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apkeditor.pro.R;
import com.apkeditor.pro.webtoapk.WebToApkActivity;

public class BuilderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout dibuat programatik untuk kesederhanaan
        android.widget.LinearLayout root = new android.widget.LinearLayout(this);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));
        root.setPadding(0, 0, 0, 0);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Builder");
        toolbar.setBackgroundColor(getResources().getColor(R.color.background));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary));
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(v -> finish());
        root.addView(toolbar);

        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        android.widget.LinearLayout content = new android.widget.LinearLayout(this);
        content.setOrientation(android.widget.LinearLayout.VERTICAL);
        content.setPadding(48, 80, 48, 48);

        content.addView(makeButton("🌐  Web to APK", v ->
            startActivity(new Intent(this, WebToApkActivity.class))));

        content.addView(makeButton("📱  HTML to APK", v ->
            Toast.makeText(this, "Fitur dalam pengembangan", Toast.LENGTH_SHORT).show()));

        content.addView(makeButton("🔨  Build from Template", v ->
            Toast.makeText(this, "Fitur dalam pengembangan", Toast.LENGTH_SHORT).show()));

        content.addView(makeButton("📝  Edit AndroidManifest", v ->
            Toast.makeText(this, "Fitur dalam pengembangan", Toast.LENGTH_SHORT).show()));

        scroll.addView(content);
        root.addView(scroll);
        setContentView(root);
    }

    private Button makeButton(String text, android.view.View.OnClickListener listener) {
        Button b = new Button(this);
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 160);
        lp.setMargins(0, 12, 0, 12);
        b.setLayoutParams(lp);
        b.setText(text);
        b.setTextColor(getResources().getColor(R.color.text_primary));
        b.setBackground(getResources().getDrawable(R.drawable.bg_menu_button));
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setOnClickListener(listener);
        return b;
    }
}
