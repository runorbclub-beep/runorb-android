package com.cloud.runball.service.sql.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tb_play_info")
public class PlayInfo {

  @PrimaryKey
  @ColumnInfo(name = "tb_sql_id")
  @Expose(serialize = false, deserialize = false)
  private long sqlId;

  // 摇球类型
  @ColumnInfo(name = "source")
  @SerializedName("source")
  private int source;

  // 摇加油使用
  @ColumnInfo(name = "sys_shake_id")
  @SerializedName("sys_shake_id")
  private long sysShakeId;

  // pk使用
  @ColumnInfo(name = "user_pk_list_id")
  @SerializedName("user_pk_list_id")
  private long userPkListId;

  // 赛事使用
  @ColumnInfo(name = "is_quartets")
  @SerializedName("is_quartets")
  private int isQuartets;

  // 赛事使用
  @ColumnInfo(name = "sys_match_id")
  @SerializedName("sys_match_id")
  private String sysMatchId;

  // 赛事使用
  @ColumnInfo(name = "sys_sys_match_id")
  @SerializedName("sys_sys_match_id")
  private String sysSysMatchId;

  @ColumnInfo(name = "matchs_stage_id")
  @SerializedName("matchs_stage_id")
  private String matchStageId;

  // 运动人id
  @ColumnInfo(name = "created_uid")
  @SerializedName("created_uid")
  private long createdUid;

  // 开始运动时间
  @ColumnInfo(name = "start_time")
  @SerializedName("start_time")
  private long startTime;

  // 停止运动时间
  @ColumnInfo(name = "stop_time")
  @SerializedName("stop_time")
  private long stopTime;

  // 累计运动圈数 单位：圈/分
  @ColumnInfo(name = "circle_count")
  @SerializedName("circle_count")
  private int circleCount;

  // 最高转速
  @ColumnInfo(name = "speed_max")
  @SerializedName("speed_max")
  private int maxSpeed;

  // 转速取值间隔，单位：毫秒
  @ColumnInfo(name = "interval")
  @SerializedName("interval")
  private long interval;

  // 是否为异常运动，0:正常，1:异常
  @ColumnInfo(name = "is_abnormal")
  @SerializedName("is_abnormal")
  private int isAbnormal;

  // 摇跑分子，一分钟圈数
  @ColumnInfo(name = "exponent_molecular")
  @SerializedName("exponent_molecular")
  private int exponentMolecular;

  //摇跑分母，达到半马用时 秒数
  @ColumnInfo(name = "exponent_denominator")
  @SerializedName("exponent_denominator")
  private int exponentDenominator;

  //摇跑一分钟最大圈速
  @ColumnInfo(name = "exponent_speed_max")
  @SerializedName("exponent_speed_max")
  private int exponentSpeedMax;

  //摇跑指数
  @ColumnInfo(name = "exponent")
  @SerializedName("exponent")
  private float exponent;

  //全马用时，单位：秒
  @ColumnInfo(name = "marathon")
  @SerializedName("marathon")
  private int marathon;

  //耐力，转速达到1万转每分钟的持续时间，单位：秒
  @ColumnInfo(name = "endurance_max")
  @SerializedName("endurance_max")
  private int maxEndurance;

  //本次运动累计距离，单位：米
  @ColumnInfo(name = "distance")
  @SerializedName("distance")
  private float distance;

  //本次运动累计时间，单位：秒
  @ColumnInfo(name = "duration")
  @SerializedName("duration")
  private long duration;

  /**
   * 数据保存状态
   * @see com.cloud.runball.constant.PlayingDataConstant.Update
   */
  @ColumnInfo(name = "upload_status")
//  @Expose(serialize = false, deserialize = false)
  private String uploadStatus;

  @Ignore
  @SerializedName("speed_detail")
  private Integer[] speedDetail;

  @ColumnInfo(name = "mac")
  @Expose(serialize = false, deserialize = false)
  private String mac;

  public long getSqlId() {
    return sqlId;
  }

  public void setSqlId(long sqlId) {
    this.sqlId = sqlId;
  }

  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }

  public long getSysShakeId() {
    return sysShakeId;
  }

  public void setSysShakeId(long sysShakeId) {
    this.sysShakeId = sysShakeId;
  }

  public long getUserPkListId() {
    return userPkListId;
  }

  public void setUserPkListId(long userPkListId) {
    this.userPkListId = userPkListId;
  }

  public int getIsQuartets() {
    return isQuartets;
  }

  public void setIsQuartets(int isQuartets) {
    this.isQuartets = isQuartets;
  }

  public String getSysMatchId() {
    return sysMatchId;
  }

  public void setSysMatchId(String sysMatchId) {
    this.sysMatchId = sysMatchId;
  }

  public String getSysSysMatchId() {
    return sysSysMatchId;
  }

  public void setSysSysMatchId(String sysSysMatchId) {
    this.sysSysMatchId = sysSysMatchId;
  }

  public String getMatchStageId() {
    return matchStageId;
  }

  public void setMatchStageId(String matchStageId) {
    this.matchStageId = matchStageId;
  }

  public long getCreatedUid() {
    return createdUid;
  }

  public void setCreatedUid(long createdUid) {
    this.createdUid = createdUid;
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

  public int getCircleCount() {
    return circleCount;
  }

  public void setCircleCount(int circleCount) {
    this.circleCount = circleCount;
  }

  public int getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(int maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public long getInterval() {
    return interval;
  }

  public void setInterval(long interval) {
    this.interval = interval;
  }

  public int getIsAbnormal() {
    return isAbnormal;
  }

  public void setIsAbnormal(int isAbnormal) {
    this.isAbnormal = isAbnormal;
  }

  public int getExponentMolecular() {
    return exponentMolecular;
  }

  public void setExponentMolecular(int exponentMolecular) {
    this.exponentMolecular = exponentMolecular;
  }

  public int getExponentDenominator() {
    return exponentDenominator;
  }

  public void setExponentDenominator(int exponentDenominator) {
    this.exponentDenominator = exponentDenominator;
  }

  public int getExponentSpeedMax() { return exponentSpeedMax; }

  public void setExponentSpeedMax(int exponentSpeedMax) {
    this.exponentSpeedMax = exponentSpeedMax;
  }

  public float getExponent() {
    return exponent;
  }

  public void setExponent(float exponent) {
    this.exponent = exponent;
  }

  public int getMarathon() {
    return marathon;
  }

  public void setMarathon(int marathon) {
    this.marathon = marathon;
  }

  public int getMaxEndurance() {
    return maxEndurance;
  }

  public void setMaxEndurance(int maxEndurance) {
    this.maxEndurance = maxEndurance;
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public String getUploadStatus() {
    return uploadStatus;
  }

  public void setUploadStatus(String uploadStatus) {
    this.uploadStatus = uploadStatus;
  }

  public Integer[] getSpeedDetail() {
    return speedDetail;
  }

  public void setSpeedDetail(Integer[] speedDetail) {
    this.speedDetail = speedDetail;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }
}
