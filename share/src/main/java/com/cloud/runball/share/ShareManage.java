package com.cloud.runball.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import com.cloud.runball.share.constant.ShareTargetConstant;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * date: 2021/9/12
 * author: hwl
 * description:
 */
public class ShareManage {

  Handler handler = new Handler();

  public void shareBitmap(Activity activity, int shareTarget, Bitmap bitmap, ShareCallback callback) {
    switch (shareTarget) {
      case ShareTargetConstant.LOCAL_SAVE:
        saveLocalBitmap(activity, bitmap, callback);
        break;
      case ShareTargetConstant.SHARE_WEIXIN:
        share(activity, SHARE_MEDIA.WEIXIN, bitmap, callback);
        break;
      case ShareTargetConstant.SHARE_WEIXIN_CIRCLE:
        share(activity, SHARE_MEDIA.WEIXIN_CIRCLE, bitmap, callback);
        break;
    }
  }

  private void saveLocalBitmap(Activity activity, Bitmap bitmap, ShareCallback callback) {
    callback.onStart();
    new Thread(() -> {
      // 其次把文件插入到系统图库
      try {
        MediaStore.Images.Media.insertImage(
            activity.getContentResolver(),
            bitmap,
            ""+System.currentTimeMillis(), ""); // 名字和描述没用，系统会自动更改
        handler.postDelayed(() -> {
          callback.onResult();
          Toast.makeText(activity, activity.getString(R.string.share_save_success), Toast.LENGTH_LONG).show();
        }, 0);
      } catch (Exception e) {
        handler.postDelayed(() -> {
          callback.onError(e);
          Toast.makeText(activity, activity.getString(R.string.share_save_success), Toast.LENGTH_LONG).show();
        }, 0);
      }
    }).start();
  }

  private void share(Activity activity, SHARE_MEDIA shareTarget, Bitmap bitmap, ShareCallback callback) {
    UMImage umImage = new UMImage(activity, bitmap);
    umImage.setThumb(new UMImage(activity, bitmap));
    umImage.compressStyle = UMImage.CompressStyle.SCALE;
    new ShareAction(activity)
        .withMedia(umImage)
        .setPlatform(shareTarget)
        .setCallback(new UMShareListener() {
          @Override
          public void onStart(SHARE_MEDIA share_media) {
            if (callback != null) {
              callback.onStart();
            }
          }
          @Override
          public void onResult(SHARE_MEDIA share_media) {
            if (callback != null) {
              callback.onResult();
            }
            Toast.makeText(activity, activity.getString(R.string.share_success), Toast.LENGTH_LONG).show();
          }
          @Override
          public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            if (callback != null) {
              callback.onError(throwable);
            }
            Toast.makeText(
                activity, activity.getString(R.string.share_fail) + throwable.getMessage(),
                Toast.LENGTH_LONG
            ).show();
          }
          @Override
          public void onCancel(SHARE_MEDIA share_media) {
            if (callback != null) {
              callback.onCancel();
            }
            Toast.makeText(activity, activity.getString(R.string.share_cancel),Toast.LENGTH_LONG).show();
          }
        })
        .share();
  }

  public interface ShareCallback {
    void onStart();
    void onResult();
    void onError(Throwable throwable);
    void onCancel();
  }

}
