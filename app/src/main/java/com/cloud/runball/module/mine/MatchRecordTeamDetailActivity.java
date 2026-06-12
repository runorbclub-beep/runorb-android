package com.cloud.runball.module.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.fragment.MatchRecordTeamFragment;
import com.cloud.runball.model.ListPkItem;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.widget.XTabTitleView;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.cloud.runball.databinding.ActivityMatchTeamRecordDetailBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: MatchRecordDetailActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/16 10:55
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/16 10:55
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchRecordTeamDetailActivity extends BaseActivity {

  private ActivityMatchTeamRecordDetailBinding binding;
  Toolbar toolbar;

  MagicIndicator magic_indicator;

  ViewPager2 viewPager2;

  private String[] titles = new String[]{ "red", "blue" };

  private String pk_room_id;
  private String user_group;

  private String group_title1 = "red";
  private String group_title2 = "blue";

  private final List<ListPkItem> red_list = new ArrayList<>();
  private final List<ListPkItem> blue_list = new ArrayList<>();

  private int red_is_win = 0;
  private int blue_is_win = 0;

  public static void startAction(Context context, int pkType, String pkRoomId, String userGroup) {
    Intent intent = new Intent(context, MatchRecordTeamDetailActivity.class);
    intent.putExtra("pk_type", pkType);
    intent.putExtra("pk_room_id", pkRoomId);
    intent.putExtra("user_group", userGroup);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_match_team_record_detail;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMatchTeamRecordDetailBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    magic_indicator = binding.magicIndicator;
    viewPager2 = binding.viewPager2;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    pk_room_id = this.getIntent().getStringExtra("pk_room_id");
    user_group = this.getIntent().getStringExtra("user_group");
    //请求团队PK信息
    getPkDetail(pk_room_id);
  }

  private void initTabNav() {
    titles[0] = group_title1;
    titles[1] = group_title2;

    List<Fragment> fragments = new ArrayList<>();
    fragments.add(MatchRecordTeamFragment.newInstance(red_list));
    fragments.add(MatchRecordTeamFragment.newInstance(blue_list));

    viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager2.setAdapter(new XFragmentStateAdapter(this,fragments));

    CommonNavigator commonNavigator = new CommonNavigator(this);
    commonNavigator.setAdapter(new CommonNavigatorAdapter() {

      @Override
      public int getCount() {
        return titles == null ? 0 : titles.length;
      }

      @Override

      public IPagerTitleView getTitleView(Context context, final int index) {

        XTabTitleView commonPagerTitleView = new XTabTitleView(context);
        commonPagerTitleView.setText(titles[index]);
//        if(index==0){
//          commonPagerTitleView.setTipVisible(red_is_win);
//        }else{
//          commonPagerTitleView.setTipVisible(blue_is_win);
//        }

        commonPagerTitleView.setNormalColor(Color.parseColor("#ff767779"));
        commonPagerTitleView.setSelectedColor(Color.parseColor("#FDE833"));
        commonPagerTitleView.setTextSize(20);
        commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            viewPager2.setCurrentItem(index);
          }
        });
        return commonPagerTitleView;
      }

      @Override
      public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        indicator.setLineWidth(130);
        indicator.setColors(Color.parseColor("#FDE833"));
        return indicator;
      }
    });
    magic_indicator.setNavigator(commonNavigator);
    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(MatchRecordTeamDetailActivity.this, 15);
      }
    });

    final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magic_indicator);
    fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    fragmentContainerHelper.setDuration(300);
    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        fragmentContainerHelper.handlePageSelected(position);
      }
    });
  }

  /**
   * 团队PK详情
   * @param pk_room_id
   */
  private void getPkDetail(String pk_room_id) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("pk_room_id", pk_room_id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.myTeamPKInfo(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try {
              if(responseBody!=null){
                parseTeamResultBody(responseBody);
                initTabNav();
              }
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {

          }
        })
    );
  }

  private void parseTeamResultBody(ResponseBody responseBody) throws IOException, JSONException {
    JSONObject jsonObject = new JSONObject(responseBody.string());
    int code = jsonObject.optInt("code", 0);
    if(code != 0) {
      JSONObject dataObject = jsonObject.optJSONObject("data");
      if(dataObject != null){
        JSONObject listObject = dataObject.optJSONObject("list");
        if(listObject != null && listObject.optJSONObject("red")!=null){
          JSONObject itemObject = listObject.optJSONObject("red");
          String group_title = itemObject.optString("group_title");
          group_title1 = group_title;
          int is_win = itemObject.optInt("is_win",0);
          red_is_win = is_win;
          JSONArray list = itemObject.optJSONArray("list");
          red_list.clear();
          red_list.addAll(parseArray(list));
        }
        if(listObject != null && listObject.optJSONObject("blue") != null){
          JSONObject itemObject = listObject.optJSONObject("blue");
          String group_title = itemObject.optString("group_title");
          group_title2 = group_title;
          int is_win = itemObject.optInt("is_win", 0);
          blue_is_win = is_win;
          JSONArray list = itemObject.optJSONArray("list");
          blue_list.clear();
          blue_list.addAll(parseArray(list));
        }
      }
    }
  }

  private List<ListPkItem> parseArray(JSONArray list) {
    int len = list.length();
    List<ListPkItem> pkItems = new ArrayList<>();
    for(int i = 0; i < len; i++){

      ListPkItem pkItem=new ListPkItem();
      JSONObject item=list.optJSONObject(i);

      pkItem.setUser_pk_list_id(item.optString("user_pk_list_id"));
      pkItem.setDuration(item.optInt("duration",180));
      pkItem.setUser_group(item.optString("user_group"));
      pkItem.setGroup_win(item.optString("group_win"));
      pkItem.setDistance(item.optString("distance"));
      pkItem.setSpeed_max(item.optInt("speed_max"));
      pkItem.setUser_id(item.optString("user_id"));
      pkItem.setUser_name(item.optString("user_name"));
      pkItem.setPk_type(item.optInt("pk_type"));
      pkItem.setCreated_time(item.optInt("created_time"));
      pkItem.setUser_img(item.optString("user_img"));
      pkItem.setStart_date(item.optString("start_date"));

      pkItems.add(pkItem);
    }

    return pkItems;
  }
}
