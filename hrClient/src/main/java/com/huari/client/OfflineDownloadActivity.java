package com.huari.client;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class OfflineDownloadActivity extends Activity implements MKOfflineMapListener {

    private static final String TAG = "OfflineDownloadActivity";

    private MKOfflineMap mOffline = null;
    private TextView cidView;
    private TextView stateView;
    private EditText cityNameView;
    TextView next;
    /**
     * 已下载的离线地图信息列表
     */
    private ArrayList<MKOLUpdateElement> localMapList = null;
    private LocalMapAdapter lAdapter = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults!=null&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                }else {
                }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        initView();
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET)==PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
        }else {
        }
    }

    private void initView() {
        next = findViewById(R.id.next);
        cidView = findViewById(R.id.cityid);
        cityNameView = findViewById(R.id.city);
        stateView = findViewById(R.id.state);
        next.setOnClickListener(v -> startActivity(new Intent(OfflineDownloadActivity.this, MajorActivity.class)));
        ListView hotCityList = findViewById(R.id.hotcitylist);
        ArrayList<String> hotCities = new ArrayList<>();
        final ArrayList<String> hotCityNames = new ArrayList<>();
        // 获取热闹城市列表
        ArrayList<MKOLSearchRecord> records1 = mOffline.getHotCityList();
        if (records1 != null) {
            for (MKOLSearchRecord r : records1) {
                //V4.5.0起，保证数据不溢出，使用long型保存数据包大小结果
                hotCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
                        + this.formatDataSize(r.size));
                hotCityNames.add(r.cityName);
            }
        }
        ListAdapter hAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, hotCities);
        hotCityList.setAdapter(hAdapter);
        hotCityList.setOnItemClickListener((adapterView, view, i, l) -> cityNameView.setText(hotCityNames.get(i)));

        ListView allCityList = findViewById(R.id.allcitylist);
        // 获取所有支持离线地图的城市
        ArrayList<String> allCities = new ArrayList<String>();
        final ArrayList<String> allCityNames = new ArrayList<String>();
        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
        if (records2 != null) {
            for (MKOLSearchRecord r : records2) {
                //V4.5.0起，保证数据不溢出，使用long型保存数据包大小结果
                allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
                        + this.formatDataSize(r.size));
                allCityNames.add(r.cityName);
            }
        }
        ListAdapter aAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, allCities);
        allCityList.setAdapter(aAdapter);
        allCityList.setOnItemClickListener((parent, view, position, id) -> {
            cityNameView.setText(allCityNames.get(position));
            search(allCityList);
        });
        LinearLayout cl = findViewById(R.id.citylist_layout);
        LinearLayout lm = findViewById(R.id.localmap_layout);
        lm.setVisibility(View.GONE);
        cl.setVisibility(View.VISIBLE);

        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<>();
        }

        ListView localMapListView = findViewById(R.id.localmaplist);
        lAdapter = new LocalMapAdapter();
        localMapListView.setAdapter(lAdapter);
    }

    /**
     * 切换至城市列表
     *
     * @param view
     */
    public void clickCityListButton(View view) {
        LinearLayout cl = findViewById(R.id.citylist_layout);
        LinearLayout lm = findViewById(R.id.localmap_layout);
        lm.setVisibility(View.GONE);
        cl.setVisibility(View.VISIBLE);

    }

    /**
     * 切换至下载管理列表
     *
     * @param view
     */
    public void clickLocalMapListButton(View view) {
        LinearLayout cl = findViewById(R.id.citylist_layout);
        LinearLayout lm = findViewById(R.id.localmap_layout);
        lm.setVisibility(View.VISIBLE);
        cl.setVisibility(View.GONE);
    }

    /**
     * 搜索离线需市
     *

     */
    public void search(View view) {
        ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityNameView
                .getText().toString());

        if (records == null || records.size() != 1) {
            Toast.makeText(this, "不支持该城市离线地图", Toast.LENGTH_SHORT).show();
            return;
        }

        cidView.setText(String.valueOf(records.get(0).cityID));
    }

    /**
     * 开始下载
     *
     * @param view
     */
    public void start(View view) {
        int cityid = Integer.parseInt(cidView.getText().toString());
        mOffline.start(cityid);
        clickLocalMapListButton(null);

        Toast.makeText(this, "开始下载离线地图. cityid: " + cityid, Toast.LENGTH_SHORT).show();
        updateView();
    }

    /**
     * 暂停下载
     *
     * @param view
     */
    public void stop(View view) {
        int cityid = Integer.parseInt(cidView.getText().toString());
        mOffline.pause(cityid);
        Toast.makeText(this, "暂停下载离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
                .show();
        updateView();
    }

    /**
     * 删除离线地图
     *
     * @param view
     */
    public void remove(View view) {
        int cityid = Integer.parseInt(cidView.getText().toString());
        mOffline.remove(cityid);
        Toast.makeText(this, "删除离线地图. cityid: " + cityid, Toast.LENGTH_SHORT).show();
        updateView();
    }

    /**
     * 更新状态显示
     */
    public void updateView() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<>();
        }
        lAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        int cityid = Integer.parseInt(cidView.getText().toString());
        MKOLUpdateElement temp = mOffline.getUpdateInfo(cityid);
        if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
            mOffline.pause(cityid);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * V4.5.0起，保证数据不溢出，使用long型保存数据包大小结果
     */
    public String formatDataSize(long size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    @Override
    protected void onDestroy() {
        /**
         * 退出时，销毁离线地图模块
         */
        mOffline.destroy();
        super.onDestroy();
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
                    stateView.setText(String.format("%s : %d%%", update.cityName, update.ratio));
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

    /**
     * 离线地图管理列表适配器
     */
    public class LocalMapAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localMapList.size();
        }

        @Override
        public Object getItem(int index) {
            return localMapList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
            view = View.inflate(OfflineDownloadActivity.this,
                    R.layout.offline_localmap_list, null);
            initViewItem(view, e);
            return view;
        }

        void initViewItem(View view, final MKOLUpdateElement e) {
//            Button display = view.findViewById(R.id.display);
            Button remove = view.findViewById(R.id.remove);
            TextView title = view.findViewById(R.id.title);
            TextView update = view.findViewById(R.id.update);
            TextView ratio = view.findViewById(R.id.ratio);
            ratio.setText(e.ratio + "%");
            title.setText(e.cityName);

            if (e.update) {
                update.setText("可更新");
            } else {
                update.setText("最新");
            }

//            if (e.ratio != 100) {
//                display.setEnabled(false);
//            } else {
//                display.setEnabled(true);
//            }

            remove.setOnClickListener(arg0 -> {
                mOffline.remove(e.cityID);
                updateView();
            });

//            display.setOnClickListener(v -> {
//                Intent intent = new Intent();
//                intent.putExtra("customStyle", true);
//                intent.putExtra("x", e.geoPt.longitude);
//                intent.putExtra("y", e.geoPt.latitude);
//                intent.putExtra("level", 13.0f);
//                intent.setClass(OfflineDownloadActivity.this, MapActivity.class);
//                startActivity(intent);
//            });
        }

    }


}
