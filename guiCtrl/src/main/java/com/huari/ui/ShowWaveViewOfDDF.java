package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 频谱分析（单频测量）下方的控件，一次性画出
 * 
 * f1、fh分别代表频率的最小值和最大值，比如100、200，而headf、tailf则是在横向上扩、缩放用的，比如通过滑动后，只让其展示120--150段
 * ， 则headf=120,tailf=150,headf和tailf也是画图时在X轴两端上标出的值。
 * 该控件中没有中心频率和带宽的变量，如果需要根据这两个值画图，则需要转换为适合该控件的变量，即转换成f1、fh、bujin等变量。
 * 
 * 
 * @author jianghu
 * 
 */
public class ShowWaveViewOfDDF extends View {
	private int mywidth, myheight;// 单位: dp
	Context con;
	private boolean have;// 该变量用以控制更新时是否画波形
	private float al = -20f, ah = 80f;// 分别代表y轴上两端的幅度值，即最小值和最大值。默认为-20和80。
	public int leftmargin, upmargin;// 坐标横向和纵向上的起点，即纵轴从x坐标为leftmargin处画起，
									// 左侧有leftmargin宽的空白区，横轴从upmargin处画起，上侧有upmargin高的空白
	private float fl, fh;// 分别代表x轴上两端的频率值，即最小值和最大值
	// private float xpoint;// 每个dp代表多少频率差值

	// 以下是用来画波形、写字等多个部分专用的画笔
	private float disbetweenpoints;
	private Paint wavepaint;
	private Paint maxpaint;
	private Paint minpaint;
	private Paint avgpaint;
	private Paint mengbanpaint;
	private Paint marginpaint, textpaint, p2, shutextpaint;// 画示波界面四条边框的画笔
	private Paint virtualpaint;// 画示波界面内虚线的画笔
	private PathEffect effects;// 用以制作虚线效果

	// 分别用来存储实时值、最大值、最小值、平均值
	private short[] m;

	private float startx = 0, endx = 0, textx;// 分别记录触屏事件的起点、终点的x坐标,纵坐标标值的x起点
	private float starty = 0, endy = 0;// 分别记录触屏事件的起点、终点的y坐标
	private float headf, tailf, bujin;// 调频率后两端的频率值,bujin单位Hz
	private int showpointcount;// 实际要展示的点数。因为扩、缩放的问题，实际展示的点数可能并不是接收到的所有点数。
	float newbujin = bujin / 1000;
	private float biaoxianx;
	private float getx;
	private boolean show = false;// 用以判断是否需要画出触摸点所在的竖线，当为true时画出，否则不画出
	private float xp, yp;// 即x轴和y轴上每个小刻度的宽度,单位：dp
	private float aperdp;// 纵向上每个dp代表多少个幅度值
	private float wavexs;// 每个点占多少dp
	private float zeroatdp;
	private float fxlocation;
	private float fylocation;
	private Path fudupath;
	private Rect r;
	private float chufazhi;// 滑动的距离达到或超过了该值，才算有滑动发生
	private int startnum, endnum;// 要展示的一串点的第一个点和最后一个点的索引
	private int count;
	private float linex, startlinex;

	public void settailf(float tailf) {
		this.tailf = tailf;
	}

	public void setBujin(float bujin) {
		this.bujin = bujin;
	}

	public void setParams(float headf, float tailf, float bujin) {
		this.headf = headf;
		this.tailf = tailf;
		this.bujin = bujin;
	}

	public void initialize() {
		have = false;
		leftmargin = 60;
		textx = leftmargin - 5;
		upmargin = 8;
		fl = 88.000f;
		fh = 108.000f;
		tailf = fh;
		headf = fl;
		bujin = 25;
		count = (int) ((fh - fl) * 1000 / bujin + 1);
		m = new short[count];

		wavepaint = new Paint();
		maxpaint = new Paint();
		minpaint = new Paint();
		avgpaint = new Paint();
		textpaint = new Paint();
		shutextpaint = new Paint();
		mengbanpaint = new Paint();
		mengbanpaint.setColor(Color.YELLOW);
		mengbanpaint.setAlpha(100);
		wavepaint.setColor(Color.GREEN);
		maxpaint.setColor(Color.RED);
		minpaint.setColor(Color.BLUE);
		avgpaint.setColor(Color.YELLOW);
		wavepaint.setStyle(Style.STROKE);
		maxpaint.setStyle(Style.STROKE);
		minpaint.setStyle(Style.STROKE);
		avgpaint.setStyle(Style.STROKE);
		marginpaint = new Paint();
		marginpaint.setColor(Color.parseColor("#70f3ff"));
		marginpaint.setStrokeWidth(0.4f);
		textpaint.setColor(Color.parseColor("#70f3ff"));
		textpaint.setTextAlign(Align.RIGHT);
		textpaint.setTextSize(16);
		shutextpaint.setColor(Color.parseColor("#70f3ff"));
		shutextpaint.setTextAlign(Align.RIGHT);
		virtualpaint = new Paint();
		virtualpaint.setColor(Color.parseColor("#70f3ff"));
		virtualpaint.setStyle(Paint.Style.STROKE);
		p2 = new Paint();
		p2.setColor(Color.YELLOW);
		p2.setTextSize(10);
		effects = new DashPathEffect(new float[] { 1, 2 }, 1);// 虚线风格
		virtualpaint.setStyle(Style.STROKE);

		marginpaint.setStrokeWidth(2);
		fudupath = new Path();
		startnum = 0;
		endnum = count - 1;
		showpointcount = count;

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		chufazhi = getWidth() / 12;
		mywidth = getWidth();
		myheight = getHeight();
		xp = ((float) (mywidth - leftmargin - 13)) / 20;// 横向上每个坐标的间距，单位dp。横向上总共有20列。
		// 坐标界面的左边有x1-1个dp的空白，右边有14dp的空白.
		yp = ((float) (myheight - upmargin - 33)) / 20;// 纵向上每个坐标点间的间距，单位是dp
	}

	/**
	 * public void setWavepaint(Paint wavepaint) <br/>
	 * <br/>
	 * 用来设置画波形的Paint。
	 * 
	 * @param wavepaint
	 *            画波形的画笔。
	 */
	public void setWavepaint(Paint wavepaint) {
		this.wavepaint = wavepaint;
	}

	/**
	 * public void setMaxpaint(Paint maxpaint) <br/>
	 * <br/>
	 * 用来设置画最大值波形的Paint。
	 * 
	 * @param wavepaint
	 *            画最大值波形的画笔。
	 */
	public void setMaxpaint(Paint maxpaint) {
		this.maxpaint = maxpaint;
	}

	/**
	 * public void setMinpaint(Paint minpaint) <br/>
	 * 用来设置画最小值波形的Paint。
	 * 
	 * @param minpaint
	 *            画最小值波形的画笔。
	 */
	public void setMinpaint(Paint minpaint) {
		this.minpaint = minpaint;
	}

	/**
	 * public void setAvgpaint(Paint avgpaint) <br/>
	 * 用来设置画平均值波形的Paint。
	 * 
	 * @param avgpaint
	 *            画平均值波形的画笔。
	 */
	public void setAvgpaint(Paint avgpaint) {
		this.avgpaint = avgpaint;
	}

	public void setFl(float fl) {
		this.fl = fl;
	}

	public void setFh(float fh) {
		this.fh = fh;
	}

	/**
	 * 设置起始频率和终止频率，即波形所能展示的最大频率段范围。bujin为步进。
	 * 
	 * @param fl
	 * @param fh
	 * @param bujin
	 */
	public void setF(float fl, float fh, float bujin) {
		this.fl = fl;
		this.fh = fh;
		tailf = fh;
		headf = fl;
		this.bujin = bujin;
		postInvalidate();
	}

	// 为能在单频测量的功能上使用，后来加的方法。设置起始频率、终止频率和展示的点数。
	public void setFandC(float f1, float fh, int count) {
		this.fl = f1;
		this.fh = fh;
		tailf = fh;
		headf = f1;
		this.bujin = (fh - fl) * 1000 / (count - 1);

		postInvalidate();
	}

	/**
	 * 用来设置画示波界面的矩形区域边框的画笔。
	 * 
	 * @param marginpaint
	 *            画矩形区域边框的画笔。“频率”、“幅度”和坐标的标值等也是用这个画笔画的。
	 */
	public void setMarginpaint(Paint marginpaint) {
		this.marginpaint = marginpaint;
	}

	/**
	 * 用来设置画坐标网格内虚线的画笔。
	 * 
	 * @param virtualpaint
	 *            画虚线的画笔。
	 */
	public void setVirtualpaint(Paint virtualpaint) {
		this.virtualpaint = virtualpaint;
	}

	/**
	 * 用来设置表示波形幅度值的整型数组。
	 * 
	 * @param m
	 *            一个含有count个元素的整型数组，每个元素表示对应点的实时幅度值。
	 */
	public void setM(short[] m) {
		this.m = m;

	}

	/**
	 * 控制控件是否显示波形。当变量have为true时，显示波形。
	 * 
	 * @param b
	 */
	public void setHave(boolean b) {
		have = b;
	}

	public ShowWaveViewOfDDF(Context context) {
		super(context);
		con = context;
		initialize();
	}

	public ShowWaveViewOfDDF(Context context, AttributeSet set) {
		super(context, set);
		con = context;
		initialize();
	}


	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG)); // 抗锯齿效果
		canvas.drawColor(Color.BLACK);
		showpointcount = (int) ((tailf - headf) * 1000 / bujin + 1);
		wavexs = (float) ((float) (mywidth - leftmargin - 13) / (showpointcount - 1));// 宽度被分成了若干份
		DrawXY(canvas);
		if (have == true) {// 如果需要画波形，则画波形
			DrawWaves(canvas, wavepaint, arraytoy(m));
		}
	}

	private void DrawXY(Canvas canvas)// 画坐标系
	{
		canvas.drawColor(Color.BLACK);
		aperdp = (ah - al) / (myheight - upmargin - 33);
		zeroatdp = upmargin + ah / aperdp;
		float ya = (ah - al) / 20;// y轴上每个小刻度代表的幅度差值,单位是dBuV
		float xf = (tailf - headf) / 20;// x轴上每个小刻度代表的频率差值,单位是MHz
		// 以下画四条边框和边框指向的坐标值
		canvas.drawLine(leftmargin - 4, upmargin, leftmargin + xp * 20,
				upmargin, marginpaint);// 第一条横框线,左起点稍微突出，是为了标记坐标值，其他情况同理。即要标坐标的地方，都凸出4个宽度
		canvas.drawText(ah + "", textx, 10, shutextpaint);// 标坐标值,即y轴轴端上的最大幅度值，默认值为80
		canvas.drawLine(leftmargin - 4, upmargin + yp * 20, leftmargin + xp
				* 20, upmargin + yp * 20, marginpaint);// 第二条横框线
		canvas.drawText(al + "", textx, upmargin + 5 + yp * 20, shutextpaint);// 标坐标值，即y轴轴端上的最小幅度值，默认值-20
		canvas.drawLine(leftmargin, upmargin, leftmargin, upmargin + 4 + yp
				* 20, marginpaint);// 第一条竖框线
		canvas.drawText(headf + "", leftmargin - 9, upmargin + 15 + yp * 20,
				marginpaint);// x轴端频率的最小值，默认值为88;x方向左移了9，y方向上下移了15
		canvas.drawLine(leftmargin + xp * 20, upmargin, leftmargin + xp * 20,
				upmargin + 4 + yp * 20, textpaint);// 第二条竖框线
		canvas.drawText(tailf + "", leftmargin + xp * 20 - 30, upmargin + 15
				+ yp * 20, marginpaint);// x轴端频率的最大值，默认值为108;x方向左移了9，y方向上下移了15
		fxlocation = mywidth - 15;
		fylocation = myheight - 3;
		fudupath.moveTo(15, myheight / 2 - 30);
		fudupath.lineTo(15, 60);
		canvas.drawText("频 率  (MHz)", fxlocation, fylocation, textpaint);
		canvas.drawLine(leftmargin - 4, zeroatdp, leftmargin, zeroatdp,
				marginpaint);
		canvas.drawText(0 + "", textx, zeroatdp + 3, shutextpaint);// 标0幅度的位置
		canvas.drawTextOnPath("幅 度  (dBuV)", fudupath, 0, 0, textpaint);

		Path pt = new Path();
		pt.moveTo(leftmargin, 20);

		for (int i = 1; i < 20; i++) {
			canvas.drawLine(leftmargin, upmargin + yp * i,
					leftmargin + xp * 20, upmargin + yp * i, virtualpaint);// 划X向的横虚线
			canvas.drawLine(leftmargin + xp * i, upmargin, leftmargin + xp * i,
					upmargin + yp * 20, virtualpaint);// 划Y向的竖虚线
			if (i % 5 == 0) {
				canvas.drawLine(leftmargin - 4, upmargin + yp * i, leftmargin,
						upmargin + yp * i, marginpaint);
				canvas.drawLine(leftmargin + xp * i, upmargin + yp * 20,
						leftmargin + xp * i, upmargin + yp * 20 + 4,
						marginpaint);
				canvas.drawText((float) Math.round((headf + i * xf) * 1000)
						/ 1000 + "", leftmargin - 9 + i * xp, upmargin + 15
						+ yp * 20, marginpaint);// 画x轴上的坐标
				canvas.drawText((ah - i * ya) + "", textx, upmargin + 5 + yp
						* i, shutextpaint);// 画y轴上的坐标
			}
		}
	}// DrawXY()结束

	private int[] arraytoy(short[] m)// 将服务端传来的幅度转化为与之对应的纵坐标
	{
		int[] n = new int[m.length];
		for (int t = 0; t < m.length; t++) {
			n[t] = (int) (zeroatdp - m[t] / aperdp);
		}
		return n;
	}

	private int xtoindex(float x) {
		int returnvalue = 0;
		if (x - leftmargin <= 0) {
			returnvalue = startnum;
		} else {
			returnvalue = (int) ((x - leftmargin) / wavexs) + startnum;
			if (returnvalue > endnum) {
				returnvalue = endnum;
			}
		}
		return returnvalue;
	}

	private float indextof(int n) {
		float f = 0;
		f = n * bujin / 1000 + fl;
		return f;
	}

	private void DrawWaves(Canvas canvas, Paint p, int[] m)// 画波形
	{
		Rect r = new Rect(leftmargin, upmargin, (int) (leftmargin + xp * 20),
				(int) (upmargin + yp * 20));
		canvas.clipRect(r, Op.INTERSECT);
		Path wavepath = new Path();
		float temp1 = (float) (Math.round((headf - fl) * 1000 / bujin));// 保留三位小数。为什么要这么写而不是直接用（fl-88.000f)/0.025呢？
		// 因为实际运行时有可能出现非预期内的结果，如88.075-88.000结果可能为0.07435789而不是0.075，导致得出的startindex小1
		int startindex = (int) temp1;
		float temp2 = (float) (Math.round((tailf - fl) * 1000 / bujin));
		int endindex = (int) temp2;
		wavepath.moveTo(leftmargin, m[startindex]);
		if (startindex != endindex) {
			int y = 1;// 用以控制x坐标的逐步延伸
			for (int i = startindex + 1; i <= endindex; i++) {
				wavepath.lineTo(leftmargin + wavexs * y, m[i]);
				y++;
			}
		} else {
			wavepath.lineTo(leftmargin + 20 * xp, m[startindex]);
		}
		canvas.drawPath(wavepath, p);

	}

	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			show = true;
			getx = event.getX();
			biaoxianx = indextof(xtoindex(getx));
			startx = getx;
			starty = event.getY();
			linex = leftmargin + (xtoindex(getx) - startnum) * wavexs;
			startlinex = linex;
		} else if (MotionEvent.ACTION_MOVE == event.getAction()) {
			show = true;
			endx = event.getX();
			endy = event.getY();
			getx = endx;
			linex = leftmargin + (xtoindex(getx) - startnum) * wavexs;
			biaoxianx = indextof(xtoindex(endx));
			// if(Math.abs(starty-endy)-Math.abs(startx-endx)>0)//纵向滑动
			// {
			if (endy - starty > chufazhi && ah >= 40)// 向下滑动
			{
				ah = ah - 10;
				al = al + 10;
				starty = endy;
				startx = endx;
			} else if (starty - endy > chufazhi)// 向上滑动
			{
				ah = ah + 10;
				al = al - 10;
				starty = endy;
				startx = endx;
			}
			// }
		} else if (MotionEvent.ACTION_UP == event.getAction()) {
			show = false;
			endx = event.getX();
			endy = event.getY();
		}
		postInvalidate();
		return true;
	}

	public void clear() {
		setHave(false);
		postInvalidate();
	}

}
