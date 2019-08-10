package com.huari.ui;

import com.huari.diskactivity.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

/**
 * 刷新时，通过refresh( )输入两个角度值，分别是相对值、绝对值。 可通过setRischecked（ ）设置是显示相对值还是绝对值
 * 可通过diskclear( )清空界面
 * 
 * @author scuwin
 * 
 */
public class Disk extends View {

	private Context context;
	private int width, height;
	private int textleft, texttop;
	private Paint circlepaint, nullpaint, textpaint;
	private Bitmap bm, clearno, greenbar, yellowbar, lightcircle, diskcenter;// 从res解析出来
	private int realdiameter;// 圆的真实直径
	private int diameter;// 圆直径
	private int pointrealwidth, pointrealheight;
	private int pointwidth, pointheight;// 画图时，表针图片的宽和高
	private int diskcenterdiameter, lightcirclediameter;// 画图时盘心圆盘、光晕的直径
	private int frameheight, framewidth;
	private Bitmap kedupan, lv, huang, guangyun, panxin;// 再次转换，转为适用于本控件的Bitmap
	private int w, h, framespace, frametoleft, frametobottom;
	private boolean rischecked;// 分别控制是该显示相对角度还是正北角度、该显示“清零复位”按钮的被点击状态还是正常状态。
	private float relativeangle, absoluteangle;
	private int kedupanx, kedupany;// 刻度盘的左上角坐标
	private int centerx, centery;// 刻度盘的圆心坐标
	private boolean show;// 清零时用到。
	private Paint greenpaint, yellowpaint;// 用来写显示的相对/正北角度
	private float scale;// 各图片的缩放比例
	// private HColumns Hcolumns;
	// private VColumns Vcolumns;
	private int level, quality;
	private boolean hshow;//
	private View popmenu;
	private Button sureButton;
	private RadioGroup rg;
	private int totop;
	private boolean pause = true;

	public void setPause(boolean p) {
		pause = p;
	}

	// /**
	// * 设置与该Disk实例相关联的HColumns实例。在点击“清零复位”按钮时会用到，将与之关联的HColumns实例数据恢复初始化。
	// *
	// * @param hcolumns
	// */
	// public void setHcolumns(HColumns hcolumns) {
	// Hcolumns = hcolumns;
	// Vcolumns = null;
	// hshow=true;
	// }
	//
	// /**
	// * 设置与该Disk实例相关联的VColumns实例。在点击“清零复位”按钮时会用到，将与之关联的VColumns实例数据恢复初始化。
	// */
	// public void setVcolumns(VColumns vcolumns) {
	// Vcolumns = vcolumns;
	// Hcolumns =null;
	// hshow=false;
	// }

	public void setRischecked(boolean rischecked) {
		this.rischecked = rischecked;
	}

	private void initialize() {
		try {
			show = false;
			totop = 24;
			setBackgroundColor(Color.BLACK);
			circlepaint = new Paint();
			circlepaint.setStyle(Style.STROKE);
			greenpaint = new Paint();
			greenpaint.setColor(Color.GREEN);
			greenpaint.setTextSize(18);
			greenpaint.setTextAlign(Align.CENTER);
			yellowpaint = new Paint();
			yellowpaint.setColor(Color.YELLOW);
			yellowpaint.setTextSize(18);
			yellowpaint.setTextAlign(Align.CENTER);
			textpaint = new Paint();
			textpaint.setColor(Color.CYAN);
			textpaint.setTextAlign(Align.CENTER);
			textpaint.setTextSize(20);
			nullpaint = new Paint();
			bm = BitmapFactory.decodeResource(getResources(),
					R.drawable.kedupannew);
			greenbar = BitmapFactory.decodeResource(getResources(),
					R.drawable.lvnew);
			yellowbar = BitmapFactory.decodeResource(getResources(),
					R.drawable.hnew);
			lightcircle = BitmapFactory.decodeResource(getResources(),
					R.drawable.yunnew);
			diskcenter = BitmapFactory.decodeResource(getResources(),
					R.drawable.yuannew);
			realdiameter = bm.getWidth();
			pointrealwidth = greenbar.getWidth();
			pointrealheight = greenbar.getHeight();
			// frametoleft = 3;
			frametobottom = 3;
			// Hcolumns = new HColumns(getContext());
			// Vcolumns = new VColumns(getContext());
		} catch (Exception e) {
			System.out.println("Disk初始化时异常" + e.getStackTrace() + "  "
					+ e.getCause().getClass().getName());
		}

	}

	public Disk(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initialize();
	}

	public Disk(Context context) {
		super(context);
		this.context = context;
		initialize();
	}

	public void refresh(float relativeangle, float absoluteangle) {
		show = true;
		this.relativeangle = relativeangle;
		this.absoluteangle = absoluteangle;
		postInvalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getWidth();
		height = getHeight();
		frametoleft = width / 2;
		diameter = (height - frametobottom - totop) < width ? (height
				- frametobottom - totop) : width;// 为了美观，
		// 将刻度盘与View上边框之间隔开4dp的距离，与位于它下面的按钮行隔开3dp的距离，总7dp。
		scale = (float) diameter / (float) realdiameter;
		diskcenterdiameter = (int) (diskcenter.getWidth() * scale);
		lightcirclediameter = (int) (lightcircle.getWidth() * scale);
		kedupanx = (width - diameter) / 2;
		kedupany = height - frametobottom - diameter;
		centerx = width / 2;
		centery = height - frametobottom - diameter / 2;
		kedupan = Bitmap.createScaledBitmap(bm, diameter, diameter, true);
		guangyun = Bitmap.createScaledBitmap(lightcircle, lightcirclediameter,
				lightcirclediameter, true);
		panxin = Bitmap.createScaledBitmap(diskcenter, diskcenterdiameter,
				diskcenterdiameter, true);
		lv = Bitmap.createScaledBitmap(greenbar,
				(int) (greenbar.getWidth() * scale),
				(int) (greenbar.getHeight() * scale), true);
		huang = Bitmap.createScaledBitmap(yellowbar,
				(int) (yellowbar.getWidth() * scale),
				(int) (yellowbar.getHeight() * scale), true);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	public void onDraw(Canvas canvas) {
		try {
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.drawBitmap(kedupan, kedupanx, kedupany, nullpaint);
			canvas.drawBitmap(guangyun, centerx - lightcirclediameter / 2,
					centery - lightcirclediameter / 2, nullpaint);
			// int levelcon=-20,qualitycon=0;
			// if(hshow)
			// {
			// levelcon=Hcolumns.getEthreshold();
			// qualitycon=Hcolumns.getQthreshold();
			// }
			// else
			// {
			// levelcon=Vcolumns.getEthreshold();
			// qualitycon=Vcolumns.getQthreshold();
			// }
			if (rischecked && show) {
				// if(level>=levelcon&&quality>=qualitycon){
				canvas.save();
				canvas.rotate(relativeangle - 180, centerx, centery);
				canvas.drawBitmap(lv, centerx - lv.getWidth() / 2, centery,
						nullpaint);
				canvas.restore();
				canvas.drawText(relativeangle + "°", frametoleft, 20,
						greenpaint);
				// }
			} else if (rischecked == false && show) {
				// if(level>=levelcon&&quality>=qualitycon){
				canvas.save();
				canvas.rotate(absoluteangle - 180, centerx, centery);
				canvas.drawBitmap(huang, centerx - lv.getWidth() / 2, centery,
						nullpaint);
				canvas.restore();
				canvas.drawText(absoluteangle + "°", frametoleft, 20,
						yellowpaint);
				// }
			}
			;
			canvas.drawBitmap(panxin, centerx - diskcenterdiameter / 2, centery
					- diskcenterdiameter / 2, nullpaint);
		} catch (Exception e) {

		}
	}

	public void diskclear() {
		show = false;
		invalidate();
	}

	public void gc() {
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();
			bm = null;
		}

		if (diskcenter != null && !diskcenter.isRecycled()) {
			diskcenter.recycle();
			diskcenter = null;
		}
		if (lightcircle != null && !lightcircle.isRecycled()) {
			lightcircle.recycle();
			lightcircle = null;
		}
		if (yellowbar != null && !yellowbar.isRecycled()) {
			yellowbar.recycle();
			yellowbar = null;
		}
		if (greenbar != null && !greenbar.isRecycled()) {
			greenbar.recycle();
			greenbar = null;
		}
		if (clearno != null && !clearno.isRecycled()) {
			clearno.recycle();
			clearno = null;
		}
		if (kedupan != null && !kedupan.isRecycled()) {
			kedupan.recycle();
			kedupan = null;
		}
		if (lv != null && !lv.isRecycled()) {
			lv.recycle();
			lv = null;
		}
		if (huang != null && !huang.isRecycled()) {
			huang.recycle();
			huang = null;
		}
		if (guangyun != null && !guangyun.isRecycled()) {
			guangyun.recycle();
			guangyun = null;
		}
		if (panxin != null && !panxin.isRecycled()) {
			panxin.recycle();
			panxin = null;
		}
		System.gc();
	}

}
