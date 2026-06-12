package com.cloud.runball.module.match;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.cloud.runball.App;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.listener.TabItemClickListener;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.cloud.runball.fragment.MatchSubFragment;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.module.match.adapter.ImageAdapter;
import com.cloud.runball.bean.BannerData;
import com.cloud.runball.bean.MineMatchItem;
import com.cloud.runball.model.BannerModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.CheckHelper;
import com.cloud.runball.R;
import com.to.aboomy.pager2banner.Banner;
import com.to.aboomy.pager2banner.IndicatorView;
import com.to.aboomy.pager2banner.ScaleInTransformer;
import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: 赛事
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/1/30 15:20
 * @UpdateUser: zhd
 * @UpdateDate: 2021/1/30 15:20
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchFragment extends Fragment implements View.OnClickListener, ImageAdapter.OnItemClickListener, TabItemClickListener {

  MagicIndicator magic_indicator;
  ViewPager2 viewPager2;
  Banner banner;
  EditText edtSearch;
  ImageAdapter imageAdapter;
  View vIndicator;

  /**
   * 使用内置Indicator
   */
  IndicatorView indicator;
  private boolean isFirstLoad = true;

  private CompositeDisposable disposable = new CompositeDisposable();

  public static MatchFragment newInstance() {
    return new MatchFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_match, container, false);
    initView(root);
    initBanner();
    return root;
  }

  private void initView(View root) {
    vIndicator = root.findViewById(R.id.vIndicator);
    magic_indicator = root.findViewById(R.id.magic_indicator);
    viewPager2 = root.findViewById(R.id.viewPager2);
    banner = root.findViewById(R.id.banner);
    edtSearch = root.findViewById(R.id.edtSearch);
    edtSearch.setOnClickListener(this);
  }

  private void initBanner(){
    indicator= new IndicatorView(getContext())
        .setIndicatorRatio(1f)
        .setIndicatorSelectedRatio(3f)
        .setIndicatorSelectedRadius(2f)
        .setIndicatorColor(Color.DKGRAY)
        .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_BIG_CIRCLE)
        .setIndicatorSelectorColor(Color.WHITE);

    //创建adapter
    imageAdapter = new ImageAdapter(getContext());
    imageAdapter.setOnItemClickListener(this);
    //设置无限轮播
    banner.setAutoTurningTime(5000);
    banner.setAdapter(imageAdapter);
    banner.setOuterPageChangeListener(new ViewPager2.OnPageChangeCallback() {
      @Override
      public void onPageSelected(int position) {


      }
    });

    banner.setPageMargin(AppUtils.dip2px(getContext(), 20), AppUtils.dip2px(getContext(), 10));
//    banner.addPageTransformer(new ScaleInTransformer());
  }

  @Override
  public void onResume(){
    super.onResume();
    if (isFirstLoad) {
      isFirstLoad = false;
      onLazyLoadData();
    }
    if(banner!=null){
      banner.setAutoPlay(true);
    }
  }

  private void onLazyLoadData(){
    showBanners();
    requestTypeList();
  }


  @Override
  public void onPause(){
    super.onPause();
    if(banner!=null){
      banner.setAutoPlay(false);
    }
  }

  @Override
  public void onDestroyView(){
    super.onDestroyView();
    vIndicator=null;
    indicator=null;
    isFirstLoad = true;
    banner=null;
    magic_indicator=null;
    edtSearch=null;
    imageAdapter=null;
    viewPager2=null;
    if (disposable != null) {
      disposable.dispose();
    }
  }

  /**
   * 请求赛事类型列表
   */
  private void requestTypeList(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<ResponseBody> observable = apiServer.matchTypeList();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody responseBody) {
            try{
              parseTypeList(responseBody);
            }catch (Exception ex){
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d("---requestTypeList-"+msg);
          }
        })
    );
  }

  /**
   * 赛事类型列表
   * @param responseBody
   * @throws Exception
   */
  private void parseTypeList(ResponseBody responseBody) throws Exception{
    JSONObject jsonObject=new JSONObject(responseBody.string());
    int code=jsonObject.optInt("code",0);
    if(code==1){
      JSONObject data=jsonObject.optJSONObject("data");
      JSONArray list =data.optJSONArray("list");
      int len=list.length();

      List<MineMatchItem> matchItems=new ArrayList<>();
      for(int index=0;index<len;index++){
        JSONObject item=list.optJSONObject(index);
        String match_event_id=item.optString("match_event_id");
        String match_event_title=item.optString("match_event_title");
        MineMatchItem mineMatchItem=new MineMatchItem(match_event_id,match_event_title);
        matchItems.add(mineMatchItem);
      }
      showTypeTabs(matchItems);
    }
  }



  public String getSearchContent(){
    return edtSearch.getText().toString().trim();
  }


  @Override
  public void onClick(View v) {
    if(v.getId()==R.id.edtSearch){
      onSearchRequested();
    }
  }

  private void onSearchRequested(){
    Intent it=new Intent(getContext(), SearchableActivity.class);
    startActivity(it);
  }

  @Override
  public void onItemClick(View view, BannerData data) {
    if(CheckHelper.onCheckFunc()==CheckHelper.PHONE){
      goToLogin();
    }else if(CheckHelper.onCheckFunc()==CheckHelper.NONE){
      Toast.makeText(App.self().getApplicationContext(),R.string.lbl_pk_net_error,Toast.LENGTH_LONG).show();
    }else{
      //Banner点击
      if(!TextUtils.isEmpty(data.getBanner_matchs_id())){
        requestMatchDetail(data.getBanner_matchs_id());
      }
    }
  }

  private void requestMatchDetail(String sys_match_id){
    Intent it= new Intent(getContext(), MatchDetailActivity.class);
    it.putExtra("sys_match_id",sys_match_id);
    startActivity(it);
  }

  private void showTypeTabs(List<MineMatchItem> typeList){
    if(typeList == null){
      return;
    }
    int listSize = typeList.size();
    if(listSize > 0){
      String[] titles = new String[listSize];
      List<Fragment> fragments = new ArrayList<>();
      int index = 0;
      for (MineMatchItem matchItem : typeList) {
        fragments.add(MatchSubFragment.newInstance(matchItem.getMatch_event_id(),index));
        titles[index] = matchItem.getMatch_event_title();
        index++;
      }
      viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
      viewPager2.setAdapter(new XFragmentStateAdapter(this, fragments));

      CommonNavigator commonNavigator = new CommonNavigator(getContext());
      commonNavigator.setAdapter(new XCommonNavigatorAdapter(titles,this));
      magic_indicator.setNavigator(commonNavigator);
      LinearLayout titleContainer = commonNavigator.getTitleContainer();
      titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
      titleContainer.setDividerDrawable(new ColorDrawable() {
        @Override
        public int getIntrinsicWidth() {
          return UIUtil.dip2px(getContext(), 15);
        }
      });
      final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magic_indicator);
      fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
      fragmentContainerHelper.setDuration(1000);
      viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
        @Override
        public void onPageSelected(int position) {
          fragmentContainerHelper.handlePageSelected(position);
        }
      });
    }
  }

  private void showBanners(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<BannerModel> observable = apiServer.banner();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<BannerModel>() {
          @Override
          public void onSuccess(BannerModel bannerModel) {
            if(imageAdapter!=null){
              if(bannerModel.getList().size()==1){
                List<BannerData> datas=new ArrayList<>();
                datas.addAll(bannerModel.getList());
                datas.addAll(bannerModel.getList());
                imageAdapter.setData(datas);
                vIndicator.setVisibility(View.VISIBLE);
              }else if(bannerModel.getList().size()>1){
                banner.setIndicator(indicator);
                vIndicator.setVisibility(View.GONE);
                imageAdapter.setData(bannerModel.getList());
              }
              imageAdapter.notifyDataSetChanged();
            }
          }
          @Override
          public void onError(int code, String msg) {
            AppLogger.d("-----Banner列表--------" + msg);
          }
        })
    );
  }



  /**
   * 打开用户手机号登录页面
   */
  private void goToLogin(){
    Intent it=new Intent(getActivity(), LoginOtherActivity.class);
    startActivityForResult(it,LoginOtherActivity.LoginOtherActivity_result);
  }

  @Override
  public void onTabItemClick(int index) {
    viewPager2.setCurrentItem(index);
  }
}
