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

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {

    private LinearLayout root;
    private TextView appTitle, appSubtitle, settingsButton, nowPlayingLabel, nowPlaying, status;
    private EditText searchBox;
    private Button allButton, favoritesButton, viewButton, stopButton;
    private ImageView playerLogo;
    private GridLayout stationsGrid;

    private SharedPreferences prefs;
    private String lang;
    private String viewMode;
    private boolean favoritesOnly = false;
    private Set<String> favorites;

    private final BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra("status");
            String stationName = intent.getStringExtra("station");
            if (stationName != null && !stationName.isEmpty()) {
                nowPlaying.setText(stationName);
                Station station = findStation(stationName);
                if (station != null) LogoLoader.load(station.logoUrl, playerLogo, R.drawable.ic_radio);
            }
            if (state != null) status.setText(state);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("radio_maroc_prefs", MODE_PRIVATE);
        lang = prefs.getString("lang", "ar");
        viewMode = prefs.getString("view", "list");
        favorites = new HashSet<>(prefs.getStringSet("favorites", new HashSet<>()));

        bindViews();
        applyLanguage();
        requestNotificationPermission();
        buildStationList("");

        stopButton.setOnClickListener(v -> stopRadio());
        allButton.setOnClickListener(v -> {
            favoritesOnly = false;
            updateModeButtons();
            buildStationList(searchBox.getText().toString());
        });
        favoritesButton.setOnClickListener(v -> {
            favoritesOnly = true;
            updateModeButtons();
            buildStationList(searchBox.getText().toString());
        });
        viewButton.setOnClickListener(v -> cycleViewMode());
        settingsButton.setOnClickListener(v -> showSettings());

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
        viewButton = findViewById(R.id.viewButton);
        playerLogo = findViewById(R.id.playerLogo);
        nowPlayingLabel = findViewById(R.id.nowPlayingLabel);
        nowPlaying = findViewById(R.id.nowPlaying);
        status = findViewById(R.id.status);
        stopButton = findViewById(R.id.stopButton);
        stationsGrid = findViewById(R.id.stationsGrid);
    }

    private void applyLanguage() {
        boolean rtl = "ar".equals(lang);
        root.setLayoutDirection(rtl ? LinearLayout.LAYOUT_DIRECTION_RTL : LinearLayout.LAYOUT_DIRECTION_LTR);
        appTitle.setText("Radio Maroc");
        appSubtitle.setText(t("إذاعات مغربية مباشرة", "Radios marocaines en direct", "Moroccan radio live"));
        searchBox.setHint(t("ابحث عن محطة…", "Rechercher une station…", "Search stations…"));
        allButton.setText(t("الكل", "Toutes", "All"));
        favoritesButton.setText(t("★ المفضلة", "★ Favoris", "★ Favorites"));
        nowPlayingLabel.setText(t("الآن على الهواء", "EN DIRECT", "NOW PLAYING"));
        if (nowPlaying.getText().toString().isEmpty() || nowPlaying.getText().toString().contains("اختر")) {
            nowPlaying.setText(t("اختر محطة", "Choisissez une station", "Choose a station"));
        }
        if (status.getText().toString().isEmpty() || status.getText().toString().equals("متوقف")) {
            status.setText(t("متوقف", "Arrêté", "Stopped"));
        }
        settingsButton.setContentDescription(t("الإعدادات", "Paramètres", "Settings"));
    }

    private String t(String ar, String fr, String en) {
        if ("fr".equals(lang)) return fr;
        if ("en".equals(lang)) return en;
        return ar;
    }

    private void buildStationList(String query) {
        stationsGrid.removeAllViews();
        boolean grid = "grid".equals(viewMode);
        boolean compact = "compact".equals(viewMode);
        stationsGrid.setColumnCount(grid ? 2 : 1);

        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        for (Station station : Stations.ALL) {
            if (favoritesOnly && !favorites.contains(station.name)) continue;
            String searchable = (station.name + " " + station.subtitleAr + " " + station.subtitleFr + " " + station.subtitleEn).toLowerCase(Locale.ROOT);
            if (!q.isEmpty() && !searchable.contains(q)) continue;

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(grid ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(compact ? 8 : 12), dp(compact ? 7 : 11), dp(compact ? 8 : 12), dp(compact ? 7 : 11));
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
            int logoSize = dp(grid ? 92 : (compact ? 44 : 60));
            LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(logoSize, logoSize);
            if (grid) {
                logoParams.gravity = Gravity.CENTER_HORIZONTAL;
                logoParams.bottomMargin = dp(8);
            } else {
                logoParams.setMarginEnd(dp(12));
            }
            logo.setLayoutParams(logoParams);
            logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            logo.setPadding(dp(4), dp(4), dp(4), dp(4));
            logo.setBackgroundResource(R.drawable.logo_bg);
            LogoLoader.load(station.logoUrl, logo, R.drawable.ic_radio);

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
            name.setTextSize(compact ? 15 : (grid ? 16 : 17));
            name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            name.setTextColor(getColor(R.color.navy));
            name.setGravity(grid ? Gravity.CENTER : Gravity.START);
            name.setMaxLines(grid ? 2 : 1);

            TextView subtitle = new TextView(this);
            subtitle.setText(station.subtitle(lang));
            subtitle.setTextSize(compact ? 11 : 12);
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
            star.setTextSize(compact ? 22 : 25);
            star.setTextColor(getColor(R.color.gold));
            star.setGravity(Gravity.CENTER);
            star.setPadding(dp(8), 0, dp(8), 0);
            star.setOnClickListener(v -> toggleFavorite(station.name, star));

            TextView play = new TextView(this);
            play.setText("▶");
            play.setTextSize(compact ? 20 : 24);
            play.setTextColor(getColor(R.color.green));
            play.setGravity(Gravity.CENTER);
            play.setPadding(dp(8), 0, dp(8), 0);
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
        if (favoritesOnly) buildStationList(searchBox.getText().toString());
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
        allButton.setAlpha(favoritesOnly ? 0.55f : 1f);
        favoritesButton.setAlpha(favoritesOnly ? 1f : 0.55f);
    }

    private void showSettings() {
        String[] items = new String[] {
            "العربية", "Français", "English",
            t("عرض: قائمة عادية", "Affichage : liste", "View: list"),
            t("عرض: قائمة مدمجة", "Affichage : liste compacte", "View: compact list"),
            t("عرض: صور مصغرة", "Affichage : vignettes", "View: thumbnails")
        };

        new AlertDialog.Builder(this)
            .setTitle(t("الإعدادات", "Paramètres", "Settings"))
            .setItems(items, (dialog, which) -> {
                if (which <= 2) {
                    lang = which == 0 ? "ar" : (which == 1 ? "fr" : "en");
                    prefs.edit().putString("lang", lang).apply();
                    recreate();
                } else {
                    viewMode = which == 3 ? "list" : (which == 4 ? "compact" : "grid");
                    prefs.edit().putString("view", viewMode).apply();
                    updateViewIcon();
                    buildStationList(searchBox.getText().toString());
                }
            })
            .setNegativeButton(t("إغلاق", "Fermer", "Close"), null)
            .show();
    }

    private void playStation(Station station) {
        nowPlaying.setText(station.name);
        status.setText(t("جاري الاتصال…", "Connexion…", "Connecting…"));
        LogoLoader.load(station.logoUrl, playerLogo, R.drawable.ic_radio);

        Intent intent = new Intent(this, RadioService.class);
        intent.setAction(RadioService.ACTION_PLAY);
        intent.putExtra(RadioService.EXTRA_NAME, station.name);
        intent.putExtra(RadioService.EXTRA_URL, station.streamUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent);
        else startService(intent);
    }

    private Station findStation(String name) {
        for (Station station : Stations.ALL) if (station.name.equals(name)) return station;
        return null;
    }

    private void stopRadio() {
        Intent intent = new Intent(this, RadioService.class);
        intent.setAction(RadioService.ACTION_STOP);
        startService(intent);
        status.setText(t("متوقف", "Arrêté", "Stopped"));
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.master.radiomaroc.STATE");
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else registerReceiver(stateReceiver, filter);
    }

    @Override
    protected void onStop() {
        try { unregisterReceiver(stateReceiver); } catch (Exception ignored) {}
        super.onStop();
    }
}
