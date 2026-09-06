package com.master.radiomaroc;

public class Station {
    public final String name;
    public final String subtitleAr;
    public final String subtitleFr;
    public final String subtitleEn;
    public final String streamUrl;
    public final String logoUrl;

    public Station(String name, String subtitleAr, String subtitleFr, String subtitleEn,
                   String streamUrl, String logoUrl) {
        this.name = name;
        this.subtitleAr = subtitleAr;
        this.subtitleFr = subtitleFr;
        this.subtitleEn = subtitleEn;
        this.streamUrl = streamUrl;
        this.logoUrl = logoUrl;
    }

    public String subtitle(String lang) {
        if ("fr".equals(lang)) return subtitleFr;
        if ("en".equals(lang)) return subtitleEn;
        return subtitleAr;
    }
}
