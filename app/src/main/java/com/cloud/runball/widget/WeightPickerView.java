package com.cloud.runball.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StyleRes;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.utils.ScreenWindowManager;
import com.github.phoenix.widget.Keyboard;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.widget
 * @ClassName: WeightPickerView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/8 17:18
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/8 17:18
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class WeightPickerView extends Dialog implements Keyboard.OnClickKeyboardListener, View.OnClickListener {


    Context mContext;

    private static final String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            ".", "0", "d"
    };

    onFinishListener finishListener;
    Keyboard keyboardView;
    TextView tvWeight;
    StringBuilder stringBuilder = new StringBuilder();

    public WeightPickerView(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        //设置dialog大小，这里是一个小赠送，模块好的控件大小设置
        Window dialogWindow = getWindow();
        dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);
        dialogWindow.setDimAmount(0.3f);
        dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);

        //以view的方式引入，然后回调activity方法，setContentView，实现自定义布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.pickerview_custom_weight, null);
        setContentView(view);

        tvWeight=view.findViewById(R.id.tvWeight);

        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        TextView tv_finish = view.findViewById(R.id.tv_finish);
        tv_finish.setOnClickListener(this);

        keyboardView = (Keyboard) view.findViewById(R.id.KeyboardView);
        keyboardView.setKeyboardKeys(KEY);
        keyboardView.setKeyBoardBackground(mContext.getResources().getColor(R.color.dialog_bg_color));
        keyboardView.setOnClickKeyboardListener(this);



        WindowManager manager = ((Activity) mContext).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.BOTTOM);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) ScreenWindowManager.widthScreen(mContext);
        params.height= (int) ScreenWindowManager.heightScreen(mContext)/2+20;
        dialogWindow.setAttributes(params);

    }

    public void setFinishListener(onFinishListener callback) {
        this.finishListener = callback;
    }


    public void showDialog() {
        show();
    }

    @Override
    public void onKeyClick(int position, String value) {
        if (position != 11) {
            if(stringBuilder.length()<5){
                stringBuilder.append(KEY[position]);
                if(stringBuilder.indexOf(".")!=stringBuilder.lastIndexOf(".")){
                    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("."));
                }
                if(stringBuilder.indexOf("0")==0 || stringBuilder.indexOf(".")==0){
                    stringBuilder.deleteCharAt(0);
                }
                tvWeight.setText(stringBuilder.toString());
            }
        } else if (position == 11) {
            if (stringBuilder.toString().length() >= 1) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                tvWeight.setText(stringBuilder.toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_finish) {
            if(stringBuilder.length()>0){

                if(stringBuilder.indexOf(".")==stringBuilder.length()-1){
                    stringBuilder.deleteCharAt(stringBuilder.length()-1);
                }

                if(Float.valueOf(stringBuilder.toString())<35.0f || Float.valueOf(stringBuilder.toString())>200.0f){
                    Toast.makeText(mContext,R.string.lbl_weight_error,Toast.LENGTH_LONG).show();
                    return;
                }
                dismiss();
                if (finishListener != null) {
                    finishListener.onFinish(stringBuilder.toString());
                }
            }else{
                Toast.makeText(mContext,R.string.lbl_weight_error,Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.tv_cancel) {
            dismiss();
        }
    }

    public interface onFinishListener {
        void onFinish(String content);
    }
}
