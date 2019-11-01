package com.huari.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.huari.client.R;

//建议总刻度数不超过20，即:
// (keheight-low)/oneStep<=20
// 否者会影响下方球体的刻度显示，当然也还可以继续优化来改善，但是这里只需要-60----80,暂时就这样吧，
// 还有就是若需要频繁的刷新这个view的显示，那么最好再对draw方法里面的数值计算做一定的优化，
// 因为这里在onDraw方法中进行了大量重复的参数运算，这个是不必要的，且对于onDraw这个频繁调用的方法来说，其中大量的运算是很耗费性能的
// 但是我懒，这个坑留给下一任来填吧
public class LinView extends CustomView {

    int width;
    int height;
    int value = -30;

    Path path;
    Path fillPath;
    Paint textPaint;
    Paint paint;

    Context context;
    AttributeSet attributeSet;
    String textBottom = "温度";
    String unit = "℃";
    String title = "温度计";
    private float dp_7;
    private float dp_10;
    private int fillColor;
    private int low;
    private int keHeight;
    private int oneStep;

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
        invalidate();
    }

    public int getValue() {
        return value;
    }

    private void init() {
        dp_7 = getResources().getDimension(R.dimen.dp_7);
        dp_10 = getResources().getDimension(R.dimen.dp_10);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LinView);
        textBottom = typedArray.getString(R.styleable.LinView_text_bottom_lin);
        unit = typedArray.getString(R.styleable.LinView_unit_lin);
        low = typedArray.getInt(R.styleable.LinView_low, 0);
        keHeight = typedArray.getInt(R.styleable.LinView_heigh, 100);
        oneStep = typedArray.getInt(R.styleable.LinView_one_step, 10);
        fillColor = typedArray.getColor(R.styleable.LinView_fill, Color.parseColor("#FF0000"));
        title = typedArray.getString(R.styleable.LinView_titile_text);

        mDeafultPaint = new Paint();
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setStyle(Paint.Style.STROKE);
        mDeafultPaint.setColor(Color.parseColor("#FFFFFF"));
        mDeafultPaint.setStrokeWidth(3);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(25);
        textPaint.setColor(Color.parseColor("#FFFFFF"));
        textPaint.setTextAlign(Paint.Align.CENTER);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);

        path = new Path();
        fillPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTitle(canvas);
        Log.d("xiaolaile", String.valueOf(value));
        canvas.translate(width / 2, height / 6 * 5);
        RectF oval = new RectF(-width / 20, -width / 20, width / 20, width / 20);
        path.reset();
        fillPath.reset();
        path.addArc(oval, -45, 270);

        path.lineTo((float) -(width / 20 / Math.sqrt(2)), -width * 2);
        path.lineTo((float) (width / 20 / Math.sqrt(2)), -width * 2);
        path.close();

        if (value < keHeight - ((keHeight - low) * 19 / 20)) {
            fillPath.addArc(oval, 90 - (float) (1.8 * value * 20), (float) (3.6 * value * 20));
            fillPath.close();
        } else {
            fillPath.addArc(oval, -45, 270);
            double m = -((width * 2) / ((keHeight - low) / oneStep) - width / 20 - (width / 20 / Math.sqrt(2))) - (float) (value - low - oneStep) / oneStep * (float) (width * 2 / ((keHeight - low) / oneStep));
            fillPath.lineTo((float) (-width / 20 / Math.sqrt(2)), (float) m);
            fillPath.lineTo((float) (width / 20 / Math.sqrt(2)), (float) m);
            fillPath.close();
        }
        canvas.drawPath(fillPath, paint);
        canvas.drawPath(path, mDeafultPaint);
        canvas.drawCircle(0, 0, width / 40, mDeafultPaint);
        float[] ints = new float[(keHeight - low) / oneStep * 4];//刻度线的点集
        float[] intText = new float[(keHeight - low) / oneStep * 2];//刻度值的点集
        double m = -((width * 2) / ((keHeight - low) / oneStep) - width / 20 - (width / 20 / Math.sqrt(2)));
        double n = width * 2 / ((keHeight - low) / oneStep);
        for (int i = 0; i < ((keHeight - low) / oneStep); i++) {
            ints[i * 4] = (float) -(width / 40 * Math.sqrt(2));
            ints[i * 4 + 1] = (float) (m - i * n);
            ints[i * 4 + 2] = (float) (-(width / 40 * Math.sqrt(2)) + width / 60);
            ints[i * 4 + 3] = (float) (m - i * n);
            intText[i * 2] = (float) (-(width / 20 / Math.sqrt(2)) - 50);
            intText[i * 2 + 1] = (float) (m - i * n);
        }

        canvas.drawLines(ints, mDeafultPaint);
        for (int i = 0; i < ((keHeight - low) / oneStep); i++) {
            canvas.drawText(String.valueOf(low + oneStep * (i + 1)), intText[i * 2], intText[i * 2 + 1], textPaint);
        }
        textPaint.setColor(fillColor);
        canvas.drawText(textBottom + ": " + value + " " + unit, 0, 100, textPaint);
        textPaint.setColor(Color.parseColor("#FFFFFF"));
    }

    private void drawTitle(Canvas canvas) {
        canvas.save();
        canvas.translate(width / 2, (height / 6 * 5 - width * 2) / 2);
        textPaint.setTextSize(dp_10);
        canvas.drawText(title, 0, 0, textPaint);
        textPaint.setTextSize(dp_7);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

}
