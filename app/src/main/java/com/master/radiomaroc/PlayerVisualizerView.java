package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/** Gold bars driven by the real audio output level captured by RadioService. */
public final class PlayerVisualizerView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float level;
    private boolean playing;
    private float phase;
    public PlayerVisualizerView(Context c) { super(c); init(); }
    public PlayerVisualizerView(Context c, AttributeSet a) { super(c, a); init(); }
    private void init(){ paint.setColor(0xFFF8D990); paint.setStrokeCap(Paint.Cap.ROUND); }
    public void setAudioLevel(float input, boolean isPlaying){ playing=isPlaying; float target=isPlaying?Math.max(.10f,Math.min(1f,input)):.05f; level=level*.48f+target*.52f; if(isPlaying) postInvalidateOnAnimation(); else invalidate(); }
    @Override protected void onDraw(Canvas c){ super.onDraw(c); float w=getWidth(),h=getHeight(); paint.setStrokeWidth(Math.max(2f,w/9f)); if(playing) phase+=.22f; for(int i=0;i<4;i++){float pulse=.34f+.66f*(float)Math.abs(Math.sin(phase+i*1.31f));float variation=.35f+.65f*pulse;float bar=Math.max(paint.getStrokeWidth(),h*(playing?Math.max(.16f,level)*variation:.10f));float x=w*(i+1)/5f;c.drawLine(x,(h-bar)/2f,x,(h+bar)/2f,paint);} if(playing) postInvalidateOnAnimation(); }
}
