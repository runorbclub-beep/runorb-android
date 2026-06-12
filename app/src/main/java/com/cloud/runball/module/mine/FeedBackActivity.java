package com.cloud.runball.module.mine;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: FeedBackActivity
 * @Description: 意见反馈
 * @Author: zhd
 * @CreateDate: 2021/2/9 14:23
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/9 14:23
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class FeedBackActivity extends BaseActivity {
    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_feedback);
    }
}
