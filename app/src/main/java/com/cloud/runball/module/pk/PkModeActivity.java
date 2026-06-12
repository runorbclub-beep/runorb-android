package com.cloud.runball.module.pk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MinePkInfoV2;
import com.cloud.runball.dialog.PKRuleDialog;
import com.cloud.runball.model.MinePkListV2Model;
import com.cloud.runball.model.RuleIntroduceModel;
import com.cloud.runball.model.UserPkWinRateModel;
import com.cloud.runball.module.go.GoFragment;
import com.cloud.runball.module.mine.MatchRecordDetailActivity;
import com.cloud.runball.module.mine.MatchRecordTeamDetailActivity;
import com.cloud.runball.module.mine_record.MinePkRecordActivity;
import com.cloud.runball.module.mine_record.adapter.MinePkRecordAdapter;
import com.cloud.runball.module.pk.dialog.SelectPkModeDialog;
import com.cloud.runball.module.race.CreateMatchActivity;
import com.cloud.runball.module.race.CreateMatchAddActivity;
import com.cloud.runball.module.race.CreateMatchTeamActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityPkModeBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PkModeActivity extends BaseActivity {
  private ActivityPkModeBinding binding;
  Toolbar toolbar;
  TextView tvWinCount;
  TextView tvWinPercentage;
  XRecyclerView recyclerView;

  private String rule;

  private int page = 1;

  private final List<MinePkInfoV2> data = new ArrayList<>();

  public static void startAction(Context context) {
    Intent intent = new Intent(context, PkModeActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_pk_mode;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityPkModeBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    tvWinCount = binding.tvWinCount;
    tvWinPercentage = binding.tvWinPercentage;
    recyclerView = binding.recyclerView;
    // Replace @OnClick with listeners
    binding.ivRule.setOnClickListener(this::onClick);
    binding.tvCreate.setOnClickListener(this::onClick);
    binding.tvJoin.setOnClickListener(this::onClick);
    binding.tvAll.setOnClickListener(this::onClick);
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    recyclerView.setPullRefreshEnabled(true);
    recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        loadData(true, page);
      }
      @Override
      public void onLoadMore() {
        loadData(false, page + 1);
      }
    });

//    getUserPkWinRate();
//
//    loadData(true, page);
  }

  @Override
  protected void onResume() {
    super.onResume();
    getUserPkWinRate();
    loadData(true, page);
  }

  public void onClick(View view) {
    if (view.getId() == R.id.ivRule) {
      if (TextUtils.isEmpty(rule)) {
        getRule();
      } else {
        PKRuleDialog.show(this, rule);
      }
    } else if (view.getId() == R.id.tvCreate) {
      SelectPkModeDialog selectPkModeDialog = new SelectPkModeDialog(this);
      selectPkModeDialog.setOnCallback(new SelectPkModeDialog.OnCallback() {
        @Override
        public void onDoubleMode(Dialog dialog) {
          dialog.dismiss();
          Intent it = new Intent(PkModeActivity.this, CreateMatchActivity.class);
          startActivity(it);
        }
        @Override
        public void onTeamMode(Dialog dialog) {
          dialog.dismiss();
          Intent it = new Intent(PkModeActivity.this, CreateMatchTeamActivity.class);
          startActivity(it);
        }
        @Override
        public void onCancel(Dialog dialog) {
          dialog.dismiss();
        }
      });
    } else if (view.getId() == R.id.tvJoin) {
      Intent it = new Intent(this, CreateMatchAddActivity.class);
      startActivity(it);
    } else if (view.getId() == R.id.tvAll) {
      MinePkRecordActivity.startAction(this);
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
                rule = model.getPkRule();
                PKRuleDialog.show(PkModeActivity.this, rule);
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getRuleIntroduce " + msg);
              }
            })
    );
  }

  private void loadData(boolean isReset, int page) {
    HashMap<String, Object> map = new HashMap<>(2);
    if (isReset) {
      map.put("page", 1);
    } else {
      map.put("page", page);
    }
    map.put("limit", 10);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<MinePkListV2Model> observable = apiServer.getMinePkListV2(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MinePkListV2Model>() {
              @Override
              public void onSuccess(MinePkListV2Model model) {
                if (isReset) {
                  PkModeActivity.this.page = 1;
                  data.clear();
                  data.addAll(model.getList());
                } else {
                  if (model.getList() != null && model.getList().size() != 0) {
                    PkModeActivity.this.page++;
                    data.addAll(model.getList());
                  }
                }

                MinePkRecordAdapter adapter = (MinePkRecordAdapter) recyclerView.getAdapter();
                if (adapter == null) {
                  adapter = new MinePkRecordAdapter(data);
                  adapter.setListener(itemData -> {
                    if (itemData.getPkType() == 1) {
                      MatchRecordTeamDetailActivity.startAction(PkModeActivity.this, 1, itemData.getPkRoomId(), "");
                    } else {
                      MatchRecordDetailActivity.startAction(PkModeActivity.this, 0, itemData.getPkRoomId(), "");
                    }
                  });
                  recyclerView.setAdapter(adapter);
                } else {
                  adapter.notifyDataSetChanged();
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getMinePkListV2 " + msg);
              }
              @Override
              public void onComplete() {
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
              }
            })
    );
  }

  private void getUserPkWinRate() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<UserPkWinRateModel> observable = apiServer.getUserPkWinRate();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserPkWinRateModel>() {
              @Override
              public void onSuccess(UserPkWinRateModel model) {
                if (model != null) {
                  tvWinCount.setText(model.getVictory() + "");
                  tvWinPercentage.setText(new BigDecimal(model.getWinRate() + "").setScale(1, BigDecimal.ROUND_HALF_DOWN).toString()  + "%");
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getUserPkWinRate " + msg);
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
