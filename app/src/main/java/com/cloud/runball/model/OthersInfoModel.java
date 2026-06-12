package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

public class OthersInfoModel extends BasicResponse<OthersInfoModel> {

  @SerializedName("user_id")
  private String userId;

  @SerializedName("user_name")
  private String userName;

  @SerializedName("user_img")
  private String userImg;

  @SerializedName("age")
  private String age;

  @SerializedName("user_height")
  private String userHeight;

  @SerializedName("user_weight")
  private String userWeight;

  @SerializedName("address")
  private String address;

  @SerializedName("wechart_id")
  private String weChartId;

  @SerializedName("self_description")
  private String selfDescription;

  @SerializedName("created_time")
  private String createdTime;

  @SerializedName("sys_sex_id")
  private String sysSexId;

  @SerializedName("user_achievement_one")
  private UserAchievement userAchievement;

  @SerializedName("user_clan_members")
  private UserClanMembers userClanMembers;

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

  public String getUserImg() {
    return userImg;
  }

  public void setUserImg(String userImg) {
    this.userImg = userImg;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getUserHeight() {
    return userHeight;
  }

  public void setUserHeight(String userHeight) {
    this.userHeight = userHeight;
  }

  public String getUserWeight() {
    return userWeight;
  }

  public void setUserWeight(String userWeight) {
    this.userWeight = userWeight;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getWeChartId() {
    return weChartId;
  }

  public void setWeChartId(String weChartId) {
    this.weChartId = weChartId;
  }

  public String getSelfDescription() {
    return selfDescription;
  }

  public void setSelfDescription(String selfDescription) {
    this.selfDescription = selfDescription;
  }

  public String getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(String createdTime) {
    this.createdTime = createdTime;
  }

  public UserAchievement getUserAchievement() {
    return userAchievement;
  }

  public void setUserAchievement(UserAchievement userAchievement) {
    this.userAchievement = userAchievement;
  }

  public String getSysSexId() {
    return sysSexId;
  }

  public void setSysSexId(String sysSexId) {
    this.sysSexId = sysSexId;
  }

  public UserClanMembers getUserClanMembers() {
    return userClanMembers;
  }

  public void setUserClanMembers(UserClanMembers userClanMembers) {
    this.userClanMembers = userClanMembers;
  }

  public class UserAchievement {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("speed_max")
    private String speedMax;

    @SerializedName("speed_max_time")
    private String speedMaxTime;

    @SerializedName("exponent_molecular")
    private String exponentMolecular;

    @SerializedName("exponent_molecular_time")
    private String exponentMolecularTime;

    @SerializedName("runball_exponent")
    private String runballExponent;

    @SerializedName("runball_exponent_time")
    private String runballExponentTime;

    @SerializedName("marathon")
    private String marathon;

    @SerializedName("marathon_time")
    private String marathonTime;

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getSpeedMax() {
      return speedMax;
    }

    public void setSpeedMax(String speedMax) {
      this.speedMax = speedMax;
    }

    public String getSpeedMaxTime() {
      return speedMaxTime;
    }

    public void setSpeedMaxTime(String speedMaxTime) {
      this.speedMaxTime = speedMaxTime;
    }

    public String getExponentMolecular() {
      return exponentMolecular;
    }

    public void setExponentMolecular(String exponentMolecular) {
      this.exponentMolecular = exponentMolecular;
    }

    public String getExponentMolecularTime() {
      return exponentMolecularTime;
    }

    public void setExponentMolecularTime(String exponentMolecularTime) {
      this.exponentMolecularTime = exponentMolecularTime;
    }

    public String getRunballExponent() {
      return runballExponent;
    }

    public void setRunballExponent(String runballExponent) {
      this.runballExponent = runballExponent;
    }

    public String getRunballExponentTime() {
      return runballExponentTime;
    }

    public void setRunballExponentTime(String runballExponentTime) {
      this.runballExponentTime = runballExponentTime;
    }

    public String getMarathon() {
      return marathon;
    }

    public void setMarathon(String marathon) {
      this.marathon = marathon;
    }

    public String getMarathonTime() {
      return marathonTime;
    }

    public void setMarathonTime(String marathonTime) {
      this.marathonTime = marathonTime;
    }
  }

  public class UserClanMembers {

    @SerializedName("user_clan_id")
    private String userClanId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("is_captain")
    private int isCaptain;

    @SerializedName("status")
    private int status;

    @SerializedName("title")
    private String title;

    @SerializedName("clan_avatar")
    private String clanAvatar;

    @SerializedName("address")
    private String address;

    @SerializedName("clan_count")
    private int clanCount;

    public String getUserClanId() {
      return userClanId;
    }

    public void setUserClanId(String userClanId) {
      this.userClanId = userClanId;
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public int getIsCaptain() {
      return isCaptain;
    }

    public void setIsCaptain(int isCaptain) {
      this.isCaptain = isCaptain;
    }

    public int getStatus() {
      return status;
    }

    public void setStatus(int status) {
      this.status = status;
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

    public int getClanCount() {
      return clanCount;
    }

    public void setClanCount(int clanCount) {
      this.clanCount = clanCount;
    }
  }

}
