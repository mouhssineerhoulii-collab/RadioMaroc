package com.master.radiomaroc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves radio endpoints while preserving redirects and discovering live HLS URLs. */
public final class PlaylistResolver {
    private PlaylistResolver() {}

    private static final int MAX_REDIRECTS = 8;
    private static final int MAX_PAGE_CHARS = 1024 * 1024;
    private static final Pattern HLS_URL = Pattern.compile("(?i)(https?:\\\\?/\\\\?/[^\\\"'<>\\s]+?\\.m3u8(?:\\?[^\\\"'<>\\s]*)?)");
    private static final Pattern IFRAME = Pattern.compile("(?i)<iframe[^>]+src=[\\\"']([^\\\"']+)[\\\"']");

    public static final class Resolved {
        public final String url;
        public final boolean hls;
        Resolved(String url, boolean hls) { this.url = url; this.hls = hls; }
    }

    public static boolean shouldResolve(String url) {
        if (url == null) return false;
        String u = url.toLowerCase(Locale.ROOT);
        return u.contains("snrtlive.ma") || u.contains("snrt.ma/") || u.contains("player.easybroadcast.io") || u.contains("stream.bodkas.com/playlist") || u.endsWith(".m3u") || u.endsWith(".pls");
    }

    private static boolean isSnrt(String u) {
        return u.contains("globecast.akamaized.net") || u.contains("snrtlive.ma") || u.contains("snrt.ma") || u.contains("easybroadcast.io");
    }

    public static String resolve(String source) throws Exception { return resolveDetailed(source).url; }

    public static Resolved resolveDetailed(String source) throws Exception {
        return resolveDetailed(source, 0);
    }

    /** Resolves redirects, playlist wrappers, SNRT web players and extensionless HLS endpoints. */
    private static Resolved resolveDetailed(String source, int pageDepth) throws Exception {
        String current = source;
        for (int redirects = 0; redirects <= MAX_REDIRECTS; redirects++) {
            HttpURLConnection c = open(current);
            try {
                int code = c.getResponseCode();
                if (code >= 300 && code < 400) {
                    String location = c.getHeaderField("Location");
                    if (location == null || location.trim().isEmpty()) throw new IllegalStateException("Redirect without Location");
                    current = absolute(current, location.trim());
                    continue;
                }
                if (code < 200 || code >= 400) throw new IllegalStateException("HTTP " + code);

                String finalUrl = c.getURL().toString();
                String contentType = c.getContentType();
                String ct = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
                if (ct.contains("mpegurl") || ct.contains("vnd.apple")) return new Resolved(finalUrl, true);
                if ((ct.startsWith("audio/") || ct.startsWith("video/")) && !ct.contains("scpls") && !ct.contains("mpegurl"))
                    return new Resolved(finalUrl, false);

                if (ct.contains("text/html") || finalUrl.contains("snrtlive.ma") || finalUrl.contains("player.easybroadcast.io")) {
                    String html = readLimited(c);
                    String hls = extractHls(html);
                    if (hls != null) return new Resolved(unescape(hls), true);

                    // SNRT currently embeds its player. Resolve that player at runtime instead of
                    // persisting short-lived CDN/token URLs in Stations.java.
                    if (pageDepth < 2) {
                        Matcher frame = IFRAME.matcher(html);
                        while (frame.find()) {
                            String src = absolute(finalUrl, unescape(frame.group(1)));
                            if (src.contains("easybroadcast.io") || src.contains("snrt")) {
                                try { return resolveDetailed(src, pageDepth + 1); }
                                catch (Exception ignored) { /* try another embedded player */ }
                            }
                        }
                    }
                    throw new IllegalStateException("SNRT page contains no discoverable live stream");
                }

                try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
                    String line;
                    int lines = 0;
                    boolean hlsHeader = false;
                    while ((line = r.readLine()) != null && lines++ < 400) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        if (line.equals("#EXTM3U") || line.startsWith("#EXT-X-")) {
                            hlsHeader = true;
                            if (line.startsWith("#EXT-X-")) return new Resolved(finalUrl, true);
                            continue;
                        }
                        if (line.startsWith("#")) continue;
                        int eq = line.indexOf('=');
                        if (eq > 0 && line.substring(0, eq).toLowerCase(Locale.ROOT).startsWith("file")) line = line.substring(eq + 1).trim();
                        if (line.startsWith("http://") || line.startsWith("https://"))
                            return new Resolved(line, hlsHeader || line.toLowerCase(Locale.ROOT).contains(".m3u8"));
                        if (hlsHeader && !line.contains("=")) return new Resolved(finalUrl, true);
                    }
                    if (hlsHeader) return new Resolved(finalUrl, true);
                }
                throw new IllegalStateException("No stream URL in playlist");
            } finally { c.disconnect(); }
        }
        throw new IllegalStateException("Too many redirects");
    }

    private static String readLimited(HttpURLConnection c) throws Exception {
        StringBuilder b = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
            char[] buf = new char[8192];
            int n;
            while ((n = r.read(buf)) > 0 && b.length() < MAX_PAGE_CHARS)
                b.append(buf, 0, Math.min(n, MAX_PAGE_CHARS - b.length()));
        }
        return b.toString();
    }

    private static String extractHls(String text) {
        Matcher m = HLS_URL.matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private static String unescape(String s) {
        return s.replace("\\/", "/").replace("&amp;", "&").replace("\\u0026", "&");
    }

    private static HttpURLConnection open(String source) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(source).openConnection();
        c.setInstanceFollowRedirects(false);
        c.setConnectTimeout(12000);
        c.setReadTimeout(15000);
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 16) AppleWebKit/537.36 Chrome/140 Mobile Safari/537.36 RadioMaroc/5.9");
        c.setRequestProperty("Accept", "text/html,application/vnd.apple.mpegurl,application/x-mpegURL,audio/mpeg,audio/aac,audio/*,*/*;q=0.8");
        c.setRequestProperty("Accept-Language", "ar,fr;q=0.9,en;q=0.8");
        c.setRequestProperty("Connection", "keep-alive");
        if (isSnrt(source.toLowerCase(Locale.ROOT))) {
            c.setRequestProperty("Referer", "https://snrtlive.ma/");
            c.setRequestProperty("Origin", "https://snrtlive.ma");
        }
        return c;
    }

    private static String absolute(String base, String location) throws Exception {
        return new URI(base).resolve(location).toString();
    }
}
