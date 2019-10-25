package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.text.RegexOption;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.adapter.OnlyTextAdapter;
import com.huari.adapter.SearchFileResultAdapter;
import com.huari.dataentry.ForADataInformation;
import com.huari.dataentry.recentContent;
import com.huari.tools.FileOsImpl;
import com.huari.tools.RealTimeSaveAndGetStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FindFileActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView spinList;
    LinearLayout topLayoutDelete;
    ConstraintLayout initialTopLayout;
    ImageView station_ima;
    ImageView device_ima;
    ImageView allCheck;
    ImageView mm_ima;
    TextView stationText;
    TextView deviceText;
    TextView mmText;
    TextView deleteText;
    ImageView back;
    SearchFileResultAdapter searchFileResult;
    OnlyTextAdapter onlyTextAdapter;
    List<File> files = new ArrayList<>();
    private int headerViewHeight;
    private List<ImageIndex> list;
    private boolean refreshFlag;
    List<String> forSaveTheStationInformation = new ArrayList<>();
    List<String> forSaveTheDeviceInformation = new ArrayList<>();
    List<ForADataInformation> forADataInformationList = new ArrayList<>();
    private List<String> kindOfData;
    private boolean firstIn = true;
    private View view;
    private int recordWhichView;
    private boolean finlish;
    private List<File> files1;
    Dialog dialog;
    private String s;
    private TextView title;

    private void deleteFile() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    private void hideHeader() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(View.VISIBLE);
            getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 34:
                    ForADataInformation forADataInformation = (ForADataInformation) msg.obj;
                    forADataInformationList.add(forADataInformation);
                    if (!forSaveTheStationInformation.contains(forADataInformation.getStationName())) {
                        forSaveTheStationInformation.add(forADataInformation.getStationName());
                    }
                    if (!forSaveTheDeviceInformation.contains(forADataInformation.getDeviceName())) {
                        forSaveTheDeviceInformation.add(forADataInformation.getDeviceName());
                    }
                    if (finlish == true) {
                        refreshFlag = true;
                        refresh();
                    }
                    break;
                case 35:
                    LongClickBack();
                    break;
            }
        }
    };

    Thread thread = new Thread(() -> {
        boolean change = false;
        if (files1.size() != 0) {
            for (File file : files1) {
                int length = FileOsImpl.recentUseFiles.size();
                String name;
                int j = -1;
                for (int i = 0; i < length; i++) {
                    j++;
                    name = FileOsImpl.recentUseFiles.get(j).getFilename();
                    if(name.equals(file.getName())){
                        FileOsImpl.recentUseFiles.remove(j);
                        change = true;
                        j--;
                    }
                }
                file.delete();
                if (files.contains(file)) {
                    files.remove(file);
                }
            }
            if(change == true){
                FileOsImpl.saveRecentFilesFormMem();
            }
        }
        handler.sendEmptyMessage(35);
    });

    private void refresh() {
        for (ImageIndex imageIndex : list) {
            if (imageIndex.status == true) {
                if (imageIndex.index == 1) {
                    onlyTextAdapter.setKind(false);
                    onlyTextAdapter.setStringList(forSaveTheStationInformation, 0);
                } else if (imageIndex.index == 2) {
                    onlyTextAdapter.setKind(false);
                    onlyTextAdapter.setStringList(forSaveTheDeviceInformation, 1);
                } else if (imageIndex.index == 3) {
                    onlyTextAdapter.setKind(true);
                    onlyTextAdapter.setStringList(kindOfData, 2);
                }
            }
        }
        onlyTextAdapter.notifyDataSetChanged();
        heightChanger();
    }

    @Override
    public void onBackPressed() {
        if (list.get(recordWhichView).isStatus() == true) {
            topClick(recordWhichView);
            return;
        }
        if (SearchFileResultAdapter.longClickFlag == true) {
            LongClickBack();
        } else {
            super.onBackPressed();
        }
    }

    private void LongClickBack() {
        if (files1 != null) {
            for (File file : files1) {
                ForADataInformation forADataInformation = new ForADataInformation(null, null, null, null);
                forADataInformation.setFile(file.getName());
                if (forADataInformationList.contains(forADataInformation)) {
                    forADataInformationList.remove(forADataInformation);
                }
            }
        }
        initialTopLayout.bringToFront();
        topLayoutDelete.setClickable(false);
        SearchFileResultAdapter.longClickFlag = false;
        searchFileResult.recordCheck.clear();
        searchFileResult.notifyDataSetChanged();
        allCheck.setSelected(false);
    }

    class ImageIndex {
        ImageView imageView;
        int index;
        boolean status;

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public boolean isStatus() {
            return status;
        }

        public ImageIndex(ImageView imageView, boolean status, int index) {
            this.imageView = imageView;
            this.status = status;
            this.index = index;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_find_file);
//        hideHeader();
        firstIn = true;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        title = findViewById(R.id.title_item);
        initData();
        recyclerView = findViewById(R.id.serach_recycle);
        back = findViewById(R.id.back);
        topLayoutDelete = findViewById(R.id.delete_top_layout);
        initialTopLayout = findViewById(R.id.initial_layout);
        spinList = findViewById(R.id.spin_list);
        station_ima = findViewById(R.id.station_spain);
        device_ima = findViewById(R.id.device_spain);
        allCheck = findViewById(R.id.all_check);
        deleteText = findViewById(R.id.delete);
        view = findViewById(R.id.cover_layer);
        mm_ima = findViewById(R.id.mm_spain);
        stationText = findViewById(R.id.station_text);
        deviceText = findViewById(R.id.device_text);
        mmText = findViewById(R.id.mm_text);
        list = new ArrayList<>();
        list.add(new ImageIndex(station_ima, false, 1));
        list.add(new ImageIndex(device_ima, false, 2));
        list.add(new ImageIndex(mm_ima, false, 3));
        List<String> list1 = new ArrayList<>();
        searchFileResult = new SearchFileResultAdapter(files, this);
        searchFileResult.setLongClickListener(() -> {
            topLayoutDelete.bringToFront();
            topLayoutDelete.setClickable(true);
        });
        onlyTextAdapter = new OnlyTextAdapter(list1, stationText, deviceText, mmText);
        spinList.setLayoutManager(new LinearLayoutManager(this));
        allCheck.setOnClickListener(v -> searchFileResult.checkAllorNot(allCheck));
        spinList.setAdapter(onlyTextAdapter);
        heightChanger();
        view.setOnClickListener(v -> topClick(recordWhichView));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchFileResult);
        recyclerView.setSystemUiVisibility(View.INVISIBLE);
        station_ima.setOnClickListener(v -> topClick(0));
        device_ima.setOnClickListener(v -> topClick(1));
        mm_ima.setOnClickListener(v -> topClick(2));
        back.setOnClickListener(v -> finish());
        getDataFormation();
        dialog = new AlertDialog.Builder(FindFileActivity.this)
                .setTitle("数据删除后不可恢复，确定删除？")
                .setNegativeButton("取消", (arg0, arg1) -> {
                })
                .setPositiveButton("确定", (arg0, arg1) -> deleteFile()).create();
        deleteText.setOnClickListener(v -> {
            List<Integer> record = searchFileResult.getRecordCheck();
            List<File> fileList = searchFileResult.getFileList();
            files1 = new ArrayList<>();
            if (record.size() != 0) {
                for (int i = 0; i < record.size(); i++) {
                    files1.add(fileList.get(record.get(i)));
                }
                dialog.show();
            }
        });
    }


    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            s = bundle.getString("type");
            if (s != null) {
                File file = new File(FileOsImpl.forSaveFloder + File.separator + "data");
                if (s.contains(HistoryListActivity.DF)) {
                    title.setText("单频测向");
                    files.clear();
                    for (File file1 : file.listFiles()) {
                        if (file1.getName().contains(HistoryListActivity.DF)) {
                            files.add(file1);
                        }
                    }
                } else if (s.contains(HistoryListActivity.AN)) {
                    title.setText("频谱分析");
                    files.clear();
                    for (File file1 : file.listFiles()) {
                        if (file1.getName().contains(HistoryListActivity.AN)) {
                            files.add(file1);
                        }
                    }
                } else if (s.contains(HistoryListActivity.PD)) {
                    title.setText("频段扫描");
                    files.clear();
                    for (File file1 : file.listFiles()) {
                        if (file1.getName().contains(HistoryListActivity.PD)) {
                            files.add(file1);
                        }
                    }
                } else if (s.contains(HistoryListActivity.RE)) {
                    title.setText("音频数据");
                    files.clear();
                    for (File file1 : file.listFiles()) {
                        if (file1.getName().contains(HistoryListActivity.RE)) {
                            files.add(file1);
                        }
                    }
                }
            }
            kindOfData = new ArrayList<>();
            finlish = false;
        }
    }

    private void getDataFormation() {
        kindOfData.clear();
        finlish = false;
        forADataInformationList.clear();
        forSaveTheStationInformation.clear();
        forSaveTheDeviceInformation.clear();
        for (File file : files) {
            String s = file.getName().substring(0, 2);
            if (!kindOfData.contains(s)) {
                kindOfData.add(s);
            }
            RealTimeSaveAndGetStore.deserializeFlyPig(file.getName(), handler);
        }
        finlish = true;
    }

    private void heightChanger() {
        spinList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                headerViewHeight = spinList.getHeight();
                if (firstIn == true) {
                    spinList.setTranslationY(-headerViewHeight);
                    firstIn = false;
                }
                spinList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setViewFornt(boolean b) {
        if (b) {
            view.bringToFront();
            spinList.bringToFront();
        } else {
            filter();
            recyclerView.bringToFront();
        }
    }

    private void filter() {
        List<ForADataInformation> list1 = new ArrayList<>();
        List<ForADataInformation> list2 = new ArrayList<>();
        list2.addAll(forADataInformationList);
        if (onlyTextAdapter.forSaveSelect.stationSelect != null && list2.size() > 0) {
            for (ForADataInformation forADataInformation : list2) {
                if (forADataInformation.getStationName().equals(onlyTextAdapter.forSaveSelect.stationSelect)) {
                    list1.add(forADataInformation);
                }
            }
            list2.clear();
            list2.addAll(list1);
            list1.clear();
        }
        if (onlyTextAdapter.forSaveSelect.deviceSelect != null && list2.size() > 0) {
            for (ForADataInformation forADataInformation : list2) {
                if (forADataInformation.getDeviceName().equals(onlyTextAdapter.forSaveSelect.deviceSelect)) {
                    list1.add(forADataInformation);
                }
            }
            list2.clear();
            list2.addAll(list1);
            list1.clear();
        }
        if (onlyTextAdapter.forSaveSelect.mmSelect != null && list2.size() > 0) {
            for (ForADataInformation forADataInformation : list2) {
                if (forADataInformation.getFile().substring(0, 2).equals(onlyTextAdapter.forSaveSelect.mmSelect)) {
                    list1.add(forADataInformation);
                }
            }
            list2.clear();
            list2.addAll(list1);
            list1.clear();
        }//三次筛选完成数据和选项的条件重叠
        List<File> fileList = new ArrayList<>();
        for (ForADataInformation forADataInformation : list2) {
            File file = new File(FileOsImpl.forSaveFloder + File.separator + "data" + File.separator + forADataInformation.getFile());
            fileList.add(file);
        }
        searchFileResult.setFileList(fileList);
        searchFileResult.notifyDataSetChanged();
    }

    public void topClick(int i) {
        recordWhichView = i;
        ImageIndex imageIndex = list.get(i);
        if (imageIndex.isStatus() == false) {
            setViewFornt(true);
            for (ImageIndex imageIndex1 : list) {
                imageIndex1.getImageView().setImageResource(R.drawable.pack_up);
                imageIndex1.setStatus(false);
            }
            imageIndex.setStatus(true);
            refresh();
            imageIndex.getImageView().setImageResource(R.drawable.expand_icon);
            spinList.setTranslationY(0);
            onlyTextAdapter.notifyDataSetChanged();
        } else {
            setViewFornt(false);
            imageIndex.setStatus(false);
            imageIndex.getImageView().setImageResource(R.drawable.pack_up);
            spinList.setTranslationY(-headerViewHeight);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMoonEvent(List<File> files) {
        this.files = files;
        kindOfData = new ArrayList<>();
        finlish = false;
    }
}
