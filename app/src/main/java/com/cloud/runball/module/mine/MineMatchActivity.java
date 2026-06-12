package com.cloud.runball.module.mine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.LayoutInflater;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.fragment.MatchRecordFragment;
import com.cloud.runball.fragment.MineMatchFragment;
import com.google.android.material.tabs.TabLayout;
import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import java.util.ArrayList;
import java.util.List;
import com.cloud.runball.databinding.ActivityMineMatchBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: MineMatchActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/12 10:10
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/12 10:10
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MineMatchActivity extends BaseActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener{

    private ActivityMineMatchBinding binding;
    MagicIndicator magic_indicator;
    ViewPager2 viewPager2;

    String[] titles = new String[2];

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_match;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMineMatchBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        magic_indicator = binding.magicIndicator;
        viewPager2 = binding.viewPager2;
        initTabNav();
    }

    @Override
    protected void setOnResult() {

    }

    private void initTabNav() {
        //对抗赛+锦标赛
        titles[0]=getString(R.string.lbl_match_tab_1);
        titles[1]=getString(R.string.lbl_match_tab_2);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MatchRecordFragment());
        fragments.add(new MineMatchFragment());

        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setAdapter(new FragmentStateAdapter(this){

            @Override
            public int getItemCount() {
                return fragments.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }
        });
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles == null ? 0 : titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#ff767779"));
                colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#FDE833"));
                colorTransitionPagerTitleView.setText(titles[index]);
                colorTransitionPagerTitleView.setTextSize(18);
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewPager2.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
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
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(MineMatchActivity.this, 15);
            }
        });
        //ViewPagerHelper.bind(magic_indicator, viewPager);
        final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magic_indicator);
        fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
        fragmentContainerHelper.setDuration(300);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       @Px int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                fragmentContainerHelper.handlePageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(@ViewPager2.ScrollState int state) {
            }
        });
    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_mine_match);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
