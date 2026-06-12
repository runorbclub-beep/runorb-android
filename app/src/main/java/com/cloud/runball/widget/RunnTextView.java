package com.cloud.runball.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

/**
 * 金币滚动控件
 * activityAnimtestTv.setMoney(Float.valueOf(activityAnimtestEt.getText().toString().trim()));
 */
public class RunnTextView extends androidx.appcompat.widget.AppCompatTextView {
    private ValueAnimator mValueAnimator;
    private DecimalFormat mDf;

    public RunnTextView(Context context) {
        this(context,null);
    }
    public RunnTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public RunnTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        //格式化小数（保留小数点后两位）
        mDf = new DecimalFormat("0.00");
        initAnim();
    }
    /**
     * 初始化动画
     */
    private void initAnim() {
        mValueAnimator = ValueAnimator.ofFloat(0,0);
        mValueAnimator.setDuration(1000);//动画时间为1秒
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if(value>0){
                    setText(mDf.format((float) animation.getAnimatedValue()));//当数值大于0的时候才赋值
                }
            }
        });
    }
    /**
     * 设置要显示的金钱
     * @param money
     */
    public void setMoney(float money){
        mValueAnimator.setFloatValues(0,money);
        mValueAnimator.start();
    }
    /**
     * 取消动画和动画监听
     */
    public void cancle(){
        mValueAnimator.removeAllUpdateListeners();
        mValueAnimator.cancel();
    }
}
