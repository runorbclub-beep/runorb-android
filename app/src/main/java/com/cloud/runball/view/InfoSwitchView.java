package com.cloud.runball.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.FileUtils;
import com.cloud.runball.bean.JsonBean2;
import com.cloud.runball.bean.SexOption;
import com.cloud.runball.constant.SexConstant;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.clan.dialog.ClanNoticeDialog;
import com.cloud.runball.module.clan.dialog.EditClanDialog;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.utils.GetJsonDataUtil;
import com.cloud.runball.widget.CircleTransform;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.view
 * @ClassName: InfoSwitchView
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/2 17:38
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/2 17:38
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class InfoSwitchView extends RelativeLayout implements View.OnClickListener, View.OnTouchListener, CompoundButton.OnCheckedChangeListener {

  //个人,青年
  int is_group = 0;
  int is_yang = 1;

  private List<JsonBean2> options1CityItems = new ArrayList<>();
  private final ArrayList<ArrayList<String>> options2CityItems = new ArrayList<>();
  private final ArrayList<ArrayList<ArrayList<String>>> options3CityItems = new ArrayList<>();


  private final ArrayList<ArrayList<String>> options2CityItemsCode = new ArrayList<>();
  private final ArrayList<ArrayList<ArrayList<String>>> options3CityItemsCode = new ArrayList<>();

  ImageView ivClose;

  ImageView tvPortrait;
  TextView tvName;
  EditText edtName;
  EditText edtWeixin;

  TextView tvBirthday;
  EditText edtSign;
  EditText edtBirthday;
  EditText edtSex;
  TextView tvCity;
  EditText edtCity;
  EditText edtClan;
  TextView btnConfirm;

  TextView tvGroupType;
  RadioGroup rgGroupType;

  RadioButton rbTeen;
  RadioButton rbAdult;

  private boolean isTargetDialog=false;
  OnSoftKeyboardListener onSoftKeyboardListener;
  OnDismissClickListener mlistener;
  OnNavigationClickListener navigationClickListener;

  private OnClanAreaClickListener onClanAreaClickListener;
  private OnClanAlbumClickListener onClanAlbumClickListener;

  private OnAlbumClickListener onAlbumClickListener;

  int nodify_birthday_times = 3;
  int nodify_city_times = 3;
  int modifySexTimes = 3;

  String curSex;

  private CompositeDisposable disposable = new CompositeDisposable();

  private EditClanDialog editClanDialog;


  public InfoSwitchView(Context context) {
    super(context);
    init();
  }

  public InfoSwitchView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public void setTargetDialog(boolean targetDialog){
    this.isTargetDialog=targetDialog;
  }

  public void setOnSoftKeyboardListener(OnSoftKeyboardListener listener) {
    this.onSoftKeyboardListener = listener;
  }

  public void setOnDismissClickListener(OnDismissClickListener listener){
    this.mlistener=listener;
  }

  public void setOnNavigationClickListener(OnNavigationClickListener listener){
    this.navigationClickListener=listener;
  }

  public void setOnClanAreaClickListener(OnClanAreaClickListener listener) {
    this.onClanAreaClickListener = listener;
  }

  public void setOnClanAlbumClickListener(OnClanAlbumClickListener listener) {
    this.onClanAlbumClickListener = listener;
  }

  public void setOnAlbumClickListener(OnAlbumClickListener listener) {
    this.onAlbumClickListener = listener;
  }

  public void disposableNet() {
    if (disposable != null) {
      disposable.dispose();
    }
  }


  private void init(){
    View view = View.inflate(getContext(), R.layout.layout_info, null);
    ivClose = view.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(this);

    tvPortrait = view.findViewById(R.id.tvPortrait);
    tvPortrait.setOnClickListener(this);

    tvName = view.findViewById(R.id.tvName);
    edtName = view.findViewById(R.id.edtName);

    edtWeixin = view.findViewById(R.id.edtWeixin);

    tvBirthday = view.findViewById(R.id.tvBirthday);
    edtSign = view.findViewById(R.id.edtSign);
    edtBirthday = view.findViewById(R.id.edtBirthday);
    edtBirthday.setOnClickListener(this);

    edtSex = view.findViewById(R.id.edtSex);
    edtSex.setOnClickListener(this);
    edtSex.setText(getContext().getString(R.string.lbl_man));
    curSex = SexConstant.SEX_MAN;

    tvCity = view.findViewById(R.id.tvCity);
    edtCity = view.findViewById(R.id.edtCity);
    edtCity.setOnClickListener(this);

    edtClan = view.findViewById(R.id.edtClan);
    edtClan.setOnClickListener(this);

    btnConfirm = view.findViewById(R.id.btnConfirm);
    btnConfirm.setOnClickListener(this);


    tvGroupType = view.findViewById(R.id.tvGroupType);
    rgGroupType = view.findViewById(R.id.rgGroupType);

    rbTeen = view.findViewById(R.id.rbTeen);
    rbAdult = view.findViewById(R.id.rbAdult);

    rbTeen.setOnCheckedChangeListener(this);
    rbAdult.setOnCheckedChangeListener(this);


    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

    this.setOnTouchListener(this);
    this.addView(view,lp);
    initLunarPicker();
    initJsonData();

    UserInfoModel model = AppDataManager.getInstance().getUserInfoModel();
    if(model != null && model.getUser_info() != null) {
      nodify_birthday_times = model.getUser_info().getUser_birthday_change();
      nodify_city_times = model.getUser_info().getUser_city_change();
      modifySexTimes = model.getUser_info().getSys_sex_id_change();
    }
  }


  @Override
  public void onClick(View v) {
    if(v.getId() == R.id.ivClose) {
      removeThis();
    } else if(v.getId() == R.id.btnConfirm) {
      //提交
      commit();
    } else if (v.getId() == R.id.edtBirthday) {
      if(nodify_birthday_times <= 0) {
        Toast.makeText(getContext(), R.string.lbl_nodify_3_times, Toast.LENGTH_SHORT).show();
        return;
      }
      showDateDialog();
    } else if (v.getId() == R.id.edtSex) {
      if (modifySexTimes <= 0) {
        Toast.makeText(getContext(), R.string.lbl_modify_over_times, Toast.LENGTH_SHORT).show();
        return;
      }
      showSexDialog();
    } else if (v.getId() == R.id.edtCity) {
      if(nodify_city_times <= 0) {
        Toast.makeText(getContext(),R.string.lbl_nodify_3_times,Toast.LENGTH_SHORT).show();
        return;
      }
      if(navigationClickListener != null) {
        navigationClickListener.navigation();
      }
    } else if (v.getId() == R.id.edtClan) {
//      showEditClanDialog();
    } else if (v.getId() == R.id.tvPortrait) {
      if (onAlbumClickListener != null) {
        onAlbumClickListener.onClick();
      }
    }
  }

  private void showEditClanDialog() {
    ClanNoticeDialog noticeDialog = new ClanNoticeDialog(this.getContext());
    noticeDialog.setOnCreateClanCallback(dialog -> {
      dialog.dismiss();
      editClanDialog = new EditClanDialog(this.getContext());
      editClanDialog.setDate(false);
      editClanDialog.setCallback(new EditClanDialog.Callback() {
        @Override
        public void onPortraitClick() {
          if (onClanAlbumClickListener != null) {
            onClanAlbumClickListener.onClick();
          }
        }
        @Override
        public void onAreaClick(String area) {
          if (onClanAreaClickListener != null) {
            onClanAreaClickListener.onClick(area);
          }
        }
        @Override
        public void onSubmit(Dialog dialog, boolean isEdit, String clanName, String clanAvatar, String address, String introduction, String telephone, String remark) {
//          registerClan(dialog, isEdit, clanName, clanAvatar, address, introduction, telephone, remark);
        }
      });
    });
  }

  public void setPortrait(String portraitNetUrl, String portraitUrl) {
    String netUrl = portraitNetUrl;
    if (!netUrl.startsWith("http")) {
      netUrl = Constant.getBaseUrl() + "/" + netUrl;
    }
    Picasso.with(this.getContext())
        .load(netUrl)
        .transform(new CircleTransform(this.getContext()))
        .into(tvPortrait);
    tvPortrait.setTag(portraitUrl);
  }

  private void switchSex(String sexId) {
    switch (sexId) {
      case SexConstant.SEX_MAN:
        edtSex.setText(getContext().getString(R.string.lbl_man));
        break;
      case SexConstant.SEX_WOMEN:
        edtSex.setText(getContext().getString(R.string.lbl_feman));
        break;
    }
    curSex = sexId;
  }

  private void showSexDialog() {
    List<SexOption> sexOptions = new ArrayList<>();
    sexOptions.add(new SexOption(
        SexConstant.SEX_MAN,
        getContext().getString(R.string.lbl_man)
    ));
    sexOptions.add(new SexOption(
        SexConstant.SEX_WOMEN,
        getContext().getString(R.string.lbl_feman)
    ));

    OptionsPickerView<SexOption> pvOptions = new OptionsPickerBuilder(InfoSwitchView.this.getContext(), (options1, option2, options3, v) -> {
      switchSex(sexOptions.get(options1).getCode());
    }).build();
    pvOptions.setPicker(sexOptions, null, null);
    pvOptions.show();
  }

  private void complete() {
    if(isTargetDialog && mlistener != null) {
      mlistener.onComplete();
    } else {
      if(getParent() != null) {
        ((ViewGroup)getParent()).removeView(this);
      }
    }
  }

  private void removeThis() {
    if(isTargetDialog && mlistener != null) {
      mlistener.onDismiss();
    } else {
      if(getParent() != null) {
        ((ViewGroup)getParent()).removeView(this);
      }
    }
  }

  public void setCity(String city) {
    edtCity.setText(city);
    edtCity.setTag("");
  }

  public void setClanCity(String city) {
    if (editClanDialog != null) {
      editClanDialog.setArea(city);
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    return true;
  }

  /**
   * 签名最高18个字，名称6个字
   */
  protected void commit() {
    if (!TextUtils.isEmpty(edtName.getText().toString().trim()) &&
        !TextUtils.isEmpty(edtCity.getText().toString().trim()) &&
        !TextUtils.isEmpty(edtSex.getText().toString().trim())) {
      //提交到服务器
//      String portrait = tvPortrait.getTag() == null ? null : tvPortrait.getTag().toString();
      String nickname = edtName.getText().toString().trim();
      String weixin = edtWeixin.getText().toString().trim();
      String address = edtCity.getText().toString().trim();
      String address_json = (String) edtCity.getTag();
      if(FileUtils.getWordCount(nickname) > 6 * 2) {
        Toast.makeText(getContext(), R.string.please_fill_nick_short, Toast.LENGTH_LONG).show();
      }else{
        if(is_group == 0) {
          if(TextUtils.isEmpty(edtBirthday.getText().toString().trim())) {
            Toast.makeText(getContext(), R.string.please_fill_nick_or_sign2, Toast.LENGTH_LONG).show();
            return;
          }
        }
        String birthday=edtBirthday.getText().toString().trim();
        String sign=edtSign.getText().toString().trim();
        if(FileUtils.getWordCount(sign)>18*4){
          Toast.makeText(getContext(), R.string.please_fill_sign_short, Toast.LENGTH_LONG).show();
          return;
        }
        //提交个人或者团队信息
        notifyUserInfo(nickname, weixin, sign, address, address_json, birthday, is_group, is_yang, curSex);
      }
    } else {
      Toast.makeText(getContext(), R.string.please_fill_nick_or_sign2, Toast.LENGTH_LONG).show();
    }
  }


  private void notifyUserInfo(String nickname, String weixin, String sign,String address,String address_json,String birthday,int is_group,int user_age_type, String sexId){
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("user_name", nickname);
    map.put("wechart_id", weixin);
    map.put("self_description", sign);
    map.put("address", address);
    map.put("address_json", address_json);
    map.put("birthday", birthday);
    map.put("is_group", is_group);
    if(is_group==1){
      map.put("user_age_type", user_age_type);
    }
    map.put("sys_sex_id", sexId);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());

    Observable<ResponseBody> observable = apiServer.modifyUserInfo(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
          @Override
          public void onSuccess(ResponseBody o){
            try{
              Logger.d("----修改个人信息成功----"+o.string());
              UserInfoModel model= AppDataManager.getInstance().getUserInfoModel();
              if(model!=null && model.getUser_info()!=null){
                AppDataManager.getInstance().getUserInfoModel().getUser_info().setAddress(address);
                AppDataManager.getInstance().getUserInfoModel().getUser_info().setBirthday(birthday);
                AppDataManager.getInstance().getUserInfoModel().getUser_info().setUser_name(nickname);
                AppDataManager.getInstance().getUserInfoModel().getUser_info().setSelf_description(sign);

                //新增
                AppDataManager.getInstance().getUserInfoModel().getUser_info().setIs_group(is_group);
                AppDataManager.getInstance().getUserInfoModel().getUser_info().setIs_yang(user_age_type);

                AppDataManager.getInstance().setUserInfoModel(model);
              }
              Toast.makeText(getContext(),R.string.nodifySuccess,Toast.LENGTH_LONG).show();
            }catch (Exception ex){
              ex.printStackTrace();
            }
            //移除本页面
            //                removeThis();
            complete();
          }
          @Override
          public void onError(int code, String msg) {
            Logger.d(msg);
          }
        })
    );
  }


  TimePickerView pvCustomLunar;

  private void showDateDialog() {
    if (onSoftKeyboardListener != null) {
      onSoftKeyboardListener.hidden();
    }
    pvCustomLunar.show();
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
    pvCustomLunar = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
      @Override
      public void onTimeSelect(Date startDate, Date endDate, boolean isFullMonth, View v) {
        edtBirthday.setText(getDateString(startDate));
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
    OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
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
          JSONObject opt1=new JSONObject();
          opt1.put("name",opt1tx);
          opt1.put("code",opt1Code);

          JSONObject opt2=new JSONObject();
          opt2.put("name",opt2tx);
          opt2.put("code",opt2Code);

          JSONObject opt3=new JSONObject();
          opt3.put("name",opt3tx);
          opt3.put("code",opt3txCode);

          JSONArray array=new JSONArray();
          array.put(opt1);
          array.put(opt2);
          array.put(opt3);

          edtCity.setTag(array.toString());
          AppLogger.d(array.toString());
        }catch (JSONException ex){
          ex.printStackTrace();
        }
        edtCity.setText(tx);
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

  private void initJsonData() {
    //获取assets目录下的json文件数据
    //这里因该是开启线程处理，或者用RXJAVA
    Observable.create(new ObservableOnSubscribe<String>() {
      @Override
      public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        //读取assets数据
        String JsonData = new GetJsonDataUtil().getJson(getContext(), "region.json");
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

  private String getDateString(Date date) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    return format.format(date);
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

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if(buttonView.getId()==R.id.rbTeen){
      //青年组
      is_yang=1;
    }else if(buttonView.getId()==R.id.rbAdult){
      //成人组
      is_yang=0;
    }
  }

  public interface OnSoftKeyboardListener {
    void hidden();
  }

  public interface OnDismissClickListener {
    void onComplete();
    void onDismiss();
  }

  public interface OnNavigationClickListener {
    void navigation();
  }

  public interface OnAlbumClickListener {
    void onClick();
  }

  public interface OnClanAlbumClickListener {
    void onClick();
  }

  public interface OnClanAreaClickListener {
    void onClick(String area);
  }


}
