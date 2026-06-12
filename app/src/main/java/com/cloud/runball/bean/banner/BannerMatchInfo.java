package com.cloud.runball.bean.banner;

import com.google.gson.annotations.SerializedName;

public class BannerMatchInfo {

  @SerializedName("sys_match_id")
  private String sysMatchId;

  @SerializedName("is_quartets")
  private int isQuartets;

  public String getSysMatchId() {
    return sysMatchId;
  }

  public void setSysMatchId(String sysMatchId) {
    this.sysMatchId = sysMatchId;
  }

  public int getIsQuartets() {
    return isQuartets;
  }

  public void setIsQuartets(int isQuartets) {
    this.isQuartets = isQuartets;
  }
}
