package com.huari.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class leftSlideView extends LinearLayout {
    public static int LEFT = 1;
    public static int RIGHT = 2;
    private float downX;
    private float downY;

    public leftSlideView(Context context) {
        super(context);
    }

    public leftSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public leftSlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                Log.d("xiaodown", String.valueOf(downX));
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                Log.d("xiaoup", String.valueOf(event.getX()));
                if ((event.getX() - downX) < -50) {
                    leftTouchListener.left(LEFT);
                    return true;
                } else if ((event.getX() - downX) > 50) {
                    leftTouchListener.left(RIGHT);
                }
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface LeftTouchListener {
        void left(int i);
    }

    LeftTouchListener leftTouchListener;

    public void setLeftTouchListener(LeftTouchListener leftTouchListener) {
        this.leftTouchListener = leftTouchListener;
    }
}
