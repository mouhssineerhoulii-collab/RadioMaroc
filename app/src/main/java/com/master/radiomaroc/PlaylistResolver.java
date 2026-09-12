package com.master.radiomaroc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** Resolves small M3U/PLS-style endpoints that hide the actual radio stream URL. */
public final class PlaylistResolver {
    private PlaylistResolver() {}

    public static boolean shouldResolve(String url) {
        if (url == null) return false;
        String u = url.toLowerCase();
        return u.contains("stream.bodkas.com/playlist") || u.endsWith(".m3u") || u.endsWith(".pls");
    }

    public static String resolve(String source) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(source).openConnection();
        c.setInstanceFollowRedirects(true);
        c.setConnectTimeout(10000);
        c.setReadTimeout(10000);
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android) RadioMaroc/5.7");
        c.setRequestProperty("Accept", "audio/*,application/vnd.apple.mpegurl,application/x-mpegURL,audio/x-mpegurl,*/*");
        try {
            int code = c.getResponseCode();
            if (code < 200 || code >= 400) throw new IllegalStateException("HTTP " + code);
            String contentType = c.getContentType();
            // If the endpoint redirected directly to audio/HLS, return the final URL.
            if (contentType != null) {
                String ct = contentType.toLowerCase();
                if (ct.startsWith("audio/") && !ct.contains("mpegurl") && !ct.contains("scpls")) return c.getURL().toString();
            }
            try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
                String line;
                int lines = 0;
                while ((line = r.readLine()) != null && lines++ < 200) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int eq = line.indexOf('=');
                    if (eq > 0 && line.substring(0, eq).toLowerCase().startsWith("file")) line = line.substring(eq + 1).trim();
                    if (line.startsWith("http://") || line.startsWith("https://")) return line;
                }
            }
            throw new IllegalStateException("No stream URL in playlist");
        } finally {
            c.disconnect();
        }
    }
}
