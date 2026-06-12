package com.cloud.runball.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.bean
 * @ClassName: MatchRankInfoData
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/22 18:08
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/22 18:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRankInfoData implements Serializable {

  /**
   * match_champion_prize_description :
   * sys_match_id : 48988132351152128
   * match_title : 大学生长征挑战赛
   * match_start_time : 1621428461
   * match_stop_time : 1621741520
   * match_image : http://10.20.73.103:89/matchs_image/2021/05/2021-05-20/competition.png
   * match_status : 2
   * match_user_sign_count : 3
   * is_group : 1
   * start_time : 2021-05-19 20:47:41
   * stop_time : 2021-05-23 11:45:20
   * match_status_title : 比赛中
   */

  @SerializedName("match_champion_prize_description")
  private String matchChampionPrizeDescription;

  @SerializedName("sys_match_id")
  private String sysMatchId;

  @SerializedName("match_title")
  private String matchTitle;

  @SerializedName("match_start_time")
  private String matchStartTime;

  @SerializedName("match_stop_time")
  private String matchStopTime;

  @SerializedName("match_image")
  private String matchImage;

  @SerializedName("match_status")
  private int matchStatus;

  @SerializedName("match_user_sign_count")
  private int matchUserSignCount;

  @SerializedName("is_group")
  private int isGroup;

  @SerializedName("start_time")
  private String startTime;

  @SerializedName("stop_time")
  private String stopTime;

  @SerializedName("match_status_title")
  private String matchStatusTitle;

  public String getMatchChampionPrizeDescription() {
    return matchChampionPrizeDescription;
  }

  public void setMatchChampionPrizeDescription(String matchChampionPrizeDescription) {
    this.matchChampionPrizeDescription = matchChampionPrizeDescription;
  }

  public String getSysMatchId() {
    return sysMatchId;
  }

  public void setSysMatchId(String sysMatchId) {
    this.sysMatchId = sysMatchId;
  }

  public String getMatchTitle() {
    return matchTitle;
  }

  public void setMatchTitle(String matchTitle) {
    this.matchTitle = matchTitle;
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

  public String getMatchImage() {
    return matchImage;
  }

  public void setMatchImage(String matchImage) {
    this.matchImage = matchImage;
  }

  public int getMatchStatus() {
    return matchStatus;
  }

  public void setMatchStatus(int matchStatus) {
    this.matchStatus = matchStatus;
  }

  public int getMatchUserSignCount() {
    return matchUserSignCount;
  }

  public void setMatchUserSignCount(int matchUserSignCount) {
    this.matchUserSignCount = matchUserSignCount;
  }

  public int getIsGroup() {
    return isGroup;
  }

  public void setIsGroup(int isGroup) {
    this.isGroup = isGroup;
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

  public String getMatchStatusTitle() {
    return matchStatusTitle;
  }

  public void setMatchStatusTitle(String matchStatusTitle) {
    this.matchStatusTitle = matchStatusTitle;
  }
}
