package com.huari.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * create by xiao
 * 19/8/12
 */
@SuppressLint("AppCompatCustomView")
public class CircleView extends ImageView {
    Drawable drawable;
    Paint paint;
    float redis;
    Bitmap bitmap;
    float scale;
    BitmapShader bitmapShader;
    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(),getMaxHeight());
        redis = size/2;
        setMeasuredDimension(size,size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawable = getDrawable();
        if(drawable!=null){
            bitmap = ((BitmapDrawable)drawable).getBitmap();
            bitmapShader = new BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
            scale = redis*2/Math.min(bitmap.getWidth(),bitmap.getHeight());
            Matrix matrix = new Matrix();
            matrix.setScale(scale,scale);
            bitmapShader.setLocalMatrix(matrix);
            paint.setShader(bitmapShader);
            canvas.drawCircle(redis,redis,redis,paint);
        }else {
            super.onDraw(canvas);
        }
    }
}
