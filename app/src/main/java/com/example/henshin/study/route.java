package com.example.henshin.study;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class route extends AppCompatActivity {
    private SharedPreferences sp;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        sp=getSharedPreferences("setting", 0);
        String username =sp.getString("username",null);
        url = "http://idashuai.cf/LoveStudy/findRoute.jsp?username="+username+"&button=submit";

        test();
    }
    private void test(){
        WebView wv = (WebView)findViewById(R.id.word_web_view);

        WebSettings webSettings = wv.getSettings();
        wv.loadUrl(url);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);//允许DCOM
    }
}
