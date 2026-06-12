package com.cloud.runball.module.social;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.model.OthersInfoModel;
import com.cloud.runball.module.clan.ClanActivity;
import com.cloud.runball.module.social.adapter.DynamicAdapter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityMineHomepageBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MineHomepageActivity extends BaseActivity {

  private ActivityMineHomepageBinding binding;

  Toolbar toolbar;
  ImageView imgInfoAvatar;
  ImageView ivVip;
  TextView tvNickname;
  TextView tvAge;
  View areaDivide;
  TextView tvArea;
  View weixinDivide;
  TextView tvWeixin;
  TextView tvInfoSign;
  TextView tvExponent;
  TextView tvMaxSpeed;
  TextView tvOneMinute;
  TextView tvMarathon;
  View layClan;
  ImageView ivHead;
  TextView tvName;
  TextView tvClanArea;
  TextView tvMemberCount;
  RecyclerView recyclerview;


  private OthersInfoModel model;


  private final WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  private final List<DynamicAdapter.ItemData> dynamicData = new ArrayList<>();

  public static void startAction(Context context, String id) {
    Intent intent = new Intent(context, MineHomepageActivity.class);
    intent.putExtra("id", id);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_mine_homepage;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMineHomepageBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    imgInfoAvatar = binding.imgInfoAvatar;
    ivVip = binding.ivVip;
    tvNickname = binding.tvInfoNickname;
    tvAge = binding.tvAge;
    areaDivide = binding.areaDivide;
    tvArea = binding.tvArea;
    weixinDivide = binding.weixinDivide;
    tvWeixin = binding.tvWeixin;
    tvInfoSign = binding.tvInfoSign;
    tvExponent = binding.tvExponent;
    tvMaxSpeed = binding.tvMaxSpeed;
    tvOneMinute = binding.tvOneMinute;
    tvMarathon = binding.tvMarathon;
    layClan = binding.layClan;
    ivHead = binding.ivHead;
    tvName = binding.tvName;
    tvClanArea = binding.tvClanArea;
    tvMemberCount = binding.tvMemberCount;
    recyclerview = binding.recyclerview;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    Intent intent = getIntent();
    if (intent == null) {
      finish();
      return;
    }

    String id = intent.getStringExtra("id");
    loadData(id);

    // Replace @OnClick with listener
    View layClanInfo = findViewById(R.id.layClanInfo);
    if (layClanInfo != null) layClanInfo.setOnClickListener(this::onClick);
  }

  public void onClick(View view) {
    if (view.getId() == R.id.layClanInfo) {
      if (model != null && model.getUserClanMembers() != null) {
        ClanActivity.startAction(this, model.getUserClanMembers().getUserClanId());
      }
    }
  }

  private void loadData(String id) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("member_user_id", id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.getUserOthersInfo(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<OthersInfoModel>() {
              @Override
              public void onSuccess(OthersInfoModel model) {
                MineHomepageActivity.this.model = model;
                showUserData(model);
                showAchievementData(model.getUserAchievement());
                showClanData(model.getUserClanMembers());
                showDynamicList(model.getUserAchievement());
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(MineHomepageActivity.this, msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  private void showUserData(OthersInfoModel data) {
    String imgUrl = data.getUserImg();
    if(!imgUrl.startsWith("http")) {
      imgUrl = Constant.getBaseUrl() + "/" + imgUrl;
    }
    Picasso.with(this)
        .load(imgUrl)
        .transform(new CircleTransform(this))
        .into(imgInfoAvatar);

    Drawable drawableSex = null;
    if (SexConstant.SEX_MAN.equals(data.getSysSexId())) {
      drawableSex = getResources().getDrawable(R.mipmap.ic_man);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    } else if (SexConstant.SEX_WOMEN.equals(data.getSysSexId())) {
      drawableSex = getResources().getDrawable(R.mipmap.ic_women);
      drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
    }
    tvNickname.setCompoundDrawables(drawableSex, null, null, null);
    tvNickname.setText(data.getUserName());

    if (TextUtils.isEmpty(data.getAge())) {
      tvAge.setVisibility(View.GONE);
      areaDivide.setVisibility(View.GONE);
    } else if (Integer.parseInt(data.getAge()) < 18) {
      tvAge.setText(R.string.lbl_age_between_2);
    } else {
      tvAge.setVisibility(View.GONE);
      areaDivide.setVisibility(View.GONE);
    }
//    tvAge.setText(getString(R.string.format_age, data.getAge() + ""));

    if (TextUtils.isEmpty(data.getAddress())) {
      areaDivide.setVisibility(View.GONE);
      tvArea.setVisibility(View.GONE);
    } else {
      areaDivide.setVisibility(View.VISIBLE);
      tvArea.setVisibility(View.VISIBLE);
      tvArea.setText(data.getAddress());
    }

    if (TextUtils.isEmpty(data.getWeChartId())) {
      weixinDivide.setVisibility(View.GONE);
      tvWeixin.setVisibility(View.GONE);
    } else {
      weixinDivide.setVisibility(View.VISIBLE);
      tvWeixin.setVisibility(View.VISIBLE);
      tvWeixin.setText(getString(R.string.format_weixin, data.getWeChartId()));
    }

    tvInfoSign.setText(data.getSelfDescription());
  }

  private void showAchievementData(OthersInfoModel.UserAchievement data) {
    tvExponent.setText(data.getRunballExponent());
    tvMaxSpeed.setText(data.getSpeedMax());

    if (TextUtils.isEmpty(data.getExponentMolecular())) {
      tvOneMinute.setText("0");
    } else {
      DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
      String distanceFormat = mDecimalFormat.format(Float.parseFloat(data.getExponentMolecular()) / 1000);
      tvOneMinute.setText(distanceFormat);
    }

    if (TextUtils.isEmpty(data.getMarathon())) {
      tvMarathon.setText("00:00:00");
    } else {
      tvMarathon.setText(TimeUtils.formatDuration3(Integer.parseInt(data.getMarathon())));
    }
  }

  private void showClanData(OthersInfoModel.UserClanMembers data) {
    if (data == null) {
      layClan.setVisibility(View.GONE);
      return;
    }

    // 0审核中  1正常  2已拒绝 -- 0不可加入 1待审核  2已加入  3未加入
    if (data.getStatus() == 0 || data.getStatus() == 1 || data.getStatus() == 3) {
      layClan.setVisibility(View.GONE);
      return;
    }

    layClan.setVisibility(View.VISIBLE);

    String imgUrl = data.getClanAvatar();
    if(!imgUrl.startsWith("http")) {
      imgUrl = Constant.getBaseUrl() + "/" + imgUrl;
    }
    Picasso.with(this)
        .load(imgUrl)
//        .transform(new CircleTransform(this))
        .into(ivHead);

    tvName.setText(data.getTitle());

    tvClanArea.setText(data.getAddress());

    tvMemberCount.setText(getString(R.string.association_match_join_sum, data.getClanCount() + ""));
  }

  private void showDynamicList(OthersInfoModel.UserAchievement data) {
    dynamicData.clear();
    if (data != null) {
      if (!TextUtils.isEmpty(data.getSpeedMax()) && !"0".equals(data.getSpeedMax()) && !TextUtils.isEmpty(data.getSpeedMaxTime()) && !"0".equals(data.getSpeedMaxTime())) {
        dynamicData.add(new DynamicAdapter.ItemData(0, Long.parseLong(data.getSpeedMaxTime()), data.getSpeedMax()));
      }
      if (!TextUtils.isEmpty(data.getExponentMolecular()) && !"0".equals(data.getExponentMolecular()) && !TextUtils.isEmpty(data.getExponentMolecularTime()) && !"0".equals(data.getExponentMolecularTime())) {
        dynamicData.add(new DynamicAdapter.ItemData(1, Long.parseLong(data.getExponentMolecularTime()), data.getExponentMolecular()));
      }
      if (!TextUtils.isEmpty(data.getRunballExponent()) && !"0".equals(data.getRunballExponent()) && !TextUtils.isEmpty(data.getRunballExponentTime()) && !"0".equals(data.getRunballExponentTime())) {
        dynamicData.add(new DynamicAdapter.ItemData(2, Long.parseLong(data.getRunballExponentTime()), data.getRunballExponent()));
      }
      if (!TextUtils.isEmpty(data.getMarathon()) && !"0".equals(data.getMarathon()) && !TextUtils.isEmpty(data.getMarathonTime()) && !"0".equals(data.getMarathonTime())) {
        dynamicData.add(new DynamicAdapter.ItemData(3, Long.parseLong(data.getMarathonTime()), data.getMarathon()));
      }
    }
    Collections.sort(dynamicData, (o1, o2) -> {
      return (int) (o1.getDatetime() - o2.getDatetime());
    });

    DynamicAdapter adapter = (DynamicAdapter) recyclerview.getAdapter();
    if (adapter == null) {
      adapter = new DynamicAdapter(dynamicData);
      recyclerview.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }
  }

}
