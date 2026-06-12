package com.cloud.runball.module.match_football_association.entity;

import com.google.gson.annotations.SerializedName;

public class MatchMenu {

  @SerializedName(value = "sys_match_id")
  private String sysMatchId;

  @SerializedName(value = "match_champion_prize_description")
  private String matchChampionPrizeDescription;

  @SerializedName(value = "match_title")
  private String matchTitle;

  @SerializedName(value = "match_user_type_description")
  private String matchUserTypeDescription;

  @SerializedName(value = "match_user_sex_description")
  private String matchUserSexDescription;

  @SerializedName(value = "match_start_time")
  private String matchStartTime;

  @SerializedName(value = "match_stop_time")
  private String matchStopTime;

  @SerializedName(value = "status")
  private int status;

  @SerializedName(value = "match_user_sign_count")
  private int matchUserSignCount;

  @SerializedName(value = "match_status")
  private int matchStatus;

  @SerializedName(value = "is_group")
  private int isGroup;

  @SerializedName(value = "sys_sys_match_id")
  private String sysSysMatchId;

  @SerializedName(value = "match_image")
  private String matchImage;

  @SerializedName(value = "match_image_list")
  private String matchImageList;

  @SerializedName(value = "join_status")
  private int joinStatus;

  @SerializedName(value = "is_hot")
  private int isHot;

  @SerializedName(value = "matchs_event_type_id")
  private String matchsEventTypeId;

  @SerializedName(value = "start_time")
  private String startTime;

  @SerializedName(value = "stop_time")
  private String stopTime;

  @SerializedName(value = "pass_join")
  private int passJoin;

  @SerializedName(value = "is_quartets")
  private int isQuartets;

  @SerializedName(value = "match_status_title")
  private String matchStatusTitle;

  @SerializedName(value = "user_join_status")
  private UserJoinStatus userJoinStatus;

  @SerializedName(value = "matchs_stage_id")
  private String matchsStageId;

  @SerializedName(value = "view_type")
  private int viewType;

  @SerializedName(value = "team_name")
  private String teamName;

  public String getSysMatchId() {
    return sysMatchId;
  }

  public void setSysMatchId(String sysMatchId) {
    this.sysMatchId = sysMatchId;
  }

  public String getMatchChampionPrizeDescription() {
    return matchChampionPrizeDescription;
  }

  public void setMatchChampionPrizeDescription(String matchChampionPrizeDescription) {
    this.matchChampionPrizeDescription = matchChampionPrizeDescription;
  }

  public String getMatchTitle() {
    return matchTitle;
  }

  public void setMatchTitle(String matchTitle) {
    this.matchTitle = matchTitle;
  }

  public String getMatchUserTypeDescription() {
    return matchUserTypeDescription;
  }

  public void setMatchUserTypeDescription(String matchUserTypeDescription) {
    this.matchUserTypeDescription = matchUserTypeDescription;
  }

  public String getMatchUserSexDescription() {
    return matchUserSexDescription;
  }

  public void setMatchUserSexDescription(String matchUserSexDescription) {
    this.matchUserSexDescription = matchUserSexDescription;
  }

  public String getMatchStartTime() {
    return matchStartTime;
  }

  public void setMatchStartTime(String matchStartTime) {
    this.matchStartTime = matchStartTime;
  }

  public String getMatchStopTime() {
    return matchStopTime;
  }

  public void setMatchStopTime(String matchStopTime) {
    this.matchStopTime = matchStopTime;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getMatchUserSignCount() {
    return matchUserSignCount;
  }

  public void setMatchUserSignCount(int matchUserSignCount) {
    this.matchUserSignCount = matchUserSignCount;
  }

  public int getMatchStatus() {
    return matchStatus;
  }

  public void setMatchStatus(int matchStatus) {
    this.matchStatus = matchStatus;
  }

  public int getIsGroup() {
    return isGroup;
  }

  public void setIsGroup(int isGroup) {
    this.isGroup = isGroup;
  }

  public String getSysSysMatchId() {
    return sysSysMatchId;
  }

  public void setSysSysMatchId(String sysSysMatchId) {
    this.sysSysMatchId = sysSysMatchId;
  }

  public String getMatchImage() {
    return matchImage;
  }

  public void setMatchImage(String matchImage) {
    this.matchImage = matchImage;
  }

  public String getMatchImageList() {
    return matchImageList;
  }

  public void setMatchImageList(String matchImageList) {
    this.matchImageList = matchImageList;
  }

  public int getJoinStatus() {
    return joinStatus;
  }

  public void setJoinStatus(int joinStatus) {
    this.joinStatus = joinStatus;
  }

  public int getIsHot() {
    return isHot;
  }

  public void setIsHot(int isHot) {
    this.isHot = isHot;
  }

  public String getMatchsEventTypeId() {
    return matchsEventTypeId;
  }

  public void setMatchsEventTypeId(String matchsEventTypeId) {
    this.matchsEventTypeId = matchsEventTypeId;
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

  public int getPassJoin() {
    return passJoin;
  }

  public void setPassJoin(int passJoin) {
    this.passJoin = passJoin;
  }

  public int getIsQuartets() {
    return isQuartets;
  }

  public void setIsQuartets(int isQuartets) {
    this.isQuartets = isQuartets;
  }

  public String getMatchStatusTitle() {
    return matchStatusTitle;
  }

  public void setMatchStatusTitle(String matchStatusTitle) {
    this.matchStatusTitle = matchStatusTitle;
  }

  public UserJoinStatus getUserJoinStatus() {
    return userJoinStatus;
  }

  public void setUserJoinStatus(UserJoinStatus userJoinStatus) {
    this.userJoinStatus = userJoinStatus;
  }

  public String getMatchsStageId() {
    return matchsStageId;
  }

  public void setMatchsStageId(String matchsStageId) {
    this.matchsStageId = matchsStageId;
  }

  public int getViewType() {
    return viewType;
  }

  public void setViewType(int viewType) {
    this.viewType = viewType;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }
}
