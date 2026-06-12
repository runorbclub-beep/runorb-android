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
import com.cloud.runball.databinding.ActivityBindingMobileBinding;


/**
 * @author ns467
 */
public class LoginBindingActivity extends BaseActivity {

    private ActivityBindingMobileBinding binding;

    Button btnSend;
    EditText edtPhone;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_binding_mobile;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityBindingMobileBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        btnSend = binding.btnSend;
        edtPhone = binding.edtPhone;

        // Replace @OnClick with listener
        btnSend.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_add_match);
    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
    }

    public void onViewClicked(View v){
        String phone=edtPhone.getText().toString();
          if(!TextUtils.isEmpty(phone) && phone.length()==11){
              Intent id=new Intent(getApplicationContext(),LoginMobileActivity.class);
              id.putExtra("phone",phone);
              startActivity(id);
          }
    }

}
