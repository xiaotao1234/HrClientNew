package com.huari.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huari.client.HistoryAnalysisActivity;
import com.huari.client.HistoryDFActivity;
import com.huari.client.HistoryPinDuanActivity;
import com.huari.client.PlayerActivity;
import com.huari.client.R;
import com.huari.dataentry.MessageEvent;
import com.huari.dataentry.recentContent;
import com.huari.ui.TagsAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @Version:
 * @Author:
 * @CreateTime:
 * @Descrpiton:
 */
public class TagCloudAdapter extends TagsAdapter {
    private List<String> mList = new ArrayList<>();
    List<recentContent> list;
    RecyclerView recyclerView;
    Context context;

    public TagCloudAdapter(List<recentContent> list, RecyclerView recyclerView, Context context) {
        this.context = context;
        mList.clear();
        this.recyclerView = recyclerView;
        this.list = list;
        if (list.size() != 0) {
            for (recentContent recentContent1 : list) {
                String filename = recentContent1.getFilename();
                if (filename.length() > 19) {
                    if(recentContent1.getType()==4){
                        filename = filename.replaceAll("_",":");
                    }
                    mList.add(filename.substring(14, 19));
                }
            }
        } else {
            mList.add("暂无数据");
        }
    }

    //返回Tag数量
    @Override
    public int getCount() {
        return mList.size();
    }

    //返回每个Tag实例
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public View getView(Context context, final int position, ViewGroup parent) {
        final TextView tv = new TextView(context);
        float dp_20 = context.getResources().getDimension(R.dimen.dp_30);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams((int) dp_20, (int) dp_20);
        float dp_10 = context.getResources().getDimension(R.dimen.dp_4);
        tv.setLayoutParams(lp);
        tv.setText(mList.get(position));
        tv.setTextSize(dp_10);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(v -> {
            smoothMoveToPosition(recyclerView, position);
            if (list.size() != 0) {
                Intent intent;
                switch (getTypename(position)) {
                    case "单频测量":
                        intent = new Intent(context, HistoryDFActivity.class);
                        break;
                    case "频谱分析":
                        intent = new Intent(context, HistoryAnalysisActivity.class);
                        break;
                    case "频段扫描":
                        intent = new Intent(context, HistoryPinDuanActivity.class);
                        break;
                    case "音频":
                        intent = new Intent(context, PlayerActivity.class);
                        break;
                    default:
                        intent = null;
                        break;
                }
                if (getTypename(position) == "音频") {
                    EventBus.getDefault().postSticky(new MessageEvent(list.get(position).getFile(), position));
                } else if (intent == null) {
                    return;
                } else {
                    intent.putExtra("filename", new File(list.get(position).getFile()).getName());
                    intent.putExtra("from", "history");
                }
                context.startActivity(intent);
            }
        });
        tv.setClickable(false);
        tv.setBackgroundResource(R.drawable.main_top_item_bg);
        return tv;
    }

    private String getTypename(int position) {
        String name;
        switch (list.get(position).getType()) {
            case 1:
                name = "单频测量";
                break;
            case 2:
                name = "频谱分析";
                break;
            case 3:
                name = "频段扫描";
                break;
            case 4:
                name = "音频";
                break;
            case 5:
                name = "暂无数据";
                break;
            default:
                name = null;
                break;
        }
        return name;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //返回Tag数据
    @Override
    public int getPopularity(int position) {
        return position % 7;
    }

    //针对每个Tag返回一个权重值，该值与ThemeColor和Tag初始大小有关
    @Override
    public void onThemeColorChanged(View view, int themeColor) {
        ((TextView) view).setTextColor(themeColor);
    }

    public boolean mShouldScroll;
    //记录目标项位置
    public int mToPosition;

    public void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }
}
