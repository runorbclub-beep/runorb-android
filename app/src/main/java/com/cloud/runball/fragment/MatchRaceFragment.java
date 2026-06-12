package com.cloud.runball.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.module.race.CreateMatchActivity;
import com.cloud.runball.module.race.CreateMatchAddActivity;
import com.cloud.runball.module.race.CreateMatchTeamActivity;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.CheckHelper;

import com.cloud.runball.databinding.FragmentMatchRaceBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: MatchRaceFragment
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/6 13:59
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/6 13:59
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRaceFragment extends BaseFragment {

  private FragmentMatchRaceBinding binding;

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_match_race;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentMatchRaceBinding.bind(view);
    // Replace ButterKnife @OnClick with explicit listeners
    if (binding.btnDoubleMatch != null) binding.btnDoubleMatch.setOnClickListener(this::onClick);
    if (binding.btnTeamMatch != null) binding.btnTeamMatch.setOnClickListener(this::onClick);
    if (binding.btnMatchAdd != null) binding.btnMatchAdd.setOnClickListener(this::onClick);
  }

  @Override
  protected void onLazyLoad() {

  }

  public void onClick(View v) {
    if(CheckHelper.onCheckFunc() == CheckHelper.PHONE) {
      goToLogin();
    } else if(CheckHelper.onCheckFunc() == CheckHelper.NONE) {
      Toast.makeText(App.self().getApplicationContext(), R.string.lbl_pk_net_error, Toast.LENGTH_LONG).show();
    } else {
      if(v.getId() == R.id.btnDoubleMatch) {
        Intent it = new Intent(getContext(), CreateMatchActivity.class);
        startActivity(it);
      } else if(v.getId() == R.id.btnTeamMatch) {
        Intent it = new Intent(getContext(), CreateMatchTeamActivity.class);
        startActivity(it);
      } else if(v.getId() == R.id.btnMatchAdd) {
        Intent it = new Intent(getContext(), CreateMatchAddActivity.class);
        startActivity(it);
      }
    }
  }

  /**
   * 打开用户手机号登录页面
   */
  private void goToLogin(){
    Intent it=new Intent(getContext(), LoginOtherActivity.class);
    it.putExtra("resultCode",true);
    loginActivityLaunch.launch(it);
  }

  ActivityResultLauncher<Intent> loginActivityLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    int resultCode=result.getResultCode();
    if(resultCode==LoginOtherActivity.LoginOtherActivity_result){
      if(AppDataManager.getInstance().getUserInfoModel()!=null){
        //获取用户信息
        requestUserInfo();
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
            AppLogger.d("--MatchRaceFragment--获取个人信息成功----");
            AppDataManager.getInstance().setUserInfoModel(userInfoModel);
            WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
            SPUtils.put(getContext(), "token", userInfoModel.getUser_info().getToken());
          }

          @Override
          public void onError(int code, String msg) {

          }
        })
    );
  }
}
