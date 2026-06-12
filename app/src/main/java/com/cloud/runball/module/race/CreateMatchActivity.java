package com.cloud.runball.module.race;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.bean.CreteRoomResp;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.PkInfoModel;
import com.cloud.runball.model.PkUserDataModel;
import com.cloud.runball.model.UserGroupModel;
import com.cloud.runball.model.UserInfoModel;
import com.cloud.runball.module.login.LoginOtherActivity;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.model.AppDataManager;
import com.cloud.runball.utils.AppLogger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityMatchDoubleBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: CreateMatchActivity
 * @Description: 双人PK
 * @Author: zhd
 * @CreateDate: 2021/4/7 10:34
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/7 10:34
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CreateMatchActivity extends BaseActivity {

    private ActivityMatchDoubleBinding binding;

    TextView tvRoomID1;
    TextView tvRoomID2;
    TextView tvRoomID3;
    TextView tvRoomID4;
    TextView tvRoomID5;
    TextView tvRoomID6;
    TextView edtTime;
    TextView tvTime;
    Button btnConfirm;
    TextView tvTimeValid;

    String pk_room_number;

    long currentTimeMillis=System.currentTimeMillis();

    int time=180;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_match_double;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMatchDoubleBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void initView() {
        tvRoomID1 = binding.tvRoomID1;
        tvRoomID2 = binding.tvRoomID2;
        tvRoomID3 = binding.tvRoomID3;
        tvRoomID4 = binding.tvRoomID4;
        tvRoomID5 = binding.tvRoomID5;
        tvRoomID6 = binding.tvRoomID6;
        edtTime = binding.edtTime;
        tvTime = binding.tvTime;
        btnConfirm = binding.btnConfirm;
        tvTimeValid = binding.tvTimeValid;

        requestRoomID();

        // Replace @OnClick with listeners
        btnConfirm.setOnClickListener(this::onViewClicked);
        tvTimeValid.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_double_match);
    }


    public void onViewClicked(View v) {
        if (v.getId() == R.id.btnConfirm) {
            if (!TextUtils.isEmpty(pk_room_number)) {
                if(System.currentTimeMillis() - currentTimeMillis >= 500){
                    //创建房间
                    createRoom("0", time, pk_room_number);
                }
                currentTimeMillis=System.currentTimeMillis();
            } else {
                Toast.makeText(this, R.string.lbl_input_match_tip, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.tvTimeValid) {
            requestRoomID();
        }
    }

    private void requestRoomID() {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("pk_type",0);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.requestRoomID(requestBody);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody o) {
                try {
                    JSONObject jsonObject = new JSONObject(o.string());
                    int code = jsonObject.optInt("code", 0);
                    if (code == 1) {
                        pk_room_number = jsonObject.optJSONObject("data").optString("pk_room_number", "");
                        //赛事持续时间+最大参与人
                        int pk_time_long=jsonObject.optJSONObject("data").optInt("pk_time_long",180);
                        int pk_max_person=jsonObject.optJSONObject("data").optInt("pk_max_person",3);

                        if (pk_room_number != null && pk_room_number.trim().length() == 6) {
                            if(tvRoomID1!=null){
                                tvRoomID1.setText(pk_room_number.substring(0, 1));
                            }

                            if(tvRoomID2!=null){
                                tvRoomID2.setText(pk_room_number.substring(1, 2));
                            }

                            if(tvRoomID3!=null){
                                tvRoomID3.setText(pk_room_number.substring(2, 3));
                            }

                            if(tvRoomID4!=null){
                                tvRoomID4.setText(pk_room_number.substring(3, 4));
                            }

                            if(tvRoomID5!=null){
                                tvRoomID5.setText(pk_room_number.substring(4, 5));
                            }

                            if(tvRoomID6!=null){
                                tvRoomID6.setText(pk_room_number.substring(5));
                            }

                            time=pk_time_long;
                            edtTime.setText(TimeUtils.formatMatchDuration(pk_time_long));
                            tvTime.setText(R.string.lbl_double_match_time);
                            //AppLogger.d("-------------requestRoomID-------------"+jsonObject.toString());
                        }
                    } else if(code==2){
                        String msg=jsonObject.optString("msg");
                        Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                        autoLogin();
                    }
                } catch (IOException | JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String msg) {
                if(code==2){
                    Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                    autoLogin();
                }
                AppLogger.d(msg);
            }
        });
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("sys_country", AppDataManager.getInstance().getCountry());
        map.put("device_uid", AppDataManager.getInstance().getAndroidId());
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<UserInfoModel> observable = apiServer.autoLogin(requestBody);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<UserInfoModel>() {
            @Override
            public void onSuccess(UserInfoModel userInfoModel) {
                //把token保存起来
                AppLogger.d("---onSuccess--UserInfoModel=" + userInfoModel);
                SPUtils.put(getApplication(), "token", userInfoModel.getUser_info().getToken());
                AppDataManager.getInstance().setUserInfoModel(userInfoModel);
                WristBallRetrofitHelper.getInstance().updateToken(userInfoModel.getUser_info().getToken());
                //登录成功发送获取比赛tabs
                EventBus.getDefault().post(new MessageEvent(MessageEvent.ON_SEND_RANK_MATCH));
                //弹出登录框
                startLoginOtherActivity();
            }

            @Override
            public void onError(int code, String msg) {
                AppLogger.d(msg);
            }
        });
    }

    private void startLoginOtherActivity(){
        Intent it=new Intent(this, LoginOtherActivity.class);
        it.putExtra("resultCode",true);
        startActivityLaunch.launch(it);
    }

    ActivityResultLauncher<Intent> startActivityLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode=result.getResultCode();
        if(resultCode==LoginOtherActivity.LoginOtherActivity_result){
            requestRoomID();
        }else{
            finish();
        }
    });

    private void createRoom(String pk_type, int time_long, String pk_room_number) {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(4);
        map.put("pk_type", pk_type);
        map.put("pk_result_type", "1");
        map.put("time_long", time_long/60.0f);
        map.put("pk_room_number", pk_room_number);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<CreteRoomResp> observable = apiServer.createRoom(requestBody);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<CreteRoomResp>() {
            @Override
            public void onSuccess(CreteRoomResp creteRoomResp) {
                AppLogger.d(creteRoomResp.toString());
                addRoom(pk_room_number);
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }

    private void addRoom(String pk_room_number){
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("pk_room_number", pk_room_number);

        RequestBody requestBody=RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
        Observable<ResponseBody> observable  =apiServer.addRoom(requestBody);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try{
                    JSONObject jObject = new JSONObject(responseBody.string());
                    AppLogger.d("--CreateMatchActivity.addRoom--" + jObject.toString());
                    int code = jObject.optInt("code", -1);
                    if (code == 1) {
                        //双人PK跳转到主游戏界面
                        if (jObject.optJSONObject("data") != null) {
                            PkInfoModel pkInfoModel = parsePKInfo(jObject.optJSONObject("data"));
                            startMatchMainActivity(pk_room_number, pkInfoModel);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            @Override
            public void onError(int code, String msg) {

            }
        });
    }


    private PkInfoModel parsePKInfo(JSONObject jsonObject) {


        PkInfoModel model = new PkInfoModel();
        model.setPk_room_id(jsonObject.optString("pk_room_id"));
        model.setPk_room_title(jsonObject.optString("pk_room_title"));
        model.setPk_room_number(jsonObject.optString("pk_room_number"));
        model.setPk_type(jsonObject.optInt("pk_type", -1));
        model.setPk_result_type(jsonObject.optString("pk_result_type"));
        model.setUser_id(jsonObject.optLong("user_id", -1));
        model.setCreated_uid(jsonObject.optString("created_uid"));
        model.setStatus(jsonObject.optInt("status", -1));
        model.setTime_long(jsonObject.optInt("time_long", -1));

        JSONArray redArray = jsonObject.optJSONArray("red");
        if (redArray != null && redArray.length() > 0) {
            List<PkUserDataModel> redList=parseTeamItem(redArray);
            model.setRedList(redList);
        }

        JSONArray blueArray = jsonObject.optJSONArray("blue");
        if (blueArray != null && blueArray.length() > 0) {
            List<PkUserDataModel> blueList=parseTeamItem(blueArray);
            model.setBlueList(blueList);
        }

        //添加group_info
        JSONArray group_info=jsonObject.optJSONArray("group_info");
        if(group_info!=null && group_info.length()>0){
            List<UserGroupModel> groupModels=parseTeamGroup(group_info);
            model.setGroupModels(groupModels);
        }


        return model;
    }

    private List<UserGroupModel> parseTeamGroup(JSONArray group_info){
        int len = group_info.length();
        List<UserGroupModel> list = new ArrayList<>();
        for (int index = 0; index < len; index++) {
            JSONObject item = group_info.optJSONObject(index);
            UserGroupModel model=new UserGroupModel(item.optString("user_group"),item.optString("user_group_title"));
            list.add(model);
        }
        return list;
    }

    private List<PkUserDataModel> parseTeamItem(JSONArray redArray) {
        int len = redArray.length();
        List<PkUserDataModel> list = new ArrayList<>();
        for (int index = 0; index < len; index++) {
            JSONObject item = redArray.optJSONObject(index);

            PkUserDataModel model = new PkUserDataModel();

            model.setStatus(item.optInt("status", -1));
            model.setUser_id(item.optString("user_id"));
            model.setPk_room_id(item.optString("pk_room_id"));
            model.setUser_group(item.optString("user_group"));
            model.setUser_name(item.optString("user_name"));
            model.setUser_img(item.optString("user_img"));
            model.setFd(item.optInt("fd"));
            model.setIs_stop(item.optInt("is_stop"));
            model.setIs_ready(item.optInt("is_ready"));
            model.setCircle_count(item.optInt("circle_count"));
            model.setDuration(item.optInt("duration"));

            list.add(model);
        }
        return list;
    }

    boolean isMatchMainActivity=false;
    private void startMatchMainActivity(String pk_room_number, PkInfoModel pk_info) {
        if(!isMatchMainActivity){
            Intent it = new Intent(this, MatchMainActivity.class);
            it.putExtra("pk_room_number", pk_room_number);
            it.putExtra("pk_info", pk_info);
            startActivity(it);
            isMatchMainActivity=true;
        }

        finish();
    }

}
