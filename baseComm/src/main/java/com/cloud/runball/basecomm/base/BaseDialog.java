package com.cloud.runball.basecomm.base;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cloud.runball.basecomm.R;

public abstract class BaseDialog {

  public Dialog dialog;

  public BaseDialog(Context context, int layoutId) {
    View contentView = LayoutInflater.from(context).inflate(layoutId, null);

    onContentView(contentView);

    dialog = new Dialog(context, R.style.core_dialog);
    dialog.setCancelable(false);
    dialog.setContentView(contentView);
    dialog.setCanceledOnTouchOutside(false);
    Window window = dialog.getWindow();
    WindowManager.LayoutParams params = window.getAttributes();
    params.width = WindowManager.LayoutParams.MATCH_PARENT;
    params.height = WindowManager.LayoutParams.MATCH_PARENT;
    params.alpha = 1.0f;
    window.setAttributes(params);
    dialog.show();
  }

  public void dismiss() {
    if (dialog == null) {
      return;
    }
    if (dialog.isShowing()) {
      dialog.dismiss();
    }
    dialog = null;
  }

  protected abstract void onContentView(View contentView);

}
