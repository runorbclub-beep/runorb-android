package com.cloud.runball.module.match_football_association.entity;

public class MatchPageSettingsInfo {

  private int matchStatus;

  private String matchStatusTitle;

  public MatchPageSettingsInfo(int matchStatus, String matchStatusTitle) {
    this.matchStatus = matchStatus;
    this.matchStatusTitle = matchStatusTitle;
  }

  public int getMatchStatus() {
    return matchStatus;
  }

  public void setMatchStatus(int matchStatus) {
    this.matchStatus = matchStatus;
  }

  public String getMatchStatusTitle() {
    return matchStatusTitle;
  }

  public void setMatchStatusTitle(String matchStatusTitle) {
    this.matchStatusTitle = matchStatusTitle;
  }
}
