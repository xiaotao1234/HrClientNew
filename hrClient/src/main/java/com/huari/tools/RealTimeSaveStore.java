package com.huari.tools;

import android.os.Message;
import android.util.Log;

import com.huari.client.HistoryDFActivity;
import com.huari.client.PinDuanScanningActivity;
import com.huari.client.SinglefrequencyDFActivity;
import com.huari.client.SpectrumsAnalysisActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RealTimeSaveStore {
    public static int PARSE_DDF = 1;
    public static int PARSE_M = 2;
    public static int PARSE_N = 3;
    public static volatile boolean ParseFlg = false;
    public static boolean progressFlg = false;
    public static int progress = 0;
    private static Thread thread;
    private static InputStream inputStream;
    private static byte[] readWithTiem;
    private static int available;
    private static int delayTime;
    private static int frameLength;
    private static int allLength;
    private static Message message;

    public static void ParseLocalWrap(String fileName, int type, android.os.Handler handler) {
        ParseFlg = false;
        ParseLocalDdfData(fileName, type, handler);
    }

    public static void ParseLocalDdfData(String fileName, int type, android.os.Handler handler) {
        //使上一个解析线程读取到标志位停止
//重新组装一个完整的帧头
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                frameScadule();
            }

            private void frameScadule() {//对每个加了时间包头的数据帧处理
                headScadule(fileName, progress);
                while (available > 12 && MyTools.fourBytesToInt(MyTools.nigetPartByteArray(readWithTiem, 0, 3)) == 0xAAAAAAAA) {
                    delayTime = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 4, 7));
                    frameLength = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 8, 11));
                    byte[] dateFrame = new byte[frameLength];
                    Log.d("xiaothread", Thread.currentThread().getName());
                    try {
                        inputStream.read(dateFrame, 0, frameLength);
                        available = inputStream.available();
                        message = Message.obtain();
                        message.what = 121;
                        message.obj = (allLength-available) / (allLength/100);
                        handler.sendMessage(message);
                        try {
                            Thread.sleep(delayTime);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        switch (type) {
                            case 1:
                                Parse.setHandler(HistoryDFActivity.handler);
                                Parse.parseDDF(dateFrame);
                                break;
                            case 2:
                                Parse.newParseSpectrumsAnalysis(dateFrame);
                                break;
                            case 3:
                                Parse.newParsePDScan(dateFrame);
                                break;
                            default:
                                break;
                        }
                        if (progressFlg == true) {
                            inputStream.close();
                            progressFlg = false;
                            frameScadule();
                            break;
                        }
                        inputStream.read(readWithTiem, 0, 12);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            private void headScadule(String fileName, int progress) {//对数据头部的处理。
                ParseFlg = true;
                inputStream = null;
                try {
                    String a = new File(fileName).getName();
                    inputStream = SysApplication.byteFileIoUtils.readFile(File.separator + "data" + File.separator + a);
                    allLength = inputStream.available();
                    int nowstart = allLength / 100 * progress;
                    inputStream.skip(nowstart);
                    byte[] b = new byte[1];
                    inputStream.read(b);
                    int i = 0;
                    while (inputStream.available() > 0) {
                        int m = (b[0] & 0xFF);
                        if ((b[0] & 0xFF) != 0xAA) {
                            i = 0;
                        } else {
                            i++;
                            if (i == 4) {
                                break;
                            }
                        }
                        inputStream.read(b);
                    }
                } catch (Exception e) {
                    Log.d("xiao", "文件不存在");
                }
                readWithTiem = new byte[12];
                byte[] headBytes = MyTools.int2ByteArray(0xAAAAAAAA);
                try {
                    if (inputStream.available() > 8) {
                        System.arraycopy(headBytes, 0, readWithTiem, 0, 4);//重新组装一个完整的帧头
                        inputStream.read(readWithTiem, 4, 8);
                    } else {
                        ParseFlg = false;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                available = 0;
                try {
                    available = inputStream.available();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });
        thread.start();
    }

    public static long SaveAtTime(int available, byte[] info, long time, int type) {//向文件存入一个帧
        int delay = 0;
        if (time != 0) {
            delay = (int) (System.currentTimeMillis() - time);
        }
        time = System.currentTimeMillis();
        byte[] headBytes = MyTools.int2ByteArray(0xAAAAAAAA);//帧头
        byte[] timeBytes = MyTools.int2ByteArray(delay);//当前数据帧距下一个数据帧延时的时间
        byte[] lengthBytes = MyTools.int2ByteArray(available);//数据帧长度
        byte[] bytesForSave = new byte[available + 4 + 4 + 4];
        Log.d("liyuqian", String.valueOf(MyTools.fourBytesToInt(MyTools.nigetPartByteArray(headBytes, 0, 3))) + 1);
        System.arraycopy(headBytes, 0, bytesForSave, 0, 4);
        System.arraycopy(timeBytes, 0, bytesForSave, 4, 4);
        System.arraycopy(lengthBytes, 0, bytesForSave, 8, 4);
        System.arraycopy(info, 0, bytesForSave, 12, info.length);
        switch (type) {
            case 1:
                synchronized (SinglefrequencyDFActivity.queue) {
                    SinglefrequencyDFActivity.queue.offer(bytesForSave);
                }
                break;
            case 2:
                synchronized (SpectrumsAnalysisActivity.queue) {
                    SpectrumsAnalysisActivity.queue.offer(bytesForSave);
                }
                break;
            case 3:
                synchronized (PinDuanScanningActivity.queue) {
                    PinDuanScanningActivity.queue.offer(bytesForSave);
                }
                break;
            default:
                break;
        }
        return time;
    }
}
