package com.example.henshin.study.fanqie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OvalTimeView extends SurfaceView implements SurfaceHolder.Callback {

    public OvalTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        holder = this.getHolder();
        holder.addCallback(this);
    }
    public OvalTimeView(Context context) {
        super(context);
        holder = this.getHolder();
        holder.addCallback(this);
    }
    public OvalTimeView(Context context, AttributeSet attrs, int defStyle)  {
        super(context,attrs,defStyle);
        holder = this.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        ispause = false;
    }

    @SuppressLint("WrongCall")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                onDraw();
            }
        }).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        ispause = true;
    }

    private int width;
    private int height;
    private int radius,radius2;
    private int zxX = 0;
    private int zxY = 0;
    public SurfaceHolder holder;
    private boolean ispause = false;
    private float textSixe1,textSixe2;

    public void init(){
        width = getWidth();
        height = getHeight();

        if(width >= height)
            radius = height / 2 - 10;
        else
            radius = width / 2 - 10;

        zxX = width / 2;
        zxY = height / 2;
        radius2 = radius - radius / 20;
        textSixe1 = (float) (radius * 1.0 / 2.5);
        textSixe2 = (float) (radius * 1.0 / 6.3);
    }

    public float angle = 0;
    public boolean istop = true;
    public int sleep = 1000;
    public int longtime = 0;
    public int finaltime = 0;
    public int bgcolor;
    private Handler thandler;

    public void setTHandler(Handler thandler){
        this.thandler = thandler;
    }

    public void setLongtime(int finaltime,int currentlong, int color){
        this.longtime = finaltime - currentlong;
        this.finaltime = finaltime;
        bgcolor = color;
        angle = (float) (angle + (360 * 1.0 / finaltime) * currentlong);
        istop = false;
    }

    public void startDraw(Canvas canvas, SurfaceHolder holder){
        if(!ispause){
            canvas = holder.lockCanvas(null);
            canvas.drawColor(bgcolor);
            drawWOval(canvas);
            drawSOval(canvas, angle);
            drawNOval(canvas);
            drawNText(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }


    public void drawWOval(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawOval(new RectF(zxX - radius, zxY - radius, zxX + radius, zxY + radius), paint);
    }

    public void drawNOval(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(bgcolor);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawOval(new RectF(zxX - radius2, zxY - radius2, zxX + radius2, zxY + radius2), paint);
    }

    public void drawSOval(Canvas canvas, float angle){
        Paint paint = new Paint();
        paint.setColor(bgcolor);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(50);
        paint.setAntiAlias(true);
        canvas.drawArc(new RectF(zxX - radius, zxY - radius, zxX + radius, zxY + radius), 270, angle, true, paint);
    }

    public void drawNText(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(textSixe1);

        String str = CommonUtil.SecondsToTimeString(longtime);
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        int w = rect.width();
        int h = rect.height();
        canvas.drawText(str, zxX - (w / 2), zxY + (h / 2), paint);

        str = AppApplication.getInstances().getWork().getWorkname();
        paint.setTextSize(textSixe2);
        paint.getTextBounds(str, 0, str.length(), rect);
        int ww = rect.width();
        int hw = rect.height();
        canvas.drawText(str, zxX - (ww / 2), zxY - hw * 2, paint);
    }

    public void onDraw(){
        while(true){
            if(!istop){
                Canvas canvas = null;
                synchronized (holder){
                    try {
                        startDraw(canvas , holder);
                        angle = (float) (angle + (360 * 1.0 / finaltime));
                        if(angle >= 360 || longtime < 0){
                            thandler.sendEmptyMessage(1001);
                            break;
                        }
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    longtime --;
                }
            }else{
                Canvas canvas = null;
                startDraw(canvas , holder);
            }
        }
    }
}