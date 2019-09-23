package com.huari.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.adapter.SimpleTestAdapter;
import com.huari.adapter.TagCloudAdapter;
import com.huari.client.FileListActivity;
import com.huari.client.HistoryListActivity;
import com.huari.client.MonthDataActivity;
import com.huari.client.OfflineDownloadActivity;
import com.huari.client.R;
import com.huari.dataentry.recentContent;
import com.huari.tools.FileOsImpl;
import com.huari.ui.CalendarView;
import com.huari.ui.TagCloudView;
import com.huari.ui.statisticalGraph;

import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OfflineFragment2 extends Fragment {
    TagCloudView tagCloudView;
    RecyclerView rv;
    SimpleTestAdapter simpleTestAdapter;
    LinearLayout pinpulayout;
    LinearLayout danpinLayout;
    LinearLayout pinduanLayout;
    LinearLayout musicLayout;
    LinearLayout fileLayout;
    LinearLayout downMapLayout;
    com.huari.ui.pieLineView pieLineView;
    NestedScrollView nestedScrollView;

    TextView danpinSize;
    TextView pinduanSize;
    TextView pinpuSize;
    TextView yinpinSize;
    TextView danpinMem;
    TextView pinpuMem;
    TextView pinduanMem;
    TextView yinpinMem;
    TextView danpinNew;
    TextView pinduanNew;
    TextView pinpuNew;
    TextView yinpinNew;
    statisticalGraph weekLayout;
    CalendarView monthLayout;

    List<Integer> list;
    List<String> stringList;
    List<String> danpinAll;
    List<String> pinpuAll;
    List<String> pinduanAll;
    List<String> musicAll;

    private String danpinlength;
    private String pinpulength;
    private String pinduanlength;
    private String yinpinlength;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<recentContent> list = (List<recentContent>) msg.obj;
            if(list.size()==0){
                list.add(new recentContent("暂无数据","",5));
            }
            simpleTestAdapter.setRecentContent(list);
            if (rv != null) {
                rv.setAdapter(simpleTestAdapter);
            }
            final TagCloudAdapter adapter = new TagCloudAdapter(list, rv);
            tagCloudView.setAdapter(adapter);
            tagCloudView.setBackgroundColor(Color.parseColor("#00000000"));
        }
    };
    private Intent intent;
    private Context context;
    private Activity activity;
    private boolean monFlag = false;

    @SuppressLint("ValidFragment")
    public OfflineFragment2(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public OfflineFragment2() {}

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main31, null, false);
        nestedScrollView = view.findViewById(R.id.offline_scroll);
        pinpulayout = view.findViewById(R.id.pinpu_layout);
        danpinLayout = view.findViewById(R.id.danpin_layout);
        downMapLayout = view.findViewById(R.id.downoad_offlinemap);
        pinduanLayout = view.findViewById(R.id.pinduan_layout);
        musicLayout = view.findViewById(R.id.music_layout);
        fileLayout = view.findViewById(R.id.file_layout);
        tagCloudView = view.findViewById(R.id.tag_cloud);
        pieLineView = view.findViewById(R.id.pie_show_precent);

        danpinSize = view.findViewById(R.id.danpin_size);
        pinpuSize = view.findViewById(R.id.pinpu_size);
        pinduanSize = view.findViewById(R.id.pinduan_size);
        yinpinSize = view.findViewById(R.id.yinpin_size);

        danpinMem = view.findViewById(R.id.danpin_mem);
        pinpuMem = view.findViewById(R.id.pinpu_mem);
        pinduanMem = view.findViewById(R.id.pinduan_mem);
        yinpinMem = view.findViewById(R.id.yinpin_mem);

        danpinNew = view.findViewById(R.id.danpin_new);
        pinpuNew = view.findViewById(R.id.pinpu_new);
        pinduanNew = view.findViewById(R.id.pinduan_new);
        yinpinNew = view.findViewById(R.id.yinpin_new);

        weekLayout = view.findViewById(R.id.week_view);
        weekLayout.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        monthLayout = view.findViewById(R.id.month_view);
        monthLayout.setOnTouchListener((v, event) -> {
            if(monFlag==false){
//                switch (event.getAction()){
//
//                }
                monFlag = true;
                Intent intent = new Intent(context, MonthDataActivity.class);
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(
                        activity, monthLayout, "shareimage").toBundle();
                startActivity(intent,options);
            }
            return true;
        });

//        monthLayout.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
//        back = findViewById(R.id.imageview_back);

        danpinLayout.setOnClickListener(v -> click(HistoryListActivity.DF));
        pinpulayout.setOnClickListener(v -> click(HistoryListActivity.AN));
        pinduanLayout.setOnClickListener(v -> click(HistoryListActivity.PD));
        musicLayout.setOnClickListener(v -> click(HistoryListActivity.REC));
        fileLayout.setOnClickListener(v -> startActivity(new Intent(context, FileListActivity.class)));
        downMapLayout.setOnClickListener(v -> startActivity(new Intent(context, OfflineDownloadActivity.class)));
//        back.setOnClickListener(v -> finish());

        simpleTestAdapter = new SimpleTestAdapter();
        simpleTestAdapter.setContext(context);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(context));
        FileOsImpl.getRecentList(handler); //  请求刷新历史数据
        return view;
    }

    private void click(String s) {
        intent = new Intent(context, HistoryListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", s);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        monFlag = false;
        List<recentContent> listFile = new ArrayList<>();
        File file1 = new File(FileOsImpl.forSaveFloder + File.separator + "data");
        for (File file : file1.listFiles()) {
            if (file.getName().contains("DF")) {
                listFile.add(new recentContent(file.getAbsolutePath(), file.getName(), 1));
            } else if (file.getName().contains("AN")) {
                listFile.add(new recentContent(file.getAbsolutePath(), file.getName(), 2));
            } else if (file.getName().contains("PD")) {
                listFile.add(new recentContent(file.getAbsolutePath(), file.getName(), 3));
            } else if (file.getName().contains("REC")) {
                listFile.add(new recentContent(file.getAbsolutePath(), file.getName(), 4));
            }
        }
        initData(listFile);
    }

    private void initData(List<recentContent> list) {
        danpinAll = new ArrayList<>();
        pinpuAll = new ArrayList<>();
        pinduanAll = new ArrayList<>();
        musicAll = new ArrayList<>();
        for (recentContent recentContent : list) {
            switch (recentContent.getType()) {
                case 1:
                    danpinAll.add(recentContent.getFile());
                    break;
                case 2:
                    pinpuAll.add(recentContent.getFile());
                    break;
                case 3:
                    pinduanAll.add(recentContent.getFile());
                    break;
                case 4:
                    musicAll.add(recentContent.getFile());
                    break;
                default:
                    break;
            }
        }
        int danpin = 0;
        for (String filename : danpinAll) {
            danpin = danpin + (int) (new File(filename).length());
        }
        danpinlength = getSize(danpin);

        int pinpu = 0;
        for (String filename : pinpuAll) {
            pinpu = pinpu + (int) (new File(filename).length());
        }
        pinpulength = getSize(pinpu);

        int pinduan = 0;
        for (String filename : pinduanAll) {
            pinduan = pinduan + (int) (new File(filename).length());
        }
        pinduanlength = getSize(pinduan);

        int yinpin = 0;
        for (String filename : musicAll) {
            yinpin = yinpin + (int) (new File(filename).length());
        }
        yinpinlength = getSize(yinpin);

//        int[] floats = new int[]{1, 12, 24, 5, 15, 3, 12};
        weekLayout.startWeek();
//        monthLayout.startMonth();
        refreshView();
    }

    private void refreshView() {
        danpinSize.setText("共" + danpinAll.size() + "条数据");
        pinpuSize.setText("共" + pinpuAll.size() + "条数据");
        pinduanSize.setText("共" + pinduanAll.size() + "条数据");
        yinpinSize.setText("共" + musicAll.size() + "条数据");

        danpinMem.setText("共占用" + danpinlength + "空间");
        pinpuMem.setText("共占用" + pinpulength + "空间");
        pinduanMem.setText("共占用" + pinduanlength + "空间");
        yinpinMem.setText("共占用" + yinpinlength + "空间");

        danpinNew.setText("最新：" + (danpinAll.size() != 0 ? (new File(danpinAll.get(0)).getName()) : ""));
        pinpuNew.setText("最新：" + (pinpuAll.size() != 0 ? (new File(pinpuAll.get(0)).getName()) : ""));
        pinduanNew.setText("最新：" + (pinduanAll.size() != 0 ? (new File(pinduanAll.get(0)).getName()) : ""));
        yinpinNew.setText("最新：" + (musicAll.size() != 0 ? (new File(musicAll.get(0)).getName()) : "无"));

        list = new ArrayList<>();
        stringList = new ArrayList<>();
        if (danpinAll.size() != 0) {
            stringList.add("单频测量");
            list.add(danpinAll.size());
        }
        if (pinpuAll.size() != 0) {
            stringList.add("频谱分析");
            list.add(pinpuAll.size());
        }
        if (pinduanAll.size() != 0) {
            stringList.add("频段扫描");
            list.add(pinduanAll.size());
        }
        if (musicAll.size() != 0) {
            stringList.add("音频回放");
            list.add(musicAll.size());
        }
        pieLineView.setList(list, stringList);
    }

    public String getSize(long size) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        long m = size;
        while (size / 1024 > 0) {
            i++;
            m = size % 1024;
            size = size / 1024;
        }
        switch (i) {
            case 0:
                stringBuilder.append(size);
                stringBuilder.append(" ");
                stringBuilder.append("B");
                break;
            case 1:
                stringBuilder.append(size);
                stringBuilder.append(".");
                Format f1 = new DecimalFormat("000");
                String ss = f1.format(m);
                stringBuilder.append(ss);
                stringBuilder.append(" ");
                stringBuilder.append("KB");
                break;
            case 2:
                stringBuilder.append(size);
                stringBuilder.append(".");
                Format f2 = new DecimalFormat("000");
                String s2 = f2.format(m);
                stringBuilder.append(s2);
                stringBuilder.append(" ");
                stringBuilder.append("MB");
                break;
            case 3:
                stringBuilder.append(size);
                stringBuilder.append(".");
                Format f3 = new DecimalFormat("000");
                String s3 = f3.format(m);
                stringBuilder.append(s3);
                stringBuilder.append(" ");
                stringBuilder.append("GB");
                break;
        }
        return String.valueOf(stringBuilder);
    }

}
