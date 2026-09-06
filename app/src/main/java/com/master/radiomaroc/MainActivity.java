package com.master.radiomaroc;

import android.Manifest;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {

    private TextView nowPlaying;
    private TextView status;
    private LinearLayout stationsContainer;

    private final BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("status");
            String station = intent.getStringExtra("station");
            if (station != null && !station.isEmpty()) nowPlaying.setText(station);
            if (s != null) status.setText(s);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nowPlaying = findViewById(R.id.nowPlaying);
        status = findViewById(R.id.status);
        stationsContainer = findViewById(R.id.stationsContainer);
        Button stopButton = findViewById(R.id.stopButton);

        requestNotificationPermission();
        buildStationList();
        stopButton.setOnClickListener(v -> stopRadio());
    }

    private void buildStationList() {
        int margin = dp(6);
        for (Station station : Stations.ALL) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(18), dp(15), dp(18), dp(15));
            card.setBackgroundResource(R.drawable.card_bg);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setClickable(true);
            card.setFocusable(true);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, margin, 0, margin);
            card.setLayoutParams(cardParams);

            TextView name = new TextView(this);
            name.setText("▶  " + station.name);
            name.setTextSize(19);
            name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            name.setTextColor(getColor(R.color.navy));
            name.setGravity(Gravity.START);

            TextView subtitle = new TextView(this);
            subtitle.setText(station.subtitle);
            subtitle.setTextSize(14);
            subtitle.setTextColor(getColor(R.color.muted));
            subtitle.setPadding(0, dp(4), 0, 0);
            subtitle.setGravity(Gravity.START);

            card.addView(name);
            card.addView(subtitle);
            card.setOnClickListener(v -> playStation(station));
            stationsContainer.addView(card);
        }
    }

    private void playStation(Station station) {
        nowPlaying.setText(station.name);
        status.setText("جاري الاتصال…");

        Intent intent = new Intent(this, RadioService.class);
        intent.setAction(RadioService.ACTION_PLAY);
        intent.putExtra(RadioService.EXTRA_NAME, station.name);
        intent.putExtra(RadioService.EXTRA_URL, station.streamUrl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void stopRadio() {
        Intent intent = new Intent(this, RadioService.class);
        intent.setAction(RadioService.ACTION_STOP);
        startService(intent);
        status.setText("متوقف");
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.master.radiomaroc.STATE");
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(stateReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(stateReceiver);
        } catch (Exception ignored) {}
        super.onStop();
    }
}
