package com.cloud.runball.module_bluetooth.constant;

/**
 * date: 2021/9/16
 * author: hwl
 * description:
 */
public interface ServiceSendConstant {

  /**
   * 开启扫描
   */
  int CODE_SCAN_OPEN = 1;

  /**
   * 停止扫描
   */
  int CODE_SCAN_STOP = 2;

  /**
   * 查询设备电量
   */
  int CODE_REQUEST_ELECTRICITY = 3;

  /**
   * 连接设备
   */
  int CODE_CONNECT_DEVICE = 4;

  /**
   * 断开设备
   */
  int CODE_CONNECT_FINISH = 7;

  /**
   * 清零设备缓存
   */
  int CODE_CIRCLE_CLEAR = 8;

  /**
   * 开启赛事计时
   */
  int CODE_START_MATCH_TIMING = 9;

  /**
   * 关闭赛事计时
   */
  int CODE_CLOSE_MATCH_TIMING = 10;

}
