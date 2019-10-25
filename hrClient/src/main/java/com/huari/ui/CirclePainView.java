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
    String title = "电表";
    private float dp_10;
    private float dp_7;
    private int pointerColor;

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
        dp_7 = getResources().getDimension(R.dimen.dp_7);
        dp_10 = getResources().getDimension(R.dimen.dp_10);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//禁止硬件加速来使得drawTextOnPath生效

        typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CirclePainView);
        name = typedArray.getString(R.styleable.CirclePainView_text_bottom);
        unit = typedArray.getString(R.styleable.CirclePainView_unit);
        pointerColor = typedArray.getColor(R.styleable.CirclePainView_pointer, Color.parseColor("#FF0000"));
        all = typedArray.getInteger(R.styleable.CirclePainView_all, 20);
        oneSmallStep = typedArray.getInteger(R.styleable.CirclePainView_one_small_step, 1);
        oneBigStep = typedArray.getInteger(R.styleable.CirclePainView_one_big_step, 5);
        title = typedArray.getString(R.styleable.CirclePainView_title_text);

        mDeafultPaint = new Paint();
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setColor(Color.parseColor("#FFFFFF"));
        mDeafultPaint.setStrokeWidth(2);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(pointerColor);
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
        paintCenterBig.setColor(Color.parseColor("#44FFFFFF"));

        paintOut = new Paint();
        paintOut.setStyle(Paint.Style.STROKE);
        paintOut.setColor(Color.parseColor("#44FFFFFF"));
        paintOut.setStrokeWidth(30);
        paintOut.setAntiAlias(true);
        paintOut.setTextAlign(Paint.Align.CENTER);

        paintArc = new Paint();
        paintArc.setStyle(Paint.Style.FILL);
        paintArc.setColor(Color.parseColor("#666666"));
        paintArc.setAntiAlias(true);

        paintValue = new Paint();
        paintValue.setColor(Color.parseColor("#FFFFFF"));
        paintValue.setTextAlign(Paint.Align.CENTER);
        paintValue.setTextSize(dp_7);
        paintValue.setAntiAlias(true);

        paintIn = new Paint();
        paintIn.setStyle(Paint.Style.STROKE);
        paintIn.setColor(Color.parseColor("#22FFFFFF"));
        paintIn.setStrokeWidth(14);
        paintIn.setAntiAlias(true);
        paintIn.setTextAlign(Paint.Align.CENTER);

        path = new Path();
        pathCenter = new Path();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(width / 2, (height / 3 * 2 - width / 2) / 2);
        paintValue.setTextSize(dp_10);
        canvas.drawText(title, 0, 0, paintValue);
        paintValue.setTextSize(dp_7);
        canvas.restore();
        Path path12 = new Path();

        path.moveTo(-width / 20, 0);
        path.lineTo(0, br - 100);
        path.lineTo(width / 20, 0);
        path.close();

        pathCenter.moveTo(-width / 40, 0);
        pathCenter.lineTo(0, br - 180);
        pathCenter.lineTo(width / 40, 0);
        pathCenter.close();

        canvas.translate(width / 2, height / 3 * 2);
        RectF rectF1 = new RectF(-br - 20, -br - 20, br + 20, br + 20);
        paintOut.setStrokeWidth(width / 30);
        canvas.drawArc(rectF1, 216, 108, false, paintOut);
        RectF rectF2 = new RectF(-br - 20 + width / 30, -br - 20 + width / 30, br + 20 - width / 30, br + 20 - width / 30);
        paintIn.setStrokeWidth(width / 40);
        canvas.drawArc(rectF2, 217, 106, false, paintIn);
        paintValue.setColor(pointerColor);
        canvas.drawText(name + ":  " + showValue + "  " + unit, 0, height/5, paintValue);
        paintValue.setColor(Color.parseColor("#FFFFFF"));
        canvas.rotate(130);

        for (int i = 0; i <= all; i += oneSmallStep) {               // 绘制刻度
            if (i % oneBigStep == 0) {
                canvas.drawLine(0, br - width / 15, 0, br, mDeafultPaint);
                path12.moveTo(30, br - 70);
                path12.lineTo(-30, br - 70);
                canvas.drawTextOnPath(String.valueOf(i), path12, 0, 0, paintValue);//使文字反向
            } else {
                canvas.drawLine(0, br - width / 30, 0, br, mDeafultPaint);
            }
            canvas.rotate(100 / (all / oneSmallStep));
        }

        canvas.drawCircle(0, 0, width / 20, paint);
        canvas.drawCircle(0, 0, width / 40, paintCenter);
        canvas.drawCircle(0, 0, width / 12, paintCenter);
        canvas.drawCircle(0, 0, width / 10, paintCenterBig);

        canvas.save();//指针
        canvas.rotate(showValue * 100 / all - 100 / (all / oneSmallStep) - 100);//减5是因为上面在最后一次循环的时候多转了5度
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
