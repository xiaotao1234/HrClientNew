package com.huari.dataentry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.huari.tools.SysApplication;

public class GlobalData {

	public static Object a = new Object();
	public static boolean isFirstAudio = true;
	public static boolean willplay = false;

	public static class MyUncaughtExceptionHander implements
			Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread arg0, Throwable arg1) {
			System.out.println("发生UncaughtException异常，查找原因比较困难");
			System.out.println("参考信息" + arg0.getName() + "  "
					+ arg0.getClass().getName() + "  "
					+ arg1.getClass().getName());
			SysApplication.getInstance().exit();
		}
	}

	public static MyUncaughtExceptionHander myExceptionHandler = new MyUncaughtExceptionHander();
	public static byte[] infoBuffer;
	public static int infoIndex;

	public static String mainIP = "192.168.0.151";  //192.168.137.1
	public static String requestTaskIP = "192.168.0.151";
	public static int port1 = 5000, port2 = 5012;
	public static String mainTitle = "已登陆";

	public static HashMap<String, Station> stationHashMap = new HashMap<>();// 用以存储监测站
	public static HashMap<String, UnManStation> unmanHashMap = new HashMap<String, UnManStation>();// 用以存储无人站
	public static HashMap<String, UnManServer> unmanServerHashMap = new HashMap<String, UnManServer>();// 用以存储无人站的服务器
	public static byte[] unmanbuffer;
	public static int unmanbufferindex;

	public static boolean toCreatService;
	public static short dianping;// 频谱分析时每次的电平
	public static short[] Spectrummax;
	public static short[] Spectrummin;
	public static short[] Spectrumavg;
	public static short[] Spectrumpinpu;
	public static int haveCount;// 已经收到haveCount次数据。算平均值时用到。(haveCount*avg+newdata)/(haveCount+1)。
	public static int oldcount;// 当该值发生变化时说明新来的数据点数已经不一样了，平均值、最大值、最小值都需要重新计算了
	public static byte[] buffer;
	public static int bufferIndex;
	public static byte[] audioBuffer;
	public static short[] yinzi;
	public static HashMap<String, String> ituHashMap;
	public static int[] iBuffer;
	public static int[] qBuffer;
	public static short[] siBuffer;
	public static short[] sqBuffer;
	public static int hin;

	// 以下用于频段扫描的数据
	public static short[] pinduanScan;// 存放一串完整的扫描数据
	public static short[] pinduanMax;
	public static short[] pinduanMin;
	public static short[] pinduanAvg;
	public static int arraylength;// 每次收到的数据个数，其实也就是pinduanScan的长度
	public static ArrayList<short[]> pinduanlist;// 存放多次收到的数组，当最后收到结束标志时，
	// 表示收到了一个完整的数据，将该list里的多个数组组装成一个完整的
	public static int haveScanCount;// 算平均值时用。（上次的平均值*haveScanCount+本次值）/(haveScanCount+1)=本次平均值
	public static byte[] pinduanbuffer;// 用以缓存不完整的数据，以便后后续的数据连接起来
	public static int pinduanbufferIndex;// pinduanbuffer已缓存到的位置，比如已存进了5个值，则该变量值为5。
	public static short[] PDyinzi;
	public static float fragment = 0.1f;
	public static LinkedList<short[]> pinduanQueue = new LinkedList<short[]>();
	public static LinkedList<byte[]> pCacheQueue = new LinkedList<byte[]>();
	public static boolean isEnd = true;
	public static int refreshLength = 0;
	public static int topValue = 8000;// topValue以下展示最大值、最小值、平均值；以上，则只展示最大值

	public static void clearPinDuan() {
		pinduanScan = null;
		pinduanMax = null;
		pinduanMin = null;
		pinduanAvg = null;
		arraylength = 0;
		pinduanlist = null;
		haveScanCount = 0;
		pinduanbuffer = null;
		pinduanbufferIndex = 0;
		PDyinzi = null;
		pinduanQueue.clear();
		pCacheQueue.clear();
		fragment = 0.1f;
		isEnd = true;
		refreshLength = 0;
		System.gc();
	}

	// 以下四个为单频测向的数据
	public static short DDFdianping;
	public static short north;
	public static short xiangdui;
	public static short qua;
	public static short fudu;
	public static double startfreq = 80;
	public static double endfreq = 100;
	public static int count;
	public static short[] pinpu;
	public static float bujin;
	public static short[] ddfmax;
	public static short[] ddfmin;
	public static short[] ddfavg;
	public static byte[] withFrameHeadBuffer;
	public static int framelength;

	// 地图实时画线所用
	public static String itemTitle = "实时示向";
	public static String stationKey, deviceName, logicId;

	public static void clearDDF() {
		DDFdianping = 0;
		north = 0;
		xiangdui = 0;
		qua = 0;
		fudu = 0;
		startfreq = 80;
		endfreq = 80;
		count = 0;
		pinpu = null;
		bujin = 0;
		System.gc();
	}

	public static void clearSpectrums() {
		Spectrummax = null;
		Spectrummin = null;
		Spectrumavg = null;
		Spectrumpinpu = null;
		haveCount = 0;
		buffer = null;
		bufferIndex = 0;
		audioBuffer = null;
		if (ituHashMap != null)
			ituHashMap.clear();
		iBuffer = null;
		qBuffer = null;
		siBuffer = null;
		sqBuffer = null;
		yinzi = null;
		hin = 0;
	}

}
