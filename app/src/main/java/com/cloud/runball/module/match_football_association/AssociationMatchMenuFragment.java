package com.cloud.runball.module.match_football_association;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.BannerData;
import com.cloud.runball.model.BannerModel;
import com.cloud.runball.module.match.MatchDetailActivity;
import com.cloud.runball.module.match_football_association.adapter.BannerAdapter;
import com.cloud.runball.module.match_football_association.entity.MatchPageSettingsInfo;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.to.aboomy.pager2banner.Banner;
import com.to.aboomy.pager2banner.ScaleInTransformer;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import com.cloud.runball.databinding.FragmentAssociationMatchMenuBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AssociationMatchMenuFragment extends BaseFragment {

  private FragmentAssociationMatchMenuBinding binding;
  Toolbar toolbar;

  Banner banner;

  MagicIndicator magicIndicator;

  ViewPager2 viewPager;

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_association_match_menu;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentAssociationMatchMenuBinding.bind(view);
    toolbar = binding.toolbar;
    banner = binding.banner;
    magicIndicator = binding.magicIndicator;
    viewPager = binding.viewPager;
    toolbar.setNavigationOnClickListener(v -> {
      Activity activity = getActivity();
      if (activity != null) {
        activity.finish();
      }
    });
    initBanner();
    initMatchPage();
  }

  @Override
  protected void onLazyLoad() {
    loadBannersData();
  }

  private void initBanner(){
    //创建adapter
    BannerAdapter imageAdapter = new BannerAdapter(getContext());
    imageAdapter.setOnItemClickListener((view, data) -> {
      if(!TextUtils.isEmpty(data.getBanner_matchs_id())){


        if (data.getIs_quartets() == 1) {
          AssociationMatchDetailActivity.startAction(this.getContext(), data.getBanner_matchs_id());
        } else {
          MatchDetailActivity.startAction(getContext(), data.getBanner_matchs_id());
        }

      }
    });
    //设置无限轮播
    banner.setAutoTurningTime(5000);
    banner.setAdapter(imageAdapter);
    banner.setOuterPageChangeListener(new ViewPager2.OnPageChangeCallback() {
      @Override
      public void onPageSelected(int position) {


      }
    });

//    banner.addPageTransformer(new ScaleInTransformer());
  }

  private void initMatchPage() {
    List<MatchPageSettingsInfo> matchPageSettingsInfoList = new ArrayList<>();
    matchPageSettingsInfoList.add(new MatchPageSettingsInfo(AssociationMatchMenuSubFragment.MATCH_STATUS_ALL, getString(R.string.association_match_status_all)));
    matchPageSettingsInfoList.add(new MatchPageSettingsInfo(AssociationMatchMenuSubFragment.MATCH_STATUS_NEAR, getString(R.string.association_match_status_near)));
    matchPageSettingsInfoList.add(new MatchPageSettingsInfo(AssociationMatchMenuSubFragment.MATCH_STATUS_PREVIOUS, getString(R.string.association_match_status_previous)));

    String[] titles = new String[matchPageSettingsInfoList.size()];
    List<Fragment> fragments = new ArrayList<>();

    for (int i = 0; i < matchPageSettingsInfoList.size(); i++) {
      MatchPageSettingsInfo info = matchPageSettingsInfoList.get(i);
      fragments.add(AssociationMatchMenuSubFragment.newInstance(info.getMatchStatus()));
      titles[i] = info.getMatchStatusTitle();
    }

    viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager.setAdapter(new XFragmentStateAdapter(this, fragments));

    CommonNavigator commonNavigator = new CommonNavigator(getContext());
    commonNavigator.setAdjustMode(true);
    commonNavigator.setAdapter(new XCommonNavigatorAdapter(titles, index -> {
      viewPager.setCurrentItem(index);
    }));
    magicIndicator.setNavigator(commonNavigator);
    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(getContext(), 15);
      }
    });
    final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magicIndicator);
    fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    fragmentContainerHelper.setDuration(1000);
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        fragmentContainerHelper.handlePageSelected(position);
      }
    });
  }

  private void loadBannersData(){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    Observable<BannerModel> observable = apiServer.banner();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<BannerModel>() {
              @Override
              public void onSuccess(BannerModel bannerModel) {
                BannerAdapter bannerAdapter = (BannerAdapter) banner.getAdapter();
                if(bannerAdapter != null){
                  if(bannerModel.getList().size() == 1) {
                    List<BannerData> datas = new ArrayList<>();
                    datas.addAll(bannerModel.getList());
                    datas.addAll(bannerModel.getList());
                    bannerAdapter.setData(datas);
                  }else if(bannerModel.getList().size() > 1) {
                    bannerAdapter.setData(bannerModel.getList());
                  }
                  bannerAdapter.notifyDataSetChanged();
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("-----Banner列表--------" + msg);
              }
            }
        )
    );
  }

}
