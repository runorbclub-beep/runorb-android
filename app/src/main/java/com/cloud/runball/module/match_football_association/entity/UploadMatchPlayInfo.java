package com.cloud.runball.module.match_football_association.entity;

import com.google.gson.annotations.SerializedName;

public class UploadMatchPlayInfo {

  @SerializedName(value = "s_runball_exponent")
  private float runballExponent;

  @SerializedName(value = "s_runball_exponent_time")
  private int runballExponent_time;

  @SerializedName(value = "s_exponent_molecular")
  private int exponentMolecular;

  @SerializedName(value = "s_exponent_molecular_time")
  private int exponentMolecularTime;

  @SerializedName(value = "s_exponent_denominator")
  private int exponentDenominator;

  @SerializedName(value = "s_marathon")
  private int marathon;

  @SerializedName(value = "s_marathon_time")
  private int marathonTime;

  @SerializedName(value = "s_endurance_max")
  private int enduranceMax;

  @SerializedName(value = "s_play_count")
  private int playCount;

  public float getRunballExponent() {
    return runballExponent;
  }

  public void setRunballExponent(float runballExponent) {
    this.runballExponent = runballExponent;
  }

  public int getRunballExponent_time() {
    return runballExponent_time;
  }

  public void setRunballExponent_time(int runballExponent_time) {
    this.runballExponent_time = runballExponent_time;
  }

  public int getExponentMolecular() {
    return exponentMolecular;
  }

  public void setExponentMolecular(int exponentMolecular) {
    this.exponentMolecular = exponentMolecular;
  }

  public int getExponentMolecularTime() {
    return exponentMolecularTime;
  }

  public void setExponentMolecularTime(int exponentMolecularTime) {
    this.exponentMolecularTime = exponentMolecularTime;
  }

  public int getExponentDenominator() {
    return exponentDenominator;
  }

  public void setExponentDenominator(int exponentDenominator) {
    this.exponentDenominator = exponentDenominator;
  }

  public int getMarathon() {
    return marathon;
  }

  public void setMarathon(int marathon) {
    this.marathon = marathon;
  }

  public int getMarathonTime() {
    return marathonTime;
  }

  public void setMarathonTime(int marathonTime) {
    this.marathonTime = marathonTime;
  }

  public int getEnduranceMax() {
    return enduranceMax;
  }

  public void setEnduranceMax(int enduranceMax) {
    this.enduranceMax = enduranceMax;
  }

  public int getPlayCount() {
    return playCount;
  }

  public void setPlayCount(int playCount) {
    this.playCount = playCount;
  }
}
