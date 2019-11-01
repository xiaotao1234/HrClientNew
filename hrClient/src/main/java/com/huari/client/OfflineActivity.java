package com.huari.client;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.huari.adapter.BottomDialogAdapter;
import com.huari.adapter.ExtendableListViewAdapter;
import com.huari.adapter.MapSearchResult;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class OfflineActivity extends AppCompatActivity implements MKOfflineMapListener {
    ExpandableListView expandableListView;
    private MKOfflineMap mOffline = null;
    private List<List> childList = new ArrayList<>();
    private ArrayList<String> allCityNames;
    private ArrayList<String> allCities;
    private ArrayList<ArrayList<MKOLSearchRecord>> cityRecord;
    private ArrayList<MKOLUpdateElement> localMapList = null;
    private ExtendableListViewAdapter extendableListViewAdapter;
    private EditText editText;
    private RecyclerView recyclerView;
    private ArrayList<MKOLSearchRecord> records;
    private BottomSheetDialog bsd2;
    private BottomDialogAdapter adapter;
    private LinearLayout linearLayout;
    private ImageView back;
    private boolean search;
    private PopupWindow window;
    private ImageView search1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlines);
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        initData();
        initView();
    }

    private void initData() {
        allCities = new ArrayList<>();
        allCityNames = new ArrayList<>();
        cityRecord = new ArrayList<>();
        search = false;
        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
        if (records2 != null) {
            for (MKOLSearchRecord r : records2) {
                allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
                        + this.formatDataSize(r.size));
                allCityNames.add(r.cityName);
                cityRecord.add(r.childCities);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (search == true) {
            recyclerView.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
            search = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        back.setSystemUiVisibility(View.INVISIBLE);

    }

    private void initView() {
        search1 = findViewById(R.id.search_button);
        editText = findViewById(R.id.main_edit);
        recyclerView = findViewById(R.id.show_result_list);
        back = findViewById(R.id.back);
        expandableListView = findViewById(R.id.expend_list);
        linearLayout = findViewById(R.id.parent_layout);
        search1.setOnClickListener(v -> {
            if(editText.getText().length()!=0){
                editSearch(editText.getText());
            }
        });
        back.setOnClickListener(v -> finish());
        extendableListViewAdapter = new ExtendableListViewAdapter();
        childList.add(new ArrayList());
        childList.add(new ArrayList());
        for (int i = 0; i < allCityNames.size(); i++) {
            childList.get(1).add(i, allCityNames.get(i));
        }
        extendableListViewAdapter.setChildString(childList, cityRecord);
        extendableListViewAdapter.setSection1Listener((s, status, id) -> {
            bsd2.show();
            List<String> list = new ArrayList<>();
            list.add(s);
            if (status == 3) {
                list.add("继续下载");
            }
//            if (status != 4 || status != 10) {
//                list.add("重新下载");
//            }
            list.add("删除");
            list.add("取消");
            adapter.setmList(list, id);
            adapter.notifyDataSetChanged();
        }, position -> popWindow((String) childList.get(1).get(position)));
        expandableListView.setAdapter(extendableListViewAdapter);
        for (int i = 0; i < extendableListViewAdapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }
        editText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    boolean find = false;
                    localMapList = mOffline.getAllUpdateInfo();
                    ArrayList<MKOLSearchRecord> arrayList = mOffline.searchCity(String.valueOf(s));
                    if (arrayList != null && arrayList.size() > 0) {
                        String s1 = arrayList.get(0).cityName;
                        if (localMapList != null && localMapList.size() > 0) {
                            for (MKOLUpdateElement mkolUpdateElement : localMapList) {
                                if (mkolUpdateElement.cityName.equals(s1)) {
                                    find = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (find == false) {
                        editSearch(s);
                    } else {
                        Toast.makeText(OfflineActivity.this, "已下载" + s + "的离线地图包", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(this).inflate(R.layout.recycle_item, null);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BottomDialogAdapter(this, null);
        adapter.setDownloadListener(id -> {
            mOffline.start(id);
            bsd2.hide();
            Toast.makeText(OfflineActivity.this, "开始重新下载", Toast.LENGTH_SHORT).show();
            updateView();
        }, id -> {
            mOffline.remove(id);
            bsd2.hide();
            Toast.makeText(OfflineActivity.this, "已删除离线地图包", Toast.LENGTH_SHORT).show();
            updateView();
        }, () -> window.dismiss());
        recyclerView.setAdapter(adapter);
        bsd2 = new BottomSheetDialog(this);
        bsd2.setContentView(recyclerView);
        updateView();
        back.postDelayed(() -> popWindowDia(),500);
    }

    private void editSearch(Editable s) {
        records = mOffline.searchCity(String.valueOf(s));
        if (records == null || records.size() != 1) {
            Toast.makeText(OfflineActivity.this, "不支持该城市离线地图", Toast.LENGTH_SHORT).show();
            return;
        }
        expandableListView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        search = true;
        MapSearchResult searchResult = new MapSearchResult(records);
        recyclerView.setLayoutManager(new LinearLayoutManager(OfflineActivity.this));
        searchResult.setClickListeners(id -> startDownload(id));
        recyclerView.setAdapter(searchResult);
    }

    public void startDownload(int id) {
        mOffline.start(id);
        updateView();
        recyclerView.setVisibility(View.GONE);
        expandableListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
//                    stateView.setText(String.format("%s : %d%%", update.cityName, update.ratio));
                    updateView();
                }
            }
            break;

            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDownloadActivity", String.format("add offlinemap num:%d", state));
                break;

            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);
                break;

            default:
                break;
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void updateView() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<>();
        }
        childList.set(0, localMapList);
        extendableListViewAdapter.setChildString(childList, cityRecord);
        extendableListViewAdapter.notifyDataSetChanged();
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

    private void popWindow(String name) {
        View popupView = OfflineActivity.this.getLayoutInflater().inflate(R.layout.delete_warn, null);
        popupView.setPadding(50, 0, 50, 0);
        TextView oktext = popupView.findViewById(R.id.ok_button);
        TextView canceltext = popupView.findViewById(R.id.cancel_button);
        TextView textviewShow = popupView.findViewById(R.id.text_show);
        popupView.setPadding(50, 0, 50, 0);
        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, WRAP_CONTENT, true);
        window.setWidth((int) getResources().getDimension(R.dimen.dp_280));
        window.showAtLocation(linearLayout, Gravity.CLIP_VERTICAL, 0, 0);
        window.setAnimationStyle(R.style.popup_window_anim);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        oktext.setText("继续下载");
        canceltext.setText("取消");
        oktext.setOnClickListener(v -> {
            mOffline.start(mOffline.searchCity(name).get(0).cityID);
            updateView();
            window.dismiss();

            boolean find = false;
            localMapList = mOffline.getAllUpdateInfo();
            ArrayList<MKOLSearchRecord> arrayList = mOffline.searchCity(String.valueOf(name));
            if (arrayList != null && arrayList.size() > 0) {
                String s1 = arrayList.get(0).cityName;
                if (localMapList != null && localMapList.size() > 0) {
                    for (MKOLUpdateElement mkolUpdateElement : localMapList) {
                        if (mkolUpdateElement.cityName.equals(s1)) {
                            find = true;
                            break;
                        }
                    }
                }
            }
            if(find==true){
                Toast.makeText(OfflineActivity.this,"本地已有该城市的离线地图包",Toast.LENGTH_SHORT).show();
            }

        });
        textviewShow.setText("您选择的是下载整个" + name + "的地图包，这个地图包大小为" + formatDataSize(mOffline.searchCity(name).get(0).dataSize)
                +","+"所以下载时间可能比较长"+"若您只需要某个城市的离线地图包，可以在上方搜索框中输入城市名然后下载对应城市的离线地图包");
        canceltext.setOnClickListener(v -> window.dismiss());
    }
    private void popWindowDia() {
        View popupView = OfflineActivity.this.getLayoutInflater().inflate(R.layout.delete_warn, null);
        popupView.setPadding(50, 0, 50, 0);
        TextView oktext = popupView.findViewById(R.id.ok_button);
        TextView canceltext = popupView.findViewById(R.id.cancel_button);
        TextView textviewShow = popupView.findViewById(R.id.text_show);
        popupView.setPadding(50, 0, 50, 0);
        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, WRAP_CONTENT, true);
        window.setWidth((int) getResources().getDimension(R.dimen.dp_280));
        window.showAtLocation(linearLayout, Gravity.CLIP_VERTICAL, 0, 0);
        window.setAnimationStyle(R.style.popup_window_anim);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        oktext.setVisibility(View.GONE);
        canceltext.setText("继续");
        textviewShow.setText("下载离线地图包需要连接到可用外网中，请在开始下载前手动完成相关设置，连接外网会改变当前和站点的连接状态，稍后需要重新进行连接和登陆");
        canceltext.setOnClickListener(v -> window.dismiss());
    }
}
