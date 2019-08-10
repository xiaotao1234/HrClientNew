package com.huari.ui;

import com.huari.diskactivity.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 频谱分析（单频测量）上方的控件，分步画出
 *
 * @author jianghu
 */
public class PartWaveShowView extends View {
    int startx, topy, bottomy, endx;// 坐标系的两条线的坐标
    private int width, height;
    private Paint cdpaint, textpaint, netpaint, wavepaint, linepaint,
            mengbanpaint;
    float valueperunitofy;
    float dpperunitofx, dpperunitofy;// X轴、Y轴分别被分割为16和20个小格，这两个参数代表每个小格是多少dp。
    private int topvalue, bottomvalue;// Y轴上顶端和底端的数值。
    private Path textpath, wavepath;
    private PathEffect effect;
    private int[] dataarray;
    private int sum;
    private int count;// 本地用于缓存数据的数组有count个元素。该参数从外部传入。
    private float dpperx, dppery;// 每个值占多少dp。如，Y轴为-20--80,则dppery=(bottomy-topy)/100。
    private boolean show;
    private int clickedpointercount;// 触屏时有几个触点。
    private int showpointcount;// 界面上展示多少个点的数据。
    private PointF p1, p2;
    private float olddis, newdis;
    private int haveint;// 已经存储了haveint个数据
    private int startnum, endnum;// 要展示的一串点的第一个点和最后一个点的索引
    private int chufazhi;// 当两根手指滑动后距离改变大小>=chufazhi后，触发坐标系上相应值的改变。
    private Rect waverect;
    private float oldx1, oldy1, newx1, newy1, oldx2, oldy2, newx2, newy2;// 记录两个点之间的x/y距离差值,得出是哪个方向的扩缩放。
    private float oldx, oldy, newx, newy;
    private float linex;// 显示触点的x坐标位置
    private float kslinex;// 扩、缩放时起始点的坐标位置，用于标画出起始线。
    private boolean showline;
    private int tempint;// 画触点竖线的时候用，由于莫明的原因，在UP中获得的X所转化成的endnum总是错误的，但是在MOVE中却是正确的，暂且引进tempint完成功能。
    private String title = "电平dBuV";

    private void ini() {
        topvalue = 80;
        bottomvalue = -20;
        valueperunitofy = (topvalue - bottomvalue) / 20;
        cdpaint = new Paint();
        cdpaint.setColor(Color.WHITE);
        cdpaint.setTextAlign(Align.RIGHT);
        textpaint = new Paint();
        textpaint.setColor(Color.WHITE);
        textpaint.setTextAlign(Align.CENTER);
        textpaint.setTextSize(16);
        netpaint = new Paint();
        netpaint.setColor(getResources().getColor(R.color.net));
        netpaint.setStrokeWidth(0.4f);
        mengbanpaint = new Paint();
        mengbanpaint.setColor(Color.YELLOW);
        mengbanpaint.setAlpha(100);
        effect = new DashPathEffect(new float[]{1, 2}, 1);// 虚线风格
        netpaint.setPathEffect(effect);
        wavepaint = new Paint();
        wavepaint.setAntiAlias(true);
        wavepaint.setColor(Color.GREEN);
        wavepaint.setStrokeWidth(0.75f);
        wavepaint.setStyle(Style.STROKE);
        linepaint = new Paint();
        linepaint.setColor(Color.YELLOW);
        wavepath = new Path();
        p1 = new PointF();
        p2 = new PointF();
        count = 80;
        startnum = 0;
        endnum = 79;
        showpointcount = endnum - startnum + 1;
        dataarray = new int[count];

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setCount(int count) {
        this.count = count;
        postInvalidate();
    }

    public PartWaveShowView(Context context) {
        super(context);
        // this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        ini();
    }

    public PartWaveShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        ini();
    }

    public PartWaveShowView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        ini();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
        chufazhi = height / 6;
        textpath = new Path();
        startx = 60;
        endx = width - 14;
        topy = 5;
        bottomy = height - 15;
        dpperunitofx = (endx - startx) / 16.0f;
        dpperunitofy = (bottomy - topy) / 20.0f;
        dppery = (bottomy - topy) / (float) (topvalue - bottomvalue);
        if (showpointcount > 1) {
            dpperx = (endx - startx) / (float) (showpointcount - 1);
        } else {
            dpperx = endx - startx;
        }
        textpath.moveTo(startx - 45, height / 2 + 60);
        textpath.lineTo(startx - 45, height / 2 - 60);
        waverect = new Rect(startx, topy, endx + 1, bottomy + 1);
        show = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCdsystem(canvas);
        canvas.clipRect(waverect, Op.INTERSECT);
        canvas.drawPath(wavepath, wavepaint);
        // if(showline)
        // {
        // canvas.drawLine(linex, topy, linex, bottomy, linepaint);
        // canvas.drawLine(kslinex, topy, kslinex, bottomy, linepaint);
        // canvas.drawRect(new RectF(linex,topy,kslinex,bottomy), mengbanpaint);
        // }
    }

    public void setDanwei(String s) {
        title = s;
        clear();
        postInvalidate();
    }

    public void clear() {
        wavepath.reset();
        count = 80;
        startnum = 0;
        endnum = 79;
        showpointcount = endnum - startnum + 1;
        dataarray = new int[count];
        haveint = 0;
    }

    private void drawCdsystem(Canvas canvas) {
        canvas.drawLine(startx, topy, startx, bottomy, cdpaint);
        canvas.drawLine(startx - 8, bottomy, endx, bottomy, cdpaint);
        for (int i = 0; i < 17; i++) {
            float tempx = startx + i * dpperunitofx;
            if (i % 2 == 0) {
                canvas.drawLine(tempx, bottomy, tempx, bottomy + 8, cdpaint);
                canvas.drawLine(tempx, topy, tempx, bottomy, netpaint);
            } else {
                canvas.drawLine(tempx, bottomy, tempx, bottomy + 5, cdpaint);
            }
        }
        ;
        for (int n = 0; n < 21; n++) {
            float tempy = topy + n * dpperunitofy;
            if (n % 2 == 0) {
                canvas.drawLine(startx - 8, tempy, startx, tempy, cdpaint);
                canvas.drawText(topvalue - n * valueperunitofy + "",
                        startx - 8, tempy + 5, cdpaint);
                canvas.drawLine(startx, tempy, endx, tempy, netpaint);
            } else {
                canvas.drawLine(startx - 5, tempy, startx, tempy, cdpaint);
            }
        }
        canvas.drawTextOnPath(title, textpath, 0, 0, textpaint);

    }

    private float yvaluetoydp(int f) {
        return bottomy - (f - bottomvalue) * dppery;
    }

    private int xtoindex(float x) {
        int returnvalue = 0;
        if (x - startx <= 0) {
            returnvalue = startnum;
        } else {
            returnvalue = (int) ((x - startx) / dpperx) + startnum;
            if (returnvalue > endnum) {
                returnvalue = endnum;
            }
        }
        return returnvalue;
    }

    /**
     * 用以设置数组某个特定元素的值
     *
     * @param array
     * @param n     特定元素位置的索引
     * @param m     要赋给该元素的值
     */
    public void setArrayElement(int[] array, int n, int m) {
        array[n] = m;
    }

    public void refresh(int value) {
        if (show) {
            wavepath.reset();
            if (haveint < count) {
                dataarray[haveint] = value;
                haveint++;
            } else {
                haveint = 0;
                dataarray[haveint] = value;
                haveint++;
            }
            ;
            if (startnum < haveint) {
                wavepath.moveTo(startx, yvaluetoydp(dataarray[startnum]));
                int temp = Math.min(haveint - 1, endnum);
                if (startnum == endnum) {
                    wavepath.lineTo(endx, yvaluetoydp(dataarray[startnum]));
                } else {
                    for (int i = 1; i <= temp - startnum; i++) {
                        wavepath.lineTo(startx + i * dpperx,
                                yvaluetoydp(dataarray[startnum + i]));
                    }
                }
                postInvalidate();
            } else {
                postInvalidate();
            }
        }
    }

    /**
     * 该方法其实只是用在扩、缩放操作后执行，更新wavepath
     */
    private void drawwaves() {
        wavepath.reset();
        if (startnum < haveint) {
            wavepath.moveTo(startx, yvaluetoydp(dataarray[startnum]));
            int temp = Math.min(haveint - 1, endnum);
            if (startnum == endnum) {
                wavepath.lineTo(endx, yvaluetoydp(dataarray[startnum]));
            } else {
                for (int i = 1; i <= temp - startnum; i++) {
                    wavepath.lineTo(startx + i * dpperx,
                            yvaluetoydp(dataarray[startnum + i]));
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            oldx = event.getX();
            oldy = event.getY();
            showline = true;
            linex = (xtoindex(oldx) - startnum) * dpperx + startx;
            kslinex = linex;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            newx = event.getX();
            newy = event.getY();
            tempint = xtoindex(newx);
            if (Math.abs(newy - oldy) - Math.abs(newx - oldx) > 0)// 纵向滑动
            {
                if (newy - oldy > chufazhi && topvalue >= 40)// 向下滑动
                {
                    topvalue = topvalue - 10;
                    bottomvalue = bottomvalue + 10;
                    dppery = (bottomy - topy)
                            / (float) (topvalue - bottomvalue);
                    valueperunitofy = (topvalue - bottomvalue) / 20;
                    oldy = newy;
                    oldx = newx;
                } else if (oldy - newy > chufazhi)// 向上滑动
                {
                    topvalue = topvalue + 10;
                    bottomvalue = bottomvalue - 10;
                    dppery = (bottomy - topy)
                            / (float) (topvalue - bottomvalue);
                    valueperunitofy = (topvalue - bottomvalue) / 20;
                    oldy = newy;
                    oldx = newx;
                }
            } else if (Math.abs(newy - oldy) - Math.abs(newx - oldx) < 0) {
                linex = (tempint - startnum) * dpperx + startx;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            newx = event.getX();
            newy = event.getY();
            showline = false;
            // if(Math.abs(newx-oldx)-Math.abs(newy-oldy)>0)//横向滑动
            // {
            // if(newx-oldx>0)//向右滑动，即扩放
            // {
            // startnum=xtoindex(oldx);
            // endnum=tempint;
            // showpointcount=endnum-startnum+1;
            // if(showpointcount>1)
            // {
            // dpperx=(endx-startx)/(float)(showpointcount-1);
            // }
            // else
            // {
            // dpperx=endx-startx;
            // }
            // }
            // else if(oldx-newx>10)
            // {
            // startnum=0;
            // endnum=count-1;
            // showpointcount=count;
            // dpperx=(endx-startx)/(float)(showpointcount-1);
            // }
            // drawwaves();
            // }
        }
        postInvalidate();
        return true;
    }

    private float getdistance(PointF po1, PointF po2) {
        float x = po1.x - po2.x;
        float y = po1.y - po2.y;
        return (float) (Math.sqrt(x * x + y * y));
    }

}
