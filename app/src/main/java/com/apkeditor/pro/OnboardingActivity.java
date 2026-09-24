package com.apkeditor.pro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Onboarding 3 slide, muncul sekali saat pertama buka app.
 * Disimpan di SharedPreferences "onboarding_done".
 */
public class OnboardingActivity extends AppCompatActivity {

    private static final String PREFS = "app_prefs";
    private static final String KEY_DONE = "onboarding_done";

    private static final String[][] SLIDES = {
        {"🎨", "Editor APK", "Buka & edit APK dengan mudah.\nDecompile, ubah, recompile."},
        {"🔨", "Builder", "Bikin APK dari WebView, HTML, atau template.\nCocok buat pemula."},
        {"📚", "Learn Code", "9 topik coding + kuis interaktif.\nBelajar bareng YsDev."},
    };

    private int current = 0;
    private LinearLayout root;
    private TextView emoji, title, desc;
    private Button btnNext;

    public static boolean shouldShow(android.content.Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS,
            android.content.Context.MODE_PRIVATE);
        return !p.getBoolean(KEY_DONE, false);
    }

    public static void markDone(android.content.Context ctx) {
        ctx.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_DONE, true).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(64, 64, 64, 64);
        root.setBackgroundColor(Color.parseColor("#FF121212"));

        emoji = new TextView(this);
        emoji.setTextSize(96);
        emoji.setGravity(Gravity.CENTER);
        root.addView(emoji);

        title = new TextView(this);
        title.setTextSize(28);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 32, 0, 16);
        root.addView(title);

        desc = new TextView(this);
        desc.setTextSize(16);
        desc.setTextColor(Color.parseColor("#B0B0B0"));
        desc.setGravity(Gravity.CENTER);
        root.addView(desc);

        btnNext = new Button(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 140);
        lp.setMargins(0, 64, 0, 0);
        btnNext.setLayoutParams(lp);
        btnNext.setText("Lanjut ➡");
        btnNext.setTextColor(Color.WHITE);
        btnNext.setBackgroundColor(Color.parseColor("#2196F3"));
        btnNext.setAllCaps(false);
        btnNext.setTextSize(16);
        btnNext.setOnClickListener(v -> next());
        root.addView(btnNext);

        setContentView(root);
        render();
    }

    private void render() {
        String[] s = SLIDES[current];
        emoji.setText(s[0]);
        title.setText(s[1]);
        desc.setText(s[2]);
        btnNext.setText(current == SLIDES.length - 1
            ? "Mulai 🚀" : "Lanjut ➡");
    }

    private void next() {
        if (current < SLIDES.length - 1) {
            current++;
            render();
        } else {
            markDone(this);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
