package com.cloud.runball.module.match_football_association.entity;

import com.google.gson.annotations.SerializedName;

public class MatchStage {

  @SerializedName(value = "matchs_stage_id")
  private String matchStageId;

  @SerializedName(value = "match_stage_title")
  private String matchStageTitle;

  @SerializedName(value = "match_stage_start_time")
  private String matchStageStartTime;

  @SerializedName(value = "match_stage_stop_time")
  private String matchStageStopTime;

  @SerializedName(value = "match_promotion_type")
  private int matchPromotionType;

  @SerializedName(value = "match_promotion_value")
  private int matchPromotionValue;

  @SerializedName(value = "sys_sys_match_id")
  private String sysSysMatchId;

  @SerializedName(value = "match_stage_distance")
  private int matchStageDistance;

  @SerializedName(value = "view_type")
  private int viewType;

  @SerializedName(value = "matchs_stage_status")
  private int matchStageStatus;

  @SerializedName(value = "this_stage")
  private int thisStage;

  @SerializedName(value = "start_time")
  private String startTime;

  @SerializedName(value = "stop_time")
  private String stopTime;

  @SerializedName(value = "match_stage_promotion_rule")
  private String matchStagePromotionRule;

  public String getMatchStageId() {
    return matchStageId;
  }

  public void setMatchStageId(String matchStageId) {
    this.matchStageId = matchStageId;
  }

  public String getMatchStageTitle() {
    return matchStageTitle;
  }

  public void setMatchStageTitle(String matchStageTitle) {
    this.matchStageTitle = matchStageTitle;
  }

  public String getMatchStageStartTime() {
    return matchStageStartTime;
  }

  public void setMatchStageStartTime(String matchStageStartTime) {
    this.matchStageStartTime = matchStageStartTime;
  }

  public String getMatchStageStopTime() {
    return matchStageStopTime;
  }

  public void setMatchStageStopTime(String matchStageStopTime) {
    this.matchStageStopTime = matchStageStopTime;
  }

  public int getMatchPromotionType() {
    return matchPromotionType;
  }

  public void setMatchPromotionType(int matchPromotionType) {
    this.matchPromotionType = matchPromotionType;
  }

  public int getMatchPromotionValue() {
    return matchPromotionValue;
  }

  public void setMatchPromotionValue(int matchPromotionValue) {
    this.matchPromotionValue = matchPromotionValue;
  }

  public String getSysSysMatchId() {
    return sysSysMatchId;
  }

  public void setSysSysMatchId(String sysSysMatchId) {
    this.sysSysMatchId = sysSysMatchId;
  }

  public int getMatchStageDistance() {
    return matchStageDistance;
  }

  public void setMatchStageDistance(int matchStageDistance) {
    this.matchStageDistance = matchStageDistance;
  }

  public int getViewType() {
    return viewType;
  }

  public void setViewType(int viewType) {
    this.viewType = viewType;
  }

  public int getMatchStageStatus() {
    return matchStageStatus;
  }

  public void setMatchStageStatus(int matchStageStatus) {
    this.matchStageStatus = matchStageStatus;
  }

  public int getThisStage() {
    return thisStage;
  }

  public void setThisStage(int thisStage) {
    this.thisStage = thisStage;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getStopTime() {
    return stopTime;
  }

  public void setStopTime(String stopTime) {
    this.stopTime = stopTime;
  }

  public String getMatchStagePromotionRule() {
    return matchStagePromotionRule;
  }

  public void setMatchStagePromotionRule(String matchStagePromotionRule) {
    this.matchStagePromotionRule = matchStagePromotionRule;
  }
}
