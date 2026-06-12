package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

/**
 * date: 2021/10/25
 * author: hwl
 * description:
 */
public class UpLoadInfoModel extends BasicResponse<UpLoadInfoModel> {

  @SerializedName(value = "edition")
  private String edition;

  @SerializedName(value = "domain_name")
  private String domainName;

  @SerializedName(value = "paly_url")
  private String playUrl;

  public String getEdition() {
    return edition;
  }

  public void setEdition(String edition) {
    this.edition = edition;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  public String getPlayUrl() {
    return playUrl;
  }

  public void setPlayUrl(String playUrl) {
    this.playUrl = playUrl;
  }
}
