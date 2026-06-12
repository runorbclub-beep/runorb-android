package com.cloud.runball.module.pk;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;

public class PkActivity extends BaseActivity {

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_pk;
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
    return getString(R.string.title_go_module_pk);
  }

}
