package com.example.pomoz.views;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class SemiCircleProgressView extends View {

    private int progress = 0;
    private int currentStep = 1;
    private int maxStep = 5;

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint glowPaint;
    private Paint textPaint;
    private Paint textGlowPaint;
    private Drawable tokenDrawable;
    private int tokenSize = 60; // wielkość żetonu w px


    private final RectF arcRect = new RectF();

    public SemiCircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(LAYER_TYPE_SOFTWARE, null); // ✅ Glow działa tylko w Software Layer

        // tło półokręgu
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.DKGRAY);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(35f);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        // pasek progress
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.GREEN);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(35f);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // glow dla progress
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setColor(Color.GREEN);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(50f);
        glowPaint.setStrokeCap(Paint.Cap.ROUND);
        glowPaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL));

        // tekst zwykły (opcjonalnie czarny kontrast)
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(60f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // neonowy glow dla tekstu
        textGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textGlowPaint.setColor(Color.GREEN);
        textGlowPaint.setTextSize(60f);
        textGlowPaint.setTextAlign(Paint.Align.CENTER);
        textGlowPaint.setStyle(Paint.Style.FILL);
        textGlowPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int size = Math.min(getWidth(), getHeight());
        float radius = size / 2f - 50f;

        arcRect.set(
                getWidth() / 2f - radius,
                getHeight() / 2f - radius,
                getWidth() / 2f + radius,
                getHeight() / 2f + radius
        );

        // tło
        canvas.drawArc(arcRect, 180f, 180f, false, backgroundPaint);

        // progress
        float sweepAngle = (progress / 100f) * 180f;
        canvas.drawArc(arcRect, 180f, sweepAngle, false, glowPaint);
        canvas.drawArc(arcRect, 180f, sweepAngle, false, progressPaint);

        // tekst neonowy
        String stepText = currentStep + "/" + maxStep;
        if (tokenDrawable != null) {
            float textWidth = textPaint.measureText(stepText);

            // pozycja żetonu obok tekstu
            float cx = getWidth() / 2f + textWidth / 2f + 10f;
            float cy = getHeight() / 2f + 25f - (tokenSize / 2f);

            tokenDrawable.setBounds(
                    (int) cx,
                    (int) cy,
                    (int) (cx + tokenSize),
                    (int) (cy + tokenSize)
            );
            tokenDrawable.draw(canvas);
        }


        // najpierw glow
        canvas.drawText(stepText,
                getWidth() / 2f,
                getHeight() / 2f + 40f,
                textGlowPaint);

        // potem biały kontrast na wierzchu
        canvas.drawText(stepText,
                getWidth() / 2f,
                getHeight() / 2f + 40f,
                textPaint);
    }

    public void setProgressSteps(int current, int max) {
        this.currentStep = current;
        this.maxStep = max;
        this.progress = (int) ((float) current / max * 100f);
        invalidate();
    }
    public void setTokenDrawable(int drawableRes) {
        tokenDrawable = getResources().getDrawable(drawableRes, null);
        invalidate();
    }

}
