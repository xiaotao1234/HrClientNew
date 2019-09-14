package com.huari.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import struct.JavaStruct;
import struct.StructException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.Base.AnalysisBase;
import com.huari.NetMonitor.WindowController;
import com.huari.NetMonitor.WindowHelper;
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
import com.huari.dataentry.Type;
import com.huari.tools.ByteFileIoUtils;
import com.huari.tools.FileOsImpl;
import com.huari.tools.MyTools;
import com.huari.tools.Parse;
import com.huari.tools.RealTimeSaveAndGetStore;
import com.huari.tools.SysApplication;
import com.huari.ui.ShowWaveView;

import org.greenrobot.eventbus.EventBus;

public class SpectrumsAnalysisActivity extends AnalysisBase {
	PowerManager pm;
	PowerManager.WakeLock wl;

	boolean cq;// 是否显示场强

	public static int IQDATA = 0x4;
	public static int AUDIODATA = 0x5;
	public static int PARAMETERREFRESH = 0x6;
	public static int FIRSTAUDIOCOME = 0x9;
	public static int tempLength = 409600;
	public static Queue<byte[]> queue;
	public static boolean saveFlag = false;
	float lan,lon;
	
	private final  static String AUDIO_RAW_FILENAME = "RawAudio.raw";

    private static long AUDIO_SAMPLE_RATE = 44100;
    private static int  AUDIO_CHANNL = 2;
    
    private String AudioName = "";        //原始音频数据文件 ，麦克风    
    private String NewAudioName = "";     //可播放的音频文件  
	private static File recordFile ;

	ShowWaveView waveview;
	com.huari.ui.PartWaveShowView showwave;
	ViewPager viewpager;
	ItuAdapterOfListView listAdapter;
	PagerAdapterOfSpectrum spectrumAdapter;
	ListView itulistview;
	LinearLayout ituLinearLayout;
	ArrayList<View> viewlist;

	public static ArrayList<byte[]> audiolist1, audiolist2;
	public static boolean firstaudio = true;

	boolean partispause, fullispause = true;
	ArrayList<Parameter> ap;
	float startFreq = 0f, endFreq = 0f, pStepFreq = 0f, centerFreq = 0f,
			daikuan = 0f;
	float halfSpectrumsWide;// 频谱带宽的一半
	String logicId;
	String txname;
	MenuItem mitem;

	static byte[] info;

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
	private String fileName;
	private static String fileBasePath;
	private Station stationF;

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
		Thread thread = new Thread(() -> {
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
		});
		thread.start();
	}

	class MyThread extends Thread {
		boolean end = false;

		private void setEnd(boolean b) {
			end = b;
		}

		private void sendStartCmd() {
//			savePrepare();
			Thread thread = new Thread(() -> {
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
			});
			thread.start();
		}


		private void sendEndCmd() {
			ByteFileIoUtils.runFlag = false;
			Thread thread = new Thread(() -> {
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
			});
			thread.start();
		}

		public void run() {
			try {
				Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
				int available = 0;
				long time = 0;
				byte[] info;
				int flag = 0;

				while (available == 0 && end == false) {
					available = ins.available();
					if (available > 0) {
						info = new byte[available];
						ins.read(info);
						Parse.newParseSpectrumsAnalysis(info);
						if (saveFlag == true) {
							if (flag == 0) {
								savePrepare();
								flag++;
							}
							time = RealTimeSaveAndGetStore.SaveAtTime(available, info, time, 2);//给数据加一个时间的包头后递交到缓存队列中
						}
						available = 0;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void savePrepare() {
		ByteFileIoUtils.runFlag = true;
		queue = new LinkedBlockingDeque<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		fileName = "AN|" + df.format(new Date()).replaceAll(" ", "|");
//                    + "||" + stationname + "|" + devicename + "|" + stationKey + "|" + lan + "|" + lon;
//                    +"|"+logicId;    //会导致名字长度超出限制
		SharedPreferences sharedPreferences = getSharedPreferences("myclient", MODE_PRIVATE);
		SharedPreferences.Editor shareEditor = sharedPreferences.edit();
		shareEditor.putString(fileName, stationname + "|" + devicename + "||" + stationKey + "|||" + lan + "||||" + lon + "|||||" + logicId);
		shareEditor.commit();  //以文件名作为key来将台站信息存入shareReferences
		Log.d("xiaoxiao", String.valueOf(fileName.length()));
		SysApplication.byteFileIoUtils.writeBytesToFile(fileName, 2); //开始保存数据前的初始化
		RealTimeSaveAndGetStore.serializeFlyPig(stationF,fileName,2);//在消费者线程开启后，开始Statio的序列化并放入队列缓冲区中等待消费者线程遍历之
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

//		actionbar = getSupportActionBar();
//		actionbar.setDisplayShowHomeEnabled(false);
//		actionbar.setDisplayHomeAsUpEnabled(false);
//		actionbar.setDisplayShowCustomEnabled(true);
//		actionbar.setDisplayShowTitleEnabled(true);
//		actionbar.setCustomView(titlebar);

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
		stationF = GlobalData.stationHashMap.get(stationKey);
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
				fullispause = false;
				partispause = false;
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
			      
			      FileOsImpl.copyWaveFile(AudioName, NewAudioName);//给裸数据加上头文件

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
						(dialog, which) -> {
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
			      
			      FileOsImpl.copyWaveFile(AudioName, NewAudioName);   //给裸数据加上头文件
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
				e.printStackTrace();
			}
			inithread = null;
		}
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
		fullispause = false;
		partispause = false;
		startWindow();
//		RealTimeSaveAndGetStore.ParseLocalDdfData("nba",2,30);
		super.onResume();
	}

	private void startWindow() {
		Type type = new Type(WindowController.FLAG_ANALYSIS);
		EventBus.getDefault().postSticky(type);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.canDrawOverlays(this)) {
				WindowHelper.instance.setHasPermission(true);
				WindowHelper.instance.startWindowService(getApplicationContext());
			} else {
				new AlertDialog.Builder(this)
						.setTitle("提示：")
						.setMessage("需要悬浮窗权限")
						.setCancelable(true)
						.setPositiveButton("设置", (dialog, which) -> {
							Intent intent = new Intent();
							intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
							intent.setData(Uri.parse("package:" + getPackageName()));
							startActivity(intent);
						})
						.setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).show();
			}
		} else {
			WindowHelper.instance.setHasPermission(true);
			WindowHelper.instance.startWindowService(getApplicationContext());
		}
	}

	@Override
	protected void onPause() {
		wl.release();
		super.onPause();
		ByteFileIoUtils.runFlag = false;
		WindowHelper.instance.stopWindowService(this);
		willExit();
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
			fileBasePath = FileOsImpl.forSaveFloder;
            mAudioRawPath = fileBasePath + File.separator + "Voice"+File.separator+"transfer";
        }   
        if (!(new File(fileBasePath).exists())){
        	new File(fileBasePath).mkdirs();
		}
        return mAudioRawPath;
    }
    
    public static String getWavFilePath(){
        String mAudioWavPath = "";
        if (isSdcardExit()) {
            String fileBasePath = FileOsImpl.forSaveFloder;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String fileName = "REC|"+df.format(new Date()).replaceAll(" ", "|")+".wav";
            mAudioWavPath = fileBasePath + File.separator + "data" +File.separator+ fileName;
            if (!((new File(mAudioWavPath)).getParentFile().exists())){
				new File(mAudioWavPath).mkdirs();
			}
        }
        return mAudioWavPath;
    }
}
