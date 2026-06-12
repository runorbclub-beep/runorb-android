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
public class PKFinishDialog {
  static Dialog dialog;
  static View mView;

  public static void show(Context context, OnCallback onCallback) {

    mView = LayoutInflater.from(context).inflate(R.layout.activity_match_main_finish_market, null);
    //设置字体
    TextView tvTip = (TextView) mView.findViewById(R.id.tvTip);
    Typeface typeface = ResourcesCompat.getFont(context, R.font.rzsy_yellow);
    tvTip.setTypeface(typeface);

    ImageView img_exit = mView.findViewById(R.id.img_exit);
    img_exit.setOnClickListener(v -> {
      if (onCallback != null) {
        dialog.dismiss();
        onCallback.onExit();
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
      mView=null;
      dialog.dismiss();
      dialog=null;
    }
  }

  public interface OnCallback {
    void onExit();
  }

}
