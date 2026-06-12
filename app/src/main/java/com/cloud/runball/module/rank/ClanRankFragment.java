package com.cloud.runball.module.rank;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.model.ClanRankingModel;
import com.cloud.runball.model.UserImageModel;
import com.cloud.runball.module.clan.ClanActivity;
import com.cloud.runball.module.clan.dialog.EditClanDialog;
import com.cloud.runball.module.clan.dialog.ClanNoticeDialog;
import com.cloud.runball.module.clan.dialog.SearchClanDialog;
import com.cloud.runball.module.rank.adapter.ClanRankAdapter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.FragmentClanRankBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.fragment
 * @ClassName: MatchRankingFragment
 * @Description: 榜单排行榜
 * @Author: zhd
 * @CreateDate: 2021/6/2 18:05
 * @UpdateUser: zhd
 * @UpdateDate: 2021/6/2 18:05
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ClanRankFragment extends BaseFragment {

  private FragmentClanRankBinding binding;

  RelativeLayout ryEmpty;
  XRecyclerView recyclerview;
  TextView tvRankNum;
  ImageView ivHead;
  TextView tvClanName;
  TextView tvValue;
  TextView tvMemberCount;
  LinearLayout lyBottom;
  TextView tvArea;
  TextView tvUnit;
  ImageView ivMore;
  Group groupPass;
  TextView tvPendingState;
  View layBottomTools;

  private static final String KEY_TYPE = "type";

  private int page = 1;
  private final List<ClanRankingModel.ClanRankItem> list = new ArrayList<>();
  private int type;
  private boolean isShowBottom;
  private String parameter;
  private ClanRankingModel.MyClanInfo myClanInfo;

  private static final int ALBUM_CODE = 0x00000014;
  public static final int REQUEST_CODE = 100;

  private final WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  private EditClanDialog editClanDialog;

  public static ClanRankFragment newInstance(String type, String parameter, boolean isShowBottom) {
    ClanRankFragment fragment = new ClanRankFragment();
    Bundle args = new Bundle();
    if ("max_speed".equals(type)) {
      args.putInt(KEY_TYPE, 1);
    } else if ("onemin".equals(type)) {
      args.putInt(KEY_TYPE, 2);
    } else if ("exponent".equals(type)) {
      args.putInt(KEY_TYPE, 3);
    } else if ("marathon".equals(type)) {
      args.putInt(KEY_TYPE, 4);
    }
    args.putBoolean("isShowBottom", isShowBottom);
    args.putString("parameter", parameter);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_clan_rank;
  }

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentClanRankBinding.bind(view);
    ryEmpty = binding.ryEmpty;
    recyclerview = binding.recyclerview;
    tvRankNum = binding.tvRankNum;
    ivHead = binding.ivHead;
    tvClanName = binding.tvClanName;
    tvValue = binding.tvValue;
    tvMemberCount = binding.tvMemberCount;
    lyBottom = binding.lyBottom;
    tvArea = binding.tvArea;
    tvUnit = binding.tvUnit;
    ivMore = binding.ivMore;
    groupPass = binding.groupPass;
    tvPendingState = binding.tvPendingState;
    layBottomTools = binding.layBottomTools;
    EventBus.getDefault().register(this);
    // Replace @OnClick with listeners
    binding.tvCreateClan.setOnClickListener(this::onClick);
    binding.tvJoinClan.setOnClickListener(this::onClick);
    lyBottom.setOnClickListener(this::onClick);
    if (getArguments() != null) {
      type = getArguments().getInt(KEY_TYPE);
      parameter = getArguments().getString("parameter");
      isShowBottom = getArguments().getBoolean("isShowBottom");
    }
    initRankList();
  }

  @Override
  protected void onLazyLoad() {
    loadRankListData(true, 1);
  }

  public void onClick(View view) {
    if (view.getId() == R.id.tvCreateClan) {
      ClanNoticeDialog noticeDialog = new ClanNoticeDialog(this.getContext());
      noticeDialog.setOnCreateClanCallback(dialog -> {
        dialog.dismiss();
        editClanDialog = new EditClanDialog(ClanRankFragment.this.getContext());
        editClanDialog.setDate(false);
        editClanDialog.setCallback(new EditClanDialog.Callback() {
          @Override
          public void onPortraitClick() {
            openAlbum();
          }
          @Override
          public void onAreaClick(String area) {
            ARouter.getInstance().build("/city/CityActivity")
                .withBoolean("area", true)
                .navigation(ClanRankFragment.this.getActivity(), REQUEST_CODE);
          }
          @Override
          public void onSubmit(Dialog dialog, boolean isEdit, String clanName, String clanAvatar, String address, String introduction, String telephone, String remark) {
            registerClan(dialog, isEdit, clanName, clanAvatar, address, introduction, telephone, remark);
          }
        });
      });
    } else if (view.getId() == R.id.tvJoinClan) {
      ClanNoticeDialog noticeDialog = new ClanNoticeDialog(this.getContext());
      noticeDialog.setOnJoinClanCallback(dialog -> {
        dialog.dismiss();
        SearchClanDialog searchClanDialog = new SearchClanDialog(this.getContext());
        searchClanDialog.setCallback((dialog1, itemData) -> {
          ClanActivity.startAction(ClanRankFragment.this.getContext(), itemData.getId() + "");
        });
      });
    } else if (view.getId() == R.id.lyBottom) {
      ClanActivity.startAction(ClanRankFragment.this.getContext(), myClanInfo.getUserClanId());
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onServiceNoticeEvent(MessageEvent event) {
    switch (event.getEvetId()) {
      case MessageEvent.REFRESH: {
        loadRankListData(true, 1);
      }
    }
  }

  private void initRankList() {
    ClanRankAdapter adapter = new ClanRankAdapter(type, list, ( itemData ) -> {
      ClanActivity.startAction(this.getContext(), itemData.getId());
    });
    //初始化我的数据信息
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        loadRankListData(true, 1);
      }
      @Override
      public void onLoadMore() {
        loadRankListData(false, page + 1);
      }
    });
    recyclerview.setAdapter(adapter);
  }

  private void loadRankListData(boolean isRefresh, int pageIndex) {
    if (!isShowBottom && TextUtils.isEmpty(parameter)) {
      list.clear();
      ClanRankAdapter adapter = (ClanRankAdapter) recyclerview.getAdapter();
      if(adapter != null){
        adapter.notifyDataSetChanged();
      }
      showBottom(null);
      return;
    }
    AppLogger.d("--榜单列表---" + type + "; pageIndex = " + pageIndex);
    HashMap<String, Object> map = new HashMap<>();
    map.put("page", pageIndex);
    map.put("limit", 10);
    map.put("type", type);
    if (!TextUtils.isEmpty(parameter)) {
      map.put("title", parameter);
    }
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.getClanRankingList(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ClanRankingModel>() {
              @Override
              public void onSuccess(ClanRankingModel model) {
                if(model != null) {
                  List<ClanRankingModel.ClanRankItem> listData = model.getUserClanList().getList();
                  if (isRefresh) {
                    list.clear();
                    page = 1;
                  } else {
                    if (listData == null || listData.size() == 0) {
                      return;
                    }
                    page = pageIndex;
                  }
                  if (listData != null) {
                    list.addAll(listData);
                  }
                  ClanRankAdapter adapter = (ClanRankAdapter) recyclerview.getAdapter();
                  if(adapter != null){
                    adapter.notifyDataSetChanged();
                  }
                  showBottom(model.getMyClanInfo());
                } else {
                  showBottom(null);
                }
                if (list.size() == 0) {
                  ryEmpty.setVisibility(View.VISIBLE);
                } else {
                  ryEmpty.setVisibility(View.GONE);
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("--requestRanking---" + msg);
                Toast.makeText(ClanRankFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
              @Override
              public void onComplete() {
                super.onComplete();
                if(recyclerview != null) {
                  recyclerview.refreshComplete();
                  recyclerview.loadMoreComplete();
                }
              }
            })
    );
  }

  private void showBottom(ClanRankingModel.MyClanInfo data) {
    if (!isShowBottom) {
      layBottomTools.setVisibility(View.GONE);
      lyBottom.setVisibility(View.GONE);
      return;
    }

    if(AppDataManager.getInstance().getUserInfoModel() != null) {
      //没有手机号或邮箱
      if("游客".equals(AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_type_name())) {
        lyBottom.setVisibility(View.GONE);
        layBottomTools.setVisibility(View.GONE);
        return;
      }
    }

    myClanInfo = data;
    if (data != null && data.getUserClan() != null && !TextUtils.isEmpty(data.getUserClan().getId())) {
      //俱乐部状态：状态：0审核中  1正常  2已拒绝
      //用户申请加入俱乐部状态：'状态：1待审核 2已通过 0已拒绝'
      if (data.getStatus() != 2 || data.getUserClan().getStatus() != 1) {
        lyBottom.setVisibility(View.GONE);
        layBottomTools.setVisibility(View.GONE);
        return;
      }

      lyBottom.setVisibility(View.VISIBLE);
      layBottomTools.setVisibility(View.GONE);
      groupPass.setVisibility(View.VISIBLE);
//      // 俱乐部状态：0审核中 1正常 2已拒绝
//      if (data.getUserClan().getStatus() == 0) {
//        groupPass.setVisibility(View.GONE);
//        tvPendingState.setVisibility(View.VISIBLE);
//      } else if (data.getUserClan().getStatus() == 1) {
//        groupPass.setVisibility(View.VISIBLE);
//        tvPendingState.setVisibility(View.GONE);
//      } else if (data.getUserClan().getStatus() == 2) {
//        lyBottom.setVisibility(View.GONE);
//        layBottomTools.setVisibility(View.VISIBLE);
//      }
      String user_img;
      if(data.getUserClan().getClanAvatar().startsWith("http")) {
        user_img = data.getUserClan().getClanAvatar();
      } else {
        user_img = Constant.getBaseUrl() + "/" + data.getUserClan().getClanAvatar();
      }
      Picasso.with(ClanRankFragment.this.getContext())
          .load(user_img)
//          .transform(new CircleTransform(ClanRankFragment.this.getContext()))
//          .placeholder(R.mipmap.default_head)
          .into(ivHead);

      tvClanName.setText(data.getUserClan().getTitle());

      tvArea.setText(data.getUserClan().getAddress());
      tvMemberCount.setText(getString(R.string.association_match_join_sum, data.getCount() + ""));

      String value = null, unit = null;
      // 1最高转速 2摇跑一分钟 3摇跑指数 4马拉松
      if (this.type == 1) {
        value = data.getSpeedMax();
        unit = data.getSpeedMaxUnit();
      } else if (this.type == 2) {
        value = data.getExponentMolecular();
        unit = data.getExponentMolecularUnit();
      } else if (this.type == 3) {
        value = data.getRunballExponents();
      } else if (this.type == 4) {
        value = data.getMarathons();
      }
      if (TextUtils.isEmpty(value) || "0".equals(value) || "00:00:00".equals(value)) {
        tvRankNum.setText("/");
        tvValue.setText("/");
        tvUnit.setVisibility(View.GONE);
      } else {
        tvRankNum.setText(String.valueOf(data.getIndex()));
        tvValue.setText(value);
        if (TextUtils.isEmpty(unit)) {
          tvUnit.setVisibility(View.GONE);
        } else {
          tvUnit.setVisibility(View.VISIBLE);
          tvUnit.setText(this.getString(R.string.format_brackets, unit));
        }
      }
    } else {
      lyBottom.setVisibility(View.GONE);
      layBottomTools.setVisibility(View.VISIBLE);
    }
  }

  private void registerClan(Dialog dialog, boolean isEdit, String clanName, String clanAvatar, String address, String introduction, String telephone, String remark) {
    if (!isEdit) {
      if (TextUtils.isEmpty(clanName)) {
        Toast.makeText(ClanRankFragment.this.getContext(), R.string.tip_name_is_must_not_empty, Toast.LENGTH_SHORT).show();
        return;
      }
      if (TextUtils.isEmpty(clanAvatar)) {
        Toast.makeText(ClanRankFragment.this.getContext(), R.string.tip_avatar_is_must_not_empty, Toast.LENGTH_SHORT).show();
        return;
      }
      if (TextUtils.isEmpty(address)) {
        Toast.makeText(ClanRankFragment.this.getContext(), R.string.tip_area_is_must_not_empty, Toast.LENGTH_SHORT).show();
        return;
      }
      if (TextUtils.isEmpty(telephone)) {
        Toast.makeText(ClanRankFragment.this.getContext(), R.string.tip_contact_is_must_not_empty, Toast.LENGTH_SHORT).show();
        return;
      }
      if (!isEdit) {
        if (TextUtils.isEmpty(remark)) {
          Toast.makeText(ClanRankFragment.this.getContext(), R.string.tip_remark_is_must_not_empty, Toast.LENGTH_SHORT).show();
          return;
        }
      }
    }
    HashMap<String, Object> map = new HashMap<>();
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
    if (!isEdit) {
      map.put("remark", remark);
    }
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.registerClan(requestBody)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<Boolean>() {
              @Override
              public void onSuccess(Boolean model) {
                Toast.makeText(ClanRankFragment.this.getContext(), R.string.tip_clan_create_pending, Toast.LENGTH_SHORT).show();
                if (dialog != null) {
                  dialog.dismiss();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("registerClan --- " + msg);
                Toast.makeText(ClanRankFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  /**
   * 打开相册
   */
  private void openAlbum() {
    if (ActivityCompat.checkSelfPermission(ClanRankFragment.this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(ClanRankFragment.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
    } else if(requestCode == REQUEST_CODE && resultCode == REQUEST_CODE) {
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
    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    AppLogger.d("----------ClanRankFragment-----onDestroyView--------------mRanking_type = " + type);
    EventBus.getDefault().unregister(this);
    if(recyclerview != null) {
      recyclerview.destroy();
      recyclerview = null;
    }
  }
}
