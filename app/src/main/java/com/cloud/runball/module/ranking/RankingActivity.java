package com.cloud.runball.module.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;
import com.cloud.runball.databinding.ActivityRankingBinding;
import com.cloud.runball.module_bluetooth.constant.ServiceSendConstant;
import com.cloud.runball.module_bluetooth.data.event.ServiceSendEvent;

import org.greenrobot.eventbus.EventBus;

 

public class RankingActivity extends BaseActivity {

  private ActivityRankingBinding binding;
  FragmentContainerView layContent;

  @Override
  protected int onLayoutId() {
    return R.layout.activity_ranking;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityRankingBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    layContent = binding.layContent;
    setEmptyStatusBar();
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  @Override
  public void onBackPressed() {
//    super.onBackPressed();
    showExitDialog();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    super.onKeyDown(keyCode, event);
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      showExitDialog();
    }
    return false;
  }

  public void showExitDialog() {
    AssociationCommonDialog dialog = new AssociationCommonDialog(this);
    dialog.setContent(getString(R.string.tip), getString(R.string.tip_ranking_exit));
    dialog.addBtn(getString(R.string.btn_cancel), false, commonDialog -> {
      commonDialog.dismiss();
    });
    dialog.addBtn(getString(R.string.btn_ok), true, commonDialog -> {
      FragmentManager fragmentManager = getSupportFragmentManager();
      MainFragment fragment = (MainFragment)fragmentManager.findFragmentByTag("mainFragment");
      commonDialog.dismiss();
      //这里需要加上
      if (fragment == null) {
        EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CLOSE_MATCH_TIMING));
        finish();
        return;
      }
      fragment.uploadSurplusData();
//      EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH_RANKING));
//      finish();
    });
  }

}
