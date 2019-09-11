package com.huari.client;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huari.adapter.RecordAllAdapter;
import com.huari.adapter.RecordAllBaseAdapter;
import com.huari.adapter.StationFunctionListAdapter;
import com.huari.dataentry.ClassBean;
import com.huari.dataentry.GlobalData;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Station;

import java.util.ArrayList;
import java.util.List;

public class AllRecordQueryActivity extends AppCompatActivity {
//    private RecyclerView rvShow;
//    private RecyclerView stationFunction;
//    List<Station> stations;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_all_record_query);
//        stations = new ArrayList<>();
//        for (String key:GlobalData.stationHashMap.keySet()){
//            stations.add(GlobalData.stationHashMap.get(key));
//            Log.d("xiaoname", String.valueOf(GlobalData.stationHashMap.get(key)));
//        }
//        Log.d("xiaoname", String.valueOf(stations.size()));
//        this.rvShow = findViewById(R.id.rvShow);
//        stationFunction = findViewById(R.id.station_function_list);
//        leftListRecycle();
//    }
//
//    public void reightListRecycle(MyDevice myDevice) {
//        if(myDevice.getName()!=null){
//            Log.d("xiao",myDevice.getName());
//        }
//        for (String s :myDevice.getLogic().keySet()){
//            Log.d("xiao", String.valueOf(myDevice.getLogic().get(s)));
//            Log.d("xiao","1");
//        }
//        stationFunction.setLayoutManager(new LinearLayoutManager(this));
//        StationFunctionListAdapter stationFunctionListAdapter = new StationFunctionListAdapter(myDevice);
//        stationFunction.setAdapter(stationFunctionListAdapter);
//    }
//
//    public void leftListRecycle() {
////        rvShow.setLayoutManager(new GridLayoutManager(this, 3));
//        rvShow.setLayoutManager(new LinearLayoutManager(this));
//        RecordAllBaseAdapter mWrapper = new RecordAllAdapter(this, stations, device -> reightListRecycle(device));
//        rvShow.setAdapter(mWrapper);
//    }
}
