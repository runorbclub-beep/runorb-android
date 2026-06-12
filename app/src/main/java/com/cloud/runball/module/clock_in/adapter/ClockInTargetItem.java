package com.cloud.runball.module.clock_in.adapter;

import com.cloud.runball.model.ClockInTarget;

import java.util.Date;

public class ClockInTargetItem {

  private Date date;
  // 是否无计划数据
  private boolean isEmpty;
  // 日期是否已过去
  private boolean isOverdue;
  private ClockInTarget clockInTarget;

  public ClockInTargetItem(Date date, boolean isEmpty, boolean isOverdue, ClockInTarget clockInTarget) {
    this.date = date;
    this.isEmpty = isEmpty;
    this.clockInTarget = clockInTarget;
    this.isOverdue = isOverdue;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public void setEmpty(boolean empty) {
    isEmpty = empty;
  }

  public boolean isOverdue() {
    return isOverdue;
  }

  public void setOverdue(boolean overdue) {
    isOverdue = overdue;
  }

  public ClockInTarget getClockInTarget() {
    return clockInTarget;
  }

  public void setClockInTarget(ClockInTarget clockInTarget) {
    this.clockInTarget = clockInTarget;
  }
}
