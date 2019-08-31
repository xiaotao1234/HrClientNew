package com.huari.Base;

import android.media.AudioTrack;
import android.os.Handler;

import java.io.FileOutputStream;

import androidx.appcompat.app.AppCompatActivity;

public abstract class AnalysisBase extends AppCompatActivity {//抽出一个基类是为了解析功能的复用
    public static Handler handle;
    public static int DIANPINGDATA = 0x987;
    public static int PINPUDATA = 0x10;
    public static int ITUDATA = 0x3;
    public static int tempbufferindex = 0;
    public static byte[] tempAudioBuffer;
    public static byte[] audioBuffer;
    public static int audioindex = 0;
    public static int audioBuffersize;
    public static AudioTrack at;
    public static boolean isRecording = false;
    public static FileOutputStream fos = null;
    public static Object synObject = new Object();

    public static void createAudioPlay(int frequency, byte bitcounts, short channelcount, int framelength) {
    }
}
