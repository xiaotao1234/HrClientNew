package com.huari.adapter;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class DzPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public DzPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    public void changeData(int i,Fragment fragment){
        List<Fragment> fragmentss = new ArrayList<>();
        for(int j = 0;j<fragments.size();j++){
            if(j == i){
                fragmentss.add(fragment);
            }else {
                fragmentss.add(fragments.get(j));
            }
        }
        fragments = fragmentss;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
