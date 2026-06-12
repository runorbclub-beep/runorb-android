package com.cloud.runball.utils;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {

    public static boolean isNavigationBarShow(WindowManager windowManager) {
        Display defaultDisplay = windowManager.getDefaultDisplay();
        //获取屏幕高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;
        //宽度
        int widthPixels = outMetrics.widthPixels;


        //获取内容高度
        DisplayMetrics outMetrics2 = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics2);
        int heightPixels2 = outMetrics2.heightPixels;
        //宽度
        int widthPixels2 = outMetrics2.widthPixels;

        return heightPixels - heightPixels2 > 0 || widthPixels - widthPixels2 > 0;
    }

}
