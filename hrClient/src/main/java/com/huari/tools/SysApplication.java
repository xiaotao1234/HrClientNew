package com.huari.tools;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class SysApplication extends Application {


	private List<Activity> mList = new LinkedList<Activity>();
	private static SysApplication instance;
	public static FileOsImpl fileOs;
	public static TimeTools timeTools;
	public static PermissionManager permissionManager;
	public static ByteFileIoUtils byteFileIoUtils;

	@Override
	public void onCreate() {
		super.onCreate();
		fileOs = FileOsImpl.getInstance();
		fileOs.getOsDicteoryPath(getApplicationContext());
		timeTools = TimeTools.getInstance();
		permissionManager = PermissionManager.getInastance();
		byteFileIoUtils = ByteFileIoUtils.getInstance();
	}



	public synchronized static SysApplication getInstance() {
		if (null == instance) {
			instance = new SysApplication();
		}
		return instance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}