package com.gnr.esgi.taggle.activitie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.constant.ArticleConstants;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (WebView) findViewById(R.id.detail_article_webview);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        webView.loadUrl(
                intent.getStringExtra(
                        ArticleConstants.ARTICLE_KEY_SOURCE_URL
                ));
    }
}
