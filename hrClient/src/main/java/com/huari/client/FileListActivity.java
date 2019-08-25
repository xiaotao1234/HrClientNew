package com.huari.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.huari.adapter.FileListAdapter;
import com.huari.tools.SysApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    FileListAdapter fileListAdapter;
    List<String> filesname = new ArrayList<>();
    List<File> files = new ArrayList<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(FileListActivity.this, "未获得文件读取权限", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.list_files);
        ImageView back = findViewById(R.id.back);
        TextView currentFloderName = findViewById(R.id.file_diecetory);
        ImageView searhFile = findViewById(R.id.searh_file);
        ImageView addfloderButton = findViewById(R.id.add_floder);
        ImageView settingButton = findViewById(R.id.setting);
        addfloderButton.setOnClickListener(v -> {
            Intent intent = new Intent(FileListActivity.this, FileAbout.class);
            startActivity(intent);
        });
        searhFile.setOnClickListener(v -> startActivity(new Intent(FileListActivity.this, SearhFileActivity.class)));
        settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(FileListActivity.this, SettingActivity.class);
            startActivity(intent);
        });
        SysApplication.fileOs.setCurrentFloder(SysApplication.fileOs.getOsDicteoryPath(this));
        files = SysApplication.fileOs.getFiles();
        filesname = SysApplication.fileOs.getFilesName();
        recyclerView.setSystemUiVisibility(View.INVISIBLE);
        currentFloderName.setText(SysApplication.fileOs.getCurrentFloder().getAbsolutePath());
        fileListAdapter = new FileListAdapter(filesname, this, files);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileListAdapter);
        back.setOnClickListener(v -> finish());
    }

    @Override
    protected void onPause() {
        super.onPause();
        SysApplication.fileOs.pushStack(SysApplication.fileOs.getCurrentFloder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SysApplication.permissionManager.requestPermission(FileListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1, () -> init());
        if (!SysApplication.fileOs.fileStack.isEmpty()) {
            SysApplication.fileOs.setCurrentFloder(SysApplication.fileOs.popStack());
        }
    }

    @Override
    public void onBackPressed() {
        if (SysApplication.fileOs.getFileStack().isEmpty() == true) {
            super.onBackPressed();
        } else {
            SysApplication.fileOs.setCurrentFloder(SysApplication.fileOs.popStack());
            fileListAdapter.files = SysApplication.fileOs.getFiles();
            fileListAdapter.filesname = SysApplication.fileOs.getFilesName();
            fileListAdapter.notifyDataSetChanged();
        }

    }
}

