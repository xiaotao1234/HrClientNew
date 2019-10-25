package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.huari.dataentry.DateString;

public class CalendarItemView extends CustomView{

    private Rect rect;
    private Paint textPaint;
    private int min;
    private Paint forkPaint;
    private Paint monthtextPaint;

    public interface deleteOwn {
        void deleteOwnListener(View view);
    }

    public deleteOwn deleteOwn;

    public void setDeleteOwn(deleteOwn deleteOwn) {
        this.deleteOwn = deleteOwn;
    }

    public void setValue(String value,String value1) {
        this.value = value;
        this.value1 = value1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CalendarItemView) {
            if(value1.equals(((CalendarItemView) obj).getValue1())&&m.equals(((CalendarItemView)obj).getM())){
                return true;
            }
        }
        return false;
    }

    private String value = String.valueOf(10);
    private String value1 ;

    public String getValue1() {
        return year+"年"+month+"月";
    }

    int year;

    public int getYear() {
        return year;
    }

    int month;

    public int getMonth() {
        return month;
    }

    public int[] getM() {
        return m;
    }

    int[] m;

    public CalendarItemView(Context context, String position, int year,int month,int[] m) {
        super(context);
        value = position;
        this.value1 = year+"年"+month+"月";
        this.m = m;
        this.year = year;
        this.month = month;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }



    private int measureWidth(int defaultWidth, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) textPaint.measureText(value) + getPaddingLeft() + getPaddingRight();
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (-textPaint.ascent() + textPaint.descent()) + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                break;
        }
        return defaultHeight;
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

        monthtextPaint = new Paint();
        monthtextPaint.setAntiAlias(true);
        monthtextPaint.setTextAlign(Paint.Align.LEFT);
        monthtextPaint.setColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (deleteOwn != null) {
                    deleteOwn.deleteOwnListener(this);
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
        min = w > h ? h : w;
        rect = new Rect(0, 0, w, h);
        forkPaint.setStrokeWidth(min / 100);
        textPaint.setTextSize(min / 2);
        monthtextPaint.setTextSize(min/5);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect != null) {
            canvas.drawRect(rect, mDeafultPaint);
        }
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        Paint.FontMetrics fontMetrics1 = monthtextPaint.getFontMetrics();
        //得到基线的位置
        int textHeight = (int) (monthtextPaint.descent()-monthtextPaint.ascent());
        float baselineY = min / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float monthY = fontMetrics1.top-fontMetrics1.bottom;
//                min / 5 + (fontMetrics1.bottom - fontMetrics1.top) / 2 - fontMetrics1.bottom;
        canvas.drawText(value, mViewWidth / 2, baselineY, textPaint);
        canvas.drawText(value1, 0, textHeight, monthtextPaint);
        drawFork(canvas);
    }

    private void drawFork(Canvas canvas) {
        int m = min / 8;
        canvas.translate(mViewWidth-m, m);
        canvas.drawLine(-m + m / 8, -m + m / 8, m - m / 8, m - m / 8, forkPaint);
        canvas.drawLine(m - m / 8, -m + m / 8, -m + m / 8, m - m / 8, forkPaint);
    }
}