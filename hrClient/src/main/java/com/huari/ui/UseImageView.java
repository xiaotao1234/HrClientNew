package com.huari.ui;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.huari.client.R;

public class UseImageView extends AppCompatImageView {
    private float wh = 0;
    public UseImageView(Context context) {
        super(context);
    }

    public UseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public UseImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }
    public void init(Context context,AttributeSet attributeSet){
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,R.styleable.UseImageView);
        wh = typedArray.getFloat(R.styleable.UseImageView_wh,0);
        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (wh != 0) {
            int width = measureWidth(widthMeasureSpec);
            int height = (int) (width / wh);
            setMeasuredDimension(width, height);
            heightMeasureSpec = View.MeasureSpec
                    .makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    private int measureWidth(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        //wrap_content
        if (specMode == View.MeasureSpec.AT_MOST) {
        }
        //fill_parent或者精确值
        else if (specMode == View.MeasureSpec.EXACTLY) {
        }
        return specSize;
    }
}

