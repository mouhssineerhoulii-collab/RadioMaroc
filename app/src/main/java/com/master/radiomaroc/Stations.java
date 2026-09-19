package com.master.radiomaroc;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/** Curated and deduplicated directory; each station keeps ordered fallback streams. */
public final class Stations {
 private Stations(){}
 private static final String H="https://www.haca.ma/sites/default/files/upload/",G=H+"images/rn.jpg",M=H+"images/Logo%20Medi1.jpg",T=H+"images/logohitradio.jpg";
 private static Station s(String n,String ar,String fr,String en,String logo,String cat,String...u){return new Station(n,ar,fr,en,logo,cat,u);}
 /** Keeps the first screen useful: national and requested stations appear before the long directory. */
 private static List<Station> ordered(List<Station> source){
  List<Station> result=new ArrayList<>();
  String[] first={"إذاعة محمد السادس للقرآن الكريم","Radio Aswat","Chada FM","Medi 1 Soufi","Medi 1 Andalouse","Medi 1 Radio","Medi 1 Hits","Medi 1 DJ","Medi 1 Tarab","Medi 1 Lounge","Medi 1 Latino","Medi 1 Jazz"};
  String[] snrt={"Radio Nationale","Radio Amazigh","Chaîne Inter","Radio Casablanca","Radio Fès","Radio Meknès","Radio Tanger","Radio Tétouan","Radio Al Hoceima","Radio Oujda","Radio Marrakech","Radio Agadir","Radio Laâyoune","Radio Dakhla"};
  String[] news={"Al Jazeera Radio Arabic","BBC Radio Arabic","Radio Al Arabiya","Monte Carlo Doualiya","Radio Sky News Arabia","Atlantic Radio","France Maghreb 2"};
  for(String name:first)addByName(source,result,name);
  for(String name:snrt)addByName(source,result,name);
  for(String name:news)addByName(source,result,name);
  for(Station station:source)if(!result.contains(station))result.add(station);
  return result;
 }
 private static void addByName(List<Station> source,List<Station> result,String name){for(Station station:source)if(name.equals(station.name)&&!result.contains(station)){result.add(station);return;}}
 public static final List<Station> ALL=ordered(Arrays.asList(
  // إسلامية وقرآن
  s("إذاعة محمد السادس للقرآن الكريم","القرآن الكريم والبرامج الدينية","Coran et programmes religieux","Quran and religious programs",H+"images/Mohamed%20VI.PNG","islamic","https://cdn.live.easybroadcast.io/live/radio_med_VI/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_mohammed_6/hls_snrt_radio/index.m3u8"),
  s("Medi 1 Soufi","صوفي ومديح","Soufi et chants spirituels","Sufi and spiritual music",M,"islamic","https://cdn.live.easybroadcast.io/medi1radio/Soufi"),
  // عمومية
  s("Radio Nationale","الإذاعة الوطنية المغربية","Radio publique nationale","National public radio",H+"images/radioNationale.PNG","public","https://cdn.live.easybroadcast.io/live/radio_nationale/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_idaa_watanya/hls_snrt_radio/index.m3u8"),
  s("Radio Amazigh","الإذاعة الأمازيغية الوطنية","Radio nationale amazighe","National Amazigh radio",H+"images/amazighia.PNG","public","https://cdn.live.easybroadcast.io/live/radio_amazigh/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_amazigh/hls_snrt_radio/index.m3u8"),
  s("Chaîne Inter","السلسلة الدولية","Chaîne Inter","International public channel",H+"images/chaineInter.PNG","public","https://cdn.live.easybroadcast.io/live/radio_inter/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_chaine_inter/hls_snrt_radio/index.m3u8"),
  s("Radio 2M","راديو دوزيم","Radio 2M","Radio 2M",H+"images/RADIO%202M.jpg","public","https://cdn-globecast.akamaized.net/live/eds/radio_2m/radio_hls_ts_hy217612tge1f21j83/radio_2m.m3u8","https://radio2m-i.akamaihd.net/hls/live/624355/radio2m/playlist-128000.m3u8"),
  // جهوية
  s("Radio Casablanca","إذاعة الدار البيضاء الجهوية","Radio régionale de Casablanca","Casablanca regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_casablanca/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_casa/hls_snrt_radio/index.m3u8"),
  s("Radio Fès","إذاعة فاس الجهوية","Radio régionale de Fès","Fes regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_fes/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_fes/hls_snrt_radio/index.m3u8"),
  s("Radio Meknès","إذاعة مكناس الجهوية","Radio régionale de Meknès","Meknes regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_meknes/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_meknes/hls_snrt_radio/index.m3u8"),
  s("Radio Tanger","إذاعة طنجة الجهوية","Radio régionale de Tanger","Tangier regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_tanger/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tanger/hls_snrt_radio/index.m3u8"),
  s("Radio Tétouan","إذاعة تطوان الجهوية","Radio régionale de Tétouan","Tetouan regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_tetouan/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_tetouan/hls_snrt_radio/index.m3u8"),
  s("Radio Al Hoceima","إذاعة الحسيمة الجهوية","Radio régionale d’Al Hoceima","Al Hoceima regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_houceima/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_el_hoceima/hls_snrt_radio/index.m3u8"),
  s("Radio Oujda","إذاعة وجدة الجهوية","Radio régionale d’Oujda","Oujda regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_oujda/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_oujda/hls_snrt_radio/index.m3u8"),
  s("Radio Marrakech","إذاعة مراكش الجهوية","Radio régionale de Marrakech","Marrakech regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_marrakech/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_marrakech/hls_snrt_radio/index.m3u8"),
  s("Radio Agadir","إذاعة أكادير الجهوية","Radio régionale d’Agadir","Agadir regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_agadir/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_agadir/hls_snrt_radio/index.m3u8"),
  s("Radio Laâyoune","إذاعة العيون الجهوية","Radio régionale de Laâyoune","Laayoune regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_laayoune/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_laayoune/hls_snrt_radio/index.m3u8"),
  s("Radio Dakhla","إذاعة الداخلة الجهوية","Radio régionale de Dakhla","Dakhla regional radio",G,"regional","https://cdn.live.easybroadcast.io/live/radio_dakhla/playlist.m3u8?","https://cdnamd-hls-globecast.akamaized.net/live/ramdisk/radio_dakhla/hls_snrt_radio/index.m3u8"),
  s("Radio Tanger Med","إذاعة طنجة المتوسط","Radio Tanger Med","Tanger Med radio",G,"regional","https://radiotangermed-22.ice.infomaniak.ch/radiotangermed-22-128.mp3"),
  s("Cap Radio","كاب راديو - شمال المغرب","Cap Radio","Northern Morocco",H+"images/CAP.jpg","regional","https://listen.radioking.com/radio/710810/stream/776366"),
  s("Radio Plus Agadir","راديو بلوس أكادير","Radio Plus Agadir","Radio Plus Agadir",G,"regional","https://stream-158.zeno.fm/bqdbb6hd0neuv"),
  // أخبار ورياضة
  s("Medi 1 Radio","ميدي 1 - أخبار وبرامج عامة","Actualités et programmes","News and general programs",M,"news","https://cdn.live.easybroadcast.io/live/83_medi1radio-maghreb_8s9i4bn/playlist.m3u8"),
  s("Atlantic Radio","أتلانتيك - اقتصاد وأخبار","Économie et actualités","Business and news",H+"images/Atlantic-l.jpg","news","https://atlantic-sonic.nindohost.net:9300/stream/1/","https://atlantic-sonic.nindohost.net:9300/stream"),
  s("Radio Al Arabiya","العربية إف إم - أخبار","Al Arabiya FM","Al Arabiya FM news",G,"news","https://fm.alarabiya.net/fm/myStream/playlist.m3u8"),
  s("Radio Sky News Arabia","سكاي نيوز عربية","Sky News Arabia","Sky News Arabia",G,"news","https://radio.skynewsarabia.com/stream/radio/skynewsarabia"),
  s("Al Jazeera Radio Arabic","الجزيرة الإخبارية صوتياً","Al Jazeera arabe","Al Jazeera Arabic",G,"news","https://live-hls-web-aja.getaj.net/AJA/index.m3u8"),
  s("BBC Radio Arabic","بي بي سي عربي","BBC arabe","BBC Arabic",G,"news","https://stream.live.vc.bbcmedia.co.uk/bbc_arabic_radio"),
  s("Monte Carlo Doualiya","مونت كارلو الدولية","Monte Carlo Doualiya","Monte Carlo Doualiya",G,"news","https://montecarlodoualiya128k.ice.infomaniak.ch/mc-doualiya.mp3"),
  s("France Maghreb 2","فرانس مغرب 2","France Maghreb 2","France Maghreb 2",G,"news","https://francemaghreb2.ice.infomaniak.ch/francemaghreb2-high.mp3"),
  s("Radio Mars","راديو مارس - الرياضة المغربية","Radio Mars - sport","Moroccan sports",H+"images/logo-radio-marsN-new%282%29.PNG","sports","https://radiomars.ice.infomaniak.ch/radiomars-128.mp3"),
  // متنوعة
  s("Radio Aswat","أصوات - أخبار وثقافة وبرامج عامة","Information et culture","News, culture and general programs",H+"logo-aswat-HD-1111.jpg","general","https://broadcast.ice.infomaniak.ch/aswat-high.mp3","https://aswat.ice.infomaniak.ch/aswat-high.mp3"),
  s("MFM Radio","إم إف إم","MFM Radio","MFM Radio",H+"MFM%20Radio.jpg","general","https://a5.asurahosting.com:7980/radio.mp3","https://eu.stream4cast.com/proxy/mfmradio/stream"),
  s("Medradio","ميد راديو - مجتمع وحوار","Medradio","Talk radio",H+"images/logo%20med%20vf.PNG","general","https://medradio.ice.infomaniak.ch/medradio-128.mp3"),
  s("Medina FM","مدينة إف إم","Medina FM","Medina FM",H+"images/medina%20fm%20logo%20A.PNG","general","https://medinafm.ice.infomaniak.ch/medinafm-64.mp3","https://medinafm.ice.infomaniak.ch/medinafm-128.mp3"),
  s("Radio RIM","ريم راديو","Radio RIM","Radio RIM",G,"general","https://streaming.rimradio.ma/live"),
  s("Radio Orient","راديو الشرق","Radio Orient","Radio Orient",G,"general","https://stream.rcs.revma.com/7hnrkawf4p8uv.mp3"),
  s("Radio Beur FM","بور إف إم","Beur FM","Beur FM",G,"general","https://beurfm.ice.infomaniak.ch/beurfm-high.mp3"),
  s("Radio Soleil","راديو صولي","Radio Soleil","Radio Soleil",G,"general","https://radiosoleil.ice.infomaniak.ch/radiosoleil-128.mp3"),
  // أمازيغية ومغربية
  s("Radio Azawan","أزاوان - موسيقى أمازيغية","Musique amazighe","Amazigh music",H+"LOGO%20AZAWAN.PNG","amazigh","https://az-maroc.ice.infomaniak.ch/az-maroc-128.mp3"),
  s("Yabiladi Radio","راديو يابلادي","Radio Yabiladi","Yabiladi Radio",G,"general","https://radio.yabiladi.com:8002/stream.mp3"),
  s("Yabiladi Chaabi","يابلادي شعبي","Yabiladi Chaabi","Yabiladi Chaabi",G,"music","https://radio.yabiladi.com:8102/stream.mp3"),
  s("Yabiladi Amazigh","يابلادي أمازيغ","Yabiladi Azawan Amazigh","Yabiladi Amazigh",G,"amazigh","https://radio.yabiladi.com:9002/stream.mp3"),
  s("Yabiladi Nayda","يابلادي نايدا","Yabiladi Nayda","Yabiladi Nayda",G,"music","https://radio.yabiladi.com:9102/stream.mp3"),
  // موسيقى
  s("Chada FM","شدى إف إم - موسيقى وبرامج مغربية","Musique marocaine","Moroccan music",H+"images/LogoCHADAFM.jpg","music","https://edge16.vedge.infomaniak.com/livecast/ik:chadatv/manifest.m3u8","https://edge19.vedge.infomaniak.com/livecast/ik:chadatv/playlist.m3u8","https://stream.bodkas.com/playlist?id=chadafmradio"),
  s("Hit Radio","هيت راديو","Hit Radio","Hit Radio",T,"music","https://hitradio-maroc.ice.infomaniak.ch/hitradio-maroc-128.mp3"),
  s("Hit Radio Classic","هيت راديو كلاسيك","Hit Radio Classic","Hit Radio Classic",T,"music","https://gold.ice.infomaniak.ch/gold-128.mp3"),
  s("Hit Radio Mgharba","هيت راديو مغاربة","Hit Radio Mgharba","Hit Radio Mgharba",T,"music","https://mgharba.ice.infomaniak.ch/mgharba-128.mp3"),
  s("Hit Radio R&B","هيت راديو آر أند بي","Hit Radio R&B","Hit Radio R&B",T,"music","https://rnb.ice.infomaniak.ch/rnb-128.mp3"),
  s("Hit Radio Urban","هيت راديو أوربان","Hit Radio Urban","Hit Radio Urban",T,"music","https://urban.ice.infomaniak.ch/urban-128.aac"),
  s("Hit Radio DanceFloor","هيت راديو دانس فلور","Hit Radio DanceFloor","Hit Radio DanceFloor",T,"music","https://dancefloor.ice.infomaniak.ch/dancefloor-128.mp3"),
  s("Medi 1 Hits","ميدي 1 هيتس","Medi 1 Hits","Medi 1 Hits",M,"music","https://cdn.live.easybroadcast.io/medi1radio/Hits"),
  s("Medi 1 DJ","ميدي 1 دي جي","Medi 1 DJ","Medi 1 DJ",M,"music","https://cdn.live.easybroadcast.io/medi1radio/Dj"),
  s("Medi 1 Tarab","ميدي 1 طرب","Medi 1 Tarab","Medi 1 Tarab",M,"music","https://cdn.live.easybroadcast.io/medi1radio/Tarab"),
  s("Medi 1 Andalouse","ميدي 1 أندلسي","Medi 1 Andalouse","Medi 1 Andalouse",M,"music","https://cdn.live.easybroadcast.io/medi1radio/Andalouse"),
  s("Medi 1 Lounge","ميدي 1 لاونج","Medi 1 Lounge","Medi 1 Lounge",M,"music","https://cdn.live.easybroadcast.io/medi1radio/Lounge"),
  s("Medi 1 Latino","ميدي 1 لاتينو","Medi 1 Latino","Medi 1 Latino",M,"music","https://cdn.live.easybroadcast.io/medi1radio/Latino"),
  s("Medi 1 Jazz","ميدي 1 جاز","Medi 1 Jazz","Medi 1 Jazz",M,"music","https://cdn.live.easybroadcast.io/medi1radio/Jazz"),
  s("Radio Ray","راديو راي","Radio Ray","Radio Ray",G,"music","https://onlyrai.ice.infomaniak.ch/onlyrai-high.mp3"),
  s("Ness Radio","نيس راديو","Ness Radio","Ness Radio",G,"music","https://radio.nessradio.net:8212/nessradio-hd"),
  s("Adwaa FM","أضواء إف إم","Adwaa FM","Adwaa FM",G,"music","https://stream.zeno.fm/5bxh2nh0x1zuv"),
  s("Adwaa FM 2","أضواء إف إم 2","Adwaa FM 2","Adwaa FM 2",G,"music","https://stream.zeno.fm/vrrtqsh0x1zuv"),
  s("Adwaa FM 3","أضواء إف إم 3","Adwaa FM 3","Adwaa FM 3",G,"music","https://stream.zeno.fm/x4451xh0x1zuv"),
  s("Adwaa FM 4","أضواء إف إم 4","Adwaa FM 4","Adwaa FM 4",G,"music","https://stream.zeno.fm/8wrs2bh0x1zuv","https://node-27.zeno.fm/8wrs2bh0x1zuv"),
  s("Adwaa FM 6","أضواء إف إم 6","Adwaa FM 6","Adwaa FM 6",G,"music","https://stream.zeno.fm/tvxvud2kpm0uv"),
  s("Adwaa FM 7","أضواء إف إم 7","Adwaa FM 7","Adwaa FM 7",G,"music","https://node-18.zeno.fm/v83swh05098uv"),
  s("Tarab Radio","راديو طرب","Tarab Radio","Tarab Radio",G,"music","https://stream.zeno.fm/fy8achbq97zuv"),
  // أجنبية
  s("ALLZIC ORIENTALE","ألزيك أورينتال","ALLZIC Orientale","ALLZIC Orientale",G,"foreign","https://allzic33.ice.infomaniak.ch/allzic33.mp3"),
  s("Skyrock Casablanca","سكاي روك الدار البيضاء","Skyrock Casablanca","Skyrock Casablanca",G,"foreign","https://icecast.skyrock.net/s/casa_aac_64k")
 ));
 public static Station find(String n){if(n==null)return null;for(Station s:ALL)if(s.name.equals(n))return s;return null;}
}
