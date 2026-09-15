package com.master.radiomaroc;

import android.app.Activity;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/** Large, icon-first, distraction-reduced playback controls for driving. */
public final class DriverActivity extends Activity {
    private TextView station, state;
    private Button play;
    private boolean playing;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) {
            String name=i.getStringExtra("station"), s=i.getStringExtra("state");
            playing=i.getBooleanExtra("playing",false);
            if(name!=null&&!name.isEmpty())station.setText(name);
            state.setText(label(s)); play.setText(playing?"Ⅱ":"▶");
        }
    };

    @Override protected void onCreate(Bundle b){super.onCreate(b);enableFullscreen();setContentView(R.layout.activity_driver);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        station=findViewById(R.id.driverStation);state=findViewById(R.id.driverState);play=findViewById(R.id.driverPlay);
        play.setOnClickListener(v->command(RadioService.ACTION_TOGGLE));
        findViewById(R.id.driverPrevious).setOnClickListener(v->command(RadioService.ACTION_PREVIOUS));
        findViewById(R.id.driverNext).setOnClickListener(v->command(RadioService.ACTION_NEXT));
        findViewById(R.id.driverExit).setOnClickListener(v->finish());
    }
    @Override public void onWindowFocusChanged(boolean hasFocus){super.onWindowFocusChanged(hasFocus);if(hasFocus)enableFullscreen();}
    private void enableFullscreen(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}
    private void command(String action){startService(new Intent(this,RadioService.class).setAction(action));}
    private String label(String s){if("playing".equals(s))return "LIVE";if("connecting".equals(s)||"reconnecting".equals(s))return "CONNECTING";if("paused".equals(s))return "PAUSED";if("error".equals(s))return "UNAVAILABLE";return "READY";}
    @Override protected void onStart(){super.onStart();IntentFilter f=new IntentFilter("com.master.radiomaroc.STATE");if(Build.VERSION.SDK_INT>=33)registerReceiver(receiver,f,Context.RECEIVER_NOT_EXPORTED);else registerReceiver(receiver,f);command(RadioService.ACTION_QUERY);}
    @Override protected void onStop(){try{unregisterReceiver(receiver);}catch(Exception ignored){}super.onStop();}
}
