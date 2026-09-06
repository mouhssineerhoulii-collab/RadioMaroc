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
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/** Uses the selected Moroccan rooftop artwork as the single app background. */
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
        background = loadEmbeddedBackground();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private Bitmap loadEmbeddedBackground() {
        try (InputStream in = getResources().openRawResource(R.raw.selected_background_base64);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            String encoded = out.toString("UTF-8").replaceAll("\\s+", "");
            byte[] imageBytes = Base64.decode(encoded, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception ignored) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.moroccan_background);
        }
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

        overlayPaint.setShader(new LinearGradient(
                0, 0, 0, vh,
                new int[]{0x16000000, 0x08000000, 0x10000000, 0x26000000},
                new float[]{0f, .28f, .68f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, vw, vh, overlayPaint);
        overlayPaint.setShader(null);
    }
}
