package com.cloud.runball.bean;

import com.google.gson.annotations.SerializedName;

public class ClanItem {

  @SerializedName("id")
  private long id;

  @SerializedName("title")
  private String title;

  @SerializedName("clan_avatar")
  private String clanAvatar;

  @SerializedName("address")
  private String address;

  @SerializedName("introduction")
  private String introduction;

  @SerializedName("telephone")
  private String telephone;

  @SerializedName("created_at")
  private String createdAt;

  @SerializedName("clan_count")
  private int clanCount;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getClanAvatar() {
    return clanAvatar;
  }

  public void setClanAvatar(String clanAvatar) {
    this.clanAvatar = clanAvatar;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getIntroduction() {
    return introduction;
  }

  public void setIntroduction(String introduction) {
    this.introduction = introduction;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public int getClanCount() {
    return clanCount;
  }

  public void setClanCount(int clanCount) {
    this.clanCount = clanCount;
  }
}
