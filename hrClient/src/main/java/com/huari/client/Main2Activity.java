package com.huari.client;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.huari.dataentry.GlobalData;
import com.huari.service.MainService;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;

public class Main2Activity extends AppCompatActivity {
    CardView cardView;
    CardView cardView1;
    CardView recentCardView;
    CardView queryInfo;
    CardView playerCard;
    CardView managerCard;
    CardView dhMoudle;
    CardView bombCard;
    CardView danCard;
    CardView mapCard;
    CardView DataHuifDanpin;
    CardView fileManager;
    CardView pinPuFX;
    CardView fileB;
    NestedScrollView scrollView;
    ViewGroup v;
    Intent serviceIntent;
    private int buttoncount;
    private String fileUrl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "test.doc";//远程文档地址
    private String fileUrl1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "test.xlsx";//远程文档地址

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    clickbomb();
                } else {

                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main22);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        serviceIntent = new Intent();
        serviceIntent.setAction("com.huari.service.mainservice");
        serviceIntent.setPackage(getPackageName());
        dhMoudle = findViewById(R.id.dh_moudle);
        cardView = findViewById(R.id.ppu);
        cardView.setSystemUiVisibility(View.INVISIBLE);
        cardView.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this,
                RecordListActivity.class)));
        cardView1 = findViewById(R.id.pp);
        cardView1.setOnClickListener(v -> skip("频段扫描",3));
        recentCardView = findViewById(R.id.recent_card);
        recentCardView.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this,
                RecordListActivity.class)));
        fileB = findViewById(R.id.file_b);
        fileB.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this,
                AllRecordQueryActivity.class)));
        queryInfo = findViewById(R.id.query_info);
        queryInfo.setOnClickListener(v ->
                        startActivity(new Intent(Main2Activity.this, IquareActivity.class))
        );
        playerCard = findViewById(R.id.player_card);
        playerCard.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this,
                HistoryDataActivity.class)));
        pinPuFX = findViewById(R.id.pinpufx);
        pinPuFX.setOnClickListener(v -> skip("频谱分析", 2));
        managerCard = findViewById(R.id.server_manager);
        managerCard.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this, ServerManagerActivity.class)));
        fileManager = findViewById(R.id.file);
        fileManager.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this, FileListActivity.class)));
        bombCard = findViewById(R.id.bomb);
        bombCard.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                clickbomb();
            }
        });
        danCard = findViewById(R.id.danpin);
        danCard.setOnClickListener(v -> skip("单频测向", 2));
        DataHuifDanpin = findViewById(R.id.data_get_danpin);
        DataHuifDanpin.setOnClickListener(v -> {
        });
        mapCard = findViewById(R.id.p);
        mapCard.setOnClickListener(v -> skip("地图显示",4));
        dhMoudle.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this, DzActivity.class)));
    }

    private void skip(String name, int i) {
        if (GlobalData.toCreatService == false) {
            new Thread() {
                public void run() {
                    startService(serviceIntent);
                    MainService.startFunction();
                    GlobalData.toCreatService = true;
                }
            }.start();
        }
        Intent intent = new Intent();
        intent.setAction("function" + i);
        Bundle bundle = new Bundle();
        bundle.putString("from", "FUN");
        bundle.putString("functionname", name);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void clickbomb() {
        BmobPushManager bmobPushManager = new BmobPushManager();
        bmobPushManager.pushMessageAll("消息内容", new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("xiao", "推送成功！");
                } else {
                    Log.d("xiao", "异常：");
                }
            }
        });
//        forceSendRequestByMobileData();
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
                    BmobPushManager bmobPushManager = new BmobPushManager();
                    bmobPushManager.pushMessageAll("消息内容", new PushListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.d("xiao", "推送成功！");
                            } else {
                                Log.d("xiao", "异常：");
                            }
                        }
                    });
                } catch (Exception e) {

                }

            }
        });
    }
}
