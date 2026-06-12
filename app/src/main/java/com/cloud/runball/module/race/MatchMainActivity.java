package com.cloud.runball.module.race;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.constant.BallStatusConstant;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.dialog.ConfirmDialog;
import com.cloud.runball.model.ErrSpeed;
import com.cloud.runball.model.ListPkItem;
import com.cloud.runball.model.PKDataDetailModel;
import com.cloud.runball.model.PkInfoModel;
import com.cloud.runball.model.PkUserDataModel;
import com.cloud.runball.model.PlayOverModel;
import com.cloud.runball.model.UserPlayModel;
import com.cloud.runball.module.home.AddDeviceInfoActivity;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonTipDialog;
import com.cloud.runball.module_bluetooth.constant.ServiceNoticeConstant;
import com.cloud.runball.module_bluetooth.constant.ServiceSendConstant;
import com.cloud.runball.module_bluetooth.data.event.BallRunDetail;
import com.cloud.runball.module_bluetooth.data.event.ServiceNoticeEvent;
import com.cloud.runball.module_bluetooth.data.event.ServiceSendEvent;
import com.cloud.runball.module_bluetooth.utils.BleUtils;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.BallUtils;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.utils.Constants;
import com.cloud.runball.dialog.ExitDialog;
import com.cloud.runball.dialog.PKFinishDialog;
import com.cloud.runball.dialog.PKLoadingDialog;
import com.cloud.runball.dialog.PKResultDialog;
import com.cloud.runball.dialog.PKStartDialog;
import com.cloud.runball.service.websocket.WebSocketServiceManager;
import com.cloud.runball.widget.CarMoveImageView;
import com.cloud.runball.widget.CircleTransform;
import com.cloud.runball.widget.MagicTextView2;
import com.cloud.runball.widget.MoveSurfaceView;
import com.cloud.runball.widget.PointerImageView;
import com.cloud.runball.widget.SpeedCircleImageView;
import com.google.gson.Gson;
import com.littlejie.circleprogress.CircleProgress;
import com.squareup.picasso.Picasso;
import com.cloud.runball.databinding.ActivityMatchMainBinding;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
/* ButterKnife removed: using ViewBinding instead */
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: MatchMainActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/8 17:31
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/8 17:31
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
@SuppressWarnings("AliMissingOverrideAnnotation")
public class MatchMainActivity extends BaseActivity {

  private ActivityMatchMainBinding binding;

  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();

  private PlayInfo playInfo = null;

  private AssociationCommonTipDialog loadupCommonDialog = null;

  FrameLayout fyRoot;

  CarMoveImageView carMine;

  CarMoveImageView carOther;

  FrameLayout fy_2pk;

  ImageView img_mine_avatar;

  ImageView img_other_avatar;

  ImageView img_exit;

  FrameLayout fy_team;

  TextView tvMine;

  TextView tvOther;

  //下面是比赛进度，总长度为8KM
  LinearLayout lySeekBar;

  ImageView ivBottom;


  SeekBar seekBarMine;

  SeekBar seekBarOther;

  //公路
  MoveSurfaceView moveSurfaceView;

  TextView tvTime;

  TextView tvMaxDistanceTag;

  TextView tvMaxDistance;

  TextView tvMaxSpeedTag;

  TextView tvMaxSpeed;

  //下面是转圈面板
  CircleProgress circle_progress_bar;

  MagicTextView2 tvSpeedRPMFormat;

  PointerImageView ivPointer;

  SpeedCircleImageView ivSpeedCircle;

  TextView tvMineNamePk;

  TextView tvMineNameTeam;

  TextView tvOtherNamePk;

  TextView tvOtherNameTeam;

  TextView tvPower;

  //作弊提示
  Boolean[] err_speedsTarget = new Boolean[]{ false, false, false, false, false, false, false, false, false, false };

  PkInfoModel pk_info;

  List<PkUserDataModel> red_list = new ArrayList<>();
  List<PkUserDataModel> blue_list = new ArrayList<>();

  String pk_room_number;
  //运动id+开始运动时间
  long user_play_id = 0;
  long user_play_detail_id_1 = 0;
  long user_play_detail_id_2 = 0;
  long start_time = 0;
  /**
   * 最高转速
   */
  int mHighSpeedRPM = 0;

  /**
   * 总圈数
   */
  int mTotalCircle = 0;

  /**
   * 总圈数不变次数
   */
  int comCircleCount = 0;

  //本地保存的转速，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
  List<Integer> speedCache = new ArrayList<>();

  //本地保存的总圈数，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
  List<Integer> circleCache = new ArrayList<>();

  //格式化数字
  DecimalFormat mDecimalFormat = new DecimalFormat("0.000");

  //间隔多少秒上报一次数据
  public static final int UPDATE_DATA_INTERVAL = 5;

  int mRpmSpeed=0;

  //比赛倒计时
  int keepCountTime = 0;
  int mKeepPlayTime=0;

  String pk_room_id;
  String user_group;

  double mMine_distance = 0;
  double mOther_distance = 0;
  int pk_type = -1;
  String user_id;
  final int ON_MESSAGE = 0x11;
  final int ON_SPEED = 0x12;
  boolean started = false;
  int pk_max_person = 1;

  int red_max_person = 1;
  int blue_max_person = 1;

  int pk_start_time = (int) (System.currentTimeMillis() / 1000);
  int pk_stop_time = (int) (System.currentTimeMillis() / 1000);

  long user_pk_list_id = 0;

  private AtomicBoolean matchPlaying = new AtomicBoolean(false);

  int is_abnormal = 0;

  /**
   * 默认总比赛总长度（为进度条提供可视化）
   */
  private float defaultTotalMeter = 5f;

  String otherAvatar = "";

  View startView = null;
  PKStartDialog mPKStartDialog = null;

  /**
   * 是否断线重连进来
   */
  boolean isReconnect = false;

  // 当前摇球状态
  private int curBallStatus = BallStatusConstant.IDLE;

  // todo 是否接收到pk结果，防止自己尚未发送结束标记，服务器就下发结束了用
  private boolean isResult = false;

  Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == ON_MESSAGE) {
        if (getSelfIsReady() == 0 && mPKStartDialog!=null) {
          mPKStartDialog.toggleInitReady(startView);
        }
      } else if (msg.what == ON_SPEED) {
        Bundle bundle = msg.getData();
        if (bundle != null) {
          ArrayList<Integer> speedArray = bundle.getIntegerArrayList("speed");
          if (speedArray != null && speedArray.size() > 0) {
            //不断取速度并设置汽车移动
            //AppLogger.d("----------------------------------下发车数据-------------------------------------------------"+speedArray.toString());
            int blue_speed = speedArray.get(0);
            carOther.setValue(blue_speed, false);
            speedArray.remove(0);
            //再次发送
            Message targetMsg = mHandler.obtainMessage();
            targetMsg.what = ON_SPEED;
            Bundle data = new Bundle();
            data.putIntegerArrayList("speed", speedArray);
            targetMsg.setData(data);
            mHandler.sendMessageDelayed(targetMsg, 520);
          }
        }
      }
    }
  };

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_match_main;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMatchMainBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {
    EventBus.getDefault().register(this);
  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_add_match);
  }

  @Override
  protected void initView() {
    // Bind frequently used views early
    fyRoot = binding.fyRoot;
    carMine = binding.carMine;
    carOther = binding.carOther;
    fy_2pk = binding.fy2pk;
    img_mine_avatar = binding.imgMineAvatar;
    img_other_avatar = binding.imgOtherAvatar;
    img_exit = binding.imgExit;
    fy_team = binding.fyTeam;
    tvMine = binding.tvMine;
    tvOther = binding.tvOther;
    lySeekBar = binding.lySeekBar;
    ivBottom = binding.ivBottom;
    seekBarMine = binding.seekBarMine;
    seekBarOther = binding.seekBarOther;
    moveSurfaceView = binding.moveSurfaceView;
    tvTime = binding.tvTime;
    tvMaxDistanceTag = binding.tvMaxDistanceTag;
    tvMaxDistance = binding.tvMaxDistance;
    tvMaxSpeedTag = binding.tvMaxSpeedTag;
    tvMaxSpeed = binding.tvMaxSpeed;
    circle_progress_bar = binding.circleProgressBar;
    tvSpeedRPMFormat = binding.tvSpeedRPMFormat;
    ivPointer = binding.ivPointer;
    ivSpeedCircle = binding.ivSpeedCircle;
    tvMineNamePk = binding.tvMineNamePk;
    tvMineNameTeam = binding.tvMineNameTeam;
    tvOtherNamePk = binding.tvOtherNamePk;
    tvOtherNameTeam = binding.tvOtherNameTeam;
    tvPower = binding.tvPower;
    // Replace ButterKnife @OnClick with explicit listener
    img_exit.setOnClickListener(this::onViewClicked);
    HiddenNavigation();
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    isResult = false;

    //断线重连传递过来的信息
    String pkdata = this.getIntent().getStringExtra("pkdata");
    if (!TextUtils.isEmpty(pkdata) && !isReconnect) {
      //显示加载框
      isReconnect=true;
      PKLoadingDialog.show(this);
      parsePkData(pkdata);
    } else {
      //判断是哪个位置过来的,这里可能是团队列表中传递过来
      started = this.getIntent().getBooleanExtra("started", false);
      String pkStart = this.getIntent().getStringExtra("pkStart");
      pk_room_number = this.getIntent().getStringExtra("pk_room_number");
      pk_info = (PkInfoModel) this.getIntent().getSerializableExtra("pk_info");
      pk_max_person = pk_info.getPk_max_person();

      red_max_person = pk_info.getRedList().size();
      blue_max_person = pk_info.getBlueList().size();

      //判断是双人PK还是多人PK
      pk_type = pk_info.getPk_type();
      //比赛时长
      keepCountTime = pk_info.getTime_long();
      if(keepCountTime >= 60 && keepCountTime < 180){
        defaultTotalMeter = 2.5f;
      }else if(keepCountTime >= 180 && keepCountTime <= 300){
        defaultTotalMeter = 5.0f;
      }else if(keepCountTime > 300){
        defaultTotalMeter = 8.0f;
      }

      //AppLogger.d("---keepCountTime---" + keepCountTime);

      pk_room_id = pk_info.getPk_room_id();
      user_id = String.valueOf(pk_info.getUser_id());
      //选出user_group
      user_group = this.getUser_group(pk_info, user_id);
      try {
        JSONObject pkdataObj = new JSONObject();
        pkdataObj.put("pk_room_id", pk_room_id);
        pkdataObj.put("user_id", user_id);
        pkdataObj.put("user_group", user_group);
        pkdata = pkdataObj.toString();
        //保存pkdata数据到本地
        SPUtils.put(getApplication(), "pkdata", pkdata);
        SPUtils.put(getApplication(), "pkdata_keepPlayTime", keepCountTime);
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      if (user_group.equalsIgnoreCase("red")) {
        red_list = pk_info.getRedList();
        blue_list = pk_info.getBlueList();
      } else {
        red_list = pk_info.getBlueList();
        blue_list = pk_info.getRedList();
      }

      if (!started) {
        initStartPK(pk_type == 0 ? PKStartDialog.PK : PKStartDialog.TEAM_PK);
        if (!TextUtils.isEmpty(pkStart)) {
          //弹出读秒倒计时
          if(mPKStartDialog!=null){
            mPKStartDialog.togglePlayTimer(startView);
          }
        }
      }

      if (!TextUtils.isEmpty(pkStart)) {
        parsePKStart(pkStart);
      }

      //其他页面传毒UI
      //判断双人赛还是团队赛
      showBothHeadDesc();
      //其他页面传递UI
      //更新用户头像
      initHeadAvatars();
    }

    if (!WebSocketServiceManager.getInstance().isOpen()) {
      Map<String, String> httpHeaders = new HashMap<String, String>();
      httpHeaders.put("Content-Type", "application/json");
      httpHeaders.put("token", WristBallRetrofitHelper.getInstance().getToken());
      httpHeaders.put("pkdata", pkdata);
      WebSocketServiceManager.getInstance().initSocketClient(httpHeaders);
    } else {
      AppLogger.d("-----isOpen websocket已经连接------" + pkdata);
    }

    //通用UI
    Typeface typeface = ResourcesCompat.getFont(this, R.font.rzsy_2);
    tvMaxDistanceTag.setTypeface(typeface);
    tvMaxDistance.setTypeface(typeface);
    tvMaxSpeedTag.setTypeface(typeface);
    tvMaxSpeed.setTypeface(typeface);
    tvTime.setTypeface(typeface);

    tvMine.setTypeface(typeface);
    tvOther.setTypeface(typeface);

    seekBarMine.setEnabled(false);
    seekBarOther.setEnabled(false);

    initWallData();

    carMine.loadCarImageView(R.mipmap.match_red_car, false);
    carOther.loadCarImageView(R.mipmap.match_blue_car, true);

//        //团队PK没有退出按钮
//        if (pk_type == 1) {
//            img_exit.setVisibility(View.GONE);
//        }

    //车向上的最顶位置
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lySeekBar.getLayoutParams();
    int startY = layoutParams.topMargin+100;

    //这里重新计算位置
    FrameLayout.LayoutParams bottomLayoutParams = (FrameLayout.LayoutParams) ivBottom.getLayoutParams();
    ivBottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        int endY=ivBottom.getTop()-carMine.getCarHeight();
        carMine.setMoveDistance(startY, endY);
        carOther.setMoveDistance(startY, endY);

        carMine.updateCarPos();
        carOther.updateCarPos();

        //初始化车辆开始栏杆
        moveSurfaceView.initLine(carMine.getCarStartY());

        // 如果只需要调整一次，需要把这个Listener移掉
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
          // 这个方法被deprecated了，不过为了兼容低版本，也只能硬着头皮用了
          ivBottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
          ivBottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      }
    });
    //int endY = ScreenWindowManager.heightScreen(this) - bottomLayoutParams.height - carMine.getCarHeight() -90-bottomLayoutParams.bottomMargin;


    //团队比赛已经开始了
    if (started) {
      SPUtils.put(getApplication(), "pkdata_startTime", pk_start_time);
      SPUtils.put(getApplication(), "pkdata_stopTime", pk_stop_time);
      mPKStartDialog=new PKStartDialog();
      startView = mPKStartDialog.show(this, pk_room_number, pk_type, () -> {
        cancelMatch();
        if(startView!=null){
          fyRoot.removeView(startView);
          startView=null;
        }
      });
      mHandler.postDelayed(() -> {
        mPKStartDialog.dismiss();
        if(startView!=null){
          fyRoot.removeView(startView);
          startView=null;
        }
        moveSurfaceView.move();
        startTimer();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_PLAY_MATCH_READY));
      }, 11 * 1000);

      fyRoot.addView(startView);
    }
  }

  private void parsePkData(String pkdata) {
    try {
      JSONObject pkdataObj = new JSONObject(pkdata);
      pk_room_id = pkdataObj.optString("pk_room_id");
      user_id = pkdataObj.optString("user_id");
      user_group = pkdataObj.optString("user_group");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 更新自身头像UI
   */
  public void updateUserInfo() {
    if(AppDataManager.getInstance().getUserInfoModel()!=null && AppDataManager.getInstance().getUserInfoModel().getUser_info()!=null){
      String url = AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img();
      if (url.startsWith("http")) {
        Picasso.with(this)
            .load(url).centerCrop().transform(new CircleTransform(this)).resize(480, 480)
            .into(img_mine_avatar);
      } else {
        Picasso.with(this)
            .load(Constant.getBaseUrl() + "/" + url).transform(new CircleTransform(this)).centerCrop().resize(480, 480)
            .into(img_mine_avatar);
      }
    }
  }

  /**
   * 初始化开始界面蒙版
   * @param pk_type
   */
  private void initStartPK(int pk_type) {
    if (!isFinishing() && mPKStartDialog == null) {
      mPKStartDialog =new PKStartDialog();
      startView = mPKStartDialog.show(this, pk_room_number, pk_type, true, view -> {
        if (!BleUtils.isConnectedDevice()) {
          showConnectStateDialog();
        } else {
          if (tempSpeed >= 300) {
            Toast.makeText(MatchMainActivity.this, R.string.tip_please_stop_and_wind_up_the_ball, Toast.LENGTH_SHORT).show();
          } else {
            mPKStartDialog.setWaitStatus(view);
            startPK(pk_room_id, user_id, user_group);
            mHandler.sendEmptyMessageDelayed(ON_MESSAGE, 3000);
          }
        }
      }, new PKStartDialog.DismissCallBack(){
        @Override
        public void dismiss() {
          if(startView!=null){
            fyRoot.removeView(startView);
            startView=null;
          }
        }
      }, new PKStartDialog.ExitCallBack() {
        @Override
        public void exitCallback() {
          //弹出框对话框让用户选择退出
          //弹出退出框，退出比赛
          if(startView!=null){
            fyRoot.removeView(startView);
            startView=null;
          }
          cancelMatch();
        }
      });
      fyRoot.addView(startView);
    }
  }

  private final static int REQUEST_CODE = 100;

  private void showConnectStateDialog() {
    ConfirmDialog.show(this, getString(R.string.tip_bluetooth_disconnected), getResources().getText(R.string.btn_connect).toString(), () -> {
      Intent it= new Intent(this, AddDeviceInfoActivity.class);
      startActivityForResult(it, REQUEST_CODE);
    });

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
    //红色外圈
    ivSpeedCircle.setValue(BallUtils.getAngleWithSpeedRPM(currentRpm));
  }

  @Override
  protected void setOnResult() {

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    keepCountTime = 0;
    mKeepPlayTime=0;
    matchPlaying.set(false);
    stopTimer();
    //移除PK比赛信息
    mHandler.removeCallbacksAndMessages(null);
    PKFinishDialog.dismiss();
    if(startView!=null && fyRoot!=null){
      fyRoot.removeView(startView);
      startView=null;
    }

    if(mPKStartDialog!=null){
      mPKStartDialog.dismiss();
    }
    PKResultDialog.dismiss();
    WebSocketServiceManager.getInstance().closeConnect();
    EventBus.getDefault().unregister(this);
  }


  private void removeMatchSpPrefe() {
    SPUtils.remove(getApplication(), "pkdata_startTime");
    SPUtils.remove(getApplication(), "pkdata_stopTime");
    SPUtils.remove(getApplication(), "pkdata");
    SPUtils.remove(getApplication(), "pkdata_keepPlayTime");
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    AppLogger.d("--onBackPressed--");
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    super.onKeyDown(keyCode, event);
    AppLogger.d("--onKeyDown--");
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      PKResultDialog.dismiss();
      //弹出退出框，退出比赛
      ExitDialog.show(this, new ExitDialog.ConfirmCallBack() {
        @Override
        public void confirm() {
          AppLogger.d("--user_id--:" + user_id + ";pk_room_id=" + pk_room_id + ";user_group=" + user_group);
          stopTimer();
          deletePK(pk_room_id, user_id, user_group);
          WebSocketServiceManager.getInstance().closeConnect();
          removeMatchSpPrefe();
          finish();
        }
      });
      return false;
    } else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){

    }
    return true;
  }

  private int tempSpeed;

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onServiceNoticeEvent(ServiceNoticeEvent event) {
    switch (event.getCode()) {
      case ServiceNoticeConstant.CODE_SCAN_START: {

      } break;
      case ServiceNoticeConstant.CODE_SCAN_DEVICE: {

      } break;
      case ServiceNoticeConstant.CODE_SCAN_FINISHED: {

      } break;
      case ServiceNoticeConstant.CODE_CONNECT_START: {

      } break;
      case ServiceNoticeConstant.CODE_CONNECT_SUCCESS: {

      } break;
      case ServiceNoticeConstant.CODE_CONNECT_FAIL: {

      } break;
      case ServiceNoticeConstant.CODE_CONNECT_FINISHED: {
        tempSpeed = 0;

        curBallStatus = BallStatusConstant.IDLE;
        setSpeedRPMBoardAnim(0,true);
        tvPower.setText("0%");
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUN_START: {
        curBallStatus = BallStatusConstant.RUNNING;
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUNNING: {
        tempSpeed = ((BallRunDetail) event.getData()).getSpeed();

        curBallStatus = BallStatusConstant.RUNNING;
        //接收到摇跑球转动下发的数据
        if (!matchPlaying.get()) {
          return;
        }

        BallRunDetail ballDetail = (BallRunDetail) event.getData();
        int circle = ballDetail.getCircle();
        int speed = ballDetail.getSpeed();
        int runningTime = ballDetail.getTime();

        mRpmSpeed = speed;
        //最大转速
        mHighSpeedRPM = Math.max(mHighSpeedRPM, speed);
        //总圈数
        if (circle != mTotalCircle) {
          mTotalCircle = circle;
          comCircleCount = 0;
        } else {
          if (speed <= 10) {
            comCircleCount += 1;
          }
        }

        //根据下发数据直接更新UI
        if (curBallStatus == BallStatusConstant.RUNNING) {
          //总圈数+转速
          circleCache.add(circle);
          speedCache.add(speed);

          //这里还需要判断从N突然变为0的情况，这样才不显得突兀,以总圈数为基准并强制设置rpm偏移
          if (comCircleCount > 0) {
            speed = 0;
            Log.d("PRETTY_LOGGER", "--腕力球停止了-,不摇了--" + System.currentTimeMillis());
            setSpeedRPMBoardAnim(speed, false, true);
            setPlayingBoard(mHighSpeedRPM, mTotalCircle);
            initSpeedRPMBoardAnim();
            return;
          }
          setSpeedRPMBoardAnim(speed, true, true);
          setPlayingBoard(mHighSpeedRPM, mTotalCircle);

          //这里设置红车或者蓝车速度
          if (pk_info != null) {
            carMine.setValue(speed, true);
          }
        }
      } break;
      case ServiceNoticeConstant.CODE_NOTIFY_RUN_FINISH: {
        tempSpeed = 0;
        curBallStatus = BallStatusConstant.IDLE;
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


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(MessageEvent event) {
    if (event.getEvetId() == MessageEvent.ON_PKConnected) {
      //重新连接,需要根据状态设置
    } else if (event.getEvetId() == MessageEvent.ON_PKListChange) {
      //这里如果是断线重连,也是应该恢复现场
      String pkListChangeStr = (String) event.getObject();
      parsePKpkListChange(pkListChangeStr);
      //根据用户状态判断是否显示对话框(准备)
      if (getSelfIsReady() != 1) {
        AppLogger.d("-------------------ON_PKListChange--ready---------------------");
        if (mPKStartDialog!=null && mPKStartDialog.isShowing()) {
          mPKStartDialog.toggleInitReady(startView);
        }
      }
      if (red_list.size() <= 0 && blue_list.size() <= 0) {
        PKLoadingDialog.dismiss();
        WebSocketServiceManager.getInstance().closeConnect();
        removeMatchSpPrefe();
        finish();
      } else {
        if (PKLoadingDialog.isShowing()) {
          PKLoadingDialog.dismiss();
        }
      }
    } else if (event.getEvetId() == MessageEvent.ON_bind_again) {
      PKLoadingDialog.dismiss();
      //断线重连后
      String pkBindAgainStr = (String) event.getObject();
      try {
        ParsePkBindAgain(pkBindAgainStr);
      } catch (Exception ex) {
        AppLogger.d("---MessageEvent.ON_bind_again--:" + ex.getMessage());
      }
    } else if (event.getEvetId() == MessageEvent.ON_PKBetween) {
      if (mPKStartDialog != null && mPKStartDialog.isShowing()) {
        mPKStartDialog.dismiss();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_PLAY_START));
        if(startView != null){
          fyRoot.removeView(startView);
          startView  =null;
        }
      }
      //服务器传递过来的腕力球数据
      String pkBetween = (String) event.getObject();
      //解析并设置车的速度
      parsePKBetween(pkBetween);
    } else if (event.getEvetId() == MessageEvent.ON_PKStart) {
      EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CIRCLE_CLEAR));
      AppLogger.d("----------MatchMainActivity---所有人都点击开始，下发------------");
      String pkStart = (String) event.getObject();
      parsePKStart(pkStart);
      SPUtils.put(getApplication(), "pkdata_startTime", pk_start_time);
      SPUtils.put(getApplication(), "pkdata_stopTime", pk_stop_time);
      //弹出读秒倒计时
      if(startView != null && mPKStartDialog != null){
        mPKStartDialog.togglePlayTimer(startView);
      }
    }else if(event.getEvetId() == MessageEvent.ON_SEND_HIDDEN_TIME){
      if(startView != null && mPKStartDialog != null){
        mPKStartDialog.hiddenToggleTime(startView);
      }
    } else if(event.getEvetId() == MessageEvent.ON_SEND_PLAY_START){
      startTimer();
      //开赛的时候路开始运动
      moveSurfaceView.move();
      EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_PLAY_MATCH_READY));
    } else if (event.getEvetId() == MessageEvent.ON_PKResult) {
      isResult = true;
      //关闭比赛结果等待页面
      PKFinishDialog.dismiss();
      String pkResult = (String) event.getObject();
      //AppLogger.d(pkResult);
      try {
        //用户PK结束，弹出胜败对话框
        parsePKResult(pkResult);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } else if (event.getEvetId() == MessageEvent.ON_PKError) {
      //
    } else if (event.getEvetId() == MessageEvent.ON_INIT_DATA) {
      initWallData();
    } else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_MATCH_READY) {
      //开赛运动准备，不同于主页面
      initWallData2();
      initSpeedRPMBoardAnim();
      setPlayingBoard(0, 0);
      setPlayingTimeBoard(keepCountTime);
      requestStartPlay(String.valueOf(user_pk_list_id));
    }




//    else if (event.getEvetId() == MessageEvent.STATE_DISCONNECTED) {
//      //位置归零
//      setSpeedRPMBoardAnim(0,true);
//      tvPower.setText("0%");
//    }else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_DATA) {
//      //接收到摇跑球转动下发的数据
//      if (!matchPlaying.get()) {
//        return;
//      }
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
//      if (App.self().isBallPlaying()) {
//        //总圈数+转速
//        circleCache.add(tCircle);
//        speedCache.add(rpmSpeed);
//
//        //这里还需要判断从N突然变为0的情况，这样才不显得突兀,以总圈数为基准并强制设置rpm偏移
//        if (comCircleCount > 0) {
//          rpm = 0;
//          Log.d("PRETTY_LOGGER", "--腕力球停止了-,不摇了--" + System.currentTimeMillis());
//          setSpeedRPMBoardAnim(rpm, false, true);
//          setPlayingBoard(mHighSpeedRPM, mTotalCircle);
//          initSpeedRPMBoardAnim();
//          return;
//        }
//        setSpeedRPMBoardAnim(rpm, true, true);
//        setPlayingBoard(mHighSpeedRPM, mTotalCircle);
//
//        //这里设置红车或者蓝车速度
//        if (pk_info != null) {
//          carMine.setValue(rpm, true);
//        }
//      }
//    }
    else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_TIME_2) {
      //倒计时时间
      int keepCountTime = event.getKeepTime();
      setPlayingTimeBoard(keepCountTime);
      //如果时间达到制定比赛时间，则停止运动
      if (pk_info != null) {
        //AppLogger.d("-------------------判断是否停止比赛，如果通知比赛，则采用不同方案--------keepCountTime=" + keepCountTime + ";pk_room_id=" + pk_room_id+";user_play_id="+user_play_id);
        if (keepCountTime == 0) {
          stopTimer();
          //停止结束时停止路(跟IOS统一，路也在动)
          moveSurfaceView.stop();
          //float meter = getTotalMeter();
          AppLogger.d("-----时间到比赛停止，上传停止数据----");
          if (isResult) {
            return;
          }
          PKFinishDialog.show(this, () -> {
            finish();
          });
//          //todo 临时的，要改
//          mHandler.postDelayed(() -> {
//            WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
//            HashMap<String, Object> map = new HashMap<>(1);
//            map.put("pk_room_id", pk_room_id);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
//            Observable<PKDataDetailModel> observable = apiServer.myPKInfo(requestBody);
//            disposable.add(
//                observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<PKDataDetailModel>() {
//                  @Override
//                  public void onSuccess(PKDataDetailModel pKDataDetailModel) {
//                    try {
//                      if(pKDataDetailModel!=null){
//                        parsePKResultgg(pKDataDetailModel);
//                      }
//                    } catch (Exception ex) {
//                      ex.printStackTrace();
//                    }
//                  }
//
//                  @Override
//                  public void onError(int code, String msg) {
//
//                  }
//                })
//            );
//          }, 3000);
        }else{
          if (isResult) {
            return;
          }
          cheatTip(mRpmSpeed);
          //如果没有摇也要传递数据
          if(mKeepPlayTime % 2 == 0 && mKeepPlayTime != 0) {
            if(circleCache.size() == 0) {
              circleCache.add(0);
            }

            if(speedCache.size() == 0) {
              speedCache.add(0);
            }

            //AppLogger.d("----------MatchMainActivity--运动过程--------------"+circleCache.toString());
            //总圈数
            List<Integer> tempCircles = new ArrayList<>();
            tempCircles.addAll(circleCache);
            circleCache.clear();
            //转速
            List<Integer> tempSpeeds = new ArrayList<>();
            tempSpeeds.addAll(speedCache);
            speedCache.clear();
            //这里传递数据进去
            EventBus.getDefault().post(new MessageEvent(MessageEvent.PLAY_ING, tempCircles, tempSpeeds));
          }
        }
      }
    } else if (event.getEvetId() == MessageEvent.PLAY_START) {
      //开始运动
      Log.d("PRETTY_LOGGER", "--------开始运动--MatcbMainActivity--PLAY_START-------");
    } else if (event.getEvetId() == MessageEvent.PLAY_ING) {
      //运动过程中传递数据(差异数据)
      if (event.getCircles().size() > 0 && keepCountTime > 0) {
        //websocke上报
        playingBetweenWebSocket(pk_room_id, user_id, user_group, event.getCircles(), event.getSpeeds());
        //http上报
        playingBetweenHttp(user_play_id, start_time, event.getCircles(), event.getSpeeds());

        Log.d("jjj", "user_play_id = " + user_play_id + ", start_time = " + start_time + ", Circles =" + event.getCircles() + ", Speeds = " + event.getSpeeds());
      }
    }
//    else if (event.getEvetId() == MessageEvent.ACTION_BLUETOOTH_DEVICE) {
//
//    } else if (event.getEvetId() == MessageEvent.ON_POWER_ELE) {
//      //获得下发电量,这里在开始和结束获得
//      int power = event.getKeepTime();
//      tvPower.setText(power + "%");
//    }
  }

  Timer timer;

  /**
   * 计时器开始计时
   */
  public void startTimer() {
    stopTimer();
    if (timer == null) {
      timer = new Timer();
    }
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        matchPlaying.set(true);
        mKeepPlayTime += 1;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_PLAY_TIME_2, keepCountTime));
        keepCountTime -= 1;
      }
    }, 100, Constants.UPDATE_PERIOD_TIME);
  }

  /**
   * 停止计时
   */
  public void stopTimer() {
    matchPlaying.set(false);
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  /**
   * 初始化摇跑球相关数据
   */
  private void initWallData() {
    mRpmSpeed = 0;
    mKeepPlayTime = 0;
    mHighSpeedRPM = 0;
    mTotalCircle = 0;
    comCircleCount = 0;
    is_abnormal = 0;
    circleCache.clear();
    speedCache.clear();
    tvTime.setText("");
    tvMaxSpeed.setText("0");
    tvMaxDistance.setText("0.000");
    setPlayingTimeBoard(keepCountTime);
    setSpeedRPMBoardAnim(0, true);
  }

  private void initWallData2() {
    mRpmSpeed = 0;
    is_abnormal = 0;
    mKeepPlayTime = 0;
    mHighSpeedRPM = 0;
    mTotalCircle = 0;
    comCircleCount = 0;
    circleCache.clear();
    speedCache.clear();
  }

  /**
   * 更新运动数据
   * @param maxRpm      最高转速
   * @param totalCircle 累计圈数(单位千米)
   */
  public void setPlayingBoard(int maxRpm, int totalCircle) {
    //最高速率
    tvMaxSpeed.setText(String.valueOf(maxRpm));
    //直径为5.28cm ，周长16.588cm,单位 km
    float meter = BallUtils.getTotalMeter(totalCircle) / 1000;
    tvMaxDistance.setText(mDecimalFormat.format(meter));
    playVoice(meter);
    //这里设置汽车进度
    //seekBarMine.setProgress((int) (meter * 100 / 8));
  }

  /**
   * 根据不同距离提示不同声音(单位KM)
   *
   * @param meter
   */
  private void playVoice(float meter) {

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
   * 更新运动时间数据
   * @param keepTime
   */
  public void setPlayingTimeBoard(int keepTime) {
    tvTime.setText(TimeUtils.formatDuration2(keepTime));
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
    //红色外圈
    ivSpeedCircle.setValue(BallUtils.getAngleWithSpeedRPM(currentRpm));
  }

  /**
   * 开始运动
   */
  private void requestStartPlay(String user_pk_list_id) {
//    HashMap<String, Object> map = new HashMap<>(1);
//    map.put("user_pk_list_id", user_pk_list_id);
//    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
//    Observable<UserPlayModel> observable = apiServer.startPlayWithPK(requestBody);
//    observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserPlayModel>() {
//      @Override
//      public void onSuccess(UserPlayModel userPlayModel) {
//        AppLogger.d("---开始运动---requestStartPlay=result");
//        if (userPlayModel != null && userPlayModel.getUser_play() != null) {
//          user_play_id = userPlayModel.getUser_play().getUser_play_id();
//          AppLogger.d("----------------------------------------------------user_play_id="+user_play_id);
//          start_time = userPlayModel.getUser_play().getStart_time();
//
//
//          playInfo = new PlayInfo();
//          playInfo.setStatus(PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT);
//          playInfo.setSqlId(user_play_id);
//          playInfo.setUserPkListId(Long.parseLong(user_pk_list_id));
//          playInfo.setSource(PlayingDataConstant.PlayingSource.PK);
//          playInfo.setCreatedUid(Long.parseLong(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id()));
//          playInfo.setStartTime(start_time);
//          playInfo.setInterval(500);
//
//          BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
//          if (connectedDevice != null) {
//            playInfo.setMac(connectedDevice.getAddress());
//          }
//          sqlService.insertOrUpdatePlayInfo(playInfo);
//
//
//          EventBus.getDefault().post(new MessageEvent(MessageEvent.PLAY_START));
//        }
//      }
//
//      @Override
//      public void onError(int code, String msg) {
//        AppLogger.d(msg);
//      }
//    });

    user_play_id = System.currentTimeMillis();
    start_time = System.currentTimeMillis() / 1000;

    playInfo = new PlayInfo();
    playInfo.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT);
    playInfo.setSqlId(user_play_id);
    playInfo.setUserPkListId(Long.parseLong(user_pk_list_id));
    playInfo.setSource(PlayingDataConstant.PlayingSource.PK);
    playInfo.setCreatedUid(Long.parseLong(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id()));
    playInfo.setStartTime(start_time);
    playInfo.setInterval(500);

    BluetoothDevice connectedDevice = BleUtils.getConnectedDevice();
    if (connectedDevice != null) {
      playInfo.setMac(connectedDevice.getAddress());
    }
    sqlService.insertOrUpdatePlayInfo(playInfo);


    EventBus.getDefault().post(new MessageEvent(MessageEvent.PLAY_START));
  }


  private void playingBetweenWebSocket(String pk_room_id, String user_id, String user_group, List<Integer> circle_detail, List<Integer> speed_detail) {
    //websocket的上报
    betweenPlayPk(pk_room_id, user_id, user_group, circle_detail, speed_detail);
  }

  private void playingBetweenHttp(long user_play_id, long start_time, List<Integer> circle_detail, List<Integer> speed_detail) {
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

      playingBetweenHttp(user_play_id, start_time, tempCircle_detail, tempSpeeds);
    }
  }

  /**
   * 运动过程中(客户端上传数据)
   * @param user_play_id  当前运动ID
   * @param start_time    当前运动开始时间
   * @param circle_detail 当前运动中每个时刻的圈数，[3400, 8137, 17861, 15506, 9780]
   */
  private void playingBetweenHttp(long user_play_id, long start_time, int[] circle_detail, int[] speed_detail) {
    HashMap<String, Object> map = new HashMap<>(4);
    map.put("user_play_id", user_play_id);
    map.put("start_time", start_time);
    map.put("circle_detail", circle_detail);
    map.put("speed_detail", speed_detail);

    if (BuildConfig.DEBUG) {
      AppLogger.d("【上传数据】运动过程中上传:user_play_id=" + user_play_id + ";start_time=" + start_time + ";circle_detail=" + Arrays.toString(circle_detail) + ";speed_detail=" + Arrays.toString(speed_detail));
    }



    long userPlayId = playInfo.getSqlId();
    for (int itemData : speed_detail) {
      SpeedDetail newSpeedDetail = new SpeedDetail();
      newSpeedDetail.setUserPlayId(userPlayId);
      newSpeedDetail.setSpeed(itemData);
      sqlService.insertOrUpdateSpeedDetail(newSpeedDetail);
    }
    long stopTime = System.currentTimeMillis() / 1000;
    playInfo.setStopTime(stopTime);
    playInfo.setCircleCount(mTotalCircle);
    playInfo.setMaxSpeed(mHighSpeedRPM);
    //       playInfo.setMaxEndurance();
    float distance = BallUtils.getTotalMeter(mTotalCircle);
    playInfo.setIsAbnormal(is_abnormal);
    playInfo.setDistance(distance);
    playInfo.setDuration(mKeepPlayTime);
    playInfo.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT);
    sqlService.insertOrUpdatePlayInfo(playInfo);



//    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
//    Observable<ResponseBody> observable = apiServer.playing(requestBody);
//    observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
//      @Override
//      public void onSuccess(ResponseBody responseBody) {
//
//      }
//
//      @Override
//      public void onError(int code, String msg) {
//        //Logger.d(msg);
//      }
//    });

    //todo


  }

  /**
   * 计算还剩下的时间
   * @param pk_start_time
   * @param pk_stop_time
   * @param time_long
   * @return
   */
  private int curNetSurplusTime(int pk_start_time,int pk_stop_time,int time_long){
    int curTime=(int)(System.currentTimeMillis()/1000);
    if(pk_stop_time>curTime && curTime>pk_start_time){
      return pk_stop_time-curTime;
    }
    return time_long;
  }

  private int LocalSurplusTime(int time_long){
    int curTime=(int)(System.currentTimeMillis()/1000);
    pk_start_time=(int)SPUtils.get(getApplication(), "pkdata_startTime", pk_start_time);
    pk_stop_time=(int)SPUtils.get(getApplication(), "pkdata_stopTime", pk_stop_time);
    int surplusTime=time_long-(curTime-pk_start_time);

    AppLogger.d("time_long="+time_long);
    AppLogger.d("pk_start_time="+pk_start_time);
    AppLogger.d("pk_stop_time="+pk_stop_time);
    AppLogger.d("surplusTime="+surplusTime);
    if(surplusTime>0 && surplusTime<time_long){
      return surplusTime;
    }
    return time_long;
  }

  private void adjustdefaultTotalMeter(int time_long){
    //比赛时长
    int keepCountTime = time_long;
    if(keepCountTime>=60 && keepCountTime<180){
      defaultTotalMeter=2.5f;
    }else if(keepCountTime>=180 && keepCountTime<=300){
      defaultTotalMeter=5.0f;
    }else if(keepCountTime>300){
      defaultTotalMeter=8.0f;
    }
  }

  private void parsePKpkListChange(String pkListChangeStr) {
    try {
      JSONObject data = new JSONObject(pkListChangeStr);
      int code = data.optInt("code");
      if (code == 1) {
        JSONObject list = data.optJSONObject("data").optJSONObject("list");
        if (list != null) {

          int tempPk_type = list.optInt("pk_type", -1);
          pk_max_person = list.optInt("pk_max_person", 1);

          JSONArray redArray = list.optJSONArray("red");
          red_list.clear();
          red_list.addAll(parseTeamItemArray(redArray));

          JSONArray blueArray = list.optJSONArray("blue");
          blue_list.clear();
          blue_list.addAll(parseTeamItemArray(blueArray));

          red_max_person=red_list.size();
          blue_max_person=blue_list.size();

          //这里有下发开赛时间
          pk_start_time = list.optInt("pk_start_time", pk_start_time);
          pk_stop_time = list.optInt("pk_stop_time", pk_stop_time);
          int time_long=list.optInt("time_long", 60);
          //有断线重连场景
          if(isReconnect && pk_stop_time>(System.currentTimeMillis()/1000)){
            isReconnect=false;
            keepCountTime=curNetSurplusTime(pk_start_time,pk_stop_time,time_long);
            adjustdefaultTotalMeter(time_long);
            //如果未开始计时，则开始计时
            if(moveSurfaceView!=null){
              if(!moveSurfaceView.isMoving()){
                EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_PLAY_START));
              }else{
                //已经在运动了
              }
            }
          }

          if (tempPk_type != -1) {
            pk_type = tempPk_type;
            showBothHeadDesc();
          }

        }
      }
    } catch (JSONException ex) {
      ex.printStackTrace();
    }

    //显示头像
    updateHeadAvatars();
  }

  private int getSelfIsReady() {
    int isReady = 0;
    if (user_group.equalsIgnoreCase("red")) {
      if (red_list.size() > 0) {
        for (PkUserDataModel redModel : red_list) {
          if (redModel.getUser_id().equalsIgnoreCase(user_id)) {
            isReady = redModel.getIs_ready();
            break;
          }
        }
      }
    } else {
      if (blue_list.size() > 0) {
        for (PkUserDataModel redModel : blue_list) {
          if (redModel.getUser_id().equalsIgnoreCase(user_id)) {
            isReady = redModel.getIs_ready();
            break;
          }
        }
      }
    }
    return isReady;
  }

  public void parsePKStart(String startStr) {
    try {
      JSONObject data = new JSONObject(startStr);
      int code = data.optInt("code");
      if (code == 1) {
        JSONObject list = data.optJSONObject("data").optJSONObject("list");
        if (list != null) {
          JSONArray redArray = list.optJSONArray("red");
          red_list.clear();
          red_list.addAll(parseTeamItemArray(redArray));
          JSONArray blueArray = list.optJSONArray("blue");
          blue_list.clear();
          blue_list.addAll(parseTeamItemArray(blueArray));

          pk_start_time = list.optInt("pk_start_time", pk_start_time);
          pk_stop_time = list.optInt("pk_stop_time", pk_stop_time);

          AppLogger.d("pk_start_time="+pk_start_time);
          AppLogger.d("pk_stop_time ="+pk_stop_time);

          if (pk_type == 0) {
            tvOtherNamePk.setVisibility(View.VISIBLE);
            tvOtherNameTeam.setVisibility(View.GONE);
            tvMineNamePk.setVisibility(View.VISIBLE);
            tvMineNameTeam.setVisibility(View.GONE);
            if (user_group.equalsIgnoreCase("red")) {
              tvMineNamePk.setText(red_list.get(0).getUser_name());
              tvOtherNamePk.setText(blue_list.get(0).getUser_name());
            } else {
              tvMineNamePk.setText(blue_list.get(0).getUser_name());
              tvOtherNamePk.setText(red_list.get(0).getUser_name());
            }
          } else if (pk_type == 1) {
            tvOtherNamePk.setVisibility(View.GONE);
            tvOtherNameTeam.setVisibility(View.VISIBLE);
            tvMineNamePk.setVisibility(View.GONE);
            tvMineNameTeam.setVisibility(View.VISIBLE);
            if (user_group.equalsIgnoreCase("red")) {
              tvMineNameTeam.setText(data.optJSONObject("data").optJSONObject("list").optString("group_red_title"));
              tvOtherNameTeam.setText(data.optJSONObject("data").optJSONObject("list").optString("group_blue_title"));
            } else {
              tvMineNameTeam.setText(data.optJSONObject("data").optJSONObject("list").optString("group_blue_title"));
              tvOtherNameTeam.setText(data.optJSONObject("data").optJSONObject("list").optString("group_red_title"));
            }
          }

          red_max_person = red_list.size();
          blue_max_person = blue_list.size();

        }
      }
    } catch (JSONException ex) {
      ex.printStackTrace();
    }

    //更新用户头像
    updateHeadAvatars();
    setPlayingTimeBoard(keepCountTime);
  }


  private void parsePKBetween(String pkBetweenStr) {
    double red_distance = 0;
    double blue_distance = 0;

    ArrayList<Integer> red_speed = new ArrayList<>();
    ArrayList<Integer> blue_speed = new ArrayList<>();

    try {
      JSONObject data = new JSONObject(pkBetweenStr);
      int code = data.optInt("code");
      if (code == 1) {
        JSONObject list = data.optJSONObject("data").optJSONObject("list");
        if (list != null) {
          //单位米
          red_distance = list.optDouble("red_distance", 0.0f);
          AppLogger.d("---------------red_distance------------"+red_distance);
          blue_distance = list.optDouble("blue_distance", 0.0f);
          //速度
          //暂时取第一个数据
          if (list.optJSONArray("red_speed") != null) {
            int len = list.optJSONArray("red_speed").length();
            for (int i = 0; i < len; i++) {
              red_speed.add(list.optJSONArray("red_speed").optInt(i));
            }
          }

          //暂时取第一个数据
          if (list.optJSONArray("blue_speed") != null) {
            int len = list.optJSONArray("blue_speed").length();
            for (int i = 0; i < len; i++) {
              blue_speed.add(list.optJSONArray("blue_speed").optInt(i));
            }
          }
        }
      }
    } catch (JSONException ex) {
      ex.printStackTrace();
    }

    //判断颜色
    if (user_group.equalsIgnoreCase("red")) {
      mMine_distance= Math.max(mMine_distance, red_distance);
      mOther_distance= Math.max(mOther_distance, blue_distance);
      //设置他队的汽车速度
      //carOther.setValue(blue_speed, false);
      Message msg = mHandler.obtainMessage();
      msg.what = ON_SPEED;
      Bundle data = new Bundle();
      data.putIntegerArrayList("speed", blue_speed);
      msg.setData(data);
      mHandler.sendMessage(msg);
    } else {
      mMine_distance = Math.max(mMine_distance, blue_distance);
      mOther_distance = Math.max(mOther_distance, red_distance);
      //设置他队的汽车速度
      //carOther.setValue(red_speed, false);
      Message msg = mHandler.obtainMessage();
      msg.what = ON_SPEED;
      Bundle data = new Bundle();
      data.putIntegerArrayList("speed", red_speed);
      msg.setData(data);
      mHandler.sendMessage(msg);
    }

    //显示距离，双人+团队
    if (pk_type == 0) {
      //默认3分钟是5公里，那么1分钟默认设置2.5公里
      seekBarMine.setProgress((int) (mMine_distance * 100 / defaultTotalMeter));
      seekBarOther.setProgress((int) (mOther_distance * 100 / defaultTotalMeter));
    } else {

      seekBarMine.setProgress((int) (mMine_distance * 100 /(defaultTotalMeter*Math.max(red_max_person,blue_max_person)) ));
      seekBarOther.setProgress((int) (mOther_distance * 100 / (defaultTotalMeter*Math.max(red_max_person,blue_max_person)) ));

      //seekBarMine.setProgress((int) (mMine_distance * 100 * Math.max(red_max_person,blue_max_person) / defaultTotalMeter ));
      //seekBarOther.setProgress((int) (mOther_distance * 100 * Math.max(red_max_person,blue_max_person) / defaultTotalMeter ));
    }
  }

  private void initHeadAvatars() {
    if (pk_type == 0) {
      //双人
      updateUserInfo();
      //别人
      if (user_group.equalsIgnoreCase("red")) {
        if (blue_list.size() > 0) {
          user_pk_list_id = blue_list.get(0).getUser_pk_list_id();
          updateHeadAvatarOther(blue_list.get(0).getUser_img());
        }
      } else {
        if (red_list.size() > 0) {
          user_pk_list_id = red_list.get(0).getUser_pk_list_id();
          updateHeadAvatarOther(red_list.get(0).getUser_img());
        }
      }
    } else {
      //团队
      if (user_group.equalsIgnoreCase("red")) {
        if (red_list.size() > 0) {
          user_pk_list_id = red_list.get(0).getUser_pk_list_id();
          tvMine.setText("x" + red_max_person);
        }
        if (blue_list.size() > 0) {
          user_pk_list_id = blue_list.get(0).getUser_pk_list_id();
          tvOther.setText("x" + blue_max_person);
        }
      } else {
        if (blue_list.size() > 0) {
          user_pk_list_id = blue_list.get(0).getUser_pk_list_id();
          tvOther.setText("x" + blue_max_person);
        }
        if (red_list.size() > 0) {
          user_pk_list_id = red_list.get(0).getUser_pk_list_id();
          tvMine.setText("x" + red_max_person);
        }
      }
    }
  }


  private void updateHeadAvatars() {
    if (pk_type == 0) {
      //双人
      updateUserInfo();
      //判断自己和对方所属的队伍
      if (user_group.equalsIgnoreCase("red")) {
        if (red_list.size() > 0) {
          updateHeadAvatarMine(red_list.get(0).getUser_img());
        }
        if (blue_list.size() > 0) {
          otherAvatar=blue_list.get(0).getUser_img();
          updateHeadAvatarOther(otherAvatar);
        }
      } else {
        if (blue_list.size() > 0) {
          updateHeadAvatarMine(blue_list.get(0).getUser_img());
        }

        if (red_list.size() > 0) {
          otherAvatar=red_list.get(0).getUser_img();
          updateHeadAvatarOther(otherAvatar);
        }
      }
    } else {
      //团队显示x人数
      if (user_group.equalsIgnoreCase("red")) {
        if (red_list.size() > 0) {
          tvMine.setText("x" + red_max_person);
        }
        if (blue_list.size() > 0) {
          tvOther.setText("x" + blue_max_person);
        }
      } else {
        if (blue_list.size() > 0) {
          tvMine.setText("x" + blue_max_person);
        }
        if (red_list.size() > 0) {
          tvOther.setText("x" + red_max_person);
        }
      }
    }
  }

  private void updateHeadAvatarMine(String url) {
    if (url.startsWith("http")) {
      Picasso.with(this)
          .load(url).centerCrop().transform(new CircleTransform(this)).resize(480, 480)
          .into(img_mine_avatar);
    } else {
      Picasso.with(this)
          .load(Constant.getBaseUrl() + "/" + url).transform(new CircleTransform(this)).centerCrop().resize(480, 480)
          .into(img_mine_avatar);
    }
  }

  private void updateHeadAvatarOther(String url) {
    if (url.startsWith("http")) {
      Picasso.with(this)
          .load(url).centerCrop().transform(new CircleTransform(this)).resize(480, 480)
          .into(img_other_avatar);
    } else {
      Picasso.with(this)
          .load(Constant.getBaseUrl() + "/" + url).transform(new CircleTransform(this)).centerCrop().resize(480, 480)
          .into(img_other_avatar);
    }
  }

  private String getUser_group(PkInfoModel pk_info, String user_id) {
    String user_group = "";
    int blue_size = pk_info.getBlueList().size();
    for (int i = 0; i < blue_size; i++) {
      if (pk_info.getBlueList().get(i).getUser_id().equalsIgnoreCase(String.valueOf(user_id))) {
        user_group = pk_info.getBlueList().get(i).getUser_group();
        break;
      }
    }

    int red_size = pk_info.getRedList().size();
    for (int i = 0; i < red_size; i++) {
      if (pk_info.getRedList().get(i).getUser_id().equalsIgnoreCase(String.valueOf(user_id))) {
        user_group = pk_info.getRedList().get(i).getUser_group();
        break;
      }
    }

    return user_group;
  }

  private ArrayList<PkUserDataModel> parseTeamItemArray(JSONArray teamArray) throws JSONException {
    ArrayList<PkUserDataModel> dataModels = new ArrayList<>();
    if (teamArray != null && teamArray.length() > 0) {
      int len = teamArray.length();
      for (int index = 0; index < len; index++) {

        PkUserDataModel model = new PkUserDataModel();
        JSONObject itemObject = teamArray.optJSONObject(index);
        model.setStatus(itemObject.optInt("status"));
        long tempuser_pk_list_id = itemObject.optLong("user_pk_list_id");
        model.setUser_pk_list_id(tempuser_pk_list_id);

        //区分队伍
        if(user_group.equalsIgnoreCase(itemObject.optString("user_group"))){
          user_pk_list_id = tempuser_pk_list_id;
        }

        model.setPk_room_id(itemObject.optString("pk_room_id"));
        model.setUser_group(itemObject.optString("user_group"));
        model.setUser_name(itemObject.optString("user_name"));
        model.setUser_img(itemObject.optString("user_img"));
        model.setFd(itemObject.optInt("fd"));
        model.setUser_id(itemObject.optString("user_id"));
        model.setIs_stop(itemObject.optInt("is_stop"));
        model.setIs_ready(itemObject.optInt("is_ready"));
        model.setDuration(itemObject.optInt("duration"));
        model.setCircle_count(itemObject.optInt("circle_count"));
        dataModels.add(model);
      }
    }
    return dataModels;
  }

  private void ParsePkBindAgain(String bind_againStr) throws JSONException {
    JSONObject data = new JSONObject(bind_againStr);
    int code = data.optInt("code");
    if (code == 1) {
      JSONObject list = data.optJSONObject("data").optJSONObject("list");
      if (list != null) {
        keepCountTime = list.optInt("time_long", 3 * 60);

        AppLogger.d("-------ParsePkBindAgain--------keepCountTime--" + keepCountTime);

        user_group = list.optString("user_group");
        if (user_group.equalsIgnoreCase("red")) {
          mMine_distance = list.optDouble("red_distance", 0.0f);
          mOther_distance = list.optDouble("blue_distance", 0.0f);
        } else {
          mOther_distance = list.optDouble("red_distance", 0.0f);
          mMine_distance = list.optDouble("blue_distance", 0.0f);
        }

        pk_room_number = list.optString("pk_room_number");
        pk_type = list.optInt("pk_type", -1);

        //pk_result_type = list.optInt("pk_result_type", 0);
        pk_max_person = list.optInt("pk_max_person", 1);

        pk_start_time = list.optInt("pk_start_time", 0);
        pk_stop_time = list.optInt("pk_stop_time", 0);

        JSONArray redArray = list.optJSONArray("red");
        red_list.clear();
        red_list.addAll(parseTeamItemArray(redArray));
        JSONArray blueArray = list.optJSONArray("blue");
        blue_list.clear();
        blue_list.addAll(parseTeamItemArray(blueArray));

        red_max_person = red_list.size();
        blue_max_person = blue_list.size();

        //其他页面传毒UI
        //判断双人赛还是团队赛
        showBothHeadDesc();
        //其他页面传递UI
        //更新用户头像
        initHeadAvatars();

        startTimer();
      }

    }
  }


  private void showBothHeadDesc() {
    if (pk_type == 0) {
      //双人
      fy_2pk.setVisibility(View.VISIBLE);
      fy_team.setVisibility(View.GONE);
      updateUserInfo();
    } else {
      fy_2pk.setVisibility(View.GONE);
      fy_team.setVisibility(View.VISIBLE);
    }
  }

  public void parsePKResultgg(PKDataDetailModel pKDataDetailModel) throws JSONException {

    //这里有个问题，自己尚未发送结束标记，服务器就下发结束了
    float meter = BallUtils.getTotalMeter(mTotalCircle);
    //上传时间为500*6 毫秒
    if(circleCache.size()==0){
      circleCache.add(0);
    }

    if(speedCache.size()==0){
      speedCache.add(0);
    }


    playStop(user_play_id, user_pk_list_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
//    finishPK(pk_room_id, user_id, user_group);

    //表盘设置为初始状态（表盘数据保持在最后一帧）
    //initSpeedRPMBoardAnim();
    matchPlaying.set(false);

    //收到服务器返回结果，直接关闭socket
    WebSocketServiceManager.getInstance().closeConnect();

    List<ListPkItem> result_info = pKDataDetailModel.getList();
      if (result_info != null) {

        ListPkItem resultInfo1 = result_info.get(0);


        ListPkItem resultInfo2 = result_info.get(1);


        int redDuration, blueDuration;
        String redDistance = null, blueDistance = null;
        if ("red".equals(resultInfo1.getUser_group())) {
          redDuration = resultInfo1.getDuration();  //红队总用时(已经格式化)
          redDistance = resultInfo1.getDistance();  //红队总距离km
        } else {
          blueDuration = resultInfo1.getDuration();
          blueDistance = resultInfo1.getDistance();
        }

        if ("red".equals(resultInfo2.getUser_group())) {
          redDuration = resultInfo2.getDuration();  //红队总用时(已经格式化)
          redDistance = resultInfo2.getDistance();  //红队总距离km
        } else {
          blueDuration = resultInfo2.getDuration();
          blueDistance = resultInfo2.getDistance();
        }


        //是否胜利
        boolean isWin = false;
        if (resultInfo1.getIs_win() == 1) {
          isWin = true;
        }

        String mineAvatar = AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img();


        String mineDistance = redDistance;
        String otherDistance = blueDistance;
        if (user_group.equalsIgnoreCase("blue")) {
          mineDistance = blueDistance;
          otherDistance = redDistance;
          if (red_list.size() > 0) {
            otherAvatar = red_list.get(0).getUser_img();
          }
        } else {
          if (blue_list.size() > 0) {
            otherAvatar = blue_list.get(0).getUser_img();
          }
        }

        //把服务器距离数据同步到客户端
        tvMaxDistance.setText(mineDistance);

        //不论是主动结束还是被动，都需要重设时间
        keepCountTime = 0;
        stopTimer();
        //显示胜利对话框,这里还需要新增其他头像数据
        //判断双人赛还是团队赛
        if (pk_type == 0) {
          //双人(红蓝)
          if (!PKResultDialog.isShowing()) {
            PKFinishDialog.dismiss();
            String mineName = tvMineNamePk.getText().toString();
            String otherName = tvOtherNamePk.getText().toString();
            PKResultDialog.show(MatchMainActivity.this,mineName, otherName, isWin, mineDistance, otherDistance, mineAvatar, otherAvatar, new PKResultDialog.DismissCallBack() {
              @Override
              public void dismiss() {
                stopTimer();
                removeMatchSpPrefe();
                finish();
              }
            });
          }
        } else {
          //团队
          if (!PKResultDialog.isShowing()) {
            PKFinishDialog.dismiss();
            String mineName = tvMineNameTeam.getText().toString();
            String otherName = tvOtherNameTeam.getText().toString();
            PKResultDialog.show(MatchMainActivity.this, mineName, otherName, true, isWin, mineDistance, otherDistance, new PKResultDialog.DismissCallBack() {
              @Override
              public void dismiss() {
                stopTimer();
                removeMatchSpPrefe();
                finish();
              }
            });
          }
        }

    }
  }

  public void parsePKResult(String resultStr) throws JSONException {

    //这里有个问题，自己尚未发送结束标记，服务器就下发结束了
    float meter = BallUtils.getTotalMeter(mTotalCircle);
    //上传时间为500*6 毫秒
    if(circleCache.size() == 0) {
      circleCache.add(0);
    }

    if(speedCache.size() == 0) {
      speedCache.add(0);
    }


//    playStop(user_play_id, user_pk_list_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
//    finishPK(pk_room_id, user_id, user_group);

//    uploadLocalMatchPlayV3(playInfo);

    if (loadupCommonDialog == null) {
      loadupCommonDialog = new AssociationCommonTipDialog(this);
      loadupCommonDialog.setContent(getString(R.string.tip_uploading_result));
    }
    uploadByUserPlayId();

    //表盘设置为初始状态（表盘数据保持在最后一帧）
    //initSpeedRPMBoardAnim();
    matchPlaying.set(false);

    //收到服务器返回结果，直接关闭socket
    WebSocketServiceManager.getInstance().closeConnect();

    JSONObject data = new JSONObject(resultStr);
    int code = data.optInt("code");
    if (code == 1) {
      JSONObject result_info = data.optJSONObject("data").optJSONObject("list");
      if (result_info != null) {
        String group_win = result_info.optString("group_win");             //胜利red,blue
        result_info.optString("group_red_duration");                       //红队总用时(已经格式化)
        String redDistance = result_info.optString("group_red_distance");  //红队总距离km

        result_info.optString("group_blue_duration");
        String blueDistance = result_info.optString("group_blue_distance");

        //是否胜利
        boolean isWin = false;
        if (user_group.equalsIgnoreCase(group_win)) {
          isWin = true;
        }

        String mineAvatar = AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img();


        String mineDistance = redDistance;
        String otherDistance = blueDistance;
        if (user_group.equalsIgnoreCase("blue")) {
          mineDistance = blueDistance;
          otherDistance = redDistance;
          if (red_list.size() > 0) {
            otherAvatar = red_list.get(0).getUser_img();
          }
        } else {
          if (blue_list.size() > 0) {
            otherAvatar = blue_list.get(0).getUser_img();
          }
        }

        //把服务器距离数据同步到客户端
        tvMaxDistance.setText(mineDistance);

        //不论是主动结束还是被动，都需要重设时间
        keepCountTime = 0;
        stopTimer();
        //显示胜利对话框,这里还需要新增其他头像数据
        //判断双人赛还是团队赛
        if (pk_type == 0) {
          //双人(红蓝)
          if (!PKResultDialog.isShowing()) {
            PKFinishDialog.dismiss();
            String mineName = tvMineNamePk.getText().toString();
            String otherName = tvOtherNamePk.getText().toString();
            PKResultDialog.show(MatchMainActivity.this,mineName, otherName, isWin, mineDistance, otherDistance, mineAvatar, otherAvatar, new PKResultDialog.DismissCallBack() {
              @Override
              public void dismiss() {
                stopTimer();
                removeMatchSpPrefe();
                finish();
              }
            });
          }
        } else {
          //团队
          if (!PKResultDialog.isShowing()) {
            PKFinishDialog.dismiss();
            String mineName = tvMineNameTeam.getText().toString();
            String otherName = tvOtherNameTeam.getText().toString();
            PKResultDialog.show(MatchMainActivity.this, mineName, otherName, true, isWin, mineDistance, otherDistance, new PKResultDialog.DismissCallBack() {
              @Override
              public void dismiss() {
                stopTimer();
                removeMatchSpPrefe();
                finish();
              }
            });
          }
        }
      }
    }
  }

  private void bindAgainPK(String pk_room_id, String user_group) {
    try {
      JSONObject msgObject = new JSONObject();
      msgObject.put("event", "bind_again");
      msgObject.put("pk_room_id", pk_room_id);
      msgObject.put("user_group", user_group);
      String sendMsg = msgObject.toString();
      WebSocketServiceManager.getInstance().sendMsg(sendMsg);
    } catch (JSONException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 用户取消准备
   *
   * @param pk_room_id
   * @param user_id
   * @param user_group
   */
  private void cancelStartPK(String pk_room_id, String user_id, String user_group) {
    try {
      JSONObject msgObject = new JSONObject();
      msgObject.put("event", "pk_unready");
      msgObject.put("pk_room_id", pk_room_id);
      msgObject.put("user_id", user_id);
      msgObject.put("user_group", user_group);
      String sendMsg = msgObject.toString();
      WebSocketServiceManager.getInstance().sendMsg(sendMsg);
    } catch (JSONException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 用户点击开始准备
   *
   * @param pk_room_id
   * @param user_group
   */
  private void startPK(String pk_room_id, String user_id, String user_group) {
    try {
      JSONObject msgObject = new JSONObject();
      msgObject.put("event", "pk_ready");
      msgObject.put("pk_room_id", pk_room_id);
      msgObject.put("user_id", user_id);
      msgObject.put("user_group", user_group);
      String sendMsg = msgObject.toString();
      WebSocketServiceManager.getInstance().sendMsg(sendMsg);
    } catch (JSONException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 取消PK
   *
   * @param pk_room_id
   * @param user_id
   */
  private void deletePK(String pk_room_id, String user_id, String user_group) {
    try {
      JSONObject msgObject = new JSONObject();
      msgObject.put("event", "pk_cancel");
      msgObject.put("pk_room_id", pk_room_id);
      msgObject.put("user_id", user_id);
      msgObject.put("user_group", user_group);
      String sendMsg = msgObject.toString();
      WebSocketServiceManager.getInstance().sendMsg(sendMsg);
    } catch (JSONException ex) {
      ex.printStackTrace();
    }
  }

  private void betweenPlayPk(String pk_room_id, String user_id, String user_group, List<Integer> circle_detail, List<Integer> speed_detail) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("event", "between_play");
    map.put("pk_room_id", pk_room_id);
    map.put("user_id", user_id);
    map.put("user_group", user_group);
    map.put("circle_detail", circle_detail);
    map.put("speed_detail", speed_detail);
    map.put("is_abnormal", is_abnormal);
    String sendMsg = new JSONObject(map).toString();
    WebSocketServiceManager.getInstance().sendMsg(sendMsg);
  }

  /**
   * 用户完成PK
   * @param pk_room_id
   * @param user_group
   */
  private void finishPK(String pk_room_id, String user_id, String user_group) {
    try {
      JSONObject msgObject = new JSONObject();
      msgObject.put("event", "pk_stop");
      msgObject.put("pk_room_id", pk_room_id);
      msgObject.put("user_id", user_id);
      msgObject.put("user_group", user_group);
      String sendMsg = msgObject.toString();
      WebSocketServiceManager.getInstance().sendMsg(sendMsg);
    } catch (JSONException ex) {
      ex.printStackTrace();
    }
  }


  private void playStop(long user_play_id, long user_pk_list_id, long start_time, int interval, List<Integer> circle_detail, List<Integer> speed_detail, float distance, int circle_count, int speed_max) {
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

    playStop(user_play_id, user_pk_list_id, start_time, interval, tempCircles, tempSpeeds, distance, circle_count, speed_max);
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
  private void playStop(long u_play_id, long user_pk_list_id, long u_start_time, int interval, int[] circle_detail, int[] speed_detail, float distance, int circle_count, int speed_max) {

    HashMap<String, Object> map = new HashMap<>();
    map.put("user_play_id", u_play_id);
    map.put("start_time", u_start_time);
    map.put("interval", interval);
    map.put("circle_detail", circle_detail);
    map.put("speed_detail", speed_detail);
    map.put("user_pk_list_id", String.valueOf(user_pk_list_id));
    map.put("distance", distance);
    map.put("circle_count", circle_count);
    map.put("speed_max", speed_max);
    map.put("is_abnormal", is_abnormal);
    map.put("stop_time", System.currentTimeMillis()/1000);
    AppLogger.d("【上传数据】运动结束上传:user_play_id=" + u_play_id + ";user_pk_list_id=" + user_pk_list_id + ";start_time=" + u_start_time + ";interval=" + interval + ";circle_detail=" + Arrays.toString(circle_detail));

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<PlayOverModel> observable = apiServer.playStop(requestBody);
    observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<PlayOverModel>() {
      @Override
      public void onSuccess(PlayOverModel playOverModel) {
        //if (playOverModel != null) {
        //    user_play_id = 0;
        //    start_time = 0;
        //}
        finishPK(pk_room_id, user_id, user_group);
        uploadLocalMatchPlayV3(playInfo);
      }

      @Override
      public void onError(int code, String msg) {
        AppLogger.d(msg);
      }
    });

  }

  private int user_play_detail_id_cont = 0;

  private void uploadByUserPlayId() {
    if (user_play_detail_id_cont > 1) {
      user_play_detail_id_cont = 0;
      uploadLocalMatchPlayV3(playInfo);
      return;
    }
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("user_pk_list_id", user_pk_list_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserPlayModel> observable = apiServer.startPlayWithPK(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserPlayModel>() {
              @Override
              public void onSuccess(UserPlayModel userPlayModel) {
                AppLogger.d("---开始运动---requestStartPlay=result");
                if (userPlayModel != null && userPlayModel.getUser_play() != null) {
                  if (user_play_detail_id_cont == 0) {
                    user_play_detail_id_1 = (long) (userPlayModel.getUser_play().getUser_play_id() + (Math.random() * 1000));
                    user_play_detail_id_cont++;
                    uploadByUserPlayId();
                  } else if (user_play_detail_id_cont == 1){
                    user_play_detail_id_2 = (long) (userPlayModel.getUser_play().getUser_play_id() + (Math.random() * 1000));
                    user_play_detail_id_cont++;
                    uploadByUserPlayId();
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
                AssociationCommonDialog loadupErrorDialog = new AssociationCommonDialog(MatchMainActivity.this);
                loadupErrorDialog.setContent(getString(R.string.tip), getString(R.string.tip_upload_result_fail));
                loadupErrorDialog.addBtn(getString(R.string.btn_upload_later), false, commonDialog -> {
                  user_play_detail_id_cont = 0;
                  commonDialog.dismiss();
                  finish();
                });
                loadupErrorDialog.addBtn(getString(R.string.btn_upload_again), true, commonDialog -> {
                  commonDialog.dismiss();
                  user_play_detail_id_cont = 0;
                  uploadByUserPlayId();
                });
              }
        })
    );
  }



  private void uploadLocalMatchPlayV3(PlayInfo data) {
    if (data == null) {
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

    HashMap<String, Object> map = new HashMap<>();
    map.put("user_play_id", user_play_detail_id_1);
    map.put("user_play_detail_id", user_play_detail_id_2);
    map.put("exponent_molecular", data.getExponentMolecular());
    map.put("user_pk_list_id", data.getUserPkListId());
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
//    map.put("duration", data.getDuration());
    // 说要传成60秒。。。。
    map.put("duration", 60);
    map.put("distance", data.getDistance());
    map.put("circle_count", data.getCircleCount());
    map.put("exponent_denominator", data.getExponentDenominator());
    map.put("exponent_speed_max", data.getExponentSpeedMax());
    map.put("speed_detail", new Gson().toJson(speedDetailArr));
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<Object> observable = apiServer.uploadLocalMatchPlayV3(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Object>() {
              @Override
              public void onSuccess(Object o) {
                if (loadupCommonDialog != null) {
                  loadupCommonDialog.dismiss();
                  loadupCommonDialog = null;
                }
//                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
//                sqlService.insertOrUpdatePlayInfo(data);
                sqlService.deletePlayInfo(data.getSqlId());
                sqlService.deleteSpeedDetail(data.getSqlId());
              }
              @Override
              public void onError(int code, String msg) {
                if (loadupCommonDialog != null) {
                  loadupCommonDialog.dismiss();
                  loadupCommonDialog = null;
                }
                AssociationCommonDialog loadupErrorDialog = new AssociationCommonDialog(MatchMainActivity.this);
                loadupErrorDialog.setContent(getString(R.string.tip), getString(R.string.tip_upload_result_fail));
                loadupErrorDialog.addBtn(getString(R.string.btn_upload_later), false, commonDialog -> {
                  commonDialog.dismiss();
                  finish();
                });
                loadupErrorDialog.addBtn(getString(R.string.btn_upload_again), true, commonDialog -> {
                  commonDialog.dismiss();
                  uploadLocalMatchPlayV3(playInfo);
                });
                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                sqlService.insertOrUpdatePlayInfo(data);
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
    observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
      @Override
      public void onSuccess(ResponseBody responseBody) {

      }

      @Override
      public void onError(int code, String msg) {
        AppLogger.d(msg);
      }
    });
  }
  /**
   * 作弊提示
   * @param rpmSpeed
   */
  private void cheatTip(int rpmSpeed){
    if (AppDataManager.getInstance().getErrSpeeds().size() > 0) {
      int len=AppDataManager.getInstance().getErrSpeeds().size();
      for(int index=0;index<len;index++){
        ErrSpeed err=AppDataManager.getInstance().getErrSpeeds().get(index);
        if ((int) (err.getTime()) == mKeepPlayTime && err.getMax_speed() <= rpmSpeed && !err_speedsTarget[index]) {
          Toast.makeText(this, R.string.data_err_tip, Toast.LENGTH_LONG).show();
          err_speedsTarget[index]=true;
          is_abnormal=1;
          updateAbnormal(user_play_id,is_abnormal);
          break;
        }
      }
    }
  }


  public void onViewClicked(View v) {
    if (v.getId() == R.id.img_exit) {
      //弹出退出框，退出比赛
      ExitDialog.show(this, new ExitDialog.ConfirmCallBack() {
        @Override
        public void confirm() {
          cancelMatch();
        }
      });
    }
  }

  private void cancelMatch(){
    removeMatchSpPrefe();
    stopTimer();
    AppLogger.d("--user_id--:" + user_id + ";pk_room_id=" + pk_room_id + ";user_group=" + user_group);
    if (keepCountTime >=0) {
      deletePK(pk_room_id, user_id, user_group);
    }

    float meter = BallUtils.getTotalMeter(mTotalCircle);
    //上传时间为500*6 毫秒
    if(circleCache.size()==0){
      circleCache.add(0);
    }
    if(speedCache.size()==0){
      speedCache.add(0);
    }
    if(user_pk_list_id!=0){
//      playStop(user_play_id, user_pk_list_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
    }
//    finishPK(pk_room_id, user_id, user_group);
    WebSocketServiceManager.getInstance().closeConnect();
    finish();
  }

}
