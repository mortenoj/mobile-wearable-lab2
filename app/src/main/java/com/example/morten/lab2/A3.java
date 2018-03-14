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
        renderContent();
    }

    private void renderContent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            TextView title = findViewById(R.id.title);
            title.setText(bundle.getString("title"));

            TextView description = findViewById(R.id.description);
            description.setText(bundle.getString("description"));

            String link = bundle.getString("link");

            addListenerOnButton();
            addListenerTextView(link);

        }


    }

    private void addListenerTextView(final String url) {
        TextView link = findViewById(R.id.link);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens link in Chrome or default web browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                // Opens link in web view
                // addListenerWebView(url);
            }
        });
    }

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

    private void addListenerOnButton() {

        Button button = findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(A3.this, A1.class);
                startActivity(intent);
            }
        });
    }
}
