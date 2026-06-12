package com.cloud.runball.module.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.module.mine.adapter.RankListAdapter;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.bean.RankInfo;
import com.cloud.runball.model.RankModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityMineMatchSwitchBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: RankingSwitchActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/19 11:04
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/19 11:04
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RankingSwitchActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, RankListAdapter.onItemClickListener,RankListAdapter.onLongClickListener {

  public static final int RESULT_CODE=100;

  private ActivityMineMatchSwitchBinding binding;
  RecyclerView rvRank;

  RecyclerView rvCustomRank;

  RankListAdapter rankListAdapter;
  RankListAdapter rankCustomListAdapter;

  List<RankInfo> baseRankInfos=new ArrayList<>();
  List<RankInfo> customRankInfos=new ArrayList<>();

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_mine_match_switch;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMineMatchSwitchBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {

  }

  @Override
  protected void initView() {
    rvRank = binding.rvRank;
    rvCustomRank = binding.rvCustomRank;
    rankListAdapter=new RankListAdapter(this);
    rankListAdapter.setOnLongClickListener(this);
    //初始化我的数据信息
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);

    rvRank.addItemDecoration(new RecyclerViewDivider(0));
    rvRank.setHasFixedSize(true);
    rvRank.setLayoutManager(manager);
    rankListAdapter.setOnItemClickListener(this);
    rvRank.setItemAnimator(new DefaultItemAnimator());
    rvRank.setAdapter(rankListAdapter);


    //自定义榜单
    rankCustomListAdapter=new RankListAdapter(this);
    rankCustomListAdapter.setOnLongClickListener(this);

    rvCustomRank.addItemDecoration(new RecyclerViewDivider(0));
    rvCustomRank.setHasFixedSize(true);
    rvCustomRank.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    rankCustomListAdapter.setOnItemClickListener(this);
    rvCustomRank.setItemAnimator(new DefaultItemAnimator());
    rvCustomRank.setAdapter(rankCustomListAdapter);
  }

  @Override
  protected void setOnResult() {

  }

  @Override
  public void onResume(){
    super.onResume();

    requestRankList();
  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.lbl_match_ranking);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_rank, menu);
    MenuItem item = menu.findItem(R.id.action_add);
    item.getActionView().setOnClickListener(v -> {
      //添加
      startAddRankActivity();
    });
    return super.onCreateOptionsMenu(menu);
  }


  @Override
  protected void supportToolbar() {
    super.supportToolbar();
    getToolbar().setOnMenuItemClickListener(this);
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    if (item.getItemId() == R.id.action_add) {
      //添加
      startAddRankActivity();
    }
    return true;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode==RESULT_CODE){
      requestRankList();
    }
  }

  private void onParseRankList(List<RankInfo> infos){
    baseRankInfos.clear();
    customRankInfos.clear();

    for(RankInfo info:infos){
      if(TextUtils.isEmpty(info.getUser_rank_list_id())){
        baseRankInfos.add(info);
      }else{
        customRankInfos.add(info);
      }
    }
    rankCustomListAdapter.notifyDataSetChanged(customRankInfos);
    rankListAdapter.notifyDataSetChanged(baseRankInfos);
  }

  private void requestRankList(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<RankModel> observable = apiServer.rankList();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<RankModel>() {
                 @Override
                 public void onSuccess(RankModel rankModel) {
                   AppLogger.d(rankModel.toString());
                   onParseRankList(rankModel.getList());
                 }

                 @Override
                 public void onError(int code, String msg) {
                   AppLogger.d(msg);
                 }
               }
            )
    );
  }

  private void deleteRankList(String user_rank_list_id){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

    HashMap<String, Object> map = new HashMap<>(1);
    map.put("user_rank_list_id", user_rank_list_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable  =apiServer.deleteRankList(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody){
            try{
              AppLogger.d(responseBody.string());
              removeRankInfo(user_rank_list_id);
            }catch (Exception ex){
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }

  private void removeRankInfo(String user_rank_list_id){
    for(RankInfo item:customRankInfos){
      if(item.getUser_rank_list_id().equalsIgnoreCase(user_rank_list_id)){
        customRankInfos.remove(item);
        break;
      }
    }
    rankCustomListAdapter.notifyDataSetChanged(customRankInfos);
  }

  private void startAddRankActivity(){
    Intent it=new Intent(getApplicationContext(), AddRankActivity.class);
    startActivity(it);
  }

  @Override
  public void onItemClick(RankInfo info) {
//        Intent it=new Intent(this,RankingActivity.class);
    Intent it = new Intent();
    it.putExtra("user_age_type",info.getUser_age_type());
    it.putExtra("user_type",info.getUser_type());
    it.putExtra("address",info.getAddress());
    it.putExtra("title",info.getTitle());
    it.putExtra("sys_sex_id", info.getSys_sex_id());
//        startActivity(it);
    setResult(RESULT_CODE, it);
    finish();
  }

  @Override
  public void onLongClick(RankInfo info) {
    //弹出框并选择
    if(!TextUtils.isEmpty(info.getUser_rank_list_id())){
      showDialog(getString(R.string.tip), getString(R.string.tip_message), null,() -> {
        deleteRankList(info.getUser_rank_list_id());
      });
    }
  }
}
