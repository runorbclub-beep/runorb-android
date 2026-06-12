package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: ExitDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/19 17:00
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/19 17:00
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ExitDialog {
    static Dialog dialog;
    static View mView;

    public static void show(Context context,ConfirmCallBack mConfirmCallBack) {
        mView = LayoutInflater.from(context).inflate(R.layout.exit_dialog, null);
        //设置字体
        TextView tvTip2 = (TextView) mView.findViewById(R.id.tvTip2);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rzsy_2);
        tvTip2.setTypeface(typeface);

        TextView imgCancel2 = (TextView) mView.findViewById(R.id.imgCancel2);
        imgCancel2.setTypeface(typeface);
        imgCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView imgConfirm2 = (TextView) mView.findViewById(R.id.imgConfirm2);
        imgConfirm2.setTypeface(typeface);
        imgConfirm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConfirmCallBack!=null){
                    mConfirmCallBack.confirm();
                    dismiss();
                }
            }
        });

        dialog = new Dialog(context, R.style.dialog2);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(false);
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
            dialog=null;
            mView=null;
        }
    }

    public interface ConfirmCallBack {
        void confirm();
    }
}
