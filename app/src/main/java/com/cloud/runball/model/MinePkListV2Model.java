package com.cloud.runball.model;

import com.cloud.runball.bean.MinePkInfoV2;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinePkListV2Model extends BasicResponse<MinePkListV2Model> {

  @SerializedName("count")
  private int count;

  @SerializedName("list")
  private List<MinePkInfoV2> list;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<MinePkInfoV2> getList() {
    return list;
  }

  public void setList(List<MinePkInfoV2> list) {
    this.list = list;
  }

}
