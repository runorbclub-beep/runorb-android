package com.cloud.runball;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.cloud.runball.basecomm.app.IApplication;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.model.AdModel;
import com.cloud.runball.module.WristBallActivity;
import com.cloud.runball.module.WebActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.dialog.AgreementDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.QMUITouchableSpan;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.cloud.runball.databinding.ActivitySplashBinding;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ns467
 */
public class SplashActivity extends BaseActivity {
  private ActivitySplashBinding binding;
  ConstraintLayout lyRoot;
  ImageView ivSplash;
  ImageView ivSplashLogo;
  TextView tvSlogan;
  ImageView ivDynamicBall;

  private final int REQUEST_PHONE_STATE = 100;
  private int START_DELAY = 1000;
  private final int START_DELAY_NOW = 0;
  private boolean isStartNow = false;
  private String language = "zh";

  @Override
  protected int onLayoutId() {
    return R.layout.activity_splash;
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    // Bind ViewBinding to the current content view
    binding = ActivitySplashBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    lyRoot = binding.lyRoot;
    ivSplash = binding.ivSplash;
    ivSplashLogo = binding.ivSplashLogo;
    tvSlogan = binding.tvSlogan;
    ivDynamicBall = binding.ivDynamicBall;
    Glide.with(this).load(R.drawable.ball_dynamic).into(ivDynamicBall);

    // 创建透明动画效果，透明度是0~1
    AlphaAnimation alpha = new AlphaAnimation(0, 1);
    // 设置动画时间 长度，单位毫秒
    alpha.setDuration(START_DELAY);
    tvSlogan.startAnimation(alpha);

    isFirstUse = (boolean) SPUtils.get(this, "isFirstUse", true);

    // 获得语言码 zh en ja
    Locale locale = getResources().getConfiguration().locale;
    language = locale.getLanguage();
    AppDataManager.getInstance().setLanguage(language);
//    String android_id = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//    AppDataManager.getInstance().setAndroidId(android_id);
    WristBallRetrofitHelper.getInstance().updateLanguage(language);

    //显示splash图片
    String tempFilePath = (String) SPUtils.get(getApplicationContext(), "splash", "");
    if(!TextUtils.isEmpty(tempFilePath)) {
      File file = new File(tempFilePath);
      if(file.isFile() && file.exists()) {
        Glide.with(this).load(file).into(ivSplash);
        ivSplashLogo.setVisibility(View.GONE);
        START_DELAY = 2500;
      }
    } else {
      START_DELAY = 2500;
    }

    //判断不同渠道，如果是谷歌渠道，需隐藏与gcd相关内容，显示位置服务获取提示
    if(BuildConfig.FLAVOR.equalsIgnoreCase("googleplay")) {
      //英文屏蔽gcd首页图片
      this.findViewById(R.id.fySplash).setVisibility(View.GONE);
      if (isFirstUse) {
        View guide_layout = LayoutInflater.from(this).inflate(R.layout.guide_layout,null);
        guide_layout.findViewById(R.id.btnNext).setOnClickListener(v -> {
          isStartNow = true;
          lyRoot.removeView(guide_layout);
          getTransIntent();
          avoidLauncherAgain();
        });
        guide_layout.findViewById(R.id.btnCancel).setOnClickListener(v -> {
          finish();
        });
        lyRoot.addView(guide_layout);
        return;
      }
    }

    getTransIntent();
    avoidLauncherAgain();
  }

  public void getTransIntent() {
    Intent intent = getIntent();
    String scheme = intent.getScheme();
    String dataString = intent.getDataString();

    //Logger.d(scheme+";"+dataString);

    Uri uri = intent.getData();
    if (uri != null) {
      //完整的url信息
      String url = uri.toString();
      //scheme部分
      String schemes = uri.getScheme();
      //host部分
      String host = uri.getHost();
      //port部分
      int port = uri.getPort();
      //访问路径
      String path = uri.getPath();
      //编码路径
      String path1 = uri.getEncodedPath();
      //query部分
      String queryString = uri.getQuery();
      //获取参数值
      String systemInfo = uri.getQueryParameter("page_id");

      try {
        if (systemInfo.equalsIgnoreCase("100")) {
          String tempUrl = uri.getQueryParameter("url");
          Intent it = new Intent(SplashActivity.this, WebActivity.class);
          it.putExtra("url", URLDecoder.decode(tempUrl, "UTF-8"));
          startActivity(it);
          finish();
        } else {
          //其他连接
          delayStart();
        }
      } catch (UnsupportedEncodingException e) {
        delayStart();
      }
    } else {
      delayStart();
    }
  }


  private boolean isFirstUse;
  Handler mHandler = new Handler();
  private void delayStart() {
    if (isFirstUse) {
      String content = getString(R.string.lbl_privacy_content);
      SpannableString spStr = generateSp(content);
      new AgreementDialog(this, spStr, null).setOnClickListener(v -> {
        switch (v.getId()) {
          case R.id.tv_dialog_ok:
            ((IApplication)getApplication()).lazyInit(getApplication());
            requirePermission();
            break;
          case R.id.tv_dialog_no:
            finish();
            break;
        }
      }).show();
    } else {
      //已经看过弹框，则跳转到主页面
      mHandler.postDelayed(() -> {
        startMain();
      }, isStartNow ? START_DELAY_NOW : START_DELAY);
    }
  }

  private void startMain() {
    SPUtils.put(getApplication(), "isFirstUse", false);
    WristBallActivity.startAction(SplashActivity.this, isFirstUse, 1);
    finish();
  }

  private SpannableString generateSp(String text) {
    //定义需要操作的内容
    String high_light_1 = getString(R.string.lbl_privacy_1);
    String high_light_2 = getString(R.string.lbl_privacy_2);

    SpannableString spannableString = new SpannableString(text);
    //初始位置
    int start = 0;
    //结束位置
    int end;
    int index;
    //indexOf(String str, int fromIndex): 返回从 fromIndex 位置开始查找指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1。
    //简单来说，(index = text.indexOf(high_light_1, start)) > -1这部分代码就是为了查找你的内容里面有没有high_light_1这个值的内容，并确定它的起始位置
    while ((index = text.indexOf(high_light_1, start)) > -1) {
      //结束的位置
      end = index + high_light_1.length();
      spannableString.setSpan(new QMUITouchableSpan(this.getResources().getColor(R.color.google_blue), this.getResources().getColor(R.color.google_blue),
          this.getResources().getColor(R.color.white), this.getResources().getColor(R.color.white)) {
        @Override
        public void onSpanClick(View widget) {
          startActivity(getString(R.string.app_protocol_url), high_light_1);
          AppLogger.d("点击用户协议的相关操作");
        }
      }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
      start = end;
    }

    start = 0;
    while ((index = text.indexOf(high_light_2, start)) > -1) {
      end = index + high_light_2.length();
      spannableString.setSpan(new QMUITouchableSpan(this.getResources().getColor(R.color.google_blue), this.getResources().getColor(R.color.google_blue),
          this.getResources().getColor(R.color.white), this.getResources().getColor(R.color.white)) {
        @Override
        public void onSpanClick(View widget) {
          startActivity(getString(R.string.app_privacy_url), high_light_2);
          AppLogger.d("点击隐私政策的相关操作");
        }
      }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
      start = end;
    }
    return spannableString;
  }


  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    //Uri uri = intent.getData();
    //一般可以自己管理

    // 此处要调用，否则App在后台运行时，会无法截获
    //MobclickAgent.handleUMLinkURI(this, intent.getData(), umlinkAdapter);
  }


  private void avoidLauncherAgain() {
    // 判断当前activity是不是所在任务栈的根
    if (!this.isTaskRoot()) {
      Intent intent = getIntent();
      if (intent != null) {
        String action = intent.getAction();
        if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
          finish();
        }
      }
    }
  }

  @Override
  public void onDestroy(){
    super.onDestroy();
    mHandler.removeCallbacksAndMessages(null);
    mHandler=null;
  }

  private boolean schemeValid() {
    PackageManager manager = getPackageManager();
    Intent action = new Intent(Intent.ACTION_VIEW);
    action.setData(Uri.parse("runball://cloud.runball.bazu/query?page_id=1"));
    List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
    return list != null && list.size() > 0;
  }

  private void startActivity(String url, String title) {
    Intent it = new Intent(this, WebActivity.class);
    it.putExtra("url", url);
    it.putExtra("title", title);
    startActivity(it);
  }


  List<String> mPermission=new ArrayList<>();

  private void requirePermission() {
    //需要动态获取权限
    String[] permissions=new String[]{
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    mPermission.clear();
    for (int i = 0; i < permissions.length; i++) {
      if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
        mPermission.add(permissions[i]);
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_PHONE_STATE);
    }else {
      startMain();
    }
  }


  /**
   * 加个获取权限的监听
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 3 && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
      AppLogger.d("onRequestPermissionsResult=" + System.currentTimeMillis());
    }
    startMain();
  }

}
