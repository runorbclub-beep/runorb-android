package com.cloud.runball.module.match_football_association;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.RankTypeInfo;
import com.cloud.runball.constant.ChampionshipsConstant;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.dialog.SysnDataDialog;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.service.sql.entity.PlayInfo;
import com.cloud.runball.service.sql.entity.SpeedDetail;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.utils.ResourceUtils;
import com.cloud.runball.widget.CircleTransform;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.ActivityAssociationRankingBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class AssociationRankingActivity extends BaseActivity {
  private ActivityAssociationRankingBinding binding;
  Toolbar toolbar;
  ImageView ivQuartetsIcon;
  ImageView logo;
  TextView tvToolBarTitle;
  TextView tvMatchStatus;
  TextView tvMatchTile;
  TextView tvMatchTime;
  LinearLayout layMatchSponsor;
  LinearLayout layTabSwitch;
  TextView tvPersonal;
  TextView tvTeam;
  MagicIndicator personalMagicIndicator;
  ViewPager2 personalViewPager;
  MagicIndicator teamMagicIndicator;
  ViewPager2 teamViewPager;
  ImageView ivUpload;

  public static final String KEY_SYS_SYS_MATCH_ID = "sysSysMatchId";
  public static final String KEY_SYS_MATCH_ID = "sysMatchId";
  public static final String KEY_IS_QUARTETS = "isQuartets";
  public static final String KEY_MATCH_NAME = "matchName";
  public static final String KEY_MATCH_STATUS = "matchStatus";
  public static final String KEY_MATCH_START_TIME = "matchStartTime";
  public static final String KEY_MATCH_STOP_TIME = "matchStopTime";
  public static final String KEY_MATCH_QUARTETS_ICON = "quartetsIcon";

  private boolean isQuartets;
  private String sysSysMatchId;
  private String sysMatchId;
  private String matchName;
  private int matchStatus;
  private long matchStartTime;
  private long matchStopTime;
  private String quartetsIcon;

  private SysnDataDialog dialog;

  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();

  public static void starAction(
      Context context, String sysSysMatchId, String sysMatchId, boolean isQuartets,
      String matchName,
      int matchStatus, long matchStartTime, long matchStopTime, String quartetsIcon
  ) {
    Intent intent = new Intent(context, AssociationRankingActivity.class);
    intent.putExtra(KEY_SYS_SYS_MATCH_ID, sysSysMatchId);
    intent.putExtra(KEY_SYS_MATCH_ID, sysMatchId);
    intent.putExtra(KEY_IS_QUARTETS, isQuartets);
    intent.putExtra(KEY_MATCH_NAME, matchName);
    intent.putExtra(KEY_MATCH_STATUS, matchStatus);
    intent.putExtra(KEY_MATCH_START_TIME, matchStartTime);
    intent.putExtra(KEY_MATCH_STOP_TIME, matchStopTime);
    intent.putExtra(KEY_MATCH_QUARTETS_ICON, quartetsIcon);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_association_ranking;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityAssociationRankingBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    ivQuartetsIcon = binding.ivQuartetsIcon;
    logo = binding.logo;
    tvToolBarTitle = binding.tvToolBarTitle;
    tvMatchStatus = binding.tvMatchStatus;
    tvMatchTile = binding.tvMatchTile;
    tvMatchTime = binding.tvMatchTime;
    layMatchSponsor = binding.layMatchSponsor;
    layTabSwitch = binding.layTabSwitch;
    tvPersonal = binding.tvPersonal;
    tvTeam = binding.tvTeam;
    personalMagicIndicator = binding.personalMagicIndicator;
    personalViewPager = binding.personalViewPager;
    teamMagicIndicator = binding.teamMagicIndicator;
    teamViewPager = binding.teamViewPager;
    ivUpload = binding.ivUpload;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    Intent intent = getIntent();
    isQuartets = intent.getBooleanExtra(KEY_IS_QUARTETS, false);
    sysSysMatchId = intent.getStringExtra(KEY_SYS_SYS_MATCH_ID);
    sysMatchId = intent.getStringExtra(KEY_SYS_MATCH_ID);
    matchName = intent.getStringExtra(KEY_MATCH_NAME);
    matchStatus = intent.getIntExtra(KEY_MATCH_STATUS, 0);
    matchStartTime = intent.getLongExtra(KEY_MATCH_START_TIME, 0);
    matchStopTime = intent.getLongExtra(KEY_MATCH_STOP_TIME, 0);
    quartetsIcon = intent.getStringExtra(KEY_MATCH_QUARTETS_ICON);

    if (isQuartets) {
      layMatchSponsor.setVisibility(View.VISIBLE);
      layTabSwitch.setVisibility(View.VISIBLE);
    } else {
      layMatchSponsor.setVisibility(View.GONE);
      layTabSwitch.setVisibility(View.GONE);
    }

    // Replace @OnClick with listeners
    binding.tvPersonal.setOnClickListener(this::onClick);
    binding.tvTeam.setOnClickListener(this::onClick);
    binding.ivUpload.setOnClickListener(this::onClick);

    if (!TextUtils.isEmpty(quartetsIcon)) {
      String img;
      if (quartetsIcon.startsWith("http")) {
        img = quartetsIcon;
      } else {
        img = Constant.getBaseUrl() + "/" + quartetsIcon;
      }

      Picasso.with(this)
          .load(img)
          .transform(new CircleTransform(this))
          .into(binding.ivQuartetsIcon);
    }

    Picasso.with(this)
        .load(R.mipmap.logo)
        .transform(new CircleTransform(this))
        .into(logo);

    tvMatchTile.setText(matchName);

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    tvMatchTime.setText(format.format(matchStartTime * 1000) + " - " + format.format(matchStopTime * 1000));

    if (matchStatus == ChampionshipsConstant.MATCH_STATUS_NOT_STARTED) {
      tvMatchStatus.setText("(" + getString(R.string.lbl_other_match_status_0) + ")");
      tvToolBarTitle.setText(getString(R.string.association_match_ranking_sign_up));
      tvPersonal.setText(R.string.association_tab_name_list_personal);
      tvTeam.setText(R.string.association_tab_name_list_team);
    } else if (matchStatus == ChampionshipsConstant.MATCH_STATUS_PLAYING) {
      tvMatchStatus.setText("(" + getString(R.string.lbl_other_match_status_2) + ")");
      tvToolBarTitle.setText(getString(R.string.association_match_ranking));
      tvPersonal.setText(getString(R.string.association_tab_ranking_personal));
      tvTeam.setText(getString(R.string.association_tab_ranking_team));

    } else if (matchStatus == ChampionshipsConstant.MATCH_STATUS_FINISH) {
      tvMatchStatus.setText("(" + getString(R.string.lbl_other_match_status_3) + ")");
      tvToolBarTitle.setText(getString(R.string.association_match_ranking));
      tvPersonal.setText(getString(R.string.association_tab_ranking_personal));
      tvTeam.setText(getString(R.string.association_tab_ranking_team));
    }

    initRequestRankTypes();

    List<PlayInfo> temp = sqlService.queryAllPlayInfoList();
    if (temp == null || temp.size() == 0) {
      ivUpload.setEnabled(false);
    } else {
      ivUpload.setEnabled(true);
    }

  }

  public void onClick(View view) {
    if (view.getId() == R.id.tvPersonal) {
      tvPersonal.setBackgroundResource(R.drawable.bg_ranking_switch_select_yes);
      tvPersonal.setTextColor(Color.parseColor("#1E1D1F"));
      tvTeam.setBackgroundResource(R.drawable.bg_ranking_switch_select_no);
      tvTeam.setTextColor(Color.parseColor("#F9E85B"));
      personalMagicIndicator.setVisibility(View.VISIBLE);
      personalViewPager.setVisibility(View.VISIBLE);
      teamMagicIndicator.setVisibility(View.GONE);
      teamViewPager.setVisibility(View.GONE);
    } else if (view.getId() == R.id.tvTeam) {
      tvPersonal.setBackgroundResource(R.drawable.bg_ranking_switch_select_no);
      tvPersonal.setTextColor(Color.parseColor("#F9E85B"));
      tvTeam.setBackgroundResource(R.drawable.bg_ranking_switch_select_yes);
      tvTeam.setTextColor(Color.parseColor("#1E1D1F"));
      personalMagicIndicator.setVisibility(View.GONE);
      personalViewPager.setVisibility(View.GONE);
      teamMagicIndicator.setVisibility(View.VISIBLE);
      teamViewPager.setVisibility(View.VISIBLE);
    } else if (view.getId() == R.id.ivUpload) {
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
          AssociationRankingActivity.this.dialog = null;
        }
      });

    }
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
//    UpLoadInfoModel upLoadInfoModel = App.self().getUpLoadInfoModel();
//    Observable<ResponseBody> observable = apiServer.uploadLocalMatchPlayV2(upLoadInfoModel.getDomainName() + upLoadInfoModel.getPlayUrl(), part);
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
////                  Toast.makeText(AssociationRankingActivity.this, "数据上传成功", Toast.LENGTH_SHORT).show();
//              }
//              @Override
//              public void onError(int code, String msg) {
//                data.setStatus(PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
//                sqlService.insertOrUpdatePlayInfo(data);
//                if (dialog != null) {
//                  dialog.updateItem(position, PlayingDataConstant.Update.STATUS_UPDATE_FAIL);
//                }
////                  Toast.makeText(AssociationRankingActivity.this, "数据上传失败", Toast.LENGTH_SHORT).show();
//              }
//            })
//    );
//  }

  public MultipartBody.Part uploadFile(String fileName, File file){
    RequestBody requestBody = getRequestBody(file);
    MultipartBody.Part part = MultipartBody.Part.createFormData(fileName, file.getName(), requestBody);
    return part;
  }

  public RequestBody getRequestBody(File file){
    MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
    RequestBody fileBody = RequestBody.create(mediaType, file);
    return fileBody;
  }

  private void initRequestRankTypes(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ResponseBody> observable = apiServer.requestRankTypes();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
                 @Override
                 public void onSuccess(ResponseBody responseBody){
                   try {
                     JSONObject jsonObject=new JSONObject(responseBody.string());
                     if(jsonObject.optInt("code") == 1) {
                       Gson gson=new Gson();
                       List<RankTypeInfo> rankTypeInfos = gson.fromJson(jsonObject.optString("data"), new TypeToken<List<RankTypeInfo>>(){}.getType());
                       initTabNav(rankTypeInfos);
                     }
                   }catch (Exception ex){
                     ex.printStackTrace();
                   }
                 }

                 @Override
                 public void onError(int code, String msg) {
                   AppLogger.d(msg);
                 }
               }
)
    );
  }

  private void initTabNav(List<RankTypeInfo> rankTypes) {
    boolean isZh = ResourceUtils.isZhCn(this);

    List<Fragment> personalFragments = new ArrayList<>();
    String[] personalTitles = new String[rankTypes.size()];
    int personalI = 0;
    for(RankTypeInfo typeInfo : rankTypes){
      personalFragments.add(AssociationRankingPersonalSubFragment.newInstance(typeInfo.getType(), sysSysMatchId, sysMatchId, matchStatus != ChampionshipsConstant.MATCH_STATUS_NOT_STARTED));
      personalTitles[personalI] = isZh ? typeInfo.getTitle_zh() : typeInfo.getTitle_en();
      personalI++;
    }
    personalViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    personalViewPager.setAdapter(new XFragmentStateAdapter(this, personalFragments));
    CommonNavigator personalCommonNavigator = new CommonNavigator(this);
    personalCommonNavigator.setAdjustMode(true);
    personalCommonNavigator.setAdapter(new XCommonNavigatorAdapter(personalTitles, index -> {
      personalViewPager.setCurrentItem(index);
    }));
    personalMagicIndicator.setNavigator(personalCommonNavigator);
//    LinearLayout personaTitleContainer = personalCommonNavigator.getTitleContainer();
//    personaTitleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//    personaTitleContainer.setDividerDrawable(new ColorDrawable() {
//      @Override
//      public int getIntrinsicWidth() {
//        return UIUtil.dip2px(AssociationRankingActivity.this, 15);
//      }
//    });
    final FragmentContainerHelper personalFragmentContainerHelper = new FragmentContainerHelper(personalMagicIndicator);
    personalFragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    personalFragmentContainerHelper.setDuration(300);
    personalViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        personalFragmentContainerHelper.handlePageSelected(position);
      }
    });

    List<Fragment> teamFragments = new ArrayList<>();
    String[] teamTitles = new String[rankTypes.size()];
    int i = 0;
    for(RankTypeInfo typeInfo : rankTypes){
      teamFragments.add(AssociationRankingTeamSubFragment.newInstance(typeInfo.getType(), typeInfo.getTitle_zh(), sysSysMatchId, sysMatchId, matchStatus, matchStatus != ChampionshipsConstant.MATCH_STATUS_NOT_STARTED));
      teamTitles[i] = isZh ? typeInfo.getTitle_zh() : typeInfo.getTitle_en();
      i++;
    }
    teamViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    teamViewPager.setAdapter(new XFragmentStateAdapter(this, teamFragments));
    CommonNavigator teamCommonNavigator = new CommonNavigator(this);
    teamCommonNavigator.setAdjustMode(true);
    teamCommonNavigator.setAdapter(new XCommonNavigatorAdapter(teamTitles, index -> {
      teamViewPager.setCurrentItem(index);
    }));
    teamMagicIndicator.setNavigator(teamCommonNavigator);
//    LinearLayout teamTitleContainer = teamCommonNavigator.getTitleContainer();
//    teamTitleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//    teamTitleContainer.setDividerDrawable(new ColorDrawable() {
//      @Override
//      public int getIntrinsicWidth() {
//        return UIUtil.dip2px(AssociationRankingActivity.this, 15);
//      }
//    });
    final FragmentContainerHelper teamFragmentContainerHelper = new FragmentContainerHelper(teamMagicIndicator);
    teamFragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    teamFragmentContainerHelper.setDuration(300);
    teamViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        teamFragmentContainerHelper.handlePageSelected(position);
      }
    });

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
