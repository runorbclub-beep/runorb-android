package com.cloud.runball.utils;

import java.text.DecimalFormat;

public class BallUtils {

    /**
     * 根据转速转化为百分比
     * @param rpm 转速
     * @return 百分比
     */
    public static float getPercentWithSpeedRPM(int rpm) {
        float percent;
        if (rpm <= 8000) {
            percent = (float) (rpm * 50.0 / 8000.0f);
            if (percent < 0.1) {
                percent = 0;
            }
        } else {
            percent = 50.0f + (12.5f * (rpm - 8000) / 3000.0f);
        }
        if (percent >= 100) {
            percent = 100.0f;
        }
        return percent;
    }

    /**
     * 根据转速转化为角度
     * 其中0-2-4-6-8是 30度每一刻
     * 8-11-14-17-21 是也是30度每一刻
     * @param rpm 转速
     * @return 角度
     */
    public static float getAngleWithSpeedRPM(int rpm) {
        float angle;
        if (rpm <= 8000) {
            angle = (float) (rpm * 30.0 / 2000.0f);
        } else {
            angle = 120.0f + (float) ((rpm - 8000) * 30.0 / 3000.0f);
        }
        return angle;
    }

    /**
     * 计算圈数的总周长长度
     * @param circle 圈数
     * @return 总周长长度,单位 m
     */
    public static float getTotalMeter(int circle) {
        //直径为5.28cm，周长16.588cm
        return circle * 16.588f / 100;
    }

    public static String formatDistance(double distance){
        DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
        if(distance<1000){
            return mDecimalFormat.format(distance)+"km";
        }else if(distance>=1000 && distance < 10000){
            return mDecimalFormat.format(distance/1000.0)+"k km";
        }else{
            return mDecimalFormat.format(distance/10000.0)+"w km";
        }
    }

    public static String formatDistanceNoKM(double distance){
        DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
        if(distance<1000){
            return mDecimalFormat.format(distance);
        }else if(distance>=1000 && distance < 10000){
            return mDecimalFormat.format(distance/1000.0)+"k";
        }else{
            return mDecimalFormat.format(distance/10000.0)+"w";
        }
    }

    /**
     * 一趟马拉松需要多少圈
     * @return 圈数
     */
    public static int marathonCircle() {
        //42.195km=42195
        double maxCircle = 42.195 * 1000 * 100 / 16.588;
        return (int) maxCircle;
    }

    /**
     * 跑多少米需要多少圈
     * @param meter
     * @return
     */
    public int getCircleWithMeter(int meter) {
        double maxCircle = meter * 100 / 16.588;
        return (int) maxCircle;
    }

}
