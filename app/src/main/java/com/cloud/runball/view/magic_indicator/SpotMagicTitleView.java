package com.cloud.runball.view.magic_indicator;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cloud.runball.R;

import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

public class SpotMagicTitleView extends FrameLayout implements IPagerTitleView {

  protected int mSelectedColor;
  protected int mNormalColor;

  private TextView tvTitle;
  private TextView tvInfo;

  public SpotMagicTitleView(@NonNull Context context) {
    super(context);
    init(context);
  }

  private void init(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_spot_magic_title, this, false);
    tvTitle = view.findViewById(R.id.tvTitle);
    tvInfo = view.findViewById(R.id.tvInfo);
    addView(view);
  }

  public void setSelectedColor(int mSelectedColor) {
    this.mSelectedColor = mSelectedColor;
  }

  public void setNormalColor(int mNormalColor) {
    this.mNormalColor = mNormalColor;
  }

  public void setTitle(String title) {
    if (tvTitle == null) {
      return;
    }
    tvTitle.setText(title);
  }

  public void setInfo(String info) {
    if (tvInfo == null) {
      return;
    }
    if (TextUtils.isEmpty(info) || "0".equals(info)) {
      tvInfo.setVisibility(GONE);
      tvInfo.setText("");
    } else {
      tvInfo.setVisibility(VISIBLE);
      tvInfo.setText(info);
    }

  }

  @Override
  public void onSelected(int index, int totalCount) {

  }

  @Override
  public void onDeselected(int index, int totalCount) {

  }

  @Override
  public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
    int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
    if (tvTitle != null) {
      tvTitle.setTextColor(color);
    }
  }

  @Override
  public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
    int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
    if (tvTitle != null) {
      tvTitle.setTextColor(color);
    }
  }

}
