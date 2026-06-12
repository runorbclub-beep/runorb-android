package com.cloud.runball.module_bluetooth.data.event;

public class MatchTimingInfo {

  private int time;

  private boolean isPlaying;

  public MatchTimingInfo(int time, boolean isPlaying) {
    this.time = time;
    this.isPlaying = isPlaying;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  public void setPlaying(boolean playing) {
    isPlaying = playing;
  }

}
