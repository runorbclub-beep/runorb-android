package com.cloud.runball.module.mine_record;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.constant.ModuleConstant;
import com.cloud.runball.dialog.ButtonSelectModeDialog;
import com.cloud.runball.model.MinePlayDataInfoModel;
import com.cloud.runball.module.clock_in.ClockInActivity;
import com.cloud.runball.module.mine_record.adapter.MinePlayDataAdapter;
import com.cloud.runball.module.mine_record.entity.MinePlayDataInfo;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.FragmentMinePlayDataBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MinePlayDataFragment extends BaseFragment {

  private FragmentMinePlayDataBinding binding;

  TextView tvSelectDate;
  TextView tvSelectType;
  XRecyclerView recyclerview;
  RelativeLayout ryEmpty;
  TextView tvTotalDate;
  TextView tvTotalValue;

  private final List<MinePlayDataInfo> data = new ArrayList<>();

  private final List<MinePlayDataInfo> netDataList = new ArrayList<>();
  private final List<ButtonSelectModeDialog.ModeInfo> modeInfoData = new ArrayList<>();

  private Date startDate = new Date();
  private Date endDate = new Date();
  private int selectType = 0;

  private int page = 1;

  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();
  private final List<PlayInfo> localDataList = new ArrayList<>();

  public static MinePlayDataFragment newInstance(Date date, boolean isFullMonth) {
    MinePlayDataFragment fragment = new MinePlayDataFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable("date", date);
    bundle.putBoolean("isFullMonth", isFullMonth);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_mine_play_data;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentMinePlayDataBinding.bind(view);
    tvSelectDate = binding.tvSelectDate;
    tvSelectType = binding.tvSelectType;
    recyclerview = binding.recyclerview;
    ryEmpty = binding.ryEmpty;
    tvTotalDate = binding.tvTotalDate;
    tvTotalValue = binding.tvTotalValue;
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    recyclerview.setPullRefreshEnabled(true);
    recyclerview.setEmptyView(ryEmpty);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        loadList(true, 1);
        loadLocalList(selectType, startDate, endDate);
      }
      @Override
      public void onLoadMore() {
        loadList(false, page + 1);
        loadLocalList(selectType, startDate, endDate);
      }
    });
    // Replace @OnClick with listeners
    tvSelectDate.setOnClickListener(this::onClick);
    tvSelectType.setOnClickListener(this::onClick);
    binding.layTotal.setOnClickListener(this::onClick);
  }

  @Override
  protected void onLazyLoad() {
    modeInfoData.add(new ButtonSelectModeDialog.ModeInfo(getContext().getString(R.string.title_go_module_all), ModuleConstant.MODULE_ALL));
    modeInfoData.add(new ButtonSelectModeDialog.ModeInfo(getContext().getString(R.string.title_go_module_ranking), ModuleConstant.MODULE_RANKING));
    modeInfoData.add(new ButtonSelectModeDialog.ModeInfo(getContext().getString(R.string.title_go_module_pk), ModuleConstant.MODULE_PK));
    modeInfoData.add(new ButtonSelectModeDialog.ModeInfo(getContext().getString(R.string.title_go_module_upup), ModuleConstant.MODULE_UPUP));
    modeInfoData.add(new ButtonSelectModeDialog.ModeInfo(getContext().getString(R.string.title_go_module_events), ModuleConstant.MODULE_EVENTS));
    modeInfoData.add(new ButtonSelectModeDialog.ModeInfo(getContext().getString(R.string.title_go_module_free_style), ModuleConstant.MODULE_FREE_STYLE));

    tvSelectType.setText(modeInfoData.get(0).name);
    selectType = modeInfoData.get(0).value;

    Bundle bundle = getArguments();
    if (bundle != null) {
      startDate = (Date) bundle.getSerializable("date");
      endDate = (Date) bundle.getSerializable("date");
      if (!bundle.getBoolean("isFullMonth")) {
        tvSelectDate.setText(new SimpleDateFormat(getString(R.string.format_year_month_day), Locale.getDefault()).format(this.startDate));
        setSelectDate(startDate, endDate, false);
        return;
      }
    }

    tvSelectDate.setText(new SimpleDateFormat(getString(R.string.format_year_month), Locale.getDefault()).format(this.startDate));

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    startDate = calendar.getTime();
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
    endDate = calendar.getTime();
    setSelectDate(startDate, endDate, true);
  }

  public void onClick(View view) {
    if (view.getId() == R.id.tvSelectDate) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(startDate);
      new TimePickerBuilder(this.getContext(), (startDate, endDate, isFullMonth, v) -> {
        setSelectDate(startDate, endDate, isFullMonth);
      })
          .setType(new boolean[] { true, true, true, false, false, false })
          .setIsFullMonth(true)
          .setBgColor(Color.parseColor("#28272A"))
          .setCancelColor(Color.parseColor("#FFFFFF"))
          .setSubmitColor(Color.parseColor("#FFFFFF"))
          .setTitleBgColor(Color.parseColor("#28272A"))
          .setTextColorCenter(Color.parseColor("#FFFFFF"))
          .setDate(calendar)
          .build()
          .show();
    } else if (view.getId() == R.id.tvSelectType) {
      ButtonSelectModeDialog selectModeDialog = new ButtonSelectModeDialog(this.getContext());
      selectModeDialog.setOnModeClickListener(this.selectType, modeInfoData, (dialog, modeInfo) -> {
        setSelectType(modeInfo.name, modeInfo.value);
        dialog.dismiss();
      });
    } else if (view.getId() == R.id.layTotal) {
      if (startDate != null) {
        ClockInActivity.startAction(this.getContext(), startDate);
      }
    }
  }

  private void loadList(boolean isReset, int page) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("source", selectType);
    if (isReset) {
      map.put("page", 1);
    } else {
      map.put("page", page);
    }
    map.put("limit", 10);
    map.put("start_date", new SimpleDateFormat("yyyy-MM-dd").format(startDate));
    map.put("stop_date", new SimpleDateFormat("yyyy-MM-dd").format(endDate));
    map.put("type", "day");
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<MinePlayDataInfoModel> observable = apiServer.getMinePlayDataV2(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MinePlayDataInfoModel>() {
              @Override
              public void onSuccess(MinePlayDataInfoModel data) {
                if (isReset) {
                  MinePlayDataFragment.this.netDataList.clear();
                }
                if (data != null) {
                  if (data.getMinePlayDataInfo() != null) {
                    MinePlayDataFragment.this.netDataList.addAll(data.getMinePlayDataInfo());
                  }
                  if (data.getOdometerSum() != null) {

                    String source = "";
                    if (selectType == ModuleConstant.MODULE_ALL) {
                      source = getContext().getString(R.string.title_go_module_all);
                    } else if (selectType == ModuleConstant.MODULE_RANKING) {
                      source = getContext().getString(R.string.title_go_module_ranking);
                    } else if (selectType == ModuleConstant.MODULE_PK) {
                      source = getContext().getString(R.string.title_go_module_pk);
                    } else if (selectType == ModuleConstant.MODULE_UPUP) {
                      source = getContext().getString(R.string.title_go_module_upup);
                    } else if (selectType == ModuleConstant.MODULE_EVENTS) {
                      source = getContext().getString(R.string.title_go_module_events);
                    } else if (selectType == ModuleConstant.MODULE_FREE_STYLE) {
                      source = getContext().getString(R.string.title_go_module_free_style);
                    }


                    tvTotalDate.setText(tvSelectDate.getText() + " " + source + "：");
                    tvTotalValue.setText(data.getOdometerSum().getDistanceSum() + data.getOdometerSum().getUnit());
                  }
                }
                showList();
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getMinePlayDataV2 - " + msg);
              }

              @Override
              public void onComplete() {
                if (isReset) {
                  MinePlayDataFragment.this.page = page;
                } else {
                  MinePlayDataFragment.this.page++;
                }
                recyclerview.loadMoreComplete();
                recyclerview.refreshComplete();
              }
            })
    );
  }

  private void loadLocalList(int selectType, Date startDate, Date endDate) {
    int[] source;
    if (selectType == 0) {
      source = new int[] { 1, 2, 3, 4, 5 };
    } else {
      source = new int[] { selectType };
    }
//    , startDate.getTime(), endDate.getTime()
    List<PlayInfo> localDataList = sqlService.queryPlayInfoList(source, startDate.getTime(), endDate.getTime());
    this.localDataList.clear();
    if (localDataList != null) {
      this.localDataList.addAll(localDataList);
    }
    showList();
  }

  private void showList() {
    this.data.clear();
    for (int i = 0; i < localDataList.size(); i++) {
      PlayInfo playInfo = localDataList.get(i);
      MinePlayDataInfo minePlayDataInfo = new MinePlayDataInfo();
      minePlayDataInfo.setUserId(playInfo.getCreatedUid() + "");
      minePlayDataInfo.setUserPlayId(playInfo.getSqlId() + "");
      minePlayDataInfo.setDistance(playInfo.getDistance());
      DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
      String distanceFormat = mDecimalFormat.format(playInfo.getDistance() / 1000) + "km";
      minePlayDataInfo.setDistanceFormat(distanceFormat);
      minePlayDataInfo.setSpeedMax(playInfo.getMaxSpeed());
      minePlayDataInfo.setStartTime(playInfo.getStartTime());
      String startTimeFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(new Date(playInfo.getStartTime() * 1000));
      minePlayDataInfo.setStartTimeFormat(startTimeFormat);
      minePlayDataInfo.setStopTime(playInfo.getStopTime());
      minePlayDataInfo.setDuration((int) playInfo.getDuration());
      String durationFormat = TimeUtils.formatDurationFull(playInfo.getDuration());
      minePlayDataInfo.setDurationFormat(durationFormat);
      minePlayDataInfo.setSource(playInfo.getSource());
      minePlayDataInfo.setLocal(true);

      this.data.add(minePlayDataInfo);
    }
    this.data.addAll(netDataList);

    MinePlayDataAdapter adapter = (MinePlayDataAdapter) recyclerview.getAdapter();
    if (adapter == null) {
      adapter = new MinePlayDataAdapter(data);
      adapter.setOnItemClickListener(itemData -> {
        // todo
        MinePlayDataDetailActivity.startAction(MinePlayDataFragment.this.getActivity(), itemData.isLocal(), Long.parseLong(itemData.getUserPlayId()));
      });
      recyclerview.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == 1) {
      loadList(true, page);
      loadLocalList(selectType, startDate, endDate);
    }
  }

  private void setSelectDate(Date startDate, Date endDate, boolean isFullMonth) {
    this.startDate = startDate;
    this.endDate = endDate;
    if (isFullMonth) {
      tvSelectDate.setText(new SimpleDateFormat(getString(R.string.format_year_month), Locale.getDefault()).format(this.startDate));
    } else {
      tvSelectDate.setText(new SimpleDateFormat(getString(R.string.format_year_month_day), Locale.getDefault()).format(this.startDate));
    }
    tvTotalDate.setText(tvSelectDate.getText() + " " + getString(R.string.total_distance));
    loadList(true, 1);
    loadLocalList(selectType, startDate, endDate);
  }

  private void setSelectType(String name, int value) {
    tvSelectType.setText(name);
    selectType = value;
    loadList(true, 1);
    loadLocalList(selectType, startDate, endDate);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (recyclerview != null) {
      recyclerview.destroy();
      recyclerview = null;
    }
  }
}
