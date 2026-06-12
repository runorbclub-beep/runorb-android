package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

public class ClanMemberItem {

  @SerializedName("user_id")
  private String userId;

  @SerializedName("user_name")
  private String userName;

  @SerializedName("sys_sex_id")
  private String sysSexId;

  @SerializedName("user_img")
  private String userImg;

  @SerializedName("address")
  private String address;

  @SerializedName("remark")
  private String remark;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getSysSexId() {
    return sysSexId;
  }

  public void setSysSexId(String sysSexId) {
    this.sysSexId = sysSexId;
  }

  public String getUserImg() {
    return userImg;
  }

  public void setUserImg(String userImg) {
    this.userImg = userImg;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }
}
