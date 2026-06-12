package com.cloud.runball;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cloud.runball.basecomm.app.BaseApplication;
import com.cloud.runball.basecomm.app.IApplication;
import com.cloud.runball.basecomm.utils.PermissionWallUtils;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.BlePackData;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.UpLoadInfoModel;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.BleByteHelper;
import com.cloud.runball.utils.Constant;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ns467
 */
public class App extends BaseApplication implements Application.ActivityLifecycleCallbacks {

  static App mApp;

  public static App self() {
    return mApp;
  }

  @Override
  public void init(Application application) {
    mApp = this;

    String curProcessName = null;
    int pid = android.os.Process.myPid();
    ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo appProcessInfo: activityManager.getRunningAppProcesses()) {
      if (appProcessInfo.pid == pid) {
        curProcessName = appProcessInfo.processName;
      }
    }

    // 判断进程id是否与application的进程id相同，防止因sdk的进程生成导致重复执行
    if (getApplicationInfo().packageName.equals(curProcessName)) {
      //切换url
      String channelName = BuildConfig.FLAVOR;
      Integer serverType = (Integer) SPUtils.get(this, "server",
          channelName.equalsIgnoreCase("googleplay") ? Constant.NATION_SERVER_TYPE : Constant.CHINA_SERVER_TYPE);
      if(serverType.intValue() == Constant.CHINA_SERVER_TYPE) {
        Constant.setServer(Constant.CHINA_SERVER_TYPE);
      }else{
        Constant.setServer(Constant.NATION_SERVER_TYPE);
      }

      registerActivityLifecycleCallbacks(this);

      //初始化ARouter
      ARouter.init(this);

      //初始化日志框架
      Logger.addLogAdapter(new AndroidLogAdapter() {
        @Override
        public boolean isLoggable(int priority, String tag) {
          return BuildConfig.DEBUG;
        }
      });

      modulesApplicationInitOk();
      if(!"googleplay".equals(BuildConfig.FLAVOR)){
        modulesApplicationInit();
      }

      PermissionWallUtils.init(this);

//            //bugly
//            CrashReport.initCrashReport(this, "39a4395f96", false);
//
//            //添加内存检测
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
    }
  }

  @Override
  public void lazyInit(Application application) {
    for (String moduleImpl : MODULES_LIST){
      try {
        Class<?> clazz = Class.forName(moduleImpl);
        Object obj = clazz.newInstance();
        if (obj instanceof IApplication){
          ((IApplication) obj).lazyInit(this);
        }
      } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void attachBaseContext(Context context) {
    super.attachBaseContext(context);
    MultiDex.install(this);
  }

  /**
   * 清除缓存数据
   * 这里应该这样设计，清除所有模块内的缓存数据(每个模块都提供一个接口来被反射处理)
   */
  public void clearCacheData(){
    try{
      AppDataManager.getInstance().setUserInfoModel(null);
      AppDataManager.getInstance().setPlayOverModel(null);
      //AppDataManager.getInstance().clearRankMatch();
      SPUtils.remove(getApplicationContext(), "token");
      SPUtils.remove(getApplicationContext(),"pkdata");
      SPUtils.remove(getApplicationContext(),"pkdata_startTime");
      SPUtils.remove(getApplicationContext(),"pkdata_keepPlayTime");
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

  // 各需初始化的模块位置 - 根据打包渠道未必需要的
  private static final String[] MODULES_LIST = {
      "com.cloud.runball.share.ShareModule"
  };

  // 各需初始化的模块位置 - 必定初始化的
  private static final String[] MODULES_LIST_OK = {
      "com.cloud.runball.module_bluetooth.app.ModuleApp"
  };

  private void modulesApplicationInitOk() {
    for (String moduleImpl : MODULES_LIST_OK) {
      try {
        Class<?> clazz = Class.forName(moduleImpl);
        Object obj = clazz.newInstance();
        if (obj instanceof IApplication) {
          ((IApplication) obj).init(this);
        }
      } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 通过反射加载模块初始化
   */
  private void modulesApplicationInit() {
    for (String moduleImpl : MODULES_LIST) {
      try {
        Class<?> clazz = Class.forName(moduleImpl);
        Object obj = clazz.newInstance();
        if (obj instanceof IApplication) {
          ((IApplication) obj).init(this);
        }
      } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
        e.printStackTrace();
      }
    }
  }

  //todo
  int init_circle_count=28;

  public void setCircleCount(int circleCount){
    this.init_circle_count = circleCount;
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
  }

  private static int actionCount = 0;

  @Override
  public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

  }

  @Override
  public void onActivityStarted(@NonNull Activity activity) {
    int temp = actionCount;
    actionCount++;
    if (temp == 0 && actionCount > 0) {
      EventBus.getDefault().post(new MessageEvent(MessageEvent.STATE_APP_TO_FOREGROUND));
//      modulesApplicationInitOk();
    }
  }

  @Override
  public void onActivityResumed(@NonNull Activity activity) {

  }

  @Override
  public void onActivityPaused(@NonNull Activity activity) {

  }

  @Override
  public void onActivityStopped(@NonNull Activity activity) {
    actionCount--;
    if (actionCount <= 0) {
      EventBus.getDefault().post(new MessageEvent(MessageEvent.STATE_APP_TO_BACKSTAGE));
    }
  }

  @Override
  public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

  }

  @Override
  public void onActivityDestroyed(@NonNull Activity activity) {

  }

//    private UpLoadInfoModel upLoadInfoModel = null;
//
//    public UpLoadInfoModel getUpLoadInfoModel() {
//        return upLoadInfoModel;
//    }
//
//    public void setUpLoadInfoModel(UpLoadInfoModel upLoadInfoModel) {
//        this.upLoadInfoModel = upLoadInfoModel;
//    }
}
