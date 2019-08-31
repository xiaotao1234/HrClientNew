package com.huari.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huari.client.AllRecordQueryActivity;
import com.huari.client.DzActivity;
import com.huari.client.FileListActivity;
import com.huari.client.HistoryDataActivity;
import com.huari.client.IquareActivity;
import com.huari.client.Main2Activity;
import com.huari.client.Main3Activity;
import com.huari.client.R;
import com.huari.client.RecordListActivity;
import com.huari.client.ServerManagerActivity;
import com.huari.dataentry.GlobalData;
import com.huari.service.MainService;
import com.huari.ui.LinView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;

public class OfflineFragment extends Fragment {
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
    Activity activity;
    Context context;

    public OfflineFragment() {

    }

    @SuppressLint("ValidFragment")
    public OfflineFragment(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main22, container, false);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        serviceIntent = new Intent();
        serviceIntent.setAction("com.huari.service.mainservice");
        serviceIntent.setPackage(context.getPackageName());
        dhMoudle = view.findViewById(R.id.dh_moudle);
        cardView = view.findViewById(R.id.ppu);
        cardView.setSystemUiVisibility(View.INVISIBLE);
        cardView.setOnClickListener(v -> startActivity(new Intent(context,
                Main3Activity.class)));
        cardView1 = view.findViewById(R.id.pp);
        cardView1.setOnClickListener(v -> skip("频段扫描", 3));
        recentCardView = view.findViewById(R.id.recent_card);
        recentCardView.setOnClickListener(v -> startActivity(new Intent(context,
                RecordListActivity.class)));
        fileB = view.findViewById(R.id.file_b);
        fileB.setOnClickListener(v -> startActivity(new Intent(context,
                AllRecordQueryActivity.class)));
        queryInfo = view.findViewById(R.id.query_info);
        queryInfo.setOnClickListener(v ->
                startActivity(new Intent(context, IquareActivity.class))
        );
        playerCard = view.findViewById(R.id.player_card);
        playerCard.setOnClickListener(v -> startActivity(new Intent(context,
                HistoryDataActivity.class)));
        pinPuFX = view.findViewById(R.id.pinpufx);
        pinPuFX.setOnClickListener(v -> skip("频谱分析", 2));
        managerCard = view.findViewById(R.id.server_manager);
        managerCard.setOnClickListener(v -> startActivity(new Intent(context, ServerManagerActivity.class)));
        fileManager = view.findViewById(R.id.file);
        fileManager.setOnClickListener(v -> startActivity(new Intent(context, FileListActivity.class)));
        bombCard = view.findViewById(R.id.bomb);
        bombCard.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                clickbomb();
            }
        });
        danCard = view.findViewById(R.id.danpin);
        danCard.setOnClickListener(v -> skip("单频测向", 2));
        DataHuifDanpin = view.findViewById(R.id.data_get_danpin);
        DataHuifDanpin.setOnClickListener(v -> {
        });
        mapCard = view.findViewById(R.id.p);
        mapCard.setOnClickListener(v -> skip("地图显示", 4));
        dhMoudle.setOnClickListener(v -> startActivity(new Intent(context, DzActivity.class)));
        return view;
    }

    private void skip(String name, int i) {
        if (GlobalData.toCreatService == false) {
            new Thread() {
                public void run() {
                    context.startService(serviceIntent);
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

    public void clickbomb() {
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
    }
}
