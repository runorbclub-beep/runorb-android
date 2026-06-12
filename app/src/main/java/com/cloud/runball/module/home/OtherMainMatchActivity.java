package com.cloud.runball.module.home;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.listener.OnItemClickListener;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.constant.BallStatusConstant;
import com.cloud.runball.constant.MatchStatusConstant;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.constant.QrCodeConstant;
import com.cloud.runball.dialog.ConfirmDialog;
import com.cloud.runball.dialog.MatchExitDialog;
import com.cloud.runball.dialog.ShareCardDialog;
import com.cloud.runball.dialog.ShareTargetDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.ErrSpeed;
import com.cloud.runball.model.ShakeMatchModel;
import com.cloud.runball.model.UserPlayModel;
import com.cloud.runball.module.home.adapter.OtherMainMatchHorseAdapter;
import com.cloud.runball.module.yjy.history.OtherMainMatchListActivity;
import com.cloud.runball.module_bluetooth.constant.ServiceNoticeConstant;
import com.cloud.runball.module_bluetooth.constant.ServiceSendConstant;
import com.cloud.runball.module_bluetooth.data.event.BallInfo;
import com.cloud.runball.module_bluetooth.data.event.BallRunDetail;
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
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.BallUtils;
import com.cloud.runball.utils.Constants;
import com.cloud.runball.utils.DeviceUtils;
import com.cloud.runball.view.HorseSurfaceView;
import com.cloud.runball.widget.MagicTextView2;
import com.cloud.runball.widget.PointerImageView;
import com.google.gson.Gson;
import com.littlejie.circleprogress.CircleProgress;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cloud.runball.databinding.ActivityOtherMainMatchBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.module.home
 * @ClassName: OtherMainMatchActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/21 14:02
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/21 14:02
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMainMatchActivity extends BaseActivity implements OnItemClickListener {

  private ActivityOtherMainMatchBinding binding;

  OtherMainMatchHorseAdapter lyHorses;
  HorseSurfaceView lyHorsesRoad;
  ImageView img_exit;
  TextView tvPower;
  CircleProgress circle_progress_bar;
  MagicTextView2 tvSpeedRPMFormat;
  PointerImageView ivPointer;
  TextView tvPersonTime;
  TextView tvPersonDistance;
  TextView tvMatchCountdown;
  TextView tvIntegral;
  LinearLayout lyAction;
  TextView tvTip;
  TextView tvAction;
  LinearLayout lyBottom;
  FrameLayout fyBottom;


  //下面这几个是报名确认页面

  ImageView ivHorse;
  TextView tvHorseName;
  TextView tvHorseAttachPerson;
  ImageView ivConfirm;
  TextView tvMatchCountdownTag;
  TextView tvShare;


  public static final int REQUEST_CODE = 100;

  //运动id+开始运动时间
  private long userPlayId = 0;
  private int startTime = 0;
  private long stopTime = 0;

  private boolean isUploading = false;

  // 0:未获取；1：正在获取；2：获取了
  private int startPlayStatus = 0;

  /**
   * 最高转速
   */
  private int mHighSpeedRPM = 0;

  /**
   * 总圈数
   */
  private int mTotalCircle = 0;

  /**
   * 是否作弊
   */
  private int isAbnormal = 0;

  //作弊提示
  private final Boolean[] errSpeedsTarget = new Boolean[]{ false, false, false, false, false, false, false, false, false, false };

  //本地保存的转速，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
  private final List<Integer> speedCache = new ArrayList<>();

  //本地保存的总圈数，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
  private final List<Integer> circleCache = new ArrayList<>();

  //格式化数字
  private DecimalFormat mDecimalFormat = new DecimalFormat("0.000");

  private final AtomicBoolean isTickRunning = new AtomicBoolean(false);

  private long shakeGroupId = -1;
  private long sysShakeId = -1;
  private int each_integral = 0;
  private String date;

  private int mRpmSpeed = 0;

  //比赛倒计时
  private int matchCountdownTime = 0;

  // 个人当前新增摇球时间（秒）
  //运行时间
  private int mKeepPlayTime = 0;

  //是否已经报名
  private boolean isSignUp = false;
  //是否开赛
  private int curMatchStatus = MatchStatusConstant.READY;

  // 当前摇球状态
  private int curBallStatus = BallStatusConstant.IDLE;

  // 个人本赛事过往累计摇球时间（秒）
  private int myDuration = 0;
  private double myDistance = 0.0f;


  private ScheduledExecutorService countDownExecutor = null;

  private boolean residueTimeStop = false;
  private int reFlashCount = 0;

  private int horseIndex;
  private String horseTitle;

  Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0:
          if (tvMatchCountdown != null) {
            tvMatchCountdown.setText(TimeUtils.formatDuration3(matchCountdownTime));
          }
          break;
        case 1:
          requestHorseList();
          break;
      }
    }
  };

  private final CompositeDisposable playingBetweenDisposable = new CompositeDisposable();

  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();
  private PlayInfo playInfo = null;

  @Override
  protected int onLayoutId() {
    return R.layout.activity_other_main_match;
  }
  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityOtherMainMatchBinding.inflate(inflater);
    return binding.getRoot();
  }
  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    lyHorses = binding.lyHorses;
    lyHorsesRoad = binding.lyHorsesRoad;
    img_exit = binding.imgExit;
    tvPower = binding.lyAction.tvPower;
    circle_progress_bar = binding.circleProgressBar;
    tvSpeedRPMFormat = binding.tvSpeedRPMFormat;
    ivPointer = binding.ivPointer;
    tvPersonTime = binding.tvPersonTime;
    tvPersonDistance = binding.tvPersonDistance;
    tvMatchCountdown = binding.tvMatchCountdown;
    tvIntegral = binding.tvIntegral;
    lyAction = binding.lyAction.getRoot();
    tvTip = binding.lyAction.tvTip;
    tvAction = binding.lyAction.tvAction;
    lyBottom = binding.lyBottom;
    fyBottom = binding.fyBottom;
    ivHorse = binding.lyOtherMainMatchBottom.ivHorse;
    tvHorseName = binding.lyOtherMainMatchBottom.tvHorseName;
    tvHorseAttachPerson = binding.lyOtherMainMatchBottom.tvHorseAttachPerson;
    ivConfirm = binding.lyOtherMainMatchBottom.ivConfirm;
    tvMatchCountdownTag = binding.tvMatchCountdownTag;
    tvShare = binding.tvShare;
    setEmptyStatusBar();
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    EventBus.getDefault().register(this);
    EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CIRCLE_CLEAR));
    new Handler().postDelayed(() -> {
      EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_REQUEST_ELECTRICITY));
    }, 1000);
    lyHorses.setOnItemClickListener(this);
    lyHorsesRoad.move();
    initWallData();
    initWallUI();
    requestHorseList();

    // Replace @OnClick with listeners
    img_exit.setOnClickListener(this::onViewClicked);
    lyAction.setOnClickListener(this::onViewClicked);
    ivConfirm.setOnClickListener(this::onViewClicked);
    tvShare.setOnClickListener(this::onViewClicked);
  }

  /**
   * 表盘数据
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
  }

  @Override
  public void onResume(){
    super.onResume();
    showSnackBarChanged();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CIRCLE_CLEAR));
    EventBus.getDefault().unregister(this);
    mKeepPlayTime = 0;
    isTickRunning.set(false);
    stopScheduleCountDown();
    playingBetweenDisposable.dispose();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    super.onKeyDown(keyCode, event);
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      //弹出退出框，退出比赛
      MatchExitDialog.show(this, () -> {
        onExitMatch(true);
//        finish();
      });
      return false;
    }
    return true;
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
        BallInfo data = (BallInfo) event.getData();
        String name = data.getName();
        SPUtils.put(App.self(), SPUtils.KEY_MATCH_DEVICE, name);
        name = DeviceUtils.getDeviceNickname(this, name);
        String str = String.format(getString(R.string.connected_device_finished2), name);
        showSnackBarInConnecting(str);
//        startPlayStatus = 0;
      } break;
//            case ServiceNoticeConstant.CODE_CONNECT_FAIL: {
//
//            } break;
      case ServiceNoticeConstant.CODE_CONNECT_FINISHED: {
        curBallStatus = BallStatusConstant.IDLE;
        showSnackBarNotConnect(getString(R.string.wall_ball_disconnected));

        //停止运动+数据还是显示在最终结果
        if(userPlayId > 0) {
          if(curMatchStatus == MatchStatusConstant.RUN) {
            float meter = BallUtils.getTotalMeter(mTotalCircle);
            playStop(false, userPlayId, startTime, Constants.UPDATE_PERIOD, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
          }
        }
        setSpeedRPMBoardAnim(0, false, true);
        initSpeedRPMBoardAnim();
        myDistance = myDistance + BallUtils.getTotalMeter(mTotalCircle) / 1000;
        userPlayId = 0;
        startTime = 0;
        mHighSpeedRPM = 0;
        mTotalCircle = 0;
        startPlayStatus = 0;
        circleCache.clear();
        speedCache.clear();


      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUN_START: {
        if (startPlayStatus == 0) {
          //运动准备
          initWallData();
          initSpeedRPMBoardAnim();
          AppLogger.d("test_ON_SEND_PLAY_DATA 111 sys_shake_id = " + sysShakeId + ", curMatchStatus = " + curMatchStatus);
          if(sysShakeId > 0 && curMatchStatus == MatchStatusConstant.RUN && isSignUp) {
//            // 为防止在不结束该页面，球停止后重新摇动计数时个人累计摇动距离异常，myDistance需更新为当前最新摇动距离
//            myDistance = myDistance + BallUtils.getTotalMeter(mTotalCircle) / 1000;
            EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CIRCLE_CLEAR));
            requestStartPlay(sysShakeId);
          }
          return;
        } else if (startPlayStatus == 1) {
          return;
        } else if (startPlayStatus == 2) {
          curBallStatus = BallStatusConstant.RUNNING;
        } else {
          return;
        }
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUNNING: {
        Log.d(
            "ggggggggggggg",
            "curBallStatus = " + curBallStatus
                + ", startPlayStatus = " + startPlayStatus
                + ", sysShakeId = " + sysShakeId
                + ", curMatchStatus = " + curMatchStatus
                + ", isSignUp = " + isSignUp
        );
        if (sysShakeId <= 0) {
          return;
        }
        if (curBallStatus != BallStatusConstant.RUNNING) {
          if (startPlayStatus == 0) {
            //运动准备
            initWallData();
            initSpeedRPMBoardAnim();
            AppLogger.d("test_ON_SEND_PLAY_DATA 111 sys_shake_id = " + sysShakeId + ", curMatchStatus = " + curMatchStatus);
            if(sysShakeId > 0 && curMatchStatus == MatchStatusConstant.RUN && isSignUp) {
//              // 为防止在不结束该页面，球停止后重新摇动计数时个人累计摇动距离异常，myDistance需更新为当前最新摇动距离
//              myDistance = myDistance + BallUtils.getTotalMeter(mTotalCircle) / 1000;
              EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CIRCLE_CLEAR));
              requestStartPlay(sysShakeId);
            }
            return;
          } else if (startPlayStatus == 1) {
            return;
          } else if (startPlayStatus == 2) {
            curBallStatus = BallStatusConstant.RUNNING;
          } else {
            return;
          }
        }

        if(userPlayId <= 0 || curMatchStatus != MatchStatusConstant.RUN || !isSignUp) {
          return;
        }

        BallRunDetail ballDetail = (BallRunDetail) event.getData();
        int circle = ballDetail.getCircle();
        int speed = ballDetail.getSpeed();
        int runningTime = ballDetail.getTime();

        AppLogger.d("---运动过程--CODE_NOTIFY_RUNNING---" +
            " userPlayId = " + userPlayId +
            ", startTime = " + startTime +
            ", circles = " + circle +
            ", speeds = " + speed +
            ", runningTime = " + runningTime);

        // 转速
        this.mRpmSpeed = speed;
        // 最大转速
        this.mHighSpeedRPM = Math.max(this.mHighSpeedRPM, speed);
        // 总圈数
        this.mTotalCircle = circle;
        this.mKeepPlayTime = runningTime;

        circleCache.add(circle);
        speedCache.add(speed);

        setSpeedRPMBoardAnim(speed, true, true);

//        cheatTip(mRpmSpeed);

        // 跟人累计距离
        double distance = myDistance + BallUtils.getTotalMeter(mTotalCircle) / 1000;
        updatePersonMatchLeft(distance);

        if (mKeepPlayTime % 5 != 0) {
          return;
        }

        if (circleCache.size() == 0) {
          circleCache.add(0);
        }
        if (speedCache.size() == 0) {
          speedCache.add(0);
        }

        // 总圈数
        List<Integer> tempCircles = new ArrayList<>(circleCache);
        circleCache.clear();
        // 转速
        List<Integer> tempSpeeds = new ArrayList<>(speedCache);
        speedCache.clear();

        // 运动过程中传递数据(差异数据)
        if (tempCircles.size() > 0 && tempSpeeds.size() > 0) {
          // http上报
          playingBetweenHttp(userPlayId, startTime, tempCircles, tempSpeeds);
        }
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUN_FINISH: {
        curBallStatus = BallStatusConstant.IDLE;
        //停止运动+数据还是显示在最终结果
        if(userPlayId > 0) {
          if(curMatchStatus == MatchStatusConstant.RUN) {
            float meter = BallUtils.getTotalMeter(mTotalCircle);
            playStop(false, userPlayId, startTime, Constants.UPDATE_PERIOD, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
          }
        }
        setSpeedRPMBoardAnim(0, false, true);
        initSpeedRPMBoardAnim();
        myDistance = myDistance + BallUtils.getTotalMeter(mTotalCircle) / 1000;
        userPlayId = 0;
        startTime = 0;
        mHighSpeedRPM = 0;
        mTotalCircle = 0;
        startPlayStatus = 0;
        circleCache.clear();
        speedCache.clear();
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_TOTAL_TIME: {

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

  /**
   * 初始化摇跑球相关数据
   */
  private void initWallData() {
    mRpmSpeed=0;
    isAbnormal =0;
    mHighSpeedRPM = 0;
    mTotalCircle = 0;
    circleCache.clear();
    speedCache.clear();
  }

  private void initWallUI(){
//        tvPersonTime.setText("00:00:00");
    tvPersonDistance.setText("0.000km");
    tvMatchCountdown.setText("00:00:00");
    tvIntegral.setText("0");
    setSpeedRPMBoardAnim(0, true);
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
  }

  /**
   * 表盘数据
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
  }

  private void startAddDeviceActivity() {
    Intent it= new Intent(this, AddDeviceInfoActivity.class);
    startActivityForResult(it, REQUEST_CODE);
  }

  /**
   * 开始运动
   */
  private void requestStartPlay(long sysShakeId) {
    //未开赛
    if(!isSignUp || curMatchStatus != MatchStatusConstant.RUN) {
      return;
    }
    startPlayStatus = 1;
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_shake_id", sysShakeId);
    map.put("start_time", System.currentTimeMillis() / 1000);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserPlayModel> observable = apiServer.startPlayForMatch(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserPlayModel>() {
              @Override
              public void onSuccess(UserPlayModel userPlayModel) {
                AppLogger.d("---开始运动---requestStartPlay=result");
                if (userPlayModel != null && userPlayModel.getUser_play() != null) {
                  userPlayId = userPlayModel.getUser_play().getUser_play_id();
                  startTime = userPlayModel.getUser_play().getStart_time();
                  startPlayStatus = 2;

                  playInfo = new PlayInfo();
                  playInfo.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT);
                  playInfo.setSqlId(userPlayId);
                  playInfo.setSource(PlayingDataConstant.PlayingSource.UPUP);
                  playInfo.setCreatedUid(Long.parseLong(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id()));
                  playInfo.setStartTime(startTime);
                  playInfo.setInterval(500);
                  playInfo.setSysShakeId(sysShakeId);

                  BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
                  if (connectedDevice != null) {
                    playInfo.setMac(connectedDevice.getAddress());
                  }
                  sqlService.insertOrUpdatePlayInfo(playInfo);
                }
              }

              @Override
              public void onError(int code, String msg) {
                startPlayStatus = 0;
                AppLogger.d("---------requestStartPlay-------" + msg);
              }
            })
    );
  }

  /**
   * 运动过程中(客户端上传数据)
   * @param userPlayId  当前运动ID
   * @param startTime    当前运动开始时间
   * @param circleDetail 当前运动中每个时刻的圈数，[3400, 8137, 17861, 15506, 9780]
   * @param speedDetail 当前运动中每个时刻的速度，[3400, 8137, 17861, 15506, 9780]
   */
  private void playingBetweenHttp(long userPlayId, int startTime, List<Integer> circleDetail, List<Integer> speedDetail) {
    if (isUploading) {
      return;
    }
    if (userPlayId <= 0 || !isSignUp || curMatchStatus != MatchStatusConstant.RUN) {
      return;
    }

    if (playInfo == null) {
      return;
    }

    if (circleDetail.size() > 0 && speedDetail.size() > 0) {
      int[] tempCircleDetail = { circleDetail.get(circleDetail.size() - 1) };
      //上传后把圈数缓存清理掉
      circleDetail.clear();

      int[] tempSpeedsDetail = { speedDetail.get(speedDetail.size() - 1) };
      speedDetail.clear();

      long sqlId = playInfo.getSqlId();
      for (int i = 0; i < tempCircleDetail.length; i++) {
        SpeedDetail newSpeedDetail = new SpeedDetail();
        newSpeedDetail.setUserPlayId(sqlId);
        newSpeedDetail.setSpeed(tempSpeedsDetail[i]);
        newSpeedDetail.setCircle(tempCircleDetail[i]);
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
      sqlService.insertOrUpdatePlayInfo(playInfo);


      HashMap<String, Object> map = new HashMap<>();
      map.put("sys_shake_id", sysShakeId);
      map.put("user_play_id", userPlayId);
      map.put("start_time", startTime);
      map.put("circle_detail", tempCircleDetail);
      map.put("speed_detail", tempSpeedsDetail);
      map.put("show_all", 1);
      map.put("current_time", System.currentTimeMillis()/1000);

      AppLogger.d("【上传数据】运动过程中上传: sysShakeId = " + sysShakeId +
          "; userPlayId = " + userPlayId +
          "; startTime = " + startTime +
          "; circleDetail = " + Arrays.toString(tempCircleDetail) +
          "; speedDetail=" + Arrays.toString(tempSpeedsDetail));

      playingBetweenDisposable.clear();
      RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
      WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
      Observable<ResponseBody> observable = apiServer.playing(requestBody);
      WristBallObserver<ResponseBody> observer = new WristBallObserver<ResponseBody>() {
        @Override
        public void onSuccess(ResponseBody responseBody) {
          AppLogger.d("【上传数据】运动过程中上传onSuccess");
          try {
            parsePlayBetween(responseBody);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }

        @Override
        public void onError(int code, String msg) {
          AppLogger.d("---------playingBetweenHttp-------" + msg);
        }
      };
      playingBetweenDisposable.add(
          observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(observer)
      );
    }
  }

  private void parsePlayBetween(ResponseBody responseBody) throws Exception {
    JSONObject jsonObject = new JSONObject(responseBody.string());
    int code = jsonObject.optInt("code", 0);
    if (code == 1) {
      JSONObject dataObject = jsonObject.optJSONObject("data");
      Gson gson = new Gson();
      JSONObject resultObject = new JSONObject(dataObject.toString());
      AppLogger.d("--parsePlayBetween---" + resultObject);
      if(resultObject.optInt("is_end",0) == 1 && curMatchStatus == MatchStatusConstant.RUN) {
        //这里查询判断会有问题，不建议通过查询来判断
        curMatchStatus = MatchStatusConstant.FINISH;

        String listStr = resultObject.optString("list");
        if(!TextUtils.isEmpty(listStr) && listStr.length() > 10){
          ShakeMatchModel shakeMatchModel = gson.fromJson(listStr, ShakeMatchModel.class);
          updateMatchMainUI(shakeMatchModel,1);
        }else{
          updateMatchMainUI(1);
        }
        //达到赛点，发送结束
        if(userPlayId > 0) {
          float meter = BallUtils.getTotalMeter(mTotalCircle);
          playStop(false, userPlayId, startTime, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
        }
        stopScheduleCountDown();
        updateResultMatchMainUI();
      } else {
        String listStr = resultObject.optString("list");
        ShakeMatchModel shakeMatchModel = gson.fromJson(listStr, ShakeMatchModel.class);
        updateMatchMainUI(shakeMatchModel,0);
        //积分总奖池
        tvIntegral.setText(String.valueOf(getAllIntegrals(shakeMatchModel)));
      }
    }
  }

  /**
   * 发送结束标记
   * @param userPlayId  本次运动ID
   * @param startTime    本次运动开始时间
   * @param interval      时间间隔（ms），默认 1000，
   * @param circleDetail 当前运动中每个时刻的总圈数，
   * @param speedDetail  当前运动中每个时刻的转速
   * @param distance      运动米数
   * @param circleCount  运动总圈数
   * @param maxSpeed     最高转速，rpm 圈/分
   */
  private void playStop(boolean isFinish, long userPlayId, int startTime, int interval, List<Integer> circleDetail, List<Integer> speedDetail, float distance, int circleCount, int maxSpeed) {
//    int[] tempCircles = new int[circleDetail.size()];
//    for (int i = 0; i < circleDetail.size(); i++) {
//      tempCircles[i] = circleDetail.get(i);
//    }
//    circleDetail.clear();
//
//    int[] tempSpeeds = new int[speedDetail.size()];
//    for (int i = 0; i < speedDetail.size(); i++) {
//      tempSpeeds[i] = speedDetail.get(i);
//    }
//    speedDetail.clear();

    if (isUploading) {
      if (isFinish) {
        finish();
      }
      return;
    }

    playingBetweenHttp(userPlayId, startTime, circleDetail, speedDetail);

    isUploading = true;

    PlayInfo data = sqlService.queryPlayInfo(userPlayId);
    List<SpeedDetail> speedDetailData = sqlService.querySpeedDetail(data.getSqlId());
    List<Integer> speedDetailList = new ArrayList<>();
    List<Integer> circleDetailList = new ArrayList<>();
    for (int i = 0; i < speedDetailData.size(); i++) {
      SpeedDetail itemSpeedDetail = speedDetailData.get(i);
      speedDetailList.add(itemSpeedDetail.getSpeed());
      circleDetailList.add(itemSpeedDetail.getCircle());
    }
    Integer[] speedDetailArr = speedDetailList.toArray(new Integer[0]);
    Integer[] circleDetailArr = circleDetailList.toArray(new Integer[0]);

    HashMap<String, Object> map = new HashMap<>();
    map.put("user_play_id", data.getSqlId());
    map.put("source", data.getSource());
    map.put("exponent_molecular", 0);
    map.put("endurance_max", data.getMaxEndurance());
    map.put("is_abnormal", 0);
    map.put("sys_match_id", 0);
    map.put("sys_sys_match_id", 0);
    map.put("matchs_stage_id", 0);
    map.put("sys_shake_id", data.getSysShakeId());
    map.put("stop_time", data.getStopTime());
    map.put("start_time", data.getStartTime());
    map.put("interval", data.getInterval());
    map.put("created_uid", data.getCreatedUid());
    map.put("speed_max", data.getMaxSpeed());
    map.put("exponent", 0);
    map.put("marathon", 0);
    map.put("is_quartets", 0);
    map.put("duration", data.getDuration());
    map.put("distance", data.getDistance());
    map.put("circle_count", data.getCircleCount());
    map.put("exponent_denominator", 0);
    map.put("exponent_speed_max", data.getExponentSpeedMax());
    map.put("speed_detail", new Gson().toJson(speedDetailArr));
    map.put("circle_detail", new Gson().toJson(circleDetailArr));
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<Object> observable = apiServer.uploadLocalMatchPlayV3(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Object>() {
              @Override
              public void onSuccess(Object o) {
//                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
//                sqlService.insertOrUpdatePlayInfo(data);
                sqlService.deletePlayInfo(data.getSqlId());
                sqlService.deleteSpeedDetail(data.getSqlId());
                isUploading = false;
              }
              @Override
              public void onError(int code, String msg) {
                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                sqlService.insertOrUpdatePlayInfo(data);
                isUploading = false;
              }

              @Override
              public void onComplete() {
                super.onComplete();
                playInfo = null;
                if (isFinish) {
                  finish();
                }
              }
            })
    );





//    HashMap<String, Object> map = new HashMap<>();
//    map.put("sys_shake_id", sysShakeId);
//    map.put("user_play_id", userPlayId);
//    map.put("start_time", startTime);
//    map.put("interval", interval);
//    map.put("circle_detail", tempCircles);
//    map.put("speed_detail", tempSpeeds);
//
//    map.put("distance", distance);
//    map.put("circle_count", circleCount);
//    map.put("speed_max", maxSpeed);
//    map.put("is_abnormal", isAbnormal);
//    map.put("stop_time", System.currentTimeMillis()/1000);
//
//    AppLogger.d("【上传数据】运动结束上传: user_play_id = " + userPlayId
//        + "; start_time=" + startTime
//        + "; interval=" + interval
//        + "; circle_detail=" + Arrays.toString(tempCircles));
//
//    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
//    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
//    Observable<PlayOverModel> observable = apiServer.playStop(requestBody);
//    disposable.add(
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(new WristBallObserver<PlayOverModel>() {
//              @Override
//              public void onSuccess(PlayOverModel playOverModel) {
////                OtherMainMatchActivity.this.userPlayId = 0;
////                OtherMainMatchActivity.this.startTime = 0;
//              }
//              @Override
//              public void onError(int code, String msg) {
////                OtherMainMatchActivity.this.userPlayId = 0;
////                OtherMainMatchActivity.this.startTime = 0;
//                AppLogger.d("---------playStop-------" + msg);
//              }
//            })
//    );
  }

  public void onViewClicked(View v) {
    if (v.getId() == R.id.img_exit) {
      //弹出退出框，退出比赛
      MatchExitDialog.show(this, () -> {
        onExitMatch(true);
//        finish();
      });
    }else if(v.getId() == R.id.lyAction) {
      startAddDeviceActivity();
    }else if(v.getId() == R.id.ivConfirm) {
      //报名确认
      if(shakeGroupId != -1) {
        showConfirmDialog();
      }
    } else if (v.getId() == R.id.tvShare) {
      requestShareData();
    }
  }

  private void requestShareData() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ShakeMatchModel> observable = apiServer.shakeMatchData();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ShakeMatchModel>() {
              @Override
              public void onSuccess(ShakeMatchModel shakeMatchModel) {
                if(shakeMatchModel!=null){
                  if (shakeMatchModel.getGroup_list() == null) {
                    return;
                  }
                  if (sysShakeId > 0 && sysShakeId != shakeMatchModel.getSys_shake_id()) {
                    updateResultMatchMainUI();
                    return;
                  }
                  if(shakeMatchModel.getMy_info() != null && !TextUtils.isEmpty(shakeMatchModel.getMy_info().getShake_group_user_id())){
                    int index = getShareItem(shakeMatchModel.getGroup_list(),shakeMatchModel.getMy_info().getShake_group_id());
                    if(index == -1) {
                      return;
                    }
                    int ranking = 1;
                    ShakeMatchModel.ShakeItem tempShakeItem = shakeMatchModel.getGroup_list().get(index);
                    for (int i = 0; i < shakeMatchModel.getGroup_list().size(); i++) {
                      if (shakeMatchModel.getGroup_list().get(i).getDistance() > shakeMatchModel.getGroup_list().get(index).getDistance()) {
                        ranking++;
                      }
                    }

                    showShareCardDialog(
                        shakeMatchModel.getDate(), shakeMatchModel.getStatus(),
                        shakeMatchModel.getMy_info().getIndex(), shakeMatchModel.getMy_info().getIndex(),
                        shakeMatchModel.getMy_info().getTitle(), tempShakeItem.getNum(),
                        BigDecimal.valueOf(tempShakeItem.getDistance()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_DOWN).doubleValue(),
                        ranking,
                        new BigDecimal(shakeMatchModel.getMy_info().getDistance()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_DOWN).doubleValue(), shakeMatchModel.getMy_info().getDatetime(),
                        shakeMatchModel.getMy_info().getIntegral()
                    );

                  }

                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("---requestHorseList--code="+code+"-----"+msg);
              }
        })
    );
  }

  private void showShareTargetDialog(Bitmap bitmap) {
    ShareTargetDialog dialog = new ShareTargetDialog();
    dialog.show(OtherMainMatchActivity.this, new ShareTargetDialog.ConfirmCallBack() {
      @Override
      public void onCancel() {

      }
      @Override
      public void onShareTarget(ShareTargetDialog.ShareTarget shareTarget) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (ActivityCompat.checkSelfPermission(OtherMainMatchActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OtherMainMatchActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 188);
            return;
          }
        }
        ShareManage shareManage = new ShareManage();
        shareManage.shareBitmap(OtherMainMatchActivity.this, shareTarget.getType(), bitmap, new ShareManage.ShareCallback() {
          @Override
          public void onStart() {

          }
          @Override
          public void onResult() {
            ShareCardDialog.dismiss();
          }
          @Override
          public void onError(Throwable throwable) {

          }
          @Override
          public void onCancel() {

          }
        });
      }
    });
  }

  private void showShareCardDialog(
      String date, int upupStatus, int horsePath, int horseNum,
      String horseTitle, int helpPlayers, double helpDistance, int ranking,
      double myHelpDistance, int myHelpTime, int score) {
    ShareCardDialog.showShareUpup(this,
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_name(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getAddress(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img(),
        date, upupStatus, horsePath, horseNum, horseTitle,
        helpPlayers, helpDistance, ranking,
        myHelpDistance, myHelpTime, score,
        QrCodeConstant.WECHAT_OFFICIAL_ACCOUNTS_URL,
        new ShareCardDialog.ConfirmCallBack() {
          @Override
          public void onCancel() {

          }
          @Override
          public void onMore() {

          }
          @Override
          public void onShare(Bitmap bitmap) {
            showShareTargetDialog(bitmap);
          }
        });
  }

  private void requestMatchSignUp(long shakeGroupId){
    HashMap<String, Object> map = new HashMap<>();
    map.put("shake_group_id", shakeGroupId);
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.shakeMatchSignUp(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try{
              JSONObject jsonObject = new JSONObject(responseBody.string());
              if(jsonObject.optInt("code") == 1){
                //报名成功
                isSignUp = true;
                fyBottom.setVisibility(View.GONE);
                lyBottom.setVisibility(View.VISIBLE);
                lyHorses.enabledHorseSelected(false);

                int index = getShareItem(lyHorses.getDataInfo(), shakeGroupId);
                if(index != -1) {
                  ShakeMatchModel.ShakeItem tempShakeItem = lyHorses.getDataInfo().get(index);
                  tvPersonTime.setText(tempShakeItem.getTitle());
                }

                horseStop();
              }
            }catch (Exception ex) {
              ex.printStackTrace();
            }
          }
          @Override
          public void onError(int code, String msg) {
            AppLogger.d("----------requestMatchSignUp----------code="+code+";msg="+msg);
          }
        })
    );
  }

  private int getAllIntegrals(ShakeMatchModel shakeMatchModel) {
    int allNums = 0;
    for(ShakeMatchModel.ShakeItem shakeItem:shakeMatchModel.getGroup_list()) {
      allNums += shakeItem.getNum();
    }
    return each_integral * allNums;
  }

  private void requestHorseList(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ShakeMatchModel> observable = apiServer.shakeMatchData();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ShakeMatchModel>() {
          @Override
          public void onSuccess(ShakeMatchModel shakeMatchModel) {
            if(shakeMatchModel != null){
              if (shakeMatchModel.getGroup_list() == null) {
                return;
              }
              if (sysShakeId > 0 && sysShakeId != shakeMatchModel.getSys_shake_id()) {
                updateResultMatchMainUI();
                return;
              }
              //区分自己的
              ShakeMatchModel.ShakeItem shakeItem = shakeMatchModel.getGroup_list().get(0);
              selectShakeMatchItem(shakeItem);
              showSectionHorse(shakeItem);
              date = shakeMatchModel.getDate();
              lyHorses.setList(shakeMatchModel.getGroup_list());
              each_integral = shakeMatchModel.getEach_integral();
              //显示倒计时
              matchCountdownTime = shakeMatchModel.getCountdown();
              //积分总奖池
              tvIntegral.setText(String.valueOf(getAllIntegrals(shakeMatchModel)));
              //根据状态进行判断是否开赛
              if(shakeMatchModel.getStatus() == 1) {
                //状态  0：未开始 1：进行中 2：开始报名 3：已结束
                tvShare.setEnabled(true);
                tvShare.setTextColor(Color.parseColor("#50F0FF"));
                curMatchStatus = MatchStatusConstant.RUN;
                tvMatchCountdownTag.setText(getString(R.string.lbl_match_main_total_countdown));
                if(shakeMatchModel.getMy_info() != null && !TextUtils.isEmpty(shakeMatchModel.getMy_info().getShake_group_user_id())) {
                  int index = getShareItem(shakeMatchModel.getGroup_list(), shakeMatchModel.getMy_info().getShake_group_id());
                  ShakeMatchModel.ShakeItem tempShakeItem;
                  myDuration = shakeMatchModel.getMy_info().getDuration();
                  myDistance = Double.parseDouble(shakeMatchModel.getMy_info().getDistance()) / 1000.0f;
                  if(index == -1) {
                    tempShakeItem = shakeMatchModel.getGroup_list().get(0);
                  } else {
                    tempShakeItem = shakeMatchModel.getGroup_list().get(index);
                    updatePersonMatchLeft(myDistance);
                    tvPersonTime.setText(tempShakeItem.getTitle());
                  }
                  selectShakeMatchItem(tempShakeItem);
                  showSectionHorse(tempShakeItem);
                  isSignUp=true;
                }
                if(!isSignUp) {
                  fyBottom.setVisibility(View.VISIBLE);
                  lyBottom.setVisibility(View.GONE);
                } else {
                  fyBottom.setVisibility(View.GONE);
                  lyBottom.setVisibility(View.VISIBLE);
                  lyHorses.enabledHorseSelected(false);
                }
              } else if(shakeMatchModel.getStatus() == 2) {
                tvShare.setEnabled(false);
                tvShare.setTextColor(Color.parseColor("#ffffff"));
                //2：开始报名
                curMatchStatus = MatchStatusConstant.READY;
                tvMatchCountdownTag.setText(getString(R.string.lbl_match_main_total_countdown2));
                if(!isSignUp) {
                  fyBottom.setVisibility(View.VISIBLE);
                  lyBottom.setVisibility(View.GONE);
                } else {
                  fyBottom.setVisibility(View.GONE);
                  lyBottom.setVisibility(View.VISIBLE);
                  lyHorses.enabledHorseSelected(false);
                }
              } else if(shakeMatchModel.getStatus() == 3) {
                tvShare.setEnabled(true);
                tvShare.setTextColor(Color.parseColor("#50F0FF"));
                tvMatchCountdownTag.setText(getString(R.string.lbl_match_main_total_countdown));
                Intent it=new Intent(getApplication(), OtherMainMatchListActivity.class);
                it.putExtra("sys_shake_id", String.valueOf(sysShakeId));
                startActivity(it);
                finish();
                return;
              } else {
                tvShare.setEnabled(false);
                tvShare.setTextColor(Color.parseColor("#ffffff"));
              }
              //开始比赛倒计时
              if(!isTickRunning.get()) {
                startScheduleCountDown();
              }
            }
          }
          @Override
          public void onError(int code, String msg) {
            AppLogger.d("---requestHorseList--code="+code+"-----"+msg);
          }
        })
    );
  }

  private int getShareItem(List<ShakeMatchModel.ShakeItem> shakeItems, long shakeGroupId) {
    int size = shakeItems.size();
    for(int i = 0; i < size; i++){
      if(shakeItems.get(i).getShake_group_id() == shakeGroupId) {
        return i;
      }
    }
    return -1;
  }

  private void onExitMatch(boolean isFinish) {
    if(userPlayId > 0 && isSignUp && curMatchStatus == MatchStatusConstant.RUN && curBallStatus == BallStatusConstant.RUNNING) {
      float meter = BallUtils.getTotalMeter(mTotalCircle);
      playStop(isFinish, userPlayId, startTime, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
    } else {
      finish();
    }
  }

  /**
   * 作弊提示
   * @param rpmSpeed
   */
  private void cheatTip(int rpmSpeed) {
    if (AppDataManager.getInstance().getErrSpeeds().size() > 0) {
      int len = AppDataManager.getInstance().getErrSpeeds().size();
      for(int index = 0; index < len; index++) {
        ErrSpeed err = AppDataManager.getInstance().getErrSpeeds().get(index);
        if ((int) (err.getTime()) == mKeepPlayTime && err.getMax_speed() <= rpmSpeed && !errSpeedsTarget[index]) {
//                    Toast.makeText(this, R.string.data_err_tip, Toast.LENGTH_LONG).show();
          errSpeedsTarget[index] = true;
          isAbnormal = 1;
          updateAbnormal(userPlayId, isAbnormal);
          break;
        }
      }
    }
  }


  /**
   * 异常数据上传
   * @param userPlayId
   * @param isAbnormal
   */
  private void updateAbnormal(long userPlayId, int isAbnormal){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_play_id", userPlayId);
    map.put("is_abnormal", isAbnormal);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.abnormal(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
              @Override
              public void onSuccess(ResponseBody responseBody) {

              }

              @Override
              public void onError(int code, String msg) {
                AppLogger.d("---------updateAbnormal-------" + msg);
              }
            })
    );
  }

  /**
   * 获取比赛列表基本信息，在不运动时候请求
   */
  private void requestShakeMatchBase() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ShakeMatchModel> observable = apiServer.shakeMatchData();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ShakeMatchModel>() {
              @Override
              public void onSuccess(ShakeMatchModel shakeMatchModel) {
                try{
                  if(shakeMatchModel != null) {
                    if (sysShakeId > 0 && sysShakeId != shakeMatchModel.getSys_shake_id()) {
                      updateResultMatchMainUI();
                      return;
                    }
                    if (shakeMatchModel.getGroup_list() != null) {
                      lyHorses.notifyDataSetChanged(shakeMatchModel.getGroup_list());
                      //积分总奖池
                      tvIntegral.setText(String.valueOf(getAllIntegrals(shakeMatchModel)));
                    }
                    if(shakeMatchModel.getStatus() == 1) {
                      //状态  0：未开始 1：进行中 2：开始报名 3：已结束
                      tvShare.setEnabled(true);
                      tvShare.setTextColor(Color.parseColor("#50F0FF"));
                    }else if(shakeMatchModel.getStatus() == 2) {
                      tvShare.setEnabled(false);
                      tvShare.setTextColor(Color.parseColor("#ffffff"));
                    }else if(shakeMatchModel.getStatus() == 3) {
                      tvShare.setEnabled(true);
                      tvShare.setTextColor(Color.parseColor("#50F0FF"));
                    } else {
                      tvShare.setEnabled(false);
                      tvShare.setTextColor(Color.parseColor("#ffffff"));
                    }
                  }
                }catch (Exception ex) {
                  ex.printStackTrace();
                }
              }

              @Override
              public void onError(int code, String msg) {
                AppLogger.d("---------requestShakeMatchBase-------" + msg);
              }
            })
    );
  }

  /**
   * 弹出结果页面
   */
  private void updateResultMatchMainUI() {
    curMatchStatus = MatchStatusConstant.RUN;
    residueTimeStop = true;
    stopScheduleCountDown();
    //弹出结束框
    Intent it=new Intent(this, OtherMainMatchListActivity.class);
    it.putExtra("sys_shake_id", String.valueOf(sysShakeId));
    it.putExtra("date", date);
    startActivity(it);
    finish();
  }

  private void updateMatchMainUI(ShakeMatchModel shakeMatchModel, int is_end) {
    updateMatchMainUI(is_end);
    if(shakeMatchModel != null && shakeMatchModel.getGroup_list() != null) {
      lyHorses.notifyDataSetChanged(shakeMatchModel.getGroup_list());
    }
  }

  private void updateMatchMainUI(int isEnd) {
    if(isEnd == 1 && curMatchStatus == MatchStatusConstant.RUN) {
      //达到赛点，发送结束
      if(userPlayId > 0) {
        float meter = BallUtils.getTotalMeter(mTotalCircle);
        playStop(false, userPlayId, startTime, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
      }
      curMatchStatus = MatchStatusConstant.FINISH;
      residueTimeStop = true;
      stopScheduleCountDown();
      //弹出结束框
      Intent it = new Intent(this, OtherMainMatchListActivity.class);
      it.putExtra("sys_shake_id", String.valueOf(sysShakeId));
      it.putExtra("date", date);
      startActivity(it);
      finish();
    }
  }

  /**
   * 更新个人累计时间和距离
   * @param distance 累计距离
   */
  private void updatePersonMatchLeft(double distance) {
    //个人累计距离
    tvPersonDistance.setText(mDecimalFormat.format(distance) + "km");
  }

  /**
   * 比赛结束倒计时
   */
  private void startScheduleCountDown() {
    AppLogger.d("startScheduleCountDown");
    if(countDownExecutor != null) {
      AppLogger.d("startScheduleCountDown 2");
      stopScheduleCountDown();
    }
    countDownExecutor = Executors.newScheduledThreadPool(1);
    countDownExecutor.scheduleAtFixedRate(() -> {
      if(!residueTimeStop) {
        isTickRunning.set(true);
        AppLogger.d( "--countDownExecutor-- matchCountdownTime = " + matchCountdownTime);
        if(matchCountdownTime >= 0) {
          //服务器计时有可能比较慢，为了一直发数据，因此还需要定时去取数据并上传,直到结束赛事
          if(matchCountdownTime > 0) {
            matchCountdownTime -= 1;
          }
          reFlashCount += 1;

          if(curMatchStatus == MatchStatusConstant.RUN) {
            AppLogger.d( "--countDownExecutor-- isMatchStart = " + curMatchStatus);
            //开始未摇也刷新列表
            if(userPlayId == 0) {
              if(reFlashCount % 5 == 0) {
                requestShakeMatchBase();
              }
            }
            if (matchCountdownTime == 0) {
              mHandler.sendEmptyMessageDelayed(1, 2000);
            }
          } else {
            //未开赛也刷新列表
            if(userPlayId == 0 && reFlashCount % 5 == 0) {
              requestShakeMatchBase();
            }
            if(matchCountdownTime == 0) {
              //开赛倒计时结束,则发送协议再次请求
              curMatchStatus = MatchStatusConstant.RUN;
              mHandler.sendEmptyMessageDelayed(1, 2000);
            }
          }
          mHandler.sendEmptyMessage(0);
        }
      }
    }, 1, 1, TimeUnit.SECONDS);
  }

  private void stopScheduleCountDown() {
    AppLogger.d("stopScheduleCountDown 1");
    if(countDownExecutor != null) {
      try {
        countDownExecutor.shutdown();
        AppLogger.d("stopScheduleCountDown 2");
        if (!countDownExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
          AppLogger.d("stopScheduleCountDown 3");
          // 超时的时候向线程池中所有的线程发出中断(interrupted)。
          countDownExecutor.shutdownNow();
        }
        isTickRunning.set(false);
      }catch (InterruptedException e) {
        countDownExecutor.shutdownNow();
      }
    }
  }

  private void showSectionHorse(ShakeMatchModel.ShakeItem shakeItem) {
    String name = "anima_horse_" + (shakeItem.getIndex() + 1);
    int resourceId = getResources().getIdentifier(name, "drawable", getPackageName());
    ivHorse.setBackgroundResource(resourceId);
    AnimationDrawable animationDrawable = (AnimationDrawable) ivHorse.getBackground();
    if(animationDrawable != null) {
      animationDrawable.stop();
      animationDrawable.start();
    }

    //多少号 马-------------
    tvHorseName.setText(String.format(getString(R.string.lbl_input_match_horse), shakeItem.getIndex() + 1, shakeItem.getTitle()));
    tvHorseAttachPerson.setText(String.format(getString(R.string.lbl_input_match_person_num), String.valueOf(shakeItem.getNum())));
  }

  private void horseStop() {
    AnimationDrawable animationDrawable = (AnimationDrawable) ivHorse.getBackground();
    if(animationDrawable != null) {
      animationDrawable.stop();
    }
  }

  private void selectShakeMatchItem(ShakeMatchModel.ShakeItem shakeItem) {
    horseIndex = shakeItem.getIndex() + 1;
    horseTitle = shakeItem.getTitle();
    shakeGroupId = shakeItem.getShake_group_id();
    sysShakeId = shakeItem.getSys_shake_id();
    lyHorses.selectMe(shakeGroupId);
    lyHorses.notifyDataSetChanged();
  }

  @Override
  public void onItemClick(Object data, int index) {
    if(data instanceof ShakeMatchModel.ShakeItem) {
      selectShakeMatchItem(((ShakeMatchModel.ShakeItem)data));
      showSectionHorse((ShakeMatchModel.ShakeItem)data);
    }
  }

  private void showConfirmDialog() {
    String msg = String.format(String.valueOf(getResources().getText(R.string.lbl_input_match_horse_selected)), horseIndex, horseTitle);
    ConfirmDialog.show(this, msg, getResources().getText(R.string.btn_confirm2).toString(), () -> {
      requestMatchSignUp(shakeGroupId);
    });
  }

  public void showSnackBarChanged() {
    BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
    if (connectedDevice == null) {
      showSnackBarNotConnect(getString(R.string.wall_ball_to_connected));
      return;
    }
    String deviceName = connectedDevice.getName();
    deviceName = DeviceUtils.getDeviceNickname(this, deviceName);
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
