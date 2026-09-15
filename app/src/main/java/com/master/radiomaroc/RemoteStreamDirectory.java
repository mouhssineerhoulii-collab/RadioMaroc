package com.master.radiomaroc;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fetches a public, app-owned stream directory so broken station URLs can be
 * repaired without shipping a new APK. It never reads another application's
 * private database or credentials. Last known-good data is cached locally.
 */
public final class RemoteStreamDirectory {
    private static final String PREFS="remote_stream_directory";
    private static final String KEY_JSON="json";
    private static final String DIRECTORY_URL="https://raw.githubusercontent.com/mouhssineerhoulii-collab/RadioMaroc/main/streams.json";
    private RemoteStreamDirectory(){}

    public interface Callback { void done(List<String> urls); }

    public static void resolve(Context context,String stationName,List<String> builtIn,Callback callback){
        ArrayList<String> fallback=new ArrayList<>();
        if(builtIn!=null) for(String u:builtIn) add(fallback,u);
        new Thread(()->{
            String json=download();
            if(json!=null&&!json.trim().isEmpty()) context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString(KEY_JSON,json).apply();
            else json=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getString(KEY_JSON,"");
            ArrayList<String> out=new ArrayList<>();
            try{
                JSONObject root=new JSONObject(json);
                JSONArray a=root.optJSONArray(stationName);
                if(a!=null) for(int i=0;i<a.length();i++) add(out,a.optString(i));
            }catch(Exception ignored){}
            for(String u:fallback) add(out,u);
            callback.done(Collections.unmodifiableList(out));
        }).start();
    }

    private static String download(){
        HttpURLConnection c=null;
        try{
            c=(HttpURLConnection)new URL(DIRECTORY_URL).openConnection();
            c.setConnectTimeout(5000); c.setReadTimeout(5000); c.setInstanceFollowRedirects(true);
            c.setRequestProperty("User-Agent","RadioMaroc/6.5 Android");
            if(c.getResponseCode()<200||c.getResponseCode()>=300)return null;
            BufferedReader r=new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder b=new StringBuilder(); String line; while((line=r.readLine())!=null)b.append(line);
            r.close(); return b.toString();
        }catch(Exception e){return null;}finally{if(c!=null)c.disconnect();}
    }
    private static void add(List<String> l,String u){if(u!=null){u=u.trim();if(!u.isEmpty()&&!l.contains(u))l.add(u);}}
}
