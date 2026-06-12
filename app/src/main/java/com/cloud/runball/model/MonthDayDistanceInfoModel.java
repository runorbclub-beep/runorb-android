package com.cloud.runball.model;

import com.cloud.runball.bean.MonthDayDistanceInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MonthDayDistanceInfoModel extends BasicResponse<MonthDayDistanceInfoModel> {

  @SerializedName("target_distance")
  private String targetDistance;

  @SerializedName("sum_month")
  private List<MonthDayDistanceInfo> sumMonth;

  public String getTargetDistance() {
    return targetDistance;
  }

  public void setTargetDistance(String targetDistance) {
    this.targetDistance = targetDistance;
  }

  public List<MonthDayDistanceInfo> getSumMonth() {
    return sumMonth;
  }

  public void setSumMonth(List<MonthDayDistanceInfo> sumMonth) {
    this.sumMonth = sumMonth;
  }
}
