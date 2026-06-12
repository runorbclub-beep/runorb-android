package com.cloud.runball.module.free_style;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.constant.QrCodeConstant;
import com.cloud.runball.dialog.ShareCardDialog;
import com.cloud.runball.dialog.ShareTargetDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.ErrSpeed;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.model.UserPlayModel;
import com.cloud.runball.module.home.AddDeviceInfoActivity;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonTipDialog;
import com.cloud.runball.module.match_football_association.dialog.AssociationShareCardDialog;
import com.cloud.runball.module.mine.InfoActivity;
import com.cloud.runball.module_bluetooth.constant.ServiceNoticeConstant;
import com.cloud.runball.module_bluetooth.constant.ServiceSendConstant;
import com.cloud.runball.module_bluetooth.data.event.BallInfo;
import com.cloud.runball.module_bluetooth.data.event.BallRunDetail;
import com.cloud.runball.module_bluetooth.data.event.MatchTimingInfo;
import com.cloud.runball.module_bluetooth.data.event.ServiceNoticeEvent;
import com.cloud.runball.module_bluetooth.data.event.ServiceSendEvent;
import com.cloud.runball.module_bluetooth.utils.BleUtils;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;
import com.cloud.runball.share.ShareManage;
import com.cloud.runball.utils.AccountUtil;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.BallUtils;
import com.cloud.runball.utils.DeviceUtils;
import com.cloud.runball.utils.SpeechUtils;
import com.cloud.runball.widget.MagicTextView2;
import com.cloud.runball.widget.SpeedCircleImageView;
import com.cloud.runball.widget.V2PointerView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.FragmentFreeStyleBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @author ns467
 */
public class FreeStyleFragment extends BaseFragment {

  Toolbar toolbar;

  TextView tvToolBarTitle;

  ImageView ivShareEntry;

  ImageView ivStop;


  SpeedCircleImageView ivSpeedCircle;

  V2PointerView v2PointerView;

  // 大圆盘显示转速
  MagicTextView2 tvSpeedRPMFormat;

  LinearLayout layNotice;

  ImageView ivNoticeSwitch;

  TextView tvNoticeTip;


  // 最高转速
  TextView tvTurnHeightSpeedRPM;

  // 距离
  TextView tvTurnDistance;

  // 时间
  TextView tvTurnTime;


  TextView tvTip;

  TextView tvAction;

  TextView tvPower;

  private FragmentFreeStyleBinding binding;

  private AssociationCommonDialog commonDialog = null;


  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();

  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  //运动id+开始运动时间
  private long userPlayId = 0;
  long user_play_detail_id_1 = 0;
  long user_play_detail_id_2 = 0;
  private long startPlayTime = 0;
  private long stopTime = 0;

  private boolean isUploading = false;

  //本地保存的转速，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
  private final List<Integer> speedCache = new ArrayList<>();

  //本地保存的总圈数，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
  private final List<Integer> circleCache = new ArrayList<>();

  //格式化数字
  private DecimalFormat mDecimalFormat = new DecimalFormat("0.000");

  // 运动时间
  private int mKeepPlayTime = 0;
  // 最高转速
  private int mHighSpeedRPM = 0;
  // 当前转速
  private int mRpmSpeed = 0;
  // 总圈数
  private int mTotalCircle = 0;

  //分子(指定时间)
  private final int mExponent_molecular = 60;
  //分母(指定距离)
  private final float mExponent_denominator = 21098F;

  // 半马拉松目标距离
  private final float halfMarathonTarget = 21098F;
  // 全马拉松目标距离
  private final float fullMarathonTarget = 42195F;

//  //摇跑1分钟的距离/半马耗时
//  private final String mExponent_title = "摇跑指数 = 第1分钟距离(米) / 半程马拉松耗时(分钟)";
//  private final String mExponent_title_en = "YPI = distance of 1 minute(m) / time of half marathon(min)";
//
//  //时间提示
//  private final String exponent_molecular_tips_en = "Meters in run 1 minutes";
//  private final String exponent_molecular_tips_zh = "摇跑1分钟距离";
//
//  //距离提示
//  private final String exponent_denominator_tips_en = "Half marathon running record";
//  private final String exponent_denominator_tips_zh = "摇跑半马用时";

  /**
   * 运动数据是否异常
   */
  private int isAbnormal = 0;

  //上传指定时间和距离
  private Boolean[] challengeTarget = new Boolean[]{ false, false, false };

  private float mMeter = 0.0f;

  private Handler mHandler = new Handler();

  //摇跑指数
  private String runball_exponent = "0";

  private int halfMarathon;
  private int marathon;

  private PlayInfo playInfo = null;

  private int continueCircle = 0;

  private boolean isFirst = true;

  private AssociationCommonTipDialog pleaseStopCommonDialog = null;
  private AssociationCommonDialog stopedCommonDialog = null;
  private AssociationCommonTipDialog loadupCommonDialog = null;

  public static FreeStyleFragment newInstance() {
    return new FreeStyleFragment();
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_free_style;
  }

  @Override
  protected View getImmersiveView() {
    return toolbar;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentFreeStyleBinding.bind(view);
    // map views
    toolbar = binding.toolbar;
    tvToolBarTitle = binding.tvToolBarTitle;
    ivShareEntry = binding.ivShareEntry;
    ivStop = binding.ivStop;
    ivSpeedCircle = binding.ivSpeedCircle;
    v2PointerView = binding.v2PointerView;
    tvSpeedRPMFormat = binding.tvSpeedRPMFormat;
    layNotice = binding.layNotice;
    ivNoticeSwitch = binding.ivNoticeSwitch;
    tvNoticeTip = binding.tvNoticeTip;
    tvTurnHeightSpeedRPM = binding.tvTurnHeightSpeedRPM;
    tvTurnDistance = binding.tvTurnDistance;
    tvTurnTime = binding.tvTurnTime;
    tvTip = binding.tvTip;
    tvAction = binding.tvAction;
    tvPower = binding.tvPower;

    adaptImmersiveStatusBar();
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
    EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CIRCLE_CLEAR));
    new Handler().postDelayed(() -> {
      EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_REQUEST_ELECTRICITY));
    }, 1000);
    toolbar.setNavigationOnClickListener(v -> {
      Activity activity = getActivity();
      if (activity != null) {
        if (activity instanceof FreeStyleActivity) {
          ((FreeStyleActivity) activity).showExitDialog();
        }
      }
    });

//    tvNoticeIndexTip.setText(String.format(getString(R.string.tv_run_notice_tip_0), "" + runball_exponent));

//    // todo
//    mExponent_molecular = 30;
//    mExponent_denominator = 500F;
//    halfMarathonTarget = 500F;
//    fullMarathonTarget = 1000F;

    boolean isOpenNotice = (boolean) SPUtils.get(FreeStyleFragment.this.getContext(), "isOpenNotice", true);
    if (isOpenNotice) {
      ivNoticeSwitch.setImageResource(R.mipmap.icon_notice);
    } else {
      ivNoticeSwitch.setImageResource(R.mipmap.icon_notice_stop);
    }

    // Click listeners replacement for @OnClick
    View lyAction = view.findViewById(R.id.lyAction);
    if (lyAction != null) {
      lyAction.setOnClickListener(this::onClick);
    }
    ivShareEntry.setOnClickListener(this::onClick);
    ivStop.setOnClickListener(this::onClick);
    ivNoticeSwitch.setOnClickListener(this::onClick);
  }

  @Override
  protected void onLazyLoad() {

  }

  @Override
  protected void onFragmentShow() {
    super.onFragmentShow();
    reloadUserInfo();
    showSnackBarChanged();

    if(AccountUtil.isUserAccount()) {
      if(playInfo != null) {
        ivShareEntry.setEnabled(true);
        ivStop.setEnabled(true);
      } else {
        ivShareEntry.setEnabled(false);
        ivStop.setEnabled(false);
      }
    } else {
      ivShareEntry.setEnabled(false);
      ivStop.setEnabled(false);
    }

    BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
    if (connectedDevice == null) {
      if (commonDialog != null) {
        commonDialog.dismiss();
        commonDialog = null;
      }
//      commonDialog = new AssociationCommonDialog(FreeStyleFragment.this.getContext());
//      commonDialog.setContent(getString(R.string.tip), getString(R.string.tip_no_conn_device));
//      commonDialog.addBtn(getString(R.string.btn_cancel), false, commonDialog -> {
//        commonDialog.dismiss();
//      });
//      commonDialog.addBtn(getString(R.string.btn_go_to_connect), true, commonDialog -> {
//        toAddDeviceInfoActivity();
//      });
      showNoticeTip(getString(R.string.tip_no_conn_device), getString(R.string.tip_no_conn_device));
    } else {
//      mHandler.postDelayed(() -> {
//        if (mHighSpeedRPM == 0) {
//          showNoticeTip(getString(R.string.tip_pleas_play_device), getString(R.string.tip_pleas_play_device));
//        }
//      }, 1000);
      showNoticeTip(getString(R.string.connected_success), getString(R.string.connected_success));
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
//    PopupWindowIndex.self().dismiss();
    mHandler.removeCallbacksAndMessages(null);
    EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CLOSE_MATCH_TIMING));
  }

  ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
  });

  private void toAddDeviceInfoActivity() {
    Intent intent = new Intent(getContext(), AddDeviceInfoActivity.class);
    resultLauncher.launch(intent);
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }

  // 提交剩余数据
  public void uploadSurplusData() {
    if (playInfo == null) {
      if (getActivity() != null) {
        getActivity().finish();
      }
      return;
    }
    playStop(true, false, true);
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
      //请求去更新摇跑指数
      if (AccountUtil.isUserAccount()) {
        requestUserInfo();
//        requestExponent();
      } else {

      }
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
            AppLogger.d("--MainFragment--获取个人信息成功----" + userInfoModel);
            AppDataManager.getInstance().setUserInfoModel(userInfoModel);
            //把token保存起来
            WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
            SPUtils.put(getActivity(), "token", userInfoModel.getUser_info().getToken());

            float runBallExponent = userInfoModel.getUser_info().getAchievement().getRunball_exponent();
            //todo
//            tvNoticeIndexTip.setText(String.format(getString(R.string.tv_run_notice_tip_0), "" + runBallExponent));

            //判断是否已经设置了团队或者个人
            if(AccountUtil.isUserAccount() && userInfoModel.getUser_info().getIs_group() == -1) {
              startActivity(new Intent(getActivity(), InfoActivity.class));
            }
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

  private void stopRotateAnim() {
    ivSpeedCircle.stop();
  }

  /**
   * 初始化摇跑球相关数据
   */
  private void initWallData() {
    userPlayId = 0;
    challengeTarget = new Boolean[]{ false, false, false, false };
    stopTime = 0;
    isAbnormal = 0;
    mMeter = 0.0f;
    mRpmSpeed = 0;
    mKeepPlayTime = 0;
    mHighSpeedRPM = 0;
    mTotalCircle = 0;
    circleCache.clear();
    speedCache.clear();
    setPlayingBoard(0, 0);
    setTimingBoard(0);
    setSpeedBoard(0, true, false);
    initSpeedBoard();

    isMiddleKeepPlayTimeTarget = false;
    isMiddleHalfMarathonTarget = false;
    isSprintHalfMarathonTarget = false;
    isMiddleMarathonTarget = false;
    isSprintMarathonTarget = false;
  }

  /**
   * 初始化表盘数据
   */
  private void initSpeedBoard() {
    //百分比
//    circleProgressBar.initValue(0);
    //角度
//    ivPointer.initValue(0);
    v2PointerView.setAngle(0);
    //显示转速
    tvSpeedRPMFormat.initValue(0);
    //红色外圈
    ivSpeedCircle.setValue(0);
  }

  /**
   * 更新表盘数据
   * @param currentRpm
   * @param anim
   * @param stop
   */
  private void setSpeedBoard(int currentRpm, boolean anim, boolean stop) {
    //百分比
//    circleProgressBar.setValue(BallUtils.getPercentWithSpeedRPM(currentRpm), anim);
    //角度
//    ivPointer.setValue(BallUtils.getAngleWithSpeedRPM(currentRpm), anim);
    v2PointerView.setAngle(BallUtils.getAngleWithSpeedRPM(currentRpm));
    //显示转速
    tvSpeedRPMFormat.setValue(currentRpm, anim, stop);
    //红色外圈
    ivSpeedCircle.setValue(BallUtils.getAngleWithSpeedRPM(currentRpm));
  }

  /**
   * 更新运动数据
   * @param maxRpm      最高转速
   * @param totalCircle 累计圈数(单位千米)
   */
  public void setPlayingBoard(int maxRpm, int totalCircle) {
    //最高速率
    tvTurnHeightSpeedRPM.setText(String.valueOf(maxRpm));
    //直径为5.28cm ，周长16.588cm,单位 km
    float meter = BallUtils.getTotalMeter(totalCircle) / 1000;
    tvTurnDistance.setText(mDecimalFormat.format(meter));
    checkChallenge(meter * 1000, totalCircle);
  }

  /**
   *
   * @param timingTime 累计时间
   */
  private void setTimingBoard(int timingTime) {
    mKeepPlayTime = timingTime;
    tvTurnTime.setText(TimeUtils.formatDuration(timingTime));
  }

  private boolean isMiddleKeepPlayTimeTarget = false;

  private boolean isMiddleHalfMarathonTarget = false;
  private boolean isSprintHalfMarathonTarget = false;

  private boolean isMiddleMarathonTarget = false;
  private boolean isSprintMarathonTarget = false;

  /**
   * Tip提示
   * @param m_meter     米
   * @param totalCircle 圈数
   */
  private void checkChallenge(float m_meter, int totalCircle) {

    if (mKeepPlayTime >= 10 && !isMiddleKeepPlayTimeTarget) {
      isMiddleKeepPlayTimeTarget = true;
//      showNoticeTip(getString(R.string.tip_pleas_speed_up), getString(R.string.tip_pleas_speed_up));
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));
    }

    if (m_meter >= halfMarathonTarget / 2 && !isMiddleHalfMarathonTarget) {
      isMiddleHalfMarathonTarget = true;
//      showNoticeTip(getString(R.string.tip_test_your_endurance), getString(R.string.tip_test_your_endurance));
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));
    }

    if (m_meter >= halfMarathonTarget * 5 / 6 && !isSprintHalfMarathonTarget) {
      isSprintHalfMarathonTarget = true;
//      showNoticeTip(getString(R.string.tip_pleas_sprint_to_the_goal), getString(R.string.tip_pleas_sprint_to_the_goal));
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));
    }

    if (m_meter >= fullMarathonTarget / 2 && !isMiddleMarathonTarget) {
      isMiddleMarathonTarget = true;
//      showNoticeTip(getString(R.string.tip_test_your_endurance), getString(R.string.tip_test_your_endurance));
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));
    }

    if (m_meter >= fullMarathonTarget * 11 / 12 && !isSprintMarathonTarget) {
      isSprintMarathonTarget = true;
//      showNoticeTip(getString(R.string.tip_pleas_sprint_to_the_goal), getString(R.string.tip_pleas_sprint_to_the_goal));
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));
    }

    //半马
    if (m_meter >= halfMarathonTarget && !challengeTarget[0]) {
      challengeTarget[0] = true;
      halfMarathon = mKeepPlayTime;
//      String textTip = String.format(getString(R.string.tv_run_notice_tip_2), TimeUtils.formatDurationFull(mKeepPlayTime));
//      String voiceTip = isZhCn() ? textTip.replace("s","秒") : textTip.replace("s","second");
//      showNoticeTip(textTip, voiceTip);
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));
    }

    //全马
    if (m_meter >= fullMarathonTarget && !challengeTarget[1]) {
      challengeTarget[1] = true;
      marathon = mKeepPlayTime;
//      String textTip = String.format(getString(R.string.tv_run_notice_tip_3), TimeUtils.formatDurationFull(marathon));
//      String voiceTip = isZhCn() ? textTip.replace("s","秒") : textTip.replace("s","second");
//      showNoticeTip(textTip, voiceTip);
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));
      //全马数据提交
      if(AccountUtil.isUserAccount()) {
        //todo
        playInfo.setMarathon(marathon);
        sqlService.updatePlayInfo(playInfo);
//        playStop(true, true);
      }
    }

    //新增指定时间，指定距离提交数据
    if (mKeepPlayTime >= mExponent_molecular && mExponent_molecular > 0 && mKeepPlayTime <= mExponent_molecular && !challengeTarget[2]) {
      challengeTarget[2] = true;
      mMeter = m_meter;
//      String msg = mDecimalFormat.format(mMeter / 1000);
//      String tipShow = isZhCn() ? exponent_molecular_tips_zh + "，" + msg + " km" : exponent_molecular_tips_en + "," + msg + " km";
//      //语音,km中英文需要转化
//      String voiceTip = tipShow.replace("km", getString(R.string.lbl_km_unit));
//      //时间提示
//      showNoticeTip(tipShow, voiceTip);
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));

      //摇跑指定时间数据提交
      if(AccountUtil.isUserAccount()) {
        playInfo.setExponentMolecular((int) BallUtils.getTotalMeter(totalCircle));
        sqlService.updatePlayInfo(playInfo);
      }
    }

    //指定距离
//    if (m_meter >= mExponent_denominator && mExponent_denominator > 0 && !challengeTarget[3]) {
    if (mKeepPlayTime == mExponent_molecular && !challengeTarget[3]) {
      challengeTarget[3] = true;
//      String tipShow = isZhCn() ? exponent_denominator_tips_zh + " " + TimeUtils.formatDurationFull(mKeepPlayTime) : exponent_denominator_tips_en + " " + TimeUtils.formatDurationFull(mKeepPlayTime);
//      //距离提示
//      showNoticeTip(tipShow, null);
      showNoticeTip(getString(R.string.tip_come_on), getString(R.string.tip_come_on));

      float value = new BigDecimal(mHighSpeedRPM).multiply(new BigDecimal(mMeter)).divide(new BigDecimal(1000 * 1000), 2, RoundingMode.HALF_UP).floatValue();

//      float value = new BigDecimal(mMeter + "").multiply(new BigDecimal("60")).divide(new BigDecimal(mKeepPlayTime + ""), 2, BigDecimal.ROUND_HALF_UP).floatValue();
      runball_exponent = value + "";
      //半马数据提交
      if(AccountUtil.isUserAccount()) {
        playInfo.setDistance(mMeter);
        playInfo.setExponentSpeedMax(mHighSpeedRPM);
        playInfo.setExponent(value);
        sqlService.updatePlayInfo(playInfo);
      }
    }
  }

  private boolean isZhCn(){
    Locale locale = getResources().getConfiguration().locale;
    String language = locale.getLanguage();
    return language.startsWith("zh");
  }

  /**
   * 开始运动
   */
  private void requestStartPlay() {
    userPlayId = System.currentTimeMillis();
    startPlayTime = System.currentTimeMillis() / 1000;
    if(AccountUtil.isUserAccount()) {
      playInfo = new PlayInfo();
      playInfo.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT);
      playInfo.setSqlId(userPlayId);
      playInfo.setSource(PlayingDataConstant.PlayingSource.FREE_STYLE);
      playInfo.setCreatedUid(Long.parseLong(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id()));
      playInfo.setStartTime(startPlayTime);
      playInfo.setInterval(500);

      BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
      if (connectedDevice != null) {
        playInfo.setMac(connectedDevice.getAddress());
      }
      sqlService.insertOrUpdatePlayInfo(playInfo);
    } else {
      userPlayId = 0;
    }
  }

  /**
   * 运动过程中(客户端上传数据)(上传后把圈数缓存清理掉)
   * @param circleDetail 当前运动中每个时刻的圈数
   */
  private void playingBetween(List<Integer> circleDetail, List<Integer> speedDetail) {
    if (circleDetail.size() > 0) {
      int[] tempCircleDetail = new int[circleDetail.size()];
      for (int i = 0; i < circleDetail.size(); i++) {
        tempCircleDetail[i] = circleDetail.get(i);
      }

      int[] tempSpeedsDetail = new int[speedDetail.size()];
      for (int i = 0; i < speedDetail.size(); i++) {
        tempSpeedsDetail[i] = speedDetail.get(i);
      }

      if (playInfo != null) {
        long userPlayId = playInfo.getSqlId();
        for (int itemData : tempSpeedsDetail) {
          SpeedDetail newSpeedDetail = new SpeedDetail();
          newSpeedDetail.setUserPlayId(userPlayId);
          newSpeedDetail.setSpeed(itemData);
          sqlService.insertOrUpdateSpeedDetail(newSpeedDetail);
        }
        stopTime = System.currentTimeMillis() / 1000;
        playInfo.setStopTime(stopTime);
        playInfo.setCircleCount(mTotalCircle);
        playInfo.setMaxSpeed(mHighSpeedRPM);
        //       playInfo.setMaxEndurance();
        float distance = BallUtils.getTotalMeter(mTotalCircle);
        playInfo.setDistance(distance);
        playInfo.setDuration(mKeepPlayTime);
        playInfo.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT);
        sqlService.updatePlayInfo(playInfo);
      }


    }
  }

  /**
   * 发送结束标记
   */
  private void playStop(boolean isFinish, boolean isShowCard, boolean isExit) {
    if (playInfo == null) {
      if (commonDialog != null) {
        commonDialog.dismiss();
        commonDialog = null;
      }
//      if (getActivity() != null) {
//        getActivity().finish();
//      }
      return;
    }
    if(AccountUtil.isUserAccount()) {
      if (loadupCommonDialog == null) {
        loadupCommonDialog = new AssociationCommonTipDialog(FreeStyleFragment.this.getContext());
        loadupCommonDialog.setContent(getString(R.string.tip_uploading_result));
      }
      PlayInfo data = sqlService.queryPlayInfo(userPlayId);
      uploadByUserPlayId(data, isFinish, isShowCard, isExit);
    }
  }

  private int user_play_detail_id_cont = 0;

  private void uploadByUserPlayId(PlayInfo data, boolean isFinish, boolean isShowCard, boolean isExit) {
    isUploading = true;
    if (user_play_detail_id_cont > 1) {
      user_play_detail_id_cont = 0;
      uploadLocalMatchPlayV3(data, isFinish, isShowCard, isExit);
      return;
    }

    HashMap<String, Object> map = new HashMap<>(1);
    map.put("start_time", System.currentTimeMillis() / 1000);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserPlayModel> observable = apiServer.startPlay(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserPlayModel>() {
          @Override
          public void onSuccess(UserPlayModel userPlayModel) {
            AppLogger.d("---开始运动---requestStartPlay=result");
            if (userPlayModel != null && userPlayModel.getUser_play() != null) {
              if (user_play_detail_id_cont == 0) {
                user_play_detail_id_1 = (long) (userPlayModel.getUser_play().getUser_play_id() + (Math.random() * 1000));
                user_play_detail_id_cont++;
                uploadByUserPlayId(playInfo, isFinish, isShowCard, isExit);
              } else if (user_play_detail_id_cont == 1) {
                user_play_detail_id_2 = (long) (userPlayModel.getUser_play().getUser_play_id() + (Math.random() * 1000));
                user_play_detail_id_cont++;
                uploadByUserPlayId(playInfo, isFinish, isShowCard, isExit);
              }
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
            if (loadupCommonDialog != null) {
              loadupCommonDialog.dismiss();
              loadupCommonDialog = null;
            }
            if (isExit) {
              if (getActivity() != null) {
                getActivity().finish();
              }
              return;
            }
            AssociationCommonDialog loadupErrorDialog = new AssociationCommonDialog(FreeStyleFragment.this.getContext());
            loadupErrorDialog.setContent(getString(R.string.tip), getString(R.string.tip_upload_result_fail));
            loadupErrorDialog.addBtn(getString(R.string.btn_upload_later), false, commonDialog -> {
              user_play_detail_id_cont = 0;
              isUploading = false;
              commonDialog.dismiss();
            });
            loadupErrorDialog.addBtn(getString(R.string.btn_upload_again), true, commonDialog -> {
              commonDialog.dismiss();
              user_play_detail_id_cont = 0;
              uploadByUserPlayId(data, isFinish, isShowCard, isExit);
            });
          }
        })
    );
  }

  private void uploadLocalMatchPlayV3(PlayInfo data, boolean isFinish, boolean isShowCard, boolean isExit) {
    if (data == null) {
      if (getActivity() != null) {
        getActivity().finish();
      }
      return;
    }
    List<SpeedDetail> speedDetailData = sqlService.querySpeedDetail(data.getSqlId());
    List<Integer> speedDetail = new ArrayList<>();
    for (int i = 0; i < speedDetailData.size(); i++) {
      SpeedDetail itemSpeedDetail = speedDetailData.get(i);
      speedDetail.add(itemSpeedDetail.getSpeed());
    }
    Integer[] speedDetailArr = speedDetail.toArray(new Integer[0]);
//    data.setSpeedDetail(new Gson().toJson(speedDetailArr));
    data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING);

//    if (loadupCommonDialog == null) {
//      loadupCommonDialog = new AssociationCommonTipDialog(FreeStyleFragment.this.getContext());
//      loadupCommonDialog.setContent(getString(R.string.tip_uploading_result));
//    }
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_play_id", user_play_detail_id_1);
    map.put("user_play_detail_id", user_play_detail_id_2);
    map.put("exponent_molecular", data.getExponentMolecular());
    map.put("source", data.getSource());
    map.put("endurance_max", data.getMaxEndurance());
    map.put("is_abnormal", data.getIsAbnormal());
    map.put("sys_match_id", 0);
    map.put("sys_sys_match_id", 0);
    map.put("stop_time", data.getStopTime());
    map.put("start_time", data.getStartTime());
    map.put("interval", data.getInterval());
    map.put("created_uid", data.getCreatedUid());
    map.put("speed_max", data.getMaxSpeed());
    map.put("exponent", data.getExponent());
    map.put("marathon", data.getMarathon());
    map.put("is_quartets", 0);
    map.put("duration", data.getDuration());
    map.put("distance", data.getDistance());
    map.put("exponent_speed_max", data.getExponentSpeedMax());
    map.put("circle_count", data.getCircleCount());
    map.put("exponent_denominator", data.getExponentDenominator());
    map.put("speed_detail", new Gson().toJson(speedDetailArr));
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<Object> observable = apiServer.uploadLocalMatchPlayV3(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Object>() {
              @Override
              public void onSuccess(Object o) {
//                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
//                int count = sqlService.updatePlayInfo(data);
                sqlService.deletePlayInfo(data.getSqlId());
                sqlService.deleteSpeedDetail(data.getSqlId());
                if (loadupCommonDialog != null) {
                  loadupCommonDialog.dismiss();
                  loadupCommonDialog = null;
                }
                if (stopedCommonDialog != null) {
                  stopedCommonDialog.dismiss();
                  stopedCommonDialog = null;
                }
                if (isExit) {
                  if (getActivity() != null) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH_RANK_LIST));
                    getActivity().finish();
                  }
                  return;
                }
                isUploading = false;
                if (isShowCard) {
                  showShareCardDialog(isFinish);
                }
              }
              @Override
              public void onError(int code, String msg) {
                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                sqlService.updatePlayInfo(data);
                if (loadupCommonDialog != null) {
                  loadupCommonDialog.dismiss();
                  loadupCommonDialog = null;
                }
                if (isExit) {
                  if (getActivity() != null) {
                    getActivity().finish();
                  }
                  return;
                }
                AssociationCommonDialog loadupErrorDialog = new AssociationCommonDialog(FreeStyleFragment.this.getContext());
                loadupErrorDialog.setContent(getString(R.string.tip), getString(R.string.tip_upload_result_fail));
                loadupErrorDialog.addBtn(getString(R.string.btn_upload_later), false, commonDialog -> {
                  isUploading = false;
                  commonDialog.dismiss();
                });
                loadupErrorDialog.addBtn(getString(R.string.btn_upload_again), true, commonDialog -> {
                  commonDialog.dismiss();
                  uploadLocalMatchPlayV3(data, isFinish, isShowCard, isExit);
                });
              }
            })
    );
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
        if (playInfo == null) {
          initWallData();
        }
        BallInfo data = (BallInfo) event.getData();
        String name = data.getName();
        name = DeviceUtils.getDeviceNickname(this.getContext(), name);
        String str = String.format(getString(R.string.connected_device_finished2), name);
        showSnackBarInConnecting(str);
        showNoticeTip(getString(R.string.connected_success), getString(R.string.connected_success));
      } break;
//            case ServiceNoticeConstant.CODE_CONNECT_FAIL: {
//
//            } break;
      case ServiceNoticeConstant.CODE_CONNECT_FINISHED: {
        stopRotateAnim();
        setSpeedBoard(0, true, false);
        initSpeedBoard();
        continueCircle = mTotalCircle;
        showSnackBarNotConnect(getString(R.string.wall_ball_no_connected));

        if (commonDialog != null) {
          commonDialog.dismiss();
          commonDialog = null;
        }
        if (stopedCommonDialog != null) {
          stopedCommonDialog.dismiss();
          stopedCommonDialog = null;
        }
        if (!AssociationShareCardDialog.isShowing()) {
          commonDialog = new AssociationCommonDialog(FreeStyleFragment.this.getContext());
          commonDialog.setContent(null, this.getString(R.string.dialog_text_playing_bluetooth_disconnected));
          commonDialog.addBtn(getString(R.string.dialog_btn_text_connect), true, commonDialog -> {
            toAddDeviceInfoActivity();
          });
          commonDialog.addBtn(getString(R.string.dialog_btn_text_continue_stop), false, commonDialog -> {
            commonDialog.dismiss();
            playStop(true, true, false);
          });
        }
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUN_START: {
        if (isUploading) {
          return;
        }
        isFirst = false;
        // 运动准备
        BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
        if (connectedDevice == null) {
          return;
        }
        if (playInfo == null || !playInfo.getMac().equals(connectedDevice.getAddress())) {
//          hiddenNoticeTip();
          initWallData();
          requestStartPlay();
          continueCircle = 0;
          EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_START_MATCH_TIMING));
        }
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUNNING: {
        if (isUploading) {
          return;
        }
        if (isFirst) {
          isFirst = false;
          BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
          if (connectedDevice == null) {
            return;
          }
          if (playInfo == null || !playInfo.getMac().equals(connectedDevice.getAddress())) {
//          hiddenNoticeTip();
            initWallData();
            requestStartPlay();
            continueCircle = 0;
          }
          EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_START_MATCH_TIMING));
        }

        if(playInfo != null) {
          if (!ivShareEntry.isEnabled()) {
            ivShareEntry.setEnabled(true);
          }
          if (!ivStop.isEnabled()) {
            ivStop.setEnabled(true);
          }
        }

//        if (isFirst) {
//          if (pleaseStopCommonDialog == null) {
//            pleaseStopCommonDialog = new AssociationCommonTipDialog(FreeStyleFragment.this.getContext());
//            pleaseStopCommonDialog.setContent(getString(R.string.tip_please_stop_and_wind_up_the_ball));
//            pleaseStopCommonDialog.setReturn(() -> {
//              Activity activity = getActivity();
//              if (activity != null) {
//                activity.finish();
//              }
//            });
//          }
//        } else {
//          if (pleaseStopCommonDialog != null) {
//            pleaseStopCommonDialog.dismiss();
//            pleaseStopCommonDialog = null;
//          }
//          if (commonDialog != null) {
//            commonDialog.dismiss();
//            commonDialog = null;
//          }
//          if(AccountUtil.isUserAccount()) {
//            if(playInfo != null) {
//              if (!ivShareEntry.isEnabled()) {
//                ivShareEntry.setEnabled(true);
//              }
//              if (!ivStop.isEnabled()) {
//                ivStop.setEnabled(true);
//              }
//            }
//          }
//        }
//
//        if (isFirst) {
//          return;
//        }

        BallRunDetail ballDetail = (BallRunDetail) event.getData();
        int circle = ballDetail.getCircle();
        int speed = ballDetail.getSpeed();
//        int runningTime = ballDetail.getTime();
//        mKeepPlayTime = runningTime;
        mRpmSpeed = speed;
        //最大转速
        mHighSpeedRPM = Math.max(mHighSpeedRPM, speed);
        //总圈数
        mTotalCircle = continueCircle + circle;

        AppLogger.d(
            "PRETTY_LOGGER --腕力球圈数---" +
                " circle = " + mTotalCircle +
                " runningTime = " + mKeepPlayTime +
                " speed = " + mRpmSpeed +
                " maxSpeed = " + mHighSpeedRPM
        );

        // 检查作弊
        checkCheat(mRpmSpeed);

        setSpeedBoard(speed, true, true);
        setPlayingBoard(mHighSpeedRPM, mTotalCircle);

        circleCache.add(circle);
        speedCache.add(speed);
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUN_FINISH: {
        continueCircle = mTotalCircle;
        stopRotateAnim();
        setSpeedBoard(0, true, false);
        initSpeedBoard();

        if (isUploading) {
          return;
        }

        if (commonDialog != null) {
          commonDialog.dismiss();
          commonDialog = null;
        }
        if (stopedCommonDialog != null) {
          stopedCommonDialog.dismiss();
          stopedCommonDialog = null;
        }
        if (pleaseStopCommonDialog == null && !AssociationShareCardDialog.isShowing()) {
          stopedCommonDialog = new AssociationCommonDialog(FreeStyleFragment.this.getContext());
          stopedCommonDialog.setContent(getString(R.string.tip), this.getString(R.string.dialog_text_playing_stop));
          stopedCommonDialog.addBtn(getString(R.string.dialog_btn_text_continue), false, commonDialog -> {
            commonDialog.dismiss();
            stopedCommonDialog = null;
          });
          stopedCommonDialog.addBtn(getString(R.string.dialog_btn_text_continue_stop), true, commonDialog -> {
            playStop(true, true, false);
          });
        } else {
          pleaseStopCommonDialog.dismiss();
          pleaseStopCommonDialog = null;
          showNoticeTip(getString(R.string.tip_pleas_play_device), getString(R.string.tip_pleas_play_device));
        }

      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_TOTAL_TIME: {

      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_ELECTRICITY: {
        int electricity = (int) event.getData();
        if (electricity <= 20) {
          tvPower.setTextColor(Color.parseColor("#E26863"));
        } else {
          tvPower.setTextColor(Color.parseColor("#F7DC29"));
        }
        tvPower.setText(electricity + "%");
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_MATCH_TIME: {
        MatchTimingInfo data = (MatchTimingInfo) event.getData();
        int timingTime = data.getTime();
        boolean isRunning = data.isPlaying();
        setTimingBoard(timingTime);

        AppLogger.d("ServiceNoticeConstant.CODE_NOTIFY_MATCH_TIME matchRunningTime = " + timingTime + ", isRunning = " + isRunning);

//        if (mKeepPlayTime == 60 && mMeter <= 0) {
//          String msg = mDecimalFormat.format(BallUtils.getTotalMeter(mTotalCircle) / 1000);
//          String tipShow = isZhCn() ? exponent_molecular_tips_zh + "，" + msg + " km" : exponent_molecular_tips_en + "," + msg + " km";
//          //语音,km中英文需要转化
//          String voiceTip = tipShow.replace("km", getString(R.string.lbl_km_unit));
//          //时间提示
//          showNoticeTip(tipShow, voiceTip);
//        }

        if (isUploading) {
          return;
        }

        if (mKeepPlayTime > 0 && mKeepPlayTime % 3 == 0) {
          if (isRunning) {
            if (circleCache.size() == 0) {
              circleCache.add(0);
            }
            if (speedCache.size() == 0) {
              speedCache.add(0);
            }
          }
          // 圈数
          List<Integer> tempCircles = new ArrayList<>(circleCache);
          circleCache.clear();
          // 转速
          List<Integer> tempSpeeds = new ArrayList<>(speedCache);
          speedCache.clear();

          //运动过程中传递数据(差异数据)
          if (tempCircles.size() > 0 && tempSpeeds.size() > 0) {
            if (userPlayId > 0) {
              playingBetween(tempCircles, tempSpeeds);
            }
          }
        }
      } break;
    }
  }

//  @Subscribe(threadMode = ThreadMode.MAIN)
//  public void onMessageEvent(MessageEvent event) {
//    if (event.getEvetId() == MessageEvent.STATE_APP_TO_BACKSTAGE) {
//
//    } else if (event.getEvetId() == MessageEvent.STATE_APP_TO_FOREGROUND) {
//
//    }
//  }

  public void onClick(View v) {
    if(v.getId() == R.id.lyAction){
      toAddDeviceInfoActivity();
    } else if (v.getId() == R.id.ivShareEntry) {
      showShareCardDialog(false);
    } else if (v.getId() == R.id.ivStop) {
      AssociationCommonDialog stopDialog = new AssociationCommonDialog(this.getContext());
      stopDialog.setContent(null, getString(R.string.dialog_text_stop));
      stopDialog.addBtn(getString(R.string.dialog_btn_text_continue), false, commonDialog -> {
        commonDialog.dismiss();
      });
      stopDialog.addBtn(getString(R.string.dialog_btn_text_continue_stop), true, commonDialog -> {
        commonDialog.dismiss();
        playStop(true, true, false);
      });
    } else if (v.getId() == R.id.ivNoticeSwitch) {
      boolean isOpenNotice = (boolean) SPUtils.get(FreeStyleFragment.this.getContext(), "isOpenNotice", true);
      if (isOpenNotice) {
        SPUtils.put(FreeStyleFragment.this.getContext(), "isOpenNotice", false);
        ivNoticeSwitch.setImageResource(R.mipmap.icon_notice_stop);
      } else {
        SPUtils.put(FreeStyleFragment.this.getContext(), "isOpenNotice", true);
        ivNoticeSwitch.setImageResource(R.mipmap.icon_notice);
      }
    }
  }

  public void showSnackBarChanged() {
    BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
    if (connectedDevice == null) {
      showSnackBarNotConnect(getString(R.string.wall_ball_no_connected));
      return;
    }
    if (commonDialog != null) {
      commonDialog.dismiss();
      commonDialog = null;
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

  private void showShareTargetDialog(Bitmap bitmap, boolean isEnd) {
    if (getActivity() == null || getActivity().isFinishing()) {
      return;
    }
    ShareTargetDialog dialog = new ShareTargetDialog();
    dialog.show(FreeStyleFragment.this.getContext(), new ShareTargetDialog.ConfirmCallBack() {
      @Override
      public void onCancel() {

      }
      @Override
      public void onShareTarget(ShareTargetDialog.ShareTarget shareTarget) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (ActivityCompat.checkSelfPermission(FreeStyleFragment.this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FreeStyleFragment.this.requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 188);
            return;
          }
        }
        ShareManage shareManage = new ShareManage();
        shareManage.shareBitmap(FreeStyleFragment.this.getActivity(), shareTarget.getType(), bitmap, new ShareManage.ShareCallback() {
          @Override
          public void onStart() {

          }
          @Override
          public void onResult() {
            AssociationShareCardDialog.dismiss();
            ShareCardDialog.dismiss();
            if (isEnd) {
              Activity activity = getActivity();
              if (activity != null) {
                activity.finish();
              }
            }
          }
          @Override
          public void onError(Throwable throwable) {

          }
          @Override
          public void onCancel() {
            AssociationShareCardDialog.dismiss();
          }
        });
      }
    });
  }

  private void showShareCardDialog(boolean isEnd) {
    if (getActivity() == null || getActivity().isFinishing()) {
      return;
    }
    AssociationShareCardDialog.show(
        FreeStyleFragment.this.getContext(),
        getString(R.string.title_go_module_free_style),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_name(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getAddress(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img(),
        stopTime * 1000, mMeter / 1000, Float.parseFloat(runball_exponent),
        halfMarathon, marathon,
        mHighSpeedRPM + "",
        BallUtils.getTotalMeter(mTotalCircle) / 1000,
        TimeUtils.formatDuration(mKeepPlayTime),
        QrCodeConstant.WECHAT_OFFICIAL_ACCOUNTS_URL,
        new AssociationShareCardDialog.ConfirmCallBack() {
          @Override
          public void onOther() {
            AssociationCommonDialog dialog = new AssociationCommonDialog(FreeStyleFragment.this.getContext());
            dialog.setContent(getString(R.string.tip), getString(R.string.tip_whether_to_exit_free_style_mode));
            dialog.addBtn(getString(R.string.btn_once_more), false, commonDialog -> {
              commonDialog.dismiss();
//              hiddenNoticeTip();
              initWallData();
//              requestStartPlay();
              playInfo = null;
              isFirst = true;

              EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CLOSE_MATCH_TIMING));
            });
            dialog.addBtn(getString(R.string.btn_exit), true, commonDialog -> {
              commonDialog.dismiss();
              Activity activity = getActivity();
              if (activity != null) {
                activity.finish();
              }
            });
          }
          @Override
          public void onCancel() {

          }
          @Override
          public void onMore() {
            if (commonDialog != null) {
              commonDialog.dismiss();
              commonDialog = null;
            }
//            hiddenNoticeTip();
            initWallData();
//              requestStartPlay();
            isFirst = true;
            playInfo = null;
            EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CLOSE_MATCH_TIMING));
          }
          @Override
          public void onShare(Bitmap bitmap) {
            showShareTargetDialog(bitmap, isEnd);
          }
        }, isEnd);
  }

  private void showNoticeTip(String textTip, String voiceTip) {
//    layNotice.setVisibility(View.VISIBLE);
    tvNoticeTip.setText(textTip);
    tvNoticeTip.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    tvNoticeTip.setSingleLine(true);
    tvNoticeTip.setSelected(true);
    if (!TextUtils.isEmpty(voiceTip) && (boolean) SPUtils.get(FreeStyleFragment.this.getContext(), "isOpenNotice", true)) {
      SpeechUtils.getInstance(getContext()).speakText(voiceTip);
    }
  }

//  private void hiddenNoticeTip() {
//    layNotice.setVisibility(View.INVISIBLE);
//  }

  /**
   * 作弊提示
   * @param rpmSpeed
   */
  private void checkCheat(int rpmSpeed){
    if (AppDataManager.getInstance().getErrSpeeds().size() > 0) {
      int len = AppDataManager.getInstance().getErrSpeeds().size();
      for(int index = 0; index < len; index++){
        ErrSpeed err = AppDataManager.getInstance().getErrSpeeds().get(index);
        if ((int) (err.getTime()) == mKeepPlayTime && err.getMax_speed() <= rpmSpeed) {
//                   Toast.makeText(getContext(), R.string.data_err_tip, Toast.LENGTH_LONG).show();
          isAbnormal = 1;
          if(AccountUtil.isUserAccount()) {
            playInfo.setIsAbnormal(isAbnormal);
            sqlService.updatePlayInfo(playInfo);
          }
          break;
        }
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CIRCLE_CLEAR));
  }
}