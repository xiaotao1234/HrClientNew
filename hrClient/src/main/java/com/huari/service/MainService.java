package com.huari.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import struct.JavaStruct;

//import com.huari.client.MainActivity;
import com.huari.client.CircleActivity;
import com.huari.client.Login2Activity;
import com.huari.client.LoginActivity;
import com.huari.commandstruct.FunctionFrame;
import com.huari.dataentry.GlobalData;
import com.huari.thread.ReceiveData;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends Service {

	static Socket socket;
	static OutputStream ops;
	static InputStream ins;
	static boolean will = true, have = false;

	public static void send(byte[] b) {
		try {
			if (ops == null) {
				System.out.println("OPS是空的");
			}
			if (b == null) {
				System.out.println("命令是空的");
			}
			ops.write(b);
			ops.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public static void createSocket( )
	// {
	//
	// try {
	// socket=new Socket(GlobalData.mainIP,5000);
	// socket.setSoTimeout(5000);
	// } catch (UnknownHostException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// try{
	// ops = socket.getOutputStream();
	// FunctionFrame ff=new FunctionFrame();
	// ff.length=1;
	// ff.functionNum=1;
	// byte[] b=null;
	// b=JavaStruct.pack(ff);
	// ops.write(b);
	// ops.flush();
	// System.out.println("第一段已发送完");
	// FunctionFrame f1=new FunctionFrame();
	// f1.length=1;
	// f1.functionNum=13;
	// byte[] b1=JavaStruct.pack(f1);
	// ops.write(b1);
	// ops.flush();
	// }
	// catch(Exception e)
	// {
	//
	// }

	// }

	public static void stopFunction() {
		try {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void startFunction() {
		int attemp = 3;
		while (socket == null && attemp != 0) {
			try {
				socket = new Socket(GlobalData.mainIP, GlobalData.port1);
				socket.setSoTimeout(5000);
				ops = socket.getOutputStream();
				//MainActivity.handler.sendEmptyMessage(MainActivity.LINKSUCCESS);
				Login2Activity.handler.sendEmptyMessage(LoginActivity.LINKSUCCESS);
			} catch (Exception e) {
				//MainActivity.handler.sendEmptyMessage(MainActivity.LINKFAILED);
				Login2Activity.handler.sendEmptyMessage(LoginActivity.LINKFAILED);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				attemp = attemp - 1;
				continue;
			}
			
			try {
				FunctionFrame ff = new FunctionFrame();
				ff.length = 1;
				ff.functionNum = 1;
				byte[] b = null;
				b = JavaStruct.pack(ff);
				ops.write(b);
				ops.flush();
				System.out.println("第一段已发送完");
//				FunctionFrame f1 = new FunctionFrame();
//				f1.length = 1;
//				f1.functionNum = 13;
//				byte[] b1 = JavaStruct.pack(f1);
//				ops.write(b1);
//				ops.flush();
				new ReceiveData(socket).start();
			} catch (Exception e) {

			}
		}
	}

	@Override
	public void onCreate() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
