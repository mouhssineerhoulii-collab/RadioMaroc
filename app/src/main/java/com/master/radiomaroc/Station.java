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
                if (u.startsWith("https://") && isLegacyRadioHost(u)) addUnique(clean, "http://" + u.substring(8));
            }
        }
        addStationFallback(name, clean);
        this.streamUrls = Collections.unmodifiableList(clean);
        this.streamUrl = clean.isEmpty() ? "" : clean.get(0);
    }

    public String displayName(String lang) {
        if (!"ar".equals(lang)) return name;
        switch (name) {
            case "Radio Nationale": return "الإذاعة الوطنية المغربية (Radio Nationale)";
            case "Radio Amazigh": return "الإذاعة الأمازيغية (Radio Amazigh)";
            case "Chaîne Inter": return "السلسلة الدولية (Chaîne Inter)";
            case "Radio Casablanca": return "إذاعة الدار البيضاء (Radio Casablanca)";
            case "Radio Fès": return "إذاعة فاس (Radio Fès)";
            case "Radio Meknès": return "إذاعة مكناس (Radio Meknès)";
            case "Radio Tanger": return "إذاعة طنجة (Radio Tanger)";
            case "Radio Tétouan": return "إذاعة تطوان (Radio Tétouan)";
            case "Radio Al Hoceima": return "إذاعة الحسيمة (Radio Al Hoceima)";
            case "Radio Oujda": return "إذاعة وجدة (Radio Oujda)";
            case "Radio Marrakech": return "إذاعة مراكش (Radio Marrakech)";
            case "Radio Agadir": return "إذاعة أكادير (Radio Agadir)";
            case "Radio Laâyoune": return "إذاعة العيون (Radio Laâyoune)";
            case "Radio Dakhla": return "إذاعة الداخلة (Radio Dakhla)";
            case "Radio Tanger Med": return "راديو طنجة المتوسط (Radio Tanger Med)";
            default: return name;
        }
    }

    private static void addUnique(List<String> list, String url) { if (url != null && !url.isEmpty() && !list.contains(url)) list.add(url); }
    private static void addStationFallback(String name, List<String> urls) {
        if (name == null) return;
        switch (name) {
            case "Radio Tanger Med": addUnique(urls,"http://radiotangermed-22.ice.infomaniak.ch/radiotangermed-22-128.mp3");addUnique(urls,"http://radiotangermed-22.ice.infomaniak.ch/radiotangermed-22-192.mp3");break;
            case "Medi 1 Tarab": addUnique(urls,"http://live.medi1.com/Tarab");break;
            case "Medi 1 Andalouse": addUnique(urls,"http://live.medi1.com/Andalouse");break;
            case "Medi 1 Soufi": addUnique(urls,"http://live.medi1.com/Soufi");break;
            case "Medi 1 Nayda": addUnique(urls,"http://live.medi1.com/Nayda");break;
            case "Medi 1 Lounge": addUnique(urls,"http://live.medi1.com/Lounge");break;
            case "Medi 1 Latino": addUnique(urls,"http://live.medi1.com/Latino");break;
            case "Medi 1 Jazz": addUnique(urls,"http://live.medi1.com/Jazz");break;
            case "Medi 1 DJ": addUnique(urls,"http://live.medi1.com/Dj");break;
            case "Medi 1 Hits": addUnique(urls,"https://cdn.live.easybroadcast.io/medi1radio/Hits");addUnique(urls,"http://live.medi1.com/Hits");break;
            default: break;
        }
    }
    private static boolean isLegacyRadioHost(String url) {
        return url.contains("radiotangermed-22.ice.infomaniak.ch/")||url.contains("broadcast.ice.infomaniak.ch/")||url.contains("aswat.ice.infomaniak.ch/")||url.contains("broadcast.infomaniak.ch/")||url.contains("broadcast.infomaniak.net/")||url.contains("cdnamd-hls-globecast.akamaized.net/")||url.contains("live.medi1.com/");
    }
    public boolean hasStream(){return !streamUrls.isEmpty();}
    public String subtitle(String lang){if("fr".equals(lang))return subtitleFr;if("en".equals(lang))return subtitleEn;return subtitleAr;}
}
