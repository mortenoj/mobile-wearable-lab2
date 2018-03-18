package com.example.morten.lab2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;


public class A3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a3);

        // Fetches data from intent and renders content
        renderContent();
    }

    /**
     * renderContent gets data from intent as extras and sets title and description
     * then adds listener on a button that opens a webview or in web browser
     * **/
    private void renderContent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Sets title to title from extras
            TextView title = findViewById(R.id.title);
            title.setText(bundle.getString("title"));

            // Sets description to description from extras
            TextView description = findViewById(R.id.description);
            description.setText(bundle.getString("description"));

            // Gets url from extras
            String link = bundle.getString("link");

            // Add listener to button
            addListenerOnButton();

            // Set listener to add more url with given url from extras
            addListenerTextView(link);

        }


    }

    /**
     * addListenerTextView either opens browser or web view
     * **/
    private void addListenerTextView(final String url) {
        // Get link from Id
        TextView link = findViewById(R.id.link);

        // Set onclick listener
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens link in Chrome or default web browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

                // Added functionality for opening in web view but I liked using browser
                // Opens link in web view
                // addListenerWebView(url);
            }
        });
    }

    /**
     * addListenerWebView takes url and starts a webview
     * is currently not used**/
    private void addListenerWebView(final String url) {
        WebView link = findViewById(R.id.webview);
        link.setVisibility(View.VISIBLE);
        link.loadUrl(url);
        link.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });
    }

    /**
     * addListenerOnButton sets listener to back button
     * sends you back to Main Activity or A1**/
    private void addListenerOnButton() {

        Button button = findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // New intent from A3 to A1
                Intent intent = new Intent(A3.this, A1.class);
                // Starts new activity
                startActivity(intent);
            }
        });
    }
}
