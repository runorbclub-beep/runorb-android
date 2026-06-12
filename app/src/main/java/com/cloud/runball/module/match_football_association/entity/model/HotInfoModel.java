package com.cloud.runball.module.match_football_association.entity.model;

import com.cloud.runball.model.BasicResponse;
import com.cloud.runball.module.match_football_association.entity.HotInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HotInfoModel extends BasicResponse<HotInfoModel> {

  @SerializedName(value = "count")
  private int count;

  @SerializedName(value = "list")
  private List<HotInfo> list;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<HotInfo> getList() {
    return list;
  }

  public void setList(List<HotInfo> list) {
    this.list = list;
  }

}
