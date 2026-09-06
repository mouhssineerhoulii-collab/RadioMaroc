package com.master.radiomaroc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LogoLoader {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final LruCache<String, Bitmap> CACHE = new LruCache<String, Bitmap>(24) {
        @Override protected int sizeOf(String key, Bitmap value) {
            return Math.max(1, value.getByteCount() / 1024);
        }
    };

    private LogoLoader() {}

    public static void load(String url, ImageView imageView, int placeholderRes) {
        imageView.setImageResource(placeholderRes);
        if (url == null || url.isEmpty()) return;

        Bitmap cached = CACHE.get(url);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }

        imageView.setTag(url);
        EXECUTOR.execute(() -> {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("User-Agent", "RadioMaroc/2.0 Android");
                InputStream in = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
                if (bitmap != null) CACHE.put(url, bitmap);
            } catch (Exception ignored) {
            } finally {
                if (connection != null) connection.disconnect();
            }

            Bitmap result = bitmap;
            MAIN.post(() -> {
                Object tag = imageView.getTag();
                if (result != null && url.equals(tag)) imageView.setImageBitmap(result);
            });
        });
    }
}
