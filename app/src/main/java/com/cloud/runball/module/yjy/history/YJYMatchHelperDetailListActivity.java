package com.cloud.runball.module.yjy.history;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.yjy.YJYHelperRankModel;
import com.cloud.runball.module.yjy.history.adapter.HelperAdapter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityYjyMatchHelperDetailListBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class YJYMatchHelperDetailListActivity extends BaseActivity {

  private ActivityYjyMatchHelperDetailListBinding binding;

  XRecyclerView rvRank;
  TextView tvScore;
  TextView tvHelpScore;
  TextView tvDistance;
  TextView tvName;
  TextView tvDate;
  TextView tvIndex;
  RelativeLayout ryEmpty;
  TextView tvRankNum;
  ImageView ivHead;
  LinearLayout lyBottom;

  private YJYHelperRankModel model;

  private List<YJYHelperRankModel.ShakeInfo> records;

  private String sysShakeId;
  private String matchDate;
  private int index;
  private String house;

  private HelperAdapter adapter;
  private int page = 1;


  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
  return R.layout.activity_yjy_match_helper_detail_list;
}

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityYjyMatchHelperDetailListBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {

  }

  @Override
  protected void initView() {
    rvRank = binding.rvRank;
    tvScore = binding.tvScore;
    tvHelpScore = binding.tvHelpScore;
    tvDistance = binding.tvDistance;
    tvName = binding.tvName;
    tvDate = binding.tvDate;
    tvIndex = binding.tvIndex;
    ryEmpty = binding.ryEmpty;
    tvRankNum = binding.tvRankNum;
    ivHead = binding.ivHead;
    lyBottom = binding.lyBottom;
    if (getIntent() != null) {
      sysShakeId = getIntent().getStringExtra("sysShakeId");
      matchDate = getIntent().getStringExtra("date");
      index = getIntent().getIntExtra("index", 0);
      house = getIntent().getStringExtra("house");
      tvDate.setText("(" + String.format(getString(R.string.lbl_main_match_index), index + 1) + " " + house + " " + matchDate + ")");
    }
    records = new ArrayList<>();
    adapter = new HelperAdapter(this, records, false);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    rvRank.setLayoutManager(layoutManager);
    rvRank.addItemDecoration(new RecyclerViewDivider(40, 20, 40, 0));
    rvRank.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    rvRank.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    rvRank.setArrowImageView(R.drawable.iconfont_downgrey);
    rvRank.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    rvRank.setPullRefreshEnabled(true);
    rvRank.setAdapter(adapter);
    rvRank.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        page = 1;
        requestDetailsRecord(sysShakeId);
      }
      @Override
      public void onLoadMore() {
        page++;
        requestDetailsRecord(sysShakeId);
      }
    });
    requestDetailsRecord(sysShakeId);
  }

  @Override
  protected void setOnResult() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_match_helper_detail);
  }

  private void requestDetailsRecord(String sysShakeId){
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("sys_shake_id", sysShakeId);
    map.put("index", index);
    map.put("page", page);
    map.put("limit", 10);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<YJYHelperRankModel> observable = apiServer.getMyShakeHelpDetail(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<YJYHelperRankModel>() {
          @Override
          public void onSuccess(YJYHelperRankModel model) {
            if(model != null && model.getShake_list().getList() != null) {
              YJYMatchHelperDetailListActivity.this.model = model;
              if (page == 1) {
                adapter.setData(YJYMatchHelperDetailListActivity.this.model.getShake_list().getList());
              } else {
                adapter.addData(YJYMatchHelperDetailListActivity.this.model.getShake_list().getList());
              }
              showSelfDetail(YJYMatchHelperDetailListActivity.this.model.getMy_info());
              ryEmpty.setVisibility(View.GONE);
            }
            if(rvRank != null){
              rvRank.refreshComplete();
              rvRank.loadMoreComplete();
            }
          }

          @Override
          public void onError(int code, String msg) {
            if (page <= 1) {
              page = 1;
            } else {
              page--;
            }
            if(rvRank != null){
              rvRank.refreshComplete();
              rvRank.loadMoreComplete();
            }
          }
        })
    );
  }

  DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

  private void showSelfDetail(YJYHelperRankModel.MyInfo selfDetailInfo){
    if(selfDetailInfo != null){
      lyBottom.setVisibility(View.VISIBLE);
      tvIndex.setText(String.format(getResources().getString(R.string.lbl_main_match_index),selfDetailInfo.getIndex()+1));
      tvName.setText(selfDetailInfo.getTitle());
      tvScore.setText(String.valueOf(selfDetailInfo.getIntegral_join()));
      tvHelpScore.setText(String.valueOf(selfDetailInfo.getIntegral() - selfDetailInfo.getIntegral_join()));
      tvDistance.setText(selfDetailInfo.getDistance()!=null? formatDistance(Double.parseDouble(selfDetailInfo.getDistance())/1000.0f):"0km");
      tvRankNum.setText("" + selfDetailInfo.getIndex());
      if(selfDetailInfo.getUser_img().startsWith("http")) {
        Picasso.with(this)
            .load(selfDetailInfo.getUser_img())
            .transform(new CircleTransform(this))
            .placeholder(R.mipmap.default_head)
            .into(ivHead);
      } else {
        Picasso.with(this)
            .load(Constant.getBaseUrl() + "/" + selfDetailInfo.getUser_img())
            .transform(new CircleTransform(this))
            .placeholder(R.mipmap.default_head)
            .into(ivHead);
      }
    }else{
      lyBottom.setVisibility(View.GONE);
    }
  }

  private String formatDistance(double distance){
    if(distance < 1000){
      return mDecimalFormat.format(distance)+"km";
    }else if(distance>=1000 && distance < 10000){
      return mDecimalFormat.format(distance/1000.0)+"k km";
    }else{
      return mDecimalFormat.format(distance/10000.0)+"w km";
    }
  }
}
