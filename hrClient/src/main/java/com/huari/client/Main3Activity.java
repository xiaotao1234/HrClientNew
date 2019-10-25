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
import android.view.View;
import android.widget.ImageView;
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
    SimpleTestAdapter simpleTestAdapter;
    NestedScrollView nestedScrollView;
    LinearLayout pinpulayout;
    LinearLayout danpinLayout;
    LinearLayout pinduanLayout;
    LinearLayout musicLayout;
    LinearLayout fileLayout;
    pieLineView pieLineView;
    TagCloudView tagCloudView;
    RecyclerView rv;

    ImageView back;
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

    private String danpinlength;
    private String pinpulength;
    private String pinduanlength;
    private String yinpinlength;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<recentContent> list = (List<recentContent>) msg.obj;
            simpleTestAdapter.setRecentContent(list);
            if (rv != null) {
                rv.setAdapter(simpleTestAdapter);
            }
            final TagCloudAdapter adapter = new TagCloudAdapter(list, rv,getApplicationContext());
            tagCloudView.setAdapter(adapter);
            tagCloudView.setBackgroundColor(Color.parseColor("#00000000"));
        }
    };
    private Intent intent;

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

    @Override
    protected void onResume() {
        super.onResume();
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

    private void initView() {
        nestedScrollView = findViewById(R.id.offline_scroll);
        pinpulayout = findViewById(R.id.pinpu_layout);
        danpinLayout = findViewById(R.id.danpin_layout);
        pinduanLayout = findViewById(R.id.pinduan_layout);
        musicLayout = findViewById(R.id.music_layout);
        fileLayout = findViewById(R.id.file_layout);
        tagCloudView = findViewById(R.id.tag_cloud);
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

//        back = findViewById(R.id.imageview_back);

        danpinLayout.setOnClickListener(v -> click(HistoryListActivity.DF));
        pinpulayout.setOnClickListener(v -> click(HistoryListActivity.AN));
        pinduanLayout.setOnClickListener(v -> click(HistoryListActivity.PD));
        musicLayout.setOnClickListener(v -> click(HistoryListActivity.RE));
        fileLayout.setOnClickListener(v -> startActivity(new Intent(Main3Activity.this, FileListActivity.class)));
//        back.setOnClickListener(v -> finish());

        simpleTestAdapter = new SimpleTestAdapter();
        simpleTestAdapter.setContext(this);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        FileOsImpl.getRecentList(handler); //  请求刷新历史数据
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

    private void click(String s) {
        intent = new Intent(Main3Activity.this, HistoryListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", s);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static int days(int year, int month) {
        int days = 0;
        if (month != 2) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    days = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    days = 30;
            }
        } else {
            // 闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                days = 29;
            else
                days = 28;

        }
        return days;
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
