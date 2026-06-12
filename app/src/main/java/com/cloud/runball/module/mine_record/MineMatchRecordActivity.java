package com.cloud.runball.module.mine_record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;

import com.cloud.runball.databinding.ActivityMineMatchRecordBinding;

public class MineMatchRecordActivity extends BaseActivity {

  private ActivityMineMatchRecordBinding binding;
  Toolbar toolbar;

  public static void startAction(Context context) {
    Intent intent = new Intent(context, MineMatchRecordActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_mine_match_record;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMineMatchRecordBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });
  }
}
