package com.huari.ui;

import java.util.ArrayList;


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
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 频段扫描，上方的控件
 *
 * @author jianghu
 *
 */
public class PinScanningShowWave extends View {
	private int mywidth, myheight;// 单位: dp
	Context con;
	int no=0;
	float mk_x = 0, mk_y = 0, inputf = 0;
	boolean mk = false;

	String danwei = "dBuV";// 是场强的dBuV/m还是电平的dBuV
	String danweiName = "幅度";

	LinearLayout freqinputLayout;
	EditText inputtext;
	public void setDanwei(String danweiName, String danwei) {
		try {
			this.danweiName = danweiName;
			this.danwei = danwei;
			fudupath.reset();
			postInvalidate();
		} catch (Exception e) {

		}
	}

	public void setinputf(float inputfreq)
	{
		inputf = inputfreq;
	}

	private float al = -30f, ah = 80f;// 分别代表y轴上两端的幅度值，即最小值和最大值。默认为-20和80。
	public int leftmargin, upmargin;// 坐标横向和纵向上的起点，即纵轴从x坐标为leftmargin处画起，
	// 左侧有leftmargin宽的空白区，横轴从upmargin处画起，上侧有upmargin高的空白
	private float fl, fh;// 分别代表x轴上两端的频率值，即最小值和最大值
	// private float xpoint;// 每个dp代表多少频率差值
	//private float disbetweenpoints;
	private Paint wavepaint;
	private Paint maxpaint;
	private Paint minpaint;
	private Paint avgpaint;
	private Paint mengbanpaint;
	private Paint markpaint;
	private Paint marginpaint, textpaint, p2, shutextpaint, yuzhipaint,
			textpaint2;// 画示波界面四条边框的画笔
	private Paint virtualpaint;// 画示波界面内虚线的画笔
	private PathEffect effects;// 用以制作虚线效果
	private short[] m;
	private short[] max;
	private short[] min;
	private short[] avg;
	private float startx = 0, endx = 0, textx;// 分别记录触屏事件的起点、终点的x坐标,纵坐标标值的x起点
	private float starty = 0, endy = 0;// 分别记录触屏事件的起点、终点的y坐标
	private float headf, tailf, bujin;// 调频率后两端的频率值,bujin单位Hz
	private int showpointcount;
	float newbujin = bujin / 1000;
	private float biaoxianx;
	private float getx;
	private boolean show = false;// 用以判断是否需要画出触摸点所在的竖线，当为true时画出，否则不画出
	private float xp, yp;// 即x轴和y轴上每个小刻度的宽度,单位：dp
	private float aperdp;// 纵向上每个dp代表多少个幅度值
	private float wavexs;// 横向每个点占多少dp
	private float zeroatdp;
	private float fxlocation;
	private float fylocation;
	private Path fudupath;
	private boolean showyz=true;
	private Rect r;
	private float chufazhi;
	private int startnum, endnum;// 要展示的一串点的第一个点和最后一个点的索引
	private int count;
	private float linex, startlinex;
	private boolean yuzhiboolean;// 当纵向滑动时，是调整阈值，还是调整幅度值
	public float yuzhifudu = 70;// 展示阈值线所标的刻度（幅度），也是阈值。
	private float yuzhixiany;
	private float networkheight;
	public ArrayList<Integer> datalist;
	static int marktxt_x = 300;

	// Canvas canvas;
	// DrawCacheBitmap drawCacheBitmapTread;

	// class DrawCacheBitmap extends Thread
	// {
	// public Handler handler;
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// Looper.prepare();
	// handler=new Handler()
	// {
	// public void handleMessage(android.os.Message msg) {
	// if(msg.what==1)
	// {
	// canvas.drawColor(Color.BLACK);
	// //showpointcount=(int) ((tailf - headf) * 1000 / bujin + 1);
	// showpointcount=endnum-startnum+1;
	// wavexs = (float) ((float) (mywidth - leftmargin - 13) /
	// (showpointcount-1));// 宽度被分成了若干份
	// DrawXY(canvas);
	// canvas.drawText(yuzhifudu+"dBuV", 1, myheight-50, yuzhipaint);
	// drawLinee(canvas);
	//
	// if (show == true && m!=null) {
	// Biaoxian( canvas);
	// }
	// if(max!=null && min!=null && avg!=null){
	// DrawWaves(canvas, maxpaint, arraytoy(max));
	// DrawWaves(canvas, minpaint, arraytoy(min));
	// DrawWaves(canvas, avgpaint, arraytoy(avg));
	// }
	// if(m!=null){
	// DrawWaves(canvas, wavepaint, arraytoy(m));
	// }
	// postInvalidate();
	// };
	// }
	// };
	// Looper.loop();
	// }
	// }


	public void settailf(float tailf) {
		this.tailf = tailf;
	}

	public void setMk(boolean mk) {
		this.mk = mk;
	}

	public void setBujin(float bujin) {
		this.bujin = bujin;
	}

	public void setTiaoZhi(boolean yuzhi) {
		yuzhiboolean = yuzhi;
	}

	public void setParams(float headf, float tailf, float bujin) {
		this.headf = headf;
		this.tailf = tailf;
		this.bujin = bujin;
	}

	public void setDanwei(String s) {
		danwei = s;
	}

	public void setyuzhi(boolean value)
	{
		showyz = value;
	}

	public void initialize() {
		// drawCacheBitmapTread=new DrawCacheBitmap();
		// drawCacheBitmapTread.start();
		leftmargin = 65;
		textx = leftmargin - 5;
		upmargin = 8;
		fl = 88.000f;
		fh = 108.000f;
		tailf = fh;
		headf = fl;
		bujin = 25;
		count = (int) ((fh - fl) * 1000 / bujin);
		wavepaint = new Paint();
		maxpaint = new Paint();
		minpaint = new Paint();
		avgpaint = new Paint();
		textpaint = new Paint();
		textpaint2 = new Paint();
		shutextpaint = new Paint();
		mengbanpaint = new Paint();
		markpaint = new Paint();
		markpaint.setColor(Color.RED);
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
		maxpaint.setStrokeWidth(2);
		minpaint.setStrokeWidth(2);
		maxpaint.setStrokeWidth(2);
		avgpaint.setStrokeWidth(2);
		marginpaint = new Paint();
		marginpaint.setColor(Color.parseColor("#70f3ff"));
		marginpaint.setStrokeWidth(0.4f);
		marginpaint.setTextSize(16);
		textpaint.setColor(Color.parseColor("#70f3ff"));
		textpaint.setTextAlign(Align.CENTER);
		textpaint.setTextSize(20);
		textpaint2.setColor(Color.parseColor("#70f3ff"));
		textpaint2.setTextAlign(Align.RIGHT);
		textpaint2.setTextSize(20);
		shutextpaint.setColor(Color.parseColor("#70f3ff"));
		shutextpaint.setTextAlign(Align.RIGHT);
		shutextpaint.setTextSize(16);
		virtualpaint = new Paint();
		virtualpaint.setColor(Color.parseColor("#70f3ff"));
		virtualpaint.setStyle(Paint.Style.STROKE);
		p2 = new Paint();
		p2.setColor(Color.YELLOW);
		p2.setTextSize(16);
		effects = new DashPathEffect(new float[] { 1, 2 }, 1);// 虚线风格
		virtualpaint.setStyle(Style.STROKE);

		marginpaint.setStrokeWidth(2);

		yuzhipaint = new Paint();
		yuzhipaint.setColor(Color.WHITE);
		yuzhipaint.setStrokeWidth(2);
		yuzhipaint.setTextAlign(Align.LEFT);

		fudupath = new Path();
		startnum = 0;
		endnum = count - 1;
		showpointcount = count;

		// canvas=new Canvas();
		// canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
		// Paint.ANTI_ALIAS_FLAG
		// | Paint.FILTER_BITMAP_FLAG)); // 抗锯齿效果

		datalist = new ArrayList<Integer>();
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	public void find_marker(int flag)
	{
		int begin=0,end=0;
		float tmp= -200f;

		if (m!=null) {
			if (flag == 0)
			{
				begin = 0;
				end = m.length;
			}
			if (flag == 1)
			{
				begin = 0;
				end = no-1;
			}
			if (flag == 2)
			{
				begin = no+1;
				end = m.length;
			}

			for (int i = begin; i < end; i++)
			{
				if (m[i]>tmp)
				{
					tmp = m[i];
					no = i;
				}
			}
			mk = true;
			mk_x = leftmargin + wavexs * no;
			mk_y = tmp;
			Log.d("guidebug",String.format("no =%d / %d",no,m.length) + String.format("   x = %8.3f",(float)mk_x));
		}
	}

	public void find_input(float inputf)
	{
		int begin=0,end=0;

		if (m!=null) {
			begin = 0;
			end = m.length;

			for (int i = begin; i < end; i++)
			{
				if (fl+(fh-fl)/showpointcount*i >= inputf)
				{
					mk_y = m[i];
					no = i;
					mk = true;
					mk_x = leftmargin + wavexs * no;
					break;
				}
			}

			if (mk)
				Log.d("guidebug",String.format("no =%d / %d",no,m.length) + String.format("   x = %8.3f",(float)mk_x));
		}
	}

	public void refreshListData() {
		int a = 0;
		try {
			datalist.clear();
			for (int i = startnum; i <= endnum; i++) {
				if (m[i] >= yuzhifudu) {
					datalist.add(i);// 存放的是达标的数据的索引
					a = a + 1;
					if (a > DataSave.pinduanshowpointcounts - 1)
						break;
				}
			}
		} catch (Exception e) {
			if (datalist == null) {
				System.out.println("datalist是空的");
			}
			if (m == null) {
				System.out.println("m是空的");
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		chufazhi = getWidth() / 6;
		mywidth = getWidth();
		myheight = getHeight();
		xp = ((float) (mywidth - leftmargin - 13)) / 20;// 横向上每个坐标的间距，单位dp。横向上总共有20列。
		// 坐标界面的左边有x1-1个dp的空白，右边有14dp的空白.
		networkheight = myheight - upmargin - 33;
		yp = ((float) networkheight) / 20;// 纵向上每个坐标点间的间距，单位是dp
		yuzhixiany = yuxianzhitoy(yuzhifudu);
		// cacheBitmap=Bitmap.createBitmap(w,h,Config.ARGB_8888);
		// canvas.setBitmap(cacheBitmap);
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
		count = (int) ((fh - fl) * 1000 / bujin);
		startnum = 0;
		endnum = count - 1;
		showpointcount = count;
		postInvalidate();
	}

	/**
	 * public void setMax(int[] max) <br/>
	 * 用来设置max[]
	 *
	 * @param max
	 *            一个含有count个元素的整型数组，用以表示count个元素各自对应的最高值
	 *
	 */
	public void setMax(short[] max) {// 为该View设置max[]
		this.max = max;
	}

	/**
	 * public void setMin(int[] min) <br/>
	 * 用来设置min[]
	 *
	 * @param min
	 *            一个含有count个元素的整型数组，用以表示count个元素各自对应的最小值
	 */
	public void setMin(short[] min) {
		this.min = min;
	}

	/**
	 * public void setAvg(int[] avg) <br/>
	 * 用来设置avg[ ]
	 *
	 * @param avg
	 *            一个含有count个元素的整型数组，用以表示count个元素各自对应的平均值
	 */
	public void setAvg(short[] av) {
		this.avg = av;
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
	 * 设置为true，则表示进入调整阈值的模式，当在界面上上下滑动时不会再调整幅度值的大小，而是调整阈值的大小。
	 *
	 * @param yuzhiboolean
	 */
	public void setAdjustLimit(boolean yuzhiboolean) {
		this.yuzhiboolean = yuzhiboolean;
	}

	public int getMValue(int i) {
		return m[i];
	}

	public void setDanweiName(String n) {
		danweiName = n;
	}

	public PinScanningShowWave(Context context) {
		super(context);
		con = context;
		initialize();
	}

	public PinScanningShowWave(Context context, AttributeSet set) {
		super(context, set);
		con = context;
		initialize();
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG)); // 抗锯齿效果
		canvas.drawColor(Color.BLACK);
		// showpointcount=(int) ((tailf - headf) * 1000 / bujin + 1);
		showpointcount = endnum - startnum + 1;
		wavexs = (float) ((float) (mywidth - leftmargin - 13) / (showpointcount - 1));// 宽度被分成了若干份
		DrawXY(canvas);
		//canvas.drawText(yuzhifudu + danwei, 1, myheight - 50, yuzhipaint);
		drawLinee(canvas);

		if (show == true && m != null) {
			Biaoxian(canvas);
		}
		if (max != null) {
			DrawWaves(canvas, maxpaint, arraytoy(max));

		}
		if (min != null) {
			DrawWaves(canvas, minpaint, arraytoy(min));

		}
		if (avg != null) {

			DrawWaves(canvas, avgpaint, arraytoy(avg));
		}
		if (m != null) {
			DrawWaves(canvas, wavepaint, arraytoy(m));

			if (mk && (no >= startnum && no <= endnum))
			{
				Path mkpath = new Path();
				mk_x = leftmargin + wavexs * (no-startnum);

				mkpath.moveTo(mk_x, zeroatdp - m[no] / aperdp); //aperdp 纵向上每个dp代表多少个幅度值
				mkpath.lineTo(mk_x-20,zeroatdp - m[no] / aperdp-29);
				mkpath.lineTo(mk_x+20,zeroatdp - m[no] / aperdp-29);
				//Log.d("guidebug",String.format("x=%8.3f",mk_x) + String.format("   y = %8.3f",(float)zeroatdp - m[no] / aperdp));
				mkpath.close();

				markpaint.setColor(Color.RED);
				canvas.drawPath(mkpath,markpaint);

				markpaint.setColor(Color.WHITE);
				markpaint.setTextSize(40);
				canvas.drawText(String.format("Mkr1: %8.3f MHz, ",headf+(tailf-headf)/showpointcount*(no-startnum))+String.format("%6.3f dBuV",(float)m[no]),marktxt_x,80,markpaint); //绘制文字
			}
		}
		// canvas.drawBitmap(cacheBitmap, 0, 0, wavepaint);

	}

	private void DrawXY(Canvas canvas)// 画坐标
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
		fudupath.moveTo(15, (myheight - 33) / 2 + 80);
		fudupath.lineTo(15, (myheight - 33) / 2 - 84);
		canvas.drawText("频 率  (MHz)", fxlocation, fylocation, textpaint2);
		canvas.drawLine(leftmargin - 4, zeroatdp, leftmargin, zeroatdp,
				marginpaint);
		// canvas.drawText(0 + "", textx, zeroatdp + 3, shutextpaint);// 标0幅度的位置
		canvas.drawTextOnPath(danweiName + "(" + danwei + ")", fudupath, 0, 0,
				textpaint);

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

	private int xtoindex(float x) {   //x坐标转点数
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

	public float indextof(int n) {   //点数转频率
		float f = 0;
		f = n * bujin / 1000 + fl;
		return f;
	}

	private float ytofudu(float y) {
		float f = al + aperdp * (myheight - 33 - y);
		// return Math.round(f*100)/100.00f;
		return Math.round(f);
	}

	private float yuxianzhitoy(float yuxianzhi) {
		float y = myheight - 33 - (yuzhifudu - al) / (ah - al)
				* (myheight - 33 - upmargin);
		return y;
	}

	private void drawLinee(Canvas canvas) {
		Rect r = new Rect(leftmargin, upmargin, (int) (leftmargin + xp * 20),
				(int) (upmargin + yp * 20));
		canvas.save();
		canvas.clipRect(r, Op.INTERSECT);

		if (showyz)
			canvas.drawLine(leftmargin, yuzhixiany, leftmargin + xp * 20,
					yuzhixiany, yuzhipaint);
		canvas.restore();
	}

	public void set_ah_al(float ah,float al)
	{
		this.ah = ah;
		this.al = al;
		postInvalidate();
	}

	private void DrawWaves(Canvas canvas, Paint p, int[] m)   // 画波形
	{
		Rect r = new Rect(leftmargin, upmargin, (int) (leftmargin + xp * 20),
				(int) (upmargin + yp * 20));
		canvas.clipRect(r, Op.INTERSECT);
		Path wavepath = new Path();

		// float temp1 = (float) (Math.round((headf - fl)*1000 / bujin));//
		// 保留三位小数。为什么要这么写而不是直接用（fl-88.000f)/0.025呢？
		// 因为实际运行时有可能出现非预期内的结果，如88.075-88.000结果可能为0.07435789而不是0.075，导致得出的startindex小1
		// float temp1=startnum;
		// int startindex = (int) temp1;
		// float temp2 = (float) (Math.round((tailf - fl)*1000 / bujin ));
		// float temp2=endnum;
		// int endindex = (int) temp2;

		wavepath.moveTo(leftmargin, m[startnum]);
		try {
			if (startnum != endnum) {
				int y = 1;  // 用以控制x坐标的逐步延伸
				for (int i = startnum + 1; i <= endnum; i++) {
					wavepath.lineTo(leftmargin + wavexs * y, m[i]);
					y++;
				}
			} else {
				wavepath.lineTo(leftmargin + 20 * xp, m[startnum]);
			}
			canvas.drawPath(wavepath, p);
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.i("数组", "越界");
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			if (yuzhiboolean) {
				yuzhixiany = event.getY();
				yuzhifudu = ytofudu(yuzhixiany);
			} else {
				show = true;
				getx = event.getX();
				biaoxianx = indextof(xtoindex(getx));
				startx = getx;
				starty = event.getY();
				linex = leftmargin + (xtoindex(getx) - startnum) * wavexs;
				startlinex = linex;
			}
		} else if (MotionEvent.ACTION_MOVE == event.getAction()) {
			if (yuzhiboolean) {
				yuzhixiany = event.getY();
				yuzhifudu = ytofudu(yuzhixiany);
			} else {
				show = true;
				endx = event.getX();
				endy = event.getY();
				getx = endx;
				linex = leftmargin + (xtoindex(getx) - startnum) * wavexs;
				biaoxianx = indextof(xtoindex(endx));
				if (Math.abs(starty - endy) - Math.abs(startx - endx) > 0)// 纵向滑动
				{
					if (endy - starty > chufazhi && ah >= 30)// 向下滑动
					{
						al = al + 10;
						starty = endy;
						startx = endx;
					} else if (starty - endy > chufazhi)// 向上滑动
					{
						//ah = ah + 10;
						al = al - 10;
						starty = endy;
						startx = endx;
					}
					yuzhixiany = yuxianzhitoy(yuzhifudu);
				}
			}
		} else if (MotionEvent.ACTION_UP == event.getAction()) {
			if (yuzhiboolean) {
				yuzhixiany = event.getY();
				yuzhifudu = ytofudu(yuzhixiany);
			} else {
				show = false;
				endx = event.getX();
				endy = event.getY();

				if (Math.abs(endx - startx) - Math.abs(endy - starty) > 0)// 横向滑动
				{
					biaoxianx = indextof(xtoindex(endx));
					if (endx - startx > 0)// 向右滑动，即扩放
					{
						startnum = xtoindex(startx);
						System.out.println(startnum + "开始");
						endnum = xtoindex(endx);
						System.out.println("结束" + endnum);

						if (showpointcount > 1) {
							headf = indextof(startnum);
							// tailf=indextof(endnum);

							tailf = biaoxianx;
						} else {
							headf = tailf = indextof(startnum);
						}
						endnum = Math.round((tailf - fl) * 1000 / bujin);
						showpointcount = endnum - startnum + 1;
						System.out.println(endnum + "最后的索引");
					} else if (startx - endx > 10) {
						startnum = 0;
						endnum = count - 1;
						showpointcount = count;
						headf = fl;
						tailf = fh;
					}
				}
			}
		}
		postInvalidate();
		// notiRefresh();
		return true;
	}

	private void Biaoxian(Canvas can)// 画竖标线
	{
		float ttt = upmargin + yp * 20;
		can.drawLine(startlinex, upmargin, startlinex, ttt, p2);
		can.drawLine(linex, upmargin, linex, upmargin + yp * 20, p2);
		can.drawRect(new RectF(startlinex, upmargin, linex, ttt), mengbanpaint);
		can.drawText(biaoxianx + "MHz", 0, upmargin + 30, p2);
		can.drawText(m[xtoindex(getx)] + danwei, 2, upmargin + 50, p2);
	}

	// public void notiRefresh( )
	// {
	// drawCacheBitmapTread.handler.sendEmptyMessage(1);
	// }


}
