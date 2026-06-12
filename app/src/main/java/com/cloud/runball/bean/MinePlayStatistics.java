package com.cloud.runball.bean;

import com.google.gson.annotations.SerializedName;

public class MinePlayStatistics {

  @SerializedName("type")
  private int type;

  @SerializedName("count")
  private int count;

  @SerializedName("unit")
  private String unit;

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }
}
