package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huari.adapter.MusicListAdapter;
import com.huari.dataentry.HistoryDataDescripe;
import com.huari.tools.SysApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryListActivity extends AppCompatActivity {
    List<HistoryDataDescripe> list;
    public static String DF = "DF";
    public static String AN = "AN";
    public static String PD = "PD";
    public static String RE = "RE";
    private String i = "DF";

    TextView top;
    RecyclerView recyclerView;
    MusicListAdapter musicListAdapter;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        i = bundle.getString("type");
        init();
    }

    public void init() {
        list = new ArrayList<>();
        top = findViewById(R.id.item_name);
        recyclerView = findViewById(R.id.history_list);
        switch (i) {
            case "DF":
                top.setText("单频测向");
                break;
            case "AN":
                top.setText("频谱分析");
                break;
            case "PD":
                top.setText("频段扫描");
                break;
            case "RE":
                top.setText("音频");
                break;
            default:
                break;
        }
        File file = new File(SysApplication.fileOs.forSaveFloder + File.separator + "data");
        for (File file1 : file.listFiles()) {
            if (file1.getName().contains(i)) {
                list.add(new HistoryDataDescripe(file1.getName(), file1.getAbsolutePath(), file1.lastModified(), file1.length()));
            }
        }
        musicListAdapter = new MusicListAdapter(list, this, i);
        recyclerView = findViewById(R.id.history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicListAdapter);
        recyclerView.setSystemUiVisibility(View.INVISIBLE);
    }
}
