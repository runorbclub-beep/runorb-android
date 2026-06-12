package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClanRankingModel extends BasicResponse<ClanRankingModel> {

  @SerializedName("my_clan_info")
  private MyClanInfo myClanInfo;

  @SerializedName("user_clan_list")
  private UserClanList userClanList;

  public MyClanInfo getMyClanInfo() {
    return myClanInfo;
  }

  public void setMyClanInfo(MyClanInfo myClanInfo) {
    this.myClanInfo = myClanInfo;
  }

  public UserClanList getUserClanList() {
    return userClanList;
  }

  public void setUserClanList(UserClanList userClanList) {
    this.userClanList = userClanList;
  }

  public class MyClanInfo {

    @SerializedName("user_clan_id")
    private String userClanId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("count")
    private int count;

    @SerializedName("status")
    private int status; // 用户申请加入俱乐部状态：'状态：1待审核 2已通过 0已拒绝',

    @SerializedName("indexs")
    private String index;

    @SerializedName("avg_marathon")
    private String marathons;

    @SerializedName("avg_speed_max")
    private String speedMax;

    @SerializedName("avg_speed_max_unit")
    private String speedMaxUnit;

    @SerializedName("avg_exponent_molecular")
    private String exponentMolecular;

    @SerializedName("avg_exponent_molecular_unit")
    private String exponentMolecularUnit;

    @SerializedName("avg_runball_exponent")
    private String runballExponents;

    @SerializedName("user_clan")
    private UserClan userClan;

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

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }

    public int getStatus() {
      return status;
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public String getIndex() {
      return index;
    }

    public void setIndex(String index) {
      this.index = index;
    }

    public String getMarathons() {
      return marathons;
    }

    public void setMarathons(String marathons) {
      this.marathons = marathons;
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

    public String getRunballExponents() {
      return runballExponents;
    }

    public void setRunballExponents(String runballExponents) {
      this.runballExponents = runballExponents;
    }

    public UserClan getUserClan() {
      return userClan;
    }

    public void setUserClan(UserClan userClan) {
      this.userClan = userClan;
    }
  }

  public class UserClan {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("clan_avatar")
    private String clanAvatar;

    @SerializedName("address")
    private String address;

    @SerializedName("introduction")
    private String introduction;

    @SerializedName("status")
    private int status; // 俱乐部状态：状态：0审核中  1正常  2已拒绝

    @SerializedName("telephone")
    private String telephone;

    @SerializedName("created_at")
    private String createdAt;

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
  }

  public class UserClanList {

    @SerializedName("count")
    private int count;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("currentNo")
    private int currentNo;

    @SerializedName("list")
    private List<ClanRankItem> list;

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

    public List<ClanRankItem> getList() {
      return list;
    }

    public void setList(List<ClanRankItem> list) {
      this.list = list;
    }
  }

  public class ClanRankItem {

    @SerializedName("user_clan_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("clan_avatar")
    private String clanAvatar;

    @SerializedName("address")
    private String address;

    @SerializedName("count_t")
    private int clanCount;

    @SerializedName("introduction")
    private String introduction;

    @SerializedName("status")
    private int status;

    @SerializedName("telephone")
    private String telephone;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("avg_speed_max")
    private String speedMax;

    @SerializedName("avg_speed_max_unit")
    private String speedMaxUnit;

    @SerializedName("avg_marathon")
    private String marathon;

    @SerializedName("avg_exponent_molecular")
    private String exponentMolecular;

    @SerializedName("avg_exponent_molecular_unit")
    private String exponentMolecularUnit;

    @SerializedName("avg_runball_exponent")
    private String runballExponent;

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

    public int getClanCount() {
      return clanCount;
    }

    public void setClanCount(int clanCount) {
      this.clanCount = clanCount;
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

    public String getMarathon() {
      return marathon;
    }

    public void setMarathon(String marathon) {
      this.marathon = marathon;
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
  }

}
