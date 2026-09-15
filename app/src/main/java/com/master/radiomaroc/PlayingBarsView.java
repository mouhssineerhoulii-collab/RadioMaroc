package com.master.radiomaroc;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/** Small, lightweight live-audio indicator used inside the active station card. */
public final class PlayingBarsView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ValueAnimator animator;
    private float phase;

    public PlayingBarsView(Context context) { super(context); init(); }
    public PlayingBarsView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        paint.setColor(0xFFF8D990);
        paint.setStrokeCap(Paint.Cap.ROUND);
        setVisibility(GONE);
    }

    public void setPlaying(boolean playing) {
        if (playing) {
            setVisibility(VISIBLE);
            if (animator == null) {
                animator = ValueAnimator.ofFloat(0f, (float) (Math.PI * 2));
                animator.setDuration(820);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(a -> { phase = (float) a.getAnimatedValue(); invalidate(); });
            }
            if (!animator.isStarted()) animator.start();
        } else {
            if (animator != null) animator.cancel();
            setVisibility(GONE);
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth(), h = getHeight();
        paint.setStrokeWidth(Math.max(3f, w / 16f));
        for (int i = 0; i < 4; i++) {
            float x = w * (i + 1) / 5f;
            float amplitude = .25f + .65f * Math.abs((float) Math.sin(phase + i * 1.17f));
            float bar = Math.max(paint.getStrokeWidth(), h * amplitude);
            canvas.drawLine(x, (h - bar) / 2f, x, (h + bar) / 2f, paint);
        }
    }

    @Override protected void onDetachedFromWindow() {
        if (animator != null) animator.cancel();
        super.onDetachedFromWindow();
    }
}
