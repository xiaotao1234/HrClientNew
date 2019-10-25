package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CalendarLayout extends ViewGroup {

    private int viewH;
    private int w;
    private int h;
    boolean fla = true;

    public CalendarLayout(Context context) {
        super(context);
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);//这个方法触发了onsizechanged方法
        //测量并保存layout的宽高(使用getDefaultSize时，wrap_content和match_perent都是填充屏幕)
        //稍后会重新写这个方法，能达到wrap_content的效果
//        setMeasuredDimension( getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
//        getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("xiao111", "onsizechanger");
        fla = false;
        this.w = w;
        this.h = h;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int countChild = getChildCount();
        int viewW;
        View child;
        int nowUseW = 0;
        int nowUseH = 0;
        int edgeDistance = 30;
        for (int i = 0; i < countChild; i++) {
            viewH = getChildAt(0).getMeasuredHeight();
            child = getChildAt(i);
            viewW = getChildAt(i).getMeasuredWidth();
            if (getWidth() - nowUseW >= viewW + edgeDistance + edgeDistance) {
                child.layout(nowUseW + edgeDistance, nowUseH + edgeDistance, nowUseW + edgeDistance + viewW, nowUseH + edgeDistance + viewH);
                nowUseW = nowUseW + edgeDistance + viewW;
            } else {
                nowUseW = 0;
                nowUseH = nowUseH + edgeDistance + viewH;
                child.layout(nowUseW + edgeDistance, nowUseH + edgeDistance, nowUseW + edgeDistance + viewW, nowUseH + edgeDistance + viewH);
                nowUseW = nowUseW + edgeDistance + viewW;
            }
        }
    }
}
