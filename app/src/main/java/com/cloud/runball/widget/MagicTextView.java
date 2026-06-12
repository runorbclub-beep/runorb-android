package com.cloud.runball.widget;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;


import com.cloud.runball.R;

import java.math.BigDecimal;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: MagicTextView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/6 18:46
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/6 18:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
@SuppressWarnings("AliMissingOverrideAnnotation")
public class MagicTextView extends androidx.appcompat.widget.AppCompatTextView {
    private Context mContext;

    // 递减/递增 的变量值
    private double mRate;
    // 当前显示的值
    private double mCurValue;
    // 当前变化后最终状态的目标值
    private double mGalValue;
    // 控制加减法
    private int rate = 1;
    // 当前变化状态(增/减/不变)
    private boolean refreshing;
    private static final int REFRESH = 1;
    //把数值分为几等分
    private static final int DELIVIDE=18;
    //动画时间
    private long mAnimTime = 800;



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH:
                    if (rate * mCurValue < rate * mGalValue) {
                        //setText(String.valueOf(Math.round(mCurValue / 1000.0f)));
                        setText(String.valueOf(Math.round((int)mCurValue)));
                        mCurValue += mRate * rate;
                        mHandler.sendEmptyMessageDelayed(REFRESH, mAnimTime/DELIVIDE);
                    } else {
                        //setText(String.valueOf(Math.round(mGalValue / 1000.0f)));
                        setText(String.valueOf(Math.round((int)mGalValue)));
                        mCurValue = mGalValue;      //滚动完成之后当前值设置为目标值
                    }
                    break;
            }
        }
    };


    public MagicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MagicTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, com.cloud.runball.R.styleable.MagicRPMTextView);
        mAnimTime = typedArray.getInt(R.styleable.MagicRPMTextView_animTime, 1000);
    }

    public void initValue(int value) {
        setText(String.valueOf((int)value));
    }

    /**
     * 转速
     * @param
     * @return
     */
    public void setValue(int value) {
        setText(String.valueOf((int)mGalValue));
        //setText(String.valueOf(Math.round(value / 1000.0f)));
        mGalValue = value;
        //这里线屏蔽跳动文字2021-02-22
        mRate = Math.abs((mGalValue - mCurValue) / DELIVIDE);
        BigDecimal b = new BigDecimal(mRate);
        mRate = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        doScroll();
    }

    public void setValue(int value,boolean anim) {
        mHandler.removeMessages(REFRESH);
        if(anim){
            setText(String.valueOf((int)mGalValue));
            if(value-mGalValue==0){
                mCurValue=value+6;
                setText(String.valueOf((int)mCurValue));
            }
            mGalValue = value;
            //这里线屏蔽跳动文字2021-02-22,
            mRate = Math.abs((mGalValue - mCurValue) / DELIVIDE);
            BigDecimal b = new BigDecimal(mRate);
            mRate = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
            doScroll();
        }else{
            setText(String.valueOf((int)mGalValue));
            mGalValue = value;
            //这里线屏蔽跳动文字2021-02-22
            mRate = Math.abs((mGalValue - mCurValue) / DELIVIDE);
            BigDecimal b = new BigDecimal(mRate);
            mRate = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            doScroll();
        }
    }

    private void doScroll() {
        //if ( refreshing)
        //return;
        if (mCurValue > mGalValue) {
            rate = -1;      //如果当前值大于目标值,向下滚动
        } else {
            rate = 1;       //如果当前值小于目标值,向上滚动
        }
        mHandler.sendEmptyMessage(REFRESH);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

}
