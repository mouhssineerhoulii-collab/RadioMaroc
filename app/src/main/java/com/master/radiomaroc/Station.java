package com.master.radiomaroc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Station {
    public final String name;
    public final String subtitleAr;
    public final String subtitleFr;
    public final String subtitleEn;
    public final String streamUrl;
    public final String logoUrl;
    public final List<String> streamUrls;
    public final String category;

    public Station(String name, String subtitleAr, String subtitleFr, String subtitleEn,
                   String streamUrl, String logoUrl) {
        this(name, subtitleAr, subtitleFr, subtitleEn, logoUrl, "general", streamUrl);
    }

    public Station(String name, String subtitleAr, String subtitleFr, String subtitleEn,
                   String logoUrl, String category, String... urls) {
        this.name = name;
        this.subtitleAr = subtitleAr;
        this.subtitleFr = subtitleFr;
        this.subtitleEn = subtitleEn;
        this.logoUrl = logoUrl;
        this.category = category == null ? "general" : category;
        List<String> clean = new ArrayList<>();
        if (urls != null) {
            for (String raw : urls) {
                if (raw == null || raw.trim().isEmpty()) continue;
                String u = raw.trim();
                addUnique(clean, u);
                // Some Moroccan broadcasters still expose the reliable radio endpoint over HTTP.
                // Add it only for explicitly allow-listed hosts; Android network security remains
                // blocked for every other cleartext destination.
                if (u.startsWith("https://") && isLegacyRadioHost(u)) {
                    addUnique(clean, "http://" + u.substring("https://".length()));
                }
            }
        }
        this.streamUrls = Collections.unmodifiableList(clean);
        this.streamUrl = clean.isEmpty() ? "" : clean.get(0);
    }

    private static void addUnique(List<String> list, String url) {
        if (!list.contains(url)) list.add(url);
    }

    private static boolean isLegacyRadioHost(String url) {
        return url.contains("radiotangermed-22.ice.infomaniak.ch/")
            || url.contains("broadcast.ice.infomaniak.ch/")
            || url.contains("aswat.ice.infomaniak.ch/")
            || url.contains("broadcast.infomaniak.ch/")
            || url.contains("broadcast.infomaniak.net/")
            || url.contains("cdnamd-hls-globecast.akamaized.net/")
            || url.contains("live.medi1.com/");
    }

    public boolean hasStream() { return !streamUrls.isEmpty(); }

    public String subtitle(String lang) {
        if ("fr".equals(lang)) return subtitleFr;
        if ("en".equals(lang)) return subtitleEn;
        return subtitleAr;
    }
}
