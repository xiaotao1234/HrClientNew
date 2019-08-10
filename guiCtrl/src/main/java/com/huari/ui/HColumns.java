package com.huari.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HColumns extends View {

	private Paint graypaint;
	private Paint greenpaint;
	private Paint yellowpaint;
	private Paint whitepaint;
	private RectF rectf1, rectf2;
	private Rect rect1, rect2, rect3, rect4, rect5, rect6;// 5、6为三角形游标区域
	private float width, height;
	private float dppere;// 每个电平占多少dp
	private float dpperq;// 每个质量值占多少dp
	private int e, q;// 电平，质量的实时值
	private float etexty, qtexty;
	private float space;// 电平值100和-20之外，在展示条上，两端还有space个dp的空闲
	private int ethreshold, qthreshold; // 电平和质量的阈值
	private Path textepath, textqpath, etrianglepath, qtrianglepath;
	private float totop, tobottom;// 距离上/下边距离，即展示条外侧距离边界的距离
	private float toleft;// 距离左边界距离
	private float endx;// 展示条终端的x坐标值（单位：dp)
	private float columnheight;// 展示条的纵向高度
	private float extendlength;// 黄色游标超出展示条右边界的长度，也是其右端高度的一半。游标右端的高度由它控制。
	private float currentx, currenty;
	private boolean atezone, atqzone;// 用以控制是否移动游标。若触点在游标区域内则置为true，游标移动。
	public boolean pause;

	public HColumns(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		ini();
	}

	public HColumns(Context context) {
		super(context);
		ini();
	}

	public void refresh(int level, int quality) {
		if (pause == false) {
			e = level;
			q = quality;
			postInvalidate();
		}
	}

	/**
	 * 设置展示条两端坐标开始的位置距离展示条顶端的距离。通常为几个dp。即从展示条顶端往中心数space个dp的距离开始出现坐标线。
	 * 
	 * @param space
	 */
	public void setSpace(float space) {
		this.space = space;
	}

	/**
	 * 设置展示条顶端距离左/右边界的距离。默认为2dp。
	 * 
	 * @param toleft
	 */
	public void setToleft(float toleft) {
		this.toleft = toleft;
	}

	/**
	 * 设置展示条的宽度。即在纵向上的高度。
	 * 
	 * @param columnheight
	 */
	public void setColumnheight(float columnheight) {
		this.columnheight = columnheight;
	}

	/**
	 * 设置电平的阈值。
	 * 
	 * @param ethreshold
	 */
	private void setEthreshold(int ethreshold) {
		this.ethreshold = ethreshold;
	}

	/**
	 * 设置质量的阈值。
	 * 
	 * @param qthreshold
	 */
	private void setQthreshold(int qthreshold) {
		this.qthreshold = qthreshold;
	}

	/**
	 * 设置电平的实时值。
	 * 
	 * @param e
	 */
	public void setE(int e) {
		this.e = e;
	}

	/**
	 * 设置质量的实时值。
	 * 
	 * @param q
	 */
	public void setQ(int q) {
		this.q = q;
	}

	/**
	 * 获取电平的阈值。
	 * 
	 * @return
	 */
	public int getEthreshold() {
		return ethreshold;
	}

	/**
	 * 获取质量的阈值。
	 * 
	 * @return
	 */
	public int getQthreshold() {
		return qthreshold;
	}

	/**
	 * 设置第一条展示条（即电平展示条）距离上边界的距离。默认为70dp。
	 * 
	 * @param totop
	 */
	public void setTotop(float totop) {
		this.totop = totop;
	}

	/**
	 * 设置第二条展示条（质量展示条)距离下边界的距离。默认为25dp。
	 * 
	 * @param tobottom
	 */
	public void setTobottom(float tobottom) {
		this.tobottom = tobottom;
	}

	/**
	 * 设置“电平”、“质量”两个字符串的字体大小。默认为16。
	 * 
	 * @param textSize
	 */
	public void setYellowPaintTextSize(float textSize) {
		yellowpaint.setTextSize(textSize);
	}

	/**
	 * 设置“电平dBuV"、“质量%”的字体大小。默认为系统默认值。
	 * 
	 * @param textSize
	 */
	public void setWhitePaintTextSize(float textSize) {
		whitepaint.setTextSize(textSize);
	}

	/**
	 * 设置字符串“电平阈值”距离电平展示条上边框的距离。默认为30。
	 * 
	 * @param etexty
	 */
	public void setEtexty(float etexty) {
		this.etexty = etexty;
	}

	/**
	 * 设置字符串“质量阈值”距离质量展示条上边框的距离。默认为30。
	 * 
	 * @param qtexty
	 */
	public void setQtexty(float qtexty) {
		this.qtexty = qtexty;
	}

	private void ini() {
		setBackgroundColor(Color.BLACK);
		space = 9;
		e = -20;
		toleft = 2;
		totop = 70;
		tobottom = 25;
		columnheight = 25;
		extendlength = 8;
		etexty = 30;
		qtexty = 30;
		graypaint = new Paint();
		graypaint.setColor(Color.GRAY);
		greenpaint = new Paint();
		greenpaint.setColor(Color.GREEN);
		yellowpaint = new Paint();
		yellowpaint.setColor(Color.YELLOW);
		yellowpaint.setTextSize(16);
		yellowpaint.setTextAlign(Align.RIGHT);
		whitepaint = new Paint();
		whitepaint.setColor(Color.WHITE);
		whitepaint.setStrokeWidth(2);
		whitepaint.setTextSize(15);
		whitepaint.setTextAlign(Align.CENTER);
		textepath = new Path();
		textqpath = new Path();
		etrianglepath = new Path();
		qtrianglepath = new Path();

	}

	private int xtoe(float x) {
		int b = -20 + (int) ((x - toleft - space) / dppere);
		if (b > 100) {
			b = 100;
		} else if (b < -20) {
			b = -20;
		}
		return b;
	}

	private int xtoq(float x) {
		int a = (int) ((x - space - toleft) / dpperq);
		if (a > 100) {
			a = 100;
		} else if (a < 0) {
			a = 0;
		}
		return a;
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
		endx = width - toleft;
		float mm = width - toleft * 2 - space * 2;
		dppere = mm / 120;
		dpperq = mm / 100;
		rect1 = new Rect((int) toleft, (int) totop, (int) endx,
				(int) (totop + columnheight));
		rect2 = new Rect((int) toleft,
				(int) (height - tobottom - columnheight), (int) endx,
				(int) (height - tobottom));
		rect5 = new Rect(0, (int) (totop - 40), (int) width, (int) (totop
				+ columnheight + extendlength));
		rect6 = new Rect(0, (int) (height - tobottom - columnheight - 20),
				(int) width, (int) (height - tobottom + extendlength));
		rectf1 = new RectF(rect1);
		rectf2 = new RectF(rect2);
	}

	public void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		float nn = height - tobottom - columnheight;// 第二条展示条的上边界坐标（单位：dp)
		float ethresholdtodp = toleft + space + (ethreshold + 20) * dppere;
		float qthresholdtodp = toleft + space + qthreshold * dpperq;
		etrianglepath.reset();
		etrianglepath.moveTo(ethresholdtodp, totop - 1);
		etrianglepath.lineTo(ethresholdtodp + extendlength, totop
				+ columnheight + extendlength);
		etrianglepath.lineTo(ethresholdtodp - extendlength, totop
				+ columnheight + extendlength);
		etrianglepath.lineTo(ethresholdtodp, totop - 1);
		qtrianglepath.reset();
		qtrianglepath.moveTo(qthresholdtodp, height - tobottom - columnheight
				- 1);
		qtrianglepath.lineTo(qthresholdtodp + extendlength, height - tobottom
				+ extendlength);
		qtrianglepath.lineTo(qthresholdtodp - extendlength, height - tobottom
				+ extendlength);
		qtrianglepath.lineTo(qthresholdtodp, height - tobottom - columnheight
				- 1);
		rect3 = new Rect((int) toleft, (int) (totop), (int) ((e + 20) * dppere
				+ space + toleft), (int) (totop + columnheight));
		rect4 = new Rect((int) toleft,
				(int) (height - tobottom - columnheight), (int) (q * dpperq
						+ space + toleft), (int) (height - tobottom));
		canvas.drawRoundRect(rectf1, 4, 4, graypaint);
		canvas.drawRoundRect(rectf2, 4, 4, graypaint);
		canvas.drawText("电平dBuV", 40, totop - etexty, whitepaint);
		canvas.drawText("质量%    ", 40, nn - qtexty, whitepaint);
		canvas.drawText("电平阈值 " + ethreshold, width - 5, totop - etexty,
				yellowpaint);
		canvas.drawText("质量阈值 " + qthreshold, width - 5, nn - qtexty,
				yellowpaint);
		for (int i = 0; i <= 120; i = i + 10) {
			float ff = toleft + space + i * dppere;
			float hh = toleft + space + i * dpperq;
			if (i % 20 == 0) {
				canvas.drawLine(ff, totop - 8, ff, totop, whitepaint);
				canvas.drawText(-20 + i + "", ff, totop - 9, whitepaint);
				if (i <= 100) {
					canvas.drawText(i + "", hh, nn - 9, whitepaint);
					canvas.drawLine(hh, nn, hh, nn - 8, whitepaint);
				}
			} else {
				canvas.drawLine(ff, totop - 5, ff, totop, whitepaint);
				if (i <= 100)
					canvas.drawLine(hh, nn - 5, hh, nn, whitepaint);
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
				setEthreshold(xtoe(currentx));
			} else if (rect6.contains((int) currentx, (int) currenty)) {
				atqzone = true;
				setQthreshold(xtoq(currentx));
			}
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (atezone == true) {
				setEthreshold(xtoe(currentx));
			} else if (atqzone == true) {
				setQthreshold(xtoq(currentx));
			}
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			atezone = false;
			atqzone = false;
		}
		return true;

	}

}
