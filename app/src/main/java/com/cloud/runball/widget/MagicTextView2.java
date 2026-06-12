package com.cloud.runball.widget;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;
import com.cloud.runball.R;
import java.text.DecimalFormat;

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
public class MagicTextView2 extends androidx.appcompat.widget.AppCompatTextView {
    private Context mContext;
    private ValueAnimator mValueAnimator;
    private DecimalFormat mDf;
    //动画时间
    private long mAnimTime = 600;
    private long defaultAnimTime = 600;
    // 当前变化后最终状态的目标值
    private int mGalValue = 0;

    public MagicTextView2(Context context) {
        this(context, null);
    }

    public MagicTextView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicTextView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, com.cloud.runball.R.styleable.MagicRPMTextView);
        mAnimTime = typedArray.getInt(R.styleable.MagicRPMTextView_animTime, 600);
        defaultAnimTime = mAnimTime;
        init();
    }

    private void init() {
        mDf = new DecimalFormat("0");
        initAnim();
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        mValueAnimator = ValueAnimator.ofInt(0, 0);
        mValueAnimator.setDuration(mAnimTime);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mGalValue = value;
                if (value >= 0) {
                    setText(mDf.format(value));
                }
                invalidate();
            }
        });
    }


    private void startAnimator(int start, int end, long animTime) {
        mValueAnimator = ValueAnimator.ofInt(start, end);
        mValueAnimator.setDuration(animTime);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mGalValue = value;
                if (value >= 0) {
                    setText(mDf.format(value));
                }
                invalidate();
            }
        });
        mValueAnimator.start();
    }


    public void initValue(int value) {
        setText(mDf.format(value));
    }

    public void setValue(int value, boolean anim, boolean stop) {
        //Log.d("PRETTY_LOGGER", "----mGalValue=" + mGalValue+";value="+value);
        if (value == 0) {
            //AppLogger.d( "setValue--停止了---" + System.currentTimeMillis());
            mAnimTime = 200;
            mValueAnimator.setDuration(mAnimTime);
            mValueAnimator.setIntValues((mGalValue == value && mGalValue != 0) ? mGalValue + 10 : mGalValue, value);
            mValueAnimator.start();
            //setText(mDf.format(value));
            return;
        }
        if (mValueAnimator.isRunning()) {
            mGalValue = (int) mValueAnimator.getAnimatedValue();
            mValueAnimator.end();
        }
        if (anim) {
            if (defaultAnimTime != mAnimTime) {
                mAnimTime = defaultAnimTime;
                mValueAnimator.setDuration(mAnimTime);
            }
        } else {
            mAnimTime = 400;
        }
        mValueAnimator.setIntValues((mGalValue == value && mGalValue != 0) ? mGalValue + 10 : mGalValue, value);
        mValueAnimator.start();
    }

    /**
     * 取消动画和动画监听（优化内存）
     */
    public void cancel() {
        mValueAnimator.removeAllUpdateListeners();
        mValueAnimator.cancel();
        mValueAnimator=null;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null) {
            cancel();
        }
    }

}
