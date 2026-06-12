package com.cloud.runball.module.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.RecyclerViewDivider;
import com.cloud.runball.basecomm.listener.OnItemClickListener;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.OtherMatchInfo;
import com.cloud.runball.dialog.PKRuleDialog;
import com.cloud.runball.model.OtherMatchModel;
import com.cloud.runball.model.ShakeMatchModel;
import com.cloud.runball.module.WebActivity;
import com.cloud.runball.module.guidance.GuidanceActivity;
import com.cloud.runball.module.home.adapter.OtherMatchAdapter;
import com.cloud.runball.module.yjy.history.OtherMainMatchListActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.Constant;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.cloud.runball.databinding.ActivityOtherMatchBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.module.home
 * @ClassName: OtherMatchActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/20 17:17
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/20 17:17
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMatchActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, OnItemClickListener {

  private ActivityOtherMatchBinding binding;

  Toolbar toolbar;
  ImageView ivBanner;
  View layNotice;
  TextView tvNotice;
  TextView tvRecord;
  TextView tvStatus;
  TextView tvMatchTime;
  View layHelpInfo;
  TextView tvHelperCount;
  TextView tvHelperScore;
  XRecyclerView recyclerview;
  RelativeLayout ryEmpty;

  private List<OtherMatchInfo> records;
  private OtherMatchAdapter otherMatchAdapter = null;

  private String ruleData;

  /**
   * 状态  0：未开始 1：进行中 2：开始报名 3：已结束
   */
  private int status = 0;

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_other_match;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityOtherMatchBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {
    toolbar.setOnMenuItemClickListener(this);
  }

  @Override
  protected void initView() {
    toolbar = binding.lyHeader.toolbar;
    ivBanner = binding.ivBanner;
    layNotice = binding.layNotice;
    tvNotice = binding.tvNotice;
    tvRecord = binding.tvRecord;
    tvStatus = binding.tvStatus;
    tvMatchTime = binding.tvMatchTime;
    layHelpInfo = binding.layHelpInfo;
    tvHelperCount = binding.tvHelperCount;
    tvHelperScore = binding.tvHelperScore;
    recyclerview = binding.recyclerview;
    ryEmpty = binding.ryEmpty;
    Picasso.with(this)
          .load(R.mipmap.banner_guidance)
          .into(ivBanner);

    records = new ArrayList<>();
    otherMatchAdapter = new OtherMatchAdapter(this, records);
    otherMatchAdapter.setOnItemClickListener(this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerview.setLoadingMoreEnabled(false);
    recyclerview.setPullRefreshEnabled(false);
    recyclerview.setLayoutManager(layoutManager);
    recyclerview.addItemDecoration(new RecyclerViewDivider(40, 40, 40, 0));
    recyclerview.setAdapter(otherMatchAdapter);

    // Replace @OnClick with listeners
    tvRecord.setOnClickListener(this::onViewClick);
    View laySection = findViewById(R.id.laySection);
    if (laySection != null) laySection.setOnClickListener(this::onViewClick);
    ivBanner.setOnClickListener(this::onViewClick);
  }

  @Override
  protected void setOnResult() {

  }

  @Override
  protected void onResume() {
    super.onResume();
    requestRecentRecord();
    if(ruleData == null) {
      requestShakeRule(false);
    }
  }


  @Override
  protected String getTitleLabel() {
    return getString(R.string.lbl_other_match);
  }


  private void requestRecentRecord(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<OtherMatchModel> observable = apiServer.shakeData();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<OtherMatchModel>() {
          @Override
          public void onSuccess(OtherMatchModel otherMatchModel) {
            if(otherMatchModel != null) {
              if(otherMatchModel.getMy_logs().size() > 0) {
                ryEmpty.setVisibility(View.GONE);
              }
              otherMatchAdapter.notifyDataSetChanged(otherMatchModel.getMy_logs());
              status = otherMatchModel.getStatus();
              showStatus(status, otherMatchModel.getStart_time(), otherMatchModel.getStop_time());
//              showBanner(otherMatchModel.getBanner_img(),otherMatchModel.getBanner_link());

              requestHorseList();
            }
          }

          @Override
          public void onError(int code, String msg) {
            Toast.makeText(OtherMatchActivity.this, msg, Toast.LENGTH_SHORT).show();
          }
        })
    );
  }

  private void requestHorseList() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ShakeMatchModel> observable = apiServer.shakeMatchData();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ShakeMatchModel>() {
              @Override
              public void onSuccess(ShakeMatchModel shakeMatchModel) {
                if (shakeMatchModel != null && shakeMatchModel.getMy_info() != null) {
                  String horsesName = shakeMatchModel.getMy_info().getTitle();
                  int index = shakeMatchModel.getMy_info().getIndex() + 1;
                  int helperCount = 0;
                  int helperScore = 0;
                  
                  long horsesId = shakeMatchModel.getMy_info().getShake_group_id();
                  for (ShakeMatchModel.ShakeItem item: shakeMatchModel.getGroup_list()) {
                    if (item.getShake_group_id() == horsesId) {
                      helperCount = item.getNum();
                      helperScore = item.getIntegral();
                      break;
                    }
                  }
                  showNotice(index, horsesName, helperCount, helperScore);
                }
              }

              @Override
              public void onError(int code, String msg) {
                Toast.makeText(OtherMatchActivity.this, msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  private void showNotice(int index, String horsesName, int helperCount, int helperScore) {
    layNotice.setVisibility(View.VISIBLE);
    tvMatchTime.setVisibility(View.VISIBLE);
    layHelpInfo.setVisibility(View.GONE);
    if(status == 0) { // 未开始
      tvNotice.setText(getString(R.string.tip_other_match_status_0));
    }else if(status == 1) { // 进行中
      if (TextUtils.isEmpty(horsesName)) {
        tvNotice.setText(getString(R.string.tip_other_match_status_1));
      } else {
        tvNotice.setText(getString(R.string.tip_other_match_status_1_format, index + "", horsesName));
        tvMatchTime.setVisibility(View.GONE);
        layHelpInfo.setVisibility(View.VISIBLE);
        tvHelperCount.setText(getString(R.string.format_helper_count, helperCount + ""));
        tvHelperScore.setText(getString(R.string.format_helper_score, helperScore + ""));
      }
    }else if(status == 2) { // 即将开始
      tvNotice.setText(getString(R.string.tip_other_match_status_2));
    }else if(status == 3) { // 已结束
      tvNotice.setText(getString(R.string.tip_other_match_status_3));
    }
  }

  private void requestShakeRule(boolean isShow) {
    if(!TextUtils.isEmpty(ruleData)) {
      if (isShow) {
        PKRuleDialog.show(this, ruleData);
      }
      return;
    }
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ResponseBody> observable = apiServer.shakeRuleData();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try{
              JSONObject jsonObject=new JSONObject(responseBody.string());
              if(jsonObject.optInt("code") == 1) {
                JSONObject data = jsonObject.optJSONObject("data");
                if(data!=null){
                  ruleData = data.optString("content");
                }
                if (isShow) {
                  PKRuleDialog.show(OtherMatchActivity.this, ruleData);
                }
              }
            }catch (Exception ex){
              ex.printStackTrace();
            }
          }
          @Override
          public void onError(int code, String msg) {
            Toast.makeText(OtherMatchActivity.this, msg, Toast.LENGTH_SHORT).show();
          }
        })
    );
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main_rule, menu);
    return true;
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    if (item.getItemId() == R.id.action_rule) {
      requestShakeRule(true);
    }
    return true;
  }

  public void onViewClick(View v) {
    if(v.getId() == R.id.tvRecord) {
      startActivity(new Intent(this,OtherMatchHistoryActivity.class));
    }else if(v.getId() == R.id.laySection){
      if(status == 1 || status == 2) {
        startActivity(new Intent(this,OtherMainMatchActivity.class));
      }
    }else if(v.getId() == R.id.ivBanner) {
//      if(ivBanner.getTag()!=null && !TextUtils.isEmpty(ivBanner.getTag().toString())){
//        Intent it = new Intent(this, WebActivity.class);
//        it.putExtra("url", ivBanner.getTag().toString());
//        startActivity(it);
//      }
      GuidanceActivity.startAction(this);
    }
  }

  public void showStatus(int status, String start_time, String end_time){
    String timeStr = "(" + start_time + "-" + end_time + ")";
    tvMatchTime.setText(timeStr);
    if(status == 0) {
      tvStatus.setText(getString(R.string.lbl_other_match_status_0));
      tvStatus.setEnabled(false);
      tvStatus.setTextColor(getResources().getColor(R.color.main_match_disabled));
    }else if(status == 1) {
      tvStatus.setText(getString(R.string.lbl_other_match_status_1));
      tvStatus.setTextColor(getResources().getColor(R.color.main_match_enabled));
    }else if(status == 2) {
      tvStatus.setText(getString(R.string.lbl_other_match_status_2));
      tvStatus.setTextColor(getResources().getColor(R.color.main_match_enabled));
    }else if(status == 3) {
      tvStatus.setText(getString(R.string.lbl_other_match_status_3));
      tvStatus.setEnabled(false);
      tvStatus.setTextColor(getResources().getColor(R.color.main_match_disabled));
    }
  }


  @Override
  public void onItemClick(Object t, int index) {
    Intent it = new Intent(this, OtherMainMatchListActivity.class);
    it.putExtra("sys_shake_id", ((OtherMatchInfo)t).getSys_shake_id());
    it.putExtra("date", ((OtherMatchInfo)t).getDate());
    startActivity(it);
  }
}
