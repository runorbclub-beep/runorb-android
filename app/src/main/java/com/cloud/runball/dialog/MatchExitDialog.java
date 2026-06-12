package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: MatchExitDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/17 17:59
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/17 17:59
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchExitDialog {
    static View mView;
    static Dialog dialog;

    public static void show(Context context,ConfirmCallBack confirmCallBack) {
        show(context,"",confirmCallBack);
    }

    public static void show(Context context, String content,ConfirmCallBack confirmCallBack) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_layout_exit, null);
        dialog = new Dialog(context, R.style.dialog2);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(false);

        if(!TextUtils.isEmpty(content)){
            ((TextView) mView.findViewById(R.id.lvContent)).setText(content);
        }


        mView.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.tv_Confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmCallBack!=null){
                    confirmCallBack.confirm();
                }
                dismiss();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ScreenWindowManager.widthScreen(context);
        lp.height = ScreenWindowManager.heightScreen(context);
        lp.alpha = 1.0f;
        window.setAttributes(lp);
        dialog.show();
    }

    public static void show1(Context context, String content,ConfirmCallBack confirmCallBack) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_layout_exit_1, null);
        dialog = new Dialog(context, R.style.dialog2);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(false);

        if(!TextUtils.isEmpty(content)){
            ((TextView) mView.findViewById(R.id.lvContent)).setText(content);
        }

        mView.findViewById(R.id.tv_Confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmCallBack!=null){
                    confirmCallBack.confirm();
                }
                dismiss();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ScreenWindowManager.widthScreen(context);
        lp.height = ScreenWindowManager.heightScreen(context);
        lp.alpha = 1.0f;
        window.setAttributes(lp);
        dialog.show();
    }

    public static boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }


    public static void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            mView=null;
            dialog=null;
        }
    }

    public interface ConfirmCallBack {
        void confirm();
    }
}
