package com.cloud.runball.utils;

import com.cloud.runball.model.AppDataManager;

public class AccountUtil {

  public static boolean isUserAccount() {
    return AppDataManager.getInstance().getUserInfoModel() != null
        && !"游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name());
  }

}
