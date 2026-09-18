package com.master.radiomaroc;

import android.os.Handler;
import android.os.Looper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/** Resolves EasyBroadcast public short-lived tokens and HLS child playlists at playback time. */
public final class EasyBroadcastResolver {
    public interface Callback { void done(String url); }
    private EasyBroadcastResolver() {}

    public static boolean shouldResolve(String url) {
        if (url == null) return false;
        String u = url.toLowerCase();
        // These are direct MP3 endpoints, not tokenized HLS manifests.
        if (u.contains("/medi1radio/")) return false;
        return u.contains("cdn.live.easybroadcast.io") && !u.contains("token.easybroadcast.io");
    }

    public static void resolve(String streamUrl, Callback callback) {
        new Thread(() -> {
            String result = streamUrl;
            try {
                String clean = streamUrl.endsWith("?") ? streamUrl.substring(0, streamUrl.length()-1) : streamUrl;
                String token = fetchToken(clean);
                if (validToken(token)) {
                    String master = appendToken(clean, token);
                    String manifest = fetchText(master);
                    String child = findChildPlaylist(clean, manifest);
                    result = child == null ? master : appendToken(child, token);
                }
            } catch (Exception ignored) { }
            final String resolved=result;
            new Handler(Looper.getMainLooper()).post(() -> callback.done(resolved));
        }).start();
    }

    private static String fetchToken(String clean) throws Exception {
        String endpoint = "https://token.easybroadcast.io/all?url=" + URLEncoder.encode(clean, "UTF-8");
        String token = fetchText(endpoint).trim();
        if (token.startsWith("\"") && token.endsWith("\"") && token.length()>1)
            token=token.substring(1,token.length()-1);
        return token;
    }

    private static boolean validToken(String token) {
        return token != null && !token.isEmpty() && token.indexOf('<') < 0 && token.indexOf('\n') < 0;
    }

    private static String fetchText(String url) throws Exception {
        HttpURLConnection c = null;
        try {
            c=(HttpURLConnection)new URL(url).openConnection();
            c.setConnectTimeout(12000); c.setReadTimeout(12000); c.setInstanceFollowRedirects(true);
            c.setRequestProperty("User-Agent","RadioMaroc/7.1 Android"); c.setRequestProperty("Accept","*/*");
            if(url.contains("medi1radio")){c.setRequestProperty("Referer","https://www.medi1.com/");c.setRequestProperty("Origin","https://www.medi1.com");}
            else {c.setRequestProperty("Referer","https://snrtlive.ma/");c.setRequestProperty("Origin","https://snrtlive.ma");}
            int code=c.getResponseCode();
            if(code<200||code>=300) throw new IOException("HTTP "+code);
            InputStream in=c.getInputStream(); ByteArrayOutputStream out=new ByteArrayOutputStream();
            byte[] buf=new byte[4096]; int n;
            while((n=in.read(buf))!=-1 && out.size()<262144) out.write(buf,0,n);
            return new String(out.toByteArray(),StandardCharsets.UTF_8);
        } finally { if(c!=null)c.disconnect(); }
    }

    /** EasyBroadcast/SNRT master manifests may omit the token from their variant URI. */
    private static String findChildPlaylist(String base, String manifest) {
        if(manifest==null || !manifest.contains("#EXTM3U")) return null;
        String[] lines=manifest.replace("\r","").split("\n");
        boolean afterVariant=false;
        for(String raw:lines){
            String line=raw.trim();
            if(line.startsWith("#EXT-X-STREAM-INF")){ afterVariant=true; continue; }
            if(line.isEmpty()||line.startsWith("#")) continue;
            if(afterVariant || line.toLowerCase().contains(".m3u8")) {
                try { return new URL(new URL(base),line).toString(); } catch(Exception ignored) { return line; }
            }
        }
        return null;
    }

    private static String appendToken(String url,String token){
        if(url==null||token==null||token.isEmpty()) return url;
        if(url.contains("token=") || url.endsWith(token)) return url;
        if(url.endsWith("?")) return url+token;
        return url+(url.contains("?")?"&":"?")+token;
    }
}
