package com.huari.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huari.client.R;
import com.huari.ui.TagsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Version:
 * @Author:
 * @CreateTime:
 * @Descrpiton:
 */
public class TagCloudAdapter extends TagsAdapter {
    private List<String> mList = new ArrayList<String>();
    RecyclerView recyclerView;
    public TagCloudAdapter(String[] list,RecyclerView recyclerView) {
        mList.clear();
        this.recyclerView = recyclerView;
        Collections.addAll(mList, list);
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
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(100, 100);
        tv.setLayoutParams(lp);
        tv.setText("8." + position);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(v -> smoothMoveToPosition(recyclerView,position));
        tv.setClickable(false);
        tv.setBackgroundResource(R.drawable.circle_bg);
        return tv;
    }

    //返回Tag数据
    @Override
    public int getPopularity(int position) {
        return position % 7;
    }

    //针对每个Tag返回一个权重值，该值与ThemeColor和Tag初始大小有关
    @Override
    public void onThemeColorChanged(View view, int themeColor) {
        ((TextView)view).setTextColor(themeColor);
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
