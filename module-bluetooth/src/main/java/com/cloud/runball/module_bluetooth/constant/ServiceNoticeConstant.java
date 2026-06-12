package com.cloud.runball.module_bluetooth.constant;

public interface ServiceNoticeConstant {

  /**
   * 扫描开始
   */
  int CODE_SCAN_START = 1;

  /**
   * 发现设备
   */
  int CODE_SCAN_DEVICE = 2;

  /**
   * 扫描结束
   */
  int CODE_SCAN_FINISHED = 3;

  /**
   * 连接开始
   */
  int CODE_CONNECT_START = 4;

  /**
   * 连接失败
   */
  int CODE_CONNECT_FAIL = 5;

  /**
   * 连接成功
   */
  int CODE_CONNECT_SUCCESS = 6;

  /**
   * 连接结束
   */
  int CODE_CONNECT_FINISHED = 7;

  /**
   * 通知开启成功
   */
  int CODE_NOTIFY_SUCCESS = 8;

  /**
   * 通知开启失败
   */
  int CODE_NOTIFY_FAILURE = 9;

  /**
   * 通知设备摇动数据
   */
  int CODE_NOTIFY_RUN_START = 10;

  /**
   * 通知设备摇动数据
   */
  int CODE_NOTIFY_RUNNING = 11;

  /**
   * 通知设备摇动结束
   */
  int CODE_NOTIFY_RUN_FINISH = 12;

  /**
   * 通知设备版本
   */
  int CODE_NOTIFY_BALL_VER = 13;

  /**
   * 通知设备电量
   */
  int CODE_NOTIFY_ELECTRICITY = 14;

  /**
   * 通知设备摇动结束
   */
  int CODE_NOTIFY_TOTAL_TIME = 15;

  /**
   * 通知比赛计时
   */
  int CODE_NOTIFY_MATCH_TIME = 16;

}
