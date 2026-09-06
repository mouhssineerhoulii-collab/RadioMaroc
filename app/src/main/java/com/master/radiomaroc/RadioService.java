package com.master.radiomaroc;

import android.app.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

public class RadioService extends Service {

    public static final String ACTION_PLAY = "com.master.radiomaroc.PLAY";
    public static final String ACTION_STOP = "com.master.radiomaroc.STOP";
    public static final String EXTRA_NAME = "station_name";
    public static final String EXTRA_URL = "station_url";

    private static final String CHANNEL_ID = "radio_playback";
    private static final int NOTIFICATION_ID = 1001;

    private MediaPlayer player;
    private String currentName = "";

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopPlayback();
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
            broadcastState(tr("متوقف", "Arrêté", "Stopped"), false, currentName);
            return START_NOT_STICKY;
        }
        if (ACTION_PLAY.equals(action)) {
            String name = intent.getStringExtra(EXTRA_NAME);
            String url = intent.getStringExtra(EXTRA_URL);
            if (name != null && url != null) play(name, url);
        }
        return START_NOT_STICKY;
    }

    private void play(String name, String url) {
        currentName = name;
        stopPlayback();
        String connecting = tr("جاري الاتصال…", "Connexion…", "Connecting…");
        startForeground(NOTIFICATION_ID, buildNotification(name, connecting));
        broadcastState(connecting, false, name);

        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build());
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        player.setOnPreparedListener(mp -> {
            mp.start();
            String playing = tr("يعمل الآن", "En direct", "Playing now");
            startForeground(NOTIFICATION_ID, buildNotification(name, playing));
            broadcastState(playing, true, name);
        });

        player.setOnErrorListener((mp, what, extra) -> {
            String error = tr("تعذر تشغيل هذه المحطة", "Impossible de lire cette station", "Unable to play this station");
            broadcastState(error, false, name);
            startForeground(NOTIFICATION_ID, buildNotification(name, error));
            return true;
        });

        try {
            player.setDataSource(url);
            player.prepareAsync();
        } catch (Exception e) {
            broadcastState(tr("تعذر فتح رابط البث", "Flux indisponible", "Stream unavailable"), false, name);
        }
    }

    private String tr(String ar, String fr, String en) {
        SharedPreferences p = getSharedPreferences("radio_maroc_prefs", MODE_PRIVATE);
        String lang = p.getString("lang", "ar");
        if ("fr".equals(lang)) return fr;
        if ("en".equals(lang)) return en;
        return ar;
    }

    private void stopPlayback() {
        if (player != null) {
            try { if (player.isPlaying()) player.stop(); } catch (Exception ignored) {}
            player.reset();
            player.release();
            player = null;
        }
    }

    private Notification buildNotification(String station, String state) {
        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, RadioService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 1, stopIntent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this);

        return builder.setSmallIcon(R.drawable.ic_radio)
            .setContentTitle(station)
            .setContentText(state)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .addAction(new Notification.Action.Builder(null, tr("إيقاف", "Arrêter", "Stop"), stopPendingIntent).build())
            .build();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                tr("تشغيل الراديو", "Lecture radio", "Radio playback"), NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(tr("إشعار تشغيل الراديو في الخلفية", "Lecture en arrière-plan", "Background radio playback"));
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private void broadcastState(String status, boolean playing, String station) {
        Intent i = new Intent("com.master.radiomaroc.STATE");
        i.setPackage(getPackageName());
        i.putExtra("status", status);
        i.putExtra("playing", playing);
        i.putExtra("station", station);
        sendBroadcast(i);
    }

    @Override public void onDestroy() {
        stopPlayback();
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
