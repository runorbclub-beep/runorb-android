package com.cloud.runball.bean.banner;

import com.google.gson.annotations.SerializedName;

public class RankBannerData {

  @SerializedName("img_url")
  private String imgUrl;

  @SerializedName("jump_link")
  private String jumpLink;

  @SerializedName("banner_type")
  private int bannerType;

  @SerializedName("is_quartets")
  private int isQuartets;

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public String getJumpLink() {
    return jumpLink;
  }

  public void setJumpLink(String jumpLink) {
    this.jumpLink = jumpLink;
  }

  public int getBannerType() {
    return bannerType;
  }

  public void setBannerType(int bannerType) {
    this.bannerType = bannerType;
  }

  public int getIsQuartets() {
    return isQuartets;
  }

  public void setIsQuartets(int isQuartets) {
    this.isQuartets = isQuartets;
  }
}
