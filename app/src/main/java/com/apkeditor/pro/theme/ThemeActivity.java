
package com.apkeditor.pro.theme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apkeditor.pro.R;

public class ThemeActivity extends AppCompatActivity {

    private static final String PREFS = "theme_prefs";
    private static final String KEY_COLOR = "primary_color";

    private static final String[][] THEMES = {
        {"Blue",    "#2196F3"},
        {"Green",   "#4CAF50"},
        {"Purple",  "#9C27B0"},
        {"Orange",  "#FF9800"},
        {"Red",     "#F44336"},
        {"Teal",    "#009688"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Theme");
        toolbar.setBackgroundColor(getResources().getColor(R.color.background));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary));
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(v -> finish());
        root.addView(toolbar);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(48, 80, 48, 48);

        TextView label = new TextView(this);
        label.setText("🎨 Pilih warna utama aplikasi:");
        label.setTextColor(getResources().getColor(R.color.text_primary));
        label.setTextSize(16);
        label.setPadding(0, 0, 0, 32);
        content.addView(label);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String current = prefs.getString(KEY_COLOR, "#2196F3");

        for (String[] theme : THEMES) {
            content.addView(makeThemeRow(theme[0], theme[1], current, prefs));
        }

        scroll.addView(content);
        root.addView(scroll);
        setContentView(root);
    }

    private View makeThemeRow(String name, String hex, String current, SharedPreferences prefs) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 160);
        lp.setMargins(0, 12, 0, 12);
        row.setLayoutParams(lp);
        row.setBackground(getResources().getDrawable(R.drawable.bg_menu_button));
        row.setPadding(32, 0, 32, 0);

        View colorDot = new View(this);
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(80, 80);
        dotLp.setMargins(0, 0, 32, 0);
        colorDot.setLayoutParams(dotLp);
        colorDot.setBackgroundColor(android.graphics.Color.parseColor(hex));
        row.addView(colorDot);

        TextView tv = new TextView(this);
        tv.setText(name + (hex.equals(current) ? "  ✓" : ""));
        tv.setTextColor(getResources().getColor(R.color.text_primary));
        tv.setTextSize(16);
        row.addView(tv);

        row.setOnClickListener(v -> {
            prefs.edit().putString(KEY_COLOR, hex).apply();
            Toast.makeText(this, "Tema " + name + " dipilih. Restart app.", Toast.LENGTH_LONG).show();
        });
        return row;
    }
}
