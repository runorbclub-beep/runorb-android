package com.cloud.runball.module_bluetooth.data;

import com.clj.fastble.data.BleDevice;

public class DeviceBallInfo {

  private BleDevice bleDevice;

  public BleDevice getBleDevice() {
    return bleDevice;
  }

  public void setBleDevice(BleDevice bleDevice) {
    this.bleDevice = bleDevice;
  }
}
