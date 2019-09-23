package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.huari.Fragment.LineFragment;
import com.huari.Fragment.OfflineFragment;
import com.huari.Fragment.OfflineFragment2;
import com.huari.Fragment.StationShowFragment;
import com.huari.adapter.DzPagerAdapter;

import java.util.ArrayList;

import java.util.List;

public class MajorActivity extends AppCompatActivity {
    ViewPager viewPager;
    DzPagerAdapter dzPagerAdapter;
    TabLayout tabLayout;
    List<Fragment> fragments = new ArrayList<>();
    OfflineFragment offlineFragment;
    OfflineFragment2 offlineFragment2;
    boolean back = false;
    long time;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (time == 0) {
            time = System.currentTimeMillis();
            Toast.makeText(this, "再次点击退出应用", Toast.LENGTH_SHORT).show();
        } else {
            if (System.currentTimeMillis() - time < 2000) {
                super.onBackPressed();
            }else {
                time = System.currentTimeMillis();
                Toast.makeText(this, "再次点击退出应用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        back = false;
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major);
        viewPager = findViewById(R.id.major_viewpager);
        offlineFragment = new OfflineFragment(this, this);
        offlineFragment2 = new OfflineFragment2(this,MajorActivity.this);
        fragments.add(offlineFragment2);
        fragments.add(new LineFragment(this));
//        fragments.add(offlineFragment);
        dzPagerAdapter = new DzPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(dzPagerAdapter);
        tabLayout = findViewById(R.id.tl_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("本地");
        tabLayout.getTabAt(1).setText("登陆");
        Log.d("xiao", String.valueOf(viewPager.getCurrentItem()));
    }

    public void setFragment() {
        fragments.clear();
        fragments.add(new OfflineFragment2(this,MajorActivity.this));
        fragments.add(new StationShowFragment(this));
        DzPagerAdapter dzPagerAdapter = new DzPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.removeAllViews();
        viewPager.removeAllViewsInLayout();
        viewPager.setAdapter(dzPagerAdapter);
        viewPager.setCurrentItem(1);
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("本地");
        tabLayout.getTabAt(1).setText("登陆");
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 1:
//                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    offlineFragment.clickbomb();
//                } else {
//
//                }
//        }
//    }
}
