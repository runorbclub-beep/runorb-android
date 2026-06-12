package com.cloud.runball.module.match_football_association;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.module.clan.ClanMemberFragment;
import com.cloud.runball.module.match_football_association.adapter.AssociationPersonalRankingAdapter;
import com.cloud.runball.module.match_football_association.entity.AssociationMatchRankInfo;
import com.cloud.runball.module.match_football_association.entity.AssociationMatchRankMyInfo;
import com.cloud.runball.module.match_football_association.entity.model.AssociationMatchRankModel;
import com.cloud.runball.module.social.MineHomepageActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentAssociationRankingPersonalSubBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: MatchRankingFragment
 * @Description: 榜单排行榜
 * @Author: zhd
 * @CreateDate: 2021/6/2 18:05
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/2 18:05
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AssociationRankingPersonalSubFragment extends BaseFragment {

  private FragmentAssociationRankingPersonalSubBinding binding;

  RelativeLayout ryEmpty;
  XRecyclerView recyclerview;
  TextView tvRankNum;
  ImageView ivHead;
  TextView tvUserName;
  TextView tvUserSpeed;
  TextView tvRankTime;
  LinearLayout lyBottom;
  TextView tvArea;
  TextView tvUnit;
  View vDivider;

  public static final String KEY_RANKING_TYPE = "ranking_type";
  public static final String KEY_SYS_SYS_MATCH_ID = "sys_sys_match_id";
  public static final String KEY_SYS_MATCH_ID = "sys_match_id";
  public static final String KEY_IS_SHOW = "is_show";


  int isShowUnit = 1;
  static final String exponent = "exponent";
  static final String marathon = "marathon";
  Object tag = new Object();

  AssociationPersonalRankingAdapter mRankingAdapter;
  private final List<AssociationMatchRankInfo> rankingList = new ArrayList<>();
  private int page = 1;
  private final int limit = 10;

  private String rankingType;
  private String sysSysMatchId;
  private String sysMatchId;
  private boolean isShow;

  public static AssociationRankingPersonalSubFragment newInstance(String rankingType, String sysSysMatchId, String sysMatchId, boolean isShow) {
    AssociationRankingPersonalSubFragment fragment = new AssociationRankingPersonalSubFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_RANKING_TYPE, rankingType);
    bundle.putString(KEY_SYS_SYS_MATCH_ID, sysSysMatchId);
    bundle.putString(KEY_SYS_MATCH_ID, sysMatchId);
    bundle.putBoolean(KEY_IS_SHOW, isShow);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_association_ranking_personal_sub;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentAssociationRankingPersonalSubBinding.bind(view);
    ryEmpty = binding.ryEmpty;
    recyclerview = binding.recyclerview;
    tvRankNum = binding.tvRankNum;
    ivHead = binding.ivHead;
    tvUserName = binding.tvUserName;
    tvUserSpeed = binding.tvUserSpeed;
    tvRankTime = binding.tvRankTime;
    lyBottom = binding.lyBottom;
    tvArea = binding.tvArea;
    tvUnit = binding.tvUnit;
    vDivider = binding.vDivider;
    Bundle bundle = getArguments();
    if (bundle != null) {
      rankingType = bundle.getString(KEY_RANKING_TYPE);
      sysSysMatchId = bundle.getString(KEY_SYS_SYS_MATCH_ID);
      sysMatchId = bundle.getString(KEY_SYS_MATCH_ID);
      isShow = bundle.getBoolean(KEY_IS_SHOW);
    }
    initRanking();
  }

  @Override
  protected void onLazyLoad() {
    //请求榜单列表
    loadRankingListData(true, 1);
  }

  private void initRanking() {
    rankingList.clear();
    page = 1;

    boolean isIndex = false;
    if(exponent.equalsIgnoreCase(rankingType)){
      isShowUnit = 0;
    }

    if(marathon.equalsIgnoreCase(rankingType)){
      isShowUnit = 0;
    }

    mRankingAdapter = new AssociationPersonalRankingAdapter(rankingList, isShowUnit, tag, isIndex, isShow);
    mRankingAdapter.setOnItemClickListener(new AssociationPersonalRankingAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(AssociationMatchRankInfo itemData) {
        MineHomepageActivity.startAction(AssociationRankingPersonalSubFragment.this.getContext(), itemData.getUserId());
      }
    });
    //初始化我的数据信息
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          Picasso.with(getContext()).resumeTag(tag);
        }else {
          Picasso.with(getContext()).pauseTag(tag);
        }
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

    recyclerview.setAdapter(mRankingAdapter);

  }

  private void loadRankingListData(boolean isClear, int page) {
    AppLogger.d("--榜单列表---" + rankingType + "; page = " + page);
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(5);
    map.put("ranking_type", rankingType);
    map.put("sys_match_id", sysMatchId);
    map.put("sys_sys_match_id", sysSysMatchId);
    map.put("page", page);
    map.put("limit", limit);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<AssociationMatchRankModel> observable = apiServer.getMatchPersonalLeaderboard(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<AssociationMatchRankModel>() {
              @Override
              public void onSuccess(AssociationMatchRankModel model) {
                if(recyclerview != null){
                  recyclerview.refreshComplete();
                  recyclerview.loadMoreComplete();
                }
                if (model == null) {
                  ryEmpty.setVisibility(View.VISIBLE);
                  lyBottom.setVisibility(View.GONE);
                  rankingList.clear();
                  if(mRankingAdapter != null){
                    mRankingAdapter.notifyDataSetChanged();
                  }
                  return;
                }


                if (model.getRankList().size() > 0) {
                  if (isClear) {
                    rankingList.clear();
                  }
                  rankingList.addAll(model.getRankList());
                  AssociationRankingPersonalSubFragment.this.page++;
                  ryEmpty.setVisibility(View.GONE);
                } else {
                  if (rankingList.size() == 0) {
                    ryEmpty.setVisibility(View.VISIBLE);
                  } else {
                    ryEmpty.setVisibility(View.GONE);
                  }
                }
                AssociationMatchRankMyInfo myInfo = model.getMyRankingInfo();
                if (myInfo != null) {
                  lyBottom.setVisibility(View.VISIBLE);
                  if(myInfo.getUserImg().startsWith("http")) {
                    Picasso.with(AssociationRankingPersonalSubFragment.this.getContext())
                        .load(myInfo.getUserImg())
                        .transform(new CircleTransform(AssociationRankingPersonalSubFragment.this.getContext()))
                        .tag(tag)
                        .placeholder(R.mipmap.default_head)
                        .into(ivHead);
                  } else {
                    Picasso.with(AssociationRankingPersonalSubFragment.this.getContext())
                        .load(Constant.getBaseUrl() + "/" + myInfo.getUserImg())
                        .transform(new CircleTransform(AssociationRankingPersonalSubFragment.this.getContext()))
                        .tag(tag)
                        .placeholder(R.mipmap.default_head)
                        .into(ivHead);
                  }

                  Drawable drawableSex = null;
                  if (SexConstant.SEX_MAN.equals(myInfo.getSysSexId())) {
                    drawableSex = getResources().getDrawable(R.mipmap.ic_man);
                    drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
                  } else if (SexConstant.SEX_WOMEN.equals(myInfo.getSysSexId())) {
                    drawableSex = getResources().getDrawable(R.mipmap.ic_women);
                    drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
                  }

                  tvArea.setText(myInfo.getAddress());

                  if (isShow) {
                    if ("0".equals(myInfo.getValue()) || "00:00:00".equals(myInfo.getValue())) {
                      tvRankNum.setText("/");
                      tvUserSpeed.setVisibility(View.INVISIBLE);
                    } else {
                      tvRankNum.setText(String.valueOf(myInfo.getIndex()));
                      if (TextUtils.isEmpty(myInfo.getUnit())) {
                        tvUserSpeed.setText(myInfo.getValue());
                        tvUnit.setVisibility(View.GONE);
                      } else {
                        tvUserSpeed.setText(myInfo.getValue());
                        tvUnit.setText("（" + model.getMyRankingInfo().getUnit() + "）");
                        tvUnit.setVisibility(View.VISIBLE);
                      }
                      tvUserSpeed.setVisibility(View.VISIBLE);
                    }
                  } else {
                    tvRankNum.setText("/");
                    tvUserSpeed.setVisibility(View.GONE);
                  }

                  tvUserName.setCompoundDrawables(null, null, drawableSex, null);
                  tvUserName.setText(myInfo.getUserName());

                  if (TextUtils.isEmpty(myInfo.getTime())) {
                    vDivider.setVisibility(View.GONE);
                  } else {
                    vDivider.setVisibility(View.VISIBLE);
                    tvRankTime.setText(myInfo.getTime());
                  }
                } else {
                  lyBottom.setVisibility(View.GONE);
                }

                if(mRankingAdapter != null){
                  mRankingAdapter.notifyDataSetChanged();
                }

              }

              @Override
              public void onError(int code, String msg) {
                AppLogger.d("--requestRanking---" + msg);
                if(mRankingAdapter != null){
                  mRankingAdapter.notifyDataSetChanged();
                }
                if(recyclerview != null){
                  recyclerview.refreshComplete();
                  recyclerview.loadMoreComplete();
                }
              }
            })
    );
  }

  @Override
  public void onDestroyView(){
    super.onDestroyView();
    AppLogger.d("----------MatchRankingFragment-----onDestroyView--------------mRanking_type="+ rankingType);
    rankingList.clear();
    if(mRankingAdapter != null){
      mRankingAdapter.notifyDataSetChanged();
      mRankingAdapter = null;
    }
    if(recyclerview != null){
      recyclerview.destroy();
      recyclerview = null;
    }
    page = 1;
    Picasso.with(getContext()).pauseTag(tag);
  }
}
