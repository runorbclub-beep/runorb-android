package com.cloud.runball.basecomm.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cloud.runball.basecomm.R;
import com.cloud.runball.basecomm.utils.SPUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import io.reactivex.disposables.CompositeDisposable;


/**
 * 作者： zh
 * 时间： 2020/11/19 0015-上午 11:09
 * 描述： 基类
 * 来源：
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView {

    public Context context;
    private ProgressDialog dialog;
    protected P presenter;
    protected Toolbar toolbar;
    protected TextView tvToolBarTitle;
    protected abstract P createPresenter();

    protected abstract int getLayoutId();

    protected abstract void addListener();

    protected abstract void initView();

    protected abstract void setOnResult();

    protected abstract String getTitleLabel();

    protected CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeAppLanguage();
        context = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View preInflated = onCreateContentView(getLayoutInflater());
        if (preInflated != null) {
            setContentView(preInflated);
        } else {
            setContentView(getLayoutId());
        }
        supportToolbar();
        presenter = createPresenter();
        initView();
        addListener();
    }

    /**
     * Optional hook for subclasses to provide a pre-inflated content view (e.g., ViewBinding.getRoot()).
     * If this returns non-null, BaseActivity will call setContentView with this view and ignore getLayoutId().
     */
    protected View onCreateContentView(LayoutInflater inflater) {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void settingNavigationBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int ui = window.getDecorView().getSystemUiVisibility();
            ui |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            window.getDecorView().setSystemUiVisibility(ui);
            window.setNavigationBarColor(Color.parseColor("#1A1D20"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
        if (disposable != null) {
            disposable.dispose();
        }
    }

    protected void supportToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolBarTitle = (TextView) findViewById(R.id.tvToolBarTitle);
        //这里自动设置,需要获取子类的title
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        tvToolBarTitle.setText(getTitleLabel());
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitleMargin(0, 5, 5, 5);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.btn_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnResult();
                if (!mIntercept) {
                    if (isFinish()) {
                        finish();
                    }
                }
            }
        });
    }

    protected boolean isFinish() {
        return true;
    }


    public void setFullscreen(boolean isShowStatusBar, boolean isShowNavigationBar) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (!isShowStatusBar) {
            uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (!isShowNavigationBar) {
            uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        //隐藏标题栏
        getSupportActionBar().hide();
        //专门设置一下状态栏导航栏背景颜色为透明，凸显效果。
        setNavigationStatusColor(Color.TRANSPARENT);
    }

    public void setFullscreenShowActionBar(boolean isShowStatusBar, boolean isShowNavigationBar) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (!isShowStatusBar) {
            uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (!isShowNavigationBar) {
            uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        //隐藏标题栏
        //getSupportActionBar().hide();
        //专门设置一下状态栏导航栏背景颜色为透明，凸显效果。
        setNavigationStatusColor(Color.TRANSPARENT);
    }

    public void setNavigationStatusColor(int color) {
        //VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(color);
            getWindow().setStatusBarColor(color);
        }
    }

    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        if(Build.VERSION.SDK_INT >= 24){
            return false;
        }
        boolean result = false;
        Window window=activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if(dark){
                    extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
                }else{
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result=true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if(dark){
                        activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            }catch (Exception e){

            }
        }
        return result;
    }

    public static CharSequence getLabel(Context context, String pkgName, String clsName) {
        if (context == null || pkgName == null) {
            return null;
        }
        ActivityInfo info = null;
        try {
            info = context.getPackageManager().getActivityInfo(new ComponentName(pkgName, clsName), PackageManager.MATCH_DEFAULT_ONLY);
            if (info.labelRes > 0 && info.exported) {
                String str = context.getString(info.labelRes);
                if (str != null) {
                    return str;
                }
            }
            return info.loadLabel(context.getPackageManager());
        } catch (Exception e) {
        }
        return null;
    }

    boolean mIntercept = false;

    public void setInterceptReturnButton(boolean intercept) {
        this.mIntercept = intercept;
    }

    protected void HiddenNavigation() {
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
    }

    protected void showNavigation() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    protected void HiddenNavigationIcon() {
        if (toolbar != null) {
            toolbar.setNavigationIcon(null);
        }
    }

    protected void setNavigationIcon(@DrawableRes int resId) {
        if (toolbar != null) {
            toolbar.setNavigationIcon(resId);
        }
    }

    protected void setNavigationTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle("");
            tvToolBarTitle.setText(title);
        }
    }

    protected void setNavigationTitle(@StringRes int resId) {
        if (toolbar != null) {
            toolbar.setTitle(resId);
            tvToolBarTitle.setText(resId);
        }
    }

    protected void HiddenNavigationTitle() {
        if (toolbar != null) {
            toolbar.setTitle("");
            tvToolBarTitle.setText("");
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.transparent));
            toolbar.setNavigationContentDescription("");
        }
    }


    public Toolbar getToolbar() {
        return toolbar;
    }


    /**
     * @param s
     */
    public void showtoast(String s) {
        Toast.makeText(getApplication(), s, Toast.LENGTH_LONG).show();
    }


    public void showFileDialog() {
        dialog = new ProgressDialog(context);
        dialog.setMessage(getResources().getText(R.string.loading_wait));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMax(100);
        dialog.show();
    }

    public void showFileDialog(String message) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMax(100);
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    public void hideFileDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    private void closeLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    private void showLoadingDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
        }
        dialog.setCancelable(false);
        dialog.show();
    }

    //@NonNull
    //@Override
    //public AppCompatDelegate getDelegate() {
    //    return SkinAppCompatDelegateImpl.get(this, this);
    //}

    @Override
    public void showLoading() {
        showLoadingDialog();
    }


    public void showLoading(String message) {
        showFileDialog(message);
    }


    @Override
    public void hideLoading() {
        closeLoadingDialog();
    }


    @Override
    public void showError(String msg) {
        showtoast(msg);
    }

    @Override
    public void onErrorCode(int code, String msg) {
        showtoast(msg);
    }

    @Override
    public void showLoadingFileDialog() {
        showFileDialog();
    }

    @Override
    public void hideLoadingFileDialog() {
        hideFileDialog();
    }

    @Override
    public void onProgress(long totalSize, long downSize) {
        if (dialog != null) {
            dialog.setProgress((int) (downSize * 100 / totalSize));
        }
    }


    public void showDialogWithCancel(String title, String message,OnConfirmListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (confirmListener != null) {
                    confirmListener.onConfirm();
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void showDialog(String title, String message, OnCancelListener cancelListener, OnConfirmListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cancelListener != null) {
                    cancelListener.onCancel();
                }
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (confirmListener != null) {
                    confirmListener.onConfirm();
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void showDialog(int titleResId, int messageResId, OnCancelListener cancelListener, OnConfirmListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleResId);
        builder.setMessage(messageResId);
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cancelListener != null) {
                    cancelListener.onCancel();
                }
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (confirmListener != null) {
                    confirmListener.onConfirm();
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void showDialog(int titleResId, int messageResId,OnConfirmListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleResId);
        builder.setMessage(messageResId);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (confirmListener != null) {
                    confirmListener.onConfirm();
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void showDialog(String title, String message,OnConfirmListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (confirmListener != null) {
                    confirmListener.onConfirm();
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void showDialogNoCancel(String title, Spanned message, OnConfirmListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_confirm,null);
        AlertDialog dialog=builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmListener!=null){
                    confirmListener.onConfirm();
                }
                //dialog.dismiss();
            }
        });
    }

    public void showDialogNoCancel(String title, String message,OnConfirmListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_confirm,null);
        AlertDialog dialog=builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmListener!=null){
                    confirmListener.onConfirm();
                }
                //dialog.dismiss();
            }
        });
    }


    public void changeAppLanguage() {
        String sta = (String) SPUtils.get(this, "language", "");
        if (!TextUtils.isEmpty(sta)) {
            //Locale locale = getResources().getConfiguration().locale;
            // 本地语言设置
            Locale myLocale = null;
            if (sta.equals("zh_CN")) {
                myLocale = new Locale(sta, Locale.CHINESE.getCountry());
            } else if (sta.equals("zh_TW")) {
                myLocale = new Locale("TW", Locale.TRADITIONAL_CHINESE.getCountry());
            } else if (sta.equalsIgnoreCase("en") || sta.equalsIgnoreCase("en_US") || sta.equalsIgnoreCase("en_rUS")) {
                myLocale = new Locale("en", Locale.ENGLISH.getCountry());
            }
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
    }




    public interface OnCancelListener {
        void onCancel();
    }

    public interface OnConfirmListener {
        void onConfirm();
    }

}
