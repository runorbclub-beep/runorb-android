package com.cloud.runball.widget.calendar;

import java.util.Date;

public class ClockInCalendarItemData {

  private boolean isPlaceholder;

  private int status;

  private Date date;

  private float distance;

  private String unit;

  private String targetDistance;

  public ClockInCalendarItemData(boolean isPlaceholder) {
    this.isPlaceholder = isPlaceholder;
  }

  public ClockInCalendarItemData(int status, Date date, float distance, String unit, String targetDistance) {
    this.status = status;
    this.date = date;
    this.distance = distance;
    this.unit = unit;
    this.targetDistance = targetDistance;
  }

  public boolean isPlaceholder() {
    return isPlaceholder;
  }

  public void setPlaceholder(boolean placeholder) {
    isPlaceholder = placeholder;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getTargetDistance() {
    return targetDistance;
  }

  public void setTargetDistance(String targetDistance) {
    this.targetDistance = targetDistance;
  }
}
