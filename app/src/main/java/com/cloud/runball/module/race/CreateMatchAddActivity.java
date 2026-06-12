package com.cloud.runball.module.race;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.PkInfoModel;
import com.cloud.runball.model.PkUserDataModel;
import com.cloud.runball.model.UserGroupModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.github.phoenix.widget.Keyboard;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.cloud.runball.databinding.ActivityMatchAddBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: CreateMatchAddActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/8 14:34
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/8 14:34
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CreateMatchAddActivity extends BaseActivity {


  private static final String[] KEY = new String[]{
      "1", "2", "3",
      "4", "5", "6",
      "7", "8", "9",
      "", "0", "d"
  };

  private ActivityMatchAddBinding binding;

  TextView tvRoomID1;
  TextView tvRoomID2;
  TextView tvRoomID3;
  TextView tvRoomID4;
  TextView tvRoomID5;
  TextView tvRoomID6;
  TextView tvTimeValid;
  Keyboard keyboard;

  String pk_room_number;

  long currentTimeMillis=System.currentTimeMillis();

  ArrayList<UserGroupModel> userGroupModelList = new ArrayList<UserGroupModel>();

  //是否竞标赛
  boolean isRankMatch=false;

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_match_add;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMatchAddBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {

  }

  @Override
  protected void initView() {
    tvRoomID1 = binding.tvRoomID1;
    tvRoomID2 = binding.tvRoomID2;
    tvRoomID3 = binding.tvRoomID3;
    tvRoomID4 = binding.tvRoomID4;
    tvRoomID5 = binding.tvRoomID5;
    tvRoomID6 = binding.tvRoomID6;
    tvTimeValid = binding.tvTimeValid;
    keyboard = binding.KeyboardViewPay;
    isRankMatch=this.getIntent().getBooleanExtra("isRankMatch",false);
    setSubView();
    initEvent();

    // Replace @OnClick with listeners
    View btnConfirm = findViewById(R.id.btnConfirm);
    if (btnConfirm != null) btnConfirm.setOnClickListener(this::onViewClicked);
    tvTimeValid.setOnClickListener(this::onViewClicked);
  }

  @Override
  protected void setOnResult() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_add_match);
  }

  public void onViewClicked(View v) {
    if (v.getId() == R.id.btnConfirm) {
      if (!TextUtils.isEmpty(stringBuilder) && stringBuilder.length() == 6) {
        if(System.currentTimeMillis()-currentTimeMillis>=800){
          //跳转到第二个页面
          pk_room_number = stringBuilder.toString();
          addRoom(pk_room_number);
        }
        currentTimeMillis = System.currentTimeMillis();
      } else {
        Toast.makeText(this, R.string.lbl_input_match_tip, Toast.LENGTH_LONG).show();
      }
    } else if (v.getId() == R.id.tvTimeValid) {
      //剪切板黏贴
      String clipText = AppUtils.getStringClipboard(this);
      if (!TextUtils.isEmpty(clipText) && clipText.length() == 6) {
        pk_room_number = clipText;
        if (stringBuilder.length() > 0) {
          stringBuilder.delete(0, stringBuilder.length() - 1);
        }
        stringBuilder.append(clipText);
        showMatchRoomNum(clipText);
      } else {
        Toast.makeText(this, R.string.lbl_input_clip_error_tip, Toast.LENGTH_LONG).show();
      }
    }
  }

  private void showMatchRoomNum(String pk_room_number) {
    if (pk_room_number != null && pk_room_number.trim().length() == 6) {
      tvRoomID1.setText(pk_room_number.substring(0, 1));
      tvRoomID2.setText(pk_room_number.substring(1, 2));
      tvRoomID3.setText(pk_room_number.substring(2, 3));
      tvRoomID4.setText(pk_room_number.substring(3, 4));
      tvRoomID5.setText(pk_room_number.substring(4, 5));
      tvRoomID6.setText(pk_room_number.substring(5));
    }
  }

  private void setSubView() {
    //设置键盘
    keyboard.setKeyboardKeys(KEY);
    keyboard.setKeyBoardBackground(getResources().getColor(R.color.bg_content));
  }


  StringBuilder stringBuilder = new StringBuilder();

  private void initEvent() {
    keyboard.setOnClickKeyboardListener(new Keyboard.OnClickKeyboardListener() {
      @Override
      public void onKeyClick(int position, String value) {
        if (position < 11 && position != 9) {
          stringBuilder.append(value);
          if (stringBuilder.length() > 6) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
          }
          updateRoomKeyBoard();
        } else if (position == 11 || position == 9) {
          if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
          }
          updateRoomKeyBoard();
        }
      }
    });
  }

  public void updateRoomKeyBoard() {
    switch (stringBuilder.length()) {
      case 0:
        tvRoomID1.setText("   ");
        tvRoomID2.setText("   ");
        tvRoomID3.setText("   ");
        tvRoomID4.setText("   ");
        tvRoomID5.setText("   ");
        tvRoomID6.setText("   ");
        break;
      case 1:
        tvRoomID1.setText(stringBuilder.substring(0, 1));
        tvRoomID2.setText("   ");
        tvRoomID3.setText("   ");
        tvRoomID4.setText("   ");
        tvRoomID5.setText("   ");
        tvRoomID6.setText("   ");
        break;
      case 2:
        tvRoomID2.setText(stringBuilder.substring(1, 2));
        tvRoomID3.setText("   ");
        tvRoomID4.setText("   ");
        tvRoomID5.setText("   ");
        tvRoomID6.setText("   ");
        break;
      case 3:
        tvRoomID3.setText(stringBuilder.substring(2, 3));
        tvRoomID4.setText("   ");
        tvRoomID5.setText("   ");
        tvRoomID6.setText("   ");
        break;
      case 4:
        tvRoomID4.setText(stringBuilder.substring(3, 4));
        tvRoomID5.setText("   ");
        tvRoomID6.setText("   ");
        break;
      case 5:
        tvRoomID5.setText(stringBuilder.substring(4, 5));
        tvRoomID6.setText("   ");
        break;
      case 6:
        tvRoomID6.setText(stringBuilder.substring(5));
        break;
    }
  }

  private void addRoom(String pk_room_number) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("pk_room_number", pk_room_number);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.addRoom(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try {
              JSONObject jObject = new JSONObject(responseBody.string());

              AppLogger.d("--CreateMatchAddActivity.addRoom--" + jObject.toString());

              int code = jObject.optInt("code", -1);
              if (code == 1) {
                //双人PK跳转到主游戏界面
                if (jObject.optJSONObject("data") != null) {
                  PkInfoModel pkInfoModel = parsePKInfo(jObject.optJSONObject("data"));
                  startMatchMainActivity(pk_room_number, pkInfoModel);
                }
              } else if (code == 0) {
                //跳转到团队选择界面
                if (jObject.optJSONArray("data") != null) {
                  userGroupModelList.clear();
                  JSONArray jsonArray = jObject.optJSONArray("data");
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    UserGroupModel model = new UserGroupModel(jsonObject.optString("user_group", ""), jsonObject.optString("user_group_title", ""));
                    userGroupModelList.add(model);
                  }
                  if (userGroupModelList.size() >= 2) {
                    //跳转到团队选择页面
                    startChooseTeamActivity(pk_room_number, userGroupModelList);
                  }
                } else {
                  Toast.makeText(getApplication(), jObject.optString("msg"), Toast.LENGTH_LONG).show();
                }
              } else if(code == 2){
                //提示接口错误
                String msg=jObject.optString("msg");
                Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                autoLogin();
              }
            } catch (IOException | JSONException ex) {
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            if(code == 2){
              Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
              autoLogin();
            }
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
            AppLogger.d("---onSuccess--UserInfoModel=" + userInfoModel);
            SPUtils.put(getApplication(), "token", userInfoModel.getUser_info().getToken());
            AppDataManager.getInstance().setUserInfoModel(userInfoModel);
            WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
            //登录成功发送获取比赛tabs
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_RANK_MATCH));
            //弹出登录框
            startLoginOtherActivity();
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }

  private void startLoginOtherActivity(){
    Intent it=new Intent(this, LoginOtherActivity.class);
    it.putExtra("resultCode",true);
    startActivityLaunch.launch(it);
  }

  ActivityResultLauncher<Intent> startActivityLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    int resultCode=result.getResultCode();
    if(resultCode== LoginOtherActivity.LoginOtherActivity_result){

    }else{
      finish();
    }
  });


  private void startChooseTeamActivity(String pk_room_number, ArrayList<UserGroupModel> models) {
    Intent it = new Intent(this, CreateMatchAdd2Activity.class);
    it.putExtra("pk_room_number", pk_room_number);
    it.putParcelableArrayListExtra("groups", models);
    startActivity(it);
    finish();
  }

  boolean isMatchMainActivity=false;

  private void startMatchMainActivity(String pk_room_number, PkInfoModel pk_info) {
    if(!isMatchMainActivity){
      Intent it = new Intent(this, MatchMainActivity.class);
      it.putExtra("pk_room_number", pk_room_number);
      it.putExtra("pk_info", pk_info);
      startActivity(it);
      isMatchMainActivity=true;
    }

    finish();
  }



  private PkInfoModel parsePKInfo(JSONObject jsonObject) {

    PkInfoModel model = new PkInfoModel();

    model.setPk_room_id(jsonObject.optString("pk_room_id"));
    model.setPk_room_title(jsonObject.optString("pk_room_title"));
    model.setPk_room_number(jsonObject.optString("pk_room_number"));
    model.setPk_type(jsonObject.optInt("pk_type", -1));
    model.setPk_result_type(jsonObject.optString("pk_result_type"));
    model.setUser_id(jsonObject.optLong("user_id", -1));
    model.setCreated_uid(jsonObject.optString("created_uid"));
    model.setStatus(jsonObject.optInt("status", -1));
    model.setTime_long(jsonObject.optInt("time_long", -1));

    JSONArray redArray = jsonObject.optJSONArray("red");
    if (redArray != null && redArray.length() > 0) {
      List<PkUserDataModel> redList=parseTeamItem(redArray);
      model.setRedList(redList);
    }

    JSONArray blueArray = jsonObject.optJSONArray("blue");
    if (blueArray != null && blueArray.length() > 0) {
      List<PkUserDataModel> blueList=parseTeamItem(blueArray);
      model.setBlueList(blueList);
    }

    return model;
  }

  private List<PkUserDataModel> parseTeamItem(JSONArray redArray) {
    int len = redArray.length();
    List<PkUserDataModel> list = new ArrayList<>();
    for (int index = 0; index < len; index++) {
      JSONObject item = redArray.optJSONObject(index);

      PkUserDataModel model = new PkUserDataModel();

      model.setStatus(item.optInt("status", -1));
      model.setUser_id(item.optString("user_id"));
      model.setPk_room_id(item.optString("pk_room_id"));
      model.setUser_group(item.optString("user_group"));
      model.setUser_name(item.optString("user_name"));
      model.setUser_img(item.optString("user_img"));
      model.setFd(item.optInt("fd"));
      model.setIs_stop(item.optInt("is_stop"));
      model.setIs_ready(item.optInt("is_ready"));
      model.setCircle_count(item.optInt("circle_count"));
      model.setDuration(item.optInt("duration"));

      list.add(model);
    }
    return list;
  }

}
