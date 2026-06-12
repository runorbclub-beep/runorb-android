package com.cloud.runball.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import com.cloud.runball.R;
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: XTabTitleView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/1 19:17
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/1 19:17
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class XTabTitleView extends FrameLayout implements IMeasurablePagerTitleView{

    protected int mSelectedColor;
    protected int mNormalColor;

    View customLayout;
    ImageView titleImg;
    TextView titleText;
    public XTabTitleView(Context context) {
        super(context);
        // load custom layout
        customLayout = LayoutInflater.from(context).inflate(R.layout.item_match_team_tab, null);
        setContentView(customLayout);
        titleImg = (ImageView) customLayout.findViewById(R.id.ivWin);
        titleText = (TextView) customLayout.findViewById(R.id.tvTitle);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        if(mSelectedColor!=0){
            titleText.setTextColor(mSelectedColor);
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        if(mNormalColor!=0){
            titleText.setTextColor(mNormalColor);
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        //customLayout.setScaleX(1.3f + (0.8f - 1.3f) * leavePercent);
        //customLayout.setScaleY(1.3f + (0.8f - 1.3f) * leavePercent);

        //customLayout.setScaleX(0.8f + (1.3f - 0.8f) * leavePercent);
        //customLayout.setScaleY(0.8f + (1.3f - 0.8f) * leavePercent);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        //customLayout.setScaleX(0.8f + (1.3f - 0.8f) * enterPercent);
        //customLayout.setScaleY(0.8f + (1.3f - 0.8f) * enterPercent);
    }

    @Override
    public int getContentLeft() {
        return getLeft();
    }

    @Override
    public int getContentTop() {
        return getTop();
    }

    @Override
    public int getContentRight() {
        return getRight();
    }

    @Override
    public int getContentBottom() {
        return getBottom();
    }

    /**
     * 外部直接将布局设置进来
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        setContentView(contentView, null);
    }

    public void setContentView(View contentView, FrameLayout.LayoutParams lp) {
        removeAllViews();
        if (contentView != null) {
            if (lp == null) {
                lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            addView(contentView, lp);
        }
    }

    public void setContentView(int layoutId) {
        View child = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setContentView(child, null);
    }

    public void setText(String content){
        titleText.setText(content);
    }

    public void setTextSize(float size){
        titleText.setTextSize(size);
    }

    public int getSelectedColor() {
        return mSelectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        mSelectedColor = selectedColor;
    }

    public int getNormalColor() {
        return mNormalColor;
    }

    public void setNormalColor(int normalColor) {
        mNormalColor = normalColor;
    }

    public void setTipVisible(int is_win){
        if(is_win>0){
            titleImg.setVisibility(View.VISIBLE);
        }else{
            titleImg.setVisibility(View.GONE);
        }
    }

}
