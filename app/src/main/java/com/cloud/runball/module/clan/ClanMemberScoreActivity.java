package com.cloud.runball.module.clan;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.model.ClanMemberRankModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import com.cloud.runball.databinding.ActivtyClanMemberScoreBinding;

public class ClanMemberScoreActivity extends BaseActivity {

  private ActivtyClanMemberScoreBinding binding;
  Toolbar toolbar;

  TextView tvClanName;

  TextView tvMemberCount;

  MagicIndicator indicator;

  ViewPager2 viewPager;

  private String clanId;

  protected WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  public static void startAction(Context context, String clanId) {
    Intent intent = new Intent(context, ClanMemberScoreActivity.class);
    intent.putExtra("clanId", clanId);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activty_clan_member_score;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivtyClanMemberScoreBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    tvClanName = binding.tvClanName;
    tvMemberCount = binding.tvMemberCount;
    indicator = binding.indicator;
    viewPager = binding.viewPager;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    Intent intent = getIntent();
    if (intent == null) {
      finish();
      return;
    }

    clanId = intent.getStringExtra("clanId");

    initTabPage();
    showTabData();
  }

  /**
   * 初始化排行榜分页组件
   */
  private void initTabPage() {
    viewPager.setAdapter(new XFragmentStateAdapter(this));

    XCommonNavigatorAdapter xCommonNavigatorAdapter = new XCommonNavigatorAdapter();
    xCommonNavigatorAdapter.setRankItemClickListener(index -> {
      viewPager.setCurrentItem(index);
    });

    CommonNavigator commonNavigator = new CommonNavigator(this);
    commonNavigator.setAdjustMode(true);
    commonNavigator.setAdapter(xCommonNavigatorAdapter);
    indicator.setNavigator(commonNavigator);

    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(ClanMemberScoreActivity.this, 15);
      }
    });

    FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(indicator);
    fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    fragmentContainerHelper.setDuration(300);
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        fragmentContainerHelper.handlePageSelected(position);
      }
    });
  }

  /**
   * 填充个人排行榜分页数据
   */
  private void showTabData() {
    List<Fragment> fragments = new ArrayList<>();
    String[] titles = {
        getString(R.string.lbl_match_tab_rank_5),
        getString(R.string.lbl_match_tab_rank_3),
        getString(R.string.lbl_match_tab_rank_1),
        getString(R.string.lbl_match_tab_rank_4)
    };
    fragments.add(ClanMemberRankFragment.newInstance(clanId, 1));
    fragments.add(ClanMemberRankFragment.newInstance(clanId, 2));
    fragments.add(ClanMemberRankFragment.newInstance(clanId, 3));
    fragments.add(ClanMemberRankFragment.newInstance(clanId, 4));

    CommonNavigator commonNavigator = (CommonNavigator) indicator.getNavigator();
    XCommonNavigatorAdapter xCommonNavigatorAdapter = (XCommonNavigatorAdapter) commonNavigator.getAdapter();
    xCommonNavigatorAdapter.setTitleViewText(titles);
    xCommonNavigatorAdapter.notifyDataSetChanged();

    XFragmentStateAdapter fragmentStateAdapter = (XFragmentStateAdapter) viewPager.getAdapter();
    if (fragmentStateAdapter != null) {
      fragmentStateAdapter.setFragments(fragments);
      fragmentStateAdapter.notifyDataSetChanged();
    }
  }

  public void setTop(ClanMemberRankModel.ClanInfo clanInfo, ClanMemberRankModel.ClanAvg clanAvg) {
    tvClanName.setText(clanInfo.getTitle());
    tvMemberCount.setText(getString(R.string.association_match_join_sum, clanAvg.getUserCount() + ""));
  }


}
