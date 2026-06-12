package com.cloud.runball.utils;

import android.util.Log;

import com.cloud.runball.BuildConfig;


/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: AppLogger
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/3/23 13:26
 * @UpdateUser: zhd
 * @UpdateDate: 2021/3/23 13:26
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AppLogger {

    static boolean DEBUG= BuildConfig.DEBUG;
    static final String TAG="PRETTY_LOGGER";
    public static void d(String content){
        d(TAG,content);
    }

    public static void e(String content){
        e(TAG,content);
    }

    public static void i(String content){
        i(TAG,content);
    }

    static void d(String tag,String content){
        if(DEBUG){
            Log.d(tag,content);
        }
    }

    static void e(String tag,String content){
        if(DEBUG){
            Log.e(tag,content);
        }
    }


    public static void i(String tag,String content){
        if(DEBUG){
            Log.i(tag,content);
        }
    }





}
