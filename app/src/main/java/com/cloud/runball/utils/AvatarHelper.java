package com.cloud.runball.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class AvatarHelper {

  protected ActivityResultLauncher<Intent> imagePickerLauncher;
  protected ActivityResultLauncher<Intent> cropImageLauncher;
  protected ActivityResultLauncher<String> permissionLauncher;

  private AvatarCallback callback = null;
  private AvatarResult tempResult = null;

  protected AvatarHelper() {
  }

  protected abstract void initLauncher();

  protected abstract Context getContext();

  protected void onPickerResult(ActivityResult result) {
    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
      Uri dataUri = result.getData().getData();
      startImageCropper(dataUri);
    } else {
      cancel();
    }
  }

  protected void onImageCropped(ActivityResult result) {
    if (result.getResultCode() == Activity.RESULT_OK) {
      if (callback != null) callback.onAvatarResult(tempResult);
      callback = null;
      tempResult = null;
    } else {
      cancel();
    }
  }

  public void pickAvatar(AvatarCallback resultCallback) {
    callback = resultCallback;
    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      if (permissionLauncher != null) permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    } else {
      startPicker();
    }
  }

  protected void startPicker() {
    Intent intent = new Intent(Intent.ACTION_PICK, null);
    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
    if (imagePickerLauncher != null) imagePickerLauncher.launch(intent);
  }

  private void startImageCropper(Uri data) {
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    intent.putExtra("crop", "true");

    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);

    intent.putExtra("outputX", 450);
    intent.putExtra("outputY", 450);
    intent.setDataAndType(data, "image/*");

    tempResult = setCropOutput(intent);

    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    intent.putExtra("noFaceDetection", true);
    intent.putExtra("return-data", false);

    if (cropImageLauncher != null) cropImageLauncher.launch(intent);
  }

  private AvatarResult setCropOutput(Intent intent) {
    try {
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
      String fileName = "IMG_" + timeStamp + "_CROP.jpg";
      File rootFile = new File(getContext().getExternalCacheDir(), "capture");
      if (!rootFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        rootFile.mkdirs();
      }
      if (Build.VERSION.SDK_INT >= 30) {
        File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            + File.separator + fileName);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, imgFile.getAbsolutePath());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return new AvatarResult(uri, imgFile);
      } else {
        File imgFile = new File(rootFile.getAbsolutePath(), fileName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
        return new AvatarResult(null, imgFile);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  protected void cancel() {
    if (callback != null) callback.onAvatarResult(null);
    callback = null;
  }

  public static class AvatarResult {
    public final Uri uri;
    public final File file;

    public AvatarResult(Uri uri, File file) {
      this.uri = uri;
      this.file = file;
    }
  }

  public interface AvatarCallback {
    void onAvatarResult(AvatarResult result);
  }

  private static class AvatarHelperForActivity extends AvatarHelper {
    private final ComponentActivity activity;

    private AvatarHelperForActivity(ComponentActivity activity) {
      this.activity = activity;
      initLauncher();
    }

    @Override
    protected void initLauncher() {
      imagePickerLauncher = activity.registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(), this::onPickerResult);

      cropImageLauncher = activity.registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(), this::onImageCropped);

      permissionLauncher = activity.registerForActivityResult(
          new ActivityResultContracts.RequestPermission(), permitted -> {
            if (permitted) {
              startPicker();
            } else {
              cancel();
            }
          });
    }

    @Override
    protected Context getContext() {
      return activity;
    }
  }

  private static class AvatarHelperForFragment extends AvatarHelper {
    private final Fragment fragment;

    private AvatarHelperForFragment(Fragment fragment) {
      this.fragment = fragment;
      initLauncher();
    }

    @Override
    protected void initLauncher() {
      imagePickerLauncher = fragment.registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(), this::onPickerResult);

      cropImageLauncher = fragment.registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(), this::onImageCropped);

      permissionLauncher = fragment.registerForActivityResult(
          new ActivityResultContracts.RequestPermission(), permitted -> {
            if (permitted) {
              startPicker();
            } else {
              cancel();
            }
          });
    }

    @Override
    protected Context getContext() {
      return fragment.requireContext();
    }
  }

  public static AvatarHelper fromActivity(ComponentActivity activity) {
    return new AvatarHelperForActivity(activity);
  }

  public static AvatarHelper fromFragment(Fragment fragment) {
    return new AvatarHelperForFragment(fragment);
  }
}