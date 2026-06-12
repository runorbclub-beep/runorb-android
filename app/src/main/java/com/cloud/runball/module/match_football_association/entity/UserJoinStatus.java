package com.cloud.runball.module.match_football_association.entity;

import com.google.gson.annotations.SerializedName;

public class UserJoinStatus {

  @SerializedName(value = "is_join")
  private int isJoin;

  @SerializedName(value = "user_group_id")
  private String userGroupId;

  @SerializedName(value = "group_title")
  private String groupTitle;

  @SerializedName(value = "group_num")
  private String groupNum;

  public int getIsJoin() {
    return isJoin;
  }

  public void setIsJoin(int isJoin) {
    this.isJoin = isJoin;
  }

  public String getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(String userGroupId) {
    this.userGroupId = userGroupId;
  }

  public String getGroupTitle() {
    return groupTitle;
  }

  public void setGroupTitle(String groupTitle) {
    this.groupTitle = groupTitle;
  }

  public String getGroupNum() {
    return groupNum;
  }

  public void setGroupNum(String groupNum) {
    this.groupNum = groupNum;
  }

}
