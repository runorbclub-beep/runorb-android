package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

public class TodayDistanceSumModel extends BasicResponse<TodayDistanceSumModel> {

  @SerializedName("sum_distance")
  private float sumDistance;

  @SerializedName("unit")
  private String unit;

  public float getSumDistance() {
    return sumDistance;
  }

  public void setSumDistance(float sumDistance) {
    this.sumDistance = sumDistance;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

}
