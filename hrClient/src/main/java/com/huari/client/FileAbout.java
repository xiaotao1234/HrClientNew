package com.huari.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.tools.SysApplication;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class FileAbout extends AppCompatActivity {
    String fileContent;
    private String fileName;
    Handler handler;
    private TextView textView;
    private EditText editTextFileName;
    private EditText editTextFileName1;
    private Button saveButton;
    private Button readButton;
    private Button stopbutton;
    private Button gcbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_about);
        init();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        textView.setText(Arrays.toString((byte[]) msg.obj));
                        Toast.makeText(FileAbout.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        saveButton.setOnClickListener(v -> {
            fileContent = editTextFileName.getText().toString();
            byte[] bytes = fileContent.getBytes();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fileName = df.format(new Date()).replaceAll(" ", "|");
            Log.d("xiaotime", fileName);
            bytes = new byte[]{'a','b','c','b','c','b','c','b','c','b','c','b','c'};
            SysApplication.byteFileIoUtils.writeBytesToFile(fileName, bytes);
        });
        stopbutton.setOnClickListener(v -> {
            if (SysApplication.byteFileIoUtils.thread != null) {
                SysApplication.byteFileIoUtils.thread.interrupt();
            }
        });
        readButton.setOnClickListener(v -> {
            SysApplication.byteFileIoUtils.toByteArray3("2019-08-05|15:37:15", handler,156);
        });
        gcbutton.setOnClickListener(v -> {
            byte[] bytes =new byte[]{'a','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c','a','b','c','b','c','b','c','b','c','b','c','b','c','a','b','c','b','c','b','c','b'
                    ,'c','b','c','b','c'};
            textView.setText(Arrays.toString(bytes));
        });
    }

    private void init() {
        textView = findViewById(R.id.show);
        editTextFileName1 = findViewById(R.id.file_name);
        saveButton = findViewById(R.id.save_file);
        readButton = findViewById(R.id.read_file);
        stopbutton = findViewById(R.id.stop);
        gcbutton = findViewById(R.id.gc);
    }
}