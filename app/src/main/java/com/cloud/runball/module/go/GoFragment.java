package com.cloud.runball.module.go;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.dialog.PKRuleDialog;
import com.cloud.runball.dialog.SysnDataDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.TodayDistanceSumModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.clock_in.ClockInActivity;
import com.cloud.runball.module.free_style.FreeStyleActivity;
import com.cloud.runball.module.go.adapter.GoModuleAdapter;
import com.cloud.runball.constant.ModuleConstant;
import com.cloud.runball.module.go.adapter.GoModuleItem;
import com.cloud.runball.module.home.OtherMatchActivity;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.module.match_football_association.AssociationMatchMenuActivity;
import com.cloud.runball.module.match_football_association.entity.model.HotInfoModel;
import com.cloud.runball.module.mine_record.MinePlayDataDetailActivity;
import com.cloud.runball.module.mine_record.MineRankingRecordActivity;
import com.cloud.runball.module.pk.PkModeActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.CheckHelper;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentGoBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class GoFragment extends BaseFragment {
  private ConstraintLayout layContent;
  private RecyclerView rvModuleList;
  private ImageView ivUpload;
  private ImageView ivUserPortrait;
  private TextView tvTodayDistanceSum;
  private TextView tvTodayDistanceUnit;
  private TextView tvTip;

  private FragmentGoBinding binding;

  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();

  private SysnDataDialog dialog;

  private String rule;

  public static GoFragment newInstance() {
    return new GoFragment();
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_go;
  }

  @Override
  protected View getImmersiveView() {
    return layContent;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentGoBinding.bind(view);
    // map views
    layContent = binding.layContent;
    rvModuleList = binding.rvModuleList;
    ivUpload = binding.ivUpload;
    ivUserPortrait = binding.ivUserPortrait;
    tvTodayDistanceSum = binding.tvTodayDistanceSum;
    tvTodayDistanceUnit = binding.tvTodayDistanceUnit;
    tvTip = binding.tvTip;

    // Click listeners replacement for @OnClick
//    View layTodayDistanceSum = view.findViewById(R.id.layTodayDistanceSum);
//    View ivRule = view.findViewById(R.id.ivRule);
//    if (layTodayDistanceSum != null) layTodayDistanceSum.setOnClickListener(this::onClick);
//    if (ivRule != null) ivRule.setOnClickListener(this::onClick);
    binding.layTodayDistanceSum.setOnClickListener(this::onClick);
    binding.ivRule.setOnClickListener(this::onClick);
    ivUpload.setOnClickListener(this::onClick);
    tvTip.setOnClickListener(this::onClick);
    ivUserPortrait.setOnClickListener(this::onClick);

    adaptImmersiveStatusBar();
  }

  @Override
  protected void onLazyLoad() {
    initList();
  }

  @Override
  protected void onFragmentShow() {
    super.onFragmentShow();
    List<PlayInfo> temp = sqlService.queryAllPlayInfoList();
    if (temp == null || temp.size() == 0) {
      ivUpload.setEnabled(false);
      tvTip.setVisibility(View.GONE);
    } else {
      ivUpload.setEnabled(true);
      tvTip.setVisibility(View.VISIBLE);
    }
    reUserPortrait();
    getTodayDistanceSum();
  }

  private void initList() {
    List<GoModuleItem> data = new ArrayList<>();
    data.add(
        new GoModuleItem(
            ModuleConstant.MODULE_FREE_STYLE,
            getString(R.string.title_go_module_free_style),
            getString(R.string.title_vice_go_module_free_style),
            R.mipmap.ic_module_free_style,
            "",
            R.mipmap.bg_go_module_free_style
        )
    );
    data.add(
        new GoModuleItem(
            ModuleConstant.MODULE_RANKING,
            getString(R.string.title_go_module_ranking),
            getString(R.string.title_vice_go_module_ranking),
            R.mipmap.ic_module_ranking,
            "",
            R.mipmap.bg_go_module_ranking
        )
    );
    data.add(
        new GoModuleItem(
            ModuleConstant.MODULE_PK,
            getString(R.string.title_go_module_pk),
            getString(R.string.title_vice_go_module_pk),
            R.mipmap.ic_module_pk,
            "",
            R.mipmap.bg_go_module_pk
        )
    );
    data.add(
        new GoModuleItem(
            ModuleConstant.MODULE_UPUP,
            getString(R.string.title_go_module_upup),
            getString(R.string.title_vice_go_module_upup),
            R.mipmap.ic_module_upup,
            "",
            R.mipmap.bg_go_module_upup
        )
    );
    data.add(
        new GoModuleItem(
            ModuleConstant.MODULE_EVENTS,
            getString(R.string.title_go_module_events),
            getString(R.string.title_vice_go_module_events),
            R.mipmap.ic_module_events,
            "",
            R.mipmap.bg_go_module_events
        )
    );
    GoModuleAdapter adapter = new GoModuleAdapter(data);
    adapter.setOnItemClickListener(itemData -> {
      switch (itemData.getModuleId()) {
        case ModuleConstant.MODULE_FREE_STYLE: {
          FreeStyleActivity.startAction(GoFragment.this.getContext());
        } break;
        case ModuleConstant.MODULE_RANKING: {
          if(CheckHelper.onCheckFunc()==CheckHelper.NO_PHONE) {
            MineRankingRecordActivity.startAction(GoFragment.this.getContext());
          } else {
            Intent it=new Intent(getContext(), LoginOtherActivity.class);
            startActivity(it);
          }
        } break;
        case ModuleConstant.MODULE_PK: {
          if(CheckHelper.onCheckFunc()==CheckHelper.NO_PHONE) {
//            Intent intent = new Intent(GoFragment.this.getContext(), PkActivity.class);
//            startActivity(intent);
            PkModeActivity.startAction(GoFragment.this.getContext());
          } else {
            Intent it=new Intent(getContext(), LoginOtherActivity.class);
            startActivity(it);
          }
        } break;
        case ModuleConstant.MODULE_UPUP: {
          if(CheckHelper.onCheckFunc()==CheckHelper.NO_PHONE) {
            Intent intent = new Intent(GoFragment.this.getContext(), OtherMatchActivity.class);
            startActivity(intent);
          } else {
            Intent it = new Intent(getContext(), LoginOtherActivity.class);
            startActivity(it);
          }
        } break;
        case ModuleConstant.MODULE_EVENTS: {
          if(CheckHelper.onCheckFunc()==CheckHelper.NO_PHONE) {
//            Intent intent = new Intent(GoFragment.this.getContext(), MatchActivity.class);
            Intent intent = new Intent(GoFragment.this.getContext(), AssociationMatchMenuActivity.class);
            startActivity(intent);
          } else {
            Intent it=new Intent(getContext(), LoginOtherActivity.class);
            startActivity(it);
          }
        } break;
      }
    });
    rvModuleList.setAdapter(adapter);
    loadHotInfo();
  }

  private void loadHotInfo() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
      apiServer.getGoMatchHotV2(requestBody)
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new WristBallObserver<HotInfoModel>() {
          @Override
          public void onSuccess(HotInfoModel model) {
            if (model.getList() == null || model.getList().size() == 0) {
              return;
            }
            GoModuleAdapter adapter = (GoModuleAdapter) rvModuleList.getAdapter();
            if (adapter != null) {
              List<GoModuleItem> data = adapter.getData();
              for (GoModuleItem item: data) {
                if (item.getModuleId() == ModuleConstant.MODULE_EVENTS) {
                  item.setMark(model.getList().get(0).getMatchTitle());
                }
              }
            }
            adapter.notifyDataSetChanged();
          }
          @Override
          public void onError(int code, String msg) {

          }
        })
    );
  }

  public void reUserPortrait() {
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
    Picasso.with(getActivity())
        .load(avatarUrl)
        .transform(new CircleTransform(this.getContext()))
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
                tvTodayDistanceSum.setText(model.getSumDistance() + "");
                tvTodayDistanceUnit.setText(model.getUnit());
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getTodayDistanceSum - " + msg);
              }
            })
    );
  }

  public void onClick(View view) {
    if (view.getId() == R.id.layTodayDistanceSum || view.getId() == R.id.ivUserPortrait) {
      if(CheckHelper.onCheckFunc()==CheckHelper.NO_PHONE) {
        ClockInActivity.startAction(GoFragment.this.getContext());
      } else {
        Intent it=new Intent(getContext(), LoginOtherActivity.class);
        startActivity(it);
      }
    } else if (view.getId() == R.id.ivUpload || view.getId() == R.id.tvTip) {
      tvTip.setVisibility(View.GONE);
      if (dialog != null) {
        dialog.dismiss();
        dialog = null;
      }
      List<PlayInfo> temp = sqlService.queryAllPlayInfoList();
      dialog = new SysnDataDialog(GoFragment.this.getContext());
      dialog.updateData(temp);
      dialog.setOnClickCallback(new SysnDataDialog.OnClickCallback() {
        @Override
        public void onClick(SysnDataDialog dialog, PlayInfo itemData, int position) {
          MinePlayDataDetailActivity.startAction(GoFragment.this.getActivity(), true, itemData.getSqlId());
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
          GoFragment.this.dialog = null;
        }
      });
    } else if (view.getId() == R.id.ivRule) {
      if (TextUtils.isEmpty(rule)) {
        getGoIntroduce();
      } else {
        PKRuleDialog.show(GoFragment.this.getContext(), rule);
      }
    }
  }

  private boolean isLoading = false;

  private void uploadLocalMatchPlayV3(PlayInfo data, int position) {
    if (data == null) {
      if (getActivity() != null) {
        getActivity().finish();
      }
      return;
    }

    if (isLoading) {
      return;
    }
    isLoading = true;

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
    map.put("is_abnormal", data.getIsAbnormal());
    map.put("is_quartets", data.getIsQuartets());
    map.put("duration", data.getDuration());
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
//                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
//                sqlService.insertOrUpdatePlayInfo(data);
                sqlService.deletePlayInfo(data.getSqlId());
                sqlService.deleteSpeedDetail(data.getSqlId());
                if (dialog != null) {
                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
                }
                isLoading = false;
              }
              @Override
              public void onError(int code, String msg) {
                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                sqlService.insertOrUpdatePlayInfo(data);
                if (dialog != null) {
                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                }
                isLoading = false;
              }
            })
    );
  }

  private void getGoIntroduce() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<IntroduceModel> observable = apiServer.getGoIntroduce(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<IntroduceModel>() {
              @Override
              public void onSuccess(IntroduceModel model) {
                if("googleplay".equals(BuildConfig.FLAVOR)) {
                  rule = model.getEnUS();
                } else {
                  rule = model.getZhCN();
                }
                PKRuleDialog.show(GoFragment.this.getContext(), rule);
              }
              @Override
              public void onError(int code, String msg) {

              }
            })
    );
  }

}
