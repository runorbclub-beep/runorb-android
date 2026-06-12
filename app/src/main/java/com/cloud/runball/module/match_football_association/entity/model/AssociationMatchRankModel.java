package com.cloud.runball.module.match_football_association.entity.model;

import com.cloud.runball.model.BasicResponse;
import com.cloud.runball.module.match_football_association.entity.AssociationMatchRankInfo;
import com.cloud.runball.module.match_football_association.entity.AssociationMatchRankMyInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssociationMatchRankModel extends BasicResponse<AssociationMatchRankModel> {

  @SerializedName(value = "my_ranking")
  private int myRanking;

  @SerializedName(value = "my_ranking_info")
  private AssociationMatchRankMyInfo myRankingInfo;

  @SerializedName(value = "count")
  private int count;

  @SerializedName(value = "list")
  private List<AssociationMatchRankInfo> rankList;

  public int getMyRanking() {
    return myRanking;
  }

  public void setMyRanking(int myRanking) {
    this.myRanking = myRanking;
  }

  public AssociationMatchRankMyInfo getMyRankingInfo() {
    return myRankingInfo;
  }

  public void setMyRankingInfo(AssociationMatchRankMyInfo myRankingInfo) {
    this.myRankingInfo = myRankingInfo;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<AssociationMatchRankInfo> getRankList() {
    return rankList;
  }

  public void setRankList(List<AssociationMatchRankInfo> rankList) {
    this.rankList = rankList;
  }
}
