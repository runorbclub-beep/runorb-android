package com.cloud.runball.view.magic_indicator;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.cloud.runball.basecomm.listener.TabItemClickListener;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.adapter
 * @ClassName: XCommonNavigatorAdapter
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/18 16:21
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/18 16:21
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SpotNavigatorAdapter extends CommonNavigatorAdapter {

  private float mSize = 14f;
  private float mLineWidth = 130f;
  private int mNormalColor = Color.parseColor("#ff767779");
  private int mSelectedColor = Color.parseColor("#FDE833");
  private String[] titles = null;
  private String[] spotInfo = null;
  private TabItemClickListener itemClickListener;

  public SpotNavigatorAdapter() {

  }

  public SpotNavigatorAdapter(String[] tles){
    titles = tles;
  }

  public SpotNavigatorAdapter(String[] tles, TabItemClickListener listener) {
    titles = tles;
    this.itemClickListener=listener;
  }

  public void setRankItemClickListener(TabItemClickListener listener) {
    this.itemClickListener = listener;
  }


  public void setTextSize(float size){
    this.mSize = size;
  }

  public void setLineWidth(float lineWidth) {
    mLineWidth = lineWidth;
  }

  public void setSelectedColor(int selectedColor) {
    mSelectedColor = selectedColor;
  }

  public void setNormalColor(int normalColor) {
    mNormalColor = normalColor;
  }


  @Override
  public int getCount() {
    return titles == null ? 0 : titles.length;
  }

  public void setTitleViewText(String[] titles, String[] spotInfo) {
    this.titles = titles;
    this.spotInfo = spotInfo;
    notifyDataSetChanged();
  }

  public void setSpot(int index, String info) {
    spotInfo[index] = info;
    notifyDataSetChanged();
  }

  @Override
  public IPagerTitleView getTitleView(Context context, int index) {
    SpotMagicTitleView spotMagicTitleView = new SpotMagicTitleView(context);
    spotMagicTitleView.setNormalColor(mNormalColor);
    spotMagicTitleView.setSelectedColor(mSelectedColor);
    spotMagicTitleView.setTitle(titles[index]);
    spotMagicTitleView.setInfo(spotInfo[index]);
    spotMagicTitleView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(itemClickListener!=null){
          itemClickListener.onTabItemClick(index);
        }
      }
    });
    return spotMagicTitleView;
  }

  @Override
  public IPagerIndicator getIndicator(Context context) {
    LinePagerIndicator indicator = new LinePagerIndicator(context);
    indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
    indicator.setLineWidth(mLineWidth);
    indicator.setColors(mSelectedColor);
    return indicator;
  }
}
