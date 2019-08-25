package com.huari.adapter;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PagerAdapterOfSpectrum extends PagerAdapter {

	ArrayList<View> viewlist;

	public PagerAdapterOfSpectrum(ArrayList<View> list) {
		viewlist = list;
	}

	@Override
	public int getCount() {
		return viewlist.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewlist.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewlist.get(position), 0);
		return viewlist.get(position);
	}
}
