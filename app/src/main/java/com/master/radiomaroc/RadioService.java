package com.master.radiomaroc;

import android.app.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

public class RadioService extends Service {

    public static final String ACTION_PLAY = "com.master.radiomaroc.PLAY";
    public static final String ACTION_TOGGLE = "com.master.radiomaroc.TOGGLE";
    public static final String ACTION_STOP = "com.master.radiomaroc.STOP";
    public static final String ACTION_QUERY = "com.master.radiomaroc.QUERY";
    public static final String ACTION_SLEEP = "com.master.radiomaroc.SLEEP";
    public static final String EXTRA_NAME = "station_name";
    public static final String EXTRA_URL = "station_url";
    public static final String EXTRA_MINUTES = "minutes";

    private static final String CHANNEL_ID = "radio_playback";
    private static final int NOTIFICATION_ID = 1001;

    private MediaPlayer player;
    private MediaSession mediaSession;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable sleepRunnable;

    private String currentName = "";
    private String currentUrl = "";
    private String state = "stopped";
    private boolean prepared = false;
    private boolean playing = false;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        createMediaSession();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        String action = intent.getAction();

        if (ACTION_PLAY.equals(action)) {
            String name = intent.getStringExtra(EXTRA_NAME);
            String url = intent.getStringExtra(EXTRA_URL);
            if (name != null && url != null) play(name, url);
        } else if (ACTION_TOGGLE.equals(action)) {
            togglePlayback();
        } else if (ACTION_STOP.equals(action)) {
            stopPlayback(true);
        } else if (ACTION_QUERY.equals(action)) {
            broadcastState();
            if (currentName.isEmpty()) stopSelf(startId);
        } else if (ACTION_SLEEP.equals(action)) {
            scheduleSleep(intent.getIntExtra(EXTRA_MINUTES, 0));
        }

        return START_NOT_STICKY;
    }

    private void play(String name, String url) {
        currentName = name;
        currentUrl = url;
        releasePlayer();
        prepared = false;
        playing = false;
        state = "connecting";

        startForeground(NOTIFICATION_ID, buildNotification());
        broadcastState();

        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build());
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        player.setOnPreparedListener(mp -> {
            prepared = true;
            mp.start();
            playing = true;
            state = "playing";
            updatePlaybackState();
            refreshNotification();
            broadcastState();
        });

        player.setOnErrorListener((mp, what, extra) -> {
            prepared = false;
            playing = false;
            state = "error";
            updatePlaybackState();
            refreshNotification();
            broadcastState();
            return true;
        });

        try {
            player.setDataSource(url);
            player.prepareAsync();
        } catch (Exception e) {
            prepared = false;
            playing = false;
            state = "error";
            updatePlaybackState();
            refreshNotification();
            broadcastState();
        }
    }

    private void togglePlayback() {
        if (player == null || !prepared) {
            broadcastState();
            return;
        }
        try {
            if (playing) {
                player.pause();
                playing = false;
                state = "paused";
            } else {
                player.start();
                playing = true;
                state = "playing";
            }
            updatePlaybackState();
            refreshNotification();
            broadcastState();
        } catch (Exception e) {
            state = "error";
            playing = false;
            updatePlaybackState();
            refreshNotification();
            broadcastState();
        }
    }

    private void stopPlayback(boolean removeNotification) {
        cancelSleep();
        releasePlayer();
        prepared = false;
        playing = false;
        state = "stopped";
        updatePlaybackState();
        broadcastState();
        if (removeNotification) {
            stopForeground(STOP_FOREGROUND_REMOVE);
            currentName = "";
            currentUrl = "";
            stopSelf();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            try { player.stop(); } catch (Exception ignored) {}
            try { player.reset(); } catch (Exception ignored) {}
            try { player.release(); } catch (Exception ignored) {}
            player = null;
        }
    }

    private void scheduleSleep(int minutes) {
        cancelSleep();
        if (minutes <= 0) return;
        sleepRunnable = () -> stopPlayback(true);
        handler.postDelayed(sleepRunnable, minutes * 60_000L);
    }

    private void cancelSleep() {
        if (sleepRunnable != null) {
            handler.removeCallbacks(sleepRunnable);
            sleepRunnable = null;
        }
    }

    private void createMediaSession() {
        mediaSession = new MediaSession(this, "RadioMarocSession");
        mediaSession.setCallback(new MediaSession.Callback() {
            @Override public void onPlay() { if (!playing) togglePlayback(); }
            @Override public void onPause() { if (playing) togglePlayback(); }
            @Override public void onStop() { stopPlayback(true); }
        });
        mediaSession.setActive(true);
        updatePlaybackState();
    }

    private void updatePlaybackState() {
        int ps = playing ? PlaybackState.STATE_PLAYING :
            (prepared ? PlaybackState.STATE_PAUSED : PlaybackState.STATE_STOPPED);
        long actions = PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE |
            PlaybackState.ACTION_PLAY_PAUSE | PlaybackState.ACTION_STOP;
        mediaSession.setPlaybackState(new PlaybackState.Builder()
            .setActions(actions)
            .setState(ps, PlaybackState.PLAYBACK_POSITION_UNKNOWN, playing ? 1f : 0f)
            .build());
    }

    private Notification buildNotification() {
        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent toggleIntent = new Intent(this, RadioService.class).setAction(ACTION_TOGGLE);
        PendingIntent togglePending = PendingIntent.getService(this, 1, toggleIntent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, RadioService.class).setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(this, 2, stopIntent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? new Notification.Builder(this, CHANNEL_ID)
            : new Notification.Builder(this);

        int toggleIcon = playing ? R.drawable.ic_pause : R.drawable.ic_play;
        String toggleText = playing ? t("إيقاف مؤقت", "Pause", "Pause") : t("تشغيل", "Lire", "Play");

        builder.setSmallIcon(R.drawable.ic_radio)
            .setContentTitle(currentName.isEmpty() ? "Radio Maroc" : currentName)
            .setContentText(notificationStateText())
            .setContentIntent(contentIntent)
            .setOngoing(playing || "connecting".equals(state))
            .setOnlyAlertOnce(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setCategory(Notification.CATEGORY_TRANSPORT)
            .addAction(new Notification.Action.Builder(toggleIcon, toggleText, togglePending).build())
            .addAction(new Notification.Action.Builder(R.drawable.ic_stop, t("إيقاف", "Arrêter", "Stop"), stopPending).build())
            .setStyle(new Notification.MediaStyle()
                .setShowActionsInCompactView(0, 1)
                .setMediaSession(mediaSession.getSessionToken()));

        return builder.build();
    }

    private void refreshNotification() {
        if (currentName.isEmpty()) return;
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, buildNotification());
    }

    private String notificationStateText() {
        if ("connecting".equals(state)) return t("جاري الاتصال…", "Connexion…", "Connecting…");
        if ("playing".equals(state)) return t("يعمل الآن", "Lecture en cours", "Playing now");
        if ("paused".equals(state)) return t("متوقف مؤقتًا", "En pause", "Paused");
        if ("error".equals(state)) return t("تعذر تشغيل المحطة", "Erreur de lecture", "Playback error");
        return t("متوقف", "Arrêté", "Stopped");
    }

    private String t(String ar, String fr, String en) {
        SharedPreferences prefs = getSharedPreferences("radio_maroc_prefs", MODE_PRIVATE);
        String lang = prefs.getString("lang", "en");
        if ("fr".equals(lang)) return fr;
        if ("ar".equals(lang)) return ar;
        return en;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Radio Maroc", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Background radio playback controls");
            channel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void broadcastState() {
        Intent i = new Intent("com.master.radiomaroc.STATE");
        i.setPackage(getPackageName());
        i.putExtra("state", state);
        i.putExtra("playing", playing);
        i.putExtra("station", currentName);
        sendBroadcast(i);
    }

    @Override public void onDestroy() {
        cancelSleep();
        releasePlayer();
        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.release();
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
