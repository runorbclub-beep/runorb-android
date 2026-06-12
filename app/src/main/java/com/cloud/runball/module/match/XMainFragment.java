package com.cloud.runball.module.match;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.cloud.runball.App;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.bean.MedalInfo;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.dialog.AutoBadgesDialog;
import com.cloud.runball.dialog.CommonDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.ErrSpeed;
import com.cloud.runball.model.PlayOverModel;
import com.cloud.runball.model.RankMatchInfo2;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.model.UserPlayModel;
import com.cloud.runball.module.WristBallActivity;
import com.cloud.runball.module.home.AddDeviceInfoActivity;
import com.cloud.runball.module.mine.BadgeActivity;
import com.cloud.runball.module.mine.MineBadgeActivity;
import com.cloud.runball.module.race.MatchMainActivity;
import com.cloud.runball.module_bluetooth.constant.ServiceNoticeConstant;
import com.cloud.runball.module_bluetooth.data.event.BallInfo;
import com.cloud.runball.module_bluetooth.data.event.BallRunDetail;
import com.cloud.runball.module_bluetooth.data.event.ServiceNoticeEvent;
import com.cloud.runball.module_bluetooth.utils.BleUtils;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.BallUtils;
import com.cloud.runball.utils.Constants;
import com.cloud.runball.utils.DeviceUtils;
import com.cloud.runball.utils.SpeechUtils;
import com.cloud.runball.widget.MagicTextView2;
import com.cloud.runball.widget.PointerImageView;
import com.cloud.runball.widget.SpeedCircleImageView;
import com.google.gson.Gson;
import com.littlejie.circleprogress.CircleProgress;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: XMainFragment
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/7 9:52
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/7 9:52
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class XMainFragment extends Fragment implements View.OnClickListener, AutoBadgesDialog.DismissCallBack{
  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
  public static final int REQUEST_CODE = 100;
  public static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
  public static final int ACTION_REQUEST_ENABLE = 3;
  //间隔多少秒上报一次数据
  public static final int UPDATE_DATA_INTERVAL = 5;

  //最高转速
  TextView tvTurnHeightSpeedRPM;
  //距离
  TextView tvTurnDistance;
  //时间
  TextView tvTurnTime;

  LinearLayout lyNoticeIndex;

  TextView tvNoticeTip;

  SpeedCircleImageView ivSpeedCircle;
  PointerImageView ivPointer;

  //大圆盘显示转速
  MagicTextView2 tvSpeedRPMFormat;

  TextView tvProgressDistance;
  ProgressBar progressBar;
  TextView tvRank;


  //运动id+开始运动时间
  long user_play_id = 0;

  int start_time = 0;

  //本地保存的转速，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
  List<Integer> speedCache = new ArrayList<>();

  //本地保存的总圈数，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
  List<Integer> circleCache = new ArrayList<>();

  //格式化数字
  DecimalFormat mDecimalFormat = new DecimalFormat("0.000");

  /**
   * 真实运动时间
   */
  int mRealKeepPlayTime=0;


  int mKeepPlayTime = 0;
  /**
   * 最高转速
   */
  int mHighSpeedRPM = 0;

  /**
   * 当前转速
   */
  int mRpmSpeed=0;

  /**
   * 总圈数
   */
  int mTotalCircle = 0;

  /**
   * 总圈数不变次数
   */
  int comCircleCount = 0;

  CircleProgress circle_progress_bar;
  LinearLayout lyAction;
  TextView tvTip;
  TextView tvAction;
  LinearLayout lyInfo;

  //分子(指定时间)
  int mExponent_molecular = 0;
  //分母(指定距离)
  float mExponent_denominator = 0;

  //时间提示
  String exponent_molecular_tips_en="";
  String exponent_molecular_tips_zh="";

  //距离提示
  String exponent_denominator_tips_en="";
  String exponent_denominator_tips_zh="";

  /**
   * 运动数据是否异常
   */
  int is_abnormal=0;

  TextView tvPower;

  Boolean[] err_speedsTarget = new Boolean[]{ false, false, false, false, false, false,false, false, false, false };

  Handler mHandler = new Handler();

  private String sys_match_id;
  private String matchs_stage_id;
  private String user_group_id;

  //是否停止比赛
  public boolean isStopMatch=false;

  //重复请求user_play_id 次数
  int eStartPlayCount=0;

  private final CompositeDisposable disposable = new CompositeDisposable();

  public static XMainFragment newInstance() {
    return new XMainFragment();
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_xmain, null);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    init(view);
    initMainData();
  }

  /**
   * 暂停时保存时间
   */
  int lastKeepTime = (int)(System.currentTimeMillis() / 1000);

  @Override
  public void onResume() {
    super.onResume();
    reloadUserInfo();
    showSnackBarChanged();
  }

  @Override
  public void onPause() {
    super.onPause();
    //暂停时候保存时间
    lastKeepTime=(int)(System.currentTimeMillis()/1000);
  }

  @Override
  public void onStop() {
    super.onStop();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
//    stopScheduleCountDown();
    speedCache.clear();
    circleCache.clear();
    mHandler.removeCallbacksAndMessages(null);
    EventBus.getDefault().unregister(this);

    disposable.dispose();
  }

  private void init(View root) {
    lyNoticeIndex = root.findViewById(R.id.lyNoticeIndex);

    tvNoticeTip = root.findViewById(R.id.tvNoticeTip);

    lyAction = root.findViewById(R.id.lyAction);
    lyAction.setOnClickListener(this);
    tvTip = root.findViewById(R.id.tvTip);
    tvAction = root.findViewById(R.id.tvAction);
    tvAction.setOnClickListener(this);

    tvTurnHeightSpeedRPM = root.findViewById(R.id.tvTurnHeightSpeedRPM);
    tvTurnDistance = root.findViewById(R.id.tvTurnDistance);
    tvTurnTime = root.findViewById(R.id.tvTurnTime);

    ivSpeedCircle = root.findViewById(R.id.ivSpeedCircle);
    ivPointer = root.findViewById(R.id.ivPointer);

    tvSpeedRPMFormat = root.findViewById(R.id.tvSpeedRPMFormat);
    circle_progress_bar = root.findViewById(R.id.circle_progress_bar);

    tvProgressDistance = root.findViewById(R.id.tvProgressDistance);
    progressBar = root.findViewById(R.id.progressBar);

    tvRank = root.findViewById(R.id.tvRank);

    tvPower = root.findViewById(R.id.tvPower);
  }

  /**
   * 显示进度条
   * @param visibility
   */
  public void showProgress(int visibility) {
    tvProgressDistance.setVisibility(visibility);
    progressBar.setVisibility(visibility);
  }


  public void setRankMatchParams(String sys_match_id, String matchs_stage_id, String user_group_id) {
    this.sys_match_id = sys_match_id;
    this.matchs_stage_id = matchs_stage_id;
    this.user_group_id = user_group_id;
  }

  private ScheduledExecutorService countDownExecutor = Executors.newScheduledThreadPool(1);

  private int residue_time=0;

  /**
   * 团队比赛排名
   * @param msg
   */
  public void showRandMatchVisibly(String msg, String distance, int progress,int residueTime) {
    if(TextUtils.isEmpty(user_group_id)){
      tvRank.setText(String.format(getString(R.string.my_ranks_1), msg));
    }else{
      tvRank.setText(String.format(getString(R.string.my_ranks), msg));
    }

    String str = String.format(getString(R.string.my_ranks_progress_reach), String.valueOf(progress), distance);
    tvProgressDistance.setText(str);
    progressBar.setProgress(progress);
    this.residue_time=residueTime;
  }

  private void showRandMatchVisibly(String msg, String distance, int progress) {
    if(TextUtils.isEmpty(user_group_id)){
      tvRank.setText(String.format(getString(R.string.my_ranks_1), msg));
    }else {
      tvRank.setText(String.format(getString(R.string.my_ranks), msg));
    }
    String str = String.format(getString(R.string.my_ranks_progress_reach), String.valueOf(progress), distance);
    tvProgressDistance.setText(str);
    progressBar.setProgress(progress);
  }

  public void showRandMatchVisibly() {
    tvRank.setVisibility(View.VISIBLE);
  }

//  public void stopScheduleCountDown(){
//    if(countDownExecutor!=null){
//      try {
//        countDownExecutor.shutdown();
//        if (!countDownExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
//          // 超时的时候向线程池中所有的线程发出中断(interrupted)。
//          countDownExecutor.shutdownNow();
//        }
//      }catch (InterruptedException e){
//        countDownExecutor.shutdownNow();
//      }
//    }
//  }

  boolean residueTimeStop=false;

//  /**
//   * 比赛结束倒计时+检测比赛信息结果
//   */
//  public void startScheduleCountDown(){
//    if(countDownExecutor!=null){
//      countDownExecutor.scheduleAtFixedRate(() -> {
//        if(!residueTimeStop){
//          if(residue_time>0){
//            residue_time-=1;
//          }
//          mKeepPlayTime+=1;
//          EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_PLAY_TIME_2));
//          if(residue_time%3==0 && user_play_id==0){
//            requestRankMatchBase(sys_match_id, user_group_id);
//          }
//        }
//      },3,1, TimeUnit.SECONDS);
//    }
//  }

  /**
   * 查询比赛基本信息
   * @param sys_match_id
   * @param user_group_id
   */
  private void requestRankMatchBase(String sys_match_id,String user_group_id){
    HashMap<String, Object> map = new HashMap<>(3);
    map.put("sys_match_id", sys_match_id);
    map.put("show_all", 0);
    map.put("user_group_id", user_group_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.rankMatchInfo(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try{
              parseRankMatchResp(responseBody);
            }catch (JSONException ex){
              ex.printStackTrace();
            }catch (Exception ex){
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("---------requestRankMatchBase-------"+msg);
          }
        })
    );
  }

  /**
   * 倒计时定时查询比赛是否结束
   * @param responseBody
   * @throws IOException
   * @throws JSONException
   */
  private void parseRankMatchResp(ResponseBody responseBody) throws IOException, JSONException {
    JSONObject jObject = new JSONObject(responseBody.string());
    int code = jObject.optInt("code", -1);
    if (code == 1) {
      JSONObject dataObject=jObject.optJSONObject("data");
      if(dataObject.optInt("code")==0 && !residueTimeStop){
        residueTimeStop=true;
        //弹框退出
        String msg=dataObject.optString("msg");
        showExitDialog(getString(R.string.tip),msg);
      }else if(dataObject.optInt("code")==1){
        Gson gson=new Gson();
        AppLogger.d("----parseRankMatchResp----"+dataObject.toString());
        JSONObject resultObject = new JSONObject(dataObject.toString());
        if(resultObject.optInt("is_end",0)==1 && !residueTimeStop){
          //这里查询判断会有问题，不建议通过查询来判断
          residueTimeStop = true;
          showDialog(getString(R.string.tip), resultObject.optString("matchs_end_tips"));
        }else {
          RankMatchInfo2 rankMatchInfo = gson.fromJson(dataObject.toString(), RankMatchInfo2.class);
          int progress=(int)(Math.floor(rankMatchInfo.getDistance_percentage()*100));
          showRandMatchVisibly(rankMatchInfo.getRanking(),rankMatchInfo.getDistance_poor(),progress);
          //设置比赛主页面参数
          if (rankMatchInfo.getIs_end() == 1 && !residueTimeStop) {

            residueTimeStop = true;
            showDialog(getString(R.string.tip), rankMatchInfo.getMatchs_end_tips());
          }
        }
      }
    }
  }

  /**
   * 退出
   * @param title
   * @param message
   */
  public void showExitDialog(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.btn_cancel,null);
    builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        onExitMatch();
        if (getActivity() instanceof RankMatchMainActivity) {
          ((RankMatchMainActivity) getActivity()).finish();
        }
      }
    });
    builder.show();
  }

  private void reloadUserInfo() {
    if (AppDataManager.getInstance().getUserInfoModel() == null) {
      String token = String.valueOf(SPUtils.get(getActivity(), "token", ""));
      if (TextUtils.isEmpty(token) || token.equalsIgnoreCase("null")) {
        autoLogin();
      } else {
        WristBallRetrofitHelper.getInstance().updateToken(token);
        requestUserInfo();
      }
    }else{
      EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_RANK_MATCH));
    }
  }

  /**
   * 自动登录
   */
  private void autoLogin() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_country", AppDataManager.getInstance().getCountry());
    map.put("device_uid", AppDataManager.getInstance().getAndroidId());
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserInfoModel> observable = apiServer.autoLogin(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
          @Override
          public void onSuccess(UserInfoModel userInfoModel) {
            //把token保存起来
            if (userInfoModel != null) {
              AppLogger.d("---autoLogin--token=" + userInfoModel.getUser_info().getToken());
              SPUtils.put(getContext(), "token", userInfoModel.getUser_info().getToken() + "");
              AppDataManager.getInstance().setUserInfoModel(userInfoModel);
              WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken() + "");
              AppLogger.d(userInfoModel.getUser_info().toString());
            }
          }

          @Override
          public void onError(int code, String msg) {
            //Logger.d(msg);
          }
        })
    );
  }

  private void requestUserInfo() {
    AppLogger.d("--------autoLogin----------" + AppDataManager.getInstance().getAndroidId());
    Observable<UserInfoModel> observable = apiServer.getUserInfo();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
          @Override
          public void onSuccess(UserInfoModel userInfoModel) {
            AppLogger.d("--XMainFragment--获取个人信息成功----" + userInfoModel);
            AppDataManager.getInstance().setUserInfoModel(userInfoModel);
            //把token保存起来
            WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
            SPUtils.put(getActivity(), "token", userInfoModel.getUser_info().getToken());
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_RANK_MATCH));
            adjustPkMatch();
          }

          @Override
          public void onError(int code, String msg) {
            Logger.d(msg);
            if (code == 2) {
              WristBallRetrofitHelper.getInstance().updateToken(null);
              autoLogin();
            }
          }
        })
    );
  }

  private void adjustPkMatch() {
    if (getActivity() instanceof WristBallActivity) {
      //对比开赛时间
      int cuTime = (int) (System.currentTimeMillis() / 1000);
      int startTime = (int) SPUtils.get(getActivity(), "pkdata_startTime", cuTime);
      int stopTime = (int) SPUtils.get(getActivity(), "pkdata_stopTime", cuTime);

      if (cuTime >= stopTime) {
        SPUtils.remove(getActivity(), "pkdata");
        SPUtils.remove(getActivity(), "pkdata_startTime");
        SPUtils.remove(getActivity(), "pkdata_stopTime");
        SPUtils.remove(getActivity(), "pkdata_keepPlayTime");
        return;
      }

      String pkdata = (String) SPUtils.get(getActivity(), "pkdata", "");
      if (!TextUtils.isEmpty(pkdata) && pkdata.startsWith("{") && pkdata.endsWith("}")
          && !"游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())) {
        startMatchMainActivity(pkdata);
      }
    }
  }

  private void startMatchMainActivity(String pk_info) {
    Intent it = new Intent(getActivity(), MatchMainActivity.class);
    it.putExtra("pkdata", pk_info);
    startActivity(it);
  }

  private void stopRotateAnim() {
    ivSpeedCircle.stop();
  }

  /**
   * 表盘数据
   *
   * @param currentRpm
   * @param anim
   */
  private void setSpeedRPMBoardAnim(int currentRpm, boolean anim) {
    //百分比
    circle_progress_bar.setValue(BallUtils.getPercentWithSpeedRPM(currentRpm), anim);
    //角度
    ivPointer.setValue(BallUtils.getAngleWithSpeedRPM(currentRpm), anim);
    //显示转速
    tvSpeedRPMFormat.setValue(currentRpm, anim, false);

    //红色外圈
    ivSpeedCircle.setValue(BallUtils.getAngleWithSpeedRPM(currentRpm));
  }

  /**
   * 表盘数据
   */
  private void initSpeedRPMBoardAnim() {
    //百分比
    circle_progress_bar.initValue(0);
    //角度
    ivPointer.initValue(0);
    //显示转速
    tvSpeedRPMFormat.initValue(0);
    //红色外圈
    ivSpeedCircle.setValue(0);
  }

  /**
   * 表盘数据
   *
   * @param currentRpm
   * @param anim
   * @param stop
   */
  private void setSpeedRPMBoardAnim(int currentRpm, boolean anim, boolean stop) {
    //百分比
    circle_progress_bar.setValue(BallUtils.getPercentWithSpeedRPM(currentRpm), anim);
    //角度
    ivPointer.setValue(BallUtils.getAngleWithSpeedRPM(currentRpm), anim);
    //显示转速
    tvSpeedRPMFormat.setValue(currentRpm, anim, stop);
    //红色外圈
    ivSpeedCircle.setValue(BallUtils.getAngleWithSpeedRPM(currentRpm));
  }

  /**
   * 更新运动数据
   *
   * @param maxRpm      最高转速
   * @param totalCircle 累计圈数(单位千米)
   */
  public void setPlayingBoard(int maxRpm, int totalCircle) {
    //最高速率
    tvTurnHeightSpeedRPM.setText(String.valueOf(maxRpm));
    //直径为5.28cm ，周长16.588cm,单位 km
    float meter = BallUtils.getTotalMeter(totalCircle) / 1000;
    tvTurnDistance.setText(mDecimalFormat.format(meter));
    playVoice(meter* 1000);
    noticeTip(meter * 1000, totalCircle);
  }

  /**
   * 根据不同距离提示不同声音
   *
   * @param meter
   */
  private void playVoice(float meter) {

  }

  //提示
  Boolean[] tips = new Boolean[]{false, false, false};
  //上传指定时间和距离
  Boolean[] updateTips = new Boolean[]{false, false};

  float mMeter=0.0f;

  /**
   * Tip提示
   * @param m_meter     米
   * @param totalCircle 圈胡
   */
  private void noticeTip(float m_meter, int totalCircle) {
    //半马
    if (m_meter >= 42.195 * 1000 / 2 && !tips[1]) {

      String tipShow=String.format(getString(R.string.tv_run_notice_tip_2), TimeUtils.formatDurationFull(mKeepPlayTime));

      tips[1] = true;
      tvNoticeTip.setVisibility(View.VISIBLE);
      tvNoticeTip.setText(tipShow);
      //半马数据提交
      requestMarathon(mKeepPlayTime);

      //语音
      if(isZhCn()){
        tipShow=tipShow.replace("s","秒");
      }else{
        tipShow=tipShow.replace("s","second");
      }
      SpeechUtils.getInstance(getContext()).speakText(tipShow);
    }

    //全马
    if (m_meter >= 42.195 * 1000 && !tips[2]) {
      String tipShow=String.format(getString(R.string.tv_run_notice_tip_3), TimeUtils.formatDurationFull(mKeepPlayTime));
      tips[2] = true;
      tvNoticeTip.setVisibility(View.VISIBLE);
      tvNoticeTip.setText(tipShow);
      //全马数据提交
      requestMarathonFull(mKeepPlayTime);

      //语音
      if(isZhCn()){
        tipShow=tipShow.replace("s","秒");
      }else{
        tipShow=tipShow.replace("s","second");
      }
      SpeechUtils.getInstance(getContext()).speakText(tipShow);
    }

    //新增指定时间，指定距离提交数据
    if (mKeepPlayTime >= mExponent_molecular && mExponent_molecular != 0 && mKeepPlayTime <= (mExponent_molecular + 10) && !updateTips[0]) {
      updateTips[0] = true;
      mMeter=m_meter;
      tvNoticeTip.setVisibility(View.VISIBLE);
      String msg=mDecimalFormat.format(m_meter/1000);
      String tipShow="";
      //时间提示
      if(isZhCn()){
        tipShow=exponent_molecular_tips_zh+"，"+msg+" km";
      }else{
        tipShow=exponent_molecular_tips_en+","+msg+" km";
      }
      tvNoticeTip.setText(tipShow);
      //摇跑指定时间数据提交
      requestThirdReplaceTime(totalCircle,user_play_id);
      //语音,km中英文需要转化
      tipShow=tipShow.replace("km",getString(R.string.lbl_km_unit));
      SpeechUtils.getInstance(getContext()).speakText(tipShow);
    }

    //指定距离
    if (m_meter/1000 >= mExponent_denominator && mExponent_denominator != 0 && !updateTips[1]) {
      updateTips[1] = true;
      tvNoticeTip.setVisibility(View.VISIBLE);
      //距离提示
      if(isZhCn()){
        tvNoticeTip.setText(exponent_denominator_tips_zh+" "+ TimeUtils.formatDurationFull(mKeepPlayTime));
      }else{
        tvNoticeTip.setText(exponent_denominator_tips_en+" "+TimeUtils.formatDurationFull(mKeepPlayTime));
      }
      //指定距离提交时间
      requestMarathonWithDuration(mKeepPlayTime,user_play_id);
    }
  }

  private boolean isZhCn(){
    Locale locale = getResources().getConfiguration().locale;
    String language = locale.getLanguage();
    if(language.startsWith("zh")){
      return true;
    }
    return false;
  }

  /**
   * 更新运动时间数据
   * @param keepPlayingTime
   */
  public void setPlayingTimeBoard(int keepPlayingTime) {
    tvTurnTime.setText(TimeUtils.formatDuration(keepPlayingTime));
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Logger.d("onRequestPermissionsResult: permissions.length = " + permissions.length);
    if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        //获得授权后开始扫描,2021-03-27新增
      } else {
        if (getActivity() instanceof WristBallActivity) {
          ((WristBallActivity) getActivity()).showPermissionDialog(getString(R.string.tip), getString(R.string.refuse_ble));
        } else if (getActivity() instanceof RankMatchMainActivity) {
          ((RankMatchMainActivity) getActivity()).showPermissionDialog(getString(R.string.tip), getString(R.string.refuse_ble));
        }
      }
    } else if (requestCode == ACTION_REQUEST_ENABLE) {
      //已经从设置页面设置蓝牙回来,2021-03-27新增
    }
  }

  /**
   * 下发下来的运动时间数据提交
   * @param circle
   */
  private void requestThirdReplaceTime(int circle,long user_play_id) {
    HashMap<String, Object> map = new HashMap<>(2);
    map.put("circle", circle);
    map.put("user_play_id", user_play_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.molecular(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {

          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }

  /**
   * 跑完半马数据提交
   * @param duration
   */
  private void requestMarathon(int duration) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("duration", duration);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.maraThon(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try {
              AppLogger.d("-----跑完半马数据--------" + responseBody.string());
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("-----跑完半马数据--------" + msg);
          }
        })
    );
  }

  /**
   * 跑完全马数据提交
   * @param duration
   */
  private void requestMarathonFull(int duration) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("duration", duration);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.maraThonFull(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try {
              AppLogger.d("-----跑完全马数据--------" + responseBody.string());
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("-----跑完全马数据--------" + msg);
          }
        })
    );
  }

  /**
   * 运动多长时间
   * @param duration
   */
  private void requestMarathonWithDuration(int duration,long user_play_id) {
    HashMap<String, Object> map = new HashMap<>(2);
    map.put("duration", duration);
    map.put("user_play_id", user_play_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.denominator(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {

          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("-----运动多长距离数据提交--------" + msg);
          }
        })
    );
  }

  /**
   * 异常数据上传
   * @param user_play_id
   * @param is_abnormal
   */
  private void updateAbnormal(long user_play_id,int is_abnormal){
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_play_id", user_play_id);
    map.put("is_abnormal", is_abnormal);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.abnormal(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {

          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }


  /**
   * 开始运动
   * 这里也区分 自玩和赛事开始
   */
  private void requestStartPlay() {
    Observable<UserPlayModel> observable = null;
    //比赛时候传递进去
    if (!TextUtils.isEmpty(sys_match_id) && !TextUtils.isEmpty(matchs_stage_id) && !isStopMatch) {
      HashMap<String, Object> map = new HashMap<>();
      map.put("sys_match_id", sys_match_id);
      map.put("matchs_stage_id", matchs_stage_id);
      if(!TextUtils.isEmpty(user_group_id)) {
        map.put("user_group_id", user_group_id);
      }
      map.put("start_time", System.currentTimeMillis()/1000);
      RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
      observable = apiServer.startPlayForMatch(requestBody);
    } else {
      HashMap<String, Object> map = new HashMap<>();
      map.put("start_time", System.currentTimeMillis()/1000);
      RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
      observable = apiServer.startPlay(requestBody);
    }
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserPlayModel>() {
          @Override
          public void onSuccess(UserPlayModel userPlayModel) {
            if (userPlayModel != null && userPlayModel.getUser_play() != null) {
              user_play_id = userPlayModel.getUser_play().getUser_play_id();
              start_time = userPlayModel.getUser_play().getStart_time();

              if(AppDataManager.getInstance().getErrSpeeds().size()<=0){
                AppDataManager.getInstance().addAllErrSpeeds(userPlayModel.getErr_speed());
              }
              EventBus.getDefault().post(new MessageEvent(MessageEvent.PLAY_START));
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("---requestStartPlay---"+msg);
            if(eStartPlayCount<=3){
              eStartPlayCount+=1;
              requestReStartPlay();
            }
          }
        })
    );
  }


  /**
   * 重新去拿user_play_id 确保数据完整
   */
  private void requestReStartPlay(){
    if(eStartPlayCount>3){
      return;
    }
    HashMap<String, Object> map = new HashMap<>();
    if (!TextUtils.isEmpty(sys_match_id) && !TextUtils.isEmpty(matchs_stage_id) && !isStopMatch) {
      map.put("sys_match_id", sys_match_id);
      map.put("matchs_stage_id", matchs_stage_id);
      if(!TextUtils.isEmpty(user_group_id)) {
        map.put("user_group_id", user_group_id);
      }
      map.put("start_time", System.currentTimeMillis()/1000);
    }
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserPlayModel> observable = apiServer.startPlay(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserPlayModel>() {
          @Override
          public void onSuccess(UserPlayModel userPlayModel) {
            if (userPlayModel != null && userPlayModel.getUser_play() != null) {
              user_play_id = userPlayModel.getUser_play().getUser_play_id();
              start_time = userPlayModel.getUser_play().getStart_time();

              if(AppDataManager.getInstance().getErrSpeeds().size()<=0){
                AppDataManager.getInstance().addAllErrSpeeds(userPlayModel.getErr_speed());
              }
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("---requestStartPlay---"+msg);
            if(eStartPlayCount<=3){
              eStartPlayCount+=1;
              requestReStartPlay();
            }
          }
        })
    );
  }

  /**
   * 运动过程中(客户端上传数据)(上传后把圈数缓存清理掉)
   *
   * @param user_play_id  当前运动ID
   * @param start_time    当前运动开始时间
   * @param circle_detail 当前运动中每个时刻的圈数
   */
  private void playingBetween(long user_play_id, int start_time, List<Integer> circle_detail, List<Integer> speed_detail) {
    if (circle_detail.size() > 0) {
      int[] tempCircle_detail = new int[circle_detail.size()];
      for (int i = 0; i < circle_detail.size(); i++) {
        tempCircle_detail[i] = circle_detail.get(i);
      }
      //上传后把圈数缓存清理掉
      circle_detail.clear();

      int[] tempSpeeds = new int[speed_detail.size()];
      for (int i = 0; i < speed_detail.size(); i++) {
        tempSpeeds[i] = speed_detail.get(i);
      }
      speed_detail.clear();

      playingBetween(user_play_id, start_time, tempCircle_detail, tempSpeeds);
    }
  }


  /**
   * 运动过程中(客户端上传数据)
   *
   * @param user_play_id  当前运动ID
   * @param start_time    当前运动开始时间
   * @param circle_detail 当前运动中每个时刻的圈数，[3400, 8137, 17861, 15506, 9780]
   */
  private void playingBetween(long user_play_id, int start_time, int[] circle_detail, int[] speed_detail) {

    if(isStopMatch){
      return;
    }

    HashMap<String, Object> map = new HashMap<>();
    map.put("user_play_id", user_play_id);
    map.put("start_time", start_time);
    map.put("circle_detail", circle_detail);
    map.put("speed_detail", speed_detail);

    //比赛时候传递进去
    if (!TextUtils.isEmpty(sys_match_id) && !TextUtils.isEmpty(matchs_stage_id)) {
      map.put("sys_match_id", sys_match_id);
      map.put("matchs_stage_id", matchs_stage_id);
      if(!TextUtils.isEmpty(user_group_id)){
        map.put("user_group_id", user_group_id);
      }
      map.put("show_all", 0);

      if (BuildConfig.DEBUG) {
        AppLogger.d("【上传数据】运动过程中上传:sys_match_id="+sys_match_id+";matchs_stage_id="+matchs_stage_id+";user_group_id="+user_group_id+";user_play_id=" + user_play_id + ";start_time=" + start_time + ";circle_detail=" + Arrays.toString(circle_detail) + ";speed_detail=" + Arrays.toString(speed_detail));
      }
    }else{
      if (BuildConfig.DEBUG) {
        AppLogger.d("【上传数据】运动过程中上传:user_play_id=" + user_play_id + ";start_time=" + start_time + ";circle_detail=" + Arrays.toString(circle_detail) + ";speed_detail=" + Arrays.toString(speed_detail));
      }
    }

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.playing(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            //响应数据-  返回结构与  赛事 “比赛页面查询基本信息” 一致
            try{
              if (!TextUtils.isEmpty(sys_match_id)){
                parsePlayBetweenResponseBody(responseBody);
              }
            }catch (Exception ex){
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            //Logger.d(msg);
          }
        })
    );
  }

  private void parsePlayBetweenResponseBody(ResponseBody responseBody) throws Exception {
    String respose=responseBody.string();
    AppLogger.d("--parsePlayBetweenResponseBody----"+respose);
    JSONObject jsonObject=new JSONObject(respose);
    int code = jsonObject.optInt("code", 0);
    if (code == 1) {
      JSONObject data = jsonObject.optJSONObject("data");
      Gson gson = new Gson();
      RankMatchInfo2 rankMatchInfo = gson.fromJson(data.toString(), RankMatchInfo2.class);
      int progress=(int)(Math.floor(rankMatchInfo.getDistance_percentage()*100));
      showRandMatchVisibly(rankMatchInfo.getRanking(),rankMatchInfo.getDistance_poor(),progress);
      if(rankMatchInfo.getIs_end()==1 && !isStopMatch){
        //比赛结束提示
        if(user_play_id!=0){
          float meter = BallUtils.getTotalMeter(mTotalCircle);
          playStop(user_play_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
        }
        //标记不可这传递数据
        isStopMatch=true;
        residueTimeStop=true;
        showDialog(getString(R.string.tip),rankMatchInfo.getMatchs_end_tips());
      }
    }
  }

  public void onExitMatch() {
    if(!isStopMatch && user_play_id!=0){
      float meter = BallUtils.getTotalMeter(mTotalCircle);
      playStop(user_play_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
    }
  }

  /**
   * 发送结束标记
   *
   * @param user_play_id  本次运动ID
   * @param start_time    本次运动开始时间
   * @param interval      时间间隔（ms），默认 1000，
   * @param circle_detail 当前运动中每个时刻的总圈数，
   * @param speed_detail  当前运动中每个时刻的转速
   * @param distance      运动米数
   * @param circle_count  运动总圈数
   * @param speed_max     最高转速，rpm 圈/分
   */
  private void playStop(long user_play_id, int start_time, int interval, List<Integer> circle_detail, List<Integer> speed_detail, float distance, int circle_count, int speed_max) {
    if (circle_detail.size() > 0) {

      int[] tempCircles = new int[circle_detail.size()];
      for (int i = 0; i < circle_detail.size(); i++) {
        tempCircles[i] = circle_detail.get(i);
      }
      circle_detail.clear();

      int[] tempSpeeds = new int[speed_detail.size()];
      for (int i = 0; i < speed_detail.size(); i++) {
        tempSpeeds[i] = speed_detail.get(i);
      }
      speed_detail.clear();

      playStop(user_play_id, start_time, interval, tempCircles, tempSpeeds, distance, circle_count, speed_max);
    }
  }

  /**
   * 发送结束标记
   *
   * @param u_play_id     本次运动ID
   * @param u_start_time  本次运动开始时间
   * @param interval      时间间隔（ms），默认 1000，
   * @param circle_detail 当前运动中每个时刻的总圈数，
   * @param speed_detail  当前运动中每个时刻的转速
   * @param distance      运动米数
   * @param circle_count  运动总圈数
   * @param speed_max     最高转速，rpm 圈/分
   */
  private void playStop(long u_play_id, int u_start_time, int interval, int[] circle_detail, int[] speed_detail, float distance, int circle_count, int speed_max) {

    if(isStopMatch){
      return;
    }

    HashMap<String, Object> map = new HashMap<>();
    map.put("user_play_id", u_play_id);
    map.put("start_time", u_start_time);
    map.put("interval", interval);
    map.put("circle_detail", circle_detail);
    map.put("speed_detail", speed_detail);

    map.put("distance", distance);
    map.put("circle_count", circle_count);
    map.put("speed_max", speed_max);
    map.put("is_abnormal", is_abnormal);
    map.put("stop_time", System.currentTimeMillis()/1000);
    //比赛时候传递进去
    if (!TextUtils.isEmpty(sys_match_id) && !TextUtils.isEmpty(matchs_stage_id)) {
      map.put("sys_match_id", sys_match_id);
      map.put("matchs_stage_id", matchs_stage_id);
      if(!TextUtils.isEmpty(user_group_id)){
        map.put("user_group_id", user_group_id);
      }
    }

    AppLogger.d("【上传数据】运动结束上传:user_play_id=" + u_play_id + ";sys_match_id="+sys_match_id+";start_time=" + u_start_time + ";interval=" + interval + ";circle_detail=" + Arrays.toString(circle_detail));

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<PlayOverModel> observable = apiServer.playStop(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<PlayOverModel>() {
          @Override
          public void onSuccess(PlayOverModel playOverModel) {
            try{
              AppLogger.d(playOverModel.toString());
            }catch (Exception ex){
              ex.printStackTrace();
            }
            user_play_id = 0;
            start_time = 0;
            //锦标赛不显示---->
            if (playOverModel != null && TextUtils.isEmpty(sys_match_id)) {
              showBadgeIndex = 0;
              AppDataManager.getInstance().setPlayOverModel(playOverModel);
              if (AppDataManager.getInstance().getPlayOverModel().getNew_medal() != null) {
                if (AppDataManager.getInstance().getPlayOverModel().getNew_medal().size() > 0) {
                  MedalInfo data = AppDataManager.getInstance().getPlayOverModel().getNew_medal().get(showBadgeIndex);
                  showBadge(data);
                }
              }
            }
          }

          @Override
          public void onError(int code, String msg) {
            user_play_id=0;
            AppLogger.d(msg);
          }
        })
    );
  }

  int showBadgeIndex = 0;

  /**
   * 弹出徽章详情
   *
   * @param data
   */
  private void showBadge(MedalInfo data) {
    //显示徽章
    Intent it = new Intent(getActivity(), BadgeActivity.class);
    it.putExtra("data", data);
    startActivity(it);
  }

  /**
   * 弹出徽章列表
   *
   * @param data
   */
  private void showMineBadges(List<MedalInfo> data) {
    Intent it = new Intent(getActivity(), MineBadgeActivity.class);
    startActivity(it);
  }


  /**
   * 初始化摇跑球相关数据
   */
  private void initWallData() {
    tips[0] = false;
    tips[1] = false;
    tips[2] = false;

    try{
      for(int index=0;index<10;index++){
        err_speedsTarget[index]=false;
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }

    updateTips[0] = false;
    updateTips[1] = false;
    mMeter=0.0f;
    is_abnormal=0;
    mRealKeepPlayTime=0;
    mRpmSpeed=0;
    mKeepPlayTime = 0;
    mHighSpeedRPM = 0;
    mTotalCircle = 0;
    comCircleCount = 0;
    circleCache.clear();
    speedCache.clear();
  }

  private void initWallUI() {
    setPlayingTimeBoard(0);
    setPlayingBoard(0, 0);
    setSpeedRPMBoardAnim(0, true);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onServiceNoticeEvent(ServiceNoticeEvent event) {
    switch (event.getCode()) {
//            case ServiceNoticeConstant.CODE_SCAN_START: {
//
//            } break;
//            case ServiceNoticeConstant.CODE_SCAN_DEVICE: {
//
//            } break;
//            case ServiceNoticeConstant.CODE_SCAN_FINISHED: {
//
//            } break;
//            case ServiceNoticeConstant.CODE_CONNECT_START: {
//
//            } break;
      case ServiceNoticeConstant.CODE_CONNECT_SUCCESS: {
        initWallData();
        BallInfo data = (BallInfo) event.getData();
        String name = data.getName();
        SPUtils.put(App.self(), SPUtils.KEY_MATCH_DEVICE, name);
        name = DeviceUtils.getDeviceNickname(this.getContext(), name);
        String str = String.format(getString(R.string.connected_device_finished2), name);
        showSnackBarInConnecting(str);
      } break;
//            case ServiceNoticeConstant.CODE_CONNECT_FAIL: {
//
//            } break;
      case ServiceNoticeConstant.CODE_CONNECT_FINISHED: {
        //蓝牙已断开连接
        showSnackBarNotConnect(getString(R.string.wall_ball_disconnected));
        user_play_id = 0;
        //位置归零
        setSpeedRPMBoardAnim(0, true);
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUN_START: {
        initSpeedRPMBoardAnim();
        setPlayingBoard(0, 0);
        requestStartPlay();
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUNNING: {
        BallRunDetail ballDetail = (BallRunDetail) event.getData();
        int circle = ballDetail.getCircle();
        int speed = ballDetail.getSpeed();
        int runningTime = ballDetail.getTime();

        mRealKeepPlayTime = runningTime;
        mKeepPlayTime = runningTime;
        setPlayingTimeBoard(mRealKeepPlayTime);

        //接收到摇跑球转动下发的数据
        mRpmSpeed = speed;
        //最大转速
        mHighSpeedRPM = Math.max(mHighSpeedRPM, speed);
        //总圈数
        mTotalCircle = circle;

        cheatTip(speed, runningTime);

        setSpeedRPMBoardAnim(mRpmSpeed, true, true);
        setPlayingBoard(mHighSpeedRPM, mTotalCircle);

        circleCache.add(circle);
        speedCache.add(speed);

        Log.d("aaaaaaaaaa", mKeepPlayTime+"");

        if (mKeepPlayTime > 0 && mKeepPlayTime % 3 == 0) {
          Log.d("aaaaaaaaaa", ""+(mKeepPlayTime > 0 && mKeepPlayTime % 3 == 0));
          if (circleCache.size() == 0) {
            circleCache.add(0);
          }
          if (speedCache.size() == 0) {
            speedCache.add(0);
          }
          // 圈数
          List<Integer> tempCircles = new ArrayList<>(circleCache);
          circleCache.clear();
          // 转速
          List<Integer> tempSpeeds = new ArrayList<>(speedCache);
          speedCache.clear();

          //运动过程中传递数据(差异数据)
          if (tempCircles.size() > 0 && tempSpeeds.size() > 0) {
            if (user_play_id > 0 && !TextUtils.isEmpty(sys_match_id) && !TextUtils.isEmpty(matchs_stage_id)) {
              playingBetween(user_play_id, start_time, tempCircles, tempSpeeds);
            }
          }
        }

      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUN_FINISH: {
        //停止运动+数据还是显示在最终结果
        stopRotateAnim();

        if(user_play_id!=0){
          float meter = BallUtils.getTotalMeter(mTotalCircle);
          playStop(user_play_id, start_time, Constants.UPDATE_PERIOD, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
        }

        user_play_id = 0;
        start_time = 0;
        mHighSpeedRPM = 0;
        mTotalCircle = 0;
        comCircleCount = 0;
        is_abnormal = 0;
        mRealKeepPlayTime = 0;
        circleCache.clear();
        speedCache.clear();
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_TOTAL_TIME: {
//        cheatTip(mRpmSpeed,mRealKeepPlayTime);
//        Log.d("PRETTY_LOGGER", "--------ON_SEND_PLAY_TIME_2---------mKeepPlayTime="+mKeepPlayTime);
//        //如果没有摇也要传递数据
//        if(mKeepPlayTime %3 == 0 && mKeepPlayTime != 0){
//          if(circleCache.size()==0){
//            circleCache.add(0);
//          }
//
//          if(speedCache.size()==0){
//            speedCache.add(0);
//          }
//
//          List<Integer> tempCircles = new ArrayList<>();
//          tempCircles.addAll(circleCache);
//          circleCache.clear();
//          //转速
//          List<Integer> tempSpeeds = new ArrayList<>();
//          tempSpeeds.addAll(speedCache);
//          speedCache.clear();
//
//
//
//
//          //运动过程中传递数据(差异数据)
//          if (tempCircles.size() > 0) {
//            playingBetween(user_play_id, start_time, tempCircles, tempSpeeds);
//          }
//        }
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_ELECTRICITY: {
        int electricity = (int) event.getData();
        if (electricity <= 20) {
          tvPower.setTextColor(Color.parseColor("#E26863"));
        } else {
          tvPower.setTextColor(Color.parseColor("#FFFFFF"));
        }
        tvPower.setText(electricity + "%");
      } break;
    }
  }

//  @Subscribe(threadMode = ThreadMode.MAIN)
//  public void onMessageEvent(MessageEvent event) {
//    if (event.getEvetId() == MessageEvent.STATE_CONNECTED) {
//      //设备已经连接
//      String name = event.getDeviceName();
//      String str = String.format(getString(R.string.connected_device_finished2), name);
//      showSnackBarInConnecting(str);
//    } else if (event.getEvetId() == MessageEvent.STATE_DISCONNECTED) {
//      //蓝牙已断开连接
//      showSnackBarNotConnect(getString(R.string.wall_ball_disconnected));
//      user_play_id=0;
//      //位置归零
//      setSpeedRPMBoardAnim(0,true);
//      //String tmpMacAddress = (String) event.getObject();
//      //如果正在玩则提交数据
//      //if (!TextUtils.isEmpty(tmpMacAddress)) {
//      //    EventBus.getDefault().post(new MessageEvent(MessageEvent.PLAY_OVER, circleCache, speedCache));
//      //}
//    } else if (event.getEvetId() == MessageEvent.STATE_DISCONNECTING) {
//      AppLogger.d("----蓝牙正在断开连接----");
//      showSnackBarNotConnect(getString(R.string.wall_ball_disconnected));
//    }else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_READY) {
//      //因为是比赛页面，则不通过链接腕力球才开始初始化页面和数据，这里初始化独立onCreate设置
//      //运动准备
//      initSpeedRPMBoardAnim();
//      setPlayingBoard(0, 0);
//      requestStartPlay();
//    } else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_DATA) {
//      //接收到摇跑球转动下发的数据
//      int maxSpeed=event.getMaxSpeed();
//      int rpmSpeed = event.getRpm();
//      mRpmSpeed=rpmSpeed;
//      int rpm = event.getSpeed2();
//      int tCircle = event.getTotalCircle();
//      //最大转速
//      mHighSpeedRPM = Math.max(mHighSpeedRPM, maxSpeed);
//      //总圈数
//      if (tCircle != mTotalCircle) {
//        mTotalCircle = tCircle;
//        comCircleCount = 0;
//      } else {
//        if (rpm <= 10) {
//          comCircleCount += 1;
//        }
//      }
//
//      //根据下发数据直接更新UI
//        //总圈数+转速
//        circleCache.add(tCircle);
//        speedCache.add(rpmSpeed);
//
//        //这里还需要判断从N突然变为0的情况，这样才不显得突兀,以总圈数为基准并强制设置rpm偏移
//        if (comCircleCount > 0) {
//          rpm = 0;
//          Log.d("PRETTY_LOGGER", "--腕力球停止了---" + System.currentTimeMillis());
//
//          setSpeedRPMBoardAnim(rpm, false, true);
//          setPlayingBoard(mHighSpeedRPM, mTotalCircle);
//
//          //停止后发送,总圈数+转速
//          List<Integer> tempCircles = new ArrayList<>();
//          tempCircles.addAll(circleCache);
//          circleCache.clear();
//
//          List<Integer> tempSpeeds = new ArrayList<>();
//          tempSpeeds.addAll(speedCache);
//          speedCache.clear();
//          //数据清零(非常重要，不要会混淆前后两次数据)
//          EventBus.getDefault().post(new MessageEvent(MessageEvent.PLAY_OVER, tempCircles, tempSpeeds));
//          return;
//        }
//        setSpeedRPMBoardAnim(rpm, true, true);
//        setPlayingBoard(mHighSpeedRPM, mTotalCircle);
//    } else if (event.getEvetId() == MessageEvent.PLAY_START) {
//      //开始运动
//      Log.d("PRETTY_LOGGER", "--------开始运动--XMainFragment--PLAY_START-------");
//    }else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_TIME){
//      int keepPlayTime = event.getKeepTime();
//      mRealKeepPlayTime=keepPlayTime;
//      setPlayingTimeBoard(keepPlayTime);
//    }else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_TIME_2){
//      cheatTip(mRpmSpeed,mRealKeepPlayTime);
//      Log.d("PRETTY_LOGGER", "--------ON_SEND_PLAY_TIME_2---------mKeepPlayTime="+mKeepPlayTime);
//      //如果没有摇也要传递数据
//      if(mKeepPlayTime%3==0 && mKeepPlayTime!=0){
//        if(circleCache.size()==0){
//          circleCache.add(0);
//        }
//
//        if(speedCache.size()==0){
//          speedCache.add(0);
//        }
//
//        List<Integer> tempCircles = new ArrayList<>();
//        tempCircles.addAll(circleCache);
//        circleCache.clear();
//        //转速
//        List<Integer> tempSpeeds = new ArrayList<>();
//        tempSpeeds.addAll(speedCache);
//        speedCache.clear();
//        //这里传递数据进去
//        EventBus.getDefault().post(new MessageEvent(MessageEvent.PLAY_ING, tempCircles, tempSpeeds));
//      }
//    }else if (event.getEvetId() == MessageEvent.PLAY_ING) {
//      //运动过程中传递数据(差异数据)
//      if (event.getCircles().size() > 0) {
//        playingBetween(user_play_id, start_time, event.getCircles(), event.getSpeeds());
//      }
//    } else if (event.getEvetId() == MessageEvent.ON_POWER_ELE) {
//      //获得下发电量,这里在开始和结束获得
//      int power = event.getKeepTime();
//      tvPower.setText(power + "%");
//    } else if (event.getEvetId() == MessageEvent.PLAY_OVER) {
//      //停止运动+数据还是显示在最终结果
//      stopRotateAnim();
//
//      if(user_play_id!=0){
//        float meter = BallUtils.getTotalMeter(mTotalCircle);
//        playStop(user_play_id, start_time, Constants.UPDATE_PERIOD, event.getCircles(), event.getSpeeds(), meter, mTotalCircle, mHighSpeedRPM);
//      }
//
//      user_play_id=0;
//      start_time=0;
//      mHighSpeedRPM = 0;
//      mTotalCircle = 0;
//      comCircleCount = 0;
//      is_abnormal=0;
//      mRealKeepPlayTime=0;
//      circleCache.clear();
//      speedCache.clear();
//    }
//  }

  /**
   * 比赛时候初始化页面
   */
  private void initMainData() {
    //运动准备
    initWallData();
    initWallUI();
    initSpeedRPMBoardAnim();
    setPlayingBoard(0, 0);
    setPlayingTimeBoard(0);
  }

  private void startAddDeviceGuardActivity() {
    Intent it= new Intent(getContext(), AddDeviceInfoActivity.class);
    startActivity(it);
  }

  public void dismissBadge() {
    AutoBadgesDialog.dismissBadge();
  }

  public boolean isShowBadge() {
    return AutoBadgesDialog.isShowing();
  }

  @Override
  public void dismiss(int platform) {
    if (AppDataManager.getInstance().getPlayOverModel() != null) {
      if (AppDataManager.getInstance().getPlayOverModel().getNew_medal() != null) {
        if (showBadgeIndex < AppDataManager.getInstance().getPlayOverModel().getNew_medal().size() - 1) {
          showBadgeIndex += 1;
          MedalInfo data = AppDataManager.getInstance().getPlayOverModel().getNew_medal().get(showBadgeIndex);
          showBadge(data);
        }
      }
    }
  }

  public void showPermissionDialog(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.btn_cancel, null);
    builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
      }
    });
    builder.show();
  }

  public void showDialog(String title, String message) {
    CommonDialog dialog = new CommonDialog(this.getContext());
    dialog.setContent(title, message);
    dialog.addBtn(getString(R.string.btn_ok), commonDialog -> {
      commonDialog.dismiss();
      //退出当前页面
      if (getActivity() instanceof RankMatchMainActivity) {
        ((RankMatchMainActivity) getActivity()).finish();
      }
    });
  }

  /**
   * 作弊提示
   * @param rpmSpeed
   * @param realKeepPlayTime
   */
  private void cheatTip(int rpmSpeed,int realKeepPlayTime){
    if (AppDataManager.getInstance().getErrSpeeds().size() > 0) {
      int len=AppDataManager.getInstance().getErrSpeeds().size();
      for(int index=0;index<len;index++){
        ErrSpeed err=AppDataManager.getInstance().getErrSpeeds().get(index);
        if ((int) (err.getTime()) == realKeepPlayTime && err.getMax_speed() <= rpmSpeed && !err_speedsTarget[index]) {
//                    Toast.makeText(getContext(), R.string.data_err_tip, Toast.LENGTH_LONG).show();
          err_speedsTarget[index]=true;
          is_abnormal=1;
          updateAbnormal(user_play_id,is_abnormal);
          break;
        }
      }
    }
  }


  @Override
  public void onClick(View v) {
    if(v.getId() == R.id.lyAction || v.getId() == R.id.tvAction){
      startAddDeviceGuardActivity();
    }
  }

  public void showSnackBarChanged() {
    BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
    if (connectedDevice == null) {
      showSnackBarNotConnect(getString(R.string.wall_ball_to_connected));
      return;
    }
    String deviceName = connectedDevice.getName();
    deviceName = DeviceUtils.getDeviceNickname(this.getContext(), deviceName);
    String str = String.format(getString(R.string.connected_device_finished2), deviceName);
    showSnackBarInConnecting(str);
  }

  private void showSnackBarInConnecting(CharSequence text) {
    tvTip.setText(text);
    tvPower.setVisibility(View.VISIBLE);
    tvAction.setVisibility(View.GONE);
  }

  private void showSnackBarNotConnect(CharSequence text) {
    tvTip.setText(text);
    tvPower.setVisibility(View.GONE);
    tvAction.setVisibility(View.VISIBLE);
  }

}
