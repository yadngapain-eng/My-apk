
package com.apkeditor.pro.learn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apkeditor.pro.R;

public class QuizActivity extends AppCompatActivity {

    private static final String EXTRA_LESSON = "lesson_index";
    private LessonData.Lesson lesson;
    private int currentQ = 0;
    private int score = 0;
    private int selectedOption = -1;
    private TextView tvProgress, tvQuestion;
    private LinearLayout optionsContainer;
    private Button btnNext;

    public static void start(Context ctx, LessonData.Lesson lesson) {
        // Cari index lesson — panggil getAll() sekali saja
        int idx = 0;
        java.util.List<LessonData.Lesson> all = LessonData.getAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).title.equals(lesson.title)) { idx = i; break; }
        }
        Intent i = new Intent(ctx, QuizActivity.class);
        i.putExtra(EXTRA_LESSON, idx);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int idx = getIntent().getIntExtra(EXTRA_LESSON, 0);
        lesson = LessonData.getAll().get(idx);
        showQuestion();
    }

    private void showQuestion() {
        if (currentQ >= lesson.quiz.size()) {
            showResult();
            return;
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("🎯 Kuis: " + lesson.title);
        toolbar.setBackgroundColor(getResources().getColor(R.color.background));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary));
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(v -> finish());
        root.addView(toolbar);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(48, 48, 48, 48);

        tvProgress = new TextView(this);
        tvProgress.setText("Soal " + (currentQ + 1) + " dari " + lesson.quiz.size()
                + "   •   Skor: " + score);
        tvProgress.setTextColor(getResources().getColor(R.color.text_secondary));
        tvProgress.setTextSize(13);
        content.addView(tvProgress);

        tvQuestion = new TextView(this);
        tvQuestion.setText("\n" + lesson.quiz.get(currentQ).question);
        tvQuestion.setTextColor(getResources().getColor(R.color.text_primary));
        tvQuestion.setTextSize(18);
        tvQuestion.setPadding(0, 24, 0, 32);
        content.addView(tvQuestion);

        optionsContainer = new LinearLayout(this);
        optionsContainer.setOrientation(LinearLayout.VERTICAL);
        content.addView(optionsContainer);

        String[] opts = lesson.quiz.get(currentQ).options;
        for (int i = 0; i < opts.length; i++) {
            final int idx = i;
            Button b = new Button(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 140);
            lp.setMargins(0, 8, 0, 8);
            b.setLayoutParams(lp);
            b.setText((char)('A' + i) + ".  " + opts[i]);
            b.setTextColor(getResources().getColor(R.color.text_primary));
            b.setBackground(getResources().getDrawable(R.drawable.bg_menu_button));
            b.setTextSize(15);
            b.setAllCaps(false);
            b.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            b.setPadding(32, 0, 32, 0);
            b.setOnClickListener(v -> selectOption(idx));
            optionsContainer.addView(b);
        }

        btnNext = new Button(this);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 140);
        blp.setMargins(0, 32, 0, 0);
        btnNext.setLayoutParams(blp);
        btnNext.setText("Lanjut ➡");
        btnNext.setTextColor(Color.WHITE);
        btnNext.setBackgroundColor(Color.parseColor("#2196F3"));
        btnNext.setAllCaps(false);
        btnNext.setTextSize(16);
        btnNext.setEnabled(false);
        btnNext.setOnClickListener(v -> {
            currentQ++;
            selectedOption = -1;
            showQuestion();
        });
        content.addView(btnNext);

        scroll.addView(content);
        root.addView(scroll);
        setContentView(root);
    }

    private void selectOption(int idx) {
        if (selectedOption != -1) return; // sudah pilih
        selectedOption = idx;
        int correct = lesson.quiz.get(currentQ).correctIndex;

        // Warnai jawaban
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            Button b = (Button) optionsContainer.getChildAt(i);
            if (i == correct) {
                b.setBackgroundColor(Color.parseColor("#4CAF50"));
            } else if (i == idx) {
                b.setBackgroundColor(Color.parseColor("#F44336"));
            }
        }
        if (idx == correct) score++;

        tvProgress.setText("Soal " + (currentQ + 1) + " dari " + lesson.quiz.size()
                + "   •   Skor: " + score);
        btnNext.setEnabled(true);
    }

    private void showResult() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(getResources().getColor(R.color.background));
        root.setPadding(48, 48, 48, 48);

        TextView emoji = new TextView(this);
        int pct = (score * 100) / lesson.quiz.size();
        if (pct >= 80) emoji.setText("🏆");
        else if (pct >= 60) emoji.setText("👍");
        else emoji.setText("📚");
        emoji.setTextSize(64);
        emoji.setGravity(Gravity.CENTER);
        root.addView(emoji);

        TextView tv = new TextView(this);
        tv.setText("\n\nKuis Selesai!\n\nSkor: " + score + " / " + lesson.quiz.size()
                + "\n(" + pct + "%)");
        tv.setTextColor(getResources().getColor(R.color.text_primary));
        tv.setTextSize(24);
        tv.setGravity(Gravity.CENTER);
        root.addView(tv);

        Button retry = new Button(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 140);
        lp.setMargins(0, 64, 0, 0);
        retry.setLayoutParams(lp);
        retry.setText("🔄 Ulangi Kuis");
        retry.setTextColor(Color.WHITE);
        retry.setBackgroundColor(Color.parseColor("#2196F3"));
        retry.setAllCaps(false);
        retry.setTextSize(16);
        retry.setOnClickListener(v -> {
            currentQ = 0; score = 0; selectedOption = -1;
            showQuestion();
        });
        root.addView(retry);

        Button back = new Button(this);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 140);
        lp2.setMargins(0, 16, 0, 0);
        back.setLayoutParams(lp2);
        back.setText("⬅ Kembali ke Materi");
        back.setTextColor(Color.WHITE);
        back.setBackgroundColor(Color.parseColor("#757575"));
        back.setAllCaps(false);
        back.setTextSize(16);
        back.setOnClickListener(v -> finish());
        root.addView(back);

        setContentView(root);
    }
}
