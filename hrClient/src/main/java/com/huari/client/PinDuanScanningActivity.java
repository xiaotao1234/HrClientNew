package com.huari.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import struct.JavaStruct;
import struct.StructException;

import com.huari.commandstruct.PPFXRequest;
import com.huari.commandstruct.PinPuParameter;
import com.huari.commandstruct.StopTaskFrame;
import com.huari.dataentry.GlobalData;
import com.huari.dataentry.LogicParameter;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Parameter;
import com.huari.dataentry.Station;
import com.huari.tools.MyTools;

import com.huari.tools.Parse;
import com.huari.tools.SysApplication;

import android.app.Activity;
import androidx.appcompat.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//频段扫描功能窗体
public class PinDuanScanningActivity extends Activity {

	public static int SCANNINGDATA = 0x2;
	public static int SCANNINGDATAFSCAN = 0x4;
	public static int SCANNINGDATAFSCANNO = 0x5;
	public static int YINZIDATA;
	public static int PARAMETERREFRESH = 0x7;
	public static int PARSEDATA = 0x9;
	public static int SCANNINGDATANO = 0x3;
	public static String ScanType;

	com.huari.ui.PinDuan pinduan;
	boolean pause;
	boolean showMax, showMin, showAvg;

	String logicId, stationname, stationKey, devicename;
	ActionBar actionbar;
	TextView normaltextview, advancedtextview, titlebarname, stationtextview,
			devicetextview;
	ArrayList<Parameter> ap;
	Parameter startFreqParameter;
	Parameter endFreqParameter;
	Parameter stepFreqParameter;
	float startFreq = 0f, endFreq = 0, pStepFreq = 0f, centerFreq = 0f,
			daikuan = 0f;
	String txname;		// 天线名字
	public static Handler handler;

	MenuItem startitem;	// 开始测量/停止测量

	Socket s;			// 用来接收数据
	OutputStream outs;
	InputStream ins;
	MyThread mythread;
	boolean runMythread = true;
	IniThread inithread;
	ParseDataThread pdthread;

	boolean goRefresh = true;

	private static String fd = "fd";	// 是幅度模式还是加上因子的场强模式cq

	public static String getDwName() {
		return fd;
	}

	class IniThread extends Thread {
		public void run() {
			try {
				s = new Socket(GlobalData.mainIP, GlobalData.port2);
				ins = s.getInputStream();
				outs = s.getOutputStream();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ParseDataThread extends Thread {
		public Handler handler;

		@Override
		public void run() {
			Looper.prepare();
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x9) {
						Parse.newParsePDScan(GlobalData.pCacheQueue.poll());
					}
				};
			};
			Looper.loop();
		}
	}

	private void sendClose() {
		StopTaskFrame stf = new StopTaskFrame();
		stf.length = 2;
		stf.functionNum = 17;  //请求频段扫描
		stf.tail = 22;
		
		try {
			byte[] stop = JavaStruct.pack(stf);
			outs.write(stop);
			outs.flush();
			Log.i("发送", "停止命令");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyThread extends Thread {
		private void sendStartCmd() {
			GlobalData.clearPinDuan();
			pinduan.clear();
			pinduan.setParameters(
					Float.parseFloat(startFreqParameter.defaultValue.trim()),
					Float.parseFloat(endFreqParameter.defaultValue.trim()),
					Float.parseFloat(stepFreqParameter.defaultValue));
			try {
				byte[] bbb = iRequestInfo();
				System.out.println("客户端发送的数据长度是" + bbb.length);
				outs.write(bbb);
				outs.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		private void sendEndCmd() {
			StopTaskFrame stf = new StopTaskFrame();
			stf.length = 2;
			stf.functionNum = 17;
			try {
				byte[] stop = JavaStruct.pack(stf);
				outs.write(stop);
				outs.flush();
				Log.i("发送", "停止命令");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				int available = 0;
				byte[] info = null;

				while (available == 0 && runMythread) {
					available = ins.available();
					if (available > 0) {
						info = new byte[available];
						ins.read(info);
						try {
							// GlobalData.pCacheQueue.add(info);
							// pdthread.handler.sendEmptyMessage(PARSEDATA);
							Parse.newParsePDScan(info);
						} catch (Exception e) {
							// System.out.println("解析频段扫描数据发生异常");
						}
						available = 0;
						try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pinduansanmiao);
		inithread = new IniThread();
		inithread.start();

		SysApplication.getInstance().addActivity(this);
		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
		pause = true;
		pinduan = (com.huari.ui.PinDuan) findViewById(R.id.mypin);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int twidth = metric.widthPixels;
		int theight = metric.heightPixels;
		float density = metric.density;
		int densityDpi = metric.densityDpi;
		double dui = Math.sqrt(twidth * twidth + theight * theight);
		double x = dui / densityDpi;
		if (x >= 6.5) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
		}
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		stationname = bundle.getString("stationname");
		devicename = bundle.getString("devicename");
		stationKey = bundle.getString("stationKey");
		logicId = bundle.getString("lid");


		
		LinearLayout titlebar = (LinearLayout) getLayoutInflater().inflate(
				R.layout.actionbarview, null);
		stationtextview = (TextView) titlebar.findViewById(R.id.name1);
		devicetextview = (TextView) titlebar.findViewById(R.id.name2);

		stationtextview.setText(stationname);
		devicetextview.setText(devicename);
		actionbar.setCustomView(titlebar);
		ArrayList<MyDevice> am = null;
		try {
			Station stationF = GlobalData.stationHashMap.get(stationKey);
			am = stationF.devicelist;
		} catch (Exception e) {
			Toast.makeText(PinDuanScanningActivity.this, "空的",
					Toast.LENGTH_SHORT).show();
		}

		HashMap<String, LogicParameter> hsl = null;
		for (MyDevice md : am) {
			if (md.name.equals(devicename)) {
				hsl = md.logic;
			}
		}
		LogicParameter currentLP = hsl.get(logicId);// 获取频段扫描参数相关的数据，以便画出初始界面
		ap = currentLP.parameterlist;
		for (Parameter p : ap) {
			if (p.name.trim().equals("StartFreq")) {
				startFreqParameter = p;
				startFreq = Float.parseFloat(p.defaultValue.trim());
			} else if (p.name.trim().equals("StopFreq")) {
				endFreqParameter = p;
				endFreq = Float.parseFloat(p.defaultValue.trim());
			} else if (p.name.trim().equals("StepFreq")) {
				stepFreqParameter = p;
				pStepFreq = Float.parseFloat(p.defaultValue.trim()) / 1000f;
			} else if (p.name.trim().equals("ScanType")) {
				ScanType = p.defaultValue.trim();
			}
		}

		// startFreq=(centerFreq*1000f-daikuan/2)/1000;
		// endFreq=(centerFreq*1000f+daikuan/2)/1000;

		pinduan.setParameters(startFreq, endFreq, pStepFreq * 1000);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == PARAMETERREFRESH) {
					for (Parameter p : ap) {
						if (p.name.equals("DemodulationSpan")) {
							daikuan = Float.parseFloat(p.defaultValue);
						} else if (p.name.equals("StepFreq")) {
							pStepFreq = Float.parseFloat(p.defaultValue);
						} else if (p.name.equals("CenterFreq")) {
							centerFreq = Float.parseFloat(p.defaultValue);
						} else if (p.name.equals("AntennaSelect")) {
							txname = p.defaultValue;
						}
					}
					pinduan.clear();
					pinduan.setParameters(
							Float.parseFloat(startFreqParameter.defaultValue
									.trim()), Float
									.parseFloat(endFreqParameter.defaultValue
											.trim()), Float
									.parseFloat(stepFreqParameter.defaultValue));

					// pinduan.setParameters(startFreq, endFreq,pStepFreq);
				} else if (msg.what == SCANNINGDATA)// PSCAN模式，且点数未超过上限（即画出Max、Min、Avg）
				{
					if (pause == false) {
						try {
							int a = GlobalData.pinduanQueue.size();
							if (a > 5) {
								GlobalData.pinduanQueue.poll();
								GlobalData.pinduanQueue.poll();
							}
							short[] t = GlobalData.pinduanQueue.poll();
							//
							// for(int i=0;i<t.length;i++)
							// {
							// t[i]=(short) (t[i]+GlobalData.PDyinzi[i]);
							// }
							pinduan.setM(t);
							if (t == null) {
								System.out.println("获取到的short【】是空的");
							}
							if (showMax) {
								pinduan.setMax(GlobalData.pinduanMax);
							} else {
								pinduan.setMax(null);
							}
							if (showMin) {
								pinduan.setMin(GlobalData.pinduanMin);
							} else {
								pinduan.setMin(null);
							}
							if (showAvg) {
								pinduan.setAvg(GlobalData.pinduanAvg);
							} else {
								pinduan.setAvg(null);
							}
							// pinduan.setHave(true);
							pinduan.refreshWave();
							pinduan.refreshTable();
						} catch (Exception e) {

						}

					}
				} else if (msg.what == SCANNINGDATANO)// PSCAN模式，且点数超过上限（即不画出Max、Min、Avg）。
				{
					if (pause == false) {
						try {
							short[] t = null;
							synchronized (GlobalData.pinduanQueue) {
								int a = GlobalData.pinduanQueue.size();
								if (a > 5) {
									GlobalData.pinduanQueue.poll();
									GlobalData.pinduanQueue.poll();
								}
								t = GlobalData.pinduanQueue.poll();
							}

							// for(int i=0;i<t.length;i++)
							// {
							// t[i]=(short) (t[i]+GlobalData.PDyinzi[i]);
							// }
							pinduan.setM(t);
							if (t == null) {
								System.out.println("获取到的short【】是空的");
							}
							pinduan.setMax(null);
							pinduan.setMin(null);
							pinduan.setAvg(null);
							// pinduan.setHave(true);
							pinduan.refreshWave();
							pinduan.refreshTable();
						} catch (Exception e) {

						}

					}
				} else if (msg.what == SCANNINGDATAFSCAN)// 非PSCAN模式，且点数未超上限（即可以画出Max、Min、Avg)
				{
					if (pause == false) {
						// for(int i=0;i<GlobalData.pinduanScan.length;i++)
						// {
						// GlobalData.pinduanScan[i]=(short)
						// (GlobalData.pinduanScan[i]+GlobalData.PDyinzi[i]);
						// }

						pinduan.setM(GlobalData.pinduanScan);

						if (showMax) {
							pinduan.setMax(GlobalData.pinduanMax);
						} else {
							pinduan.setMax(null);
						}
						if (showMin) {
							pinduan.setMin(GlobalData.pinduanMin);
						} else {
							pinduan.setMin(null);
						}
						if (showAvg) {
							pinduan.setAvg(GlobalData.pinduanAvg);
						} else {
							pinduan.setAvg(null);
						}
						// pinduan.setHave(true);
						pinduan.refreshWave();
						pinduan.refreshTable();
					}
				} else if (msg.what == SCANNINGDATAFSCANNO)// 非PSCAN模式，且点数超上限（即不可以画出Max、Min、Avg)
				{
					if (pause == false) {
						synchronized (GlobalData.a) {
							// for(int i=0;i<GlobalData.pinduanScan.length;i++)
							// {
							// GlobalData.pinduanScan[i]=(short)
							// (GlobalData.pinduanScan[i]+GlobalData.PDyinzi[i]);
							// }

							pinduan.setM(GlobalData.pinduanScan);
							pinduan.setMax(null);
							pinduan.setMin(null);
							pinduan.setAvg(null);
							// pinduan.setHave(true);
							pinduan.refreshWave();
							pinduan.refreshTable();
						}

					}
				}
			}
		};
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 0 && arg1 == 2) {
			Bundle bundle = arg2.getExtras();
			stationtextview.setText(bundle.getString("stname"));
			devicetextview.setText(bundle.getString("dename"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pin_duan_scanning, menu);
		if (menu.findItem(R.id.maxshow).getTitle().equals("不显示最大值")) {
			showMax = true;
		} else {
			showMax = false;
		}
		if (menu.findItem(R.id.minshow).getTitle().equals("不显示最小值")) {
			showMin = true;
		} else {
			showMin = false;
		}
		if (menu.findItem(R.id.avgshow).getTitle().equals("不显示平均值")) {
			showAvg = true;
		} else {
			showAvg = false;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		final MenuItem i = item;
		if (id == R.id.pinduanstart) {
			startitem = item;
			if (item.getTitle().equals("开始测量")) {
				item.setTitle("停止测量");
				// pinduan.setParameters(Float.parseFloat(p.defaultValue.trim()),Float.parseFloat(p.defaultValue.trim()),
				// Float.parseFloat(p.defaultValue));
				pause = false;
				if (mythread == null) {
					mythread = new MyThread();
				}
				try {
					mythread.sendStartCmd();
					// if(fd.equals("fd"))
					// {
					// pinduan.setShowInfo("电平(dBuv)");
					// }
					// else if(fd.equals("cq"))
					// {
					// pinduan.setShowInfo("场强(dBuV/m)");
					// }

				} catch (Exception e) {

				}
				try {
					mythread.start();
				} catch (Exception e) {

				}
				if (pdthread == null) {
					pdthread = new ParseDataThread();
				}
				try {
					pdthread.start();
				} catch (Exception e) {

				}
			} else if (item.getTitle().equals("停止测量")) {
				item.setTitle("开始测量");
				pause = true;
				mythread.sendEndCmd();
				GlobalData.clearPinDuan();
			}
		}
		else if (id == R.id.tiaozheng) {
			if (item.getTitle().equals("调频调幅")) {
				item.setTitle("调整限值");
				pinduan.setTiaoZhi(false);
			} else if (item.getTitle().equals("调整限值")) {
				item.setTitle("调频调幅");
				pinduan.setTiaoZhi(true);
			}
		} else if (id == R.id.showtable) {

			if (item.getTitle().equals("隐藏表格")) {
				pinduan.hideTable(true);
				item.setTitle("展示表格");
			} else if (item.getTitle().equals("展示表格")) {
				pinduan.hideTable(false);
				item.setTitle("隐藏表格");
			}
		} else if (id == R.id.pinduanset) {
			if (pause) {
				Intent intent = new Intent(PinDuanScanningActivity.this,
						PinDuanSetActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("sname", stationname);
				bundle.putString("dname", devicename);
				bundle.putString("stakey", stationKey);
				bundle.putString("lids", logicId);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				AlertDialog.Builder ab = new AlertDialog.Builder(
						PinDuanScanningActivity.this);
				ab.setTitle("警告！");
				ab.setMessage("功能运行期间不可更改设置，确定要停止功能进行设置吗？");
				ab.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								pause = true;
								mythread.sendEndCmd();
								GlobalData.clearPinDuan();
								startitem.setTitle("开始测量");
								Intent intent = new Intent(
										PinDuanScanningActivity.this,
										PinDuanSetActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("sname", stationname);
								bundle.putString("dname", devicename);
								bundle.putString("stakey", stationKey);
								bundle.putString("lids", logicId);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						});
				ab.setNegativeButton("取消", null);
				ab.create();
				ab.show();
			}
		}
		// else if(id==R.id.pscq)
		// {
		// if(pause==false)
		// {
		// Toast.makeText(PinDuanScanningActivity.this, "任务运行期间不可切换",
		// Toast.LENGTH_SHORT).show();
		// }
		// else
		// {
		// if(item.getTitle().equals("场强"))
		// {
		// item.setTitle("电平");
		// fd="cq";
		// GlobalData.clearPinDuan();
		// pinduan.setDanWei("场强","dBuv/m");
		//
		// }
		// else if(item.getTitle().equals("电平"))
		// {
		// item.setTitle("场强");
		// fd="fd";
		// GlobalData.clearPinDuan();
		// pinduan.setDanWei("电平","dBuv");
		// }
		// }
		// }
		else if (id == R.id.maxshow) {
			if (item.getTitle().equals("不显示最大值")) {
				item.setTitle("显示最大值");
				showMax = false;
			} else if (item.getTitle().equals("显示最大值")) {
				item.setTitle("不显示最大值");
				showMax = true;
			}
		} else if (id == R.id.minshow) {
			if (item.getTitle().equals("不显示最小值")) {
				item.setTitle("显示最小值");
				showMin = false;
			} else if (item.getTitle().equals("显示最小值")) {
				item.setTitle("不显示最小值");
				showMin = true;
			}
		} else if (id == R.id.avgshow) {
			if (item.getTitle().equals("不显示平均值")) {
				item.setTitle("显示平均值");
				showAvg = false;
			} else if (item.getTitle().equals("显示平均值")) {
				item.setTitle("不显示平均值");
				showAvg = true;
			}
		} 
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					PinDuanScanningActivity.this);
			builder.setTitle("警告!");
			builder.setMessage("确定要退出该功能吗？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							willExit();
							Intent intent = new Intent(
									PinDuanScanningActivity.this,
									MainActivity.class);//CircleActivity.class);
							startActivity(intent);

							finish();
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create();
			builder.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	private byte[] iRequestInfo() {
		byte[] request = null;
		boolean havetianxian = false;
		PPFXRequest pr = new PPFXRequest();
		for (Parameter para : ap) {
			if ((para.name.contains("Anten"))) {
				havetianxian = true;
				break;
			}
		}
		// 帧体长度暂时跳过
		pr.functionNum = 17;
		pr.stationid = MyTools.toCountString(stationKey, 76).getBytes();
		pr.logicid = MyTools.toCountString(logicId, 76).getBytes();
		pr.devicename = MyTools.toCountString(devicename, 36).getBytes();
		pr.pinduancount = 0;

		pr.logictype = MyTools.toCountString("SCAN", 16).getBytes();
		PinPuParameter[] parray = null;
		if (havetianxian) {
			parray = new PinPuParameter[ap.size() - 1];
		} else {
			parray = new PinPuParameter[ap.size()];
		}
		int z = 0;
		
		pr.tianxianname = MyTools.toCountString("NULL", 36).getBytes();
		for (Parameter para : ap) {
			PinPuParameter pin = new PinPuParameter();
			if (!(para.name.contains("AntennaSelect"))) {
				pin.name = MyTools.toCountString(para.name, 36).getBytes();
				pin.value = MyTools.toCountString(para.defaultValue, 36)
						.getBytes();
				parray[z] = pin;
				z++;
			} else {
				pr.tianxianname = MyTools.toCountString(para.defaultValue, 36)
						.getBytes();
			}

		}
		if (havetianxian) {
			pr.parameterslength = 72 * (ap.size() - 1);
		} else {
			pr.parameterslength = 72 * ap.size();
		}

		pr.length = pr.parameterslength + 247;
		pr.p = parray;
		try {
			request = JavaStruct.pack(pr);
		} catch (StructException e) {
			e.printStackTrace();
		}
		return request;
	}

	private void willExit() {
		try {
			sendClose();
			Thread.sleep(50);
			s.close();
			if (mythread != null) {
				runMythread = false;
				mythread.join();
				mythread = null;
			}
			if (inithread != null) {
				inithread.destroy();
				inithread = null;
			}
			GlobalData.clearPinDuan();

		} catch (Exception e) {
			System.out.println("异常");
		}
	}

	@Override
	protected void onDestroy() {
		willExit();
		super.onDestroy();
	}

}
