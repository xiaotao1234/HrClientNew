package com.huari.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huari.client.R;

public class CirclePainView extends CustomView {

    int width;
    int height;
    int br;
    int showValue = 0;
    int all = 20;
    int oneBigStep = 5;
    int oneSmallStep = 1;

    Paint paintCenter;
    Path pathCenter;
    Paint paintCenterBig;
    Paint paint;
    Paint paintOut;
    Paint paintIn;
    Paint paintValue;
    Paint paintArc;
    Path path;

    TypedArray typedArray;
    Context context;
    AttributeSet attributeSet;
    String name;
    String unit;

    public CirclePainView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CirclePainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        attributeSet = attrs;
        init();
    }

    public CirclePainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        attributeSet = attrs;
        init();
    }

    public void setShowValue(int showValue) {
        this.showValue = showValue;
    }

    private void init() {
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//禁止硬件加速来使得drawTextOnPath生效

        typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CirclePainView);
        name = typedArray.getString(R.styleable.CirclePainView_text_bottom);
        unit = typedArray.getString(R.styleable.CirclePainView_unit);
        all = typedArray.getInteger(R.styleable.CirclePainView_all, 20);
        oneSmallStep = typedArray.getInteger(R.styleable.CirclePainView_one_small_step, 1);
        oneBigStep = typedArray.getInteger(R.styleable.CirclePainView_one_big_step, 5);

        mDeafultPaint = new Paint();
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setColor(Color.parseColor("#000000"));
        mDeafultPaint.setStrokeWidth(2);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        paintCenter = new Paint();
        paintCenter.setAntiAlias(true);
        paintCenter.setStyle(Paint.Style.FILL);
        paintCenter.setColor(Color.parseColor("#AA000000"));

        paintCenterBig = new Paint();
        paintCenterBig.setAntiAlias(true);
        paintCenterBig.setStyle(Paint.Style.FILL);
        paintCenterBig.setColor(Color.parseColor("#77000000"));

        paintOut = new Paint();
        paintOut.setStyle(Paint.Style.STROKE);
        paintOut.setColor(Color.parseColor("#66000000"));
        paintOut.setStrokeWidth(30);
        paintOut.setAntiAlias(true);
        paintOut.setTextAlign(Paint.Align.CENTER);

        paintArc = new Paint();
        paintArc.setStyle(Paint.Style.FILL);
        paintArc.setColor(Color.parseColor("#666666"));
        paintArc.setAntiAlias(true);

        paintValue = new Paint();
        paintValue.setColor(Color.parseColor("#000000"));
        paintValue.setTextAlign(Paint.Align.CENTER);
        paintValue.setTextSize(30);
        paintValue.setAntiAlias(true);

        paintIn = new Paint();
        paintIn.setStyle(Paint.Style.STROKE);
        paintIn.setColor(Color.parseColor("#33000000"));
        paintIn.setStrokeWidth(14);
        paintIn.setAntiAlias(true);
        paintIn.setTextAlign(Paint.Align.CENTER);

        path = new Path();
        pathCenter = new Path();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path12 = new Path();

        path.moveTo(-30, 0);
        path.lineTo(0, br - 100);
        path.lineTo(30, 0);
        path.close();

        pathCenter.moveTo(-15, 0);
        pathCenter.lineTo(0, br - 200);
        pathCenter.lineTo(15, 0);
        pathCenter.close();

        canvas.translate(width / 2, height / 3 * 2);
        RectF rectF1 = new RectF(-br - 20, -br - 20, br + 20, br + 20);

        canvas.drawArc(rectF1, 215, 110, false, paintOut);
        RectF rectF2 = new RectF(-br + 7, -br + 7, br - 7, br - 7);

        canvas.drawArc(rectF2, 217, 106, false, paintIn);
        canvas.drawText(name + ": " + showValue + " " + unit, 0, 200, paintValue);
        canvas.rotate(130);

        for (int i = 0; i <= all; i += oneSmallStep) {               // 绘制刻度
            if (i % oneBigStep == 0) {
                canvas.drawLine(0, br - 40, 0, br, mDeafultPaint);
                path12.moveTo(30, br - 70);
                path12.lineTo(-30, br - 70);
                canvas.drawTextOnPath(String.valueOf(i), path12, 0, 0, paintValue);//使文字反向
            } else {
                canvas.drawLine(0, br - 20, 0, br, mDeafultPaint);
            }
            canvas.rotate(100 / (all/oneSmallStep));
        }

        canvas.drawCircle(0, 0, 30, paint);
        canvas.drawCircle(0, 0, 15, paintCenter);
        canvas.drawCircle(0, 0, 60, paintCenter);
        canvas.drawCircle(0, 0, 80, paintCenterBig);

        canvas.save();//指针
        canvas.rotate(showValue * 100 / all - 100 / (all/oneSmallStep) - 100);//减5是因为上面在最后一次循环的时候多转了5度
        canvas.drawPath(path, paint);
        canvas.drawPath(pathCenter, paintCenter);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int minWidth = w > h ? h : w;
//        minWidth *= 0.8;
        br = minWidth / 2;
        width = w;
        height = h;
    }
}
