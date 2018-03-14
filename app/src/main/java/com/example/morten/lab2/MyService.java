package com.example.morten.lab2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



public class MyService extends Service {

    private RequestQueue    queue       = null;
    int                     itemLimit   = 0;
    int                     frequency   = 0;
    String                  rssUrl;
    String                  newString   = "";
    boolean                 stringsMatch;

    NotificationManager nManager = null;
    NotificationCompat.Builder nBuilder = null;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "RSS Feed";

    public MyService() {

    }

    public void onCreate() {

        nBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "RSS Feed", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.argb(100,255, 105, 180));
            notificationChannel.setVibrationPattern(new long[]{0, 100, 20, 100, 20, 100, 20, 100});
            notificationChannel.enableVibration(true);
            nManager.createNotificationChannel(notificationChannel);
        }

        final Handler handler = new Handler();

        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
                handler.postDelayed(this, frequency * 60000);
            }
        }, 0);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }




    public void getUserPreference() {

        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        frequency = sharedPref.getInt("frequency", -1);
        itemLimit = sharedPref.getInt("limitItems",-1);
        rssUrl = sharedPref.getString("URL", "");

    }

    public void getRssData(String url) {

        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                newString = response;
                stringsMatch = checkUrlDifference();
                createSharedPreferences(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        queue.add(req);

    }

    private void createSharedPreferences(String xml) {
        SharedPreferences sharedPref = getSharedPreferences("FileName",0);

        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("XML", xml);

        prefEditor.apply();
    }

    private void refresh() {
        getUserPreference();
        getRssData(rssUrl);


        if (!stringsMatch) {
            nBuilder.setSmallIcon(R.drawable.ic_launcher_background);
            nBuilder.setContentTitle("RSS Feeder");
            nBuilder.setContentText("New stories");
            nBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            nBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                    new Intent(this, A1.class), PendingIntent.FLAG_UPDATE_CURRENT));


            nManager.notify(NOTIFICATION_ID, nBuilder.build());
        }
    }

    private boolean checkUrlDifference() {
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        String current = sharedPref.getString("URL", "");
        return newString.equals(current);
    }

}
