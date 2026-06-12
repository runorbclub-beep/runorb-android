package com.cloud.runball.module.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.view.ClipViewLayout;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.cloud.runball.databinding.ActivityClipImageBinding;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: ClipImageActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/6/10 14:24
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/10 14:24
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ClipImageActivity extends BaseActivity {

    public static final int REQUEST_CROP_PHOTO=111;

    private ActivityClipImageBinding binding;
    ClipViewLayout clipViewLayout1;

    ClipViewLayout clipViewLayout2;

    int type=0;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clip_image;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityClipImageBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        clipViewLayout1 = binding.clipViewLayout1;
        clipViewLayout2 = binding.clipViewLayout2;
        type=this.getIntent().getIntExtra("type",0);
        Uri uri=getIntent().getData();
        if(type == 1){
            clipViewLayout1.setVisibility(View.VISIBLE);
            clipViewLayout2.setVisibility(View.GONE);
            //设置图片资源
            clipViewLayout1.setImageSrc(uri);
        }else {
            clipViewLayout2.setVisibility(View.VISIBLE);
            clipViewLayout1.setVisibility(View.GONE);
            clipViewLayout2.setImageSrc(uri);
        }
        // wire clicks replacing @OnClick
        View btnConfirm = findViewById(R.id.btnConfirm);
        if (btnConfirm != null) btnConfirm.setOnClickListener(this::onViewClick);
        View btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) btnCancel.setOnClickListener(this::onViewClick);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.lbl_user_info);
    }


    public void onViewClick(View v){
        if (v.getId() == R.id.btnConfirm) {
            generateUriAndReturn();
        }else if(v.getId() == R.id.btnCancel){
            finish();
        }
    }

    /**
     * 生成Uri并且通过setResult返回给打开的Activity
     */
    private void generateUriAndReturn() {
        //调用返回剪切图
        Bitmap zoomedCropBitmap;
        if (type == 1) {
            zoomedCropBitmap = clipViewLayout1.clip();
        } else {
            zoomedCropBitmap = clipViewLayout2.clip();
        }
        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        Uri mSaveUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent = new Intent();
            intent.setData(mSaveUri);
            setResult(REQUEST_CROP_PHOTO, intent);
            finish();
        }
    }

}
