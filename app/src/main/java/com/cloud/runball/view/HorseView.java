package com.cloud.runball.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.view
 * @ClassName: HorseView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/24 11:17
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/24 11:17
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class HorseView extends FrameLayout implements Animator.AnimatorListener{
    //动画时间
    private long mAnimTime = 500;
    //属性动画
    private ValueAnimator mValueAnimator=null;

    private ImageView horse;
    private int mStart=0;
    private int mGalValue=0;
    private int mTo=0;
    public HorseView(Context context) {
        this(context, null);
    }

    public HorseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MagicRPMTextView);
        mAnimTime = typedArray.getInt(R.styleable.MagicRPMTextView_animTime, 2650);


        horse=new ImageView(context);
        LayoutParams layoutParams= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.width=layoutParams.height= AppUtils.dip2px(getContext(),48);
        layoutParams.gravity= Gravity.CENTER_VERTICAL;
        horse.setLayoutParams(layoutParams);
        horse.setBackgroundResource(R.drawable.anima_horse_1);
        addView(horse,layoutParams);

        initAnim(0,0);
    }


    public void startRun(){
        AnimationDrawable animationDrawable = (AnimationDrawable) horse.getBackground();
        if(animationDrawable!=null && !animationDrawable.isRunning()){
            animationDrawable.start();
        }
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resid) {
        horse.setBackgroundResource(resid);
    }

    @Override
    public Drawable getBackground() {
        return horse.getBackground();
    }


    boolean isSelf=false;
    double lastPer=0.0f;
    public void setValue(double percent, int ivFlagWidth, boolean self){
        this.isSelf=self;
        //移动距离
        mTo= getPosOffsetX(percent, ivFlagWidth);
        if(mValueAnimator==null){
            initAnim(mStart,mTo);
        }
        if(!mValueAnimator.isRunning()){
            mValueAnimator.start();
        }

        //AppLogger.d("lastPer="+lastPer+";percent="+percent+";mStart="+mStart+";mTo="+mTo);
        lastPer=percent;
        mValueAnimator.setIntValues(mStart,mTo);
        mStart=mTo;
        /**
        if((int)mValueAnimator.getAnimatedValue()>=mMax && mTo==mMax){
            mValueAnimator.end();
            transX(mTo);
            this.clearAnimation();
        }else{
            mValueAnimator.setIntValues(mStart,mTo);
            mStart=mTo;
        }
        **/
    }

    /**
     * 需要移动到的距离
     * @param percent
     * @param ivFlagWidth 控件长度
     * @return
     */
    private int getPosOffsetX(double percent, int ivFlagWidth){
        return (int)((ivFlagWidth - horse.getWidth()) *percent);
    }



    private void initAnim(int start,int to) {
        mValueAnimator = ValueAnimator.ofInt(start,to);
        mValueAnimator.setDuration(mAnimTime);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addListener(this);
        mValueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            if(mGalValue!=0){
                transX(value);
            }
            mGalValue = value;
            invalidate();
        });
        //mValueAnimator.start();
    }

    private void transX(int x){
        FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams)horse.getLayoutParams();
        params.leftMargin=x;
        horse.setLayoutParams(params);
    }

    public void stop(){
        if(mValueAnimator!=null){
            mValueAnimator.end();
            transX(0);
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
        mStart=(int)mValueAnimator.getAnimatedValue();
        //AppLogger.d("---onAnimationEnd-----"+mStart);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
