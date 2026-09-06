package com.master.radiomaroc;

import java.util.Arrays;
import java.util.List;

public final class Stations {
    private Stations() {}

    public static final List<Station> ALL = Arrays.asList(
        new Station("راديو مارس", "رياضة وبرامج عامة", "https://radiomars.ice.infomaniak.ch/radiomars-128.mp3"),
        new Station("ميدي 1", "أخبار وبرامج عامة", "https://cdn.live.easybroadcast.io/live/83_medi1radio-maghreb_8s9i4bn/playlist.m3u8"),
        new Station("هيت راديو", "موسيقى وشباب", "https://hitradio-maroc.ice.infomaniak.ch/hitradio-maroc-128.mp3"),
        new Station("أصوات", "برامج عامة وموسيقى", "https://broadcast.ice.infomaniak.ch/aswat-high.mp3"),
        new Station("شدى إف إم", "برامج وموسيقى مغربية", "https://stream.bodkas.com/playlist?id=chadafmradio"),
        new Station("إم إف إم", "برامج عامة وموسيقى", "https://a5.asurahosting.com:7980/radio.mp3"),
        new Station("أتلانتيك راديو", "اقتصاد وأخبار وموسيقى", "https://atlantic-sonic.nindohost.net:9300/stream"),
        new Station("كاب راديو", "طنجة والشمال المغربي", "https://listen.radioking.com/radio/710810/stream/776366"),
        new Station("راديو طنجة المتوسط", "أخبار الميناء وموسيقى", "http://radiotangermed-22.ice.infomaniak.ch/radiotangermed-22-128.mp3"),
        new Station("إذاعة محمد السادس للقرآن الكريم", "الشركة الوطنية للإذاعة والتلفزة • القرآن الكريم", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_mohammed_6/hls_snrt_radio/radio_mohammed_6-mp4a_130400_qad=1.m3u8"),
        new Station("الإذاعة الوطنية المغربية", "الشركة الوطنية للإذاعة والتلفزة • الإذاعة الوطنية", "http://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_idaa_watanya/hls_snrt_radio/index.m3u8"),
        new Station("قناة إنتر", "الشركة الوطنية للإذاعة والتلفزة • قناة إنتر", "http://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_chaine_inter/hls_snrt_radio/index.m3u8"),
        new Station("الإذاعة الأمازيغية", "الشركة الوطنية للإذاعة والتلفزة • الإذاعة الأمازيغية", "http://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_amazigh/hls_snrt_radio/index.m3u8"),
        new Station("إذاعة طنجة الجهوية", "الشركة الوطنية للإذاعة والتلفزة • طنجة", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tanger/hls_snrt_radio/radio_tanger-mp4a_130400_qad=1.m3u8"),
        new Station("راديو بلوس أكادير", "جهوي وموسيقى", "https://stream-158.zeno.fm/bqdbb6hd0neuv")
    );
}
