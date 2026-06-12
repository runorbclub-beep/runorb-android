package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.cloud.runball.R;
import com.cloud.runball.bean.MedalInfo;
import com.cloud.runball.utils.Constant;
import com.squareup.picasso.Picasso;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class AutoBadgesDialog{

    public static final int DOUYIN=1;
    public static final int QQ=2;
    public static final int QQZONE=3;
    public static final int WECHAT=4;
    public static final int WECHAT_CIRCLE=5;


    static View mView;
    static Dialog processDialog;
    static DismissCallBack mCallBack;


    public static void dismissBadge(){
        if(processDialog!=null){
            processDialog.dismiss();
            if (mCallBack != null) {
                mCallBack.dismiss(0);
            }
        }
    }

    public static boolean isShowing(){
        if(processDialog!=null && processDialog.isShowing()){
            return true;
        }

        return false;
    }

    public static void show(Context context, MedalInfo data, DismissCallBack callBack) {
        mCallBack=callBack;
        mView = LayoutInflater.from(context).inflate(R.layout.layout_badge, null);
        mView.findViewById(R.id.tv_douyin).setOnClickListener(onViewClick);
        mView.findViewById(R.id.tv_qq).setOnClickListener(onViewClick);
        mView.findViewById(R.id.tv_qqzone).setOnClickListener(onViewClick);
        mView.findViewById(R.id.tv_wechat).setOnClickListener(onViewClick);
        mView.findViewById(R.id.tv_wechat_circle).setOnClickListener(onViewClick);


        mView.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    processDialog.dismiss();
                    mCallBack.dismiss(0);
                }
            }
        });

        ImageView ivBadge = mView.findViewById(R.id.ivBadge);
        if (data.getMedal_image_active().startsWith("http")) {
            Picasso.with(context)
                    .load(data.getMedal_image_active())
                    .into(ivBadge);
        } else {
            Picasso.with(context)
                    .load(Constant.getBaseUrl() + "/" + data.getMedal_image_active())
                    .into(ivBadge);
        }

        TextView tvBadgeName = mView.findViewById(R.id.tvBadgeName);
        TextView tvBadgeDesc = mView.findViewById(R.id.tvBadgeDesc);

        String medalname=data.getUser_medal_name_cn();
        String medaldesc=data.getDescription_cn();
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.startsWith("zh")){
            medalname=data.getUser_medal_name_cn();
            medaldesc=data.getDescription_cn();
        }else{
            medalname=data.getUser_medal_name_en();
            medaldesc=data.getDescription_en();
        }

        tvBadgeName.setText(medalname);
        tvBadgeDesc.setText(medaldesc);

        processDialog = new Dialog(context, R.style.processDialog);
        processDialog.setCancelable(false);
        processDialog.setContentView(mView);
        processDialog.show();

        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                try{
                    if (processDialog!=null && processDialog.isShowing()) {
                        processDialog.dismiss();
                        if(mCallBack != null){
                            mCallBack.dismiss(0);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        },5000, TimeUnit.SECONDS);
    }


    static View.OnClickListener onViewClick=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (mCallBack != null && processDialog!=null) {
                processDialog.dismiss();
                int platform=0;
                if(v.getId()==R.id.tv_douyin){
                    platform=DOUYIN;
                }else if(v.getId()==R.id.tv_qq){
                    platform=QQ;
                }else if(v.getId()==R.id.tv_qqzone){
                    platform=QQZONE;
                }else if(v.getId()==R.id.tv_wechat){
                    platform=WECHAT;
                }else if(v.getId()==R.id.tv_wechat_circle){
                    platform=WECHAT_CIRCLE;
                }
                mCallBack.dismiss(platform);
                //后面根据不同的ICON调用不同分享平台
            }
        }
    };


    public interface DismissCallBack {
        void dismiss(int platform);
    }

}
