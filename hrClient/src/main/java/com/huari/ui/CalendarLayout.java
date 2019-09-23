package com.huari.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CalendarLayout extends LinearLayout {

    private int viewH;
    private int w;
    private int h;
    int nowUseW = 0;
    int nowUseH = 0;
    int edgeDistance = 30;
    boolean fla = true;
    private int width;

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
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            Log.d("xiao111","onsizechanger");
            fla = false;
            this.w = w;
            this.h = h;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("xiao111","onLayout");
//        super.onLayout(changed,l,t,r,b);
        if (getChildCount() != 0) {
            viewH = getChildAt(0).getHeight();
        }
        for (int i = 0; i < getChildCount(); i++) {
            int viewW = getChildAt(i).getWidth();
            if (width - nowUseW >= viewW + edgeDistance + edgeDistance) {
                getChildAt(i).layout(nowUseW + edgeDistance, nowUseH + edgeDistance, nowUseW + edgeDistance + viewW, nowUseH + edgeDistance + viewH);
                nowUseW = nowUseW + edgeDistance + viewW;
            } else {
                nowUseW = 0;
                nowUseH = nowUseH + edgeDistance + viewH;
                getChildAt(i).layout(nowUseW + edgeDistance, nowUseH + edgeDistance, nowUseW + edgeDistance + viewW, nowUseH + edgeDistance + viewH);
                nowUseW = nowUseW + edgeDistance + viewW;
            }
        }
//        super.onLayout(changed, l, t, r, b);
    }
}
