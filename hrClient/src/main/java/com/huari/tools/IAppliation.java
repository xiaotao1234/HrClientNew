package com.huari.tools;

import android.app.Application;


public class IAppliation extends Application {
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
}
