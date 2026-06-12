package com.cloud.runball.module.match_football_association;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.constant.ChampionshipsConstant;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;
import com.cloud.runball.dialog.PKRuleDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.module.match.MatchMainActivity2;
import com.cloud.runball.module.match.MatchRankActivity;
import com.cloud.runball.module.match.RankMatchMainActivity;
import com.cloud.runball.module.match_football_association.adapter.MatchDetailOptionAdapter;
import com.cloud.runball.module.match_football_association.adapter.MatchStageItemAdapter;
import com.cloud.runball.module.match_football_association.dialog.AssociationMatchTipDialog;
import com.cloud.runball.module.match_football_association.dialog.AssociationSelectTeamDialog;
import com.cloud.runball.module.match_football_association.entity.MatchDetailInfoItem;
import com.cloud.runball.module.match_football_association.entity.MatchStage;
import com.cloud.runball.module.match_football_association.entity.model.MatchDetailModel;
import com.cloud.runball.module.race.CreateRankMatchAddActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.ActivityAssociationMatchDetailBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: MatchDetailActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/1 10:25
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/1 10:25
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AssociationMatchDetailActivity extends BaseActivity implements View.OnClickListener {

  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  public static final int REPORT_OK = 99;

  public static final String KEY_MATCH_STATUS = "match_status";
  public static final String KEY_MATCH_ID = "match_id";

  private ActivityAssociationMatchDetailBinding binding;
  ImageView ivBanner;
  Toolbar toolbar;
  TextView tvMatchName;
  TextView tvMatchStatus;
  RecyclerView rvMatchOptionList;
  RecyclerView recyclerviewStages;
  TextView btnCheckSignUp;
  TextView btnSignUp;
  TextView btnCheckRank;
  TextView btnEnterMatch;
  LinearLayout layTeamInfo;
  TextView tvTeamName;
  TextView tvNemberSum;

  private MatchDetailOptionAdapter mMatchOptionAdapter;
  private final List<MatchDetailInfoItem> matchDetailInfoList = new ArrayList<>();
  private final List<MatchStage> matchStages = new ArrayList<>();
  private String sys_sys_match_id;
  private int is_group;

  private String sysMatchId;
  private int matchStatus;

  private String matchTitle;
  private MatchDetailModel mDetailModel;
  private MatchStageItemAdapter mStageItemAdapter;
  private int userJoinStatus = -1;
  private int isMembers = 0;

  private boolean isQuartets = false;

  private boolean signSuccess=false;

  private String joinMatchUnits;

  public static void startAction(Context context, String sysMatchId) {
    Intent intent = new Intent(context, AssociationMatchDetailActivity.class);
    intent.putExtra(KEY_MATCH_ID, sysMatchId);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_association_match_detail;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityAssociationMatchDetailBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    ivBanner = binding.ivBanner;
    toolbar = binding.toolbar;
    tvMatchName = binding.tvMatchName;
    tvMatchStatus = binding.tvMatchStatus;
    rvMatchOptionList = binding.rvMatchOptionList;
    recyclerviewStages = binding.recyclerviewStages;
    btnCheckSignUp = binding.btnCheckSignUp;
    btnSignUp = binding.btnSignUp;
    btnCheckRank = binding.btnCheckRank;
    btnEnterMatch = binding.btnEnterMatch;
    layTeamInfo = binding.layTeamInfo;
    tvTeamName = binding.tvTeamName;
    tvNemberSum = binding.tvNemberSum;
    sysMatchId = getIntent().getStringExtra(KEY_MATCH_ID);

    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    mMatchOptionAdapter = new MatchDetailOptionAdapter(matchDetailInfoList);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);


    rvMatchOptionList.setHasFixedSize(true);
    rvMatchOptionList.setLayoutManager(manager);
    rvMatchOptionList.setItemAnimator(new DefaultItemAnimator());
    rvMatchOptionList.setAdapter(mMatchOptionAdapter);

    //规则
    LinearLayoutManager manager2 = new LinearLayoutManager(this);
    manager2.setOrientation(LinearLayoutManager.VERTICAL);
    mStageItemAdapter = new MatchStageItemAdapter(this, matchStages);
    mStageItemAdapter.setStageItemRuleListener(rule -> {
      PKRuleDialog.show(this, rule);
    });
    recyclerviewStages.setHasFixedSize(true);
    recyclerviewStages.setLayoutManager(manager2);
    recyclerviewStages.setAdapter(mStageItemAdapter);

    // Replace @OnClick with listeners
    btnSignUp.setOnClickListener(this::onViewClicked);
    btnCheckSignUp.setOnClickListener(this::onViewClicked);
    btnCheckRank.setOnClickListener(this::onViewClicked);
    btnEnterMatch.setOnClickListener(this::onViewClicked);
  }

  @Override
  public void onResume(){
    super.onResume();
    //请求锦标赛赛事详情
    requestRankMatchDetail(sysMatchId);
  }

  @Override
  public void finish() {
    super.finish();
    if(signSuccess){
      setResult(REPORT_OK);
    }
  }

  public void onViewClicked(View v) {
    if(v.getId() == R.id.btnSignUp) {
      if(matchStatus != 3) {
        //未开始---->已开始
        if(userJoinStatus == 0) {
          //未报名
          if (isQuartets) {
            AssociationMatchTipDialog dialog = new AssociationMatchTipDialog(this);
            dialog.setCallback(joinMatchUnits, new AssociationMatchTipDialog.Callback() {
              @Override
              public void onSubmit(Dialog dialog) {
                dialog.dismiss();
                joinRankMatch(sys_sys_match_id, sysMatchId, is_group);
              }
              @Override
              public void onCancel(Dialog dialog) {
                dialog.dismiss();
              }
            });
          } else {
            joinRankMatch(sys_sys_match_id, sysMatchId, is_group);
          }
        } else if(userJoinStatus == 1) {
          //已经报名
          //根据所处赛段进去进入主页面赛段还是摇跑赛赛段
          if (isQuartets) {
            AssociationCommonDialog dialog = new AssociationCommonDialog(this);
            dialog.setContent(getString(R.string.tip), getString(R.string.tip_are_you_sure_to_cancel_your_registration));
            dialog.addBtn(getString(R.string.btn_cancel), false, commonDialog -> {
              commonDialog.dismiss();
            });
            dialog.addBtn(getString(R.string.btn_ok), true, commonDialog -> {
              matchUserSignOutV2(mDetailModel.getSysMatchId());
              commonDialog.dismiss();
            });
          } else {
            if(mDetailModel != null) {
              if(mDetailModel.getViewType() == ChampionshipsConstant.TYPE_RANKING) {
                if (isQuartets) {
//                  AssociationMatchActivity.startAction(
//                      this, mDetailModel.getMatchTitle(), sysMatchId, sys_sys_match_id, isQuartets ? 1 : 0,
//                      mDetailModel.getMatchStartTime(), mDetailModel.getMatchStopTime()
//                  );
                  for (MatchStage stage : mDetailModel.getMatchStageList()) {
                    if (stage.getMatchStageStatus() == ChampionshipsConstant.MATCH_STATUS_PLAYING) {
                      SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                      try {
                        Date startDate = format.parse(stage.getStartTime());
                        Date stopDate = format.parse(stage.getStopTime());
                        AssociationMatchActivity.startAction(
                            this, mDetailModel.getMatchTitle(), sysMatchId, sys_sys_match_id, mDetailModel.getMatchStageId(), isQuartets ? 1 : 0,
                            startDate.getTime() / 1000, stopDate.getTime() / 1000, joinMatchUnits
                        );
                      } catch (ParseException e) {
                        e.printStackTrace();
                      }
                      break;
                    }
                  }
                } else {
                  startRankMatchMainActivity(matchTitle, mDetailModel.getSysMatchId(), mDetailModel.getUserJoinStatus().getUserGroupId(), mDetailModel.getMatchStageId());
                }
              }else if(mDetailModel.getViewType() == ChampionshipsConstant.TYPE_YJY) {
                startListMatchActivity(mDetailModel.getSysMatchId(), mDetailModel.getMatchStageId(), mDetailModel.getUserJoinStatus().getUserGroupId(), mDetailModel.getMatchTitle());
              }else if(mDetailModel.getViewType() == ChampionshipsConstant.TYPE_NO) {
                Toast.makeText(App.self().getApplicationContext(),R.string.lbl_match_no_match,Toast.LENGTH_LONG).show();
              }
            }
          }
        }
      } else if(matchStatus == 3) {
        //已经结束 TODO Is_Exponent
        startRankActivity(matchTitle, mDetailModel.getSysMatchId(), mDetailModel.getIsGroup(), 0);
//        startRankActivity(matchTitle, mDetailModel.getSysMatchId(), mDetailModel.getIsGroup(), mDetailModel.getIsExponent());
      }
    } else if (v.getId() == R.id.btnCheckRank) {
      AssociationRankingActivity.starAction(
          this, sys_sys_match_id, sysMatchId, isQuartets, mDetailModel.getMatchTitle(),
          matchStatus, mDetailModel.getMatchStartTime(), mDetailModel.getMatchStopTime(), quartetsIcon
      );
    } else if (v.getId() == R.id.btnEnterMatch) {
//      AssociationMatchActivity.startAction(
//          this, mDetailModel.getMatchTitle(), sysMatchId, sys_sys_match_id, isQuartets ? 1 : 0,
//          mDetailModel.getMatchStartTime(), mDetailModel.getMatchStopTime()
//      );
      for (MatchStage stage : mDetailModel.getMatchStageList()) {
        if (stage.getMatchStageStatus() == ChampionshipsConstant.MATCH_STATUS_PLAYING) {
          SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
          try {
            Date startDate = format.parse(stage.getStartTime());
            Date stopDate = format.parse(stage.getStopTime());
            AssociationMatchActivity.startAction(
                this, mDetailModel.getMatchTitle(), sysMatchId, sys_sys_match_id, mDetailModel.getMatchStageId(), isQuartets ? 1 : 0,
                startDate.getTime() / 1000, stopDate.getTime() / 1000, joinMatchUnits
            );
          } catch (ParseException e) {
            e.printStackTrace();
          }
          break;
        }
      }
    } else if (v.getId() == R.id.btnCheckSignUp) {
      AssociationRankingActivity.starAction(
          this, sys_sys_match_id, sysMatchId, isQuartets, mDetailModel.getMatchTitle(),
          matchStatus, mDetailModel.getMatchStartTime(), mDetailModel.getMatchStopTime(), quartetsIcon
      );
    }
  }

  /**
   * 报名
   * @param sys_sys_match_id
   * @param sys_match_id
   * @param is_group
   */
  private void joinRankMatch(String sys_sys_match_id, String sys_match_id, int is_group){
    if (isQuartets) {
      getSignUpTeamTag(sys_match_id, sys_sys_match_id);
    } else {
      if(is_group == 0) {
        //个人赛直接报名
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.startsWith("zh")){
          language = "zh-CN";
        }else{
          language = "en-US";
        }
        requestRankMatchSign(sys_sys_match_id, sys_match_id, language);
      }else{
        startRankCreateMatchActivity(sys_sys_match_id, sys_match_id, is_group);
      }
    }
  }

  private void getSignUpTeamTag(String sys_match_id, String sys_sys_match_id) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_match_id", sys_sys_match_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.getSignUpTeamTagV2(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
                 @Override
                 public void onSuccess(ResponseBody responseBody) {
                   try{
                     JSONObject jsonObject = new JSONObject(responseBody.string());
                     int code = jsonObject.optInt("code");
                     if(code == 1) {
                       JSONArray data = jsonObject.optJSONArray("data");
                       List<String> teamData = new ArrayList<>();
                       for (int i = 0; i < data.length(); i++) {
                         teamData.add(data.optString(i));
                       }

                       AssociationSelectTeamDialog dialog = new AssociationSelectTeamDialog(AssociationMatchDetailActivity.this);
                       dialog.setData(teamData);
                       dialog.setCallback((dialog1, team) -> {
                         dialog.dismiss();
                         //个人赛直接报名
                         Locale locale = getResources().getConfiguration().locale;
                         String language = locale.getLanguage();
                         if(language.startsWith("zh")){
                           language = "zh-CN";
                         }else{
                           language = "en-US";
                         }

                         AssociationCommonDialog dialog2 = new AssociationCommonDialog(AssociationMatchDetailActivity.this);
                         dialog2.setContent(getString(R.string.tip), getString(R.string.tip_confirm_whether_to_select_join) + team + "?");
                         dialog2.addBtn(getString(R.string.btn_cancel), false, commonDialog -> {
                           commonDialog.dismiss();
                         });
                         String finalLanguage = language;
                         dialog2.addBtn(getString(R.string.btn_ok), true, commonDialog -> {
                           commonDialog.dismiss();
                           requestRankMatchSignV2(sys_sys_match_id, sys_match_id, finalLanguage, team);
                         });
                       });
                     }
                   }catch (Exception ex) {
                     ex.printStackTrace();
                   }
                 }
                 @Override
                 public void onError(int code, String msg) {
                   AppLogger.d("---getSignUpTeamTag-----"+msg);
                 }
               }
            )
    );
  }

  /**
   * 报名参赛 v2
   * @param sys_sys_match_id
   * @param sys_match_id
   * @param language
   */
  private void requestRankMatchSignV2(String sys_sys_match_id, String sys_match_id, String language, String teamTag){
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_sys_match_id", sys_sys_match_id);
    map.put("sys_match_id", sys_match_id);
    map.put("language", language);
    map.put("is_group", 0);
    map.put("is_quartets", isQuartets);
    map.put("team_tag", teamTag);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.matchUserSignV2(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
               @Override
               public void onSuccess(ResponseBody responseBody) {
                 try{
                   JSONObject jsonObject = new JSONObject(responseBody.string());
                   int code = jsonObject.optInt("code");
                   if(code == 1) {
                     //报名成功,显示立即比赛
                     Toast.makeText(getApplication(),jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                     if(mDetailModel.getUserJoinStatus() != null) {
                       mDetailModel.getUserJoinStatus().setIsJoin(1);
                       btnSignUp.setText(R.string.lbl_match_right_now);
                     }
                     signSuccess=true;
                     //请求锦标赛赛事详情
                     requestRankMatchDetail(sys_match_id);
                   }else if(code==2) {
                     String msg=jsonObject.optString("msg");
                     Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                   }else{
                     Toast.makeText(getApplication(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                   }
                 }catch (Exception ex) {
                   ex.printStackTrace();
                 }
               }
               @Override
               public void onError(int code, String msg) {
                 if(code == 2) {
                   Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                 }
                 AppLogger.d("---requestRankMatchSign-----"+msg);
               }
             }
            )
    );
  }

  // 取消报名
  private void matchUserSignOutV2(String sys_match_id) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_sys_match_id", sys_sys_match_id);
    map.put("sys_match_id", sys_match_id);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.matchUserSignOutV2(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
               @Override
               public void onSuccess(ResponseBody responseBody) {
                 try{
                   JSONObject jsonObject = new JSONObject(responseBody.string());
                   int code = jsonObject.optInt("code");
                   if(code == 1) {
                     requestRankMatchDetail(sys_match_id);
                   }else if(code == 2) {
                     String msg=jsonObject.optString("msg");
                     Toast.makeText(getApplication(), msg,Toast.LENGTH_SHORT).show();
                   }else{
                     Toast.makeText(getApplication(), jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                   }
                 }catch (Exception ex) {
                   ex.printStackTrace();
                 }
               }
               @Override
               public void onError(int code, String msg) {
                 if(code == 2) {
                   Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                 }
                 AppLogger.d("---matchUserSignOutV2-----"+msg);
               }
             }
            )
    );
  }

  /**
   * 报名参赛
   * @param sys_sys_match_id
   * @param sys_match_id
   * @param language
   */
  private void requestRankMatchSign(String sys_sys_match_id,String sys_match_id,String language){
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_sys_match_id", sys_sys_match_id);
    map.put("sys_match_id", sys_match_id);
    map.put("language", language);
    map.put("is_group", 0);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.matchSign(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
              @Override
              public void onSuccess(ResponseBody responseBody) {
                try{
                  JSONObject jsonObject = new JSONObject(responseBody.string());
                  int code = jsonObject.optInt("code");
                  if(code == 1) {
                    //报名成功,显示立即比赛
                    Toast.makeText(getApplication(),jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                    if(mDetailModel.getUserJoinStatus() != null) {
                      mDetailModel.getUserJoinStatus().setIsJoin(1);
                      btnSignUp.setText(R.string.lbl_match_right_now);
                    }
                    signSuccess=true;
                    //请求锦标赛赛事详情
                    requestRankMatchDetail(sys_match_id);
                  }else if(code == 2) {
                    String msg=jsonObject.optString("msg");
                    Toast.makeText(getApplication(), msg,Toast.LENGTH_SHORT).show();
                  }else{
                    Toast.makeText(getApplication(), jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                  }
                }catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
              @Override
              public void onError(int code, String msg) {
                if(code == 2) {
                  Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                }
                AppLogger.d("---requestRankMatchSign-----"+msg);
              }
            }
        )
    );
  }

  /**
   * 进入报名页面
   * @param sys_sys_match_id
   * @param sys_match_id
   * @param is_group
   */
  private void startRankCreateMatchActivity(String sys_sys_match_id,String sys_match_id,int is_group){
    Intent it=new Intent(this, CreateRankMatchAddActivity.class);
    it.putExtra("sys_sys_match_id",sys_sys_match_id);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("is_group",is_group);
    startActivity(it);
  }

  /**
   * 进入摇跑赛页面
   * @param sys_match_id      赛事ID
   * @param matchs_stage_id   赛段ID
   * @param user_group_id     所属队伍ID
   */
  private void startListMatchActivity(String sys_match_id,String matchs_stage_id,String user_group_id,String match_title) {
    Intent it=new Intent(this, MatchMainActivity2.class);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("matchs_stage_id",matchs_stage_id);
    it.putExtra("user_group_id",user_group_id);
    it.putExtra("match_title",match_title);
    startActivity(it);
  }

  /**
   * 锦标赛(主界面赛事)
   * @param title
   * @param sys_match_id
   * @param user_group_id  所属用户组ID
   */
  private void startRankMatchMainActivity(String title,String sys_match_id,String user_group_id,String matchs_stage_id) {
    Intent it = new Intent(this, RankMatchMainActivity.class);
    it.putExtra("title",title);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("user_group_id",user_group_id);
    it.putExtra("matchs_stage_id",matchs_stage_id);
    startActivity(it);
  }

  /**
   * 排行榜
   * @param title
   * @param sys_match_id
   * @param is_group
   */
  private void startRankActivity(String title,String sys_match_id,int is_group,int is_exponent) {
    Intent it = new Intent(this, MatchRankActivity.class);
    it.putExtra("title",title);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("is_group",is_group);
    it.putExtra("is_exponent",is_exponent);
    startActivity(it);
  }

  private void requestRankMatchDetail(String sysMatchId){
    HashMap<String, Object> map = new HashMap<>(2);
    map.put("sys_match_id", sysMatchId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<MatchDetailModel> observable = apiServer.getMatchDetailV2(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MatchDetailModel>() {
              @Override
              public void onSuccess(MatchDetailModel model) {
                showDetail(model);
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("--------requestRankMatchDetail-------code-"+msg);
              }
            }
        )
    );
  }

  private String quartetsIcon;

  private void showDetail(MatchDetailModel model) {
    if (model.getTeamInfo() == null) {
      layTeamInfo.setVisibility(View.GONE);
    } else {
      layTeamInfo.setVisibility(View.VISIBLE);
      tvTeamName.setText(getString(R.string.my_team) + "：" + model.getTeamInfo().getTeamTag());
      tvNemberSum.setText(getString(R.string.association_match_join_sum, model.getTeamInfo().getJoinSum() + ""));
    }

    joinMatchUnits = model.getTeamName();
    mDetailModel = model;
    //会员报名情况
    isMembers = AppDataManager.getInstance().getUserInfoModel().getUser_info().getIs_members();

    isQuartets = model.getIsQuartets() == 1;
    quartetsIcon = model.getQuartetsIcon();

    tvMatchName.setText(model.getMatchTitle());

    //赛事状态
    matchStatus = model.getMatchStatus();
    if (matchStatus == ChampionshipsConstant.MATCH_STATUS_NOT_STARTED) { // 未开始
      tvMatchStatus.setBackgroundColor(Color.parseColor("#CC464A71"));
      tvMatchStatus.setTextColor(Color.parseColor("#FFFFFF"));
      tvMatchStatus.setText(getString(R.string.association_match_status_no_start));
    } else if(matchStatus == ChampionshipsConstant.MATCH_STATUS_PLAYING) { // 进行中
      tvMatchStatus.setBackgroundColor(Color.parseColor("#CCFDE833"));
      tvMatchStatus.setTextColor(Color.parseColor("#25282C"));
      if (model.getMatchStageList() == null || model.getMatchStageList().size() <= 1) {
        tvMatchStatus.setText(getString(R.string.association_match_status_playing_single_stage));
      } else {
        tvMatchStatus.setText(getString(R.string.association_match_status_playing_multiple_stage));
        for (MatchStage item: model.getMatchStageList()) {
          if (ChampionshipsConstant.MATCH_STATUS_PLAYING == item.getMatchStageStatus()) {
            tvMatchStatus.append(item.getMatchStageTitle());
            break;
          }
        }
      }
    }else if(matchStatus == ChampionshipsConstant.MATCH_STATUS_FINISH) { // 已结束
      tvMatchStatus.setBackgroundColor(Color.parseColor("#CC999999"));
      tvMatchStatus.setTextColor(Color.parseColor("#FFFFFF"));
      tvMatchStatus.setText(getString(R.string.association_match_status_finish));
    }

    // 页面广告图片
    if (model.getMatchImage().startsWith("http")) {
      Picasso.with(this)
          .load(model.getMatchImage())
          .into(ivBanner);
    } else {
      Picasso.with(this)
          .load(Constant.getBaseUrl() + "/" + model.getMatchImage())
          .into(ivBanner);
    }

    matchTitle = model.getMatchTitle();
    sys_sys_match_id = model.getSysSysMatchId();
    is_group = model.getIsGroup();
    btnSignUp.setVisibility(View.VISIBLE);

    //显示赛段信息
    showStages(model.getMatchStageList());

    // 底部按钮
    if (model.getUserJoinStatus() == null) {
      btnCheckSignUp.setEnabled(false);
      btnCheckSignUp.setVisibility(View.GONE);
      btnSignUp.setEnabled(false);
      btnSignUp.setVisibility(View.GONE);
      btnCheckRank.setEnabled(true);
      btnCheckRank.setVisibility(View.VISIBLE);
      btnEnterMatch.setEnabled(false);
      btnEnterMatch.setVisibility(View.GONE);
    } else {
      userJoinStatus = model.getUserJoinStatus().getIsJoin();
      if (matchStatus == ChampionshipsConstant.MATCH_STATUS_NOT_STARTED) {
        //未开始,按钮显示即将开始
        btnCheckSignUp.setEnabled(true);
        btnCheckSignUp.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(true);
        btnSignUp.setVisibility(View.VISIBLE);
        if (userJoinStatus == ChampionshipsConstant.USER_JOIN_STATUS_NO) { // 用户未报名
          btnSignUp.setText(R.string.lbl_match_sign_now);
          btnSignUp.setBackgroundResource(R.drawable.selector_match_btn_sign_up);
          btnSignUp.setTextColor(Color.parseColor("#25282C"));
          //赛事报名条件 0：开放报名，1：关闭报名，2：允许会员报名
          if (model.getJoinStatus() == ChampionshipsConstant.JOIN_STATUS_CLOSE) {
            btnSignUp.setEnabled(false);
          } else if (model.getJoinStatus() == ChampionshipsConstant.JOIN_STATUS_MEMBER) {
            if (isMembers == 1) {
              btnSignUp.setEnabled(false);
            } else {
              btnSignUp.setEnabled(true);
            }
          } else {
            btnSignUp.setEnabled(true);
          }
        } else if (userJoinStatus == ChampionshipsConstant.USER_JOIN_STATUS_YES) { // 用户已报名
          btnSignUp.setText(R.string.lbl_match_cancel_registration);
          btnSignUp.setBackgroundResource(R.drawable.selector_match_btn_sign_up2);
          btnSignUp.setTextColor(Color.parseColor("#FFFFFF"));
        }
        btnCheckRank.setEnabled(false);
        btnCheckRank.setVisibility(View.GONE);
        btnEnterMatch.setEnabled(false);
        btnEnterMatch.setVisibility(View.GONE);
      } else if (matchStatus == ChampionshipsConstant.MATCH_STATUS_PLAYING) {
        btnCheckSignUp.setEnabled(false);
        btnCheckSignUp.setVisibility(View.GONE);
        if (userJoinStatus == ChampionshipsConstant.USER_JOIN_STATUS_NO) { // 用户未报名
          btnSignUp.setEnabled(true);
          btnSignUp.setVisibility(View.VISIBLE);
          btnSignUp.setText(R.string.lbl_match_sign_now);
          btnSignUp.setBackgroundResource(R.drawable.selector_match_btn_sign_up);
          btnSignUp.setTextColor(Color.parseColor("#25282C"));
          btnEnterMatch.setEnabled(false);
          btnEnterMatch.setVisibility(View.GONE);
          //赛事报名条件 0：开放报名，1：关闭报名，2：允许会员报名
          if (model.getJoinStatus() == ChampionshipsConstant.JOIN_STATUS_CLOSE) {
            btnSignUp.setEnabled(false);
          } else if (model.getJoinStatus() == ChampionshipsConstant.JOIN_STATUS_MEMBER) {
            if (isMembers == 1) {
              btnSignUp.setEnabled(false);
            } else {
              btnSignUp.setEnabled(true);
            }
          } else {
            btnSignUp.setEnabled(true);
          }
        } else if (userJoinStatus == ChampionshipsConstant.USER_JOIN_STATUS_YES) { // 用户已报名
          btnSignUp.setEnabled(false);
          btnSignUp.setVisibility(View.GONE);
          btnEnterMatch.setEnabled(true);
          btnEnterMatch.setVisibility(View.VISIBLE);
        }
        btnCheckRank.setEnabled(true);
        btnCheckRank.setVisibility(View.VISIBLE);
      } else if (matchStatus == ChampionshipsConstant.MATCH_STATUS_FINISH) {
        btnCheckSignUp.setEnabled(false);
        btnCheckSignUp.setVisibility(View.GONE);
        btnSignUp.setEnabled(false);
        btnSignUp.setVisibility(View.GONE);
        btnCheckRank.setEnabled(true);
        btnCheckRank.setVisibility(View.VISIBLE);
        btnEnterMatch.setEnabled(false);
        btnEnterMatch.setVisibility(View.GONE);
      }
    }

    matchDetailInfoList.clear();
    matchDetailInfoList.addAll(model.getMatchDetailInfoList());
    rvMatchOptionList.forceLayout();
    rvMatchOptionList.setAdapter(mMatchOptionAdapter);
    mMatchOptionAdapter.notifyDataSetChanged(matchDetailInfoList, isQuartets, joinMatchUnits);
  }

  private void showStages(List<MatchStage> matchStages){
    if(matchStages != null && matchStages.size() > 0) {
      this.matchStages.clear();
      this.matchStages.addAll(matchStages);
      mStageItemAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onClick(View v) {

  }

}
