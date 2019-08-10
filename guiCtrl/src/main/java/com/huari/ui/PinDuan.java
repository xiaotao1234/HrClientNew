package com.huari.ui;

import java.util.ArrayList;

import com.huari.diskactivity.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PinDuan extends LinearLayout {
	com.huari.ui.PinScanningShowWave pss;
	LinearLayout linearlayout;
	ArrayList<Integer> datalist;
	MyAdapter adapter;
	ListView listview;
	Handler handler;

	TextView daneiNameTextView;
	String showinfo = "幅度（dBuV）";

	public void setShowInfo(String s) {
		showinfo = s;
	}

	public PinDuan(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		ini();
	}

	public PinDuan(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		ini();
	}

	public PinDuan(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		ini();
	}

	public void setTiaoZhi(boolean booleanValue) {
		pss.setTiaoZhi(booleanValue);
	}

	public void hideTable(boolean hide) {
		if (hide) {
			linearlayout.setVisibility(View.GONE);
		} else {
			linearlayout.setVisibility(View.VISIBLE);
		}
	}

	private void ini() {
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundColor(Color.BLACK);

		linearlayout = (LinearLayout) LayoutInflater.from(getContext())
				.inflate(R.layout.pinduanlistview, null);
		daneiNameTextView = (TextView) linearlayout.findViewById(R.id.pl);
		listview = (ListView) linearlayout.findViewById(R.id.pinduanlistview);
		pss = new PinScanningShowWave(getContext());

		datalist = pss.datalist;

		LinearLayout.LayoutParams s1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
		LinearLayout.LayoutParams s2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);

		try {
			adapter = new MyAdapter();
		} catch (Exception e) {
		}
		;
		listview.setAdapter(adapter);

		addView(pss, s1);
		addView(linearlayout, s2);

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 0x1)
					pss.refreshListData();
				// daneiNameTextView.setText(showinfo);
				adapter.notifyDataSetChanged();
			};
		};

	}

	public void setM(short[] m) {
		pss.setM(m);
	}

	public void setMax(short[] max) {
		pss.setMax(max);
	}

	public void setMin(short[] min) {
		pss.setMin(min);
	}

	public void setAvg(short[] avg) {
		pss.setAvg(avg);
	}

	public void setParameters(float fl, float fh, float bujin) {
		pss.setF(fl, fh, bujin);
		pss.postInvalidate();
	}

	public void refreshWave() {
		pss.postInvalidate();
		// pss.notiRefresh();
	}

	public void refreshTable() {
		Message msg = new Message();
		msg.what = 0x1;
		handler.sendMessage(msg);
	}

	class MyAdapter extends BaseAdapter {

		int max;

		class ViewHolder {
			TextView tv1, tv2, tv3;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			max = Math.max(datalist.size(), 30);
			if (max < DataSave.pinduanshowpointcounts) {
				return max;
			} else {
				return 1;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder viewholder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.pinduanlistviewdata, null);
				viewholder = new ViewHolder();
				viewholder.tv1 = (TextView) convertView
						.findViewById(R.id.listxuhao);
				viewholder.tv2 = (TextView) convertView
						.findViewById(R.id.listpinlv);
				viewholder.tv3 = (TextView) convertView
						.findViewById(R.id.listfudu);
				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}
			// viewholder.tv1.setText((Integer)datalist.get(position)+"");
			if (max < 500) {
				if (position < datalist.size()) {
					viewholder.tv1.setText(position + 1 + "");
					viewholder.tv3.setText(pss.getMValue((Integer) datalist
							.get(position)) + "");
					viewholder.tv2.setText(pss.indextof((Integer) datalist
							.get(position)) + "");
				} else {
					viewholder.tv1.setText("");
					viewholder.tv2.setText("");
					viewholder.tv3.setText("");
				}

			}

			else {
				viewholder.tv2.setText("将导致效率严重降低");
				viewholder.tv1.setText("限值过低");

				viewholder.tv3.setText("请重调限值");
			}

			return convertView;

		}

	}

	public void setDanWei(String name, String danwei) {
		pss.setDanwei(name, danwei);
		showinfo = name + "(" + danwei + ")";
		clear();
	}

	public void clear() {
		setM(null);
		setMax(null);
		setMin(null);
		setAvg(null);
		datalist.clear();
		refreshTable();
	}
}
