package com.master.radiomaroc;

import android.os.Handler;
import android.os.Looper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/** Resolves EasyBroadcast public short-lived tokens at playback time. */
public final class EasyBroadcastResolver {
    public interface Callback { void done(String url); }
    private EasyBroadcastResolver() {}

    public static boolean shouldResolve(String url) {
        if (url == null) return false;
        String u = url.toLowerCase();
        return u.contains("cdn.live.easybroadcast.io") && !u.contains("token.easybroadcast.io");
    }

    public static void resolve(String streamUrl, Callback callback) {
        new Thread(() -> {
            String result = streamUrl;
            HttpURLConnection c = null;
            try {
                String clean = streamUrl.endsWith("?") ? streamUrl.substring(0, streamUrl.length()-1) : streamUrl;
                String endpoint = "https://token.easybroadcast.io/all?url=" + URLEncoder.encode(clean, "UTF-8");
                c = (HttpURLConnection) new URL(endpoint).openConnection();
                c.setConnectTimeout(12000); c.setReadTimeout(12000); c.setInstanceFollowRedirects(true);
                c.setRequestProperty("User-Agent", "ExoPlayer"); c.setRequestProperty("Accept", "*/*");
                c.setRequestProperty("Referer", "https://snrt.player.easybroadcast.io/");
                int code = c.getResponseCode();
                if (code >= 200 && code < 300) {
                    InputStream in = c.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[2048]; int n; while ((n=in.read(buf))!=-1 && out.size()<16384) out.write(buf,0,n);
                    String token = new String(out.toByteArray(), StandardCharsets.UTF_8).trim();
                    if (token.startsWith("\"") && token.endsWith("\"") && token.length()>1) token=token.substring(1,token.length()-1);
                    if (!token.isEmpty() && token.indexOf('<')<0 && token.indexOf('\n')<0) result = clean + "?" + token;
                }
            } catch (Exception ignored) { } finally { if (c != null) c.disconnect(); }
            final String resolved=result; new Handler(Looper.getMainLooper()).post(() -> callback.done(resolved));
        }).start();
    }
}
