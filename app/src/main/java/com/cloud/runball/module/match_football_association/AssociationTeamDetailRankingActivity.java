package com.cloud.runball.module.match_football_association;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.constant.ChampionshipsConstant;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.dialog.SysnDataDialog;
import com.cloud.runball.module.match_football_association.adapter.AssociationTeamDetailRankingAdapter;
import com.cloud.runball.module.match_football_association.entity.model.AssociationTeamDetailRankingModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivtyAssociationTeamDetailRankingBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class AssociationTeamDetailRankingActivity extends BaseActivity {
  private ActivtyAssociationTeamDetailRankingBinding binding;

  Toolbar toolbar;

  TextView tvToolBarTitle;

  TextView tvMatchStatus;

  TextView tvTeamName;

  TextView tvNumberSum;

  XRecyclerView recyclerview;

  RelativeLayout ryEmpty;

//  @BindView(R.id.tvRankNum)
//  TextView tvRankNum;

//  @BindView(R.id.tvUserName)
//  TextView tvUserName;

//  @BindView(R.id.tvUserSpeed)
//  TextView tvUserSpeed;

  LinearLayout lyBottom;

  LinearLayout layYao;

  TextView tvType;

  TextView tvYao;

  TextView tvRank;
  
  ImageView ivRank;
  
  TextView tvName;
  
  TextView tvShow;
  
  TextView tvUnit;
  
  TextView tvArea;
  
  View vDivider;
  
  TextView tvTime;
  
  FrameLayout fyHead;
  
  ImageView ivHead;

  ImageView ivUpload;

  public static final String KEY_MATCH_STATUS = "match_status";
  public static final String KEY_RANKING_TYPE = "ranking_type";
  public static final String KEY_RANKING_TYPE_NAME = "ranking_type_name";
  public static final String KEY_SYS_SYS_MATCH_ID = "sys_sys_match_id";
  public static final String KEY_SYS_MATCH_ID = "sys_match_id";
  public static final String KEY_TEAM_TAG = "team_tag";
  public static final String KEY_YAOSU = "yaosu";
  public static final String KEY_UNIT = "unit";
  public static final String KEY_IS_SHOW = "is_show";

  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();
  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  private AssociationTeamDetailRankingAdapter adapter;
  private final List<AssociationTeamDetailRankingModel.MyRankingInfo> rankingList = new ArrayList<>();

  int isShowUnit = 1;
  private int page = 1;
  private final int limit = 10;

  private int matchStatus;
  private String rankingType;
  private String rankingTypeName;
  private String sysMatchId;
  private String sysSysMatchId;
  private String teamTag;
  private String yaosu;
  private String unit;
  private boolean isShow;

  private SysnDataDialog dialog;

  public static void startAction(
      Context context, int matchStatus, String rankingType, String rankingTypeName, String sys_sys_match_id, String sys_match_id, String team_tag, String yaosu, String unit, boolean isShow
  ) {
    Intent intent = new Intent(context, AssociationTeamDetailRankingActivity.class);
    intent.putExtra(KEY_MATCH_STATUS, matchStatus);
    intent.putExtra(KEY_RANKING_TYPE, rankingType);
    intent.putExtra(KEY_RANKING_TYPE_NAME, rankingTypeName);
    intent.putExtra(KEY_SYS_SYS_MATCH_ID, sys_sys_match_id);
    intent.putExtra(KEY_SYS_MATCH_ID, sys_match_id);
    intent.putExtra(KEY_TEAM_TAG, team_tag);
    intent.putExtra(KEY_YAOSU, yaosu);
    intent.putExtra(KEY_UNIT, unit);
    intent.putExtra(KEY_IS_SHOW, isShow);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activty_association_team_detail_ranking;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivtyAssociationTeamDetailRankingBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    tvToolBarTitle = binding.tvToolBarTitle;
    tvMatchStatus = binding.tvMatchStatus;
    tvTeamName = binding.tvTeamName;
    recyclerview = binding.recyclerview;
    ryEmpty = binding.ryEmpty;
    lyBottom = binding.lyBottom;
    layYao = binding.layYao;
    tvType = binding.tvType;
    tvYao = binding.tvYao;
    tvRank = binding.tvRank;
    ivRank = binding.ivRank;
    tvName = binding.tvName;
    tvShow = binding.tvShow;
    tvUnit = binding.tvUnit;
    tvArea = binding.tvArea;
    vDivider = binding.vDivider;
    tvTime = binding.tvTime;
    fyHead = binding.fyHead;
    ivHead = binding.ivHead;
    ivUpload = binding.ivUpload;
    tvNumberSum = binding.tvNumberSum;
    Intent intent = getIntent();
    if (intent == null) {
      finish();
      return;
    }
    matchStatus = intent.getIntExtra(KEY_MATCH_STATUS, 0);
    rankingType = intent.getStringExtra(KEY_RANKING_TYPE);
    rankingTypeName = intent.getStringExtra(KEY_RANKING_TYPE_NAME);
    sysMatchId = intent.getStringExtra(KEY_SYS_MATCH_ID);
    sysSysMatchId = intent.getStringExtra(KEY_SYS_SYS_MATCH_ID);
    teamTag = intent.getStringExtra(KEY_TEAM_TAG);
    yaosu = intent.getStringExtra(KEY_YAOSU);
    unit = intent.getStringExtra(KEY_UNIT);
    isShow = intent.getBooleanExtra(KEY_IS_SHOW, false);


    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    ivUpload.setOnClickListener(v -> {
      if (dialog != null) {
        dialog.dismiss();
        dialog = null;
      }
      List<PlayInfo> temp = sqlService.queryPlayInfoPopupList(PlayingDataConstant.PlayingSource.MATCH);
      dialog = new SysnDataDialog(this);
      dialog.updateData(temp);
      dialog.setOnClickCallback(new SysnDataDialog.OnClickCallback() {
        @Override
        public void onClick(SysnDataDialog dialog, PlayInfo itemData, int position) {
          if (PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT.equals(itemData.getUploadStatus())
              || PlayingDataConstant.Update.STATUS_UPDATE_FAIL.equals(itemData.getUploadStatus())) {
//            UpLoadInfoModel upLoadInfoModel = App.self().getUpLoadInfoModel();
//            if (upLoadInfoModel == null) {
//              uploadLocalMatchPlayDefault(itemData, position);
//            } else {
//              if ("v2".equals(upLoadInfoModel.getEdition())) {
//                uploadLocalMatchPlayV2(itemData, position);
//              } else {
                uploadLocalMatchPlayV3(itemData, position);
//              }
//            }
          }
        }
        @Override
        public void onSysn(SysnDataDialog dialog, PlayInfo itemData, int position) {
          if (
              PlayingDataConstant.Update.STATUS_UPDATE_INCOMPLETE.equals(itemData.getUploadStatus())
                  || PlayingDataConstant.Update.STATUS_UPDATE_DEFAULT.equals(itemData.getUploadStatus())
                  || PlayingDataConstant.Update.STATUS_UPDATE_FAIL.equals(itemData.getUploadStatus())) {
//            UpLoadInfoModel upLoadInfoModel = App.self().getUpLoadInfoModel();
//            if (upLoadInfoModel == null) {
//              uploadLocalMatchPlayDefault(itemData, position);
//            } else {
//              if ("v2".equals(upLoadInfoModel.getEdition())) {
//                uploadLocalMatchPlayV2(itemData, position);
//              } else {
                uploadLocalMatchPlayV3(itemData, position);
//              }
//            }
          }
        }
        @Override
        public void onClose(SysnDataDialog dialog) {
          dialog.dismiss();
          AssociationTeamDetailRankingActivity.this.dialog = null;
        }
      });
    });

    if (matchStatus == ChampionshipsConstant.MATCH_STATUS_NOT_STARTED) {
      tvToolBarTitle.setText(getText(R.string.association_match_team_menber));
      tvMatchStatus.setText("(" + getString(R.string.association_match_status_no_start) + ")");
    } else if(matchStatus == ChampionshipsConstant.MATCH_STATUS_PLAYING) {
      tvToolBarTitle.setText(getText(R.string.association_match_team_chengji));
      tvMatchStatus.setText("(" + getString(R.string.association_match_status_playing_single_stage) + ")");
    } else if(matchStatus == ChampionshipsConstant.MATCH_STATUS_FINISH) {
      tvToolBarTitle.setText(getText(R.string.association_match_team_chengji));
      tvMatchStatus.setText("(" + getString(R.string.association_match_status_finish) + ")");
    }

    tvTeamName.setText(teamTag);

    if (isShow) {
      layYao.setVisibility(View.VISIBLE);
      tvType.setText(rankingTypeName);
      if (TextUtils.isEmpty(unit)) {
        tvYao.setText(getString(R.string.members_average_result) + "：" + yaosu);
      } else {
        tvYao.setText(getString(R.string.members_average_result) + "：" + yaosu + "（" + unit + "）");
      }
    } else {
      layYao.setVisibility(View.GONE);
    }

    initRanking();
    loadRankingListData(true, 1);

    List<PlayInfo> temp = sqlService.queryAllPlayInfoList();
    if (temp == null || temp.size() == 0) {
      ivUpload.setEnabled(false);
    } else {
      ivUpload.setEnabled(true);
    }
  }

  private void initRanking() {
    rankingList.clear();
    page = 1;

    adapter = new AssociationTeamDetailRankingAdapter(this, rankingList, isShowUnit, false, isShow);
    //初始化我的数据信息
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerview.setLayoutManager(layoutManager);
    recyclerview.addItemDecoration(new RecyclerViewDivider(0));
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    recyclerview.setPullRefreshEnabled(true);
    recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener(){
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

      }
    });
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        page = 0;
        loadRankingListData(true, 1);
      }

      @Override
      public void onLoadMore() {
        loadRankingListData(false, page + 1);
      }
    });

    recyclerview.setAdapter(adapter);

  }

  private void loadRankingListData(boolean isClear, int page) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(5);
    map.put("ranking_type", rankingType);
    map.put("sys_match_id", sysMatchId);
    map.put("sys_sys_match_id", sysSysMatchId);
    map.put("team_tag", teamTag);
    map.put("page", page);
    map.put("limit", limit);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<AssociationTeamDetailRankingModel> observable = apiServer.getMatchTeamDetailsLeaderboard(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<AssociationTeamDetailRankingModel>() {
              @Override
              public void onSuccess(AssociationTeamDetailRankingModel model) {
                if(recyclerview != null){
                  recyclerview.refreshComplete();
                  recyclerview.loadMoreComplete();
                }
                if (model == null) {
                  lyBottom.setVisibility(View.GONE);
                  rankingList.clear();
                  if(adapter != null){
                    adapter.notifyDataSetChanged();
                  }
                  return;
                }
                tvNumberSum.setText(getString(R.string.association_match_join_sum, model.getJoinSum() + ""));

                if (model.getList().size() > 0) {
                  if (isClear) {
                    rankingList.clear();
                  }
                  rankingList.addAll(model.getList());
                  AssociationTeamDetailRankingActivity.this.page++;
                }

                if (rankingList.size() == 0) {
                  ryEmpty.setVisibility(View.VISIBLE);
                } else {
                  ryEmpty.setVisibility(View.GONE);
                }

                AssociationTeamDetailRankingModel.MyRankingInfo myInfo = model.getMyRankingInfo();
                if (myInfo != null) {
                  lyBottom.setVisibility(View.VISIBLE);
                  Drawable drawableSex = null;
                  if (SexConstant.SEX_MAN.equals(myInfo.getSysSexId())) {
                    drawableSex = getResources().getDrawable(R.mipmap.ic_man);
                    drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
                  } else if (SexConstant.SEX_WOMEN.equals(myInfo.getSysSexId())) {
                    drawableSex = getResources().getDrawable(R.mipmap.ic_women);
                    drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
                  }
                  tvName.setCompoundDrawables(null, null, drawableSex, null);
                  tvName.setText(myInfo.getUserName());
                  tvArea.setText(myInfo.getAddress());
  
                  if (isShow) {
                    if(myInfo.getIndex() <= 3) {
                      ivRank.setVisibility(View.VISIBLE);
                      tvRank.setVisibility(View.GONE);
                      fyHead.setBackgroundResource(R.drawable.border_ranking_portrait_top);
                      if(myInfo.getIndex() == 1) {
                        ivRank.setBackgroundResource(R.mipmap.match_range_1);
                      } else if(myInfo.getIndex() == 2) {
                        ivRank.setBackgroundResource(R.mipmap.match_range_2);
                      } else if(myInfo.getIndex() == 3) {
                        ivRank.setBackgroundResource(R.mipmap.match_range_3);
                      }
                    } else{
                      ivRank.setVisibility(View.GONE);
                      tvRank.setVisibility(View.VISIBLE);
                      tvRank.setText(String.valueOf(myInfo.getIndex()));
                      fyHead.setBackgroundResource(R.drawable.border_ranking_portrait);
                    }
  
                    if ("0".equals(myInfo.getValue()) || "00:00:00".equals(myInfo.getValue())) {
                      ivRank.setVisibility(View.GONE);
                      tvRank.setVisibility(View.VISIBLE);
                      tvRank.setText("/");
                      tvShow.setVisibility(View.GONE);
                      tvUnit.setVisibility(View.GONE);
                      tvTime.setVisibility(View.GONE);
                      vDivider.setVisibility(View.GONE);
                    } else {
                      tvShow.setVisibility(View.VISIBLE);
                      tvShow.setText(myInfo.getValue());
                      if(isShowUnit == 1) {
                        tvUnit.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(myInfo.getUnit())) {
                          tvUnit.setText("（" + myInfo.getUnit() + "）");
                        }
                      }else{
                        tvUnit.setVisibility(View.GONE);
                      }
                      tvTime.setVisibility(View.VISIBLE);
                      vDivider.setVisibility(View.VISIBLE);
                      tvTime.setText(myInfo.getTime());
                    }
                  } else {
                    ivRank.setVisibility(View.GONE);
                    tvRank.setVisibility(View.VISIBLE);
                    tvRank.setText("/");
  
                    tvShow.setVisibility(View.GONE);
                    tvUnit.setVisibility(View.GONE);
                    tvTime.setVisibility(View.GONE);
                    vDivider.setVisibility(View.GONE);
                  }
  
                  if(myInfo.getUserImg().startsWith("http")) {
                    Picasso.with(AssociationTeamDetailRankingActivity.this)
                        .load(myInfo.getUserImg())
  //                    .centerCrop()
                        .transform(new CircleTransform(AssociationTeamDetailRankingActivity.this))
                        .placeholder(R.mipmap.default_head)
                        .into(ivHead);
                  } else {
                    Picasso.with(AssociationTeamDetailRankingActivity.this)
                        .load(Constant.getBaseUrl() + "/" + myInfo.getUserImg())
  //                    .centerCrop()
                        .transform(new CircleTransform(AssociationTeamDetailRankingActivity.this))
                        .placeholder(R.mipmap.default_head)
                        .into(ivHead);
                  } 
                } else {
                  lyBottom.setVisibility(View.GONE);
                }
                if(adapter != null){
                  adapter.notifyDataSetChanged();
                }

              }

              @Override
              public void onError(int code, String msg) {
                AppLogger.d("--requestRanking---" + msg);
                if(recyclerview != null){
                  recyclerview.refreshComplete();
                  recyclerview.loadMoreComplete();
                }
              }
            })
    );
  }

//  private void uploadLocalMatchPlayDefault(PlayInfo data, int position) {
//    List<SpeedDetail> speedDetailData = sqlService.querySpeedDetail(data.getSqlId());
//    List<Integer> speedDetail = new ArrayList<>();
//    for (int i = 0; i < speedDetailData.size(); i++) {
//      SpeedDetail itemSpeedDetail = speedDetailData.get(i);
//      speedDetail.add(itemSpeedDetail.getSpeed());
//    }
//    Integer[] speedDetailArr = speedDetail.toArray(new Integer[0]);
////    data.setSpeedDetail(new Gson().toJson(speedDetailArr));
//    data.setStatus(PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING);
//
//    if (dialog != null) {
//      dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING);
//    }
//
//    HashMap<String, Object> map = new HashMap<>();
//    map.put("exponent_molecular", data.getExponentMolecular());
//    map.put("endurance_max", data.getMaxEndurance());
//    map.put("is_abnormal", data.getIsAbnormal());
//    map.put("sys_match_id", data.getSysMatchId());
//    map.put("sys_sys_match_id", data.getSysSysMatchId());
//    map.put("stop_time", data.getStopTime());
//    map.put("start_time", data.getStartTime());
//    map.put("interval", data.getInterval());
//    map.put("created_uid", data.getCreatedUid());
//    map.put("speed_max", data.getMaxSpeed());
//    map.put("exponent", data.getExponent());
//    map.put("marathon", data.getMarathon());
//    map.put("is_quartets", data.getIsQuartets());
//    map.put("duration", data.getDuration());
//    map.put("distance", data.getDistance());
//    map.put("circle_count", data.getCircleCount());
//    map.put("exponent_denominator", data.getExponentDenominator());
//    map.put("speed_detail", new Gson().toJson(speedDetailArr));
//    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
//    Observable<ResponseBody> observable = apiServer.uploadLocalMatchPlayDefault(requestBody);
//    disposable.add(
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(new WristBallObserver<ResponseBody>() {
//              @Override
//              public void onSuccess(ResponseBody o) {
//                data.setStatus(PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
//                sqlService.insertOrUpdatePlayInfo(data);
//                if (dialog != null) {
//                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
//                }
//              }
//              @Override
//              public void onError(int code, String msg) {
//                data.setStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
//                sqlService.insertOrUpdatePlayInfo(data);
//                if (dialog != null) {
//                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
//                }
//              }
//            })
//    );
//  }

  private void uploadLocalMatchPlayV3(PlayInfo data, int position) {
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
    map.put("exponent_molecular", data.getExponentMolecular());
    map.put("endurance_max", data.getMaxEndurance());
    map.put("is_abnormal", data.getIsAbnormal());
    map.put("sys_match_id", data.getSysMatchId());
    map.put("sys_sys_match_id", data.getSysSysMatchId());
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
              }
              @Override
              public void onError(int code, String msg) {
                data.setUploadStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                sqlService.insertOrUpdatePlayInfo(data);
                if (dialog != null) {
                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
                }
              }
            })
    );
  }

//  private void uploadLocalMatchPlayV2(PlayInfo data, int position) {
//    List<SpeedDetail> speedDetailData = sqlService.querySpeedDetail(data.getSqlId());
//    List<Integer> speedDetail = new ArrayList<>();
//    for (int i = 0; i < speedDetailData.size(); i++) {
//      SpeedDetail itemSpeedDetail = speedDetailData.get(i);
//      speedDetail.add(itemSpeedDetail.getSpeed());
//    }
//    Integer[] speedDetailArr = speedDetail.toArray(new Integer[0]);
//    data.setSpeedDetail(speedDetailArr);
//    data.setStatus(PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING);
//    File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + "/play_data.json");
//    try {
//      if(!file.exists()){
//        file.createNewFile();
//      }
//      FileWriter fileWriter = new FileWriter(file);
//      fileWriter.write(new Gson().toJson(data));
//      fileWriter.close();
//    } catch (IOException e) {
//      e.printStackTrace();
//      return;
//    }
//    if (dialog != null) {
//      dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_UPLOADING);
//    }
//    MultipartBody.Part part = uploadFile("file", file);
//    Observable<ResponseBody> observable = apiServer.uploadLocalMatchPlayV2(part);
//    disposable.add(
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(new WristBallObserver<ResponseBody>() {
//              @Override
//              public void onSuccess(ResponseBody o) {
//                data.setStatus(PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
//                sqlService.insertOrUpdatePlayInfo(data);
//                if (dialog != null) {
//                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_SUCCESS);
//                }
//              }
//              @Override
//              public void onError(int code, String msg) {
//                data.setStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
//                sqlService.insertOrUpdatePlayInfo(data);
//                if (dialog != null) {
//                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
//                }
//              }
//            })
//    );
//  }
//
//  public MultipartBody.Part uploadFile(String fileName, File file){
//    RequestBody requestBody = getRequestBody(file);
//    MultipartBody.Part part = MultipartBody.Part.createFormData(fileName, file.getName(), requestBody);
//    return part;
//  }
//
//  public RequestBody getRequestBody(File file){
//    MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
//    RequestBody fileBody = RequestBody.create(mediaType, file);
//    return fileBody;
//  }

  @Override
  public void onDestroy(){
    super.onDestroy();
    rankingList.clear();
    if(adapter != null){
      adapter.notifyDataSetChanged();
      adapter = null;
    }
    if(recyclerview != null){
      recyclerview.destroy();
      recyclerview = null;
    }
    page = 1;
  }

}
