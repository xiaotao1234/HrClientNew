package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.huari.adapter.MusicListAdapter;

public class MusicListActivity extends AppCompatActivity {
    RecyclerView musicList;
    MusicListAdapter musicListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        init();
    }

    private void init() {
        musicList = findViewById(R.id.music_list);
//        musicListAdapter = new MusicListAdapter();
        musicList.setSystemUiVisibility(View.INVISIBLE);
        musicList.setLayoutManager(new LinearLayoutManager(this));
        musicList.setAdapter(musicListAdapter);
        musicList.setSystemUiVisibility(View.INVISIBLE);
    }
}
