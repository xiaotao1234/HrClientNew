package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.huari.client.R;
import com.huari.tools.SysApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class statisticalGraph extends CustomView {

    Paint numTextPaint;
    Paint numLinePaint;
    Paint numCirclePaint;
    Paint textvPaint;
    Paint texthPaint;
    Paint bgPaint;

    int min;
    int multiplev = 1;
    int multipleh = 1;

    SweepGradient sweepGradient;
    LinearGradient linearGradient;

    Path path;
    Path pathLine;

    int[] colors = {0xffff0000, 0xff00ff00};
    int[] num = new int[7];
    String[] xText = new String[7];
    Point[] points;

    float rectWidth;
    float rectHeight;
    private PathEffect effects;
    private int max = 0;
    private List<Region> regions;
    private Region globalRegion;
    private Matrix matrix;
    private int m = -1;

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

    public void startWeek() {
        xText = new String[7];
        int[] num = {0, 0, 0, 0, 0, 0, 0};
        Calendar calendar = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            xText[i] = String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        Thread thread = new Thread(() -> {
            for (int i = 0; i < xText.length; i++) {
                String m = xText[i];
                String s = calendar.get(Calendar.YEAR) + "-" + m;
                File file1 = new File(SysApplication.fileOs.forSaveFloder + File.separator + "data");
                for (File file11 : file1.listFiles()) {
                    String ma = file11.getName();
                    if (String.valueOf(ma).contains(s)) {
                        num[i]++;
                    }
                }
            }
            this.num = num;
            for (int i = 0; i < num.length; i++) {
                max = (max > num[i] ? max : num[i]);
            }
            multiplev = (max + (5 - (max % 5))) / 5;
            pointChange();
            postInvalidate();
        });
        thread.start();
    }

    public void startMonth() {
        xText = new String[7];
        int[] num = {0, 0, 0, 0, 0, 0, 0};
        Calendar calendar = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            xText[i] = String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        Thread thread = new Thread(() -> {
            for (int i = 0; i < xText.length; i++) {
                String m = xText[i];
                String s = calendar.get(Calendar.YEAR) + "-" + m;
                File file1 = new File(SysApplication.fileOs.forSaveFloder + File.separator + "data");
                for (File file11 : file1.listFiles()) {
                    String ma = file11.getName();
                    if (String.valueOf(ma).contains(s)) {
                        num[i]++;
                    }
                }
            }
            this.num = num;
            for (int i = 0; i < num.length; i++) {
                max = (max > num[i] ? max : num[i]);
            }
            multiplev = (max + (5 - (max % 5))) / 5;
            pointChange();
            postInvalidate();
        });
        thread.start();
    }

    private void init() {
        float size_15 = getResources().getDimension(R.dimen.dp_7);
        float size_30 = getResources().getDimension(R.dimen.dp_8);
        mDeafultPaint = new Paint();
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setColor(Color.parseColor("#FFFFFF"));
        effects = new DashPathEffect(new float[]{10f, 7f,}, 4);
        mDeafultPaint.setPathEffect(effects);
        textvPaint = new Paint();
        textvPaint.setTextSize(size_15);
        textvPaint.setAntiAlias(true);
        textvPaint.setColor(Color.parseColor("#FFFFFF"));
        textvPaint.setTextAlign(Paint.Align.RIGHT);

        texthPaint = new Paint();
        texthPaint.setTextSize(size_15);
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
        numTextPaint.setTextSize(size_30);
        numTextPaint.setTextAlign(Paint.Align.CENTER);

        numCirclePaint = new Paint();
        numCirclePaint.setAntiAlias(true);
        numCirclePaint.setStyle(Paint.Style.FILL);
        numCirclePaint.setColor(Color.parseColor("#ff2288CC"));

        points = new Point[num.length];
        matrix = new Matrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("xiaoxiao", "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        matrix.reset();
        mViewHeight = h;
        mViewWidth = w;
        min = (h > w ? w : h);
        linearGradient = new LinearGradient(0, mViewHeight, 0, min / 5, 0xff2288CC, 0x00FFFFFF, Shader.TileMode.REPEAT);
        bgPaint.setShader(linearGradient);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStrokeWidth(25);
        rectWidth = mViewWidth - min / 5 - 30;
        rectHeight = mViewHeight * 5 / 7;
        pointChange();
    }

    private void pointChange() {
        globalRegion = new Region(-mViewWidth, -mViewHeight, mViewWidth, mViewHeight);
        path = new Path();
        path.moveTo(min / 5, min / 5);

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
        regions = new ArrayList<>();
        for (Point point : points) {
            Path path = new Path();
            path.addCircle(point.x, -point.y, min / 8, Path.Direction.CW);
            Region region = new Region();
            region.setPath(path, globalRegion);
            regions.add(region);
        }
    }

    public float getx(int position) {
        return min / 5 + rectWidth / (num.length - 1) * position;
    }

    public float gety(int position) {
        return (float) (min / 5 + (((float) num[position]) / ((float) multiplev)) / 5.0 * rectHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getMatrix().invert(matrix);
        canvas.translate(0, mViewHeight);
        canvas.scale(1, -1);
        drawback(canvas);
        drawNumLine(canvas);
        drawLine(canvas);
        drawScaleValue(canvas);
        drawindicate(canvas);
        drawnum(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] pts = new float[2];
        pts[0] = event.getX();
        pts[1] = event.getY();
        matrix.mapPoints(pts);
        int x = (int) pts[0];
        int y = (int) pts[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Region region : regions) {
                    if (region.contains(x, y)) {
                        m = regions.indexOf(region);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                for (Region region : regions) {
//                    if (region.contains(x, y)) {
////                        m = regions.indexOf(region);
////                        Log.d("xiao", String.valueOf(m));
//                    }
//                }
                break;
            case MotionEvent.ACTION_UP:
//                if(m!=-1){
//                    m = -1;
//                    invalidate();
//                }
                break;
        }
        return true;
    }

    private void drawindicate(Canvas canvas) {
        canvas.drawLine(min / 30, -min / 30, min / 10, -min / 10, texthPaint);
        Path pathh = new Path();
        Path pathv = new Path();
        pathh.lineTo(min / 5, 0);
        pathv.moveTo(0, -min / 2);
        pathv.lineTo(0, 0);
        canvas.drawTextOnPath("日期", pathh, min / 20, -min / 100, texthPaint);
        canvas.drawTextOnPath("记录数", pathv, min / 20, -min / 100, texthPaint);
    }

    private void drawnum(Canvas canvas) {
        if (matrix.isIdentity()) {
            canvas.getMatrix().invert(matrix);
        }
        for (int i = 0; i < num.length; i++) {
            canvas.drawText(String.valueOf(num[i]), points[i].x, -points[i].y - min / 40, numTextPaint);
            canvas.drawCircle(points[i].x, -points[i].y, min / 50, numCirclePaint);
            if(m == i){
                canvas.drawLine(0,-points[i].y,mViewWidth - 30,-points[i].y,numLinePaint);
                canvas.drawLine(points[i].x,0,points[i].x,-min/5-rectHeight,numLinePaint);
            }
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
        for (int i = 0; i < num.length; i++) {
            canvas.drawText(String.valueOf(i * 1 * multiplev), min / 7, -(min / 5 + i * mViewHeight / 7), textvPaint);
            canvas.drawText(String.valueOf(xText[i]), min / 5 + (rectWidth / (num.length - 1)) * i, -(min / 5 - min / 15), texthPaint);
        }
    }

    private void drawLine(Canvas canvas) {
        for (int i = 0; i < 6; i++) {
            canvas.drawLine(min / 5, min / 5 + i * mViewHeight / 7, mViewWidth - 30, min / 5 + i * mViewHeight / 7, mDeafultPaint);
        }
    }
}
