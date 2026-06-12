package com.cloud.runball.module_bluetooth.utils;

import android.bluetooth.BluetoothDevice;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.List;

public class BleUtils {

  /**
   * 是否连接设备
   * @return 是否
   */
  public static boolean isConnectedDevice() {
    List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
    return deviceList != null && deviceList.size() > 0;
  }

  /**
   * 获取连接的设备
   * @return
   */
  public static BluetoothDevice getConnectedDevice() {
    List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
    if (deviceList == null || deviceList.size() == 0) {
      return null;
    }
    return deviceList.get(0).getDevice();
  }

  /**
   * 设备是否连接中
   * @param mac
   * @return
   */
  public static boolean isConnectedDevice(String mac) {
    return BleManager.getInstance().isConnected(mac);
  }

  /**
   * 手机是否支持BLE
   * @return
   */
  public static boolean isSupportBle() {
    return BleManager.getInstance().isSupportBle();
  }

  /**
   * 手机的蓝牙是否已经打开
   * @return
   */
  public static boolean isBlueEnable() {
    return BleManager.getInstance().isBlueEnable();
  }

}
