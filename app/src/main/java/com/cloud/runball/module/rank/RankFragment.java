package com.cloud.runball.module.rank;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.bean.RankTypeInfo;
import com.cloud.runball.bean.banner.BannerMatchInfo;
import com.cloud.runball.bean.banner.RankBannerData;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.module.WebActivity;
import com.cloud.runball.module.match.MatchDetailActivity;
import com.cloud.runball.module.match_football_association.AssociationMatchDetailActivity;
import com.cloud.runball.module.rank.adapter.RankBannerAdapter;
import com.cloud.runball.module.mine.RankingSwitchActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AccountUtil;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.ResourceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.to.aboomy.pager2banner.Banner;
import com.to.aboomy.pager2banner.ScaleInTransformer;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentMineMatchBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class RankFragment extends BaseFragment {
  private FragmentMineMatchBinding binding;
  TextView tvArea;
  ImageView tvMenu;
  LinearLayout layContent;
  Banner banner;
  TextView tabPersonalRank;
  TextView tabClanRank;
  MagicIndicator personalIndicator;
  ViewPager2 personalViewPager;
  MagicIndicator teamIndicator;
  ViewPager2 teamViewPager;

  private final WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  private String user_age_type = "";
  private String user_type = "";
  private String address = "";
  private String sys_sex_id = "";

  private final ActivityResultLauncher<Intent> startActivityLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    if(result.getResultCode() == 100) {
      Intent intent = result.getData();
      if (intent != null) {
        user_age_type = intent.getStringExtra("user_age_type");
        user_type = intent.getStringExtra("user_type");
        address = intent.getStringExtra("address");
        sys_sex_id = intent.getStringExtra("sys_sex_id");
        tvArea.setText(getString(R.string.format_brackets, intent.getStringExtra("title")));
      }
      loadPersonalTabData();
    }
  });

  public static RankFragment newInstance() {
    return new RankFragment();
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_mine_match;
  }

  @Override
  protected View getImmersiveView() {
    return layContent;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentMineMatchBinding.bind(view);
    tvArea = binding.tvArea;
    tvMenu = binding.tvMenu;
    layContent = binding.layContent;
    banner = binding.banner;
    tabPersonalRank = binding.tabPersonalRank;
    tabClanRank = binding.tabClanRank;
    personalIndicator = binding.personalIndicator;
    personalViewPager = binding.personalViewPager;
    teamIndicator = binding.teamIndicator;
    teamViewPager = binding.teamViewPager;

    tvMenu.setOnClickListener(this::onClick);
    binding.ivSearch.setOnClickListener(this::onClick);
    binding.tabPersonalRank.setOnClickListener(this::onClick);
    binding.tabClanRank.setOnClickListener(this::onClick);

    adaptImmersiveStatusBar();
    EventBus.getDefault().register(this);

    if (AppDataManager.getInstance().getUserInfoModel() != null) {
      sys_sex_id = AppDataManager.getInstance().getUserInfoModel().getUser_info().getSys_sex_id();
    }

    if (BuildConfig.FLAVOR.equals("googleplay")) {
      if (SexConstant.SEX_MAN.equals(sys_sex_id)) {
        tvArea.setText(getString(R.string.format_brackets, getString(R.string.global_general_list_man)));
      } else if (SexConstant.SEX_WOMEN.equals(sys_sex_id)) {
        tvArea.setText(getString(R.string.format_brackets, getString(R.string.global_general_list_women)));
      } else  {
        tvArea.setText(getString(R.string.format_brackets, getString(R.string.global_general_list)));
      }
    } else {
      if (SexConstant.SEX_MAN.equals(sys_sex_id)) {
        tvArea.setText(getString(R.string.format_brackets, getString(R.string.national_general_list_man)));
      } else if (SexConstant.SEX_WOMEN.equals(sys_sex_id)) {
        tvArea.setText(getString(R.string.format_brackets, getString(R.string.national_general_list_women)));
      } else {
        tvArea.setText(getString(R.string.format_brackets, getString(R.string.national_general_list)));
      }
    }

  }

  @Override
  protected void onLazyLoad() {
    initBanner();
    loadBannerData();

    initPersonalRankTabPage();
    initTeamRankTabPage();
    switchPersonalRankTab();
  }

//  private String token = null;

  @Override
  protected void onFragmentShow() {
//    UserInfoModel userInfoModel = AppDataManager.getInstance().getUserInfoModel();
//    if (userInfoModel != null && userInfoModel.getUser_info() != null) {
//      String newToken = userInfoModel.getUser_info().getToken();
//      if (!TextUtils.isEmpty(newToken) && !newToken.equals(token)) {
//        token = newToken;
//        //请求榜单列表
//        loadPersonalTabPage();
//      }
//    } else {
//      loadPersonalTabPage();
//    }
  }

  public void onClick(View view) {
    if (view.getId() == R.id.tvMenu) {
      if (AccountUtil.isUserAccount()) {
        Intent intent = new Intent(RankFragment.this.getContext(), RankingSwitchActivity.class);
        startActivityLaunch.launch(intent);
      } else {
        Toast.makeText(RankFragment.this.getContext(), getString(R.string.tip_no_tourist_enter_rank), Toast.LENGTH_SHORT).show();
      }
    } else if (view.getId() == R.id.ivSearch) {
      SearchRankActivity.startAction(RankFragment.this.getContext());
    } else if (view.getId() == R.id.tabPersonalRank) {
      switchPersonalRankTab();
    } else if (view.getId() == R.id.tabClanRank) {
      switchTeamRankTab();
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(MessageEvent event) {
    if (event.getEvetId() == MessageEvent.REFRESH_RANK_LIST) {
      loadPersonalTabData();
    }
  }

  /**
   * 初始化banner组件
   */
  private void initBanner(){
    //创建adapter
    RankBannerAdapter imageAdapter = new RankBannerAdapter();
    imageAdapter.setOnItemClickListener((view, data) -> {
      if (data.getBannerType() == 1) {
        // 网页跳转
        Intent intent = new Intent(RankFragment.this.getContext(), WebActivity.class);
        intent.putExtra("url", data.getJumpLink());
        startActivity(intent);
      } else if (data.getBannerType() == 2) {
        // 赛事跳转
        String jumpLink = data.getJumpLink();
        if (TextUtils.isEmpty(jumpLink)) {
          return;
        }
        jumpLink = "{" + jumpLink.substring(jumpLink.indexOf("?") + 1).replaceAll("=", ":").replaceAll("&", ",") + "}";
        BannerMatchInfo matchInfo = new Gson().fromJson(jumpLink, BannerMatchInfo.class);
        if (matchInfo.getIsQuartets() == 1) {
          AssociationMatchDetailActivity.startAction(RankFragment.this.getContext(), matchInfo.getSysMatchId());
        } else {
          MatchDetailActivity.startAction(RankFragment.this.getContext(), matchInfo.getSysMatchId());
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

  /**
   * 加载banner组件数据
   */
  private void loadBannerData() {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(new HashMap<>()).toString());
    Observable<List<RankBannerData>> observable = apiServer.getBannerList(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<List<RankBannerData>>() {
              @Override
              public void onSuccess(List<RankBannerData> model) {
                RankBannerAdapter bannerAdapter = (RankBannerAdapter) banner.getAdapter();
                if(bannerAdapter != null) {
                  bannerAdapter.setData(model);
                  bannerAdapter.notifyDataSetChanged();
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d(msg);
              }
            })
    );
  }

  /**
   * 初始化排行榜分页组件
   */
  private void initPersonalRankTabPage() {
    personalViewPager.setAdapter(new XFragmentStateAdapter(this));

    XCommonNavigatorAdapter xCommonNavigatorAdapter = new XCommonNavigatorAdapter();
    xCommonNavigatorAdapter.setRankItemClickListener(index -> {
      personalViewPager.setCurrentItem(index);
    });

    CommonNavigator commonNavigator = new CommonNavigator(RankFragment.this.getContext());
    commonNavigator.setAdjustMode(true);
    commonNavigator.setAdapter(xCommonNavigatorAdapter);
    personalIndicator.setNavigator(commonNavigator);

    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(RankFragment.this.getContext(), 15);
      }
    });

    FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(personalIndicator);
    fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    fragmentContainerHelper.setDuration(300);
    personalViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        fragmentContainerHelper.handlePageSelected(position);
      }
    });
  }

  /**
   * 初始化排行榜分页组件
   */
  private void initTeamRankTabPage() {
    teamViewPager.setAdapter(new XFragmentStateAdapter(this));

    XCommonNavigatorAdapter xCommonNavigatorAdapter = new XCommonNavigatorAdapter();
    xCommonNavigatorAdapter.setRankItemClickListener(index -> {
      teamViewPager.setCurrentItem(index);
    });

    CommonNavigator commonNavigator = new CommonNavigator(RankFragment.this.getContext());
    commonNavigator.setAdjustMode(true);
    commonNavigator.setAdapter(xCommonNavigatorAdapter);
    teamIndicator.setNavigator(commonNavigator);

    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(RankFragment.this.getContext(), 15);
      }
    });

    FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(teamIndicator);
    fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    fragmentContainerHelper.setDuration(300);
    teamViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        fragmentContainerHelper.handlePageSelected(position);
      }
    });
  }

  /**
   * 切换个人排行榜
   */
  private void switchPersonalRankTab() {
    tabPersonalRank.setTextColor(Color.parseColor("#F7DC29"));
    tabClanRank.setTextColor(Color.parseColor("#888888"));

    personalIndicator.setVisibility(View.VISIBLE);
    personalViewPager.setVisibility(View.VISIBLE);
    teamIndicator.setVisibility(View.GONE);
    teamViewPager.setVisibility(View.GONE);

    tvArea.setVisibility(View.VISIBLE);
    tvMenu.setVisibility(View.VISIBLE);

    loadPersonalTabData();
  }

  /**
   * 切换团队排行榜
   */
  private void switchTeamRankTab() {
    tabPersonalRank.setTextColor(Color.parseColor("#888888"));
    tabClanRank.setTextColor(Color.parseColor("#F7DC29"));

    personalIndicator.setVisibility(View.GONE);
    personalViewPager.setVisibility(View.GONE);
    teamIndicator.setVisibility(View.VISIBLE);
    teamViewPager.setVisibility(View.VISIBLE);

    tvArea.setVisibility(View.GONE);
    tvMenu.setVisibility(View.GONE);

    loadClanTabData();
  }

  /**
   * 加载个人排行榜分页数据
   */
  private void loadPersonalTabData() {
    XFragmentStateAdapter fragmentStateAdapter = (XFragmentStateAdapter) personalViewPager.getAdapter();
    if (fragmentStateAdapter == null) {
      initPersonalRankTabPage();
    } else {
      List<Fragment> fragments = fragmentStateAdapter.getFragments();
      if (fragments != null && fragments.size() > 0) {
//        return;
      }
    }

    Observable<ResponseBody> observable = apiServer.requestRankTypes();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
              @Override
              public void onSuccess(ResponseBody responseBody) {
                try {
                  JSONObject jsonObject = new JSONObject(responseBody.string());
                  if(jsonObject.optInt("code") == 1) {
                    Gson gson = new Gson();
                    List<RankTypeInfo> rankTypeInfos = gson.fromJson(jsonObject.optString("data"), new TypeToken<List<RankTypeInfo>>(){}.getType());
                    showPersonalTabData(rankTypeInfos);
                  }
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d(msg);
              }
            }
        )
    );
  }

  /**
   * 填充个人排行榜分页数据
   * @param data 分页数据
   */
  private void showPersonalTabData(List<RankTypeInfo> data) {
    boolean isZh = ResourceUtils.isZhCn(RankFragment.this.getContext());
    List<Fragment> fragments = new ArrayList<>();
    String[] titles = new String[data.size()];
    int index = 0;
    for(RankTypeInfo typeInfo : data) {
      fragments.add(PersonalRankFragment.newInstance(typeInfo.getType(), user_age_type, user_type, address, sys_sex_id, null, true));
      titles[index] = isZh ? typeInfo.getTitle_zh() : typeInfo.getTitle_en();
      index++;
    }

    CommonNavigator commonNavigator = (CommonNavigator) personalIndicator.getNavigator();
    XCommonNavigatorAdapter xCommonNavigatorAdapter = (XCommonNavigatorAdapter) commonNavigator.getAdapter();
    xCommonNavigatorAdapter.setTitleViewText(titles);
    xCommonNavigatorAdapter.notifyDataSetChanged();

    XFragmentStateAdapter fragmentStateAdapter = (XFragmentStateAdapter) personalViewPager.getAdapter();
    if (fragmentStateAdapter != null) {
      fragmentStateAdapter.setFragments(fragments);
      fragmentStateAdapter.notifyDataSetChanged();
    }
  }

  /**
   * 加载俱乐部排行榜分页数据
   */
  private void loadClanTabData() {
    XFragmentStateAdapter fragmentStateAdapter = (XFragmentStateAdapter) teamViewPager.getAdapter();
    if (fragmentStateAdapter == null) {
      initTeamRankTabPage();
    } else {
      List<Fragment> fragments = fragmentStateAdapter.getFragments();
      if (fragments != null && fragments.size() > 0) {
        return;
      }
    }

    Observable<ResponseBody> observable = apiServer.requestRankTypes();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ResponseBody>() {
             @Override
             public void onSuccess(ResponseBody responseBody) {
               try {
                 JSONObject jsonObject = new JSONObject(responseBody.string());
                 if(jsonObject.optInt("code") == 1) {
                   Gson gson = new Gson();
                   List<RankTypeInfo> rankTypeInfos = gson.fromJson(jsonObject.optString("data"), new TypeToken<List<RankTypeInfo>>(){}.getType());
                   showClanTabData(rankTypeInfos);
                 }
               } catch (Exception ex) {
                 ex.printStackTrace();
               }
             }
             @Override
             public void onError(int code, String msg) {
               AppLogger.d(msg);
             }
           }
          )
    );
  }

  /**
   * 填充团队排行榜分页数据
   */
  private void showClanTabData(List<RankTypeInfo> data) {
    boolean isZh = ResourceUtils.isZhCn(RankFragment.this.getContext());
    List<Fragment> fragments = new ArrayList<>();
    String[] titles = new String[data.size()];
    int index = 0;
    for(RankTypeInfo typeInfo : data) {
      fragments.add(ClanRankFragment.newInstance(typeInfo.getType(), null, true));
      titles[index] = isZh ? typeInfo.getTitle_zh() : typeInfo.getTitle_en();
      index++;
    }

    CommonNavigator commonNavigator = (CommonNavigator) teamIndicator.getNavigator();
    XCommonNavigatorAdapter xCommonNavigatorAdapter = (XCommonNavigatorAdapter) commonNavigator.getAdapter();
    xCommonNavigatorAdapter.setTitleViewText(titles);
    xCommonNavigatorAdapter.notifyDataSetChanged();

    XFragmentStateAdapter fragmentStateAdapter = (XFragmentStateAdapter) teamViewPager.getAdapter();
    if (fragmentStateAdapter != null) {
      fragmentStateAdapter.setFragments(fragments);
      fragmentStateAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (teamViewPager == null) {
      return;
    }
    XFragmentStateAdapter fragmentStateAdapter = (XFragmentStateAdapter) teamViewPager.getAdapter();
    if (fragmentStateAdapter != null) {
      List<Fragment> fragments = fragmentStateAdapter.getFragments();
      if (fragments != null) {
        for (Fragment fragment: fragments) {
          if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
          }
        }
      }
    }
    loadPersonalTabData();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
  }

}
