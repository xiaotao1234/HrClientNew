package com.huari.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class SuperTextview extends TextView {
    public SuperTextview(Context context) {
        super(context);
    }

    public SuperTextview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperTextview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case  MotionEvent.ACTION_MOVE:
                ViewParent parent = getParent();
                if(parent instanceof ViewGroup){
                    ((ViewGroup) parent).onTouchEvent(event);
                }
                return false;
        }
        return super.onTouchEvent(event);
    }
}
