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

        setupNumberPicker();
        addListenerOnButton();
        getUserPreference();

    }

    private void setupNumberPicker() {
        NumberPicker limitItems = findViewById(R.id.limitItems);
        NumberPicker frequency = findViewById(R.id.frequency);

        limitItems.setMinValue(1);
        limitItems.setMaxValue(20);
        limitItems.setWrapSelectorWheel(false);

        frequency.setMinValue(1);
        frequency.setMaxValue(10);
        frequency.setWrapSelectorWheel(false);
    }

    private void addListenerOnButton() {

        Button button = findViewById(R.id.fetchRssBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createSharedPreferences();

                Intent intent = new Intent(A2.this, A1.class);
                // intent.putExtra();
                startActivity(intent);
            }
        });
    }

    private void createSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("FileName",0);

        NumberPicker limitItems = findViewById(R.id.limitItems);
        NumberPicker frequency = findViewById(R.id.frequency);
        EditText rssUrl = findViewById(R.id.rssURl);


        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt("limitItems", limitItems.getValue());
        prefEditor.putInt("frequency", frequency.getValue());
        prefEditor.putString("URL", rssUrl.getText().toString());

        prefEditor.apply();
    }

    private void getUserPreference() {

        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        int limit= sharedPref.getInt("limitItems",-1);
        int freq = sharedPref.getInt("frequency", -1);
        String url = sharedPref.getString("URL", "");

        NumberPicker limitItems = findViewById(R.id.limitItems);
        NumberPicker frequency = findViewById(R.id.frequency);
        EditText rssUrl = findViewById(R.id.rssURl);

        limitItems.setValue(limit);
        frequency.setValue(freq);
        rssUrl.setText(url);
    }
}
