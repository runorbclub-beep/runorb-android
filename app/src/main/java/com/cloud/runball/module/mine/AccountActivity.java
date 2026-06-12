package com.cloud.runball.module.mine;

import android.content.Intent;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.module.login.ChangePhoneActivity;
import com.cloud.runball.databinding.ActivityAccountBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: AccountActivity
 * @Description:  账号管理
 * @Author: zhd
 * @CreateDate: 2021/2/9 13:54
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/9 13:54
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AccountActivity extends BaseActivity {

    private ActivityAccountBinding binding;

    RelativeLayout ryPhone;
    ImageView img_phone_more;


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityAccountBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        ryPhone = binding.ryPhone;
        img_phone_more = binding.imgPhoneMore;

        // Replace @OnClick with listeners
        ryPhone.setOnClickListener(this::onClick);
        img_phone_more.setOnClickListener(this::onClick);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.lbl_account);
    }

    public void onClick(View v) {
        if(v.getId()==R.id.ryPhone|| v.getId()==R.id.img_phone_more){
          //手机号
           startActivity(new Intent(this, ChangePhoneActivity.class));
        }else if(v.getId()==R.id.btnExit){
            //退出
        }
    }

}
