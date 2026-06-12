package com.cloud.runball.module.match_football_association.entity.model;

import com.cloud.runball.model.BasicResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssociationTeamDetailRankingModel extends BasicResponse<AssociationTeamDetailRankingModel> {

  @SerializedName(value = "my_ranking")
  private int myRanking;

  @SerializedName(value = "my_ranking_info")
  private MyRankingInfo myRankingInfo;

  @SerializedName(value = "count")
  private int count;

  @SerializedName(value = "list")
  private List<MyRankingInfo> list;

  @SerializedName(value = "team_avg")
  private String teamAvg;

  @SerializedName(value = "join_sum")
  private String joinSum;

  public int getMyRanking() {
    return myRanking;
  }

  public void setMyRanking(int myRanking) {
    this.myRanking = myRanking;
  }

  public MyRankingInfo getMyRankingInfo() {
    return myRankingInfo;
  }

  public void setMyRankingInfo(MyRankingInfo myRankingInfo) {
    this.myRankingInfo = myRankingInfo;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<MyRankingInfo> getList() {
    return list;
  }

  public void setList(List<MyRankingInfo> list) {
    this.list = list;
  }

  public String getTeamAvg() {
    return teamAvg;
  }

  public void setTeamAvg(String teamAvg) {
    this.teamAvg = teamAvg;
  }

  public String getJoinSum() {
    return joinSum;
  }

  public void setJoinSum(String joinSum) {
    this.joinSum = joinSum;
  }

  public static class MyRankingInfo {

    @SerializedName(value = "team_tag")
    private String teamTag;

    @SerializedName(value = "index")
    private int index;

    @SerializedName(value = "user_id")
    private String userId;

    @SerializedName(value = "user_img")
    private String userImg;

    @SerializedName(value = "user_name")
    private String userName;

    @SerializedName(value = "address")
    private String address;

    @SerializedName(value = "value")
    private String value;

    @SerializedName(value = "unit")
    private String unit;

    @SerializedName(value = "time")
    private String time;

    @SerializedName(value = "sys_sex_id")
    private String sysSexId;

    public String getTeamTag() {
      return teamTag;
    }

    public void setTeamTag(String teamTag) {
      this.teamTag = teamTag;
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
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

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public String getUnit() {
      return unit;
    }

    public void setUnit(String unit) {
      this.unit = unit;
    }

    public String getTime() {
      return time;
    }

    public void setTime(String time) {
      this.time = time;
    }

    public String getSysSexId() {
      return sysSexId;
    }

    public void setSysSexId(String sysSexId) {
      this.sysSexId = sysSexId;
    }
  }

}
