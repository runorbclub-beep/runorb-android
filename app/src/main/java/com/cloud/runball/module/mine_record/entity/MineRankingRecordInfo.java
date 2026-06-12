package com.cloud.runball.module.mine_record.entity;

public class MineRankingRecordInfo {

  private int rank;
  private String objectName;
  private String unit;
  private String achievement;
  private String datetime;

  public MineRankingRecordInfo(int rank, String objectName, String unit, String achievement, String datetime) {
    this.rank = rank;
    this.objectName = objectName;
    this.unit = unit;
    this.achievement = achievement;
    this.datetime = datetime;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getAchievement() {
    return achievement;
  }

  public void setAchievement(String achievement) {
    this.achievement = achievement;
  }

  public String getDatetime() {
    return datetime;
  }

  public void setDatetime(String datetime) {
    this.datetime = datetime;
  }
}
