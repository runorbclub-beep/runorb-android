package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

public class RuleIntroduceModel extends BasicResponse<RuleIntroduceModel> {

  @SerializedName("ranking_rule")
  private String rankingRule;

  @SerializedName("pk_rule")
  private String pkRule;

  public String getRankingRule() {
    return rankingRule;
  }

  public void setRankingRule(String rankingRule) {
    this.rankingRule = rankingRule;
  }

  public String getPkRule() {
    return pkRule;
  }

  public void setPkRule(String pkRule) {
    this.pkRule = pkRule;
  }
}
