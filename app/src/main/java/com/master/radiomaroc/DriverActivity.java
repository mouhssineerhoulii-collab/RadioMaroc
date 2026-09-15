package com.master.radiomaroc;

import android.app.Activity;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/** High-contrast, distraction-reduced playback controls for use in a car. */
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

    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_driver);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        station=findViewById(R.id.driverStation);state=findViewById(R.id.driverState);play=findViewById(R.id.driverPlay);
        play.setOnClickListener(v->command(RadioService.ACTION_TOGGLE));
        findViewById(R.id.driverPrevious).setOnClickListener(v->command(RadioService.ACTION_PREVIOUS));
        findViewById(R.id.driverNext).setOnClickListener(v->command(RadioService.ACTION_NEXT));
        findViewById(R.id.driverStop).setOnClickListener(v->command(RadioService.ACTION_STOP));
        findViewById(R.id.driverExit).setOnClickListener(v->finish());
    }
    private void command(String action){startService(new Intent(this,RadioService.class).setAction(action));}
    private String label(String s){if("playing".equals(s))return "EN DIRECT • على الهواء";if("connecting".equals(s)||"reconnecting".equals(s))return "CONNEXION • جاري الاتصال";if("paused".equals(s))return "PAUSE • متوقف مؤقتًا";if("error".equals(s))return "INDISPONIBLE • غير متاح";return "ARRÊT • متوقف";}
    @Override protected void onStart(){super.onStart();IntentFilter f=new IntentFilter("com.master.radiomaroc.STATE");if(Build.VERSION.SDK_INT>=33)registerReceiver(receiver,f,Context.RECEIVER_NOT_EXPORTED);else registerReceiver(receiver,f);command(RadioService.ACTION_QUERY);}
    @Override protected void onStop(){try{unregisterReceiver(receiver);}catch(Exception ignored){}super.onStop();}
}
