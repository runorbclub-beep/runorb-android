package com.cloud.runball.view;


import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: Background
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/14 17:32
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/14 17:32
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class HorseBackground {
    private Bitmap bmp;
    //private Bitmap redBmp;
    //private Bitmap tart_line;
    private int x1, x2;
    private int y1, y2;
    private int speed;
    private int widthScreen, heightScreen;
    private int widthBitmap, heigthtBitmap;


    public HorseBackground(Bitmap bmp,int speed, int widthScreen, int heightScreen) {
        this.bmp = bmp;
        widthBitmap = bmp.getWidth();
        heigthtBitmap = bmp.getHeight();
        x1 = 0;
        y1 = 0;
        x2 = x1+widthBitmap;;
        y2 = 0;
        this.speed = speed;
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;
    }

    public void setSpeed(int speed){
        this.speed=speed;
    }

    public void logic() {

        x1 -=speed;
        x2 -=speed;

        if(x2 <=0){
            x1 = x2 + widthBitmap;
        }

        if(x1 <= 0){
            x2 = x1 + widthBitmap;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bmp, x1, y1, null);
        canvas.drawBitmap(bmp, x2, y2, null);
    }

}
