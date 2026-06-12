package com.cloud.runball.module.race;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.SPUtils;
import com.cloud.runball.basecomm.utils.TimeUtils;
import com.cloud.runball.bean.CreteRoomResp;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.GroupInfoModel;
import com.cloud.runball.model.PkInfoModel;
import com.cloud.runball.model.PkUserDataModel;
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
import com.cloud.runball.databinding.ActivityMatchTeamBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: CreateMatchTeamActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/8 10:24
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/8 10:24
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CreateMatchTeamActivity extends BaseActivity {

    private ActivityMatchTeamBinding binding;

    TextView tvRoomID1;
    TextView tvRoomID2;
    TextView tvRoomID3;
    TextView tvRoomID4;
    TextView tvRoomID5;
    TextView tvRoomID6;
    Button btnConfirm;
    EditText edtBlue;
    EditText edtRed;
    TextView edtMax;
    TextView edtTime;
    TextView tvTimeValid;
    ImageView img_match_more;


    String pk_room_number;

    long currentTimeMillis = System.currentTimeMillis();

    //用户可选择人数
    private List<String> persons = new ArrayList<>();

    int time=180;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_match_team;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMatchTeamBinding.inflate(inflater);
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
        btnConfirm = binding.btnConfirm;
        edtBlue = binding.edtBlue;
        edtRed = binding.edtRed;
        edtMax = binding.edtMax;
        edtTime = binding.edtTime;
        tvTimeValid = binding.tvTimeValid;
        img_match_more = binding.imgMatchMore;

        requestRoomID();
        updateMaxPerson(2);

        // Replace @OnClick with listeners
        btnConfirm.setOnClickListener(this::onViewClicked);
        tvTimeValid.setOnClickListener(this::onViewClicked);
        img_match_more.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_team_match);
    }

    public void onViewClicked(View v) {
        if (v.getId() == R.id.btnConfirm) {

            //String maxPerson = edtMax.getText().toString();
            String maxPerson = edtMax.getTag().toString();

            String group_red_title = edtRed.getText().toString();
            String group_blue_title = edtBlue.getText().toString();

            if (maxPerson.length() > 2) {
                Toast.makeText(this, R.string.lbl_input_match_person_tip, Toast.LENGTH_LONG).show();
                return;
            }
            if (!TextUtils.isEmpty(pk_room_number)  && !TextUtils.isEmpty(maxPerson) && !TextUtils.isEmpty(group_red_title) && !TextUtils.isEmpty(group_blue_title)) {
                //创建房间
                if (System.currentTimeMillis() - currentTimeMillis >= 1000) {
                    int pk_max_person = Integer.parseInt(maxPerson);
                    createRoom("1", time, pk_room_number, pk_max_person, group_red_title, group_blue_title);
                }
                currentTimeMillis = System.currentTimeMillis();
            } else {
                Toast.makeText(this, R.string.lbl_input_match_tip, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.tvTimeValid) {
            requestRoomID();
        } else if (v.getId() == R.id.img_match_more) {
            showPickerView();
        }
    }

    private void updateMaxPerson(int max){
        persons.clear();
        for (int i = max; i <= 10; i++) {
            persons.add(String.valueOf(i));
        }
        edtMax.setText(String.format(getResources().getString(R.string.lbl_input_match_person_num),String.valueOf(max)));
        edtMax.setTag(String.valueOf(max));
    }

    /**
     * 展示选择器
     * 核心代码
     */
    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //展示选中数据
                edtMax.setText(String.format(getResources().getString(R.string.lbl_input_match_person_num),persons.get(options1)));
                edtMax.setTag(persons.get(options1));
            }
        })
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build();
        pvOptions.setPicker(persons);
        pvOptions.show();
    }


    private void requestRoomID() {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("pk_type",1);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.requestRoomID(requestBody);
        disposable.add(
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
                            int pk_max_person=jsonObject.optJSONObject("data").optInt("pk_max_person",2);

                            if (pk_room_number != null && pk_room_number.trim().length() == 6) {
                                tvRoomID1.setText(pk_room_number.substring(0, 1));
                                tvRoomID2.setText(pk_room_number.substring(1, 2));
                                tvRoomID3.setText(pk_room_number.substring(2, 3));
                                tvRoomID4.setText(pk_room_number.substring(3, 4));
                                tvRoomID5.setText(pk_room_number.substring(4, 5));
                                tvRoomID6.setText(pk_room_number.substring(5));

                                time=pk_time_long;
                                edtTime.setText(TimeUtils.formatMatchDuration(pk_time_long));
                                updateMaxPerson(pk_max_person);

                                AppLogger.d(jsonObject.toString());
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
                    if(code==2 && !isShowLogin){
                        Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                        autoLogin();
                    }
                    AppLogger.d(msg);
                }
            })
        );
    }

    boolean isShowLogin=false;

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
        disposable.add(
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
            })
        );
    }

    private void startLoginOtherActivity(){
        isShowLogin=true;
        Intent it=new Intent(this, LoginOtherActivity.class);
        it.putExtra("resultCode",true);
        startActivityLaunch.launch(it);
    }

    ActivityResultLauncher<Intent> startActivityLaunch =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode=result.getResultCode();
        if(resultCode== LoginOtherActivity.LoginOtherActivity_result){
            isShowLogin=false;
            requestRoomID();
        }else{
            finish();
        }
    });

    private void createRoom(String pk_type, int time_long, String pk_room_number, int pk_max_person, String group_red_title, String group_blue_title) {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(7);
        map.put("pk_type", pk_type);
        map.put("pk_result_type", "1");
        map.put("time_long", time_long/60.0f);
        map.put("pk_room_number", pk_room_number);
        map.put("pk_max_person", pk_max_person);
        map.put("group_red_title", group_red_title);
        map.put("group_blue_title", group_blue_title);

        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<CreteRoomResp> observable = apiServer.createRoom(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<CreteRoomResp>() {
                @Override
                public void onSuccess(CreteRoomResp creteRoomResp) {
                    AppLogger.d(creteRoomResp.toString());
                    AppLogger.d("-----创建房间--用户加入房间，参与PK---------用户进入房间”---");
                    //自己创建默认红方
                    addRoom(pk_room_number, "red");
                }

                @Override
                public void onError(int code, String msg) {
                    if(code==2){
                        Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
                        autoLogin();
                    }
                }
            })
        );
    }


    private void addRoom(String pk_room_number, String user_group) {
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("pk_room_number", pk_room_number);
        map.put("user_group", user_group);

        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.addRoom(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        String response = responseBody.string();
                        AppLogger.d("---CreateMatchTeamActivity.--addRoom--" + response);
                        JSONObject jObject = new JSONObject(response);
                        int code = jObject.optInt("code", -1);
                        if (code == 1) {
                            JSONObject pk_info = jObject.optJSONObject("data");
                            JSONObject data = jObject.optJSONObject("data");
                            if (pk_info != null) {
                                //跳转到双方列表页面并创建 websocket
                                //PkInfoModel pkInfoModel=parsePKInfo(pk_info);
                                //PkDataModel model=new PkDataModel();
                                //model.setPk_room_id(pkInfoModel.getPk_room_id());
                                //model.setUser_group(pkInfoModel.getUser_group());
                                //model.setUser_id(String.valueOf(pkInfoModel.getUser_id()));
                            }
                            if (data != null) {
                                ArrayList<PkUserDataModel> red_list = parsePKGroups(data, "red");
                                ArrayList<PkUserDataModel> blue_list = parsePKGroups(data, "blue");

                                JSONArray group_info = jObject.optJSONObject("data").optJSONArray("group_info");
                                ArrayList<GroupInfoModel> group_list = parsePKGroups(group_info);

                                PkInfoModel pkInfoModel = parsePKInfo(pk_info);
                                startMatchAddOverActivity(pkInfoModel.getPk_max_person(), pk_room_number, pkInfoModel, red_list, blue_list, group_list);
                            }

                            AppLogger.d(responseBody.string());
                        } else if (code == 0) {

                        } else if(code==2){
                            String msg=jObject.optString("msg");
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
                }
            })
        );
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
        model.setPk_max_person(jsonObject.optInt("pk_max_person", 10));

        JSONArray redArray = jsonObject.optJSONArray("red");
        if (redArray != null && redArray.length() > 0) {
            List<PkUserDataModel> redList = parseTeamItem(redArray);
            model.setRedList(redList);
        }

        JSONArray blueArray = jsonObject.optJSONArray("blue");
        if (blueArray != null && blueArray.length() > 0) {
            List<PkUserDataModel> blueList = parseTeamItem(blueArray);
            model.setBlueList(blueList);
        }

        return model;
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

    private void startMatchAddOverActivity(int pk_max_person, String pk_room_number, PkInfoModel pk_info, ArrayList<PkUserDataModel> red_list, ArrayList<PkUserDataModel> blue_list, ArrayList<GroupInfoModel> group_list) {
        Intent it = new Intent(this, CreateMatchAddOverActivity.class);
        it.putExtra("pk_max_person", pk_max_person);
        it.putExtra("pk_room_number", pk_room_number);
        it.putExtra("pk_info", pk_info);
        it.putParcelableArrayListExtra("red", red_list);
        it.putParcelableArrayListExtra("blue", blue_list);
        it.putParcelableArrayListExtra("group", group_list);
        startActivity(it);
        finish();
    }


    private ArrayList<GroupInfoModel> parsePKGroups(JSONArray group_info) {
        ArrayList<GroupInfoModel> list = new ArrayList<>();
        if (group_info != null && group_info.length() == 2) {
            int len = group_info.length();
            for (int index = 0; index < len; index++) {
                GroupInfoModel model = new GroupInfoModel();
                JSONObject data = group_info.optJSONObject(index);
                model.setUser_group(data.optString("user_group"));
                model.setUser_group_title(data.optString("user_group_title"));
                list.add(model);
            }
        }
        return list;
    }

    /**
     * 解析不同组的玩家列表
     *
     * @param pk_group_list
     * @param user_group
     * @return
     */
    private ArrayList<PkUserDataModel> parsePKGroups(JSONObject pk_group_list, String user_group) {
        ArrayList<PkUserDataModel> list = new ArrayList<>();
        JSONArray group_list_array = pk_group_list.optJSONArray(user_group);
        if (group_list_array != null) {
            int len = group_list_array.length();
            for (int index = 0; index < len; index++) {
                PkUserDataModel model = new PkUserDataModel();
                JSONObject data = group_list_array.optJSONObject(index);
                model.setUser_pk_list_id(data.optLong("user_pk_list_id"));
                model.setUser_id(data.optString("user_id"));
                model.setPk_room_id(data.optString("pk_room_id"));
                model.setFd(data.optInt("fd"));
                model.setIs_stop(data.optInt("is_stop"));
                model.setIs_ready(data.optInt("is_ready"));
                model.setUser_group(data.optString("user_group"));
                model.setUser_name(data.optString("user_name"));
                model.setUser_img(data.optString("user_img"));

                list.add(model);
            }
        }
        return list;
    }

}
