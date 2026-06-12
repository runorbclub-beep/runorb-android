package com.cloud.runball.module.mine;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.cloud.runball.App;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.SplashActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.constant.PlayingDataConstant;
import com.cloud.runball.dialog.CommonDialog;
import com.cloud.runball.module.WebActivity;
import com.cloud.runball.module.WristBallActivity;
import com.cloud.runball.module.free_style.FreeStyleFragment;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.bean.CountryCodeInfo;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivitySettingBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: SettingActivity
 * @Description: 设置
 * @Author: zhd
 * @CreateDate: 2021/2/9 11:25
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/9 11:25
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SettingActivity extends BaseActivity {

  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();


  public static final int LoginOtherActivity_result=101;

  private ActivitySettingBinding binding;

  RelativeLayout ryNotify;
  CheckBox cbSelected;
  RelativeLayout ryUserInfo;
  ImageView img_user_more;
  RelativeLayout ryAccount;
  ImageView img_account_more;
  RelativeLayout ryPrivacy;
  RelativeLayout ryLanguage;
  ImageView img_privacy_more;
  RelativeLayout ryService;
  ImageView img_service_more;
  RelativeLayout ryServer;
  ImageView img_server_more;
  RelativeLayout ryFeedback;
  ImageView img_feedback_more;
  ImageView img_language_more;
  TextView tvServer;
  RelativeLayout ryAbout;
  TextView tv_about_more;
  Button btnExit;
  TextView tvUserInfo;
  View ryCancelAccount;


  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_setting;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivitySettingBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_setting);
  }

  @Override
  protected void initView() {
    ryNotify = binding.ryNotify;
    cbSelected = binding.cbSelected;
    ryUserInfo = binding.ryUserInfo;
    img_user_more = binding.imgUserMore;
    ryAccount = binding.ryAccount;
    img_account_more = binding.imgAccountMore;
    ryPrivacy = binding.ryPrivacy;
    ryLanguage = binding.ryLanguage;
    img_privacy_more = binding.imgPrivacyMore;
    ryService = binding.ryService;
    img_service_more = binding.imgServiceMore;
    ryServer = binding.ryServer;
    img_server_more = binding.imgServerMore;
    ryFeedback = binding.ryFeedback;
    img_feedback_more = binding.imgFeedbackMore;
    img_language_more = binding.imgLanguageMore;
    tvServer = binding.tvServer;
    ryAbout = binding.ryAbout;
    tv_about_more = binding.tvAboutMore;
    btnExit = binding.btnExit;
    tvUserInfo = binding.tvUserInfo;
    ryCancelAccount = binding.ryCancelAccount;

    String str = String.format(getString(R.string.lbl_about_ver), AppUtils.getVersionName(this));
    tv_about_more.setText(str);

    // Replace @OnClick with listeners
    ryNotify.setOnClickListener(this::onClick);
    ryUserInfo.setOnClickListener(this::onClick);
    img_user_more.setOnClickListener(this::onClick);
    ryAccount.setOnClickListener(this::onClick);
    img_account_more.setOnClickListener(this::onClick);
    ryPrivacy.setOnClickListener(this::onClick);
    img_privacy_more.setOnClickListener(this::onClick);
    ryService.setOnClickListener(this::onClick);
    img_service_more.setOnClickListener(this::onClick);
    ryFeedback.setOnClickListener(this::onClick);
    img_feedback_more.setOnClickListener(this::onClick);
    ryAbout.setOnClickListener(this::onClick);
    tv_about_more.setOnClickListener(this::onClick);
    btnExit.setOnClickListener(this::onClick);
    img_language_more.setOnClickListener(this::onClick);
    ryLanguage.setOnClickListener(this::onClick);
    ryServer.setOnClickListener(this::onClick);
    img_server_more.setOnClickListener(this::onClick);
    ryCancelAccount.setOnClickListener(this::onClick);
  }

  @Override
  public void onResume(){
    super.onResume();
    Integer serverType = (Integer) SPUtils.get(this, "server", "googleplay".equals(BuildConfig.FLAVOR) ? Constant.NATION_SERVER_TYPE : Constant.CHINA_SERVER_TYPE);
    if(serverType.intValue() == Constant.CHINA_SERVER_TYPE) {
      tvServer.setText(getString(R.string.lbl_server_china));
    }else{
      tvServer.setText(getString(R.string.lbl_server_nation));
    }

    if(AppDataManager.getInstance().getUserInfoModel() != null) {
      //没有手机号或邮箱
      if("游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())) {
        btnExit.setText(R.string.login);
      }else{
        ryCancelAccount.setVisibility(View.VISIBLE);
        btnExit.setText(R.string.lbl_exit);
      }

      if(AppDataManager.getInstance().getUserInfoModel().getUser_info() != null) {
        int is_group = AppDataManager.getInstance().getUserInfoModel().getUser_info().getIs_group();
        if(is_group == 1) {
          tvUserInfo.setText(getString(R.string.lbl_group_info));
        }else if(is_group == -1) {
          if(!"游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())) {
            startActivity(new Intent(this, InfoActivity.class));
          }
        }
      }
    } else {
      btnExit.setText(R.string.login);
    }
  }

  @Override
  protected void setOnResult() {

  }

  public void onClick(View v) {
    if(v.getId() == R.id.img_language_more || v.getId() == R.id.img_language_more || v.getId()==R.id.ryLanguage) {
      //语言选择
      startActivity(new Intent(this, LanguageActivity.class));
    }else if(v.getId() == R.id.ryUserInfo || v.getId() == R.id.img_user_more) {
      //个人信息
      if(isOfficialUser()) {
        startActivity(new Intent(this, UserInfoActivity.class));
      }else{
        Intent it=new Intent(this, LoginOtherActivity.class);
        startActivity(it);
      }
    }else if(v.getId() == R.id.ryAccount || v.getId() == R.id.img_account_more) {
      //账号管理
      startActivity(new Intent(this, AccountActivity.class));
    }else if(v.getId() == R.id.ryPrivacy || v.getId() == R.id.img_privacy_more) {
      //隐私服务
      startActivity(getString(R.string.app_privacy_url),getString(R.string.lbl_privacy));
    }else if(v.getId() == R.id.ryService || v.getId() == R.id.img_service_more) {
      //服务条款
      startActivity(getString(R.string.app_protocol_url),getString(R.string.lbl_service));
    }else if(v.getId() == R.id.ryFeedback || v.getId() == R.id.img_feedback_more) {
      //反馈与帮助
      startActivity(new Intent(this, FeedBackActivity.class));
    }else if(v.getId() == R.id.ryAbout || v.getId() == R.id.tv_about_more) {
      //关于
      startActivity(new Intent(this, AboutActivity.class));
    }else if(v.getId() == R.id.btnExit) {
      exitHandler();
    }else if(v.getId() == R.id.ryServer || v.getId() == R.id.img_server_more) {
      //服务器
      startActivity(new Intent(this,ServerActivity.class));
    } else if (v.getId() == R.id.ryCancelAccount) { // 注销账号
      CommonDialog dialog = new CommonDialog(this);
      dialog.setContent(getString(R.string.tip_cancel_account_title), getString(R.string.tip_cancel_account));
      dialog.addBtn(getString(R.string.btn_cancel), commonDialog -> {
        commonDialog.dismiss();
      });
      dialog.addBtn(getString(R.string.btn_ok), commonDialog -> {
        cancelAccount(commonDialog);
      });
    }
  }

  private void cancelAccount(Dialog dialog) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ResponseBody> observable = apiServer.accountCancel();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
              @Override
              public void onSuccess(ResponseBody responseBody) {
                try{
                  JSONObject jsonObject = new JSONObject(responseBody.string());
                  if(jsonObject.optInt("code",0) == 1) {
                    if (dialog != null && dialog.isShowing()) {
                      dialog.dismiss();
                    }
                    confirmExit();
                  } else {
                    Toast.makeText(SettingActivity.this, R.string.tip_api_error, Toast.LENGTH_SHORT).show();
                  }
                } catch (Exception e){
                  Toast.makeText(SettingActivity.this, R.string.tip_api_error, Toast.LENGTH_SHORT).show();
                }
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(SettingActivity.this, R.string.tip_api_error, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  /**
   * 是否正式用户
   * @return
   */
  private boolean isOfficialUser(){
    if(AppDataManager.getInstance().getUserInfoModel()!=null){
      if(AppDataManager.getInstance().getUserInfoModel().getUser_info()!=null){
        if(!"游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())){
          return true;
        }
      }
    }
    return false;
  }

  private void exitHandler(){
    if(AppDataManager.getInstance().getUserInfoModel()!=null){
      if(!"游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())){
        //退出,弹出手机号页面，然后清理token
        showDialog(getString(R.string.tip),getString(R.string.lbl_exit_message), null, new OnConfirmListener(){
          @Override
          public void onConfirm() {
            confirmExit();
          }
        });
      }else{
        startLoginOtherActivity();
      }
    }else{
      startLoginOtherActivity();
    }
  }

  private void confirmExit() {
    AppDataManager.getInstance().setUserInfoModel(null);
    SPUtils.remove(getApplicationContext(), "token");
    SPUtils.remove(getApplicationContext(),"pkdata");
    SPUtils.remove(getApplicationContext(),"pkdata_startTime");
    SPUtils.remove(getApplicationContext(),"pkdata_keepPlayTime");
    SPUtils.putData(SettingActivity.this, "bleDeviceList", null);

    sqlService.deletePlayInfo();
    sqlService.deleteSpeedDetail();

    ryCancelAccount.setVisibility(View.GONE);
//    EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));

    //新增
    btnExit.setText(R.string.login);
    if(AppDataManager.getInstance().getUserInfoModel()!=null){
      if(!"游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())){
        btnExit.setText(R.string.lbl_exit);
      }
    }else{
      autoLogin();
    }
    startLoginOtherActivity();
  }


  private void startActivity(String url, String title) {
    Intent it = new Intent(this, WebActivity.class);
    it.putExtra("url", url);
    it.putExtra("title", title);
    startActivity(it);
  }

  private void startLoginOtherActivity(){
    Intent it=new Intent(this, LoginOtherActivity.class);
    it.putExtra("resultCode",true);
    startActivityLaunch.launch(it);
  }


  ActivityResultLauncher<Intent> startActivityLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    int resultCode=result.getResultCode();
    if(resultCode == LoginOtherActivity_result) {
      btnExit.setText(R.string.login);
      if(AppDataManager.getInstance().getUserInfoModel()!=null){
        if(!"游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())){
          btnExit.setText(R.string.lbl_exit);
        }
        //获取用户信息
        requestUserInfo();
      }else{
        autoLogin();
      }
    }
  });

  private void requestUserInfo() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<UserInfoModel> observable = apiServer.getUserInfo();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
          @Override
          public void onSuccess(UserInfoModel userInfoModel) {
            if(!isFinishing()){
              AppLogger.d("--SettingActivity--获取个人信息成功----");
              AppDataManager.getInstance().setUserInfoModel(userInfoModel);
              WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
              SPUtils.put(getApplication(), "token", userInfoModel.getUser_info().getToken());
            }
            EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
          }

          @Override
          public void onError(int code, String msg) {

          }
        })
    );
  }

  /**
   * 自动登录
   */
  private void autoLogin() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(2);
    map.put("sys_country", AppDataManager.getInstance().getCountry());
    map.put("device_uid", AppDataManager.getInstance().getAndroidId());
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserInfoModel> observable = apiServer.autoLogin(requestBody);

    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
          @Override
          public void onSuccess(UserInfoModel userInfoModel) {
            //把token保存起来
            if(userInfoModel!=null){
              if(userInfoModel.getCode() != 0) {
                Logger.d("---autoLogin--token=" + userInfoModel.getUser_info().getToken());
                SPUtils.put(getApplication(), "token", userInfoModel.getUser_info().getToken()+"");
                AppDataManager.getInstance().setUserInfoModel(userInfoModel);
                WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken()+"");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
            }
          }

          @Override
          public void onError(int code, String msg) {
            //Logger.d(msg);
          }
        })
    );
  }

}
