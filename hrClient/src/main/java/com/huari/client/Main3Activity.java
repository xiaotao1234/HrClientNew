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
import com.huari.ui.TagCloudView;

public class Main3Activity extends AppCompatActivity {
    TagCloudView tagCloudView;
    RecyclerView rv;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        tagCloudView = findViewById(R.id.tag_cloud);
        tagCloudView.setBackgroundColor(Color.parseColor("#00000000"));
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new SimpleTestAdapter(this));
        final TagCloudAdapter adapter = new TagCloudAdapter(new String[100], rv);
        tagCloudView.setAdapter(adapter);
        tagCloudView.setSystemUiVisibility(View.INVISIBLE);
    }
}
