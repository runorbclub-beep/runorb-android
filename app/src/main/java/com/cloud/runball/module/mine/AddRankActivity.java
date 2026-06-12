package com.cloud.runball.module.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cloud.city.Constants;
import com.cloud.city.util.LocationCreator;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.RxBus;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import com.cloud.runball.databinding.ActivityRankAddBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: AddRankActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/8 13:29
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/8 13:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AddRankActivity extends BaseActivity{

  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  public static final int REQUEST_CODE=100;

  private static final String[] REQUEST_PERMISSIONS = {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
  };

  private ActivityRankAddBinding binding;

  TextView tvCurCity;
  TextView tv_age_adult;
  TextView tv_age_teen;
  TextView tv_age_all;
  TextView tv_group_person;
  TextView tv_group_group;
  TextView tv_group_all;
  TextView tvSexMan;
  TextView tvSexWomen;
  TextView tvSexAll;
  TextView tv_Reset;

  int age_type = 0;
  int group_type = 0;
  int sexType = 0;
  String mAddress;

  private Disposable subscription = null;

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_rank_add;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityRankAddBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void initView() {
    tvCurCity = binding.tvCurCity;
    tv_age_adult = binding.tvAgeAdult;
    tv_age_teen = binding.tvAgeTeen;
    tv_age_all = binding.tvAgeAll;
    tv_group_person = binding.tvGroupPerson;
    tv_group_group = binding.tvGroupGroup;
    tv_group_all = binding.tvGroupAll;
    tvSexMan = binding.tvSexMan;
    tvSexWomen = binding.tvSexWomen;
    tvSexAll = binding.tvSexAll;
    tv_Reset = binding.tvReset;

    initObservable();

    if (ActivityCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      registerLocationChange();
    }else{
      //提示打开定位
      launcher.launch(REQUEST_PERMISSIONS);
    }
  }

  /**
   * 监听
   */
  private void initObservable() {
    subscription= RxBus.getDefault().toObservable(String.class).subscribe(data -> {
      if(tvCurCity!=null){
        if(!TextUtils.isEmpty(data)){
          mAddress=data;
          tvCurCity.setText(data);
        }
      }
    });
  }

  ActivityResultLauncher<String[]> launcher = registerForActivityResult(
      new ActivityResultContracts.RequestMultiplePermissions(),
      result -> {
        for (int i = 0; i < REQUEST_PERMISSIONS.length; i++) {
          if(result.containsKey(REQUEST_PERMISSIONS[i])){
            registerLocationChange();
            break;
          }
        }
      });
  @Override
  protected void setOnResult() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_add_rank);
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
    if(subscription!=null){
      subscription.dispose();
    }
    LocationCreator.self(getApplicationContext()).removeCityLocationListener();
  }

  private void registerLocationChange(){
    LocationCreator.self(getApplicationContext()).executeCNBylocation();
  }


  public void onViewClicked(View v) {
    if(v.getId()==R.id.tv_age_adult || v.getId()==R.id.tv_age_teen|| v.getId()==R.id.tv_age_all){
      int typeindex=0;
      if(v.getId()==R.id.tv_age_teen ){
        typeindex=1;
      }else if(v.getId()==R.id.tv_age_all){
        typeindex=-1;
      }
      switchAgeType(typeindex);
    } else if(v.getId()==R.id.tv_group_person || v.getId()==R.id.tv_group_group|| v.getId()==R.id.tv_group_all){
      int typeindex=0;
      if(v.getId()==R.id.tv_group_group ){
        typeindex=1;
      }else if(v.getId()==R.id.tv_group_all){
        typeindex=-1;
      }
      switchGroupType(typeindex);
    } else if (v.getId() == R.id.tvSexMan) {
      switchSexType(0);
    } else if (v.getId() == R.id.tvSexWomen) {
      switchSexType(1);
    } else if (v.getId() == R.id.tvSexAll) {
      switchSexType(2);
    } else if(v.getId()==R.id.tv_Reset){
      //重置
      switchReset(0);
    } else if(v.getId() == R.id.tv_Confirm){
      //确认
      String sexId = null;
      if (sexType == 0) {
        sexId = SexConstant.SEX_MAN;
      } else if (sexType == 1) {
        sexId = SexConstant.SEX_WOMEN;
      }
      requestRankAdd(age_type, group_type, mAddress!=null ? mAddress : tvCurCity.getText().toString(), sexId);
    } else if(v.getId() == R.id.tvCurCity){
      //选择城市
      startCityActivity();
    }
  }

  @Override
  protected void addListener() {
    // wire click listeners previously using @OnClick
    binding.tvAgeAdult.setOnClickListener(this::onViewClicked);
    binding.tvAgeTeen.setOnClickListener(this::onViewClicked);
    binding.tvAgeAll.setOnClickListener(this::onViewClicked);
    binding.tvGroupPerson.setOnClickListener(this::onViewClicked);
    binding.tvGroupGroup.setOnClickListener(this::onViewClicked);
    binding.tvGroupAll.setOnClickListener(this::onViewClicked);
    binding.tvSexMan.setOnClickListener(this::onViewClicked);
    binding.tvSexWomen.setOnClickListener(this::onViewClicked);
    binding.tvSexAll.setOnClickListener(this::onViewClicked);
    binding.tvReset.setOnClickListener(this::onViewClicked);
    binding.tvConfirm.setOnClickListener(this::onViewClicked);
    binding.tvCurCity.setOnClickListener(this::onViewClicked);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode==REQUEST_CODE && resultCode==REQUEST_CODE){
      String city=data.getStringExtra("city");
      mAddress=city;
      tvCurCity.setText(city);
    }
  }


  private void requestRankAdd(int user_age_type, int user_type, String address, String sexId){
    Map<String, Object> map = new HashMap<>();
    map.put("user_age_type", user_age_type);
    map.put("user_type", user_type);
    if (!getString(R.string.global).equals(address)) {
      map.put("address", address);
    }
    if (sexId != null) {
      map.put("sys_sex_id", sexId);
    }
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable  =apiServer.rankListAdd(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody){
            setResult(100);
            finish();
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }


  //ActivityResultLauncher<Intent> launcherActivity =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
  //    if(BuildConfig.isCityEnabled){
  //        if(result.getResultCode()== com.cloud.city.CityActivity.RESULT_CODE){
  //            String city=result.getData().getStringExtra("city");
  //            tvCurCity.setText(city);
  //        }
  //    }
  //});

  private void startCityActivity(){
    ARouter.getInstance().build(Constants.CITY).withBoolean("area", true).navigation(this,REQUEST_CODE);
  }



  private void switchReset(int typeindex){
    switchAgeType(typeindex);
    switchGroupType(typeindex);
    switchSexType(typeindex);
  }

  private void switchAgeType(int typeindex){
    if(typeindex==0){
      tv_age_adult.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tv_age_adult.setTextColor(Color.parseColor("#F9E85B"));
      tv_age_teen.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_age_teen.setTextColor(Color.parseColor("#767779"));
      tv_age_all.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_age_all.setTextColor(Color.parseColor("#767779"));
    }else if(typeindex==1){
      tv_age_adult.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_age_adult.setTextColor(Color.parseColor("#767779"));
      tv_age_teen.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tv_age_teen.setTextColor(Color.parseColor("#F9E85B"));
      tv_age_all.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_age_all.setTextColor(Color.parseColor("#767779"));
    }else if(typeindex==-1){
      tv_age_adult.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_age_adult.setTextColor(Color.parseColor("#767779"));
      tv_age_teen.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_age_teen.setTextColor(Color.parseColor("#767779"));
      tv_age_all.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tv_age_all.setTextColor(Color.parseColor("#F9E85B"));
    }

    age_type=typeindex;
  }

  /**
   *  个人，团体，全部
   * @param typeindex
   */
  private void switchGroupType(int typeindex){
    if(typeindex==0){
      tv_group_person.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tv_group_person.setTextColor(Color.parseColor("#F9E85B"));
      tv_group_group.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_group_group.setTextColor(Color.parseColor("#767779"));
      tv_group_all.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_group_all.setTextColor(Color.parseColor("#767779"));
    }else if(typeindex==1){
      tv_group_person.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_group_person.setTextColor(Color.parseColor("#767779"));
      tv_group_group.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tv_group_group.setTextColor(Color.parseColor("#F9E85B"));
      tv_group_all.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_group_all.setTextColor(Color.parseColor("#767779"));
    }else if(typeindex==-1){
      tv_group_person.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_group_person.setTextColor(Color.parseColor("#767779"));
      tv_group_group.setBackgroundResource(R.drawable.selector_ellipse_button);
      tv_group_group.setTextColor(Color.parseColor("#767779"));
      tv_group_all.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tv_group_all.setTextColor(Color.parseColor("#F9E85B"));
    }

    group_type=typeindex;
  }

  private void switchSexType(int typeIndex){
    if(typeIndex == 0) {
      tvSexMan.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tvSexMan.setTextColor(Color.parseColor("#F9E85B"));
      tvSexWomen.setBackgroundResource(R.drawable.selector_ellipse_button);
      tvSexWomen.setTextColor(Color.parseColor("#767779"));
      tvSexAll.setBackgroundResource(R.drawable.selector_ellipse_button);
      tvSexAll.setTextColor(Color.parseColor("#767779"));
    } else if(typeIndex == 1) {
      tvSexMan.setBackgroundResource(R.drawable.selector_ellipse_button);
      tvSexMan.setTextColor(Color.parseColor("#767779"));
      tvSexWomen.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tvSexWomen.setTextColor(Color.parseColor("#F9E85B"));
      tvSexAll.setBackgroundResource(R.drawable.selector_ellipse_button);
      tvSexAll.setTextColor(Color.parseColor("#767779"));
    } else if(typeIndex == 2) {
      tvSexMan.setBackgroundResource(R.drawable.selector_ellipse_button);
      tvSexMan.setTextColor(Color.parseColor("#767779"));
      tvSexWomen.setBackgroundResource(R.drawable.selector_ellipse_button);
      tvSexWomen.setTextColor(Color.parseColor("#767779"));
      tvSexAll.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tvSexAll.setTextColor(Color.parseColor("#F9E85B"));
    } else if(typeIndex == -1) {
      tvSexMan.setBackgroundResource(R.drawable.selector_ellipse_button);
      tvSexMan.setTextColor(Color.parseColor("#767779"));
      tvSexWomen.setBackgroundResource(R.drawable.selector_ellipse_button);
      tvSexWomen.setTextColor(Color.parseColor("#767779"));
      tvSexAll.setBackgroundResource(R.drawable.selector_ellipse_selected_button);
      tvSexAll.setTextColor(Color.parseColor("#F9E85B"));
    }
    sexType = typeIndex;
  }

}
