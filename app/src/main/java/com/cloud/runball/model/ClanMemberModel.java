package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClanMemberModel extends BasicResponse<ClanMemberModel> {

  @SerializedName("count")
  private int count;

  @SerializedName("totalCount")
  private int totalCount;

  @SerializedName("currentNo")
  private int currentNo;

  @SerializedName("list")
  private List<ClanMemberItem> list;

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

  public List<ClanMemberItem> getList() {
    return list;
  }

  public void setList(List<ClanMemberItem> list) {
    this.list = list;
  }
}
