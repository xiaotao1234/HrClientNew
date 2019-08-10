package com.huari.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import struct.JavaStruct;

import com.huari.commandstruct.FunctionFrame;
import com.huari.tools.Parse;

import android.app.IntentService;
import android.content.Intent;

public class TaskService extends IntentService {

	static Socket socket;
	static OutputStream ops;
	static InputStream ins;

	public static void send(byte[] b) {
		try {
			ops.write(b);
			ops.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public TaskService() {
		super("");
	}

	public TaskService(String name) {
		super(name);
		System.out.println("启动Intent服务");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			System.out.println("服务启动");
			try {
				socket = new Socket("192.168.0.109", 5000);
			} catch (Exception e) {
				System.out.println("Task连接异常");
			}
			ops = socket.getOutputStream();
			FunctionFrame ff = new FunctionFrame();
			ff.length = 1;
			ff.functionNum = 1;
			byte[] b = null;
			b = JavaStruct.pack(ff);
			ops.write(b);
			ops.flush();
			System.out.println("第一段已发送完");
			FunctionFrame f1 = new FunctionFrame();
			f1.length = 1;
			f1.functionNum = 13;
			byte[] b1 = JavaStruct.pack(f1);
			ops.write(b1);
			ops.flush();
			System.out.println("即将启动线程");

			while (true) {
				try {

					System.out.println("接收线程开始");
					if (socket == null) {
						System.out.println("socket为空");
					}
					InputStream ins = socket.getInputStream();
					int a = ins.available();
					if (a > 0) {
						byte[] info = new byte[a];
						ins.read(info);
						switch (info[8]) {
						case 6:// 监测站
							Parse.parseMonitoringStation(info);
							break;
						case 60:// 无人站
							Parse.parseUnManedStation(info);
							break;
						case 61:// 无人站更新
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Taskldlkdkdkdk 出现异常");
		}
	}

}
