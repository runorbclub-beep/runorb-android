package com.cloud.runball.module;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cloud.runball.App;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.cloud.runball.basecomm.utils.LaunchApp;
import com.cloud.runball.basecomm.utils.PermissionWallUtils;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.CountryCodeInfo;
import com.cloud.runball.bean.UserInfo;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.dialog.CommonDialog;
import com.cloud.runball.dialog.SexChangedDialog;
import com.cloud.runball.module.mine.MineFragment;
import com.cloud.runball.model.AdModel;
import com.cloud.runball.model.CheatModel;
import com.cloud.runball.model.DeviceWithServerModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.go.GoFragment;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.module.race.MatchMainActivity;
import com.cloud.runball.module.rank.RankFragment;
import com.cloud.runball.module_bluetooth.service.BleService;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.SpeechUtils;
import com.cloud.runball.service.websocket.WebSocketServiceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.cloud.runball.databinding.WristballLayoutBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class WristBallActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

  static final int RE_LOGIN = 2;

  private RankFragment rankFragment;
  private GoFragment goFragment;
  private MineFragment mineFragment;

  private FragmentManager fragmentManager;

  private WristballLayoutBinding binding;
  TextView rbRank;
  TextView rbGo;
  TextView rbMine;

  private @IdRes int mCheckedId = R.id.rbGo;

  private long mExitTime;

  public static void startAction(Context context, boolean isFirst, int tabIndex) {
    Intent intent = new Intent(context, WristBallActivity.class);
    intent.putExtra("isFirst", isFirst);
    intent.putExtra("tabIndex", tabIndex);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.wristball_layout;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = WristballLayoutBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    setEmptyStatusBar();
    rbRank = binding.rbRank;
    rbGo = binding.rbGo;
    rbMine = binding.rbMine;
    // Replace @OnClick with listeners
    rbRank.setOnClickListener(this::onViewClicked);
    rbGo.setOnClickListener(this::onViewClicked);
    rbMine.setOnClickListener(this::onViewClicked);
    startService(new Intent(this, BleService.class));

    String android_id = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    AppDataManager.getInstance().setAndroidId(android_id);

    Intent intent = getIntent();
    int tabIndex = 0;
    if (intent != null) {
      boolean isFirst = intent.getBooleanExtra("isFirst", false);
      tabIndex = intent.getIntExtra("tabIndex", 0);
      if (isFirst) {
        Intent it = new Intent(this, LoginOtherActivity.class);
        it.putExtra("resultCode",true);
        startActivity(it);
      }
    }

    //获取管理者
    fragmentManager = getSupportFragmentManager();

    rankFragment = RankFragment.newInstance();
    goFragment = GoFragment.newInstance();
    mineFragment = MineFragment.newInstance();

    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.add(R.id.layFragment, rankFragment, RankFragment.class.getSimpleName()).hide(rankFragment).setMaxLifecycle(rankFragment, Lifecycle.State.CREATED);
    transaction.add(R.id.layFragment, goFragment, GoFragment.class.getSimpleName()).hide(goFragment).setMaxLifecycle(goFragment, Lifecycle.State.CREATED);
    transaction.add(R.id.layFragment, mineFragment, MineFragment.class.getSimpleName()).hide(mineFragment).setMaxLifecycle(mineFragment, Lifecycle.State.CREATED);
    transaction.commit();

    if (tabIndex == 0) {
      showFragment(R.id.rbRank);
    } else if (tabIndex == 1) {
      showFragment(R.id.rbGo);
    } else if (tabIndex == 2) {
      showFragment(R.id.rbMine);
    }

    String token = String.valueOf(SPUtils.get(this, "token", ""));
    if (TextUtils.isEmpty(token)) {
      autoLogin(AppDataManager.getInstance().getCountry(), android_id);
    } else {
      WristBallRetrofitHelper.getInstance().updateToken(token);
      requestUserInfo();
      requestCheatConfig();
    }
    onRequestCountryCode();

    // 获得语言码 zh en en-rUs ja
    Locale locale = getResources().getConfiguration().locale;
    String language = locale.getLanguage();
    AppDataManager.getInstance().setLanguage(language);

    //设置语音播报语言
    SpeechUtils.getInstance(getApplicationContext()).speakLanguage(locale);

    WristBallRetrofitHelper.getInstance().updateLanguage(language);




    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
      showDialogWithExit(getString(R.string.tip), getString(R.string.no_support_ble));
    }

    if(!"googleplay".equals(BuildConfig.FLAVOR)) {
      new Handler().postDelayed(() -> {
        requestCheckUpdate();
      },100);
    }

    if (AppDataManager.getInstance().getUserInfoModel() != null) {
      UserInfo userInfo = AppDataManager.getInstance().getUserInfoModel().getUser_info();
      if(userInfo == null){
        return;
      }
      if("游客".equals(userInfo.getUser_type_name())) {
        return;
      }
      if (userInfo.getSys_sex_id() == null || "".equals(userInfo.getSys_sex_id()) || SexConstant.SEX_UNKNOWN.equals(userInfo.getSys_sex_id())) {
        SexChangedDialog sexChangedDialog = new SexChangedDialog(this);
        sexChangedDialog.setCallback((dialog, sex) -> {
          notifyUserInfo(
              userInfo.getUser_name(),
              userInfo.getSelf_description(),
              userInfo.getAddress(),
              userInfo.getAddress_detail(),
              userInfo.getBirthday(),
              userInfo.getIs_group(),
              userInfo.getIs_yang(),
              sex
          );
          dialog.dismiss();
        });
      }
    }
  }

  private void notifyUserInfo(String nickname, String sign, String address, String address_json, String birthday, int is_group, int user_age_type, String sexId){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_name", nickname);
    map.put("self_description", sign);
    map.put("address", address);
    map.put("address_json", address_json);
    map.put("birthday", birthday);
    map.put("is_group", is_group);
    if(is_group == 1) {
      map.put("user_age_type", user_age_type);
    }
    map.put("sys_sex_id", sexId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());

    Observable<ResponseBody> observable = apiServer.modifyUserInfo(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
              @Override
              public void onSuccess(ResponseBody o) {
                try{
                  Logger.d("----修改个人信息成功----"+o.string());
                  UserInfoModel model= AppDataManager.getInstance().getUserInfoModel();
                  if(model != null && model.getUser_info() != null){
                    AppDataManager.getInstance().getUserInfoModel().getUser_info().setAddress(address);
                    AppDataManager.getInstance().getUserInfoModel().getUser_info().setBirthday(birthday);
                    AppDataManager.getInstance().getUserInfoModel().getUser_info().setUser_name(nickname);
                    AppDataManager.getInstance().getUserInfoModel().getUser_info().setSelf_description(sign);
                    //新增
                    AppDataManager.getInstance().getUserInfoModel().getUser_info().setIs_group(is_group);
                    AppDataManager.getInstance().getUserInfoModel().getUser_info().setIs_yang(user_age_type);

                    AppDataManager.getInstance().setUserInfoModel(model);
                  }
                  Toast.makeText(WristBallActivity.this, R.string.nodifySuccess, Toast.LENGTH_LONG).show();
                }catch (Exception ex){
                  ex.printStackTrace();
                }
              }
              @Override
              public void onError(int code, String msg) {
                Logger.d(msg);
              }
            }
        )
    );
  }


  private void showFragment(@IdRes int checkedId) {
    changeResTab(checkedId);
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    switch (checkedId) {
      case R.id.rbRank: {
        fragmentTransaction.show(rankFragment).setMaxLifecycle(rankFragment, Lifecycle.State.RESUMED).hide(goFragment).hide(mineFragment).commit();
      } break;
      case R.id.rbGo: {
        fragmentTransaction.show(goFragment).setMaxLifecycle(goFragment, Lifecycle.State.RESUMED).hide(rankFragment).hide(mineFragment).commit();
      } break;
      case R.id.rbMine: {
        fragmentTransaction.show(mineFragment).setMaxLifecycle(mineFragment, Lifecycle.State.RESUMED).hide(goFragment).hide(rankFragment).commit();
      } break;
    }
  }

  private void changeResTab(@IdRes int checkedId) {
    rbRank.setTextColor(getResources().getColor(R.color.tabUnSelectedTextColor));
    rbRank.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ic_tab_rank_1), null, null);

    rbGo.setTextColor(getResources().getColor(R.color.tabUnSelectedTextColor));
    rbGo.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ic_tab_go_1), null, null);

    rbMine.setTextColor(getResources().getColor(R.color.tabUnSelectedTextColor));
    rbMine.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ic_tab_me_1), null, null);

    switch (checkedId) {
      case R.id.rbRank:
        rbRank.setTextColor(getResources().getColor(R.color.tabSelectedTextColor));
        rbRank.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ic_tab_rank_2) , null, null);
        break;
      case R.id.rbGo:
        rbGo.setTextColor(getResources().getColor(R.color.tabSelectedTextColor));
        rbGo.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ic_tab_go_2) , null, null);
        break;
      case R.id.rbMine:
        rbMine.setTextColor(getResources().getColor(R.color.tabSelectedTextColor));
        rbMine.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.ic_tab_me_2), null, null);
        break;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    AppLogger.d("-------WristBallActivity.-onResume--------");
    requestSplashAdData();
  }

  public void showPermissionDialog(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setCancelable(false);
    builder.setNegativeButton(R.string.btn_cancel, (dialogInterface, i) -> {

    });
    builder.setPositiveButton(R.string.btn_confirm, (dialogInterface, i) -> {
      PermissionWallUtils.startPermissionSetting();
    });
    builder.show();
  }

  private void showDialogWithExit(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setCancelable(false);
    builder.setPositiveButton(R.string.btn_confirm, (dialogInterface, i) -> {
      android.os.Process.killProcess(android.os.Process.myPid());
    });
    builder.show();
  }

  public static final int ALBUM_CODE = 101;

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode== LoginOtherActivity.LoginOtherActivity_result && resultCode == LoginOtherActivity.LoginOtherActivity_result){
      AppLogger.d("-------------------onActivityResult------------------LoginOtherActivity_result="+LoginOtherActivity.LoginOtherActivity_result);
      showFragment(mCheckedId);
      requestUserInfo();
      //这里也需要判断是否有保存PK信息，有则跳转到比赛页面进行断线重连
      String pkdata = (String) SPUtils.get(this,"pkdata","");
      if(!TextUtils.isEmpty(pkdata) && pkdata.startsWith("{") && pkdata.endsWith("}")){
        startMatchMainActivity(pkdata);
      }
    }
    mineFragment.onActivityResult(requestCode, resultCode, data);
    rankFragment.onActivityResult(requestCode, resultCode, data);
  }

  private void requestUserInfo() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<UserInfoModel> observable = apiServer.getUserInfo();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserInfoModel>() {
              @Override
              public void onSuccess(UserInfoModel userInfoModel) {
                Logger.d("--WristBallActivity--获取个人信息成功----");
                AppDataManager.getInstance().setUserInfoModel(userInfoModel);
                //把token保存起来
                WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
                SPUtils.put(getApplication(), "token", userInfoModel.getUser_info().getToken());

                if (goFragment != null) {
                  goFragment.reUserPortrait();
                }

                changeUserDeviceInfo();
                getBleDeviceList();
              }

              @Override
              public void onError(int code, String msg) {
                Logger.d(msg);
              }
            }
        )
    );
  }

  /**
   * 请求开屏广告页图片
   */
  private void requestSplashAdData() {
    if(AppDataManager.getInstance().getSpalshDate() == null) {
      WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
      Observable<AdModel> observable = apiServer.advertising();
      disposable.add(
          observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
              .subscribeWith(new WristBallObserver<AdModel>() {
                @Override
                public void onSuccess(AdModel adModel) {
                  if(adModel != null) {
                    AppDataManager.getInstance().setAdSplashData(adModel);
                    loadAdSplashData();
                  }
                }
                @Override
                public void onError(int code, String msg) {

                }
              })
      );
    } else {
      loadAdSplashData();
    }
  }

  private void requestCheckUpdate(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

    HashMap<String, Object> map = new HashMap<>(2);
    map.put("android_code", AppUtils.getVersionCode(this));
    map.put("channel", BuildConfig.FLAVOR);

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());

    Observable<ResponseBody> observable = apiServer.checkupdate(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
              @Override
              public void onSuccess(ResponseBody responseBody) {
                try{
                  JSONObject jsonObject = new JSONObject(responseBody.string());
                  if(jsonObject.optInt("code",0) == 1) {
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    boolean is_update = dataObject.optBoolean("is_update",false);
                    if(is_update) {
                      //弹出升级框,用户选择并跳转
                      String msg = dataObject.optString("description","");
                      int strong = dataObject.optInt("is_strong_update",0);
                      if(strong > 0) {
                        //判断是否强制升级
                        CommonDialog dialog = new CommonDialog(WristBallActivity.this);
                        dialog.setContent(getString(R.string.update_tip), Html.fromHtml(msg));
                        dialog.addBtn(getString(R.string.btn_ok), commonDialog -> {
                          handlerChannel(BuildConfig.FLAVOR);
                        });
                      }else{
                        CommonDialog dialog = new CommonDialog(WristBallActivity.this);
                        dialog.setContent(getString(R.string.update_tip), Html.fromHtml(msg));
                        dialog.addBtn(getString(R.string.btn_ok), commonDialog -> {
                          handlerChannel(BuildConfig.FLAVOR);
                        });
                      }
                    }
                  }
                } catch (Exception e){
                  e.getMessage();
                }
              }

              @Override
              public void onError(int code, String msg) {

              }
            })
    );
  }

  private void handlerChannel(String channel){
    if(channel.equalsIgnoreCase("googleplay")) {
      //直接跳转到googleplay下载
      LaunchApp.openGooglePlay(this);
    }else{
      //不同渠道跳转到不同渠道下载
      String marketPkg=LaunchApp.YINGYONGBAO;
      if(channel.equalsIgnoreCase("yingyongbao")) {
        marketPkg = LaunchApp.YINGYONGBAO;
      }else if(channel.equalsIgnoreCase("xiaomi")) {
        marketPkg = LaunchApp.XIAOMI;
      }else if(channel.equalsIgnoreCase("qh360")) {
        marketPkg = LaunchApp.QIHOO;
      }else if(channel.equalsIgnoreCase("huawei")) {
        marketPkg = LaunchApp.HUAWEI;
      }else if(channel.equalsIgnoreCase("oppo")) {
        marketPkg = LaunchApp.OPPO;
      }else if(channel.equalsIgnoreCase("vivo")) {
        marketPkg = LaunchApp.VIVO;
      }else if(channel.equalsIgnoreCase("baidu")) {
        marketPkg = LaunchApp.BAIDU;
      }
      if(LaunchApp.isAvilible(this, marketPkg)) {
        LaunchApp.launchAppDetail(this, marketPkg);
      } else {
        LaunchApp.launchBrowser(this, "https://www.pgyer.com/JCVW");
      }
    }
  }

  private void loadAdSplashData() {
    AdModel model = AppDataManager.getInstance().getSpalshDate();
    if(model!=null && model.getImg_414_896() != null) {
      String splash_md5url = (String)SPUtils.get(getApplicationContext(), "splash_md5url", "");
      if(!splash_md5url.equals(AppUtils.md5(model.getImg_414_896()))) {
        //俩个url地址md5不同
        downImgToCache(this,model.getImg_414_896());
      }
      if(TextUtils.isEmpty(model.getImg_414_896())) {
        SPUtils.remove(getApplicationContext(), "splash_md5url");
        SPUtils.remove(getApplicationContext(), "splash");
      }
    }
    downImgToCache(this,model.getImg_414_896());
  }

  private void downImgToCache(Context context, String url) {
    //如果是网络图片，抠图的结果，需要先保存到本地
    Glide.with(context)
        .downloadOnly()
        .load(url)
        .listener(new RequestListener<File>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
            //Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            return false;
          }

          @Override
          public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
            //Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
            //saveToAlbum(context, resource.getAbsolutePath());
            saveSplashData(url, resource.getAbsolutePath());
            AppLogger.d("------下载成功onResourceReady------"+resource.getAbsolutePath());
            return false;
          }
        })
        .preload();
  }

  private void saveSplashData(String url,String filePath) {
    String urlMD5 = AppUtils.md5(url);
    String splash_md5url = (String)SPUtils.get(getApplicationContext(),"splash_md5url","");
    if(!splash_md5url.equals(urlMD5)) {
      //需要更新图片(删除旧图片,然后更新新图片)
      String tempFilePath = (String)SPUtils.get(getApplicationContext(),"splash","");
      if(!TextUtils.isEmpty(tempFilePath)) {
        File file = new File(tempFilePath);
        if(file.isFile() && file.exists()) {
          file.delete();
        }
      }
      SPUtils.put(getApplicationContext(), "splash_md5url", urlMD5);
      SPUtils.put(getApplicationContext(), "splash", filePath);
    }
  }

  private void startMatchMainActivity(String pk_info) {
    Intent it = new Intent(this, MatchMainActivity.class);
    it.putExtra("pkdata", pk_info);
    startActivity(it);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    super.onKeyDown(keyCode, event);
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if ((System.currentTimeMillis() - mExitTime) > 2000) {
        //mExitTime的初始值为0，currentTimeMillis()肯定大于2000（毫秒），所以第一次按返回键的时候一定会进入此判断
        Toast.makeText(getApplication(), R.string.exit_app_tip, Toast.LENGTH_SHORT).show();
        mExitTime = System.currentTimeMillis();
      } else {
        stopService(new Intent(this, BleService.class));
        return true;
        //android.os.Process.killProcess(android.os.Process.myPid());
      }
      return false;
    }
    return false;
  }

  public void onViewClicked(View v) {
    if(v.getId() == R.id.rbRank) {
      showFragment(R.id.rbRank);
    } else if(v.getId() == R.id.rbGo) {
      showFragment(R.id.rbGo);
    } else if(v.getId() == R.id.rbMine) {
      showFragment(R.id.rbMine);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    AppLogger.d("----------------------WristBallActivity---onDestroy-------------------");
    WebSocketServiceManager.getInstance().closeConnect();
//    if(fyFragments != null){
//      fyFragments.removeAllViews();
//      fyFragments = null;
//    }
    fragmentManager = null;
  }

  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    showFragment(checkedId);
  }


  private void onRequestCountryCode() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ResponseBody> observable = apiServer.requestSmsCode();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try{
              JSONObject jsonObject=new JSONObject(responseBody.string());
              if(jsonObject.optInt("code")==1){
                Gson gson=new Gson();
                List<CountryCodeInfo> countryCodeInfos = gson.fromJson(jsonObject.optString("data"), new TypeToken<List<CountryCodeInfo>>(){}.getType());
                AppDataManager.getInstance().clearCountryCodes();
                AppDataManager.getInstance().putAllCountryCodes(countryCodeInfos);
              }
            }catch (Exception ex){
              ex.printStackTrace();
            }
          }
          @Override
          public void onError(int code, String msg) {
            AppLogger.d("-----国际码列表--------" + msg);
          }
        })
    );
  }

  /**
   * 获取作弊配置数据
   */
  private void requestCheatConfig(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    if(AppDataManager.getInstance().getErrSpeeds().size()<=0){
      Observable<CheatModel> observable = apiServer.cheat();
      disposable.add(
          observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<CheatModel>() {
            @Override
            public void onSuccess(CheatModel cheatModel) {
              if(!isFinishing()){
                if(cheatModel!=null){
                  if(AppDataManager.getInstance().getErrSpeeds().size()<=0){
                    AppDataManager.getInstance().addAllErrSpeeds(cheatModel.getErr_speed());
                  }
                  App.self().setCircleCount(cheatModel.getInit_circle_count());
                }
              }
            }
            @Override
            public void onError(int code, String msg) {
              //AppLogger.d(msg);
            }
          })
      );
    }
  }

  private void getBleDeviceList() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<List<DeviceWithServerModel>> observable = apiServer.getDevices();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<List<DeviceWithServerModel>>() {
          @Override
          public void onSuccess(List<DeviceWithServerModel> list) {
            if(list!=null){
              SPUtils.putData(WristBallActivity.this, "bleDeviceList", list);
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }

  private void changeUserDeviceInfo() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("version", BuildConfig.VERSION_CODE); // 版本号
    map.put("channel", BuildConfig.FLAVOR); // 渠道

    String deviceInfo = String.format(
        "sysversion = %s, brand = %s, phonetype = %s, systype = android",
        android.os.Build.VERSION.RELEASE,
        android.os.Build.BRAND,
        android.os.Build.MODEL
    );
    map.put("device_model", deviceInfo); // 设备型号 其他手机任何信息，系统版本，蓝牙版本之类的都拼接在设备型号

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.changeUserDeviceInfo(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody list) {

          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }

  private void autoLogin(String sys_country,String device_uid) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(2);
    map.put("sys_country", sys_country);
    map.put("device_uid", device_uid);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserInfoModel> observable = apiServer.autoLogin(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
          @Override
          public void onSuccess(UserInfoModel userInfoModel) {
            if(!isFinishing()){
              AppDataManager.getInstance().setUserInfoModel(userInfoModel);
              WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
            }
            SPUtils.putData(WristBallActivity.this, "bleDeviceList", null);
          }
          @Override
          public void onError(int code, String msg) {
            if(!isFinishing()){
              AppLogger.d(msg);
              if (code == RE_LOGIN) {
                WristBallRetrofitHelper.getInstance().updateToken(null);
                autoLogin(AppDataManager.getInstance().getCountry(),AppDataManager.getInstance().getAndroidId());
              }
            }
          }
        })
    );
  }

}
