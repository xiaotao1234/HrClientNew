package com.huari.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huari.dataentry.GlobalData;
import com.huari.service.MainService;
import com.zhy.view.CircleMenuLayout;
import com.zhy.view.CircleMenuLayout.OnMenuItemClickListener;

/**
 * <pre>
 * @author yz
 * http://blog.csdn.net/forestagan/article/details/43131133
 * </pre>
 */
public class CircleActivity extends Activity // ActionBarActivity
{
	private CircleMenuLayout mCircleMenuLayout;

	private String[] mItemTexts = new String[] { "换算工具", "频谱分析","单频测向",
			"频段扫描", "地图显示","无人站", "监测站列表" };
	private int[] mItemImgs = new int[] { R.drawable.home_mbank_3_normal,
			R.drawable.fenxi, R.drawable.cexiang,R.drawable.ganrao,
			R.drawable.ditu,  R.drawable.wurenzhan, R.drawable.liebiao };

	// ViewGroup v;
	LinearLayout ipsetLayout;
	Button ipcancle, ipsave;
	EditText iptextview, port1textview, port2textview;

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

		setContentView(R.layout.activity_main02);

		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
		setTitle(GlobalData.mainTitle);

		mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
		mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);

		mCircleMenuLayout
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void itemClick(View view, int pos) {
//						Toast.makeText(CircleActivity.this, mItemTexts[pos],
//								Toast.LENGTH_SHORT).show();
						
						if (pos == 0){
						
							
						}
						else{
							if (GlobalData.mainTitle !="已登录")
							{
								Toast.makeText(CircleActivity.this, "请先登录服务器，才能操作这个功能...", Toast.LENGTH_LONG).show();
								return;
							}

							final String s = pos + "";
							
							Intent intent = new Intent();
							intent.setAction("function" + s);
							Bundle bundle = new Bundle();
							bundle.putString("from", "FUN");
							bundle.putString("functionname", mItemTexts[pos]);
							intent.putExtras(bundle);
							//startActivity(intent);
							startActivityForResult(intent, 0);
						}
					}

					@Override
					public void itemCenterClick(View view) {
//						Toast.makeText(CircleActivity.this,
//								"成都华日通讯技术有限公司竭诚为您服务...", Toast.LENGTH_SHORT)
//								.show();
//						startActivity(new Intent(CircleActivity.this,
//								BrowerActivity.class));
						
						if (GlobalData.mainTitle == "已登录")
							return;
						
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

						dialog = new AlertDialog.Builder(CircleActivity.this)
								.setTitle("登录服务器")
								.setView(ipsetLayout)
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface arg0,
													int arg1) {
												// TODO Auto-generated method stub
												iptextview.setText(ip);
												port1textview.setText(port1 + "");
												port2textview.setText(port2 + "");
												GlobalData.mainIP = ip;
												GlobalData.port1 = port1;
												GlobalData.port2 = port2;
											}
										})
								.setPositiveButton("登录",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface arg0,
													int arg1) {
												// TODO Auto-generated method stub
												try {
													ip = iptextview.getText().toString();
													port1 = Integer.parseInt(port1textview
															.getText().toString());
													port2 = Integer.parseInt(port2textview
															.getText().toString());
													seditor.putInt("port1", port1);
													seditor.putInt("port2", port2);
													seditor.putString("ip", ip);
													GlobalData.mainIP = ip;
													GlobalData.port1 = port1;
													GlobalData.port2 = port2;
													seditor.commit();
													
													if (GlobalData.toCreatService == false) {
														new Thread() {
															public void run() {
																startService(serviceIntent);
																MainService.startFunction();
																GlobalData.toCreatService = true;
															}
														}.start();
													}
														
												} catch (Exception e) {
													Toast.makeText(CircleActivity.this,
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

						dialog.show();


					}
				});

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == LINKFAILED) {
					Toast.makeText(CircleActivity.this, "连接服务器失败",
							Toast.LENGTH_SHORT).show();
					setTitle("未登录");
					GlobalData.mainTitle = "未登录";
				} else if (msg.what == LINKSUCCESS) {
					Toast.makeText(CircleActivity.this, "连接服务器成功",
							Toast.LENGTH_SHORT).show();
					setTitle("已登录");
					GlobalData.mainTitle = "已登录";
				}
			};
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
					CircleActivity.this);
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
	
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.ipset, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		if (item.getItemId() == R.id.pullipset) {
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

			dialog = new AlertDialog.Builder(CircleActivity.this)
					.setTitle("服务器登录设置")
					.setView(ipsetLayout)
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									iptextview.setText(ip);
									port1textview.setText(port1 + "");
									port2textview.setText(port2 + "");
									GlobalData.mainIP = ip;
									GlobalData.port1 = port1;
									GlobalData.port2 = port2;

								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									try {
										ip = iptextview.getText().toString();
										port1 = Integer.parseInt(port1textview
												.getText().toString());
										port2 = Integer.parseInt(port2textview
												.getText().toString());
										seditor.putInt("port1", port1);
										seditor.putInt("port2", port2);
										seditor.putString("ip", ip);
										GlobalData.mainIP = ip;
										GlobalData.port1 = port1;
										GlobalData.port2 = port2;
										seditor.commit();
									} catch (Exception e) {
										Toast.makeText(CircleActivity.this,
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
*/
}
