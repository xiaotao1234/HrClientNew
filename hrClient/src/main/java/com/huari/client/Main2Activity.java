package com.huari.client;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

public class Main2Activity extends AppCompatActivity {
    CardView cardView;
    CardView cardView1;
    CardView recentCardView;
    CardView queryInfo;
    CardView playerCard;
    NestedScrollView scrollView;
    ViewGroup v;
    private int buttoncount;
    private String fileUrl=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+File.separator+"test.doc";//远程文档地址
    private String fileUrl1=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+File.separator+"test.xlsx";//远程文档地址

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
        cardView = findViewById(R.id.ppu);
        cardView.setSystemUiVisibility(View.INVISIBLE);
        cardView.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this,
                RecordListActivity.class)));
        cardView1 = findViewById(R.id.p);
        cardView1.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this,
                AllRecordQueryActivity.class)));
        recentCardView = findViewById(R.id.recent_card);
        recentCardView.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this,
                RecordListActivity.class)));
        queryInfo = findViewById(R.id.query_info);
        queryInfo.setOnClickListener(v ->
                startActivity(new Intent(Main2Activity.this,IquareActivity.class))
//                FileDisplayActivity.actionStart(Main2Activity.this, fileUrl1,null)
        );
        playerCard = findViewById(R.id.player_card);
        playerCard.setOnClickListener(v -> startActivity(new Intent(Main2Activity.this,
                FileListActivity.class)));
//        v = findViewById(R.id.main_group);
//        for (int i = 0; i < v.getChildCount(); i++) {
//            ViewGroup sonviewGroup = (ViewGroup) v.getChildAt(i);
//            for (int m = 0; m < sonviewGroup.getChildCount(); m++) {
//                buttoncount = buttoncount + 1;
//                final String s = buttoncount + "";
//                Button bn = (Button) sonviewGroup.getChildAt(m);
//                final String funcname = bn.getText().toString();
//                bn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent();
//                        intent.setAction("function" + s);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("from", "FUN");
//                        bundle.putString("functionname", funcname);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        // MainActivity.this.finish();
//                    }
//                });
//            }
//        }
    }
}
