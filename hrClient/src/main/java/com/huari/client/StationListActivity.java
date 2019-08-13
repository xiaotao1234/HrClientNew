package com.huari.client;

import com.huari.dataentry.GlobalData;
import com.huari.dataentry.LogicParameter;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Station;
import com.huari.tools.SysApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StationListActivity extends Activity {

	public static int REFRESHSTATE = 0x1;

	ExpandableListView mylistview;
	String functionname;
	String logicPPFXId;// 频谱分析
	String logicPDSMId;// 频段扫描
	String logicDPCXId;// 单频测向
	public static Handler handler;
	String from;// FUN,功能；map，地图。以此判断是从功能导航界面进入还是从地图进入。

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_list);
		SysApplication.getInstance().addActivity(this);
		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
		mylistview = (ExpandableListView) findViewById(R.id.listexpandablelistview);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		from = bundle.getString("from");
		if (from.equals("FUN")) {
			functionname = bundle.getString("functionname");
		}

		final MyExpandableListAdapter myadapter = new MyExpandableListAdapter();
		mylistview.setGroupIndicator(null);
		mylistview.setAdapter(myadapter);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x1) {
					myadapter.notifyDataSetChanged();
				}
			}

		};
	}

	class MyExpandableListAdapter extends BaseExpandableListAdapter {

		private Station[] stationObjectArray;
		private MyDevice[][] deviceObjectArray;

		public void iniadapter() {
			try {
				// 将各个台站和设备存进数组
				stationObjectArray = new Station[GlobalData.stationHashMap
						.size()];
				deviceObjectArray = new MyDevice[GlobalData.stationHashMap
						.size()][];
				int i = 0;
				for (String sk : GlobalData.stationHashMap.keySet()) {
					Station sta = GlobalData.stationHashMap.get(sk);
					stationObjectArray[i] = sta;

					MyDevice[] my = new MyDevice[sta.showdevicelist.size()];
					int y = 0;
					for (MyDevice devi : sta.showdevicelist) {
						my[y] = devi;
						y++;
					}
					deviceObjectArray[i] = my;
					i++;
				}
			} catch (Exception e) {
				System.out.println("初始化StationListActivity中的Adapter时出现错误");
			}
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return deviceObjectArray[groupPosition][childPosition];
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final int m = groupPosition;
			final int n = childPosition;
			MyDevice curDevice = deviceObjectArray[groupPosition][childPosition];
			final MyDevice temp = curDevice;
			LinearLayout linearview = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.deviceitem, null);
			TextView tv = (TextView) linearview
					.findViewById(R.id.deviceitemtextview);
			if (curDevice.isOccupied == 1) {
				tv.setText(curDevice.name + "(正在使用)");
			} else if (curDevice.isOccupied == 0) {
				tv.setText(curDevice.name + "(空闲)");
			}
			if (curDevice.state == 1) {
				tv.setText(curDevice.name + "(故障)");
			}
			Button bn = (Button) linearview.findViewById(R.id.deviceitembutton);
			if (from.equals("FUN")
					&& (functionname.equals("频谱分析")
							|| functionname.equals("单频测向") || functionname
								.equals("频段扫描"))) {
				// spinner.setVisibility(View.INVISIBLE);
				bn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String[] functionArray = new String[deviceObjectArray[m][n].logic
								.size()];
						int f1 = 0;
						for (String ffff : deviceObjectArray[m][n].logic
								.keySet()) {
							LogicParameter llp = deviceObjectArray[m][n].logic
									.get(ffff);
							functionArray[f1] = llp.type;
							if (llp.type.startsWith("L")) {
								logicPPFXId = llp.id;       //频谱分析
							} else if (llp.type.startsWith("S")) {
								logicPDSMId = llp.id;		//频段扫描
							} else if (llp.type.startsWith("D")) {
								logicDPCXId = llp.id;		//单频测向
							}
							f1++;
						}
						if (functionname.equals("频谱分析")) {
							boolean have = false;
							float centerFreq = 0f;// 中心频率，单位为MHz
							float demodulationSpan = 0f;// 频率带宽，单位为KHz
							float stepFreq = 0f;// 频率步进，单位KHz
							for (String h : functionArray) {
								if (h.startsWith("LEVE")) {
									have = true;
									break;
								}
							}
							if (!have) {
								Toast.makeText(getApplicationContext(),
										"该设备无此功能", Toast.LENGTH_SHORT).show();
							} else {

								Intent intent = new Intent();
								intent.setAction("function0");
								Bundle bundle = new Bundle();
								bundle.putString("devicename",
										deviceObjectArray[m][n].name);
								bundle.putString("stationname",
										stationObjectArray[m].name);
								bundle.putString("stationKey",
										stationObjectArray[m].id);
								bundle.putString("lid", logicPPFXId);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						} else if (functionname.equals("单频测向")) {
							boolean have = false;
							for (String h : functionArray) {
								if (h.startsWith("DDF")) {
									have = true;
									break;
								}
							}
							if (!have) {
								Toast.makeText(getApplicationContext(),
										"该设备无此功能", Toast.LENGTH_SHORT).show();
							} else {

								Intent intent = new Intent();
								intent.setAction("function12");
								Bundle bundle = new Bundle();
								bundle.putString("devicename",
										deviceObjectArray[m][n].name);
								bundle.putString("stationname",
										stationObjectArray[m].name);
								bundle.putFloat("lan",
										stationObjectArray[m].lan);
								bundle.putFloat("lon",
										stationObjectArray[m].lon);
								bundle.putString("stationKey",
										stationObjectArray[m].id);
								bundle.putString("lid", logicDPCXId);
								Log.i(deviceObjectArray[m][n].name + "   "
										+ stationObjectArray[m].name, "key"
										+ stationObjectArray[m].id
										+ "   logicID" + logicDPCXId);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						} else if (functionname.equals("频段扫描")) {
							boolean have = false;
							for (String h : functionArray) {
								if (h.startsWith("SCAN")) {
									have = true;
									break;
								}
							}
							if (!have) {
								Toast.makeText(getApplicationContext(),
										"该设备无此功能", Toast.LENGTH_SHORT).show();
							} else {
								Intent intent = new Intent();
								intent.setAction("function18");
								Bundle bundle = new Bundle();
								bundle.putString("devicename",
										deviceObjectArray[m][n].name);
								bundle.putString("stationname",
										stationObjectArray[m].name);
								bundle.putString("stationKey",
										stationObjectArray[m].id);
								bundle.putString("lid", logicPDSMId);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						}
					}
				});
			} else if (from.equals("map")) {
				bn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String[] functionArray = new String[deviceObjectArray[m][n].logic
								.size()];
						String locId = null;
						boolean have = false;
						int f1 = 0;
						for (String ffff : deviceObjectArray[m][n].logic
								.keySet()) {
							LogicParameter llp = deviceObjectArray[m][n].logic
									.get(ffff);
							if (llp.type.startsWith("D")) {
								have = true;
								locId = llp.id;
								break;
							}
						}

						if (!have) {
							Toast.makeText(getApplicationContext(), "该设备无此功能",
									Toast.LENGTH_SHORT).show();
						} else {
							Intent intent = new Intent(
									StationListActivity.this,
									FromMapActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("dname",
									deviceObjectArray[m][n].name);
							bundle.putString("sname",
									stationObjectArray[m].name);
							bundle.putString("stakey", stationObjectArray[m].id);
							bundle.putString("lids", locId);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					}
				});
			} else {
				bn.setVisibility(View.INVISIBLE);
			}
			return linearview;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return deviceObjectArray[groupPosition].length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return stationObjectArray[groupPosition];
		}

		@Override
		public int getGroupCount() {
			iniadapter();
			return stationObjectArray.length;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			LinearLayout lv = (LinearLayout) getLayoutInflater().inflate(
					R.layout.stationname, null);
			ImageView head = (ImageView) lv.findViewById(R.id.headimage);
			if (isExpanded == false) {
				head.setImageResource(R.drawable.up);
			} else {
				head.setImageResource(R.drawable.down);
			}
			TextView tview = (TextView) lv.findViewById(R.id.groupname);
			tview.setTextColor(Color.CYAN);
			tview.setTextSize(20);
			tview.setText(((Station) getGroup(groupPosition)).name);
			return lv;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
