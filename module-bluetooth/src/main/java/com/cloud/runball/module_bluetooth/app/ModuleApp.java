package com.cloud.runball.module_bluetooth.app;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.cloud.runball.basecomm.app.IApplication;
import com.cloud.runball.module_bluetooth.service.BleService;

public class ModuleApp implements IApplication {

  @Override
  public void init(@NonNull Application application) {
    BleManager.getInstance().init(application);
    BleManager.getInstance().enableLog(true);
//    Intent intent = new Intent(application, BleService.class);
//    application.startService(intent);
  }

  @Override
  public void lazyInit(@NonNull Application application) {

  }

}
