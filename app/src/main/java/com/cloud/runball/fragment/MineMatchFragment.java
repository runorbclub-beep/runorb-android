package com.cloud.runball.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.module.match.MatchDetailActivity;
import com.cloud.runball.module.match.MatchRankActivity;
import com.cloud.runball.module.match.adapter.MineMatchAdapter;
import com.cloud.runball.bean.MatchRankInfoData;
import com.cloud.runball.model.MatchRankInfoModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.CheckHelper;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.MineMatchLayoutBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: MineMatchFragment
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/12 10:17
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/12 10:17
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MineMatchFragment extends BaseFragment implements MineMatchAdapter.OnItemClickListener{

  private MineMatchLayoutBinding binding;
  XRecyclerView recyclerview;

  private MineMatchAdapter mMatchAdapter;

  private int mPage = 1;

  private final List<MatchRankInfoData> list = new ArrayList<>();

  @Override
  protected int setLayoutId() {
    return R.layout.mine_match_layout;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = MineMatchLayoutBinding.bind(view);
    recyclerview = binding.recyclerview;
  }

  @Override
  protected void onLazyLoad() {
    initMatch();
  }

  private void initMatch() {
    mMatchAdapter = new MineMatchAdapter(getActivity(), list);
    mMatchAdapter.setOnItemClickListener(this);
    //初始化我的数据信息
    recyclerview.addItemDecoration(new RecyclerViewDivider());
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        recyclerview.refreshComplete();
      }

      @Override
      public void onLoadMore() {
        requestMatchList(mPage + 1);
      }
    });

    recyclerview.setAdapter(mMatchAdapter);
    mMatchAdapter.notifyDataSetChanged();
    //请求个人/团队比赛列表
    requestMatchList(mPage);
  }

  private void requestMatchList(int page){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(2);
    map.put("page", page);
    map.put("limit", 10);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<MatchRankInfoModel> observable = apiServer.matchListWithGroups(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MatchRankInfoModel>() {
              @Override
              public void onSuccess(MatchRankInfoModel matchRankInfoModel) {
                AppLogger.d("--requestMatchList---" + matchRankInfoModel.toString());
                list.addAll(matchRankInfoModel.getList());
                if(mMatchAdapter != null){
                  mMatchAdapter.notifyDataSetChanged();
                }
                if(recyclerview != null){
                  recyclerview.loadMoreComplete();
                }
                mPage = page;
              }

              @Override
              public void onError(int code, String msg) {
                AppLogger.d("--requestMatchList---"+msg);
                if(mMatchAdapter!=null){
                  mMatchAdapter.notifyDataSetChanged();
                }
                if(recyclerview!=null){
                  recyclerview.loadMoreComplete();
                }
              }
            })
    );
  }


  @Override
  public void onDestroyView(){
    super.onDestroyView();
    if(recyclerview != null){
      recyclerview.destroy(); // this will totally release XR's memory
      recyclerview = null;
    }
  }

  @Override
  public void onItemClick(int goType, MatchRankInfoData data) {
    if(CheckHelper.onCheckFunc() == CheckHelper.PHONE) {
      goToLogin();
    }else{
      if(goType == MineMatchAdapter.OnItemClickListener.DETAIL) {
        requestMatchDetail(data.getSysMatchId());
      }else if(goType == MineMatchAdapter.OnItemClickListener.RANK) {
        startRankStageListActivity(data.getMatchTitle(), data.getSysMatchId(), data.getIsGroup());
      }else{
        if(data.getMatchStatus() == 3){
          //已结束
          startRankStageListActivity(data.getMatchTitle(), data.getSysMatchId(), data.getIsGroup());
        }else{
          //比赛中，先进去赛事详情页面
          requestMatchDetail(data.getSysMatchId());
        }
      }
    }
  }


  /**
   * 打开用户手机号登录页面
   */
  private void goToLogin(){
    Intent it=new Intent(getActivity(), LoginOtherActivity.class);
    startActivity(it);
  }


  private void startRankStageListActivity(String title,String sys_match_id,int is_group){
    Intent it = new Intent(getActivity(), MatchRankActivity.class);
    it.putExtra("title",title);
    it.putExtra("sys_match_id",sys_match_id);
    it.putExtra("is_group",is_group);
    it.putExtra("is_exponent",0);
    startActivity(it);
  }

  private void requestMatchDetail(String sys_match_id){
    Intent it= new Intent(getContext(), MatchDetailActivity.class);
    it.putExtra("sys_match_id",sys_match_id);
    startActivity(it);
  }

}
