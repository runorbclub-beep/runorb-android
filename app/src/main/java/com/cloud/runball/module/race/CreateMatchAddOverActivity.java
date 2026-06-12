package com.cloud.runball.module.race;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.App;
import com.cloud.runball.R;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.dialog.ConfirmDialog;
import com.cloud.runball.module.home.AddDeviceInfoActivity;
import com.cloud.runball.module.race.adapter.MatchGridAdapter;
import com.cloud.runball.bean.BlePackData;
import com.cloud.runball.bean.MessageEvent;
import com.cloud.runball.model.GroupInfoModel;
import com.cloud.runball.model.PkInfoModel;
import com.cloud.runball.model.PkUserDataModel;
import com.cloud.runball.module_bluetooth.utils.BleUtils;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.dialog.PKRuleDialog;
import com.cloud.runball.service.websocket.WebSocketServiceManager;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: CreateMatchAddOverActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/4/9 13:58
 * @UpdateUser: zhd
 * @UpdateDate: 2021/4/9 13:58
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CreateMatchAddOverActivity extends AppCompatActivity implements View.OnClickListener,MatchGridAdapter.OnItemClickListener, Toolbar.OnMenuItemClickListener {

    RecyclerView ryTeamRed;
    RecyclerView ryTeamBlue;
    TextView tvTeamRed;
    TextView tvTeamBlue;
    Button btnWaitReady;

    ArrayList<PkUserDataModel> red_list = new ArrayList<>();
    MatchGridAdapter mRedMatchGridAdapter;

    ArrayList<PkUserDataModel> blue_list = new ArrayList<>();
    MatchGridAdapter mBlueMatchGridAdapter;

    String pk_room_number;
    long user_pk_list_id;
    long blue_user_pk_list_id;
    String pk_room_id;
    int max_person_num = -1;
    PkInfoModel pk_info;
    String user_group;

    TextView tvPower;

    protected Toolbar toolbar;
    int max_person=10;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_add_over);

        tvPower=findViewById(R.id.tvPower);
        ryTeamRed=findViewById(R.id.ryTeamRed);
        ryTeamBlue=findViewById(R.id.ryTeamBlue);
        tvTeamRed=findViewById(R.id.tvTeamRed);
        tvTeamBlue=findViewById(R.id.tvTeamBlue);
        btnWaitReady=findViewById(R.id.btnWaitReady);
        btnWaitReady.setOnClickListener(this);

        max_person = this.getIntent().getIntExtra("pk_max_person",10);
        pk_room_number = this.getIntent().getStringExtra("pk_room_number");
        supportToolbar(pk_room_number);
        EventBus.getDefault().register(this);
        initView();
    }


    protected void supportToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        String tempTitle=getString(R.string.title_match_team)+title;
        toolbar.setTitle(tempTitle);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitleMargin(0, 5, 5, 5);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.btn_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnResult();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
    }

    protected void initView() {
        AppLogger.d("--CreateMatchAddOverActivity---initView-");
        pk_info = (PkInfoModel) this.getIntent().getSerializableExtra("pk_info");
        if(pk_info!=null){
            max_person_num = pk_info.getMax_person_num();
            pk_room_id = pk_info.getPk_room_id();
            long user_id=pk_info.getUser_id();
            //选出user_group
            user_group=this.getUser_group(pk_info,user_id);
            String pkdata="";
            try {
                JSONObject pkdataObj=new JSONObject();
                pkdataObj.put("pk_room_id",pk_room_id);
                pkdataObj.put("user_id",user_id);
                pkdataObj.put("user_group",user_group);
                pkdata=pkdataObj.toString();
            }catch (Exception ex){
                ex.printStackTrace();
            }

            ArrayList<GroupInfoModel> group_list = this.getIntent().getParcelableArrayListExtra("group");
            if (group_list != null && group_list.size() == 2) {
                tvTeamRed.setText(group_list.get(0).getUser_group_title());
                tvTeamBlue.setText(group_list.get(1).getUser_group_title());
            }

            red_list = this.getIntent().getParcelableArrayListExtra("red");
            blue_list = this.getIntent().getParcelableArrayListExtra("blue");

            //初始化
            if (!WebSocketServiceManager.getInstance().isOpen()) {

                AppLogger.d("--初始化pkdata--"+pkdata);

                Map<String, String> httpHeaders = new HashMap<String, String>();
                httpHeaders.put("content-type", "application/json");
                httpHeaders.put("token", WristBallRetrofitHelper.getInstance().getToken());
                httpHeaders.put("pkdata", pkdata);
                WebSocketServiceManager.getInstance().initSocketClient(httpHeaders);
            }

            do {
                red_list.add(new PkUserDataModel(true,"red"));
            } while (red_list.size() < max_person);

            mRedMatchGridAdapter = new MatchGridAdapter(this, red_list, MatchGridAdapter.RED);
            mRedMatchGridAdapter.setOnItemClickListener(this);
            ryTeamRed.setLayoutManager(new GridLayoutManager(this, 5));
            ryTeamRed.setAdapter(mRedMatchGridAdapter);

            do {
                blue_list.add(new PkUserDataModel(true,"blue"));
            } while (blue_list.size() < max_person);

            mBlueMatchGridAdapter = new MatchGridAdapter(this, blue_list, MatchGridAdapter.BLUE);
            mBlueMatchGridAdapter.setOnItemClickListener(this);
            ryTeamBlue.setLayoutManager(new GridLayoutManager(this, 5));
            ryTeamBlue.setAdapter(mBlueMatchGridAdapter);

//            //显示电量
//            int power=App.self().getPower();
//            tvPower.setText(power+"%");
        }else{
            //传递过来数据为空,需要弹框提示退出
            showDialog(getString(R.string.tip),getString(R.string.tip_data_error));
        }
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getEvetId() == MessageEvent.ON_PKConnected){
            //重新连接,需要根据状态设置
            AppLogger.d("-------------------ON_PKConnected-----------------------");

        }else if (event.getEvetId() == MessageEvent.ON_PKListChange) {
            String pkListChange = (String) event.getObject();
            AppLogger.d("-------------------------onMessageEvent------------------------------");
            try {
                //解析JSON
                parsePKListChange(pkListChange);
                //根据用户状态判断是否显示对话框(准备)
                if(getSelfIsReady()!=1){
                    btnWaitReady.setText(R.string.lbl_ready_pk);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } else if (event.getEvetId() == MessageEvent.ON_PKStart) {
            String pkStart = (String) event.getObject();
            AppLogger.d("-------------所有人都点击开始，下发------------"+pkStart);
            startMatchMainActivity(pkStart,pk_room_number, pk_info,true);
        } else if (event.getEvetId() == MessageEvent.ON_PKResult) {
            String pkResult = (String) event.getObject();
            AppLogger.d(pkResult);
            //结束后退出本页面
            WebSocketServiceManager.getInstance().closeConnect();
            finish();
        } else if (event.getEvetId() == MessageEvent.ON_PKError) {
            //
        }else if(event.getEvetId() == MessageEvent.ON_POWER_ELE){
            //获得下发电量,这里在开始和结束获得
            int power=event.getKeepTime();
            AppLogger.d("--power--"+power);
            tvPower.setText(power+"%");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rule, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_rule) {
            PKRuleDialog.show(CreateMatchAddOverActivity.this);
        }
        return true;
    }

    private int getSelfIsReady(){
        int isReady=0;
        String user_id=String.valueOf(pk_info.getUser_id());
        if(user_group.equalsIgnoreCase("red")){
            if(red_list.size()>0){
                for(PkUserDataModel redModel:red_list){
                    if(redModel.getUser_id().equalsIgnoreCase(user_id)){
                        isReady=redModel.getIs_ready();
                        break;
                    }
                }
            }
        }else{
            if(blue_list.size()>0){
                for(PkUserDataModel redModel:blue_list){
                    if(redModel.getUser_id().equalsIgnoreCase(user_id)){
                        isReady=redModel.getIs_ready();
                        break;
                    }
                }
            }
        }
        return isReady;
    }

    protected void setOnResult() {
        //退出前弹框
        showDialog(getResources().getString(R.string.tip), getResources().getString(R.string.lbl_exit_the_pk), new OnCancelListener() {
            @Override
            public void onCancel() {

            }
        }, new OnConfirmListener() {
            @Override
            public void onConfirm() {
                //确认退出
                if (!TextUtils.isEmpty(pk_room_id)) {
                    deletePK(pk_info.getPk_room_id(), String.valueOf(pk_info.getUser_id()), user_group);
                } else {
                    Toast.makeText(CreateMatchAddOverActivity.this, R.string.lbl_not_find_pk_id, Toast.LENGTH_LONG).show();
                }
                WebSocketServiceManager.getInstance().closeConnect();
                finish();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        disposable.dispose();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Logger.d("--onBackPressed--");
        if (PKRuleDialog.isShowing()) {
            WebSocketServiceManager.getInstance().closeConnect();
            PKRuleDialog.dismiss();
        }
    }


    



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d("--onKeyDown--");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (PKRuleDialog.isShowing()) {
                WebSocketServiceManager.getInstance().closeConnect();
                PKRuleDialog.dismiss();
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void parsePKListChange(String pkListChangeStr) throws JSONException {
        JSONObject data = new JSONObject(pkListChangeStr);
        int code = data.optInt("code");
        if (code == 1) {
            JSONObject listObject = data.optJSONObject("data").optJSONObject("list");
            if (listObject != null) {
                ArrayList<PkUserDataModel> redlist = parseTeamGroup(listObject.optJSONArray("red"),"red");
                ArrayList<PkUserDataModel> bluelist = parseTeamGroup(listObject.optJSONArray("blue"),"blue");
                //刷新UI
                red_list.clear();
                red_list.addAll(redlist);
                if (red_list.size() < max_person && red_list.size()>=0) {
                    do {
                        red_list.add(new PkUserDataModel(true,"red",user_pk_list_id));
                    } while (red_list.size() < max_person);
                }

                blue_list.clear();
                blue_list.addAll(bluelist);
                if (blue_list.size() < max_person && blue_list.size()>=0) {
                    do {
                        blue_list.add(new PkUserDataModel(true,"blue",blue_user_pk_list_id));
                    } while (blue_list.size() < max_person);
                }

                mRedMatchGridAdapter.notifyDataSetChanged();
                mBlueMatchGridAdapter.notifyDataSetChanged();
                updateReadyBtnStatus();
                //判断是否需要跳转到主游戏界面，用户点击开始PK
                if (redlist.size() == max_person_num && bluelist.size() == max_person_num) {
                    startMatchMainActivity(pk_room_number, pk_info);
                }
            }
        }
    }


    private void updateReadyBtnStatus() {
            if (user_group.equalsIgnoreCase("red")) {
                int len = red_list.size();
                for (int index = 0; index < len; index++) {
                    //已经是准备状态
                    if (red_list.get(index).getUser_id().equalsIgnoreCase(String.valueOf(pk_info.getUser_id()))) {
                        if (red_list.get(index).getIs_ready() == 1) {
                            btnWaitReady.setText(R.string.lbl_ready_cancel);
                        } else {
                            btnWaitReady.setText(R.string.lbl_ready_pk);
                        }
                        //btnWaitReady.setEnabled(true);
                        break;
                    }
                }
            } else if (user_group.equalsIgnoreCase("blue")) {
                int len = blue_list.size();
                for (int index = 0; index < len; index++) {
                    //已经是准备状态
                    if (blue_list.get(index).getUser_id().equalsIgnoreCase(String.valueOf(pk_info.getUser_id()))) {
                        if (blue_list.get(index).getIs_ready() == 1) {
                            btnWaitReady.setText(R.string.lbl_ready_cancel);
                        } else {
                            btnWaitReady.setText(R.string.lbl_ready_pk);
                        }
                        //btnWaitReady.setEnabled(true);
                        break;
                    }
                }
            }

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


    private void startMatchMainActivity(String pkStart,String pk_room_number, PkInfoModel pk_info,boolean started) {
        if(!isMatchMainActivity){
            Intent it = new Intent(this, MatchMainActivity.class);
            it.putExtra("pkStart", pkStart);
            it.putExtra("started", started);
            it.putExtra("pk_room_number", pk_room_number);
            it.putExtra("pk_info", pk_info);
            startActivity(it);
            isMatchMainActivity=true;
        }
        finish();
    }



    /**
     * 解析玩家列表
     *
     * @param team
     * @return
     */
    private ArrayList<PkUserDataModel> parseTeamGroup(JSONArray team,String user_group) {
        ArrayList<PkUserDataModel> list = new ArrayList<>();
        int len = team.length();
        for (int index = 0; index < len; index++) {

            PkUserDataModel model = new PkUserDataModel();
            JSONObject data = team.optJSONObject(index);
            long temp_pk_list_id=data.optLong("user_pk_list_id");
            if(user_group.equalsIgnoreCase("red")){
                user_pk_list_id=temp_pk_list_id;
            }else{
                blue_user_pk_list_id=temp_pk_list_id;
            }
            model.setUser_pk_list_id(temp_pk_list_id);
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
        return list;
    }

    /**
     * 取消PK
     * @param pk_room_id
     */
    private void deletePK(String pk_room_id, String user_id, String user_group) {
        AppLogger.d("--------------deletePK------------");
        try {
            JSONObject msgObject = new JSONObject();
            msgObject.put("event", "pk_cancel");
            msgObject.put("pk_room_id", pk_room_id);
            msgObject.put("user_id", user_id);
            msgObject.put("user_group", user_group);
            String sendMsg = msgObject.toString();
            WebSocketServiceManager.getInstance().sendMsg(sendMsg);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 切换比赛团队
     *
     * @param pk_room_id
     * @param user_pk_list_id
     * @param new_user_group
     */
    private void changeGroupPK(String pk_room_id, String user_pk_list_id, String new_user_group) {
         WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("pk_room_id", pk_room_id);
        map.put("user_pk_list_id", user_pk_list_id);
        map.put("new_user_group", new_user_group);

        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.changeGroup(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    user_group=new_user_group;
                }

                @Override
                public void onError(int code, String msg) {

                }
            })
        );
    }




    /**
     * 用户点击准备
     * @param pk_room_id
     * @param user_group
     */
    private void startPK(String pk_room_id, String user_id, String user_group) {
        try {
            JSONObject msgObject = new JSONObject();
            msgObject.put("event", "pk_ready");
            msgObject.put("pk_room_id", pk_room_id);
            msgObject.put("user_id", user_id);
            msgObject.put("user_group", user_group);
            String sendMsg = msgObject.toString();
            WebSocketServiceManager.getInstance().sendMsg(sendMsg);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 用户取消准备
     *
     * @param pk_room_id
     * @param user_id
     * @param user_group
     */
    private void cancelStartPK(String pk_room_id, String user_id, String user_group) {
        try {
            JSONObject msgObject = new JSONObject();
            msgObject.put("event", "pk_unready");
            msgObject.put("pk_room_id", pk_room_id);
            msgObject.put("user_id", user_id);
            msgObject.put("user_group", user_group);
            String sendMsg = msgObject.toString();
            WebSocketServiceManager.getInstance().sendMsg(sendMsg);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onItemClick(int whichTeam, PkUserDataModel data) {
        /**切换比赛团队去除
        if (whichTeam == MatchGridAdapter.RED) {
            if (data.isVirtualSeat() && !TextUtils.isEmpty(data.getUser_group()) && !data.getUser_group().equalsIgnoreCase("red")) {
                //虚拟位置且不是当前队伍
                if (!TextUtils.isEmpty(String.valueOf(data.getUser_pk_list_id())) && !TextUtils.isEmpty(pk_room_id)) {
                    changeGroupPK(pk_room_id, String.valueOf(data.getUser_pk_list_id()), "red");
                }
            }
        } else if (whichTeam == MatchGridAdapter.BLUE) {
            if (data.isVirtualSeat() && !TextUtils.isEmpty(data.getUser_group()) && !data.getUser_group().equalsIgnoreCase("blue")) {
                //虚拟位置且不是当前队伍
                if (!TextUtils.isEmpty(String.valueOf(data.getUser_pk_list_id())) && !TextUtils.isEmpty(pk_room_id)) {
                    changeGroupPK(pk_room_id, String.valueOf(data.getUser_pk_list_id()), "blue");
                }
            }
        }
        **/
    }


    public void showDialog(String title, String message, OnCancelListener cancelListener, OnConfirmListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cancelListener != null) {
                    cancelListener.onCancel();
                }
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (confirmListener != null) {
                    confirmListener.onConfirm();
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private String getUser_group(PkInfoModel pk_info,long user_id){
        String user_group="";
        int blue_size=pk_info.getBlueList().size();
        for(int i=0;i<blue_size;i++){
            if(pk_info.getBlueList().get(i).getUser_id().equalsIgnoreCase(String.valueOf(user_id))){
                user_group=pk_info.getBlueList().get(i).getUser_group();
                break;
            }
        }

        int red_size=pk_info.getRedList().size();
        for(int i=0;i<red_size;i++){
            if(pk_info.getRedList().get(i).getUser_id().equalsIgnoreCase(String.valueOf(user_id))){
                user_group=pk_info.getRedList().get(i).getUser_group();
                break;
            }
        }

       return user_group;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnWaitReady) {
            //点击准备
            if (pk_info != null) {
                if(getSelfIsReady()!=1){
                    if (!BleUtils.isConnectedDevice()) {
                        showConnectStateDialog();
                    } else {
                        startPK(pk_info.getPk_room_id(), String.valueOf(pk_info.getUser_id()), user_group);
                    }
                }else{
                    cancelStartPK(pk_info.getPk_room_id(), String.valueOf(pk_info.getUser_id()), user_group);
                }
            }
        }
    }

    private final static int REQUEST_CODE = 100;

    private void showConnectStateDialog() {
        ConfirmDialog.show(this, getString(R.string.tip_bluetooth_disconnected), getResources().getText(R.string.btn_connect).toString(), () -> {
            Intent it= new Intent(this, AddDeviceInfoActivity.class);
            startActivityForResult(it, REQUEST_CODE);
        });
    }

    public interface OnCancelListener {
        void onCancel();
    }

    public interface OnConfirmListener {
        void onConfirm();
    }
}
