package com.master.radiomaroc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** Resolves small M3U/PLS-style endpoints that hide the actual radio stream URL. */
public final class PlaylistResolver {
    private PlaylistResolver() {}

    public static final class Result {
        public final String url;
        public final boolean hls;
        Result(String url, boolean hls) { this.url = url; this.hls = hls; }
    }

    public static boolean shouldResolve(String url) {
        if (url == null) return false;
        String u = url.toLowerCase();
        return u.contains("stream.bodkas.com/playlist") || u.endsWith(".m3u") || u.endsWith(".pls");
    }

    /** Backwards-compatible URL-only resolver used by the playback service. */
    public static String resolve(String source) throws Exception {
        return resolveDetailed(source).url;
    }

    /** Resolves the URL and also reports hidden HLS manifests. */
    public static Result resolveDetailed(String source) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(source).openConnection();
        c.setInstanceFollowRedirects(true);
        c.setConnectTimeout(10000);
        c.setReadTimeout(10000);
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android) RadioMaroc/5.7");
        c.setRequestProperty("Accept", "audio/*,application/vnd.apple.mpegurl,application/x-mpegURL,audio/x-mpegurl,*/*");
        try {
            int code = c.getResponseCode();
            if (code < 200 || code >= 400) throw new IllegalStateException("HTTP " + code);
            String finalUrl = c.getURL().toString();
            String contentType = c.getContentType();
            if (contentType != null) {
                String ct = contentType.toLowerCase();
                if (ct.contains("mpegurl") || ct.contains("vnd.apple")) return new Result(finalUrl, true);
                if (ct.startsWith("audio/") && !ct.contains("scpls")) return new Result(finalUrl, false);
            }
            try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
                String line;
                int lines = 0;
                while ((line = r.readLine()) != null && lines++ < 200) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    if (line.startsWith("#EXT-X-")) return new Result(finalUrl, true);
                    if (line.startsWith("#")) continue;
                    int eq = line.indexOf('=');
                    if (eq > 0 && line.substring(0, eq).toLowerCase().startsWith("file")) line = line.substring(eq + 1).trim();
                    if (line.startsWith("http://") || line.startsWith("https://")) return new Result(line, line.toLowerCase().contains(".m3u8"));
                }
            }
            throw new IllegalStateException("No stream URL in playlist");
        } finally {
            c.disconnect();
        }
    }
}
