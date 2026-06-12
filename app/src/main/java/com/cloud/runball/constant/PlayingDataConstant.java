package com.cloud.runball.constant;

public interface PlayingDataConstant {

  interface Update {
    // 状态 - 数据不完整
    String STATUS_UPDATE_INCOMPLETE = "0";

    // 状态 - 默认
    String STATUS_UPDATE_DEFAULT = "1";

    // 状态 - 上传中
    String STATUS_UPDATE_UPLOADING = "2";

    // 状态 - 上传成功
    String STATUS_UPDATE_SUCCESS = "3";

    // 状态 - 上传失败
    String STATUS_UPDATE_FAIL = "4";
  }

  interface PlayingSource {
    // 摇球打榜
    int RANKING = 1;

    // pk
    int PK = 2;

    // 摇加油
    int UPUP = 3;

    // 体协、赛事
    int MATCH = 4;

    // 随手摇
    int FREE_STYLE = 5;
  }

}
