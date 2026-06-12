package com.cloud.runball.module.clan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.model.ClanInfoModel;

import com.cloud.runball.databinding.FragmentClanHomepageBinding;

public class ClanHomepageFragment extends BaseFragment {

  private FragmentClanHomepageBinding binding;

  TextView tvClanIntroduction;
  ImageView ivOpenIntroduction;
  TextView tvMaxSpeed;
  TextView tvOneMinute;
  TextView tvExponent;
  TextView tvMarathon;
  TextView tvGotoClanScore;
  View layBottom;

  private String clanId;
  private int captainStatus;
  private int status;

  public static ClanHomepageFragment newInstance(String clanId, int captainStatus, int status, String introduction, ClanInfoModel.Achievement achievement) {
    ClanHomepageFragment fragment = new ClanHomepageFragment();
    Bundle bundle = new Bundle();
    bundle.putString("clanId", clanId);
    bundle.putInt("captainStatus", captainStatus);
    bundle.putInt("status", status);
    bundle.putString("introduction", introduction);
    bundle.putSerializable("achievement", achievement);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_clan_homepage;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentClanHomepageBinding.bind(view);
    tvClanIntroduction = binding.tvClanIntroduction;
    ivOpenIntroduction = binding.ivOpenIntroduction;
    tvMaxSpeed = binding.tvMaxSpeed;
    tvOneMinute = binding.tvOneMinute;
    tvExponent = binding.tvExponent;
    tvMarathon = binding.tvMarathon;
    tvGotoClanScore = binding.tvGotoClanScore;
    layBottom = binding.layBottom;

    // Replace @OnClick with listeners
    tvGotoClanScore.setOnClickListener(this::onClick);
    ivOpenIntroduction.setOnClickListener(this::onClick);
    tvClanIntroduction.setOnClickListener(this::onClick);
  }

  @Override
  protected void onLazyLoad() {
    Bundle bundle = getArguments();
    if (bundle == null) {
      return;
    }

    clanId = bundle.getString("clanId");
    captainStatus = bundle.getInt("captainStatus");
    status = bundle.getInt("status");
    String introduction = bundle.getString("introduction");
    ClanInfoModel.Achievement achievement = (ClanInfoModel.Achievement) bundle.getSerializable("achievement");

    tvClanIntroduction.setText(introduction);

    // 0不可加入 1待审核  2已加入  3未加入
    if (status == 0 || status == 1) {
      tvGotoClanScore.setVisibility(View.GONE);
    } else {
      tvGotoClanScore.setVisibility(View.VISIBLE);
    }

    // captainStatus 用户是否队长：0否  1是 2尚未加入俱乐部
    // status 0不可加入 1待审核  2已加入  3未加入
    if (status == 0) {
      layBottom.setVisibility(View.GONE);
    } else if (status == 1) {
      if (captainStatus == 1) {
        layBottom.setVisibility(View.VISIBLE);
      } else {
        layBottom.setVisibility(View.GONE);
      }
    } else if (status == 2) {
      layBottom.setVisibility(View.GONE);
    } else if (status == 3) {
      layBottom.setVisibility(View.GONE);
    } else {
      layBottom.setVisibility(View.GONE);
    }

    if (TextUtils.isEmpty(achievement.getAvgSpeedMax())) {
      tvMaxSpeed.setText("/");
    } else {
      tvMaxSpeed.setText(achievement.getAvgSpeedMax());
    }

    if (TextUtils.isEmpty(achievement.getAvgSpeedMax())) {
      tvOneMinute.setText("/");
    } else {
      tvOneMinute.setText(achievement.getAvgExponentMolecular());
    }

    if (TextUtils.isEmpty(achievement.getAvgSpeedMax())) {
      tvExponent.setText("/");
    } else {
      tvExponent.setText(achievement.getAvgRunballExponent());
    }

    if (TextUtils.isEmpty(achievement.getAvgSpeedMax())) {
      tvMarathon.setText("/");
    } else {
      tvMarathon.setText(achievement.getAvgMarathon());
    }
  }

  public void onClick(View view) {
    if (view.getId() == R.id.tvGotoClanScore) {
      ClanMemberScoreActivity.startAction(this.getContext(), clanId);
    } else if (view.getId() == R.id.ivOpenIntroduction || view.getId() == R.id.tvClanIntroduction) {
      openIntroduction();
    }
  }

  private void openIntroduction() {
    if (tvClanIntroduction.getMaxLines() != 3) {
      ivOpenIntroduction.setImageResource(R.mipmap.item_more2);
      tvClanIntroduction.setMaxLines(3);
    } else {
      ivOpenIntroduction.setImageResource(R.mipmap.item_more3);
      tvClanIntroduction.setMinLines(1);
      tvClanIntroduction.setMaxLines(Integer.MAX_VALUE);
    }
  }

}
