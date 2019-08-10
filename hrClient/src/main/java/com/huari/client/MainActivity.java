package com.huari.client;

import com.huari.dataentry.GlobalData;
import com.huari.service.MainService;

import android.os.Bundle;
import android.os.Handler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	ViewGroup v;
	int buttoncount;
	Button bns;
	LinearLayout ipsetLayout;
	Button ipcancle, ipsave;
	EditText iptextview, port1textview, port2textview;

	ActionBar bar;

	SharedPreferences preferences;
	SharedPreferences.Editor seditor;
	int saveStationCount;// 单频测向，多线交汇指示出信号源方向时会用到，
							// 表示已经保存了多少个示向度。删除数据不会使其变小，主要用作key的一部分。
	String ip;
	int port1, port2;
	AlertDialog dialog;

	public static int LINKFAILED = 1;
	public static int LINKSUCCESS = 2;

	public static Handler handler;
	Intent serviceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		SysApplication.getInstance().addActivity(this);
		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
		bar = getSupportActionBar();
		bar.setTitle(GlobalData.mainTitle);

		bns = (Button) findViewById(R.id.seven2);
		ipsetLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.ipsetdialog, null);
		iptextview = (EditText) ipsetLayout.findViewById(R.id.ipstring);
		port1textview = (EditText) ipsetLayout.findViewById(R.id.ipport1);
		port2textview = (EditText) ipsetLayout.findViewById(R.id.ipport2);

		preferences = getSharedPreferences("myclient", MODE_PRIVATE);
		seditor = preferences.edit();
		ip = preferences.getString("ip", "192.168.1.1");
		port1 = preferences.getInt("port1", 5000);
		port2 = preferences.getInt("port2", 5012);
		saveStationCount = preferences.getInt("savecount", -1);
		if (saveStationCount == -1) {
			seditor.putInt("savecount", 0);
		}

		GlobalData.mainIP = ip;
		GlobalData.port1 = port1;
		GlobalData.port2 = port2;

		iptextview.setText(ip);
		port1textview.setText(port1 + "");
		port2textview.setText(port2 + "");

		serviceIntent = new Intent();
		serviceIntent.setAction("com.huari.service.mainservice");

		dialog = new AlertDialog.Builder(MainActivity.this).setTitle("服务器登录设置")
				.setView(ipsetLayout)
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						iptextview.setText(ip);
						port1textview.setText(port1 + "");
						port2textview.setText(port2 + "");
						GlobalData.mainIP = ip;
						GlobalData.port1 = port1;
						GlobalData.port2 = port2;
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						try {
							ip = iptextview.getText().toString();
							port1 = Integer.parseInt(port1textview.getText()
									.toString());
							port2 = Integer.parseInt(port2textview.getText()
									.toString());
							seditor.putInt("port1", port1);
							seditor.putInt("port2", port2);
							seditor.putString("ip", ip);
							GlobalData.mainIP = ip;
							GlobalData.port1 = port1;
							GlobalData.port2 = port2;
							seditor.commit();
						} catch (Exception e) {
							Toast.makeText(MainActivity.this,
									"参数值输入格式错误或不可为空,请检查后重新输入",
									Toast.LENGTH_SHORT).show();
							iptextview.setText(ip);
							port1textview.setText(port1 + "");
							port2textview.setText(port2 + "");
							GlobalData.mainIP = ip;
							GlobalData.port1 = port1;
							GlobalData.port2 = port2;
						}
					}
				}).create();

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels; // 屏幕高度（像素）
		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）

		v = (ViewGroup) findViewById(R.id.spectrumsanalysis);

		for (int i = 0; i < v.getChildCount(); i++) {
			ViewGroup sonviewGroup = (ViewGroup) v.getChildAt(i);
			for (int m = 0; m < sonviewGroup.getChildCount(); m++) {
				buttoncount = buttoncount + 1;
				final String s = buttoncount + "";
				Button bn = (Button) sonviewGroup.getChildAt(m);
				final String funcname = bn.getText().toString();
				bn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction("function" + s);
						Bundle bundle = new Bundle();
						bundle.putString("from", "FUN");
						bundle.putString("functionname", funcname);
						intent.putExtras(bundle);
						startActivity(intent);
						// MainActivity.this.finish();
					}
				});
			}
		}

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {
					Toast.makeText(MainActivity.this, "连接服务器失败",
							Toast.LENGTH_SHORT).show();
					bar.setTitle("未登录");
					GlobalData.mainTitle = "未登录";
				} else if (msg.what == 2) {
					Toast.makeText(MainActivity.this, "连接服务器成功",
							Toast.LENGTH_SHORT).show();
					bar.setTitle("已登录");
					GlobalData.mainTitle = "已登录";
				}
			}
		};

	}

	@Override
	protected void onDestroy() {
		MainService.stopFunction();
		stopService(serviceIntent);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle("注意");
			builder.setMessage("确定要退出程序吗？");

			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// android.os.Process.killProcess(android.os.Process.myPid());
							MainService.stopFunction();
							stopService(serviceIntent);
							SysApplication.getInstance().exit();
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create();
			builder.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ipset, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.pullipset) {
			dialog.show();
		} else if (item.getItemId() == R.id.getlink) {
			if (GlobalData.toCreatService == false) {
				new Thread() {
					public void run() {
						startService(serviceIntent);
						MainService.startFunction();
						GlobalData.toCreatService = true;
					}
				}.start();
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
