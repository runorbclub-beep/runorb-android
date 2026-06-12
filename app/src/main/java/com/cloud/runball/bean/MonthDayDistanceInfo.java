package com.cloud.runball.bean;

import com.google.gson.annotations.SerializedName;

public class MonthDayDistanceInfo {

  @SerializedName("distance")
  private float distance;

  @SerializedName("date")
  private String date;

  @SerializedName("unit")
  private String unit;

  public float getDistance() {
    return distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }
}
