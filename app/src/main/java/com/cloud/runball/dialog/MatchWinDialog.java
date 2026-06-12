package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: MatchWinDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/17 17:13
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/17 17:13
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchWinDialog {
    static View mView;
    static Dialog dialog;

    public static void show(Context context,String result,String result2,String pNums,String distance,ConfirmCallBack confirmCallBack) {
        show( context, "", result, result2, pNums, distance, confirmCallBack);
    }

    public static void show(Context context,String title,String result,String result2,String pNums,String distance,ConfirmCallBack confirmCallBack) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_layout_win, null);
        dialog = new Dialog(context, R.style.dialog2);
        dialog.setCancelable(true);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(true);

        if(TextUtils.isEmpty(title)){
            ((TextView) mView.findViewById(R.id.lvName)).setVisibility(View.INVISIBLE);
        }else{
            ((TextView) mView.findViewById(R.id.lvName)).setText(title);
        }

        ((TextView) mView.findViewById(R.id.tvResult)).setText(result);
        ((TextView) mView.findViewById(R.id.tvResult2)).setText(result2);

        String str1 = String.format(context.getResources().getString(R.string.lbl_match_attend_persons), pNums);
        String str2 = String.format(context.getResources().getString(R.string.lbl_match_attend_distance), distance);


        ((TextView) mView.findViewById(R.id.tvPerson)).setText(str1);
        ((TextView) mView.findViewById(R.id.tvDistance)).setText(str2);


        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(confirmCallBack!=null){
                    confirmCallBack.confirm();
                }
                dismiss();
                return true;
            }
        });
        mView.findViewById(R.id.ivConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(confirmCallBack!=null){
                    confirmCallBack.confirm();
                }
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
