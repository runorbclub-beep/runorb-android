package com.cloud.runball.module.mine;

import android.view.LayoutInflater;
import android.view.View;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.view.ZoomImageView;
import com.cloud.runball.widget.CircleTransform;
import com.squareup.picasso.Picasso;
import com.cloud.runball.databinding.ActivityImageBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: BigImageActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/17 14:58
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/17 14:58
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class BigImageActivity extends BaseActivity {

    private ActivityImageBinding binding;
    ZoomImageView zoomImageView;


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityImageBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        zoomImageView = binding.zoomImageView;
        String url = this.getIntent().getStringExtra("url");
        updateUserInfo(url);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.app_name);
    }


    public void updateUserInfo(String url) {
        if (url.startsWith("http")) {
            Picasso.with(this)
                    .load(url).centerCrop().transform(new CircleTransform(this)).resize(480, 480)
                    .into(zoomImageView);
        } else {
            Picasso.with(this)
                    .load(Constant.getBaseUrl() + "/" + url).transform(new CircleTransform(this)).centerCrop().resize(480, 480)
                    .into(zoomImageView);
        }
    }
}
