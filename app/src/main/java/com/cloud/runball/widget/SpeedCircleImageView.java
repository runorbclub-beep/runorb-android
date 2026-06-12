package com.cloud.runball.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import androidx.annotation.Nullable;
import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;


/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: SpeedCircleImageView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/6 18:07
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/6 18:07
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SpeedCircleImageView extends androidx.appcompat.widget.AppCompatImageView implements Animator.AnimatorListener {

    //动画时间
    private long mAnimTime = 600;
    private int mGalValue = 0;
    private int mCircle=0;
    //属性动画
    private ValueAnimator mValueAnimator;

    private long defaultAnimTime = 600;

    private float MPI=3.1415926f;

    private RotateAnimation animation;

    public SpeedCircleImageView(Context context) {
        this(context, null);
    }

    public SpeedCircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedCircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, com.cloud.runball.R.styleable.MagicRPMTextView);
        mAnimTime = typedArray.getInt(R.styleable.MagicRPMTextView_animTime, 600);
        defaultAnimTime = mAnimTime;
        init();
    }


    private void init() {
        initAnim();
    }

    /**
     * 设置旋转动画，这个旋转最终会停在0角度
     */
    public void setCircleValue(int value) {
        if (mValueAnimator.isRunning()) {
            mValueAnimator.end();
        }
        if (value == 0) {
            //AppLogger.d( "setValue--停止了---" + System.currentTimeMillis());
            mAnimTime = 200;
        }else{
            mAnimTime=getResources().getInteger(R.integer.animation_duration);
        }

        int start=0;
        int end=Math.abs(value)*360;
        mCircle=value;

        mValueAnimator.setIntValues(start, end);
        mValueAnimator.start();
    }


    int mStart=0;
    public void setValue(float angle){
        //根据转速转化为角度
        //        * 其中0-2-4-6-8是 30度每一刻
        //        * 8-11-14-17-21 是也是30度每一刻

        if(BuildConfig.DEBUG){
            //AppLogger.d("--angle---"+angle);
        }

        /** 屏蔽绘制 **/
        int tempAngle=0;

        if(angle<=220 && angle>180){
            tempAngle=360*7;
        }if(angle<=180 && angle>150){
            tempAngle=360*6;
        }else if(angle<=150 && angle>120){
            tempAngle=360*5;
        }else if(angle<=120 && angle>=90){
            tempAngle=360*4;
        }else if(angle<=90 && angle>60){
            tempAngle=360*3;
        }else if(angle<=60 && angle>30){
            tempAngle=360*2;
        }else if(angle<=30 && angle>0){
            tempAngle=360*1;
        }

        if(!mValueAnimator.isRunning()){
            mValueAnimator.start();
        }
        if((int)mValueAnimator.getAnimatedValue()>=0 && tempAngle==0){
            mValueAnimator.end();
            setRotation(0);
            this.clearAnimation();
            //AppLogger.d("--红色转圈停止---");
        }else{
            mValueAnimator.setIntValues(mStart,mStart+tempAngle);
        }
        //AppLogger.d("--onAnimationUpdate--start="+start+"; end="+end+";angle="+angle);

    }


    private void initAnim() {

        mValueAnimator = ValueAnimator.ofInt(0,0);
        mValueAnimator.setDuration(mAnimTime);
        mValueAnimator.setRepeatCount(Animation.INFINITE);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addListener(this);
        //mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                //AppLogger.d("--SpeedCircleImageView-onAnimationUpdate---"+value);
                if(mGalValue!=0){
                    setRotation(mGalValue);
                }
                mGalValue = value;
                invalidate();
            }
        });
        //mValueAnimator.start();
    }



    public void stop(){
        if(mValueAnimator!=null){
            mValueAnimator.end();
            this.setRotation(0);
        }
        this.clearAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源
        if(mValueAnimator!=null){
            mValueAnimator.removeAllListeners();
            mValueAnimator.cancel();
            mValueAnimator=null;
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        mStart=0;
        //AppLogger.d("---onAnimationRepeat--");
    }
}
