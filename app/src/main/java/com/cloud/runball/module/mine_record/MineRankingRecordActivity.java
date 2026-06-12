package com.cloud.runball.module.mine_record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.dialog.PKRuleDialog;
import com.cloud.runball.model.MineRankingDetailsModel;
import com.cloud.runball.model.RuleIntroduceModel;
import com.cloud.runball.module.go.GoFragment;
import com.cloud.runball.module.mine_record.adapter.MineRankingRecordAdapter;
import com.cloud.runball.module.mine_record.entity.MineRankingRecordInfo;
import com.cloud.runball.module.pk.PkModeActivity;
import com.cloud.runball.module.ranking.RankingActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.cloud.runball.databinding.ActivityMineRankingRecordBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MineRankingRecordActivity extends BaseActivity {

  private ActivityMineRankingRecordBinding binding;

  Toolbar toolbar;
  XRecyclerView recyclerView;
  RelativeLayout ryEmpty;

  private String rule;

  private final List<MineRankingRecordInfo> data = new ArrayList<>();

  public static void startAction(Context context) {
    Intent intent = new Intent(context, MineRankingRecordActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_mine_ranking_record;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMineRankingRecordBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    recyclerView = binding.recyclerview;
    ryEmpty = binding.ryEmpty;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    recyclerView.setPullRefreshEnabled(true);
    recyclerView.setLoadingMoreEnabled(false);
    recyclerView.setEmptyView(ryEmpty);
    recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        loadData();
      }
      @Override
      public void onLoadMore() {

      }
    });

    loadData();

    // Replace @OnClick with listeners
    View tvGotoRanking = findViewById(R.id.tvGotoRanking);
    View ivRule = findViewById(R.id.ivRule);
    if (tvGotoRanking != null) tvGotoRanking.setOnClickListener(this::onClick);
    if (ivRule != null) ivRule.setOnClickListener(this::onClick);
  }

  public void onClick(View view) {
    if (view.getId() == R.id.tvGotoRanking) {
      Intent intent = new Intent(this, RankingActivity.class);
      startActivity(intent);
    } else if (view.getId() == R.id.ivRule) {
      if (TextUtils.isEmpty(rule)) {
        getRule();
      } else {
        PKRuleDialog.show(this, rule);
      }
    }
  }

  private void getRule() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<RuleIntroduceModel> observable = apiServer.getRuleIntroduce();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<RuleIntroduceModel>() {
              @Override
              public void onSuccess(RuleIntroduceModel model) {
                rule = model.getRankingRule();
                PKRuleDialog.show(MineRankingRecordActivity.this, rule);
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getRuleIntroduce " + msg);
              }
            })
    );
  }

  private void loadData() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<MineRankingDetailsModel> observable = apiServer.getMineRankingDetails();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MineRankingDetailsModel>() {
              @Override
              public void onSuccess(MineRankingDetailsModel model) {
                data.clear();
                data.add(new MineRankingRecordInfo(
                    model.getSpeedMaxCount(),
                    getString(R.string.turn_speed_2),
                    model.getSpeedMaxUnit(),
                    model.getSpeedMax() + "",
                    model.getSpeedMaxTime()
                ));
                data.add(new MineRankingRecordInfo(
                    model.getExponentMolecularCount(),
                    getString(R.string.turn_one_minute_2),
                    model.getExponentMolecularUnit(),
                    model.getExponentMolecular() + "",
                    model.getExponentMolecularTime()
                ));
                data.add(new MineRankingRecordInfo(
                    model.getRunballExponentCount(),
                    getString(R.string.turn_exponent_2),
                    "",
                    model.getRunballExponent() + "",
                    model.getRunballExponentTime()
                ));
                data.add(new MineRankingRecordInfo(
                    model.getMarathonCount(),
                    getString(R.string.turn_marathon_2),
                    "",
                    model.getMarathon() + "",
                    model.getMarathonTime()
                ));
                MineRankingRecordAdapter adapter = (MineRankingRecordAdapter) recyclerView.getAdapter();
                if (adapter == null) {
                  adapter = new MineRankingRecordAdapter(data);
                  recyclerView.setAdapter(adapter);
                } else {
                  adapter.notifyDataSetChanged();
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getMineRankingDetails" + msg);
              }

              @Override
              public void onComplete() {
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
              }
            })
    );
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (recyclerView != null) {
      recyclerView.destroy();
      recyclerView = null;
    }
  }
}
