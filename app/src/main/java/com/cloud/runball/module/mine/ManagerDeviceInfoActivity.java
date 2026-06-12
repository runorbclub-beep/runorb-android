package com.cloud.runball.module.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.base.RecycleViewDivider;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.dialog.NicknameDialog;
import com.cloud.runball.module.home.AddDeviceInfoActivity;
import com.cloud.runball.module.home.adapter.ManagerDeviceAdapter;
import com.cloud.runball.model.DeviceWithServerModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityManagerDeviceBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 管理设备(我的页面跳转使用)
 */
public class ManagerDeviceInfoActivity extends BaseActivity implements ManagerDeviceAdapter.OnItemBindClickListener, ManagerDeviceAdapter.OnItemCheckedClickListener, ManagerDeviceAdapter.OnItemChangeNicknameClickListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = ManagerDeviceInfoActivity.class.getSimpleName();


    public static final int RESULT_CODE = 101;
    ManagerDeviceAdapter deviceAdapter;
    //服务器下发的设备列表(绑定过的)
    List<DeviceWithServerModel> serverModelList = new ArrayList<>();

    private ActivityManagerDeviceBinding binding;
    XRecyclerView recyclerview;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_device;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityManagerDeviceBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        recyclerview = binding.recyclerview;
        initAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    protected void setOnResult() {
        //把设备保存到本地
        this.setResult(RESULT_CODE);
    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.mine_device);
    }

    @Override
    public void onBackPressed() {
        Logger.d("--onBackPressed--");
        super.onBackPressed();
        this.setResult(RESULT_CODE);
        finish();
    }

    /**
     * 请求绑定过的设备列表
     */
    private void requstDeviceWithServer() {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        Observable<List<DeviceWithServerModel>> observable = apiServer.getDevices();
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<List<DeviceWithServerModel>>() {
                @Override
                public void onSuccess(List<DeviceWithServerModel> list) {
                    if(deviceAdapter!=null && recyclerview!=null){
                        serverModelList.clear();
                        serverModelList.addAll(list);
                        onBindDevices();
                        deviceAdapter.setDevices(serverModelList);
                        deviceAdapter.notifyDataSetChanged();
                        recyclerview.refreshComplete();

                        if(list!=null){
                            SPUtils.putData(ManagerDeviceInfoActivity.this, "bleDeviceList", list);
                        } else {
                            SPUtils.putData(ManagerDeviceInfoActivity.this, "bleDeviceList", null);
                        }
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EDIT_NICKNAME));
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                    if(deviceAdapter!=null && recyclerview!=null){
                        deviceAdapter.notifyDataSetChanged();
                        recyclerview.refreshComplete();
                    }
                }
            })
        );
    }


    private void onBindDevices() {
        String deviceUid = (String) SPUtils.get(getApplicationContext(), SPUtils.KEY_MATCH_DEVICE, "");
        if (!TextUtils.isEmpty(deviceUid)) {
            for (DeviceWithServerModel deviceInfo : serverModelList) {
                if (deviceInfo.getDevice_uid().equalsIgnoreCase(deviceUid)) {
                    deviceInfo.setIs_select(true);
                    break;
                }
            }
        }
    }

    /**
     * 新增设备
     * 2021-03-29新增修改传递WLQ_开头
     *
     * @param info
     */
    private void requestAddDevice(DeviceWithServerModel info) {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("device_uid", info.getDevice_uid());
        map.put("device_name", info.getDevice_name());
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<DeviceWithServerModel> observable = apiServer.addDevice(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<DeviceWithServerModel>() {
                @Override
                public void onSuccess(DeviceWithServerModel deviceWithServerModel) {
                    updateDevice(deviceWithServerModel);
                }

                @Override
                public void onError(int code, String msg) {
                    Logger.d(msg);
                }
            })
        );
    }

    /**
     * 批量删除设备列表
     *
     * @param array_devices
     */
    private void requestDeleteDevices(List<Long> array_devices) {
        if (array_devices != null && array_devices.size() <= 0) {
            return;
        }
        Long[] tempDevices = new Long[array_devices.size()];
        for (int i = 0; i < array_devices.size(); i++) {
            tempDevices[i] = array_devices.get(i);
        }

        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("user_device_id", tempDevices);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.deleteDevices(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    //删除设备
                    try{
                        AppLogger.d("--requestDeleteDevices--" + responseBody.string());
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    deleteDevice(array_devices);
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                }
            })
        );
    }


    private void initAdapter() {
        //设备列表
        deviceAdapter = new ManagerDeviceAdapter(this, serverModelList, true);
        deviceAdapter.OnItemCheckedClickListener(this);
        deviceAdapter.OnItemBindClickListener(this);
        deviceAdapter.setOnItemChangeNicknameClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
        recyclerview.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                requstDeviceWithServer();
                //if(recyclerview!=null){
                //    recyclerview.refreshComplete();
                //}
            }

            @Override
            public void onLoadMore() {

            }
        });
        recyclerview.setNoMore(false);
        recyclerview.setAdapter(deviceAdapter);
        requstDeviceWithServer();
    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
        getToolbar().setOnMenuItemClickListener(this);
    }

    public void updateDevice(String device_uid, boolean isBind) {
        for (DeviceWithServerModel deviceInfo : serverModelList) {
            if (deviceInfo.getDevice_uid().equalsIgnoreCase(device_uid)) {
                deviceInfo.setIs_select(isBind);
                break;
            }
        }
    }


    public void updateDevice(DeviceWithServerModel model) {
        boolean isAdd = true;
        for (DeviceWithServerModel deviceInfo : serverModelList) {
            if (deviceInfo.getDevice_uid().equalsIgnoreCase(model.getDevice_uid())) {
                isAdd = false;
                break;
            }
        }

        if (isAdd) {
            serverModelList.add(model);
        }
    }

    public void updateDeviceChecked(boolean isChecked) {
        for (DeviceWithServerModel deviceInfo : serverModelList) {
            deviceInfo.setChecked(isChecked);
        }
        deviceAdapter.setDevices(serverModelList);
        deviceAdapter.notifyDataSetChanged();
    }

    private void updateDeviceNickname(String userDeviceId, String deviceName, String nickname) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_device_id", userDeviceId);
        map.put("device_name", deviceName);
        map.put("name", nickname);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        Observable<ResponseBody> observable = apiServer.updateDevice(requestBody);
        disposable.add(
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody data) {
                    requstDeviceWithServer();
                }
                @Override
                public void onError(int code, String msg) {
                    AppLogger.d(msg);
                }
            })
        );
    }

    /**
     * 删除设备
     */
    public void deleteDevice() {
        //准备删除的设备列表
        List<Long> tempDevices = new ArrayList<>();
        for (int pos = serverModelList.size() - 1; pos >= 0; pos--) {
            if (serverModelList.get(pos).getChecked()) {
                //保存到临时变量
                String spDeviceUid = (String) SPUtils.get(getApplicationContext(), SPUtils.KEY_MATCH_DEVICE, "");
                if (!TextUtils.isEmpty(spDeviceUid) && spDeviceUid.equalsIgnoreCase(serverModelList.get(pos).getDevice_uid())) {
                    SPUtils.remove(getApplicationContext(), SPUtils.KEY_MATCH_DEVICE);
                }
                tempDevices.add(serverModelList.get(pos).getUser_device_id());
                serverModelList.remove(pos);
            }
        }
        //删除本地缓存的服务器临时列表
        deleteDeviceWithServerModels(tempDevices);
        //删除服务器设备列表
        requestDeleteDevices(tempDevices);
        deviceAdapter.setDevices(serverModelList);
        deviceAdapter.notifyDataSetChanged();
    }

    private void deleteDeviceWithServerModels(List<Long> tempDevices) {
        for (int pos = serverModelList.size() - 1; pos >= 0; pos--) {
            for (int i = 0; i < tempDevices.size(); i++) {
                if (serverModelList.get(pos).getUser_device_id() == tempDevices.get(i)) {
                    serverModelList.remove(pos);
                }
            }
        }
    }

    private void deleteDeviceWithServerWithMacs(List<String> tempDevices) {
        for (int pos = serverModelList.size() - 1; pos >= 0; pos--) {
            for (int i = 0; i < tempDevices.size(); i++) {
                if (serverModelList.get(pos).getDevice_uid().equalsIgnoreCase(tempDevices.get(i))) {
                    serverModelList.remove(pos);
                }
            }
        }
    }


    private void deleteDevice(List<Long> deleteDevices) {
        if (deleteDevices.size() <= 0) {
            return;
        }
        for (int pos = serverModelList.size() - 1; pos >= 0; pos--) {
            for (int i = 0; i < deleteDevices.size(); i++) {
                if (serverModelList.get(pos).getUser_device_id() == deleteDevices.get(i)) {
                    serverModelList.remove(pos);
                }
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            //删除
            deleteDevice();
        } else if (item.getItemId() == R.id.action_add) {
//            //添加
//            Intent it = new Intent(this, AddDeviceGuardActivity.class);
//            it.putExtra("addDevices", true);
//            startActivity(it);
//            finish();
            Intent it = new Intent(this, AddDeviceInfoActivity.class);
            startActivityForResult(it, 100);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        requstDeviceWithServer();
    }

    @Override
    public void onItemCheckedClick(DeviceWithServerModel data) {
        Logger.d("--onItemCheckedClick--" + data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recyclerview != null) {
            recyclerview.destroy();
            recyclerview=null;
        }
    }


    @Override
    public void onItemBindClick(int position, DeviceWithServerModel data) {
        if (data.isIs_select()) {
            SPUtils.remove(getApplicationContext(), SPUtils.KEY_MATCH_DEVICE);
            updateDevice(data.getDevice_uid(), false);
        } else {
            SPUtils.put(getApplicationContext(), SPUtils.KEY_MATCH_DEVICE, data.getDevice_uid());
            updateDevice(data.getDevice_uid(), true);
        }
        deviceAdapter.setDevices(serverModelList);
        deviceAdapter.notifyDataSetChanged();
        recyclerview.refreshComplete();
    }

    @Override
    public void onItemChangeClick(int position, DeviceWithServerModel data) {
        NicknameDialog dialog = new NicknameDialog();
        dialog.show(this, data.getDevice_name(), data.getName(), getResources().getText(R.string.btn_confirm2).toString(), newNickname -> {
//            data.setNickname(newNickname);
//            List<DeviceNicknameData> deviceNicknameList = new ArrayList<>();
//            if (serverModelList != null) {
//                for (int i = 0; i < serverModelList.size(); i++) {
//                    DeviceWithServerModel item = serverModelList.get(i);
//                    if (!TextUtils.isEmpty(item.getNickname())) {
//                        deviceNicknameList.add(new DeviceNicknameData(item.getDevice_name(), item.getNickname()));
//                    }
//                }
//            }
//            SPUtils.putData(this, "deviceNickname", deviceNicknameList);
//            deviceAdapter.setDeviceNicknameList(deviceNicknameList);
//            deviceAdapter.notifyDataSetChanged();

//            String userDeviceId = serverModelList.get(position).getDevice_uid();
            String userDeviceId = Long.toString(serverModelList.get(position).getUser_device_id());
            String deviceName = serverModelList.get(position).getDevice_name();
            updateDeviceNickname(userDeviceId, deviceName, newNickname);
        });
    }
}
