package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

public class MineRankingDetailsModel extends BasicResponse<MineRankingDetailsModel> {

  @SerializedName("speed_max")
  private int speedMax;

  @SerializedName("speed_max_time")
  private String speedMaxTime;

  @SerializedName("exponent_molecular")
  private String exponentMolecular;

  @SerializedName("exponent_molecular_time")
  private String exponentMolecularTime;

  @SerializedName("runball_exponent")
  private float runballExponent;

  @SerializedName("runball_exponent_time")
  private String runballExponentTime;

  @SerializedName("marathon")
  private String marathon;

  @SerializedName("marathon_time")
  private String marathonTime;

  @SerializedName("speed_max_count")
  private int speedMaxCount;

  @SerializedName("speed_max_unit")
  private String speedMaxUnit;

  @SerializedName("exponent_molecular_count")
  private int exponentMolecularCount;

  @SerializedName("runball_exponent_count")
  private int runballExponentCount;

  @SerializedName("marathon_count")
  private int marathonCount;

  @SerializedName("exponent_molecular_unit")
  private String exponentMolecularUnit;

  public int getSpeedMax() {
    return speedMax;
  }

  public void setSpeedMax(int speedMax) {
    this.speedMax = speedMax;
  }

  public String getSpeedMaxTime() {
    return speedMaxTime;
  }

  public void setSpeedMaxTime(String speedMaxTime) {
    this.speedMaxTime = speedMaxTime;
  }

  public String getExponentMolecular() {
    return exponentMolecular;
  }

  public void setExponentMolecular(String exponentMolecular) {
    this.exponentMolecular = exponentMolecular;
  }

  public String getExponentMolecularTime() {
    return exponentMolecularTime;
  }

  public void setExponentMolecularTime(String exponentMolecularTime) {
    this.exponentMolecularTime = exponentMolecularTime;
  }

  public float getRunballExponent() {
    return runballExponent;
  }

  public void setRunballExponent(float runballExponent) {
    this.runballExponent = runballExponent;
  }

  public String getRunballExponentTime() {
    return runballExponentTime;
  }

  public void setRunballExponentTime(String runballExponentTime) {
    this.runballExponentTime = runballExponentTime;
  }

  public String getMarathon() {
    return marathon;
  }

  public void setMarathon(String marathon) {
    this.marathon = marathon;
  }

  public String getMarathonTime() {
    return marathonTime;
  }

  public int getSpeedMaxCount() {
    return speedMaxCount;
  }

  public void setSpeedMaxCount(int speedMaxCount) {
    this.speedMaxCount = speedMaxCount;
  }

  public void setMarathonTime(String marathonTime) {
    this.marathonTime = marathonTime;
  }

  public String getSpeedMaxUnit() {
    return speedMaxUnit;
  }

  public void setSpeedMaxUnit(String speedMaxUnit) {
    this.speedMaxUnit = speedMaxUnit;
  }

  public int getExponentMolecularCount() {
    return exponentMolecularCount;
  }

  public void setExponentMolecularCount(int exponentMolecularCount) {
    this.exponentMolecularCount = exponentMolecularCount;
  }

  public int getRunballExponentCount() {
    return runballExponentCount;
  }

  public void setRunballExponentCount(int runballExponentCount) {
    this.runballExponentCount = runballExponentCount;
  }

  public int getMarathonCount() {
    return marathonCount;
  }

  public void setMarathonCount(int marathonCount) {
    this.marathonCount = marathonCount;
  }

  public String getExponentMolecularUnit() {
    return exponentMolecularUnit;
  }

  public void setExponentMolecularUnit(String exponentMolecularUnit) {
    this.exponentMolecularUnit = exponentMolecularUnit;
  }
}
