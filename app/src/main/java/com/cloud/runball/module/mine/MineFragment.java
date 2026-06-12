package com.cloud.runball.module.mine;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseFragment;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.model.OthersInfoModel;
import com.cloud.runball.module.WristBallActivity;
import com.cloud.runball.module.clan.ClanActivity;
import com.cloud.runball.module.clan.dialog.ClanNoticeDialog;
import com.cloud.runball.module.clan.dialog.EditClanDialog;
import com.cloud.runball.module.clan.dialog.SearchClanDialog;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.CheatModel;
import com.cloud.runball.model.UserImageModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.mine_record.MineRankingRecordActivity;
import com.cloud.runball.module.mine_record.MineRecordActivity;
import com.cloud.runball.module.race.MatchMainActivity;
import com.cloud.runball.module.rank.ClanRankFragment;
import com.cloud.runball.module.social.MineHomepageActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AccountUtil;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.AvatarHelper;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.FragmentMineBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @author ns467
 */
public class MineFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks,View.OnClickListener {


  private static final String TAG = MineFragment.class.getSimpleName();

  private WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  public static final int REQUEST_CODE = 100;
  public static final int ALBUM_CODE = 101;
  public static final int REQUEST_CODE3 = 200;
  public static final int ALBUM_CODE3 = 201;

  final int HEAD_AVATAR_SIZE = 48 * 5;
  final int BADGE_SIZE = 64 * 2;
  final int BADGE_SIZE_HEIGHT = 72 * 2;

  private FragmentMineBinding binding;
  AppBarLayout layTop;
  CollapsingToolbarLayout layCollapsingToolbar;
  Toolbar toolbar;


  TextView tvTitle;
  ImageView img_info_avatar;
  TextView tv_info_nickname;
  TextView tvAge;
  TextView tvArea;
  TextView tvWeixin;
  TextView tv_info_score;
  TextView tv_info_sign;
  ImageView ivVip;
  View layItemClan;


  LinearLayout layItemMedal;
  LinearLayout layItemRecord;
  LinearLayout layItemDevice;
  LinearLayout layItemFeedback;
  LinearLayout layItemAbout;


  TextView tvMaxSpeed;
  TextView tvOneMinute;
  TextView tvExponent;
  TextView tvMarathon;

  private EditClanDialog editClanDialog;

  boolean isLoadUserInfo=false;
  boolean loadAvater = false;
  File file;

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_mine;
  }

  @Override
  protected View getImmersiveView() {
    return toolbar;
  }

  public enum State {
    EXPANDED,
    COLLAPSED,
    IDLE
  }

  private State mCurrentState = State.IDLE;

  private final AvatarHelper avatarHelper = AvatarHelper.fromFragment(this);

  @Override
  protected void onContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding = FragmentMineBinding.bind(view);
    layTop = binding.layTop;
    layCollapsingToolbar = binding.layCollapsingToolbar;
    toolbar = binding.toolbar;
    tvTitle = binding.tvTitle;
    img_info_avatar = binding.imgInfoAvatar;
    tv_info_nickname = binding.tvInfoNickname;
    tvAge = binding.tvAge;
    tvArea = binding.tvArea;
    tvWeixin = binding.tvWeixin;
    tv_info_score = binding.tvInfoScore;
    tv_info_sign = binding.tvInfoSign;
    ivVip = binding.ivVip;
    layItemClan = binding.layItemClan;
    layItemMedal = binding.layItemMedal;
    layItemRecord = binding.layItemRecord;
    layItemDevice = binding.layItemDevice;
    layItemFeedback = binding.layItemFeedback;
    layItemAbout = binding.layItemAbout;
    tvMaxSpeed = binding.tvMaxSpeed;
    tvOneMinute = binding.tvOneMinute;
    tvExponent = binding.tvExponent;
    tvMarathon = binding.tvMarathon;

    adaptImmersiveStatusBar();
    // Replace @OnClick with listeners
    // Header actions
    if (binding.imgInfoSetting != null) binding.imgInfoSetting.setOnClickListener(this::onClick);
    img_info_avatar.setOnClickListener(this::onClick);
    // Section entries
    if (binding.layAchievement != null) binding.layAchievement.setOnClickListener(this::onClick);
    if (binding.layItemMineDynamic != null) binding.layItemMineDynamic.setOnClickListener(this::onClick);
    layItemClan.setOnClickListener(this::onClick);
    layItemMedal.setOnClickListener(this::onClick);
    layItemRecord.setOnClickListener(this::onClick);
    layItemDevice.setOnClickListener(this::onClick);
    layItemFeedback.setOnClickListener(this::onClick);
    layItemAbout.setOnClickListener(this::onClick);
    if (binding.tvExchange != null) binding.tvExchange.setOnClickListener(this::onClick);
    if (binding.layScore != null) binding.layScore.setOnClickListener(this::onClick);
    layTop.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
          if (mCurrentState != State.EXPANDED) {
//            onStateChanged(appBarLayout, State.EXPANDED);
            tvTitle.setVisibility(View.GONE);
          }
          mCurrentState = State.EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
          if (mCurrentState != State.COLLAPSED) {
//            onStateChanged(appBarLayout, State.COLLAPSED);
            tvTitle.setVisibility(View.VISIBLE);
          }
          mCurrentState = State.COLLAPSED;
        } else {
          if (mCurrentState != State.IDLE) {
//            onStateChanged(appBarLayout, State.IDLE);
          }
          mCurrentState = State.IDLE;
        }
      }
    });
  }

  @Override
  protected void onLazyLoad() {

  }

  public static MineFragment newInstance() {
    return new MineFragment();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (loadAvater && file != null) {
      try {
        //上传头像
        Picasso.with(getContext())
            .load(file)
            .centerCrop()
            .resize(HEAD_AVATAR_SIZE, HEAD_AVATAR_SIZE)
            .into(img_info_avatar);
        try {
          //上传头像
          if (Build.VERSION.SDK_INT >= 30) {
            if (uri != null) {
              file =getCropFile(getContext(), uri);
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
      }catch (Exception ex){
        ex.printStackTrace();
      }
      loadAvater = false;
    }
    else{
      reloadUserInfo();
    }
  }

  private void reloadUserInfo() {
    if (AppDataManager.getInstance().getUserInfoModel() == null) {
      String token = String.valueOf(SPUtils.get(getActivity(), "token", ""));
      if (TextUtils.isEmpty(token)) {
        autoLogin();
      } else {
        WristBallRetrofitHelper.getInstance().updateToken(token);
        requestUserInfo();
      }
    } else {
      //这个剥离到切换tab时候显示
      onRefreshUserInfo(AppDataManager.getInstance().getUserInfoModel());
//      onRequestBadge();
    }
    requestCheatConfig();
    requestUserInfo();
  }

  private void requestUserInfo() {
    Observable<UserInfoModel> observable = apiServer.getUserInfo();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserInfoModel>() {
              @Override
              public void onSuccess(UserInfoModel userInfoModel) {
                isLoadUserInfo=true;
                AppLogger.d("--MineFragment--获取个人信息成功----");
                AppDataManager.getInstance().setUserInfoModel(userInfoModel);
                //把token保存起来
                WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
                SPUtils.put(getActivity(), "token", userInfoModel.getUser_info().getToken());
                onRefreshUserInfo(AppDataManager.getInstance().getUserInfoModel());
    //            onRequestBadge();

                adjustPkMatch();

                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }

              @Override
              public void onError(int code, String msg) {
                if(code==2){
                  Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                  autoLogin();
                }
              }
            })
    );
  }

  private void adjustPkMatch() {
    if (getActivity() instanceof WristBallActivity) {
      //对比开赛时间
      int cuTime = (int) (System.currentTimeMillis() / 1000);
      int startTime = (int) SPUtils.get(getActivity(), "pkdata_startTime", cuTime);
      int stopTime = (int) SPUtils.get(getActivity(), "pkdata_stopTime", cuTime);

      if (cuTime >= stopTime) {
        SPUtils.remove(getActivity(), "pkdata");
        SPUtils.remove(getActivity(), "pkdata_startTime");
        SPUtils.remove(getActivity(), "pkdata_stopTime");
        SPUtils.remove(getActivity(), "pkdata_keepPlayTime");
        return;
      }

      String pkdata = (String) SPUtils.get(getActivity(), "pkdata", "");
      String phone = AppDataManager.getInstance().getUserInfoModel().getUser_info().getPhone();
      if (!TextUtils.isEmpty(pkdata) && pkdata.startsWith("{") && pkdata.endsWith("}") && !TextUtils.isEmpty(phone)) {
        startMatchMainActivity(pkdata);
      }
    }
  }

  private void startMatchMainActivity(String pk_info) {
    Intent it = new Intent(getActivity(), MatchMainActivity.class);
    it.putExtra("pkdata", pk_info);
    startActivity(it);
  }

  public void onRefreshUserInfoWithOutSide() {
    onRefreshUserInfo(AppDataManager.getInstance().getUserInfoModel());
  }

//  /**
//   * 更新徽章信息
//   */
//  public void onRefreshBadges() {
//    if (AppDataManager.getInstance().getUserInfoModel() != null) {
//      if (AppDataManager.getInstance().getUserInfoModel().getUser_info().getMy_medal() != null) {
//        //徽章个数
//        int size = AppDataManager.getInstance().getUserInfoModel().getUser_info().getSys_medal_count();
//        initView(size);
//        //先清理
//        lyBadges.removeAllViews();
//        for (MedalInfo medalInfo : AppDataManager.getInstance().getUserInfoModel().getUser_info().getMy_medal()) {
//          View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_badge_item_small, null);
//          ImageView imageView = view.findViewById(R.id.ivBadge);
//          if (medalInfo.getMedal_image_active().startsWith("http")) {
//            Picasso.with(getActivity())
//                .load(medalInfo.getMedal_image_active()).centerCrop().resize(BADGE_SIZE,BADGE_SIZE_HEIGHT)
//                .into(imageView);
//          } else {
//            Picasso.with(getActivity())
//                .load(Constant.getBaseUrl() + "/" + medalInfo.getMedal_image_active()).centerCrop().resize(BADGE_SIZE,BADGE_SIZE_HEIGHT)
//                .into(imageView);
//          }
//          lyBadges.addView(view);
//        }
//      }
//    }
//  }

//  private void onRequestBadge(){
//    Observable<MedalInfoModel> observable  =apiServer.getAllBadges();
//    disposable.add(
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<MedalInfoModel>() {
//          @Override
//          public void onSuccess(MedalInfoModel medalInfoModel){
//            int medalCount=0;
//            for(MedalInfo medalInfo:medalInfoModel.getUser_medal()){
//              if(medalInfo.isIs_get()){
//                medalCount+=1;
//              }
//            }
//            AppDataManager.getInstance().getUserInfoModel().getUser_info().setSys_medal_count(medalCount);
//            AppDataManager.getInstance().getUserInfoModel().getUser_info().setMy_medal(medalInfoModel.getUser_medal());
//            onRefreshBadges();
//          }
//
//          @Override
//          public void onError(int code, String msg) {
//            AppLogger.d(msg);
//          }
//        })
//    );
//  }

  /**
   * 获取作弊配置数据
   */
  private void requestCheatConfig(){
    if(AppDataManager.getInstance().getErrSpeeds().size()<=0){
      Observable<CheatModel> observable = apiServer.cheat();
      disposable.add(
          observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<CheatModel>() {
            @Override
            public void onSuccess(CheatModel cheatModel) {
              AppLogger.d(cheatModel.toString());
              if(cheatModel!=null){
                if(AppDataManager.getInstance().getErrSpeeds().size()<=0){
                  AppDataManager.getInstance().addAllErrSpeeds(cheatModel.getErr_speed());
                }
                App.self().setCircleCount(cheatModel.getInit_circle_count());
              }
            }
            @Override
            public void onError(int code, String msg) {
              AppLogger.d(msg);
            }
          })
      );
    }
  }

  /**
   * 自动登录
   */
  private void autoLogin() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("sys_country", AppDataManager.getInstance().getCountry());
    map.put("device_uid", AppDataManager.getInstance().getAndroidId());
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<UserInfoModel> observable = apiServer.autoLogin(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
          @Override
          public void onSuccess(UserInfoModel userInfoModel) {
            //把token保存起来
            AppLogger.d("---onSuccess--UserInfoModel=" + userInfoModel);
            SPUtils.put(getContext(), "token", userInfoModel.getUser_info().getToken());
            AppDataManager.getInstance().setUserInfoModel(userInfoModel);
            WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
            onRefreshUserInfo(userInfoModel);
            requestUserInfo();
            //登录成功发送获取比赛tabs
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_RANK_MATCH));
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }

  /**
   * 更新用户信息header
   * @param model
   */
  public void onRefreshUserInfo(UserInfoModel model) {
    if (model != null) {
      Drawable drawableSex = null;
      if (SexConstant.SEX_MAN.equals(model.getUser_info().getSys_sex_id())) {
        drawableSex = getResources().getDrawable(R.mipmap.ic_man);
        drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
      } else if (SexConstant.SEX_WOMEN.equals(model.getUser_info().getSys_sex_id())) {
        drawableSex = getResources().getDrawable(R.mipmap.ic_women);
        drawableSex.setBounds(0, 0, drawableSex.getMinimumWidth(), drawableSex.getMinimumHeight());
      }
      tv_info_nickname.setCompoundDrawables(drawableSex, null, null, null);
      tv_info_nickname.setText(model.getUser_info().getUser_name());
      tvTitle.setText(model.getUser_info().getUser_name());
      tv_info_sign.setText(model.getUser_info().getSelf_description());

      if (AccountUtil.isUserAccount()) {
        String birthdayStr = model.getUser_info().getBirthday();
        if (TextUtils.isEmpty(birthdayStr)) {
          tvAge.setVisibility(View.GONE);
        } else {
          Date birthdayDate = null;
          try {
            birthdayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthdayStr);
          } catch (ParseException e) {
            e.printStackTrace();
          }
          if (birthdayDate != null) {
            long timestamp = new Date().getTime() - birthdayDate.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            int age = calendar.get(Calendar.YEAR) - 1970;
            if (age < 18) {
              tvAge.setVisibility(View.VISIBLE);
              tvAge.setText(R.string.lbl_age_between_2);
            } else {
              tvAge.setVisibility(View.GONE);
            }
          } else {
            tvAge.setVisibility(View.GONE);
          }
        }
        if (TextUtils.isEmpty(model.getUser_info().getAddress())) {
          tvArea.setVisibility(View.GONE);
        } else {
          tvArea.setText(model.getUser_info().getAddress());
        }
        if (TextUtils.isEmpty(model.getUser_info().getWechart_id())) {
          tvWeixin.setVisibility(View.GONE);
        } else {
          tvWeixin.setVisibility(View.VISIBLE);
          tvWeixin.setText(getString(R.string.format_weixin, model.getUser_info().getWechart_id()));
        }
      } else {
        tvArea.setVisibility(View.GONE);
        tvAge.setVisibility(View.GONE);
        tvWeixin.setVisibility(View.GONE);
      }

      tv_info_score.setText(String.valueOf(model.getUser_info().getIntegral()));

      String avatarUrl;
      if (model.getUser_info().getUser_img().startsWith("http")) {
        avatarUrl = model.getUser_info().getUser_img();
      } else {
        avatarUrl = Constant.getBaseUrl() + "/" + model.getUser_info().getUser_img();
      }
      Picasso.with(getActivity())
          .load(avatarUrl)
          .transform(new CircleTransform(MineFragment.this.getContext()))
          .into(img_info_avatar);


      if(model.getUser_info().getIs_members() == 1) {
        ivVip.setVisibility(View.VISIBLE);
      }else{
        ivVip.setVisibility(View.GONE);
      }

      if (model.getUser_info().getAchievement() != null) {
        tvMaxSpeed.setText(model.getUser_info().getAchievement().getSpeed_max() + "");
        tvOneMinute.setText(new BigDecimal(model.getUser_info().getAchievement().getExponent_molecular() / 1000 + "").setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        tvExponent.setText(model.getUser_info().getAchievement().getRunball_exponent() + "");
        tvMarathon.setText(TimeUtils.formatDuration3(model.getUser_info().getAchievement().getMarathon()));
      }


//      //是否显示申请会员入口
//      if(model.getUser_info().getShow_members_entrance()==1){
//        ryVip.setVisibility(View.VISIBLE);
//      }else{
//        ryVip.setVisibility(View.GONE);
//      }
      layItemClan.setEnabled(false);
      if (AppDataManager.getInstance().getUserInfoModel() != null) {
        String uid = AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id();
        loadUserOthersInfo(uid);
      }
    }
  }

  private OthersInfoModel.UserClanMembers userClanMembers = null;

  private void loadUserOthersInfo(String id) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("member_user_id", id);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    disposable.add(
        apiServer.getUserOthersInfo(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<OthersInfoModel>() {
              @Override
              public void onSuccess(OthersInfoModel model) {
                layItemClan.setEnabled(true);
                if (model.getUserClanMembers() != null) {
                  userClanMembers = model.getUserClanMembers();
                } else {
                  userClanMembers = null;
                }
              }
              @Override
              public void onError(int code, String msg) {
                Toast.makeText(MineFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  /**
   * 打开相册
   */
  private void openAlbum() {
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    } else {
      Intent intent = new Intent(Intent.ACTION_PICK, null);
      intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
      startActivityForResult(intent, ALBUM_CODE);
    }
  }

  private File getFileFromUri(Uri uri, Context context) {
    if (uri == null) {
      return null;
    }
    switch (uri.getScheme()) {
      case "content":
        return getFileFromContentUri(uri, context);
      case "file":
        return new File(uri.getPath());
      default:
        return null;
    }
  }

  /**
   * 通过内容解析中查询uri中的文件路径
   */
  private File getFileFromContentUri(Uri contentUri, Context context) {
    if (contentUri == null) {
      return null;
    }
    File file = null;
    String filePath;
    String[] filePathColumn = {MediaStore.MediaColumns.DATA};
    ContentResolver contentResolver = context.getContentResolver();
    Cursor cursor = contentResolver.query(contentUri, filePathColumn, null,
        null, null);
    if (cursor != null) {
      cursor.moveToFirst();
      filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
      cursor.close();

      if (!TextUtils.isEmpty(filePath)) {
        file = new File(filePath);
      }
    }
    return file;
  }

  public  File getCropFile(Context context,Uri uri){
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

  /**
   * 文件封装
   * @param file
   */
  public void updateFile(File file) {
    AppLogger.d("---updateFile--上传头像----调用-");
    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    Observable<UserImageModel> observable = apiServer.uploadImage(filePart);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserImageModel>() {
          @Override
          public void onSuccess(UserImageModel result) {
            try {
              AppLogger.d("---updateFile--上传头像-" + result.toString());
              AppDataManager.getInstance().getUserInfoModel().getUser_info().setUser_img(result.getUser_img_path());
              //不直接显示，避免图片重新加载
              //updateUserInfo(AppDataManager.getInstance().getUserInfoModel());
              if(file.isFile() && file.exists()){
                file.delete();
              }
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }

          @Override
          public void onError(int code, String msg) {
            Logger.d(msg);
          }
        })
    );
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    if (requestCode == ALBUM_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //允许权限,打开相册
        openAlbum();
      }
    } else if (requestCode == ALBUM_CODE3) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //允许权限,打开相册
        openAlbum3();
      }
    }
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    AppLogger.d("onActivityResult:requestCode=" + requestCode);
    if (requestCode == ALBUM_CODE && resultCode == Activity.RESULT_OK) {
      AppLogger.d("修改用户信息返回2");
      loadAvater = true;
      Uri sourceUri = data.getData();
      gotoClipActivity2(sourceUri);
    } else if (requestCode == ALBUM_CODE3 && resultCode == Activity.RESULT_OK) {
      Uri sourceUri = data.getData();
      gotoClipActivity3(sourceUri);
    } else if(requestCode == REQUEST_CODE && resultCode == REQUEST_CODE){
      String city = data.getStringExtra("city");
      if (editClanDialog != null) {
        editClanDialog.setArea(city);
      }
    }
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    isLoadUserInfo=false;
  }


  /**
   * 是否正式用户
   * @return
   */
  private boolean isOfficialUser(){
    if(AppDataManager.getInstance().getUserInfoModel()!=null){
      if(AppDataManager.getInstance().getUserInfoModel().getUser_info()!=null){
        String phone=AppDataManager.getInstance().getUserInfoModel().getUser_info().getPhone();
        if(!TextUtils.isEmpty(phone)){
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      Intent intent = new Intent(Intent.ACTION_PICK, null);
      intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
      startActivityForResult(intent, ALBUM_CODE);
    }
  }

  @Override
  public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

  }

  /**
   * 打开截图的界面
   * @param uri
   */
  private void gotoClipActivity(Uri uri){
    if(uri == null){
      return;
    }
    Intent intent = new Intent(getActivity(),ClipImageActivity.class);
    intent.putExtra("type",0);
    intent.setData(uri);
    startActivityLaunch.launch(intent);
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

  private void gotoClipActivity2(Uri sourceUri){
    // 调用系统中自带的图片剪裁
    file = createImageFile(getContext(), true);
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
    if(result.getResultCode()==android.app.Activity.RESULT_OK){
      AppLogger.d("---------裁剪头像返回----------");
      loadAvater = true;
    }
  });

  ActivityResultLauncher<Intent> startActivityLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    int resultCode=result.getResultCode();
    if(resultCode== UserInfoActivity.REPORT_OK){
      AppLogger.d("修改用户信息返回");
      onRefreshUserInfoWithOutSide();
      requestUserInfoWithReturn();
    }else if(resultCode== ClipImageActivity.REQUEST_CROP_PHOTO){
      AppLogger.d("---------裁剪头像返回----------");
      final Uri uri = result.getData().getData();
      if (uri == null) {
        return;
      }
      file=getFileFromUri(uri,getActivity());
    }
  });

  private void requestUserInfoWithReturn() {
    Observable<UserInfoModel> observable = apiServer.getUserInfo();
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
          @Override
          public void onSuccess(UserInfoModel userInfoModel) {
            AppLogger.d("--MineFragment-获取个人信息成功----");
            AppDataManager.getInstance().setUserInfoModel(userInfoModel);
            WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
            SPUtils.put(getContext(), "token", userInfoModel.getUser_info().getToken());

            EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
          }

          @Override
          public void onError(int code, String msg) {
            Toast.makeText(MineFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
          }
        })
    );
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onServiceNoticeEvent(MessageEvent event) {
    switch (event.getEvetId()) {
      case MessageEvent.REFRESH: {
        layItemClan.setEnabled(false);
        if (AppDataManager.getInstance().getUserInfoModel() != null) {
          String uid = AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id();
          loadUserOthersInfo(uid);
        }
      }
    }
  }

  public void onClick(View view) {
    if (view.getId() == R.id.img_info_setting) {
      startActivity(new Intent(getContext(), SettingActivity.class));
    } else if (view.getId() == R.id.img_info_avatar) {
      if(isOfficialUser()){
        Intent it = new Intent(getContext(), UserInfoActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityLaunch.launch(it);
      }else{
        //头像
        openAlbum();
      }
    } else if (view.getId() == R.id.layAchievement) {
      MineRankingRecordActivity.startAction(MineFragment.this.getContext());
    } else if (view.getId() == R.id.layItemMineDynamic) {
      if (!AccountUtil.isUserAccount()) {
        Intent it = new Intent(getContext(), LoginOtherActivity.class);
        startActivity(it);
        return;
      }
      if (AppDataManager.getInstance().getUserInfoModel() != null) {
        String uid = AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_id();
        MineHomepageActivity.startAction(MineFragment.this.getContext(), uid);
      }
    } else if (view.getId() == R.id.layItemClan) {
      if (!AccountUtil.isUserAccount()) {
        Intent it = new Intent(getContext(), LoginOtherActivity.class);
        startActivity(it);
        return;
      }
      if (userClanMembers == null || userClanMembers.getStatus() == 3) {
        ClanNoticeDialog clanNoticeDialog = new ClanNoticeDialog(MineFragment.this.getContext());
        clanNoticeDialog.setOnCreateClanCallback(dialog -> {
          dialog.dismiss();
          editClanDialog = new EditClanDialog(MineFragment.this.getContext());
          editClanDialog.setDate(false);
          editClanDialog.setCallback(new EditClanDialog.Callback() {
            @Override
            public void onPortraitClick() {
              avatarHelper.pickAvatar(result -> {
                if (result != null) {
                  editClanDialog.setPortrait(result.file, result.file.getPath());
                }
              });
            }
            @Override
            public void onAreaClick(String area) {
              ARouter.getInstance().build("/city/CityActivity")
                  .withBoolean("area", true)
                  .navigation(MineFragment.this.getActivity(), REQUEST_CODE);
            }
            @Override
            public void onSubmit(Dialog dialog, boolean isEdit, String clanName, String clanAvatar, String address, String introduction, String telephone, String remark) {
              registerClan(dialog, isEdit, clanName, clanAvatar, address, introduction, telephone, remark);
            }
          });
        });
        clanNoticeDialog.setOnJoinClanCallback(dialog -> {
          dialog.dismiss();
          SearchClanDialog searchClanDialog = new SearchClanDialog(this.getContext());
          searchClanDialog.setCallback((dialog1, itemData) -> {
            ClanActivity.startAction(MineFragment.this.getContext(), itemData.getId() + "");
          });
        });
      } else {
        ClanActivity.startAction(MineFragment.this.getContext(), userClanMembers.getUserClanId() + "");
      }
    } else if (view.getId() == R.id.layItemMedal) {
      startActivity(new Intent(getContext(), MineBadgeActivity.class));
    } else if (view.getId() == R.id.layItemRecord) {
      MineRecordActivity.startAction(MineFragment.this.getContext(), new Date(), true);
    } else if (view.getId() == R.id.layItemDevice) {
      //管理设备(返回处理在activity中处理)
      Intent it = new Intent(getContext(), ManagerDeviceInfoActivity.class);
      getActivity().startActivity(it);
    } else if (view.getId() == R.id.layItemFeedback) {
      //反馈与帮助
      startActivity(new Intent(MineFragment.this.getContext(), FeedBackActivity.class));
    } else if (view.getId() == R.id.layItemAbout) {
      //关于
      startActivity(new Intent(MineFragment.this.getContext(), AboutActivity.class));
//      startActivity(new Intent(getContext(), MineDataActivity.class));
    } else if (view.getId() == R.id.layScore) {
      Toast.makeText(MineFragment.this.getContext(), R.string.score_tip, Toast.LENGTH_SHORT).show();
    }
  }

//  @Override
//  public void onClick(View v) {
//    if(v.getId() == R.id.ivScore){
//      PopupWindowIndex.self().build(getContext(), getString(R.string.score_tip)).show(v);
//    }else if (v.getId() == R.id.ryMineMatch) {
//      //我的赛事,首先要判断是否事注册用户
//      if(isOfficialUser()){
//        Intent it = new Intent(getContext(), MineMatchActivity.class);
//        startActivity(it);
//      }else{
//        Intent it=new Intent(getContext(), LoginOtherActivity.class);
//        startActivity(it);
//      }
//    }else if (v.getId() == R.id.ryRanking) {
//      if(isOfficialUser()){
//        Intent it = new Intent(getContext(), RankingSwitchActivity.class);
//        startActivity(it);
//      }else{
//        startLoginOtherActivity();
//      }
//    }else if (v.getId() == R.id.ryAddDevices) {
//      //管理设备(返回处理在activity中处理)
//      Intent it = new Intent(getContext(), ManagerDeviceInfoActivity.class);
//      getActivity().startActivity(it);
//    } else if (v.getId() == R.id.ryScore) {
//      //我的成就
//      Intent it = new Intent(getContext(), MineScoreActivity.class);
//      startActivity(it);
//    } else if (v.getId() == R.id.ryData) {
//      //我的数据
//      startActivity(new Intent(getContext(), MineDataActivity.class));
//    } else if (v.getId() == R.id.img_info_edit) {
//      //个人信息
//      if(isOfficialUser()){
//        Intent it = new Intent(getContext(), UserInfoActivity.class);
//        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivityLaunch.launch(it);
//      }else{
//        startLoginOtherActivity();
//      }
//    } else if (v.getId() == R.id.tv_info || v.getId() == R.id.img_info_more) {
//      //我的徽章
//      startActivity(new Intent(getContext(), MineBadgeActivity.class));
//    } else if (v.getId() == R.id.ry_info_avatar) {
//      if(isOfficialUser()){
//        Intent it = new Intent(getContext(), UserInfoActivity.class);
//        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivityLaunch.launch(it);
//      }else{
//        //头像
//        openAlbum();
//      }
//    }else if (v.getId() == R.id.img_info_setting) {
//      startActivity(new Intent(getContext(), SettingActivity.class));
//    }
//    else if (v.getId() == R.id.ryVip || v.getId() == R.id.img_mine_Vip_more) {
//      if(isOfficialUser()){
//        //会员申请
//        if(AppDataManager.getInstance().getUserInfoModel()!=null){
//          if(AppDataManager.getInstance().getUserInfoModel().getUser_info()!=null){
//            String url=AppDataManager.getInstance().getUserInfoModel().getUser_info().getMembers_entrance_url();
//            openBrowser(getContext(),url);
//          }
//        }
//      }else{
//        startLoginOtherActivity();
//      }
//    }else if(v.getId() == R.id.ryMarket || v.getId() == R.id.img_mine_Market_more){
//      if(AppDataManager.getInstance().getUserInfoModel()!=null){
//        if(AppDataManager.getInstance().getUserInfoModel().getUser_info()!=null){
//          String url=AppDataManager.getInstance().getUserInfoModel().getUser_info().getShop_url();
//          openBrowser(getContext(),url);
//        }else{
//          openBrowser(getContext(),"https://hisport.cloud/product/product-buy");
//        }
//      }
//    }
//  }

  private void openBrowser(Context context, String url){
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    Uri content_url = Uri.parse(url);
    intent.setData(content_url);
    startActivity(intent);
  }

  private void startLoginOtherActivity(){
    Intent it=new Intent(getContext(), LoginOtherActivity.class);
    it.putExtra("resultCode",true);
    loginActivityLaunch.launch(it);
  }

  ActivityResultLauncher<Intent> loginActivityLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    int resultCode = result.getResultCode();
    if(resultCode == LoginOtherActivity.LoginOtherActivity_result) {
      if(AppDataManager.getInstance().getUserInfoModel()!=null) {
        //获取用户信息
        requestUserInfo();
      }
    }
  });

  private void registerClan(Dialog dialog, boolean isEdit, String clanName, String clanAvatar, String address, String introduction, String telephone, String remark) {
    if (!isEdit) {
      if (TextUtils.isEmpty(clanName)) {
        Toast.makeText(MineFragment.this.getContext(), R.string.tip_name_is_must_not_empty, Toast.LENGTH_SHORT).show();
        return;
      }
      if (TextUtils.isEmpty(clanAvatar)) {
        Toast.makeText(MineFragment.this.getContext(), R.string.tip_avatar_is_must_not_empty, Toast.LENGTH_SHORT).show();
        return;
      }
      if (TextUtils.isEmpty(address)) {
        Toast.makeText(MineFragment.this.getContext(), R.string.tip_area_is_must_not_empty, Toast.LENGTH_SHORT).show();
        return;
      }
      if (TextUtils.isEmpty(telephone)) {
        Toast.makeText(MineFragment.this.getContext(), R.string.tip_contact_is_must_not_empty, Toast.LENGTH_SHORT).show();
        return;
      }
      if (!isEdit) {
        if (TextUtils.isEmpty(remark)) {
          Toast.makeText(MineFragment.this.getContext(), R.string.tip_remark_is_must_not_empty, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MineFragment.this.getContext(), R.string.tip_clan_create_pending, Toast.LENGTH_SHORT).show();
                if (dialog != null) {
                  dialog.dismiss();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH));
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("registerClan --- " + msg);
                Toast.makeText(MineFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
              }
            })
    );
  }

  /**
   * 打开相册
   */
  private void openAlbum3() {
    if (ActivityCompat.checkSelfPermission(MineFragment.this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(MineFragment.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    } else {
      Intent intent = new Intent(Intent.ACTION_PICK, null);
      intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
      startActivityForResult(intent, ALBUM_CODE);
    }
  }

  public Uri uri3;
  public File getAppRootDirPath3() {
    return getActivity().getExternalFilesDir(null).getAbsoluteFile();
  }

  public File createImageFile3(Context context,boolean isCrop) {
    try {
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String fileName = "";
      if (isCrop) {
        fileName = "IMG_"+timeStamp+"_CROP.jpg";
      } else {
        fileName = "IMG_"+timeStamp+".jpg";
      }
      File rootFile = new File(getAppRootDirPath3() + File.separator + "capture");
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
        uri3 = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      }else {
        imgFile = new File(rootFile.getAbsolutePath() + File.separator + fileName);
      }
      return imgFile;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  File file3 = null;
  public File getCropFile3(Context context, Uri uri){
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

  private void gotoClipActivity3(Uri sourceUri){
    file = createImageFile3(MineFragment.this.getContext(), true);
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
    launcher3.launch(intent);
  }

  ActivityResultLauncher<Intent> launcher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    if(result.getResultCode() == Activity.RESULT_OK) {
      if(file3 != null) {
        //上传头像
        try {
          //上传头像
          if (Build.VERSION.SDK_INT >= 30) {
            if (uri != null) {
              file3 = getCropFile3(MineFragment.this.getContext(), uri3);
              if (file3.exists()) {
                updateFile3(file3);
              }
            }
          } else {
            if (file3.exists()) {
              updateFile3(file3);
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
  private void updateFile3(File file) {
    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    Observable<UserImageModel> observable = apiServer.uploadImage(filePart);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<UserImageModel>() {
              @Override
              public void onSuccess(UserImageModel result) {
                try {
                  if (editClanDialog != null) {
                    editClanDialog.setPortrait(file, result.getFile_path().getFile_path());
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