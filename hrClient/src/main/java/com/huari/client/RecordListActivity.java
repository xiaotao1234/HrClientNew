package com.huari.client;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.huari.adapter.SimpleTestAdapter;
import com.huari.ui.MyNestedScrollView;
import com.huari.adapter.TagCloudAdapter;
import com.huari.ui.TagCloudView;

public class RecordListActivity extends AppCompatActivity {
    private String TAG = Main2Activity.class.getSimpleName();
    TagCloudView tagCloudView;
    RecyclerView rv;
    MyNestedScrollView nsv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//        rv = findViewById(R.id.rv);
//        nsv = findViewById(R.id.nsv);
//        rv.setLayoutManager(new LinearLayoutManager(this));
//        rv.setAdapter(new SimpleTestAdapter(this));
//        tagCloudView = findViewById(R.id.tag_cloud);
//        tagCloudView.setBackgroundColor(Color.parseColor("#000000"));
//        final TagCloudAdapter adapter = new TagCloudAdapter(new String[100],rv);
//        tagCloudView.setAdapter(adapter);
//        final View rootView = findViewById(android.R.id.content);
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                View topView1 = findViewById(R.id.tag_cloud);
//                nsv.setMyScrollHeight(topView1.getHeight());
//                nsv.scrollTo(0, topView1.getHeight());
//                int rvNewHeight = rootView.getHeight();
//                rv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rvNewHeight));
//            }
//        });
//
//        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                Log.d("xiao", String.valueOf(newState));
//                if (adapter.mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
//                    Log.d("xiao","recycle");
//                    adapter.mShouldScroll = false;
//                    adapter.smoothMoveToPosition(rv, adapter.mToPosition);
//                }
//            }
//        });
    }
}
