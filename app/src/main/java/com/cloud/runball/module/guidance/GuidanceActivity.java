package com.cloud.runball.module.guidance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.XFragmentStateAdapter;
import com.cloud.runball.basecomm.page.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import com.cloud.runball.databinding.ActivityGuidanceBinding;

public class GuidanceActivity extends BaseActivity {
  private ActivityGuidanceBinding binding;
  Toolbar toolbar;
  ViewPager2 viewPager;
  TextView tvPageIndex;

  public static void startAction(Context context) {
    Intent intent = new Intent(context, GuidanceActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected int onLayoutId() {
    return R.layout.activity_guidance;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityGuidanceBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    viewPager = binding.viewPager;
    tvPageIndex = binding.tvPageIndex;
    toolbar.setNavigationOnClickListener(v -> {
      finish();
    });
    initGuidanceList();
  }

  private void initGuidanceList() {
    List<Fragment> fragments = new ArrayList<>();
    fragments.add(GuidanceFragment.startAction(R.mipmap.guidance_01));
    fragments.add(GuidanceFragment.startAction(R.mipmap.guidance_02));
    fragments.add(GuidanceFragment.startAction(R.mipmap.guidance_03));
    fragments.add(GuidanceFragment.startAction(R.mipmap.guidance_04));
    fragments.add(GuidanceFragment.startAction(R.mipmap.guidance_05));
    fragments.add(GuidanceFragment.startAction(R.mipmap.guidance_06));
    fragments.add(GuidanceFragment.startAction(R.mipmap.guidance_07));

    XFragmentStateAdapter adapter = new XFragmentStateAdapter(this, fragments);

    viewPager.setAdapter(adapter);
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      @Override
      public void onPageSelected(int position) {
        super.onPageSelected(position);
        tvPageIndex.setText(getString(R.string.format_guidance_page_index, position + 1 + "", fragments.size() + ""));
      }
    });
    tvPageIndex.setText(getString(R.string.format_guidance_page_index, "1", fragments.size() + ""));


  }

}
