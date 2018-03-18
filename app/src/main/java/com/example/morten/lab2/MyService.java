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

    public MyService() {}

    /**
     * onCreate automatic function that runs when service is created
     * **/
    public void onCreate() {
        // Variables for notification
        nBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // sets notification channel
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


        // Avoids null pointer exception error when doing a request
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        // Handler variable used for postDelayed function
        final Handler handler = new Handler();
        // Runs repeatably every given minute
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Calls other function that runs the service
                refresh();
                handler.postDelayed(this, frequency * 60000); // delay on frequency times a minute
            }
        }, 0);

    }

    /**
     * onStartCommand default function
     * **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * onDestroy default function that runs when service stops
     * **/
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * onBind default function that runs when service is binded
     * **/
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }


    /**
     * getUserPreference get user prefs from shared prefs
     * **/
    public void getUserPreference() {
        // Get shared prefs
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        // Get values from shared prefs
        frequency = sharedPref.getInt("frequency", -1);
        itemLimit = sharedPref.getInt("limitItems",-1);
        rssUrl = sharedPref.getString("URL", "");

    }

    /**
     * getRssData function that makes request to given url and
     * saves the xml to shared prefs
     * **/
    public void getRssData(String url) {

        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Check if old xml and new xml matches to send a notification if different
                newString = response.replaceAll("\\s+", "");
                stringsMatch = checkUrlDifference();
                // Save shared prefs
                createSharedPreferences(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add request to queue
        queue.add(req);

    }

    /**
     * createSharedPreferences saves xml to shared prefs
     * **/
    private void createSharedPreferences(String xml) {
        SharedPreferences sharedPref = getSharedPreferences("FileName",0);

        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("XML", xml);

        prefEditor.apply();
    }

    /**
     * refresh function calls getUserPreference and getRssData
     * checks if the old xml and new xml are different and notifies the client
     * **/
    private void refresh() {
        getUserPreference();
        getRssData(rssUrl);

        // If old xml and new xml doesn't match
        if (!stringsMatch) {
            // Create and send a notification to user
            nBuilder.setSmallIcon(R.drawable.ic_launcher_background);
            nBuilder.setContentTitle("RSS Feeder");
            nBuilder.setContentText("New stories");
            nBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            nBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                    new Intent(this, A1.class), PendingIntent.FLAG_UPDATE_CURRENT));


            nManager.notify(NOTIFICATION_ID, nBuilder.build());
        }
    }

    /**
     * checkUrlDifference gets url from shared prefs matches it with global variable newString
     * **/
    private boolean checkUrlDifference() {
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        String current = sharedPref.getString("URL", "").replaceAll("//s+", "");
        return newString.equals(current);
    }

}
