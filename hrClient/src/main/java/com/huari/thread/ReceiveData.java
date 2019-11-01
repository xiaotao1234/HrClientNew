package com.huari.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.util.Log;

import com.huari.dataentry.*;
import com.huari.tools.FileOsImpl;
import com.huari.tools.Parse;

import org.greenrobot.eventbus.EventBus;

public class ReceiveData extends Thread {

    Socket s;
    HashMap<String, Station> stationmap;
    Intent intentPinpu = null;
    boolean isFull = false;
    boolean isFirst = true;
    byte[] info;
    int haves;
    int infolength;

    public ReceiveData(Socket ss) {
        s = ss;
        try {
            s.setReceiveBufferSize(655360);
            s.setSoTimeout(1000);
            s.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        intentPinpu = new Intent();
        intentPinpu.setAction("com.huari.client.p");
    }

    public void run() {
        InputStream inputstream = null;
        OutputStream outputStream = null;
        while (true) {
            try {
                Thread.sleep(2);
                if (!s.getKeepAlive()) {
                    EventBus.getDefault().postSticky(new SocketStopEvent(true));
                } else {
                    inputstream = s.getInputStream();
                    outputStream = s.getOutputStream();
                }
                int available = inputstream.available();
                if (available > 4) {
                    byte[] b = new byte[available];
                    inputstream.read(b);
                    ParseDeviceSettingInfo();//将本地化的设置信息加载进来，和读取的台站信息进行合并，设置保存的生效之地
                    Parse.parseReceiveInfo(b);
                }
                try {
                    outputStream.write(1);
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("xiaofa","错误");
                }
            } catch (Exception e) {
                System.out.println("接收初始数据时异常");
            }
        }
    }

    private void ParseDeviceSettingInfo() throws IOException, ClassNotFoundException {
        File file = new File(FileOsImpl.forSaveFloder + File.separator + "data" + File.separator + "ForSaveStation");
        if (!(file.exists())) {
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        FileOsImpl.simpleStations = (List<SimpleStation>) objectInputStream.readObject();
    }
}
