package com.cloud.runball.module.match_football_association;

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

public class AssociationMatchActivity extends BaseActivity {

  public static final String KEY_TITLE = "title";
  public static final String KEY_SYS_SYS_MATCH_ID = "sys_sys_match_id";
  public static final String KEY_SYS_MATCH_ID = "sys_match_id";
  public static final String KEY_MATCH_STAGE_ID = "match_stage_id";
  public static final String KEY_IS_QUARTETS = "is_quartets";
  public static final String KEY_MATCH_START_TIME = "match_start_time";
  public static final String KEY_MATCH_END_TIME = "match_end_time";
  public static final String KEY_JOIN_MATCH_UNITS = "join_match_units";

  public static void startAction(
      Context context, String title, String sysMatchId, String sysSysMatchId, String matchStageId, int isQuartets,
      long matchStartTime, long matchEndTime, String joinMatchUnits
  ) {
    Intent intent = new Intent(context, AssociationMatchActivity.class);
    intent.putExtra(KEY_TITLE, title);
    intent.putExtra(KEY_SYS_SYS_MATCH_ID, sysSysMatchId);
    intent.putExtra(KEY_SYS_MATCH_ID, sysMatchId);
    intent.putExtra(KEY_MATCH_STAGE_ID, matchStageId);
    intent.putExtra(KEY_IS_QUARTETS, isQuartets);
    intent.putExtra(KEY_MATCH_START_TIME, matchStartTime);
    intent.putExtra(KEY_MATCH_END_TIME, matchEndTime);
    intent.putExtra(KEY_JOIN_MATCH_UNITS, joinMatchUnits);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_association_match;
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    Intent intent = getIntent();
    if (intent == null) {
      finish();
      return;
    }
    String title = intent.getStringExtra(KEY_TITLE);
    String sysMatchId = intent.getStringExtra(KEY_SYS_MATCH_ID);
    String sysSysMatchId = intent.getStringExtra(KEY_SYS_SYS_MATCH_ID);
    String matchStageId = intent.getStringExtra(KEY_MATCH_STAGE_ID);
    int isQuartets = intent.getIntExtra(KEY_IS_QUARTETS, 0);
    long matchStartTime = intent.getLongExtra(KEY_MATCH_START_TIME, 0);
    long matchEndTime = intent.getLongExtra(KEY_MATCH_END_TIME, 0);;
    String joinMatchUnits = intent.getStringExtra(KEY_JOIN_MATCH_UNITS);

    FragmentManager fragmentManager = getSupportFragmentManager();
    AssociationMatchFragment fragment = (AssociationMatchFragment)fragmentManager.findFragmentById(R.id.mainFragment);
    //这里需要加上
    if (fragment == null) {
      finish();
      return;
    }
    fragment.setRankMatchParams(title, sysSysMatchId, sysMatchId, matchStageId, isQuartets, matchStartTime, matchEndTime, joinMatchUnits);
  }

  @Override
  public void onBackPressed() {
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
      AssociationMatchFragment fragment = (AssociationMatchFragment)fragmentManager.findFragmentById(R.id.mainFragment);
      commonDialog.dismiss();
      //这里需要加上
      if (fragment == null) {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH_RANK_LIST));
        finish();
        return;
      }
      fragment.uploadSurplusData();
      finish();
    });
  }

}
