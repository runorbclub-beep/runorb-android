package com.cloud.runball.module.race;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.basecomm.utils.AppUtils;
import com.cloud.runball.model.UserGroupModel;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.github.phoenix.widget.Keyboard;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.cloud.runball.databinding.ActivityMatchAddBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.activity
 * @ClassName: CreateRankMatchAddActivity
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/5/13 10:08
 * @UpdateUser: zhd
 * @UpdateDate: 2021/5/13 10:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CreateRankMatchAddActivity extends BaseActivity {

    public static final int REPORT_OK=99;
    private static final String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "", "0", "d"
    };

    private ActivityMatchAddBinding binding;

    TextView tvRoomID1;
    TextView tvRoomID2;
    TextView tvRoomID3;
    TextView tvRoomID4;
    TextView tvRoomID5;
    TextView tvRoomID6;
    Button btnConfirm;
    TextView tvTimeValid;
    Keyboard keyboard;

    String pk_room_number;

    long currentTimeMillis=System.currentTimeMillis();

    ArrayList<UserGroupModel> userGroupModelList = new ArrayList<UserGroupModel>();

    String sys_sys_match_id;
    String sys_match_id;
    int is_group;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_match_add;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityMatchAddBinding.inflate(inflater);
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
        tvTimeValid = binding.tvTimeValid;
        keyboard = binding.KeyboardViewPay;
        sys_sys_match_id=this.getIntent().getStringExtra("sys_sys_match_id");
        sys_match_id=this.getIntent().getStringExtra("sys_match_id");
        is_group=this.getIntent().getIntExtra("is_group",0);

        setSubView();
        initEvent();

        // Replace @OnClick with listeners
        btnConfirm.setOnClickListener(this::onViewClicked);
        tvTimeValid.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_add_match);
    }

    public void onViewClicked(View v) {
        if (v.getId() == R.id.btnConfirm) {
            if (!TextUtils.isEmpty(stringBuilder) && stringBuilder.length() == 6) {
                if(System.currentTimeMillis()-currentTimeMillis>=500){
                    //跳转到第二个页面
                    pk_room_number = stringBuilder.toString();
                    requestSearchMatchGroup(pk_room_number);
                }
                currentTimeMillis=System.currentTimeMillis();
            } else {
                Toast.makeText(this, R.string.lbl_input_match_tip, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.tvTimeValid) {
            //剪切板黏贴
            String clipText = AppUtils.getStringClipboard(this);
            if (!TextUtils.isEmpty(clipText) && clipText.length() == 6) {
                pk_room_number = clipText;
                if (stringBuilder.length() > 0) {
                    stringBuilder.delete(0, stringBuilder.length() - 1);
                }
                stringBuilder.append(clipText);
                showMatchRoomNum(clipText);
            } else {
                Toast.makeText(this, R.string.lbl_input_clip_error_tip, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showMatchRoomNum(String pk_room_number) {
        if (pk_room_number != null && pk_room_number.trim().length() == 6) {
            tvRoomID1.setText(pk_room_number.substring(0, 1));
            tvRoomID2.setText(pk_room_number.substring(1, 2));
            tvRoomID3.setText(pk_room_number.substring(2, 3));
            tvRoomID4.setText(pk_room_number.substring(3, 4));
            tvRoomID5.setText(pk_room_number.substring(4, 5));
            tvRoomID6.setText(pk_room_number.substring(5));
        }
    }

    private void setSubView() {
        //设置键盘
        keyboard.setKeyboardKeys(KEY);
        keyboard.setKeyBoardBackground(getResources().getColor(R.color.bg_content));
    }


    StringBuilder stringBuilder = new StringBuilder();

    private void initEvent() {
        keyboard.setOnClickKeyboardListener(new Keyboard.OnClickKeyboardListener() {
            @Override
            public void onKeyClick(int position, String value) {
                if (position < 11 && position != 9) {
                    stringBuilder.append(value);
                    if (stringBuilder.length() > 6) {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }
                    updateRoomKeyBoard();
                } else if (position == 11 || position == 9) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }
                    updateRoomKeyBoard();
                }
            }
        });
    }

    public void updateRoomKeyBoard() {
        switch (stringBuilder.length()) {
            case 0:
                tvRoomID1.setText("   ");
                tvRoomID2.setText("   ");
                tvRoomID3.setText("   ");
                tvRoomID4.setText("   ");
                tvRoomID5.setText("   ");
                tvRoomID6.setText("   ");
                break;
            case 1:
                tvRoomID1.setText(stringBuilder.substring(0, 1));
                tvRoomID2.setText("   ");
                tvRoomID3.setText("   ");
                tvRoomID4.setText("   ");
                tvRoomID5.setText("   ");
                tvRoomID6.setText("   ");
                break;
            case 2:
                tvRoomID2.setText(stringBuilder.substring(1, 2));
                tvRoomID3.setText("   ");
                tvRoomID4.setText("   ");
                tvRoomID5.setText("   ");
                tvRoomID6.setText("   ");
                break;
            case 3:
                tvRoomID3.setText(stringBuilder.substring(2, 3));
                tvRoomID4.setText("   ");
                tvRoomID5.setText("   ");
                tvRoomID6.setText("   ");
                break;
            case 4:
                tvRoomID4.setText(stringBuilder.substring(3, 4));
                tvRoomID5.setText("   ");
                tvRoomID6.setText("   ");
                break;
            case 5:
                tvRoomID5.setText(stringBuilder.substring(4, 5));
                tvRoomID6.setText("   ");
                break;
            case 6:
                tvRoomID6.setText(stringBuilder.substring(5));
                break;
        }
    }

    /**
     * 报名参赛
     * @param sys_sys_match_id
     * @param sys_match_id
     * @param language
     * @param is_group
     * @param group_num
     * @param user_group_id
     */
    private void requestRankMatchSign(String sys_sys_match_id,String sys_match_id,String language,int is_group,String group_num,String user_group_id){
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(6);
        map.put("sys_sys_match_id", sys_sys_match_id);
        map.put("sys_match_id", sys_match_id);
        map.put("user_group_id", user_group_id);
        map.put("language", language);
        map.put("is_group", is_group);
        map.put("group_num", group_num);

        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.matchSign(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                        try{
                            JSONObject jsonObject=new JSONObject(responseBody.string());
                            int code=jsonObject.optInt("code");
                            if(code==1){
                                //报名成功
                                setResult(REPORT_OK);
                                finish();
                            }else{
                                showDialog(getString(R.string.tip), jsonObject.optString("msg"),null);
                            }
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                @Override
                public void onError(int code, String msg) {
                    AppLogger.d("---requestRankMatchSign----code="+code+";msg="+msg);
                }
            })
        );
    }

    String user_group_id="";

    private void requestSearchMatchGroup(String group_num){
        WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("group_num", group_num);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
        Observable<ResponseBody> observable = apiServer.matchGroup(requestBody);
        disposable.add(
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new WristBallObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try{
                        JSONObject jsonObject=new JSONObject(responseBody.string());
                        int code=jsonObject.optInt("code");
                        if(code==1){
                            JSONObject dataJSONObject=jsonObject.optJSONObject("data");
                            if(dataJSONObject!=null){
                                user_group_id=dataJSONObject.optString("user_group_id");
                            }
                            showDialog(getString(R.string.tip), jsonObject.optString("msg"), null, new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    //报名
                                    Locale locale = getResources().getConfiguration().locale;
                                    String language = locale.getLanguage();
                                    if(language.startsWith("zh")){
                                        language="zh-CN";
                                    }else{
                                        language="en-US";
                                    }
                                    requestRankMatchSign(sys_sys_match_id,sys_match_id,language,is_group,pk_room_number, user_group_id);
                                }
                            });
                        }else{
                            showDialog(getString(R.string.tip), jsonObject.optString("msg"), null);
                        }
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    AppLogger.d("---requestSearchMatchGroup---onError--"+msg+";code="+code);
                }
            })
        );
    }

}
