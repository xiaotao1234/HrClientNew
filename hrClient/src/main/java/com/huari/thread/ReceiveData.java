package com.huari.thread;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import android.content.Intent;
import com.huari.dataentry.*;
import com.huari.tools.Parse;

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
		} catch (SocketException e) {
			e.printStackTrace();
		}
		intentPinpu = new Intent();
		intentPinpu.setAction("com.huari.client.p");
	}

	public void run() {
		InputStream inputstream = null;
		while (true) {
			try {
				if (s == null) {
					System.out.println("Socket为空");
				} else if (s != null) {
					inputstream = s.getInputStream();
				}
				int available = inputstream.available();

				if (available > 4) {
					byte[] b = new byte[available];
					inputstream.read(b);
					
					Parse.parseReceiveInfo(b);
				}
			} catch (Exception e) {
				System.out.println("接收初始数据时异常");
			}
		}
	}

}
