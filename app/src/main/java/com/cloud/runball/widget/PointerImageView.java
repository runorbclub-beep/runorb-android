package com.cloud.runball.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import com.cloud.runball.R;


/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: PointerImageView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/6 17:03
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/6 17:03
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 * androidx.appcompat.widget.AppCompatImageView
 */
public class PointerImageView extends androidx.appcompat.widget.AppCompatImageView {

    public PointerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PointerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //动画时间
    private long mAnimTime = 600;
    private float mStart = 0;
    //属性动画
    private ObjectAnimator mAnimator;

    public void initValue(float angle) {

    }

    public void setValue(float angle) {
        if (angle > 360) {
            angle = 360;
        }
        float start = mStart;
        startAnimator(start, angle, mAnimTime);
    }

    public void setValue(float angle,boolean anim) {
        if (angle > 360) {
            angle = 360;
        }
        float start = mStart;
        long tempAnimTime=mAnimTime;
        if(anim){
            tempAnimTime=angle<=2?400:mAnimTime;
        }else{
            tempAnimTime=400;
        }
        startAnimator(start, angle, tempAnimTime);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, com.cloud.runball.R.styleable.PointerCircleBar);
        mAnimTime = typedArray.getInt(R.styleable.PointerCircleBar_animTime, 600);
        //mAnimator = new ObjectAnimator();
        init();
    }


    private void init(){
        mAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 0);
        mAnimator.setDuration(mAnimTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStart = (float)(animation.getAnimatedValue());
            }
        });
    }

    private void startAnimator(float start, float end, long animTime) {
        /**
        mAnimator = ObjectAnimator.ofFloat(this, "rotation", start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStart = (float)(animation.getAnimatedValue());
            }
        });
        **/
        mAnimator.setDuration(animTime);
        mAnimator.setFloatValues(start,end);
        mAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源
        if(mAnimator!=null){
            mAnimator.removeAllListeners();
            mAnimator.cancel();
            mAnimator=null;
        }
    }
}
