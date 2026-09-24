
package com.apkeditor.pro.learn;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apkeditor.pro.R;

public class LearnCodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Learn Code");
        toolbar.setBackgroundColor(getResources().getColor(R.color.background));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary));
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(v -> finish());
        root.addView(toolbar);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(48, 80, 48, 48);

        TextView intro = new TextView(this);
        intro.setText("📚 Belajar Coding Android\n\nPilih topik di bawah untuk mulai belajar:");
        intro.setTextColor(getResources().getColor(R.color.text_primary));
        intro.setTextSize(16);
        intro.setPadding(0, 0, 0, 32);
        content.addView(intro);

        String[][] topics = {
            {"☕  Java Dasar", "Variabel, tipe data, perulangan, kondisi"},
            {"📱  Android Activity", "Siklus hidup Activity & Intent"},
            {"🎨  Layout XML", "LinearLayout, ConstraintLayout, dll"},
            {"🔨  Build APK", "Gradle, signing, zipalign"},
            {"🌐  WebView", "Menampilkan website di aplikasi"},
            {"🔐  Keamanan", "Keystore, signing, permission"},
        };

        for (String[] t : topics) {
            TextView card = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 12, 0, 12);
            card.setLayoutParams(lp);
            card.setPadding(32, 32, 32, 32);
            card.setBackground(getResources().getDrawable(R.drawable.bg_menu_button));
            card.setText(t[0] + "\n" + t[1]);
            card.setTextColor(getResources().getColor(R.color.text_primary));
            card.setTextSize(14);
            card.setOnClickListener(v ->
                Toast.makeText(this, "Materi: " + t[0], Toast.LENGTH_SHORT).show());
            content.addView(card);
        }

        scroll.addView(content);
        root.addView(scroll);
        setContentView(root);
    }
}
