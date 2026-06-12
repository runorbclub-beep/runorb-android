package com.cloud.runball.module.guidance;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;

import com.cloud.runball.databinding.ItemListGuidanceBinding;

public class GuidanceFragment extends BaseFragment {

  private ItemListGuidanceBinding binding;
  ImageView tvGuidance;

  public static GuidanceFragment startAction(int resId) {
    GuidanceFragment fragment = new GuidanceFragment();
    Bundle bundle = new Bundle();
    bundle.putInt("resId", resId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.item_list_guidance;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = ItemListGuidanceBinding.bind(view);
    tvGuidance = binding.tvGuidance;
    if (getArguments() == null) {
      return;
    }
    int resId = getArguments().getInt("resId");
    tvGuidance.setImageResource(resId);
  }

  @Override
  protected void onLazyLoad() {

  }
}
