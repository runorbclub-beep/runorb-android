package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: PKResultDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/14 10:47
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/14 10:47
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PKResultDialog {

    static Dialog dialog;
    static View mView;
    public static void show(Context context, String mineName, String otherName, boolean isTeam,boolean isWin,String mineDistance, String otherDistance,DismissCallBack callBack) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_result, null);
        if(!isWin){
            mView.findViewById(R.id.img_result_bg).setBackgroundResource(R.mipmap.lost_bg);
        }

        if(isTeam){
            mView.findViewById(R.id.fyWinHead).setVisibility(View.GONE);
            mView.findViewById(R.id.fyLostHead).setVisibility(View.GONE);
        }

        //胜利和失败
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rzsy_2);
        ((TextView)mView.findViewById(R.id.tvWin)).setTypeface(typeface);
        ((TextView)mView.findViewById(R.id.tvLost)).setTypeface(typeface);

        ((TextView)mView.findViewById(R.id.tvWinDistance)).setTypeface(typeface);
        ((TextView)mView.findViewById(R.id.tvLostDistance)).setTypeface(typeface);

        if(isWin){
//            ((TextView)mView.findViewById(R.id.tvWin)).setText(R.string.lbl_mine_total_distance);
//            ((TextView)mView.findViewById(R.id.tvLost)).setText(R.string.lbl_other_total_distance);
            ((TextView)mView.findViewById(R.id.tvWinDistance)).setText(mineDistance + "km");
            ((TextView)mView.findViewById(R.id.tvLostDistance)).setText(otherDistance + "km");

            ((TextView)mView.findViewById(R.id.tvWin)).setText(mineName);
            ((TextView)mView.findViewById(R.id.tvLost)).setText(otherName);
        }else{
//            ((TextView)mView.findViewById(R.id.tvWin)).setText(R.string.lbl_other_total_distance);
//            ((TextView)mView.findViewById(R.id.tvLost)).setText(R.string.lbl_mine_total_distance);
            ((TextView)mView.findViewById(R.id.tvWinDistance)).setText(otherDistance + "km");
            ((TextView)mView.findViewById(R.id.tvLostDistance)).setText(mineDistance + "km");

            ((TextView)mView.findViewById(R.id.tvWin)).setText(otherName);
            ((TextView)mView.findViewById(R.id.tvLost)).setText(mineName);
        }

        mView.findViewById(R.id.imgReturn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack!=null){
                    callBack.dismiss();
                }
                dismiss();
            }
        });

        dialog = new Dialog(context,R.style.dialog2);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width= ScreenWindowManager.widthScreen(context);
        lp.height=ScreenWindowManager.heightScreen(context);
        lp.alpha = 1.0f;
        window.setAttributes(lp);
        dialog.show();
    }

    public static void show(Context context, String mineName, String otherName, boolean isWin,String mineDistance, String otherDistance,String mineAvatar,String otherAvatar,DismissCallBack callBack) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_result, null);
        if(!isWin){
            mView.findViewById(R.id.img_result_bg).setBackgroundResource(R.mipmap.lost_bg);
        }

        ImageView  img_win_avatar =mView.findViewById(R.id.img_win_avatar);
        ImageView  img_lost_avatar =mView.findViewById(R.id.img_lost_avatar);

        if(isWin){
            //我的头像
            if (mineAvatar.startsWith("http")) {
                Picasso.with(context)
                        .load(mineAvatar).centerCrop().transform(new CircleTransform(context)).resize(80, 80)
                        .into(img_win_avatar);
            } else {
                Picasso.with(context)
                        .load(Constant.getBaseUrl() + "/" + mineAvatar).transform(new CircleTransform(context)).centerCrop().resize(80, 80)
                        .into(img_win_avatar);
            }

            //其他人的
            if (otherAvatar.startsWith("http")) {
                Picasso.with(context)
                        .load(otherAvatar).centerCrop().transform(new CircleTransform(context)).resize(80, 80)
                        .into(img_lost_avatar);
            } else {
                Picasso.with(context)
                        .load(Constant.getBaseUrl() + "/" + otherAvatar).transform(new CircleTransform(context)).centerCrop().resize(80, 80)
                        .into(img_lost_avatar);
            }
        }else{
            if (mineAvatar.startsWith("http")) {
                Picasso.with(context)
                        .load(mineAvatar).centerCrop().transform(new CircleTransform(context)).resize(80, 80)
                        .into(img_lost_avatar);
            } else {
                Picasso.with(context)
                        .load(Constant.getBaseUrl() + "/" + mineAvatar).transform(new CircleTransform(context)).centerCrop().resize(80, 80)
                        .into(img_lost_avatar);
            }

            //其他人的
            if (otherAvatar.startsWith("http")) {
                Picasso.with(context)
                        .load(otherAvatar).centerCrop().transform(new CircleTransform(context)).resize(80, 80)
                        .into(img_win_avatar);
            } else {
                Picasso.with(context)
                        .load(Constant.getBaseUrl() + "/" + otherAvatar).transform(new CircleTransform(context)).centerCrop().resize(80, 80)
                        .into(img_win_avatar);
            }
        }




        //我的和对方的胜局
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rzsy_2);
        ((TextView)mView.findViewById(R.id.tvWin)).setTypeface(typeface);
        ((TextView)mView.findViewById(R.id.tvLost)).setTypeface(typeface);

        ((TextView)mView.findViewById(R.id.tvWinDistance)).setTypeface(typeface);
        ((TextView)mView.findViewById(R.id.tvLostDistance)).setTypeface(typeface);

        if(isWin){
//            ((TextView)mView.findViewById(R.id.tvWin)).setText(R.string.lbl_mine_total_distance);
//            ((TextView)mView.findViewById(R.id.tvLost)).setText(R.string.lbl_other_total_distance);
            ((TextView)mView.findViewById(R.id.tvWinDistance)).setText(mineDistance + "km");
            ((TextView)mView.findViewById(R.id.tvLostDistance)).setText(otherDistance + "km");

            ((TextView)mView.findViewById(R.id.tvWin)).setText(mineName);
            ((TextView)mView.findViewById(R.id.tvLost)).setText(otherName);
        }else{
//            ((TextView)mView.findViewById(R.id.tvWin)).setText(R.string.lbl_other_total_distance);
//            ((TextView)mView.findViewById(R.id.tvLost)).setText(R.string.lbl_mine_total_distance);
            ((TextView)mView.findViewById(R.id.tvWinDistance)).setText(otherDistance + "km");
            ((TextView)mView.findViewById(R.id.tvLostDistance)).setText(mineDistance + "km");

            ((TextView)mView.findViewById(R.id.tvWin)).setText(otherName);
            ((TextView)mView.findViewById(R.id.tvLost)).setText(mineName);
        }

        mView.findViewById(R.id.imgReturn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack!=null){
                    callBack.dismiss();
                }
                dismiss();
            }
        });

        dialog = new Dialog(context,R.style.dialog2);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width= ScreenWindowManager.widthScreen(context);
        lp.height=ScreenWindowManager.heightScreen(context);
        lp.alpha = 1.0f;
        window.setAttributes(lp);
        dialog.show();
    }

    public static boolean isShowing(){
        if(dialog!=null){
            return dialog.isShowing();
        }
        return false;
    }

    public static void dismiss(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
            mView=null;
            dialog=null;
        }
    }

    public interface DismissCallBack {
        void dismiss();
    }
}
