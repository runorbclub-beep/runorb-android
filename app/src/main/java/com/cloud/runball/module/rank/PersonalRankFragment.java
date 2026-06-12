package com.cloud.runball.module.rank;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.module.clan.ClanMemberFragment;
import com.cloud.runball.module.rank.adapter.ClanRankAdapter;
import com.cloud.runball.module.rank.adapter.RankingAdapter;
import com.cloud.runball.bean.MatchRankItem;
import com.cloud.runball.model.MatchRankDataModel;
import com.cloud.runball.module.social.MineHomepageActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentPersonalRankBinding;
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
public class PersonalRankFragment extends BaseFragment {
  private FragmentPersonalRankBinding binding;
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

  static final String TYPE = "type";
  static final String AGE_TYPE = "user_age_type";

  static final String USER_TYPE = "user_type";
  static final String ADDRESS = "address";

  private int mPage = 1;
  RankingAdapter mRankingAdapter;
  private final List<MatchRankItem> list = new ArrayList<>();
  private String mRanking_type;
  private String mUser_age_type = "";
  static final String exponent = "exponent";
  static final String marathon = "marathon";

  String user_type = "";
  String address = "";
  String sys_sex_id = "";

  private String parameter;
  private boolean isShowBottom;

  private PersonalRankFragment(String ranking_type){
    this.mRanking_type = ranking_type;
  }

  public static PersonalRankFragment newInstance(String ranking_type, String user_age_type, String user_type, String address, String sys_sex_id, String parameter, boolean isShowBottom) {
    PersonalRankFragment fragment = new PersonalRankFragment(ranking_type);
    Bundle args = new Bundle();
    args.putString(TYPE, ranking_type);
    args.putString(AGE_TYPE, user_age_type);

    args.putString(USER_TYPE, user_type);
    args.putString(ADDRESS, address);
    args.putString("sys_sex_id", sys_sex_id);

    args.putString("parameter", parameter);
    args.putBoolean("isShowBottom", isShowBottom);

    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_personal_rank;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentPersonalRankBinding.bind(view);
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
    // wire click listeners
    lyBottom.setOnClickListener(this::onClick);
    EventBus.getDefault().register(this);
    if (getArguments() != null) {
      mRanking_type = getArguments().getString(TYPE);
      mUser_age_type = getArguments().getString(AGE_TYPE);

      user_type = getArguments().getString(USER_TYPE);
      address = getArguments().getString(ADDRESS);
      sys_sex_id = getArguments().getString("sys_sex_id");

      parameter = getArguments().getString("parameter");
      isShowBottom = getArguments().getBoolean("isShowBottom");
    }
  }

  @Override
  protected void onLazyLoad() {
    initRanking();
  }

  public void onClick(View view) {
    if (view.getId() == R.id.lyBottom) {
      if (matchRankDataModel != null) {
        MineHomepageActivity.startAction(PersonalRankFragment.this.getContext(), matchRankDataModel.getMy_ranking_info().getUser_id());
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onServiceNoticeEvent(MessageEvent event) {
    switch (event.getEvetId()) {
      case MessageEvent.REFRESH: {
        requestRanking(true, 1);
      }
    }
  }

//  private String token = null;
//
//  @Override
//  protected void onFragmentShow() {
//    UserInfoModel userInfoModel = AppDataManager.getInstance().getUserInfoModel();
//    if (userInfoModel != null && userInfoModel.getUser_info() != null) {
//      String newToken = userInfoModel.getUser_info().getToken();
//      if (!TextUtils.isEmpty(newToken) && !newToken.equals(token)) {
//        token = newToken;
//        //请求榜单列表
//        requestRanking(mPage);
//      }
//    } else {
//      requestRanking(mPage);
//    }
//  }

  private void initRanking() {
    list.clear();
    mPage = 1;

    boolean isIndex=false;

    mRankingAdapter = new RankingAdapter(getContext(), list, isIndex, (itemData) -> {
      MineHomepageActivity.startAction(PersonalRankFragment.this.getContext(), itemData.getUser_id());
    });
    //初始化我的数据信息
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerview.setLayoutManager(layoutManager);
    recyclerview.addItemDecoration(new RecyclerViewDivider(0));
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        requestRanking(true, 1);
      }

      @Override
      public void onLoadMore() {
        requestRanking(false, mPage + 1);
      }
    });

    recyclerview.setAdapter(mRankingAdapter);

    requestRanking(true, 1);
  }

  private void requestRanking(boolean isRefresh, int pageIndex) {
    if (!isShowBottom && TextUtils.isEmpty(parameter)) {
      list.clear();
      RankingAdapter adapter = (RankingAdapter) recyclerview.getAdapter();
      if(adapter != null){
        adapter.notifyDataSetChanged();
      }
      showBottomData(null);
      return;
    }

    AppLogger.d("--榜单列表---" + mRanking_type + "; pageIndex = " + pageIndex);
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("page", pageIndex);
    map.put("limit", 10);
    map.put("ranking_type", mRanking_type);
    map.put("user_age_type", mUser_age_type);
    map.put("user_type", user_type);
    map.put("address", address);
    map.put("sys_sex_id", sys_sex_id);
    if (!TextUtils.isEmpty(parameter)) {
      map.put("title", parameter);
    }

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<MatchRankDataModel> observable = apiServer.matchRanking(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<MatchRankDataModel>() {
          @Override
          public void onSuccess(MatchRankDataModel matchRankDataModel) {
            PersonalRankFragment.this.matchRankDataModel = matchRankDataModel;
            if(matchRankDataModel != null) {
              List<MatchRankItem> listData = matchRankDataModel.getList();
              if (isRefresh) {
                list.clear();
                mPage = 1;
              } else {
                if (listData == null || listData.size() == 0) {
                  return;
                }
                mPage = pageIndex;
              }
              if (listData != null) {
                list.addAll(listData);
                mPage = pageIndex;
              }

              showBottomData(matchRankDataModel);
            }

            if (list.size() == 0) {
              ryEmpty.setVisibility(View.VISIBLE);
            } else {
              ryEmpty.setVisibility(View.GONE);
            }
            if(mRankingAdapter != null){
              mRankingAdapter.notifyDataSetChanged();
            }
            if(recyclerview != null){
              recyclerview.refreshComplete();
              recyclerview.loadMoreComplete();
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("--requestRanking---"+msg);
            if(mRankingAdapter!=null){
              mRankingAdapter.notifyDataSetChanged();
            }
            if(recyclerview!=null){
              recyclerview.refreshComplete();
              recyclerview.loadMoreComplete();
            }
          }
        })
    );
  }

  private MatchRankDataModel matchRankDataModel;

  private void showBottomData(MatchRankDataModel matchRankDataModel) {
    if (!isShowBottom) {
      lyBottom.setVisibility(View.GONE);
      return;
    }

    if (matchRankDataModel.getMy_ranking_info() != null) {
      lyBottom.setVisibility(View.VISIBLE);
      if(matchRankDataModel.getMy_ranking_info().getUser_img().startsWith("http")) {
        Picasso.with(PersonalRankFragment.this.getContext())
            .load(matchRankDataModel.getMy_ranking_info().getUser_img())
            .transform(new CircleTransform(PersonalRankFragment.this.getContext()))
            .placeholder(R.mipmap.default_head)
            .into(ivHead);
      } else {
        Picasso.with(PersonalRankFragment.this.getContext())
            .load(Constant.getBaseUrl() + "/" + matchRankDataModel.getMy_ranking_info().getUser_img())
            .transform(new CircleTransform(PersonalRankFragment.this.getContext()))
            .placeholder(R.mipmap.default_head)
            .into(ivHead);
      }
      tvRankNum.setText(String.valueOf(matchRankDataModel.getMy_ranking_info().getIndex()));
      Drawable drawableSex = null;
      if (SexConstant.SEX_MAN.equals(matchRankDataModel.getMy_ranking_info().getSys_sex_id())) {
        drawableSex = getResources().getDrawable(R.mipmap.ic_man);
        drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
      } else if (SexConstant.SEX_WOMEN.equals(matchRankDataModel.getMy_ranking_info().getSys_sex_id())) {
        drawableSex = getResources().getDrawable(R.mipmap.ic_women);
        drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
      }

      tvArea.setText(matchRankDataModel.getMy_ranking_info().getAddress());

      tvUserName.setCompoundDrawables(null, null, drawableSex, null);
      tvUserName.setText(matchRankDataModel.getMy_ranking_info().getUser_name());
      tvUserSpeed.setText(matchRankDataModel.getMy_ranking_info().getValue());
      if (!TextUtils.isEmpty(matchRankDataModel.getMy_ranking_info().getUnit())) {
        tvUnit.setVisibility(View.VISIBLE);
        tvUnit.setText("（" + matchRankDataModel.getMy_ranking_info().getUnit() + "）");
      } else {
        tvUnit.setVisibility(View.GONE);
      }
      tvRankTime.setText(matchRankDataModel.getMy_ranking_info().getTime());
    } else {
      lyBottom.setVisibility(View.GONE);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == 100) {

    }
  }

  @Override
  public void onDestroyView(){
    super.onDestroyView();
    AppLogger.d("----------MatchRankingFragment-----onDestroyView--------------mRanking_type="+mRanking_type);
    list.clear();
    EventBus.getDefault().unregister(this);
    if(mRankingAdapter != null){
      mRankingAdapter.notifyDataSetChanged();
      mRankingAdapter = null;
    }
    if(recyclerview != null){
      recyclerview.destroy();
      recyclerview = null;
    }
    mPage=1;
  }
}
