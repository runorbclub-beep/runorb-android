package com.cloud.runball.widget;

public class NodeInfo {

  private float progressValue;
  private String title;
  private String viceTitle;
  private String arriveSeatTip;
  private String arriveTip;
  private int nodeType;
  private float nodeValue;

  public NodeInfo() {

  }

  public NodeInfo(int nodeType, float nodeValue, String title, String viceTitle, String arriveSeatTip, float progressValue) {
    this.nodeType = nodeType;
    this.nodeValue = nodeValue;
    this.title = title;
    this.viceTitle = viceTitle;
    this.arriveSeatTip = arriveSeatTip;
    this.progressValue = progressValue;
  }


  public float getProgressValue() {
    return progressValue;
  }

  public void setProgressValue(float progressValue) {
    this.progressValue = progressValue;
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

  public String getArriveSeatTip() {
    return arriveSeatTip;
  }

  public void setArriveSeatTip(String arriveSeatTip) {
    this.arriveSeatTip = arriveSeatTip;
  }

  public String getArriveTip() {
    return arriveTip;
  }

  public void setArriveTip(String arriveTip) {
    this.arriveTip = arriveTip;
  }

  public int getNodeType() {
    return nodeType;
  }

  public void setNodeType(int nodeType) {
    this.nodeType = nodeType;
  }

  public float getNodeValue() {
    return nodeValue;
  }

  public void setNodeValue(float nodeValue) {
    this.nodeValue = nodeValue;
  }
}
