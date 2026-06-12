package com.cloud.runball.module.free_style;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;

import org.greenrobot.eventbus.EventBus;

public class FreeStyleActivity extends BaseActivity {

  public static void startAction(Context context) {
    Intent intent = new Intent(context, FreeStyleActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_free_style;
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
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
      FreeStyleFragment fragment = (FreeStyleFragment)fragmentManager.findFragmentByTag("freeStyleFragment");
      commonDialog.dismiss();
      //这里需要加上
      if (fragment == null) {
        finish();
        return;
      }
      fragment.uploadSurplusData();
//      EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH_RANKING));
//      finish();
    });
  }

}
