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
        if (urls != null) for (String u : urls) if (u != null && !u.trim().isEmpty() && !clean.contains(u.trim())) clean.add(u.trim());
        this.streamUrls = Collections.unmodifiableList(clean);
        this.streamUrl = clean.isEmpty() ? "" : clean.get(0);
    }

    public boolean hasStream() { return !streamUrls.isEmpty(); }

    public String subtitle(String lang) {
        if ("fr".equals(lang)) return subtitleFr;
        if ("en".equals(lang)) return subtitleEn;
        return subtitleAr;
    }
}
