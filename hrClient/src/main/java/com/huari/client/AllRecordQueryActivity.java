package com.huari.client;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huari.adapter.RecordAllAdapter;
import com.huari.adapter.RecordAllBaseAdapter;
import com.huari.adapter.StationFunctionListAdapter;
import com.huari.dataentry.ClassBean;

import java.util.ArrayList;
import java.util.List;

public class AllRecordQueryActivity extends AppCompatActivity {
    private RecyclerView rvShow;
    private RecyclerView stationFunction;
    private List<ClassBean> mListClass = new ArrayList<>();
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_record_query);
        this.rvShow = findViewById(R.id.rvShow);
        stationFunction = findViewById(R.id.station_function_list);
        leftListRecycle();
    }

    public void reightListRecycle(List<String> list) {
        stationFunction.setLayoutManager(new LinearLayoutManager(this));
        StationFunctionListAdapter stationFunctionListAdapter = new StationFunctionListAdapter();
        stationFunction.setAdapter(stationFunctionListAdapter);
    }

    public void leftListRecycle() {
        for (int i = 1; i < 4; i++) {
            List<String> studentName = new ArrayList<>();
            for (int j = 1; j < 3; j++) {
                studentName.add(i + "班 学生" + j);
            }
            ClassBean bean = new ClassBean();
            bean.className = "二年级" + i + "班";
            bean.classStudents = studentName;
            mListClass.add(bean);
        }
//        rvShow.setLayoutManager(new GridLayoutManager(this, 3));
        rvShow.setLayoutManager(new LinearLayoutManager(this));
        RecordAllBaseAdapter mWrapper = new RecordAllAdapter(this, mListClass, () -> reightListRecycle(list));
        rvShow.setAdapter(mWrapper);
    }
}
