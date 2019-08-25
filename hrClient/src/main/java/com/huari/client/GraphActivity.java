package com.huari.client;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.huari.ui.CirclePieView;

public class GraphActivity extends AppCompatActivity {
    CirclePieView circlePieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        circlePieView = findViewById(R.id.circle_pie);
        circlePieView.initAnimator();
    }
}
