package com.cloud.runball.model;

import com.cloud.runball.module.mine_record.entity.MinePlayDataInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinePlayDataInfoModel extends BasicResponse<MinePlayDataInfoModel> {

  @SerializedName("odometer_sum")
  private OdometerSum odometerSum;

  @SerializedName("list")
  private List<MinePlayDataInfo> minePlayDataInfo;

  public OdometerSum getOdometerSum() {
    return odometerSum;
  }

  public void setOdometerSum(OdometerSum odometerSum) {
    this.odometerSum = odometerSum;
  }

  public List<MinePlayDataInfo> getMinePlayDataInfo() {
    return minePlayDataInfo;
  }

  public void setMinePlayDataInfo(List<MinePlayDataInfo> minePlayDataInfo) {
    this.minePlayDataInfo = minePlayDataInfo;
  }

  public class OdometerSum {
    @SerializedName("distance_sum")
    private String distanceSum;

    @SerializedName("unit")
    private String unit;

    public String getDistanceSum() {
      return distanceSum;
    }

    public void setDistanceSum(String distanceSum) {
      this.distanceSum = distanceSum;
    }

    public String getUnit() {
      return unit;
    }

    public void setUnit(String unit) {
      this.unit = unit;
    }
  }

}
