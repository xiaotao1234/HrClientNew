package com.huari.client;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;

public class IquareActivity extends AppCompatActivity {
    LinearLayout wordView;
    LinearLayout excelView;
    LinearLayout graphView;
    private String fileUrl=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+File.separator+"test.doc";//远程文档地址
    private String fileUrl1=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+File.separator+"test.xlsx";//远程文档地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iquare);
        view();
    }

    private void view() {
        wordView = findViewById(R.id.word_view);
        excelView = findViewById(R.id.excel_view);
        graphView = findViewById(R.id.graph_view);
        wordView.setOnClickListener(v -> FileDisplayActivity.actionStart(IquareActivity.this, fileUrl,null));
        excelView.setOnClickListener(v -> FileDisplayActivity.actionStart(IquareActivity.this, fileUrl1,null));
        graphView.setOnClickListener(v -> startActivity(new Intent(IquareActivity.this,GraphActivity.class)));
    }
}
