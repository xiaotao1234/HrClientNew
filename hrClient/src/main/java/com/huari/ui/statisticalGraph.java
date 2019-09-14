package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;

public class statisticalGraph extends CustomView {


    public statisticalGraph(Context context) {
        super(context);
        init();
    }

    public statisticalGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public statisticalGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private Paint numTextPaint;
    private Paint numLinePaint;
    Paint numCirclePaint;
    private int min;
    int multiplev = 1;
    int multipleh = 1;
    Paint textvPaint;
    Paint texthPaint;
    SweepGradient sweepGradient;

    Paint bgPaint;
    LinearGradient linearGradient;
    int[] colors = {0xffff0000, 0xff00ff00};
    Path path;
    Path pathLine;
    int i;
    float[] num = {2, 3, 4, 1, 2, 3};

    float rectWidth;
    float rectHeight;
    Point[] points;

    public void setNum(float[] num) {
        this.num = num;
    }

    private void init() {
        mDeafultPaint = new Paint();
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setColor(Color.parseColor("#FFFFFF"));
        PathEffect effects = new DashPathEffect(new float[]{10f, 7f,}, 4);
        mDeafultPaint.setPathEffect(effects);
        textvPaint = new Paint();
        textvPaint.setTextSize(25);
        textvPaint.setAntiAlias(true);
        textvPaint.setColor(Color.parseColor("#FFFFFF"));
        textvPaint.setTextAlign(Paint.Align.RIGHT);

        texthPaint = new Paint();
        texthPaint.setTextSize(25);
        texthPaint.setAntiAlias(true);
        texthPaint.setColor(Color.parseColor("#FFFFFF"));
        texthPaint.setTextAlign(Paint.Align.CENTER);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sweepGradient = new SweepGradient(50, 50, colors, null);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        numLinePaint = new Paint();
        numLinePaint.setAntiAlias(true);
        numLinePaint.setColor(Color.parseColor("#ff2288CC"));
        numLinePaint.setStyle(Paint.Style.STROKE);
        numLinePaint.setStrokeWidth(2);

        numTextPaint = new Paint();
        numTextPaint.setAntiAlias(true);
        numTextPaint.setColor(Color.parseColor("#ff2288CC"));
        numTextPaint.setTextSize(30);
        numTextPaint.setTextAlign(Paint.Align.CENTER);

        numCirclePaint = new Paint();
        numCirclePaint.setAntiAlias(true);
        numCirclePaint.setStyle(Paint.Style.FILL);
        numCirclePaint.setColor(Color.parseColor("#ff2288CC"));

        points = new Point[num.length];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeight = h;
        mViewWidth = w;
        min = (h > w ? w : h);

        linearGradient = new LinearGradient(0, mViewHeight, 0, min / 5, 0xff2288CC, 0x00FFFFFF, Shader.TileMode.REPEAT);
        bgPaint.setShader(linearGradient);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStrokeWidth(25);

        rectWidth = mViewWidth - min / 5-30;
        rectHeight = mViewWidth / 2;
        path = new Path();
        path.moveTo(min / 5, min / 5);
//        path.lineTo(min/3,min/3);
//        path.lineTo(mViewWidth,min/5);
////        path.lineTo(rectWidth / (6) * (num.length - 1), min / 5);

        for (int i = 0; i < num.length; i++) {
            path.lineTo(getx(i), gety(i));
        }
        path.lineTo(getx(num.length - 1), min / 5);
        path.close();

        pathLine = new Path();
        pathLine.moveTo(getx(0), gety(0));
        for (int i = 1; i < num.length; i++) {
            pathLine.lineTo(getx(i), gety(i));
        }

        for (int i = 0; i < num.length; i++) {
            points[i] = new Point((int) getx(i), (int) gety(i));
        }
    }

    public float getx(int position) {
        return min / 5 + rectWidth / 5 * position;
    }

    public float gety(int position) {
        return min / 5 + num[position] / 5 * rectHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, mViewHeight);
        canvas.scale(1, -1);
        drawback(canvas);
        drawNumLine(canvas);
        drawLine(canvas);
        drawScaleValue(canvas);
        drawnum(canvas);
//        drawTitle(canvas);
    }

//    private void drawTitle(Canvas canvas) {
//        canvas.drawText();
//    }

    private void drawnum(Canvas canvas) {
        for (int i = 0; i < num.length; i++) {
            canvas.drawText(String.valueOf(num[i]), points[i].x, -points[i].y - min / 40, numTextPaint);
            canvas.drawCircle(points[i].x, -points[i].y, min / 100,numCirclePaint);
        }
    }

    private void drawNumLine(Canvas canvas) {
        canvas.drawPath(pathLine, numLinePaint);
    }

    private void drawback(Canvas canvas) {
        canvas.drawPath(path, bgPaint);
    }

    private void drawScaleValue(Canvas canvas) {
        canvas.scale(1, -1);
        for (int i = 0; i < 6; i++) {
            canvas.drawText(String.valueOf(i * 1 * multiplev), min / 7, -(min / 5 + i * mViewWidth / 10), textvPaint);
            canvas.drawText(String.valueOf(i * 1 * multipleh), min / 5 + (rectWidth / (num.length-1)) * i, -(min / 5 - min / 15), texthPaint);
        }
    }

    private void drawLine(Canvas canvas) {
        for (int i = 0; i < 6; i++) {
            canvas.drawLine(min / 5, min / 5 + i * mViewWidth / 10, mViewWidth-30, min / 5 + i * mViewWidth / 10, mDeafultPaint);
        }
    }
}
