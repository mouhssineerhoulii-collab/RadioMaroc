package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Unified Moroccan art direction for the app background.
 * One coherent composition: zellige-inspired geometry, Andalusian arch,
 * Atlas silhouettes and Saharan warmth, all blended into a single scene.
 * No photographs and no collage bands.
 */
public class MoroccanHeritageView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final RectF oval = new RectF();
    private float d;

    public MoroccanHeritageView(Context context) { super(context); init(); }
    public MoroccanHeritageView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public MoroccanHeritageView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        d = getResources().getDisplayMetrics().density;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) return;

        drawBase(canvas, w, h);
        drawLight(canvas, w, h);
        drawAndalusianFrame(canvas, w, h);
        drawAtlas(canvas, w, h);
        drawSahara(canvas, w, h);
        drawZelligeAccents(canvas, w, h);
        drawVignette(canvas, w, h);
    }

    private void drawBase(Canvas c, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(
                0, 0, 0, h,
                new int[]{
                        0xFF071E2C,
                        0xFF0A3445,
                        0xFF0A4B53,
                        0xFF14585A,
                        0xFF6B4A34,
                        0xFFB46B34,
                        0xFF3E271F
                },
                new float[]{0f, .16f, .34f, .50f, .68f, .83f, 1f},
                Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
    }

    private void drawLight(Canvas c, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                w * .72f, h * .19f, w * .72f,
                new int[]{0x66FFD98A, 0x28F39A4C, 0x00000000},
                new float[]{0f, .42f, 1f},
                Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h * .58f, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(
                w * .18f, h * .48f, w * .60f,
                new int[]{0x4437D4C5, 0x11125D65, 0x00000000},
                null,
                Shader.TileMode.CLAMP));
        c.drawRect(0, h * .12f, w, h * .72f, paint);
        paint.setShader(null);
    }

    private void drawAndalusianFrame(Canvas c, float w, float h) {
        float cx = w * .50f;
        float top = h * .045f;
        float side = w * .08f;
        float archBottom = h * .36f;

        path.reset();
        path.moveTo(side, archBottom);
        path.lineTo(side, top + h * .12f);
        path.cubicTo(side, top + h * .025f, cx - w * .18f, top, cx, top + h * .07f);
        path.cubicTo(cx + w * .18f, top, w - side, top + h * .025f, w - side, top + h * .12f);
        path.lineTo(w - side, archBottom);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1.5f));
        paint.setColor(0x55F8D990);
        paint.setShadowLayer(dp(10), 0, 0, 0x663D240C);
        c.drawPath(path, paint);
        paint.clearShadowLayer();

        paint.setStrokeWidth(dp(.7f));
        paint.setColor(0x44E7C478);
        path.offset(0, dp(8));
        c.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawAtlas(Canvas c, float w, float h) {
        float base = h * .69f;

        path.reset();
        path.moveTo(0, base + h * .07f);
        path.lineTo(0, base + h * .015f);
        path.lineTo(w * .09f, base - h * .025f);
        path.lineTo(w * .18f, base - h * .075f);
        path.lineTo(w * .27f, base - h * .040f);
        path.lineTo(w * .39f, base - h * .125f);
        path.lineTo(w * .49f, base - h * .055f);
        path.lineTo(w * .61f, base - h * .155f);
        path.lineTo(w * .73f, base - h * .060f);
        path.lineTo(w * .84f, base - h * .110f);
        path.lineTo(w, base - h * .025f);
        path.lineTo(w, base + h * .10f);
        path.close();

        paint.setShader(new LinearGradient(
                0, base - h * .17f, 0, base + h * .10f,
                new int[]{0xCC173943, 0xD90A2B32, 0xF0061D25},
                null,
                Shader.TileMode.CLAMP));
        c.drawPath(path, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1));
        paint.setColor(0x5538D1C4);
        c.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawSahara(Canvas c, float w, float h) {
        float y = h * .79f;

        path.reset();
        path.moveTo(0, y + h * .09f);
        path.cubicTo(w * .15f, y - h * .035f, w * .31f, y - h * .01f, w * .46f, y + h * .035f);
        path.cubicTo(w * .60f, y + h * .075f, w * .71f, y - h * .055f, w * .86f, y - h * .005f);
        path.cubicTo(w * .93f, y + h * .015f, w * .98f, y + h * .01f, w, y - h * .01f);
        path.lineTo(w, h);
        path.lineTo(0, h);
        path.close();

        paint.setShader(new LinearGradient(
                0, y - h * .08f, 0, h,
                new int[]{0x00E89B4F, 0xB6D1783B, 0xE2914D2D, 0xF03E251D},
                new float[]{0f, .24f, .60f, 1f},
                Shader.TileMode.CLAMP));
        c.drawPath(path, paint);
        paint.setShader(null);

        path.reset();
        path.moveTo(0, y + h * .11f);
        path.cubicTo(w * .18f, y + h * .04f, w * .34f, y + h * .10f, w * .52f, y + h * .07f);
        path.cubicTo(w * .67f, y + h * .04f, w * .79f, y + h * .12f, w, y + h * .055f);
        path.lineTo(w, h);
        path.lineTo(0, h);
        path.close();
        paint.setColor(0x8A7C3E2C);
        c.drawPath(path, paint);
    }

    private void drawZelligeAccents(Canvas c, float w, float h) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(.85f));

        float cell = dp(22);
        drawLatticeCorner(c, dp(10), h * .10f, cell, 5, 4, 0x6658C9D4, 0x55F1B44B);
        drawLatticeCorner(c, w - dp(118), h * .88f, cell, 5, 4, 0x667ACB9B, 0x55D84D46);

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawLatticeCorner(Canvas c, float startX, float startY, float cell, int cols, int rows, int colorA, int colorB) {
        for (int r = 0; r < rows; r++) {
            for (int col = 0; col < cols; col++) {
                float cx = startX + col * cell;
                float cy = startY + r * cell;
                paint.setColor(((r + col) & 1) == 0 ? colorA : colorB);
                drawTile(c, cx, cy, cell * .38f);
            }
        }
    }

    private void drawTile(Canvas c, float cx, float cy, float r) {
        path.reset();
        for (int i = 0; i < 8; i++) {
            double a = Math.PI / 4.0 * i - Math.PI / 8.0;
            float x = cx + (float) Math.cos(a) * r;
            float y = cy + (float) Math.sin(a) * r;
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.close();
        c.drawPath(path, paint);

        oval.set(cx - r * .42f, cy - r * .42f, cx + r * .42f, cy + r * .42f);
        c.drawOval(oval, paint);
    }

    private void drawVignette(Canvas c, float w, float h) {
        paint.setShader(new RadialGradient(
                w * .5f, h * .46f, Math.max(w, h) * .70f,
                new int[]{0x00000000, 0x12000000, 0x66000000},
                new float[]{0f, .62f, 1f},
                Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
    }

    private float dp(float v) { return v * d; }
}
