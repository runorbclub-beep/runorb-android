package com.cloud.runball.module.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cloud.runball.BuildConfig;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.RecycleViewDivider;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.PermissionWallUtils;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.dialog.ConnectingDialog;
import com.cloud.runball.module.home.adapter.DeviceAdapter;
import com.cloud.runball.model.DeviceInfo;
import com.cloud.runball.model.DeviceWithServerModel;
import com.cloud.runball.module_bluetooth.constant.ServiceNoticeConstant;
import com.cloud.runball.module_bluetooth.constant.ServiceSendConstant;
import com.cloud.runball.module_bluetooth.data.DeviceBallInfo;
import com.cloud.runball.module_bluetooth.data.event.BallInfo;
import com.cloud.runball.module_bluetooth.data.event.ServiceNoticeEvent;
import com.cloud.runball.module_bluetooth.data.event.ServiceSendEvent;
import com.cloud.runball.module_bluetooth.utils.BleUtils;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.cloud.runball.databinding.ActivityAddDeviceBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 选择添加设备(主页面跳转使用)
 * @author ns467
 */
public class AddDeviceInfoActivity extends BaseActivity implements DeviceAdapter.OnItemConnectClickListener {

  private static final String TAG = AddDeviceInfoActivity.class.getSimpleName();

  private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
  private static final int REQUEST_CODE_ACCESS_BLUETOOTH_SCAN = 2;
  /**
   * 请求开启蓝牙
   */
  private static final int ACTION_REQUEST_ENABLE = 3;
  private static final int REQUEST_CODE_FOR_SCAN = 4;
  public static final int RESULT_CODE = 101;
  private final int REQUEST_CODE_OPEN_GPS = 0x16;

  ArrayList<DeviceInfo> deviceList = new ArrayList<>();

  private ActivityAddDeviceBinding binding;
  XRecyclerView recyclerview;

  DeviceAdapter deviceAdapter;

  List<DeviceWithServerModel> serverModelList = new ArrayList<>();

  //是否加入
  boolean isAppendAdd = true;

  //是否用户点选设备
  boolean isSelfConnected = false;

  @Override
  protected BasePresenter createPresenter() {
    return null;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.activity_add_device;
  }

  @Override
  protected View onCreateContentView(LayoutInflater inflater) {
    binding = ActivityAddDeviceBinding.inflate(inflater);
    return binding.getRoot();
  }

  @Override
  protected void addListener() {

  }

  @Override
  protected String getTitleLabel() {
    return getString(R.string.title_mine_add_device);
  }

  @Override
  protected void initView() {
    recyclerview = binding.recyclerview;
    isAppendAdd = getIntent().getBooleanExtra("addDevices", true);

    EventBus.getDefault().register(this);
    initAdapter();

    if (verifyIfRequestPermission()) {
      recyclerview.refresh();
    }
  }

  @Override
  protected void setOnResult() {
    //把设备保存到本地
    setBlueToothData();
    this.setResult(RESULT_CODE);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    setBlueToothData();
    this.setResult(RESULT_CODE);
    finish();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onServiceNoticeEvent(ServiceNoticeEvent event) {
    switch (event.getCode()) {
//            case ServiceNoticeConstant.CODE_SCAN_START: {
//
//            }break;
      case ServiceNoticeConstant.CODE_SCAN_DEVICE: {
        //封装蓝牙数据对象到设备列表
        DeviceBallInfo data = (DeviceBallInfo) event.getData();
        addBleDevice(data.getBleDevice().getName(), data.getBleDevice().getMac(), data);
        deviceAdapter.notifyDataSetChanged();
      }break;
      case ServiceNoticeConstant.CODE_SCAN_FINISHED: {
        dismissProgressLoading();
      }break;
      case ServiceNoticeConstant.CODE_CONNECT_START: {
        showProgressLoading(getString(R.string.connecting));
      }break;
      case ServiceNoticeConstant.CODE_CONNECT_SUCCESS: {
        Log.d("PRETTY_LOGGER", "------已经连接-------------");
        new Handler().postDelayed(() -> {
          BallInfo data = (BallInfo) event.getData();
          // 提交新增设备
          String deviceName = data.getName();
          SPUtils.put(getApplicationContext(), SPUtils.KEY_MATCH_DEVICE, deviceName);
          updateAddDevice(deviceName);
          //设备已经连接
          String mac = data.getMac();
          updateDevice(mac, true);
          deviceAdapter.notifyDataSetChanged();

          dismissProgressLoading();

          if(isSelfConnected){
            finish();
          }
        }, 1000);

      }break;
      case ServiceNoticeConstant.CODE_CONNECT_FAIL: {
        dismissProgressLoading();
      }break;
      case ServiceNoticeConstant.CODE_CONNECT_FINISHED: {
        //设备断开连接
        allDisConnectDevice();
        deviceAdapter.notifyDataSetChanged();
        dismissProgressLoading();
      }break;
//            case ServiceNoticeConstant.CODE_NOTIFY_RUNNING: {
//
//            }break;
//            case ServiceNoticeConstant.CODE_NOTIFY_RUN_FINISH: {
//
//            }break;
//            case ServiceNoticeConstant.CODE_NOTIFY_TOTAL_TIME: {
//
//            }break;
//            case ServiceNoticeConstant.CODE_NOTIFY_ELECTRICITY: {
//
//            }break;
    }
  }

  public void addBleDevice(String tmpDevName, String tmpMacAddress, DeviceBallInfo deviceBallInfo) {
    boolean isAdd = true;
    int size = deviceList.size();
    for (int i = 0; i < size; i++) {
      if (deviceList.get(i).getName().equalsIgnoreCase(tmpDevName)) {
        isAdd = false;
        break;
      }
    }
    if (isAdd) {
      DeviceInfo deviceInfo = new DeviceInfo(tmpDevName, tmpMacAddress, deviceBallInfo);
      deviceList.add(deviceInfo);
    }

    Collections.sort(deviceList);
  }

  /**
   * 新增设备
   * 2021-03-29新增修改传递WLQ_开头
   * @param info
   */
  private void requestAddDevice(DeviceInfo info) {
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    HashMap<String, Object> map = new HashMap<>();
    map.put("device_uid", info.getName());
    map.put("device_name", info.getName());
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    Observable<DeviceWithServerModel> observable = apiServer.addDevice(requestBody);
    disposable.add(
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<DeviceWithServerModel>() {
          @Override
          public void onSuccess(DeviceWithServerModel deviceWithServerModel) {
            //updateDevice(deviceWithServerModel);
          }

          @Override
          public void onError(int code, String msg) {
            AppLogger.d(msg);
          }
        })
    );
  }

  /**
   * 保存设备列表
   */
  private void setBlueToothData() {
    if (deviceList.size() > 0) {
      for (int i = deviceList.size() - 1; i >= 0; i--) {
        if (!deviceList.get(i).getConnected()) {
          deviceList.remove(i);
        }
      }
    }
  }

  private void initAdapter() {
    //从本地中获取保存的设备信息
    //设备列表
    deviceAdapter = new DeviceAdapter(this, deviceList, true);
    deviceAdapter.OnItemConnectClickListener(this);

    //初始化我的数据信息
    recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
    recyclerview.setLoadingMoreEnabled(false);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        if (verifyIfRequestPermission()) {
          EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_SCAN_OPEN));
        } else {
          recyclerview.refreshComplete();
        }
      }

      @Override
      public void onLoadMore() {

      }
    });
    recyclerview.setAdapter(deviceAdapter);
    deviceAdapter.notifyDataSetChanged();
  }

  @Override
  protected void supportToolbar() {
    super.supportToolbar();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
      if (
          grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
          && grantResults[1] == PackageManager.PERMISSION_GRANTED
      ) {
        //获得授权后开始扫描
        if (verifyIfRequestPermission()) {
          recyclerview.refresh();
        }
      } else {
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(AddDeviceInfoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(AddDeviceInfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
          if (verifyIfRequestPermission()) {
            recyclerview.refresh();
          }
        } else {
          showPermissionDialog0(getString(R.string.tip), getString(R.string.location_ble_tip));
        }
//        showPermissionDialog0(getString(R.string.tip), getString(R.string.refuse_ble));
      }
    } else if (requestCode == REQUEST_CODE_ACCESS_BLUETOOTH_SCAN) {
      if (
              grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                      && grantResults[1] == PackageManager.PERMISSION_GRANTED
      ) {
        //获得授权后开始扫描
        if (verifyIfRequestPermission()) {
          recyclerview.refresh();
        }
      } else {
        if (
                ActivityCompat.shouldShowRequestPermissionRationale(AddDeviceInfoActivity.this, "android.permission.BLUETOOTH_SCAN")
                || ActivityCompat.shouldShowRequestPermissionRationale(AddDeviceInfoActivity.this, "android.permission.BLUETOOTH_CONNECT")
        ) {
          if (verifyIfRequestPermission()) {
            recyclerview.refresh();
          }
        } else {
          showPermissionDialog0(getString(R.string.tip), getString(R.string.bluetooth_ble_tip));
        }
//        showPermissionDialog0(getString(R.string.tip), getString(R.string.refuse_ble));
      }
    } else if (requestCode == ACTION_REQUEST_ENABLE) {
      //已经从设置页面设置蓝牙回来
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //AppLogger.d("AddDeviceInfoActivity:onActivityResult:requestCode=" + requestCode + ";RESULT_CODE=" + resultCode);
    if (requestCode == ACTION_REQUEST_ENABLE && resultCode == -1) {
      if (verifyIfRequestPermission()) {
        recyclerview.refresh();
      }
    } else if (requestCode == REQUEST_CODE_OPEN_GPS) {
      if (verifyIfRequestPermission()) {
        recyclerview.refresh();
      }
    }
  }

  private boolean verifyIfRequestPermission() {
    if (!BleUtils.isSupportBle()) {
      return false;
    }
    if (!BleUtils.isBlueEnable()) {
      Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(intent, ACTION_REQUEST_ENABLE);
      return false;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
      if (locationManager == null) {
        return false;
      }
      if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
          && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        showLocationServiceDialog(getString(R.string.tip), getString(R.string.location_service));
        return false;
      }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (
          ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
      ) {
        ActivityCompat.requestPermissions(AddDeviceInfoActivity.this,
            new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            },
            REQUEST_CODE_ACCESS_COARSE_LOCATION
        );
        return false;
      }
    }
    if (Build.VERSION.SDK_INT >= 31) {
      if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN") != PackageManager.PERMISSION_GRANTED
      || ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(AddDeviceInfoActivity.this, new String[] {
                "android.permission.BLUETOOTH_SCAN",
                        "android.permission.BLUETOOTH_CONNECT"
        },
                REQUEST_CODE_ACCESS_BLUETOOTH_SCAN);
      }
    }
    return true;
  }

  private void showProgressLoading(String tip) {
    ConnectingDialog.show(this, () -> {
      dismissProgressLoading();
      EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CONNECT_FINISH));
    });
  }

  private void dismissProgressLoading() {
    ConnectingDialog.dismiss();
    if (recyclerview != null) {
      recyclerview.refreshComplete();
    }
  }

  public void showPermissionDialog0(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.btn_cancel, null);
    builder.setPositiveButton(R.string.btn_confirm, (dialogInterface, i) -> {
      PermissionWallUtils.startPermissionSetting();
    });
    builder.show();
  }

  public void showPermissionDialog(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.btn_cancel, null);
    builder.setPositiveButton(R.string.btn_confirm, (dialogInterface, i) -> {
      Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
      Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
      startActivity(intent);
      dialogInterface.dismiss();
    });
    builder.show();
  }

  public void showLocationServiceDialog(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.btn_cancel, null);
    builder.setPositiveButton(R.string.btn_confirm, (dialogInterface, i) -> {
      Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
      startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
    });
    builder.show();
  }

  @Override
  public void onItemConnectClick(DeviceInfo data) {
    DeviceBallInfo deviceBallInfo = data.getDeviceBallInfo();
//        if (BleManager.getInstance().isConnected(deviceBallInfo.getBleDevice())) {
    if (BleUtils.isConnectedDevice(deviceBallInfo.getBleDevice().getMac())) {
      String spName = (String) SPUtils.get(getApplicationContext(), SPUtils.KEY_MATCH_DEVICE, "");
      if (!TextUtils.isEmpty(spName) && spName.equalsIgnoreCase(data.getName())) {
        SPUtils.remove(getApplicationContext(), SPUtils.KEY_MATCH_DEVICE);
      }
      EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CONNECT_FINISH, deviceBallInfo));
    } else {
      isSelfConnected = true;
      EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CONNECT_DEVICE, deviceBallInfo));
    }


  }

  /**
   * 更新设备连接状态
   */
  public void allDisConnectDevice() {
    for (DeviceInfo deviceInfo : deviceList) {
      if (deviceInfo.getConnected()) {
        deviceInfo.setConnected(false);
      }
    }
  }

  /**
   * 更新设备连接状态
   * @param mac
   * @param isConnected
   */
  public void updateDevice(String mac, boolean isConnected) {
    for (DeviceInfo deviceInfo : deviceList) {
      if (deviceInfo.getMac().equalsIgnoreCase(mac)) {
        deviceInfo.setConnected(isConnected);
        break;
      }
    }
  }

  /**
   * 提交新增设备
   */
  public void updateAddDevice(String deviceName) {
    //这里还需要处理----------------------------------------------------------------------------->
    if(isAppendAdd){
      boolean isAdd=true;
      for (DeviceWithServerModel info : serverModelList) {
        if (info.getDevice_uid().equalsIgnoreCase(deviceName)) {
          isAdd=false;
        }
      }
      if(isAdd){
        requestAddDevice(new DeviceInfo(deviceName,deviceName));
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if(recyclerview!=null){
      recyclerview.destroy();
      recyclerview=null;
    }
    EventBus.getDefault().post(new ServiceSendEvent<>(ServiceSendConstant.CODE_CIRCLE_CLEAR));
    EventBus.getDefault().unregister(this);
  }


}
