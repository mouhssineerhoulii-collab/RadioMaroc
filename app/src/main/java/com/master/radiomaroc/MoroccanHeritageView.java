package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Decorative Moroccan heritage layer drawn entirely on-device.
 * It keeps the photographic background and adds zellige, Atlas,
 * Saharan, Amazigh and Andalusian visual cues behind the glass UI.
 */
public class MoroccanHeritageView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private float d;

    private static final int COBALT = 0xFF1457C5;
    private static final int TURQUOISE = 0xFF00A79D;
    private static final int GREEN = 0xFF0B7A4B;
    private static final int RED = 0xFFC52D2F;
    private static final int SAFFRON = 0xFFF0A51A;
    private static final int IVORY = 0xFFFFF1CF;
    private static final int GOLD = 0xFFE4B64F;
    private static final int SAND = 0xFFD88B3A;

    public MoroccanHeritageView(Context context) { super(context); init(); }
    public MoroccanHeritageView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public MoroccanHeritageView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        d = getResources().getDisplayMetrics().density;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        float w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;

        drawZelligeBand(c, 0, h * .115f, w, dp(54));
        drawSideZellige(c, w, h);
        drawAndalusianArches(c, w, h);
        drawAtlas(c, w, h);
        drawDunes(c, w, h);
        drawAmazighMotifs(c, w, h);
        drawBottomZellige(c, w, h);
    }

    private void drawZelligeBand(Canvas c, float left, float top, float width, float height) {
        int[] colors = {COBALT, TURQUOISE, GREEN, RED, SAFFRON, IVORY};
        float cell = dp(26);
        p.setStyle(Paint.Style.FILL);
        for (int row = 0; row < 2; row++) {
            for (int col = -1; col <= (int)(width / cell) + 1; col++) {
                float cx = left + col * cell + (row % 2 == 0 ? 0 : cell / 2f);
                float cy = top + row * cell;
                p.setColor(withAlpha(colors[Math.floorMod(col + row, colors.length)], 120));
                drawEightPointStar(c, cx, cy, cell * .40f, cell * .18f, p);
                p.setColor(withAlpha(GOLD, 90));
                p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(dp(1));
                c.drawCircle(cx, cy, cell * .43f, p);
                p.setStyle(Paint.Style.FILL);
            }
        }
    }

    private void drawSideZellige(Canvas c, float w, float h) {
        float r = dp(18);
        int[] colors = {TURQUOISE, RED, SAFFRON, COBALT, GREEN};
        for (int i = 0; i < 7; i++) {
            float y = h * .27f + i * dp(74);
            p.setColor(withAlpha(colors[i % colors.length], 92));
            drawEightPointStar(c, dp(15), y, r, r * .45f, p);
            drawEightPointStar(c, w - dp(15), y + dp(34), r, r * .45f, p);
        }
    }

    private void drawAndalusianArches(Canvas c, float w, float h) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(withAlpha(IVORY, 65));
        float top = h * .19f;
        float archW = dp(92), archH = dp(116);
        drawHorseshoeArch(c, dp(18), top, archW, archH);
        drawHorseshoeArch(c, w - archW - dp(18), top + dp(18), archW, archH);
        p.setColor(withAlpha(GOLD, 55));
        p.setStrokeWidth(dp(1));
        drawHorseshoeArch(c, dp(24), top + dp(6), archW - dp(12), archH - dp(12));
        drawHorseshoeArch(c, w - archW - dp(12), top + dp(24), archW - dp(12), archH - dp(12));
        p.setStyle(Paint.Style.FILL);
    }

    private void drawHorseshoeArch(Canvas c, float x, float y, float aw, float ah) {
        path.reset();
        path.moveTo(x, y + ah);
        path.lineTo(x, y + aw * .58f);
        path.cubicTo(x, y, x + aw, y, x + aw, y + aw * .58f);
        path.lineTo(x + aw, y + ah);
        c.drawPath(path, p);
    }

    private void drawAtlas(Canvas c, float w, float h) {
        float base = h * .66f;
        path.reset();
        path.moveTo(0, base + dp(18));
        path.lineTo(w * .10f, base - dp(8));
        path.lineTo(w * .23f, base - dp(48));
        path.lineTo(w * .34f, base - dp(20));
        path.lineTo(w * .49f, base - dp(72));
        path.lineTo(w * .62f, base - dp(28));
        path.lineTo(w * .76f, base - dp(54));
        path.lineTo(w * .90f, base - dp(14));
        path.lineTo(w, base - dp(30));
        path.lineTo(w, base + dp(54));
        path.lineTo(0, base + dp(54));
        path.close();
        p.setColor(0x6C123C46);
        c.drawPath(path, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(1.4f));
        p.setColor(withAlpha(TURQUOISE, 95));
        c.drawPath(path, p);
        p.setStyle(Paint.Style.FILL);
    }

    private void drawDunes(Canvas c, float w, float h) {
        float y = h * .77f;
        path.reset();
        path.moveTo(0, y + dp(44));
        path.cubicTo(w * .20f, y - dp(30), w * .36f, y - dp(6), w * .53f, y + dp(14));
        path.cubicTo(w * .68f, y + dp(30), w * .82f, y - dp(24), w, y - dp(5));
        path.lineTo(w, y + dp(92));
        path.lineTo(0, y + dp(92));
        path.close();
        p.setColor(withAlpha(SAND, 70));
        c.drawPath(path, p);

        path.reset();
        path.moveTo(0, y + dp(64));
        path.cubicTo(w * .22f, y + dp(16), w * .42f, y + dp(54), w * .62f, y + dp(28));
        path.cubicTo(w * .80f, y + dp(8), w * .90f, y + dp(40), w, y + dp(18));
        path.lineTo(w, y + dp(102));
        path.lineTo(0, y + dp(102));
        path.close();
        p.setColor(withAlpha(SAFFRON, 48));
        c.drawPath(path, p);
    }

    private void drawAmazighMotifs(Canvas c, float w, float h) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.SQUARE);
        p.setStrokeWidth(dp(3));
        p.setColor(withAlpha(RED, 125));
        drawYaz(c, dp(31), h * .53f, dp(26));
        p.setColor(withAlpha(TURQUOISE, 125));
        drawYaz(c, w - dp(31), h * .57f, dp(26));
        p.setColor(withAlpha(SAFFRON, 105));
        drawYaz(c, w * .5f, h * .705f, dp(22));
        p.setStyle(Paint.Style.FILL);
    }

    private void drawYaz(Canvas c, float cx, float cy, float s) {
        c.drawLine(cx, cy - s, cx, cy + s, p);
        c.drawLine(cx - s * .55f, cy - s * .55f, cx + s * .55f, cy - s * .08f, p);
        c.drawLine(cx - s * .55f, cy + s * .55f, cx + s * .55f, cy + s * .08f, p);
    }

    private void drawBottomZellige(Canvas c, float w, float h) {
        float y = h - dp(52);
        float cell = dp(22);
        int[] colors = {RED, GREEN, TURQUOISE, COBALT, SAFFRON};
        for (int i = -1; i <= (int)(w / cell) + 1; i++) {
            p.setColor(withAlpha(colors[Math.floorMod(i, colors.length)], 82));
            drawEightPointStar(c, i * cell, y, cell * .35f, cell * .15f, p);
        }
    }

    private void drawEightPointStar(Canvas c, float cx, float cy, float outer, float inner, Paint paint) {
        path.reset();
        for (int i = 0; i < 16; i++) {
            double a = -Math.PI / 2 + i * Math.PI / 8;
            float r = (i % 2 == 0) ? outer : inner;
            float x = cx + (float)Math.cos(a) * r;
            float y = cy + (float)Math.sin(a) * r;
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.close();
        c.drawPath(path, paint);
    }

    private int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    private float dp(float v) { return v * d; }
}
