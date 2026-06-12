package com.cloud.runball.module_bluetooth.data.event;

/**
 * date: 2021/9/20
 * author: hwl
 * description:
 */
public class BallInfo {

  private String name;
  private String mac;

  public BallInfo(String name, String mac) {
    this.name = name;
    this.mac = mac;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

}
