package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/** Single coherent Moroccan background chosen for the app. */
public class MoroccanHeritageView extends View {
    private final Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
    private final Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect src = new Rect();
    private final RectF dst = new RectF();
    private Bitmap background;

    public MoroccanHeritageView(Context context) { super(context); init(); }
    public MoroccanHeritageView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public MoroccanHeritageView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        background = BitmapFactory.decodeResource(getResources(), R.drawable.moroccan_background);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (background == null || getWidth() <= 0 || getHeight() <= 0) return;

        float vw = getWidth();
        float vh = getHeight();
        float bw = background.getWidth();
        float bh = background.getHeight();
        float scale = Math.max(vw / bw, vh / bh);
        float sw = vw / scale;
        float sh = vh / scale;

        float centerX = bw * 0.50f;
        float centerY = bh * 0.48f;
        int left = Math.max(0, Math.round(centerX - sw / 2f));
        int top = Math.max(0, Math.round(centerY - sh / 2f));
        int right = Math.min(background.getWidth(), Math.round(left + sw));
        int bottom = Math.min(background.getHeight(), Math.round(top + sh));
        src.set(left, top, right, bottom);
        dst.set(0, 0, vw, vh);
        canvas.drawBitmap(background, src, dst, imagePaint);

        // Mild readability treatment only; the Moroccan scene remains clearly visible through the glass UI.
        overlayPaint.setShader(new LinearGradient(
                0, 0, 0, vh,
                new int[]{0x26000000, 0x10000000, 0x18000000, 0x32000000},
                new float[]{0f, .28f, .66f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, vw, vh, overlayPaint);
        overlayPaint.setShader(null);
    }
}
