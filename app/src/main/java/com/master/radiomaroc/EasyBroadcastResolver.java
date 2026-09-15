package com.master.radiomaroc;

import android.os.Handler;
import android.os.Looper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/** Resolves EasyBroadcast public token URLs at playback time. */
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
                String endpoint = "https://token.easybroadcast.io/all?url=" + URLEncoder.encode(streamUrl, "UTF-8");
                c = (HttpURLConnection) new URL(endpoint).openConnection();
                c.setConnectTimeout(12000);
                c.setReadTimeout(12000);
                c.setInstanceFollowRedirects(true);
                c.setRequestProperty("User-Agent", "ExoPlayer");
                c.setRequestProperty("Accept", "*/*");
                c.setRequestProperty("Referer", "https://snrt.player.easybroadcast.io/");
                int code = c.getResponseCode();
                if (code >= 200 && code < 300) {
                    InputStream in = c.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[4096]; int n;
                    while ((n = in.read(buf)) != -1 && out.size() < 65536) out.write(buf,0,n);
                    String body = new String(out.toByteArray(), StandardCharsets.UTF_8).trim();
                    String candidate = extractUrl(body);
                    if (candidate != null && candidate.startsWith("http")) result = candidate;
                }
            } catch (Exception ignored) {
            } finally { if (c != null) c.disconnect(); }
            final String resolved = result;
            new Handler(Looper.getMainLooper()).post(() -> callback.done(resolved));
        }).start();
    }

    private static String extractUrl(String body) {
        if (body == null || body.isEmpty()) return null;
        if (body.startsWith("http://") || body.startsWith("https://")) return unquote(body);
        String[] keys = {"\"url\"", "\"stream\"", "\"src\""};
        for (String key : keys) {
            int k = body.indexOf(key); if (k < 0) continue;
            int colon = body.indexOf(':', k + key.length()); if (colon < 0) continue;
            int q1 = body.indexOf('"', colon + 1); if (q1 < 0) continue;
            int q2 = q1 + 1;
            while (q2 < body.length()) { if (body.charAt(q2)=='"' && body.charAt(q2-1)!='\\') break; q2++; }
            if (q2 < body.length()) return body.substring(q1+1,q2).replace("\\/", "/");
        }
        int h = body.indexOf("https://"); if (h < 0) h = body.indexOf("http://");
        if (h >= 0) { int e=h; while(e<body.length() && body.charAt(e)!='"' && !Character.isWhitespace(body.charAt(e))) e++; return body.substring(h,e).replace("\\/", "/"); }
        return null;
    }
    private static String unquote(String s) { s=s.trim(); if(s.length()>1 && s.charAt(0)=='"' && s.charAt(s.length()-1)=='"') s=s.substring(1,s.length()-1); return s.replace("\\/", "/"); }
}
