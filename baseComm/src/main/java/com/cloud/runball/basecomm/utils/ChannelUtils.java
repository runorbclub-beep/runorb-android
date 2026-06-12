package com.cloud.runball.basecomm.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * date: 2021/8/10
 * author: hwl
 * description:
 */
public class ChannelUtils {

  /**
   * 获取渠道名字
   * @return
   */
  public static String getChannelName(Context context) {
    try {
      PackageManager pm = context.getPackageManager();
      ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      // key为<meta-data>标签中的name
      String channel = appInfo.metaData.getString("UMENG_CHANNEL");
      if (!TextUtils.isEmpty(channel)) {
        return channel;
      }
    } catch (Exception e) {

    }
    return "";
  }

}
