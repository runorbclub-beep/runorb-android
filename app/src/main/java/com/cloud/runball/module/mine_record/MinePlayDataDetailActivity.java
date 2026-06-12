package com.cloud.runball.module.mine_record;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.bean.UserPlayData;
import com.cloud.runball.constant.ModuleConstant;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.constant.QrCodeConstant;
import com.cloud.runball.dialog.ShareCardDialog;
import com.cloud.runball.dialog.ShareTargetDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.UserPlayDataModel;
import com.cloud.runball.module.match_football_association.dialog.AssociationShareCardDialog;
import com.cloud.runball.module.mine.adapter.TurnCircleAdapter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;
import com.cloud.runball.share.ShareManage;
import com.cloud.runball.utils.ChartUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.ActivityMinePlayDataDetailBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MinePlayDataDetailActivity extends BaseActivity {

  private ActivityMinePlayDataDetailBinding binding;

  Toolbar toolbar;
  SwipeRefreshLayout layRefresh;
  TextView tvCreateTime;
  TextView tvSource;
  LinearLayout layMaxSpeed;
  TextView tvMaxSpeed;
  LinearLayout layOneMinute;
  TextView tvOneMinute;
  LinearLayout layExponent;
  TextView tvExponent;
  LinearLayout layMarathon;
  TextView tvMarathon;
  RecyclerView rvSpeedBetweenList;
  LineChart speedChart;
  LinearLayout layMessage;
  TextView tvMessage;
  ConstraintLayout layOperation;
  TextView tvTurnDuration;
  TextView tvTurnMetre;

  private final WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();

  private long userPlayId;
  private boolean isLocalData;

  private PlayInfo playInfo;
  private UserPlayData userPlayData;

  public static void startAction(Activity context, boolean isLocalData, long userPlayId) {
    Intent intent = new Intent(context, MinePlayDataDetailActivity.class);
    intent.putExtra("userPlayId", userPlayId);
    intent.putExtra("isLocalData", isLocalData);
    context.startActivityForResult(intent, 1);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_mine_play_data_detail;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMinePlayDataDetailBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    layRefresh = binding.layRefresh;
    tvCreateTime = binding.tvCreateTime;
    tvSource = binding.tvSource;
    layMaxSpeed = binding.layMaxSpeed;
    tvMaxSpeed = binding.tvMaxSpeed;
    layOneMinute = binding.layOneMinute;
    tvOneMinute = binding.tvOneMinute;
    layExponent = binding.layExponent;
    tvExponent = binding.tvExponent;
    layMarathon = binding.layMarathon;
    tvMarathon = binding.tvMarathon;
    rvSpeedBetweenList = binding.rvSpeedBetweenList;
    speedChart = binding.speedChart;
    layMessage = binding.layMessage;
    tvMessage = binding.tvMessage;
    layOperation = binding.layOperation;
    tvTurnDuration = binding.tvTurnDuration;
    tvTurnMetre = binding.tvTurnMetre;
    layRefresh.setEnabled(false);

    this.userPlayId = getIntent().getLongExtra("userPlayId", 0);
    this.isLocalData = getIntent().getBooleanExtra("isLocalData", false);

    toolbar.setNavigationOnClickListener((view) -> {
      finish();
    });

    // Replace @OnClick with listeners
    binding.ivShareEntry.setOnClickListener(this::onClick);
    binding.tvUpload.setOnClickListener(this::onClick);
    binding.tvDelete.setOnClickListener(this::onClick);

    ViewTreeObserver viewTreeObserver = rvSpeedBetweenList.getViewTreeObserver();
    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        rvSpeedBetweenList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int px= AppUtils.px2dip(getApplicationContext(), rvSpeedBetweenList.getWidth());
        TurnCircleAdapter adapter = new TurnCircleAdapter((int)(px * 2));
        adapter.updateMeasure((int)(px * 2));
        rvSpeedBetweenList.setAdapter(adapter);

        if (isLocalData) {
          loadLocalData(userPlayId);
        } else {
          loadNetData(userPlayId, true);
        }
      }
    });

  }

  public void onClick(View view) {
    if (view.getId() == R.id.ivShareEntry) {
      showShareCardDialog();
    } else if (view.getId() == R.id.tvUpload) {
      // todo
//      RankingResultDialog rankingResultDialog = new RankingResultDialog(this);
//      rankingResultDialog.setData();
      uploadLocalMatchPlayV3(playInfo);
    } else if (view.getId() == R.id.tvDelete) {
      sqlService.deletePlayInfo(userPlayId);
      sqlService.deleteSpeedDetail(userPlayId);
      setResult(1);
      finish();
    }
  }

  private void showShareCardDialog() {
    if (userPlayData == null) {
      return;
    }
    if (isFinishing()) {
      return;
    }

    long marathon = 0;
    try {
      marathon = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(userPlayData.getMarathon()).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    AssociationShareCardDialog.show(
        this,
        getString(R.string.title_go_module_ranking),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_name(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getAddress(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img(),
        userPlayData.getStop_time(), Float.parseFloat(userPlayData.getExponent_molecular() + "") / 1000, userPlayData.getExponent(),
        0, (int) marathon, userPlayData.getSpeed_max() + "",
        (float) userPlayData.getDistance(),
        userPlayData.getDuration_format(),
        QrCodeConstant.WECHAT_OFFICIAL_ACCOUNTS_URL,

        new AssociationShareCardDialog.ConfirmCallBack() {
          @Override
          public void onOther() {

          }
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
        }, false);

//    RankingResultDialog dialog = new RankingResultDialog(this);
//    DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
//    String distanceFormat = mDecimalFormat.format(Float.parseFloat(userPlayData.getExponent_molecular() + "") / 1000);
//    dialog.setData(
//        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img(),
//        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_name(),
//        userPlayData.getStart_time_format(), QrCodeConstant.WECHAT_OFFICIAL_ACCOUNTS_URL,
//        userPlayData.getSpeed_max(), distanceFormat, userPlayData.getExponent() + "", userPlayData.getMarathon(),
//        userPlayData.getDuration_format(), userPlayData.getDistance_format(),
//        new RankingResultDialog.ConfirmCallBack() {
//          @Override
//          public void onCancel() {
//
//          }
//
//          @Override
//          public void onShare(Bitmap bitmap) {
//            showShareTargetDialog(bitmap);
//          }
//        }
//    );
  }

  private void showShareTargetDialog(Bitmap bitmap) {
    if (isFinishing()) {
      return;
    }
    ShareTargetDialog dialog = new ShareTargetDialog();
    dialog.show(MinePlayDataDetailActivity.this, new ShareTargetDialog.ConfirmCallBack() {
      @Override
      public void onCancel() {

      }
      @Override
      public void onShareTarget(ShareTargetDialog.ShareTarget shareTarget) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (ActivityCompat.checkSelfPermission(MinePlayDataDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MinePlayDataDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 188);
            return;
          }
        }
        ShareManage shareManage = new ShareManage();
        shareManage.shareBitmap(MinePlayDataDetailActivity.this, shareTarget.getType(), bitmap, new ShareManage.ShareCallback() {
          @Override
          public void onStart() {

          }
          @Override
          public void onResult() {
            AssociationShareCardDialog.dismiss();
            ShareCardDialog.dismiss();
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

  private void loadNetData(long user_play_id, boolean need_format) {
    HashMap<String, Object> map = new HashMap<>(2);
    map.put("user_play_id", user_play_id);
    map.put("need_format", need_format);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserPlayDataModel> observable = apiServer.playInfo(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserPlayDataModel>() {
              @Override
              protected void onStart() {
                super.onStart();
                layRefresh.setRefreshing(true);
              }
              @Override
              public void onSuccess(UserPlayDataModel userPlayDetailModel) {
                if(userPlayDetailModel != null && userPlayDetailModel.getUser_play() != null) {
                  //区间转速
                  TurnCircleAdapter adapter = (TurnCircleAdapter) rvSpeedBetweenList.getAdapter();
                  if (adapter != null) {
                    adapter.setData(userPlayDetailModel.getUser_play().getSection_duration());
                  }

                  //转速曲线
                  initChartStyle(userPlayDetailModel.getUser_play().getUser_play_detail());

                  userPlayData = userPlayDetailModel.getUser_play();
                  //
                  updateTopData(userPlayDetailModel.getUser_play());
                }
                layRefresh.setRefreshing(false);
              }
              @Override
              public void onError(int code, String msg) {
                layRefresh.setRefreshing(false);
                Logger.d(msg);
              }
            })
    );
  }

  private void loadLocalData(long userPlayId) {
    playInfo = sqlService.queryPlayInfo(userPlayId);
    List<SpeedDetail> speedDetailData = sqlService.querySpeedDetail(playInfo.getSqlId());
    List<Integer> speedDetail = new ArrayList<>();
    for (int i = 0; i < speedDetailData.size(); i++) {
      SpeedDetail itemSpeedDetail = speedDetailData.get(i);
      speedDetail.add(itemSpeedDetail.getSpeed());
    }
    Integer[] speedDetailArr = speedDetail.toArray(new Integer[0]);
    playInfo.setSpeedDetail(speedDetailArr);


    UserPlayData userPlayData = new UserPlayData();
    userPlayData.setUser_play_id(playInfo.getSqlId() + "");
    userPlayData.setDuration((int) playInfo.getDuration());
    userPlayData.setSpeed_max(playInfo.getMaxSpeed());
    userPlayData.setStart_time((int) playInfo.getStartTime());
    userPlayData.setStop_time((int) playInfo.getStopTime());
    userPlayData.setDistance(playInfo.getDistance());
    userPlayData.setExponent_molecular(playInfo.getExponentMolecular());
    userPlayData.setExponent_denominator(playInfo.getExponentDenominator());
    userPlayData.setExponent(playInfo.getExponent());
    userPlayData.setSource(playInfo.getSource());
    userPlayData.setIs_abnormal(playInfo.getIsAbnormal());
    userPlayData.setMarathon(playInfo.getMarathon() + "");
    String durationFormat = TimeUtils.formatDurationFull(playInfo.getDuration());
    userPlayData.setDuration_format(durationFormat);
    DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
    String distanceFormat = mDecimalFormat.format(playInfo.getDistance() / 1000) + "km";
    userPlayData.setDistance_format(distanceFormat);
    String startTimeFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(new Date(playInfo.getStartTime() * 1000));
    userPlayData.setStart_time_format(startTimeFormat);



    List<UserPlayData.SectionDurationDTO> sectionDuration = new ArrayList<>();
    UserPlayData.SectionDurationDTO sectionDurationDTO;
    sectionDurationDTO = new UserPlayData.SectionDurationDTO();
    sectionDurationDTO.setStart_section(0);
    sectionDurationDTO.setStop_section(2000);
    sectionDuration.add(sectionDurationDTO);
    sectionDurationDTO = new UserPlayData.SectionDurationDTO();
    sectionDurationDTO.setStart_section(2000);
    sectionDurationDTO.setStop_section(6000);
    sectionDuration.add(sectionDurationDTO);
    sectionDurationDTO = new UserPlayData.SectionDurationDTO();
    sectionDurationDTO.setStart_section(6000);
    sectionDurationDTO.setStop_section(10000);
    sectionDuration.add(sectionDurationDTO);
    sectionDurationDTO = new UserPlayData.SectionDurationDTO();
    sectionDurationDTO.setStart_section(10000);
    sectionDurationDTO.setStop_section(14000);
    sectionDuration.add(sectionDurationDTO);
    sectionDurationDTO = new UserPlayData.SectionDurationDTO();
    sectionDurationDTO.setStart_section(14000);
    sectionDurationDTO.setStop_section(18000);
    sectionDuration.add(sectionDurationDTO);
    sectionDurationDTO = new UserPlayData.SectionDurationDTO();
    sectionDurationDTO.setStart_section(18000);
    sectionDurationDTO.setStop_section(22000);
    sectionDuration.add(sectionDurationDTO);
    for (int i = 0; i < speedDetailData.size(); i++) {
      SpeedDetail itemSpeedDetail = speedDetailData.get(i);
      int speed = itemSpeedDetail.getSpeed();
      int position = 0;
      if (0 <= speed && 2000 > speed) {
        position = 0;
      } else if (2000 <= speed && 6000 > speed) {
        position = 1;
      } else if (6000 <= speed && 10000 > speed) {
        position = 2;
      } else if (10000 <= speed && 14000 > speed) {
        position = 3;
      } else if (14000 <= speed && 18000 > speed) {
        position = 4;
      } else if (18000 <= speed && 22000 > speed) {
        position = 5;
      }
      UserPlayData.SectionDurationDTO targetItem = sectionDuration.get(position);
      targetItem.setSection_duration(targetItem.getSection_duration() + 0.5F);
    }
    userPlayData.setSection_duration(sectionDuration);


    long moment = playInfo.getStartTime();
    List<UserPlayData.UserPlayDetailDTO> userPlayDetail = new ArrayList<>();
    for (int i = 0; i < speedDetailData.size(); i++) {
      UserPlayData.UserPlayDetailDTO item = new UserPlayData.UserPlayDetailDTO();
      item.setMoment(moment + 500L * i);
      item.setSpeed(speedDetailData.get(i).getSpeed());
      userPlayDetail.add(item);
    }
    userPlayData.setUser_play_detail(userPlayDetail);


    //区间转速
    TurnCircleAdapter adapter = (TurnCircleAdapter) rvSpeedBetweenList.getAdapter();
    if (adapter != null) {
      adapter.setData(userPlayData.getSection_duration());
    }
    //转速曲线
    initChartStyle(userPlayData.getUser_play_detail());
    this.userPlayData = userPlayData;
    //
    updateTopData(userPlayData);

  }

  private void initChartStyle(List<UserPlayData.UserPlayDetailDTO> list) {
    if(list != null && list.size() > 0) {
      List<String> lables = new ArrayList<>();
      List<Entry> values = new ArrayList<>();
      //基线
      int limit = 0;
      for(int i = 0;i < list.size(); i++) {
        int speed = list.get(i).getSpeed();
        lables.add("");
        values.add(new Entry(i, speed));
        if(limit <= speed){
          limit = speed;
        }
      }
      ChartUtils.initChart(this, speedChart, true, limit);
      ChartUtils.notifyDataSetChanged(this, speedChart, R.drawable.shape_line_turn, Color.parseColor("#ffe08a4a"), false,values, lables);
    }
  }

  private void updateTopData(UserPlayData data){
    String source = "";
    switch (data.getSource()) {
      case ModuleConstant.MODULE_RANKING:
        source = getString(R.string.title_go_module_ranking);
        layMaxSpeed.setVisibility(View.VISIBLE);
        layOneMinute.setVisibility(View.VISIBLE);
        layExponent.setVisibility(View.VISIBLE);
        layMarathon.setVisibility(View.VISIBLE);
        break;
      case ModuleConstant.MODULE_PK:
        source = getString(R.string.title_go_module_pk);
        layMaxSpeed.setVisibility(View.VISIBLE);
        break;
      case ModuleConstant.MODULE_UPUP:
        source = getString(R.string.title_go_module_upup);
        layMaxSpeed.setVisibility(View.VISIBLE);
        break;
      case ModuleConstant.MODULE_EVENTS:
        source = getString(R.string.title_go_module_events);
        layMaxSpeed.setVisibility(View.VISIBLE);
        layOneMinute.setVisibility(View.VISIBLE);
        layExponent.setVisibility(View.VISIBLE);
        layMarathon.setVisibility(View.VISIBLE);
        break;
      case ModuleConstant.MODULE_FREE_STYLE:
        source = getString(R.string.title_go_module_free_style);
        layMaxSpeed.setVisibility(View.VISIBLE);
        break;
    }
    tvSource.setText(getString(R.string.mine_record_source, source));
    tvCreateTime.setText(data.getStart_time_format());

    tvTurnDuration.setText(data.getDuration_format());
    tvTurnMetre.setText(data.getDistance_format());

    tvMaxSpeed.setText(data.getSpeed_max() + "");
    DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
    String distanceFormat = mDecimalFormat.format(Float.parseFloat(data.getExponent_molecular() + "") / 1000);
    tvOneMinute.setText(distanceFormat);
    tvExponent.setText(data.getExponent() + "");
    tvMarathon.setText(data.getMarathon());

    layMessage.setVisibility(data.getIs_abnormal() == 1 ? View.VISIBLE : View.GONE);

    if (isLocalData) {
      layOperation.setVisibility(View.VISIBLE);
    } else {
      layOperation.setVisibility(View.GONE);
    }

  }

  private void uploadLocalMatchPlayV3(PlayInfo data) {

    data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING);

    HashMap<String, Object> map = new HashMap<>();
    map.put("exponent_molecular", data.getExponentMolecular());
    map.put("source", data.getSource());
    map.put("endurance_max", data.getMaxEndurance());
    map.put("is_abnormal", data.getIsAbnormal());
    map.put("sys_match_id", TextUtils.isEmpty(data.getSysMatchId()) ? "0" : data.getSysMatchId());
    map.put("sys_sys_match_id", TextUtils.isEmpty(data.getSysSysMatchId()) ? "0" : data.getSysSysMatchId());
    map.put("user_pk_list_id", data.getUserPkListId());
    map.put("stop_time", data.getStopTime());
    map.put("start_time", data.getStartTime());
    map.put("interval", data.getInterval());
    map.put("created_uid", data.getCreatedUid());
    map.put("speed_max", data.getMaxSpeed());
    map.put("exponent", data.getExponent());
    map.put("marathon", data.getMarathon());
    map.put("is_quartets", data.getIsQuartets());
    map.put("duration", data.getDuration());
    map.put("distance", data.getDistance());
    map.put("circle_count", data.getCircleCount());
    map.put("exponent_denominator", data.getExponentDenominator());
    map.put("exponent_speed_max", data.getExponentSpeedMax());
    map.put("speed_detail", new Gson().toJson(data.getSpeedDetail()));
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
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
                setResult(1);
                finish();
              }
              @Override
              public void onError(int code, String msg) {
                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                sqlService.insertOrUpdatePlayInfo(data);
              }
            })
    );
  }

}
