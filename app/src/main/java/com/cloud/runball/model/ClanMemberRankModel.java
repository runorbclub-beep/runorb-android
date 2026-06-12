package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * date: 2021/12/11
 * author: hwl
 * description:
 */
public class ClanMemberRankModel {

  @SerializedName("user_clan_avg")
  private ClanAvg userClanAvg;

  @SerializedName("user_clan_info")
  private ClanInfo userClanInfo;

  @SerializedName("user_clan_list")
  private ClanList userClanList;

  public ClanAvg getUserClanAvg() {
    return userClanAvg;
  }

  public void setUserClanAvg(ClanAvg userClanAvg) {
    this.userClanAvg = userClanAvg;
  }

  public ClanInfo getUserClanInfo() {
    return userClanInfo;
  }

  public void setUserClanInfo(ClanInfo userClanInfo) {
    this.userClanInfo = userClanInfo;
  }

  public ClanList getUserClanList() {
    return userClanList;
  }

  public void setUserClanList(ClanList userClanList) {
    this.userClanList = userClanList;
  }

  public class ClanAvg {
    @SerializedName("user_count")
    private int userCount;
    @SerializedName("avg_speed_max")
    private String avgSpeedMax;
    @SerializedName("avg_speed_max_unit")
    private String avgSpeedMaxUnit;
    @SerializedName("avg_exponent_molecular")
    private String avgExponentMolecular;
    @SerializedName("avg_exponent_molecular_unit")
    private String avgExponentMolecularUnit;
    @SerializedName("avg_runball_exponent")
    private String avgRunballExponent;
    @SerializedName("avg_marathon")
    private String avgMarathon;

    public int getUserCount() {
      return userCount;
    }

    public void setUserCount(int userCount) {
      this.userCount = userCount;
    }

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

  public class ClanInfo {
    @SerializedName("id")
    private long id;
    @SerializedName("title")
    private String title;
    @SerializedName("clan_avatar")
    private String clanAvatar;
    @SerializedName("address")
    private String address;
    @SerializedName("introduction")
    private String introduction;
    @SerializedName("status")
    private int status;
    @SerializedName("telephone")
    private String telephone;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("indexs")
    private int indexs;
    @SerializedName("speed_max")
    private String speedMax;
    @SerializedName("speed_max_unit")
    private String speedMaxUnit;
    @SerializedName("exponent_molecular")
    private String exponentMolecular;
    @SerializedName("exponent_molecular_unit")
    private String exponentMolecularUnit;
    @SerializedName("runball_exponent")
    private String runballExponent;
    @SerializedName("marathon")
    private String marathon;
    @SerializedName("user_img")
    private String userImg;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("sys_sex_id")
    private String sysSexId;

    public long getId() {
      return id;
    }

    public void setId(long id) {
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

    public int getIndexs() {
      return indexs;
    }

    public void setIndexs(int indexs) {
      this.indexs = indexs;
    }

    public String getSpeedMax() {
      return speedMax;
    }

    public void setSpeedMax(String speedMax) {
      this.speedMax = speedMax;
    }

    public String getSpeedMaxUnit() {
      return speedMaxUnit;
    }

    public void setSpeedMaxUnit(String speedMaxUnit) {
      this.speedMaxUnit = speedMaxUnit;
    }

    public String getExponentMolecular() {
      return exponentMolecular;
    }

    public void setExponentMolecular(String exponentMolecular) {
      this.exponentMolecular = exponentMolecular;
    }

    public String getExponentMolecularUnit() {
      return exponentMolecularUnit;
    }

    public void setExponentMolecularUnit(String exponentMolecularUnit) {
      this.exponentMolecularUnit = exponentMolecularUnit;
    }

    public String getRunballExponent() {
      return runballExponent;
    }

    public void setRunballExponent(String runballExponent) {
      this.runballExponent = runballExponent;
    }

    public String getMarathon() {
      return marathon;
    }

    public void setMarathon(String marathon) {
      this.marathon = marathon;
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

    public void setUser_Name(String userName) {
      this.userName = userName;
    }

    public String getSysSexId() {
      return sysSexId;
    }

    public void setSysSexId(String sysSexId) {
      this.sysSexId = sysSexId;
    }
  }

  public class ClanList {
    @SerializedName("count")
    private int count;
    @SerializedName("totalCount")
    private int totalCount;
    @SerializedName("currentNo")
    private int currentNo;
    @SerializedName("list")
    private List<ClanMemberScore> list;

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }

    public int getTotalCount() {
      return totalCount;
    }

    public void setTotalCount(int totalCount) {
      this.totalCount = totalCount;
    }

    public int getCurrentNo() {
      return currentNo;
    }

    public void setCurrentNo(int currentNo) {
      this.currentNo = currentNo;
    }

    public List<ClanMemberScore> getList() {
      return list;
    }

    public void setList(List<ClanMemberScore> list) {
      this.list = list;
    }
  }

  public class ClanMemberScore {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("speed_max")
    private String speedMax;
    @SerializedName("speed_max_time")
    private long speedMaxTime;
    @SerializedName("speed_max_unit")
    private String speedMaxUnit;
    @SerializedName("exponent_molecular")
    private String exponentMolecular;
    @SerializedName("exponent_molecular_time")
    private long exponentMolecularTime;
    @SerializedName("exponent_molecular_unit")
    private String exponentMolecularUnit;
    @SerializedName("runball_exponent")
    private String runballExponent;
    @SerializedName("runball_exponent_time")
    private long runballExponentTime;
    @SerializedName("marathon_time")
    private int marathonTime;
    @SerializedName("marathon_asc")
    private String marathonAsc;
    @SerializedName("marathons")
    private String marathons;
    @SerializedName("usr_user")
    private ClanMember user;

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

    public long getSpeedMaxTime() {
      return speedMaxTime;
    }

    public void setSpeedMaxTime(long speedMaxTime) {
      this.speedMaxTime = speedMaxTime;
    }

    public String getSpeedMaxUnit() {
      return speedMaxUnit;
    }

    public void setSpeedMaxUnit(String speedMaxUnit) {
      this.speedMaxUnit = speedMaxUnit;
    }

    public String getExponentMolecular() {
      return exponentMolecular;
    }

    public void setExponentMolecular(String exponentMolecular) {
      this.exponentMolecular = exponentMolecular;
    }

    public long getExponentMolecularTime() {
      return exponentMolecularTime;
    }

    public void setExponentMolecularTime(long exponentMolecularTime) {
      this.exponentMolecularTime = exponentMolecularTime;
    }

    public String getExponentMolecularUnit() {
      return exponentMolecularUnit;
    }

    public void setExponentMolecularUnit(String exponentMolecularUnit) {
      this.exponentMolecularUnit = exponentMolecularUnit;
    }

    public String getRunballExponent() {
      return runballExponent;
    }

    public void setRunballExponent(String runballExponent) {
      this.runballExponent = runballExponent;
    }

    public long getRunballExponentTime() {
      return runballExponentTime;
    }

    public void setRunballExponentTime(long runballExponentTime) {
      this.runballExponentTime = runballExponentTime;
    }

    public int getMarathonTime() {
      return marathonTime;
    }

    public void setMarathonTime(int marathonTime) {
      this.marathonTime = marathonTime;
    }

    public String getMarathonAsc() {
      return marathonAsc;
    }

    public void setMarathonAsc(String marathonAsc) {
      this.marathonAsc = marathonAsc;
    }

    public String getMarathons() {
      return marathons;
    }

    public void setMarathons(String marathons) {
      this.marathons = marathons;
    }

    public ClanMember getUser() {
      return user;
    }

    public void setUser(ClanMember user) {
      this.user = user;
    }
  }

  public class ClanMember {
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
  }

}
