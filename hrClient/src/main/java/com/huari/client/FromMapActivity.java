package com.huari.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.huari.dataentry.GlobalData;
import com.huari.dataentry.LogicParameter;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Parameter;
import com.huari.dataentry.Station;

import android.support.v7.app.ActionBar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class FromMapActivity extends AppCompatActivity implements
		OnClickListener, OnPageChangeListener {

	ViewPager vp;
	List<View> mylist;
	View ger, adv;
	LinearLayout gerLinearLayout, advLinearLayout;
	int offset, displaywidth, barwidth;
	ImageView imageview;
	int currentpage;
	TextView normaltextview, advancedtextview;

	String[] namesofitems, advanceditems, generalparent, generaletdata,
			advancedparent;// 每个设置选项的名字,常规、高级
	//private int generalindex;

	Dialog dialog;
	MenuItem saveItem, cancleItem;

	Button bn;
	ArrayList<String> gBigList, aBigList;
	String logicId;
	String stationName, deviceName, stationId;

	ArrayList<Parameter> generalparameters;
	ArrayList<Parameter> advancedparameters;

	HashMap<String, String> oldValues;
	HashMap<String, String> newValues;
	View titlebar;
	ActionBar actionbar;

	class MyTextWatcher implements TextWatcher {
		String key;

		public MyTextWatcher(String s) {
			key = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String m = s.toString();
			if (newValues.containsKey(key)) {
				newValues.remove(key);
			}
			newValues.put(key, m);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pinduansetactivity);
		SysApplication.getInstance().addActivity(this);
		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		vp = (ViewPager) findViewById(R.id.pinduangeneralandadvancedset);
		imageview = (ImageView) findViewById(R.id.pinduanmenubar);
		ger = (View) getLayoutInflater().inflate(R.layout.generalset, null);
		adv = (View) getLayoutInflater().inflate(R.layout.advancedset, null);

		gerLinearLayout = (LinearLayout) ger.findViewById(R.id.myppfxgeneral);
		advLinearLayout = (LinearLayout) adv.findViewById(R.id.myppfxadvanced);

		normaltextview = (TextView) findViewById(R.id.pinduannormal);
		advancedtextview = (TextView) findViewById(R.id.pinduanadvanced);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		displaywidth = metric.widthPixels;
		barwidth = displaywidth / 6 * 2;
		offset = displaywidth / 6;
		mylist = new ArrayList<View>();
		mylist.add(ger);
		mylist.add(adv);
		vp.setAdapter(new MyPagerAdapter(mylist));
		vp.setOnPageChangeListener(this);
		normaltextview.setOnClickListener(this);
		advancedtextview.setOnClickListener(this);

		gBigList = new ArrayList<String>();
		aBigList = new ArrayList<String>();

		oldValues = new HashMap<String, String>();
		newValues = new HashMap<String, String>();

		Intent intent = getIntent();
		Bundle mybundle = intent.getExtras();
		stationName = mybundle.getString("sname");// 该设置界面所属于的设备的名字、台站的名字、台站的id、逻辑数据的id
		deviceName = mybundle.getString("dname");
		stationId = mybundle.getString("stakey");
		logicId = mybundle.getString("lids");

		generalparameters = new ArrayList<Parameter>();
		advancedparameters = new ArrayList<Parameter>();

		loadparameters();

		dialog = new AlertDialog.Builder(FromMapActivity.this)
				.setTitle("确定要保存参数更改吗？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						loadparameters();
						FromMapActivity.this.finish();
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						refreshParameters();
						Intent ine = new Intent(FromMapActivity.this,
								MapActivity.class);
						GlobalData.stationKey = stationId;
						GlobalData.deviceName = deviceName;
						GlobalData.logicId = logicId;
						Message a = MapActivity.handler.obtainMessage();
						a.what = MapActivity.HUAXIAN;
						MapActivity.handler.sendMessage(a);
						GlobalData.itemTitle = "开始示向";
						startActivity(ine);
						// MapActivity.handler.sendEmptyMessage(MapActivity.HUAXIAN);
						FromMapActivity.this.finish();
					}
				}).create();
	}

	private void loadparameters() {
		try {
			gerLinearLayout.removeAllViewsInLayout();
		} catch (Exception e) {

		}
		try {
			advLinearLayout.removeAllViewsInLayout();
		} catch (Exception e) {

		}
		MyDevice iDevice = null;
		for (MyDevice myd : GlobalData.stationHashMap.get(stationId).devicelist) {
			if (myd.name.equals(deviceName)) {
				iDevice = myd;
			}
		}
		LogicParameter currentLogic = iDevice.logic.get(logicId);

		for (Parameter p : currentLogic.parameterlist) {
			if (p.isAdvanced == 0) {
				advancedparameters.add(p);
				if (!aBigList.contains(p.displayType)) {
					aBigList.add(p.displayType);
				}
			} else {
				generalparameters.add(p);
				if (!gBigList.contains(p.displayType)) {
					gBigList.add(p.displayType);
				}
			}
			
		}

		for (String s : gBigList) {
			LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(
					R.layout.big_parameter, null);
			TextView tv = (TextView) ll.findViewById(R.id.bigparamtertextview);
			tv.setText(s);
			gerLinearLayout.addView(ll);// 将大类参数显示在界面上，下面开始加载附挂到它下面的子选项
			for (Parameter p : currentLogic.parameterlist) {
				if (p.displayType.equals(s) && p.isEditable == 1
						&& p.isAdvanced == 1) {
					LinearLayout ly = (LinearLayout) getLayoutInflater()
							.inflate(R.layout.ppfxchildwithet, null);
					TextView tiew = (TextView) ly
							.findViewById(R.id.exchilditemtv);
					EditText ext = (EditText) ly
							.findViewById(R.id.exchilditemet);
					ext.addTextChangedListener(new MyTextWatcher(p.name));
					ext.setText(p.defaultValue);
					tiew.setText(p.name);
					gerLinearLayout.addView(ly);
				} else if (p.displayType.equals(s) && p.isEditable == 0
						&& p.isAdvanced == 1) {
					LinearLayout ly = (LinearLayout) getLayoutInflater()
							.inflate(R.layout.ppfxwithspinner, null);
					TextView tiew = (TextView) ly
							.findViewById(R.id.exchilditemttv);
					final Spinner sp = (Spinner) ly
							.findViewById(R.id.exchildsp);
					Spinner tempsp = sp;
					if (p.enumValues != null) {
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								FromMapActivity.this,
								R.layout.customspinnertextview, p.enumValues);
						final String[] temp = p.enumValues;
						final String tempname = p.name;

						sp.setAdapter(adapter);
						int temps = 0;
						for (String st : p.enumValues) {
							if (p.defaultValue.trim().equals(st)) {
								break;
							} else {
								temps++;
							}

						}
						if (temps == p.enumValues.length)
							temps = 0;
						sp.setSelection(temps);
						sp.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								String value = sp.getSelectedItem().toString();
								if (newValues.containsKey(tempname)) {
									newValues.remove(tempname);
								}
								newValues.put(tempname, value);
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
								// TODO Auto-generated method stub

							}
						});
					}
					tiew.setText(p.name);
					gerLinearLayout.addView(ly);
				}

			}
		}

		for (String s : aBigList) {
			LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(
					R.layout.big_parameter, null);
			TextView tv = (TextView) ll.findViewById(R.id.bigparamtertextview);
			tv.setText(s);
			advLinearLayout.addView(ll);// 将大类参数显示在界面上，下面开始加载附挂到它下面的子选项
			for (Parameter p : currentLogic.parameterlist) {
				if (p.displayType.equals(s) && p.isEditable == 1
						&& p.isAdvanced == 0) {
					LinearLayout ly = (LinearLayout) getLayoutInflater()
							.inflate(R.layout.ppfxchildwithet, null);
					TextView tiew = (TextView) ly
							.findViewById(R.id.exchilditemtv);
					EditText ext = (EditText) ly
							.findViewById(R.id.exchilditemet);
					ext.addTextChangedListener(new MyTextWatcher(p.name));

					tiew.setText(p.name);
					gerLinearLayout.addView(ly);
				} else if (p.displayType.equals(s) && p.isEditable == 0
						&& p.isAdvanced == 0) {
					LinearLayout ly = (LinearLayout) getLayoutInflater()
							.inflate(R.layout.ppfxwithspinner, null);
					TextView tiew = (TextView) ly
							.findViewById(R.id.exchilditemttv);
					final Spinner sp = (Spinner) ly
							.findViewById(R.id.exchildsp);
					Spinner tempsp = sp;
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							FromMapActivity.this,
							R.layout.customspinnertextview, p.enumValues);
					final String[] temp = p.enumValues;
					final String tempname = p.name;
					sp.setAdapter(adapter);
					int temps = 0;
					for (String st : p.enumValues) {
						if (p.defaultValue.trim().equals(st.trim())) {
							break;
						} else {
							temps++;
						}

					}
					if (temps == p.enumValues.length)
						temps = 0;
					sp.setSelection(temps);
					sp.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							String value = sp.getSelectedItem().toString();
							if (newValues.containsKey(tempname)) {
								newValues.remove(tempname);
							}
							newValues.put(tempname, value);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub

						}
					});
					tiew.setText(p.name);
					advLinearLayout.addView(ly);
				}

			}
		}

	}

	private void refreshParameters()// 更新该设备的参数
	{
		Station station = GlobalData.stationHashMap.get(stationId);
		MyDevice md = null;
		for (MyDevice mydevice : station.devicelist) {
			if (mydevice.name.equals(deviceName)) {
				md = mydevice;
			}
		}
		LogicParameter logicparameter = md.logic.get(logicId);
		ArrayList<Parameter> pa = logicparameter.parameterlist;
		for (Parameter p : pa) {
			if (newValues.containsKey(p.name)) {
				p.defaultValue = newValues.get(p.name);
				// System.out.println(p.defaultValue+"");
			}
		}
		loadparameters();
		if (SinglefrequencyDFActivity.handler != null) {
			SinglefrequencyDFActivity.handler
					.sendEmptyMessage(SinglefrequencyDFActivity.PARAMETERSREFRESH);
		}

	}

	class MyPagerAdapter extends PagerAdapter {
		List<View> list;

		public MyPagerAdapter(List<View> l) {
			list = l;
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
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return "我的标题";
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(list.get(position), 0);
			return list.get(position);
			// return super.instantiateItem(container, position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		int single = displaywidth / 2;
		TranslateAnimation ta = new TranslateAnimation(currentpage * single,
				single * arg0, 0, 0);
		ta.setFillAfter(true);
		ta.setDuration(200);
		imageview.startAnimation(ta);
		currentpage = arg0;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int single = displaywidth / 2;
		if (v.getId() == R.id.pinduannormal) {
			vp.setCurrentItem(0);
			if (currentpage != 0) {
				TranslateAnimation ta = new TranslateAnimation(currentpage
						* single, 0, 0, 0);
				ta.setFillAfter(true);
				ta.setDuration(200);
				imageview.startAnimation(ta);
			}
			currentpage = 0;
		}
		if (v.getId() == R.id.pinduanadvanced) {
			vp.setCurrentItem(1);
			if (currentpage != 1) {
				TranslateAnimation ta = new TranslateAnimation(currentpage
						* single, single, 0, 0);
				ta.setFillAfter(true);
				ta.setDuration(200);
				imageview.startAnimation(ta);
			}
			currentpage = 1;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog.show();
			// PinDuanSetActivity.this.finish();
		}
		return true;
	}

}
