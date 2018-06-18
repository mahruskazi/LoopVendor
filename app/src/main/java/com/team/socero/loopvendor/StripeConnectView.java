package com.team.socero.loopvendor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class StripeConnectView extends AppCompatActivity {

    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_connect_view);
        StripeWebViewClient client = new StripeWebViewClient();


        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(client);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.loadUrl("https://connect.stripe.com/oauth/authorize?response_type=code&client_id=ca_CZ4aDK786Nldnls07ScF72v6UnNbhMvN&scope=read_write");



    }

    private class StripeWebViewClient extends WebViewClient{

        @Override
        public void onPageFinished(WebView view, String url) {

            if(url.contains("code=")) {
                String token = url.substring(url.indexOf("code=") + "code=".length());
                Intent intent = new Intent();
                intent.putExtra("token", token);
                setResult(RESULT_OK, intent);
                finish();
            } else if(url.contains("error")){
                Intent intent = new Intent();
                intent.putExtra("token", "User Canceled");
                setResult(RESULT_CANCELED, intent);
                finish();
            }

            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Log.d("StripeConnect", "shouldOverrideUrlLoading: " + url);

            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
