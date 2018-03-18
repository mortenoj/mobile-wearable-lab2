package com.example.morten.lab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Button;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.EditText;

public class A2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a2);

        // Sets min and max values to drop downs
        setupNumberPicker();

        // Sets event listener to button
        addListenerOnButton();

        // Get values from userpreferences
        getUserPreference();

    }

    /**
     * setupNumberPicker sets max and min values to dropdowns
     * **/
    private void setupNumberPicker() {
        // Get dropdowns from ids
        NumberPicker limitItems = findViewById(R.id.limitItems);
        NumberPicker frequency = findViewById(R.id.frequency);

        // Set max and min values to limit items
        limitItems.setMinValue(1);
        limitItems.setMaxValue(20);
        limitItems.setWrapSelectorWheel(false);

        // Set max and min values to frequency
        frequency.setMinValue(1);
        frequency.setMaxValue(10);
        frequency.setWrapSelectorWheel(false);
    }

    /**
     * addListenerOnButton sends you from A2 to A1 and saves shared prefs
     * Also stops background service to force the app to update itself with fresh values
     * **/
    private void addListenerOnButton() {
        // finds button by id
        Button button = findViewById(R.id.fetchRssBtn);
        // Set onclick listener to button
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Creates shared preferences
                createSharedPreferences();

                // Stops service
                stopService(new Intent(A2.this, MyService.class));
                // Starts activity with new intent from A2 to A1
                Intent intent = new Intent(A2.this, A1.class);
                startActivity(intent);
            }
        });
    }

    /**
     * createSharedPreferences saves preferences to shared prefs
     * **/
    private void createSharedPreferences() {
        // Get shared prefs
        SharedPreferences sharedPref = getSharedPreferences("FileName",0);

        // Find values from ID
        NumberPicker limitItems = findViewById(R.id.limitItems);
        NumberPicker frequency = findViewById(R.id.frequency);
        EditText rssUrl = findViewById(R.id.rssURl);

        // Save values to shared preferences
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt("limitItems", limitItems.getValue());
        prefEditor.putInt("frequency", frequency.getValue());
        prefEditor.putString("URL", rssUrl.getText().toString());

        prefEditor.apply();
    }

    /**
     * getUserPreference gets prefs and sets the values to elements in the app
     * **/
    private void getUserPreference() {
        // Get shared prefs
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        // Get values from shared prefs
        int limit= sharedPref.getInt("limitItems",-1);
        int freq = sharedPref.getInt("frequency", -1);
        String url = sharedPref.getString("URL", "");

        // Set get elements by id
        NumberPicker limitItems = findViewById(R.id.limitItems);
        NumberPicker frequency = findViewById(R.id.frequency);
        EditText rssUrl = findViewById(R.id.rssURl);

        // Set values to elements in the app
        limitItems.setValue(limit);
        frequency.setValue(freq);
        rssUrl.setText(url);
    }
}
