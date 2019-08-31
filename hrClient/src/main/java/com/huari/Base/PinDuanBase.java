package com.huari.Base;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class PinDuanBase extends AppCompatActivity {//抽出一个基类是为了解析功能的复用
    public static String ScanType;
    public static Handler handler;
    public static int SCANNINGDATA = 0x2;
    public static int SCANNINGDATANO = 0x3;
    public static int SCANNINGDATAFSCAN = 0x4;
    public static int SCANNINGDATAFSCANNO = 0x5;
}
