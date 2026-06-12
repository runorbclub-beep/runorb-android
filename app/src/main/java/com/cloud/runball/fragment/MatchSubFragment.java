package com.cloud.runball.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecycleViewDivider;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.module.race.CreateRankMatchAddActivity;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.module.match.MatchDetailActivity;
import com.cloud.runball.module.match.MatchMainActivity2;
import com.cloud.runball.module.match.MatchRankActivity;
import com.cloud.runball.module.match.RankMatchMainActivity;
import com.cloud.runball.module.match.adapter.MatchAdapter;
import com.cloud.runball.model.RankMatchDataModel;
import com.cloud.runball.model.RankMatchDataRespModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.CheckHelper;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
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
public class MatchSubFragment extends Fragment implements MatchAdapter.OnItemClickListener{

  static final String MATCH_ID="match_event_id";
  static final String INDEX_ID="index_id";
  private String match_event_id;
  private int mIndex=-1;
  private boolean isFirstLoad = true;
  private XRecyclerView rvMatch;
  private MatchAdapter mMatchAdapter;
  long currentTimeMillis=0;

  List<RankMatchDataModel> list=new ArrayList<>();

  private CompositeDisposable disposable = new CompositeDisposable();

  public static MatchSubFragment newInstance(String match_event_id,int index) {
    MatchSubFragment fragment = new MatchSubFragment();
    Bundle args = new Bundle();
    args.putString(MATCH_ID, match_event_id);
    args.putInt(INDEX_ID, index);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      match_event_id = getArguments().getString(MATCH_ID);
      mIndex = getArguments().getInt(INDEX_ID);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.layout_match, container, false);

    rvMatch = root.findViewById(R.id.rvMatch);

    mMatchAdapter = new MatchAdapter(getActivity(), list);
    mMatchAdapter.setOnItemClickListener(this);

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
        requestMatchList(match_event_id);
      }

      @Override
      public void onLoadMore() {

      }
    });
    rvMatch.setAdapter(mMatchAdapter);
    mMatchAdapter.notifyDataSetChanged();
    return root;
  }


  @Override
  public void onResume() {
    super.onResume();
    if (isFirstLoad) {
      isFirstLoad = false;
      onLazyLoadData();
    }

    //这里更新不了
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


  private void onLazyLoadData(){
    requestMatchList(match_event_id);
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if(rvMatch!=null){
      rvMatch.destroy();
      rvMatch = null;
    }
    isFirstLoad=true;
    mMatchAdapter = null;
    list.clear();

    disposable.dispose();
  }

  @Override
  public void onItemClick(View view, int type, RankMatchDataModel data) {
    if(CheckHelper.onCheckFunc() == CheckHelper.PHONE) {
      goToLogin();
      return;
    } else if(CheckHelper.onCheckFunc() == CheckHelper.NONE) {
      Toast.makeText(App.self().getApplicationContext(),R.string.lbl_pk_net_error,Toast.LENGTH_LONG).show();
      return;
    }

    if(type == MatchAdapter.OnItemClickListener.BUTTON){
      if(data.getMatch_status() == 3){
        //已结束
        startRankActivity(data.getMatch_title(), data.getSys_match_id(), data.getIs_group(), data.getIs_exponent());
      } else if(data.getMatch_status() == 2){
        //比赛中
        if(data.getUser_join_status() != null){
          if(data.getUser_join_status().getIs_join() == 0){
            joinRankMatch(data.getSys_sys_match_id(), data.getSys_match_id(), data.getIs_group());
          } else if(data.getUser_join_status().getIs_join() == 1){
            //已报名-->显示立即比赛
            if(data.getView_type() == VIEW_TYPE_0){
              Toast.makeText(App.self().getApplicationContext(), R.string.lbl_match_no_match, Toast.LENGTH_LONG).show();
            } else if(data.getView_type() == VIEW_TYPE_1){
              startRankMatchMainActivity(data.getMatch_title(), data.getSys_match_id(), data.getUser_join_status().getUser_group_id(), data.getMatchs_stage_id());
            } else if(data.getView_type() == VIEW_TYPE_2){
              if(System.currentTimeMillis() - currentTimeMillis >= 1000){
                currentTimeMillis = System.currentTimeMillis();
                startListMatchActivity(data.getSys_match_id(), data.getMatchs_stage_id(), data.getUser_join_status().getUser_group_id(), data.getMatch_title());
              }
            }
          }
        }
      }
    } else {
      requestMatchDetail(data.getSys_match_id());
    }
  }

  /**
   * 打开用户手机号登录页面
   */
  private void goToLogin(){
    Intent it=new Intent(getActivity(), LoginOtherActivity.class);
    startActivity(it);
  }


  static final int VIEW_TYPE_0=0;
  static final int VIEW_TYPE_1=1;
  static final int VIEW_TYPE_2=2;


  private void joinRankMatch(String sys_sys_match_id,String sys_match_id,int is_group){
    //未报名
    if(is_group==0){
      Locale locale = getResources().getConfiguration().locale;
      String language = locale.getLanguage();
      if(language.startsWith("zh")){
        language="zh-CN";
      }else{
        language="en-US";
      }
      requestRankMatchSign(sys_sys_match_id,sys_match_id,language);
    }else{
      startRankCreateMatchActivity(sys_sys_match_id,sys_match_id,is_group);
    }
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
              //showDialog(getString(R.string.tip), jsonObject.optString("msg"));
              //刷新下界面
              requestMatchList(match_event_id);
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

  public void showDialog(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(R.string.btn_confirm, (dialogInterface, i) -> dialogInterface.dismiss());
    builder.show();
  }

  /**
   * 打开排行榜
   * @param title
   * @param sys_match_id
   * @param is_group
   */
  private void startRankActivity(String title,String sys_match_id,int is_group,int is_exponent){
    Intent it = new Intent(getActivity(), MatchRankActivity.class);
    it.putExtra("title",title);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("is_group",is_group);
    it.putExtra("is_exponent",is_exponent);
    startActivity(it);
  }

  /**
   * 锦标赛
   */
  private void startRankMatchMainActivity(String title,String sys_match_id,String user_group_id,String matchs_stage_id){
    Intent it = new Intent(getActivity(), RankMatchMainActivity.class);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("user_group_id",user_group_id);
    it.putExtra("matchs_stage_id",matchs_stage_id);
    it.putExtra("title",title);
    startActivity(it);
  }

  /**
   * 报名
   * @param sys_sys_match_id
   * @param sys_match_id
   * @param is_group
   */
  private void startRankCreateMatchActivity(String sys_sys_match_id,String sys_match_id,int is_group){
    Intent it=new Intent(getActivity(), CreateRankMatchAddActivity.class);
    it.putExtra("sys_sys_match_id",sys_sys_match_id);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("is_group",is_group);
    //startActivity(it);
    //这里启动还有返回
    startActivityLaunch.launch(it);
  }

  ActivityResultLauncher<Intent> startActivityLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    int resultCode=result.getResultCode();
    if(resultCode==CreateRankMatchAddActivity.REPORT_OK){
      AppLogger.d("--------报名成功，需要刷新列表----------");
      onRefreshMatchList();
    }
  });


  ActivityResultLauncher<Intent> startMatchDetailLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    int resultCode=result.getResultCode();
    if(resultCode==MatchDetailActivity.REPORT_OK){
      AppLogger.d("--------报名成功，需要刷新列表----------");
      onRefreshMatchList();
    }
  });

  private void requestMatchDetail(String sys_match_id){
    Intent it= new Intent(getContext(), MatchDetailActivity.class);
    it.putExtra("sys_match_id",sys_match_id);
    startMatchDetailLaunch.launch(it);
    //startActivity(it);
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


  private void onRefreshMatchList(){
    if(!TextUtils.isEmpty(match_event_id)){
      requestMatchList(match_event_id);
    }
  }

  public void onRefreshMatchListWithNotData(){
    if(!TextUtils.isEmpty(match_event_id) && list.size()==0){
      requestMatchList(match_event_id);
    }
  }

  /**
   * 获取赛事列表
   * @param match_event_id
   */
  private void requestMatchList(String match_event_id){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("match_event_id", match_event_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<RankMatchDataRespModel> observable = apiServer.matchList(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<RankMatchDataRespModel>() {
          @Override
          public void onSuccess(RankMatchDataRespModel rankMatchDataRespModel) {
            if(mMatchAdapter!=null && rankMatchDataRespModel!=null){
              list.clear();
              list.addAll(rankMatchDataRespModel.getList());
              //mMatchAdapter.notifyDataSetChanged();
              if(rvMatch!=null){
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

}
