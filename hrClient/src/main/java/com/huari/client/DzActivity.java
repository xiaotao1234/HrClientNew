package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.huari.adapter.DzPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DzActivity extends AppCompatActivity {
    ViewPager viewPager;
    DzPagerAdapter dzPagerAdapter;
    List<Fragment> fragments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz);


//        viewPager = findViewById(R.id.viewpager_dz);
//        fragments.add(new LineFragment());
//        fragments.add(new OfflineFragment(this));
//        dzPagerAdapter = new DzPagerAdapter(getSupportFragmentManager(),fragments);
//        viewPager.setAdapter(dzPagerAdapter);
    }
}
