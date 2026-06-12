package com.cloud.runball.module.match_football_association.entity;

import com.google.gson.annotations.SerializedName;

public class HotInfo {

  @SerializedName(value = "sys_match_id")
  private String sysMatchId;

  @SerializedName(value = "sys_sys_match_id")
  private String sysSysMatchId;

  @SerializedName(value = "match_title")
  private String matchTitle;

  public String getSysMatchId() {
    return sysMatchId;
  }

  public void setSysMatchId(String sysMatchId) {
    this.sysMatchId = sysMatchId;
  }

  public String getSysSysMatchId() {
    return sysSysMatchId;
  }

  public void setSysSysMatchId(String sysSysMatchId) {
    this.sysSysMatchId = sysSysMatchId;
  }

  public String getMatchTitle() {
    return matchTitle;
  }

  public void setMatchTitle(String matchTitle) {
    this.matchTitle = matchTitle;
  }

}
