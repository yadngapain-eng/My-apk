
package com.apkeditor.pro.learn;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apkeditor.pro.R;

import java.util.List;

public class LearnCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLessonList();
    }

    private void showLessonList() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("📚 Learn Code");
        toolbar.setBackgroundColor(getResources().getColor(R.color.background));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary));
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(v -> finish());
        root.addView(toolbar);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(48, 48, 48, 48);

        TextView intro = new TextView(this);
        intro.setText("🎓 Belajar Coding Bareng YsDev\n\nPilih topik untuk mulai belajar + kuis:");
        intro.setTextColor(getResources().getColor(R.color.text_primary));
        intro.setTextSize(15);
        intro.setPadding(0, 0, 0, 32);
        content.addView(intro);

        List<LessonData.Lesson> lessons = LessonData.getAll();
        for (LessonData.Lesson l : lessons) {
            content.addView(makeLessonCard(l));
        }

        scroll.addView(content);
        root.addView(scroll);
        setContentView(root);
    }

    private View makeLessonCard(LessonData.Lesson lesson) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 12, 0, 12);
        card.setLayoutParams(lp);
        card.setBackground(getResources().getDrawable(R.drawable.bg_menu_button));

        TextView title = new TextView(this);
        title.setText(lesson.icon + "  " + lesson.title);
        title.setTextColor(getResources().getColor(R.color.text_primary));
        title.setTextSize(17);
        title.setPadding(0, 0, 0, 8);
        card.addView(title);

        TextView desc = new TextView(this);
        desc.setText(lesson.description);
        desc.setTextColor(getResources().getColor(R.color.text_secondary));
        desc.setTextSize(13);
        card.addView(desc);

        TextView quizInfo = new TextView(this);
        quizInfo.setText("\n📝 Kuis: " + lesson.quiz.size() + " soal");
        quizInfo.setTextColor(Color.parseColor("#4CAF50"));
        quizInfo.setTextSize(12);
        card.addView(quizInfo);

        card.setOnClickListener(v -> showLessonDetail(lesson));
        return card;
    }

    private void showLessonDetail(LessonData.Lesson lesson) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.background));

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle(lesson.icon + " " + lesson.title);
        toolbar.setBackgroundColor(getResources().getColor(R.color.background));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary));
        toolbar.setNavigationIcon(android.R.drawable.ic_media_previous);
        toolbar.setNavigationOnClickListener(v -> showLessonList());
        root.addView(toolbar);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(48, 48, 48, 48);

        // Materi
        TextView materi = new TextView(this);
        materi.setText(lesson.content);
        materi.setTextColor(getResources().getColor(R.color.text_primary));
        materi.setTextSize(14);
        materi.setTypeface(android.graphics.Typeface.MONOSPACE);
        materi.setPadding(24, 24, 24, 24);
        materi.setBackground(getResources().getDrawable(R.drawable.bg_menu_button));
        content.addView(materi);

        // Tombol Kuis
        android.widget.Button btnQuiz = new android.widget.Button(this);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 140);
        blp.setMargins(0, 32, 0, 0);
        btnQuiz.setLayoutParams(blp);
        btnQuiz.setText("🎯 Mulai Kuis (" + lesson.quiz.size() + " soal)");
        btnQuiz.setTextColor(Color.WHITE);
        btnQuiz.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnQuiz.setTextSize(16);
        btnQuiz.setAllCaps(false);
        btnQuiz.setOnClickListener(v -> startQuiz(lesson));
        content.addView(btnQuiz);

        scroll.addView(content);
        root.addView(scroll);
        setContentView(root);
    }

    private void startQuiz(LessonData.Lesson lesson) {
        QuizActivity.start(this, lesson);
    }
}
