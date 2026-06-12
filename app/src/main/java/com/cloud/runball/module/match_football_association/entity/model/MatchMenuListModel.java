package com.cloud.runball.module.match_football_association.entity.model;

import com.cloud.runball.model.BasicResponse;
import com.cloud.runball.module.match_football_association.entity.MatchMenu;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatchMenuListModel extends BasicResponse<MatchMenu> {

  @SerializedName(value = "count")
  private int count;

  @SerializedName(value = "list")
  private List<MatchMenu> matchMenuList;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<MatchMenu> getMatchMenuList() {
    return matchMenuList;
  }

  public void setMatchMenuList(List<MatchMenu> matchMenuList) {
    this.matchMenuList = matchMenuList;
  }

}
