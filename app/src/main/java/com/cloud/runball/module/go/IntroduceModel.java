package com.cloud.runball.module.go;

import com.cloud.runball.model.BasicResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IntroduceModel extends BasicResponse<IntroduceModel> implements Serializable {

  @SerializedName("zh-CN")
  private String zhCN;

  @SerializedName("en-US")
  private String enUS;

  public String getZhCN() {
    return zhCN;
  }

  public void setZhCN(String zhCN) {
    this.zhCN = zhCN;
  }

  public String getEnUS() {
    return enUS;
  }

  public void setEnUS(String enUS) {
    this.enUS = enUS;
  }
}
