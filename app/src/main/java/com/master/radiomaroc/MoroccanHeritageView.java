package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Photographic Moroccan heritage layer.
 * Uses real photographs of Moroccan zellij, riad architecture,
 * Atlas Mountains and Sahara. No decorative stars or synthetic motifs.
 */
public class MoroccanHeritageView extends View {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    private static final String COMMONS = "https://commons.wikimedia.org/wiki/Special:Redirect/file/";
    private static final String[] FILES = {
        "Zellij.jpg",
        "Courtyard in the roof or the traditional Moroccan house 'riad'.jpg",
        "A view on the Atlas Mountains from Imlil, Morocco.jpg",
        "Erg Chebbi, Sahara Desert Morocco.jpg"
    };

    private final Bitmap[] photos = new Bitmap[FILES.length];
    private final Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF dst = new RectF();

    public MoroccanHeritageView(Context context) { super(context); init(); }
    public MoroccanHeritageView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public MoroccanHeritageView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        for (int i = 0; i < FILES.length; i++) {
            final int index = i;
            EXECUTOR.execute(() -> {
                Bitmap loaded = downloadCommons(FILES[index], index == 0 ? 1100 : 1400);
                if (loaded != null) {
                    photos[index] = loaded;
                    post(this::invalidate);
                }
            });
        }
    }

    private Bitmap downloadCommons(String fileName, int width) {
        HttpURLConnection connection = null;
        try {
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20");
            String url = COMMONS + encoded + "?width=" + width;
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(9000);
            connection.setReadTimeout(12000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "RadioMaroc/4.7 Android OpenSource");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 400) return null;
            try (InputStream in = connection.getInputStream()) {
                return BitmapFactory.decodeStream(in);
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Real zellij: a refined strip in the upper part, not a synthetic pattern.
        drawPhotoBand(canvas, photos[0], 0f, h * 0.105f, w, h * 0.205f, 210);

        // Real Moroccan riad/Andalusian architecture around the search/header zone.
        drawPhotoBand(canvas, photos[1], 0f, h * 0.18f, w, h * 0.39f, 150);

        // Real Atlas landscape across the middle of the interface.
        drawPhotoBand(canvas, photos[2], 0f, h * 0.43f, w, h * 0.72f, 145);

        // Real Sahara dunes in the lower section.
        drawPhotoBand(canvas, photos[3], 0f, h * 0.68f, w, h, 175);

        // Soft photographic blending and contrast for the glass UI.
        overlayPaint.setShader(new LinearGradient(
            0, 0, 0, h,
            new int[]{0x16000000, 0x08000000, 0x1E031018, 0x30030C12},
            new float[]{0f, .30f, .66f, 1f},
            Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, overlayPaint);
        overlayPaint.setShader(null);
    }

    private void drawPhotoBand(Canvas canvas, Bitmap bitmap, float left, float top, float right, float bottom, int alpha) {
        if (bitmap == null) return;
        dst.set(left, top, right, bottom);
        imagePaint.setAlpha(alpha);

        float targetW = dst.width();
        float targetH = dst.height();
        float scale = Math.max(targetW / bitmap.getWidth(), targetH / bitmap.getHeight());
        float srcW = targetW / scale;
        float srcH = targetH / scale;
        float srcLeft = (bitmap.getWidth() - srcW) * 0.5f;
        float srcTop = (bitmap.getHeight() - srcH) * 0.5f;

        android.graphics.Rect src = new android.graphics.Rect(
            Math.max(0, Math.round(srcLeft)),
            Math.max(0, Math.round(srcTop)),
            Math.min(bitmap.getWidth(), Math.round(srcLeft + srcW)),
            Math.min(bitmap.getHeight(), Math.round(srcTop + srcH))
        );
        canvas.drawBitmap(bitmap, src, dst, imagePaint);
    }
}
