package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.huari.client.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class pieLineView extends CustomView {
    private int m;
    Path[] paths;
    Matrix mMapMatrix = null;
    private RectF rectF;
    private Paint paintCircle;
    private Paint paintline;
    private Paint paintTextTouch;
    private List<Paint> paints;
    private List<Paint> Arcpaints;
    private List<Paint> thickPaints;
    private List<Paint> paintText;
    List<Integer> list = new ArrayList<>();
    List<String> stringList = new ArrayList<>();
    private Region region;
    private boolean touchDownFlag = false;
    int top;
    int bottom;
    int topRight;
    int bottomRight;
    private int circleDistance;
    private int dp_30;
    private int dp_20;

    public void setList(List<Integer> list, List<String> stringList) {
        this.list.clear();
        circleDistance = mViewHeight / (list.size() + 1);
        int all = 0;
        for (int mm : list) {
            all = all + mm;

        }
        for (int mm : list) {
            this.list.add((int) (((float) mm / (float) all) * 360));
        }
        this.stringList = stringList;
        paints = new ArrayList<>();
        Arcpaints = new ArrayList<>();
        thickPaints = new ArrayList<>();
        paintText = new ArrayList<>();
        paints = getFixationPaint();
        Arcpaints = getArcPaint();
        thickPaints = getThickFixationPaint();
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

        dp_30 = R.dimen.dp_30;
        dp_20 = R.dimen.dp_20;

        paintCircle = new Paint();
        paintCircle.setAntiAlias(true);
        paintCircle.setColor(Color.parseColor("#44000000"));
        paintCircle.setStyle(Paint.Style.FILL);

        paintline = new Paint();
        paintline.setAntiAlias(true);
        paintline.setColor(Color.parseColor("#FFFFFF"));
        paintline.setStrokeWidth(2);
        paintline.setStyle(Paint.Style.STROKE);

        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        mDeafultPaint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        mDeafultPaint.setStrokeWidth(10);

        paintTextTouch = new Paint();
        paintTextTouch.setAntiAlias(true);
        paintTextTouch.setColor(Color.WHITE);
        paintTextTouch.setTextSize(30);

        mMapMatrix = new Matrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        circleDistance = mViewHeight / (list.size() + 1);
        mMapMatrix.reset();
        m = w > h ? h : w;
        rectF = new RectF(-h / 3, -h / 3, h / 3, h / 3);
        Region globalRegion = new Region(-w, -h, w, h);
        Path path = new Path();
        path.addCircle(0, 0, h / 3, Path.Direction.CW);
        region = new Region();
        region.setPath(path, globalRegion);
        paintTextTouch.setTextSize(w / 45);
        for (int i = 0; i < paints.size(); i++) {
            paintText.get(i).setTextSize(w / 40);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] pts = new float[2];
        pts[0] = event.getX();
        pts[1] = event.getY();
        mMapMatrix.mapPoints(pts);
        int x = (int) pts[0];
        int y = (int) pts[1];
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (region.contains(x, y)) {
                    touchDownFlag = true;
                }
                touchDownFlag = !touchDownFlag;
                break;
            case MotionEvent.ACTION_MOVE:
//                if (region.contains(x, y)) {
//                    touchDownFlag = true;
//                } else {
//                    touchDownFlag = false;
//                }
//                touchDownFlag = !touchDownFlag;
                break;
            case MotionEvent.ACTION_UP:
//                touchDownFlag = false;
//                touchDownFlag = !touchDownFlag;
        }
        if (touchDownFlag == true) {
            Log.d("touch", "按下");
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getMatrix().invert(mMapMatrix);
        top = -mViewHeight * 4 / 9;
        bottom = mViewHeight * 4 / 9 - 8;
        topRight = 48;
        bottomRight = 48;
        int now = -180;
        int i = 0;
        drawArc(canvas, now, i);
        drawExplain(canvas);
        if (touchDownFlag == true) {
            drawLine(canvas);
        }
    }

    private void drawLine(Canvas canvas) {

    }

    private void drawArc(Canvas canvas, int now, int i) {
        canvas.translate(mViewHeight / 2 + mViewWidth / 20, mViewHeight / 2);
        if (list.size() == 0) {
            canvas.drawCircle(0, 0, mViewHeight / 3, paints.get(0));
        }
        if (mMapMatrix.isIdentity()) {
            canvas.getMatrix().invert(mMapMatrix);
        }
        for (int value : list) {
            if (touchDownFlag == true) {
                float[] point = new float[2];
                float[] tan = new float[2];
                Path path = new Path();
                if(list.size()==1){
                    path.addArc(rectF, now, value);
                }else {
                    path.addArc(rectF, now, value - 4);
                }
                PathMeasure pathMeasure = new PathMeasure();
                pathMeasure.setPath(path, false);
                pathMeasure.getPosTan(pathMeasure.getLength() / 2, point, tan);
                canvas.drawArc(rectF, now, value - 4, false, thickPaints.get(i));
                canvas.drawCircle(point[0], point[1], 15, paintCircle);
                Path path1 = new Path();
                path1.moveTo(point[0], point[1]);
                if (point[1] < 0 && point[0] < 0) {
                    path1.lineTo(point[0], top);
                    path1.lineTo(mViewHeight / 3 + topRight + mViewWidth * 3 / 10, top);
                    path1.lineTo(mViewHeight / 3 + topRight + mViewWidth * 3 / 10, circleDistance * (i + 1) - mViewHeight / 2);
                    path1.lineTo(mViewWidth * 7 / 15 - mViewHeight / 40, circleDistance * (i + 1) - mViewHeight / 2);
                    topRight = topRight - 18;
                    top = top + 8;
                } else if (point[1] < 0 && point[0] > 0) {
                    path1.lineTo(mViewHeight / 3 + topRight + mViewWidth * 3 / 10, point[1]);
                    path1.lineTo(mViewHeight / 3 + topRight + mViewWidth * 3 / 10, circleDistance * (i + 1) - mViewHeight / 2);
                    path1.lineTo(mViewWidth * 7 / 15 - mViewHeight / 40, circleDistance * (i + 1) - mViewHeight / 2);
                    topRight = topRight - 18;
                    top = top + 8;
                } else if (point[1] > 0 && point[0] > 0) {
                    path1.lineTo(mViewHeight / 3 + topRight + mViewWidth * 3 / 10, point[1]);
                    path1.lineTo(mViewHeight / 3 + topRight + mViewWidth * 3 / 10, circleDistance * (i + 1) - mViewHeight / 2);
                    path1.lineTo(mViewWidth * 7 / 15 - mViewHeight / 40, circleDistance * (i + 1) - mViewHeight / 2);
                    bottomRight = bottomRight + 18;
                    bottom = bottom + 8;
                } else {
                    path1.lineTo(point[0], bottom);
                    path1.lineTo(mViewHeight / 3 + bottomRight + mViewWidth * 3 / 10, bottom);
                    path1.lineTo(mViewHeight / 3 + bottomRight + mViewWidth * 3 / 10, circleDistance * (i + 1) - mViewHeight / 2);
                    path1.lineTo(mViewWidth * 7 / 15 - mViewHeight / 40, circleDistance * (i + 1) - mViewHeight / 2);
                    bottomRight = bottomRight + 18;
                    bottom = bottom + 8;
                }
                canvas.drawPath(path1, paintline);
            } else {
                canvas.drawArc(rectF, now, value - 4, false, Arcpaints.get(i));
            }
            now = now + value;
            i++;
        }
    }

    private void drawExplain(Canvas canvas) {
        canvas.translate(mViewWidth / 4 + mViewWidth / 20, -mViewHeight / 2);
        if (list.size() == 0) {
            canvas.drawText("暂无数据", 0, mViewHeight / 2, paintText.get(0));
        }
        int j = 0;
        for (int value : list) {
            canvas.drawCircle(mViewWidth / 6, circleDistance * (j + 1), mViewHeight / 40, paints.get(j));
            canvas.drawText(stringList.get(j), mViewWidth / 6 + mViewHeight / 5, circleDistance * (j + 1)+mViewHeight/60, paintText.get(j));
            if (touchDownFlag == true) {
                canvas.drawText(String.valueOf((int) (value / 3.6)) + "%", mViewWidth / 6 + mViewHeight, circleDistance * (j + 1), paintTextTouch);
            }
            j++;
        }
    }

    public List<Paint> getFixationPaint() {
        float size_30 = getResources().getDimension(R.dimen.dp_8);
        List<Paint> paints = new ArrayList<>();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DE47A6"));
        paint.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint.setStrokeWidth(5);
        paint.setTextSize(size_30);
        paints.add(paint);

        Paint paint1 = new Paint();
        paint1.setColor(Color.parseColor("#3DDE55"));
        paint1.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint1.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint1.setStrokeWidth(5);
        paint1.setTextSize(size_30);
        paints.add(paint1);

        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#ED9922"));
        paint2.setAntiAlias(true);
//        paint2.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint2.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint2.setStrokeWidth(5);
        paint2.setTextSize(size_30);
        paints.add(paint2);

        Paint paint3 = new Paint();
        paint3.setColor(Color.parseColor("#FB4326"));
        paint3.setAntiAlias(true);
//        paint3.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint3.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint3.setStrokeWidth(5);
        paint3.setTextSize(size_30);
        paints.add(paint3);

        return paints;
    }

    public List<Paint> getThickFixationPaint() {
        float size_30 = getResources().getDimension(R.dimen.dp_9);
        List<Paint> paints = new ArrayList<>();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DE47A6"));
        paint.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint.setStrokeWidth(30);
        paint.setTextSize(size_30);
        paints.add(paint);

        Paint paint1 = new Paint();
        paint1.setColor(Color.parseColor("#3DDE55"));
        paint1.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint1.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint1.setStrokeWidth(30);
        paint1.setTextSize(size_30);
        paints.add(paint1);

        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#ED9922"));
        paint2.setAntiAlias(true);
//        paint2.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint2.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint2.setStrokeWidth(30);
        paint2.setTextSize(size_30);
        paints.add(paint2);

        Paint paint3 = new Paint();
        paint3.setColor(Color.parseColor("#FB4326"));
        paint3.setAntiAlias(true);
//        paint3.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint3.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint3.setStrokeWidth(30);
        paint3.setTextSize(size_30);
        paints.add(paint3);

        return paints;
    }

    public List<Paint> getFixationTextPaint() {
        float size_30 = getResources().getDimension(R.dimen.dp_8);
        List<Paint> paints = new ArrayList<>();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DE47A6"));
        paint.setAntiAlias(true);
        paint.setTextSize(size_30);
        paints.add(paint);

        Paint paint1 = new Paint();
        paint1.setColor(Color.parseColor("#3DDE55"));
        paint1.setAntiAlias(true);
        paint1.setTextSize(size_30);
        paints.add(paint1);

        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#ED9922"));
        paint2.setAntiAlias(true);
        paint2.setTextSize(size_30);
        paints.add(paint2);

        Paint paint3 = new Paint();
        paint3.setColor(Color.parseColor("#FB4326"));
        paint3.setAntiAlias(true);
        paint3.setTextSize(size_30);
        paints.add(paint3);

        return paints;
    }

    public List<Paint> getArcPaint() {
        float size_30 = getResources().getDimension(R.dimen.dp_7);
        List<Paint> paints = new ArrayList<>();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DE47A6"));
        paint.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint.setStrokeWidth(15);
        paint.setTextSize(size_30);
        paints.add(paint);

        Paint paint1 = new Paint();
        paint1.setColor(Color.parseColor("#3DDE55"));
        paint1.setAntiAlias(true);
//        paint.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint1.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint1.setStrokeWidth(15);
        paint1.setTextSize(size_30);
        paints.add(paint1);

        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#ED9922"));
        paint2.setAntiAlias(true);
//        paint2.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint2.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint2.setStrokeWidth(15);
        paint2.setTextSize(size_30);
        paints.add(paint2);

        Paint paint3 = new Paint();
        paint3.setColor(Color.parseColor("#FB4326"));
        paint3.setAntiAlias(true);
//        paint3.setColor(Color.BLACK);           // 画笔颜色 - 黑色
        paint3.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        paint3.setStrokeWidth(15);
        paint3.setTextSize(size_30);
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
