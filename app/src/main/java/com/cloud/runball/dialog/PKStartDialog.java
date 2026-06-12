package com.cloud.runball.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.cloud.runball.R;
import com.cloud.runball.bean.MessageEvent;
import org.greenrobot.eventbus.EventBus;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: PKStartDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/12 15:58
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/12 15:58
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */

public class PKStartDialog {

    public static final int PK=0;
    public static final int TEAM_PK=1;

     TextView tvTime;
     int maxSecond=4;
     PKStartDialog.DismissCallBack mCallBack;
     PKStartDialog.StartCallBack mStart;
     ExitCallBack mExitCallBack;
     int mPK_TYPE=PK;
     AlphaAnimation alphaAnimation;

    static int wait=0;

    public  View show(Context context,String pk_room_number,int pk_type,ExitCallBack exitCallBack) {
        maxSecond=4;
        mPK_TYPE=pk_type;
        mExitCallBack=exitCallBack;
        View mView = LayoutInflater.from(context).inflate(R.layout.activity_match_main_market, null);
        mView.findViewById(R.id.imgStart).setVisibility(View.GONE);
        //设置字体
        TextView tvTip=(TextView)mView.findViewById(R.id.tvTip);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rzsy_yellow);
        tvTip.setTypeface(typeface);

        //房间号
        TextView tvRoomNum=((TextView)mView.findViewById(R.id.tvRoomNum));
        tvRoomNum.setTypeface(typeface);
        tvRoomNum.setText(context.getResources().getString(R.string.lbl_match_pk_id)+pk_room_number);

        tvTime=mView.findViewById(R.id.tvTime);
        if(pk_type==PK){
            mView.findViewById(R.id.ryStart).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.ryTime).setVisibility(View.GONE);
        }else{
            mView.findViewById(R.id.ryStart).setVisibility(View.GONE);
            mView.findViewById(R.id.ryTime).setVisibility(View.VISIBLE);
        }

        //退出
        mView.findViewById(R.id.img_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibleExitView(context,mView);
            }
        });

        //闪烁
        alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1100);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.RESTART);
        mView.findViewById(R.id.ryTime).setAnimation(alphaAnimation);
        alphaAnimation.start();

        startTimer();

        //这里要添加进根布局
        isShow=true;

        return mView;
    }

    public  View show(Context context,String pk_room_number,int pk_type,boolean isSelf,StartCallBack mStartCallBack,PKStartDialog.DismissCallBack callBack,ExitCallBack exitCallBack) {
        maxSecond=4;
        mCallBack=callBack;
        mStart=mStartCallBack;
        mExitCallBack=exitCallBack;
        mPK_TYPE=pk_type;
        View mView = LayoutInflater.from(context).inflate(R.layout.activity_match_main_market, null);
        if(!isSelf){
            mView.findViewById(R.id.imgStart).setBackgroundResource(R.mipmap.match_start_2);
        }
        mView.findViewById(R.id.imgStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStart!=null){
                    mStart.start(mView);
                }
            }
        });

        //退出
        mView.findViewById(R.id.img_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.findViewById(R.id.imgStart).setVisibility(View.GONE);
                setVisibleExitView(context,mView);
            }
        });


        //设置字体
        TextView tvTip=(TextView)mView.findViewById(R.id.tvTip);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rzsy_yellow);
        tvTip.setTypeface(typeface);

        //房间号
        TextView tvRoomNum=((TextView)mView.findViewById(R.id.tvRoomNum));
        tvRoomNum.setTypeface(typeface);
        tvRoomNum.setText(context.getResources().getString(R.string.lbl_match_pk_id)+pk_room_number);

        tvTime=mView.findViewById(R.id.tvTime);
        tvTime.setTypeface(typeface);
        if(pk_type==PK){
            mView.findViewById(R.id.ryStart).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.ryTime).setVisibility(View.GONE);
        }else{
            mView.findViewById(R.id.ryStart).setVisibility(View.GONE);
            mView.findViewById(R.id.ryTime).setVisibility(View.VISIBLE);
            //这里需要闪烁5秒中
            alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
            alphaAnimation.setDuration(1100);
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.RESTART);
            mView.findViewById(R.id.ryTime).setAnimation(alphaAnimation);
            alphaAnimation.start();
            startTimer();
        }

        //这里要添加进根布局
        isShow=true;
        return mView;
    }

    public void setWaitStatus(View view) {
        wait=1;
        view.findViewById(R.id.imgStart).setBackgroundResource(R.mipmap.match_start_wait);
    }


    public void toggleInitReady(View mView){
        wait=0;
        if(mView!=null){
            mView.findViewById(R.id.imgStart).setBackgroundResource(R.mipmap.match_start);
        }
    }

    public  void togglePlayTimer(View mView){
        if(mPK_TYPE==PK && mView!=null){
            mView.findViewById(R.id.ryStart).setVisibility(View.GONE);
            mView.findViewById(R.id.ryTime).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.tvRoomNum).setVisibility(View.GONE);
            wait=-1;
            //这里需要闪烁5秒中
            alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
            alphaAnimation.setDuration(1100);
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.RESTART);
            mView.findViewById(R.id.ryTime).setAnimation(alphaAnimation);
            alphaAnimation.start();
            startTimer();
        }
    }


    private  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    doHandler();
                 break;
            }
        }
    };

    public  void hiddenToggleTime(View mView){
        if(mView!=null){
            mView.findViewById(R.id.ryTime).clearAnimation();
        }
    }

    private  void doHandler(){
        EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_HIDDEN_TIME));
        if(maxSecond>0){
            maxSecond--;
            if(maxSecond==0){
                tvTime.setText("GO!");
            }else{
                tvTime.setText(String.valueOf(maxSecond));
            }
        }else{
            isToggleRun=true;
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_PLAY_START));
            if (mCallBack != null) {
                mCallBack.dismiss();
            }
            dismiss();
        }
    }


     Timer timer;
     boolean isToggleRun=false;
    /**
     * 计时器开始计时,第6秒开始
     */
    private  void startTimer() {
        stopTimer();
        //这里可能会有线程问题
        if(timer==null){
            timer=new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isToggleRun){
                    handler.sendEmptyMessage(1);
                }
            }
        },7000,1000);
    }

     boolean isShow=false;
    public boolean isShowing(){
        return isShow;
    }

     void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    public  void dismiss(){
        if(isShow){
            if(handler!=null){
                handler.removeCallbacksAndMessages(null);
                handler= null;
            }
            isShow=false;
            wait=-1;
            isToggleRun=false;
            mCallBack=null;
            mStart=null;
            mExitCallBack=null;
            stopTimer();
        }
    }

     void setVisibleExitView(Context context,View view){
        view.findViewById(R.id.ryExit).setVisibility(View.VISIBLE);
        //设置字体
        TextView tvTip2 = (TextView) view.findViewById(R.id.tvTip2);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rzsy_yellow);
        tvTip2.setTypeface(typeface);

        TextView imgCancel2 = (TextView) view.findViewById(R.id.imgCancel2);
        imgCancel2.setTypeface(typeface);
        imgCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wait!=-1){
                    view.findViewById(R.id.imgStart).setVisibility(View.VISIBLE);
                }
                view.findViewById(R.id.ryExit).setVisibility(View.GONE);
            }
        });

        TextView imgConfirm2 = (TextView) view.findViewById(R.id.imgConfirm2);
        imgConfirm2.setTypeface(typeface);
        imgConfirm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mExitCallBack!=null){
                    mExitCallBack.exitCallback();
                    dismiss();
                 }
            }
        });
    }


    public interface DismissCallBack {
        void dismiss();
    }

    public interface StartCallBack {
        void start(View view);
    }

    public interface ExitCallBack{
        void exitCallback();
    }

}
