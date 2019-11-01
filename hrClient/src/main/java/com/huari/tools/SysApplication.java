package com.huari.tools;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.huari.NetMonitor.WindowHelper;
import com.huari.client.R;
import com.huari.dataentry.PinDuanSettingSave;
import com.huari.dataentry.Station;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;

public class SysApplication extends Application {


    private List<Activity> mList = new LinkedList<Activity>();
    private static SysApplication instance;
    public static FileOsImpl fileOs;
    public static TimeTools timeTools;
    public static PermissionManager permissionManager;
    public static ByteFileIoUtils byteFileIoUtils;
    private int activityCount;
    public static PinDuanSettingSave settingSave;
    public static boolean SocketFlag = false;

    @Override
    public void onCreate() {
        super.onCreate();
        fileOs = FileOsImpl.getInstance();
        fileOs.getOsDicteoryPath(getApplicationContext());
        timeTools = TimeTools.getInstance();
        permissionManager = PermissionManager.getInastance();
        byteFileIoUtils = ByteFileIoUtils.getInstance();
        settingSave = new PinDuanSettingSave();
        Bmob.initialize(getApplicationContext(), "5352155de67348c988d054ca0fe25d95");
// 使用推送服务时的初始化操作
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                Log.d("xiao", "in");
                if (e == null) {
                    Log.d("xiao", bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
                } else {
                    Log.d("xiao", "错误");
                }
            }
        });
        BmobPush.startWork(getApplicationContext());

        createNotificationChannel();

        SDKInitializer.initialize(this);

    }

    @TargetApi(28)
    private void forceSendRequestByMobileData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NET_CAPABILITY_INTERNET);
        //强制使用蜂窝数据网络-移动数据
        builder.addTransportType(TRANSPORT_CELLULAR);
        NetworkRequest build = builder.build();
        connectivityManager.requestNetwork(build, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(final Network network) {
                super.onAvailable(network);
                try {
                    Bmob.initialize(getApplicationContext(), "5352155de67348c988d054ca0fe25d95");
// 使用推送服务时的初始化操作
                    BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
                        @Override
                        public void done(BmobInstallation bmobInstallation, BmobException e) {
                            Log.d("xiao", "in");
                            if (e == null) {
                                Log.d("xiao", bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
                            } else {
                                Log.d("xiao", "错误");
                            }
                        }
                    });
                    BmobPush.startWork(getApplicationContext());
                } catch (Exception e) {

                }

            }
        });
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}