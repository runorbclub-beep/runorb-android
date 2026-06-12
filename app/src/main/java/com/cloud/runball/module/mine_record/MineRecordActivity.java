package com.cloud.runball.module.mine_record;

import android.content.Context;
import android.content.Intent;
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
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.module.mine.MineScoreActivity;
import com.cloud.runball.utils.ResourceUtils;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cloud.runball.databinding.ActivityMineRecordBinding;

public class MineRecordActivity extends BaseActivity {

  private ActivityMineRecordBinding binding;

  Toolbar toolbar;
  MagicIndicator magicIndicator;
  ViewPager2 viewPager;

  private List<Fragment> fragments;

  public static void startAction(Context context, Date date, boolean isFullMonth) {
    Intent intent = new Intent(context, MineRecordActivity.class);
    intent.putExtra("date", date);
    intent.putExtra("isFullMonth", isFullMonth);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_mine_record;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityMineRecordBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    magicIndicator = binding.magicIndicator;
    viewPager = binding.viewPager;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });

    // wire click listeners for header buttons
    View tvToTotal = findViewById(R.id.tvToTotal);
    if (tvToTotal != null) tvToTotal.setOnClickListener(this::onClick);

    fragments = new ArrayList<>();
    String[] titles = { getString(R.string.tab_play_data), getString(R.string.tab_play_mode) };

    Intent intent = getIntent();
    Date playDataDate = (Date) intent.getSerializableExtra("date");
    boolean isFullMonth = intent.getBooleanExtra("isFullMonth", false);

    fragments.add(MinePlayDataFragment.newInstance(playDataDate, isFullMonth));
    fragments.add(MinePlayModeFragment.newInstance());

    viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager.setAdapter(new XFragmentStateAdapter(this, fragments));

    CommonNavigator commonNavigator = new CommonNavigator(this);
    commonNavigator.setAdapter(new XCommonNavigatorAdapter(titles, index -> {
      viewPager.setCurrentItem(index);
    }));
    magicIndicator.setNavigator(commonNavigator);
    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(MineRecordActivity.this, 15);
      }
    });
    final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magicIndicator);
    fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
    fragmentContainerHelper.setDuration(300);
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
      @Override
      public void onPageSelected(int position) {
        fragmentContainerHelper.handlePageSelected(position);
      }
    });
  }

  public void onClick(View view) {
    if (view.getId() == R.id.tvToTotal) {
      Intent it = new Intent(this, MineScoreActivity.class);
      startActivity(it);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    for (Fragment fragment : fragments) {
      fragment.onActivityResult(requestCode, resultCode, data);
    }
  }
}
