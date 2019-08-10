package com.huari.ui;

import java.util.ArrayList;
import java.util.Map;
import com.huari.diskactivity.R;

import android.content.Context;
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

public class FourModeView extends LinearLayout {
	View bottomView, headView;
	com.huari.ui.SingDrawWave singdrawwave;
	Handler handler;
	MyListViewAdapter cusadapter;
	LinearLayout withlistview;
	ListView mylistview;
	TextView shixiangdu;// 触摸时，显示触摸点的角度
	TextView tabletitlesecond, tabletitlethird, headshowtjmode, headshowmax;
	String tjmode;// 统计模式
	boolean zhengbeiboolean;
	boolean graphshowboolean;

	public FourModeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ini();
	}

	public FourModeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ini();
	}

	public FourModeView(Context context) {
		super(context);
		ini();
	}

	public void clear() {
		DataSave.clear();
		headshowmax.setText("");
		singdrawwave.postInvalidate();
		cusadapter.notifyDataSetChanged();
	}

	public void setTableTitleSecond(String s) {
		tabletitlesecond.setText(s);
	}

	public void setTableTitleThird(String s) {
		tabletitlethird.setText(s);
	}

	public void tongjimodeswitch(String tjmodename) {
		tjmode = tjmodename;
		if (tjmode.equals("fudu")) {
			headshowtjmode.setText("按最大幅度统计");
			tabletitlesecond.setText("幅度（dBuV）");
		} else if (tjmode.equals("gailv")) {
			headshowtjmode.setText("按最大概率统计");
			tabletitlesecond.setText("概率（%）");
		} else if (tjmode.equals("zhiliang")) {
			headshowtjmode.setText("按最高质量统计");
			tabletitlesecond.setText("质量（%）");
		}
		if (graphshowboolean) {
			singdrawwave.setThreeMode(tjmodename);
			cusadapter.notifyDataSetChanged();
		} else {
			cusadapter.notifyDataSetChanged();
			singdrawwave.setThreeMode(tjmodename);
		}
	}

	public void setGraphModeShow(boolean graphmode) {
		graphshowboolean = graphmode;
		singdrawwave.postInvalidate();
		cusadapter.notifyDataSetChanged();
		if (graphshowboolean) {
			singdrawwave.setVisibility(View.VISIBLE);
			withlistview.setVisibility(View.INVISIBLE);
		} else {
			singdrawwave.setVisibility(View.INVISIBLE);
			withlistview.setVisibility(View.VISIBLE);
		}
	}

	public void setNorthBoolean(boolean northboolean) {
		zhengbeiboolean = northboolean;
		singdrawwave.setNorthBoolean(northboolean);
		cusadapter.notifyDataSetChanged();
		if (northboolean) {
			tabletitlethird.setText("正北示向度");
		} else {
			tabletitlethird.setText("相对示向度");
		}
	}

	private void ini() {
		zhengbeiboolean = true;
		tjmode = "fudu";
		graphshowboolean = true;
		setOrientation(VERTICAL);
		headView = LayoutInflater.from(getContext()).inflate(
				R.layout.headlinearlayout, null);
		bottomView = LayoutInflater.from(getContext()).inflate(
				R.layout.framelayout, null);// 是一个FrameLayout
		mylistview = (ListView) bottomView.findViewById(R.id.listview);
		withlistview = (LinearLayout) bottomView.findViewById(R.id.listlinear);// 切换
		tabletitlesecond = (TextView) withlistview.findViewById(R.id.innertv2);
		tabletitlethird = (TextView) withlistview.findViewById(R.id.innertv3);
		headshowtjmode = (TextView) headView.findViewById(R.id.tongjimodename);
		headshowmax = (TextView) headView.findViewById(R.id.tv1);
		shixiangdu = (TextView) headView.findViewById(R.id.tv2);
		singdrawwave = (SingDrawWave) (bottomView.findViewById(R.id.mynewview));// 切换
		singdrawwave.setTextView(shixiangdu);
		singdrawwave.setThreeMode(tjmode);
		singdrawwave.setNorthBoolean(zhengbeiboolean);

		if (tjmode.equals("fudu")) {
			tabletitlesecond.setText("幅度（dBuV）");
			headshowtjmode.setText("按最大幅度统计");
		} else if (tjmode.equals("zhiliang")) {
			tabletitlesecond.setText("质量（%）");
			headshowtjmode.setText("按最高质量统计");
		} else if (tjmode.equals("gailv")) {
			tabletitlesecond.setText("概率（%）");
			headshowtjmode.setText("按最大概率统计");
		}
		
		if (zhengbeiboolean) {
			tabletitlethird.setText("正北示向度");
		} else {
			tabletitlethird.setText("相对示向度");
		}
		
		if (graphshowboolean) {
			singdrawwave.setVisibility(View.VISIBLE);
			withlistview.setVisibility(View.INVISIBLE);
		} else {
			singdrawwave.setVisibility(View.INVISIBLE);
			withlistview.setVisibility(View.VISIBLE);
		}
		
		cusadapter = new MyListViewAdapter();
		mylistview.setAdapter(cusadapter);

		addView(headView);
		addView(bottomView);

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 0x16) {
					try {
						if (graphshowboolean) {
							singdrawwave.postInvalidate();
						} else {
							cusadapter.notifyDataSetChanged();
						}
						;

						if (tjmode.equals("fudu")) {
							headshowmax
									.setText("最优值( 正北"
											+ DataSave.MaxPlidegree
											+ "° / "
											+ "相对"
											+ DataSave.datamap
													.get(DataSave.MaxPlidegree).reldegree
											+ "° )");

						}
						else if (tjmode.equals("zhiliang")) {
							headshowmax
									.setText("最优值( 正北"
											+ DataSave.MaxQuadegree
											+ "° / "
											+ "相对"
											+ DataSave.datamap
													.get(DataSave.MaxQuadegree).reldegree
											+ "° )");
						} else if (tjmode.equals("gailv")) {
							headshowmax
									.setText("最优值( 正北"
											+ DataSave.MaxProdegree
											+ "° / "
											+ "相对"
											+ DataSave.datamap
													.get(DataSave.MaxProdegree).reldegree
											+ "° )");
						}
					} catch (Exception e) {

					}
				}

			};
		};
	}

	public void refresh() {
		Message msg = new Message();
		msg.what = 0x16;
		handler.sendMessage(msg);
	}

	class MyListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return DataSave.datamap.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View line = LayoutInflater.from(getContext()).inflate(
					R.layout.table, null);
			TextView tvs1 = (TextView) line.findViewById(R.id.t1);// 用来显示序号
			TextView tvs2 = (TextView) line.findViewById(R.id.t2);
			TextView tvs3 = (TextView) line.findViewById(R.id.t3);
			tvs1.setText(position + 1 + "");
			
			if (tjmode.equals("fudu")) {
				ArrayList<Map.Entry<Float, MyData>> list = DataSave
						.sortByPli(DataSave.datamap);
				tvs2.setText(((Map.Entry<Float, MyData>) list.get(position))
						.getValue().maxplitude + "");
				if (zhengbeiboolean) {
					tvs3.setText(((Map.Entry<Float, MyData>) list.get(position))
							.getKey() + "°");
				} else {
					tvs3.setText(((Map.Entry<Float, MyData>) list.get(position))
							.getValue().reldegree + "°");
				}
			} else if (tjmode.equals("zhiliang")) {
				ArrayList<Map.Entry<Float, MyData>> list = DataSave
						.sortByQua(DataSave.datamap);
				tvs2.setText(((Map.Entry<Float, MyData>) list.get(position))
						.getValue().maxquality + "");
				if (zhengbeiboolean) {
					tvs3.setText(((Map.Entry<Float, MyData>) list.get(position))
							.getKey() + "°");
				} else {
					tvs3.setText(((Map.Entry<Float, MyData>) list.get(position))
							.getValue().reldegree + "°");
				}
			} else if (tjmode.equals("gailv")) {
				ArrayList<Map.Entry<Float, MyData>> list = DataSave
						.sortByPro(DataSave.datamap);
				float g = Math.round(((Map.Entry<Float, MyData>) list
						.get(position)).getValue().count
						/ (float) (DataSave.sum) * 100) / 100f;
				tvs2.setText(g + "");
				if (zhengbeiboolean) {
					tvs3.setText(((Map.Entry<Float, MyData>) list.get(position))
							.getKey() + "°");
				} else {
					tvs3.setText(((Map.Entry<Float, MyData>) list.get(position))
							.getValue().reldegree + "°");
				}
			}
			return line;
		}

	}
}
