package com.cloud.runball.module.mine_record;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MinePlayStatistics;
import com.cloud.runball.model.MinePlayStatisticsModel;
import com.cloud.runball.module.home.OtherMatchHistoryActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;

import com.cloud.runball.databinding.FragmentMinePlayModeBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MinePlayModeFragment extends BaseFragment {

  private FragmentMinePlayModeBinding binding;
  TextView tvItemPKText;

  TextView tvItemUpUpText;

  TextView tvItemEventsText;

  public static MinePlayModeFragment newInstance() {
    MinePlayModeFragment fragment = new MinePlayModeFragment();
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_mine_play_mode;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentMinePlayModeBinding.bind(view);
    tvItemPKText = binding.tvItemPKText;
    tvItemUpUpText = binding.tvItemUpUpText;
    tvItemEventsText = binding.tvItemEventsText;
    // wire click listeners replacing @OnClick
    View layItemRanking = view.findViewById(R.id.layItemRanking);
    if (layItemRanking != null) layItemRanking.setOnClickListener(this::onClick);
    View layItemPK = view.findViewById(R.id.layItemPK);
    if (layItemPK != null) layItemPK.setOnClickListener(this::onClick);
    View layItemUpUp = view.findViewById(R.id.layItemUpUp);
    if (layItemUpUp != null) layItemUpUp.setOnClickListener(this::onClick);
    View layItemEvents = view.findViewById(R.id.layItemEvents);
    if (layItemEvents != null) layItemEvents.setOnClickListener(this::onClick);
  }

  @Override
  protected void onLazyLoad() {
    loadMinePlayStatistics();
  }

  public void onClick(View view) {
    if (view.getId() == R.id.layItemRanking) {
      MineRankingRecordActivity.startAction(this.getContext());
    } else if (view.getId() == R.id.layItemPK) {
      MinePkRecordActivity.startAction(this.getContext());
    } else if (view.getId() == R.id.layItemUpUp) {
      startActivity(new Intent(this.getContext(), OtherMatchHistoryActivity.class));
    } else if (view.getId() == R.id.layItemEvents) {
      MineMatchRecordActivity.startAction(this.getContext());
    }
  }

  private void loadMinePlayStatistics() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<MinePlayStatisticsModel> observable = apiServer.getMinePlayStatistics();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MinePlayStatisticsModel>() {
              @Override
              public void onSuccess(MinePlayStatisticsModel model) {
                if (model == null) {
                  return;
                }
                MinePlayStatistics pkStatistics = model.getPkStatistics();
                if (pkStatistics != null) {
                  tvItemPKText.setText(pkStatistics.getCount() + pkStatistics.getUnit());
                }
                MinePlayStatistics shakeStatistics = model.getShakeStatistics();
                if (shakeStatistics != null) {
                  tvItemUpUpText.setText(shakeStatistics.getCount() + shakeStatistics.getUnit());
                }
                MinePlayStatistics matchStatistics = model.getMatchStatistics();
                if (matchStatistics != null) {
                  tvItemEventsText.setText(matchStatistics.getCount() + matchStatistics.getUnit());
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d(msg);
              }
            })
    );
  }

}
