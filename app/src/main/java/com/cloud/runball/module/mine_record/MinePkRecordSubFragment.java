package com.cloud.runball.module.mine_record;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MinePkInfoV2;
import com.cloud.runball.model.MinePkListV2Model;
import com.cloud.runball.module.mine.MatchRecordDetailActivity;
import com.cloud.runball.module.mine.MatchRecordTeamDetailActivity;
import com.cloud.runball.module.mine_record.adapter.MinePkRecordAdapter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentMinePkRecordSubBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MinePkRecordSubFragment extends BaseFragment {

  private FragmentMinePkRecordSubBinding binding;
  XRecyclerView recyclerView;

  private final List<MinePkInfoV2> data = new ArrayList<>();
  private int page = 1;
  private int type = 0;

  public static MinePkRecordSubFragment newInstance(int type) {
    MinePkRecordSubFragment fragment = new MinePkRecordSubFragment();
    Bundle bundle = new Bundle();
    bundle.putInt("type", type);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_mine_pk_record_sub;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentMinePkRecordSubBinding.bind(view);
    recyclerView = binding.recyclerview;
    Bundle bundle = getArguments();
    if (bundle != null) {
      type = bundle.getInt("type", 0);
    }

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
  }

  @Override
  protected void onLazyLoad() {
    loadData(true, page);
  }

  private void loadData(boolean isReset, int page) {
    HashMap<String, Object> map = new HashMap<>(3);
    if (isReset) {
      map.put("page", 1);
    } else {
      map.put("page", page);
    }
    map.put("limit", 10);
    // 获取类型 1胜场 2负场 0全部
    map.put("source", type);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<MinePkListV2Model> observable = apiServer.getMinePkListV2(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<MinePkListV2Model>() {
              @Override
              public void onSuccess(MinePkListV2Model model) {
                if (isReset) {
                  MinePkRecordSubFragment.this.page = 1;
                  data.clear();
                  data.addAll(model.getList());
                } else {
                  if (model.getList() != null && model.getList().size() != 0) {
                    MinePkRecordSubFragment.this.page++;
                    data.addAll(model.getList());
                  }
                }

                MinePkRecordAdapter adapter = (MinePkRecordAdapter) recyclerView.getAdapter();
                if (adapter == null) {
                  adapter = new MinePkRecordAdapter(data);
                  adapter.setListener(itemData -> {
                    if (itemData.getPkType() == 1) {
                      MatchRecordTeamDetailActivity.startAction(MinePkRecordSubFragment.this.getContext(), 1, itemData.getPkRoomId(), "");
                    } else {
                      MatchRecordDetailActivity.startAction(MinePkRecordSubFragment.this.getContext(), 0, itemData.getPkRoomId(), "");
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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (recyclerView != null) {
      recyclerView.destroy();
      recyclerView = null;
    }
    binding = null;
  }

}
