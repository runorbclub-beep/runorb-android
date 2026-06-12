package com.cloud.runball.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.CountryCodeInfo;
import com.cloud.runball.bean.MedalInfo;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.bean.UserInfo;
import com.cloud.runball.model.DeviceWithServerModel;
import com.cloud.runball.model.MobileUserInfoModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.WebActivity;
import com.cloud.runball.module.mine.InfoActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.service.sql.AppDatabase;
import com.cloud.runball.service.sql.IApiSqlService;
import com.cloud.runball.utils.AppLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.cloud.runball.databinding.ActivityLoginMobileOtherBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


/**
 * @author ns467
 */
public class LoginOtherActivity extends BaseActivity {

  private final IApiSqlService sqlService = AppDatabase.getInstance().apiSqlService();

  public static final int LoginOtherActivity_result = 101;
  public static final int LoginOtherActivity_result2 = 102;

  private ActivityLoginMobileOtherBinding binding;
  View layContent;

  RadioGroup bgModeTab;

  RadioButton rbModePhone;

  RadioButton rbModeEmail;


  private int mode = mode_phone;
  private final static int mode_phone = 1;
  private final static int mode_email = 2;


  LinearLayout layEmail;

  EditText edtEmail;

  EditText edtSmsEmail;

  TextView tvSmsSendEmail;

  TextView btnSmsSendEmail;

  CountDownTimer emailTimer;



  LinearLayout layPhone;

  Button btnSend;

  EditText edtPhone;

  EditText edtSms;

  TextView tvSmsSend;

  TextView btnSmsSend;

  ImageView ivArrowDown;



  ImageView ivClose;

  TextView tvService;

  TextView tvPrivacy;

  CheckBox cbSelected;

  CountDownTimer phoneTimer;

  boolean resultCode = false;

  TextView tvNationCode;

  private final List<String> countryCodes = new ArrayList<>();
  private String selectCountryCode = "86";

  @Override
  protected int onLayoutId() {
    return R.layout.activity_login_mobile_other;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityLoginMobileOtherBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected View getImmersiveView() {
    return layContent;
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    // map views
    layContent = binding.layContent;
    bgModeTab = binding.bgModeTab;
    rbModePhone = binding.rbModePhone;
    rbModeEmail = binding.rbModeEmail;
    layEmail = binding.layEmail;
    edtEmail = binding.edtEmail;
    edtSmsEmail = binding.edtSmsEmail;
    tvSmsSendEmail = binding.tvSmsSendEmail;
    btnSmsSendEmail = binding.btnSmsSendEmail;
    layPhone = binding.layPhone;
    btnSend = binding.btnSend;
    edtPhone = binding.edtPhone;
    edtSms = binding.edtSms;
    tvSmsSend = binding.tvSmsSend;
    btnSmsSend = binding.btnSmsSend;
    ivArrowDown = binding.ivArrowDown;
    ivClose = binding.ivClose;
    tvService = binding.tvService;
    tvPrivacy = binding.tvPrivacy;
    cbSelected = binding.cbSelected;
    tvNationCode = binding.tvNationCode;

    setEmptyStatusBar();
    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
    resultCode = this.getIntent().getBooleanExtra("resultCode", false);

    Locale locale = getResources().getConfiguration().locale;
    String language = locale.getLanguage();
    if(!language.equalsIgnoreCase("zh")) {
      selectCountryCode = "1";
      tvNationCode.setText("+1");
    }else{
      selectCountryCode = "86";
      tvNationCode.setText("+86");
    }
    initCountryCodes();


    // Always show both phone and email login tabs
    bgModeTab.setOnCheckedChangeListener((group, checkedId) -> {
      switch (checkedId) {
        case R.id.rbModePhone:
          changeMode(mode_phone);
          break;
        case R.id.rbModeEmail:
          changeMode(mode_email);
          break;
      }
    });
    bgModeTab.check(R.id.rbModeEmail);

    // Replace @OnClick with listeners
    tvPrivacy.setOnClickListener(this::onClick);
    tvService.setOnClickListener(this::onClick);
    ivClose.setOnClickListener(this::onClick);
    btnSend.setOnClickListener(this::onClick);
    btnSmsSend.setOnClickListener(this::onClick);
    ivArrowDown.setOnClickListener(this::onClick);
    tvNationCode.setOnClickListener(this::onClick);
    btnSmsSendEmail.setOnClickListener(this::onClick);
  }

  private void changeMode(int mode) {
    this.mode = mode;
    switch (mode) {
      case mode_phone:
        layEmail.setVisibility(View.GONE);
        layPhone.setVisibility(View.VISIBLE);
        rbModePhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        rbModeEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        break;
      case mode_email:
        layEmail.setVisibility(View.VISIBLE);
        layPhone.setVisibility(View.GONE);
        rbModePhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        rbModeEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        break;
    }
  }

  private void initCountryCodes() {
    if (AppDataManager.getInstance().getCountryCodes() == null || AppDataManager.getInstance().getCountryCodes().size() == 0) {
      onRequestCountryCode();
    } else {
      fillCountryCodes();
    }
  }

  private void fillCountryCodes() {
    for(CountryCodeInfo countryCodeInfo: AppDataManager.getInstance().getCountryCodes()) {
      countryCodes.add(countryCodeInfo.getCode());
    }
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
              if(jsonObject.optInt("code") == 1) {
                Gson gson = new Gson();
                List<CountryCodeInfo> countryCodeInfos = gson.fromJson(jsonObject.optString("data"), new TypeToken<List<CountryCodeInfo>>(){}.getType());
                AppDataManager.getInstance().clearCountryCodes();
                AppDataManager.getInstance().putAllCountryCodes(countryCodeInfos);
                fillCountryCodes();
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

  private void hideKeyBoard() {
    if(getCurrentFocus() != null) {
      ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(phoneTimer !=null) {
      phoneTimer.cancel();
      phoneTimer = null;
    }
    if (emailTimer != null) {
      emailTimer.cancel();
      emailTimer = null;
    }
  }

  public void onClick(View v) {
    if (v.getId() == R.id.tvService) {
      //用户协议
      startUserService(getString(R.string.app_protocol_url),getString(R.string.lbl_privacy_privacy));
    } else if (v.getId() == R.id.tvPrivacy) {
      //用户隐私
      startUserService(getString(R.string.app_privacy_url),getString(R.string.lbl_privacy_service));
    } else if (v.getId() == R.id.ivClose) {
      setResult(LoginOtherActivity_result2);
      finish();
    } else if (v.getId() == R.id.btnSmsSend) {
      String phone = edtPhone.getText().toString();
      if (!TextUtils.isEmpty(phone)) {
        btnSmsSend.setVisibility(View.GONE);
        tvSmsSend.setVisibility(View.VISIBLE);
        startPhoneTimer();
        sendSms(phone,selectCountryCode);
      }
    } else if (v.getId() == R.id.ivArrowDown || v.getId() ==  R.id.tvNationCode) {
      //打开国际码列表
      hideKeyBoard();
      showPickerView();
    } else if (v.getId() == R.id.btnSmsSendEmail) {
      String email = edtEmail.getText().toString();
      if (!TextUtils.isEmpty(email) && Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", email)) {
        btnSmsSendEmail.setVisibility(View.GONE);
        tvSmsSendEmail.setVisibility(View.VISIBLE);
        startEmailTimer();
        sendEmail(email);
      } else {
        Toast.makeText(this, R.string.lbl_login_email_error, Toast.LENGTH_SHORT).show();
      }
    } else if(v.getId() == R.id.btnSend) {
      if (mode == mode_phone) {
        String phone = edtPhone.getText().toString();
        String number = edtSms.getText().toString();
        if(cbSelected.isChecked()){
          if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(number)) {
            login(phone,number);
          }else{
            Toast.makeText(this, R.string.toast_phone_sms_error,Toast.LENGTH_LONG).show();
          }
        }else{
          Toast.makeText(this, R.string.toast_check_protocol,Toast.LENGTH_LONG).show();
        }
      } else if (mode == mode_email){
        String email = edtEmail.getText().toString();
        String number = edtSmsEmail.getText().toString();
        if(cbSelected.isChecked()){
          if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(number) && Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", email)) {
            loginByEmail(email, number);
          } else{
            Toast.makeText(this, R.string.toast_email_sms_error,Toast.LENGTH_LONG).show();
          }
        }else{
          Toast.makeText(this, R.string.toast_check_protocol, Toast.LENGTH_LONG).show();
        }
      }

    }
  }

  private void showPickerView() {
    OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
      if(countryCodes.size() > options1) {
        selectCountryCode = countryCodes.get(options1);
        tvNationCode.setText("+" + countryCodes.get(options1));
        tvNationCode.setTag(countryCodes.get(options1));
      }else{
        Toast.makeText(this, R.string.tip_country_codes, Toast.LENGTH_SHORT).show();
      }
    })
        .setSelectOptions(0)
        .setOutSideCancelable(false)
        .build();
    pvOptions.setPicker(countryCodes);
    pvOptions.show();
  }

  private void startPhoneTimer() {
    phoneTimer = new CountDownTimer(60 * 1000, 1000) {
      @Override
      public void onTick(long l) {
        //计时过程显示
        if(tvSmsSend!=null){
          tvSmsSend.setVisibility(View.VISIBLE);
          tvSmsSend.setText(String.format(getResources().getString(R.string.phone_valid_code_send_again), (l / 1000)));
        }
      }

      @Override
      public void onFinish() {
        //计时完毕时触发
        AppLogger.d("---------time-onFinish---------");
        if(tvSmsSend!=null && btnSmsSend!=null){
          tvSmsSend.setVisibility(View.GONE);
          btnSmsSend.setVisibility(View.VISIBLE);
        }
      }
    }.start();
  }

  private void startEmailTimer() {
    emailTimer = new CountDownTimer(60 * 1000, 1000) {
      @Override
      public void onTick(long l) {
        //计时过程显示
        if(tvSmsSendEmail!=null){
          tvSmsSendEmail.setVisibility(View.VISIBLE);
          tvSmsSendEmail.setText(String.format(getResources().getString(R.string.phone_valid_code_send_again), (l / 1000)));
        }
      }

      @Override
      public void onFinish() {
        //计时完毕时触发
        AppLogger.d("---------time-onFinish---------");
        if(tvSmsSendEmail!=null && btnSmsSendEmail!=null){
          tvSmsSendEmail.setVisibility(View.GONE);
          btnSmsSendEmail.setVisibility(View.VISIBLE);
        }
      }
    }.start();
  }


  private void startUserService(String url,String title){
    startActivity(url,title);
  }

  private void startActivity(String url, String title) {
    Intent it = new Intent(this, WebActivity.class);
    it.putExtra("url", url);
    it.putExtra("title", title);
    startActivity(it);
  }

  private void sendSms(String phone,String phone_prefix){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(3);
    map.put("phone", phone);
    map.put("msg_type", "login");
    map.put("phone_prefix", phone_prefix);
    RequestBody requestBody=RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
    Observable<ResponseBody> observable  =apiServer.loginSendSms(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            Toast.makeText(LoginOtherActivity.this, getString(R.string.phone_valid_code_sended), Toast.LENGTH_SHORT).show();
          }
          @Override
          public void onError(int code, String msg) {
            Toast.makeText(LoginOtherActivity.this, "验证码发送失败: " + msg, Toast.LENGTH_LONG).show();
          }
        })
    );
  }

  private void sendEmail(String email) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(3);
    map.put("email", email);
    map.put("msg_type", "login");
    RequestBody requestBody=RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
    Observable<ResponseBody> observable  =apiServer.loginSendEmail(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            Toast.makeText(LoginOtherActivity.this, "验证码已发送到邮箱", Toast.LENGTH_SHORT).show();
          }
          @Override
          public void onError(int code, String msg) {
            Toast.makeText(LoginOtherActivity.this, "验证码发送失败: " + msg, Toast.LENGTH_LONG).show();
          }
        })
    );
  }

  private void login(String phone,String number){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("phone", phone);
    map.put("number", number);
    map.put("version", BuildConfig.VERSION_CODE); // 版本号
    map.put("channel", BuildConfig.FLAVOR); // 渠道
    map.put("device_model", String.format(
        "sysversion = %s, brand = %s, phonetype = %s, systype = android",
        android.os.Build.VERSION.RELEASE,
        android.os.Build.BRAND,
        android.os.Build.MODEL
    )); // 设备型号 其他手机任何信息，系统版本，蓝牙版本之类的都拼接在设备型号

    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
    Observable<MobileUserInfoModel> observable = apiServer.login(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<MobileUserInfoModel>() {
          @Override
          public void onSuccess(MobileUserInfoModel userModel) {
            loginResult(userModel);
          }
          @Override
          public void onError(int code, String msg) {
            if(code==2){
              Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
              autoLogin();
            }
            Toast.makeText(LoginOtherActivity.this,msg,Toast.LENGTH_LONG).show();
          }
        })
    );
  }

  private void loginByEmail(String email, String number){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("email", email);
    map.put("number", number);
    map.put("version", BuildConfig.VERSION_CODE); // 版本号
    map.put("channel", BuildConfig.FLAVOR); // 渠道
    map.put("device_model", String.format(
        "sysversion = %s, brand = %s, phonetype = %s, systype = android",
        android.os.Build.VERSION.RELEASE,
        android.os.Build.BRAND,
        android.os.Build.MODEL
    )); // 设备型号 其他手机任何信息，系统版本，蓝牙版本之类的都拼接在设备型号

    RequestBody requestBody=RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
    Observable<MobileUserInfoModel> observable = apiServer.loginByEmail(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<MobileUserInfoModel>() {
          @Override
          public void onSuccess(MobileUserInfoModel userModel) {
            loginResult(userModel);
          }
          @Override
          public void onError(int code, String msg) {
            if(code==2){
              Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
              autoLogin();
            }
            Toast.makeText(LoginOtherActivity.this,msg,Toast.LENGTH_LONG).show();
          }
        })
    );
  }

  private void loginResult(MobileUserInfoModel userModel) {
    sqlService.deletePlayInfo();
    sqlService.deleteSpeedDetail();

    if(userModel != null) {
      parseUserInfo(userModel);
      WristBallRetrofitHelper.getInstance().updateToken(userModel.getUser_info().getToken());
      SPUtils.put(getApplication(), "token", userModel.getUser_info().getToken());
      AppLogger.d(userModel.getUser_info().toString());

      if (userModel.getUser_info().isGroup() == -1) {
        startActivity(new Intent(LoginOtherActivity.this, InfoActivity.class));
        finish();
        return;
      }

      WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
      Observable<List<DeviceWithServerModel>> observable = apiServer.getDevices();
      disposable.add(
          observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<List<DeviceWithServerModel>>() {
            @Override
            public void onSuccess(List<DeviceWithServerModel> list) {
              SPUtils.putData(LoginOtherActivity.this, "bleDeviceList", list);
              EventBus.getDefault().post(new MessageEvent(MessageEvent.EDIT_NICKNAME));
              setResult(LoginOtherActivity_result);
              finish();
            }

            @Override
            public void onError(int code, String msg) {
              AppLogger.d(msg);
            }
          })
      );
    }else{
      autoLogin();
    }
  }

  private void parseUserInfo(MobileUserInfoModel userModel){
    UserInfo userInfo = new UserInfo();

    userInfo.setUser_id(userModel.getUser_info().getUser_id());
    userInfo.setStatus(userModel.getUser_info().getStatus());
    userInfo.setUser_name(userModel.getUser_info().getUser_name());
    userInfo.setSelf_description(userModel.getUser_info().getSelf_description());
    userInfo.setUser_img_change(userModel.getUser_info().getUser_img_change());
    userInfo.setUser_img(userModel.getUser_info().getUser_img());

    userInfo.setToken(userModel.getUser_info().getToken());
    userInfo.setSys_sex_id(userModel.getUser_info().getSys_sex_id());
    userInfo.setSex_name(userModel.getUser_info().getSex_name());
    userInfo.setDevice_uid(userModel.getUser_info().getDevice_uid());
    userInfo.setName_cn(userModel.getUser_info().getName_cn());
    userInfo.setSys_user_type_id(userModel.getUser_info().getSys_user_type_id());
    userInfo.setUser_type_name(userModel.getUser_info().getUser_type_name());
    userInfo.setPhone(userModel.getUser_info().getPhone());
    userInfo.setToken(userModel.getUser_info().getAccess_token());
    userInfo.setSys_sex_id_change(userModel.getUser_info().getSys_sex_id_change());

    //徽章
    userInfo.setAchievement(userModel.getUser_info().getAchievement());
    userInfo.setMy_medal(userModel.getUser_info().getMy_medal());
    int medalCount=0;
    for(MedalInfo medalInfo:userModel.getUser_info().getMy_medal()){
      if(medalInfo.isIs_get()){
        medalCount+=1;
      }
    }
    userInfo.setSys_medal_count(medalCount);

    UserInfoModel userInfoModel=new UserInfoModel();
    userInfoModel.setUser_info(userInfo);
    AppDataManager.getInstance().setUserInfoModel(userInfoModel);

//    EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
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
                SPUtils.put(getApplication(), "token", userInfoModel.getUser_info().getToken()+"");
                AppDataManager.getInstance().setUserInfoModel(userInfoModel);
                WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken()+"");
                Logger.d(userInfoModel.getUser_info().toString());
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
