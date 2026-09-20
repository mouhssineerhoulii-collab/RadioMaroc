package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/** Compact live-audio indicator for the active station card. */
public final class PlayingBarsView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private float target=.06f, level=.06f;
    private boolean playing;
    private long lastFrame;

    public PlayingBarsView(Context c){super(c);init();}
    public PlayingBarsView(Context c,AttributeSet a){super(c,a);init();}
    private void init(){paint.setColor(0xFFF8D990);paint.setStrokeCap(Paint.Cap.ROUND);setVisibility(GONE);}

    public void setAudioLevel(float value,boolean active){
        playing=active; target=active?Math.max(.07f,Math.min(1f,value)):.05f;
        setVisibility(active?VISIBLE:GONE);
        if(active)postInvalidateOnAnimation();else{level=.05f;invalidate();}
    }
    public void setPlaying(boolean active){setAudioLevel(active?.18f:0f,active);}

    @Override protected void onDraw(Canvas c){
        super.onDraw(c); if(!playing)return;
        float w=getWidth(),h=getHeight(); long now=System.nanoTime();
        float dt=lastFrame==0?.016f:Math.min(.05f,(now-lastFrame)/1_000_000_000f); lastFrame=now;
        float speed=target>level?10f:4.2f, alpha=1f-(float)Math.exp(-speed*dt); level+=(target-level)*alpha;
        paint.setStrokeWidth(Math.max(2f,w/14f)); float[] shape={.62f,.88f,1f,.76f,.54f};
        for(int i=0;i<5;i++){float x=w*(i+1)/6f;float bar=Math.max(paint.getStrokeWidth(),h*(.14f+level*.76f)*shape[i]);c.drawLine(x,(h-bar)/2f,x,(h+bar)/2f,paint);}
        postInvalidateOnAnimation();
    }
}
