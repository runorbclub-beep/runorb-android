package com.cloud.runball.module.match_football_association.entity;

import com.google.gson.annotations.SerializedName;

public class AssociationMatchRankMyInfo {

  @SerializedName(value = "index")
  private int index;

  @SerializedName(value = "user_img")
  private String userImg;

  @SerializedName(value = "user_name")
  private String userName;

  @SerializedName(value = "user_id")
  private String userId;

  @SerializedName(value = "address")
  private String address;

  @SerializedName(value = "sys_sex_id")
  private String sysSexId;

  @SerializedName(value = "unit")
  private String unit;

  @SerializedName(value = "value")
  private String value;

  @SerializedName(value = "time")
  private String time;

  @SerializedName(value = "team_tag")
  private String teamTag;

  @SerializedName(value = "join_sum")
  private String joinSum;

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getUserImg() {
    return userImg;
  }

  public void setUserImg(String userImg) {
    this.userImg = userImg;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSysSexId() {
    return sysSexId;
  }

  public void setSysSexId(String sysSexId) {
    this.sysSexId = sysSexId;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getTeamTag() {
    return teamTag;
  }

  public void setTeamTag(String teamTag) {
    this.teamTag = teamTag;
  }

  public String getJoinSum() {
    return joinSum;
  }

  public void setJoinSum(String joinSum) {
    this.joinSum = joinSum;
  }
}
