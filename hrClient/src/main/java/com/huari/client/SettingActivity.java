package com.huari.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.huari.tools.SysApplication;

public class SettingActivity extends AppCompatActivity {
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        view = findViewById(R.id.reset_file_directory);
        view.setOnClickListener(v -> setView());
        view.setSystemUiVisibility(View.INVISIBLE);
    }

    public void setView() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("RootDirectory", null);
        editor.commit();
        SysApplication.fileOs.setCurrentFloder(SysApplication.fileOs.getOsDicteoryPath(this));
        SysApplication.fileOs.getFileStack().clear();
    }
}
