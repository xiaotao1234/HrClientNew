package com.huari.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.huari.client.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItuAdapterOfListView extends BaseAdapter {

	HashMap<String, String> itumap;
	ArrayList<String> key;
	ArrayList<String> value;
	Iterator<String> it;
	private LayoutInflater mInflater;

	static class ViewHolder {
		TextView numtv, keytv, valuetv;
	}

	public ItuAdapterOfListView(Context con, HashMap<String, String> map) {
		this.itumap = map;
		key = new ArrayList<String>();
		value = new ArrayList<String>();
		mInflater = LayoutInflater.from(con);
	}

	@Override
	public int getCount() {
		key.clear();
		value.clear();
		it = itumap.keySet().iterator();
		while (it.hasNext()) {
			String t = (String) it.next();
			key.add(t);
			value.add(itumap.get(t));
		}
		return Math.max(15, itumap.size());
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder vh = null;
		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.pagerthreetext, null);
			vh = new ViewHolder();
			vh.numtv = (TextView) arg1.findViewById(R.id.itunum);
			vh.keytv = (TextView) arg1.findViewById(R.id.itukey);
			vh.valuetv = (TextView) arg1.findViewById(R.id.ituvalue);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		if (arg0 < itumap.size()) {
			vh.numtv.setText(arg0 + 1 + "");
			vh.keytv.setText(key.get(arg0));
			vh.valuetv.setText(value.get(arg0));
		} else {
			vh.numtv.setText("");
			vh.keytv.setText("");
			vh.valuetv.setText("");
		}
		return arg1;
	}

}
