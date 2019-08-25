package com.huari.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.huari.dataentry.GlobalData;
import com.huari.tools.SysApplication;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Bitmap;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//计划任务功能窗体
public class ScheduledTaskActivity extends Activity implements OnClickListener,
		OnPageChangeListener {
	List<View> viewlist;
	View task, plan;
	LinearLayout l;
	ViewPager viewpager;
	Spinner sp, planspinner, detailspinner;
	RadioGroup rg;
	RadioButton rb1, rb2;
	TextView textview, weekday, tasktextview, plantextview;
	EditText year, month, day, timeet, runday, runhour, runminute, detailet;
	ArrayAdapter<String> weekitems, monthitems;
	FrameLayout frlayout;
	Button detailbutton, surebutton, cancelbutton;
	boolean[] weekboolean;
	boolean[] weektempboolean;
	AlertDialog.Builder ab;
	int currentpage;
	Bitmap bitmap;
	int offset, displaywidth, barwidth;
	ImageView imageview;
	boolean monthboolean = true, dayboolean = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scheduled_task);
		SysApplication.getInstance().addActivity(this);
		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
		task = getLayoutInflater().inflate(R.layout.task, null);
		plan = getLayoutInflater().inflate(R.layout.plan, null);
		rg = (RadioGroup) plan.findViewById(R.id.planradiogroup);
		rb1 = (RadioButton) plan.findViewById(R.id.planfirstrb);
		rb2 = (RadioButton) plan.findViewById(R.id.plansecondrb);
		textview = (TextView) plan.findViewById(R.id.hint);
		tasktextview = (TextView) findViewById(R.id.tasktitle);
		plantextview = (TextView) findViewById(R.id.plantitle);
		weekday = (TextView) plan.findViewById(R.id.dayofweek);
		year = (EditText) plan.findViewById(R.id.year);
		month = (EditText) plan.findViewById(R.id.month);
		day = (EditText) plan.findViewById(R.id.day);
		timeet = (EditText) plan.findViewById(R.id.time);
		runday = (EditText) plan.findViewById(R.id.dayset);
		runhour = (EditText) plan.findViewById(R.id.hourset);
		runminute = (EditText) plan.findViewById(R.id.minuteset);
		sp = (Spinner) task.findViewById(R.id.taskspinner);
		planspinner = (Spinner) plan.findViewById(R.id.planspinner);
		detailspinner = (Spinner) plan.findViewById(R.id.detailspinner);
		detailbutton = (Button) plan.findViewById(R.id.detailbutton);
		frlayout = (FrameLayout) plan.findViewById(R.id.nostable);
		detailet = (EditText) plan.findViewById(R.id.detailet);
		surebutton = (Button) findViewById(R.id.taskcommit);
		cancelbutton = (Button) findViewById(R.id.taskcancle);
		imageview = (ImageView) findViewById(R.id.myimageview);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		displaywidth = metric.widthPixels;
		barwidth = displaywidth / 6 * 2;
		offset = displaywidth / 6;
		weekboolean = new boolean[] { false, false, false, false, false, false,
				false };
		weektempboolean = new boolean[] { false, false, false, false, false,
				false, false };
		frshoworhide();
		tasktextview.setOnClickListener(this);
		plantextview.setOnClickListener(this);
		ab = new AlertDialog.Builder(ScheduledTaskActivity.this);
		ab.setTitle("请选择每周执行任务的日期");
		ab.setMultiChoiceItems(R.array.manydaysofweek, weekboolean,
				new OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							weektempboolean[which] = true;
						}
					}
				});

		ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < weektempboolean.length; i++) {
					if (weektempboolean[i]) {
						weekboolean[i] = true;
						weektempboolean[i] = false;
					}

				}

			}
		});
		ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < weektempboolean.length; i++) {
					weektempboolean[i] = false;
				}
			}
		});
		ab.create();

		/**
		 * 设置计划任务周期的Spinner的监听。
		 */
		planspinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == 1)// 每周执行
				{
					frlayout.setVisibility(View.VISIBLE);
					detailbutton.setVisibility(View.VISIBLE);
					detailet.setVisibility(View.INVISIBLE);
					detailspinner.setVisibility(View.INVISIBLE);

				} else if (arg2 == 2)// 每月执行
				{
					frlayout.setVisibility(View.VISIBLE);
					detailbutton.setVisibility(View.INVISIBLE);
					detailet.setVisibility(View.INVISIBLE);
					detailspinner.setVisibility(View.VISIBLE);
				} else if (arg2 == 4)// 系统空闲
				{
					frlayout.setVisibility(View.VISIBLE);
					detailbutton.setVisibility(View.INVISIBLE);
					detailet.setVisibility(View.VISIBLE);
					detailspinner.setVisibility(View.INVISIBLE);
				} else {
					frlayout.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		/**
		 * 选择星期几会执行任务。可多选。
		 */
		detailbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ab.show();
			}
		});
		Time time = new Time();
		time.setToNow();
		year.setText(time.year + "");
		month.setText((time.month + 1) + "");
		day.setText(time.monthDay + "");
		ArrayAdapter<String> spa = new ArrayAdapter<String>(
				ScheduledTaskActivity.this,
				android.R.layout.simple_spinner_item, new String[] {
						"windows7-ASUS-A450", "Lenovo-Y470", "Samsung-450R4v" });
		sp.setAdapter(spa);
		viewlist = new ArrayList<View>();
		viewlist.add(task);
		viewlist.add(plan);
		viewpager = (ViewPager) findViewById(R.id.taskandplan);
		viewpager.setAdapter(new MyViewPagerAdapter(viewlist));
		viewpager.setOnPageChangeListener(this);

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.planfirstrb) {
					textview.setText("非计划执行，只是简单保存任务执行需要的信息，由用户手动加载执行");
				} else {
					showweekday();
				}
			}
		});

		year.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == false && currentpage == 1
						&& year.getText().toString() != null
						&& month.getText().toString() != null
						&& day.getText().toString() != null) {
					showweekday();
				}
			}
		});

		year.addTextChangedListener(ytw);
		month.addTextChangedListener(mtw);
		day.addTextChangedListener(dtw);
		timeet.addTextChangedListener(timetw);
		surebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					int m = Integer.parseInt(month.getText().toString());
					int d = Integer.parseInt(day.getText().toString());
					int runh = Integer.parseInt(runhour.getText().toString());
					int runm = Integer.parseInt(runminute.getText().toString());
					String temp = timeet.getText().toString();
					int[] timehms = checktime();
					int ho = timehms[0];
					int mi = timehms[1];
					int se = timehms[2];
					if (m > 12 || m < 1 || d > 31 || d < 1 || runh < 0
							|| runh > 24 || runm > 60 || runm < 0 || ho == -1
							|| mi == -1 || se == -1) {
						Toast.makeText(ScheduledTaskActivity.this,
								"输入的参数存在错误或未输入，请重新输入", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(ScheduledTaskActivity.this, "已成功保存",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Toast.makeText(ScheduledTaskActivity.this,
							"输入的参数存在错误或未输入，请重新输入", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void showweekday() {
		String ys = year.getText().toString();
		String ms = month.getText().toString();
		String ds = day.getText().toString();
		int[] timea = checktime();
		if (!ys.equals("") && !ms.equals("") && !ds.equals("")) {
			int y = Integer.parseInt(ys);
			int m = Integer.parseInt(ms);
			int d = Integer.parseInt(ds);
			Calendar c = Calendar.getInstance();
			c.set(y, m - 1, d);
			c.getFirstDayOfWeek();
			String s = null;
			switch (c.get(Calendar.DAY_OF_WEEK)) {
			case 1:
				s = "星期日";
				break;
			case 2:
				s = "星期一";
				break;
			case 3:
				s = "星期二";
				break;
			case 4:
				s = "星期三";
				break;
			case 5:
				s = "星期四";
				break;
			case 6:
				s = "星期五";
				break;
			case 7:
				s = "星期六";
				break;
			}
			weekday.setText(s);

			if (rg.getCheckedRadioButtonId() == R.id.plansecondrb
					&& timea[0] != -1) {
				textview.setText("从" + y + "-" + m + "-" + d + "   " + timea[0]
						+ ":" + timea[1] + ":" + timea[2] + " 开始，"
						+ planspinner.getSelectedItem().toString());
			} else if (rg.getCheckedRadioButtonId() == R.id.plansecondrb
					&& timea[0] == -1) {
				textview.setText("从待定时间开始，"
						+ planspinner.getSelectedItem().toString());
			}
		} else {
			weekday.setText("");
			if (rg.getCheckedRadioButtonId() == R.id.plansecondrb) {
				textview.setText("从待定时间开始，"
						+ planspinner.getSelectedItem().toString());
			}
		}
	}

	private int[] checktime() {
		String times = timeet.getText().toString();
		String[] timearray = times.split(":");
		int[] hmstime = new int[3];
		if (timearray.length == 3 && !timearray[0].equals("")
				&& !timearray[1].equals("") && !timearray[2].equals("")) {
			int a = Integer.parseInt(timearray[0]);
			int b = Integer.parseInt(timearray[1]);
			int c = Integer.parseInt(timearray[2]);
			if (a >= 0 && a <= 23 && b >= 0 && b < 60 && c >= 0 && c < 60) {
				hmstime[0] = a;
				hmstime[1] = b;
				hmstime[2] = c;
			}
		} else {
			hmstime[0] = -1;
			hmstime[1] = -1;
			hmstime[2] = -1;
		}
		return hmstime;
	}

	private void frshoworhide() {
		if (planspinner.getSelectedItem().toString().equals("每周执行")) {
			detailbutton.setVisibility(View.VISIBLE);
			detailet.setVisibility(View.INVISIBLE);
			detailspinner.setVisibility(View.INVISIBLE);
		} else if (planspinner.getSelectedItem().toString().equals("每月执行")) {
			detailbutton.setVisibility(View.INVISIBLE);
			detailet.setVisibility(View.INVISIBLE);
			detailspinner.setVisibility(View.VISIBLE);
		} else if (planspinner.getSelectedItem().toString().equals("系统空闲执行")) {
			detailbutton.setVisibility(View.INVISIBLE);
			detailet.setVisibility(View.VISIBLE);
			detailspinner.setVisibility(View.INVISIBLE);
		} else {
			frlayout.setVisibility(View.INVISIBLE);
		}
	}

	class MyViewPagerAdapter extends PagerAdapter {
		List<View> mylist;

		public MyViewPagerAdapter(List<View> my) {
			mylist = my;
		}

		@Override
		public int getCount() {
			return mylist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
			// super.destroyItem(container, position, object);
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "我的标题";
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(mylist.get(position), 0);
			return mylist.get(position);
			// return super.instantiateItem(container, position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		int single = displaywidth / 2;
		TranslateAnimation ta = new TranslateAnimation(currentpage * single,
				single * arg0, 0, 0);
		ta.setFillAfter(true);
		ta.setDuration(200);
		imageview.startAnimation(ta);
		currentpage = arg0;
		if (arg0 == 1) {
			showweekday();
		}
	}

	@Override
	public void onClick(View v) {
		int single = displaywidth / 2;
		if (v.getId() == R.id.tasktitle) {
			viewpager.setCurrentItem(0);
			if (currentpage != 0) {
				TranslateAnimation ta = new TranslateAnimation(currentpage
						* single, 0, 0, 0);
				ta.setFillAfter(true);
				ta.setDuration(200);
				imageview.startAnimation(ta);
			}
			currentpage = 0;
		}
		if (v.getId() == R.id.plantitle) {
			viewpager.setCurrentItem(1);
			viewpager.setCurrentItem(1);
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

	TextWatcher ytw = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			showweekday();
		}
	};

	TextWatcher mtw = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String tempmonth = s.toString();
			if (!tempmonth.equals("")) {
				int tempd = Integer.parseInt(tempmonth);
				if (tempd > 12 || tempd < 1) {
					Toast.makeText(ScheduledTaskActivity.this,
							"输入的月份不存在，请重新输入", Toast.LENGTH_SHORT).show();
				}

			}
			showweekday();
		}
	};

	TextWatcher dtw = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String tempsss = s.toString();
			if (!tempsss.equals("")) {
				int tempd = Integer.parseInt(tempsss);
				if (tempd > 31 || tempd < 1) {
					Toast.makeText(ScheduledTaskActivity.this,
							"输入的日期不存在，请重新输入", Toast.LENGTH_SHORT).show();
				}
			}

			showweekday();

		}
	};

	TextWatcher timetw = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String tempsss = s.toString();
			if (tempsss.split(":").length == 3) {
				showweekday();
			}

		}
	};
}
