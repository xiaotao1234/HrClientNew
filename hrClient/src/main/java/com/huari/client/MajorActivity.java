package com.huari.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.huari.Fragment.LineFragment;
import com.huari.Fragment.OfflineFragment;
import com.huari.adapter.DzPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MajorActivity extends AppCompatActivity {
    ViewPager viewPager;
    DzPagerAdapter dzPagerAdapter;
    TabLayout tabLayout;
    List<Fragment> fragments = new ArrayList<>();
    OfflineFragment offlineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major);
        viewPager = findViewById(R.id.major_viewpager);
        offlineFragment = new OfflineFragment(this, this);
        fragments.add(new LineFragment(this));
        fragments.add(offlineFragment);
        dzPagerAdapter = new DzPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(dzPagerAdapter);
        tabLayout = findViewById(R.id.tl_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("在线");
        tabLayout.getTabAt(1).setText("离线");
        Log.d("xiao", String.valueOf(viewPager.getCurrentItem()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    offlineFragment.clickbomb();
                } else {

                }
        }
    }
}
