package com.huari.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import struct.JavaStruct;
import struct.StructException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.adapter.ItuAdapterOfListView;
import com.huari.adapter.PagerAdapterOfSpectrum;
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
import com.huari.ui.ShowWaveView;

public class SpectrumsAnalysisActivity extends AppCompatActivity {
	PowerManager pm;
	PowerManager.WakeLock wl;

	boolean cq;// 是否显示场强

	public static int PINPUDATA = 0x10;
	public static int DIANPINGDATA = 0x987;
	public static int ITUDATA = 0x3;
	public static int IQDATA = 0x4;
	public static int AUDIODATA = 0x5;
	public static int PARAMETERREFRESH = 0x6;
	public static int FIRSTAUDIOCOME = 0x9;
	public static int tempLength = 409600;
	
	private final  static String AUDIO_RAW_FILENAME = "RawAudio.raw";

    private static long AUDIO_SAMPLE_RATE = 44100;
    private static int  AUDIO_CHANNL = 2;
    
    private String AudioName = "";        //原始音频数据文件 ，麦克风    
    private String NewAudioName = "";     //可播放的音频文件  
	private static File recordFile ;
    public  static FileOutputStream fos = null;
    
	ShowWaveView waveview;
	com.huari.ui.PartWaveShowView showwave;
	android.support.v4.view.ViewPager viewpager;
	ItuAdapterOfListView listAdapter;
	PagerAdapterOfSpectrum spectrumAdapter;
	ListView itulistview;
	LinearLayout ituLinearLayout;
	ArrayList<View> viewlist;

	public static ArrayList<byte[]> audiolist1, audiolist2;
	public static boolean firstaudio = true;
	public static boolean isRecording = false;
	public static int audioindex = 0;
	public static Object synObject = new Object();
	
	boolean partispause, fullispause = true;
	ArrayList<Parameter> ap;
	float startFreq = 0f, endFreq = 0f, pStepFreq = 0f, centerFreq = 0f,
			daikuan = 0f;
	float halfSpectrumsWide;// 频谱带宽的一半
	String logicId;
	String txname;
	MenuItem mitem;

	static byte[] info;
	public static Handler handle;

	View parentview;
	String[] namesofitems, advanceditems, generalparent, generaletdata;
	//private int generalindex;
	String[][] generalchild;
	TextView normaltextview, advancedtextview, titlebarname, stationtextview,
			devicetextview;

	int offset, displaywidth, barwidth;
	String stationname = null, devicename = null, stationKey = null;
	//static String mydevicename;// 仅在播放声音创建声音播放器时使用。
	ActionBar actionbar;

	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0,
			LinearLayout.LayoutParams.WRAP_CONTENT, 1);
	LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0,
			LinearLayout.LayoutParams.WRAP_CONTENT, 2);
	String tempstationname, tempdevicename;

	Socket socket;// 用来接收数据
	OutputStream outs;
	InputStream ins;
	MyThread mythread;
	IniThread inithread;

	static Parameter centerParameter;
	static Parameter filterSpanParameter;
	static Parameter spectrumParameter;

	boolean showMax, showMin, showAvg;

	// 解析声音相关的东西
	public static AudioTrack at;
	public static int audioBuffersize;
	public static byte[] audioBuffer;
	public static byte[] tempAudioBuffer;
	public static int tempbufferindex = 0;

	//public static PlayAudioThread playAudioThread;

	@SuppressWarnings("deprecation")
	public static void createAudioPlay(int frequency, byte bitcounts,
			short channelcount, int framelength) {

		AUDIO_SAMPLE_RATE = frequency;
		AUDIO_CHANNL = channelcount;
		
		if (bitcounts == 0 && channelcount == 1) {
			audioBuffersize = AudioTrack
					.getMinBufferSize(frequency, AudioFormat.CHANNEL_OUT_MONO,
							AudioFormat.ENCODING_PCM_8BIT);
			audioBuffersize = Math.max(audioBuffersize, framelength);
			
			at = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_8BIT, audioBuffersize*4 ,
					AudioTrack.MODE_STREAM);
			audioBuffer = new byte[audioBuffersize];
			tempAudioBuffer = new byte[tempLength];
		} else if (bitcounts == 1 && channelcount == 1) {
			audioBuffersize = AudioTrack.getMinBufferSize(frequency,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			audioBuffersize = Math.max(audioBuffersize, framelength);
			
			at = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, audioBuffersize,
					AudioTrack.MODE_STREAM);
			audioBuffer = new byte[audioBuffersize];
			tempAudioBuffer = new byte[tempLength];
			at.play();
		} else if (bitcounts == 0 && channelcount == 2) {
			audioBuffersize = AudioTrack.getMinBufferSize(frequency,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_8BIT);
			audioBuffersize = Math.max(audioBuffersize, framelength);

			at = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_8BIT, audioBuffersize,
					AudioTrack.MODE_STREAM);
			audioBuffer = new byte[audioBuffersize];
			tempAudioBuffer = new byte[tempLength];
		} else if (bitcounts == 1 && channelcount == 2) {
			audioBuffersize = AudioTrack.getMinBufferSize(frequency,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT);
			audioBuffersize = Math.max(audioBuffersize, framelength);
			
			at = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, audioBuffersize,
					AudioTrack.MODE_STREAM);
			audioBuffer = new byte[audioBuffersize];
			tempAudioBuffer = new byte[tempLength];
		}
	}

	class IniThread extends Thread {
		public void run() {
			try {
				socket = new Socket(GlobalData.mainIP, GlobalData.port2);
				socket.setSoTimeout(5000);
				ins = socket.getInputStream();
				outs = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendClose() {
		StopTaskFrame st = new StopTaskFrame();
		st.length = 2;
		st.functionNum = 16;
		st.tail = 22;
		byte[] b;
		try {
			b = JavaStruct.pack(st);
			outs.write(b);
			outs.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyThread extends Thread {
		boolean end = false;

		private void setEnd(boolean b) {
			end = b;
		}

		private void sendStartCmd() {
			try {
				byte[] bbb = iRequestInfo();

				GlobalData.clearSpectrums();
				if (filterSpanParameter != null) {
					halfSpectrumsWide = Float
							.parseFloat(filterSpanParameter.defaultValue) / 2000f;
				}
				if (spectrumParameter != null) {
					halfSpectrumsWide = Float
							.parseFloat(spectrumParameter.defaultValue) / 2000f;
				}
				startFreq = (float) (Math.floor(Float
						.parseFloat(centerParameter.defaultValue)
						* 1000f
						- halfSpectrumsWide * 1000)) / 1000;
				endFreq = (Float.parseFloat(centerParameter.defaultValue) * 1000f + halfSpectrumsWide * 1000) / 1000;
				waveview.setF(startFreq, endFreq, pStepFreq);
				outs.write(bbb);
				outs.flush();

			} catch (NullPointerException e) {
				System.out.println("异常");
				sendStartCmd();
			} catch (Exception e) {
				System.out.println("84854959异常");
			}
		}

		private void sendEndCmd() {
			StopTaskFrame st = new StopTaskFrame();
			st.functionNum = 16;
			st.length = 2;
			byte[] b;
			try {
				b = JavaStruct.pack(st);
				outs.write(b);
				outs.flush();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
				int available = 0;
				byte[] info = null;

				while (available == 0 && end == false) {
					available = ins.available();
					if (available > 0) {
						info = new byte[available];
						ins.read(info);
						Parse.newParseSpectrumsAnalysis(info);
						available = 0;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("InvalidWakeLockTag")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spectrums_analysis);
		inithread = new IniThread();
		inithread.start();
		// playAudioThread=new PlayAudioThread();
		// playAudioThread.start();
		// audioBuffer=playAudioThread.getAudioBuffer();
		SysApplication.getInstance().addActivity(this);

		GlobalData.willplay = false;

		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);

		pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");

		ituLinearLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.listviewwithitu, null);
		itulistview = (ListView) ituLinearLayout.findViewById(R.id.itulistview);
		viewlist = new ArrayList<View>();

		spectrumAdapter = new PagerAdapterOfSpectrum(viewlist);
		if (GlobalData.ituHashMap == null) {
			GlobalData.ituHashMap = new HashMap<String, String>();
		}
		listAdapter = new ItuAdapterOfListView(SpectrumsAnalysisActivity.this,
				GlobalData.ituHashMap);
		viewpager = (ViewPager) findViewById(R.id.firstviewpager);
		itulistview.setAdapter(listAdapter);

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
		actionbar = getSupportActionBar();
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		stationname = bundle.getString("stationname");
		devicename = bundle.getString("devicename");
		//mydevicename = devicename;
		stationKey = bundle.getString("stationKey");
		logicId = bundle.getString("lid");

		LinearLayout titlebar = (LinearLayout) getLayoutInflater().inflate(
				R.layout.actionbarview, null);
		stationtextview = (TextView) titlebar.findViewById(R.id.name1);
		devicetextview = (TextView) titlebar.findViewById(R.id.name2);
		// Button bn=(Button)titlebar.findViewById(R.id.zhuanxiang);

		stationtextview.setText(stationname);
		devicetextview.setText(devicename);
		actionbar.setCustomView(titlebar);

		showwave = (com.huari.ui.PartWaveShowView) getLayoutInflater().inflate(
				R.layout.a, null);
		// (com.huari.ui.PartWaveShowView) findViewById(R.id.buildpartwaveshow);
		viewlist.add(showwave);
		viewlist.add(ituLinearLayout);
		viewpager.setAdapter(spectrumAdapter);
		parentview = getLayoutInflater().inflate(
				R.layout.activity_spectrums_analysis, null);

		// 开始设置waveview的相关参数。参数从GlobalData中读取。
		waveview = (ShowWaveView) findViewById(R.id.buildshowwaveview);
		Station stationF = GlobalData.stationHashMap.get(stationKey);
		ArrayList<MyDevice> am = stationF.devicelist;
		HashMap<String, LogicParameter> hsl = null;
		for (MyDevice md : am) {
			if (md.name.equals(devicename)) {
				hsl = md.logic;
			}
		}
		LogicParameter currentLP = hsl.get(logicId);// 获取频谱分析相关的数据，以便画出初始界面
		ap = currentLP.parameterlist;

		for (Parameter p : ap) {
			if (p.name.equals("DemodulationSpan")) {
				daikuan = Float.parseFloat(p.defaultValue);
			} else if (p.name.equals("StepFreq")) {
				pStepFreq = Float.parseFloat(p.defaultValue);
			} else if (p.name.equals("CenterFreq")) {
				centerFreq = Float.parseFloat(p.defaultValue);
				centerParameter = p;
			} else if (p.name.equals("AntennaSelect")) {
				txname = p.defaultValue;
			};
			
			if (p.name.equals("FilterSpan")) {
				halfSpectrumsWide = Float.parseFloat(p.defaultValue) / 2000f;
				filterSpanParameter = p;
			}
			if (p.name.equals("SpectrumSpan")) {
				halfSpectrumsWide = Float.parseFloat(p.defaultValue) / 2000f;
				spectrumParameter = p;
			}
		}
		startFreq = (float) (Math.floor(centerFreq * 1000f - halfSpectrumsWide
				* 1000)) / 1000;
		endFreq = (centerFreq * 1000f + halfSpectrumsWide * 1000) / 1000;
		waveview.setF(startFreq, endFreq, pStepFreq);

		handle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					if (msg.what == DIANPINGDATA && fullispause == false
							&& partispause == false) {
						if (cq == false) {
							showwave.refresh(GlobalData.dianping);
						} else {
							try {
								showwave.refresh(GlobalData.dianping
										+ GlobalData.yinzi[0]);
							} catch (NullPointerException e) {
								showwave.refresh(GlobalData.dianping);
							}
						}
					} else if (msg.what == 0x6)// 参数更新了，可能涉及到坐标的变化，所以需要刷新界面
					{
						if (filterSpanParameter != null) {
							halfSpectrumsWide = Float
									.parseFloat(filterSpanParameter.defaultValue) / 2000f;
						}
						if (spectrumParameter != null) {
							halfSpectrumsWide = Float
									.parseFloat(spectrumParameter.defaultValue) / 2000f;
						}
						startFreq = (float) (Math.floor(Float
								.parseFloat(centerParameter.defaultValue)
								* 1000f - halfSpectrumsWide * 1000)) / 1000;
						endFreq = (Float
								.parseFloat(centerParameter.defaultValue) * 1000f + halfSpectrumsWide * 1000) / 1000;
						waveview.setM(null);
						waveview.setMax(null);
						waveview.setMin(null);
						waveview.setAvg(null);
						waveview.setF(startFreq, endFreq, pStepFreq);
						GlobalData.ituHashMap.clear();
						listAdapter.notifyDataSetChanged();
					} else if (msg.what == 0x10) {
						waveview.setHave(true);
						waveview.setM(GlobalData.Spectrumpinpu);
						if (showMax) {
							waveview.setMax(GlobalData.Spectrummax);
						} else {
							waveview.setMax(null);
						}
						if (showMin) {
							waveview.setMin(GlobalData.Spectrummin);
						} else {
							waveview.setMin(null);
						}
						if (showAvg) {
							waveview.setAvg(GlobalData.Spectrumavg);
						} else {
							waveview.setAvg(null);
						}
						waveview.postInvalidate();
						waveview.setFandC(startFreq, endFreq,
								GlobalData.Spectrumpinpu.length);
					}

					else if (msg.what == AUDIODATA)  //=0x5   声音
					{
						synchronized (synObject) {
							// if(GlobalData.willplay)
							// {
							//at.flush();
							at.write(audioBuffer, 0, audioBuffer.length);
							at.flush();
							at.play();
							at.stop();
							while (at.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
								Thread.sleep(1);
							}
							audioindex = 0;
							// GlobalData.willplay=false;
							// }
						}
					}

					else if (msg.what == ITUDATA) {
						listAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {

				}
			}
		};

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					SpectrumsAnalysisActivity.this);
			builder.setTitle("警告!");
			builder.setMessage("确定要退出该功能吗？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							willExit();
							Intent intent = new Intent(
									SpectrumsAnalysisActivity.this,
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pinpufenxiitem, menu);
		MenuItem mi = (MenuItem) menu.findItem(R.id.zanting1);
		if (mi.getTitle().equals("开始测量")) {
			fullispause = true;
			partispause = true;
		} else {
			fullispause = false;
			partispause = false;
		}
		if (menu.findItem(R.id.ppfxshowmin).getTitle().equals("不显示最小值")) {
			showMin = true;
		} else {
			showMin = false;
		}
		if (menu.findItem(R.id.ppfxshowmax).getTitle().equals("不显示最大值")) {
			showMax = true;
		} else {
			showMax = false;
		}
		if (menu.findItem(R.id.ppfxshowavg).getTitle().equals("不显示平均值")) {
			showAvg = true;
		} else {
			showAvg = false;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final MenuItem i = item;
		if (item.getItemId() == R.id.zanting1) {
			mitem = item;
			if (item.getTitle().equals("停止测量")) {
				partispause = true;
				fullispause = true;
				mythread.sendEndCmd();
				GlobalData.Spectrumpinpu = null;
				GlobalData.oldcount = 0;
				GlobalData.haveCount = 0;
				item.setTitle("开始测量");
				
				if (isRecording)
				{
				  Menu menu = null;
				  getMenuInflater().inflate(R.menu.pinpufenxiitem, menu);
				  MenuItem itm;
				  itm = menu.findItem(R.id.recorder);
				  itm.setTitle("录音");
				  isRecording = false;
			      try {
			            if(fos != null)
			                fos.close();// 关闭写入流  
			      } catch (IOException e) {  
			            e.printStackTrace();  
			      } 
			      
			      copyWaveFile(AudioName, NewAudioName);//给裸数据加上头文件  
				}
				
			} else {
				partispause = false;
				fullispause = false;
				item.setTitle("停止测量");
				if (mythread == null) {
					mythread = new MyThread();
				}
				mythread.sendStartCmd();
				try {
					mythread.start();
				} catch (Exception e) {

				}
			}
		} else if (item.getItemId() == R.id.custompinpuset) {
			if (fullispause == true) {
				Intent intent = new Intent(SpectrumsAnalysisActivity.this,
						PPFXsetActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("sname", stationname);
				bundle.putString("dname", devicename);
				bundle.putString("stakey", stationKey);
				bundle.putString("lids", logicId);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
			} else {
				AlertDialog.Builder ab = new AlertDialog.Builder(
						SpectrumsAnalysisActivity.this);
				ab.setTitle("警告！");
				ab.setMessage("功能运行期间不可更改设置，确定要停止功能进行设置吗？");
				ab.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								partispause = true;
								fullispause = true;
								findViewById(R.id.zanting1);
								mitem.setTitle("开始测量");

								mythread.sendEndCmd();

								if (mythread != null) {
									try {
										mythread.setEnd(true);
									} catch (Exception e) {

									}
								}
								mythread = null;
								if (inithread != null) {
									try {
										inithread.destroy();
									} catch (Exception e) {

									}
									inithread = null;
								}
								GlobalData.Spectrumpinpu = null;
								GlobalData.oldcount = 0;
								GlobalData.haveCount = 0;
								System.gc();

								Intent intent = new Intent(
										SpectrumsAnalysisActivity.this,
										PPFXsetActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("sname", stationname);
								bundle.putString("dname", devicename);
								bundle.putString("stakey", stationKey);
								bundle.putString("lids", logicId);
								intent.putExtras(bundle);
								startActivityForResult(intent, 0);
							}
						});
				ab.setNegativeButton("取消", null);
				ab.create();
				ab.show();
			}

		} else if (item.getItemId() == R.id.changqiang) {
			if (item.getTitle().equals("场强")) {
				if (partispause) {
					cq = true;
					item.setTitle("电平");
					showwave.setDanwei("场强dBuV/m");
				} else {
					Toast.makeText(SpectrumsAnalysisActivity.this,
							"任务运行期间，不可切换", Toast.LENGTH_SHORT).show();
				}
			} else if (item.getTitle().equals("电平")) {
				if (partispause) {
					cq = false;
					item.setTitle("场强");
					showwave.setDanwei("电平dBuV");
				} else {
					Toast.makeText(SpectrumsAnalysisActivity.this,
							"任务运行期间，不可切换", Toast.LENGTH_SHORT).show();
				}
			}
		} else if (item.getItemId() == R.id.ppfxshowmax) {
			if (item.getTitle().equals("不显示最大值")) {
				item.setTitle("显示最大值");
				showMax = false;
			} else if (item.getTitle().equals("显示最大值")) {
				item.setTitle("不显示最大值");
				showMax = true;
			}
		} else if (item.getItemId() == R.id.ppfxshowmin) {
			if (item.getTitle().equals("不显示最小值")) {
				item.setTitle("显示最小值");
				showMin = false;
			} else if (item.getTitle().equals("显示最小值")) {
				item.setTitle("不显示最小值");
				showMin = true;
			}
		} else if (item.getItemId() == R.id.ppfxshowavg) {
			if (item.getTitle().equals("不显示平均值")) {
				item.setTitle("显示平均值");
				showAvg = false;
			} else if (item.getTitle().equals("显示平均值")) {
				item.setTitle("不显示平均值");
				showAvg = true;
			}
		} else if (item.getItemId() == R.id.capture) {
			if (item.getTitle().equals("截图")) {
				
			}
			
		} else if (item.getItemId() == R.id.recorder) {
			if (item.getTitle().equals("录音")) {
			    item.setTitle("停止录音");

		        AudioName = getRawFilePath();
		        NewAudioName = getWavFilePath(); 
		        
		        try {  
		        	recordFile = new File(AudioName);  
		            if (recordFile.exists()) {  
		            	recordFile.delete();  
		            }  
		            fos = new FileOutputStream(recordFile);// 建立一个可存取字节的文件  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		        
			    isRecording = true;
			}
			else
			{
				item.setTitle("录音");
				if (isRecording)
				{
				  isRecording = false;
			      try {
			            if(fos != null)
			                fos.close();   // 关闭写入流  
			      } catch (IOException e) {  
			            e.printStackTrace();  
			      } 
			      
			      copyWaveFile(AudioName, NewAudioName);   //给裸数据加上头文件  
				}
			}
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == 1) {
			Bundle bundle = data.getExtras();
			stationtextview.setText(bundle.getString("stname"));
			devicetextview.setText(bundle.getString("dename"));
		}
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
		pr.functionNum = 16;
		pr.stationid = MyTools.toCountString(stationKey, 76).getBytes();
		pr.logicid = MyTools.toCountString(logicId, 76).getBytes();
		pr.devicename = MyTools.toCountString(devicename, 36).getBytes();
		pr.pinduancount = 0;

		pr.logictype = MyTools.toCountString("level", 16).getBytes();
		PinPuParameter[] parray = null;
		if (havetianxian) {
			parray = new PinPuParameter[ap.size() - 1];
		} else {
			parray = new PinPuParameter[ap.size()];
		}

		int z = 0;
		for (Parameter para : ap) {
			PinPuParameter pin = new PinPuParameter();
			pr.tianxianname = MyTools.toCountString("NULL", 36).getBytes();
			if (!(para.name.contains("Anten"))) {
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

	private int[] stringtoarray(String k, String b) {
		String[] v = k.split(b);
		int[] x = new int[v.length];
		for (int z = 0; z < v.length; z++) {
			x[z] = Integer.parseInt(v[z]);
		}
		return x;
	}

	private void willExit() {
		try {
			GlobalData.isFirstAudio = true;
			GlobalData.willplay = false;
			sendClose();
			Thread.sleep(50);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.getInputStream().close();
				socket.getOutputStream().close();
				socket.close();
				socket.setSoTimeout(5000);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (mythread != null) {
			try {
				mythread.setEnd(true);
				mythread.join();
				mythread = null;
			} catch (Exception e) {

			}
		}
		mythread = null;
		if (inithread != null) {
			try {
				inithread.destroy();
			} catch (Exception e) {

			}
			inithread = null;
		}
//		if (playAudioThread != null) {
//			try {
//				playAudioThread.setRunPlayAudio(false);
//				playAudioThread.join();
//				playAudioThread = null;
//			} catch (Exception e) {
//
//			}
//		}
		try {
			GlobalData.Spectrumpinpu = null;
			GlobalData.oldcount = 0;
			GlobalData.haveCount = 0;
			releaseAudioResource();
			System.gc();
		} catch (Exception e) {

		}
	}

	@Override
	protected void onDestroy() {
		willExit();
		super.onDestroy();
	}

	private void releaseAudioResource() {
		 at.stop();
		 at.release();
	}

	@Override
	protected void onResume() {
		wl.acquire();
		super.onResume();
	}

	@Override
	protected void onPause() {
		wl.release();
		super.onPause();
	}

    /**
     * 判断是否有外部存储设备sdcard
     * @return true | false
     */
    public static boolean isSdcardExit(){       
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    
    /**
     * 获取麦克风输入的原始音频流文件路径
     * @return
     */
    public static String getRawFilePath(){
        String mAudioRawPath = "";
        if(isSdcardExit()){
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioRawPath = fileBasePath + "/" + AUDIO_RAW_FILENAME;
        }   
         
        return mAudioRawPath;
    }
    
    public static String getWavFilePath(){
        String mAudioWavPath = "";
	    Time t = new Time("GMT+8");
	    t.setToNow();
	    String tt = t.toString();
        if (isSdcardExit()) {
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioWavPath = fileBasePath + "/rec_" + centerParameter.defaultValue + tt +".wav";;
        }
        return mAudioWavPath;
    }
    
    /** 
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。 
     * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav 
     * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有 
     * 自己特有的头文件。 
     */ 
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,  
            long totalDataLen, long longSampleRate, int channels, long byteRate)  
            throws IOException {  
        byte[] header = new byte[44];  
        header[0] = 'R'; // RIFF/WAVE header  
        header[1] = 'I';  
        header[2] = 'F';  
        header[3] = 'F';  
        header[4] = (byte) (totalDataLen & 0xff);  
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);  
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);  
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);  
        header[8] = 'W';  
        header[9] = 'A';  
        header[10] = 'V';  
        header[11] = 'E';  
        header[12] = 'f'; // 'fmt ' chunk  
        header[13] = 'm';  
        header[14] = 't';  
        header[15] = ' ';  
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk  
        header[17] = 0;  
        header[18] = 0;  
        header[19] = 0;  
        header[20] = 1; // format = 1  
        header[21] = 0;  
        header[22] = (byte) channels;  
        header[23] = 0;  
        header[24] = (byte) (longSampleRate & 0xff);  
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);  
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);  
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);  
        header[28] = (byte) (byteRate & 0xff);  
        header[29] = (byte) ((byteRate >> 8) & 0xff);  
        header[30] = (byte) ((byteRate >> 16) & 0xff);  
        header[31] = (byte) ((byteRate >> 24) & 0xff);  
        header[32] = (byte) (2 * 16 / 8); // block align  
        header[33] = 0;  
        header[34] = 16; // bits per sample  
        header[35] = 0;  
        header[36] = 'd';  
        header[37] = 'a';  
        header[38] = 't';  
        header[39] = 'a';  
        header[40] = (byte) (totalAudioLen & 0xff);  
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);  
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);  
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);  
        out.write(header, 0, 44);  
    }      

    // 这里得到可播放的音频文件  
    private void copyWaveFile(String inFilename, String outFilename) {  
        FileInputStream in = null;  
        FileOutputStream out = null;  
        long totalAudioLen = 0;  
        long totalDataLen = totalAudioLen + 36;  
        long longSampleRate = AUDIO_SAMPLE_RATE;  
        int channels = AUDIO_CHANNL;
        long byteRate = 16 * AUDIO_SAMPLE_RATE * channels / 8;  
        byte[] data = new byte[audioBuffersize];  
        
        try {  
            in = new FileInputStream(inFilename);  
            out = new FileOutputStream(outFilename);  
            totalAudioLen = in.getChannel().size();  
            totalDataLen = totalAudioLen + 36;  
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,  
                    longSampleRate, channels, byteRate);  
            while (in.read(data) != -1) {  
                out.write(data);  
            }  
            in.close();  
            out.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }      
}
