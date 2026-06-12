package com.cloud.runball.module.match_football_association;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecycleViewDivider;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.constant.ChampionshipsConstant;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.module.match.MatchDetailActivity;
import com.cloud.runball.module.match.MatchMainActivity2;
import com.cloud.runball.module.match.MatchRankActivity;
import com.cloud.runball.module.match.RankMatchMainActivity;
import com.cloud.runball.module.match_football_association.adapter.MatchMenuAdapter;
import com.cloud.runball.module.match_football_association.dialog.AssociationMatchTipDialog;
import com.cloud.runball.module.match_football_association.dialog.AssociationSelectTeamDialog;
import com.cloud.runball.module.match_football_association.entity.MatchMenu;
import com.cloud.runball.module.match_football_association.entity.MatchStage;
import com.cloud.runball.module.match_football_association.entity.model.MatchDetailModel;
import com.cloud.runball.module.match_football_association.entity.model.MatchMenuListModel;
import com.cloud.runball.module.race.CreateRankMatchAddActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.FragmentAssociationMatchMenuSubBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: MatchSubFragment
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/1/30 15:46
 * @UpdateUser: zhd
 * @UpdateDate: 2021/1/30 15:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AssociationMatchMenuSubFragment extends BaseFragment {

  private FragmentAssociationMatchMenuSubBinding binding;
  XRecyclerView rvMatch;

  static final String KEY_MATCH_STATUS = "match_status";

  private int matchStatus;
  private MatchMenuAdapter mMatchAdapter;
  private long currentTimeMillis = 0;

  // 赛事状态 - 未开始
  public static final int MATCH_STATUS_NOT_STARTED = 1;
  // 赛事状态 - 进行中
  public static final int MATCH_STATUS_STARTED = 2;
  // 赛事状态 - 已结束
  public static final int MATCH_STATUS_FINISH = 3;

  // 赛事状态 - 全部
  public static final int MATCH_STATUS_ALL = 5;

  // 赛事状态 - 近期
  public static final int MATCH_STATUS_NEAR = 4;

  // 赛事状态 - 往期
  public static final int MATCH_STATUS_PREVIOUS = 3;

  private final List<MatchMenu> matchMenuList = new ArrayList<>();

  public static AssociationMatchMenuSubFragment newInstance(int type) {
    AssociationMatchMenuSubFragment fragment = new AssociationMatchMenuSubFragment();
    Bundle args = new Bundle();
    args.putInt(KEY_MATCH_STATUS, type);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_association_match_menu_sub;
  }

  private String quartetsIcon;

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentAssociationMatchMenuSubBinding.bind(view);
    rvMatch = binding.rvMatch;
    if (getArguments() != null) {
      matchStatus = getArguments().getInt(KEY_MATCH_STATUS);
    }

    mMatchAdapter = new MatchMenuAdapter(getActivity(), matchMenuList);
    mMatchAdapter.setOnItemClickListener((view1, type, data) -> {
      int userJoinStatus = data.getUserJoinStatus().getIsJoin();
      boolean isQuartets = data.getIsQuartets() == 1;
      String sysMatchId = data.getSysMatchId();
      String sysSysMatchId = data.getSysSysMatchId();
      if(type == MatchMenuAdapter.OnItemClickListener.BUTTON) {
        if(data.getMatchStatus() == 3) {
          //已结束
          if (isQuartets) {
            AssociationRankingActivity.starAction(
                this.getContext(), sysSysMatchId, sysMatchId, isQuartets, data.getMatchTitle(),
              data.getMatchStatus(), Long.parseLong(data.getMatchStartTime()), Long.parseLong(data.getMatchStopTime()), quartetsIcon
            );
          } else {
            // todo is_exponent 是否显示摇跑指数，看后续是否直接在榜单接口直接传过来，而不是这边传给后端再获取回来
            startRankActivity(data.getMatchTitle(), sysMatchId, data.getIsGroup(), 0);
          }
//          startRankActivity(data.getMatchTitle(), sysMatchId, data.getIsGroup(), data.getIsExponent());
        } else if(data.getMatchStatus() == 2) {
          //比赛中
          if(data.getUserJoinStatus() != null) {
            if(data.getUserJoinStatus().getIsJoin() == 0) {
              if (isQuartets) {
                AssociationMatchTipDialog dialog = new AssociationMatchTipDialog(AssociationMatchMenuSubFragment.this.getContext());
                dialog.setCallback(data.getTeamName(), new AssociationMatchTipDialog.Callback() {
                  @Override
                  public void onSubmit(Dialog dialog) {
                    dialog.dismiss();
                    joinRankMatch(sysSysMatchId, sysMatchId, data.getIsGroup(), isQuartets);
                  }
                  @Override
                  public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                  }
                });
              } else {
                joinRankMatch(sysSysMatchId, sysMatchId, data.getIsGroup(), isQuartets);
              }
            } else if(data.getUserJoinStatus().getIsJoin() == 1) {
              //已报名-->显示立即比赛
              if(data.getViewType() == ChampionshipsConstant.TYPE_NO) {
                Toast.makeText(App.self().getApplicationContext(), R.string.lbl_match_no_match, Toast.LENGTH_LONG).show();
              } else if(data.getViewType() == ChampionshipsConstant.TYPE_RANKING) {
                if (data.getIsQuartets() == 1) {
                  HashMap<String, Object> map = new HashMap<>(2);
                  map.put("sys_match_id", sysMatchId);
                  map.put("type", matchStatus);
                  RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
                  WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
                  Observable<MatchDetailModel> observable = apiServer.getMatchDetailV2(requestBody);
                  disposable.add(
                      observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                          .subscribeWith(new WristBallObserver<MatchDetailModel>() {
                               @Override
                               public void onSuccess(MatchDetailModel model) {
                                 quartetsIcon = model.getQuartetsIcon();
                                 for (MatchStage stage : model.getMatchStageList()) {
                                   if (stage.getMatchStageStatus() == ChampionshipsConstant.MATCH_STATUS_PLAYING) {
                                     SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                                     try {
                                       Date startDate = format.parse(stage.getStartTime());
                                       Date stopDate = format.parse(stage.getStopTime());
                                       AssociationMatchActivity.startAction(
                                           AssociationMatchMenuSubFragment.this.getContext(), model.getMatchTitle(), sysMatchId, sysSysMatchId, stage.getMatchStageId(), isQuartets ? 1 : 0,
                                           startDate.getTime() / 1000, stopDate.getTime() / 1000, data.getTeamName()
                                       );
                                     } catch (ParseException e) {
                                       e.printStackTrace();
                                     }
                                     break;
                                   }
                                 }
                               }
                               @Override
                               public void onError(int code, String msg) {
                                 AppLogger.d("--------requestRankMatchDetail-------code-" + msg);
                               }
                             }
                          )
                  );
//                  AssociationMatchActivity.startAction(
//                      this.getContext(), data.getMatchTitle(), sysMatchId, sysSysMatchId, data.getIsQuartets(),
//                      Long.parseLong(data.getMatchStartTime()), Long.parseLong(data.getMatchStopTime())
//                  );
                } else {
                  startRankMatchMainActivity(data.getMatchTitle(), sysMatchId, data.getUserJoinStatus().getUserGroupId(), data.getMatchsStageId());
                }
              } else if(data.getViewType() == ChampionshipsConstant.TYPE_YJY) {
                if(System.currentTimeMillis() - currentTimeMillis >= 1000) {
                  currentTimeMillis = System.currentTimeMillis();
                  startListMatchActivity(sysMatchId, data.getMatchsStageId(), data.getUserJoinStatus().getUserGroupId(), data.getMatchTitle());
                }
              }
            }
          }
        } else {
          if(userJoinStatus == 0) {
            //未报名
            if (isQuartets) {
              AssociationMatchTipDialog dialog = new AssociationMatchTipDialog(AssociationMatchMenuSubFragment.this.getContext());
              dialog.setCallback(data.getTeamName(), new AssociationMatchTipDialog.Callback() {
                @Override
                public void onSubmit(Dialog dialog) {
                  dialog.dismiss();
                  joinRankMatch(sysSysMatchId, sysMatchId, data.getIsGroup(), isQuartets);
                }
                @Override
                public void onCancel(Dialog dialog) {
                  dialog.dismiss();
                }
              });
            } else {
              joinRankMatch(sysSysMatchId, sysMatchId, data.getIsGroup(), isQuartets);
            }
          } else if(userJoinStatus == 1) {
//            //已经报名
//            //根据所处赛段进去进入主页面赛段还是摇跑赛赛段
//            AssociationCommonDialog dialog = new AssociationCommonDialog(AssociationMatchMenuSubFragment.this.getContext());
//            dialog.setContent(getString(R.string.tip), "是否取消报名");
//            dialog.addBtn("取消", false, commonDialog -> {
//              commonDialog.dismiss();
//            });
//            dialog.addBtn("确定", true, commonDialog -> {
//              matchUserSignOutV2(sysSysMatchId, sysMatchId);
//              commonDialog.dismiss();
//            });

//              if(mDetailModel != null) {
//                if(mDetailModel.getViewType() == 1) {
//                  startRankMatchMainActivity(matchTitle, mDetailModel.getSysMatchId(), mDetailModel.getUserJoinStatus().getUserGroupId(), mDetailModel.getMatchStageId());
//                }else if(mDetailModel.getViewType() == 2) {
//                  startListMatchActivity(mDetailModel.getSysMatchId(), mDetailModel.getMatchStageId(), mDetailModel.getUserJoinStatus().getUserGroupId(), mDetailModel.getMatchTitle());
//                }else if(mDetailModel.getViewType() == 0) {
//                  Toast.makeText(App.self().getApplicationContext(),R.string.lbl_match_no_match,Toast.LENGTH_LONG).show();
//                }
//              }
          }
        }
      } else {
        if (data.getIsQuartets() == 1) {
          requestMatchDetail(data.getSysMatchId(), data.getMatchStatus());
        } else {
          requestOldMatchDetail(data.getSysMatchId());
        }
      }
    });

    //初始化我的数据信息
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    rvMatch.setLayoutManager(layoutManager);
    rvMatch.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL));
    rvMatch.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    rvMatch.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    rvMatch.setLoadingMoreEnabled(false);
    rvMatch.setArrowImageView(R.drawable.iconfont_downgrey);
    rvMatch.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    rvMatch.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        requestMatchList(matchStatus);
      }

      @Override
      public void onLoadMore() {

      }
    });
    rvMatch.setAdapter(mMatchAdapter);
    mMatchAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onLazyLoad() {
//    requestMatchList(matchStatus);
//    updateMembers();
  }

  @Override
  protected void onFragmentShow() {
    super.onFragmentShow();
    requestMatchList(matchStatus);
    updateMembers();
  }

  private void updateMembers(){
    if(mMatchAdapter!=null){
      if(AppDataManager.getInstance().getUserInfoModel()!=null){
        if(AppDataManager.getInstance().getUserInfoModel().getUser_info()!=null){
          int is_members=AppDataManager.getInstance().getUserInfoModel().getUser_info().getIs_members();
          mMatchAdapter.updateMembers(is_members);
          mMatchAdapter.notifyDataSetChanged();
        }
      }
    }
  }

  // 取消报名
  private void matchUserSignOutV2(String sys_sys_match_id, String sys_match_id) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_sys_match_id", sys_sys_match_id);
    map.put("sys_match_id", sys_match_id);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
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
                       requestMatchList(matchStatus);
                     }else if(code == 2) {
                       String msg=jsonObject.optString("msg");
                       Toast.makeText(AssociationMatchMenuSubFragment.this.getContext(), msg,Toast.LENGTH_SHORT).show();
                     }else{
                       Toast.makeText(AssociationMatchMenuSubFragment.this.getContext(), jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                     }
                   }catch (Exception ex) {
                     ex.printStackTrace();
                   }
                 }
                 @Override
                 public void onError(int code, String msg) {
                   if(code == 2) {
                     Toast.makeText(AssociationMatchMenuSubFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
                   }
                   AppLogger.d("---matchUserSignOutV2-----"+msg);
                 }
               }
            )
    );
  }

  private void joinRankMatch(String sys_sys_match_id, String sys_match_id, int is_group, boolean isQuartets){
    if (isQuartets) {
      getSignUpTeamTag(sys_sys_match_id, sys_match_id, isQuartets);
    } else {
      //未报名
      if(is_group == 0) {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.startsWith("zh")) {
          language="zh-CN";
        }else{
          language="en-US";
        }
        requestRankMatchSign(sys_sys_match_id, sys_match_id, language);
      }else{
        startRankCreateMatchActivity(sys_sys_match_id,sys_match_id,is_group);
      }
    }
  }

  private void getSignUpTeamTag(String sys_sys_match_id, String sys_match_id, boolean isQuartets) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_match_id", sys_sys_match_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
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
                     if (data != null) {
                       for (int i = 0; i < data.length(); i++) {
                         teamData.add(data.optString(i));
                       }
                     }

                     AssociationSelectTeamDialog dialog = new AssociationSelectTeamDialog(AssociationMatchMenuSubFragment.this.getContext());
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

                       AssociationCommonDialog dialog2 = new AssociationCommonDialog(AssociationMatchMenuSubFragment.this.getContext());
                       dialog2.setContent(getString(R.string.tip), "是否确认选择加人" + team + "?");
                       dialog2.addBtn(getString(R.string.btn_cancel), false, commonDialog -> {
                         commonDialog.dismiss();
                       });
                       String finalLanguage = language;
                       dialog2.addBtn(getString(R.string.btn_ok), true, commonDialog -> {
                         commonDialog.dismiss();
                         requestRankMatchSignV2(sys_sys_match_id, sys_match_id, finalLanguage, team, isQuartets);
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
  private void requestRankMatchSignV2(String sys_sys_match_id, String sys_match_id, String language, String teamTag, boolean isQuartets){
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_sys_match_id", sys_sys_match_id);
    map.put("sys_match_id", sys_match_id);
    map.put("language", language);
    map.put("is_group", 0);
    map.put("is_quartets", isQuartets);
    map.put("team_tag", teamTag);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
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
                       Toast.makeText(AssociationMatchMenuSubFragment.this.getContext(),jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();

                       requestMatchList(matchStatus);
                     }else if(code==2) {
                       String msg=jsonObject.optString("msg");
                       Toast.makeText(AssociationMatchMenuSubFragment.this.getContext(),msg,Toast.LENGTH_SHORT).show();
                     }else{
                       Toast.makeText(AssociationMatchMenuSubFragment.this.getContext(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                     }
                   }catch (Exception ex) {
                     ex.printStackTrace();
                   }
                 }
                 @Override
                 public void onError(int code, String msg) {
                   if(code == 2) {
                     Toast.makeText(AssociationMatchMenuSubFragment.this.getContext(),msg,Toast.LENGTH_SHORT).show();
                   }
                   AppLogger.d("---requestRankMatchSign-----"+msg);
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
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(4);
    map.put("sys_sys_match_id", sys_sys_match_id);
    map.put("sys_match_id", sys_match_id);
    map.put("language", language);
    map.put("is_group", 0);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.matchSign(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try{
              JSONObject jsonObject=new JSONObject(responseBody.string());
              int code=jsonObject.optInt("code");
              Toast.makeText(getActivity(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
              //刷新下界面
              requestMatchList(matchStatus);
            }catch (Exception ex) {
              ex.printStackTrace();
            }
          }
          @Override
          public void onError(int code, String msg) {
            AppLogger.d("---requestRankMatchSign-----"+msg);
          }
        })
    );
  }

  /**
   * 打开排行榜
   * @param title
   * @param sys_match_id
   * @param is_group
   */
  private void startRankActivity(String title, String sys_match_id, int is_group, int is_exponent) {
    Intent it = new Intent(getActivity(), MatchRankActivity.class);
    it.putExtra("title", title);
    it.putExtra("sys_match_id", sys_match_id);
    it.putExtra("is_group", is_group);
    it.putExtra("is_exponent", is_exponent);
    startActivity(it);
  }

  /**
   * 锦标赛
   */
  private void startRankMatchMainActivity(String title, String sys_match_id, String user_group_id, String matchs_stage_id) {
    Intent it = new Intent(getActivity(), RankMatchMainActivity.class);
    it.putExtra("sys_match_id", sys_match_id);
    it.putExtra("user_group_id", user_group_id);
    it.putExtra("matchs_stage_id", matchs_stage_id);
    it.putExtra("title", title);
    startActivity(it);
  }

  /**
   * 报名
   * @param sys_sys_match_id
   * @param sys_match_id
   * @param is_group
   */
  private void startRankCreateMatchActivity(String sys_sys_match_id, String sys_match_id, int is_group){
    Intent it=new Intent(getActivity(), CreateRankMatchAddActivity.class);
    it.putExtra("sys_sys_match_id", sys_sys_match_id);
    it.putExtra("sys_match_id", sys_match_id);
    it.putExtra("is_group", is_group);
    //这里启动还有返回
    startActivityLaunch.launch(it);
  }

  ActivityResultLauncher<Intent> startActivityLaunch = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode = result.getResultCode();
        if(resultCode == CreateRankMatchAddActivity.REPORT_OK){
          AppLogger.d("--------报名成功，需要刷新列表----------");
          requestMatchList(matchStatus);
        }
      }
  );


  ActivityResultLauncher<Intent> startMatchDetailLaunch = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode = result.getResultCode();
        if(resultCode == MatchDetailActivity.REPORT_OK){
          AppLogger.d("--------报名成功，需要刷新列表----------");
          requestMatchList(matchStatus);
        }
      }
  );

  private void requestOldMatchDetail(String sysMatchId) {
    Intent intent = new Intent(getContext(), MatchDetailActivity.class);
    intent.putExtra("sys_match_id", sysMatchId);
    startMatchDetailLaunch.launch(intent);
  }

  private void requestMatchDetail(String sysMatchId, int matchStatus) {
    Intent intent = new Intent(getContext(), AssociationMatchDetailActivity.class);
    intent.putExtra(AssociationMatchDetailActivity.KEY_MATCH_STATUS, matchStatus);
    intent.putExtra(AssociationMatchDetailActivity.KEY_MATCH_ID, sysMatchId);
    startMatchDetailLaunch.launch(intent);
  }


  /**
   * 进入摇跑赛页面
   * @param sys_match_id      赛事ID
   * @param matchs_stage_id   赛段ID
   * @param user_group_id     所属队伍ID
   */
  private void startListMatchActivity(String sys_match_id,String matchs_stage_id,String user_group_id,String match_title){
    Intent it=new Intent(getActivity(), MatchMainActivity2.class);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("matchs_stage_id",matchs_stage_id);
    it.putExtra("user_group_id",user_group_id);
    it.putExtra("match_title",match_title);
    startActivity(it);
  }

  /**
   * 获取赛事列表
   * @param matchStatus
   */
  private void requestMatchList(int matchStatus){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("type", matchStatus);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<MatchMenuListModel> observable = apiServer.getMatchListV2(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MatchMenuListModel>() {
          @Override
          public void onSuccess(MatchMenuListModel model) {
            if(mMatchAdapter!=null && model!=null) {
              matchMenuList.clear();
              matchMenuList.addAll(model.getMatchMenuList());
              if(rvMatch!=null) {
                mMatchAdapter.notifyDataSetChanged();
                rvMatch.refreshComplete();
              }
            }
            updateMembers();
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("--------requestMatchList-------code-"+msg);
          }
        })
    );
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if(rvMatch!=null){
      rvMatch.destroy();
      rvMatch = null;
    }
  }

}
