package com.huari.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.huari.client.R;

public class LinView extends CustomView {

    int width;
    int height;
    int value = 60;

    Path path;
    Path fillPath;
    Paint textPaint;
    Paint paint;

    Context context;
    AttributeSet attributeSet;
    String textBottom = "温度";
    String unit = "℃";

    public LinView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        attributeSet = attrs;
        init();
    }

    public LinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        attributeSet = attrs;
        init();
    }

    public void setValue(int value) {
        this.value = value;
    }

    private void init() {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LinView);
        textBottom = typedArray.getString(R.styleable.LinView_text_bottom_lin);
        unit = typedArray.getString(R.styleable.LinView_unit_lin);

        mDeafultPaint = new Paint();
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setStyle(Paint.Style.STROKE);
        mDeafultPaint.setStrokeWidth(6);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(25);
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextAlign(Paint.Align.CENTER);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#88666666"));

        path = new Path();
        fillPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 6 * 5);
        RectF oval = new RectF(-30, -30, 30, 30);
        path.addArc(oval, -45, 270);

        path.lineTo((float) -(30 / Math.sqrt(2)), -950);
        path.lineTo((float) (30 / Math.sqrt(2)), -950);
        path.close();

        if (value * 10 < 100) {
            fillPath.addArc(oval, 90 - (float) (1.8 * value * 10), (float) (3.6 * value * 10));
            fillPath.close();
        } else {
            fillPath.addArc(oval, -45, 270);
            fillPath.lineTo((float) (-30 / Math.sqrt(2)), -value * 10 + 65);
            fillPath.lineTo((float) (30 / Math.sqrt(2)), -value * 10 + 65);
            fillPath.close();
        }

        canvas.drawPath(fillPath, paint);
        canvas.drawPath(path, mDeafultPaint);
        canvas.drawCircle(0, 0, 15, mDeafultPaint);
        float[] ints = new float[40];//刻度线的点集
        float[] intText = new float[20];//刻度值的点集

        for (int i = 0; i <= 9; i++) {
            ints[i * 4] = (float) -(50 / Math.sqrt(2));
            ints[i * 4 + 1] = (float) (-(30 / Math.sqrt(2)) - i * 100);
            ints[i * 4 + 2] = (float) (-(30 / Math.sqrt(2)) - 20);
            ints[i * 4 + 3] = (float) (-(30 / Math.sqrt(2)) - i * 100);
            intText[i * 2] = (float) (-(30 / Math.sqrt(2)) - 50);
            intText[i * 2 + 1] = (float) (-(30 / Math.sqrt(2)) - i * 100);
        }

        canvas.drawLines(ints, mDeafultPaint);
        for (int i = 0; i <= 9; i++) {
            canvas.drawText(String.valueOf((i + 1) * 10), intText[i * 2], intText[i * 2 + 1], textPaint);
        }
        canvas.drawText(textBottom + ": " + value + " " + unit, 0, 100, textPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
//        if (h > 20 * w) {
//            Rect rect = new Rect(0, 20 * w, w, h);
//        } else {
//            Rect rect = new Rect(w / 2 - h / 40, 0, w / 2 + h / 40, h);
//        }
    }

}
