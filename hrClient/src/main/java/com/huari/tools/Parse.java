package com.huari.tools;

import java.io.UnsupportedEncodingException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.media.AudioTrack;

import android.util.Log;

import com.huari.client.MapActivity;
import com.huari.client.PinDuanScanningActivity;
import com.huari.client.SinglefrequencyDFActivity;
import com.huari.client.SpectrumsAnalysisActivity;
import com.huari.client.StationListActivity;
import com.huari.client.UnmanedStationsActivity;
import com.huari.dataentry.*;

public class Parse {

	static DecimalFormat df = new DecimalFormat("0.0000");

	public static void parseReceiveInfo(byte[] data) {
		try {
			int available = data.length;
			int index = 0;
			int fullframelength = 0;
			System.out.println("收到的长度是" + available);

			while (index < available - 3) {
				if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(data, index,
						index + 3)) == 0xEEEEEEEE
						&& GlobalData.infoBuffer == null)// 如果是帧头,并且缓存为Null，则证明是一个全新的开始。
				{
					int templength = MyTools.fourBytesToInt(MyTools
							.nigetPartByteArray(data, index + 4, index + 7));// 具体帧的长度
					fullframelength = templength + 8;
					System.out.println("全帧的长度是" + fullframelength);
					if (available - index >= fullframelength)// 说明当前帧是完整的
					{
						byte[] temp = new byte[fullframelength];
						System.arraycopy(data, index, temp, 0, fullframelength);
						System.out.println("当前帧完整了，开始解析");
						parseAllInfo(temp);
					} else{// 当前帧是不完整的
					
						GlobalData.infoBuffer = new byte[fullframelength];
						System.arraycopy(data, index, GlobalData.infoBuffer, 0,
								available - index);
						GlobalData.infoIndex = available - index;
						System.out.println("当前帧不完整，先缓存起来");
					}
					index = index + fullframelength;
				} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(data,
						index, index + 3)) != 0xEEEEEEEE
						&& GlobalData.infoBuffer != null)// 如果不是帧头并且GlobalData.buffer不为空，说明是前一帧的后续
				{

					int templength = Math.min(GlobalData.infoBuffer.length
							- GlobalData.infoIndex, data.length);
					System.arraycopy(data, 0, GlobalData.infoBuffer,
							GlobalData.infoIndex, templength);
					GlobalData.infoIndex = GlobalData.infoIndex + templength;
					System.out.println("缓存的数组长度是"
							+ GlobalData.infoBuffer.length + "索引是"
							+ GlobalData.infoIndex);

					if (GlobalData.infoIndex == GlobalData.infoBuffer.length) {
						Parse.parseAllInfo(GlobalData.infoBuffer);
						GlobalData.infoBuffer = null;
						GlobalData.infoIndex = 0;
						System.out.println("当前帧凑完整了，开始解析吧！");
					}
					System.out.println("前一帧的后续");
					index = index + templength;
				} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(data,
						index, index + 3)) != 0xEEEEEEEE
						&& GlobalData.buffer == null)// 如果不是帧头并且GlobalData.buffer为空，说明有漏帧,需要抛弃部分数据
				{
					Log.i("有漏帧", "发生");
					int m = 0;
					for (m = 0; m < available - 3; m++) {
						if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(
								data, m, m + 3)) == 0xEEEEEEEE) {
							break;
						}
					}
					m = m - 1;
					byte[] regment = new byte[data.length - m];
					System.arraycopy(data, m, regment, 0, data.length - m);
					index = available - 1;
					parseReceiveInfo(regment);
				}

			}
			data = null;
			System.gc();
		} catch (Exception e) {
			System.out.println("parseReceiveInfo解析中出现异常");
		}

	}

	public static void parseAllInfo(byte[] info) {
		try {
			switch (info[8]) {
			case 6:// 监测站
					// System.out.println("收到来自服务端的监测站数据");
				//String s = null;
				Parse.parseMonitoringStation(info);
				if (StationListActivity.handler != null) {
					StationListActivity.handler
							.sendEmptyMessage(StationListActivity.REFRESHSTATE);
				}
				break;
			case 60:// 无人站
				newParseUnManServer(info);
				break;
			case 61:// 无人站更新
				Parse.refreshUnManStation(info);
				if (UnmanedStationsActivity.handler != null)
					UnmanedStationsActivity.handler.sendEmptyMessage(0x9);
				break;
			case 48:// 频谱分析（单频测量）
				Parse.parseSpectrumsAnalysis(info);
				if (SpectrumsAnalysisActivity.handle != null)
					SpectrumsAnalysisActivity.handle.sendEmptyMessage(0x987);
				break;
			case 47:// 频段扫描
				Parse.parsePDScan(info);
				if (PinDuanScanningActivity.handler != null)
					PinDuanScanningActivity.handler.sendEmptyMessage(0x2);
				break;
			case 5:// 设备状态更新
				Parse.refreshDevice(info);
				break;
			case 3:// 下线
				Parse.stationDown(info);
				if (StationListActivity.handler != null)
					StationListActivity.handler
							.sendEmptyMessage(StationListActivity.REFRESHSTATE);
				if (MapActivity.handler != null) {
					MapActivity.handler
							.sendEmptyMessage(MapActivity.STATIONREFRESH);
				}
				break;
			case 4:// 上线
				Parse.parseMonitoringStation(info);
				if (StationListActivity.handler != null)
					StationListActivity.handler.sendEmptyMessage(0x1);
				break;
			case 46:// 单频测向
				switch (info[14]) {
				case 31:// 电平数据
					GlobalData.DDFdianping = Parse.parseDianping(MyTools
							.getPartByteArray(info, 9, info.length - 1));
					if (SinglefrequencyDFActivity.handler != null)
						SinglefrequencyDFActivity.handler.sendEmptyMessage(0x8);
					break;
				case 21:// 示向度测量数据
					Parse.parseShixiangdu(MyTools.getPartByteArray(info, 9,
							info.length - 1));
					if (SinglefrequencyDFActivity.handler != null)
						SinglefrequencyDFActivity.handler.sendEmptyMessage(0x8);
					break;
				case 8:// GPS数据来了

					break;
				}
			}
		} catch (Exception e) {
			Log.i("解析（如无人站更新）出现", "异常");
		}
	}

	public static void parseGPS(byte[] b) {
		try {
			String stationId = new String(MyTools.getPartByteArray(b, 9, 84),
					"UTF-16LE").trim();
//			float lon = fourBytesToFloat(MyTools.nigetPartByteArray(b, 85, 88));// 经度
//			float lan = fourBytesToFloat(MyTools.nigetPartByteArray(b, 89, 92));
			GlobalData.stationHashMap.get(stationId);
			if (MapActivity.handler != null) {

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void parseMonitoringStation(byte[] b)// 解析台站
	{
		System.out.println("我所解析时的长度是" + b.length);
		int framelength = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
				4, 7)) - 1;// 减1后即后面所有台站的长度
		// System.out.println("所有台站的长度，即帧头里所标的长度减1后的值："+framelength);
		// Log.i("所有台站的长度，即帧头里所标的长度减1后的值：",framelength+"");
		int temp = 0;// 前面所有台站的累积长度
		while (framelength - temp > 0)// 大于0说明有台站
		{
			// System.out.println("总收到数据"+b.length+"字节");
			int currentDevicesLength = MyTools.nifourBytesToInt(MyTools
					.getPartByteArray(b, temp + 242, temp + 245));// 当前台站所有设备的长度

			// System.out.println("当前台站所有设备的长度，即台站中所标的长度值："+currentDevicesLength);

			int currentStationLength = 237 + currentDevicesLength;
			byte[] current = MyTools.getPartByteArray(b, 9 + temp, 8 + temp
					+ currentStationLength);// 解析出的台站
			com.huari.dataentry.Station station = Parse.parseStation(current);// 返回一个Station对象，该对象的各个属性（除下挂设备外）已被赋值
			String currentStationId = null;
			try {
				currentStationId = new String(MyTools.getPartByteArray(current,
						37, 112), "UTF-16LE").trim();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}// Station的ID
			// System.out.println("当前台站的名称："+station.name);
			ArrayList<com.huari.dataentry.MyDevice> devicelist = new ArrayList<com.huari.dataentry.MyDevice>();
			ArrayList<MyDevice> showdevicelist = new ArrayList<MyDevice>();
			byte[] currentAllDevice = MyTools.getPartByteArray(current, 237,
					237 + currentDevicesLength - 1);// 该数组为当前台站所有设备
			int detemp = 0;// 前面所有设备的累积长度
			while (currentDevicesLength - detemp > 0)// 大于0说明后面有设备。开始解析设备。
			{
				try {
					int currentLogicLength = MyTools.fourBytesToInt(MyTools
							.nigetPartByteArray(currentAllDevice, 39 + detemp,
									detemp + 42));// 当前设备的所有逻辑参数的长度
					// System.out.println("当前设备的所有逻辑数据的长度，即设备中所标的长度："+currentLogicLength);
					int currentSingleDeviceLength = 43 + currentLogicLength;
					byte[] linshide = MyTools.getPartByteArray(
							currentAllDevice, detemp, detemp + 38);
					MyDevice mydevice = Parse.parseDevice(linshide);
					// System.out.println("当前设备的名称："+mydevice.name);
					// System.out.println("当前设备含有逻辑数据个数："+mydevice.logicParametersCount+"当前设备状态"+mydevice.state);
					byte[] currentSingleDevice = MyTools.getPartByteArray(
							currentAllDevice, detemp, detemp
									+ currentSingleDeviceLength - 1);// 当前的单个设备
					HashMap<String, LogicParameter> lpMap = new HashMap<String, LogicParameter>();

					byte[] currentLogic = MyTools.getPartByteArray(
							currentSingleDevice, 43,
							43 + currentLogicLength - 1);// 当前设备的所有逻辑参数

					int logictemp = 0;// 前面所有逻辑参数的累积长度
					while (currentLogicLength - logictemp > 0)// 大于0说明后面有逻辑参数。开始解析逻辑参数。
					{
						int currentParametersLength = MyTools
								.fourBytesToInt(MyTools.nigetPartByteArray(
										currentLogic, 128 + logictemp,
										131 + logictemp));
						// System.out.println("当前逻辑数据中所有参数的长度，即逻辑数据中所标的长度"+currentParametersLength);
						int currentSingleLogicLength = 132 + currentParametersLength;// 当前所有逻辑参数的总长度
						byte[] currentSingleLogic = MyTools.getPartByteArray(
								currentLogic, logictemp, logictemp
										+ currentSingleLogicLength - 1);// 当前的逻辑参数

						LogicParameter lp = Parse
								.parseLogic(currentSingleLogic);

						System.out.println("逻辑数据的Type:" + lp.type);

						ArrayList<Parameter> plist = new ArrayList<Parameter>();
						byte[] allparameters = MyTools.getPartByteArray(
								currentSingleLogic, 132,
								132 + currentParametersLength - 1);// 当前逻辑参数的所有参数
						int paratemp = 0;
						while (currentParametersLength - paratemp > 0) {
							int currentparaLength = MyTools
									.fourBytesToInt(MyTools.nigetPartByteArray(
											allparameters, paratemp,
											paratemp + 3));
							int currentparaTrueLength = currentparaLength + 4;
							byte[] currentsingleParameter = MyTools
									.getPartByteArray(allparameters, paratemp,
											currentparaTrueLength - 1
													+ paratemp);
							try {

								Parameter p = Parse
										.parseParameter(currentsingleParameter);
								plist.add(p);
							} catch (Exception e) {
								System.out.println("小错误");
							}
							paratemp = (paratemp + currentparaTrueLength);
						}

						lp.parameterlist = plist;
						lpMap.put(lp.id, lp);

						logictemp = logictemp + currentSingleLogicLength;
					}// 解析逻辑参数结束
					mydevice.logic = lpMap;
					if (mydevice.logic.size() > 0 || mydevice.state == 1)// state为1表示有故障
					{
						showdevicelist.add(mydevice);
					}
					devicelist.add(mydevice);
					detemp = detemp + currentSingleDeviceLength;

				} catch (Exception e) {
					System.out.println("大事不好啦！出现错误啦！");
				}
			}// 解析设备结束
			station.devicelist = devicelist;
			station.showdevicelist = showdevicelist;
			GlobalData.stationHashMap.put(currentStationId, station);
			temp = currentStationLength + temp;

		}// 最大的while截止。解析台站。
			// if(GlobalData.stationHashMap!=null)
		// {
		// System.out.println("共有 "+GlobalData.stationHashMap.size()+" 个台站\n");
		// for(String s:GlobalData.stationHashMap.keySet())
		// {
		// Station stat=GlobalData.stationHashMap.get(s);
		// System.out.println("台站名称："+stat.name+"台站ID"+stat.id);
		// System.out.println("该台站有设备："+stat.devicelist.size()+" 台");
		// for(int i=0;i<stat.devicelist.size();i++)
		// {
		// MyDevice device=stat.devicelist.get(i);
		// System.out.println("设备名称： "+device.name);
		// System.out.println("该设备有逻辑数据  "+device.logicParametersCount+"  个");
		// for(String t:device.logic.keySet())
		// {
		// LogicParameter lp=device.logic.get(t);
		// System.out.println("逻辑数据id:  "+lp.id);
		// System.out.println("该逻辑数据有参数   "+lp.parameterlist.size()+" 个");
		// for(int m=0;m<lp.parameterlist.size();m++)
		// {
		// Parameter pa=lp.parameterlist.get(m);
		// System.out.println("参数名称："+pa.name+"是否高级   "+pa.isAdvanced+"  参数最大值："+pa.maxValue+
		// "  参数最小值："+pa.minValue+"  参数默认值："+pa.defaultValue+
		// "  参数显示范畴："+pa.displayType+"  枚举类型："+pa.enumValues);
		// for(String ss:pa.enumValues)
		// {
		// System.out.print(ss+"  ");
		// }
		// }
		// }
		// }
		// System.out.println("\n\n");
		// }
		// }

	}

	public static void parseUnManedStation(byte[] b)// 解析无人站
	{
		int framelength = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
				4, 7)) - 1;// 减1后即后面所有服务器的长度
		int temp = 0;
		while (framelength - temp > 0)// 说明有服务器
		{
			int currentServerLength = MyTools.fourBytesToInt(MyTools
					.nigetPartByteArray(b, 9 + temp, 9 + temp + 3));// 当前服务器标值的长度
			int currentServerFullLength = currentServerLength + 4;
			byte[] currentServer = MyTools.getPartByteArray(b, 9 + temp, 12
					+ temp + currentServerLength);// 当前的Server
			UnManServer ums = Parse.parseUnManServer(currentServer);
			int untemp = MyTools.twoBytesToShort(MyTools.nigetPartByteArray(
					currentServer, 180, 181));// 当前服务器中的不定长
			HashMap<String, UnManStation> un = new HashMap<String, UnManStation>();

			int servertemp = 0;
			while (currentServerLength - 178 - untemp - servertemp > 0)// 说明后面还有无人站
			{
				int currentnumanstationlength = MyTools.fourBytesToInt(MyTools
						.nigetPartByteArray(currentServer, 182 + untemp
								+ servertemp, 185 + untemp + servertemp));
				int currentunmanlength = currentnumanstationlength + 4;
				byte[] unmanstation = MyTools.getPartByteArray(currentServer,
						182 + untemp + servertemp, 181 + untemp + servertemp
								+ currentunmanlength);// 当前的无人站
				UnManStation unstation = Parse.parseUnManStation(unmanstation);
				unstation.server = ums.name;
				un.put(unstation.id, unstation);
				GlobalData.unmanHashMap.put(unstation.id, unstation);
				servertemp = servertemp + currentunmanlength;
			}
			ums.unman = un;
			GlobalData.unmanServerHashMap.put(ums.id, ums);
			temp = temp + currentServerFullLength;
		}

	}

	public static MyDevice parseDevice(byte[] b) {
		MyDevice my = new MyDevice();
		try {
			my.name = new String(MyTools.getPartByteArray(b, 0, 35), "UTF-16LE")
					.trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		my.logicParametersCount = b[36];
		my.state = b[37];
		my.isOccupied = b[38];
		return my;
	}

	public static Station parseStation(byte[] b) {
		Station station = new Station();
		station.isCenter = b[0];// 是否控制中心
		try {
			station.centerName = new String(MyTools.getPartByteArray(b, 1, 32),
					"UTF-16LE").trim();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			station.id = new String(MyTools.getPartByteArray(b, 33, 108),
					"UTF-16LE").trim();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			station.parentId = new String(
					MyTools.getPartByteArray(b, 109, 184), "UTF-16LE");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			station.name = new String(MyTools.getPartByteArray(b, 185, 216),
					"UTF-16LE").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		station.lon = fourBytesToFloat(MyTools.nigetPartByteArray(b, 217, 220));// 经度
		station.lan = fourBytesToFloat(MyTools.nigetPartByteArray(b, 221, 224));// 纬度
		return station;
	}

	public static LogicParameter parseLogic(byte[] b) {
		LogicParameter lp = new LogicParameter();

		try {
			lp.id = new String(MyTools.getPartByteArray(b, 0, 75), "UTF-16LE");
			Log.i("逻辑ID", lp.id);
			String k = new String(MyTools.getPartByteArray(b, 76, 91),
					"UTF-16LE").toUpperCase();
			lp.type = k.trim();
			lp.name = new String(MyTools.getPartByteArray(b, 92, 127),
					"UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return lp;
	}

	public static Parameter parseParameter(byte[] b) {
		Parameter pa = new Parameter();
		try {
			pa.name = new String(MyTools.getPartByteArray(b, 4, 39), "UTF-16LE")
					.trim();
			// System.out.println(pa.name);
			pa.maxValue = fourBytesToFloat(MyTools.getPartByteArray(b, 40, 43));
			// System.out.println(pa.maxValue);
			pa.minValue = fourBytesToFloat(MyTools.getPartByteArray(b, 44, 47));
			// System.out.println(pa.minValue);
			pa.defaultValue = new String(MyTools.getPartByteArray(b, 48, 83),
					"UTF-16LE").trim();
			// System.out.println(pa.defaultValue);
			pa.displayType = new String(MyTools.getPartByteArray(b, 84, 99),
					"UTF-16LE").trim();
			// System.out.println(pa.displayType);
			pa.isAdvanced = b[100];
			pa.isEditable = b[101];
			if (b.length > 102) {
				String s = new String(MyTools.getPartByteArray(b, 102,
						b.length - 1), "UTF-16LE").trim();
				if (pa.name.equals("CenterFreq")) {
					System.out.println("开始吧：");
					System.out.println("中心频率参数长度：" + b.length);
					for (int i = 94; i < b.length - 1; i++) {
						System.out.print("隔" + b[i] + "开");
					}
				}
				String[] ps = s.split("\\|");
				String[] pss = null;
				if (ps[0].equals("")) {
					pss = new String[ps.length - 1];
					for (int i = 0; i < pss.length; i++) {
						pss[i] = ps[i + 1];
					}
					pa.enumValues = pss;
				} else {
					pa.enumValues = ps;
				}

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return pa;
	}

	public static UnManServer parseUnManServer(byte[] b) {
		UnManServer ums = new UnManServer();
		ums.name = new String(MyTools.getPartByteArray(b, 4, 39));
		ums.id = new String(MyTools.getPartByteArray(b, 40, 115));
		ums.parentId = new String(MyTools.getPartByteArray(b, 116, 191));
		short sho = twoBytesToShort(MyTools.nigetPartByteArray(b, 192, 193));
		ums.sonstationIdArray = new String(MyTools.getPartByteArray(b, 194,
				194 + sho - 1)).split("|");
		for (String ss : ums.sonstationIdArray) {
			ums.sonStationId.add(ss);
		}
		return ums;
	}

	public static UnManStation parseUnManStation(byte[] b) {
		UnManStation um = new UnManStation();
		um.id = new String(MyTools.getPartByteArray(b, 4, 79));
		um.name = new String(MyTools.getPartByteArray(b, 80, 115));
		um.lan = Parse
				.fourBytesToFloat(MyTools.nigetPartByteArray(b, 116, 123));
		um.lon = Parse
				.fourBytesToFloat(MyTools.nigetPartByteArray(b, 124, 131));
		um.isavailable = b[132];
		um.iskongtiao = b[133];
		int t = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, 0, 3));
		um.switcharray = new String(MyTools.getPartByteArray(b, 134, 3 + t))
				.split("|");
		for (String y : um.switcharray) {
			um.switchlist.add(y);
		}
		return um;
	}

	static class PlayAudio extends Thread {
		public void run() {
			while (true) {
				if (SpectrumsAnalysisActivity.firstaudio == true) {
					for (int i = 0; i < SpectrumsAnalysisActivity.audiolist1
							.size(); i++) {
						SpectrumsAnalysisActivity.at.write(
								SpectrumsAnalysisActivity.audiolist1.get(i), 0,
								SpectrumsAnalysisActivity.audioBuffersize);
						SpectrumsAnalysisActivity.at.flush();
						SpectrumsAnalysisActivity.at.play();
						SpectrumsAnalysisActivity.at.stop();
						while (SpectrumsAnalysisActivity.at.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					SpectrumsAnalysisActivity.firstaudio = false;
				} else if (SpectrumsAnalysisActivity.firstaudio == false) {
					for (int i = 0; i < SpectrumsAnalysisActivity.audiolist2
							.size(); i++) {
						SpectrumsAnalysisActivity.at.write(
								SpectrumsAnalysisActivity.audiolist2.get(i), 0,
								SpectrumsAnalysisActivity.audioBuffersize);
						SpectrumsAnalysisActivity.at.flush();
						SpectrumsAnalysisActivity.at.play();
						SpectrumsAnalysisActivity.at.stop();
						while (SpectrumsAnalysisActivity.at.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					SpectrumsAnalysisActivity.firstaudio = true;
				}

			}
		}
	}

	public static void newParseSpectrumsAnalysis(byte[] src)// 最新的频谱分析
	{
		try {
			int available = src.length;
			int index = 0;
			int fullframelength = 0;
			while (index < available - 3) {
				if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(src, index,
						index + 3)) == 0xEEEEEEEE)// 如果是帧头
				{
					int templength = MyTools.fourBytesToInt(MyTools
							.nigetPartByteArray(src, index + 4, index + 7));// 具体帧的长度
					fullframelength = templength + 8;
					if (available - index >= fullframelength)// 说明当前帧是完整的
					{
						switch (src[index + 8]) {
						case 31:// 电平
							byte[] temp = new byte[] { src[23 + index],
									src[22 + index] };
							GlobalData.dianping = (short) (Parse
									.twoBytesToShort(temp) / 10);
							SpectrumsAnalysisActivity.handle
									.sendEmptyMessage(SpectrumsAnalysisActivity.DIANPINGDATA);  //0x987;
							break;
						case 36:// 频谱数据
							int count = (fullframelength - 34) / 2;
							if (GlobalData.Spectrumpinpu == null
									|| GlobalData.oldcount != count) {
								GlobalData.Spectrumpinpu = new short[count];
								GlobalData.Spectrumavg = new short[count];
								GlobalData.Spectrummin = new short[count];
								GlobalData.Spectrummax = new short[count];
								for (int i = 0; i < count; i++) {
									GlobalData.Spectrummax[i] = -255;
									GlobalData.Spectrummin[i] = 500;
								}
								GlobalData.oldcount = count;
							}

							int y = 0;
							for (int n = index + 34; n < index
									+ fullframelength - 1; n = n + 2) {
								GlobalData.Spectrumpinpu[y] = (short) (MyTools
										.twoBytesToShort(new byte[] { src[n + 1],
												src[n] }) / 10);
								if (GlobalData.Spectrummax[y] < GlobalData.Spectrumpinpu[y]) {
									GlobalData.Spectrummax[y] = GlobalData.Spectrumpinpu[y];
								}
								if (GlobalData.Spectrummin[y] > GlobalData.Spectrumpinpu[y]) {
									GlobalData.Spectrummin[y] = GlobalData.Spectrumpinpu[y];
								}
								GlobalData.Spectrumavg[y] = (short) ((GlobalData.Spectrumpinpu[y] + GlobalData.Spectrumavg[y]
										* GlobalData.haveCount) / (GlobalData.haveCount + 1));
								y++;
							}
							GlobalData.haveCount++;
							SpectrumsAnalysisActivity.handle
									.sendEmptyMessage(SpectrumsAnalysisActivity.PINPUDATA);
							break;
						case 32:// ITU
							byte[] t = new byte[fullframelength - 22];
							System.arraycopy(src, index + 22, t, 0, t.length);
							String[] temp2 = new String(t, "UTF-16LE")
									.split("\\|");
							for (int e = 0; e < temp2.length; e++) {
								String[] mm = temp2[e].split(",");
								GlobalData.ituHashMap.put(mm[0], mm[1]);
							}
							SpectrumsAnalysisActivity.handle
									.sendEmptyMessage(SpectrumsAnalysisActivity.ITUDATA);
							break;
						case 35:// 声音帧
							int framelength = fullframelength - 21;

							if (GlobalData.isFirstAudio) {
								int caiyangpinlv = MyTools
										.fourBytesToInt(MyTools
												.nigetPartByteArray(src,
														index + 17, index + 20));
								short channelcount = MyTools
										.twoBytesToShort(new byte[] {
												src[index + 16], src[index + 15] });
								byte geshi = src[index + 14];
								SpectrumsAnalysisActivity.createAudioPlay(
										caiyangpinlv, geshi, channelcount,
										framelength);
								GlobalData.isFirstAudio = false;
							}
							synchronized (SpectrumsAnalysisActivity.synObject) {
								int leftlength = framelength;
								int costlength = 0;
								
								if (SpectrumsAnalysisActivity.tempbufferindex > 0)     // 说明缓存里有数据，
								{
									System.arraycopy(
											SpectrumsAnalysisActivity.tempAudioBuffer,
											0,
											SpectrumsAnalysisActivity.audioBuffer,
											0,
											SpectrumsAnalysisActivity.tempbufferindex);
									SpectrumsAnalysisActivity.audioindex += SpectrumsAnalysisActivity.tempbufferindex;
									SpectrumsAnalysisActivity.tempbufferindex = 0;
								}

								if (leftlength < SpectrumsAnalysisActivity.audioBuffersize)
								{
									int minLength = Math
											.min(leftlength,
													SpectrumsAnalysisActivity.audioBuffersize
															- SpectrumsAnalysisActivity.audioindex);
									
									System.arraycopy(src, index + 21,
											SpectrumsAnalysisActivity.audioBuffer,
											SpectrumsAnalysisActivity.audioindex,
											minLength);	
									
									SpectrumsAnalysisActivity.audioindex += minLength;
									
									if (SpectrumsAnalysisActivity.audioindex >= SpectrumsAnalysisActivity.audioBuffersize) 
									{
										SpectrumsAnalysisActivity.at.write(SpectrumsAnalysisActivity.audioBuffer, 0, SpectrumsAnalysisActivity.audioBuffersize);
										if (SpectrumsAnalysisActivity.isRecording)
										{
											SpectrumsAnalysisActivity.fos.write(SpectrumsAnalysisActivity.audioBuffer);
										}

										SpectrumsAnalysisActivity.audioindex = 0;
									}
									
									leftlength -= minLength;
									costlength += minLength;
								}
								else
								{
									int anum = leftlength / SpectrumsAnalysisActivity.audioBuffersize;
									while (anum > 0)
									{
										int minLength = Math.min(leftlength,
														SpectrumsAnalysisActivity.audioBuffersize
																- SpectrumsAnalysisActivity.audioindex);
										
										System.arraycopy(src, index + 21 + costlength,
												SpectrumsAnalysisActivity.audioBuffer,
												SpectrumsAnalysisActivity.audioindex,
												minLength);
										
										SpectrumsAnalysisActivity.audioindex += minLength;
																			
										if (SpectrumsAnalysisActivity.audioindex >= SpectrumsAnalysisActivity.audioBuffersize) 
										{
											SpectrumsAnalysisActivity.at.write(SpectrumsAnalysisActivity.audioBuffer, 0, SpectrumsAnalysisActivity.audioBuffersize);
											if (SpectrumsAnalysisActivity.isRecording)
											{
												SpectrumsAnalysisActivity.fos.write(SpectrumsAnalysisActivity.audioBuffer);
											}
											SpectrumsAnalysisActivity.audioindex = 0;
										}
																
										leftlength -= minLength;
										costlength += minLength;
										
										anum = leftlength / SpectrumsAnalysisActivity.audioBuffersize;
									}
									
								}

								if (leftlength > 0)         // 来的数据有剩余，则缓存起来
								{
									System.arraycopy(
											src,
											index + 21 + costlength,
											SpectrumsAnalysisActivity.tempAudioBuffer,
											0,  leftlength);
									SpectrumsAnalysisActivity.tempbufferindex = leftlength;
								}
							}
							break;
						case 34:// 因子
							int l = MyTools.fourBytesToInt(MyTools
									.nigetPartByteArray(src, index + 30,
											index + 33));
							GlobalData.yinzi = new short[l];
							int g = 0;
							for (int v = index + 34; v < index
									+ fullframelength - 1; v = v + 2) {
								GlobalData.yinzi[g] = (short) (MyTools
										.twoBytesToShort(new byte[] { src[v + 1],
												src[v] }) / 10);
								g++;
							}
							break;
						case 33:// IQ
							int c = MyTools.fourBytesToInt(MyTools
									.nigetPartByteArray(src, index + 23,
											index + 26));
							int ui = 0;
							int uq = 0;
							switch (src[index + 22]) {
							case 0:
								for (int r = index + 27; r < index + c * 2 + 26; r = r + 2) {
									GlobalData.siBuffer[ui] = MyTools
											.twoBytesToShort(new byte[] {
													src[r + 1], src[r] });
									ui++;
								}
								for (int w = index + 27 + c * 2; w < index + 4
										* c + 26; w = w + 2) {
									GlobalData.sqBuffer[uq] = MyTools
											.twoBytesToShort(new byte[] {
													src[w + 1], src[w] });
									uq++;
								}
								break;

							case 1:
								for (int r = index + 27; r < index + 4 * c + 23; r = r + 4) {
									GlobalData.iBuffer[ui] = MyTools
											.twoBytesToShort(MyTools
													.nigetPartByteArray(src, r,
															r + 3));
									ui++;
								}
								for (int w = index + 27 + c * 4; w < index + 8
										* c + 23; w = w + 4) {
									GlobalData.qBuffer[uq] = MyTools
											.twoBytesToShort(MyTools
													.nigetPartByteArray(src, w,
															w + 3));
									uq++;
								}
								break;
							}

						}

					} else{// 当前帧是不完整的
					
						GlobalData.buffer = new byte[fullframelength];
						Log.i("当前帧不完整", "帧长" + fullframelength + " ");
						System.arraycopy(src, index, GlobalData.buffer, 0,
								available - index);
						Log.i("不完整帧当前索引", GlobalData.bufferIndex + "  ");
						GlobalData.bufferIndex = available - index;
						Log.i("不完整帧当前最新索引", GlobalData.bufferIndex + "  ");

					}
					index = index + fullframelength;
				} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(src,
						index, index + 3)) != 0xEEEEEEEE)
				// &&
				// GlobalData.buffer!=null)//如果不是帧头并且GlobalData.buffer不为空，说明是前一帧的后续
				{
					Log.i("后续部分到了", "出发");
					int templength = Math.min(GlobalData.buffer.length
							- GlobalData.bufferIndex, src.length);
					System.arraycopy(src, 0, GlobalData.buffer,
							GlobalData.bufferIndex, templength);
					GlobalData.bufferIndex = GlobalData.bufferIndex
							+ templength;
					if (GlobalData.bufferIndex == GlobalData.buffer.length) {
						Parse.newParseSpectrumsAnalysis(GlobalData.buffer);
						GlobalData.buffer = null;
						GlobalData.bufferIndex = 0;
					}
					index = index + templength;
				} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(src,
						index, index + 3)) != 0xEEEEEEEE
						&& GlobalData.buffer == null)// 如果不是帧头并且GlobalData.buffer为空，说明有漏帧,需要抛弃部分数据
				{
					Log.i("有漏帧", "发生");
					int m = 0;
					for (m = 0; m < available - 3; m++) {
						if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(
								src, m, m + 3)) == 0xEEEEEEEE) {
							break;
						}
					}
					m = m - 1;
					byte[] regment = new byte[src.length - m];
					System.arraycopy(src, m, regment, 0, src.length - m);
					index = available - 1;
					Parse.newParseSpectrumsAnalysis(regment);
				}
			}
			src = null;
			System.gc();
		} catch (Exception e) {

		}
	}

	public static void parseSpectrumsAnalysis(byte[] b) {
		int alllength = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, 4,
				7));
		int availablelength = 0;
		int iTUlength = 0;
		byte[] itu = null;
		int pinpulength = 0;
		byte[] pinpu = null;
//		int dianpinglength = 0;
		byte[] dianping = null;
		if (alllength > 1) {
			availablelength = MyTools.fourBytesToInt(MyTools
					.nigetPartByteArray(b, 9, 12));
			iTUlength = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
					13, 16));
		}
		if (availablelength > 0) {
			itu = MyTools.getPartByteArray(b, 13, 16 + iTUlength);
		}
		if (availablelength - 4 - iTUlength > 0) {
			pinpulength = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
					17 + iTUlength, 20 + iTUlength));
			pinpu = MyTools.getPartByteArray(b, 17 + iTUlength, 20 + iTUlength
					+ pinpulength);
			parsePinPu(pinpu);
		}
		if (availablelength - 4 - iTUlength - 4 - pinpulength > 0) {
			dianping = MyTools.getPartByteArray(b,
					21 + pinpulength + iTUlength, 12 + availablelength);
			GlobalData.dianping = parseDianping(dianping);
		}
	}

	public static void parseITU(byte[] b)// 解析ITU
	{
//		int length = MyTools
//				.fourBytesToInt(MyTools.nigetPartByteArray(b, 0, 3));
//		byte type = b[4];
//		double d = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, 5, 12));
		byte[] temp = MyTools.getPartByteArray(b, 13, b.length - 1);
		String[] tempString = new String(temp).split("|");
		HashMap<String, String> map = new HashMap<String, String>();
		for (String t : tempString) {
			String[] t2 = t.split(",");
			map.put(t2[0], t2[1]);
		}
	}

	public static void parsePinPu(byte[] b)// 解析频谱数据帧
	{
//		int length = MyTools
//				.fourBytesToInt(MyTools.nigetPartByteArray(b, 0, 3));
//		byte type = b[4];
		GlobalData.startfreq = MyTools.byteToDouble(MyTools.getPartByteArray(b,
				5, 12));
		GlobalData.endfreq = MyTools.byteToDouble(MyTools.getPartByteArray(b,
				13, 20));
		GlobalData.count = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
				21, 24));
		// GlobalData.bujin=(GlobalData.startfreq-GlobalData.endfreq)/GlobalData.count;
		short[] save = new short[GlobalData.count];// 即频谱数据
		int n = 0;
		for (int i = 25; i < b.length; i = i + 2) {
			byte[] temp = new byte[] { b[i + 1], b[i] };
			save[n] = MyTools.twoBytesToShort(temp);
			n++;
		}
		GlobalData.pinpu = save;
	}

	public static void parseNewPinPu(byte[] b)// 解析频谱数据帧
	{
//		int length = MyTools.fourBytesToInt(MyTools
//				.nigetPartByteArray(b, 9, 12));
//		byte type = b[13];
		GlobalData.startfreq = MyTools.byteToDouble(MyTools.getPartByteArray(b,
				14, 21));
		GlobalData.endfreq = MyTools.byteToDouble(MyTools.getPartByteArray(b,
				22, 29));
		GlobalData.count = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
				30, 33));
		// GlobalData.bujin=(GlobalData.startfreq-GlobalData.endfreq)/GlobalData.count;
		short[] save = new short[GlobalData.count];// 即频谱数据
		int n = 0;
		for (int i = 34; i < b.length; i = i + 2) {
			byte[] temp = new byte[] { b[i], b[i + 1] };
			save[n] = MyTools.twoBytesToShort(temp);
			n++;
		}
		GlobalData.pinpu = save;
	}

	public static short parseDianping(byte[] b) {
		byte[] temp = new byte[] { b[23], b[22] };
		return (short) (Parse.twoBytesToShort(temp) / 10);
	}

	// 改写于2014.11.3
	public static void refreshUnManStation(byte[] b)// 无人站更新
	{
		try {
			int biaolength = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(
					b, 4, 7)) - 1;// 帧头后的那个长度，减去表示功能号的1个字节。
			int temp = 0;
			while (biaolength - temp > 0) {
				int sflength = MyTools.fourBytesToInt(MyTools
						.nigetPartByteArray(b, 9 + temp, 12 + temp));// 无人站帧的长度
				int stlength = sflength + 4;
				byte[] newinfo = MyTools.getPartByteArray(b, 9 + temp, 8
						+ stlength + temp);
				String id = new String(
						MyTools.getPartByteArray(newinfo, 4, 79), "UTF-16LE");
				byte isavailable = newinfo[80];
				String state = new String(MyTools.getPartByteArray(newinfo, 81,
						88), "UTF-16LE");
				String info = new String(MyTools.getPartByteArray(newinfo, 89,
						stlength - 1), "UTF-16LE");
				GlobalData.unmanHashMap.get(id).state = state;
				GlobalData.unmanHashMap.get(id).info = info;
				GlobalData.unmanHashMap.get(id).isavailable = isavailable;
				if (state.startsWith("已经断")) {
					GlobalData.unmanHashMap.get(id).isavailable = 6;
				} else if (state.startsWith("已经上")) {
					GlobalData.unmanHashMap.get(id).isavailable = 4;
				}
				if (state.startsWith("已经掉")) {
					GlobalData.unmanHashMap.get(id).isavailable = 1;
				}
				// 通知更新
				temp = temp + stlength;
			}
		} catch (Exception e) {
			Log.i("更新无人站", "异常");
		}
	}

	public static void ynewParsePDScan(byte[] b) {
		int available = b.length;
		int index = 0;
		int fullframelength = 0;
		while (index < available - 3) {
			if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, index,
					index + 3)) == 0xEEEEEEEE)// 如果是帧头
			{
				int templength = MyTools.fourBytesToInt(MyTools
						.nigetPartByteArray(b, index + 4, index + 7));// 具体帧的长度
				fullframelength = templength + 8;
				if (available - index >= fullframelength)// 说明当前帧是完整的
				{
					switch (b[index + 8]) {
					case 47:// 频段扫描数据
						int temcount = MyTools.fourBytesToInt(MyTools
								.nigetPartByteArray(b, index + 14, index + 17));// 点数

						int startindex = MyTools.fourBytesToInt(MyTools
								.nigetPartByteArray(b, index + 18, index + 21));
						int endindex = MyTools.fourBytesToInt(MyTools
								.nigetPartByteArray(b, index + 22, index + 25));
						short[] tempshort = null;
						if (b[index + 13] != 0
								&& GlobalData.pinduanQueue.size() != 0)// 不是结束
						{
							tempshort = GlobalData.pinduanQueue.getLast();
						} else if (b[index + 13] != 0
								&& GlobalData.pinduanQueue.size() == 0)// 不是结束
						{
							tempshort = new short[temcount];
						}
						if (tempshort == null) {
							tempshort = new short[temcount];
						}

						int part = startindex;
						for (int i = index + 26; i < index + fullframelength
								- 1; i = i + 2) {
							tempshort[part] = (short) (MyTools
									.twoBytesToShort(new byte[] { b[i + 1],
											b[i] }) / 10);
							part++;
						}
						if (!GlobalData.pinduanQueue.contains(tempshort))
							GlobalData.pinduanQueue.add(tempshort);

						if (PinDuanScanningActivity.ScanType.equals("PSCAN")) {
							if (b[index + 13] == 0)// 是结束
							{
								// if(GlobalData.pinduanMax==null)
								// {
								// GlobalData.pinduanMax=new
								// short[GlobalData.pinduanScan.length];
								// GlobalData.pinduanMin=new
								// short[GlobalData.pinduanScan.length];
								// GlobalData.pinduanAvg=new
								// short[GlobalData.pinduanScan.length];
								// for(int
								// bc=0;bc<GlobalData.pinduanScan.length;bc++)
								// {
								// GlobalData.pinduanMax[bc]=-255;
								// GlobalData.pinduanMin[bc]=255;
								// }
								// }
								// for(int
								// yy=0;yy<GlobalData.pinduanScan.length;yy++)
								// {
								// if(GlobalData.pinduanMax[yy]<GlobalData.pinduanScan[yy])
								// {
								// GlobalData.pinduanMax[yy]=GlobalData.pinduanScan[yy];
								// }
								//
								// if(GlobalData.pinduanMin[yy]>GlobalData.pinduanScan[yy])
								// {
								// GlobalData.pinduanMin[yy]=GlobalData.pinduanScan[yy];
								// }
								//
								// GlobalData.pinduanAvg[yy]=(short)
								// ((GlobalData.pinduanAvg[yy]*
								// GlobalData.haveScanCount+GlobalData.pinduanScan[yy])/
								// (GlobalData.haveScanCount+1));
								// }
								GlobalData.haveScanCount++;
								PinDuanScanningActivity.handler
										.sendEmptyMessage(PinDuanScanningActivity.SCANNINGDATA);
							}

						} else if (PinDuanScanningActivity.ScanType
								.equals("FSCAN")) {
							// if(GlobalData.pinduanMax==null)
							// {
							// GlobalData.pinduanMax=new
							// short[GlobalData.pinduanScan.length];
							// GlobalData.pinduanMin=new
							// short[GlobalData.pinduanScan.length];
							// GlobalData.pinduanAvg=new
							// short[GlobalData.pinduanScan.length];
							// for(int
							// bc=0;bc<GlobalData.pinduanScan.length;bc++)
							// {
							// GlobalData.pinduanMax[bc]=-255;
							// GlobalData.pinduanMin[bc]=255;
							// }
							// }
							// for(int yy=startindex;yy<=endindex;yy++)
							// {
							// if(GlobalData.pinduanMax[yy]<GlobalData.pinduanScan[yy])
							// {
							// GlobalData.pinduanMax[yy]=GlobalData.pinduanScan[yy];
							// }
							//
							// if(GlobalData.pinduanMin[yy]>GlobalData.pinduanScan[yy])
							// {
							// GlobalData.pinduanMin[yy]=GlobalData.pinduanScan[yy];
							// }
							//
							// GlobalData.pinduanAvg[yy]=(short)
							// ((GlobalData.pinduanAvg[yy]*
							// GlobalData.haveScanCount+GlobalData.pinduanScan[yy])/
							// (GlobalData.haveScanCount+1));
							// }
							//
							if (endindex >= temcount * GlobalData.fragment - 1) {
								PinDuanScanningActivity.handler
										.sendEmptyMessage(PinDuanScanningActivity.SCANNINGDATA);
								GlobalData.fragment = GlobalData.fragment + 0.1f;
							}
							System.out.println("结尾的endindex是" + endindex);
							if (b[index + 13] == 0)// 是结束
							{
								GlobalData.haveScanCount++;
								GlobalData.fragment = 0.1f;
							}
						}

						break;
					case 33:// 因子
						int l = MyTools.fourBytesToInt(MyTools
								.nigetPartByteArray(b, index + 30, index + 33));
						GlobalData.PDyinzi = new short[l / 2];
						int g = 0;
						for (int v = index + 34; v < index + fullframelength
								- 1; v = v + 2) {
							GlobalData.yinzi[g] = MyTools
									.twoBytesToShort(new byte[] { b[v + 1],
											b[v] });
							g++;
						}
						break;
					}
					index = index + fullframelength;
				} else// 当前帧是不完整的
				{
					GlobalData.pinduanbuffer = new byte[fullframelength];
					// Log.i("当前帧不完整","帧长"+fullframelength+" ");
					System.arraycopy(b, index, GlobalData.pinduanbuffer, 0,
							available - index);
					// Log.i("不完整帧当前索引",GlobalData.bufferIndex+"  ");
					GlobalData.pinduanbufferIndex = available - index;
					// Log.i("不完整帧当前最新索引",GlobalData.bufferIndex+"  ");
					index = available - 1;
				}
			} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
					index, index + 3)) != 0xEEEEEEEE
					&& GlobalData.pinduanbuffer != null)// 如果不是帧头并且GlobalData.pinduanbuffer不为空，说明是前一帧的后续
			{
				// Log.i("后续部分到了","出发");
				int templength = Math.min(GlobalData.pinduanbuffer.length
						- GlobalData.pinduanbufferIndex, b.length);
				System.arraycopy(b, 0, GlobalData.pinduanbuffer,
						GlobalData.pinduanbufferIndex, templength);
				GlobalData.pinduanbufferIndex = GlobalData.pinduanbufferIndex
						+ templength;
				if (GlobalData.pinduanbufferIndex == GlobalData.pinduanbuffer.length) {
					Parse.newParsePDScan(GlobalData.pinduanbuffer);
					GlobalData.pinduanbuffer = null;
					GlobalData.pinduanbufferIndex = 0;
				}
				index = index + templength;
			} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
					index, index + 3)) != 0xEEEEEEEE
					&& GlobalData.pinduanbuffer == null)// 如果不是帧头并且GlobalData.pinduanbuffer为空，说明有漏帧,需要抛弃部分数据
			{
				// Log.i("有漏帧", "发生");
				int m = 0;
				for (m = 0; m < available - 3; m++) {
					if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, m,
							m + 3)) == 0xEEEEEEEE) {
						break;
					}
				}
				// m=m-1;
				byte[] regment = new byte[b.length - m];
				System.arraycopy(b, m, regment, 0, b.length - m);
				index = available - 1;
				Parse.newParsePDScan(regment);

			}

		}

	}

	public static void newParsePDScan(byte[] data) {
		int available = data.length;
		int index = 0;
		int fullframelength = 0;
		while (index < available - 3) {
			if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(data, index,
					index + 3)) == 0xEEEEEEEE)// 如果是帧头
			{
				int templength = MyTools.fourBytesToInt(MyTools
						.nigetPartByteArray(data, index + 4, index + 7));// 具体帧的长度
				fullframelength = templength + 8;
				if (available - index >= fullframelength)// 说明当前帧是完整的
				{
					switch (data[index + 8]) {
					case 47:// 频段扫描数据
						int temcount = MyTools.fourBytesToInt(MyTools
								.nigetPartByteArray(data, index + 14, index + 17));// 点数

						int startindex = MyTools.fourBytesToInt(MyTools
								.nigetPartByteArray(data, index + 18, index + 21));
						int endindex = MyTools.fourBytesToInt(MyTools
								.nigetPartByteArray(data, index + 22, index + 25));
						short[] tempshort = null;

						if (PinDuanScanningActivity.ScanType.equals("PSCAN")) {
							if (data[index + 13] != 0 && GlobalData.isEnd == false)// 不是结束，并且上一个数组还未结束
							{
								tempshort = GlobalData.pinduanQueue.getLast();
								System.out.println("哪里异常了？");
							} else if (data[index + 13] != 0 && GlobalData.isEnd)// 不是结束，并且上一个数组结束了
							{
								tempshort = new short[temcount];
								GlobalData.isEnd = false;
							} else if (data[index + 13] == 0 && GlobalData.isEnd)// 是结束，并且上一个数组结束了
							{
								tempshort = new short[temcount];
								GlobalData.isEnd = true;
							} else if (data[index + 13] == 0
									&& GlobalData.isEnd == false) {
								tempshort = GlobalData.pinduanQueue.getLast();

								GlobalData.isEnd = true;
							}
							if (tempshort == null) {
								tempshort = new short[temcount];
							}

							int part = startindex;

							for (int i = index + 26; i < index
									+ fullframelength - 1; i = i + 2) {
								tempshort[part] = (short) (MyTools
										.twoBytesToShort(new byte[] { data[i + 1],
												data[i] }) / 10);
								part++;
							}

							if (!GlobalData.pinduanQueue.contains(tempshort))
								GlobalData.pinduanQueue.add(tempshort);
							if (data[index + 13] == 0)// 是结束
							{
								if (GlobalData.pinduanMax == null) {
									GlobalData.pinduanMax = new short[temcount];
									GlobalData.pinduanMin = new short[temcount];
									GlobalData.pinduanAvg = new short[temcount];
									for (int bc = 0; bc < temcount; bc++) {
										GlobalData.pinduanMax[bc] = -255;
										GlobalData.pinduanMin[bc] = 255;
									}
								}
								for (int yy = 0; yy < temcount; yy++) {
									if (GlobalData.pinduanMax[yy] < tempshort[yy]) {
										GlobalData.pinduanMax[yy] = tempshort[yy];
									}

									if (GlobalData.pinduanMin[yy] > tempshort[yy]) {
										GlobalData.pinduanMin[yy] = tempshort[yy];
									}

									GlobalData.pinduanAvg[yy] = (short) ((GlobalData.pinduanAvg[yy]
											* GlobalData.haveScanCount + tempshort[yy]) / (GlobalData.haveScanCount + 1));
								}
								GlobalData.haveScanCount++;
								if (temcount <= GlobalData.topValue) {
									PinDuanScanningActivity.handler
											.sendEmptyMessage(PinDuanScanningActivity.SCANNINGDATA);
								} else {
									PinDuanScanningActivity.handler
											.sendEmptyMessage(PinDuanScanningActivity.SCANNINGDATANO);
								}
							}

						} else if (!PinDuanScanningActivity.ScanType
								.equals("PSCAN"))// 非PSCAN模式
						{
							if (GlobalData.pinduanScan == null) {
								GlobalData.pinduanScan = new short[temcount];
							}
							if (GlobalData.pinduanMax == null) {
								GlobalData.pinduanMax = new short[temcount];
								GlobalData.pinduanMin = new short[temcount];
								GlobalData.pinduanAvg = new short[temcount];
								for (int bc = 0; bc < temcount; bc++) {
									GlobalData.pinduanMax[bc] = -255;
									GlobalData.pinduanMin[bc] = 255;
								}
							}
							int part = startindex;
							for (int i = index + 26; i < index
									+ fullframelength - 1; i = i + 2) {
								GlobalData.pinduanScan[part] = (short) (MyTools
										.twoBytesToShort(new byte[] { data[i + 1],
												data[i] }) / 10);
								part++;
							}

							for (int yy = startindex; yy <= endindex; yy++) {
								if (GlobalData.pinduanMax[yy] < GlobalData.pinduanScan[yy]) {
									GlobalData.pinduanMax[yy] = GlobalData.pinduanScan[yy];
								}

								if (GlobalData.pinduanMin[yy] > GlobalData.pinduanScan[yy]) {
									GlobalData.pinduanMin[yy] = GlobalData.pinduanScan[yy];
								}

								GlobalData.pinduanAvg[yy] = (short) ((GlobalData.pinduanAvg[yy]
										* GlobalData.haveScanCount + GlobalData.pinduanScan[yy]) / (GlobalData.haveScanCount + 1));
							}
							GlobalData.refreshLength = GlobalData.refreshLength
									+ endindex - startindex + 1;
							if (GlobalData.refreshLength >= temcount
									* GlobalData.fragment) {
								if (temcount <= GlobalData.topValue) {

									PinDuanScanningActivity.handler
											.sendEmptyMessage(PinDuanScanningActivity.SCANNINGDATAFSCAN);
								} else {
									PinDuanScanningActivity.handler
											.sendEmptyMessage(PinDuanScanningActivity.SCANNINGDATAFSCANNO);
								}
								GlobalData.fragment = GlobalData.fragment + 0.1f;
							}

							if (data[index + 13] == 0)// 是结束
							{
								GlobalData.haveScanCount++;
								GlobalData.fragment = 0.1f;
							}
						}
						break;
					case 33:
						break;
					case 34:// 因子
						int l = MyTools.fourBytesToInt(MyTools
								.nigetPartByteArray(data, index + 30, index + 33));
						GlobalData.PDyinzi = new short[l];
						int g = 0;
						try {
							for (int v = index + 34; v < index
									+ fullframelength - 1; v = v + 2) {
								GlobalData.PDyinzi[g] = (short) (MyTools
										.twoBytesToShort(new byte[] { data[v + 1],
												data[v] }) / 10);
								g++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						break;
					}
					index = index + fullframelength;
				} else {// 当前帧是不完整的
				
					GlobalData.pinduanbuffer = new byte[fullframelength];
					// Log.i("当前帧不完整","帧长"+fullframelength+" ");
					System.arraycopy(data, index, GlobalData.pinduanbuffer, 0,
							available - index);
					// Log.i("不完整帧当前索引",GlobalData.bufferIndex+"  ");
					GlobalData.pinduanbufferIndex = available - index;
					// Log.i("不完整帧当前最新索引",GlobalData.bufferIndex+"  ");
					index = available - 1;
				}
			} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(data,
					index, index + 3)) != 0xEEEEEEEE
					&& GlobalData.pinduanbuffer != null) {// 如果不是帧头并且GlobalData.pinduanbuffer不为空，说明是前一帧的后续
			
				// Log.i("后续部分到了","出发");
				int templength = Math.min(GlobalData.pinduanbuffer.length
						- GlobalData.pinduanbufferIndex, data.length);
				System.arraycopy(data, 0, GlobalData.pinduanbuffer,
						GlobalData.pinduanbufferIndex, templength);
				GlobalData.pinduanbufferIndex = GlobalData.pinduanbufferIndex
						+ templength;
				if (GlobalData.pinduanbufferIndex == GlobalData.pinduanbuffer.length) {
					Parse.newParsePDScan(GlobalData.pinduanbuffer);
					GlobalData.pinduanbuffer = null;
					GlobalData.pinduanbufferIndex = 0;
				}
				index = index + templength;
			} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(data,
					index, index + 3)) != 0xEEEEEEEE
					&& GlobalData.pinduanbuffer == null) {// 如果不是帧头并且GlobalData.pinduanbuffer为空，说明有漏帧,需要抛弃部分数据
			
				// Log.i("有漏帧", "发生");
				int m = 0;
				for (m = 0; m < available - 3; m++) {
					if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(data, m,
							m + 3)) == 0xEEEEEEEE) {
						break;
					}
				}
				// m=m-1;
				byte[] regment = new byte[data.length - m];
				System.arraycopy(data, m, regment, 0, data.length - m);
				index = available - 1;
				Parse.newParsePDScan(regment);
			}
		}
	}

	public static short[] parsePDScan(byte[] b) {
		int alllength = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, 4,
				7));
		int availablelength = 0;
		byte isend = 0;
		int count = 0;
		int startindex = 0, endindex = 0;
		short[] data = null;
		if (alllength > 1) {
			availablelength = MyTools.fourBytesToInt(MyTools
					.nigetPartByteArray(b, 9, 12));
			isend = b[13];
			count = MyTools.fourBytesToInt(MyTools
					.nigetPartByteArray(b, 14, 17));
			startindex = MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
					18, 21));
			if (availablelength > 13) {
				data = new short[(availablelength - 13) / 2];
				int m = 0;
				for (int i = 26; i < b.length; i = i + 2) {
					data[m] = MyTools.twoBytesToShort(MyTools
							.nigetPartByteArray(b, i, i + 1));
				}
			}
		}
		return data;
	}

	public static void refreshDevice(byte[] b) {
		String stationId = null;
		try {
			stationId = new String(MyTools.getPartByteArray(b, 9, 84),
					"UTF-16LE").trim();
		} catch (Exception e) {
			System.out.println("设备更新解析stationId时出现异常");
		}
		String devicename = null;
		try {
			devicename = new String(MyTools.getPartByteArray(b, 85, 120),
					"UTF-16LE").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte state = b[121];
		Station s = GlobalData.stationHashMap.get(stationId);

		for (MyDevice m : s.devicelist) {
			if (m.name.equals(devicename)) {
				MyDevice md = m;
				md.isOccupied = state;
				break;
			}
		}
		try {
			if (StationListActivity.handler != null)
				StationListActivity.handler
						.sendEmptyMessage(StationListActivity.REFRESHSTATE);
		} catch (Exception e) {
			System.out.println("台站设备刷新消息通知失败！");
		}
		if (MapActivity.handler != null) {
			MapActivity.handler.sendEmptyMessage(MapActivity.STATIONREFRESH);
		}
	}

	public static void stationDown(byte[] b) {
		try {
			String id = new String(MyTools.getPartByteArray(b, 9, 84),
					"UTF-16LE").trim();
			GlobalData.stationHashMap.remove(id);
		} catch (Exception e) {
			System.out.println("解析监测站下线时发生错误");
		}
	}

	public static void parseVoice(byte[] b) {
		int length = MyTools
				.fourBytesToInt(MyTools.nigetPartByteArray(b, 0, 3));
		byte type = b[4];
		byte format = b[5];
		short[] data = new short[(length - 4) / 2];
		int m = 0;
		if (length > 6) {
			for (int i = 6; i < b.length; i = i + 2) {
				data[m] = MyTools.twoBytesToShort(MyTools.nigetPartByteArray(b,	i, i + 1));
			}
		}
	}

	public static void parseShixiangdu(byte[] b) {
		byte function = b[8];
		if (function == 46) {
			switch (b[13]) {
			case 31:// 电平数据
				GlobalData.DDFdianping = Parse.parseDianping(b);
				Log.i(GlobalData.DDFdianping + "电平", "输出");
				// SinglefrequencyDFActivity.handler.sendEmptyMessage(0x8);
				break;
			case 37:// 示向度测量数据
				// Parse.parseShixiangdu(MyTools.getPartByteArray(b, 9,
				// b.length-1));

				GlobalData.north = (short) (MyTools.twoBytesToShort(MyTools
						.nigetPartByteArray(b, 14, 15)) / (short) 10);
				GlobalData.xiangdui = (short) (MyTools.twoBytesToShort(MyTools
						.nigetPartByteArray(b, 16, 17)) / (short) 10);
				GlobalData.qua = (short) (MyTools.twoBytesToShort(MyTools
						.nigetPartByteArray(b, 18, 19)) / (short) 10);
				double centerFreq = MyTools.byteToDouble(MyTools
						.getPartByteArray(b, 20, 27));

				// SinglefrequencyDFActivity.handler.sendEmptyMessage(0x8);
				break;
			case 36:// 频谱数据
				// byte[] pinpu=new byte[b.length-9];
				// System.arraycopy(b, 0, pinpu, 0, pinpu.length);
				// Parse.parsePinPu(pinpu);
				Parse.parseNewPinPu(b);
				// SinglefrequencyDFActivity.handler.sendEmptyMessage(0x9);
				break;
			}
		}
	}

	private static short twoBytesToShort(byte[] b) {
		return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
	}

	private static float fourBytesToFloat(byte[] b) {
		// 4 bytes
		int accum = 0;
		for (int shiftBy = 0; shiftBy < 4; shiftBy++) {
			accum |= (b[shiftBy] & 0xff) << shiftBy * 8;
		}
		return Float.intBitsToFloat(accum);
	}

	private static byte[] float2byte(float f) {
		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (fbit >> (24 - i * 8));
		}

		// 翻转数组
		int len = b.length;
		// 建立一个与源数组元素类型相同的数组
		byte[] dest = new byte[len];
		// 为了防止修改源数组，将源数组拷贝一份副本
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// 将顺位第i个与倒数第i个交换
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}
		return dest;
	}

	public static void parseDDF(byte[] b) {
		int available = b.length;
		int index = 0;

		int fullframelength = 0;
		Log.d("xiaoxiao", String.valueOf(MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, index,
				index + 3))));
		while (index < available - 2
				&& MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, index,
						index + 3)) == 0xEEEEEEEE) {   //解析判断是否是正确的包头
			if (b[index + 8] == 46) {
				int templength = MyTools.fourBytesToInt(MyTools
						.nigetPartByteArray(b, index + 9, index + 12));// 具体帧的长度
				fullframelength = templength + 13;
				switch (b[index + 13]) {
				case 31:// 电平
					byte[] temp = new byte[] { b[23 + index], b[22 + index] };
					GlobalData.DDFdianping = (short) (Parse
							.twoBytesToShort(temp) / 10);
					SinglefrequencyDFActivity.handler
							.sendEmptyMessage(SinglefrequencyDFActivity.PARTFRESH);
                    Log.d("xiaochixu", "电平");
					break;
				case 37:// 示向度
					GlobalData.north = (short) (MyTools.twoBytesToShort(MyTools
							.nigetPartByteArray(b, index + 14, index + 15)) / (short) 10);
					GlobalData.xiangdui = (short) (MyTools
							.twoBytesToShort(MyTools.nigetPartByteArray(b,
									index + 16, index + 17)) / (short) 10);
					GlobalData.qua = (short) (MyTools.twoBytesToShort(MyTools
							.nigetPartByteArray(b, index + 18, index + 19)) / (short) 10);
					double centerFreq = MyTools.byteToDouble(MyTools
							.getPartByteArray(b, index + 20, index + 27));
					if (SinglefrequencyDFActivity.handler != null)
						SinglefrequencyDFActivity.handler
								.sendEmptyMessage(SinglefrequencyDFActivity.PARTFRESH);
					if (MapActivity.handler != null) {
						MapActivity.handler
								.sendEmptyMessage(MapActivity.SHISHIHUAXIAN);
					}
                    Log.d("xiaochixu", "是想赌");
					break;
				case 36:// 频谱数据
					GlobalData.startfreq = MyTools.byteToDouble(MyTools
							.getPartByteArray(b, index + 14, index + 21));
					GlobalData.endfreq = MyTools.byteToDouble(MyTools
							.getPartByteArray(b, index + 22, index + 29));
					GlobalData.count = MyTools.fourBytesToInt(MyTools
							.nigetPartByteArray(b, index + 30, index + 33));
					// GlobalData.bujin=(GlobalData.startfreq-GlobalData.endfreq)/GlobalData.count;
					short[] save = new short[GlobalData.count];// 即频谱数据
					int n = 0;
					for (int i = index + 34; i < index + fullframelength - 1
							&& n < GlobalData.count; i = i + 2) {
						byte[] temps = new byte[] { b[i + 1], b[i] };
						save[n] = (short) (MyTools.twoBytesToShort(temps) / 10);//byte[]转为short[]数据,长度减半了

						n++;
					}
					GlobalData.pinpu = save;
					SinglefrequencyDFActivity.handler
							.sendEmptyMessage(SinglefrequencyDFActivity.NETREFRESH);
                    Log.d("xiaochixu", "频谱");
					break;
				}
			}
			index = index + fullframelength;
		}
	}

	public static void parseDDFFull(byte[] b) {
		int all = b.length;
		if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, 0, 3)) == 0xEEEEEEEE)// 如果是帧头
		{
			GlobalData.framelength = MyTools.fourBytesToInt(MyTools
					.nigetPartByteArray(b, 4, 7));
			if (all - 8 == GlobalData.framelength)// 正好是一个完整的帧
			{
				parseDDF(b);
			} else if (all - 8 < GlobalData.framelength)// 这个帧不够，还有后续部分没有到来
			{
				GlobalData.withFrameHeadBuffer = new byte[GlobalData.framelength + 8];
				System.arraycopy(b, 0, GlobalData.withFrameHeadBuffer, 0, all);
			}
		} 
		else{// 如果不是帧头，说明是前一帧的后续部分
		
			if (GlobalData.withFrameHeadBuffer != null) {

			}
		}
	}

	// 重写于2014.10.23
	public static void newParseUnManServer(byte[] b) throws Exception {
		int available = b.length;
		int index = 0;
		int fullframelength = 0;
		while (index < available - 3) {
			if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, index,
					index + 3)) == 0xEEEEEEEE)// 如果是帧头
			{
				int templength = MyTools.fourBytesToInt(MyTools
						.nigetPartByteArray(b, index + 4, index + 7));// 具体帧的长度，即帧头后的长度
				fullframelength = templength + 8;
				if (available - index >= fullframelength)// 说明当前帧是完整的
				{
					UnManServer unmanserver = new UnManServer();
					unmanserver.name = new String(MyTools.getPartByteArray(b,
							index + 13, index + 48), "UTF-16LE");
					unmanserver.id = new String(MyTools.getPartByteArray(b,
							index + 49, index + 124), "UTF-16LE");
					unmanserver.parentId = new String(MyTools.getPartByteArray(
							b, index + 125, index + 200), "UTF-16LE");
					short infolength = MyTools.twoBytesToShort(new byte[] {
							b[index + 202], b[index + 201] });
					short infolengths = MyTools.twoBytesToShort(new byte[] {
							b[index + 201], b[index + 202] });
					if (infolength != 0) {
						unmanserver.sonstationIdArray = new String(
								MyTools.getPartByteArray(b, index + 203, index
										+ 202 + infolength)).split("\\|");// 特别注意：第一个值可能是空的
					}
					
					GlobalData.unmanServerHashMap.put(unmanserver.id,unmanserver);
					int stationslength = fullframelength - (203 + infolength);// 该服务器上所有无人站数据的总长度

					if (stationslength != 0)// 说明有无人站挂载
					{
						int havelength = 0;// 已经解析了的字节数。此处该变量每次涨值都是一个无人站的字节数。
						while ((index + 203 + infolength + havelength) < (index + fullframelength)) {
							int currentstationlength = MyTools
									.fourBytesToInt(MyTools.nigetPartByteArray(
											b, index + 203 + infolength
													+ havelength, index + 206
													+ infolength + havelength));
							UnManStation station = new UnManStation();
							station.id = new String(MyTools.getPartByteArray(b,
									index + 207 + infolength + havelength,
									index + 282 + infolength + havelength),
									"UTF-16LE");
							station.name = new String(MyTools.getPartByteArray(
									b, index + 283 + infolength + havelength,
									index + 318 + infolength + havelength),
									"UTF-16LE");
							String lantemp = df
									.format(MyTools.byteToDouble(MyTools
											.getPartByteArray(b, index + 319
													+ infolength + havelength,
													index + 326 + infolength
															+ havelength)));// 纬度
							station.lan = Double.valueOf(lantemp);
							String lontemp = df
									.format(MyTools.byteToDouble(MyTools
											.getPartByteArray(b, index + 327
													+ infolength + havelength,
													index + 334 + infolength
															+ havelength)));// 纬度
							station.lon = Double.valueOf(lontemp);
							station.isavailable = b[index + 335 + infolength
									+ havelength];
							station.iskongtiao = b[index + 336 + infolength
									+ havelength];
							station.switcharray = new String(
									MyTools.getPartByteArray(b, index + 337
											+ infolength + havelength, index
											+ 336 + infolength + havelength
											+ currentstationlength - 130),
									"UTF-16LE").split("\\|");
							station.server = unmanserver.name;
							GlobalData.unmanHashMap.put(station.id, station);
							havelength = havelength + currentstationlength + 4;
						}
					}
					index = index + fullframelength;
					// UnmanedStationsActivity.handler.sendEmptyMessage(UnmanedStationsActivity.UNMANDATA);
				} else// 当前帧是不完整的
				{
					GlobalData.unmanbuffer = new byte[fullframelength];
					Log.i("当前帧不完整", "帧长" + fullframelength + " ");
					System.arraycopy(b, index, GlobalData.unmanbuffer, 0,
							available - index);
					Log.i("不完整帧当前索引", GlobalData.bufferIndex + "  ");
					GlobalData.unmanbufferindex = available - index;
					Log.i("不完整帧当前最新索引", GlobalData.bufferIndex + "  ");
					index = available - 1;
				}
			} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
					index, index + 3)) != 0xEEEEEEEE
					&& GlobalData.unmanbuffer != null)// 如果不是帧头并且GlobalData.pinduanbuffer不为空，说明是前一帧的后续
			{
				Log.i("后续部分到了", "出发");
				int templength = Math.min(GlobalData.unmanbuffer.length
						- GlobalData.unmanbufferindex, b.length);
				System.arraycopy(b, 0, GlobalData.unmanbuffer,
						GlobalData.unmanbufferindex, templength);
				GlobalData.unmanbufferindex = GlobalData.unmanbufferindex
						+ templength;
				if (GlobalData.unmanbufferindex == GlobalData.unmanbuffer.length) {
					Parse.newParseUnManServer(GlobalData.unmanbuffer);
					GlobalData.unmanbuffer = null;
					GlobalData.unmanbufferindex = 0;
				}
				index = index + templength;
			} else if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b,
					index, index + 3)) != 0xEEEEEEEE
					&& GlobalData.unmanbuffer == null)// 如果不是帧头并且GlobalData.unmanbuffer为空，说明有漏帧,需要抛弃部分数据
			{
				Log.i("有漏帧", "发生");
				int m = 0;
				for (m = 0; m < available - 3; m++) {
					if (MyTools.fourBytesToInt(MyTools.nigetPartByteArray(b, m,	m + 3)) == 0xEEEEEEEE) {
						break;
					}
				}
				m = m - 1;
				byte[] regment = new byte[b.length - m];
				System.arraycopy(b, m, regment, 0, b.length - m);
				Parse.newParseUnManServer(b);
				index = m;
			}
		}
	}
}
