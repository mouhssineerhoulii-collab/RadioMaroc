package com.master.radiomaroc;

import android.app.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import java.io.InputStream;
import java.net.URL;

public class RadioService extends Service {
    public static final String ACTION_PLAY="com.master.radiomaroc.PLAY", ACTION_TOGGLE="com.master.radiomaroc.TOGGLE", ACTION_STOP="com.master.radiomaroc.STOP", ACTION_QUERY="com.master.radiomaroc.QUERY", ACTION_SLEEP="com.master.radiomaroc.SLEEP", ACTION_NEXT="com.master.radiomaroc.NEXT", ACTION_PREVIOUS="com.master.radiomaroc.PREVIOUS";
    public static final String EXTRA_NAME="station_name", EXTRA_URL="station_url", EXTRA_MINUTES="minutes";
    private static final String CHANNEL_ID="radio_playback"; private static final int NOTIFICATION_ID=1001;
    private MediaPlayer player; private MediaSession mediaSession; private final Handler handler=new Handler(Looper.getMainLooper()); private Runnable sleepRunnable;
    private String currentName="", currentUrl="", state="stopped"; private boolean prepared=false, playing=false; private Bitmap stationArt;

    @Override public void onCreate(){super.onCreate();createChannel();createMediaSession();}
    @Override public int onStartCommand(Intent i,int flags,int id){if(i==null)return START_NOT_STICKY;String a=i.getAction(); if(ACTION_PLAY.equals(a)){String n=i.getStringExtra(EXTRA_NAME),u=i.getStringExtra(EXTRA_URL);if(n!=null&&u!=null)play(n,u);}else if(ACTION_TOGGLE.equals(a))togglePlayback();else if(ACTION_STOP.equals(a))stopPlayback(true);else if(ACTION_NEXT.equals(a))changeStation(1);else if(ACTION_PREVIOUS.equals(a))changeStation(-1);else if(ACTION_QUERY.equals(a)){broadcastState();if(currentName.isEmpty())stopSelf(id);}else if(ACTION_SLEEP.equals(a))scheduleSleep(i.getIntExtra(EXTRA_MINUTES,0));return START_NOT_STICKY;}

    private void play(String name,String url){currentName=name;currentUrl=url;stationArt=null;loadStationArt(name);releasePlayer();prepared=false;playing=false;state="connecting";updateMetadata();startForeground(NOTIFICATION_ID,buildNotification());broadcastState();player=new MediaPlayer();player.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);player.setOnPreparedListener(mp->{prepared=true;mp.start();playing=true;state="playing";updatePlaybackState();refreshNotification();broadcastState();});player.setOnErrorListener((mp,w,e)->{prepared=false;playing=false;state="error";updatePlaybackState();refreshNotification();broadcastState();return true;});try{player.setDataSource(url);player.prepareAsync();}catch(Exception e){state="error";updatePlaybackState();refreshNotification();broadcastState();}}
    private void togglePlayback(){if(player==null||!prepared){broadcastState();return;}try{if(playing){player.pause();playing=false;state="paused";}else{player.start();playing=true;state="playing";}updatePlaybackState();refreshNotification();broadcastState();}catch(Exception e){state="error";playing=false;updatePlaybackState();refreshNotification();}}
    private void changeStation(int direction){if(Stations.ALL.isEmpty())return;int current=-1;for(int x=0;x<Stations.ALL.size();x++){Station s=Stations.ALL.get(x);if(s.name.equals(currentName)||s.streamUrl.equals(currentUrl)){current=x;break;}}for(int step=1;step<=Stations.ALL.size();step++){int idx=(current+direction*step)%Stations.ALL.size();if(idx<0)idx+=Stations.ALL.size();Station s=Stations.ALL.get(idx);if(s.streamUrl!=null&&!s.streamUrl.trim().isEmpty()){play(s.name,s.streamUrl);return;}}}
    private void stopPlayback(boolean remove){cancelSleep();releasePlayer();prepared=false;playing=false;state="stopped";updatePlaybackState();broadcastState();if(remove){stopForeground(STOP_FOREGROUND_REMOVE);currentName="";currentUrl="";stationArt=null;stopSelf();}}
    private void releasePlayer(){if(player!=null){try{player.stop();}catch(Exception ignored){}try{player.release();}catch(Exception ignored){}player=null;}}
    private void scheduleSleep(int m){cancelSleep();if(m>0){sleepRunnable=()->stopPlayback(true);handler.postDelayed(sleepRunnable,m*60000L);}}
    private void cancelSleep(){if(sleepRunnable!=null){handler.removeCallbacks(sleepRunnable);sleepRunnable=null;}}

    private void createMediaSession(){mediaSession=new MediaSession(this,"RadioMarocSession");mediaSession.setCallback(new MediaSession.Callback(){@Override public void onPlay(){if(!playing)togglePlayback();}@Override public void onPause(){if(playing)togglePlayback();}@Override public void onStop(){stopPlayback(true);}@Override public void onSkipToNext(){changeStation(1);}@Override public void onSkipToPrevious(){changeStation(-1);}});mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS|MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);mediaSession.setActive(true);updatePlaybackState();}
    private void updatePlaybackState(){int ps=playing?PlaybackState.STATE_PLAYING:(prepared?PlaybackState.STATE_PAUSED:PlaybackState.STATE_STOPPED);long actions=PlaybackState.ACTION_PLAY|PlaybackState.ACTION_PAUSE|PlaybackState.ACTION_PLAY_PAUSE|PlaybackState.ACTION_STOP|PlaybackState.ACTION_SKIP_TO_NEXT|PlaybackState.ACTION_SKIP_TO_PREVIOUS;mediaSession.setPlaybackState(new PlaybackState.Builder().setActions(actions).setState(ps,PlaybackState.PLAYBACK_POSITION_UNKNOWN,playing?1f:0f).build());}
    private void updateMetadata(){if(mediaSession==null)return;MediaMetadata.Builder b=new MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_TITLE,currentName).putString(MediaMetadata.METADATA_KEY_ARTIST,"Radio Maroc");if(stationArt!=null)b.putBitmap(MediaMetadata.METADATA_KEY_ART,stationArt).putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART,stationArt);mediaSession.setMetadata(b.build());}
    private void loadStationArt(String name){for(Station s:Stations.ALL)if(s.name.equals(name)&&s.logoUrl!=null&&!s.logoUrl.isEmpty()){new Thread(()->{try(InputStream in=new URL(s.logoUrl).openStream()){Bitmap b=BitmapFactory.decodeStream(in);if(b!=null){stationArt=b;handler.post(()->{updateMetadata();refreshNotification();});}}catch(Exception ignored){}}).start();break;}}

    private PendingIntent servicePI(String action,int request){return PendingIntent.getService(this,request,new Intent(this,RadioService.class).setAction(action),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);}
    private Notification buildNotification(){PendingIntent open=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL_ID):new Notification.Builder(this);int toggleIcon=playing?R.drawable.ic_pause:R.drawable.ic_play;b.setSmallIcon(R.drawable.ic_radio).setContentTitle(currentName.isEmpty()?"Radio Maroc":currentName).setContentText(notificationStateText()).setContentIntent(open).setOnlyAlertOnce(true).setVisibility(Notification.VISIBILITY_PUBLIC).setCategory(Notification.CATEGORY_TRANSPORT).setOngoing(false).setDeleteIntent(servicePI(ACTION_STOP,9));if(stationArt!=null)b.setLargeIcon(stationArt);b.addAction(new Notification.Action.Builder(R.drawable.ic_previous,t("السابق","Précédent","Previous"),servicePI(ACTION_PREVIOUS,3)).build()).addAction(new Notification.Action.Builder(toggleIcon,playing?t("توقف مؤقت","Pause","Pause"):t("تشغيل","Lire","Play"),servicePI(ACTION_TOGGLE,1)).build()).addAction(new Notification.Action.Builder(R.drawable.ic_next,t("التالي","Suivant","Next"),servicePI(ACTION_NEXT,4)).build()).addAction(new Notification.Action.Builder(R.drawable.ic_stop,"✕",servicePI(ACTION_STOP,2)).build()).setStyle(new Notification.MediaStyle().setShowActionsInCompactView(0,1,2).setMediaSession(mediaSession.getSessionToken()));return b.build();}
    private void refreshNotification(){if(!currentName.isEmpty())((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID,buildNotification());}
    private String notificationStateText(){if("connecting".equals(state))return t("جاري الاتصال…","Connexion…","Connecting…");if("playing".equals(state))return t("يعمل الآن","Lecture en cours","Playing now");if("paused".equals(state))return t("متوقف مؤقتًا","En pause","Paused");if("error".equals(state))return t("تعذر تشغيل المحطة","Erreur de lecture","Playback error");return t("متوقف","Arrêté","Stopped");}
    private String t(String ar,String fr,String en){SharedPreferences p=getSharedPreferences("radio_maroc_prefs",MODE_PRIVATE);String l=p.getString("lang","en");return "ar".equals(l)?ar:("fr".equals(l)?fr:en);}
    private void createChannel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel(CHANNEL_ID,"Radio Maroc",NotificationManager.IMPORTANCE_LOW);c.setDescription("Background radio playback controls");c.setShowBadge(false);getSystemService(NotificationManager.class).createNotificationChannel(c);}}
    private void broadcastState(){Intent i=new Intent("com.master.radiomaroc.STATE");i.setPackage(getPackageName());i.putExtra("state",state);i.putExtra("playing",playing);i.putExtra("station",currentName);sendBroadcast(i);}
    @Override public void onDestroy(){cancelSleep();releasePlayer();if(mediaSession!=null){mediaSession.setActive(false);mediaSession.release();}super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}
