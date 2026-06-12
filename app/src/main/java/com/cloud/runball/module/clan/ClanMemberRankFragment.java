package com.cloud.runball.module.clan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.model.ClanMemberRankModel;
import com.cloud.runball.module.clan.adapter.ClanMemberScoreAdapter;
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

import com.cloud.runball.databinding.FragmentClanMemberRankBinding;
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
public class ClanMemberRankFragment extends BaseFragment {

  private FragmentClanMemberRankBinding binding;

  View layTop;
  TextView tvScore;
  RelativeLayout ryEmpty;
  XRecyclerView recyclerview;
  LinearLayout lyBottom;
  TextView tvRankNum;
  ImageView ivHead;
  TextView tvName;
  TextView tvValue;
  TextView tvTime;
  View vDivider;
  TextView tvArea;
  TextView tvUnit;

  private static final String KEY_TYPE = "type";

  private int page = 1;
  private final List<ClanMemberRankModel.ClanMemberScore> list = new ArrayList<>();
  private int type;
  private String clanId;

  private final WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  public static ClanMemberRankFragment newInstance(String clanId, int type) {
    ClanMemberRankFragment fragment = new ClanMemberRankFragment();
    Bundle args = new Bundle();
    args.putInt(KEY_TYPE, type);
    args.putString("clanId", clanId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_clan_member_rank;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentClanMemberRankBinding.bind(view);
    layTop = binding.layTop;
    tvScore = binding.tvScore;
    ryEmpty = binding.ryEmpty;
    recyclerview = binding.recyclerview;
    lyBottom = binding.lyBottom;
    tvRankNum = binding.tvRankNum;
    ivHead = binding.ivHead;
    tvName = binding.tvName;
    tvValue = binding.tvValue;
    tvTime = binding.tvTime;
    vDivider = binding.vDivider;
    tvArea = binding.tvArea;
    tvUnit = binding.tvUnit;
    if (getArguments() != null) {
      clanId = getArguments().getString("clanId");
      type = getArguments().getInt(KEY_TYPE);
    }
    initRankList();
  }

  @Override
  protected void onLazyLoad() {
    loadRankList(true, 1);
  }

  private void initRankList() {
    ClanMemberScoreAdapter adapter = new ClanMemberScoreAdapter(type, list);
    adapter.setOnItemClickListener(new ClanMemberScoreAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(ClanMemberRankModel.ClanMemberScore itemData) {
        MineHomepageActivity.startAction(ClanMemberRankFragment.this.getContext(), itemData.getUserId());
      }
    });
    //初始化我的数据信息
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        loadRankList(true, 1);
      }

      @Override
      public void onLoadMore() {
        loadRankList(false, page + 1);
      }
    });
    recyclerview.setAdapter(adapter);
  }

  private void loadRankList(boolean isRefresh, int page) {
    AppLogger.d("--榜单列表---" + type + "; pageIndex = " + page);
    HashMap<String, Object> map = new HashMap<>();
    map.put("page", page);
    map.put("limit", 10);
    map.put("type", type);
    map.put("user_clan_id", clanId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.getClanMemberScoreList(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ClanMemberRankModel>() {
              @Override
              public void onSuccess(ClanMemberRankModel model) {
                if(model != null) {
                  List<ClanMemberRankModel.ClanMemberScore> listData = model.getUserClanList().getList();
                  if (isRefresh) {
                    ClanMemberRankFragment.this.page = 1;
                    list.clear();
                  } else {
                    if (listData.size() > 0) {
                      ClanMemberRankFragment.this.page++;
                    }
                  }
                  if (listData.size() > 0) {
                    list.addAll(listData);
                  }
                  ClanMemberScoreAdapter adapter = (ClanMemberScoreAdapter) recyclerview.getAdapter();
                  if(adapter != null) {
                    adapter.notifyDataSetChanged();
                  }
                  ClanMemberScoreActivity activity = (ClanMemberScoreActivity) getActivity();
                  if (activity != null) {
                    activity.setTop(model.getUserClanInfo(), model.getUserClanAvg());
                  }
                  showTop(model.getUserClanAvg());
                  showBottom(model.getUserClanInfo());
                } else {
                  lyBottom.setVisibility(View.GONE);
                }
                if (list.size() == 0) {
                  ryEmpty.setVisibility(View.VISIBLE);
                } else {
                  ryEmpty.setVisibility(View.GONE);
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("--requestRanking---" + msg);
              }
              @Override
              public void onComplete() {
                super.onComplete();
                if(recyclerview!=null){
                  recyclerview.refreshComplete();
                  recyclerview.loadMoreComplete();
                }
              }
            })
    );
  }

  private void showTop(ClanMemberRankModel.ClanAvg clanAvg) {
    layTop.setVisibility(View.GONE);
    if (clanAvg == null) {
      return;
    }
    if (this.type == 1) {
      tvScore.setText(clanAvg.getAvgSpeedMax());
      tvScore.append(getString(R.string.format_brackets, clanAvg.getAvgSpeedMaxUnit()));
      if (!TextUtils.isEmpty(clanAvg.getAvgSpeedMax()) && !"0".equals(clanAvg.getAvgSpeedMax())) {
        layTop.setVisibility(View.VISIBLE);
      }
    } else if (this.type == 2) {
      tvScore.setText(clanAvg.getAvgExponentMolecular());
      tvScore.append(getString(R.string.format_brackets, clanAvg.getAvgExponentMolecularUnit()));
      if (!TextUtils.isEmpty(clanAvg.getAvgExponentMolecular()) && !"0".equals(clanAvg.getAvgExponentMolecular())) {
        layTop.setVisibility(View.VISIBLE);
      }
    } else if (this.type == 3) {
      tvScore.setText(clanAvg.getAvgRunballExponent());
      if (!TextUtils.isEmpty(clanAvg.getAvgRunballExponent()) && !"0".equals(clanAvg.getAvgRunballExponent())) {
        layTop.setVisibility(View.VISIBLE);
      }
    } else if (this.type == 4) {
      tvScore.setText(clanAvg.getAvgMarathon());
      if (!TextUtils.isEmpty(clanAvg.getAvgMarathon()) && !"0".equals(clanAvg.getAvgMarathon())) {
        layTop.setVisibility(View.VISIBLE);
      }
    }
  }

  private void showBottom(ClanMemberRankModel.ClanInfo data) {
    if (data != null) {
      String value = null, unit = null;
      // 1最高转速 2摇跑一分钟 3摇跑指数 4马拉松
      if (this.type == 1) {
        value = data.getSpeedMax();
        unit = data.getSpeedMaxUnit();
      } else if (this.type == 2) {
        value = data.getExponentMolecular();
        unit = data.getExponentMolecularUnit();
      } else if (this.type == 3) {
        value = data.getRunballExponent();
      } else if (this.type == 4) {
        value = data.getMarathon();
      }
      if (TextUtils.isEmpty(value) || "0".equals(value) || "00:00:00".equals(value)) {
        lyBottom.setVisibility(View.GONE);
        vDivider.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvValue.setVisibility(View.GONE);
        tvUnit.setVisibility(View.GONE);
        tvRankNum.setText("/");
      } else {
        lyBottom.setVisibility(View.VISIBLE);
        vDivider.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.VISIBLE);
        tvTime.setText(data.getCreatedAt());
        tvValue.setVisibility(View.VISIBLE);
        tvValue.setText(value);
        tvRankNum.setText(String.valueOf(data.getIndexs()));
        if (TextUtils.isEmpty(unit)) {
          tvUnit.setVisibility(View.GONE);
        } else {
          tvUnit.setVisibility(View.VISIBLE);
          tvUnit.setText(this.getString(R.string.format_brackets, unit));
        }
      }

      String userImg = data.getClanAvatar();
      if(!userImg.startsWith("http")) {
        userImg = Constant.getBaseUrl() + "/" + userImg;
      }
      Picasso.with(ClanMemberRankFragment.this.getContext())
          .load(userImg)
          .transform(new CircleTransform(ClanMemberRankFragment.this.getContext()))
          .placeholder(R.mipmap.default_head)
          .into(ivHead);

      tvName.setText(data.getUserName());

      tvArea.setText(data.getAddress());
    } else {
      lyBottom.setVisibility(View.GONE);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    AppLogger.d("----------ClanRankFragment-----onDestroyView--------------mRanking_type = " + type);
    if(recyclerview != null) {
      recyclerview.destroy();
      recyclerview = null;
    }
  }
}
