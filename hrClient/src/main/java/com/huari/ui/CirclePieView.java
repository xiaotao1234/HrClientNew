package com.huari.ui;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.huari.client.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * create by xiao
 * 19/8/13
 */
public class CirclePieView extends View {

    private int mWidth;
    private int redius;
    private int mheight;
    RectF rectF;
    List<Integer> mColorList;
    Map<String, Integer> mDataMap;
    private static Paint paint;
    List<Paint> paints;
    private float value;
    private int mwidthSize;
    private int mheightSize;
    private int mwidthModle;
    private int mheightModle;
    List<Integer> arclist;
    private Paint centerpaint;
    private Paint mTextPaint;
    private Paint mTextOutPaint;
    private int minAngle;
    private Paint linePaint;
    private Paint circlePaint;
    Context context;
    AttributeSet attributeSet;
    Path path;
    Region region;

    public CirclePieView(Context context) {
        super(context);
        this.context = context;
        path = new Path();
        region = new Region();
        init();
    }


    public CirclePieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        attributeSet = attrs;
        init();
    }


    public CirclePieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        attributeSet = attrs;
        init();
    }


    public void init() {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CirclePieView);
        int lineColor = typedArray.getColor(R.styleable.CirclePieView_line_color, Color.parseColor("#222222"));
        int textColor = typedArray.getColor(R.styleable.CirclePieView_text_color, Color.parseColor("#000000"));
        int centerCircleColor = typedArray.getColor(R.styleable.CirclePieView_center_circle_color, Color.parseColor("#FFFFFF"));
        int smallCircleColor = typedArray.getColor(R.styleable.CirclePieView_small_circle_color, Color.parseColor("#BBFFFFFF"));
        int lineStrokeWidth = typedArray.getInt(R.styleable.CirclePieView_line_stroke_width, 4);
        float textSize = typedArray.getFloat(R.styleable.CirclePieView_text_size, 15);
        int textOutColor = typedArray.getColor(R.styleable.CirclePieView_text_out_color, Color.parseColor("#000000"));
        paints = new ArrayList<>();
        arclist = new ArrayList<>();
        arclist.add(20);
        arclist.add(100);
        arclist.add(80);
        arclist.add(90);
        arclist.add(70);
        for (int i = 0;i<5;i++){
            paints.add(getPaint());
        }
        centerpaint = new Paint();
        centerpaint.setAntiAlias(true);
        centerpaint.setColor(centerCircleColor);
        centerpaint.setAntiAlias(true);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(sp2px(context, textSize));
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextOutPaint = new Paint();
        mTextOutPaint.setTextSize(sp2px(context, textSize));
        mTextOutPaint.setColor(textOutColor);
        mTextOutPaint.setAntiAlias(true);
        mTextOutPaint.setTextAlign(Paint.Align.LEFT);
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineStrokeWidth);
        linePaint.setAntiAlias(true);
        circlePaint = new Paint();
        circlePaint.setColor(smallCircleColor);
        circlePaint.setAntiAlias(true);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mwidthSize = MeasureSpec.getSize(widthMeasureSpec);
        mheightSize = MeasureSpec.getSize(heightMeasureSpec);
        mwidthModle = MeasureSpec.getMode(widthMeasureSpec);
        mheightModle = MeasureSpec.getMode(heightMeasureSpec);
        if (mwidthModle == MeasureSpec.EXACTLY) {
            mWidth = mwidthSize;
        } else {
            mWidth = getPaddingLeft() + getPaddingRight() + redius * 2 + 100;
            if (mwidthModle == MeasureSpec.AT_MOST) {
                mWidth = Math.min(mWidth, mwidthSize);
            }
        }
        if (mheightModle == MeasureSpec.EXACTLY) {
            mheight = mheightSize;
        } else {
            mheight = getPaddingTop() + getPaddingBottom() + redius * 2 + 100;
            if (mheightModle == MeasureSpec.AT_MOST) {
                mheight = Math.min(mheight, mheightSize);
            }
        }
        redius = Math.min(mWidth, mheight) / 3;
        rectF = new RectF(mWidth / 2 - redius, mheight / 2 - redius, mWidth / 2 + redius, mheight / 2 + redius);
        setMeasuredDimension(mWidth, mheight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paintme(canvas, value);
//        canvas.drawArc(rectF, 0, value, true, paint);
//        if (value < arclist.get(1) && value > arclist.get(0)) {
//            canvas.drawArc(rectF, arclist.get(0), value - 1, true, paint1);
//        }
    }

    public static Paint getPaint() {
        if (paint == null) {
            paint = new Paint();
        }
        int r = (new Random().nextInt(100) + 10) * 4;
        int g = (new Random().nextInt(100) + 10) * 3 * 4;
        int b = (new Random().nextInt(100) + 10) * 2 * 4;
        int color = Color.rgb(r, g, b);
        paint.setColor(color);
        paint.setAntiAlias(true);
        return paint;
    }

    private void paintme(Canvas canvas, float value) {//绘制函数，这里主要做的是针对传来的动画值进行相应的绘
        // 制，在动画值没有达到相应模块的绘制阈值时，就不进行绘制，若达到了阈值，就绘制到相应的动画值，若超过了阈
        // 值，就只绘制到相应模块的最大值处，即arclist.get(i)中取出的值处。
        int num = 0;
        for (int i = 0; i < arclist.size(); i++) {
            if (value > num && value < num + arclist.get(i)) {
                canvas.drawArc(rectF, num, value - num, true, paints.get(i));
                drawText(canvas, num, "总结", arclist.get(i));
            } else if (value >= num + arclist.get(i)) {
                canvas.drawArc(rectF, num, arclist.get(i) - 1, true, paints.get(i));
                drawText(canvas, num, "总结", arclist.get(i));
            }
            num = num + arclist.get(i);
        }
        canvas.drawCircle(mWidth / 2, mheight / 2, redius / 2, centerpaint);
    }

    private void drawText(Canvas mCanvas, float textAngle, String kinds, float needDrawAngle) {
        Rect rect = new Rect();
        mTextPaint.getTextBounds(kinds, 0, kinds.length(), rect);

        minAngle = 30;
        if (textAngle + needDrawAngle / 2 >= 0 && textAngle + needDrawAngle / 2 <= 90) { //画布坐标系第一象限(数学坐标系第四象限)
            Log.d("xiao", "in");
            if (needDrawAngle < minAngle) { //如果小于某个度数,就把文字画在饼状图外面
                mCanvas.drawLine((float) (redius * 0.75 * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2))) + mWidth / 2, (float) (redius * 0.75 * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2))) + mheight / 2,
                        (float) (redius * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2))) + mWidth / 2, (float) (redius * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2))) + mheight / 2, linePaint);
                mCanvas.drawLine((float) (redius * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2))) + mWidth / 2, (float) (redius * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2))) + mheight / 2,
                        (float) (redius * 1.2 * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2))) + mWidth / 2, (float) (redius * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2))) + mheight / 2, linePaint);

                mCanvas.drawCircle((float) (redius * 0.75 * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2))) + mWidth / 2, (float) (redius * 0.75 * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2))) + mheight / 2, 6, circlePaint);
                mCanvas.drawText(kinds, (float) (redius * 1.2 * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2))) + mWidth / 2, (float) (redius * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2))) + rect.height() / 2 + mheight / 2, mTextOutPaint);
            } else {
                mCanvas.drawText(kinds, (float) (redius * 0.75 * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2))) + mWidth / 2, (float) (redius * 0.75 * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2))) + rect.height() / 2 + mheight / 2, mTextPaint);
            }
        } else if (textAngle + needDrawAngle / 2 > 90 && textAngle + needDrawAngle / 2 <= 180) { //画布坐标系第二象限(数学坐标系第三象限)
            if (needDrawAngle < minAngle) {
                mCanvas.drawText(kinds, (float) (-redius * 1.2 * Math.cos(Math.toRadians(180 - (textAngle + needDrawAngle / 2)))) + mWidth / 2, (float) (redius * 1.2 * Math.sin(Math.toRadians(180 - (textAngle + needDrawAngle / 2)))) + rect.height() / 2 + mheight / 2, mTextPaint);
            } else {
                mCanvas.drawText(kinds, (float) (-redius * 0.75 * Math.cos(Math.toRadians(180 - (textAngle + needDrawAngle / 2)))) + mWidth / 2, (float) (redius * 0.75 * Math.sin(Math.toRadians(180 - (textAngle + needDrawAngle / 2)))) + rect.height() / 2 + mheight / 2, mTextPaint);
            }
        } else if (textAngle + needDrawAngle / 2 > 180 && textAngle + needDrawAngle / 2 <= 270) { //画布坐标系第三象限(数学坐标系第二象限)
            if (needDrawAngle < minAngle) {
                mCanvas.drawText(kinds, (float) (-redius * 1.2 * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2 - 180))) + mWidth / 2, (float) (-redius * 1.2 * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2 - 180))) + rect.height() / 2 + mheight / 2, mTextPaint);
            } else {
                mCanvas.drawText(kinds, (float) (-redius * 0.75 * Math.cos(Math.toRadians(textAngle + needDrawAngle / 2 - 180))) + mWidth / 2, (float) (-redius * 0.75 * Math.sin(Math.toRadians(textAngle + needDrawAngle / 2 - 180))) + rect.height() / 2 + mheight / 2, mTextPaint);
            }
        } else { //画布坐标系第四象限(数学坐标系第一象限)
            if (needDrawAngle < minAngle) {
                mCanvas.drawText(kinds, (float) ((redius * 1.2 * Math.cos(Math.toRadians(360 - (textAngle + needDrawAngle / 2)))) + mWidth / 2), (float) (-redius * 1.2 * Math.sin(Math.toRadians(360 - (textAngle + needDrawAngle / 2)))) + rect.height() / 2 + mheight / 2, mTextPaint);
            } else {
                mCanvas.drawText(kinds, (float) (redius * 0.75 * Math.cos(Math.toRadians(360 - (textAngle + needDrawAngle / 2)))) + mWidth / 2, (float) (-redius * 0.75 * Math.sin(Math.toRadians(360 - (textAngle + needDrawAngle / 2)))) + rect.height() / 2 + mheight / 2, mTextPaint);
            }
        }
    }


    public void initAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 360);
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(animation -> {
            value = (float) animation.getAnimatedValue();
            invalidate();
        });
        valueAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
//        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

    }

    public void setDatas(LinkedList<Integer> mColorList, LinkedHashMap<String, Integer> mDataMap) {
        this.mColorList = mColorList;
        this.mDataMap = mDataMap;
    }

}