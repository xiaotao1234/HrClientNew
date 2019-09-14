package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class pieLineView extends CustomView {
    private int m;
    Path path;
    private RectF rectF;
    private List<Paint> paints;
    private List<Paint> paintText;
    List<Integer> list = new ArrayList<>();
    List<String> stringList = new ArrayList<>();
    List<String> colorList;

    public void setList(List<Integer> list, List<String> stringList) {
        int all = 0;
        for (int mm : list) {
            all = all + mm;

        }
        for (int mm : list) {
            this.list.add((int) (((float) mm / (float) all) * 360));
        }
        this.stringList = stringList;
        paints = new ArrayList<>();
        paintText = new ArrayList<>();
        paints = getFixationPaint();
        paintText = getFixationTextPaint();
//        for (int i = 0; i < list.size(); i++) {
//            paintText.add(getRomdomPaint());
//        }
        invalidate();
    }

    public pieLineView(Context context) {
        super(context);
        init();
    }

    public pieLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public pieLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDeafultPaint = new Paint();
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        mDeafultPaint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        mDeafultPaint.setStrokeWidth(10);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        m = w > h ? h : w;
        rectF = new RectF(-h / 3, -h / 3, h / 3, h / 3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int now = 0;
        int i = 0;
        canvas.translate(mViewHeight / 2 + mViewWidth / 20, mViewHeight / 2);
        for (int value : list) {
            canvas.drawArc(rectF, now, value - 4, false, paints.get(i));
            now = now + value;
            i++;
        }
        canvas.translate(mViewWidth / 4 + mViewWidth / 20, -mViewHeight / 2);
        int circleDistance = mViewHeight / (list.size() + 1);
        int j = 0;
        for (int value : list) {
            canvas.drawCircle(mViewWidth / 6, circleDistance * (j + 1), mViewHeight / 60, paints.get(j));
            canvas.drawText(stringList.get(j), mViewWidth / 6 + mViewHeight / 5, circleDistance * (j + 1), paintText.get(j));
            j++;
        }
    }

    public List<Paint> getFixationPaint(){
        List<Paint> paints = new ArrayList<>();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DE47A6"));
        paint.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint.setStrokeWidth(10);
        paint.setTextSize(35);
        paints.add(paint);

        Paint paint1 = new Paint();
        paint1.setColor(Color.parseColor("#3DDE55"));
        paint1.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint1.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint1.setStrokeWidth(10);
        paint1.setTextSize(35);
        paints.add(paint1);

        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#4DCECE"));
        paint2.setAntiAlias(true);
//        paint2.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint2.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint2.setStrokeWidth(10);
        paint2.setTextSize(35);
        paints.add(paint2);

        Paint paint3 = new Paint();
        paint3.setColor(Color.parseColor("#FB4326"));
        paint3.setAntiAlias(true);
//        paint3.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint3.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint3.setStrokeWidth(10);
        paint3.setTextSize(35);
        paints.add(paint3);

        return paints;
    }

    public List<Paint> getFixationTextPaint(){
        List<Paint> paints = new ArrayList<>();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DE47A6"));
        paint.setAntiAlias(true);
        paint.setTextSize(35);
        paints.add(paint);

        Paint paint1 = new Paint();
        paint1.setColor(Color.parseColor("#3DDE55"));
        paint1.setAntiAlias(true);
        paint1.setTextSize(35);
        paints.add(paint1);

        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#4DCECE"));
        paint2.setAntiAlias(true);
        paint2.setTextSize(35);
        paints.add(paint2);

        Paint paint3 = new Paint();
        paint3.setColor(Color.parseColor("#FB4326"));
        paint3.setAntiAlias(true);
        paint3.setTextSize(35);
        paints.add(paint3);

        return paints;
    }

    public static Paint getRomdomPaint() {
        Paint paint = new Paint();
        int r = 100;
        int g = (new Random().nextInt(100) + 10) * 3 * 4;
        int b = (new Random().nextInt(100) + 10) * 1 * 1;
        int color = Color.rgb(r, g, b);
        paint.setColor(color);
        paint.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint.setStrokeWidth(10);
        paint.setTextSize(35);
        return paint;
    }
}
