package com.cloud.runball.module.race;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.model.GroupInfoModel;
import com.cloud.runball.model.PkInfoModel;
import com.cloud.runball.model.PkUserDataModel;
import com.cloud.runball.model.UserGroupModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloud.runball.databinding.ActivityMatchAdd2Binding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: CreateMatchAddActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/8 14:34
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/8 14:34
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CreateMatchAdd2Activity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {



    private ActivityMatchAdd2Binding binding;
    RadioButton rbRed;

    RadioButton rbBlue;

    Button btnConfirm;

    RadioGroup rgTeam;

    String user_group;

    String pk_room_number;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return  R.layout.activity_match_add2;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMatchAdd2Binding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {
        rgTeam.setOnCheckedChangeListener(this);
    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_add_match);
    }

    @Override
    protected void initView() {
        rbRed = binding.rbRed;
        rbBlue = binding.rbBlue;
        btnConfirm = binding.btnConfirm;
        rgTeam = binding.rgTeam;
        pk_room_number=getIntent().getStringExtra("pk_room_number");
        ArrayList<UserGroupModel> models = getIntent().getParcelableArrayListExtra("groups");
        if(models!=null && models.size()==2){
            rbRed.setText(models.get(0).getUser_group_title());
            rbRed.setTag(models.get(0).getUser_group());

            rbBlue.setText(models.get(1).getUser_group_title());
            rbBlue.setTag(models.get(1).getUser_group());

            user_group=(String) rbRed.getTag();
        }else{
            rgTeam.setVisibility(View.GONE);
        }

        // wire click listener replacing @OnClick
        btnConfirm.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void setOnResult() {

    }

    long currentTimeMillis;

    public void onViewClicked(View v){
        if(v.getId()==R.id.btnConfirm){
            if(!TextUtils.isEmpty(pk_room_number)){
                if(System.currentTimeMillis()-currentTimeMillis>=500){
                    addRoom(pk_room_number,user_group);
                }
                currentTimeMillis=System.currentTimeMillis();
            }else{
                Toast.makeText(this,R.string.lbl_error_room_num_tip,Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.rbRed){
            user_group=(String) rbRed.getTag();
        }else if(checkedId==R.id.rbBlue){
            user_group=(String) rbBlue.getTag();
        }
    }


    private void addRoom(String pk_room_number,String user_group){
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("pk_room_number", pk_room_number);
        map.put("user_group", user_group);

        RequestBody requestBody=RequestBody.create(MediaType.parse("Content-Type, application/json"),new JSONObject(map).toString());
        Observable<ResponseBody> observable  =apiServer.addRoom(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        String response=responseBody.string();
                        AppLogger.d("-------------------CreateMatchAdd2Activity.--addRoom----------"+response);

                        JSONObject jObject = new JSONObject(response);
                        int code=jObject.optInt("code",-1);
                        if(code==1){
                            JSONObject data=jObject.optJSONObject("data");
                            if(data!=null){
                                ArrayList<PkUserDataModel> red_list=parsePKGroups(data,"red");
                                ArrayList<PkUserDataModel> blue_list=parsePKGroups(data,"blue");

                                JSONArray group_info=jObject.optJSONObject("data").optJSONArray("group_info");
                                ArrayList<GroupInfoModel> group_list=parsePKGroups(group_info);


                                PkInfoModel pkInfoModel=parsePKInfo(data);
                                //这里真是数字3人
                                startMatchAddOverActivity(pkInfoModel.getPk_max_person(),pk_room_number,pkInfoModel,red_list,blue_list,group_list);
                            }
                            AppLogger.d(response);
                        }else if(code==0){
                            Toast.makeText(getApplication(),jObject.optString("msg"),Toast.LENGTH_LONG).show();
                        }else{
                            //提示接口错误
                        }
                    }catch (IOException | JSONException ex){
                        ex.printStackTrace();
                    }
                }
                @Override
                public void onError(int code, String msg) {
                      Toast.makeText(getApplication(),msg,Toast.LENGTH_LONG).show();
                }
            })
        );
    }


    private void startMatchAddOverActivity(int pk_max_person,String pk_room_number,PkInfoModel pk_info, ArrayList<PkUserDataModel> red_list, ArrayList<PkUserDataModel> blue_list, ArrayList<GroupInfoModel> group_list){
        Intent it=new Intent(this, CreateMatchAddOverActivity.class);
        it.putExtra("pk_max_person",pk_max_person);
        it.putExtra("pk_room_number",pk_room_number);
        it.putExtra("pk_info",pk_info);
        it.putParcelableArrayListExtra("red",red_list);
        it.putParcelableArrayListExtra("blue",blue_list);
        it.putParcelableArrayListExtra("group",group_list);
        startActivity(it);
        finish();
    }

    private PkInfoModel parsePKInfo(JSONObject jsonObject){


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

        String pk_max_person=jsonObject.optString("pk_max_person").replace("人","")
                .replace("Person","").replace("person","") .replace("Persons","").replace("persons","")
                .replace("Player","").replace("player","").replace("Players","").replace("players","");

        try{
            model.setPk_max_person(Integer.parseInt(pk_max_person));
        }catch (Exception ex){
            ex.printStackTrace();
            model.setPk_max_person(10);
        }

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

    private ArrayList<GroupInfoModel> parsePKGroups(JSONArray group_info){
        ArrayList<GroupInfoModel> list=new ArrayList<>();
        if(group_info!=null && group_info.length()==2){
            int len=group_info.length();
            for(int index=0;index<len;index++){
                GroupInfoModel model=new GroupInfoModel();
                JSONObject data=group_info.optJSONObject(index);
                model.setUser_group(data.optString("user_group"));
                model.setUser_group_title(data.optString("user_group_title"));
                list.add(model);
            }
        }
        return list;
    }

    /**
     * 解析不同组的玩家列表
     * @param pk_group_list
     * @param user_group
     * @return
     */
    private ArrayList<PkUserDataModel> parsePKGroups(JSONObject pk_group_list,String user_group){
        ArrayList<PkUserDataModel> list=new ArrayList<>();
        JSONArray group_list_array=pk_group_list.optJSONArray(user_group);
        if(group_list_array!=null){
            int len=group_list_array.length();
            for(int index=0;index<len;index++){
                PkUserDataModel model=new PkUserDataModel();
                JSONObject data=group_list_array.optJSONObject(index);
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
