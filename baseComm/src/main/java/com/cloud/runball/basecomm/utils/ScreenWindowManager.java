package com.cloud.runball.basecomm.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: ScreenWindowManager
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/8 15:55
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/8 15:55
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ScreenWindowManager {


    public static int widthScreen(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return screenWidth;
    }

    public static int heightScreen(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

}
