package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.adapter.SimpleTestAdapter;
import com.huari.adapter.TagCloudAdapter;
import com.huari.dataentry.recentContent;
import com.huari.tools.FileOsImpl;
import com.huari.ui.TagCloudView;
import com.huari.ui.pieLineView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {
    TagCloudView tagCloudView;
    RecyclerView rv;
    SimpleTestAdapter simpleTestAdapter;
    LinearLayout historylayout;
    LinearLayout fileLayout;
    pieLineView pieLineView;
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

    List<Integer> list;
    List<String> stringList;
    List<String> danpinAll;
    List<String> pinpuAll;
    List<String> pinduanAll;
    List<String> musicAll;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<recentContent> list = (List<recentContent>) msg.obj;
            simpleTestAdapter.setRecentContent(list);
            if (rv != null) {
                rv.setAdapter(simpleTestAdapter);
            }
            final TagCloudAdapter adapter = new TagCloudAdapter(list, rv);
            tagCloudView.setAdapter(adapter);
            initData(list);
        }
    };
    private String danpinlength;
    private String pinpulength;
    private String pinduanlength;
    private String yinpinlength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main31);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
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

        danpinNew.setText("最新：" + (danpinAll.size()!=0?(new File(danpinAll.get(0)).getName()):""));
        pinpuNew.setText("最新：" + (pinpuAll.size()!=0?(new File(pinpuAll.get(0)).getName()):""));
        pinduanNew.setText("最新：" + (pinduanAll.size()!=0?(new File(pinduanAll.get(0)).getName()):""));
        yinpinNew.setText("最新：" + (musicAll.size()!=0?(new File(musicAll.get(0)).getName()):"无"));
    }

    private void initView() {
        nestedScrollView = findViewById(R.id.offline_scroll);
        historylayout = findViewById(R.id.history);
        tagCloudView = findViewById(R.id.tag_cloud);
        fileLayout = findViewById(R.id.file);
        pieLineView = findViewById(R.id.pie_show_precent);

        danpinSize = findViewById(R.id.danpin_size);
        pinpuSize = findViewById(R.id.pinpu_size);
        pinduanSize = findViewById(R.id.pinduan_size);
        yinpinSize = findViewById(R.id.yinpin_size);

        danpinMem = findViewById(R.id.danpin_mem);
        pinpuMem = findViewById(R.id.pinpu_mem);
        pinduanMem = findViewById(R.id.pinduan_mem);
        yinpinMem = findViewById(R.id.yinpin_mem);

        danpinNew = findViewById(R.id.danpin_new);
        pinpuNew = findViewById(R.id.pinpu_new);
        pinduanNew = findViewById(R.id.pinduan_new);
        yinpinNew = findViewById(R.id.yinpin_new);

        tagCloudView.setBackgroundColor(Color.parseColor("#00000000"));
        simpleTestAdapter = new SimpleTestAdapter();
        simpleTestAdapter.setContext(this);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        FileOsImpl.getRecentList(handler); //  请求刷新历史数据
        historylayout.setOnClickListener(v -> startActivity(new Intent(Main3Activity.this, HistoryDataActivity.class)));
        fileLayout.setOnClickListener(v -> startActivity(new Intent(Main3Activity.this, FileListActivity.class)));

        list = new ArrayList<>();
        stringList = new ArrayList<>();
        stringList.add("单频测量");
        stringList.add("频段扫描");
        stringList.add("频谱扫描");
        stringList.add("音频文件");
        list.add(20);
        list.add(60);
        list.add(40);
        list.add(70);
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

//    private boolean Judgebottom() {
//        int scrollY = nestedScrollView.getScrollY();
//        View onlyChild = nestedScrollView.getChildAt(0);
//        if (onlyChild.getHeight() <= scrollY + nestedScrollView.getHeight()) {   // 如果满足就是到底部了
//            return true;
//        }
//        return false;
//    }
}
