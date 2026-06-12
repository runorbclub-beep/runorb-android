package com.cloud.runball.module.mine;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cloud.runball.R;
import com.cloud.runball.SplashActivity;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.databinding.ActivityLanguageBinding;

import java.util.Locale;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: LanguageActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/16 16:38
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/16 16:38
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LanguageActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private ActivityLanguageBinding binding;

    RadioButton chbChinaSelected;
    RadioButton chbEnglishSelected;
    RadioGroup radioGroup;


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_language;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityLanguageBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initView() {
        chbChinaSelected = binding.chbChinaSelected;
        chbEnglishSelected = binding.chbEnglishSelected;
        radioGroup = binding.radioGroup;
        String sta = (String) SPUtils.get(this,"language","");
        if(sta.equalsIgnoreCase("zh_CN")){
            chbChinaSelected.setChecked(true);
        }else if(sta.equalsIgnoreCase("en")){
            chbEnglishSelected.setChecked(true);
        }else{
            Locale locale = getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            if(language.startsWith("en")){
                chbEnglishSelected.setChecked(true);
            }else{
                chbChinaSelected.setChecked(true);
            }
        }
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getCheckedRadioButtonId() == R.id.chbChinaSelected) {
            setAppLanguage("zh_CN");
            restartApp();
        } else if (group.getCheckedRadioButtonId() == R.id.chbEnglishSelected) {
            setAppLanguage("en");
            restartApp();
        }
    }

    public void setAppLanguage(String language) {
        SPUtils.put(this, "language", language);
        AppLogger.d("----------onCheckedChanged-----language-------"+language);
    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_language);
    }

    private void restartApp() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
}
