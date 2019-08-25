package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

public class PrepareActivity extends AppCompatActivity {
    MKOfflineMap offlinemap;
    MKOfflineMapListener mkoml = (arg0, arg1) -> {};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);
        offlinemap = new MKOfflineMap();
        offlinemap.init(mkoml);
    }
}
