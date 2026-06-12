package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

public class NicknameDialog {
    View mView;
    Dialog dialog;
    String deviceName;
    String nickname;

    public void show(Context context, String deviceName, String nickname, String confirmText, ConfirmCallBack confirmCallBack) {
        this.deviceName = deviceName;
        this.nickname = nickname;

        mView = LayoutInflater.from(context).inflate(R.layout.dialog_nickname, null);
        dialog = new Dialog(context, R.style.dialog2);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(false);

        EditText etContent = mView.findViewById(R.id.etContent);
        if (TextUtils.isEmpty(nickname)) {
            etContent.setText(deviceName);
        } else {
            etContent.setText(nickname);
        }

        mView.findViewById(R.id.tvCancel).setOnClickListener(v -> dismiss());

        TextView tvConfirm = mView.findViewById(R.id.tv_Confirm);
        tvConfirm.setText(confirmText);
        tvConfirm.setOnClickListener(v -> {
            if(confirmCallBack!=null){
                String newNickname = etContent.getText().toString();
                if (TextUtils.isEmpty(newNickname)) {
                    return;
                }
                confirmCallBack.confirm(newNickname);
            }
            dismiss();
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ScreenWindowManager.widthScreen(context);
        lp.height = ScreenWindowManager.heightScreen(context);
        lp.alpha = 1.0f;
        window.setAttributes(lp);
        dialog.show();
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }


    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            mView=null;
            dialog=null;
        }
    }

    public interface ConfirmCallBack {
        void confirm(String newNickname);
    }
}
