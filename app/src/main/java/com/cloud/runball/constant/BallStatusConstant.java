package com.cloud.runball.constant;

public interface BallStatusConstant {

  /**
   * 球的状态 - 闲置
   */
  int IDLE = 0;

  /**
   * 球的状态 - 摇动
   */
  int RUNNING = 1;

  /**
   * 球的状态 - 上传数据中
   */
  int UPLOADING = 2;

}
