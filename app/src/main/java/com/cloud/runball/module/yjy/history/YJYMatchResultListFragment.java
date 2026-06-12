package com.cloud.runball.module.yjy.history;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.OtherMatchDetailInfo;
import com.cloud.runball.constant.QrCodeConstant;
import com.cloud.runball.dialog.ShareCardDialog;
import com.cloud.runball.dialog.ShareTargetDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.OtherMatchDetailModel;
import com.cloud.runball.module.yjy.history.adapter.OtherMatchListAdapter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.share.ShareManage;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentYjyMatchResultListBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class YJYMatchResultListFragment extends BaseFragment {

  private FragmentYjyMatchResultListBinding binding;

  RecyclerView rvRank;
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

  private OtherMatchDetailModel otherMatchModel;

  List<OtherMatchDetailInfo> records;

  String sys_shake_id;
  String matchDate;
  OtherMatchListAdapter otherMatchAdapter;

  public static YJYMatchResultListFragment newInstance(String sysShakeId, String date) {
    YJYMatchResultListFragment fragment = new YJYMatchResultListFragment();
    Bundle args = new Bundle();
    args.putString("sysShakeId", sysShakeId);
    args.putString("date", date);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_yjy_match_result_list;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentYjyMatchResultListBinding.bind(view);
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
      sys_shake_id = getArguments().getString("sysShakeId");
      matchDate = getArguments().getString("date");
    }
    records = new ArrayList<>();
    otherMatchAdapter = new OtherMatchListAdapter(this.getContext(), records);
    otherMatchAdapter.setOnItemClickListener((itemData) -> {
      Intent intent = new Intent(YJYMatchResultListFragment.this.getContext(), YJYMatchHelperDetailListActivity.class);
      intent.putExtra("sysShakeId", sys_shake_id);
      intent.putExtra("date", matchDate);
      intent.putExtra("index", itemData.getIndex());
      intent.putExtra("house", itemData.getTitle());
      startActivity(intent);
    });
    LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    rvRank.setLayoutManager(layoutManager);
    rvRank.addItemDecoration(new RecyclerViewDivider(40, 20, 40, 0));
    rvRank.setAdapter(otherMatchAdapter);
    // Replace @OnClick with listeners
    if (binding.tvShare != null) binding.tvShare.setOnClickListener(this::onViewClicked);
  }

  @Override
  protected void onLazyLoad() {
    requestDetailsRecord(sys_shake_id);
  }

  public void onViewClicked(View v) {
    gg();
  }

  public void gg() {
    if (otherMatchModel != null) {
      OtherMatchDetailInfo targetInfo = null;
      for (OtherMatchDetailInfo info: otherMatchModel.getGroup_list()) {
        if (info.getShake_group_id().equals(otherMatchModel.getMy_info().getShake_group_id())) {
          targetInfo = info;
          break;
        }
      }
      if (targetInfo == null) {
        return;
      }
      showShareCardDialog(
          otherMatchModel.getMy_info().getDate(), 3,
          otherMatchModel.getMy_info().getIndex(), otherMatchModel.getMy_info().getIndex(),
          otherMatchModel.getMy_info().getTitle(), targetInfo.getNum(),
          new BigDecimal(targetInfo.getDistance()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_DOWN).doubleValue(),
          targetInfo.getRanking(),
          new BigDecimal(otherMatchModel.getMy_info().getDistance()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_DOWN).doubleValue(),
          otherMatchModel.getMy_info().getDuration(),
          otherMatchModel.getMy_info().getIntegral()
      );
    }
  }

  private void showShareTargetDialog(Bitmap bitmap) {
    ShareTargetDialog dialog = new ShareTargetDialog();
    dialog.show(YJYMatchResultListFragment.this.getContext(), new ShareTargetDialog.ConfirmCallBack() {
      @Override
      public void onCancel() {

      }
      @Override
      public void onShareTarget(ShareTargetDialog.ShareTarget shareTarget) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (ActivityCompat.checkSelfPermission(YJYMatchResultListFragment.this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(YJYMatchResultListFragment.this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 188);
            return;
          }
        }
        ShareManage shareManage = new ShareManage();
        shareManage.shareBitmap(YJYMatchResultListFragment.this.getActivity(), shareTarget.getType(), bitmap, new ShareManage.ShareCallback() {
          @Override
          public void onStart() {

          }
          @Override
          public void onResult() {
            ShareCardDialog.dismiss();
          }
          @Override
          public void onError(Throwable throwable) {

          }
          @Override
          public void onCancel() {

          }
        });
      }
    });
  }

  private void showShareCardDialog(
      String date, int upupStatus, int horsePath, int horseNum,
      String horseTitle, int helpPlayers, double helpDistance, int ranking,
      double myHelpDistance, int myHelpTime, int score) {
    ShareCardDialog.showShareUpup(YJYMatchResultListFragment.this.getContext(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_name(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getAddress(),
        AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img(),
        date, upupStatus, horsePath, horseNum, horseTitle,
        helpPlayers, helpDistance, ranking,
        myHelpDistance, myHelpTime, score,
        QrCodeConstant.WECHAT_OFFICIAL_ACCOUNTS_URL,
        new ShareCardDialog.ConfirmCallBack() {
          @Override
          public void onCancel() {

          }
          @Override
          public void onMore() {

          }
          @Override
          public void onShare(Bitmap bitmap) {
            showShareTargetDialog(bitmap);
          }
        });
  }

  private void requestDetailsRecord(String sys_shake_id){
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("sys_shake_id", sys_shake_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<OtherMatchDetailModel> observable = apiServer.shakeHistoryDetailData(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<OtherMatchDetailModel>() {
          @Override
          public void onSuccess(OtherMatchDetailModel otherMatchModel) {
            if(otherMatchModel!=null && otherMatchModel.getGroup_list()!=null) {
              YJYMatchResultListFragment.this.otherMatchModel = otherMatchModel;
              otherMatchAdapter.notifyDataSetChanged(otherMatchModel.getGroup_list());
              showSelfDetail(otherMatchModel.getGroup_list(), otherMatchModel.getMy_info());
              ryEmpty.setVisibility(View.GONE);
            }
          }

          @Override
          public void onError(int code, String msg) {

          }
        })
    );
  }

  DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

  private void showSelfDetail(List<OtherMatchDetailInfo> houseListInfo, OtherMatchDetailModel.SelfDetailInfo selfDetailInfo){
    if(selfDetailInfo != null){
      lyBottom.setVisibility(View.VISIBLE);
      tvIndex.setText(getString(R.string.my) + String.format(getResources().getString(R.string.lbl_main_match_index), selfDetailInfo.getIndex() + 1));
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
        int rank = 0;
        for (OtherMatchDetailInfo info : houseListInfo) {
          if (info.getIndex() == selfDetailInfo.getIndex()) {
            rank = info.getRanking();
            break;
          }
        }
        if (rank == 0) {
          tvRankNum.setVisibility(View.INVISIBLE);
        }
        tvRankNum.setText("" + rank);
      } else {
        tvDistance.setText("0km");
        tvRankNum.setVisibility(View.INVISIBLE);
      }

      String name = "horse_" + (selfDetailInfo.getIndex() + 1 ) + "_stop";
      int id = getResources().getIdentifier(name, "drawable", this.getContext().getPackageName());
      if(id != 0) {
        ivHead.setBackgroundResource(id);
      }

//      if(selfDetailInfo.getUser_img().startsWith("http")) {
//        Picasso.with(this.getContext())
//            .load(selfDetailInfo.getUser_img())
//            .transform(new CircleTransform(this.getContext()))
//            .placeholder(R.mipmap.default_head)
//            .into(ivHead);
//      } else {
//        Picasso.with(this.getContext())
//            .load(Constant.getBaseUrl() + "/" + selfDetailInfo.getUser_img())
//            .transform(new CircleTransform(this.getContext()))
//            .placeholder(R.mipmap.default_head)
//            .into(ivHead);
//      }
    } else {
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
