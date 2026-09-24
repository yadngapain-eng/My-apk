
package com.apkeditor.pro.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Banner dengan animasi stickman bertarung + background gunung.
 * Muncul/hilang setiap 7 detik (diatur di MainActivity).
 */
public class BannerView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float t = 0f;                    // 0..1 progress animasi
    private ValueAnimator animator;
    private int phase = 0;                   // 0=bertarung, 1=makan, 2=menari, 3=push-up
    private long lastPhaseChange = 0;
    private final float density;

    public BannerView(Context c) { super(c); density = c.getResources().getDisplayMetrics().density; init(); }
    public BannerView(Context c, AttributeSet a) { super(c, a); density = c.getResources().getDisplayMetrics().density; init(); }
    public BannerView(Context c, AttributeSet a, int s) { super(c, a, s); density = c.getResources().getDisplayMetrics().density; init(); }

    private void init() {
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(14 * density);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(4, 0, 2, Color.BLACK);

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1200);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            t = (float) a.getAnimatedValue();
            long now = System.currentTimeMillis();
            if (now - lastPhaseChange > 3500) {
                phase = (phase + 1) % 4;
                lastPhaseChange = now;
            }
            invalidate();
        });
        lastPhaseChange = System.currentTimeMillis();
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) animator.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();

        // === Background gradient langit ===
        LinearGradient sky = new LinearGradient(0, 0, 0, h,
                new int[]{Color.parseColor("#0D47A1"), Color.parseColor("#1976D2"),
                          Color.parseColor("#42A5F5")},
                null, Shader.TileMode.CLAMP);
        paint.setShader(sky);
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        // === Matahari ===
        paint.setColor(Color.parseColor("#FFD54F"));
        canvas.drawCircle(w * 0.85f, h * 0.25f, 20 * density, paint);
        // Glow
        paint.setColor(Color.parseColor("#40FFD54F"));
        canvas.drawCircle(w * 0.85f, h * 0.25f, 32 * density, paint);

        // === Gunung berlapis (parallax) ===
        drawMountain(canvas, w, h, 0.55f, "#0D3D22");  // belakang
        drawMountain(canvas, w, h, 0.70f, "#1B5E20");  // tengah
        drawMountain(canvas, w, h, 0.85f, "#2E7D32");  // depan

        // === Tanah ===
        paint.setColor(Color.parseColor("#33691E"));
        canvas.drawRect(0, h * 0.85f, w, h, paint);

        // === Stickman 1 (kiri) ===
        drawStickman(canvas, w * 0.30f, h * 0.75f, phase, t, false);
        // === Stickman 2 (kanan) ===
        drawStickman(canvas, w * 0.70f, h * 0.75f, (phase + 1) % 4, t, true);

        // === Teks banner ===
        String text = "BELAJAR CODING BARENG YsDev";
        float cx = w / 2f;
        float cy = h * 0.18f;
        // Kotak semi-transparan di belakang teks
        paint.setColor(Color.parseColor("#99000000"));
        RectF r = new RectF(cx - 150 * density, cy - 18 * density,
                            cx + 150 * density, cy + 10 * density);
        canvas.drawRoundRect(r, 12 * density, 12 * density, paint);
        canvas.drawText(text, cx, cy, textPaint);
    }

    private void drawMountain(Canvas c, int w, int h, float baseY, String colorHex) {
        paint.setColor(Color.parseColor(colorHex));
        Path p = new Path();
        p.moveTo(0, h * baseY);
        // Beberapa puncak
        p.lineTo(w * 0.15f, h * (baseY - 0.20f));
        p.lineTo(w * 0.30f, h * baseY);
        p.lineTo(w * 0.45f, h * (baseY - 0.28f));
        p.lineTo(w * 0.60f, h * baseY);
        p.lineTo(w * 0.75f, h * (baseY - 0.18f));
        p.lineTo(w * 0.90f, h * (baseY - 0.22f));
        p.lineTo(w, h * baseY);
        p.lineTo(w, h);
        p.lineTo(0, h);
        p.close();
        c.drawPath(p, paint);

        // Salju di puncak tengah
        if (baseY < 0.80f) {
            paint.setColor(Color.parseColor("#E0E0E0"));
            Path snow = new Path();
            snow.moveTo(w * 0.45f, h * (baseY - 0.28f));
            snow.lineTo(w * 0.42f, h * (baseY - 0.22f));
            snow.lineTo(w * 0.48f, h * (baseY - 0.22f));
            snow.close();
            c.drawPath(snow, paint);
        }
    }

    /** Gambar stickman dengan animasi bergantung phase. */
    private void drawStickman(Canvas c, float cx, float baseY, int ph,
                              float progress, boolean flip) {
        float d = density;
        float headR = 8 * d;
        float headY = baseY - 40 * d;

        // Warna stickman
        int c1 = flip ? Color.parseColor("#FF5252") : Color.parseColor("#40C4FF");
        paint.setColor(c1);
        paint.setStrokeWidth(3.5f * d);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Kepala
        paint.setStyle(Paint.Style.FILL);
        c.drawCircle(cx, headY, headR, paint);

        paint.setStyle(Paint.Style.STROKE);

        // Badan
        float bodyTop = headY + headR;
        float bodyBot = baseY - 15 * d;
        c.drawLine(cx, bodyTop, cx, bodyBot, paint);

        // Lengan & kaki dinamis
        float swing = (float) Math.sin(progress * Math.PI * 2) * 12 * d;
        float armY = headY + headR + 8 * d;

        switch (ph) {
            case 0: // Bertarung (punch)
                if (flip) {
                    c.drawLine(cx, armY, cx - 20*d, armY - swing*0.3f, paint);
                    c.drawLine(cx, armY, cx + 18*d, armY + 5*d, paint);
                } else {
                    c.drawLine(cx, armY, cx + 20*d, armY - swing*0.3f, paint);
                    c.drawLine(cx, armY, cx - 18*d, armY + 5*d, paint);
                }
                c.drawLine(cx, bodyBot, cx - 10*d, baseY, paint);
                c.drawLine(cx, bodyBot, cx + 10*d, baseY, paint);
                break;

            case 1: // Makan
                c.drawLine(cx, armY, cx - 10*d, armY + 10*d, paint);
                c.drawLine(cx, armY, cx + 10*d, armY - 10*d + swing*0.3f, paint);
                c.drawLine(cx, bodyBot, cx - 8*d, baseY, paint);
                c.drawLine(cx, bodyBot, cx + 8*d, baseY, paint);
                // Makanan (lingkaran kecil)
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#FFEB3B"));
                c.drawCircle(cx + (flip ? -12*d : 12*d), armY - 12*d + swing*0.3f, 3*d, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(c1);
                break;

            case 2: // Menari
                c.drawLine(cx, armY, cx - 15*d, armY - 10*d + swing*0.5f, paint);
                c.drawLine(cx, armY, cx + 15*d, armY - 10*d - swing*0.5f, paint);
                c.drawLine(cx, bodyBot, cx - 12*d + swing*0.3f, baseY, paint);
                c.drawLine(cx, bodyBot, cx + 12*d - swing*0.3f, baseY, paint);
                break;

            case 3: // Push-up
                c.drawLine(cx - 12*d, baseY, cx + 12*d, baseY - 5*d, paint);
                c.drawLine(cx - 12*d, baseY, cx - 15*d, armY + 20*d, paint);
                c.drawLine(cx, armY + swing*0.2f, cx, baseY - 3*d, paint);
                break;
        }
    }
}
