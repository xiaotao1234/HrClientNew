package com.huari.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SingDrawWave extends View {
	/**
	 * 单频测向，下方画波形的控件
	 */
	private float plistartx, plistopx, prostartx, prostopx, quastartx,
			quastopx;
	private int width, height;
	private float topy, bottomy;
	private String threemode;// 只有三种可能：gailv,zhiliang,fudu
	private Paint whitepaint, whitepaint1, greenpaint;
	private float dpPerDegreeByPli, dpPerDegreeByQua, dpPerDegreeByPro;
	private float dpPerY;// 纵向上dp/Y
	private boolean northboolean;

	public SingDrawWave(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		ini();
	}

	public SingDrawWave(Context context, AttributeSet attrs) {
		super(context, attrs);
		ini();
	}

	public SingDrawWave(Context context) {
		super(context);
		ini();
	}

	private TextView tv;

	public void setNorthBoolean(boolean northboolean) {
		this.northboolean = northboolean;
		postInvalidate();
	}

	public void setTextView(TextView tv) {
		this.tv = tv;
	}

	/**
	 * 只有三种特定值：gailv,fudu,zhiliang
	 * 
	 * @param mode
	 */
	public void setThreeMode(String mode) {
		threemode = mode;
		postInvalidate();
	}

	private void ini() {
		plistartx = prostartx = quastartx = 20;
		topy = 5;
		threemode = "fudu";
		northboolean = true;
		whitepaint = new Paint();
		whitepaint.setColor(Color.WHITE);
		whitepaint.setTextAlign(Align.CENTER);
		whitepaint1 = new Paint();
		whitepaint1.setColor(Color.WHITE);
		greenpaint = new Paint();
		greenpaint.setColor(Color.GREEN);
		tv = new TextView(getContext());

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getWidth();
		height = getHeight();
		plistopx = width - 60;
		quastopx = width - 40;
		prostopx = width - 50;
		bottomy = height - 25;
		dpPerDegreeByPli = (plistopx - plistartx) / 360.0f;
		dpPerDegreeByQua = (quastopx - quastartx) / 360.0f;
		dpPerDegreeByPro = (prostopx - prostartx) / 360.0f;
		dpPerY = (bottomy - topy) / 100;

	}

	private float degreeToPlix(float degree) {
		return plistartx + degree * dpPerDegreeByPli;
	}

	private float degreeToProx(float degree) {
		return prostartx + degree * dpPerDegreeByPro;
	}

	private float degreeToQuax(float degree) {
		return quastartx + degree * dpPerDegreeByQua;
	}

	private void drawPliXY(Canvas canvas) {
		canvas.drawLine(plistartx, bottomy, plistopx + 7, bottomy, whitepaint);// 横轴
		for (int i = 0; i <= 360; i = i + 30) {
			float s = plistartx + i * dpPerDegreeByPli;
			if (i % 90 == 0) {
				canvas.drawLine(s, bottomy, plistartx + i * dpPerDegreeByPli,
						bottomy + 8, whitepaint);
				canvas.drawText(i + 0.0 + "", s, bottomy + 20, whitepaint);
			} else {
				canvas.drawLine(s, bottomy, plistartx + i * dpPerDegreeByPli,
						bottomy + 5, whitepaint);
			}
		}
		canvas.drawLine(plistopx, topy, plistopx, bottomy + 7, whitepaint);
		for (int i = 0; i <= 100; i = i + 5) {
			float m = bottomy - i * dpPerY;
			if (i % 10 == 0) {
				canvas.drawLine(plistopx, m, plistopx + 6, m, whitepaint1);
				canvas.drawText(i + "dBuV", plistopx + 7, m + 4, whitepaint1);
			} else {
				canvas.drawLine(plistopx, m, plistopx + 4, m, whitepaint1);
			}
		}
	}

	private void drawPliWave(Canvas canvas) {

		if (northboolean)// 幅度，正北
		{
			Iterator<Float> it = DataSave.datamap.keySet().iterator();
			while (it.hasNext()) {
				Float key = (Float) it.next();
				float x = degreeToPlix(key);
				canvas.drawLine(x, bottomy, x, bottomy - dpPerY
						* DataSave.datamap.get(key).maxplitude, greenpaint);
			}
		} else// 幅度，相对角度
		{
			Iterator<Float> it = DataSave.datamap.keySet().iterator();
			while (it.hasNext()) {
				Float key = (Float) it.next();
				float x = degreeToPlix(DataSave.datamap.get(key).reldegree);
				canvas.drawLine(x, bottomy, x, bottomy - dpPerY
						* DataSave.datamap.get(key).maxplitude, greenpaint);
			}
		}
	}

	private void drawQuaXY(Canvas canvas) {
		canvas.drawLine(quastartx, bottomy, quastopx + 7, bottomy, whitepaint);// 横轴
		for (int i = 0; i <= 360; i = i + 30) {
			float s = quastartx + i * dpPerDegreeByQua;
			if (i % 90 == 0) {
				canvas.drawLine(s, bottomy, quastartx + i * dpPerDegreeByQua,
						bottomy + 8, whitepaint);
				canvas.drawText(i + 0.0 + "", s, bottomy + 20, whitepaint);
			} else {
				canvas.drawLine(s, bottomy, quastartx + i * dpPerDegreeByQua,
						bottomy + 5, whitepaint);
			}
		}
		canvas.drawLine(quastopx, topy, quastopx, bottomy + 7, whitepaint);// 竖轴
		for (int i = 0; i <= 100; i = i + 5) {
			float m = bottomy - i * dpPerY;
			if (i % 10 == 0) {
				canvas.drawLine(quastopx, m, quastopx + 6, m, whitepaint1);
				canvas.drawText(i + "%", quastopx + 7, m + 4, whitepaint1);
			} else {
				canvas.drawLine(quastopx, m, quastopx + 3, m, whitepaint1);
			}
		}
	}

	private void drawQuaWave(Canvas canvas) {
		if (northboolean)// 幅度，正北
		{
			Iterator<Float> it = DataSave.datamap.keySet().iterator();
			while (it.hasNext()) {
				Float key = (Float) it.next();
				float x = degreeToPlix(key);
				canvas.drawLine(x, bottomy, x, bottomy - dpPerY
						* DataSave.datamap.get(key).maxquality, greenpaint);
			}
		} else// 幅度，相对角度
		{
			Iterator<Float> it = DataSave.datamap.keySet().iterator();
			while (it.hasNext()) {
				Float key = (Float) it.next();
				float x = degreeToPlix(DataSave.datamap.get(key).reldegree);
				canvas.drawLine(x, bottomy, x, bottomy - dpPerY
						* DataSave.datamap.get(key).maxquality, greenpaint);
			}
		}
	}

	private void drawProXYandWave(Canvas canvas) {
		ArrayList<Map.Entry<Float, MyData>> list = DataSave
				.sortByPro(DataSave.datamap);
		int maxcount = 0, mincount = 0;
		float minpro = 0f, maxpro = 1f;
		if (list.size() > 0) {
			maxcount = list.get(0).getValue().count;// 出现的最多的次数
			mincount = list.get(list.size() - 1).getValue().count;// 出现的最少的次数
			minpro = mincount / (float) DataSave.sum;
			maxpro = maxcount / (float) DataSave.sum;
		}
		
		float protopvalue = (float) (Math.ceil(maxpro * 1000) / 10 + 1f);// 两位整数位，一位小数位。如protopvalue=16.7。
		float probottomvalue = (float) (Math.floor(minpro * 1000) / 10 - 1f);
		float segment = (protopvalue - probottomvalue) / 100;
		canvas.drawLine(prostartx, bottomy, prostopx + 7, bottomy, whitepaint);// 横轴
		canvas.drawLine(prostopx, topy, prostopx, bottomy, whitepaint);// 竖轴
		for (int i = 0; i <= 360; i = i + 30) {
			float s = prostartx + i * dpPerDegreeByPro;
			if (i % 90 == 0) {
				canvas.drawLine(s, bottomy, prostartx + i * dpPerDegreeByPro,
						bottomy + 8, whitepaint);
				canvas.drawText(i + 0.0 + "", s, bottomy + 20, whitepaint);
			} else {
				canvas.drawLine(s, bottomy, prostartx + i * dpPerDegreeByPro,
						bottomy + 5, whitepaint);
			}
		}
		if (DataSave.datamap.size() == 0) {// 比如初始化时，没有任何数据
			for (int i = 0; i <= 100; i = i + 5) {
				float m = bottomy - i * dpPerY;
				if (i % 2 == 0) {
					canvas.drawLine(prostopx, m, prostopx + 7, m, whitepaint);
					canvas.drawText(i + 0.0 + "%", prostopx + 7, m + 4,
							whitepaint1);
				} else {
					canvas.drawLine(prostopx, m, prostopx + 4, m, whitepaint);
				}
			}
		} else// 已接收到部分数据后
		{
			for (int i = 0; i <= 100; i = i + 5) {
				float m = bottomy - i * dpPerY;
				if (i % 2 == 0) {
					canvas.drawLine(prostopx, m, prostopx + 7, m, whitepaint);
					canvas.drawText(
							Math.round((probottomvalue + i * segment) * 10)
									/ 10.0f + "%", prostopx + 7, m + 4,
							whitepaint1);
				} else {
					canvas.drawLine(prostopx, m, prostopx + 4, m, whitepaint);
				}
			}
		}
		;
		if (northboolean)// 开始画波形，如果是正北角度
		{
			Iterator<Float> it = DataSave.datamap.keySet().iterator();
			while (it.hasNext()) {
				Float key = (Float) it.next();// 得到正北角度
				float x = degreeToProx(key);
				canvas.drawLine(x, bottomy, x, bottomy
						- (100 * DataSave.datamap.get(key).count
								/ (float) DataSave.sum - probottomvalue)
						/ (protopvalue - probottomvalue) * (bottomy - topy),
						greenpaint);
			}
		} else// 如果是相对角度
		{
			Iterator<Float> it = DataSave.datamap.keySet().iterator();
			while (it.hasNext()) {
				Float key = (Float) it.next();// 得到key
				float x = degreeToPlix(DataSave.datamap.get(key).reldegree);
				canvas.drawLine(x, bottomy, x, bottomy
						- (100 * DataSave.datamap.get(key).count
								/ (float) DataSave.sum - probottomvalue)
						/ (protopvalue - probottomvalue) * (bottomy - topy),
						greenpaint);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (threemode.equals("fudu")) {
			drawPliXY(canvas);
			drawPliWave(canvas);
		} else if (threemode.equals("gailv")) {
			drawProXYandWave(canvas);
		} else if (threemode.equals("zhiliang")) {
			drawQuaXY(canvas);
			drawQuaWave(canvas);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float currentX = event.getX();
		float currentY = event.getY();
		int action = event.getAction();
		float showdegree = 0;
		if (action == MotionEvent.ACTION_MOVE
				|| action == MotionEvent.ACTION_DOWN) {
			if (threemode.equals("fudu")) {
				showdegree = (float) (Math.round((currentX - plistartx)
						/ dpPerDegreeByPli * 10)) / 10;
			} else if (threemode.equals("gailv")) {
				showdegree = (float) (Math.round((currentX - prostartx)
						/ dpPerDegreeByPro * 10)) / 10;
			} else if (threemode.equals("zhiliang")) {
				showdegree = (float) (Math.round((currentX - quastartx)
						/ dpPerDegreeByQua * 10)) / 10;
			}
			if (showdegree < 0) {
				showdegree = 0.0f;
			} else if (showdegree > 360) {
				showdegree = 360.0f;
			}
			tv.setText("示向度" + showdegree + "°");
		} else if (action == MotionEvent.ACTION_UP) {
			tv.setText("");
		}
		return true;
	}

}
