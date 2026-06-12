package com.cloud.runball.utils;

import android.content.Context;
import android.text.TextUtils;

import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.DeviceNicknameData;
import com.cloud.runball.model.DeviceWithServerModel;

import java.util.List;

/**
 * date: 2021/8/20
 * author: hwl
 * description:
 */
public class DeviceUtils {

  public static String getDeviceNickname(Context context, String name) {
    if (TextUtils.isEmpty(name)) {
      return "";
    }
    String result = name;
    List<DeviceWithServerModel> deviceList = SPUtils.getData(context, "bleDeviceList", DeviceWithServerModel.class);
    for (int i = 0; i < deviceList.size(); i++) {
      DeviceWithServerModel item = deviceList.get(i);
      if (name.equals(item.getDevice_name())) {
        if (TextUtils.isEmpty(item.getName())) {
          return result;
        } else {
          result = item.getName();
        }
      }
    }
    return result;
  }

}
