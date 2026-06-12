package com.cloud.runball.module.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.databinding.ActivityChangeMobileBinding;


public class ChangePhoneActivity extends BaseActivity {

    private ActivityChangeMobileBinding binding;

    Button btnSubmit;
    EditText edtPhone;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_mobile;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityChangeMobileBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        btnSubmit = binding.btnSubmit;
        edtPhone = binding.edtPhone;

        // Replace @OnClick with listeners
        btnSubmit.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_change_phone);
    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
    }

    public void onViewClicked(View v){
        String phone=edtPhone.getText().toString();
          if(!TextUtils.isEmpty(phone) && phone.length()==11){
              Intent it=new Intent(getApplicationContext(), LoginMobileActivity.class);
              it.putExtra("phone",phone);
              it.putExtra("hiddenTitle",true);
              startActivity(it);
          }
    }

}
