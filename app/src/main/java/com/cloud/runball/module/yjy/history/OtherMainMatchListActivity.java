package com.cloud.runball.module.yjy.history;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.LayoutInflater;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.XCommonNavigatorAdapter;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import com.cloud.runball.databinding.ActivityOtherMainMatchListBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.module.home
 * @ClassName: OtherMainMatchListActivity
 * @Description: 泡马比赛结果排行
 * @Author: zhd
 * @CreateDate: 2021/7/21 14:03
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/21 14:03
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class OtherMainMatchListActivity extends BaseActivity {

  private ActivityOtherMainMatchListBinding binding;
  TextView tvDate;

  MagicIndicator magicIndicator;

  ViewPager2 viewPager;

  String sysShakeId;
  String date;

  List<Fragment> fragments;

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_other_main_match_list;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityOtherMainMatchListBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void initView() {
    tvDate = binding.tvDate;
    magicIndicator = binding.magicIndicator;
    viewPager = binding.viewPager;
    sysShakeId = getIntent().getStringExtra("sys_shake_id");
    date = getIntent().getStringExtra("date");
    tvDate.setText("("+date+")");

    fragments = new ArrayList<>();
    fragments.add(YJYMatchResultListFragment.newInstance(sysShakeId, date));
    fragments.add(YJYMatchHelpListFragment.newInstance(sysShakeId, date));
    viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager.setAdapter(new XFragmentStateAdapter(this, fragments));

    CommonNavigator commonNavigator = new CommonNavigator(this);
    commonNavigator.setAdapter(new XCommonNavigatorAdapter(new String[]{ "比赛结果", "助力排行" }, index -> {
      viewPager.setCurrentItem(index);
    }));
    magicIndicator.setNavigator(commonNavigator);
    LinearLayout titleContainer = commonNavigator.getTitleContainer();
    titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    titleContainer.setDividerDrawable(new ColorDrawable() {
      @Override
      public int getIntrinsicWidth() {
        return UIUtil.dip2px(OtherMainMatchListActivity.this, 15);
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

  @Override
  protected void setOnResult() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.lbl_other_match);
  }


  public void onViewClicked(View v) {
    YJYMatchResultListFragment fragment = (YJYMatchResultListFragment) fragments.get(0);
    fragment.gg();
  }

  @Override
  protected void addListener() {
    View tvShare = findViewById(R.id.tvShare);
    if (tvShare != null) tvShare.setOnClickListener(this::onViewClicked);
  }



}
