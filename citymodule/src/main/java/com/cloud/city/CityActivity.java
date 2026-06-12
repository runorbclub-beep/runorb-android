package com.cloud.city;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.cloud.city.adapter.GridListAdapter;
import com.cloud.city.adapter.InnerListener;
import com.cloud.city.adapter.OnPickListener;
import com.cloud.city.adapter.XCityListAdapter;
import com.cloud.city.adapter.decoration.DividerItemDecoration;
import com.cloud.city.adapter.decoration.GridItemDecoration;
import com.cloud.city.adapter.decoration.SectionItemDecoration;
import com.cloud.city.db.DBManager;
import com.cloud.city.model.City;
import com.cloud.city.model.HotCity;
import com.cloud.city.util.LocationCreator;
import com.cloud.city.util.ScreenUtil;
import com.cloud.city.view.XSideIndexBar;
import com.cloud.runball.basecomm.utils.ChannelUtils;
import com.cloud.runball.basecomm.utils.RxBus;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.disposables.Disposable;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.city
 * @ClassName: CityActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/9 11:23
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/9 11:23
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
@Route(path = Constants.CITY)
public class CityActivity extends AppCompatActivity implements View.OnClickListener,OnPickListener, TextWatcher,XSideIndexBar.OnIndexTouchedChangedListener, InnerListener{

  public static final int RESULT_CODE=100;

  private static final String[] REQUEST_PERMISSIONS = {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
  };

  private RecyclerView mRecyclerView;
  private RecyclerView ryHotCity;

  private TextView tvAll;

  private View mEmptyView;
  private TextView mOverlayTextView;
  private XSideIndexBar mIndexBar;
  private EditText mSearchBox;
  private ImageView ivDelete;
  private ImageView ivReturn;

  private LinearLayoutManager mLayoutManager;

  private TextView tvCurCity;

  private XCityListAdapter mAdapter;
  private GridListAdapter mHotAdapter;

  private List<City> mAllCities;
  private List<HotCity> mHotCities;
  private List<City> mResults;

  private DBManager dbManager;

  /**
   * 是否启动直辖市区选择
   */
  @Autowired(name="area")
  boolean area=false;
  private Disposable subscription = null;
  /**
   * 直辖市
   */
  String []mdutcg=new String[]{"北京","上海","天津","重庆"};

  String [][] subArea=new String[][]{
      {"密云区","延庆区","朝阳区","丰台区","石景山区","海淀区","门头沟区","房山区","房山区","通州区","顺义区","昌平区","大兴区","怀柔区","平谷区","东城区","西城区"},
      {"黄浦区","徐汇区","长宁区","静安区","普陀区","虹口区","杨浦区","浦东新区","闵行区","宝山区","嘉定区","金山区","松江区","青浦区","奉贤区","崇明区"},
      {"和平区","河东区","河西区","南开区","河北区","红桥区","滨海新区","东丽区","西青区","津南区","北辰区","武清区","宝坻区","宁河区","静海区","蓟州区"},
      {"万州区","黔江区","涪陵区","渝中区","大渡口区","江北区","沙坪坝区","九龙坡区","南岸区","北碚区","渝北区","巴南区","长寿区","江津区","合川区","永川区","南川区",
          "綦江区","大足区","璧山区","铜梁区","潼南区","荣昌区","开州区","梁平区","武隆区","城口县","丰都县","垫江县","忠县","云阳县","奉节县","巫山县","巫溪县","石柱土家族自治县",
          "秀山土家族苗族自治县","酉阳土家族苗族自治县","彭水苗族土家族自治县"}
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.city_activity_main);
    ARouter.getInstance().inject(this);
    initData();
    initViews();

    if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      registerLocationChange();
    }else{
      //提示打开定位
      launcher.launch(REQUEST_PERMISSIONS);
    }
  }

  /**
   * 展示选择器
   * 核心代码
   */
  private void showPickerView(String [][] subArea,int index) {
    List<String> tempPickers=new ArrayList<>();
    for(String area:subArea[index]){
      tempPickers.add(area);
    }

    OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
      @Override
      public void onOptionsSelect(int options1, int option2, int options3, View v) {
        tvCurCity.setText(subArea[index][options1]);
      }
    })
        .setSelectOptions(0)
        .setOutSideCancelable(false)
        .build();
    pvOptions.setPicker(tempPickers);
    pvOptions.show();
  }

  private void initData() {
    //初始化热门城市
    if (mHotCities == null || mHotCities.isEmpty()) {
      mHotCities = new ArrayList<>();
      String channelName = ChannelUtils.getChannelName(this);
      if(channelName.equalsIgnoreCase("googleplay")){
        mHotCities.add(new HotCity("United States", null, "US"));
        mHotCities.add(new HotCity("United Kingdom", null, "UK"));
        mHotCities.add(new HotCity("Russia", null, ""));
        mHotCities.add(new HotCity("Japan", null, "JP"));
        mHotCities.add(new HotCity("South Korea", null, "KR"));
        mHotCities.add(new HotCity("Australia", null, "AU"));
      } else {
        mHotCities.add(new HotCity("北京", "北京", "101010100"));
        mHotCities.add(new HotCity("上海", "上海", "101020100"));
        mHotCities.add(new HotCity("广州", "广东", "101280101"));
        mHotCities.add(new HotCity("深圳", "广东", "101280601"));
        mHotCities.add(new HotCity("天津", "天津", "101030100"));
        mHotCities.add(new HotCity("杭州", "浙江", "101210101"));
        mHotCities.add(new HotCity("南京", "江苏", "101190101"));
        mHotCities.add(new HotCity("重庆", "重庆", "101270101"));
      }
    }

    dbManager = new DBManager(this);
    String channelName = ChannelUtils.getChannelName(this);
    if(channelName.equalsIgnoreCase("googleplay")){
      mAllCities = dbManager.getAllCountries();
    } else {
      mAllCities = dbManager.getAllCities();
    }
    mResults = mAllCities;
  }


  private void initViews() {
    tvAll = findViewById(R.id.tvAll);
    tvAll.setOnClickListener(v -> {
      tvCurCity.setText(tvAll.getText());
      finishWithResult();
    });

    tvCurCity=this.findViewById(R.id.tvCurCity);
    ivReturn=this.findViewById(R.id.ivReturn);
    ivReturn.setOnClickListener(this);

    //热门城市
    mHotAdapter=new GridListAdapter(this,mHotCities);
    ryHotCity = this.findViewById(R.id.ryHotCity);
    ryHotCity.setLayoutManager(new GridLayoutManager(this, GridListAdapter.SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
    ryHotCity.setHasFixedSize(true);
    ryHotCity.setAdapter(mHotAdapter);
    mHotAdapter.setInnerListener(this);
    int space = this.getResources().getDimensionPixelSize(R.dimen.cp_grid_item_space);
    ryHotCity.addItemDecoration(new GridItemDecoration(GridListAdapter.SPAN_COUNT, space));


    //所有城市列表
    mRecyclerView = this.findViewById(R.id.cp_city_recyclerview);
    mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setHasFixedSize(true);

    mRecyclerView.addItemDecoration(new SectionItemDecoration(this, mAllCities), 0);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(this), 1);

    mAdapter = new XCityListAdapter(this, mAllCities);
    mAdapter.setInnerListener(this);
    mAdapter.setLayoutManager(mLayoutManager);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //确保定位城市能正常刷新
        if (newState == RecyclerView.SCROLL_STATE_IDLE){
          //mAdapter.refreshLocationItem();
        }
      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      }
    });

    mEmptyView = this.findViewById(R.id.cp_empty_view);
    mOverlayTextView = this.findViewById(R.id.cp_overlay);

    mIndexBar = this.findViewById(R.id.cp_side_index_bar);
    mIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(this));
    mIndexBar.setOverlayTextView(mOverlayTextView).setOnIndexChangedListener(this);

    mSearchBox = this.findViewById(R.id.cp_search_box);
    mSearchBox.addTextChangedListener(this);

    ivDelete = this.findViewById(R.id.ivDelete);
    ivDelete.setOnClickListener(this);

    initObservable();
  }

  /**
   * 监听
   */
  private void initObservable() {
    subscription= RxBus.getDefault().toObservable(Address.class).subscribe(data -> {
      if(tvCurCity!=null){
        if(data != null){
          String channelName = ChannelUtils.getChannelName(this);
          if(channelName.equalsIgnoreCase("googleplay")){
            City city = dbManager.getCountries(data.getCountryCode());
            if (city != null) {
              tvCurCity.setText(city.getName());
            }
          } else {
            tvCurCity.setText(data.getLocality());
          }
        }
      }
    });
  }

  ActivityResultLauncher<String[]> launcher = registerForActivityResult(
      new ActivityResultContracts.RequestMultiplePermissions(),
      result -> {
        for (int i = 0; i < REQUEST_PERMISSIONS.length; i++) {
          if(result.containsKey(REQUEST_PERMISSIONS[i])){
//                        registerLocationChange();
            break;
          }
        }
      });

  @Override
  public void onResume(){
    super.onResume();

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if(subscription!=null){
      subscription.dispose();
    }
    LocationCreator.self(getApplicationContext()).removeCityLocationListener();
  }

  private void registerLocationChange(){
    LocationCreator.self(getApplicationContext()).executeCNBylocation();
  }



  @Override
  public void onPick(int position, City data) {
    //currentTV.setText(String.format("当前城市：%s，%s", data.getName(), data.getCode()));
  }

  @Override
  public void onLocate() {
    //开始定位，这里模拟一下定位
    // new Handler().postDelayed(new Runnable() {
    //     @Override
    //     public void run() {
    //         //CityPicker.from(CityActivity.this).locateComplete(new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
    //     }
    //  }, 3000);
  }

  @Override
  public void onCancel() {
    Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void dismiss(int position, City data) {
    if(!isMatch(data.getName())){
      tvCurCity.setText(data.getName());
      finishWithResult();
    }
    //Toast.makeText(getApplicationContext(), data.getName(), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void locate() {

  }

  @Override
  public void onIndexChanged(String index, int position) {
    //滚动RecyclerView到索引位置
    mAdapter.scrollToSection(index);
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    String keyword = s.toString();
    if (TextUtils.isEmpty(keyword)){
      mEmptyView.setVisibility(View.GONE);
      mResults = mAllCities;
      ((SectionItemDecoration)(mRecyclerView.getItemDecorationAt(0))).setData(mResults);
      mAdapter.updateData(mResults);
    }else {
      //开始数据库查找
      mResults = dbManager.searchCity(keyword);
      ((SectionItemDecoration)(mRecyclerView.getItemDecorationAt(0))).setData(mResults);
      if (mResults == null || mResults.isEmpty()){
        mEmptyView.setVisibility(View.VISIBLE);
      }else {
        mEmptyView.setVisibility(View.GONE);
        mAdapter.updateData(mResults);
      }
    }
    mRecyclerView.scrollToPosition(0);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if(id == R.id.ivDelete){
      mSearchBox.setText("");
    }else if(id == R.id.ivReturn){
      finishWithResult();
    }
  }

  private void finishWithResult(){
    Intent intent=new Intent();
    intent.putExtra("city", tvCurCity.getText());
    setResult(RESULT_CODE,intent);
    finish();
  }

  private boolean isMatch(String address){
    int index=0;
    for(String area:mdutcg){
      if(address.equals(area)){
        //弹出滚轮框
        hideInput();
        showPickerView(subArea,index);
        return true;
      }
      index++;
    }
    return false;
  }

  /**
   * 隐藏键盘
   */
  protected void hideInput() {
    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    View v = getWindow().peekDecorView();
    if (null != v) {
      imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
  }
}
