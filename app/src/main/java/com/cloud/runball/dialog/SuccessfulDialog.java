package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: SuccessfulDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/3/12 15:08
 * @UpdateUser: zhd
 * @UpdateDate: 2021/3/12 15:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SuccessfulDialog {
    public static final int HOME = 0;
    public static final int OTHER = -1;

    static View mView;
    static Dialog dialog;
    static SuccessfulDialog.DismissCallBack mCallBack;

    public static boolean isShowing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        }
        return false;
    }

    public static void show(Context context, SuccessfulDialog.DismissCallBack callBack) {
        mCallBack = callBack;
        mView = LayoutInflater.from(context).inflate(R.layout.layout_sign_up_dialog, null);
        mView.findViewById(R.id.tvHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    dialog.dismiss();
                    mCallBack.dismiss(HOME);
                }
            }
        });
        mView.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    dialog.dismiss();
                    mCallBack.dismiss(OTHER);
                }
            }
        });

        dialog = new Dialog(context,R.style.dialog);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width= ScreenWindowManager.widthScreen(context);
        lp.alpha = 1.0f;
        window.setAttributes(lp);
        dialog.show();

        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    if (mCallBack != null) {
                        mCallBack.dismiss(OTHER);
                    }
                }
            }
        }, 5000, TimeUnit.SECONDS);
    }

    public interface DismissCallBack {
        void dismiss(int homepage);
    }
}
