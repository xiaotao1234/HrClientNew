package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VColumns extends View {

	private Paint graypaint;
	private Paint greenpaint;
	private Paint yellowpaint;
	private Paint whitepaint;
	private RectF rectf1, rectf2;
	private Rect rect1, rect2, rect3, rect4, rect5, rect6;    // 5、6为三角形游标区域
	private float width, height;
	private float dppere;          // 每个电平占多少dp
	private float dpperq;          // 每个质量值占多少dp
	private int e, q;              // 电平，质量的实时值
	private int space;             // 电平值100和-20之外在展示条两端还有space个dp的空闲
	private int ethreshold, qthreshold = 30; // 电平和质量的阈值
	private Path textepath, textqpath, etrianglepath, qtrianglepath;
	private int onex0;             // 距离左/右边距离，即展示条外侧距离边界的距离
	private int halftocenter;      // 内侧距离中心线的距离
	private int oney0;             // 距离上部距离
	private int toleft;            // 距离上边界距离
	private int barwidth;
	private int onex1;
	private int oney1;
	private int twox0;
	private int twox1;
	private int extendlength;      // 黄色游标超出展示条右边界的长度，也是其右端高度的一半。游标右端的高度由它控制。
	private float currentx, currenty;
	private boolean atezone, atqzone; // 用以控制是否移动游标。若触点在游标区域内则置为true，游标移动。
	public boolean pause = true;

	/**
	 * 设置条形的宽度
	 * 
	 * @param w
	 */
	public void setBarwidth(int w) {
		barwidth = w;
	}

	/**
	 * 有新数据来到时，刷新
	 * 
	 * @param level
	 * @param quality
	 */
	public void refresh(int level, int quality) {
		e = level;
		q = quality;
		postInvalidate();
	}

	public void setPause(boolean p) {
		pause = p;
	}

	public void vcclear() {
		setE(-20);
		setQ(0);
		postInvalidate();
	}

	/**
	 * 用以设置展示条外侧距离左/右边界的距离
	 * 
	 * @param a
	 */
	public void setOnex0(int a) {
		onex0 = a;
	}

	/**
	 * 设置电平的实时值
	 * 
	 * @param e0
	 *            int型，电平的实时值
	 */
	public void setE(int e0) {
		e = e0;
	}

	/**
	 * 设置质量的实时值
	 * 
	 * @param q0
	 *            int型，质量的实时值
	 */
	public void setQ(int q0) {
		q = q0;
	}

	/**
	 * 设置电平的阈值
	 * 
	 * @param ethreshold
	 */
	public void setEthreshold(int ethreshold) {
		this.ethreshold = ethreshold;
	}

	/**
	 * 设置质量的阈值
	 * 
	 * @param qthreshold
	 */
	public void setQthreshold(int qthreshold) {
		this.qthreshold = qthreshold;
	}

	/**
	 * 获取电平的阈值
	 * 
	 * @return
	 */
	public int getEthreshold() {
		return ethreshold;
	}

	/**
	 * 获取质量的阈值
	 * 
	 * @return
	 */
	public int getQthreshold() {
		return qthreshold;
	}

	private void ini() {
		setBackgroundColor(Color.BLACK);
		space = 7;
		e = -20;
		barwidth = 32;
		// onex0 = 80;
		oney0 = 30;
		toleft = 2;
		halftocenter = 20;
		graypaint = new Paint();
		graypaint.setColor(Color.GRAY);
		greenpaint = new Paint();
		greenpaint.setColor(Color.GREEN);
		yellowpaint = new Paint();
		yellowpaint.setColor(Color.YELLOW);
		yellowpaint.setTextSize(16);
		whitepaint = new Paint();
		whitepaint.setColor(Color.BLACK);
		whitepaint.setStrokeWidth(2);
		whitepaint.setTextSize(15);
		whitepaint.setTextAlign(Align.LEFT);
		textepath = new Path();
		textqpath = new Path();
		etrianglepath = new Path();
		qtrianglepath = new Path();
		extendlength = 8;
	}

	public VColumns(Context context) {
		super(context);
		// this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		ini();
	}

	public VColumns(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		// this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		ini();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		width = getWidth();
		height = getHeight();
		onex0 = (int) (width / 2 - halftocenter - barwidth);
		oney1 = (int) (height - toleft);
		twox0 = (int) (width - onex0 - barwidth);
		twox1 = (int) (width - onex0);
		onex1 = onex0 + barwidth;
		dppere = (height - oney0 - toleft - 2 * space) / 120;
		dpperq = (height - oney0 - toleft - 2 * space) / 100;
		rect1 = new Rect(onex0, oney0, onex1, oney1);
		rect2 = new Rect(twox0, oney0, twox1, oney1);
		rect5 = new Rect(onex0 - 35, oney0, onex1 + extendlength,
				(int) (height - toleft));
		rect6 = new Rect(twox0 - 1, oney0, twox1 + 35, (int) (height - toleft));
	}

	public void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		int etodp = (int) (height - toleft - space - (e + 20) * dppere);// 表示电平实时值的展示条的上端纵坐标
		int qtodp = (int) (height - toleft - space - q * dpperq);// 表示质量实时值的展示条的上端纵坐标
		int ethresholdtodp = (int) (height - toleft - space - (ethreshold + 20)
				* dppere);
		int qthresholdtodp = (int) (height - toleft - space - qthreshold
				* dpperq);
		etrianglepath.reset();
		etrianglepath.moveTo(onex0 - 1, ethresholdtodp);
		etrianglepath.lineTo(onex1 + extendlength, ethresholdtodp
				- extendlength);
		etrianglepath.lineTo(onex1 + extendlength, ethresholdtodp
				+ extendlength);
		etrianglepath.lineTo(onex0 - 1, ethresholdtodp);
		qtrianglepath.reset();
		qtrianglepath.moveTo(twox0 - 1, qthresholdtodp);
		qtrianglepath.lineTo(twox1 + extendlength, qthresholdtodp
				- extendlength);
		qtrianglepath.lineTo(twox1 + extendlength, qthresholdtodp
				+ extendlength);
		qtrianglepath.lineTo(twox0 - 1, qthresholdtodp);
		rect3 = new Rect(onex0, etodp, onex1, oney1);
		rect4 = new Rect(twox0, qtodp, twox1, oney1);
		rectf1 = new RectF(rect1);
		rectf2 = new RectF(rect2);
		textepath.moveTo(onex0 - 50, height / 2 + 30);
		textepath.lineTo(onex0 - 50, height / 2 - 50);
		textqpath.moveTo(twox1 + 50, height / 2 + 30);
		textqpath.lineTo(twox1 + 50, height / 2 - 50);
		canvas.drawRoundRect(rectf1, 4, 4, graypaint);
		canvas.drawRoundRect(rectf2, 4, 4, graypaint);
		canvas.drawText("电平" + " " + ethreshold, (float) (onex0 - 35),
				oney0 - 10, yellowpaint);
		canvas.drawText("质量" + " " + qthreshold, (float) (twox0 + 12),
				oney0 - 10, yellowpaint);
		canvas.drawTextOnPath("电平dBuV", textepath, 0, 0, whitepaint);
		canvas.drawTextOnPath("质量 %", textqpath, 0, 0, whitepaint);
		for (int i = 0; i <= 120; i = i + 10) {
			if (i % 20 == 0) {
				canvas.drawLine(onex0 - 8, height
						- (toleft + space + i * dppere), onex0, height
						- (toleft + space + i * dppere), whitepaint);// 画电平的标线
				canvas.drawText(-20 + i + "", (float) (onex0 - 35), height
						- (i * dppere + 3), whitepaint);// 画电平的标值
				if (i <= 100) {
					canvas.drawLine(twox1, height
							- (toleft + space + i * dpperq), twox1 + 8, height
							- (toleft + space + i * dpperq), whitepaint);// 画质量的标线
					canvas.drawText(i + "", (float) (twox1 + 9), height
							- (2 + i * dpperq), whitepaint);
				}

			} else {
				canvas.drawLine(onex0 - 5, height
						- (toleft + space + i * dppere), onex0, height
						- (toleft + space + i * dppere), whitepaint);
				if (i <= 100)
					canvas.drawLine(twox1, height
							- (toleft + space + i * dpperq), twox1 + 5, height
							- (toleft + space + i * dpperq), whitepaint);
			}
		}
		canvas.drawRect(rect3, greenpaint);
		canvas.drawRect(rect4, greenpaint);
		canvas.drawPath(etrianglepath, yellowpaint);
		canvas.drawPath(qtrianglepath, yellowpaint);
	}

	public boolean onTouchEvent(MotionEvent event) {
		currentx = event.getX();
		currenty = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (rect5.contains((int) currentx, (int) currenty)) {
				atezone = true;
				setEthreshold(ytoe(currenty));
			} else if (rect6.contains((int) currentx, (int) currenty)) {
				atqzone = true;
				setQthreshold(ytoq(currenty));
			}
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (atezone == true) {
				setEthreshold(ytoe(currenty));
			} else if (atqzone == true) {
				setQthreshold(ytoq(currenty));
			}
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			atezone = false;
			atqzone = false;
		}
		return true;

	}

	private int ytoe(float y) {
		int b = -20 + (int) ((height - space - toleft - y) / dppere);
		if (b > 100) {
			b = 100;
		} else if (b < -20) {
			b = -20;
		}
		return b;
	}

	private int ytoq(float y) {
		int c = (int) ((height - space - toleft - y) / dpperq);
		if (c > 100) {
			c = 100;
		} else if (c < 0) {
			c = 0;
		}
		return c;
	}

}
