package com.cloud.runball.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;


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
public class CarMoveImageView extends FrameLayout implements Animator.AnimatorListener {

    //动画时间
    private long mAnimTime = 500;
    //属性动画
    private ValueAnimator mValueAnimator;
    static final int BottomOffsetY=160;
    private ImageView car;
    Bitmap tempRedCar;
    private int carHeight=0;
    private int carWidth=0;
    private int screenHeight=0;
    private int canMoveDistance=0;
    private int startY=0;
    private int endY=0;
    public CarMoveImageView(Context context) {
        this(context, null);
    }

    public CarMoveImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarMoveImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MagicRPMTextView);
        mAnimTime = typedArray.getInt(R.styleable.MagicRPMTextView_animTime, 500);

        //创建红色汽车
        tempRedCar = BitmapFactory.decodeResource(getResources(), R.mipmap.match_red_car);
        Bitmap match_main_bottom = BitmapFactory.decodeResource(getResources(), R.mipmap.match_main_bottom);
        int tempHeight=match_main_bottom.getHeight();

        screenHeight= ScreenWindowManager.heightScreen(context);
        endY= ScreenWindowManager.heightScreen(context)-tempRedCar.getHeight()-tempHeight-BottomOffsetY;
        carHeight=tempRedCar.getHeight();
        carWidth=tempRedCar.getWidth();


        car=new ImageView(context);
        LayoutParams layoutParams= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.width=carWidth;
        layoutParams.height=carHeight;
        layoutParams.leftMargin = ScreenWindowManager.widthScreen(context)/2-carWidth-50;
        layoutParams.topMargin=endY;
        car.setLayoutParams(layoutParams);
        car.setBackgroundResource(R.mipmap.match_red_car);
        addView(car);
        mStart=endY;
        init();
    }

    public int getCarHeight(){
        return carHeight;
    }

    /**
     * 设置结束位置，计算得出可移动距离
     * @param top     开始位置
     * @param bottom  底部位置
     */
    public void setMoveDistance(int top,int bottom){
        this.startY=top;
        this.endY=bottom;
        if(endY==0){
            canMoveDistance=screenHeight-endY-tempRedCar.getHeight()-mTo-BottomOffsetY;
        }
        canMoveDistance=endY-startY;
        //AppLogger.d("--可移动位移--canMoveDistance="+canMoveDistance+";startY="+startY+";endY="+endY);
    }

    public void loadCarImageView(@DrawableRes int resid,boolean other){
        LayoutParams layoutParams= (LayoutParams) car.getLayoutParams();
        if(other){
            layoutParams.width=carWidth;
            layoutParams.height=carHeight;
            layoutParams.leftMargin = ScreenWindowManager.widthScreen(getContext())/2+50;
        }else{
            layoutParams.width=carWidth;
            layoutParams.height=carHeight;
            layoutParams.leftMargin = ScreenWindowManager.widthScreen(getContext())/2-carWidth-50;
        }
        layoutParams.topMargin=endY;
        car.setLayoutParams(layoutParams);
        car.setBackgroundResource(resid);
    }

    public void updateCarPos(){
        LayoutParams layoutParams= (LayoutParams) car.getLayoutParams();
        layoutParams.topMargin=endY;
        car.setLayoutParams(layoutParams);
    }

    /**
     * 车辆起始位置Y轴
     * @return
     */
    public int getCarStartY(){
        return  endY;
    }
    
    private void init() {
        initAnim();
    }

    int mStart=0;
    int mEnd=-1;
    int mGalValue=0;
    int mTo=0;

    boolean isSelf=false;

    public void setValue(int rpm,boolean self){
        this.isSelf=self;

        //移动距离
        int tempMoveDist=getPosOffsetY(rpm);
        mTo= endY-tempMoveDist;
        //if(self){
        //    AppLogger.d("--------move-end-----mTo="+mTo+";rpm="+rpm);
        //}

        if(!mValueAnimator.isRunning()){
            mStart=mTo;
            if(mEnd!=-1){
                mStart=mEnd;
            }
            mValueAnimator.start();
        }

        if((int)mValueAnimator.getAnimatedValue()>=canMoveDistance && mTo==canMoveDistance){
            mValueAnimator.end();
            //if(self) {
            //    AppLogger.d("--------move-end-----transY=canMoveDistance=" + canMoveDistance + ";mTo=" + mTo + ";isSelf=" + isSelf);
            //}
            transY(mTo);
            this.clearAnimation();
        }else{
            //if(self){
            //    AppLogger.d("--------move-end----从="+mStart+";到="+mTo+";当前="+mGalValue);
            //}
            mValueAnimator.setIntValues(mStart,mTo);
            mEnd=mTo;
        }
    }

    /**
     * 需要移动到的距离
     * @param rpm
     * @return
     */
    private int getPosOffsetY(int rpm){
        if(canMoveDistance<0){
            canMoveDistance=endY;
        }
        if(rpm>=0 && rpm<=3000){
            //占比6%
            return (int)(canMoveDistance*0.06*rpm/3000);
        }else if(rpm>=3001 && rpm<=5000){
            //30%
            int last=(int)(canMoveDistance*0.06);
            return last+(int)(canMoveDistance*0.3*rpm/5000);
        }else if(rpm>=5001 && rpm<=8000){
            //40%
            int last=(int)(canMoveDistance*0.36);
            return last+(int)(canMoveDistance*0.4*rpm/8000);
        }else if(rpm>=8001 && rpm<=10000){
            //14%
            int last=(int)(canMoveDistance*0.76);
            return last+(int)(canMoveDistance*0.14*rpm/10000);
        }else if(rpm>=10001 && rpm<=21000){
            //10%
            int last=(int)(canMoveDistance*0.90);
            return last+(int)(canMoveDistance*0.10*rpm/21000);
        }
        return canMoveDistance;
    }



    private void initAnim() {
        mValueAnimator = ValueAnimator.ofInt(0,0);
        mValueAnimator.setDuration(mAnimTime);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addListener(this);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if(mGalValue!=0){
                    transY(value);
                }
                mGalValue = value;
                invalidate();
            }
        });
        //mValueAnimator.start();
    }

    private void transY(int y){
        FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams)car.getLayoutParams();
        params.height=carHeight;
        params.topMargin = y;
        car.setLayoutParams(params);
    }

    public void stop(){
        if(mValueAnimator!=null){
            mValueAnimator.end();
            transY(endY);
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
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
