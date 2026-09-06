package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemotePhotoView extends View {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final String PHOTO_URL = "https://commons.wikimedia.org/wiki/Special:Redirect/file/%D9%86%D9%88%D8%B1_%D8%B1%D9%85%D8%B6%D8%A7%D9%86_%D9%81%D9%88%D9%82_%D8%A7%D9%84%D9%85%D9%88%D8%AC.jpg?width=1600";

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Bitmap bitmap;

    public RemotePhotoView(Context context) { super(context); init(); }
    public RemotePhotoView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public RemotePhotoView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        setBackgroundColor(0xFF061A29);
        EXECUTOR.execute(() -> {
            Bitmap loaded = download(PHOTO_URL);
            if (loaded != null) {
                bitmap = loaded;
                post(this::invalidate);
            }
        });
    }

    private Bitmap download(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(8000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "RadioMaroc/4.3 Android OpenSource");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 400) return null;
            InputStream in = connection.getInputStream();
            Bitmap b = BitmapFactory.decodeStream(in);
            in.close();
            return b;
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap == null || getWidth() <= 0 || getHeight() <= 0) return;

        float scale = Math.max((float)getWidth() / bitmap.getWidth(), (float)getHeight() / bitmap.getHeight());
        float drawW = bitmap.getWidth() * scale;
        float drawH = bitmap.getHeight() * scale;
        float left = (getWidth() - drawW) / 2f;
        float top = (getHeight() - drawH) / 2f;
        canvas.save();
        canvas.translate(left, top);
        canvas.scale(scale, scale);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
    }
}
