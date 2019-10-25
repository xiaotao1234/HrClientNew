package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.huari.adapter.DzPagerAdapter;
import com.huari.ui.CirclePainView;

import java.util.ArrayList;
import java.util.List;

public class DzActivity extends AppCompatActivity {
    CirclePainView dlCircleView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz);
        dlCircleView = findViewById(R.id.ampere);
        dlCircleView.setSystemUiVisibility(View.INVISIBLE);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
    }
}
