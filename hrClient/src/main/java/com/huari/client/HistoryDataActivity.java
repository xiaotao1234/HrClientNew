package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class HistoryDataActivity extends AppCompatActivity {
    LinearLayout danpin;
    LinearLayout pinpu;
    LinearLayout pp;
    LinearLayout music;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data);
        init();
        events();
    }

    private void events() {
        danpin.setOnClickListener(v -> click(HistoryListActivity.DF));
        pinpu.setOnClickListener(v -> click(HistoryListActivity.AN));
        pp.setOnClickListener(v -> click(HistoryListActivity.PD));
        music.setOnClickListener(v -> click(HistoryListActivity.RE));
    }

    private void click(String s) {
        intent = new Intent(HistoryDataActivity.this, HistoryListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type",s);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void init() {
        danpin = findViewById(R.id.danpin_history);
        pinpu = findViewById(R.id.pinpu_history);
        pp = findViewById(R.id.danp_history);
        music = findViewById(R.id.music_history);
    }
}
