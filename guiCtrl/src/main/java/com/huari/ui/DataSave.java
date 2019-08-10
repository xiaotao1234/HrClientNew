package com.huari.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DataSave {

	public static HashMap<Float, MyData> datamap = new HashMap<Float, MyData>();// 以正北角度作为key，其余信息则保存在MyData实例中
	public static float MaxPlidegree;// 最大幅度的角度
	public static float MaxProdegree;
	public static float MaxQuadegree;
	public static int maxcount;// 出现次数最多的那个角度出现的次数
	public static int maxpli;
	public static int maxqua;
	public static int sum;// 总共已经接收到了sum次波
	public static int pinduanshowpointcounts = 500;

	public static void clear() {
		datamap.clear();
		MaxPlidegree = MaxProdegree = MaxQuadegree = maxcount = maxpli = maxqua = sum = 0;
	}

	public static ArrayList<Map.Entry<Float, MyData>> sortByPro(
			HashMap<Float, MyData> map) {// 按概率排序
		ArrayList<Map.Entry<Float, MyData>> list = new ArrayList<Map.Entry<Float, MyData>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Float, MyData>>() {
			public int compare(Map.Entry<Float, MyData> o1,
					Map.Entry<Float, MyData> o2) {
				return (o2.getValue().count - o1.getValue().count);
			}
		});
		return list;
	}

	public static ArrayList<Map.Entry<Float, MyData>> sortByPli(
			HashMap<Float, MyData> map) {// 按幅度排序
		ArrayList<Map.Entry<Float, MyData>> list = new ArrayList<Map.Entry<Float, MyData>>(
				map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Float, MyData>>() {
			public int compare(Map.Entry<Float, MyData> o1,
					Map.Entry<Float, MyData> o2) {
				return (o2.getValue().maxplitude - o1.getValue().maxplitude);
			}
		});

		return list;
	}

	public static ArrayList<Map.Entry<Float, MyData>> sortByQua(
			HashMap<Float, MyData> map) {// 按质量排序
		ArrayList<Map.Entry<Float, MyData>> list = new ArrayList<Map.Entry<Float, MyData>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Float, MyData>>() {
			public int compare(Map.Entry<Float, MyData> o1,
					Map.Entry<Float, MyData> o2) {
				return (o2.getValue().maxquality - o1.getValue().maxquality);
			}
		});
		return list;
	}

}
