package com.example.morten.lab2;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import android.widget.AdapterView;

import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;



public class A1 extends AppCompatActivity {

    String                  xml;
    private JSONObject      feed        = null;
    int                     itemLimit   = 0;
    JSONArray               itemArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a1);

        // Starts service
        if (!isMyServiceRunning()) {
            Intent serviceIntent = new Intent(this, MyService.class);
            this.startService(serviceIntent);
        }

        // Add event listener to button
        addListenerOnButton();

        // Delays to catch up with shared prefs that runs async
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Gets user prefs
                                getUserPreference();
                                // Parses xml to json
                                stringToJson(xml);
                                // Updates listview
                                updateListView();
                            }
                        });
                    }
                }, 500); // Delay half a second

    }

    /**
     * addListenerOnButton from main view to preferences view
     * **/
    private void addListenerOnButton() {
        // Find button by id
        Button button = findViewById(R.id.prefButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start activity with intent from A1 to A2
                Intent intent = new Intent(A1.this, A2.class);
                startActivity(intent);
            }
        });
    }

    /**
     * getUserPreference get limit items and xml document from shared prefs
     * **/
    private void getUserPreference() {
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        itemLimit = sharedPref.getInt("limitItems",-1);
        xml = sharedPref.getString("XML", "");
    }


    /**
     * stringToJson parses xml strings to json objects and arrays
     * **/
    private void stringToJson(String req) {

        try {
            feed = XML.toJSONObject(req);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
        }

    }

    /**
     * updateListView loops through correct json array and sets values to list view
     * **/
    private void updateListView() {
        // Find list view by id
        ListView list = findViewById(R.id.listView);

        // Array of strings containing values for the list
        ArrayList<String> stringArr = new ArrayList<>();

        try {
            // Get item array from json object rss
            itemArray = feed.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");

            // Loop through array and add the strings to the string array list
            for (int i = 0; i < itemLimit; i++) {
                String title = itemArray.getJSONObject(i).getString("title");
                stringArr.add(title);
            }
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
        }

        // Create an adapter with the string array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(A1.this, android.R.layout.simple_list_item_1, stringArr);
        // Set the list's adapter to the new adapter
        list.setAdapter(adapter);

        // Add listener on list view
        addListenerOnListView();
    }

    /**
     * addListenerOnListView starts intents and activities to a3 with the correct data
     * A3 renders this data in a web view
     * **/
    private void addListenerOnListView() {
        // Get list view from id
        ListView listview = findViewById(R.id.listView);

        // set onclick to list view
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(A1.this, A3.class);
                try {
                    // Put title, description and url to extras in intent
                    intent.putExtra("title", itemArray.getJSONObject(position).getString("title"));
                    intent.putExtra("description", itemArray.getJSONObject(position).getString("description"));
                    intent.putExtra("link", itemArray.getJSONObject(position).getString("link"));
                } catch (JSONException e) {
                    Log.e("JSON exception", e.getMessage());
                }
                // Start activity with intent
                startActivity(intent);
            }
        });
    }

    /**
     * isMyServiceRunning checks if the service is running
     * **/
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
