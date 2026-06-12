package com.cloud.runball.module.match;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.App;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.module.match.adapter.RankMatchMainAdapter;
import com.cloud.runball.bean.BlePackData;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.bean.RankGroupItem;
import com.cloud.runball.model.ErrSpeed;
import com.cloud.runball.model.PlayOverModel;
import com.cloud.runball.model.RankMatchInfo2;
import com.cloud.runball.model.UserPlayModel;
import com.cloud.runball.module.home.AddDeviceInfoActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.BallUtils;
import com.cloud.runball.utils.Constants;
import com.cloud.runball.dialog.MatchExitDialog;
import com.cloud.runball.dialog.MatchWinDialog;
import com.cloud.runball.widget.MagicTextView2;
import com.cloud.runball.widget.PointerImageView;
import com.google.gson.Gson;
import com.littlejie.circleprogress.CircleProgress;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cloud.runball.databinding.ActivityMatchMain2Binding;
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
public class MatchMainActivity2 extends BaseActivity {


    public static final int REQUEST_CODE = 100;

    private ActivityMatchMain2Binding binding;
    RecyclerView recyclerview;
    ImageView img_exit;
    //下面是转圈面板
    CircleProgress circle_progress_bar;
    MagicTextView2 tvSpeedRPMFormat;
    PointerImageView ivPointer;
    TextView tvPersonTime;
    TextView tvPersonDistance;
    TextView tvMatchCountdown;
    TextView tvTotalDistance;
    LinearLayout lyAction;
    TextView tvTip;
    TextView tvAction;
    TextView tvName;

    //运动id+开始运动时间
    long user_play_id = 0;
    int start_time = 0;
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

    int is_abnormal=0;

    //作弊提示
    Boolean[] err_speedsTarget = new Boolean[]{false, false, false,false, false, false,false, false, false,false};

    //本地保存的转速，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
    List<Integer> speedCache = new ArrayList<>();

    //本地保存的总圈数，以1000ms(即是1s)作为间隔,或者使用ArrayQueue
    List<Integer> circleCache = new ArrayList<>();

    //格式化数字
    DecimalFormat mDecimalFormat = new DecimalFormat("0.000");

    private AtomicBoolean matchPlaying = new AtomicBoolean(false);

    RankMatchMainAdapter mRankMatchMainAdapter;

    Handler mHandler = new Handler();

    List<RankGroupItem> list = new ArrayList<>();

    //赛事ID
    private String sys_match_id;
    //赛段ID
    private String matchs_stage_id;
    //所属队伍ID
    private String user_group_id;
    private String match_title;

    //是否停止比赛
    private boolean isStopMatch=false;

    private int mRpmSpeed=0;

    //比赛倒计时
    private int matchCountdownTime=0;
    //服务器下发的倒计时
    private int residue_time=0;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_match_main2;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMatchMain2Binding.inflate(inflater);
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
        recyclerview = binding.recyclerview;
        img_exit = binding.imgExit;
        circle_progress_bar = binding.circleProgressBar;
        tvSpeedRPMFormat = binding.tvSpeedRPMFormat;
        ivPointer = binding.ivPointer;
        tvPersonTime = binding.tvPersonTime;
        tvPersonDistance = binding.tvPersonDistance;
        tvMatchCountdown = binding.tvMatchCountdown;
        tvTotalDistance = binding.tvTotalDistance;
        lyAction = binding.lyAction.getRoot();
        tvTip = binding.lyAction.tvTip;
        tvAction = binding.lyAction.tvAction;
        tvName = binding.tvName;
        setInterceptEvent(true);
        setInterceptEvent(false);

        HiddenNavigation();
        sys_match_id = this.getIntent().getStringExtra("sys_match_id");
        matchs_stage_id = this.getIntent().getStringExtra("matchs_stage_id");
        user_group_id = this.getIntent().getStringExtra("user_group_id");
        match_title = this.getIntent().getStringExtra("match_title");

        tvName.setText(match_title);

        mRankMatchMainAdapter = new RankMatchMainAdapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        //添加Android自带的分割线
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mDividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.rank_match_divider));
        recyclerview.addItemDecoration(mDividerItemDecoration);
        recyclerview.setAdapter(mRankMatchMainAdapter);
        //区分自己的
        mRankMatchMainAdapter.selectMe(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id());

        initWallData();
        initWallUI();
        requestRankMatchBase(sys_match_id, user_group_id);
        //比赛倒计时
        startScheduleCountDown();

        // Replace @OnClick with listeners
        img_exit.setOnClickListener(this::onViewClicked);
        tvAction.setOnClickListener(this::onViewClicked);
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
        //ivSpeedCircle.setValue(getAngleWithSpeedRPM(currentRpm));
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    public void onResume(){
        super.onResume();
//        String name=App.self().getConnectDeviceName();
//        if(!TextUtils.isEmpty(name)){
//            showSnackBar(name);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mKeepPlayTime=0;
        matchPlaying.set(false);
        stopScheduleCountDown();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //运行时间
    int mKeepPlayTime=0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //弹出退出框，退出比赛
            MatchExitDialog.show(this, new MatchExitDialog.ConfirmCallBack() {
                @Override
                public void confirm() {
                    onExitMatch();
                    finish();
                }
            });
            return false;
        }
        return true;
    }

    private boolean isInterceptEvent=false;
    public void setInterceptEvent(boolean isInterceptEvent){
        this.isInterceptEvent=isInterceptEvent;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(isInterceptEvent){
            return;
        }

       if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_READY) {
            //运动准备
            initWallData2();
            initSpeedRPMBoardAnim();
            requestStartPlay();
        }else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_DATA) {
            int maxSpeed=event.getMaxSpeed();
            int rpmSpeed = event.getRpm();
            mRpmSpeed=rpmSpeed;
            int rpm = event.getSpeed2();
            int tCircle = event.getTotalCircle();
            //最大转速
            mHighSpeedRPM = Math.max(mHighSpeedRPM, maxSpeed);
            //总圈数
            if (tCircle != mTotalCircle) {
                mTotalCircle = tCircle;
                comCircleCount = 0;
            } else {
                if (rpm <= 10) {
                    comCircleCount += 1;
                }
            }

            //根据下发数据直接更新UI
//            if (App.self().isBallPlaying()) {
//                //总圈数+转速
//                circleCache.add(tCircle);
//                speedCache.add(rpmSpeed);
//                //这里还需要判断从N突然变为0的情况，这样才不显得突兀,以总圈数为基准并强制设置rpm偏移
//                if (comCircleCount > 0) {
//                    rpm = 0;
//                    Log.d("PRETTY_LOGGER", "--腕力球停止了-,不摇了--" + System.currentTimeMillis());
//                    setSpeedRPMBoardAnim(rpm, false, true);
//                    initSpeedRPMBoardAnim();
//
//                    /**避免不上传数据导致结果不下发**/
//                    if(user_play_id!=0){
//                        float meter = BallUtils.getTotalMeter(mTotalCircle);
//                        playStop(user_play_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
//                    }
//                    return;
//                }
//                setSpeedRPMBoardAnim(rpm, true, true);
//            }
        }
        else if (event.getEvetId() == MessageEvent.ON_SEND_PLAY_TIME_2) {
            //收到运动时间，这里只更新运动时间
           //更新个人累计时间
           if(!isStopMatch){
               cheatTip(mRpmSpeed);
               myDuration+=1;
               mKeepPlayTime+=1;
               double distance = myDistance + BallUtils.getTotalMeter(mTotalCircle) / 1000;
               updatePersonMatchLeft(myDuration,distance);
               updateMatchCountDown(matchCountdownTime);
           }

            if (mKeepPlayTime % 3 == 0 && mKeepPlayTime != 0 && !isStopMatch) {
                if (circleCache.size() == 0) {
                    circleCache.add(0);
                }

                if (speedCache.size() == 0) {
                    speedCache.add(0);
                }
                //AppLogger.d("-----------MatchMainActivity2-运动过程--------------" + circleCache.toString());
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

        } else if (event.getEvetId() == MessageEvent.PLAY_START) {
            //开始运动
            Log.d("PRETTY_LOGGER", "--------开始运动--MatchMainActivity2--PLAY_START-------");
        } else if (event.getEvetId() == MessageEvent.PLAY_ING) {
            //运动过程中传递数据(差异数据)
//            if (App.self().isBallPlaying() && event.getCircles().size() > 0 && mKeepPlayTime  > 0) {
//                //http上报
//                playingBetweenHttp(user_play_id, start_time, event.getCircles(), event.getSpeeds());
//            }
        } else if (event.getEvetId() == MessageEvent.PLAY_OVER) {
            //停止运动+数据还是显示在最终结果
           //停止运动+数据还是显示在最终结果
           stopRotateAnim();

           if(user_play_id!=0){
               float meter = BallUtils.getTotalMeter(mTotalCircle);
               playStop(user_play_id, start_time, Constants.UPDATE_PERIOD, event.getCircles(), event.getSpeeds(), meter, mTotalCircle, mHighSpeedRPM);
           }

           user_play_id=0;
           start_time=0;
           mHighSpeedRPM = 0;
           mTotalCircle = 0;
           comCircleCount = 0;

           circleCache.clear();
           speedCache.clear();

        } else if (event.getEvetId() == MessageEvent.ACTION_BLUETOOTH_DEVICE) {
           // todo 这样蓝牙需要整理修改
//            String spName = (String) SPUtils.get(this, SPUtils.KEY_MATCH_DEVICE, "");
//            Intent intent = event.getIntent();
//            String tmpDevName = intent.getStringExtra("name");
//            if (!TextUtils.isEmpty(spName) && spName.equalsIgnoreCase(tmpDevName)) {
//                App.self().disconnect();
//                String tmpMacAddress = intent.getStringExtra("address");
//                App.self().connectDelay(tmpMacAddress);
//            }
        } else if (event.getEvetId() == MessageEvent.STATE_CONNECTED) {
            String name = event.getDeviceName();
            showSnackBar(name);
        }else if (event.getEvetId() == MessageEvent.STATE_DISCONNECTED) {
           showSnackBar(getString(R.string.wall_ball_disconnected));
           user_play_id =0;
       }
    }


    private void showSnackBar(String name){
        SPUtils.put(this, SPUtils.KEY_MATCH_DEVICE, name);
        String str = String.format(getString(R.string.connected_device_finished2), name);
        showSnackBarAutoDismiss(str);
    }

    /**
     * 初始化摇跑球相关数据
     */
    private void initWallData() {
        mRpmSpeed=0;
        is_abnormal=0;
        mHighSpeedRPM = 0;
        mTotalCircle = 0;
        comCircleCount = 0;
        circleCache.clear();
        speedCache.clear();
    }

    private void initWallUI(){
        tvPersonTime.setText("00:00:00");
        tvPersonDistance.setText("0");
        tvMatchCountdown.setText("00:00:00");
        tvTotalDistance.setText("0km");
        setPlayingTimeBoard(0);
        setSpeedRPMBoardAnim(0, true);
    }

    private void initWallData2() {
        mRpmSpeed=0;
        is_abnormal=0;
        mHighSpeedRPM = 0;
        mTotalCircle = 0;
        comCircleCount = 0;
        circleCache.clear();
        speedCache.clear();
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
        //ivSpeedCircle.setValue(0);
    }

    /**
     * 更新运动时间数据
     *
     * @param keepPlayingTime
     */
    public void setPlayingTimeBoard(int keepPlayingTime) {
        tvPersonTime.setText(TimeUtils.formatDuration3(keepPlayingTime));
    }

    private void stopRotateAnim() {
        //ivSpeedCircle.stop();
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
        //ivSpeedCircle.setValue(getAngleWithSpeedRPM(currentRpm));
    }

    private void showSnackBar(CharSequence text) {
        lyAction.setVisibility(View.VISIBLE);
        tvTip.setText(text);
    }

    private void showSnackBarAutoDismiss(CharSequence text) {
        lyAction.setVisibility(View.VISIBLE);
        tvTip.setText(text);
    }

    private void startAddDeviceGuardActivity() {
        Intent it= new Intent(this, AddDeviceInfoActivity.class);
        startActivityForResult(it, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 开始运动
     */
    private void requestStartPlay() {
        if(isStopMatch){
            return;
        }
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("sys_match_id", sys_match_id);
        map.put("matchs_stage_id", matchs_stage_id);
        if(!TextUtils.isEmpty(user_group_id)){
            map.put("user_group_id", user_group_id);
        }
        map.put("start_time", System.currentTimeMillis()/1000);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<UserPlayModel> observable = apiServer.startPlayForMatch(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserPlayModel>() {
                @Override
                public void onSuccess(UserPlayModel userPlayModel) {
                    AppLogger.d("---开始运动---requestStartPlay=result");
                    if (userPlayModel != null && userPlayModel.getUser_play() != null) {
                        user_play_id = userPlayModel.getUser_play().getUser_play_id();
                        start_time = userPlayModel.getUser_play().getStart_time();
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.PLAY_START));
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                }
            })
        );
    }


    private void playingBetweenHttp(long user_play_id, int start_time, List<Integer> circle_detail, List<Integer> speed_detail) {
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
     *
     * @param user_play_id  当前运动ID
     * @param start_time    当前运动开始时间
     * @param circle_detail 当前运动中每个时刻的圈数，[3400, 8137, 17861, 15506, 9780]
     */
    private void playingBetweenHttp(long user_play_id, int start_time, int[] circle_detail, int[] speed_detail) {

        if(isStopMatch){
            return;
        }

        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

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
            map.put("show_all", 1);

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
                    try {
                        parsePlayBetween(responseBody);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                }
            })
        );
    }


    private void parsePlayBetween(ResponseBody responseBody) throws Exception {
        JSONObject jsonObject = new JSONObject(responseBody.string());
        int code = jsonObject.optInt("code", 0);
        if (code == 1) {
            JSONObject dataObject = jsonObject.optJSONObject("data");
            Gson gson = new Gson();
            AppLogger.d("----parseRankMatchResp----"+dataObject.toString());
            JSONObject resultObject = new JSONObject(dataObject.toString());
            if(resultObject.optInt("is_end",0)==1 && !residueTimeStop){
                //这里查询判断会有问题，不建议通过查询来判断
                residueTimeStop=true;
                isStopMatch=true;
                String matchs_end_tips=resultObject.optString("matchs_end_tips");
                String final_result_time=resultObject.optString("final_result_time");
                String match_user_join_num=resultObject.optString("match_user_join_num");
                String all_distince_value_format=resultObject.optString("all_distince_value_format");
                tvTotalDistance.setText(all_distince_value_format + "km");
                //达到赛点，发送结束
                if(user_play_id!=0){
                    float meter = BallUtils.getTotalMeter(mTotalCircle);
                    playStop(user_play_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
                }
                stopScheduleCountDown();
                updateResultMatchMainUI(matchs_end_tips,final_result_time,match_user_join_num,all_distince_value_format);
            }else{
                RankMatchInfo2 rankMatchInfo = gson.fromJson(dataObject.toString(), RankMatchInfo2.class);
                updateMatchMainUI(rankMatchInfo);
            }
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

        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

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

        AppLogger.d("【上传数据】运动结束上传:user_play_id=" + u_play_id + ";start_time=" + u_start_time + ";interval=" + interval + ";circle_detail=" + Arrays.toString(circle_detail));

        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<PlayOverModel> observable = apiServer.playStop(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<PlayOverModel>() {
                @Override
                public void onSuccess(PlayOverModel playOverModel) {
                    user_play_id = 0;
                    start_time = 0;
                }
                @Override
                public void onError(int code, String msg) {
                    user_play_id = 0;
                    start_time = 0;
                    AppLogger.d(msg);
                }
            })
        );
    }

    public void onViewClicked(View v) {
        if (v.getId() == R.id.img_exit) {
            //弹出退出框，退出比赛
            MatchExitDialog.show(this, () -> {
                onExitMatch();
                finish();
            });
        }else if(v.getId()==R.id.tvAction){
            startAddDeviceGuardActivity();
        }
    }

    private void onExitMatch() {
        if(!isStopMatch){
            float meter = BallUtils.getTotalMeter(mTotalCircle);
            playStop(user_play_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
        }
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
//                    Toast.makeText(this, R.string.data_err_tip, Toast.LENGTH_LONG).show();
                    err_speedsTarget[index]=true;
                    is_abnormal=1;
                    updateAbnormal(user_play_id,is_abnormal);
                    break;
                }
            }
        }
    }


    /**
     * 异常数据上传
     * @param user_play_id
     * @param is_abnormal
     */
    private void updateAbnormal(long user_play_id,int is_abnormal){
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
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
     * 查询比赛页面基本信息
     * @param sys_match_id
     * @param user_group_id
     */
    private void requestRankMatchBase(String sys_match_id, String user_group_id) {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("sys_match_id", sys_match_id);
        map.put("show_all", 1);
        map.put("user_group_id", user_group_id);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.rankMatchInfo(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try{
                        parseRankMatchResp(responseBody);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d("---------requestRankMatchBase-------" + msg);
                }
            })
        );
    }

    private void parseRankMatchResp(ResponseBody responseBody) throws IOException, JSONException {
        JSONObject jObject = new JSONObject(responseBody.string());
        int code = jObject.optInt("code", -1);
        if (code == 1) {
            JSONObject dataObject=jObject.optJSONObject("data");
            if(dataObject.optInt("code")==0 && !residueTimeStop){
                residueTimeStop=true;
                isStopMatch=true;
                String msg=dataObject.optString("msg");
                //弹框退出
                MatchExitDialog.show1(this, msg, () -> {
                    onExitMatch();
                    finish();
                });
            }else if(dataObject.optInt("code")==1){
                Gson gson=new Gson();
                AppLogger.d("----parseRankMatchResp----"+dataObject.toString());
                JSONObject resultObject = new JSONObject(dataObject.toString());
                if(resultObject.optInt("is_end",0)==1 && !residueTimeStop){
                    //这里查询判断会有问题，不建议通过查询来判断
                    residueTimeStop=true;
                    isStopMatch=true;
                    String matchs_end_tips=resultObject.optString("matchs_end_tips");
                    String final_result_time=resultObject.optString("final_result_time");
                    String match_user_join_num=resultObject.optString("match_user_join_num");
                    String all_distince_value_format=resultObject.optString("all_distince_value_format");
                    tvTotalDistance.setText(all_distince_value_format + "km");
                    updateResultMatchMainUI(matchs_end_tips,final_result_time,match_user_join_num,all_distince_value_format);
                }else{
                    RankMatchInfo2 rankMatchInfo = gson.fromJson(dataObject.toString(), RankMatchInfo2.class);
                    updateMatchMainUI(rankMatchInfo);
                }
            }
        }
    }


    private int myDuration=0;
    private double myDistance=0.0f;

    /**
     * 弹出结果页面
     * @param matchs_end_tips
     * @param final_result_time
     * @param match_user_join_num
     * @param all_distince_value_format
     */
    private void updateResultMatchMainUI(String matchs_end_tips,String final_result_time,String match_user_join_num,String all_distince_value_format){
        isStopMatch=true;
        residueTimeStop=true;
        stopScheduleCountDown();
        //弹出结束框
        MatchWinDialog.show(this,
                matchs_end_tips,
                final_result_time,
                match_user_join_num,
                all_distince_value_format,
                () -> finish());
    }

    private void updateMatchMainUI(RankMatchInfo2 rankMatchInfo) {
        if(rankMatchInfo.getIs_end()==1 && !MatchWinDialog.isShowing()){
            //达到赛点，发送结束
            if(user_play_id!=0){
                float meter = BallUtils.getTotalMeter(mTotalCircle);
                playStop(user_play_id, start_time, Constants.UPDATE_PERIOD * 6, circleCache, speedCache, meter, mTotalCircle, mHighSpeedRPM);
            }
            isStopMatch=true;
            residueTimeStop=true;
            stopScheduleCountDown();
            //弹出结束框
            MatchWinDialog.show(this,
                    rankMatchInfo.getUser_group_name(),
                    rankMatchInfo.getMatchs_end_tips(),
                    rankMatchInfo.getFinal_result_time(),
                    String.valueOf(rankMatchInfo.getCount()),
                    String.valueOf(rankMatchInfo.getAll_distince_value_format()),
                    () -> finish());
        }
        //列表
        mRankMatchMainAdapter.notifyDataSetChanged(rankMatchInfo.getAll_group());
        mRankMatchMainAdapter.selectMe(user_group_id);

        //底部其他字段
        //个人累计时间
        if(rankMatchInfo.getMy_duration()>=myDuration){
            myDuration=rankMatchInfo.getMy_duration();
        }

        //比赛倒计时
        if(rankMatchInfo.getResidue_time()>=0 && matchCountdownTime==0){
            residue_time=rankMatchInfo.getResidue_time();
            matchCountdownTime=rankMatchInfo.getResidue_time();
            AppLogger.d("--------------比赛倒计时-----------"+residue_time+";matchCountdownTime="+matchCountdownTime);
        }

        if(rankMatchInfo.getMy_distance()>myDistance){
            myDistance=rankMatchInfo.getMy_distance();
            updatePersonMatchLeft(myDuration,myDistance);
            //AppLogger.d("--------------个人累计距离------------"+myDistance);
        }

        tvTotalDistance.setText(rankMatchInfo.getAll_distince_value_format() + "km");
    }

    /**
     * 更新个人累计时间和距离
     * @param duration 累计时间
     * @param distance 累计距离
     */
    private void updatePersonMatchLeft(int duration,double distance){
        //个人累计时间
        tvPersonTime.setText(TimeUtils.formatDuration3(duration));
        //个人累计距离
        tvPersonDistance.setText(mDecimalFormat.format(distance));
    }

    /**
     * 比赛倒计时
     * @param matchCountdownTime
     */
    private void updateMatchCountDown(int matchCountdownTime){
        tvMatchCountdown.setText(TimeUtils.formatDuration3(matchCountdownTime));
    }


    private ScheduledExecutorService countDownExecutor = Executors.newScheduledThreadPool(1);


    boolean residueTimeStop=false;
    /**
     * 比赛结束倒计时
     */
    private void startScheduleCountDown(){
        if(countDownExecutor!=null){
            countDownExecutor.scheduleAtFixedRate(() -> {
                if(!residueTimeStop){
                    matchPlaying.set(true);
                    if(matchCountdownTime>0){
                        matchCountdownTime-=1;
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_PLAY_TIME_2));
                        if(matchCountdownTime%3==0 && user_play_id==0){
                            requestRankMatchBase(sys_match_id, user_group_id);
                        }
                    }
                }
            },3,1, TimeUnit.SECONDS);
        }
    }

    private void stopScheduleCountDown(){
        if(countDownExecutor!=null){
            try {
                countDownExecutor.shutdown();
                if (!countDownExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                    countDownExecutor.shutdownNow();
                }
            }catch (InterruptedException e){
                countDownExecutor.shutdownNow();
            }
        }
    }

}
