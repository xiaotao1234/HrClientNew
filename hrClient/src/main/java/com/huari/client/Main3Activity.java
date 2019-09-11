package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.huari.adapter.SimpleTestAdapter;
import com.huari.adapter.TagCloudAdapter;
import com.huari.dataentry.recentContent;
import com.huari.tools.FileOsImpl;
import com.huari.ui.TagCloudView;

import java.util.List;

public class Main3Activity extends AppCompatActivity {
    TagCloudView tagCloudView;
    RecyclerView rv;
    SimpleTestAdapter simpleTestAdapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<recentContent> list = (List<recentContent>) msg.obj;
            simpleTestAdapter.setRecentContent(list);
            if (rv != null) {
                rv.setAdapter(simpleTestAdapter);
            }
            final TagCloudAdapter adapter = new TagCloudAdapter(list, rv);
            tagCloudView.setAdapter(adapter);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        tagCloudView = findViewById(R.id.tag_cloud);
        tagCloudView.setBackgroundColor(Color.parseColor("#00000000"));
        simpleTestAdapter = new SimpleTestAdapter();
        simpleTestAdapter.setContext(this);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        FileOsImpl.getRecentList(handler); //  请求刷新历史数据
        tagCloudView.setSystemUiVisibility(View.INVISIBLE);
    }
}
