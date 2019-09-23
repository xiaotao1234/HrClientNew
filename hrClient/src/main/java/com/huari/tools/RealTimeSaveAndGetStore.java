package com.huari.tools;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.huari.client.HistoryDFActivity;
import com.huari.client.PinDuanScanningActivity;
import com.huari.client.R;
import com.huari.client.SinglefrequencyDFActivity;
import com.huari.client.SpectrumsAnalysisActivity;
import com.huari.dataentry.Station;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class RealTimeSaveAndGetStore {
    public static int PARSE_DDF = 1;
    public static int PARSE_M = 2;
    public static int PARSE_N = 3;
    public static volatile boolean ParseFlg = false;
    public static boolean StopFlag = false;
    public static boolean PreFlag = false;
    public static boolean progressFlg = false; //标志progress的progress的回调被触发
    public static int progress = 0;
    public static Thread thread; //负责数据解析的线程
    private static InputStream inputStream;
    public static Object person = new Object();
    public static Object person1 = new Object();
    public static Object person2 = new Object();
    private static byte[] readWithTiem;
    public static int available;
    private static int delayTime;
    private static int frameLength;
    public static int allLength;
    private static Message message;
    public static int StationMessageLength;
    private Thread thread1;
    public static Thread serializeThread;
    public static byte[] bytesForSave;
    private static boolean previousFlagFind = false;
    private static int previousPosition = 0;
    public static int type = 1;
    public static String fileNameTem;
    private static Handler handler1;
    private static Thread threadone;
    private static boolean presiousThreadStart = false;
    private static boolean nextThreadStart = false;
    private static Thread threadnext;
    private static int lastLength = 0;

    interface Stopin {
        void stopclicklistener();
    }

    Stopin stopin;

    public void setStopListener(Stopin stopListener) {
        stopin = stopListener;
    }

    public static void ParseLocalWrap(String fileName, int type1, Handler handler) {
        fileNameTem = fileName;
        ParseFlg = false;
        handler1 = handler;
        ParseLocalDdfData(fileName, type1, handler);
        type = type1;
    }

    public static void ParseLocalDdfData(String fileName, int type, Handler handler) {
        //使上一个解析线程读取到标志位停止
//重新组装一个完整的帧头
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                frameScadule();
            }

            private void frameScadule() {//对每个加了时间包头的数据帧处理
                headScadule(fileName, progress);
                SchduleOneFrame();
            }

            private void SchduleOneFrame() {//对每帧数据进行处理
                while (available > 12 && MyTools.fourBytesToInt(MyTools.nigetPartByteArray(readWithTiem, 0, 3)) == 0xAAAAAAAA && ParseFlg == true) {
                    try {
                        synchronized (person) {
                            if (StopFlag == true) {
                                person.wait();//暂停数据刷到界面
                            }
                        }
                        if(PreFlag == true){
                            inputStream = SysApplication.byteFileIoUtils.readFile(File.separator + "data" + File.separator + fileNameTem);
                            inputStream.skip(StationMessageLength);//跳过文件中开始序列化Station二进制码长度的数据
                            allLength = inputStream.available();
                            inputStream.skip(allLength-available-frameLength-4);
                            PreFlag = false;
                        }
                        delayTime = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 4, 7));
                        Log.d("xiaodelaybefore", String.valueOf(delayTime));
                        delayTime = (delayTime-1)>0?(delayTime-1):delayTime;
                        Log.d("xiaodelay", String.valueOf(delayTime));
                        frameLength = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 8, 11));
                        byte[] dateFrame = new byte[frameLength];
                        Log.d("xiaothread", Thread.currentThread().getName());
                        inputStream.skip(4);
                        inputStream.read(dateFrame, 0, frameLength);
                        available = inputStream.available();
                        message = Message.obtain();
                        message.what = 121;
                        message.obj = (allLength - available) / (allLength / 100);
                        handler.sendMessage(message);
                        Thread.sleep(delayTime);
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
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            private void headScadule(String fileName, int progress) {//跳过头部和progress进度的长度及处理由于progress改变导致帧不完整这一变化
                ParseFlg = true;
                inputStream = null;
                try {
                    inputStream = SysApplication.byteFileIoUtils.readFile(File.separator + "data" + File.separator + fileName);
                    inputStream.skip(StationMessageLength);//跳过文件中开始序列化Station二进制码长度的数据
                    allLength = inputStream.available();
                    int nowstart = allLength / 100 * progress;
                    inputStream.skip(nowstart);//由于skip一段后，后面的数据不一定是一个完整的
                    // 帧，所以为了保证后面解析不出错，要去依次向后去寻找流剩余数据中的第一个包头。
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
                    }//找包头
                    readWithTiem = new byte[12];
                    byte[] headBytes = MyTools.int2ByteArray(0xAAAAAAAA);
                    if (inputStream.available() > 8) {
                        System.arraycopy(headBytes, 0, readWithTiem, 0, 4);//重新组装一个完整的帧头
                        inputStream.read(readWithTiem, 4, 8);//读出包头中的剩余数据
                        frameLength = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 8, 11));
                    } else {
                        ParseFlg = false;
                    }
                    available = 0;
                    available = inputStream.available();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            private void headScadule1(String fileName) {//跳过头部和progress进度的长度及处理由于progress改变导致帧不完整这一变化
                ParseFlg = true;
                inputStream = null;
                try {
                    inputStream = SysApplication.byteFileIoUtils.readFile(File.separator + "data" + File.separator + fileName);
                    inputStream.skip(StationMessageLength);//跳过文件中开始序列化Station二进制码长度的数据
                    allLength = inputStream.available();
                    int nowstart = allLength / 100 * progress;
                    inputStream.skip(nowstart);//由于skip一段后，后面的数据不一定是一个完整的
                    // 帧，所以为了保证后面解析不出错，要去依次向后去寻找流剩余数据中的第一个包头。
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
                    }//找包头
                    readWithTiem = new byte[12];
                    byte[] headBytes = MyTools.int2ByteArray(0xAAAAAAAA);
                    if (inputStream.available() > 8) {
                        System.arraycopy(headBytes, 0, readWithTiem, 0, 4);//重新组装一个完整的帧头
                        inputStream.read(readWithTiem, 4, 8);//读出包头中的剩余数据
                    } else {
                        ParseFlg = false;
                    }
                    available = 0;
                    available = inputStream.available();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void previousFrame(Context context) {
        if (StopFlag == false) {
            Toast.makeText(context, "请暂停后再进行逐帧数据查看", Toast.LENGTH_SHORT).show();
        } else {
            if (presiousThreadStart == false) {
                threadone = new Thread(() -> previousFramePrepare1());//第一次先启动线程
                threadone.start();
            } else {
                synchronized (person1) {
                    person1.notify();
                }
            }
        }
    }

    public static void nextFrame(Context context) {
        if (StopFlag == false) {
            Toast.makeText(context, "请暂停后再进行逐帧数据查看", Toast.LENGTH_SHORT).show();
        } else {
            if (nextThreadStart == false) {
                threadnext = new Thread(() -> nextOneFrame());
                threadnext.start();
            } else {
                synchronized (person2) {
                    person2.notify();
                }
            }
        }
    }

    public static void nextOneFrame() {//对每帧数据进行处理
        while (available > 12 && MyTools.fourBytesToInt(MyTools.nigetPartByteArray(readWithTiem, 0, 3)) == 0xAAAAAAAA && ParseFlg == true) {
            try {
                if (nextThreadStart == true) {
                    synchronized (person2) {
                        person2.wait();//暂停数据刷到界面
                    }
                }
                if(PreFlag == true){
                     inputStream.read(readWithTiem,0,12);
                     PreFlag = false;
                }
                delayTime = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 4, 7));
                frameLength = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 8, 11));
                byte[] dateFrame = new byte[frameLength];
                Log.d("xiaothread", Thread.currentThread().getName());
                inputStream.skip(4);
                inputStream.read(dateFrame, 0, frameLength);
                available = inputStream.available();
                Log.d("xiaoavliab", String.valueOf(available));
                message = Message.obtain();
                message.what = 121;
                message.obj = (allLength - available) / (allLength / 100);
                handler1.sendMessage(message);
                Thread.sleep(delayTime);
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
                inputStream.read(readWithTiem, 0, 12);
                nextThreadStart = true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void previousFramePrepare() {
        try {
            previousPosition = 0;
            int i = 0;
            int num = 203;
            for (int j = 0; j < 10; j++) {
                int index = 0;
                inputStream = SysApplication.byteFileIoUtils.readFile(File.separator + "data" + File.separator + fileNameTem);
                inputStream.skip(StationMessageLength);//跳过文件中开始序列化Station二进制码长度的数据
                allLength = inputStream.available();
                int haveRead = allLength - available - frameLength - 12;
                //起点位置应该为排除站点序列化信息头后的总长
                // -上次剩余数据长度
                // -上一个数据帧长
                // -上一个数据帧的包头长度
                Log.d("xiaohave", String.valueOf(haveRead));
                Log.d("xiaohave", String.valueOf(allLength));
                Log.d("xiaohave", String.valueOf(available));
                Log.d("xiaohave", String.valueOf(frameLength));
                if (haveRead > (j + 1) * 200) {
                    inputStream.skip(haveRead - (j + 1) * 200);//由于skip一段后，后面的数据不一定是一个完整的
                }
                Log.d("xiaolai:haveread", String.valueOf(haveRead - (j + 1) * 200));
                byte[] b = new byte[1];
                inputStream.read(b);
                while (inputStream.available() > 0 && index <= num) {
//                        int m = (b[0] & 0xFF);
                    if ((b[0] & 0xFF) != 0xAA) {
                        i = 0;
                    } else {
                        i++;
                        if (i == 4) {
                            previousPosition = inputStream.available();
                            Log.d("xiaodad", String.valueOf(previousPosition));
                        }
                    }
                    if (index > 200 && previousPosition != 0) {
                        Log.d("xiaoprepos", String.valueOf(previousPosition));
                        previousFlagFind = true;
                        break;
                    }
                    inputStream.read(b);
                    index++;
                }//找包头
                if (previousFlagFind == true) {
                    previousFlagFind = false;
                    break;
                }
            }
            readWithTiem = new byte[12];
            byte[] headBytes = MyTools.int2ByteArray(0xAAAAAAAA);
            inputStream = SysApplication.byteFileIoUtils.readFile(File.separator + "data" + File.separator + fileNameTem);
            inputStream.skip(StationMessageLength + allLength - previousPosition);
            if (inputStream.available() > 8) {
                System.arraycopy(headBytes, 0, readWithTiem, 0, 4);//重新组装一个完整的帧头
                inputStream.read(readWithTiem, 4, 8);//读出包头中的剩余数据
            } else {
                ParseFlg = false;
            }
            available = 0;
            available = inputStream.available();
            SchduleOneFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void previousFramePrepare1() {
        try {
            inputStream = SysApplication.byteFileIoUtils.readFile(File.separator + "data" + File.separator + fileNameTem);
            inputStream.skip(StationMessageLength);//跳过文件中开始序列化Station二进制码长度的数据
            allLength = inputStream.available();
            int haveRead = allLength - available - frameLength - 4;
            //起点位置应该为排除站点序列化信息头后的总长
            // -上次剩余数据长度
            // -上一个数据帧长
            // -上一个数据帧的包头的上一个包长度的字段长度
            inputStream.skip(haveRead);
            byte[] bytes = new byte[4];
            inputStream.read(bytes);
            lastLength = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(bytes, 0, 3));
            int lastStart = haveRead - 12 - lastLength - 16;
            inputStream = SysApplication.byteFileIoUtils.readFile(File.separator + "data" + File.separator + fileNameTem);
            inputStream.skip(StationMessageLength);//跳过文件中开始序列化Station二进制码长度的数据
            inputStream.skip(lastStart);
            readWithTiem = new byte[12];
            inputStream.read(readWithTiem);//现在其为上一帧读走数据帧头的状态
            available = 0;
            available = inputStream.available();
            SchduleOneFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SchduleOneFrame() {
        if (available > 12 && MyTools.fourBytesToInt(MyTools.nigetPartByteArray(readWithTiem, 0, 3)) == 0xAAAAAAAA && ParseFlg == true) {
            try {
                delayTime = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 4, 7));
                frameLength = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(readWithTiem, 8, 11));
                byte[] dateFrame = new byte[frameLength];
                Log.d("xiaothread", Thread.currentThread().getName());
                inputStream.skip(4);
                inputStream.read(dateFrame, 0, frameLength);
                available = inputStream.available();
                message = Message.obtain();
                message.what = 121;
                message.obj = (allLength - available - frameLength) / (allLength / 100);
                handler1.sendMessage(message);
                switch (type) {
                    case 1:
                        Parse.setHandler(HistoryDFActivity.handler);  //替换解析数据模块的handler，其他两个则因为没法直
                        // 接通过设置进行替换，所以抽出父类让其向下转型来让其进行自动转换
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
                synchronized (person1) {
                    presiousThreadStart = true; //更新标志位
                    PreFlag = true;
                    person1.wait();//暂停数据刷到界面
                }
                previousFramePrepare1();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void pauseOrResume(View view) {//暂停或唤醒数据解析线程
        if (StopFlag == true) {
            synchronized (person) {
                person.notify();
                StopFlag = false;
            }
            view.setBackgroundResource(R.drawable.play_icon);
        } else {
            StopFlag = true;
            view.setBackgroundResource(R.drawable.stop_icon);
        }
    }

    public static long SaveAtTime(int available, byte[] info, long time, int type) {//向文件存入一个带时间头的帧
        int delay = 0;
        if (time != 0) {
            delay = (int) (System.currentTimeMillis() - time);
        }
        time = System.currentTimeMillis();
        byte[] headBytes = MyTools.int2ByteArray(0xAAAAAAAA);//帧头
        byte[] timeBytes = MyTools.int2ByteArray(delay);//当前数据帧距下一个数据帧延时的时间
        byte[] lengthBytes = MyTools.int2ByteArray(available);//数据帧长度
        byte[] lastLengthBytes = MyTools.int2ByteArray(lastLength);//数据帧长度
        byte[] bytesForSave = new byte[available + 4 + 4 + 4 + 4];
        Log.d("liyuqian", String.valueOf(MyTools.fourBytesToInt(MyTools.nigetPartByteArray(headBytes, 0, 3))) + 1);
        System.arraycopy(headBytes, 0, bytesForSave, 0, 4);
        System.arraycopy(timeBytes, 0, bytesForSave, 4, 4);
        System.arraycopy(lengthBytes, 0, bytesForSave, 8, 4);
        System.arraycopy(lastLengthBytes, 0, bytesForSave, 12, 4);
        System.arraycopy(info, 0, bytesForSave, 16, info.length);
        switch (type) {
            case 1:
                synchronized (SinglefrequencyDFActivity.queue) {
                    SinglefrequencyDFActivity.queue.offer(bytesForSave);
                    Log.d("xiaotaonihao", "数据包来了");
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
        lastLength = available;
        return time;
    }

    public static void serializeFlyPig(Station station, String fileName, int type) {//序列化保存
        // ObjectOutputStream 对象输出流，将 flyPig 对象存储到E盘的 flyPig.txt 文件中，完成对 flyPig 对象的序列化操作
//从序列化文件读出
//文件长度
//帧头
//数据帧长度
        serializeThread = new Thread(() -> {
            // ObjectOutputStream 对象输出流，将 flyPig 对象存储到E盘的 flyPig.txt 文件中，完成对 flyPig 对象的序列化操作
            try {
                File filebase = new File(SysApplication.fileOs.forSaveFloder + File.separator + "usetemporary");
                if (!filebase.getParentFile().exists()) {
                    filebase.getParentFile().mkdirs();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(filebase);
                ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
                oos.writeObject(station);
                oos.close();
                FileInputStream fileInputStream = new FileInputStream(filebase);//从序列化文件读出
                int length = fileInputStream.available();//文件长度
                bytesForSave = new byte[length + 4 + 4];
                byte[] headBytes = MyTools.int2ByteArray(0x77777777);//帧头
                byte[] lengthBytes = MyTools.int2ByteArray(length);//数据帧长度
                System.arraycopy(headBytes, 0, bytesForSave, 0, 4);
                System.arraycopy(lengthBytes, 0, bytesForSave, 4, 4);
                fileInputStream.read(bytesForSave, 8, length);
                synchronized (ByteFileIoUtils.object) {
                    ByteFileIoUtils.object.notify();
                }
                fileInputStream.close();
                filebase.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serializeThread.start();
    }

    public static void deserializeFlyPig(String fileName, final Handler handler) {//反序列化
        Thread thread = new Thread(() -> {
            try {
                int length;
                byte[] bytes = new byte[8];
                File file = new File(SysApplication.fileOs.forSaveFloder + File.separator + "data" + File.separator + fileName);
                if (!(file.exists())) {
                    return;
                }
                InputStream inputStream = new FileInputStream(file);
                inputStream.read(bytes);
                if (!(MyTools.fourBytesToInt(MyTools.nigetPartByteArray(bytes, 0, 3)) == 0x77777777)) {
                    return;
                }
                length = MyTools.bytesToIntLittle(MyTools.nigetPartByteArray(bytes, 4, 7));
                byte[] bytes1 = new byte[length];
                StationMessageLength = length + 8;
                inputStream.read(bytes1);
                InputStream inputStream1 = new ByteArrayInputStream(bytes1);
                ObjectInputStream ois = new ObjectInputStream(inputStream1);
                Station station = (Station) ois.readObject();
                Message message = Message.obtain();
                message.obj = station;
                message.what = 34;
                handler.sendMessage(message);
                System.out.println("FlyPig 对象反序列化成功！");
                Log.d("xiaotaohao", "序列化");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
