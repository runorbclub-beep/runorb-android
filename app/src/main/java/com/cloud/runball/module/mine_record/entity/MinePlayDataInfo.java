package com.cloud.runball.module.mine_record.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MinePlayDataInfo implements Serializable {

  @SerializedName("user_id")
  private String userId;

  @SerializedName("user_play_id")
  private String userPlayId;

  @SerializedName("status")
  private int status;

  @SerializedName("duration")
  private int duration;

  @SerializedName("speed_max")
  private int speedMax;

  @SerializedName("circle_count")
  private int circleCount;

  @SerializedName("endurance_max")
  private int enduranceMax;

  @SerializedName("compare_last")
  private int compareLast;

  @SerializedName("start_time")
  private long startTime;

  @SerializedName("stop_time")
  private long stopTime;

  @SerializedName("distance")
  private float distance;

  @SerializedName("source")
  private int source;

  @SerializedName("start_time_format")
  private String startTimeFormat;

  @SerializedName("circle_count_format")
  private String circleCountFormat;

  @SerializedName("circle_count_unit")
  private String circleCountUnit;

  @SerializedName("distance_format")
  private String distanceFormat;

  @SerializedName("distance_unit")
  private String distanceUnit;

  @SerializedName("speed_max_format")
  private String speedMaxFormat;

  @SerializedName("speed_max_unit")
  private String speedMaxUnit;

  @SerializedName("duration_format")
  private String durationFormat;

  private boolean isLocal;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserPlayId() {
    return userPlayId;
  }

  public void setUserPlayId(String userPlayId) {
    this.userPlayId = userPlayId;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getSpeedMax() {
    return speedMax;
  }

  public void setSpeedMax(int speedMax) {
    this.speedMax = speedMax;
  }

  public int getCircleCount() {
    return circleCount;
  }

  public void setCircleCount(int circleCount) {
    this.circleCount = circleCount;
  }

  public int getEnduranceMax() {
    return enduranceMax;
  }

  public void setEnduranceMax(int enduranceMax) {
    this.enduranceMax = enduranceMax;
  }

  public int getCompareLast() {
    return compareLast;
  }

  public void setCompareLast(int compareLast) {
    this.compareLast = compareLast;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getStopTime() {
    return stopTime;
  }

  public void setStopTime(long stopTime) {
    this.stopTime = stopTime;
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }

  public String getStartTimeFormat() {
    return startTimeFormat;
  }

  public void setStartTimeFormat(String startTimeFormat) {
    this.startTimeFormat = startTimeFormat;
  }

  public String getCircleCountFormat() {
    return circleCountFormat;
  }

  public void setCircleCountFormat(String circleCountFormat) {
    this.circleCountFormat = circleCountFormat;
  }

  public String getCircleCountUnit() {
    return circleCountUnit;
  }

  public void setCircleCountUnit(String circleCountUnit) {
    this.circleCountUnit = circleCountUnit;
  }

  public String getDistanceFormat() {
    return distanceFormat;
  }

  public void setDistanceFormat(String distanceFormat) {
    this.distanceFormat = distanceFormat;
  }

  public String getDistanceUnit() {
    return distanceUnit;
  }

  public void setDistanceUnit(String distanceUnit) {
    this.distanceUnit = distanceUnit;
  }

  public String getSpeedMaxFormat() {
    return speedMaxFormat;
  }

  public void setSpeedMaxFormat(String speedMaxFormat) {
    this.speedMaxFormat = speedMaxFormat;
  }

  public String getSpeedMaxUnit() {
    return speedMaxUnit;
  }

  public void setSpeedMaxUnit(String speedMaxUnit) {
    this.speedMaxUnit = speedMaxUnit;
  }

  public String getDurationFormat() {
    return durationFormat;
  }

  public void setDurationFormat(String durationFormat) {
    this.durationFormat = durationFormat;
  }

  public boolean isLocal() {
    return isLocal;
  }

  public void setLocal(boolean local) {
    isLocal = local;
  }
}
