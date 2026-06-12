package com.cloud.runball.service;

import com.cloud.runball.bean.CreteRoomResp;
import com.cloud.runball.bean.MonthDayDistanceInfo;
import com.cloud.runball.bean.UserInfo;
import com.cloud.runball.bean.banner.RankBannerData;
import com.cloud.runball.bean.yjy.YJYHelperRankModel;
import com.cloud.runball.model.AdModel;
import com.cloud.runball.model.BannerModel;
import com.cloud.runball.model.BasicResponse;
import com.cloud.runball.model.CheatModel;
import com.cloud.runball.model.ClanInfoModel;
import com.cloud.runball.model.ClanMemberModel;
import com.cloud.runball.model.ClanMemberRankModel;
import com.cloud.runball.model.ClanModel;
import com.cloud.runball.model.ClanRankingModel;
import com.cloud.runball.model.ClockInTarget;
import com.cloud.runball.model.DateRangeModel;
import com.cloud.runball.model.DeviceWithServerModel;
import com.cloud.runball.model.MatchRankDataModel;
import com.cloud.runball.model.MatchRankInfoModel;
import com.cloud.runball.model.MatchRankModel;
import com.cloud.runball.model.MatchStagesModel;
import com.cloud.runball.model.MedalInfoModel;
import com.cloud.runball.model.MineDataModel;
import com.cloud.runball.model.MinePkListV2Model;
import com.cloud.runball.model.MinePlayDataInfoModel;
import com.cloud.runball.model.MineRankingDetailsModel;
import com.cloud.runball.model.MobileUserInfoModel;
import com.cloud.runball.model.MonthDayDistanceInfoModel;
import com.cloud.runball.model.OtherMatchDetailModel;
import com.cloud.runball.model.OtherMatchModel;
import com.cloud.runball.model.OtherMatchModel2;
import com.cloud.runball.model.OthersInfoModel;
import com.cloud.runball.model.PKDataDetailModel;
import com.cloud.runball.model.PKDataRespModel;
import com.cloud.runball.model.PlayOverModel;
import com.cloud.runball.model.RankMatchDataRespModel;
import com.cloud.runball.model.RankMatchDetailModel;
import com.cloud.runball.model.RankModel;
import com.cloud.runball.model.RuleIntroduceModel;
import com.cloud.runball.model.ScoreDataModel;
import com.cloud.runball.model.ShakeMatchModel;
import com.cloud.runball.model.TodayDistanceSumModel;
import com.cloud.runball.model.UserImageModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.model.UserPkWinRateModel;
import com.cloud.runball.model.UserPlayDataModel;
import com.cloud.runball.model.UserPlayModel;
import com.cloud.runball.module.go.IntroduceModel;
import com.cloud.runball.module.match_football_association.entity.model.AssociationMatchRankModel;
import com.cloud.runball.module.match_football_association.entity.model.AssociationTeamDetailRankingModel;
import com.cloud.runball.module.match_football_association.entity.model.HotInfoModel;
import com.cloud.runball.module.match_football_association.entity.model.MatchDetailModel;
import com.cloud.runball.module.match_football_association.entity.model.MatchMenuListModel;
import com.cloud.runball.model.MinePlayStatisticsModel;
import com.cloud.runball.module.mine_record.entity.MinePlayDataInfo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WristBallServer {

  @GET("/posts")
  Observable<List<UserInfo>> getUsers();

  /**
   * 前端游客获取随机用户
   *
   * @param body
   * @return
   */
  @POST("/api/rand/user")
  Observable<UserInfoModel> autoLogin(@Body RequestBody body);

  /**
   * 获取用户信息
   * @return
   */
  @POST("/api/user/info")
  Observable<UserInfoModel> getUserInfo();

  /**
   * 用户主动修改个人信息
   * @param body
   * @return
   */
  @POST("/api/user/change")
  Observable<ResponseBody> modifyUserInfo(@Body RequestBody body);


  /**
   * 开始运动
   * @return
   */
  @POST("/api/start/play")
  Observable<UserPlayModel> startPlay(@Body RequestBody requestBody);

  /**
   * 开始运动
   * @return
   */
  @POST("/api/start/play")
  Observable<UserPlayModel> startPlayWithPK(@Body RequestBody requestBody);

  /**
   * 开始运动
   * @return
   */
  @POST("/api/start/play")
  Observable<UserPlayModel> startPlayForMatch(@Body RequestBody requestBody);

  /**
   * 运动过程中
   * @param body
   * @return
   */
  @POST("/api/between/play")
  Observable<ResponseBody> playing(@Body RequestBody body);

  /**
   * 运动完成
   * @param body
   * @return
   */
  @POST("/api/stop/play")
  Observable<PlayOverModel> playStop(@Body RequestBody body);


  /**
   * 获取作弊数据
   * @return
   */
  @POST("/api/play/cheat/data")
  Observable<CheatModel> cheat();

  /**
   * 单次运动明细
   *
   * @param body
   * @return
   */
  @POST("/api/play/info")
  Observable<UserPlayDataModel> playInfo(@Body RequestBody body);


  /**
   * 获取徽章
   *
   * @return
   */
  @POST("/api/my/medal")
  Observable<MedalInfoModel> getAllBadges();


  /**
   * 上传文件
   *
   * @param file
   * @return
   */
  @POST("/api/user/header/img/upload")
  @Multipart
  Observable<UserImageModel> uploadImage(@Part MultipartBody.Part file);

  /**
   * 清除个人成就
   * @return
   */
  @POST("/api/user/achievement/delete")
  Observable<ResponseBody> cleanScore();


  /**
   * 清除个人徽章
   *
   * @return
   */
  @POST("/api/user/medal/delete")
  Observable<ResponseBody> cleanBadges();


  /**
   * 我的成就
   *
   * @param body type stop_date
   * @return
   */
  @POST("/api/my/achievement")
  Observable<ScoreDataModel> getScore(@Body RequestBody body);


  /**
   * 我的数据
   *
   * @param body
   * @return
   */
  @POST("/api/my/play/data")
  Observable<MineDataModel> getMineData(@Body RequestBody body);


  /**
   * 获取时间范围
   *
   * @param body
   * @return
   */
  @POST("/api/play/date/range")
  Observable<DateRangeModel> getDataRange(@Body RequestBody body);

  /**
   * 查询设备列表
   *
   * @return
   */
  @POST("/api/my/device")
  Observable<List<DeviceWithServerModel>> getDevices();

  /**
   * 新增设备
   *
   * @param body
   * @return
   */
  @POST("/api/my/device/add")
  Observable<DeviceWithServerModel> addDevice(@Body RequestBody body);


  /**
   * 修改设备别名
   * @param body
   * @return
   */
  @POST("/api/my/device/update")
  Observable<ResponseBody> updateDevice(@Body RequestBody body);

  /**
   * 删除设备列表
   *
   * @param body
   * @return
   */
  @POST("/api/my/device/delete")
  Observable<ResponseBody> deleteDevices(@Body RequestBody body);


  /**
   * 获取房间号
   * @param body
   * @return
   */
  @POST("/api/pk/room/number")
  Observable<ResponseBody> requestRoomID(@Body RequestBody body);


  /**
   * 创建房间
   * @param body
   * @return
   */
  @POST("/api/pk/create/room")
  Observable<CreteRoomResp> createRoom(@Body RequestBody body);

  /**
   * 加入房间
   * @param body
   * @return
   */
  @POST("/api/pk/join/room")
  Observable<ResponseBody> addRoom(@Body RequestBody body);

  /**
   * 我的PK pk_type(PK类型，0：双人1V1， 1：团队PK ，默认 0) page limit
   * @param body
   * @return
   */
  @POST("/api/my/pk/list")
  Observable<PKDataRespModel> myPK(@Body RequestBody body);

  /**
   * 切换队伍  pk_room_id user_pk_list_id(用户PK 报名ID) new_user_group
   * @param body
   * @return
   */
  @POST("/api/pk/change/group")
  Observable<ResponseBody> changeGroup(@Body RequestBody body);

  /**
   * 取消PK
   * @param body user_pk_list_id  pk_room_id
   * @return
   */
  @POST("/api/pk/user/pk/list/delete")
  Observable<ResponseBody> deletePK(@Body RequestBody body);

  /**
   * 用户开始PK
   * @param body  pk_room_id  pk_status  user_group
   * @return
   */
  @POST("/api/pk/user/pk/start")
  Observable<ResponseBody> startPK(@Body RequestBody body);

  /**
   * 完成PK
   * @param body  pk_room_id user_group
   * @return
   */
  @POST("/api/pk/user/pk/stop")
  Observable<ResponseBody> finishPK(@Body RequestBody body);


  /**
   * PK数据详情  pk_room_id
   * @param body
   * @return
   */
  @POST("/api/my/pk/list/info")
  Observable<PKDataDetailModel> myPKInfo(@Body RequestBody body);

  /**
   * PK数据详情  pk_room_id
   * @param body
   * @return
   */
  @POST("/api/my/pk/list/info")
  Observable<ResponseBody> myTeamPKInfo(@Body RequestBody body);

  /**
   * 手机登录
   * @param body
   * @return
   */
  @POST("/api/login/phone")
  Observable<MobileUserInfoModel> login(@Body RequestBody body);

  /**
   * 发送短信
   * @param body
   * @return
   */
  @POST("/api/sent/msg")
  Observable<ResponseBody> loginSendSms(@Body RequestBody body);

  /**
   * 发送邮箱验证
   * @param body
   * @return
   */
  @POST("/api/sent/email")
  Observable<ResponseBody> loginSendEmail(@Body RequestBody body);

  /**
   * 邮箱登陆
   * @param body
   * @return
   */
  @POST("/api/login/email")
  Observable<MobileUserInfoModel> loginByEmail(@Body RequestBody body);

  /**
   * 三分钟运动提交
   * @param body
   * @return
   */
  @POST("/api/play/thrmin")
  Observable<ResponseBody> thrMin(@Body RequestBody body);

  /**
   * 运动半马用时
   * @param body
   * @return
   */
  @POST("/api/play/half/marathon")
  Observable<ResponseBody> maraThon(@Body RequestBody body);


  /**
   * 运动马用时
   * @param body
   * @return
   */
  @POST("/api/play/marathon")
  Observable<ResponseBody> maraThonFull(@Body RequestBody body);

  /**
   * 获取用户摇跑指数
   * @return
   */
  //@POST("/api/runball/exponent/v2")
  //Observable<ResponseBody> exponent();


  /**
   * 获取用户摇跑指数
   * @return
   */
  @POST("/api/runball/exponent/v3")
  Observable<ResponseBody> exponent3();

  /**
   * 摇跑指数分子
   * @return
   */
  @POST("/api/play/exponent/molecular")
  Observable<ResponseBody> molecular(@Body RequestBody body);

  /**
   * 摇跑指数分母
   * @return
   */
  @POST("/api/play/exponent/denominator")
  Observable<ResponseBody> denominator(@Body RequestBody body);

  /**
   * 用户运动异常
   * @param body
   * @return
   */
  @POST("/api/play/abnormal")
  Observable<ResponseBody> abnormal(@Body RequestBody body);

  /**
   * 赛事类型
   * @return
   */
  @POST("/api/match/event/list")
  Observable<ResponseBody> matchTypeList();

  /**
   * 榜单列表 （榜单类型  exponent：活力指数，molecular：摇跑一分钟）
   * @param body
   * @return
   */
  @POST("/api/my/ranking/v2")
  Observable<MatchRankDataModel> matchRanking(@Body RequestBody body);


  /**
   * 赛事列表
   * @return
   */
  @POST("/api/match/list")
  Observable<RankMatchDataRespModel> matchList(@Body RequestBody body);

  /**
   * 赛事详情
   * @return
   */
  @POST("/api/match/info")
  Observable<RankMatchDetailModel> matchInfo(@Body RequestBody body);



  /**
   * 个人赛，团队赛
   * @param body
   * @return
   */
  @POST("/api/my/match")
  Observable<MatchRankInfoModel> matchListWithGroups(@Body RequestBody body);

  /**
   * 根据赛事ID获取赛段列表
   * @param body
   * @return
   */
  @POST("/api/my/match/stage")
  Observable<MatchStagesModel> matchStageInfo(@Body RequestBody body);

  /**
   * 赛事榜单
   * @param body
   * @return
   */
  @POST("/api/my/match/info")
  Observable<MatchRankModel> matchStageMatchlist(@Body RequestBody body);


  /**
   * 根据团队编号查询团队
   * @param body
   * @return
   */
  @POST("/api/match/user/group")
  Observable<ResponseBody> matchGroup(@Body RequestBody body);

  /**
   * 报名
   * @param body
   * @return
   */
  @POST("/api/match/user/sign")
  Observable<ResponseBody> matchSign(@Body RequestBody body);

  /**
   * 比赛页面查询基本信息(摇跑赛红旗中需加上show_all 是否显示所有用户信息)
   * @param body RankMatchInfo
   * @return
   */
  @POST("/api/match/befor/play")
  Observable<ResponseBody> rankMatchInfo(@Body RequestBody body);

  /**
   * Banner数据
   * @return
   */
  @POST("/api/match/banner/list")
  Observable<BannerModel> banner();


  /**
   * 获取国际码
   * @return
   */
  @POST("/api/overseas/code")
  Observable<ResponseBody> requestSmsCode();

  /**
   * 获取榜单类型
   * @return
   */
  @POST("/api/ranking/list")
  Observable<ResponseBody> requestRankTypes();


  /**
   * 上传摇跑指数
   * @param body
   * @return
   */
  @POST("/api/runball/exponent/add")
  Observable<ResponseBody> exponentAdd(@Body RequestBody body);

  /**
   * 开屏广告页图片
   * @return
   */
  @POST("/api/app/advertising")
  Observable<AdModel> advertising();

  /**
   * 请求升级  android_code
   * @return
   */
  @POST("/api/android/version/check")
  Observable<ResponseBody> checkupdate(@Body RequestBody body);

  /**
   * 排行榜列表
   * @return
   */
  @POST("/api/my/ranking/list")
  Observable<RankModel> rankList();

  /**
   * 删除榜单列表
   * @return
   */
  @POST("/api/my/ranking/del")
  Observable<ResponseBody> deleteRankList(@Body RequestBody body);

  /**
   * 添加榜单列表
   * @return
   */
  @POST("/api/my/ranking/add")
  Observable<ResponseBody> rankListAdd(@Body RequestBody body);

  /**
   * 摇加油首页数据
   * @return
   */
  @POST("/api/shake/index")
  Observable<OtherMatchModel> shakeData();

  /**
   * 摇加油历史数据
   * @param body
   * @return
   */
  @POST("/api/my/shake/list")
  Observable<OtherMatchModel2> shakeHistoryData(@Body RequestBody body);

  /**
   * 记录详情
   * @param body
   * @return
   */
  @POST("/api/my/shake/info")
  Observable<OtherMatchDetailModel> shakeHistoryDetailData(@Body RequestBody body);

  /**
   * 获取助力排行
   * @param body
   * @return
   */
  @POST("/api/my/shake/getMyShakeBoostRanking")
  Observable<YJYHelperRankModel> getMyShakeBoostRanking(@Body RequestBody body);


  /**
   * 获取助力排行详情
   * @param body
   * @return
   */
  @POST("/api/my/shake/getMyShakeHelpDetail")
  Observable<YJYHelperRankModel> getMyShakeHelpDetail(@Body RequestBody body);

  /**
   * 获得摇跑赛规则
   * @return
   */
  @POST("/api/shake/rule")
  Observable<ResponseBody> shakeRuleData();

  /**
   * 获取赛事详情
   * @return
   */
  @POST("/api/shake/info")
  Observable<ShakeMatchModel> shakeMatchData();

  /**
   * 报名
   * @param body
   * @return
   */
  @POST("/api/shake/sign")
  Observable<ResponseBody> shakeMatchSignUp(@Body RequestBody body);

  /**
   * 更新用户设备与系统信息
   * @param body
   * @return
   */
  @POST("/api/user/system/change")
  Observable<ResponseBody> changeUserDeviceInfo(@Body RequestBody body);

  /**
   * 本地摇球数据上传
   * @param body
   * @return
   */
  @Multipart
  @POST("/api/local/play/upload")
  Observable<ResponseBody> localPlayUpload(@Part MultipartBody.Part body);

  /**
   * go介绍接口
   * @param body
   * @return
   */
  @POST("/api/common/setting/setGoIntroduce")
  Observable<IntroduceModel> getGoIntroduce(@Body RequestBody body);

  /**
   * 赛事列表接口 v2
   * @param body
   * @return
   */
  @POST("/api/v2/match/list")
  Observable<MatchMenuListModel> getMatchListV2(@Body RequestBody body);

  /**
   * 赛事详情接口 v2
   * @param body
   * @return
   */
  @POST("/api/v2/match/info")
  Observable<MatchDetailModel> getMatchDetailV2(@Body RequestBody body);

  /**
   * 获取GO页面赛事hot轮播 v2
   * @param body
   * @return
   */
  @POST("/api/v2/match/getGoMatchHot")
  Observable<HotInfoModel> getGoMatchHotV2(@Body RequestBody body);

  /**
   * 查看个人排行榜接口 v2
   */
  @POST("/api/v2/match/personal/leaderboard")
  Observable<AssociationMatchRankModel> getMatchPersonalLeaderboard(@Body RequestBody body);

  /**
   * 查看团队标签列表排行榜 v2
   */
  @POST("/api/v2/match/teamList/leaderboard")
  Observable<AssociationMatchRankModel> getMatchTeamLeaderboard(@Body RequestBody body);

  /**
   * 获取报名团队标签 v2
   */
  @POST("/api/v2/match/getSignUpTeamTag")
  Observable<ResponseBody> getSignUpTeamTagV2(@Body RequestBody body);

  /**
   * 用户报名 v2
   */
  @POST("/api/v2/match/user/sign")
  Observable<ResponseBody> matchUserSignV2(@Body RequestBody body);

  /**
   * 用户取消报名
   */
  @POST("/api/v2/match/user/sign/out")
  Observable<ResponseBody> matchUserSignOutV2(@Body RequestBody body);

  /**
   * 查看团队标签详情排行榜
   * @param body
   * @return
   */
  @POST("/api/v2/match/teamDetails/leaderboard")
  Observable<AssociationTeamDetailRankingModel> getMatchTeamDetailsLeaderboard(@Body RequestBody body);

//  /**
//   * 摇跑数据上传+队列
//   * @param body
//   * @return
//   */
//  @POST("https://qyd.hisport.cloud/api/v2/match/postUploadLocalPlayV3")
//  Observable<ResponseBody> uploadLocalMatchPlayDefault(@Body RequestBody body);

//  /**
//   * 赛事本地数据json文件上传
//   */
//  @Multipart
//  @POST("/api/v2/match/postUploadLocalPlayV2")
//  Observable<ResponseBody> uploadLocalMatchPlayV2(@Part MultipartBody.Part body);

  /**
   * 摇跑数据上传+队列
   * @return
   */
  @POST("/api/v2/match/postUploadLocalPlayV3")
  Observable<Object> uploadLocalMatchPlayV3(@Body RequestBody data);

  /**
   * 获取banner
   * @param body
   * @return
   */
  @POST("/api/banner/getBannerList")
  Observable<List<RankBannerData>> getBannerList(@Body RequestBody body);

//  /**
//   *
//   * @return
//   */
//  @POST("/api/v2/match/getPalyUrl")
//  Observable<UpLoadInfoModel> getPlayUrl();

  /**
   * 我的记录-摇跑模式统计
   * @return
   */
  @POST("/api/my/play/statistics")
  Observable<MinePlayStatisticsModel> getMinePlayStatistics();

  /**
   * 我的打榜详情
   * @return
   */
  @POST("/api/my/rankingDetails")
  Observable<MineRankingDetailsModel> getMineRankingDetails();

  /**
   * 我的PK列表v2
   * @return
   */
  @POST("/api/v2/my/pk/list")
  Observable<MinePkListV2Model> getMinePkListV2(@Body RequestBody body);

  /**
   * 统计用户今天的总运动距离
   * @return
   */
  @POST("/api/my/day/getDistanceSum")
  Observable<TodayDistanceSumModel> getTodayDistanceSum();

  /**
   * 我的运动数据 - v2
   * @param body
   * @return
   */
  @POST("/api/v2/my/play/data")
  Observable<MinePlayDataInfoModel> getMinePlayDataV2(@Body RequestBody body);

  /**
   * 统计用户指定月份的每天总运动距离
   * @param body
   * @return
   */
  @POST("/api/my/month/getMonthDistanceSum")
  Observable<MonthDayDistanceInfoModel> getMonthDayDistanceSum(@Body RequestBody body);

  /**
   * 获取用户所有打卡目标
   */
  @POST("/api/my/getTargetPunch")
  Observable<List<ClockInTarget>> getTargetPunch(@Body RequestBody body);

  /**
   * 添加/编辑用户打卡目标
   */
  @POST("/api/my/writeTargetPunch")
  Observable<ResponseBody> editTargetPunch(@Body RequestBody body);

  /**
   * 获取用户PK胜率
   * @return
   */
  @POST("/api/my/getUserPkWinRate")
  Observable<UserPkWinRateModel> getUserPkWinRate();

  /**
   * 获取打榜模式简介与PK模式简介
   * @return
   */
  @POST("/api/introduce/getRuleIntroduce")
  Observable<RuleIntroduceModel> getRuleIntroduce();

  /**
   * 注销账号
   * @return
   */
  @POST("/api/my/accountCancel")
  Observable<ResponseBody> accountCancel();

  /**
   * 获取俱乐部列表
   * @param body
   * @return
   */
  @POST("/api/clan/getClanList")
  Observable<ClanModel> getClanList(@Body RequestBody body);

  /**
   * 申请俱乐部
   * @param body
   * title
   * clan_avatar
   * address
   * introduction
   * telephone
   * remark
   * @return
   */
  @POST("/api/clan/postApplyTeam")
  Observable<Boolean> registerClan(@Body RequestBody body);

  /**
   * 编辑俱乐部
   * @param body
   * id
   * title
   * clan_avatar
   * address
   * introduction
   * telephone
   * @return
   */
  @POST("/api/clan/editUserClan")
  Observable<Boolean> editClanInfo(@Body RequestBody body);

  /**
   * 申请加入俱乐部
   * @param body
   * @return
   */
  @POST("/api/clan/postApplyJoinClan")
  Observable<Boolean> postApplyJoinClan(@Body RequestBody body);


  /**
   * 获取俱乐部排行榜
   * @param body
   * @return
   */
  @POST("/api/clan/getClanRankingList")
  Observable<ClanRankingModel> getClanRankingList(@Body RequestBody body);

  /**
   * 获取俱乐部详情
   * @param body
   * @return
   */
  @POST("/api/clan/getUserClanInfo")
  Observable<ClanInfoModel> getUserClanInfo(@Body RequestBody body);

  /**
   * 根据俱乐部ID获取俱乐部成员或待审核成员
   * @param body
   * @return
   */
  @POST("/api/clan/getClanMemberList")
  Observable<ClanMemberModel> getClanMemberList(@Body RequestBody body);

  /**
   * 取消申请加入俱乐部
   * @param body
   * @return
   */
  @POST("/api/clan/postUnJoinClan")
  Observable<Boolean> postUnJoinClan(@Body RequestBody body);

  /**
   * 取消申请俱乐部
   * @param body
   * @return
   */
  @POST("/api/clan/withdrawClan")
  Observable<Boolean> withdrawClan(@Body RequestBody body);

  /**
   * 队长审核俱乐部成员
   * @param body
   * @return
   */
  @POST("/api/clan/postReviewApplyClanMember")
  Observable<Boolean> postReviewApplyClanMember(@Body RequestBody body);

  /**
   * 移交队长
   * @param body
   * @return
   */
  @POST("/api/clan/postHandoverClanLeader")
  Observable<Boolean> postHandoverClanLeader(@Body RequestBody body);

  /**
   * 获取俱乐部成绩详情
   * @param body
   * @return
   */
  @POST("/api/clan/getClanDetailsList")
  Observable<ClanMemberRankModel> getClanMemberScoreList(@Body RequestBody body);

  /**
   * 获取他人资料
   * @param body
   * @return
   */
  @POST("/api/clan/getUserOthersInfo")
  Observable<OthersInfoModel> getUserOthersInfo(@Body RequestBody body);

  /**
   * 删除/退出俱乐部成员
   * @param body
   * @return
   */
  @POST("/api/clan/delUserClanMember")
  Observable<Object> delUserClanMember(@Body RequestBody body);

  /**
   * 上传通用图片文件
   * @param file
   * @return
   */
  @POST("/api/images/upload")
  @Multipart
  Observable<UserImageModel> commonUploadImage(@Part MultipartBody.Part file);

}
