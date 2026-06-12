package com.cloud.runball.module.match_football_association.entity.model;

import com.cloud.runball.model.BasicResponse;
import com.cloud.runball.module.match_football_association.entity.MatchDetailInfoItem;
import com.cloud.runball.module.match_football_association.entity.MatchStage;
import com.cloud.runball.module.match_football_association.entity.UserJoinStatus;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatchDetailModel extends BasicResponse<MatchDetailModel> {

  @SerializedName(value = "sys_sys_match_id")
  private String sysSysMatchId;

  @SerializedName(value = "sys_match_id")
  private String sysMatchId;

  @SerializedName(value = "matchs_event_type_id")
  private String matchEventTypeId;

  @SerializedName(value = "match_start_time")
  private long matchStartTime;

  @SerializedName(value = "match_stop_time")
  private long matchStopTime;

  @SerializedName(value = "is_quartets")
  private int isQuartets;

  @SerializedName(value = "match_title")
  private String matchTitle;

  @SerializedName(value = "match_status")
  private int matchStatus;

  @SerializedName(value = "match_status_title")
  private String matchStatusTitle;

  @SerializedName(value = "user_join_status")
  private UserJoinStatus userJoinStatus;

  @SerializedName(value = "match_join_pass")
  private int matchJoinPass;

  @SerializedName(value = "is_group")
  private int isGroup;

  @SerializedName(value = "match_image")
  private String matchImage;

  @SerializedName(value = "form_array")
  private List<MatchDetailInfoItem> matchDetailInfoList;

  @SerializedName(value = "matchs_stage_id")
  private String matchStageId;

  @SerializedName(value = "view_type")
  private int viewType;

  @SerializedName(value = "stage")
  private List<MatchStage> matchStageList;

  @SerializedName(value = "join_status")
  private int joinStatus;

  @SerializedName(value = "team_name")
  private String teamName;

  @SerializedName(value = "quartets_icon")
  private String quartetsIcon;

  @SerializedName(value = "team_info")
  private TeamInfo teamInfo;

  public String getSysSysMatchId() {
    return sysSysMatchId;
  }

  public void setSysSysMatchId(String sysSysMatchId) {
    this.sysSysMatchId = sysSysMatchId;
  }

  public String getSysMatchId() {
    return sysMatchId;
  }

  public void setSysMatchId(String sysMatchId) {
    this.sysMatchId = sysMatchId;
  }

  public String getMatchEventTypeId() {
    return matchEventTypeId;
  }

  public void setMatchEventTypeId(String matchEventTypeId) {
    this.matchEventTypeId = matchEventTypeId;
  }

  public long getMatchStartTime() {
    return matchStartTime;
  }

  public void setMatchStartTime(long matchStartTime) {
    this.matchStartTime = matchStartTime;
  }

  public long getMatchStopTime() {
    return matchStopTime;
  }

  public void setMatchStopTime(long matchStopTime) {
    this.matchStopTime = matchStopTime;
  }

  public int getIsQuartets() {
    return isQuartets;
  }

  public void setIsQuartets(int isQuartets) {
    this.isQuartets = isQuartets;
  }

  public String getMatchTitle() {
    return matchTitle;
  }

  public void setMatchTitle(String matchTitle) {
    this.matchTitle = matchTitle;
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

  public UserJoinStatus getUserJoinStatus() {
    return userJoinStatus;
  }

  public void setUserJoinStatus(UserJoinStatus userJoinStatus) {
    this.userJoinStatus = userJoinStatus;
  }

  public int getMatchJoinPass() {
    return matchJoinPass;
  }

  public void setMatchJoinPass(int matchJoinPass) {
    this.matchJoinPass = matchJoinPass;
  }

  public int getIsGroup() {
    return isGroup;
  }

  public void setIsGroup(int isGroup) {
    this.isGroup = isGroup;
  }

  public String getMatchImage() {
    return matchImage;
  }

  public void setMatchImage(String matchImage) {
    this.matchImage = matchImage;
  }

  public List<MatchDetailInfoItem> getMatchDetailInfoList() {
    return matchDetailInfoList;
  }

  public void setMatchDetailInfoList(List<MatchDetailInfoItem> matchDetailInfoList) {
    this.matchDetailInfoList = matchDetailInfoList;
  }

  public String getMatchStageId() {
    return matchStageId;
  }

  public void setMatchStageId(String matchStageId) {
    this.matchStageId = matchStageId;
  }

  public int getViewType() {
    return viewType;
  }

  public void setViewType(int viewType) {
    this.viewType = viewType;
  }

  public List<MatchStage> getMatchStageList() {
    return matchStageList;
  }

  public void setMatchStageList(List<MatchStage> matchStageList) {
    this.matchStageList = matchStageList;
  }

  public int getJoinStatus() {
    return joinStatus;
  }

  public void setJoinStatus(int joinStatus) {
    this.joinStatus = joinStatus;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public String getQuartetsIcon() {
    return quartetsIcon;
  }

  public void setQuartetsIcon(String quartetsIcon) {
    this.quartetsIcon = quartetsIcon;
  }

  public TeamInfo getTeamInfo() {
    return teamInfo;
  }

  public void setTeamInfo(TeamInfo teamInfo) {
    this.teamInfo = teamInfo;
  }

  public static class TeamInfo {

    @SerializedName(value = "match_user_id")
    private String matchUserId;

    @SerializedName(value = "team_tag")
    private String teamTag;

    @SerializedName(value = "join_sum")
    private int joinSum;

    public String getMatchUserId() {
      return matchUserId;
    }

    public void setMatchUserId(String matchUserId) {
      this.matchUserId = matchUserId;
    }

    public String getTeamTag() {
      return teamTag;
    }

    public void setTeamTag(String teamTag) {
      this.teamTag = teamTag;
    }

    public int getJoinSum() {
      return joinSum;
    }

    public void setJoinSum(int joinSum) {
      this.joinSum = joinSum;
    }
  }

}
