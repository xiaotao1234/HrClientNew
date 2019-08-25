package com.huari.client;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.huari.tools.SysApplication;

public class GuideActivity extends Activity {
	ViewPager vp;
	List<View> viewlist;
	List<Button> bnlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		SysApplication.getInstance().addActivity(this);
		vp = (ViewPager) findViewById(R.id.vp);
		viewlist = new ArrayList<View>();
		bnlist = new ArrayList<Button>();
		View v1 = getLayoutInflater().inflate(R.layout.guideoneview, null);
		View v2 = getLayoutInflater().inflate(R.layout.guidetwoview, null);
		View v3 = getLayoutInflater().inflate(R.layout.guidethreeview, null);
		Button bn1 = (Button) v1.findViewById(R.id.firstguidebn);
		Button bn2 = (Button) v2.findViewById(R.id.secondguidebn);
		Button bn3 = (Button) v3.findViewById(R.id.thirdguidebn);
		bnlist.add(bn1);
		bnlist.add(bn2);
		bnlist.add(bn3);
		viewlist.add(v1);
		viewlist.add(v2);
		viewlist.add(v3);
		vp.setAdapter(new MyPagerAdapter(viewlist));
		for (int i = 0; i < bnlist.size(); i++) {
			Button bn = bnlist.get(i);
			bn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(GuideActivity.this,
							MainActivity.class));
				}
			});
		}

	}

	class MyPagerAdapter extends PagerAdapter {
		List<View> list;

		public MyPagerAdapter(List<View> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView((View) object);
			// super.destroyItem(container, position, object);
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return super.getPageTitle(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(list.get(position), 0);
			return list.get(position);
			// return super.instantiateItem(container, position);
		}
	}

}
