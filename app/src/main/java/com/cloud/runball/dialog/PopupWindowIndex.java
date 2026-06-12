package com.cloud.runball.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cloud.runball.R;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: PopupWindowIndex
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/5 17:08
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/5 17:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PopupWindowIndex {

    private static PopupWindowIndex singleton;
    private PopupWindow popWindow;

    public static PopupWindowIndex self() {
        if (singleton == null) {
            synchronized (PopupWindowIndex.class) {
                if (singleton == null) {
                    singleton = new PopupWindowIndex();
                }
            }
        }
        return singleton;
    }

    public PopupWindowIndex build(Context context,String msg) {
        if(popWindow != null) {
            popWindow.dismiss();
            popWindow = null;
        }
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_popwindow, null);
        ((TextView)(contentView.findViewById(R.id.tvMessage))).setText(msg);
        popWindow = new PopupWindow(contentView);
        popWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setFocusable(true);
        popWindow.setTouchInterceptor((v, event) -> {
            popWindow = null;
            return false;
        });
        return this;
    }

    public void show(View view) {
        int[] arr = new int[2];
        view.getLocationOnScreen(arr);
        popWindow.showAtLocation(view, Gravity.TOP, 0, arr[1] + view.getHeight());
    }

    public void dismiss(){
        if(popWindow!=null){
            popWindow.dismiss();
            popWindow=null;
        }
    }

}
