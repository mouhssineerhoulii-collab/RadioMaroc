package com.master.radiomaroc;

import java.util.Arrays;
import java.util.List;

/** Official Moroccan radio catalogue based on HACA/SNRT service lists.
 * Station names are kept in their official French/Latin form as requested.
 * Empty streamUrl means the station is listed officially but no distinct HTTPS
 * web stream has been verified for this build; the app will not fake one.
 */
public final class Stations {
    private Stations() {}

    public static final List<Station> ALL = Arrays.asList(
        new Station("Radio Nationale", "إذاعة عمومية وطنية", "Radio publique nationale", "National public radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_idaa_watanya/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Amazigh", "إذاعة وطنية أمازيغية", "Radio nationale amazighe", "National Amazigh radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_amazigh/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio 2M", "إذاعة وطنية عامة وموسيقية", "Radio nationale généraliste et musicale", "National general and music radio", "https://radio2m-i.akamaihd.net/hls/live/624355/radio2m/playlist-128000.m3u8", "https://radio2m.ma/favicon.ico"),
        new Station("Chaîne Inter", "أخبار وثقافة وبرامج دولية", "Actualités, culture et programmes internationaux", "News, culture and international programs", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_chaine_inter/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Mohammed VI du Saint Coran", "القرآن الكريم والبرامج الدينية", "Coran et programmes religieux", "Quran and religious programs", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_mohammed_6/hls_snrt_radio/radio_mohammed_6-mp4a_130400_qad=1.m3u8", "https://www.snrt.ma/favicon.ico"),

        new Station("Radio Régionale de Casablanca", "إذاعة جهوية للدار البيضاء", "Radio régionale de Casablanca", "Casablanca regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_casa/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale de Fès", "إذاعة جهوية من فاس", "Radio régionale de Fès", "Fes regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_fes/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale de Meknès", "إذاعة جهوية من مكناس", "Radio régionale de Meknès", "Meknes regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_meknes/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale de Tanger", "إذاعة جهوية من طنجة", "Radio régionale de Tanger", "Tangier regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tanger/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale de Tétouan", "إذاعة جهوية من تطوان", "Radio régionale de Tétouan", "Tetouan regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tetouan/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale d’El Hoceima", "إذاعة جهوية من الحسيمة", "Radio régionale d’El Hoceima", "Al Hoceima regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_el_hoceima/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale d’Oujda", "إذاعة جهوية من وجدة", "Radio régionale d’Oujda", "Oujda regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_oujda/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale de Marrakech", "إذاعة جهوية من مراكش", "Radio régionale de Marrakech", "Marrakech regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_marrakech/hls_snrt_radio/radio_marrakech-mp4a_130400_qad=1.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale d’Agadir", "إذاعة جهوية من أكادير", "Radio régionale d’Agadir", "Agadir regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_agadir/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale de Laâyoune", "إذاعة جهوية من العيون", "Radio régionale de Laâyoune", "Laayoune regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_laayoune/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),
        new Station("Radio Régionale de Dakhla", "إذاعة جهوية من الداخلة", "Radio régionale de Dakhla", "Dakhla regional radio", "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_dakhla/hls_snrt_radio/index.m3u8", "https://www.snrt.ma/favicon.ico"),

        new Station("Medi 1 Radio", "أخبار وبرامج عامة", "Actualités et programmes généralistes", "News and general programs", "https://cdn.live.easybroadcast.io/live/83_medi1radio-maghreb_8s9i4bn/playlist.m3u8", "https://www.medi1.com/favicon.ico"),
        new Station("Hit Radio", "موسيقى وشباب", "Musique et jeunesse", "Music and youth", "https://hitradio-maroc.ice.infomaniak.ch/hitradio-maroc-128.mp3", "https://www.hitradio.ma/favicon.ico"),
        new Station("Radio Aswat", "أخبار وثقافة وبرامج عامة", "Information, culture et programmes généralistes", "News, culture and general programs", "https://broadcast.ice.infomaniak.ch/aswat-high.mp3", "https://www.aswat.ma/favicon.ico"),
        new Station("Atlantic Radio", "اقتصاد وأخبار وبرامج عامة", "Économie, actualités et programmes", "Business, news and general programs", "https://atlantic-sonic.nindohost.net:9300/stream", "https://www.atlanticradio.ma/favicon.ico"),
        new Station("Chada FM", "موسيقى وبرامج مغربية", "Musique et programmes marocains", "Moroccan music and programs", "https://broadcast.infomaniak.net/chadafm-high.mp3", "https://chada.ma/favicon.ico"),
        new Station("Radio Mars", "رياضة وبرامج عامة", "Sport et programmes généralistes", "Sports and general programs", "https://radiomars.ice.infomaniak.ch/radiomars-128.mp3", "https://www.radiomars.ma/favicon.ico"),
        new Station("Medradio", "أخبار وبرامج اجتماعية وحوارية", "Information, société et débats", "News, social programs and talk", "https://medradio.ice.infomaniak.ch/medradio-128.mp3", "https://medradio.ma/favicon.ico"),
        new Station("Cap Radio", "طنجة وشمال المغرب", "Tanger et nord du Maroc", "Tangier and northern Morocco", "https://listen.radioking.com/radio/710810/stream/776366", "https://capradio.ma/favicon.ico"),
        new Station("LUXE RADIO", "ثقافة وأسلوب حياة واقتصاد", "Culture, art de vivre et économie", "Culture, lifestyle and business", "https://streaming.luxeradio.ma/luxeradio.mp3", "https://www.luxeradio.ma/favicon.ico"),
        new Station("MFM RADIO", "برامج عامة وموسيقى", "Programmes généralistes et musique", "General programs and music", "https://a5.asurahosting.com:7980/radio.mp3", "https://mfmradio.ma/favicon.ico"),
        new Station("MEDINA FM", "ثقافة ومجتمع", "Culture et société", "Culture and society", "https://medinafm.ice.infomaniak.ch/medinafm-64.mp3", "https://medinafm.ma/favicon.ico"),
        new Station("Radio AZAWAN", "موسيقى وثقافة أمازيغية ومغربية", "Musique et culture amazighes et marocaines", "Amazigh and Moroccan music and culture", "https://stream.zeno.fm/9y8y2h8w7hhvv", "https://radioazawan.ma/favicon.ico"),

        new Station("Radio Plus Marrakech", "خدمة راديو بلوس بمراكش", "Service Radio Plus Marrakech", "Radio Plus Marrakech service", "", "https://radioplus.ma/favicon.ico"),
        new Station("Radio Plus Agadir", "خدمة راديو بلوس بأكادير", "Service Radio Plus Agadir", "Radio Plus Agadir service", "", "https://radioplus.ma/favicon.ico"),
        new Station("Radio Plus Fès", "خدمة راديو بلوس بفاس", "Service Radio Plus Fès", "Radio Plus Fes service", "", "https://radioplus.ma/favicon.ico"),
        new Station("Radio Plus Casablanca", "خدمة راديو بلوس بالدار البيضاء", "Service Radio Plus Casablanca", "Radio Plus Casablanca service", "", "https://radioplus.ma/favicon.ico"),
        new Station("Radio Plus Béni Mellal", "خدمة راديو بلوس ببني ملال", "Service Radio Plus Béni Mellal", "Radio Plus Beni Mellal service", "", "https://radioplus.ma/favicon.ico"),
        new Station("MFM Atlas", "خدمة إم إف إم أطلس", "Service MFM Atlas", "MFM Atlas service", "", "https://mfmradio.ma/favicon.ico"),
        new Station("MFM Saïss", "خدمة إم إف إم سايس", "Service MFM Saïss", "MFM Saiss service", "", "https://mfmradio.ma/favicon.ico"),
        new Station("MFM Souss", "خدمة إم إف إم سوس", "Service MFM Souss", "MFM Souss service", "", "https://mfmradio.ma/favicon.ico"),
        new Station("MFM Oriental", "خدمة إم إف إم الشرق", "Service MFM Oriental", "MFM Oriental service", "", "https://mfmradio.ma/favicon.ico"),
        new Station("MFM Sahara", "خدمة إم إف إم الصحراء", "Service MFM Sahara", "MFM Sahara service", "", "https://mfmradio.ma/favicon.ico"),
        new Station("Casa FM", "خدمة إذاعية للدار البيضاء", "Service radiophonique de Casablanca", "Casablanca radio service", "", "https://mfmradio.ma/favicon.ico")
    );
}
