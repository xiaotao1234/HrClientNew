package com.huari.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.huari.client.R;

import java.util.ArrayList;
import java.util.List;

public class ExtendableListViewAdapter extends BaseExpandableListAdapter {
    String[] strings = {"未定义", "正在下载", "等待下载", "已暂停", "完成", "校验失败", "网络异常", "读写异常", "wifi网络异常", "数据错误，需重新下载", "完成"};

    public String[] groupString = {"下载管理", "城市列表"};

    public void setChildString(List<List> childString, ArrayList<ArrayList<MKOLSearchRecord>> cityRecord) {
        this.childString = childString;
        this.cityRecord = cityRecord;
    }

    public List<List> childString;
    ArrayList<ArrayList<MKOLSearchRecord>> cityRecord;


    @Override
    public int getGroupCount() {
        return groupString.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childString.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return childString.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childString.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.download, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(groupString[groupPosition]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (groupPosition == 0) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_manager, parent, false);
                childViewHolder = new ChildViewHolder(convertView);
                convertView.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }
            childViewHolder.progressBar.setVisibility(View.VISIBLE);
            childViewHolder.status.setVisibility(View.VISIBLE);
            childViewHolder.size.setVisibility(View.VISIBLE);
            MKOLUpdateElement e = (MKOLUpdateElement) childString.get(groupPosition).get(childPosition);
            childViewHolder.tvTitle.setText(e.cityName);
            childViewHolder.size.setText(formatDataSize(e.size));
            childViewHolder.status.setText(strings[e.status]);
//            if(e.status>4&&e.status<10){
//                childViewHolder.status.setTextColor(Color.parseColor("#FF0000"));
//            }
            if (e.status == 4 || e.status == 10) {
                childViewHolder.progressBar.setVisibility(View.INVISIBLE);
            } else {
                childViewHolder.progressBar.setProgress(e.ratio);
            }
            childViewHolder.relativeLayout.setOnClickListener(v -> {
                if (section1Listener != null) {
                    section1Listener.click(e.cityName, e.status,e.cityID);
                }
            });
            return convertView;
        }
        if (groupPosition == 1) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_manager, parent, false);
                childViewHolder = new ChildViewHolder(convertView);
                convertView.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }
//            MKOLUpdateElement e = (MKOLUpdateElement) childString.get(groupPosition).get(childPosition);
            childViewHolder.progressBar.setVisibility(View.GONE);
            childViewHolder.status.setVisibility(View.GONE);
            childViewHolder.size.setVisibility(View.GONE);
//            if(cityRecord.get(childPosition).size()==0){
//                childViewHolder.imageView.setVisibility(View.VISIBLE);
//            }
            childViewHolder.tvTitle.setText((CharSequence) childString.get(groupPosition).get(childPosition));
            childViewHolder.relativeLayout.setOnClickListener(v -> {
                if (downloadListener != null) {
//                    section1Listener.click(e.cityName, e.status);
                    downloadListener.download(childPosition);
                }
            });
            return convertView;
        }
        return null;
    }

    public interface Section1Listener {
        void click(String s, int status,int id);
    }

    Section1Listener section1Listener;

    public interface DownloadListener {
        void download(int position);
    }

    DownloadListener downloadListener;

    public void setSection1Listener(Section1Listener section1Listener,DownloadListener downloadListener) {
        this.section1Listener = section1Listener;
        this.downloadListener = downloadListener;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupViewHolder {
        TextView tvTitle;

        public GroupViewHolder(View view) {
            tvTitle = (view).findViewById(R.id.group_text);
        }
    }

    static class ChildViewHolder {
        TextView tvTitle;
        TextView size;
        TextView status;
        ProgressBar progressBar;
        ImageView imageView;
        RelativeLayout relativeLayout;

        public ChildViewHolder(View view) {
            relativeLayout = view.findViewById(R.id.lin);
            this.tvTitle = view.findViewById(R.id.city_name);
            this.size = view.findViewById(R.id.all_size);
            this.status = view.findViewById(R.id.download_status);
            this.progressBar = view.findViewById(R.id.probar);
            imageView = view.findViewById(R.id.expand);
        }
    }

    public String formatDataSize(long size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }
}
