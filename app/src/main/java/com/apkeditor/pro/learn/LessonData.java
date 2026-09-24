
package com.apkeditor.pro.learn;

import java.util.ArrayList;
import java.util.List;

public class LessonData {

    public static class Lesson {
        public String icon, title, description, content;
        public List<Question> quiz = new ArrayList<>();

        public Lesson(String icon, String title, String description, String content) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.content = content;
        }
    }

    public static class Question {
        public String question;
        public String[] options;
        public int correctIndex;

        public Question(String q, String[] o, int c) {
            question = q; options = o; correctIndex = c;
        }
    }

    public static List<Lesson> getAll() {
        List<Lesson> list = new ArrayList<>();

        // 1. Java Dasar
        Lesson java = new Lesson("☕", "Java Dasar",
            "Variabel, tipe data, perulangan, kondisi",
            "Java adalah bahasa pemrograman berorientasi objek.\n\n" +
            "📌 Variabel:\n" +
            "int umur = 25;\n" +
            "String nama = \"YsDev\";\n" +
            "boolean aktif = true;\n\n" +
            "📌 Perulangan:\n" +
            "for (int i = 0; i < 5; i++) {\n    System.out.println(i);\n}\n\n" +
            "📌 Kondisi:\n" +
            "if (umur >= 18) {\n    // dewasa\n} else {\n    // anak-anak\n}\n\n" +
            "📌 Method:\n" +
            "public int tambah(int a, int b) {\n    return a + b;\n}");
        java.quiz.add(new Question("Tipe data untuk bilangan bulat?", new String[]{"String","int","boolean","double"}, 1));
        java.quiz.add(new Question("Keyword perulangan?", new String[]{"if","switch","for","try"}, 2));
        java.quiz.add(new Question("Output System.out.println(2+3)?", new String[]{"23","5","2+3","error"}, 1));
        java.quiz.add(new Question("Deklarasi String yang benar?", new String[]{"String s = 'a';","String s = \"a\";","string s = \"a\";","s = String(a);"}, 1));
        java.quiz.add(new Question("Method yang mengembalikan nilai pakai?", new String[]{"void","return","break","continue"}, 1));
        list.add(java);

        // 2. Android Activity
        Lesson act = new Lesson("📱", "Android Activity",
            "Siklus hidup Activity & Intent",
            "Activity adalah satu layar di aplikasi Android.\n\n" +
            "📌 Siklus hidup:\n" +
            "onCreate() → onStart() → onResume() →\n" +
            "onPause() → onStop() → onDestroy()\n\n" +
            "📌 Intent:\n" +
            "Intent i = new Intent(this, MainActivity.class);\n" +
            "startActivity(i);\n\n" +
            "📌 Kirim data:\n" +
            "i.putExtra(\"nama\", \"YsDev\");\n" +
            "// di Activity tujuan:\n" +
            "String nama = getIntent().getStringExtra(\"nama\");");
        act.quiz.add(new Question("Method pertama dipanggil?", new String[]{"onStart","onCreate","onResume","onDestroy"}, 1));
        act.quiz.add(new Question("Untuk pindah Activity pakai?", new String[]{"Fragment","Intent","Service","Toast"}, 1));
        act.quiz.add(new Question("File layout Activity di folder?", new String[]{"src","res/layout","assets","libs"}, 1));
        act.quiz.add(new Question("Kirim data via Intent pakai?", new String[]{"putData","putExtra","setValue","addData"}, 1));
        act.quiz.add(new Question("Method untuk ambil data di Activity tujuan?", new String[]{"getExtra","getData","getIntent","getValue"}, 2));
        list.add(act);

        // 3. Layout XML
        Lesson layout = new Lesson("🎨", "Layout XML",
            "LinearLayout, ConstraintLayout, dll",
            "Layout mengatur tampilan UI di Android.\n\n" +
            "📌 LinearLayout (horizontal/vertical):\n" +
            "<LinearLayout\n    android:orientation=\"vertical\">\n    <TextView/>\n    <Button/>\n</LinearLayout>\n\n" +
            "📌 ConstraintLayout (paling fleksibel):\n" +
            "<TextView\n    app:layout_constraintTop_toTopOf=\"parent\"\n    app:layout_constraintStart_toStartOf=\"parent\"/>\n\n" +
            "📌 Atribut umum:\n" +
            "layout_width / layout_height (match_parent, wrap_content, dp)\n" +
            "padding, margin, gravity, background");
        layout.quiz.add(new Question("Layout paling fleksibel?", new String[]{"LinearLayout","FrameLayout","ConstraintLayout","RelativeLayout"}, 2));
        layout.quiz.add(new Question("Atribut lebar TextView?", new String[]{"width","layout_width","android:width","size"}, 1));
        layout.quiz.add(new Question("Untuk susun horizontal pakai?", new String[]{"LinearLayout+vertical","LinearLayout+horizontal","FrameLayout","GridLayout"}, 1));
        layout.quiz.add(new Question("Unit ukuran standar Android?", new String[]{"px","pt","dp/sp","cm"}, 2));
        layout.quiz.add(new Question("Teks di TextView pakai atribut?", new String[]{"text","label","value","content"}, 0));
        list.add(layout);

        // 4. Build APK
        Lesson build = new Lesson("🔨", "Build APK",
            "Gradle, signing, zipalign",
            "Proses build APK:\n\n" +
            "📌 Gradle:\n" +
            "./gradlew assembleDebug\n" +
            "./gradlew assembleRelease\n\n" +
            "📌 Signing:\n" +
            "Keystore (.jks) + apksigner\n" +
            "jarsigner -keystore key.jks app.apk alias\n\n" +
            "📌 Zipalign:\n" +
            "zipalign -v 4 in.apk out.apk\n\n" +
            "📌 Version di build.gradle:\n" +
            "versionCode 1\nversionName \"1.0\"");
        build.quiz.add(new Question("Perintah build debug?", new String[]{"gradlew buildDebug","gradlew assembleDebug","gradlew make","gradlew create"}, 1));
        build.quiz.add(new Question("File keystore berekstensi?", new String[]{".key",".jks",".pem",".sign"}, 1));
        build.quiz.add(new Question("Zipalign dilakukan?", new String[]{"sebelum sign","setelah sign","tidak penting","saat coding"}, 0));
        build.quiz.add(new Question("Version code di file?", new String[]{"AndroidManifest","build.gradle","settings.gradle","gradle.properties"}, 1));
        build.quiz.add(new Question("APK release harus di?", new String[]{"debug","signed","unsigned","dihapus"}, 1));
        list.add(build);

        // 5. WebView
        Lesson web = new Lesson("🌐", "WebView",
            "Menampilkan website di aplikasi",
            "WebView = browser mini di dalam app.\n\n" +
            "📌 Setup:\n" +
            "WebView wv = new WebView(this);\n" +
            "wv.getSettings().setJavaScriptEnabled(true);\n" +
            "wv.loadUrl(\"https://google.com\");\n\n" +
            "📌 Custom WebViewClient:\n" +
            "wv.setWebViewClient(new WebViewClient());\n\n" +
            "📌 Load HTML lokal:\n" +
            "wv.loadUrl(\"file:///android_asset/index.html\");\n\n" +
            "📌 Permission INTERNET wajib di Manifest!");
        web.quiz.add(new Question("Permission wajib WebView?", new String[]{"CAMERA","INTERNET","GPS","CONTACT"}, 1));
        web.quiz.add(new Question("Aktifkan JavaScript?", new String[]{"setJS(true)","setJavaScriptEnabled(true)","enableJS()","js.on()"}, 1));
        web.quiz.add(new Question("Load HTML lokal dari?", new String[]{"res","assets","libs","raw"}, 1));
        web.quiz.add(new Question("Class untuk handle link?", new String[]{"WebClient","WebViewClient","WebChrome","LinkHandler"}, 1));
        web.quiz.add(new Question("Method load URL?", new String[]{"openUrl","loadUrl","goUrl","getUrl"}, 1));
        list.add(web);

        // 6. Keamanan
        Lesson sec = new Lesson("🔐", "Keamanan",
            "Keystore, signing, permission",
            "Keamanan aplikasi Android:\n\n" +
            "📌 Keystore:\n" +
            "Buat sekali, simpan selamanya!\n" +
            "keytool -genkey -v -keystore key.jks ...\n\n" +
            "📌 Permission:\n" +
            "<uses-permission android:name=\"...\"/>\n\n" +
            "📌 Runtime permission (Android 6+):\n" +
            "if (checkSelfPermission(...) != GRANTED) {\n    requestPermissions(...);\n}\n\n" +
            "📌 ProGuard/R8:\n" +
            "Obfuscate kode biar susah dibaca");
        sec.quiz.add(new Question("Keystore sebaiknya?", new String[]{"diganti tiap build","disimpan aman","dihapus","dibagikan"}, 1));
        sec.quiz.add(new Question("Permission di file?", new String[]{"build.gradle","AndroidManifest","strings.xml","proguard"}, 1));
        sec.quiz.add(new Question("Runtime permission sejak Android?", new String[]{"4","5","6","7"}, 2));
        sec.quiz.add(new Question("Obfuscate pakai?", new String[]{"ProGuard","Zipalign","Apktool","Dex2jar"}, 0));
        sec.quiz.add(new Question("Hilang keystore = ?", new String[]{"tetap bisa update","tidak bisa update","APK hapus","bebas"}, 1));
        list.add(sec);

        // 7. Kotlin Dasar
        Lesson kt = new Lesson("🟣", "Kotlin Dasar",
            "Bahasa modern untuk Android",
            "Kotlin = bahasa resmi Android sejak 2017.\n\n" +
            "📌 Variabel:\n" +
            "val nama = \"YsDev\"   // immutable\n" +
            "var umur = 25        // mutable\n\n" +
            "📌 Function:\n" +
            "fun tambah(a: Int, b: Int): Int = a + b\n\n" +
            "📌 Null safety:\n" +
            "var x: String? = null\n" +
            "x?.length   // aman\n\n" +
            "📌 Data class:\n" +
            "data class User(val nama: String, val umur: Int)");
        kt.quiz.add(new Question("val vs var?", new String[]{"sama","val immutable","var immutable","tidak ada beda"}, 1));
        kt.quiz.add(new Question("Function di Kotlin pakai?", new String[]{"def","func","fun","function"}, 2));
        kt.quiz.add(new Question("Null safety operator?", new String[]{"?.","!!","?:","semua benar"}, 3));
        kt.quiz.add(new Question("Data class untuk?", new String[]{"database","model data","UI","network"}, 1));
        kt.quiz.add(new Question("Kotlin resmi Android sejak?", new String[]{"2015","2016","2017","2018"}, 2));
        list.add(kt);

        // 8. Git & GitHub
        Lesson git = new Lesson("🐙", "Git & GitHub",
            "Version control & kolaborasi",
            "Git = version control untuk tracking perubahan kode.\n\n" +
            "📌 Perintah dasar:\n" +
            "git init\ngit add .\ngit commit -m \"pesan\"\n" +
            "git push origin main\n\n" +
            "📌 Branch:\n" +
            "git checkout -b fitur-baru\ngit merge fitur-baru\n\n" +
            "📌 GitHub Actions:\n" +
            "CI/CD otomatis build APK di cloud");
        git.quiz.add(new Question("Perintah commit?", new String[]{"git save","git commit","git store","git push"}, 1));
        git.quiz.add(new Question("Buat branch baru?", new String[]{"git new","git branch -c","git checkout -b","git fork"}, 2));
        git.quiz.add(new Question("Push ke GitHub?", new String[]{"git send","git upload","git push","git sync"}, 2));
        git.quiz.add(new Question("GitHub Actions untuk?", new String[]{"chat","CI/CD","desain","database"}, 1));
        git.quiz.add(new Question("File daftar file yang di-ignore?", new String[]{".gitkeep",".gitignore","ignore.txt","skip.txt"}, 1));
        list.add(git);

        // 9. UI/UX Design
        Lesson ui = new Lesson("✨", "UI/UX Design",
            "Prinsip desain aplikasi",
            "Prinsip UI/UX yang baik:\n\n" +
            "📌 Konsisten:\n" +
            "Warna, font, spacing yang sama di seluruh app\n\n" +
            "📌 Hierarki visual:\n" +
            "Elemen penting lebih besar/terang\n\n" +
            "📌 Feedback:\n" +
            "Tombol memberi respon saat diklik\n\n" +
            "📌 Minimalis:\n" +
            "Buang yang tidak perlu\n\n" +
            "📌 Aksesibilitas:\n" +
            "Kontras cukup, font cukup besar");
        ui.quiz.add(new Question("Prinsip utama UI?", new String[]{"rumit","konsisten","acak","banyak warna"}, 1));
        ui.quiz.add(new Question("Hierarki visual artinya?", new String[]{"semua sama","elemen penting menonjol","acak","kecil semua"}, 1));
        ui.quiz.add(new Question("Feedback UI contoh?", new String[]{"diam","ripple saat klik","tidak ada respon","error"}, 1));
        ui.quiz.add(new Question("Aksesibilitas penting untuk?", new String[]{"developer","semua user","Google","bos"}, 1));
        ui.quiz.add(new Question("Warna kontras perlu?", new String[]{"tidak","iya untuk baca","hanya malam","bebas"}, 1));
        list.add(ui);

        return list;
    }
}
