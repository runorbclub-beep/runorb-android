package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClanInfoModel extends BasicResponse<ClanInfoModel> {

  @SerializedName("id")
  private String id; // 俱乐部id

  @SerializedName("title")
  private String title; // 俱乐部名称

  @SerializedName("clan_avatar")
  private String clanAvatar; // 俱乐部头像

  @SerializedName("address")
  private String address; // 俱乐部地区

  @SerializedName("introduction")
  private String introduction; // 俱乐部介绍

  @SerializedName("status")
  private int status; // 俱乐部状态：0审核中 1正常 2已拒绝

  @SerializedName("telephone")
  private String telephone; //  俱乐部联系方式

  @SerializedName("created_at")
  private String createdAt; // 2021-12-07

  @SerializedName("user_count")
  private int userCount;

  @SerializedName("is_user_captain")
  private int userCaptainStatus; // 用户是否队长：0否  1是 2尚未加入俱乐部

  @SerializedName("avg_achievement")
  private Achievement avgAchievement;

  @SerializedName("captain_info")
  private CaptainInfo captainInfo;

  @SerializedName("user_status")
  private int userStatus; // 1待审核  2已加入  3未加入 4不可加入

  @SerializedName("review_count")
  private int reviewCount; // 待审核的新成员数量

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getClanAvatar() {
    return clanAvatar;
  }

  public void setClanAvatar(String clanAvatar) {
    this.clanAvatar = clanAvatar;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getIntroduction() {
    return introduction;
  }

  public void setIntroduction(String introduction) {
    this.introduction = introduction;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public int getUserCount() {
    return userCount;
  }

  public void setUserCount(int userCount) {
    this.userCount = userCount;
  }

  public int getUserCaptainStatus() {
    return userCaptainStatus;
  }

  public void setUserCaptainStatus(int userCaptainStatus) {
    this.userCaptainStatus = userCaptainStatus;
  }

  public Achievement getAvgAchievement() {
    return avgAchievement;
  }

  public void setAvgAchievement(Achievement avgAchievement) {
    this.avgAchievement = avgAchievement;
  }

  public CaptainInfo getCaptainInfo() {
    return captainInfo;
  }

  public void setCaptainInfo(CaptainInfo captainInfo) {
    this.captainInfo = captainInfo;
  }

  public int getUserStatus() {
    return userStatus;
  }

  public void setUserStatus(int userStatus) {
    this.userStatus = userStatus;
  }

  public int getReviewCount() {
    return reviewCount;
  }

  public void setReviewCount(int reviewCount) {
    this.reviewCount = reviewCount;
  }

  public class Achievement implements Serializable {

    @SerializedName("avg_speed_max")
    private String avgSpeedMax; // max转速

    @SerializedName("avg_speed_max_unit")
    private String avgSpeedMaxUnit; // rpm

    @SerializedName("avg_exponent_molecular")
    private String avgExponentMolecular; // 摇跑一分钟

    @SerializedName("avg_exponent_molecular_unit")
    private String avgExponentMolecularUnit; // km

    @SerializedName("avg_runball_exponent")
    private String avgRunballExponent; //YPI指数

    @SerializedName("avg_marathon")
    private String avgMarathon; // 马拉松

    public String getAvgSpeedMax() {
      return avgSpeedMax;
    }

    public void setAvgSpeedMax(String avgSpeedMax) {
      this.avgSpeedMax = avgSpeedMax;
    }

    public String getAvgSpeedMaxUnit() {
      return avgSpeedMaxUnit;
    }

    public void setAvgSpeedMaxUnit(String avgSpeedMaxUnit) {
      this.avgSpeedMaxUnit = avgSpeedMaxUnit;
    }

    public String getAvgExponentMolecular() {
      return avgExponentMolecular;
    }

    public void setAvgExponentMolecular(String avgExponentMolecular) {
      this.avgExponentMolecular = avgExponentMolecular;
    }

    public String getAvgExponentMolecularUnit() {
      return avgExponentMolecularUnit;
    }

    public void setAvgExponentMolecularUnit(String avgExponentMolecularUnit) {
      this.avgExponentMolecularUnit = avgExponentMolecularUnit;
    }

    public String getAvgRunballExponent() {
      return avgRunballExponent;
    }

    public void setAvgRunballExponent(String avgRunballExponent) {
      this.avgRunballExponent = avgRunballExponent;
    }

    public String getAvgMarathon() {
      return avgMarathon;
    }

    public void setAvgMarathon(String avgMarathon) {
      this.avgMarathon = avgMarathon;
    }

  }

  public class CaptainInfo implements Serializable {

    @SerializedName("user_id")
    private String userId; // 队长ID

    @SerializedName("user_name")
    private String userName;

    @SerializedName("sys_sex_id")
    private String sysSexId; // 性别

    @SerializedName("user_img")
    private String userImg; // 队长头像

    @SerializedName("address")
    private String address; // 队长地区

    @SerializedName("is_captain")
    private int isCaptain; // 是否队长：0否 1是

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

    public int getIsCaptain() {
      return isCaptain;
    }

    public void setIsCaptain(int isCaptain) {
      this.isCaptain = isCaptain;
    }


  }

}
