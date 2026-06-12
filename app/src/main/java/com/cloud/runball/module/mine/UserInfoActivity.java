package com.cloud.runball.module.mine;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.page.BaseActivity;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.FileUtils;
import com.cloud.runball.bean.PickerOption;
import com.cloud.runball.bean.JsonBean2;
import com.cloud.runball.bean.SexOption;
import com.cloud.runball.bean.UserInfo;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.model.UserImageModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.utils.GetJsonDataUtil;
import com.cloud.runball.dialog.PhotoDialog;
import com.cloud.runball.widget.CircleTransform;
import com.cloud.runball.widget.CircleTransform2;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cloud.runball.databinding.ActivityUserinfoBinding;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: UserInfoActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/2/9 11:07
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/9 11:07
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class UserInfoActivity extends BaseActivity {
  private ActivityUserinfoBinding binding;
  Toolbar toolbar;
  EditText edtName;
  EditText etWeixin;
  EditText edtSign;
  TextView tvBirthday;
  TextView tvBirthdayTip;
  TextView tvSex;
  TextView tvSexTip;
  TextView tvCity;
  TextView tvCityTip;
  ImageView img_info_avatar;
  TextView tvHeight;
  TextView tvWeight;

  public static final int REQUEST_CODE = 100;

  // 申请相机权限的requestCode
  private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
  private static final int CAMERA_REQUEST_CODE = 0x00000013;
  private static final int ALBUM_CODE = 0x00000014;

  private List<JsonBean2> options1CityItems = new ArrayList<>();
  private ArrayList<ArrayList<String>> options2CityItems = new ArrayList<>();
  private ArrayList<ArrayList<ArrayList<String>>> options3CityItems = new ArrayList<>();


  private ArrayList<ArrayList<String>> options2CityItemsCode = new ArrayList<>();
  private ArrayList<ArrayList<ArrayList<String>>> options3CityItemsCode = new ArrayList<>();

  private int modifyBirthdayTimes = 3;
  private int modifyCityTimes = 3;
  private int modifySexTimes = 3;

  private String sexId;
  private int userHeight = 165;
  private int userWeight = 50;

  private final WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();

  @Override
  protected int onLayoutId() {
    return R.layout.activity_userinfo;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityUserinfoBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void onContent(@Nullable Bundle savedInstanceState) {
    toolbar = binding.toolbar;
    edtName = binding.edtName;
    etWeixin = binding.etWeixin;
    edtSign = binding.edtSign;
    tvBirthday = binding.tvBirthday;
    tvBirthdayTip = binding.tvBirthdayTip;
    tvSex = binding.tvSex;
    tvSexTip = binding.tvSexTip;
    tvCity = binding.tvCity;
    tvCityTip = binding.tvCityTip;
    img_info_avatar = binding.imgInfoAvatar;
    tvHeight = binding.tvHeight;
    tvWeight = binding.tvWeight;
    toolbar.setNavigationOnClickListener(v -> finish());

    // Replace @OnClick with listeners
    tvBirthday.setOnClickListener(this::onViewClicked);
    tvCity.setOnClickListener(this::onViewClicked);
    img_info_avatar.setOnClickListener(this::onViewClicked);
    tvSex.setOnClickListener(this::onViewClicked);
    tvHeight.setOnClickListener(this::onViewClicked);
    tvWeight.setOnClickListener(this::onViewClicked);
    binding.tvSubmit.setOnClickListener(this::onViewClicked);

    updateUserInfo(AppDataManager.getInstance().getUserInfoModel());
    showUserInfo(AppDataManager.getInstance().getUserInfoModel());
    initJsonData();
    initLunarPicker();
  }

  public void showUserInfo(UserInfoModel model) {
    if(model == null) {
      return;
    }

    if(model.getUser_info() == null) {
      return;
    }

    //剩余修改次数
    modifyBirthdayTimes = model.getUser_info().getUser_birthday_change();
    modifyCityTimes = model.getUser_info().getUser_city_change();
    modifySexTimes = model.getUser_info().getSys_sex_id_change();

    tvBirthdayTip.setText(getString(R.string.format_tip_lbl_birthday, modifyBirthdayTimes + ""));
    tvSexTip.setText(getString(R.string.format_tip_lbl_sex, modifySexTimes + ""));
    tvCityTip.setText(getString(R.string.format_tip_lbl_city, modifyCityTimes + ""));

    UserInfo userInfo = model.getUser_info();

    etWeixin.setText(userInfo.getWechart_id());

    edtName.setText(userInfo.getUser_name());
    edtSign.setText(userInfo.getSelf_description());
    tvBirthday.setText(userInfo.getBirthday());

    switchSex(userInfo.getSys_sex_id());

    tvCity.setText(userInfo.getAddress());
  }

  private void switchSex(String sexId) {
    switch (sexId) {
      case SexConstant.SEX_MAN:
        tvSex.setText(getString(R.string.lbl_man));
        break;
      case SexConstant.SEX_WOMEN:
        tvSex.setText(getString(R.string.lbl_feman));
        break;
    }
    this.sexId = sexId;
  }

  public void updateUserInfo(UserInfoModel model) {
    if(model != null) {
      String userImgUrl;
      if (model.getUser_info().getUser_img().startsWith("http")) {
        userImgUrl = model.getUser_info().getUser_img();
      } else {
        userImgUrl = Constant.getBaseUrl() + "/" + model.getUser_info().getUser_img();
      }
      Picasso.with(this)
          .load(userImgUrl)
          .transform(new CircleTransform(this))
          .centerCrop()
          .resize(480, 480)
          .into(img_info_avatar);
    }
  }

  public ArrayList<JsonBean2> parseData(String result) {
    ArrayList<JsonBean2> detail = new ArrayList<>();
    try {
      JSONArray data = new JSONArray(result);
      Gson gson = new Gson();
      for (int i = 0; i < data.length(); i++) {
        JsonBean2 entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean2.class);
        detail.add(entity);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return detail;
  }

  private void initJsonData() {
    //获取assets目录下的json文件数据
    //这里因该是开启线程处理，或者用RXJAVA
    Observable.create(new ObservableOnSubscribe<String>() {
      @Override
      public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        //读取assets数据
        String JsonData = new GetJsonDataUtil().getJson(getApplication(), "region.json");
        emitter.onNext(JsonData);
        emitter.onComplete();
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
          String jsonData = "";

          @Override
          public void onSubscribe(@NonNull Disposable d) {

          }

          @Override
          public void onNext(@NonNull String str) {
            jsonData = str;
          }

          @Override
          public void onError(@NonNull Throwable e) {

          }

          @Override
          public void onComplete() {
            //用Gson 转成实体
            ArrayList<JsonBean2> jsonBean = parseData(jsonData);
            AppLogger.d(jsonBean.toString());


            options1CityItems = jsonBean;
            for (int i = 0; i < jsonBean.size(); i++) {
              ArrayList<String> cityList = new ArrayList<>();
              ArrayList<String> cityListCode = new ArrayList<>();

              ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();

              ArrayList<ArrayList<String>> province_AreaListCode = new ArrayList<>();

              for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {

                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);

                String cityCode = jsonBean.get(i).getCityList().get(c).getCode();
                cityListCode.add(cityCode);

                ArrayList<String> city_AreaList = new ArrayList<>();
                //自定义地址
                for(JsonBean2.ChildBean childBean:jsonBean.get(i).getCityList().get(c).getArea()){
                  city_AreaList.add(childBean.getName());
                }
                province_AreaList.add(city_AreaList);

                ArrayList<String> city_AreaListCode = new ArrayList<>();
                //编号
                for(JsonBean2.ChildBean childBean:jsonBean.get(i).getCityList().get(c).getArea()){
                  city_AreaListCode.add(childBean.getCode());
                }
                province_AreaListCode.add(city_AreaListCode);
              }


              options2CityItems.add(cityList);
              options3CityItems.add(province_AreaList);
              //编号
              options2CityItemsCode.add(cityListCode);
              options3CityItemsCode.add(province_AreaListCode);
            }

          }
        });
  }


  TimePickerView pvCustomLunar;

  private String getDateString(Date date) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    return format.format(date);
  }

  private void showDateDialog() {
    pvCustomLunar.show();
  }

  private void showSexDialog() {
    List<SexOption> sexOptions = new ArrayList<>();
    sexOptions.add(new SexOption(SexConstant.SEX_MAN, getString(R.string.lbl_man)));
    sexOptions.add(new SexOption(SexConstant.SEX_WOMEN, getString(R.string.lbl_feman)));
    OptionsPickerView<SexOption> pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
      switchSex(sexOptions.get(options1).getCode());
    }).build();
    pvOptions.setPicker(sexOptions, null, null);
    pvOptions.show();
  }

  /**
   * 显示身高选择弹窗
   */
  private void showHeightDialog() {
    List<PickerOption> options = new ArrayList<>();
    for (int i = 80; i < 250; i++) {
      options.add(new PickerOption(i + "cm", i));
    }
    OptionsPickerView<PickerOption> pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
      selectUserHeight(options.get(options1).getValue());
    }).build();
    pvOptions.setPicker(options, null, null);
    pvOptions.show();
  }

  private void selectUserHeight(int userHeight) {
    this.userHeight = userHeight;
    tvHeight.setText(userHeight + "cm");
  }

  /**
   * 显示体重选择弹窗
   */
  private void showWeightDialog() {
    List<PickerOption> options = new ArrayList<>();
    for (int i = 30; i < 200; i++) {
      options.add(new PickerOption(i + "cm", i));
    }
    OptionsPickerView<PickerOption> pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
      selectUserHeight(options.get(options1).getValue());
    }).build();
    pvOptions.setPicker(options, null, null);
    pvOptions.show();
  }

  private void selectUserWeight(int userWeight) {
    this.userWeight = userWeight;
    tvWeight.setText(userWeight + "kg");
  }

  /**
   * 农历时间已扩展至 ： 1900 - 2100年
   */
  private void initLunarPicker() {
    Calendar selectedDate = Calendar.getInstance();
    Calendar startDate = Calendar.getInstance();
    startDate.set(1900, 1, 1);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2069, 1, 1);
    //时间选择器 ，自定义布局
    pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
      @Override
      public void onTimeSelect(Date startDate, Date endDate, boolean isFullMonth, View v) {
        tvBirthday.setText(getDateString(startDate));
      }
    })
        .setType(new boolean[]{true, true, true, false, false, false})
        .isCenterLabel(false)
        .setDividerColor(Color.TRANSPARENT)
        .setLineSpacingMultiplier(1.8f)
        .setTitleBgColor(Color.WHITE)
        .setTextColorCenter(Color.WHITE)
        .setDate(selectedDate)
        .setRangDate(startDate, endDate)
        .setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {

          @Override
          public void customLayout(final View v) {
            final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
            TextView tv_cancel = (TextView) v.findViewById(R.id.tv_cancel);
            tvSubmit.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                pvCustomLunar.returnData();
                pvCustomLunar.dismiss();
              }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                pvCustomLunar.dismiss();
              }
            });
          }
        }).build();
  }

  private void showCityPickerView() {
    OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
      @Override
      public void onOptionsSelect(int options1, int options2, int options3, View v) {
        //返回的分别是三个级别的选中位置
        String opt1tx = options1CityItems.size() > 0 ? options1CityItems.get(options1).getPickerViewText() : "";
        String opt1Code=options1CityItems.size() > 0 ? options1CityItems.get(options1).getCode() : "";

        String opt2tx = options2CityItems.size() > 0 && options2CityItems.get(options1).size() > 0 ? options2CityItems.get(options1).get(options2) : "";

        String opt2Code=options2CityItemsCode.size() > 0 && options2CityItemsCode.get(options1).size() > 0 ? options2CityItemsCode.get(options1).get(options2) : "";

        String opt3tx = options2CityItems.size() > 0 && options3CityItems.get(options1).size() > 0 && options3CityItems.get(options1).get(options2).size() > 0 ? options3CityItems.get(options1).get(options2).get(options3) : "";

        String opt3txCode = options2CityItemsCode.size() > 0 && options3CityItemsCode.get(options1).size() > 0 && options3CityItemsCode.get(options1).get(options2).size() > 0 ? options3CityItemsCode.get(options1).get(options2).get(options3) : "";

        String tx = opt1tx + opt2tx + opt3tx;

        //设置文本+json数据
        //json地址，包含city-code  "[{"name":"湖北省","code":"420000"},{"name":"武汉市","code":"420100"},{"name":"洪山区","code":"420111"}]"
        try {
          JSONObject opt1 = new JSONObject();
          opt1.put("name",opt1tx);
          opt1.put("code",opt1Code);

          JSONObject opt2 = new JSONObject();
          opt2.put("name",opt2tx);
          opt2.put("code",opt2Code);

          JSONObject opt3 = new JSONObject();
          opt3.put("name",opt3tx);
          opt3.put("code",opt3txCode);

          JSONArray array = new JSONArray();
          array.put(opt1);
          array.put(opt2);
          array.put(opt3);

          tvCity.setTag(array.toString());
          AppLogger.d(array.toString());
        }catch (JSONException ex){
          ex.printStackTrace();
        }
        tvCity.setText(tx);
      }
    })
        .setTitleText(getResources().getString(R.string.lbl_select_city))
        .setTitleColor(Color.WHITE)
        .setContentTextSize(20)
        .setCancelColor(getResources().getColor(R.color.dialog_cancel_color))
        .setSubmitColor(getResources().getColor(R.color.dialog_submit_color))
        .setBgColor(getResources().getColor(R.color.dialog_bg_color))
        .setDividerColor(Color.TRANSPARENT)
        .setLineSpacingMultiplier(1.8f)
        .setTitleBgColor(getResources().getColor(R.color.dialog_bg_color))
        .setTextColorCenter(Color.WHITE)
        .build();
    pvOptions.setPicker(options1CityItems, options2CityItems, options3CityItems);
    pvOptions.show();
  }

  public void onViewClicked(View v) {
    if (v.getId() == R.id.tvBirthday) {
      if(modifyBirthdayTimes <= 0) {
        Toast.makeText(getApplicationContext(),R.string.lbl_nodify_3_times,Toast.LENGTH_SHORT).show();
        return;
      }
      showDateDialog();
    } else if (v.getId() == R.id.tvSex) {
      if (modifySexTimes <= 0) {
        Toast.makeText(getApplicationContext(),R.string.lbl_modify_over_times,Toast.LENGTH_SHORT).show();
        return;
      }
      showSexDialog();
    } else if (v.getId() == R.id.tvCity) {
      if(modifyCityTimes <= 0) {
        Toast.makeText(getApplicationContext(),R.string.lbl_nodify_3_times,Toast.LENGTH_SHORT).show();
        return;
      }
      ARouter.getInstance().build("/city/CityActivity").withBoolean("area",true).navigation(this,REQUEST_CODE);
    } else if(v.getId() == R.id.img_info_avatar){
      //弹框打开拍照
      PhotoDialog.show(this, new PhotoDialog.takePhotoListener() {
        @Override
        public void takePhoto() {
          checkPermissionAndCamera();
        }
      }, new PhotoDialog.albumListener() {
        @Override
        public void takeAlbum() {
          openAlbum();
        }
      }, new PhotoDialog.bigImageListener() {
        @Override
        public void takeBitImage() {
          if(AppDataManager.getInstance().getUserInfoModel()!=null){
            String url=AppDataManager.getInstance().getUserInfoModel().getUser_info().getUser_img();
            startImageActivity(url);
          }
        }
      });
    } else if(v.getId() == R.id.tvHeight) {
      showHeightDialog();
    } else if(v.getId() == R.id.tvWeight) {

    } else if (v.getId() == R.id.tvSubmit) {
      //保存，这里需要传递个人信息数据
      commit();
    }
  }


  /**
   * 用于保存拍照图片的uri
   */
  private Uri mCameraUri;

  /**
   * 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
   */
  //private String mCameraImagePath;

  /**
   * 是否是Android 10以上手机
   */
  private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

  /**
   * 处理权限申请的回调。
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //允许权限，有调起相机拍照。
        openCamera();
      } else {
        //拒绝权限，弹出提示框。
        Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_LONG).show();
      }
    } else if (requestCode == ALBUM_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //允许权限,打开相册
        openAlbum();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CAMERA_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Picasso.with(this)
            .load(mCameraUri).transform(new CircleTransform2(this,480,480))
            .into(img_info_avatar);
        AppLogger.d("----拍照返回数据----"+mCameraUri);
        //跳转到裁剪页面
        gotoClipActivity2(mCameraUri);
      } else {
        Toast.makeText(this, "取消", Toast.LENGTH_LONG).show();
      }
    }else if (requestCode == ALBUM_CODE && resultCode == Activity.RESULT_OK) {
      Uri sourceUri = data.getData();
      gotoClipActivity2(sourceUri);
    }else if(requestCode == REQUEST_CODE && resultCode == REQUEST_CODE){
      String city = data.getStringExtra("city");
      tvCity.setText(city);
      tvCity.setTag("");
    }
  }

  /**
   * 检查权限并拍照。
   * 调用相机前先检查权限。
   */
  private void checkPermissionAndCamera() {
    int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),Manifest.permission.CAMERA);
    if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
      //有调起相机拍照。
      openCamera();
    } else {
      //没有权限，申请权限。
      ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_CODE);
    }
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

  public File getFileFromUri(Uri uri, Context context) {
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
   * 文件封装
   * @param file
   */
  private void updateFile(File file) {
    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    Observable<UserImageModel> observable = apiServer.uploadImage(filePart);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserImageModel>() {
          @Override
          public void onSuccess(UserImageModel result) {
            try {
              //AppLogger.d("---updateFile 上传视频-成功-"+result.getUser_img_path());
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
            Logger.d("----updateFile---"+code+";msg="+msg);
          }
        })
    );
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

  /**
   * 调起相机拍照
   */
  private void openCamera() {
    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // 判断是否有相机
    if (captureIntent.resolveActivity(getPackageManager()) != null) {
      File photoFile = null;
      Uri photoUri = null;
      if (isAndroidQ) {
        // 适配android 10
        photoUri = createImageUri();
      } else {
        try {
          photoFile = createImageFile();
        } catch (IOException e) {
          e.printStackTrace();
        }

        if (photoFile != null) {
          //mCameraImagePath = photoFile.getAbsolutePath();
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
          } else {
            photoUri = Uri.fromFile(photoFile);
          }
        }
      }

      mCameraUri = photoUri;
      if (photoUri != null) {
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
      }
    }
  }


  /**
   * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
   */
  private Uri createImageUri() {
    String status = Environment.getExternalStorageState();
    // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
    if (status.equals(Environment.MEDIA_MOUNTED)) {
      return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
    } else {
      return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
    }
  }

  /**
   * 创建保存图片的文件
   */
  private File createImageFile() throws IOException {
    String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    File storageDir = getExternalFilesDir(DIRECTORY_PICTURES);
    if (!storageDir.exists()) {
      storageDir.mkdir();
    }
    File tempFile = new File(storageDir, imageName);
    if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
      return null;
    }
    return tempFile;
  }


  private void startImageActivity(String url){
    Intent intent = new Intent(this, BigImageActivity.class);
    intent.putExtra("url",url);
    startActivity(intent);
    overridePendingTransition(0, 0);
  }

  /**
   * 签名最高18个字，名称6个字
   */
  protected void commit() {
    if (!TextUtils.isEmpty(edtName.getText().toString().trim()) && !TextUtils.isEmpty(edtSign.getText().toString().trim())
        && !TextUtils.isEmpty(tvCity.getText().toString().trim()) && !TextUtils.isEmpty(tvSex.getText().toString().trim())) {
      //提交到服务器
      String nickname = edtName.getText().toString().trim();
      String weixin = etWeixin.getText().toString().trim();
      String sign = edtSign.getText().toString().trim();
      String birthday = tvBirthday.getText().toString().trim();
      String address = tvCity.getText().toString().trim();
      String address_json = (String) tvCity.getTag();
      if(FileUtils.getWordCount(nickname) > 6 * 2) {
        Toast.makeText(getApplication(), R.string.please_fill_nick_short, Toast.LENGTH_LONG).show();
      }else if(FileUtils.getWordCount(sign) > 18 * 4) {
        Toast.makeText(getApplicationContext(), R.string.please_fill_sign_short, Toast.LENGTH_LONG).show();
      }else{
        if(TextUtils.isEmpty(tvBirthday.getText().toString().trim())) {
          Toast.makeText(getApplication(), R.string.please_fill_nick_or_sign2, Toast.LENGTH_LONG).show();
          return;
        }
        //2021-07-12
        notifyUserInfo(nickname, weixin, sign, address, address_json, birthday, sexId);
      }
    } else {
      Toast.makeText(getApplication(), R.string.please_fill_nick_or_sign2, Toast.LENGTH_LONG).show();
    }
  }


  private void notifyUserInfo(String nickname, String weixin, String sign, String address, String address_json, String birthday, String sexId) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_name", nickname);
    map.put("wechart_id", weixin);
    map.put("self_description", sign);
    map.put("address", address);
    map.put("address_json", address_json);
    map.put("birthday", birthday);
    map.put("is_group", 0);
    map.put("sys_sex_id", sexId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<ResponseBody> observable = apiServer.modifyUserInfo(requestBody);
    disposable.add(
      observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(new WristBallObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody o){
              try{
                Logger.d("----修改个人信息成功----"+o.string());
                UserInfoModel model=AppDataManager.getInstance().getUserInfoModel();
                if(model!=null && model.getUser_info()!=null){
                  AppDataManager.getInstance().getUserInfoModel().getUser_info().setAddress(address);
                  AppDataManager.getInstance().getUserInfoModel().getUser_info().setBirthday(birthday);
                  AppDataManager.getInstance().getUserInfoModel().getUser_info().setUser_name(nickname);
                  AppDataManager.getInstance().getUserInfoModel().getUser_info().setSelf_description(sign);
                  AppDataManager.getInstance().getUserInfoModel().getUser_info().setWechart_id(weixin);
                  AppDataManager.getInstance().setUserInfoModel(model);
                }
                Toast.makeText(getApplicationContext(),R.string.nodifySuccess,Toast.LENGTH_LONG).show();
              }catch (Exception ex){
                ex.printStackTrace();
              }
              setResult(REPORT_OK);
              finish();
            }

            @Override
            public void onError(int code, String msg) {
              Logger.d(msg);
            }
          })
    );
  }

  public static final int REPORT_OK=99;


  /**
   * 打开截图的界面
   * @param uri
   */
  private void gotoClipActivity(Uri uri){
    if(uri == null){
      return;
    }
    Intent intent = new Intent(this, ClipImageActivity.class);
    intent.putExtra("type",1);
    intent.setData(uri);
    startActivityLaunch.launch(intent);
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
  public File getCropFile(Context context,Uri uri){
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
    if(result.getResultCode()==RESULT_OK){
      if(file !=null){
        Picasso.with(this).load(file).transform(new CircleTransform2(this,480,480)).into(img_info_avatar);
        //上传头像
        try {
          //上传头像
          if (Build.VERSION.SDK_INT >= 30) {
            if (uri != null) {
              file =getCropFile(this, uri);
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
    }else if(result.getResultCode()==RESULT_CANCELED){
      AppLogger.d("---------裁剪头像返回---RESULT_CANCELED-------");
    }
  });


  ActivityResultLauncher<Intent> startActivityLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    int resultCode=result.getResultCode();
    if(resultCode==ClipImageActivity.REQUEST_CROP_PHOTO){
      AppLogger.d("---------裁剪头像返回----------");
      final Uri uri = result.getData().getData();
      if (uri == null) {
        return;
      }
      Picasso.with(this).load(uri).transform(new CircleTransform2(this,480,480)).into(img_info_avatar);
      //上传头像
      try {
        File imgFile=getFileFromUri(uri,this);
        if (imgFile.exists()) {
          updateFile(imgFile);
        }
      }catch (Exception ex){
        ex.printStackTrace();
      }
    }
  });


  private String getImagePath(Uri uri, String selection) {
    String path = null;
    Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
      }
      cursor.close();
    }
    return path;
  }
}
