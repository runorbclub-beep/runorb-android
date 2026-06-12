package com.cloud.runball.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import com.cloud.runball.share.constant.ShareTargetConstant;
import com.cloud.runball.share.common.R;


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

  public interface ShareCallback {
    void onStart();
    void onResult();
    void onError(Throwable throwable);
    void onCancel();
  }

}
