package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Calm five-bar equalizer for the mini-player.
 * Audio capture and drawing are deliberately decoupled: incoming RMS samples only
 * update a target, while the view eases toward it at display-frame speed.
 */
public final class PlayerVisualizerView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float targetLevel = .08f;
    private float displayLevel = .08f;
    private boolean playing;
    private long lastFrameNanos;

    public PlayerVisualizerView(Context c) { super(c); init(); }
    public PlayerVisualizerView(Context c, AttributeSet a) { super(c, a); init(); }

    private void init() {
        paint.setColor(0xFFF8D990);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setAudioLevel(float input, boolean isPlaying) {
        playing = isPlaying;
        targetLevel = isPlaying ? Math.max(.08f, Math.min(1f, input)) : .06f;
        if (isPlaying) postInvalidateOnAnimation(); else { displayLevel = .06f; invalidate(); }
    }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        final float w=getWidth(), h=getHeight();
        if (w<=0 || h<=0) return;

        long now=System.nanoTime();
        float dt=lastFrameNanos==0 ? .016f : Math.min(.05f,(now-lastFrameNanos)/1_000_000_000f);
        lastFrameNanos=now;

        // Smooth attack, deliberately slower release: no nervous 75 ms jumps.
        float speed=targetLevel>displayLevel ? 11f : 4.8f;
        float alpha=1f-(float)Math.exp(-speed*dt);
        displayLevel += (targetLevel-displayLevel)*alpha;

        paint.setStrokeWidth(Math.max(2f,w/18f));
        final float[] shape={.58f,.82f,1f,.74f,.91f,.68f,.50f};
        for(int i=0;i<5;i++){
            float x=w*(i+1)/6f;
            float energy=.14f + displayLevel*.78f;
            float bar=Math.max(paint.getStrokeWidth(),h*energy*shape[i]);
            c.drawLine(x,(h-bar)/2f,x,(h+bar)/2f,paint);
        }
        if(playing) postInvalidateOnAnimation();
    }
}
