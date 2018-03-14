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

        if (!isMyServiceRunning()) {
            Intent serviceIntent = new Intent(this, MyService.class);
            this.startService(serviceIntent);
        }

        addListenerOnButton();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getUserPreference();
                                stringToJson(xml);
                                updateListView();
                            }
                        });
                    }
                }, 500);


    }


    private void addListenerOnButton() {

        Button button = findViewById(R.id.prefButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(A1.this, A2.class);
                startActivity(intent);
            }
        });
    }

    private void getUserPreference() {
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        itemLimit = sharedPref.getInt("limitItems",-1);
        xml = sharedPref.getString("XML", "");
    }



    private void stringToJson(String req) {

        try {
            feed = XML.toJSONObject(req);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
        }

    }

    private void updateListView() {
        ListView list = findViewById(R.id.listView);

        ArrayList<String> stringArr = new ArrayList<>();

        try {
            itemArray = feed.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
            for (int i = 0; i < itemLimit; i++) {
                String title = itemArray.getJSONObject(i).getString("title");
                stringArr.add(title);
            }
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(A1.this, android.R.layout.simple_list_item_1, stringArr);
        list.setAdapter(adapter);

        addListenerOnListView();
    }

    private void addListenerOnListView() {

        ListView listview = findViewById(R.id.listView);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(A1.this, A3.class);
                try {
                    intent.putExtra("title", itemArray.getJSONObject(position).getString("title"));
                    intent.putExtra("description", itemArray.getJSONObject(position).getString("description"));
                    intent.putExtra("link", itemArray.getJSONObject(position).getString("link"));
                } catch (JSONException e) {
                    Log.e("JSON exception", e.getMessage());
                }

                startActivity(intent);
            }
        });
    }

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
