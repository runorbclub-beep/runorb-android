package com.cloud.runball.model;

import com.cloud.runball.bean.ClanItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClanModel extends BasicResponse<ClanModel> {

  @SerializedName("count")
  private int count;

  @SerializedName("totalCount")
  private int totalCount;

  @SerializedName("currentNo")
  private int currentNo;

  @SerializedName("list")
  private List<ClanItem> list;

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

  public List<ClanItem> getList() {
    return list;
  }

  public void setList(List<ClanItem> list) {
    this.list = list;
  }
}
