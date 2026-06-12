package com.cloud.runball.model;

import com.cloud.runball.bean.MinePlayStatistics;
import com.google.gson.annotations.SerializedName;

public class MinePlayStatisticsModel extends BasicResponse<MinePlayStatisticsModel> {

  @SerializedName("hit_ranking_count")
  private MinePlayStatistics rankingStatistics;

  @SerializedName("pk_count")
  private MinePlayStatistics pkStatistics;

  @SerializedName("shake_count")
  private MinePlayStatistics shakeStatistics;

  @SerializedName("matchs_count")
  private MinePlayStatistics matchStatistics;

  @SerializedName("casually_count")
  private MinePlayStatistics casuallyStatistics;

  public MinePlayStatistics getRankingStatistics() {
    return rankingStatistics;
  }

  public void setRankingStatistics(MinePlayStatistics rankingStatistics) {
    this.rankingStatistics = rankingStatistics;
  }

  public MinePlayStatistics getPkStatistics() {
    return pkStatistics;
  }

  public void setPkStatistics(MinePlayStatistics pkStatistics) {
    this.pkStatistics = pkStatistics;
  }

  public MinePlayStatistics getShakeStatistics() {
    return shakeStatistics;
  }

  public void setShakeStatistics(MinePlayStatistics shakeStatistics) {
    this.shakeStatistics = shakeStatistics;
  }

  public MinePlayStatistics getMatchStatistics() {
    return matchStatistics;
  }

  public void setMatchStatistics(MinePlayStatistics matchStatistics) {
    this.matchStatistics = matchStatistics;
  }

  public MinePlayStatistics getCasuallyStatistics() {
    return casuallyStatistics;
  }

  public void setCasuallyStatistics(MinePlayStatistics casuallyStatistics) {
    this.casuallyStatistics = casuallyStatistics;
  }
}
