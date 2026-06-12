package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cloud.runball.R;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: PhotoDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/17 10:49
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/17 10:49
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PhotoDialog {
    static Dialog dialog;
    public static void show(Context context,takePhotoListener listener,albumListener alistener,bigImageListener mbigImageListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_choose_photo, null);
        mView.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.tvOntPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.takePhoto();
                }
                dismiss();
            }
        });

        mView.findViewById(R.id.tvAlbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alistener!=null){
                    alistener.takeAlbum();
                }
                dismiss();
            }
        });

        mView.findViewById(R.id.tvBigPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mbigImageListener!=null){
                    mbigImageListener.takeBitImage();
                }
                dismiss();
            }
        });

        dialog = new Dialog(context,R.style.dialog2);
        dialog.setCancelable(true);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();

        final float scale = context.getResources().getDisplayMetrics().density;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width=(int)(300*scale);
        lp.height=(int)(220*scale);
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
            dialog=null;
        }
    }

    public interface takePhotoListener{
        void takePhoto();
    }

    public interface albumListener{
        void takeAlbum();
    }

    public interface bigImageListener{
        void takeBitImage();
    }

}
