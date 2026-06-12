package com.cloud.runball.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: MoveSurfaceView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/14 17:10
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/14 17:10
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MoveSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder sHolder;
    private Canvas canvas;
    private int widthScreen, heightScreen;
    private Thread thread;
    private boolean flag;

    private Background background;
    private Resources res;
    private int speed = 0;
    private boolean mIsMoving=false;

    public MoveSurfaceView(Context context) {
        super(context);
        sHolder = getHolder();
        sHolder.addCallback(this);
        res = getResources();
    }

    public MoveSurfaceView(Context context, AttributeSet set) {
        super(context, set);
        sHolder = getHolder();
        sHolder.addCallback(this);
        res = getResources();
    }

    public void draw() {
        try {
            canvas = sHolder.lockCanvas();
            //draw something
            background.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                sHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void logic() {
        background.logic();
    }

    @Override
    public void run() {
        while (flag) {
            long start = System.currentTimeMillis();
            //这里处理一些逻辑,比如说red车加速,blue车加速
            //do something...or draw()
            logic();
            draw();
            long end = System.currentTimeMillis();
            long sleep = 50 - end + start;
            try {
                if (sleep > 0) {
                    Thread.sleep(sleep);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        heightScreen = getHeight();
        widthScreen = getWidth();

        //获取背景图片，需要根据屏幕大小缩放
        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.match_main_bg);
        bmp = resizeBitmap(bmp, ScreenWindowManager.widthScreen(getContext()), ScreenWindowManager.heightScreen(getContext()));

        Bitmap redCar = BitmapFactory.decodeResource(res, R.mipmap.match_red_car);
        Bitmap match_start_line = BitmapFactory.decodeResource(res, R.mipmap.match_start_line);
        background = new Background(bmp, redCar, match_start_line, speed, widthScreen, heightScreen);
        thread = new Thread(this);
        thread.start();


        //调整起跑线
        if (background != null) {
            background.startLine(line-match_start_line.getHeight());
        }

        flag = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        flag = false;
    }

    /**
     * 适配手机屏幕的Bitmap对象
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWight = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            //以width作为缩放系数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleWight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return res;
        } else {
            return null;
        }
    }

    int line=0;
    public void initLine(int line){
        this.line=line;
    }

    public void setLineVisible(boolean visible){
        if (background != null) {
            background.setLineVisible(visible);
        }
    }

    public void move() {
        //50毫秒
        mIsMoving=true;
        speed = 22;
        if (background != null) {
            background.setSpeed(speed);
        }
    }

    public void stop() {
        mIsMoving=false;
        speed = 0;
        if (background != null) {
            background.setSpeed(speed);
        }
    }

    public boolean isMoving(){
        return mIsMoving;
    }
}
