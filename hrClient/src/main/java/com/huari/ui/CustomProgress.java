package com.huari.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.huari.client.R;


/*
create by xt 2019/7/16
*/
public class CustomProgress extends View {
    private int backgroundColor;//进度背景
    private int currentColor;//当前进度
    private int progressColor;//进度颜色
    private Paint bPaint;//背景进度画笔
    private Paint cPaint;//滑块进度画笔
    private Paint paint;//进度画笔
    private RectF rectf_b;//背景圆角矩形
    private RectF rectf_c;//滑块圆角矩形
    private RectF rectf_p;//进度圆角矩形
    private int viewWidth, viewHeight;
    private float progress = 0;//进度值
    private float x, y;

    public CustomProgress(Context context) {
        this(context, null);
    }

    public CustomProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        initPaint();
    }

    private void initPaint() {
        bPaint = new Paint();
        bPaint.setAntiAlias(true);
        bPaint.setColor(backgroundColor);
        bPaint.setStyle(Paint.Style.FILL);
        bPaint.setStrokeCap(Paint.Cap.ROUND);

        cPaint = new Paint();
        cPaint.setAntiAlias(true);
        cPaint.setColor(currentColor);
        cPaint.setStyle(Paint.Style.FILL);
        cPaint.setStrokeCap(Paint.Cap.ROUND);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(progressColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomProgress);
            backgroundColor = array.getColor(R.styleable.CustomProgress_backgroud_color, Color.WHITE);
            currentColor = array.getColor(R.styleable.CustomProgress_current_color, Color.CYAN);
            progressColor = array.getColor(R.styleable.CustomProgress_progress_color, Color.CYAN);
            array.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        //计算宽
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 750;
//                    getResources().getDimensionPixelSize(R.dimen.x750);
        }
        //计算高
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 80;
//                    getResources().getDimensionPixelSize(R.dimen.x80);
        }
        viewWidth = width;
        viewHeight = height;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //step1:画背景
        if (rectf_b == null) {
            rectf_b = new RectF(0, 15, viewWidth, viewHeight - 15);
        }
        canvas.drawRoundRect(rectf_b, 10, 10, bPaint);
        //step2:画进度
        if (progress > 0) {
            rectf_p = new RectF(0, 15, viewWidth * progress / 100, viewHeight - 15);
        } else {
            rectf_p = new RectF(0, 15, 0, viewHeight - 15);
        }

        canvas.drawRoundRect(rectf_p, 10, 10, paint);
        //step3:画滑块
        if (progress > 0) {
            rectf_c = new RectF(viewWidth * progress / 100 - 50, 0, viewWidth * progress / 100, viewHeight);
        } else {
            rectf_c = new RectF(0, 0, 50, viewHeight);
        }
        canvas.drawRoundRect(rectf_c, 10, 10, cPaint);
    }

    public void startAnim(int count) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, count);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                progress = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.setDuration(2000);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
            if (x >= viewWidth) {
                progress = 100;
            } else if (x <= 50) {
                progress = 0;
            } else {
                progress = x * 100 / viewWidth;
            }

        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            x = event.getX();
            y = event.getY();
            if (x >= viewWidth) {
                progress = 100;
            } else if (x <= 50) {
                progress = 0;
            } else {
                progress = x * 100 / viewWidth;
            }
        }
        progressListener.getProgress(progress);
        invalidate();
        return true;
    }

    public interface ProgressListener {
        void getProgress(float progress);
    }

    public ProgressListener progressListener;

    public void setProgressListener(ProgressListener listener) {
        progressListener = listener;
    }
}