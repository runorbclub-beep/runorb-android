package com.cloud.runball.module.match;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.PermissionWallUtils;
import com.cloud.runball.dialog.CommonDialog;
import com.cloud.runball.model.RankMatchInfo2;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: RankMatchMainActivity
 * @Description: 竞标赛
 * @Author: zhd
 * @CreateDate: 2021/5/11 14:35
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/11 14:35
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankMatchMainActivity extends AppCompatActivity implements View.OnClickListener,Toolbar.OnMenuItemClickListener{

  protected Toolbar toolbar;
  XMainFragment mMainFragment;
  FragmentManager fragmentManager;
  String title;
  String sys_match_id;
  String user_group_id;
  String matchs_stage_id;

  TextView tvToolBarTitle;

  private final CompositeDisposable disposable = new CompositeDisposable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_rank_match);
    //设置可以浏览
    sys_match_id=this.getIntent().getStringExtra("sys_match_id");
    user_group_id=this.getIntent().getStringExtra("user_group_id");
    matchs_stage_id=this.getIntent().getStringExtra("matchs_stage_id");
    title=this.getIntent().getStringExtra("title");
    supportToolbar(title);
    initView();
    requestRankMatchBase(sys_match_id,user_group_id);
  }

  protected void initView() {
    fragmentManager = getSupportFragmentManager();
    mMainFragment=(XMainFragment)fragmentManager.findFragmentById(R.id.mainFragment);
    //这里需要加上
    mMainFragment.setRankMatchParams(sys_match_id, matchs_stage_id, user_group_id);
    mMainFragment.showRandMatchVisibly();
    mMainFragment.showProgress(View.VISIBLE);
  }

  @Override
  protected void onResume() {
    super.onResume();
    AppLogger.d("-------RankMatchMainActivity.onResume--------");
  }

  protected void supportToolbar(String title) {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    tvToolBarTitle = findViewById(R.id.tvToolBarTitle);
    tvToolBarTitle.setText(title);
    toolbar.setTitle("");
    setSupportActionBar(toolbar);
    toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    toolbar.setTitleMargin(0, 5, 5, 5);
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    toolbar.setNavigationIcon(R.mipmap.btn_return);
    toolbar.setNavigationOnClickListener(v -> setOnResult());
    toolbar.setOnMenuItemClickListener(this);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Logger.d("RankMatchMainActivity:onActivityResult:requestCode=" + requestCode + ";RESULT_CODE=" + resultCode);
    //QQ与新浪微博的回调
    //UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    super.onKeyDown(keyCode, event);
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (mMainFragment != null && mMainFragment.isShowBadge()) {
        mMainFragment.dismissBadge();
        return true;
      }
      return false;
    }
    return false;
  }


  protected void setOnResult() {
    showExitDialog(getString(R.string.tip),getString(R.string.lbl_exit_match_message));
  }

  @Override
  public void onClick(View v) {

  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    return false;
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
//        mMainFragment.stopScheduleCountDown();
    mMainFragment=null;
    if (disposable != null) {
      disposable.dispose();
    }
  }

  public void showPermissionDialog(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.btn_cancel, null);
    builder.setPositiveButton(R.string.btn_confirm, (dialogInterface, i) -> PermissionWallUtils.startPermissionSetting());
    builder.show();
  }

  /**
   * 退出
   * @param title
   * @param message
   */
  public void showExitDialog(String title, String message) {
    CommonDialog dialog = new CommonDialog(this);
    dialog.setContent(title, message);
    dialog.addBtn(getString(R.string.btn_ok), commonDialog -> {
      if (mMainFragment != null) {
        mMainFragment.onExitMatch();
      }
      commonDialog.dismiss();
      finish();
    });
    dialog.addBtn(getString(R.string.btn_cancel), commonDialog -> {
      commonDialog.dismiss();
    });
  }

  /**
   * 查询比赛基本信息
   * @param sys_match_id
   * @param user_group_id
   */
  private void requestRankMatchBase(String sys_match_id,String user_group_id){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(3);
    map.put("sys_match_id", sys_match_id);
    map.put("show_all", 0);
    map.put("user_group_id", user_group_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.rankMatchInfo(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try{
              parseRankMatchResp(responseBody);
            }catch (JSONException ex){
              ex.printStackTrace();
            }catch (Exception ex){
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("---------requestRankMatchBase-------"+msg);
            mMainFragment.showRandMatchVisibly("","",0,0);
          }
        })
    );
  }

  private void parseRankMatchResp(ResponseBody responseBody) throws IOException, JSONException {
    JSONObject jObject = new JSONObject(responseBody.string());
    int code = jObject.optInt("code", -1);
    if (code == 1) {
      JSONObject dataObject=jObject.optJSONObject("data");
      if(dataObject.optInt("code") == 0){
        String msg=dataObject.optString("msg");
        //弹框退出
        showExitDialog(getString(R.string.tip),msg);
      }else if(dataObject.optInt("code") == 1) {
        Gson gson=new Gson();
        AppLogger.d("---------requestRankMatchBase.onSuccess-------"+dataObject.toString());
        RankMatchInfo2 rankMatchInfo = gson.fromJson(dataObject.toString(), RankMatchInfo2.class);
        int progress=(int)(Math.floor(rankMatchInfo.getDistance_percentage() * 100));
        //设置比赛主页面参数
        if(rankMatchInfo.getIs_end()==1){
          mMainFragment.showRandMatchVisibly(rankMatchInfo.getRanking(),rankMatchInfo.getDistance_poor(),progress,rankMatchInfo.getResidue_time());
          showDialog(getString(R.string.tip), rankMatchInfo.getMatchs_end_tips());
        }else{
          mMainFragment.setRankMatchParams(sys_match_id,rankMatchInfo.getMatchs_stage_id(),user_group_id);
          mMainFragment.showRandMatchVisibly(rankMatchInfo.getRanking(),rankMatchInfo.getDistance_poor(),progress,rankMatchInfo.getResidue_time());
//                    mMainFragment.startScheduleCountDown();
        }
      }
    }
  }

  public void showDialog(String title, String message) {
    CommonDialog dialog = new CommonDialog(this);
    dialog.setContent(title, message);
    dialog.addBtn(getString(R.string.btn_ok), commonDialog -> {
      commonDialog.dismiss();
      finish();
    });
  }

}
