package com.cloud.runball.utils;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.cloud.runball.App;

import java.util.Locale;


/**
 * 资源工具类
 * @author ns467
 */
public final class ResourceUtils {

    private ResourceUtils() {

    }

    /**
     * 获取字符串资源
     *
     * @param resId 字符串资源ID
     * @return 字符串
     */
    public static String getString(@StringRes int resId) {
        return App.self().getString(resId);
    }

    /**
     * 获取颜色资源
     *
     * @param resId 颜色资源ID
     * @return 颜色
     */
    public static int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(App.self(), resId);
    }



    public static boolean isZhCn(Context context){
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.startsWith("zh")){
            return true;
        }
        return false;
    }
}
