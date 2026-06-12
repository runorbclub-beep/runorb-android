package com.cloud.runball.module.clan;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.dialog.CommonDialog;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.ClanInfoModel;
import com.cloud.runball.model.ClanMemberItem;
import com.cloud.runball.model.UserImageModel;
import com.cloud.runball.module.clan.dialog.EditClanDialog;
import com.cloud.runball.module.clan.dialog.JoinClanDialog;
import com.cloud.runball.module.clan.dialog.SearchClanMemberDialog;
import com.cloud.runball.module.match_football_association.dialog.AssociationCommonDialog;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.cloud.runball.databinding.FragmentClanSettingsBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ClanSettingsFragment extends BaseFragment {

  private FragmentClanSettingsBinding binding;

  TextView tvClanContact;

  TextView tvClanCreateTime;

  View layEditClan;

  View layTransferAdmin;

  CardView layExitClan;

  TextView tvExitClan;

  WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  private EditClanDialog editClanDialog;

  private String clanId;
  private int pendingState;
  private int captainStatus;
  private String clanName;
  private String clanAvatar;
  private String address;
  private String introduction;
  private String telephone;
  private String createdTime;
  private ClanInfoModel.CaptainInfo captainInfo;


  private static final int ALBUM_CODE = 0x00000014;
  public static final int REQUEST_CODE = 100;

  private boolean isUserJoining;

  public static ClanSettingsFragment newInstance(
      String clanId, int pendingState, int captainStatus, String clanName, String clanAvatar, String address,
      String introduction, String telephone, String createdTime, ClanInfoModel.CaptainInfo captainInfo, boolean isUserJoining
  ) {
    ClanSettingsFragment fragment = new ClanSettingsFragment();
    Bundle bundle = new Bundle();
    bundle.putString("clanId", clanId);
    bundle.putInt("pendingState", pendingState);
    bundle.putInt("captainStatus", captainStatus);
    bundle.putString("clanName", clanName);
    bundle.putString("clanAvatar", clanAvatar);
    bundle.putString("address", address);
    bundle.putString("introduction", introduction);
    bundle.putString("telephone", telephone);
    bundle.putString("createdTime", createdTime);
    bundle.putSerializable("captainInfo", captainInfo);
    bundle.putBoolean("isUserJoining", isUserJoining);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_clan_settings;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentClanSettingsBinding.bind(view);
    tvClanContact = binding.tvClanContact;
    tvClanCreateTime = binding.tvClanCreateTime;
    layEditClan = binding.layEditClan;
    layTransferAdmin = binding.layTransferAdmin;
    layExitClan = binding.layExitClan;
    tvExitClan = binding.tvExitClan;

    // Wire click listeners (replacing @OnClick)
    View tvEditClan = view.findViewById(R.id.tvEditClan);
    if (tvEditClan != null) tvEditClan.setOnClickListener(this::onClick);
    View tvTransferAdmin = view.findViewById(R.id.tvTransferAdmin);
    if (tvTransferAdmin != null) tvTransferAdmin.setOnClickListener(this::onClick);
    if (tvExitClan != null) tvExitClan.setOnClickListener(this::onClick);
  }

  @Override
  protected void onLazyLoad() {
    Bundle bundle = getArguments();
    if (bundle == null) {
      return;
    }

    clanId = bundle.getString("clanId");
    pendingState = bundle.getInt("pendingState");
    captainStatus = bundle.getInt("captainStatus");
    clanName = bundle.getString("clanName");
    clanAvatar = bundle.getString("clanAvatar");
    address = bundle.getString("address");
    introduction = bundle.getString("introduction");
    telephone = bundle.getString("telephone");
    createdTime = bundle.getString("createdTime");
    captainInfo = (ClanInfoModel.CaptainInfo) bundle.getSerializable("captainInfo");
    isUserJoining = bundle.getBoolean("isUserJoining");

    tvClanContact.setText(getString(R.string.format_clan_contact, telephone));
    tvClanCreateTime.setText(getString(R.string.format_clan_create_time, createdTime));

    // pendingState 0不可加入 1待审核  2已加入  3未加入
    // captainStatus 用户是否队长：0否  1是 2尚未加入俱乐部
    if (captainStatus == 1) {
      layEditClan.setVisibility(View.VISIBLE);
      layTransferAdmin.setVisibility(View.VISIBLE);
    } else {
      layEditClan.setVisibility(View.GONE);
      layTransferAdmin.setVisibility(View.GONE);
    }

    if (pendingState == 1) {
      layTransferAdmin.setVisibility(View.GONE);
    }

    if (pendingState == 0) {
      layExitClan.setVisibility(View.GONE);
    } else if (pendingState == 1) {
      layExitClan.setVisibility(View.VISIBLE);
      if (captainStatus == 1) {
        tvExitClan.setText(R.string.cancel_clan_create);
      } else {
        tvExitClan.setText(R.string.cancel_clan_join);
      }
    } else if (pendingState == 2) {
      if (captainStatus == 1) {
        layExitClan.setVisibility(View.GONE);
      } else {
        layExitClan.setVisibility(View.VISIBLE);
        tvExitClan.setText(R.string.exit_clan);
      }
    } else if (pendingState == 3) {
      layExitClan.setVisibility(View.VISIBLE);
      tvExitClan.setText(R.string.clan_apply_join);
    }

  }

  public void onClick(View view) {
    if (view.getId() == R.id.tvEditClan) {
      editClanDialog = new EditClanDialog(this.getContext());
      editClanDialog.setDate(true, clanName, clanAvatar, address, introduction, telephone, createdTime);
      editClanDialog.setCallback(new EditClanDialog.Callback() {
        @Override
        public void onPortraitClick() {
          openAlbum();
        }
        @Override
        public void onAreaClick(String area) {
          ARouter.getInstance().build("/city/CityActivity")
              .withBoolean("area", true)
              .navigation(ClanSettingsFragment.this.getActivity(), REQUEST_CODE);
        }
        @Override
        public void onSubmit(Dialog dialog, boolean isEdit, String clanName, String clanAvatar, String address, String introduction, String telephone, String remark) {
          editClanInfo(dialog, clanName, clanAvatar, address, introduction, telephone);
        }
      });
    } else if (view.getId() == R.id.tvTransferAdmin) {
      searchMember();
    } else if (view.getId() == R.id.tvExitClan) {
      // 0不可加入 1待审核  2已加入  3未加入
      if (pendingState == 0) {

      } else if (pendingState == 1) {
        if (captainStatus == 1) {
          withdrawClan();
        } else {
          postUnJoinClan();
        }
      } else if (pendingState == 2) {
        if (captainStatus == 1) {

        } else {
          CommonDialog dialog = new CommonDialog(ClanSettingsFragment.this.getContext());
          dialog.setContent("", "是否退出该俱乐部？");
          dialog.addBtn(getString(R.string.btn_cancel), commonDialog -> {
            commonDialog.dismiss();
          });
          dialog.addBtn(getString(R.string.btn_ok), commonDialog -> {
            String uid = AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id();
            exitClan(commonDialog, uid);
          });
        }
      } else {
        if (isUserJoining) {
          AssociationCommonDialog dialog = new AssociationCommonDialog( ClanSettingsFragment.this.getContext());
          dialog.setContent(getString(R.string.tip), getString(R.string.tip_repeat_clan));
          dialog.addBtn(getString(R.string.btn_confirm), true, commonDialog -> {
            commonDialog.dismiss();
          });
        } else {
          JoinClanDialog dialog = new JoinClanDialog(ClanSettingsFragment.this.getContext());
          dialog.setCallback(new JoinClanDialog.Callback() {
            @Override
            public void onSubmit(Dialog dialog, String remark) {
              ClanActivity activity = (ClanActivity) getActivity();
              if (activity != null) {
                activity.postApplyJoinClan(dialog, remark);
              }

            }
          });
        }
      }

    }
  }

  private void editClanInfo(Dialog dialog, String clanName, String clanAvatar, String address, String introduction, String telephone) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("id", clanId);
    if (!TextUtils.isEmpty(clanName)) {
      map.put("title", clanName);
    }
    if (!TextUtils.isEmpty(clanAvatar)) {
      map.put("clan_avatar", clanAvatar);
    }
    if (!TextUtils.isEmpty(address)) {
      map.put("address", address);
    }
    if (!TextUtils.isEmpty(introduction)) {
      map.put("introduction", introduction);
    }
    if (!TextUtils.isEmpty(telephone)) {
      map.put("telephone", telephone);
    }
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.editClanInfo(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Boolean>() {
              @Override
              public void onSuccess(Boolean model) {
                if (dialog != null) {
                  dialog.dismiss();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("editClanInfo --- " + msg);
                Toast.makeText(ClanSettingsFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  // 取消申请加入俱乐部
  private void postUnJoinClan() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_clan_id", clanId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.postUnJoinClan(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Boolean>() {
              @Override
              public void onSuccess(Boolean model) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("postUnJoinClan --- " + msg);
                Toast.makeText(ClanSettingsFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  // 取消申请俱乐部
  private void withdrawClan() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_clan_id", clanId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.withdrawClan(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Boolean>() {
              @Override
              public void onSuccess(Boolean model) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
                if (getActivity() != null) {
                  getActivity().finish();
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getClanList --- " + msg);
                Toast.makeText(ClanSettingsFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  private void searchMember() {
    SearchClanMemberDialog memberDialog = new SearchClanMemberDialog(this.getContext());
    memberDialog.setData(clanId);
    memberDialog.setCallback(new SearchClanMemberDialog.Callback() {
      @Override
      public void onItemClick(Dialog dialog, ClanMemberItem itemData) {
        transferAdmin(dialog, itemData.getUserId());
      }
    });
  }

  private void transferAdmin(Dialog dialog, String memberId) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_clan_id", clanId);
    map.put("member_user_id", memberId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.postHandoverClanLeader(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Boolean>() {
              @Override
              public void onSuccess(Boolean model) {
                if (dialog != null) {
                  dialog.dismiss();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getClanList --- " + msg);
                Toast.makeText(ClanSettingsFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  private void exitClan(Dialog dialog, String uid) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_clan_id", clanId);
    map.put("member_user_id", uid);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.delUserClanMember(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Object>() {
              @Override
              public void onSuccess(Object model) {
                if (dialog != null) {
                  dialog.dismiss();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("delUserClanMember --- " + msg);
                Toast.makeText(ClanSettingsFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }


  /**
   * 打开相册
   */
  private void openAlbum() {
    if (ActivityCompat.checkSelfPermission(ClanSettingsFragment.this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(ClanSettingsFragment.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ALBUM_CODE && resultCode == Activity.RESULT_OK) {
      Uri sourceUri = data.getData();
      gotoClipActivity2(sourceUri);
    } else if(requestCode == REQUEST_CODE && resultCode == REQUEST_CODE){
      String city = data.getStringExtra("city");
      if (editClanDialog != null) {
        editClanDialog.setArea(city);
      }
    }
  }

  public Uri uri;
  public File getAppRootDirPath() {
    return getActivity().getExternalFilesDir(null).getAbsoluteFile();
  }

  public File createImageFile(Context context, boolean isCrop) {
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
    file = createImageFile(this.getContext(), true);
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
              file = getCropFile(this.getContext(), uri);
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
    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    Observable<UserImageModel> observable = apiServer.commonUploadImage(filePart);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserImageModel>() {
              @Override
              public void onSuccess(UserImageModel result) {
                try {
                  if (editClanDialog != null) {
                    editClanDialog.setPortrait(result.getImgPath(), result.getFile_path().getFile_path());
                  }
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
