package com.example.henshin.study.fanqie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint({ "DrawAllocation", "HandlerLeak" })
public class WaterWave extends View {

	public int width = 0;
	public int height = 0;
	public Paint paint1;
	public Paint paint2;

	public WaterWave(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initPaint();
	}

	public WaterWave(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public int alpha1 = 250;
	public int alpha2 = 250;
	public float radius1 = 100;
	public float radius2 = 100;
	public int jiange = 0;

	public void initPaint(){

		paint1 = new Paint();

		paint1.setAntiAlias(true);
		paint1.setStrokeWidth(100);


		paint1.setStyle(Paint.Style.FILL);

		System.out.println("alpha1=" + alpha1);
		paint1.setAlpha(alpha1);
		System.out.println("得到的透明度：" + paint1.getAlpha());

		paint1.setColor(Color.parseColor("#6495ED"));


		paint2 = new Paint();

		paint2.setAntiAlias(true);
		paint2.setStrokeWidth(100);

		paint2.setStyle(Paint.Style.FILL);

		System.out.println("alpha2=" + alpha2);
		paint2.setAlpha(alpha2);
		System.out.println("得到的透明度：" + paint1.getAlpha());

		paint2.setColor(Color.parseColor("#6495ED"));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void initView(){
		this.width = getWidth();
		this.height = getHeight();
		if(width > height)
			jiange = height / 20 - 10;
		else
			jiange = width / 20 - 10;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		if(width == 0 && height == 0)
			initView();
		canvas.drawOval(new RectF((width / 2 - radius1),
				(height / 2 - radius1), (width / 2 + radius1), (height / 2 + radius1)), paint1);
		if(alpha1 <= 225)
			canvas.drawOval(new RectF((width / 2 - radius2),
					(height / 2 - radius2), (width / 2 + radius2), (height / 2 + radius2)), paint2);
		handler.sendEmptyMessageDelayed(0, 300);
	}

	final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			try {
				refreshView();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.handleMessage(msg);
		}
	};


	public void refreshView() throws InterruptedException {
		if(alpha1 <= 225){
			alpha2 = alpha2 - 25;
			radius2 = radius2 + jiange;
		}
		if(alpha2 < 0){
			alpha2 = 250;
			alpha1 = 250;
			radius2 = radius1 = 100;
			Thread.sleep(1000);
		}
		if(alpha1 > 0){
			alpha1 = alpha1 - 25;
			radius1 += jiange;
		}
		paint1.setAlpha(alpha1);
		paint2.setAlpha(alpha2);
		paint1.setStrokeWidth(radius1);
		paint2.setStrokeWidth(radius2);
		invalidate();
	}
}
