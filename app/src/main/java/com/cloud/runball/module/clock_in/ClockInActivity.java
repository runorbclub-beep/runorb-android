package com.cloud.runball.module.clock_in;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MonthDayDistanceInfo;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.dialog.SysnDataDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.ClockInTarget;
import com.cloud.runball.model.MonthDayDistanceInfoModel;
import com.cloud.runball.model.TodayDistanceSumModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.WristBallActivity;
import com.cloud.runball.module.clock_in.adapter.ClockInTargetAdapter;
import com.cloud.runball.module.clock_in.adapter.ClockInTargetItem;
import com.cloud.runball.module.clock_in.dialog.EditClockInTargetDialog;
import com.cloud.runball.module.mine_record.MinePlayDataDetailActivity;
import com.cloud.runball.module.mine_record.MineRecordActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.cloud.runball.widget.calendar.ClockInCalendarItemData;
import com.cloud.runball.widget.calendar.ClockInCalendarView;
import com.cloud.runball.widget.calendar.ClockInCalendarViewAdapter;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.ActivityClockInBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ClockInActivity extends BaseActivity {
  private ActivityClockInBinding binding;
  Toolbar toolbar;
  ImageView ivUserPortrait;
  TextView tvDayDistance;
  TextView tvSelectDate;
  ClockInCalendarView layCalendar;
  XRecyclerView recyclerView;
  RelativeLayout ryEmpty;

  private Date selectDate = new Date();

  private Date moreDate = new Date();
  private Date futureDate = new Date();
  private int monthNum = 9;

  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();
  private SysnDataDialog dialog;

  private final List<ClockInTargetItem> clockInTargetData = new ArrayList<>();

  public static void startAction(Context context) {
    Intent intent = new Intent(context, ClockInActivity.class);
    context.startActivity(intent);
  }

  public static void startAction(Context context, Date date) {
    Intent intent = new Intent(context, ClockInActivity.class);
    intent.putExtra("date", date);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_clock_in;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityClockInBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected View getImmersiveView() {
    return toolbar;
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    setEmptyStatusBar();
    toolbar = binding.toolbar;
    ivUserPortrait = binding.ivUserPortrait;
    tvDayDistance = binding.tvDayDistance;
    tvSelectDate = binding.tvSelectDate;
    layCalendar = binding.layCalendar;
    recyclerView = binding.recyclerView;
    ryEmpty = binding.ryEmpty;

    Intent intent = getIntent();
    if (intent != null) {
      Date date = (Date) intent.getSerializableExtra("date");
      if (date != null) {
        selectDate = date;
      }
    }

    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    reUserPortrait();
    getTodayDistanceSum();

    getMonthDayDistanceSum(selectDate);

    layCalendar.setItemClickListener(new ClockInCalendarViewAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(ClockInCalendarItemData itemData) {

        MineRecordActivity.startAction(ClockInActivity.this, itemData.getDate(), false);
      }
    });

    showClockInTargetList();

    // Replace @OnClick with listeners
    binding.ivSync.setOnClickListener(this::onClick);
    binding.ivDataTotal.setOnClickListener(this::onClick);
    tvSelectDate.setOnClickListener(this::onClick);
    binding.tvMore.setOnClickListener(this::onClick);
    binding.tvGotoClockIn.setOnClickListener(this::onClick);

    recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    recyclerView.setPullRefreshEnabled(false);
    recyclerView.setLoadingMoreEnabled(true);
    recyclerView.setEmptyView(ryEmpty);
    recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {

      }
      @Override
      public void onLoadMore() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(moreDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 9);
        getTargetPunch(1, calendar.getTime(), monthNum);
      }
    });

    Calendar futureCalendar = Calendar.getInstance();
    futureCalendar.setTime(futureDate);
    futureCalendar.set(Calendar.DAY_OF_MONTH, 1);
    futureCalendar.set(Calendar.MONTH, futureCalendar.get(Calendar.MONTH) + 3);
    futureDate = futureCalendar.getTime();


    Calendar moreCalendar = Calendar.getInstance();
    moreCalendar.setTime(moreDate);
    moreCalendar.set(Calendar.DAY_OF_MONTH, 1);
    moreCalendar.set(Calendar.MONTH, moreCalendar.get(Calendar.MONTH) + 2);
    moreDate = moreCalendar.getTime();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(moreDate);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 9);
    getTargetPunch(1, calendar.getTime(), monthNum);
  }

  private void showClockInTargetList() {
    ClockInTargetAdapter adapter = (ClockInTargetAdapter) recyclerView.getAdapter();
    if (adapter == null) {
      adapter = new ClockInTargetAdapter(clockInTargetData);
      adapter.setListener(new ClockInTargetAdapter.OnItemClickListener() {
        @Override
        public void onEdit(ClockInTargetItem itemData) {
          EditClockInTargetDialog editClockInTargetDialog = new EditClockInTargetDialog(ClockInActivity.this);
          editClockInTargetDialog.setOnCallback(itemData, new EditClockInTargetDialog.OnCallback() {
            @Override
            public void onSubmit(EditClockInTargetDialog dialog, String startYearMonth, int monthNum, int minDay, int targetDistance) {
              editTargetPunch(startYearMonth, monthNum, minDay, targetDistance, dialog);
            }
          });
        }
      });
      recyclerView.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }
  }
  public void onClick(View view) {
    if (view.getId() == R.id.ivSync) {
      if (dialog != null) {
        dialog.dismiss();
        dialog = null;
      }
      List<PlayInfo> temp = sqlService.queryAllPlayInfoList();
      dialog = new SysnDataDialog(this);
      dialog.updateData(temp);
      dialog.setOnClickCallback(new SysnDataDialog.OnClickCallback() {
        @Override
        public void onClick(SysnDataDialog dialog, PlayInfo itemData, int position) {
//          if (itemData.getStatus() == PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT
//              || itemData.getStatus() == PlayingDataConstant.Update.STATUS_UPDATE_FAIL) {
//                uploadLocalMatchPlayV3(itemData, position);
//          }
          MinePlayDataDetailActivity.startAction(ClockInActivity.this, true, itemData.getSqlId());
          dialog.dismiss();
        }
        @Override
        public void onSysn(SysnDataDialog dialog, PlayInfo itemData, int position) {
          if (
              PlayingDataConstant.Update.STATUS_UPDATE_INCOMPLETE.equals(itemData.getUploadStatus())
                  || PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT.equals(itemData.getUploadStatus())
                  || PlayingDataConstant.Update.STATUS_UPDATE_FAIL.equals(itemData.getUploadStatus())) {
            uploadLocalMatchPlayV3(itemData, position);
          }
        }
        @Override
        public void onClose(SysnDataDialog dialog) {
          dialog.dismiss();
          ClockInActivity.this.dialog = null;
        }
      });
    } else if (view.getId() == R.id.ivDataTotal) {
      MineRecordActivity.startAction(this, selectDate, true);
    } else if (view.getId() == R.id.tvSelectDate) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(selectDate);
      new TimePickerBuilder(this, (startDate, endDate, isFullMonth, v) -> {
        getMonthDayDistanceSum(startDate);
      })
          .setType(new boolean[] { true, true, false, false, false, false })
          .setBgColor(Color.parseColor("#28272A"))
          .setCancelColor(Color.parseColor("#FFFFFF"))
          .setSubmitColor(Color.parseColor("#FFFFFF"))
          .setTitleBgColor(Color.parseColor("#28272A"))
          .setTextColorCenter(Color.parseColor("#FFFFFF"))
          .setDate(calendar)
          .build()
          .show();
    } else if (view.getId() == R.id.tvMore) {
      getTargetPunch(2, futureDate, monthNum);
    } else if (view.getId() == R.id.tvGotoClockIn) {
      WristBallActivity.startAction(this, false, 1);
    }
  }

  private void uploadLocalMatchPlayV3(PlayInfo data, int position) {
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

    if (dialog != null) {
      dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING);
    }

    HashMap<String, Object> map = new HashMap<>();
    map.put("source", data.getSource());
    map.put("exponent_molecular", data.getExponentMolecular());
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
    map.put("exponent_speed_max", data.getExponentSpeedMax());
    map.put("circle_count", data.getCircleCount());
    map.put("exponent_denominator", data.getExponentDenominator());
    map.put("speed_detail", new Gson().toJson(speedDetailArr));
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
                if (dialog != null) {
                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
                }
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(ClockInActivity.this, R.string.tip_api_error, Toast.LENGTH_SHORT).show();
                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                sqlService.insertOrUpdatePlayInfo(data);
                if (dialog != null) {
                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                }
              }
            })
    );
  }

  private void reUserPortrait() {
    UserInfoModel model = AppDataManager.getInstance().getUserInfoModel();
    if (model == null) {
      return;
    }
    String avatarUrl;
    if (model.getUser_info().getUser_img().startsWith("http")) {
      avatarUrl = model.getUser_info().getUser_img();
    } else {
      avatarUrl = Constant.getBaseUrl() + "/" + model.getUser_info().getUser_img();
    }
    Picasso.with(this)
        .load(avatarUrl)
        .transform(new CircleTransform(this))
        .into(ivUserPortrait);
  }

  private void getTodayDistanceSum() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    disposable.add(
        apiServer.getTodayDistanceSum()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<TodayDistanceSumModel>() {
              @Override
              public void onSuccess(TodayDistanceSumModel model) {
                tvDayDistance.setText("今日累计摇跑" + model.getSumDistance() + model.getUnit());
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(ClockInActivity.this, R.string.tip_api_error, Toast.LENGTH_SHORT).show();
                AppLogger.d("getTodayDistanceSum - " + msg);
              }
            })
    );
  }

  private void getMonthDayDistanceSum(Date selectDate) {
    this.selectDate = selectDate;
    tvSelectDate.setText(new SimpleDateFormat("yyyy年MM月", Locale.getDefault()).format(selectDate));

    HashMap<String, Object> map = new HashMap<>();
    map.put("month_time", new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(selectDate));
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    disposable.add(
        apiServer.getMonthDayDistanceSum(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MonthDayDistanceInfoModel>() {
              @Override
              public void onSuccess(MonthDayDistanceInfoModel model) {
                setClockInCalendarDate(selectDate, model.getSumMonth(), model.getTargetDistance());
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(ClockInActivity.this, R.string.tip_api_error, Toast.LENGTH_SHORT).show();
                AppLogger.d("getMonthDayDistanceSum - " + msg);
              }
            })
    );
  }

  private void setClockInCalendarDate(Date selectDate, List<MonthDayDistanceInfo> distanceData, String targetDistance) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(selectDate);
    layCalendar.setYearMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), distanceData, targetDistance);
  }

  private void getTargetPunch(int loadMode, Date date, int monthNUm) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("start_time", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date));
    map.put("num_month", monthNUm);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    disposable.add(
        apiServer.getTargetPunch(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<List<ClockInTarget>>() {
              @Override
              public void onSuccess(List<ClockInTarget> model) {
                if (loadMode == 1) {
                  for (int i = 0; i < 10; i++) {
                    Calendar dataCalendar = Calendar.getInstance();
                    dataCalendar.setTime(moreDate);
                    dataCalendar.set(Calendar.MONTH, dataCalendar.get(Calendar.MONTH) - i);
                    dataCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    dataCalendar.set(Calendar.HOUR, 1);
                    dataCalendar.set(Calendar.MINUTE, 0);
                    dataCalendar.set(Calendar.SECOND, 0);
                    Date dataDate = dataCalendar.getTime();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    dataCalendar.set(Calendar.HOUR, 1);
                    dataCalendar.set(Calendar.MINUTE, 0);
                    dataCalendar.set(Calendar.SECOND, 0);
                    Date date = calendar.getTime();

                    boolean isOverdue = false;
                    if (date.after(dataDate)) {
                      isOverdue = true;
                    }
                    if (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dataDate).equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))){
                      isOverdue = false;
                    }
                    clockInTargetData.add(
                        new ClockInTargetItem(dataDate, true, isOverdue, null)
                    );
                  }
                  moreDate = date;
                } else if (loadMode == 2){
                  for (int i = 0; i < 10; i++) {
                    Calendar dataCalendar = Calendar.getInstance();
                    dataCalendar.setTime(futureDate);
                    dataCalendar.set(Calendar.MONTH, dataCalendar.get(Calendar.MONTH) + i);
                    dataCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    dataCalendar.set(Calendar.HOUR, 1);
                    dataCalendar.set(Calendar.MINUTE, 0);
                    dataCalendar.set(Calendar.SECOND, 0);
                    Date dataDate = dataCalendar.getTime();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    dataCalendar.set(Calendar.HOUR, 1);
                    dataCalendar.set(Calendar.MINUTE, 0);
                    dataCalendar.set(Calendar.SECOND, 0);
                    Date date = calendar.getTime();

                    boolean isOverdue = false;
                    if (date.after(dataDate)) {
                      isOverdue = true;
                    }
                    if (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dataDate).equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))){
                      isOverdue = false;
                    }
                    clockInTargetData.add(
                        0,
                        new ClockInTargetItem(dataDate, true, isOverdue, null)
                    );
                  }
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(date);
                  calendar.set(Calendar.DAY_OF_MONTH, 1);
                  calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 10);
                  futureDate = calendar.getTime();
                } else if (loadMode == 3) {

                }

                for (int i = 0; i < clockInTargetData.size(); i++) {
                  ClockInTargetItem clockInTargetItem = clockInTargetData.get(i);
                  for (int j = 0; j < model.size(); j++) {
                    ClockInTarget clockInTarget = model.get(j);
                    if (new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(clockInTargetItem.getDate()).equals(clockInTarget.getMonthTime())) {
                      clockInTargetItem.setClockInTarget(clockInTarget);
                      clockInTargetItem.setEmpty(false);
                      break;
                    }
                  }
                }
                showClockInTargetList();
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(ClockInActivity.this, R.string.tip_api_error, Toast.LENGTH_SHORT).show();
                AppLogger.d("getTargetPunch - " + msg);
              }
            })
    );
  }

  private void editTargetPunch(String startYearMonth, int monthNum, int minDays, int targetDistance, EditClockInTargetDialog dialog) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("start_time", startYearMonth);
    map.put("num_month", monthNum);
    map.put("target_distance", targetDistance);
    map.put("min_days", minDays);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    disposable.add(
        apiServer.editTargetPunch(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
              @Override
              public void onSuccess(ResponseBody model) {
                Date date = new Date();
                try {
                  date = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(startYearMonth);
                } catch (ParseException e) {
                  e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                if (dialog != null) {
                  dialog.dismiss();
                }
                getTargetPunch(3, calendar.getTime(), monthNum);
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(ClockInActivity.this, R.string.tip_api_error, Toast.LENGTH_SHORT).show();
                AppLogger.d("getTargetPunch - " + msg);
              }
            })
    );
  }

}
