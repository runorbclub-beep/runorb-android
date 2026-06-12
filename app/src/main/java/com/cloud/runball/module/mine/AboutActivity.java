package com.cloud.runball.module.mine;

import android.content.Intent;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud.runball.App;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.cloud.runball.module.WebActivity;
import com.cloud.runball.databinding.ActivityAboutBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: AboutActivty
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/9 14:01
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/9 14:01
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AboutActivity extends BaseActivity {

  private ActivityAboutBinding binding;

  TextView tvVersion;
  TextView tvCopyEmail;
  ImageView img_notify_more;
  TextView tvEmail;
  TextView tvHost;
  RelativeLayout ryUserInfo;
  RelativeLayout ryNotify;


  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_about;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityAboutBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {

  }

  @Override
  protected void initView() {
    tvVersion = binding.tvVersion;
    tvCopyEmail = binding.tvCopyEmail;
    img_notify_more = binding.imgNotifyMore;
    tvEmail = binding.tvEmail;
    tvHost = binding.tvHost;
    ryUserInfo = binding.ryUserInfo;
    ryNotify = binding.ryNotify;

    tvVersion.setText("V" + BuildConfig.VERSION_NAME);

    // Replace @OnClick with listeners
    tvCopyEmail.setOnClickListener(this::onClick);
    img_notify_more.setOnClickListener(this::onClick);
    ryUserInfo.setOnClickListener(this::onClick);
    ryNotify.setOnClickListener(this::onClick);
  }

  @Override
  protected void setOnResult() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_about);
  }


  public void onClick(View v) {
    if(v.getId()==R.id.tvCopyEmail || v.getId()==R.id.ryUserInfo){
      String email=tvEmail.getText().toString().trim();
      AppUtils.copyStringToClipboard(getApplication(),email);
      Toast.makeText(App.self().getApplicationContext(),email,Toast.LENGTH_SHORT).show();
    }else if(v.getId()==R.id.img_notify_more || v.getId()==R.id.ryNotify){
      String host=tvHost.getText().toString().trim();
      startActivity(host,getResources().getString(R.string.app_name));
    }
  }

  private void startActivity(String url, String title) {
    Intent it = new Intent(this, WebActivity.class);
    it.putExtra("url", url);
    it.putExtra("title", title);
    startActivity(it);
  }
}
