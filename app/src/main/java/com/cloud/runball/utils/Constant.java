package com.cloud.runball.utils;

import com.cloud.runball.BuildConfig;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: Constant
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/17 17:55
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/17 17:55
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class Constant {

  public static final int CHINA_SERVER_TYPE = 0;
  public static final int NATION_SERVER_TYPE = 1;

  /**
   * 默认服务器地址
   */
  static String baseServerUrl = BuildConfig.CHINA_SERVER_URL;
  static String baseSocketServer = BuildConfig.CHINA_SOCKET_URL;
  static int mServerType = CHINA_SERVER_TYPE;

  public static void setServer(int urlType) {
    mServerType = urlType;
    if(mServerType == 0) {
      baseServerUrl = BuildConfig.CHINA_SERVER_URL;
      baseSocketServer = BuildConfig.CHINA_SOCKET_URL;
    } else {
      baseServerUrl = BuildConfig.NATION_SERVER_URL;
      baseSocketServer = BuildConfig.NATION_SOCKET_URL;
    }
  }

  public static int getServerType(){
    return mServerType;
  }

  public static String getBaseUrl(){
    return baseServerUrl;
  }

  public static String getWsUrl(){
    return baseSocketServer;
  }

}
