package com.huari.client;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.huari.adapter.SearchFileListAdapter;
import com.huari.dataentry.FileSearchData;
import com.huari.dataentry.FileSearchMassage;
import com.huari.tools.SysApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SearhFileActivity extends AppCompatActivity {

    SearchFileListAdapter searchFileListAdapter;
    int searchTextLength;
    private RecyclerView recyclerView;
    private EditText editText;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searh_file);
        init();
        EventBus.getDefault().register(this);
        searchFileListAdapter = new SearchFileListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchFileListAdapter);
        recyclerView.setSystemUiVisibility(View.INVISIBLE);
    }

    private void init() {
        recyclerView = findViewById(R.id.search_listview);
        editText = findViewById(R.id.search_edit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchFileListAdapter.flag = true;
                searchTextLength = s.toString().toLowerCase().trim().length();
                searchFileListAdapter.searchResultLength = searchTextLength;
                SysApplication.fileOs.searh(SysApplication.fileOs.getCurrentFloder(), s.toString().toLowerCase().trim());
                Log.d("xiao",SysApplication.fileOs.getCurrentFloder().getAbsolutePath());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void showSearchResult(FileSearchMassage fileSearchResult) {
        List<FileSearchData> files = fileSearchResult.getFiles();
        List<String> filename = new ArrayList<>();
        for (FileSearchData file : files) {
            filename.add(file.getFile().getName());
        }
        searchFileListAdapter.fileList = files;
        searchFileListAdapter.fileNames = filename;
        searchFileListAdapter.notifyDataSetChanged();
    }
}
