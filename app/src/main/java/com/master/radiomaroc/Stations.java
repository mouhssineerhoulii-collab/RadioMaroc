package com.master.radiomaroc;

import java.util.Arrays;
import java.util.List;

/**
 * Curated Moroccan radio catalogue.
 *
 * Public stations follow the SNRT/HACA national + regional structure.
 * For networks whose regional brands currently expose a common Internet feed
 * (MFM / Radio Plus), the regional entries intentionally share that feed while
 * keeping their own names so they remain searchable and can later receive a
 * dedicated stream without changing the UI.
 */
public final class Stations {
    private Stations() {}

    public static final List<Station> ALL = Arrays.asList(
        // ── Public national services ──────────────────────────────────────────
        new Station("الإذاعة الوطنية — Al Idaa Al Watania", "إذاعة عمومية وطنية", "Radio publique nationale", "National public radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_idaa_watanya/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("الإذاعة الأمازيغية — Al Idaa Al Amazighia", "إذاعة وطنية باللغة والثقافة الأمازيغية", "Radio nationale amazighe", "National Amazigh radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_amazigh/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio 2M — إذاعة دوزيم", "إذاعة وطنية عامة وموسيقية", "Radio nationale généraliste et musicale", "National general and music radio",
            "https://radio2m-i.akamaihd.net/hls/live/624355/radio2m/playlist-128000.m3u8", "https://radio2m.ma/favicon.ico"),
        new Station("Chaîne Inter — الإذاعة الدولية", "أخبار وثقافة وبرامج دولية", "Actualités, culture et programmes internationaux", "News, culture and international programs",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_chaine_inter/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة محمد السادس للقرآن الكريم", "القرآن الكريم والبرامج الدينية", "Coran et programmes religieux", "Quran and religious programs",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_mohammed_6/hls_snrt_radio/radio_mohammed_6-mp4a_130400_qad=1.m3u8", "https://www.snrt.ma/favicon.ico"),

        // ── Public regional SNRT services ────────────────────────────────────
        new Station("إذاعة الدار البيضاء — Radio Casablanca", "إذاعة جهوية للدار البيضاء", "Radio régionale de Casablanca", "Casablanca regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_casa/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة فاس — Radio Fès", "إذاعة جهوية من فاس", "Radio régionale de Fès", "Fes regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_fes/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة مكناس — Radio Meknès", "إذاعة جهوية من مكناس", "Radio régionale de Meknès", "Meknes regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_meknes/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة طنجة — Radio Tanger", "إذاعة جهوية من طنجة", "Radio régionale de Tanger", "Tangier regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tanger/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة تطوان — Radio Tétouan", "إذاعة جهوية من تطوان", "Radio régionale de Tétouan", "Tetouan regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tetouan/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة الحسيمة — Radio Al Hoceïma", "إذاعة جهوية من الحسيمة", "Radio régionale d'Al Hoceïma", "Al Hoceima regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_el_hoceima/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة وجدة — Radio Oujda", "إذاعة جهوية من وجدة", "Radio régionale d'Oujda", "Oujda regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_oujda/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة مراكش — Radio Marrakech", "إذاعة جهوية من مراكش", "Radio régionale de Marrakech", "Marrakech regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_marrakech/hls_snrt_radio/radio_marrakech-mp4a_130400_qad=1.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة أكادير — Radio Agadir", "إذاعة جهوية من أكادير", "Radio régionale d'Agadir", "Agadir regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_agadir/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة العيون — Radio Laâyoune", "إذاعة جهوية من العيون", "Radio régionale de Laâyoune", "Laayoune regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_laayoune/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("إذاعة الداخلة — Radio Dakhla", "إذاعة جهوية من الداخلة", "Radio régionale de Dakhla", "Dakhla regional radio",
            "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_dakhla/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),

        // ── Private Moroccan networks ────────────────────────────────────────
        new Station("Medi 1 Radio — ميدي 1", "أخبار وبرامج عامة", "Actualités et programmes généralistes", "News and general programs",
            "https://cdn.live.easybroadcast.io/live/83_medi1radio-maghreb_8s9i4bn/playlist.m3u8", "https://www.medi1.com/favicon.ico"),
        new Station("HIT RADIO — هيت راديو", "موسيقى وشباب", "Musique et jeunesse", "Music and youth",
            "https://hitradio-maroc.ice.infomaniak.ch/hitradio-maroc-128.mp3", "https://www.hitradio.ma/favicon.ico"),
        new Station("Radio Aswat — أصوات", "أخبار وثقافة وبرامج عامة", "Information, culture et programmes généralistes", "News, culture and general programs",
            "https://broadcast.ice.infomaniak.ch/aswat-high.mp3", "https://www.aswat.ma/favicon.ico"),
        new Station("Atlantic Radio — أتلانتيك راديو", "اقتصاد وأخبار وبرامج عامة", "Économie, actualités et programmes", "Business, news and general programs",
            "https://atlantic-sonic.nindohost.net:9300/stream", "https://www.atlanticradio.ma/favicon.ico"),
        new Station("Chada FM — شذى إف إم", "موسيقى وبرامج مغربية", "Musique et programmes marocains", "Moroccan music and programs",
            "https://broadcast.infomaniak.net/chadafm-high.mp3", "https://chada.ma/favicon.ico"),
        new Station("Radio Mars — راديو مارس", "رياضة وبرامج عامة", "Sport et programmes généralistes", "Sports and general programs",
            "https://radiomars.ice.infomaniak.ch/radiomars-128.mp3", "https://www.radiomars.ma/favicon.ico"),
        new Station("Med Radio — ميد راديو", "أخبار وبرامج اجتماعية وحوارية", "Information, société et débats", "News, social programs and talk",
            "https://medradio.ice.infomaniak.ch/medradio-128.mp3", "https://medradio.ma/favicon.ico"),
        new Station("Cap Radio — كاب راديو", "طنجة وشمال المغرب وبرامج عامة", "Tanger, nord du Maroc et programmes généralistes", "Tangier, northern Morocco and general programs",
            "https://listen.radioking.com/radio/710810/stream/776366", "https://capradio.ma/favicon.ico"),
        new Station("Luxe Radio — لوكس راديو", "ثقافة وأسلوب حياة واقتصاد", "Culture, art de vivre et économie", "Culture, lifestyle and business",
            "https://streaming.luxeradio.ma/luxeradio.mp3", "https://www.luxeradio.ma/favicon.ico"),
        new Station("MFM Radio — إم إف إم", "برامج عامة وموسيقى", "Programmes généralistes et musique", "General programs and music",
            "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico"),
        new Station("Medina FM — مدينة إف إم", "ثقافة ومجتمع من مكناس", "Culture et société depuis Meknès", "Culture and society from Meknes",
            "https://medinafm.ice.infomaniak.ch/medinafm-64.mp3", "https://medinafm.ma/favicon.ico"),
        new Station("Radio Azawan — راديو أزاوان", "موسيقى وثقافة أمازيغية ومغربية", "Musique et culture amazighes et marocaines", "Amazigh and Moroccan music and culture",
            "https://stream.zeno.fm/9y8y2h8w7hhvv", "https://radioazawan.ma/favicon.ico"),

        // ── Radio Plus regional services ─────────────────────────────────────
        new Station("Radio Plus Marrakech — راديو بلوس مراكش", "إذاعة جهوية من مراكش", "Radio régionale de Marrakech", "Marrakech regional radio",
            "https://stream-158.zeno.fm/bqdbb6hd0neuv", "https://radioplus.ma/favicon.ico"),
        new Station("Radio Plus Agadir — راديو بلوس أكادير", "إذاعة جهوية من أكادير", "Radio régionale d'Agadir", "Agadir regional radio",
            "https://stream-158.zeno.fm/bqdbb6hd0neuv", "https://radioplus.ma/favicon.ico"),
        new Station("Radio Plus Fès — راديو بلوس فاس", "إذاعة جهوية من فاس", "Radio régionale de Fès", "Fes regional radio",
            "https://stream-158.zeno.fm/bqdbb6hd0neuv", "https://radioplus.ma/favicon.ico"),
        new Station("Radio Plus Casablanca — راديو بلوس الدار البيضاء", "إذاعة جهوية من الدار البيضاء", "Radio régionale de Casablanca", "Casablanca regional radio",
            "https://stream-158.zeno.fm/bqdbb6hd0neuv", "https://radioplus.ma/favicon.ico"),
        new Station("Radio Plus Béni Mellal — راديو بلوس بني ملال", "إذاعة جهوية من بني ملال", "Radio régionale de Béni Mellal", "Beni Mellal regional radio",
            "https://stream-158.zeno.fm/bqdbb6hd0neuv", "https://radioplus.ma/favicon.ico"),

        // ── MFM regional services ────────────────────────────────────────────
        new Station("MFM Atlas — إم إف إم أطلس", "خدمة إم إف إم لمنطقة الأطلس", "Service MFM de la région Atlas", "MFM Atlas regional service",
            "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico"),
        new Station("MFM Saïss — إم إف إم سايس", "خدمة إم إف إم لمنطقة سايس", "Service MFM de la région Saïss", "MFM Saiss regional service",
            "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico"),
        new Station("MFM Souss — إم إف إم سوس", "خدمة إم إف إم لمنطقة سوس", "Service MFM de la région Souss", "MFM Souss regional service",
            "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico"),
        new Station("MFM Oriental — إم إف إم الشرق", "خدمة إم إف إم للجهة الشرقية", "Service MFM de l'Oriental", "MFM Oriental regional service",
            "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico"),
        new Station("MFM Sahara — إم إف إم الصحراء", "خدمة إم إف إم للأقاليم الجنوبية", "Service MFM du Sahara", "MFM Sahara regional service",
            "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico"),
        new Station("Casa FM — كازا إف إم", "خدمة إذاعية للدار البيضاء", "Service radiophonique de Casablanca", "Casablanca radio service",
            "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico")
    );
}
