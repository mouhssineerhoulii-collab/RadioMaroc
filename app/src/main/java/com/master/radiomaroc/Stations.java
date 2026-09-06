package com.master.radiomaroc;

import java.util.Arrays;
import java.util.List;

public final class Stations {
    private Stations() {}

    public static final List<Station> ALL = Arrays.asList(
        new Station("Radio Mars", "رياضة وبرامج عامة", "Sport et programmes généralistes", "Sports and general programs",
            "https://radiomars.ice.infomaniak.ch/radiomars-128.mp3", "https://www.radiomars.ma/favicon.ico"),
        new Station("MEDI 1", "أخبار وبرامج عامة", "Actualités et programmes généralistes", "News and general programs",
            "https://cdn.live.easybroadcast.io/live/83_medi1radio-maghreb_8s9i4bn/playlist.m3u8", "https://www.medi1.com/favicon.ico"),
        new Station("HIT RADIO", "موسيقى وشباب", "Musique et jeunesse", "Music and youth",
            "https://hitradio-maroc.ice.infomaniak.ch/hitradio-maroc-128.mp3", "https://www.hitradio.ma/favicon.ico"),
        new Station("Radio Aswat", "برامج عامة وموسيقى", "Talk, information et musique", "Talk, information and music",
            "https://broadcast.ice.infomaniak.ch/aswat-high.mp3", "https://www.aswat.ma/favicon.ico"),
        new Station("Chada FM", "برامج وموسيقى مغربية", "Programmes et musique marocaine", "Moroccan programs and music",
            "https://stream.bodkas.com/playlist?id=chadafmradio", "https://chadafm.net/favicon.ico"),
        new Station("MFM Radio", "برامج عامة وموسيقى", "Programmes généralistes et musique", "General programs and music",
            "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico"),
        new Station("Atlantic Radio", "اقتصاد وأخبار وموسيقى", "Économie, actualités et musique", "Business, news and music",
            "https://atlantic-sonic.nindohost.net:9300/stream", "https://www.atlanticradio.ma/favicon.ico"),
        new Station("Cap Radio", "طنجة والشمال المغربي", "Tanger et nord du Maroc", "Tangier and northern Morocco",
            "https://listen.radioking.com/radio/710810/stream/776366", "https://capradio.ma/favicon.ico"),
        new Station("Radio Tanger Med", "أخبار الميناء والمنطقة", "Actualités du port et de la région", "Port and regional news",
            "http://radiotangermed-22.ice.infomaniak.ch/radiotangermed-22-128.mp3", "https://www.tangermed.ma/favicon.ico"),
        new Station("إذاعة محمد السادس للقرآن الكريم", "القرآن الكريم والبرامج الدينية", "Coran et programmes religieux", "Quran and religious programs",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_mohammed_6/hls_snrt_radio/radio_mohammed_6-mp4a_130400_qad=1.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("الإذاعة الوطنية", "الإذاعة الوطنية المغربية", "Radio nationale marocaine", "Moroccan national radio",
            "http://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_idaa_watanya/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Chaîne Inter", "قناة إنتر التابعة للشركة الوطنية للإذاعة والتلفزة", "Chaîne internationale de la SNRT", "SNRT international channel",
            "http://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_chaine_inter/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("الإذاعة الأمازيغية", "إذاعة أمازيغية وطنية", "Radio nationale amazighe", "National Amazigh radio",
            "http://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_amazigh/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة طنجة الجهوية", "إذاعة جهوية من طنجة", "Radio régionale de Tanger", "Tangier regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tanger/hls_snrt_radio/radio_tanger-mp4a_130400_qad=1.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Plus Agadir", "إذاعة جهوية من أكادير", "Radio régionale d'Agadir", "Agadir regional radio",
            "https://stream-158.zeno.fm/bqdbb6hd0neuv", "https://radioplus.ma/favicon.ico"),
        new Station("Med Radio", "برامج اجتماعية وحوارية", "Programmes sociaux et débats", "Social programs and talk",
            "https://medradio.ice.infomaniak.ch/medradio-128.mp3", "https://medradio.ma/favicon.ico"),
        new Station("Medina FM", "برامج ثقافية واجتماعية من مكناس", "Culture et société depuis Meknès", "Culture and society from Meknes",
            "https://medinafm.ice.infomaniak.ch/medinafm-64.mp3", "https://medinafm.ma/favicon.ico"),
        new Station("Radio Yabiladi", "موسيقى مغربية", "Musique marocaine", "Moroccan music",
            "https://radio.yabiladi.com:8002/;stream.mp3", "https://www.yabiladi.com/favicon.ico"),
        new Station("HIT RADIO 100% Mgharba", "موسيقى مغربية 100%", "100% musique marocaine", "100% Moroccan music",
            "https://mgharba.ice.infomaniak.ch/mgharba-128.mp3", "https://www.hitradio.ma/favicon.ico")
    );
}
