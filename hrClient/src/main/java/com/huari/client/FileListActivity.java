package com.huari.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.huari.adapter.FileListAdapter;
import com.huari.adapter.HistoryShowWindowAdapter;
import com.huari.tools.SysApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    FileListAdapter fileListAdapter;
    RecyclerView recyclerView;

    List<String> filesname = new ArrayList<>();
    List<File> files = new ArrayList<>();
    private TextView currentFloderName;
    private ImageView searhFile;
    private ImageView addfloderButton;
    private ImageView settingButton;
    private ImageView back;
    private LinearLayout linearLayout;

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
        linearLayout = findViewById(R.id.file_max);
        recyclerView = findViewById(R.id.list_files);
        back = findViewById(R.id.back);
        currentFloderName = findViewById(R.id.file_diecetory);
        searhFile = findViewById(R.id.searh_file);
        addfloderButton = findViewById(R.id.add_floder);
        settingButton = findViewById(R.id.setting);
        addfloderButton.setOnClickListener(v -> popWindow(addfloderButton));
        searhFile.setOnClickListener(v -> startActivity(new Intent(FileListActivity.this, SearhFileActivity.class)));
        settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(FileListActivity.this, SettingActivity.class);
            startActivity(intent);
        });
        SysApplication.fileOs.setCurrentFloder(new File(SysApplication.fileOs.forSaveFloder+ File.separator + "data"));
//        SysApplication.fileOs.setCurrentFloder(SysApplication.fileOs.getOsDicteoryPath(this));
        files = SysApplication.fileOs.getFiles();
        filesname = SysApplication.fileOs.getFilesName();
        recyclerView.setSystemUiVisibility(View.INVISIBLE);
        currentFloderName.setText(SysApplication.fileOs.getCurrentFloder().getAbsolutePath());
        fileListAdapter = new FileListAdapter(filesname, this, files,currentFloderName);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileListAdapter);
        back.setOnClickListener(v -> finish());
    }

    private void popWindow(View view) {
        // TODO: 2016/5/17 构建一个popupwindow的布局
        View popupView = FileListActivity.this.getLayoutInflater().inflate(R.layout.popwindow_add_floder, null);
        popupView.setPadding(50,0,50,0);
        // TODO: 2016/5/17 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
        // TODO: 2016/5/17 创建PopupWindow对象，指定宽度和高度
        PopupWindow window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        window.showAtLocation(linearLayout,Gravity.CLIP_VERTICAL,0,0);
        // TODO: 2016/5/17 设置动画
        window.setAnimationStyle(R.style.popup_window_anim);
        // TODO: 2016/5/17 设置背景颜色
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // TODO: 2016/5/17 设置可以获取焦点
        window.setFocusable(true);
        // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(true);
        // TODO：更新popupwindow的状态
        window.update();
        // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
        window.showAsDropDown(view, 0, 0, Gravity.BOTTOM);
        EditText filename = popupView.findViewById(R.id.user_edit);
        TextView newFile = popupView.findViewById(R.id.add_button);
        newFile.setOnClickListener(v -> {
            if(filename.getText().toString().length() == 0){
                Toast.makeText(FileListActivity.this,"文件夹名不能为空",Toast.LENGTH_SHORT).show();
            }else {
                File file = new File(SysApplication.fileOs.getCurrentFloder()+File.separator+filename.getText());
                file.mkdirs();
                window.dismiss();
                fileListAdapter.refreshList();
            }
        });
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
            File file = SysApplication.fileOs.popStack();
            SysApplication.fileOs.setCurrentFloder(file);
            fileListAdapter.files = SysApplication.fileOs.getFiles();
            fileListAdapter.filesname = SysApplication.fileOs.getFilesName();
            currentFloderName.setText(file.getAbsolutePath());
            fileListAdapter.notifyDataSetChanged();
        }
    }
}

