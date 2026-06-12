package com.cloud.runball.module_bluetooth.data.event;

/**
 * date: 2021/9/16
 * author: hwl
 * description:
 */
public class BallRunDetail {

  private int speed;
  private int circle;
  private int time;

  public BallRunDetail(int speed, int circle, int time) {
    this.speed = speed;
    this.circle = circle;
    this.time = time;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public int getCircle() {
    return circle;
  }

  public void setCircle(int circle) {
    this.circle = circle;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }
}
