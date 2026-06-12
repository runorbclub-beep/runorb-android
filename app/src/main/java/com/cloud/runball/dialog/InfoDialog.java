package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.cloud.runball.R;
import com.cloud.runball.view.InfoSwitchView;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.dialog
 * @ClassName: InfoDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/6 18:04
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/6 18:04
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class InfoDialog extends Dialog {
    public InfoDialog(@NonNull Context context) {
        super(context, R.style.dialog2);
        init(context);
    }

    private void init(Context context){
      InfoSwitchView infoSwitchView =new InfoSwitchView(context);
      infoSwitchView.setTargetDialog(true);
      infoSwitchView.setOnDismissClickListener(new InfoSwitchView.OnDismissClickListener() {
        @Override
        public void onComplete() {
          dismiss();
        }

        @Override
        public void onDismiss() {
          dismiss();
        }
      });
      setContentView(infoSwitchView);
    }

    @Override
    public void show() {
        super.show();
        //设置宽度全屏，要设置在show的后面
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }



}
