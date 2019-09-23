package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CalendarItemView extends CustomView{

    private Rect rect;
    private Paint textPaint;
    private int min;
    private Paint forkPaint;
    private float x;
    private float y;

    interface deleteOwn{
        public void deleteOwnListener();
    }

    private deleteOwn deleteOwn;

    public void setDeleteOwn(deleteOwn deleteOwn){
        this.deleteOwn = deleteOwn;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value = String.valueOf(10);


    public CalendarItemView(Context context) {
        super(context);
        init();
    }

    public CalendarItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDeafultPaint = new Paint();
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setColor(Color.parseColor("#88DE47A6"));
        mDeafultPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);

        forkPaint = new Paint();
        forkPaint.setAntiAlias(true);
        forkPaint.setStrokeWidth(2);
        forkPaint.setColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                if(x>min/2&&y<min/2){
                    if (deleteOwn!=null){
                        deleteOwn.deleteOwnListener();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("xiaoonsize","sizechanger");
        min = w>h?h:w;
        rect = new Rect(0,0, min, min);
        forkPaint.setStrokeWidth(min/100);
        textPaint.setTextSize(min/2);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        min = w>h?h:w;
        rect = new Rect(0,0, min, min);
        forkPaint.setStrokeWidth(min/100);
        textPaint.setTextSize(min/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(rect!=null){
            canvas.drawRect(rect,mDeafultPaint);
        }
        float textWidth = textPaint.measureText(String.valueOf(value));
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        //得到基线的位置
        float baselineY = min/2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        canvas.drawText(value,min/2,baselineY,textPaint);
        drawFork(canvas);
    }

    private void drawFork(Canvas canvas) {
        int m = min/8;
        canvas.translate(m*7,m);
        canvas.drawLine(-m+m/8,-m+m/8,m-m/8,m-m/8,forkPaint);
        canvas.drawLine(m-m/8,-m+m/8,-m+m/8,m-m/8,forkPaint);
    }
}