package com.cloud.runball.module.match_football_association.entity;

import com.google.gson.annotations.SerializedName;

public class MatchDetailInfoItem {

  @SerializedName(value = "label")
  private String label;

  @SerializedName(value = "value")
  private String value;

  @SerializedName(value = "icon")
  private String icon;

  @SerializedName(value = "is_html")
  private int isHtml;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public int getIsHtml() {
    return isHtml;
  }

  public void setIsHtml(int isHtml) {
    this.isHtml = isHtml;
  }
}
