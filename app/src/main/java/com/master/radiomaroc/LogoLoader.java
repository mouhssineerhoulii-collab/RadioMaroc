package com.master.radiomaroc;

import android.graphics.*;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LogoLoader {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final LruCache<String, Bitmap> CACHE = new LruCache<String, Bitmap>(4096) {
        @Override protected int sizeOf(String key, Bitmap value) {
            return Math.max(1, value.getByteCount() / 1024);
        }
    };

    private LogoLoader() {}

    public static void load(String url, String label, ImageView imageView, int placeholderRes) {
        imageView.setImageResource(placeholderRes);
        if (label == null) label = "RM";
        final String safeLabel = label;
        final String cacheKey = (url == null ? "" : url) + "|" + safeLabel;

        Bitmap cached = CACHE.get(cacheKey);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }

        imageView.setTag(cacheKey);
        EXECUTOR.execute(() -> {
            Bitmap bitmap = null;
            for (String candidate : candidates(url)) {
                bitmap = downloadBitmap(candidate);
                if (bitmap != null) break;
            }
            if (bitmap == null) bitmap = makeBadge(safeLabel);
            CACHE.put(cacheKey, bitmap);

            Bitmap result = bitmap;
            MAIN.post(() -> {
                Object tag = imageView.getTag();
                if (cacheKey.equals(tag)) imageView.setImageBitmap(result);
            });
        });
    }

    private static List<String> candidates(String url) {
        List<String> out = new ArrayList<>();
        if (url == null || url.trim().isEmpty()) return out;
        out.add(url);
        try {
            URL u = new URL(url);
            String base = u.getProtocol() + "://" + u.getHost();
            out.add(base + "/apple-touch-icon.png");
            out.add(base + "/android-chrome-192x192.png");
            out.add(base + "/favicon-192x192.png");
            out.add(base + "/favicon.png");
        } catch (Exception ignored) {}
        return out;
    }

    private static Bitmap downloadBitmap(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(4500);
            connection.setReadTimeout(5000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "RadioMaroc/3.0 Android OpenSource");
            connection.setRequestProperty("Accept", "image/png,image/jpeg,image/webp,image/x-icon,*/*;q=0.5");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 400) return null;
            InputStream in = connection.getInputStream();
            byte[] data = readAll(in, 2_000_000);
            in.close();
            return decodeImage(data);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static byte[] readAll(InputStream in, int maxBytes) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0, n;
        while ((n = in.read(buffer)) != -1) {
            total += n;
            if (total > maxBytes) throw new IllegalStateException("Image too large");
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    private static Bitmap decodeImage(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (bitmap != null) return bitmap;

        // Many official sites still publish .ico favicons. Android's BitmapFactory does not
        // decode ICO directly, so extract the largest PNG frame when present.
        try {
            if (data.length < 22 || le16(data, 0) != 0 || le16(data, 2) != 1) return null;
            int count = le16(data, 4);
            int bestArea = -1, bestOffset = -1, bestSize = -1;
            for (int i = 0; i < count; i++) {
                int p = 6 + i * 16;
                if (p + 16 > data.length) break;
                int w = data[p] & 0xFF; if (w == 0) w = 256;
                int h = data[p + 1] & 0xFF; if (h == 0) h = 256;
                int size = le32(data, p + 8);
                int offset = le32(data, p + 12);
                if (size <= 0 || offset < 0 || offset + size > data.length) continue;
                int area = w * h;
                if (area > bestArea) {
                    bestArea = area;
                    bestOffset = offset;
                    bestSize = size;
                }
            }
            if (bestOffset >= 0) {
                return BitmapFactory.decodeByteArray(data, bestOffset, bestSize);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static int le16(byte[] b, int p) {
        return (b[p] & 0xff) | ((b[p + 1] & 0xff) << 8);
    }

    private static int le32(byte[] b, int p) {
        return (b[p] & 0xff) | ((b[p + 1] & 0xff) << 8) |
            ((b[p + 2] & 0xff) << 16) | ((b[p + 3] & 0xff) << 24);
    }

    private static Bitmap makeBadge(String label) {
        int size = 256;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.rgb(7, 59, 103));
        c.drawRoundRect(0, 0, size, size, 44, 44, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(12);
        p.setColor(Color.rgb(184, 138, 53));
        c.drawRoundRect(10, 10, size - 10, size - 10, 38, 38, p);
        p.setStyle(Paint.Style.FILL);

        String initials = initials(label);
        p.setColor(Color.rgb(255, 247, 232));
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(initials.length() <= 2 ? 76 : 58);
        Paint.FontMetrics fm = p.getFontMetrics();
        float y = size / 2f - (fm.ascent + fm.descent) / 2f;
        c.drawText(initials, size / 2f, y, p);
        return bmp;
    }

    private static String initials(String name) {
        if (name == null || name.trim().isEmpty()) return "RM";
        String cleaned = name.replaceAll("[^\\p{L}\\p{N} ]", " ").trim();
        String[] parts = cleaned.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(part.substring(0, 1).toUpperCase());
            if (sb.length() >= 3) break;
        }
        if (sb.length() == 0) return "RM";
        return sb.toString();
    }
}
