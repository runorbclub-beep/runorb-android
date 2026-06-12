package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
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
 * @ClassName: PKRuleDialog
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/12 14:56
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/12 14:56
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PKRuleDialog {
    static Dialog dialog;
    public static void show(Context context) {
        View mView = LayoutInflater.from(context).inflate(R.layout.activity_match_add_rule, null);
        mView.findViewById(R.id.tvClose).setOnClickListener(v->dialog.dismiss());

        dialog = new Dialog(context,R.style.dialog2);
        dialog.setCancelable(true);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width= ScreenWindowManager.widthScreen(context);
        lp.alpha = 1.0f;
        window.setAttributes(lp);
        dialog.show();
    }

    public static void show(Context context, String ruleContent) {
        View mView = LayoutInflater.from(context).inflate(R.layout.activity_match_add_rule, null);
        mView.findViewById(R.id.tvClose).setOnClickListener(v -> dialog.dismiss());
        if (!TextUtils.isEmpty(ruleContent)) {
            ((TextView)(mView.findViewById(R.id.tvRule))).setText(ruleContent.replaceAll("\\\\n","\n"));
        }
        dialog = new Dialog(context,R.style.dialog2);
        dialog.setCancelable(true);
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width=ScreenWindowManager.widthScreen(context);
        lp.height=ScreenWindowManager.heightScreen(context);
        lp.alpha = 1.0f;
        window.setAttributes(lp);
        dialog.show();
    }

    public static boolean isShowing(){
        if(dialog!=null){
            return dialog.isShowing();
        }
        return false;
    }

    public static void dismiss(){
         if(dialog!=null && dialog.isShowing()){
             dialog.dismiss();
             dialog=null;
         }
    }

}
