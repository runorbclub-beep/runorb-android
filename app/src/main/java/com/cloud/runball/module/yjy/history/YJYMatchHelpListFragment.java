package com.cloud.runball.module.yjy.history;

import android.os.Bundle;
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

import com.cloud.runball.databinding.FragmentYjyMatchHelpListBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class YJYMatchHelpListFragment extends BaseFragment {

  private FragmentYjyMatchHelpListBinding binding;

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

  private HelperAdapter adapter;
  private int page = 1;

  public static YJYMatchHelpListFragment newInstance(String sysShakeId, String date) {
    YJYMatchHelpListFragment fragment = new YJYMatchHelpListFragment();
    Bundle args = new Bundle();
    args.putString("sysShakeId", sysShakeId);
    args.putString("date", date);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_yjy_match_help_list;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentYjyMatchHelpListBinding.bind(view);
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
    if (getArguments() != null) {
      sysShakeId = getArguments().getString("sysShakeId");
      matchDate = getArguments().getString("date");
    }
    records = new ArrayList<>();
    adapter = new HelperAdapter(this.getContext(), records, true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    rvRank.setLayoutManager(layoutManager);
    rvRank.addItemDecoration(new RecyclerViewDivider(40, 20, 40, 0));
    rvRank.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    rvRank.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    rvRank.setArrowImageView(R.drawable.iconfont_downgrey);
    rvRank.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    rvRank.setPullRefreshEnabled(true);
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
    rvRank.setAdapter(adapter);
  }

  @Override
  protected void onLazyLoad() {
    requestDetailsRecord(sysShakeId);
  }

  private void requestDetailsRecord(String sysShakeId){
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("sys_shake_id", sysShakeId);
    map.put("page", page);
    map.put("limit", 10);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<YJYHelperRankModel> observable = apiServer.getMyShakeBoostRanking(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<YJYHelperRankModel>() {
          @Override
          public void onSuccess(YJYHelperRankModel model) {
            if(model !=null && model.getShake_list().getList() != null) {
              YJYMatchHelpListFragment.this.model = model;
              List<YJYHelperRankModel.ShakeInfo> listData = YJYMatchHelpListFragment.this.model.getShake_list().getList();
              if (page == 1) {
                if (listData == null || listData.size() == 0) {
                  ryEmpty.setVisibility(View.VISIBLE);
                } else {
                  ryEmpty.setVisibility(View.GONE);
                }
                adapter.setData(listData);
              } else {
                ryEmpty.setVisibility(View.GONE);
                adapter.addData(YJYMatchHelpListFragment.this.model.getShake_list().getList());
              }
              showSelfDetail(YJYMatchHelpListFragment.this.model.getMy_info());
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
//      tvIndex.setText(String.format(getResources().getString(R.string.lbl_main_match_index),selfDetailInfo.getIndex()+1));
      tvIndex.setText(getString(R.string.my) + String.format(getResources().getString(R.string.lbl_main_match_index),selfDetailInfo.getIndex() + 1));
      tvName.setText(selfDetailInfo.getTitle());
      tvScore.setText(String.valueOf(selfDetailInfo.getIntegral_join()));
      tvHelpScore.setText(String.valueOf(selfDetailInfo.getIntegral() - selfDetailInfo.getIntegral_join()));

      if (selfDetailInfo.getDistance() != null) {
        tvDistance.setText(
            String.format(
                getString(R.string.lbl_main_match_record_distance),
                formatDistance(Double.parseDouble(selfDetailInfo.getDistance()) / 1000.0f)
            )
        );
        tvRankNum.setText(""+selfDetailInfo.getRank_my());
      } else {
        tvDistance.setText("0km");
        tvRankNum.setVisibility(View.INVISIBLE);
      }


//      tvDistance.setText(selfDetailInfo.getDistance() != null ? formatDistance(Double.parseDouble(selfDetailInfo.getDistance()) / 1000.0f) : "0km");
//      tvRankNum.setText(""+selfDetailInfo.getRank_my());

      if(selfDetailInfo.getUser_img().startsWith("http")) {
        Picasso.with(this.getContext())
            .load(selfDetailInfo.getUser_img())
            .transform(new CircleTransform(this.getContext()))
            .placeholder(R.mipmap.default_head)
            .into(ivHead);
      } else {
        Picasso.with(this.getContext())
            .load(Constant.getBaseUrl() + "/" + selfDetailInfo.getUser_img())
            .transform(new CircleTransform(this.getContext()))
            .placeholder(R.mipmap.default_head)
            .into(ivHead);
      }
    }else{
      lyBottom.setVisibility(View.GONE);
      tvDistance.setText("0km");
    }
  }


  private String formatDistance(double distance){
    if(distance<1000){
      return mDecimalFormat.format(distance);
    }else if(distance>=1000 && distance < 10000){
      return mDecimalFormat.format(distance/1000.0)+"k";
    }else{
      return mDecimalFormat.format(distance/10000.0)+"w";
    }
  }

}
