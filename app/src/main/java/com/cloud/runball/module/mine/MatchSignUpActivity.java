package com.cloud.runball.module.mine;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.bean.JsonBean;
import com.cloud.runball.utils.GetJsonDataUtil;
import com.cloud.runball.dialog.SuccessfulDialog;
import com.cloud.runball.widget.WeightPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cloud.runball.databinding.ActivityMatchSignUpBinding;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: MatchSignUpActivity
 * @Description: 报名信息   https://github.com/Bigkoo/Android-PickerView
 * @Author: zhd
 * @CreateDate: 2021/2/3 16:04
 * @UpdateUser: zhd
 * @UpdateDate: 2021/2/3 16:04
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MatchSignUpActivity extends BaseActivity {

    private ActivityMatchSignUpBinding binding;
    Button btnSignUp;

    EditText edtPhone;

    EditText edtBirthday;

    EditText edtHeight;

    EditText edtWeight;

    EditText edtCity;


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_match_sign_up;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMatchSignUpBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        btnSignUp = binding.btnSignUp;
        edtPhone = binding.edtPhone;
        edtBirthday = binding.edtBirthday;
        edtHeight = binding.edtHeight;
        edtWeight = binding.edtWeight;
        edtCity = binding.edtCity;

        initJsonData();
        initHeight();
        initLunarPicker();
        initHeightPicker();
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_sign_up);
    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
    }

    public void onViewClicked(View v) {
        if (v.getId() == R.id.edtBirthday) {
            showDateDialog();
        } else if (v.getId() == R.id.edtHeight) {
            showHeightDialog();
        } else if (v.getId() == R.id.edtCity) {
            showCityPickerView();
        } else if (v.getId() == R.id.edtWeight) {
            showWeightDialog();
        }else if (v.getId() == R.id.btnSignUp) {
           if(!SuccessfulDialog.isShowing()){
               SuccessfulDialog.show(MatchSignUpActivity.this,new SuccessfulDialog.DismissCallBack(){
                   @Override
                   public void dismiss(int homepage) {

                   }
               });
           }
        }
    }

    @Override
    protected void addListener() {
        // wire previous @OnClick targets
        if (binding != null) {
            binding.btnSignUp.setOnClickListener(this::onViewClicked);
            binding.edtBirthday.setOnClickListener(this::onViewClicked);
            binding.edtHeight.setOnClickListener(this::onViewClicked);
            binding.edtCity.setOnClickListener(this::onViewClicked);
            binding.edtWeight.setOnClickListener(this::onViewClicked);
        }
    }

    private String getDateString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }


    TimePickerView pvCustomLunar;
    OptionsPickerView pvOptions;

    private void showDateDialog() {
        pvCustomLunar.show();
    }

    /**
     * 农历时间已扩展至 ： 1900 - 2100年
     */
    private void initLunarPicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2069, 1, 1);
        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
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


    private List<JsonBean> options1CityItems = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2CityItems = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3CityItems = new ArrayList<>();

    private ArrayList<String> options1HeightItems = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2HeightItems = new ArrayList<>();

    private void initHeight() {
        //选项1
        options1HeightItems.clear();
        options1HeightItems.clear();
        for (int h = 120; h < 220; h++) {
            options1HeightItems.add(String.valueOf(h));
        }

        ArrayList<String> optionsSub = new ArrayList<>();
        for (int subh = 0; subh < 10; subh++) {
            optionsSub.add(String.valueOf(subh));
        }

        for (int i = 0; i < options1HeightItems.size(); i++) {
            options2HeightItems.add(optionsSub);
        }
    }

    /**
     * 身高选择
     */
    private void initHeightPicker() {

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String str = options1HeightItems.get(options1) + "." + options2HeightItems.get(options1).get(options2);
                edtHeight.setText(str);
            }
        })
                .setContentTextSize(20)
                .setSelectOptions(30, 1)
                .setCancelColor(getResources().getColor(R.color.dialog_cancel_color))
                .setSubmitColor(getResources().getColor(R.color.dialog_submit_color))
                .setBgColor(getResources().getColor(R.color.dialog_bg_color))
                .setDividerColor(Color.TRANSPARENT)
                .setLineSpacingMultiplier(1.8f)
                .setTitleBgColor(getResources().getColor(R.color.dialog_bg_color))
                .setTextColorCenter(Color.WHITE)

                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setOutSideColor(0x00000000) //设置外部遮罩颜色)
                .build();
        pvOptions.setPicker(options1HeightItems, options2HeightItems);
    }

    private void showHeightDialog() {
        pvOptions.show();
    }


    public ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
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
                String JsonData = new GetJsonDataUtil().getJson(getApplication(), "province.json");
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
                        ArrayList<JsonBean> jsonBean = parseData(jsonData);
                        options1CityItems = jsonBean;
                        for (int i = 0; i < jsonBean.size(); i++) {
                            ArrayList<String> cityList = new ArrayList<>();
                            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();

                            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {
                                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                                cityList.add(cityName);
                                ArrayList<String> city_AreaList = new ArrayList<>();
                                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                                province_AreaList.add(city_AreaList);
                            }
                            options2CityItems.add(cityList);
                            options3CityItems.add(province_AreaList);
                        }
                    }
                });
    }

    private void showCityPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1CityItems.size() > 0 ?
                        options1CityItems.get(options1).getPickerViewText() : "";

                String opt2tx = options2CityItems.size() > 0
                        && options2CityItems.get(options1).size() > 0 ?
                        options2CityItems.get(options1).get(options2) : "";

                String opt3tx = options2CityItems.size() > 0
                        && options3CityItems.get(options1).size() > 0
                        && options3CityItems.get(options1).get(options2).size() > 0 ?
                        options3CityItems.get(options1).get(options2).get(options3) : "";

                String tx = opt1tx + opt2tx + opt3tx;
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

    private void showWeightDialog(){

        WeightPickerView pickerView=new WeightPickerView(this,R.style.DialogTheme);
        pickerView.setFinishListener(new  WeightPickerView.onFinishListener() {
            @Override
            public void onFinish(String content) {
                edtWeight.setText(content);
            }
        });
        pickerView.showDialog();
    }


}
