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
                Station station = findStation(stationName);
                if (station != null) LogoLoader.load(station.logoUrl, station.name, playerLogo, R.drawable.ic_app);
            }
            if (state != null) status.setText(stateText(state));
            toggleButton.setText(currentPlaying ? "Ⅱ" : "▶");
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("radio_maroc_prefs", MODE_PRIVATE);
        lang = prefs.getString("lang", "ar");
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
        appTitle = findViewById(R.id.appTitle);
        appSubtitle = findViewById(R.id.appSubtitle);
        settingsButton = findViewById(R.id.settingsButton);
        searchBox = findViewById(R.id.searchBox);
        allButton = findViewById(R.id.allButton);
        favoritesButton = findViewById(R.id.favoritesButton);
        recentButton = findViewById(R.id.recentButton);
        viewButton = findViewById(R.id.viewButton);
        playerLogo = findViewById(R.id.playerLogo);
        nowPlayingLabel = findViewById(R.id.nowPlayingLabel);
        nowPlaying = findViewById(R.id.nowPlaying);
        status = findViewById(R.id.status);
        toggleButton = findViewById(R.id.toggleButton);
        stopButton = findViewById(R.id.stopButton);
        stationsGrid = findViewById(R.id.stationsGrid);
        homeNavButton = findViewById(R.id.homeNavButton);
        favoritesNavButton = findViewById(R.id.favoritesNavButton);
        settingsNavButton = findViewById(R.id.settingsNavButton);
    }

    private void applyLanguage() {
        boolean rtl = "ar".equals(lang);
        root.setLayoutDirection(rtl ? LinearLayout.LAYOUT_DIRECTION_RTL : LinearLayout.LAYOUT_DIRECTION_LTR);
        appTitle.setText(t("راديو المغرب", "Radio Maroc", "Radio Maroc"));
        appSubtitle.setText(t("إذاعات مغربية مباشرة", "Radios marocaines en direct", "Moroccan radio live"));
        searchBox.setHint(t("🔎  ابحث عن محطة…", "🔎  Rechercher une station…", "🔎  Search stations…"));
        allButton.setText(t("⌂  الكل", "⌂  Toutes", "⌂  All"));
        favoritesButton.setText(t("★  المفضلة", "★  Favoris", "★  Favorites"));
        recentButton.setText(t("◷  الأخيرة", "◷  Récentes", "◷  Recent"));
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
        if ("en".equals(lang)) return en;
        return ar;
    }

    private String stateText(String state) {
        if ("connecting".equals(state)) return t("جاري الاتصال…", "Connexion…", "Connecting…");
        if ("playing".equals(state)) return t("● يعمل الآن", "● Lecture", "● Playing");
        if ("paused".equals(state)) return t("متوقف مؤقتًا", "En pause", "Paused");
        if ("error".equals(state)) return t("تعذر تشغيل المحطة", "Impossible de lire la station", "Unable to play station");
        return t("متوقف", "Arrêté", "Stopped");
    }

    private void setFilter(String mode) {
        filterMode = mode;
        updateModeButtons();
        buildStationList(searchBox.getText().toString());
    }

    private void buildStationList(String query) {
        stationsGrid.removeAllViews();
        boolean grid = "grid".equals(viewMode);
        boolean compact = "compact".equals(viewMode);
        stationsGrid.setColumnCount(grid ? 2 : 1);

        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        for (Station station : Stations.ALL) {
            if ("favorites".equals(filterMode) && !favorites.contains(station.name)) continue;
            if ("recent".equals(filterMode) && !recent.contains(station.name)) continue;
            String searchable = (station.name + " " + station.subtitleAr + " " + station.subtitleFr + " " + station.subtitleEn).toLowerCase(Locale.ROOT);
            if (!q.isEmpty() && !searchable.contains(q)) continue;

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(grid ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(compact ? 7 : 10), dp(compact ? 6 : 9), dp(compact ? 7 : 10), dp(compact ? 6 : 9));
            card.setBackgroundResource(R.drawable.card_bg);
            card.setClickable(true);
            card.setFocusable(true);

            GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
            cardParams.setMargins(dp(4), dp(4), dp(4), dp(4));
            if (grid) {
                cardParams.width = 0;
                cardParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            } else {
                cardParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                cardParams.columnSpec = GridLayout.spec(0, 1f);
            }
            cardParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            card.setLayoutParams(cardParams);

            ImageView logo = new ImageView(this);
            int logoSize = dp(grid ? 100 : (compact ? 42 : 60));
            LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(logoSize, logoSize);
            if (grid) {
                logoParams.gravity = Gravity.CENTER_HORIZONTAL;
                logoParams.bottomMargin = dp(8);
            } else {
                logoParams.setMarginEnd(dp(11));
            }
            logo.setLayoutParams(logoParams);
            logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            logo.setPadding(dp(3), dp(3), dp(3), dp(3));
            logo.setBackgroundResource(R.drawable.logo_bg);
            LogoLoader.load(station.logoUrl, station.name, logo, R.drawable.ic_app);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setGravity(grid ? Gravity.CENTER_HORIZONTAL : Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                grid ? ViewGroup.LayoutParams.MATCH_PARENT : 0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                grid ? 0 : 1f
            );
            info.setLayoutParams(infoParams);

            TextView name = new TextView(this);
            name.setText(station.name);
            name.setTextSize(compact ? 14 : (grid ? 16 : 17));
            name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            name.setTextColor(getColor(R.color.navy));
            name.setGravity(grid ? Gravity.CENTER : Gravity.START);
            name.setMaxLines(grid ? 2 : 1);

            TextView subtitle = new TextView(this);
            subtitle.setText(station.subtitle(lang));
            subtitle.setTextSize(compact ? 10 : 12);
            subtitle.setTextColor(getColor(R.color.muted));
            subtitle.setGravity(grid ? Gravity.CENTER : Gravity.START);
            subtitle.setMaxLines(grid ? 2 : 1);
            subtitle.setPadding(0, dp(2), 0, 0);

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            actions.setGravity(Gravity.CENTER);
            if (grid) actions.setPadding(0, dp(7), 0, 0);

            TextView star = new TextView(this);
            star.setText(favorites.contains(station.name) ? "★" : "☆");
            star.setTextSize(compact ? 21 : 25);
            star.setTextColor(getColor(R.color.gold));
            star.setGravity(Gravity.CENTER);
            star.setPadding(dp(7), 0, dp(7), 0);
            star.setContentDescription(t("المفضلة", "Favori", "Favorite"));
            star.setOnClickListener(v -> toggleFavorite(station.name, star));

            TextView play = new TextView(this);
            play.setText("▶");
            play.setTextSize(compact ? 19 : 23);
            play.setTextColor(getColor(R.color.green));
            play.setGravity(Gravity.CENTER);
            play.setPadding(dp(8), 0, dp(8), 0);
            play.setContentDescription(t("تشغيل", "Lire", "Play"));
            play.setOnClickListener(v -> playStation(station));

            info.addView(name);
            info.addView(subtitle);
            actions.addView(star);
            actions.addView(play);
            card.addView(logo);
            card.addView(info);
            card.addView(actions);
            card.setOnClickListener(v -> playStation(station));
            stationsGrid.addView(card);
        }
    }

    private void toggleFavorite(String stationName, TextView star) {
        if (favorites.contains(stationName)) favorites.remove(stationName);
        else favorites.add(stationName);
        prefs.edit().putStringSet("favorites", new HashSet<>(favorites)).apply();
        star.setText(favorites.contains(stationName) ? "★" : "☆");
        if ("favorites".equals(filterMode)) buildStationList(searchBox.getText().toString());
    }

    private void cycleViewMode() {
        if ("list".equals(viewMode)) viewMode = "compact";
        else if ("compact".equals(viewMode)) viewMode = "grid";
        else viewMode = "list";
        prefs.edit().putString("view", viewMode).apply();
        updateViewIcon();
        buildStationList(searchBox.getText().toString());
    }

    private void updateViewIcon() {
        if ("compact".equals(viewMode)) viewButton.setText("☷");
        else if ("grid".equals(viewMode)) viewButton.setText("▦");
        else viewButton.setText("☰");
        viewButton.setContentDescription(t("طريقة العرض", "Mode d'affichage", "View mode"));
    }

    private void updateModeButtons() {
        allButton.setAlpha("all".equals(filterMode) ? 1f : 0.55f);
        favoritesButton.setAlpha("favorites".equals(filterMode) ? 1f : 0.55f);
        recentButton.setAlpha("recent".equals(filterMode) ? 1f : 0.55f);
    }

    private void showSettings() {
        String[] items = new String[] {
            "🌐  " + t("اللغة", "Langue", "Language"),
            "▦  " + t("طريقة عرض المحطات", "Affichage des stations", "Station view"),
            "◷  " + t("مؤقت النوم", "Minuterie de sommeil", "Sleep timer"),
            "🔒  " + t("الخصوصية والمصدر المفتوح", "Confidentialité et open source", "Privacy & open source")
        };
        new AlertDialog.Builder(this)
            .setTitle("⚙  " + t("الإعدادات", "Paramètres", "Settings"))
            .setItems(items, (dialog, which) -> {
                if (which == 0) showLanguageDialog();
                else if (which == 1) showViewDialog();
                else if (which == 2) showSleepDialog();
                else showPrivacyDialog();
            })
            .setNegativeButton(t("إغلاق", "Fermer", "Close"), null)
            .show();
    }

    private void showLanguageDialog() {
        String[] options = {"العربية", "Français", "English"};
        int checked = "ar".equals(lang) ? 0 : ("fr".equals(lang) ? 1 : 2);
        new AlertDialog.Builder(this)
            .setTitle("🌐  " + t("اللغة", "Langue", "Language"))
            .setSingleChoiceItems(options, checked, (d, which) -> {
                lang = which == 0 ? "ar" : (which == 1 ? "fr" : "en");
                prefs.edit().putString("lang", lang).apply();
                d.dismiss();
                recreate();
            }).show();
    }

    private void showViewDialog() {
        String[] options = {
            t("☰ قائمة عادية", "☰ Liste", "☰ List"),
            t("☷ قائمة مدمجة", "☷ Liste compacte", "☷ Compact list"),
            t("▦ صور مصغرة", "▦ Vignettes", "▦ Thumbnails")
        };
        int checked = "list".equals(viewMode) ? 0 : ("compact".equals(viewMode) ? 1 : 2);
        new AlertDialog.Builder(this)
            .setTitle(t("طريقة العرض", "Mode d'affichage", "View mode"))
            .setSingleChoiceItems(options, checked, (d, which) -> {
                viewMode = which == 0 ? "list" : (which == 1 ? "compact" : "grid");
                prefs.edit().putString("view", viewMode).apply();
                updateViewIcon();
                buildStationList(searchBox.getText().toString());
                d.dismiss();
            }).show();
    }

    private void showSleepDialog() {
        String[] options = {
            t("إيقاف المؤقت", "Désactiver", "Off"),
            "15 " + t("دقيقة", "min", "min"),
            "30 " + t("دقيقة", "min", "min"),
            "60 " + t("دقيقة", "min", "min")
        };
        new AlertDialog.Builder(this)
            .setTitle("◷  " + t("مؤقت النوم", "Minuterie de sommeil", "Sleep timer"))
            .setItems(options, (d, which) -> {
                int minutes = which == 1 ? 15 : (which == 2 ? 30 : (which == 3 ? 60 : 0));
                Intent i = new Intent(this, RadioService.class);
                i.setAction(RadioService.ACTION_SLEEP);
                i.putExtra(RadioService.EXTRA_MINUTES, minutes);
                startService(i);
                Toast.makeText(this, minutes == 0 ? t("تم إلغاء المؤقت", "Minuterie désactivée", "Sleep timer off") :
                    t("سيتم إيقاف الراديو بعد ", "La radio s'arrêtera dans ", "Radio will stop in ") + minutes + t(" دقيقة", " min", " min"), Toast.LENGTH_SHORT).show();
            }).show();
    }

    private void showPrivacyDialog() {
        String msg = t(
            "التطبيق مفتوح المصدر. لا إعلانات، لا تحليلات، لا أدوات تتبع، ولا يجمع بيانات شخصية. شعارات المحطات لا تُضمَّن داخل التطبيق؛ تُحمَّل من مواقع المحطات نفسها عند توفرها، وإلا يظهر رمز محلي بديل. الصلاحيات محصورة في الإنترنت وتشغيل الصوت في الخلفية والإشعارات.",
            "Application open source. Sans publicité, sans analytique, sans traceurs et sans collecte de données personnelles. Les logos ne sont pas intégrés au paquet : ils sont chargés depuis les sites des radios lorsqu'ils sont disponibles, sinon un badge local est affiché. Les autorisations sont limitées à Internet, la lecture en arrière-plan et les notifications.",
            "Open-source app. No ads, analytics, trackers, or personal-data collection. Station logos are not bundled in the app; they are loaded from broadcaster websites when available, otherwise a local fallback badge is shown. Permissions are limited to Internet, background playback, and notifications."
        );
        new AlertDialog.Builder(this)
            .setTitle("🔒  " + t("الخصوصية والمصدر المفتوح", "Confidentialité et open source", "Privacy & open source"))
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show();
    }

    private void playStation(Station station) {
        currentStationName = station.name;
        nowPlaying.setText(station.name);
        status.setText(stateText("connecting"));
        toggleButton.setText("Ⅱ");
        LogoLoader.load(station.logoUrl, station.name, playerLogo, R.drawable.ic_app);
        addRecent(station.name);

        Intent intent = new Intent(this, RadioService.class);
        intent.setAction(RadioService.ACTION_PLAY);
        intent.putExtra(RadioService.EXTRA_NAME, station.name);
        intent.putExtra(RadioService.EXTRA_URL, station.streamUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent);
        else startService(intent);
    }

    private void toggleRadio() {
        Intent i = new Intent(this, RadioService.class);
        i.setAction(RadioService.ACTION_TOGGLE);
        startService(i);
    }

    private void stopRadio() {
        Intent intent = new Intent(this, RadioService.class);
        intent.setAction(RadioService.ACTION_STOP);
        startService(intent);
        currentPlaying = false;
        toggleButton.setText("▶");
        status.setText(stateText("stopped"));
    }

    private Station findStation(String name) {
        for (Station station : Stations.ALL) if (station.name.equals(name)) return station;
        return null;
    }

    private void addRecent(String name) {
        recent.remove(name);
        recent.add(0, name);
        while (recent.size() > 10) recent.remove(recent.size() - 1);
        prefs.edit().putString("recent", joinRecent()).apply();
        if ("recent".equals(filterMode)) buildStationList(searchBox.getText().toString());
    }

    private void loadRecent() {
        recent.clear();
        String raw = prefs.getString("recent", "");
        if (raw == null || raw.isEmpty()) return;
        recent.addAll(Arrays.asList(raw.split("\\u001F")));
    }

    private String joinRecent() {
        StringBuilder sb = new StringBuilder();
        for (String s : recent) {
            if (sb.length() > 0) sb.append('\u001F');
            sb.append(s);
        }
        return sb.toString();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.master.radiomaroc.STATE");
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else registerReceiver(stateReceiver, filter);
        Intent query = new Intent(this, RadioService.class);
        query.setAction(RadioService.ACTION_QUERY);
        startService(query);
    }

    @Override protected void onStop() {
        try { unregisterReceiver(stateReceiver); } catch (Exception ignored) {}
        super.onStop();
    }
}
