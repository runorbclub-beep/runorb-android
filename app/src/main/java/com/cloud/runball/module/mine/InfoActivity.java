package com.cloud.runball.module.mine;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.UserImageModel;
import com.cloud.runball.module.rank.ClanRankFragment;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.view.InfoSwitchView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: InfoActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/6 18:50
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/6 18:50
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class InfoActivity extends AppCompatActivity implements InfoSwitchView.OnNavigationClickListener{

  public static final int REQUEST_CODE = 100;
  InfoSwitchView infoSwitchView = null;

  private static final int ALBUM_CODE = 0x00000014;
  private static final int REQUEST_CLAN_CITY_CODE = 200;

  private final WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
  protected final CompositeDisposable disposable = new CompositeDisposable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    infoSwitchView = new InfoSwitchView(this);
    infoSwitchView.setTargetDialog(true);
    infoSwitchView.setOnNavigationClickListener(this);
    infoSwitchView.setOnSoftKeyboardListener(() -> {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      View v = getWindow().peekDecorView();
      if (null != v) {
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
      }
    });
    infoSwitchView.setOnDismissClickListener(new InfoSwitchView.OnDismissClickListener() {
      @Override
      public void onComplete() {
        finish();
      }

      @Override
      public void onDismiss() {
        AppDataManager.getInstance().setUserInfoModel(null);
        SPUtils.remove(getApplicationContext(), "token");
        SPUtils.remove(getApplicationContext(),"pkdata");
        SPUtils.remove(getApplicationContext(),"pkdata_startTime");
        SPUtils.remove(getApplicationContext(),"pkdata_keepPlayTime");
        finish();
      }
    });
    infoSwitchView.setOnClanAreaClickListener(area -> {
      ARouter.getInstance().build("/city/CityActivity")
          .withBoolean("area", true)
          .navigation(this, REQUEST_CLAN_CITY_CODE);
    });
    infoSwitchView.setOnClanAlbumClickListener(() -> {

    });
    infoSwitchView.setOnAlbumClickListener(() -> {
      openAlbum();
    });
    setContentView(infoSwitchView);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == REQUEST_CODE && resultCode == REQUEST_CODE) {
      String city = data.getStringExtra("city");
      infoSwitchView.setCity(city);
    } else if (requestCode == REQUEST_CLAN_CITY_CODE && resultCode == REQUEST_CODE) {
      String city = data.getStringExtra("city");
      infoSwitchView.setClanCity(city);
    } else if (requestCode == ALBUM_CODE && resultCode == Activity.RESULT_OK) {
      Uri sourceUri = data.getData();
      gotoClipActivity2(sourceUri);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (infoSwitchView != null) {
      infoSwitchView.disposableNet();
    }
    if (disposable != null) {
      disposable.dispose();
    }
  }

  @Override
  public void navigation() {
    ARouter.getInstance().build("/city/CityActivity").withBoolean("area", true).navigation(this, REQUEST_CODE);
  }

  /**
   * 打开相册
   */
  private void openAlbum() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    } else {
      Intent intent = new Intent(Intent.ACTION_PICK, null);
      intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
      startActivityForResult(intent, ALBUM_CODE);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == ALBUM_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //允许权限,打开相册
        openAlbum();
      }
    }
  }

  public Uri uri;
  public File getAppRootDirPath() {
    return getExternalFilesDir(null).getAbsoluteFile();
  }

  public File createImageFile(Context context,boolean isCrop) {
    try {
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String fileName = "";
      if (isCrop) {
        fileName = "IMG_"+timeStamp+"_CROP.jpg";
      } else {
        fileName = "IMG_"+timeStamp+".jpg";
      }
      File rootFile = new File(getAppRootDirPath() + File.separator + "capture");
      if (!rootFile.exists()) {
        rootFile.mkdirs();
      }
      File imgFile;
      if (Build.VERSION.SDK_INT >= 30) {
        imgFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + fileName);
        // 通过 MediaStore API 插入file 为了拿到系统裁剪要保存到的uri（因为App没有权限不能访问公共存储空间，需要通过 MediaStore API来操作）
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, imgFile.getAbsolutePath());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      }else {
        imgFile = new File(rootFile.getAbsolutePath() + File.separator + fileName);
      }
      return imgFile;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  File file = null;
  public File getCropFile(Context context, Uri uri){
    String[] proj = { MediaStore.Images.Media.DATA };
    Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
    if (cursor.moveToFirst()) {
      int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      String path = cursor.getString(columnIndex);
      cursor.close();
      return new File(path);
    }
    return null;
  }

  private void gotoClipActivity2(Uri sourceUri){
    file = createImageFile(this, true);
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    intent.putExtra("crop", "true");
    // aspectX aspectY 是宽高的比例
    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);
    // 裁剪后输出图片的尺寸大小
    intent.putExtra("outputX", 450);
    intent.putExtra("outputY", 450);
    intent.setDataAndType(sourceUri, "image/*");
    if (Build.VERSION.SDK_INT >= 30) {
      intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    } else {
      Uri imgCropUri = Uri.fromFile(file);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, imgCropUri);
    }
    // 图片格式
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    // 取消人脸识别
    intent.putExtra("noFaceDetection", true);
    // true:不返回uri，false：返回uri,携带数据过大会奔溃
    intent.putExtra("return-data", false);
    //startActivityForResult(intent, PHOTO_ZOOM);
    launcher.launch(intent);
  }

  ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    if(result.getResultCode() == Activity.RESULT_OK) {
      if(file != null) {
//        Picasso.with(this).load(file).transform(new CircleTransform2(this,480,480)).into(img_info_avatar);
        //上传头像
        try {
          //上传头像
          if (Build.VERSION.SDK_INT >= 30) {
            if (uri != null) {
              file = getCropFile(this, uri);
              if (file.exists()) {
                updateFile(file);
              }
            }
          } else {
            if (file.exists()) {
              updateFile(file);
            }
          }
        }catch (Exception ex){
          ex.printStackTrace();
        }
      }
    }else if(result.getResultCode() == Activity.RESULT_CANCELED) {
      AppLogger.d("---------裁剪头像返回---RESULT_CANCELED-------");
    }
  });

  /**
   * 文件封装
   * @param file
   */
  private void updateFile(File file) {
    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    Observable<UserImageModel> observable = apiServer.uploadImage(filePart);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserImageModel>() {
              @Override
              public void onSuccess(UserImageModel result) {
                try {
                  infoSwitchView.setPortrait(result.getUser_img_path(), result.getFile_path().getFile_path());
                  if(file.isFile() && file.exists()){
                    file.delete();
                  }
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
              @Override
              public void onError(int code, String msg) {
                Logger.d("----updateFile---" + code + "; msg = " + msg);
              }
            })
    );
  }

}
