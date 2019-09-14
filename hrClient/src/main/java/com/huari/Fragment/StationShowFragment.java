package com.huari.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huari.adapter.RecordAllAdapter;
import com.huari.adapter.RecordAllBaseAdapter;
import com.huari.adapter.StationFunctionListAdapter;
import com.huari.client.R;
import com.huari.dataentry.GlobalData;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Station;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StationShowFragment extends Fragment {

    public static RecyclerView rvShow;
    public static RecyclerView stationFunction;
    public static List<Station> stations;
    public static Context context;
    public static List<String> list = new ArrayList<>();
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stations = new ArrayList<>();
            for (String key : GlobalData.stationHashMap.keySet()) {
                stations.add(GlobalData.stationHashMap.get(key));
                Log.d("xiaoname", String.valueOf(GlobalData.stationHashMap.get(key)));
            }
            Log.d("xiaoname", String.valueOf(stations.size()));
            leftListRecycle();
        }
    };

    public StationShowFragment() {
    }

    @SuppressLint("ValidFragment")
    public StationShowFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_all_record_query, container, false);
        this.rvShow = view.findViewById(R.id.rvShow);
        stationFunction = view.findViewById(R.id.station_function_list);
        list.add("地图");
        list.add("查询");
        list.add("管理");
        return view;
    }

    public static void leftListRecycle() {
//        rvShow.setLayoutManager(new GridLayoutManager(this, 3));
        rvShow.setLayoutManager(new LinearLayoutManager(context));
        RecordAllAdapter mWrapper = new RecordAllAdapter(context, stations, (device, station) -> reightListRecycle(device, station));
        rvShow.setAdapter(mWrapper);
    }

    public static void reightListRecycle(MyDevice myDevice, Station station) {
        stationFunction.setLayoutManager(new LinearLayoutManager(context));
        StationFunctionListAdapter stationFunctionListAdapter = new StationFunctionListAdapter(myDevice, station, context, list);
        stationFunction.setAdapter(stationFunctionListAdapter);
    }
}
