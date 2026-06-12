package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

public class UserPkWinRateModel extends BasicResponse<UserPkWinRateModel> {

  // 总
  @SerializedName("total")
  private int total;

  // 胜
  @SerializedName("victory")
  private int victory;

  // 负
  @SerializedName("burden")
  private int burden;

  //胜率
  @SerializedName("win_rate")
  private float winRate;

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getVictory() {
    return victory;
  }

  public void setVictory(int victory) {
    this.victory = victory;
  }

  public int getBurden() {
    return burden;
  }

  public void setBurden(int burden) {
    this.burden = burden;
  }

  public float getWinRate() {
    return winRate;
  }

  public void setWinRate(float winRate) {
    this.winRate = winRate;
  }
}
