package com.cloud.runball.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.cloud.runball.R;

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
public class Background {
    private Bitmap bmp;
    private Bitmap redBmp;
    private Bitmap tart_line;
    private int x1, x2;
    private int y1, y2;
    private int speed;
    private int widthScreen, heightScreen;
    private int widthBitmap, heigthtBitmap;
    //private Matrix matrix = new Matrix();

    int offsetLeft_red=0;
    int car_width=0;
    int car_height=0;

    //汽车最大可移动距离
    int maxHeightDistance=0;

    public Background(Bitmap bmp, Bitmap red,Bitmap tart_line,int speed, int widthScreen, int heightScreen) {
        this.bmp = bmp;
        this.redBmp=red;
        this.tart_line=tart_line;
        widthBitmap = bmp.getWidth();
        heigthtBitmap = bmp.getHeight();
        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = y1+heigthtBitmap;
        this.speed = speed;
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;

        offsetLeft_red=(widthScreen/2-redBmp.getWidth())/2;
        car_width=redBmp.getWidth();
        car_height=redBmp.getHeight();
        maxHeightDistance=this.heightScreen-car_height;
    }

    public void setSpeed(int speed){
        this.speed=speed;
    }

    /**
     * 贴图逻辑
     * y1初始从坐标0 开始贴图, 慢慢往下移动, y2则补齐y1 往下移动留下的空白.
     * 当y1 移除到界面时,y1 此时应该从y2 位置的开始不能从0开始, y2继续补齐y1 留下的空白
     */
    public void logic() {
        //	y1 = y1 > heightScreen ? 0 : y1+speed;
        y1 = y1 > heightScreen ? y2 : y1+speed;
        y2 = y1- heigthtBitmap;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bmp, x1, y1, null);
        canvas.drawBitmap(bmp, x2, y2, null);
        //或者不通过这个绘制方式，而是通过播放动画方式
        //绘制开始线条
        if(isVisible){
            canvas.drawBitmap(tart_line,0,line+y1,null);
            if(line+y1>heightScreen+10){
                isVisible=false;
            }
        }
    }

    int line=0;
    public void startLine(int line){
       this.line=line;
    }

    private boolean isVisible=true;
    public void setLineVisible(boolean visible){
        this.isVisible=visible;
    }

    /**
     * 获得需要位移偏移量
     * @param rpm
     * @return
     */
    private int getPosOffsetY(int rpm){
        if(rpm>=0 && rpm<=3000){
            //占比6%
            return (int)(maxHeightDistance*0.06);
        }else if(rpm>=3001 && rpm<=5000){
            //30%
            return (int)(maxHeightDistance*0.3);
        }else if(rpm>=5001 && rpm<=8000){
            //40%
            return (int)(maxHeightDistance*0.4);
        }else if(rpm>=8001 && rpm<=10000){
            //14%
            return (int)(maxHeightDistance*0.14);
        }else if(rpm>=10001 && rpm<=21000){
            //10%
            return (int)(maxHeightDistance*0.10);
        }
        return 0;
    }

}
