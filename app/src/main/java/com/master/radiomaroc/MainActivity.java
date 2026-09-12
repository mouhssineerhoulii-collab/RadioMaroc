package com.master.radiomaroc;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;

import java.util.*;

public class MainActivity extends Activity {
    private LinearLayout root;
    private TextView appTitle, appSubtitle, settingsButton, nowPlayingLabel, nowPlaying, status;
    private EditText searchBox;
    private Button allButton, favoritesButton, recentButton, viewButton, toggleButton, stopButton;
    private Button homeNavButton, favoritesNavButton, settingsNavButton;
    private ImageView playerLogo;
    private GridLayout stationsGrid;

    private SharedPreferences prefs;
    private String lang;
    private String viewMode;
    private String filterMode = "all";
    private Set<String> favorites;
    private final List<String> recent = new ArrayList<>();
    private String currentStationName = "";
    private boolean currentPlaying = false;

    private final BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra("state");
            String stationName = intent.getStringExtra("station");
            currentPlaying = intent.getBooleanExtra("playing", false);
            if (stationName != null && !stationName.isEmpty()) {
                currentStationName = stationName;
                nowPlaying.setText(stationName);
                Station s = findStation(stationName);
                if (s != null) LogoLoader.load(s.logoUrl, s.name, playerLogo, R.drawable.ic_app);
            }
            if (state != null) status.setText(stateText(state));
            toggleButton.setText(currentPlaying ? "Ⅱ" : "▶");
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("radio_maroc_prefs", MODE_PRIVATE);
        lang = prefs.getString("lang", "en");
        viewMode = prefs.getString("view", "list");
        favorites = new HashSet<>(prefs.getStringSet("favorites", new HashSet<>()));
        loadRecent();
        bindViews();
        applyLanguage();
        requestNotificationPermission();
        buildStationList("");

        stopButton.setOnClickListener(v -> stopRadio());
        toggleButton.setOnClickListener(v -> toggleRadio());
        allButton.setOnClickListener(v -> setFilter("all"));
        favoritesButton.setOnClickListener(v -> setFilter("favorites"));
        recentButton.setOnClickListener(v -> setFilter("recent"));
        viewButton.setOnClickListener(v -> cycleViewMode());
        settingsButton.setOnClickListener(v -> showSettings());
        homeNavButton.setOnClickListener(v -> setFilter("all"));
        favoritesNavButton.setOnClickListener(v -> setFilter("favorites"));
        settingsNavButton.setOnClickListener(v -> showSettings());

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                buildStationList(s == null ? "" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        updateModeButtons();
        updateViewIcon();
    }

    private void bindViews() {
        root = findViewById(R.id.root);
        appTitle = findViewById(R.id.appTitle); appSubtitle = findViewById(R.id.appSubtitle);
        settingsButton = findViewById(R.id.settingsButton); searchBox = findViewById(R.id.searchBox);
        allButton = findViewById(R.id.allButton); favoritesButton = findViewById(R.id.favoritesButton);
        recentButton = findViewById(R.id.recentButton); viewButton = findViewById(R.id.viewButton);
        playerLogo = findViewById(R.id.playerLogo); nowPlayingLabel = findViewById(R.id.nowPlayingLabel);
        nowPlaying = findViewById(R.id.nowPlaying); status = findViewById(R.id.status);
        toggleButton = findViewById(R.id.toggleButton); stopButton = findViewById(R.id.stopButton);
        stationsGrid = findViewById(R.id.stationsGrid); homeNavButton = findViewById(R.id.homeNavButton);
        favoritesNavButton = findViewById(R.id.favoritesNavButton); settingsNavButton = findViewById(R.id.settingsNavButton);
    }

    private void applyLanguage() {
        boolean rtl = "ar".equals(lang);
        root.setLayoutDirection(rtl ? LinearLayout.LAYOUT_DIRECTION_RTL : LinearLayout.LAYOUT_DIRECTION_LTR);
        appTitle.setText("Radio Maroc");
        appSubtitle.setText(t("صوت المغرب أينما كنت", "La voix du Maroc, partout avec vous", "Moroccan voices, everywhere with you"));
        searchBox.setHint(t("ابحث عن محطة…", "Rechercher une station…", "Search stations…"));
        allButton.setText(t("الكل", "Toutes", "All"));
        favoritesButton.setText(t("المفضلة", "Favoris", "Favorites"));
        recentButton.setText(t("الأخيرة", "Récentes", "Recent"));
        nowPlayingLabel.setText(t("الآن على الهواء", "EN DIRECT", "NOW PLAYING"));
        if (currentStationName.isEmpty()) nowPlaying.setText(t("اختر محطة", "Choisissez une station", "Choose a station"));
        status.setText(stateText(currentStationName.isEmpty() ? "stopped" : (currentPlaying ? "playing" : "paused")));
        settingsButton.setContentDescription(t("الإعدادات", "Paramètres", "Settings"));
        homeNavButton.setText(t("⌂\nالرئيسية", "⌂\nAccueil", "⌂\nHome"));
        favoritesNavButton.setText(t("★\nالمفضلة", "★\nFavoris", "★\nFavorites"));
        settingsNavButton.setText(t("⚙\nالإعدادات", "⚙\nParamètres", "⚙\nSettings"));
    }

    private String t(String ar, String fr, String en) {
        if ("fr".equals(lang)) return fr;
        if ("ar".equals(lang)) return ar;
        return en;
    }

    private String stateText(String state) {
        if ("connecting".equals(state)) return t("جاري الاتصال…", "Connexion…", "Connecting…");
        if ("playing".equals(state)) return t("● يعمل الآن", "● Lecture", "● Playing");
        if ("paused".equals(state)) return t("متوقف مؤقتًا", "En pause", "Paused");
        if ("error".equals(state)) return t("تعذر تشغيل المحطة", "Lecture impossible", "Playback unavailable");
        return t("متوقف", "Arrêté", "Stopped");
    }

    private void setFilter(String mode) {
        filterMode = mode; updateModeButtons(); buildStationList(searchBox.getText().toString());
    }

    private void buildStationList(String query) {
        stationsGrid.removeAllViews();
        boolean grid = "grid".equals(viewMode), compact = "compact".equals(viewMode);
        stationsGrid.setColumnCount(grid ? 2 : 1);
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        for (Station station : Stations.ALL) {
            if ("favorites".equals(filterMode) && !favorites.contains(station.name)) continue;
            if ("recent".equals(filterMode) && !recent.contains(station.name)) continue;
            String searchable = (station.name + " " + station.subtitleAr + " " + station.subtitleFr + " " + station.subtitleEn).toLowerCase(Locale.ROOT);
            if (!q.isEmpty() && !searchable.contains(q)) continue;

            final boolean available = station.hasStream();
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(grid ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(compact ? 8 : 11), dp(compact ? 7 : 10), dp(compact ? 8 : 11), dp(compact ? 7 : 10));
            card.setBackgroundResource(R.drawable.card_bg);
            card.setAlpha(available ? 1f : .62f);
            GridLayout.LayoutParams cp = new GridLayout.LayoutParams();
            cp.setMargins(dp(4), dp(4), dp(4), dp(4));
            cp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (grid) { cp.width = 0; cp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); }
            else { cp.width = ViewGroup.LayoutParams.MATCH_PARENT; cp.columnSpec = GridLayout.spec(0, 1f); }
            card.setLayoutParams(cp);

            ImageView logo = new ImageView(this);
            int logoSize = dp(grid ? 96 : (compact ? 42 : 58));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(logoSize, logoSize);
            if (grid) { lp.gravity = Gravity.CENTER_HORIZONTAL; lp.bottomMargin = dp(8); } else lp.setMarginEnd(dp(11));
            logo.setLayoutParams(lp); logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            logo.setPadding(dp(3), dp(3), dp(3), dp(3)); logo.setBackgroundResource(R.drawable.logo_bg);
            LogoLoader.load(station.logoUrl, station.name, logo, R.drawable.ic_app);

            LinearLayout info = new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL);
            info.setGravity(grid ? Gravity.CENTER_HORIZONTAL : Gravity.CENTER_VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(grid ? ViewGroup.LayoutParams.MATCH_PARENT : 0,
                ViewGroup.LayoutParams.WRAP_CONTENT, grid ? 0 : 1f));

            TextView name = new TextView(this); name.setText(station.name); name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            name.setTextSize(compact ? 14 : (grid ? 16 : 17)); name.setTextColor(getColor(R.color.text));
            name.setGravity(grid ? Gravity.CENTER : Gravity.START); name.setMaxLines(grid ? 2 : 1);
            TextView sub = new TextView(this); sub.setText(station.subtitle(lang)); sub.setTextColor(getColor(R.color.muted));
            sub.setTextSize(compact ? 10 : 12); sub.setGravity(grid ? Gravity.CENTER : Gravity.START); sub.setMaxLines(2);
            if (!available) sub.setText(sub.getText() + "  •  " + t("غير متاحة مؤقتًا", "Indisponible temporairement", "Temporarily unavailable"));
            info.addView(name); info.addView(sub);

            LinearLayout actions = new LinearLayout(this); actions.setGravity(Gravity.CENTER);
            TextView star = actionText(favorites.contains(station.name) ? "★" : "☆", R.color.gold);
            star.setOnClickListener(v -> toggleFavorite(station.name, star));
            TextView play = actionText(available ? "▶" : "—", R.color.gold_light);
            play.setAlpha(available ? 1f : .35f);
            if (available) play.setOnClickListener(v -> playStation(station));
            actions.addView(star); actions.addView(play);
            card.addView(logo); card.addView(info); card.addView(actions);
            if (available) card.setOnClickListener(v -> playStation(station));
            stationsGrid.addView(card);
        }
    }

    private TextView actionText(String text, int color) {
        TextView v = new TextView(this); v.setText(text); v.setTextSize(23); v.setTextColor(getColor(color));
        v.setGravity(Gravity.CENTER); v.setPadding(dp(8), 0, dp(8), 0); return v;
    }

    private void toggleFavorite(String name, TextView star) {
        if (favorites.contains(name)) favorites.remove(name); else favorites.add(name);
        prefs.edit().putStringSet("favorites", new HashSet<>(favorites)).apply();
        star.setText(favorites.contains(name) ? "★" : "☆");
        if ("favorites".equals(filterMode)) buildStationList(searchBox.getText().toString());
    }

    private void cycleViewMode() {
        viewMode = "list".equals(viewMode) ? "compact" : ("compact".equals(viewMode) ? "grid" : "list");
        prefs.edit().putString("view", viewMode).apply(); updateViewIcon(); buildStationList(searchBox.getText().toString());
    }
    private void updateViewIcon() { viewButton.setText("grid".equals(viewMode) ? "▦" : ("compact".equals(viewMode) ? "☷" : "☰")); }
    private void updateModeButtons() {
        allButton.setAlpha("all".equals(filterMode) ? 1f : .55f); favoritesButton.setAlpha("favorites".equals(filterMode) ? 1f : .55f); recentButton.setAlpha("recent".equals(filterMode) ? 1f : .55f);
    }

    private void showSettings() {
        String[] items = {
            "🌐  " + t("اللغة", "Langue", "Language"),
            "▦  " + t("طريقة العرض", "Mode d’affichage", "Display mode"),
            "◷  " + t("مؤقت النوم", "Minuterie", "Sleep timer"),
            "ⓘ  " + t("حول البرنامج", "À propos", "About"),
            "🔒  " + t("الخصوصية والأمان", "Confidentialité et sécurité", "Privacy & security")
        };
        new AlertDialog.Builder(this).setTitle("⚙  " + t("الإعدادات", "Paramètres", "Settings"))
            .setItems(items, (d, w) -> { if (w==0) showLanguageDialog(); else if (w==1) showViewDialog(); else if (w==2) showSleepDialog(); else if (w==3) showAboutDialog(); else showPrivacyDialog(); })
            .setNegativeButton(t("إغلاق", "Fermer", "Close"), null).show();
    }

    private void showLanguageDialog() {
        String[] options = {"English", "Français", "العربية"}; int checked = "en".equals(lang) ? 0 : ("fr".equals(lang) ? 1 : 2);
        new AlertDialog.Builder(this).setTitle("🌐  " + t("اللغة", "Langue", "Language"))
            .setSingleChoiceItems(options, checked, (d,w) -> { lang = w==0?"en":(w==1?"fr":"ar"); prefs.edit().putString("lang", lang).apply(); d.dismiss(); recreate(); }).show();
    }
    private void showViewDialog() {
        String[] options = {t("قائمة", "Liste", "List"), t("قائمة مدمجة", "Liste compacte", "Compact"), t("صور مصغرة", "Vignettes", "Thumbnails")};
        int checked = "list".equals(viewMode)?0:("compact".equals(viewMode)?1:2);
        new AlertDialog.Builder(this).setTitle(t("طريقة العرض", "Mode d’affichage", "Display mode"))
            .setSingleChoiceItems(options, checked, (d,w)->{ viewMode=w==0?"list":(w==1?"compact":"grid"); prefs.edit().putString("view",viewMode).apply(); d.dismiss(); updateViewIcon(); buildStationList(searchBox.getText().toString()); }).show();
    }
    private void showSleepDialog() {
        String[] o = {t("إيقاف", "Désactivé", "Off"), "15 min", "30 min", "60 min"};
        new AlertDialog.Builder(this).setTitle(t("مؤقت النوم", "Minuterie", "Sleep timer")).setItems(o,(d,w)->{
            int m=w==1?15:(w==2?30:(w==3?60:0)); Intent i=new Intent(this,RadioService.class).setAction(RadioService.ACTION_SLEEP); i.putExtra(RadioService.EXTRA_MINUTES,m); startService(i);
        }).show();
    }
    private void showAboutDialog() {
        String msg = t(
            "راديو المغرب تطبيق مجاني ومفتوح المصدر بدون إشهارات. المبرمج M@ster يسعى إلى خدمة الجالية المغربية بالخارج من خلال توفير وسيلة بسيطة وآمنة للاستماع إلى الإذاعات المغربية أينما كانوا.",
            "Radio Maroc est une application gratuite, open source et sans publicité. Le développeur M@ster souhaite servir la communauté marocaine à l’étranger en offrant un moyen simple et sûr d’écouter les radios marocaines partout dans le monde.",
            "Radio Maroc is a free, open-source and ad-free application. Developer M@ster created it to serve Moroccans abroad by providing a simple and secure way to listen to Moroccan radio anywhere in the world."
        );
        new AlertDialog.Builder(this).setTitle("Radio Maroc • M@ster").setMessage(msg + "\n\nGPL-3.0-or-later • No ads • No tracking").setPositiveButton("OK",null).show();
    }
    private void showPrivacyDialog() {
        String msg=t("لا إعلانات، لا تحليلات، لا تتبع، ولا جمع للبيانات الشخصية. الكود مفتوح المصدر ويمكن تدقيقه. لا يُستخدم HTTP غير المشفر للبث في هذه النسخة.",
            "Aucune publicité, analytique, traçage ni collecte de données personnelles. Le code est open source et vérifiable. Aucun flux HTTP non chiffré n’est utilisé dans cette version.",
            "No ads, analytics, tracking, or personal-data collection. The source code is open for audit. This build does not use unencrypted HTTP streams.");
        new AlertDialog.Builder(this).setTitle("🔒  "+t("الخصوصية والأمان","Confidentialité et sécurité","Privacy & security")).setMessage(msg).setPositiveButton("OK",null).show();
    }

    private void playStation(Station station) {
        if (!station.hasStream()) { Toast.makeText(this, t("هذه المحطة غير متاحة مؤقتًا", "Cette station est temporairement indisponible", "This station is temporarily unavailable"), Toast.LENGTH_LONG).show(); return; }
        currentStationName=station.name; nowPlaying.setText(station.name); status.setText(stateText("connecting"));
        LogoLoader.load(station.logoUrl, station.name, playerLogo, R.drawable.ic_app); addRecent(station.name);
        Intent i=new Intent(this,RadioService.class).setAction(RadioService.ACTION_PLAY); i.putExtra(RadioService.EXTRA_NAME,station.name); i.putExtra(RadioService.EXTRA_URL,station.streamUrl);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) startForegroundService(i); else startService(i);
    }
    private void toggleRadio(){ startService(new Intent(this,RadioService.class).setAction(RadioService.ACTION_TOGGLE)); }
    private void stopRadio(){ startService(new Intent(this,RadioService.class).setAction(RadioService.ACTION_STOP)); currentPlaying=false; toggleButton.setText("▶"); status.setText(stateText("stopped")); }
    private Station findStation(String n){ for(Station s:Stations.ALL) if(s.name.equals(n)) return s; return null; }
    private void addRecent(String n){ recent.remove(n); recent.add(0,n); while(recent.size()>10) recent.remove(recent.size()-1); prefs.edit().putString("recent",String.join("\u001F",recent)).apply(); }
    private void loadRecent(){ String r=prefs.getString("recent",""); if(r!=null&&!r.isEmpty()) recent.addAll(Arrays.asList(r.split("\\u001F"))); }
    private void requestNotificationPermission(){ if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},100); }
    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }

    @Override protected void onStart(){ super.onStart(); IntentFilter f=new IntentFilter("com.master.radiomaroc.STATE"); if(Build.VERSION.SDK_INT>=33) registerReceiver(stateReceiver,f,Context.RECEIVER_NOT_EXPORTED); else registerReceiver(stateReceiver,f); startService(new Intent(this,RadioService.class).setAction(RadioService.ACTION_QUERY)); }
    @Override protected void onStop(){ try{unregisterReceiver(stateReceiver);}catch(Exception ignored){} super.onStop(); }
}