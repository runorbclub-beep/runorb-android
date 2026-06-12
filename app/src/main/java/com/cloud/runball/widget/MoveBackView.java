package com.cloud.runball.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.AppUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: MoveBackView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/9 10:33
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/9 10:33
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MoveBackView extends View {
    //背景图的实际高度
    final int BACK_HEIGHT = 7200;
    //背景图片Bitmap
    private Bitmap back;

    private Bitmap redCar;
    private Bitmap blueCar;

    //定义图片的宽高
    int WIDTH = 1125;  //640
    int HEIGHT = 680; //880
    private Matrix matrix = new Matrix();
    private int startY = BACK_HEIGHT - HEIGHT;
    private int screenHeight=0;
    private int screenWidth=0;

    public MoveBackView(Context context, AttributeSet set){
        super(context, set);
        back = BitmapFactory.decodeResource(context.getResources(), R.drawable.match_main_bg);
        WIDTH=back.getWidth();
        //HEIGHT=back.getHeight();

        //获取窗口管理器
        //WindowManager windowManager = context.getWindowManager();
        //Display display = windowManager.getDefaultDisplay();
        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //display.getMetrics(displayMetrics);

        //获得屏幕宽度
        float screenWidth = AppUtils.widthPixels(context);

        screenWidth= AppUtils.widthPixels(context);
        screenHeight=AppUtils.heightPixels(context);

        //获得图片缩放比例
        float scaleX = screenWidth / WIDTH;
        float scaleY = screenHeight / HEIGHT;
        matrix.setScale(scaleX, scaleY);

        redCar = BitmapFactory.decodeResource(context.getResources(), R.mipmap.match_red_car);
        blueCar = BitmapFactory.decodeResource(context.getResources(), R.mipmap.match_blue_car);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123)
                {
                    if (startY <=  3)
                    {
                        startY = BACK_HEIGHT - HEIGHT;
                    }
                    else
                    {
                        startY -= 3;
                    }

                }
                invalidate();
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap2 = Bitmap.createBitmap(back, 0, startY, WIDTH, HEIGHT,matrix, false);
        //绘制新位图
        canvas.drawBitmap(bitmap2, 0, 0, null);

        //绘制汽车
        canvas.drawBitmap(redCar, 120, screenHeight-200, null);
        canvas.drawBitmap(blueCar, 420, screenHeight-200, null);

    }
}
