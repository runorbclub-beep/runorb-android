package com.cloud.runball.module.match;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;

public class MatchActivity extends BaseActivity {

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_match;
  }

  @Override
  protected void addListener() {

  }

  @Override
  protected void initView() {

  }

  @Override
  protected void setOnResult() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_go_module_events);
  }

}
