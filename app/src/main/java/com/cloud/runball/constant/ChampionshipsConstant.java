package com.cloud.runball.constant;

/**
 * 竞标赛常量
 */
public interface ChampionshipsConstant {

  // 赛事状态 - 未开始
  int MATCH_STATUS_NOT_STARTED = 1;

  // 赛事状态 - 进行中
  int MATCH_STATUS_PLAYING = 2;

  // 赛事状态 - 已结束
  int MATCH_STATUS_FINISH = 3;


  // 赛事报名条件 - 开放报名
  int JOIN_STATUS_PUBLIC = 0;

  // 赛事报名条件 - 关闭报名
  int JOIN_STATUS_CLOSE = 1;

  // 赛事报名条件 - 允许会员报名
  int JOIN_STATUS_MEMBER = 2;


  // 用户是否参加赛事状态 - 未参加
  int USER_JOIN_STATUS_NO = 0;

  // 用户是否参加赛事状态 - 已参加
  int USER_JOIN_STATUS_YES = 1;


  // 赛事类型 - 没有
  int TYPE_NO = 0;

  // 赛事类型 - 摇跑赛
  int TYPE_RANKING = 1;

  // 赛事类型 - 摇加油赛
  int TYPE_YJY = 2;


}
