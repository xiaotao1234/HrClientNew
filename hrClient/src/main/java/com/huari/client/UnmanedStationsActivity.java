package com.huari.client;

import java.util.ArrayList;

import struct.JavaStruct;
import struct.StructException;

import com.huari.commandstruct.UnManStationRequest;
import com.huari.dataentry.GlobalData;
import com.huari.dataentry.UnManStation;
import com.huari.service.MainService;
import com.huari.tools.MyTools;
import com.huari.tools.SysApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
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

public class UnmanedStationsActivity extends Activity {

	public static int UNMANDATA = 0x9;

	private void setButtonEnable(Button bn1, Button bn2, Button bn3,
			boolean b1, boolean b2) {
		bn1.setEnabled(b1);
		bn2.setEnabled(b2);
		bn3.setEnabled(b2);
	}

	class SwitchButtonOnClickListener implements OnClickListener {
		byte[] request = null;
		UnManStationRequest usr = null;

		public SwitchButtonOnClickListener(String stationId, byte functionNum,
				byte onOroff, String switchName) {
			usr = new UnManStationRequest();
			usr.functionNum = functionNum;
			usr.stationid = MyTools.toCountString(stationId.trim(), 76)
					.getBytes();
			usr.onoroff = onOroff;
			usr.switchname = switchName.getBytes();
			usr.framelength = 77 + usr.switchname.length;
			usr.length = usr.framelength + 5;
			try {
				request = JavaStruct.pack(usr);
				System.out.println(functionNum + "   唤醒、关闭或强制关闭命令组装完成");
			} catch (StructException e) {
				e.printStackTrace();
				Log.i("转换为byte[]时", "发生了异常");
			}
		}

		@Override
		public void onClick(View arg0) {
			MainService.send(request);
			System.out.println(usr.functionNum + "   唤醒、关闭或强制关闭命令发送成功");
		}

	}

	private LinearLayout returnChildLinearLayout(String s, boolean available,
			UnManStation uas) {
		LinearLayout linear = (LinearLayout) getLayoutInflater().inflate(
				R.layout.unmanchildlineaylayout, null);
		Button bn1 = linear.findViewById(R.id.unmanbn5);
		Button bn2 = linear.findViewById(R.id.unmanbn6);
		bn1.setText(s + "(开)");
		bn2.setText(s + "(关)");

		bn1.setOnClickListener(new SwitchButtonOnClickListener(uas.id,
				(byte) 78, (byte) 0, s));
		bn2.setOnClickListener(new SwitchButtonOnClickListener(uas.id,
				(byte) 78, (byte) 1, s));

		bn1.setEnabled(available);
		bn2.setEnabled(available);

		return linear;
	}

	ExpandableListView expandableListView;
	String[] stationname;
	String[] servername;
	String[] info;
	String[] state;
	NoManStationExpandableListAdapter adapter;
	public static Handler handler;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_unmaned_stations);
		SysApplication.getInstance().addActivity(this);
		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
		expandableListView = findViewById(R.id.nomanexpandedview);
		expandableListView.setGroupIndicator(null);
		adapter = new NoManStationExpandableListAdapter();
		expandableListView.setAdapter(adapter);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == UNMANDATA) {
					adapter.notifyDataSetChanged();
				}
			}
		};

	}

	class NoManStationExpandableListAdapter extends BaseExpandableListAdapter {

		ArrayList<UnManStation> list = new ArrayList<UnManStation>();

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return groupPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			LinearLayout child = (LinearLayout) getLayoutInflater().inflate(
					R.layout.newnomanchilditem, null);
			TextView tail = child.findViewById(R.id.nomaninfo);
			LinearLayout head = child
					.findViewById(R.id.nomanSwitch);
			TextView lan = child.findViewById(R.id.unmanlan);
			TextView lon = child.findViewById(R.id.unmanlon);
			TextView support = child.findViewById(R.id.unmansupport);
			Button bn = child.findViewById(R.id.unmanon);// 唤醒
			Button bn1 = child.findViewById(R.id.unmanoff);// 关闭无人站
			Button bn2 = child.findViewById(R.id.unmanforceon);// 强制关闭无人站

			UnManStation uas = list.get(groupPosition);

			bn.setOnClickListener(new SwitchButtonOnClickListener(uas.id,
					(byte) 75, (byte) 0, "NULL"));
			bn1.setOnClickListener(new SwitchButtonOnClickListener(uas.id,
					(byte) 75, (byte) 1, "NULL"));
			bn2.setOnClickListener(new SwitchButtonOnClickListener(uas.id,
					(byte) 76, (byte) 1, "NULL"));

			lan.setText("经度:" + uas.lan + "");
			lon.setText("纬度:" + uas.lon + "");
			if (uas.iskongtiao == 0) {
				support.setText("支持空调");
			} else {
				support.setText("不支持空调");
			}
			boolean is = false;
			if (uas.isavailable == 1) {
				is = false;
				setButtonEnable(bn, bn1, bn2, false, false);
			} else if (uas.isavailable == 4) {
				is = true;
				setButtonEnable(bn, bn1, bn2, true, true);
			} else if (uas.isavailable == 6) {
				is = false;
				setButtonEnable(bn, bn1, bn2, true, false);
			} else if (uas.isavailable == 0) {
				is = true;
				setButtonEnable(bn, bn1, bn2, true, true);
			}
			for (String stemp : uas.switcharray) {
				head.addView(returnChildLinearLayout(stemp, is, uas));
			}
			tail.setText(uas.info);

			return child;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return list.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			list.clear();
			for (String id : GlobalData.unmanHashMap.keySet()) {
				UnManStation uman = GlobalData.unmanHashMap.get(id);
				list.add(uman);
			}
			return list.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			LinearLayout parentview = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.nomangroup, null);
			TextView groupnum = (TextView) parentview
					.findViewById(R.id.groupnum);
			TextView groupname = (TextView) parentview
					.findViewById(R.id.groupname);
			TextView stationservername = (TextView) parentview
					.findViewById(R.id.stationservername);
			ImageView imageview = (ImageView) parentview
					.findViewById(R.id.stationimageview);
			if (isExpanded) {
				imageview.setImageResource(R.drawable.more1);
			} else {
				imageview.setImageResource(R.drawable.more);
			}
			groupname.setText(list.get(groupPosition).name);
			stationservername.setText(list.get(groupPosition).server);
			// 本来isavailable只有0（在线）和1（掉线）两个值，但是无人站刷新时，状态包含掉线、断电、上电三种，需要根据这三种情况标示出
			// 他们的状态，所以把isavailable的值增加到了三个，用二进制表示分别为100（上电）、110（断电）、001（掉线，不在线），
			// 其中上电和断电都属于在线。当必须要用两个值来使用isavailable时，可以用字节值1来跟这三个值相与。
			if (list.get(groupPosition).isavailable == 1)// 不在线，掉线了
			{
				groupnum.setText(groupPosition + 1 + "(离线)");
			} else if (list.get(groupPosition).isavailable == 4) {
				groupnum.setText(groupPosition + 1 + "(上电)");
			} else if (list.get(groupPosition).isavailable == 6) {
				groupnum.setText(groupPosition + 1 + "(断电)");
			} else if (list.get(groupPosition).isavailable == 0) {
				groupnum.setText(groupPosition + 1 + "(在线可用)");
			}
			return parentview;
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
}
