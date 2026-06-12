package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

public class ConnectingDialog {
  static View mView;
  static Dialog dialog;

  public static void show(Context context, ConfirmCallBack confirmCallBack) {
    if (dialog == null) {
      mView = LayoutInflater.from(context).inflate(R.layout.dialog_connecting, null);
      mView.findViewById(R.id.tvCancel).setOnClickListener(v -> {
        if (confirmCallBack != null) {
          dismiss();
          confirmCallBack.onCancel();
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
    } else {
      if (!isShowing()) {
        dialog.show();
      }
    }
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
    void onCancel();
  }
}
