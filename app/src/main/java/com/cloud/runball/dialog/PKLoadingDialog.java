package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

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
public class PKLoadingDialog {

    static View mView;
    static Dialog dialog;

    public static void show(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        dialog = new Dialog(context, R.style.dialog2);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(false);

        //字体
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rzsy_2);
        ((TextView) mView.findViewById(R.id.tv_loading)).setTypeface(typeface);

        //旋转动画
        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        ((ImageView) mView.findViewById(R.id.img_loading)).startAnimation(animation);

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


}
