package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

public class ClockInTarget {

  @SerializedName("id")
  private int id;

  @SerializedName("user_id")
  private long userId;

  @SerializedName("source")
  private int source;

  @SerializedName("month_time")
  private String monthTime;

  @SerializedName("target_distance")
  private String targetDistance;

  @SerializedName("min_days")
  private int minDays;

  @SerializedName("fulfil_days")
  private int fulfilDays;

  @SerializedName("status")
  private int status;

  @SerializedName("created_at")
  private String createdAt;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }

  public String getMonthTime() {
    return monthTime;
  }

  public void setMonthTime(String monthTime) {
    this.monthTime = monthTime;
  }

  public String getTargetDistance() {
    return targetDistance;
  }

  public void setTargetDistance(String targetDistance) {
    this.targetDistance = targetDistance;
  }

  public int getMinDays() {
    return minDays;
  }

  public void setMinDays(int minDays) {
    this.minDays = minDays;
  }

  public int getFulfilDays() {
    return fulfilDays;
  }

  public void setFulfilDays(int fulfilDays) {
    this.fulfilDays = fulfilDays;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }
}
