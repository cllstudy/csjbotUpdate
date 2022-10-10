package com.cll.csjupdate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cll.csjbotupdate.AppUpdater;

public class MainActivity extends AppCompatActivity {
    private static final String UPDATE_URL = "https://aztest.csjbot.com:8443/csjbot-service/api/robotAppInfoVersion/getByAppId?appId=001dfd7b325b4ee0976ac9247cd65ca3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AppUpdater.with(MainActivity.this)
//                .setHostUpdateCheckUrl(UPDATE_URL)
//                .setUpdateNavi(false,0)
//                .check();
    }
}