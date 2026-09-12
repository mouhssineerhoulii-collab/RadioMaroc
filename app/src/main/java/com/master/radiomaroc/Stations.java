package com.master.radiomaroc;

import java.util.Arrays;
import java.util.List;

public final class Stations {
    private Stations() {}
    private static final String H="https://www.haca.ma/sites/default/files/upload/";
    private static Station s(String n,String ar,String fr,String en,String logo,String cat,String... urls){return new Station(n,ar,fr,en,logo,cat,urls);}

    public static final List<Station> ALL=Arrays.asList(
        s("إذاعة محمد السادس للقرآن الكريم","القرآن الكريم والبرامج الدينية","Coran et programmes religieux","Quran and religious programs",H+"images/Mohamed%20VI.PNG","quran",
          "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_mohammed_6/hls_snrt_radio/index.m3u8",
          "https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_mohammed_6/hls_snrt_radio/radio_mohammed_6-mp4a_130400_qad=1.m3u8"),
        s("Radio Tanger Med","إذاعة طنجة المتوسط - خدمة العبور والميناء","Radio Tanger Med - service portuaire","Tanger Med port radio",H+"images/rn.jpg","regional",
          "https://radiotangermed-22.ice.infomaniak.ch/radiotangermed-22-128.mp3"),
        s("Radio Aswat","أصوات - أخبار وثقافة وبرامج عامة","Information, culture et programmes généralistes","News, culture and general programs",H+"logo-aswat-HD-1111.jpg","general",
          "https://aswat.ice.infomaniak.ch/aswat-high.mp3","https://broadcast.ice.infomaniak.ch/aswat-high.mp3"),
        s("Chada FM","شدى إف إم - موسيقى وبرامج مغربية","Musique et programmes marocains","Moroccan music and programs",H+"images/LogoCHADAFM.jpg","music",
          "https://broadcast.infomaniak.ch/chadafm-high.mp3","https://broadcast.infomaniak.net/chadafm-high.mp3"),

        s("Medi 1 Radio","ميدي 1 - أخبار وبرامج عامة","Actualités et programmes généralistes","News and general programs",H+"images/Logo%20Medi1.jpg","news",
          "https://cdn.live.easybroadcast.io/live/83_medi1radio-maghreb_8s9i4bn/playlist.m3u8"),
        s("Medi 1 Tarab","ميدي 1 طرب - الطرب العربي الأصيل","Tarab arabe classique","Classic Arabic tarab",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Tarab"),
        s("Medi 1 Andalouse","ميدي 1 أندلسي - الموسيقى الأندلسية المغربية","Musique andalouse marocaine","Moroccan Andalusian music",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Andalouse"),
        s("Medi 1 Soufi","ميدي 1 صوفي - المديح والموسيقى الروحية","Musique soufie et spirituelle","Sufi and spiritual music",H+"images/Logo%20Medi1.jpg","religious","https://live.medi1.com/Soufi"),
        s("Medi 1 Nayda","ميدي 1 نايدة - الموسيقى المغربية المعاصرة","Scène marocaine contemporaine","Contemporary Moroccan music",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Nayda"),
        s("Medi 1 Classique","ميدي 1 كلاسيك","Musique classique","Classical music",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Classique"),
        s("Medi 1 Acoustic","ميدي 1 أكوستيك","Acoustic","Acoustic",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Acoustic"),
        s("Medi 1 Lounge","ميدي 1 لاونج","Lounge","Lounge",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Lounge"),
        s("Medi 1 Latino","ميدي 1 لاتينو","Latino","Latino",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Latino"),
        s("Medi 1 Jazz","ميدي 1 جاز","Jazz","Jazz",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Jazz"),
        s("Medi 1 DJ","ميدي 1 دي جي","DJ","DJ",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Dj"),
        s("Medi 1 Hits","ميدي 1 هيتس","Hits","Hits",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Hits"),
        s("Medi 1 Française","ميدي 1 فرنسية","Musique française","French music",H+"images/Logo%20Medi1.jpg","music","https://live.medi1.com/Francaise"),

        s("Radio Nationale","الإذاعة الوطنية المغربية","Radio publique nationale","National public radio",H+"images/radioNationale.PNG","public","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_idaa_watanya/hls_snrt_radio/index.m3u8"),
        s("Radio Amazigh","الإذاعة الأمازيغية","Radio nationale amazighe","National Amazigh radio",H+"images/amazighia.PNG","amazigh","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_amazigh/hls_snrt_radio/index.m3u8"),
        s("Chaîne Inter","السلسلة الدولية - أخبار وثقافة","Actualités, culture et international","News, culture and international",H+"images/chaineInter.PNG","public","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_chaine_inter/hls_snrt_radio/index.m3u8"),
        s("Radio 2M","راديو دوزيم","Radio nationale généraliste et musicale","General and music radio",H+"images/RADIO%202M.jpg","general","https://radio2m-i.akamaihd.net/hls/live/624355/radio2m/playlist-128000.m3u8"),
        s("Hit Radio","هيت راديو - موسيقى وشباب","Musique et jeunesse","Music and youth",H+"images/logohitradio.jpg","music","https://hitradio-maroc.ice.infomaniak.ch/hitradio-maroc-128.mp3"),
        s("Radio Mars","راديو مارس - الرياضة المغربية","Sport et programmes généralistes","Moroccan sports",H+"images/logo-radio-marsN-new%282%29.PNG","sports","https://radiomars.ice.infomaniak.ch/radiomars-128.mp3"),
        s("Medradio","ميد راديو - مجتمع وحوار","Information, société et débats","News, society and talk",H+"images/logo%20med%20vf.PNG","talk","https://medradio.ice.infomaniak.ch/medradio-128.mp3"),
        s("Atlantic Radio","أتلانتيك - اقتصاد وأخبار","Économie et actualités","Business and news",H+"images/Atlantic-l.jpg","news","https://atlantic-sonic.nindohost.net:9300/stream"),
        s("Cap Radio","كاب راديو - طنجة وشمال المغرب","Tanger et nord du Maroc","Tangier and northern Morocco",H+"images/CAP.jpg","regional","https://listen.radioking.com/radio/710810/stream/776366"),
        s("Luxe Radio","لوكس راديو - ثقافة وأسلوب حياة","Culture et art de vivre","Culture and lifestyle",H+"images/logo-luxeradio.PNG","culture","https://streaming.luxeradio.ma/luxeradio.mp3"),
        s("MFM Radio","إم إف إم - برامج وموسيقى","Programmes généralistes et musique","General programs and music",H+"MFM%20Radio.jpg","general","https://a5.asurahosting.com:7980/radio.mp3"),
        s("Medina FM","مدينة إف إم - ثقافة ومجتمع","Culture et société","Culture and society",H+"images/medina%20fm%20logo%20A.PNG","culture","https://medinafm.ice.infomaniak.ch/medinafm-128.mp3","https://medinafm.ice.infomaniak.ch/medinafm-64.mp3"),
        s("Radio Azawan","أزاوان - موسيقى وثقافة أمازيغية","Culture et musique amazighes","Amazigh music and culture",H+"LOGO%20AZAWAN.PNG","amazigh","https://stream.zeno.fm/9y8y2h8w7hhvv"),
        s("Yabiladi Radio","يابلادي - موسيقى مغربية","Musique marocaine","Moroccan music",H+"images/rn.jpg","music","https://radio.yabiladi.com:8000/;stream.mp3"),
        s("Yabiladi Chaabi","يابلادي شعبي","Chaabi marocain","Moroccan chaabi",H+"images/rn.jpg","music","https://radio.yabiladi.com:8102/;stream.mp3"),

        s("Radio Casablanca","إذاعة الدار البيضاء الجهوية","Radio régionale de Casablanca","Casablanca regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_casa/hls_snrt_radio/index.m3u8"),
        s("Radio Fès","إذاعة فاس الجهوية","Radio régionale de Fès","Fes regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_fes/hls_snrt_radio/index.m3u8"),
        s("Radio Meknès","إذاعة مكناس الجهوية","Radio régionale de Meknès","Meknes regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_meknes/hls_snrt_radio/index.m3u8"),
        s("Radio Tanger","إذاعة طنجة الجهوية","Radio régionale de Tanger","Tangier regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tanger/hls_snrt_radio/index.m3u8"),
        s("Radio Tétouan","إذاعة تطوان الجهوية","Radio régionale de Tétouan","Tetouan regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tetouan/hls_snrt_radio/index.m3u8"),
        s("Radio Al Hoceima","إذاعة الحسيمة الجهوية","Radio régionale d’Al Hoceima","Al Hoceima regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_el_hoceima/hls_snrt_radio/index.m3u8"),
        s("Radio Oujda","إذاعة وجدة الجهوية","Radio régionale d’Oujda","Oujda regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_oujda/hls_snrt_radio/index.m3u8"),
        s("Radio Marrakech","إذاعة مراكش الجهوية","Radio régionale de Marrakech","Marrakech regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_marrakech/hls_snrt_radio/index.m3u8"),
        s("Radio Agadir","إذاعة أكادير الجهوية","Radio régionale d’Agadir","Agadir regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_agadir/hls_snrt_radio/index.m3u8"),
        s("Radio Laâyoune","إذاعة العيون الجهوية","Radio régionale de Laâyoune","Laayoune regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_laayoune/hls_snrt_radio/index.m3u8"),
        s("Radio Dakhla","إذاعة الداخلة الجهوية","Radio régionale de Dakhla","Dakhla regional radio",H+"images/rn.jpg","regional","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_dakhla/hls_snrt_radio/index.m3u8"),
        s("Radio Plus Agadir","راديو بلوس أكادير","Radio Plus Agadir","Radio Plus Agadir",H+"radio%20plus%20agadir_0.jpg","regional","https://stream-158.zeno.fm/bqdbb6hd0neuv")
    );

    public static Station find(String name){if(name==null)return null;for(Station s:ALL)if(s.name.equals(name))return s;return null;}
}
