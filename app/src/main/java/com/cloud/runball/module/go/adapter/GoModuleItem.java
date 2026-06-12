package com.cloud.runball.module.go.adapter;

public class GoModuleItem {

  private int moduleId;
  private String title;
  private String viceTitle;
  private int iconId;
  private String label;
  private int bgId;

  private String mark;

  public GoModuleItem(int moduleId, String title, String viceTitle, int iconId, String label, int bgId) {
    this.moduleId = moduleId;
    this.title = title;
    this.viceTitle = viceTitle;
    this.iconId = iconId;
    this.label = label;
    this.bgId = bgId;
  }

  public int getModuleId() {
    return moduleId;
  }

  public void setModuleId(int moduleId) {
    this.moduleId = moduleId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getViceTitle() {
    return viceTitle;
  }

  public void setViceTitle(String viceTitle) {
    this.viceTitle = viceTitle;
  }

  public int getIconId() {
    return iconId;
  }

  public void setIconId(int iconId) {
    this.iconId = iconId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getBgId() {
    return bgId;
  }

  public void setBgId(int bgId) {
    this.bgId = bgId;
  }

  public String getMark() {
    return mark;
  }

  public void setMark(String mark) {
    this.mark = mark;
  }
}
