package com.cloud.runball.module.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.module.match.adapter.MatchRecordDetailAdapter;
import com.cloud.runball.basecomm.base.RecycleViewDivider;
import com.cloud.runball.model.ListPkItem;
import com.cloud.runball.model.PKDataDetailModel;
import com.cloud.runball.model.PKDataItemModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.databinding.ActivityMatchRecordDetailBinding;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: MatchRecordDetailActivity
 * @Description: 双人PK详情
 * @Author: zhd
 * @CreateDate: 2021/4/16 10:55
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/16 10:55
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRecordDetailActivity extends BaseActivity {

  private ActivityMatchRecordDetailBinding binding;

  Toolbar toolbar;
  RecyclerView recyclerview;

  private MatchRecordDetailAdapter mMatchRecordDetailAdapter;
  private String pk_room_id;
  private String user_group;
  private final List<ListPkItem> list = new ArrayList<>();

  public static void startAction(Context context, int pkType, String pkRoomId, String userGroup) {
    Intent intent = new Intent(context, MatchRecordDetailActivity.class);
    intent.putExtra("pk_type", pkType);
    intent.putExtra("pk_room_id", pkRoomId);
    intent.putExtra("user_group", userGroup);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_match_record_detail;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMatchRecordDetailBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    recyclerview = binding.recyclerview;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    pk_room_id=this.getIntent().getStringExtra("pk_room_id");
    user_group=this.getIntent().getStringExtra("user_group");

    mMatchRecordDetailAdapter = new MatchRecordDetailAdapter(this,  list);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerview.setLayoutManager(layoutManager);
    recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
    recyclerview.setAdapter(mMatchRecordDetailAdapter);

    //请求双人PK信息
    getPkDetail(pk_room_id);
  }

  /**
   * 获取数据详情
   * @param pk_room_id
   */
  private void getPkDetail(String pk_room_id) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("pk_room_id", pk_room_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<PKDataDetailModel> observable = apiServer.myPKInfo(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<PKDataDetailModel>() {
              @Override
              public void onSuccess(PKDataDetailModel pKDataDetailModel) {
                try {
                  if(pKDataDetailModel!=null){
                    parsePKDataDetail(pKDataDetailModel.getList());
                  }
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }

              @Override
              public void onError(int code, String msg) {

              }
            }
        )
    );
  }


  public void parsePKDataDetail(List<ListPkItem> listItems){
    if(listItems == null)
      return;
    mMatchRecordDetailAdapter.setMatchRecords(listItems);
    mMatchRecordDetailAdapter.notifyDataSetChanged();
  }

  private PKDataItemModel getPKDataItemModel(int is_win, ListPkItem pKDataItem){
    PKDataItemModel model = new PKDataItemModel();
    model.setIs_win(is_win);
    model.setCreated_time(pKDataItem.getCreated_time());
    model.setUser_group(pKDataItem.getUser_group());
    model.setUser_id(pKDataItem.getUser_id());
    model.setUser_name(pKDataItem.getUser_name());
    model.setUser_pk_list_id(pKDataItem.getUser_pk_list_id());
    model.setDuration(String.valueOf(pKDataItem.getDuration()));
    model.setGroup_win(pKDataItem.getGroup_win());
    model.setDistance(String.valueOf(pKDataItem.getDistance()));
    model.setSpeed_max(pKDataItem.getSpeed_max());
    model.setUser_img(pKDataItem.getUser_img());
    model.setStart_date(pKDataItem.getStart_date());
    return model;
  }
}
