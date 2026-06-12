package com.cloud.runball.module.home;


import android.content.Intent;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.listener.OnItemClickListener;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.OtherMatchInfo;
import com.cloud.runball.model.OtherMatchModel2;
import com.cloud.runball.module.home.adapter.OtherMatchAdapter;
import com.cloud.runball.module.yjy.history.OtherMainMatchListActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.cloud.runball.databinding.ActivityOtherMatchHistoryBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.module.home
 * @ClassName: OtherMatchActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/20 17:17
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/20 17:17
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchHistoryActivity extends BaseActivity implements OnItemClickListener {

  private ActivityOtherMatchHistoryBinding binding;

  XRecyclerView recyclerview;
  RelativeLayout ryEmpty;

  private final List<OtherMatchInfo> records = new ArrayList<>();

  private int mPage = 1;

  private OtherMatchAdapter otherMatchAdapter = null;

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_other_match_history;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityOtherMatchHistoryBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {

  }

  @Override
  protected void initView() {
    recyclerview = binding.recyclerview;
    ryEmpty = binding.ryEmpty;
    otherMatchAdapter = new OtherMatchAdapter(this,records);
    otherMatchAdapter.setOnItemClickListener(this);
    recyclerview.addItemDecoration(new RecyclerViewDivider(40, 40, 40, 0));
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setEmptyView(ryEmpty);
    recyclerview.setLoadingMoreEnabled(true);
    recyclerview.setPullRefreshEnabled(true);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        requestRecentRecord(true, 1);
      }

      @Override
      public void onLoadMore() {
        requestRecentRecord(false, mPage + 1);
      }
    });

    recyclerview.setAdapter(otherMatchAdapter);
    otherMatchAdapter.notifyDataSetChanged();

    requestRecentRecord(true, mPage);
  }

  @Override
  protected void setOnResult() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_match_horse_record);
  }


  private void requestRecentRecord(boolean isReset, int page){
    HashMap<String, Object> map = new HashMap<>(2);
    if (isReset) {
      map.put("page", 1);
    } else {
      map.put("page", page);
    }
    map.put("limit", 10);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<OtherMatchModel2> observable  =apiServer.shakeHistoryData(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<OtherMatchModel2>() {
              @Override
              public void onSuccess(OtherMatchModel2 otherMatchModel) {
                if (otherMatchModel == null) {
                  return;
                }
                if (isReset) {
                  OtherMatchHistoryActivity.this.mPage = 1;
                  records.clear();
                  records.addAll(otherMatchModel.getList());
                } else {
                  if(otherMatchModel.getList() != null && otherMatchModel.getList().size() != 0) {
                    OtherMatchHistoryActivity.this.mPage ++;
                    records.addAll(otherMatchModel.getList());
                  }
                }
                if(otherMatchAdapter != null) {
                  otherMatchAdapter.notifyDataSetChanged();
                }
              }

              @Override
              public void onError(int code, String msg) {
                AppLogger.d("shakeHistoryData -- " + msg);
              }
              @Override
              public void onComplete() {
                recyclerview.refreshComplete();
                recyclerview.loadMoreComplete();
              }
            })
    );
  }

  @Override
  public void onItemClick(Object t, int index) {
    Intent it=new Intent(this, OtherMainMatchListActivity.class);
    it.putExtra("sys_shake_id",((OtherMatchInfo)t).getSys_shake_id());
    it.putExtra("date",((OtherMatchInfo)t).getDate());
    startActivity(it);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (recyclerview != null) {
      recyclerview.destroy();
      recyclerview = null;
    }
  }
}
